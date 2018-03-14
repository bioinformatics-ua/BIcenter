package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import diSdk.task.TaskDecoder;
import diSdk.trans.TransExecutor;
import kettleExt.App;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;
import models.*;
import models.Execution;
import org.hibernate.Hibernate;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.*;
import serializers.performance.*;
import serializers.task.PerformanceTaskSerializer;
import serializers.task.SimpleTaskSerializer;

import javax.inject.Inject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller that manages task executions.
 */
public class ExecutionController extends Controller {

    private TaskRepository taskRepository;
    private ExecutionRepository executionRepository;
    private StepMetricRepository stepMetricRepository;
    private StatusRepository statusRepository;
    private DataRowRepository dataRowRepository;
    private KeyValueRepository keyValueRepository;

    @Inject
    public ExecutionController(TaskRepository taskRepository, ExecutionRepository executionRepository, StepMetricRepository stepMetricRepository, StatusRepository statusRepository, DataRowRepository dataRowRepository, KeyValueRepository keyValueRepository){
        this.taskRepository = taskRepository;
        this.executionRepository = executionRepository;
        this.stepMetricRepository = stepMetricRepository;
        this.statusRepository = statusRepository;
        this.dataRowRepository = dataRowRepository;
        this.keyValueRepository = keyValueRepository;
    }

    /**
     * Renders Execution Logs page.
     * @param executionId
     * @return
     */
    public Result logs(long graphId, long executionId) { return ok(views.html.index.render()); }

    /**
     * Renders Steps Metrics page.
     * @param executionId
     * @return
     */
    public Result metrics(long graphId, long executionId) { return ok(views.html.index.render()); }

    /**
     * Renders Preview Task page.
     * @param executionId
     * @return
     */
    public Result previewData(long graphId, long executionId) { return ok(views.html.index.render()); }

    /**
     * Renders Preview Step page.
     * @param executionId
     * @return
     */
    public Result previewStep(long graphId, long executionId, long stepId) { return ok(views.html.index.render()); }

    /**
     * Post Method, that given a certain task and a execution configuration (XML) specification,
     * runs the defined transformation.
     * @return Transformation Results.
     * @throws Exception
     */
    public Result run(long taskId) throws Exception {
        String execution_configuration = request().body().asJson().asText();

        // Prepare Transformation based on the JPA Task.
        Task task = taskRepository.get(taskId);

        if(!task.getSteps().isEmpty()) {
            Hibernate.initialize(task.getSteps());
            for (Step step : task.getSteps()) {
                Hibernate.initialize(step.getComponent());
                Component component = step.getComponent();
                if(!component.getComponentProperties().isEmpty())
                    Hibernate.initialize(component.getComponentProperties());
                for (ComponentProperty componentProperty : step.getComponent().getComponentProperties()) {
                    if(!componentProperty.getComponentMetadatas().isEmpty())
                        Hibernate.initialize(componentProperty.getComponentMetadatas());
                }
                Hibernate.initialize(step.getCell());
                if(!step.getStepProperties().isEmpty()) Hibernate.initialize(step.getStepProperties());
            }
        }
        if(!task.getHops().isEmpty()) Hibernate.initialize(task.getHops());

        TransMeta transMeta = TaskDecoder.decode(task);

        // Prepare the Execution Configuration.
        TransExecutionConfiguration executionConfiguration = prepareExecution(transMeta,execution_configuration);

        // Execute Transformation.
        TransExecutor transExecutor = TransExecutor.initExecutor(executionConfiguration, transMeta, taskId, taskRepository, executionRepository, stepMetricRepository, statusRepository, dataRowRepository,keyValueRepository);
        new Thread(transExecutor).start();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", "RUNNING");
        jsonObject.put("executionId", transExecutor.getExecutionId());
        jsonObject.put("transName", transMeta.getName());

        return ok(jsonObject.toString()).as("text/html");
    }

    private TransExecutionConfiguration prepareExecution(TransMeta transMeta, String execution_configuration) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(execution_configuration);
        TransExecutionConfiguration executionConfiguration = new TransExecutionConfiguration();

        JSONObject executeMethod = jsonObject.optJSONObject("executeMethod");
        if(executeMethod.optInt("execMethod") == 1) {
            executionConfiguration.setExecutingLocally( true );
            executionConfiguration.setExecutingRemotely( false );
            executionConfiguration.setExecutingClustered( false );
        } else if(executeMethod.optInt("execMethod") == 2) {
            executionConfiguration.setExecutingLocally( false );
            executionConfiguration.setExecutingRemotely( true );
            executionConfiguration.setExecutingClustered( false );

            executionConfiguration.setRemoteServer( transMeta.findSlaveServer( executeMethod.optString("remoteServer")) );
            executionConfiguration.setPassingExport( executeMethod.containsKey("passingExport") );
        } else if(executeMethod.optInt("execMethod") == 3) {
            executionConfiguration.setExecutingLocally( true );
            executionConfiguration.setExecutingRemotely( false );
            executionConfiguration.setExecutingClustered( false );
        }

        JSONObject details = jsonObject.optJSONObject("details");
        executionConfiguration.setSafeModeEnabled( details.containsKey("safeModeEnabled") );
        executionConfiguration.setGatheringMetrics( details.containsKey("gatheringMetrics") );
        executionConfiguration.setClearingLog( details.containsKey("clearingLog") );
        executionConfiguration.setLogLevel( LogLevel.values()[details.optInt("logLevel")] );
        if (!Const.isEmpty(details.optString("replayDate"))) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                executionConfiguration.setReplayDate(simpleDateFormat.parse(details.optString("replayDate")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            executionConfiguration.setReplayDate(null);
        }

        executionConfiguration.getUsedVariables( transMeta );
        executionConfiguration.getUsedArguments( transMeta, App.getInstance().getArguments() );

        Map<String, String> map = new HashMap<String, String>();
        JSONArray parameters = jsonObject.optJSONArray("parameters");
        for(int i=0; i<parameters.size(); i++) {
            JSONObject param = parameters.getJSONObject(i);
            String paramName = param.optString("name");
            String paramValue = param.optString("value");
            String defaultValue = param.optString("default_value");
            if (Const.isEmpty(paramValue)) {
                paramValue = Const.NVL(defaultValue, "");
            }
            map.put( paramName, paramValue );
        }
        executionConfiguration.setParams(map);

        map = new HashMap<String, String>();
        JSONArray variables = jsonObject.optJSONArray("variables");
        for ( int i = 0; i < variables.size(); i++ ) {
            JSONObject var = variables.getJSONObject(i);
            String varname = var.optString("var_name");
            String value = var.optString("var_value");

            if ( !Const.isEmpty( varname ) ) {
                map.put( varname, value );
            }
        }
        executionConfiguration.setVariables( map );

        return executionConfiguration;
    }

    public Result result(long executionId) throws Exception {
        // Fetch and Serialize Execution.
        Execution execution = executionRepository.get(executionId);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Execution.class, new ExecutionSerializer());
        module.addSerializer(StepMetric.class, new StepMetricSerializer());
        module.addSerializer(Status.class, new StatusSerializer());
        module.addSerializer(DataRow.class, new DataRowSerializer());
        module.addSerializer(KeyValue.class, new KeyValueSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(execution)).as("text/html");
    }

    public Result getTask(long executionId) throws Exception {
        // Fetch and Serialize Execution.
        Execution execution = executionRepository.get(executionId);
        Task task = execution.getTask();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Task.class, new SimpleTaskSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(task));
    }

    public Result getLogs(long executionId) {
        Execution execution = executionRepository.get(executionId);
        Task task = execution.getTask();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Task.class, new PerformanceTaskSerializer(executionId));
        module.addSerializer(Execution.class, new LogExecutionSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(task));
    }

    public Result getMetrics(long executionId) {
        Execution execution = executionRepository.get(executionId);
        Task task = execution.getTask();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Task.class, new PerformanceTaskSerializer(executionId));
        module.addSerializer(Execution.class, new StepMetricsExecutionSerializer());
        module.addSerializer(StepMetric.class, new StepMetricSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(task));
    }

    public Result getData(long executionId) {
        Execution execution = executionRepository.get(executionId);
        Task task = execution.getTask();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Task.class, new PerformanceTaskSerializer(executionId));
        module.addSerializer(Execution.class, new PreviewDataExecutionSerializer());
        module.addSerializer(Status.class, new StatusSerializer());
        module.addSerializer(DataRow.class, new DataRowSerializer());
        module.addSerializer(KeyValue.class, new KeyValueSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(task));
    }

    public Result getStepData(long executionId, long stepId){
        Execution execution = executionRepository.get(executionId);
        Task task = execution.getTask();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Task.class, new PerformanceTaskSerializer(executionId));
        module.addSerializer(Execution.class, new PreviewDataExecutionSerializer(stepId));
        module.addSerializer(Status.class, new StatusSerializer());
        module.addSerializer(DataRow.class, new DataRowSerializer());
        module.addSerializer(KeyValue.class, new KeyValueSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(task));
    }
}

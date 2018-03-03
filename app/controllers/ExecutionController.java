package controllers;

import diSdk.task.TaskDecoder;
import diSdk.trans.TransExecutor;
import kettleExt.App;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;
import models.*;
import org.hibernate.Hibernate;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import play.cache.SyncCacheApi;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.TaskRepository;

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
    private SyncCacheApi cache;

    private TaskRepository taskRepository;

    @Inject
    public ExecutionController(SyncCacheApi cache, TaskRepository taskRepository){
        this.cache = cache;
        this.taskRepository = taskRepository;
    }

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
        TransExecutor transExecutor = TransExecutor.initExecutor(executionConfiguration, transMeta);
        new Thread(transExecutor).start();

        // Cache execution.
        cache.set(transExecutor.getExecutionId(), transExecutor);
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

    public Result result(String executionId) throws Exception {
        JSONObject jsonReply = new JSONObject();

        TransExecutor transExecutor = cache.get(executionId);

        jsonReply.put("finished", transExecutor.isFinished());
        if(transExecutor.isFinished()) {
            session().remove(executionId);

            jsonReply.put("stepMeasure", transExecutor.getStepMeasure());
            jsonReply.put("log", transExecutor.getExecutionLog());
            jsonReply.put("stepStatus", transExecutor.getStepStatus());
            jsonReply.put("previewData", transExecutor.getPreviewData());
        } else {
            jsonReply.put("stepMeasure", transExecutor.getStepMeasure());
            jsonReply.put("log", transExecutor.getExecutionLog());
            jsonReply.put("stepStatus", transExecutor.getStepStatus());
            jsonReply.put("previewData", transExecutor.getPreviewData());
        }

        return ok(jsonReply.toString()).as("text/html");
    }
}

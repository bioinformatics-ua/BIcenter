package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import diSdk.task.TaskDecoder;
import diSdk.trans.TransExecutor;
import kettleExt.utils.JSONObject;
import models.*;
import models.Execution;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.quartz.*;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.*;
import scheduler.ExecutionScheduler;
import serializers.performance.*;
import serializers.task.PerformanceTaskSerializer;
import serializers.task.SimpleTaskSerializer;

import javax.inject.Inject;
import java.text.SimpleDateFormat;

/**
 * Controller that manages task executions.
 */
public class ExecutionController extends Controller {
    private InstitutionRepository institutionRepository;
    private TaskRepository taskRepository;
    private ExecutionRepository executionRepository;
    private StepMetricRepository stepMetricRepository;
    private StatusRepository statusRepository;
    private DataRowRepository dataRowRepository;
    private KeyValueRepository keyValueRepository;
    private ServerRepository serverRepository;
    private ScheduleRepository scheduleRepository;

    @Inject
    public ExecutionController(InstitutionRepository institutionRepository, TaskRepository taskRepository, ExecutionRepository executionRepository, StepMetricRepository stepMetricRepository, StatusRepository statusRepository, DataRowRepository dataRowRepository, KeyValueRepository keyValueRepository, ServerRepository serverRepository, ScheduleRepository scheduleRepository){
        this.institutionRepository = institutionRepository;
        this.taskRepository = taskRepository;
        this.executionRepository = executionRepository;
        this.stepMetricRepository = stepMetricRepository;
        this.statusRepository = statusRepository;
        this.dataRowRepository = dataRowRepository;
        this.keyValueRepository = keyValueRepository;
        this.serverRepository = serverRepository;
        this.scheduleRepository = scheduleRepository;
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

    public Result remoteExecution(long taskId, long serverId) throws Exception {
        JsonNode details = request().body().as(JsonNode.class);
        boolean schedule = details.get("schedule").asText().equals("on") ? true: false;

        Schedule scheduleObj = null;

        int hour,minutes,dayOfMonth,month,year;
        hour = minutes = dayOfMonth = month = year = 0;
        if(schedule) {
            // Initialize Schedule.
            scheduleObj = new Schedule();

            String[] dateTime = details.get("dateTime").asText().split(" ");
            String date = dateTime[0];
            String time = dateTime[1] + " " + dateTime[2];

            // Start Date
            dayOfMonth = Integer.parseInt(date.split("/")[1]);
            month = Integer.parseInt(date.split("/")[0]);
            year = Integer.parseInt(date.split("/")[2]);

            SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");
            time = date24Format.format(date12Format.parse(time));

            // Start Time
            hour = Integer.parseInt(time.split(":")[0]);
            minutes = Integer.parseInt(time.split(":")[1]);

            scheduleObj.setStart(DateBuilder.dateOf(hour,minutes,0,dayOfMonth,month,year));
        }

        boolean periodic = details.get("periodic").asText().equals("on") ? true: false;
        String interval = "";
        if(periodic) {
            interval = details.get("intervals").asText();
            scheduleObj.setInterval(interval);
        }

        // Store the execution schedule in the database.
        if(scheduleObj != null) {
            Task task = taskRepository.get(taskId);
            scheduleObj.setTask(task);
            Server server = serverRepository.get(serverId);
            scheduleObj.setServer(server);
            Institution institution = institutionRepository.get(task.getInstitution().getId());
            scheduleObj.setInstitution(institution);
            scheduleObj = scheduleRepository.add(scheduleObj);
        }

        // Fire the job within the scheduler.
        ExecutionScheduler executionScheduler = new ExecutionScheduler();
        executionScheduler.fireJob(scheduleObj.getId(),schedule,hour,minutes,dayOfMonth,month,year,periodic,interval,taskId,serverId,taskRepository, serverRepository, executionRepository, stepMetricRepository, statusRepository, dataRowRepository,keyValueRepository);

        return ok();
    }

    public Result localExecution(long taskId) throws Exception {
        // Prepare Transformation based on the JPA Task.
        Task task = taskRepository.get(taskId);
        //initializeTask(task);
        TransMeta transMeta = TaskDecoder.decode(task);

        // Setting Execution Configurations.
        TransExecutionConfiguration executionConfiguration = new TransExecutionConfiguration();
        executionConfiguration.setExecutingLocally( true );
        executionConfiguration.setExecutingRemotely( false );
        executionConfiguration.setExecutingClustered( false );

        // Execute Transformation.
        TransExecutor transExecutor = TransExecutor.initExecutor(executionConfiguration, transMeta, taskId, taskRepository, executionRepository, stepMetricRepository, statusRepository, dataRowRepository,keyValueRepository);
        new Thread(transExecutor).start();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", "RUNNING");
        jsonObject.put("executionId", transExecutor.getExecutionId());
        jsonObject.put("transName", transMeta.getName());

        return ok(jsonObject.toString()).as("text/html");
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

    public Result deleteSchedule(long taskId, long scheduleId) throws SchedulerException {
        // Delete Schedule in the JPA.
        Schedule schedule = scheduleRepository.get(scheduleId);
        scheduleRepository.delete(schedule);

        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
        Scheduler scheduler = schedFact.getScheduler();

        // Remove Execution Trigger.
        TriggerKey triggerKey = TriggerKey.triggerKey(String.valueOf(scheduleId), String.valueOf(taskId));
        boolean unscheduled = scheduler.unscheduleJob(triggerKey);

        return ok(String.valueOf(unscheduled));
    }
}

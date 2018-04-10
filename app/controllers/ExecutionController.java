package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import controllers.login.Secured;
import controllers.rbac.annotation.CheckPermission;
import diSdk.task.TaskDecoder;
import diSdk.trans.TransExecutor;
import kettleExt.utils.JSONObject;
import models.*;
import models.Execution;
import models.rbac.Category;
import models.rbac.Operation;
import models.rbac.User;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.quartz.*;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import repositories.*;
import repositories.user.UserRepository;
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
    private UserRepository userRepository;

    @Inject
    public ExecutionController(InstitutionRepository institutionRepository, TaskRepository taskRepository, ExecutionRepository executionRepository, StepMetricRepository stepMetricRepository, StatusRepository statusRepository, DataRowRepository dataRowRepository, KeyValueRepository keyValueRepository, ServerRepository serverRepository, ScheduleRepository scheduleRepository, UserRepository userRepository){
        this.institutionRepository = institutionRepository;
        this.taskRepository = taskRepository;
        this.executionRepository = executionRepository;
        this.stepMetricRepository = stepMetricRepository;
        this.statusRepository = statusRepository;
        this.dataRowRepository = dataRowRepository;
        this.keyValueRepository = keyValueRepository;
        this.serverRepository = serverRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Renders Execution Logs page.
     * @param executionId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result logs(long institutionId, long graphId, long executionId) { return ok(views.html.index.render()); }

    /**
     * Renders Steps Metrics page.
     * @param executionId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result metrics(long institutionId, long graphId, long executionId) { return ok(views.html.index.render()); }

    /**
     * Renders Preview Task page.
     * @param executionId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result previewData(long institutionId, long graphId, long executionId) { return ok(views.html.index.render()); }

    /**
     * Renders Preview Step page.
     * @param executionId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result previewStep(long institutionId, long graphId, long executionId, long stepId) { return ok(views.html.index.render()); }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.RUN})
    public Result remoteExecution(long institutionId, long taskId, long serverId) throws Exception {
        JsonNode details = request().body().as(JsonNode.class);

        boolean schedule;
        if(details.get("schedule") == null) schedule = false;
        else schedule = details.get("schedule").asText().equals("on") ? true: false;

        Schedule scheduleObj = null;

        int hour,minutes,dayOfMonth,month,year;
        hour = minutes = dayOfMonth = month = year = 0;

        boolean periodic = false;
        String interval = "Once";

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

            scheduleObj.setStart(DateBuilder.dateOf(hour, minutes, 0, dayOfMonth, month, year));

            try {
                periodic = details.get("periodic").asText().equals("on") ? true : false;
                interval = "";
                if (periodic) {
                    interval = details.get("intervals").asText();
                    scheduleObj.setInterval(interval);
                }
            } catch (NullPointerException e) {
                periodic = false;
                interval = "Once";
                scheduleObj.setInterval("Once");
            }
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
        long scheduleId = 0;
        if(scheduleObj!=null){
            scheduleId = scheduleObj.getId();
        }

        String email = session("userEmail");
        User user = this.userRepository.findByEmail(email);

        executionScheduler.fireJob(scheduleId,schedule,hour,minutes,dayOfMonth,month,year,periodic,interval,taskId,serverId,user.getId(),scheduleRepository,taskRepository, serverRepository, executionRepository, stepMetricRepository, statusRepository, dataRowRepository,keyValueRepository,userRepository);

        return ok();
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.RUN})
    public Result localExecution(long institutionId, long taskId) throws Exception {
        // Prepare Transformation based on the JPA Task.
        Task task = taskRepository.get(taskId);
        TransMeta transMeta = TaskDecoder.decode(task);

        // Setting Execution Configurations.
        TransExecutionConfiguration executionConfiguration = new TransExecutionConfiguration();
        executionConfiguration.setExecutingLocally( true );
        executionConfiguration.setExecutingRemotely( false );
        executionConfiguration.setExecutingClustered( false );

        String email = session("userEmail");
        User user = this.userRepository.findByEmail(email);

        // Execute Transformation.
        TransExecutor transExecutor = TransExecutor.initExecutor(executionConfiguration, transMeta, taskId, taskRepository, (long) 0, serverRepository, user.getId(), userRepository, executionRepository, stepMetricRepository, statusRepository, dataRowRepository,keyValueRepository);
        new Thread(transExecutor).start();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", "RUNNING");
        jsonObject.put("executionId", transExecutor.getExecutionId());
        jsonObject.put("transName", transMeta.getName());

        return ok(jsonObject.toString()).as("text/html");
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result result(long institutionId, long executionId) throws Exception {
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getTask(long institutionId, long executionId) throws Exception {
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getLogs(long institutionId, long executionId) {
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getMetrics(long institutionId, long executionId) {
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getData(long institutionId, long executionId) {
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getStepData(long institutionId, long executionId, long stepId){
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.UPDATE})
    public Result deleteSchedule(long institutionId, long taskId, long scheduleId) throws SchedulerException {
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

package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import controllers.login.Secured;
import controllers.rbac.annotation.CheckPermission;
import diSdk.task.TaskEncoder;
import models.*;
import models.Execution;
import models.rbac.Category;
import models.rbac.Operation;
import org.hibernate.Hibernate;
import play.cache.*;

import kettleExt.trans.TransEncoder;

import org.pentaho.di.trans.TransMeta;

import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import java.io.*;
import java.util.List;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import repositories.*;
import serializers.component.ComponentMetadataSerializer;
import serializers.component.ComponentPropertySerializer;
import serializers.component.ComponentSerializer;
import serializers.component.MetadataSerializer;
import serializers.hop.HopSerializer;
import serializers.institution.CompleteServerSerializer;
import serializers.performance.*;
import serializers.step.CellSerializer;
import serializers.step.StepPropertySerializer;
import serializers.step.StepSerializer;
import serializers.task.PerformanceTaskSerializer;
import serializers.task.SimpleTaskSerializer;
import serializers.task.TaskSerializer;

import javax.inject.Inject;
import javax.persistence.NoResultException;

/**
 * Controller that manages the mxGraph.
 */
public class TransGraphController extends Controller {
    private SyncCacheApi cache;

    private TaskRepository taskRepository;
    private StepRepository stepRepository;
    private CellRepository cellRepository;
    private ComponentRepository componentRepository;
    private HopRepository hopRepository;
    private InstitutionRepository institutionRepository;

    @Inject
    public TransGraphController(SyncCacheApi cache, TaskRepository taskRepository, StepRepository stepRepository, CellRepository cellRepository, ComponentRepository componentRepository, HopRepository hopRepository, InstitutionRepository institutionRepository) {
        this.cache = cache;
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.cellRepository = cellRepository;
        this.componentRepository = componentRepository;
        this.hopRepository = hopRepository;
        this.institutionRepository = institutionRepository;
    }

    /**
     * Select a graph tab
     * @param graphId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = Operation.GET)
    public Result selectTask(long institutionId, long graphId) { return ok(views.html.index.render()); }

    /**
     * Renders Task History page.
     * @param graphId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = Operation.GET)
    public Result history(long institutionId, long graphId) { return ok(views.html.index.render()); }

    /**
     * Execution Scheduler page.
     * @param graphId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = Operation.GET)
    public Result schedule(long institutionId, long graphId) { return ok(views.html.index.render()); }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getTaskDetails(long institutionId, long graphId) {
        Task task = taskRepository.get(graphId);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Task.class, new SimpleTaskSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(task));
    }

//    public Result getTasks() {
//        List<Task> tasks = taskRepository.list();
//
//        ObjectMapper mapper = new ObjectMapper();
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(Task.class, new SimpleTaskSerializer());
//        mapper.registerModule(module);
//        Json.setObjectMapper(mapper);
//
//        return ok(Json.toJson(tasks));
//    }

    /**
     * Converts Task to mxGraph
     * @param graphId Task Id.
     * @return mxGraph
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result loadTask(long institutionId, long graphId) throws Exception {
        Task task = this.taskRepository.get(graphId);

        // Open task tab.
        task.setOpen(1);
        task = taskRepository.add(task);

        if(!task.getSteps().isEmpty()) {
            Hibernate.initialize(task.getSteps());
            for (Step step : task.getSteps()) {
                Hibernate.initialize(step.getComponent());
                Hibernate.initialize(step.getCell());
            }
        }
        if(!task.getHops().isEmpty()) Hibernate.initialize(task.getHops());

        mxCodec codec = new mxCodec();
        mxGraph graph = TaskEncoder.encode(task);
        String graphXml = mxUtils.getPrettyXml(codec.encode(graph.getModel()));
        return ok(graphXml).as("text/html");
    }

    /**
     * Check if a task with the given name exists.
     * @param name
     * @return
     */
    public boolean existsTask(long institutionId, String name){
        boolean exists = true;
        if(taskRepository.list().isEmpty()) exists = false;
        else{
            try{
                taskRepository.getByInstitutionAndName(institutionId, name);
            }
            catch(NoResultException e){
                exists = false;
            }
        }
        if(exists) return true;
        return false;
    }

    /**
     * Creates a new task with the specified name, and return the mxGraph.
     * @param name Task name.
     * @return mxGrpoh file
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.ADD})
    public Result newTask(long institutionId, String name){
        if(existsTask(institutionId, name)) forbidden();

        Task task = new Task(name);
        Institution inst = institutionRepository.get(institutionId);
        task.setInstitution(inst);
        task = taskRepository.add(task);

        return ok(taskToJson(task));
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.UPDATE})
    public Result updateTask(long institutionId, long taskId){
        JsonNode formData = request().body().asJson();

        Task task = taskRepository.get(taskId);
        Institution institution = institutionRepository.get(formData.get("institution").asInt());
        task.setInstitution(institution);
        task.setName(formData.get("name").asText());
        task = taskRepository.add(task);

        return ok(taskToJson(task));
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.DELETE})
    public Result deleteTask(long institution, long task) {
        this.taskRepository.delete(task);
        return ok();
    }

    /**
     * Get Task by name, and returns it as a Json.
     * @param name Task name
     * @return Task Json object
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getTask(long institutionId, String name) {
        if(!existsTask(institutionId, name)) return ok("not found");

        Task task = taskRepository.getByName(name);
        return ok(taskToJson(task));
    }

    /**
     * Serializes Task (JPA) to Json
     * @param task
     * @return
     */
    private JsonNode taskToJson(Task task){
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Task.class, new TaskSerializer());
        module.addSerializer(Step.class, new StepSerializer());
        module.addSerializer(Hop.class, new HopSerializer());
        module.addSerializer(Cell.class, new CellSerializer());
        module.addSerializer(Component.class, new ComponentSerializer());
        module.addSerializer(ComponentProperty.class, new ComponentPropertySerializer());
        module.addSerializer(StepProperty.class, new StepPropertySerializer());
        module.addSerializer(ComponentMetadata.class, new ComponentMetadataSerializer());
        module.addSerializer(Metadata.class, new MetadataSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return Json.toJson(task);
    }

    /**
     * Store a certain step, within the given task.
     * @param taskId Task Id.
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.UPDATE})
    public Result addStep(long institutionId, long taskId) {
        JsonNode stepMeta = request().body().as(JsonNode.class);

        // Build new step.
        String label = stepMeta.get("label").asText();
        int graphId = stepMeta.get("graphId").asInt();
        Step step = new Step(label,graphId);
        String name = stepMeta.get("component").asText();
        Component component = componentRepository.getByName(name);
        step.setComponent(component);
        Task task = taskRepository.get(taskId);
        step.setTaskSteps(task);
        step = stepRepository.add(step);

        // Build the corresponding cell.
        Cell cell = new Cell(
            stepMeta.get("x").asInt(),stepMeta.get("y").asInt(),
            stepMeta.get("width").asInt(),stepMeta.get("height").asInt()
        );
        cell.setStep(step);
        cell = cellRepository.add(cell);
        return ok(String.valueOf(step.getId()));
    }

    /**
     * Update step coordinates.
     * @param stepId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.UPDATE})
    public Result updateStep(long institutionId, long stepId) {
        Step step = stepRepository.get(stepId);
        Cell cell = step.getCell();

        JsonNode coord = request().body().as(JsonNode.class);
        int x = coord.get("x").asInt();
        int y = coord.get("y").asInt();

        cell.setX(x);
        cell.setY(y);
        cellRepository.add(cell);
        return ok();
    }

    /**
     * Delete a certain step.
     * @param stepId Step Id.
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.UPDATE})
    public Result removeStep(long institutionId, long stepId) {
        Step step = stepRepository.get(stepId);
        stepRepository.delete(step);
        return ok();
    }

//    /**
//     * Get all task steps.
//     * @param graphId Task id.
//     * @return List of steps.
//     */
//    public Result getSteps(long graphId) {
//        Task task = taskRepository.get(graphId);
//        List<Step> steps = task.getSteps();
//        return ok(taskToJson(task));
//    }

    /**
     * Store a certain hop, within the given task.
     * @param taskId Task Id.
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.UPDATE})
    public Result addHop(long institutionId, long taskId) {
        JsonNode hopMeta = request().body().as(JsonNode.class);

        // Build new step.
        int graphId = hopMeta.get("graphId").asInt();
        Hop hop = new Hop(graphId);
        Step source = stepRepository.get(hopMeta.get("source").asInt());
        hop.setSource(source);
        Step target = stepRepository.get(hopMeta.get("target").asInt());
        hop.setDestiny(target);
        Task task = taskRepository.get(taskId);
        hop.setTaskHops(task);
        hop = hopRepository.add(hop);
        return ok();
    }

    /**
     * Delete a certain hop.
     * @param hopId Hop Id.
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.UPDATE})
    public Result removeHop(long institutionId, long hopId) {
        Hop hop = hopRepository.get(hopId);
        hopRepository.delete(hop);
        return ok();
    }

    /**
     * Retrive all previous executions.
     * @param graphId Task id.
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getExecutions(long institutionId, long graphId) {
        Task task = taskRepository.get(graphId);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Task.class, new PerformanceTaskSerializer());
        module.addSerializer(Execution.class, new ExecutionSerializer());
        module.addSerializer(StepMetric.class, new StepMetricSerializer());
        module.addSerializer(Status.class, new StatusSerializer());
        module.addSerializer(DataRow.class, new DataRowSerializer());
        module.addSerializer(KeyValue.class, new KeyValueSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(task));
    }

    /**
     * Get all available Servers for the given Task.
     * @param institutionId
     * @param graphId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.SERVER, needs = {Operation.GET})
    public Result getServers(long institutionId, long graphId) {
        Task task = taskRepository.get(graphId);
        Institution institution = task.getInstitution();
        List<Server> servers = institution.getServers();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Server.class, new CompleteServerSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(servers));
    }
}
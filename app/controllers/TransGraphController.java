package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import diSdk.task.TaskEncoder;
import models.*;
import org.hibernate.Hibernate;
import play.cache.*;

import kettleExt.App;
import kettleExt.TransExecutor;
import kettleExt.trans.TransDecoder;
import kettleExt.trans.TransEncoder;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;

import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.*;
import serializers.component.ComponentMetadataSerializer;
import serializers.component.ComponentPropertySerializer;
import serializers.component.ComponentSerializer;
import serializers.component.MetadataSerializer;
import serializers.hop.HopSerializer;
import serializers.step.CellSerializer;
import serializers.step.StepPropertySerializer;
import serializers.step.StepSerializer;

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

    @Inject
    public TransGraphController(SyncCacheApi cache, TaskRepository taskRepository, StepRepository stepRepository, CellRepository cellRepository, ComponentRepository componentRepository, HopRepository hopRepository) {
        this.cache = cache;
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.cellRepository = cellRepository;
        this.componentRepository = componentRepository;
        this.hopRepository = hopRepository;
    }

    /**
     * Select a graph tab
     * @param graphId
     * @return
     */
    public Result selectTask(long graphId) { return ok(views.html.index.render()); }

    /**
     * Preview task Results
     * @param graphId
     * @return
     */
    public Result previewResults(long graphId) { return ok(views.html.index.render()); }

    /**
     * Get Method, that convert a given Pentaho (.ktr) file into a mxGraph (.xml) file, and returns it back.
     * @param filename Pentaho file.
     * @return mxGraph file.
     * @throws Exception
     */
    public Result loadTrans(String filename) throws Exception {
        File file = new File("app/assets/reposity",filename);

        TransMeta transMeta = new TransMeta(file.getAbsolutePath());

        mxCodec codec = new mxCodec();
        mxGraph graph = TransEncoder.encode(transMeta);
        String graphXml = mxUtils.getPrettyXml(codec.encode(graph.getModel()));

        return ok(graphXml).as("text/html");
    }

    /**
     * Converts Task to mxGraph
     * @param graphId Task Id.
     * @return mxGraph
     */
    public Result loadTask(long graphId) throws Exception {
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
    public boolean existsTask(String name){
        boolean exists = true;
        if(taskRepository.list().isEmpty()) exists = false;
        else{
            try{
                taskRepository.getByName(name);
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
    public Result newTask(String name){
        if(existsTask(name)) forbidden();

        Task task = new Task(name);
        task = taskRepository.add(task);
        return ok(taskToJson(task));
    }

    /**
     * Get Task by name, and returns it as a Json.
     * @param name Task name
     * @return Task Json object
     */
    public Result getTask(String name) {
        if(!existsTask(name)) return ok("not found");

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
        module.addSerializer(Task.class, new serializers.task.TaskSerializer());
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
     * Get Method, that search for the task and converts it to a mxGraph.
     * @param taskName Task name.
     * @return mxGraph file
     */
    public Result openTask(String taskName){
        return null;
    }

    /**
     * Store a certain step, within the given task.
     * @param taskId Task Id.
     * @return
     */
    public Result addStep(long taskId) {
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
    public Result updateStep(long stepId) {
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
    public Result removeStep(long stepId) {
        Step step = stepRepository.get(stepId);
        stepRepository.delete(step);
        return ok();
    }

    /**
     * Get all task steps.
     * @param graphId Task id.
     * @return List of steps.
     */
    public Result getSteps(long graphId) {
        Task task = taskRepository.get(graphId);
        List<Step> steps = task.getSteps();
        return ok(taskToJson(task));
    }

    /**
     * Store a certain hop, within the given task.
     * @param taskId Task Id.
     * @return
     */
    public Result addHop(long taskId) {
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
    public Result removeHop(long hopId) {
        Hop hop = hopRepository.get(hopId);
        hopRepository.delete(hop);
        return ok();
    }

    /**
     * Close task tab.
     * @param taskId
     */
    public Result closeTab(long taskId){
        Task task = taskRepository.get(taskId);
        task.setOpen(0);
        taskRepository.add(task);
        return ok();
    }

    /**
     * Retrieve all open tabs.
     */
    public Result getOpenTabs(){
        List<String> tabs = taskRepository.getOpenTabs();
        return ok(new Gson().toJson(tabs));
    }
}
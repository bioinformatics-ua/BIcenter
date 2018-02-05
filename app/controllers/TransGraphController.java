package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import kettleExt.task.TaskEncoder;
import models.Cell;
import models.Hop;
import models.Step;
import models.Task;
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
import java.util.Map;

import org.w3c.dom.Document;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.TaskRepository;
import serializers.task.CellSerializer;
import serializers.task.HopSerializer;
import serializers.task.StepSerializer;
import serializers.task.TaskSerializer;

import javax.inject.Inject;
import javax.persistence.NoResultException;

/**
 * Controller that manages the mxGraph.
 */
public class TransGraphController extends Controller {
    private SyncCacheApi cache;

    private TaskRepository taskRepository;

    @Inject
    public TransGraphController(SyncCacheApi cache, TaskRepository taskRepository) {
        this.cache = cache;
        this.taskRepository = taskRepository;
    }

    /**
     * Select a graph tab
     * @param graphId
     * @return
     */
    public Result select_task(int graphId) { return ok(views.html.index.render()); }

    /**
     * Preview task Results
     * @param graphId
     * @return
     */
    public Result preview_results(int graphId) {
        return ok(views.html.index.render());
    }

    /**
     * Get Method, that convert a given Pentaho (.ktr) file into a mxGraph (.xml) file, and returns it back.
     * @param filename Pentaho file.
     * @return mxGraph file.
     * @throws Exception
     */
    public Result load_trans(String filename) throws Exception {
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
    public Result load_task(long graphId) throws Exception {
        Task task = this.taskRepository.get(graphId);
        mxCodec codec = new mxCodec();
        mxGraph graph = TaskEncoder.encode(task);
        String graphXml = mxUtils.getPrettyXml(codec.encode(graph.getModel()));
        return ok(graphXml).as("text/html");
    }

    /**
     * Creates a new task with the specified name, and return the mxGraph.
     * @param name Task name.
     * @return mxGrpoh file
     */
    public Result new_task(String name){
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
        if(exists) return forbidden();

        Task task = new Task(name);
        taskRepository.add(task);
        return ok(task_to_json(task));
    }

    /**
     * Get Task by name, and returns it as a Json.
     * @param name Task name
     * @return Task Json object
     */
    public Result get_task(String name) {
        Task task = taskRepository.getByName(name);
        return ok(task_to_json(task));
    }

    /**
     * Serializes Task (JPA) to Json
     * @param task
     * @return
     */
    private JsonNode task_to_json(Task task){
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Task.class, new TaskSerializer());
        module.addSerializer(Step.class, new StepSerializer());
        module.addSerializer(Hop.class, new HopSerializer());
        module.addSerializer(Cell.class, new CellSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return Json.toJson(task);
    }

    /**
     * Get Method, that search for the task and converts it to a mxGraph.
     * @param taskName Task name.
     * @return mxGraph file
     */
    public Result open_task(String taskName){
        return null;
    }

    /**
     * Post Method, that given a mxGraph (XML) file and a execution configuration (XML) specification,
     * runs the defined transformation.
     * runs the defined transformation.
     * @return Transformation Results.
     * @throws Exception
     */
    public Result run() throws Exception {
        Object execution_json = request().body().as(Map.class).get("execution");
        String execution_configuration = (String)((String[])execution_json)[0];

        Object graph_xml = request().body().as(Map.class).get("graph");
        mxGraph graph = new mxGraph();
        mxCodec codec = new mxCodec();
        Document doc = mxUtils.parseXml((String)((String[])graph_xml)[0]);
        codec.decode(doc.getDocumentElement(), graph.getModel());

        TransMeta transMeta = TransDecoder.decode(graph);

        /*
        String xml = XMLHandler.getXMLHeader() + transMeta.getXML();
        DataOutputStream dos = new DataOutputStream(KettleVFS.getOutputStream(transMeta.getFilename(), false));
        dos.write(xml.getBytes(Const.XML_ENCODING));
        dos.close();
        */

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

        TransExecutor transExecutor = TransExecutor.initExecutor(executionConfiguration, transMeta);
        new Thread(transExecutor).start();

        cache.set(transExecutor.getExecutionId(), transExecutor);

        jsonObject = new JSONObject();
        jsonObject.put("state", "RUNNING");
        jsonObject.put("executionId", transExecutor.getExecutionId());
        jsonObject.put("transName", transMeta.getName());

        return ok(jsonObject.toString()).as("text/html");
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
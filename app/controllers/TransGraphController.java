package controllers;

import kettleExt.App;
import kettleExt.TransExecutor;
import kettleExt.trans.TransDecoder;
import kettleExt.trans.TransEncoder;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;

import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import java.io.DataOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Controller that manages the mxGraph.
 */
public class TransGraphController extends Controller {
    /**
     * Get Method, that convert a given Pentaho (.ktr) file into a mxGraph (.xml) file, and returns it back.
     * @param filename Pentaho file.
     * @return mxGraph file.
     * @throws Exception
     */
    public Result load(String filename) throws Exception {
        File file = new File("app/assets/reposity",filename);

        TransMeta transMeta = new TransMeta(file.getAbsolutePath());

        mxCodec codec = new mxCodec();
        mxGraph graph = TransEncoder.encode(transMeta);
        String graphXml = mxUtils.getPrettyXml(codec.encode(graph.getModel()));

        return ok(graphXml).as("text/html");
    }

    /**
     * Post Method, that given a mxGraph (XML) file and a execution configuration (XML) specification,
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

        String xml = XMLHandler.getXMLHeader() + transMeta.getXML();
        DataOutputStream dos = new DataOutputStream(KettleVFS.getOutputStream(transMeta.getFilename(), false));
        dos.write(xml.getBytes(Const.XML_ENCODING));
        dos.close();

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

        jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("executionId", transExecutor.getExecutionId());

        return ok(jsonObject.toString()).as("text/html");
    }
}

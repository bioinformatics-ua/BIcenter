package controllers;

import play.libs.Json;
import play.mvc.Controller;

import java.util.List;
import java.util.Map;

import kettleExt.trans.TransDecoder;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;

import play.mvc.Result;
import utils.SearchFieldsProgress;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Document;

import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

public class StepController extends Controller {

    public Result inputOutputFields() throws Exception {
        Object step_name = request().body().as(Map.class).get("stepName");
        String stepName = (String) ((String[]) step_name)[0];
        Object graph_xml = request().body().as(Map.class).get("graph");
        String graphXml = (String) ((String[]) graph_xml)[0];
        Object bef = request().body().as(Map.class).get("before");
        boolean before = Boolean.valueOf((String) ((String[]) bef)[0]);

        mxGraph graph = new mxGraph();
        mxCodec codec = new mxCodec();
        Document doc = mxUtils.parseXml(graphXml);
        codec.decode(doc.getDocumentElement(), graph.getModel());

        TransMeta transMeta = TransDecoder.decode(graph);

        StepMeta stepMeta = getStep(transMeta, stepName);
        SearchFieldsProgress op = new SearchFieldsProgress(transMeta, stepMeta, before);
        op.run();
        RowMetaInterface rowMetaInterface = op.getFields();

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < rowMetaInterface.size(); i++) {
            ValueMetaInterface v = rowMetaInterface.getValueMeta(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", v.getName());
            jsonObject.put("type", v.getTypeDesc());
            jsonObject.put("length", v.getLength() < 0 ? "-" : "" + v.getLength());
            jsonObject.put("precision", v.getPrecision() < 0 ? "-" : "" + v.getPrecision());
            jsonObject.put("origin", Const.NVL(v.getOrigin(), ""));
            jsonObject.put("storageType", ValueMeta.getStorageTypeCode(v.getStorageType()));
            jsonObject.put("conversionMask", Const.NVL(v.getConversionMask(), ""));
            jsonObject.put("currencySymbol", Const.NVL(v.getCurrencySymbol(), ""));
            jsonObject.put("decimalSymbol", Const.NVL(v.getDecimalSymbol(), ""));
            jsonObject.put("groupingSymbol", Const.NVL(v.getGroupingSymbol(), ""));
            jsonObject.put("trimType", ValueMeta.getTrimTypeDesc(v.getTrimType()));
            jsonObject.put("comments", Const.NVL(v.getComments(), ""));
            jsonArray.add(jsonObject);
        }

        return ok(Json.toJson(jsonArray));
    }

    public StepMeta getStep(TransMeta transMeta, String label) {
        List<StepMeta> list = transMeta.getSteps();
        for (int i = 0; i < list.size(); i++) {
            StepMeta step = list.get(i);
            if (label.equals(step.getName()))
                return step;
        }
        return null;
    }
}
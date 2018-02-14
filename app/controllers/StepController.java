package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Inject;
import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import kettleExt.trans.TransDecoder;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;
import models.Component;
import models.ComponentMetadata;
import models.ComponentProperty;
import models.Step;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Document;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.ComponentRepository;
import repositories.StepRepository;
import serializers.component.ComponentMetadataSerializer;
import serializers.component.ComponentPropertySerializer;
import serializers.component.ComponentSerializer;
import utils.SearchFieldsProgress;

import java.util.List;
import java.util.Map;

/**
 * Controller that manages all steps of the transformation.
 */
public class StepController extends Controller {
    private final StepRepository stepRepository;

    @Inject
    public StepController(StepRepository stepRepository) {
        this.stepRepository = stepRepository;
    }

    /**
     * Edit Step page
     * @param graphId
     * @param stepId
     * @return
     */
    public Result configure(long graphId, long stepId) {
        return ok(views.html.index.render());
    }

    /**
     * Returns the input fields name of a given step.
     *
     * @return
     * @throws Exception
     */
    public Result inputFieldsName() throws Exception {
        Object step_name = request().body().as(Map.class).get("stepName");
        String stepName = (String) ((String[]) step_name)[0];
        Object graph_xml = request().body().as(Map.class).get("graph");
        String graphXml = (String) ((String[]) graph_xml)[0];

        mxGraph graph = new mxGraph();
        mxCodec codec = new mxCodec();
        Document doc = mxUtils.parseXml(graphXml);
        codec.decode(doc.getDocumentElement(), graph.getModel());

        TransMeta transMeta = TransDecoder.decode(graph);

        StepMeta stepMeta = getStep(transMeta, stepName);
        SearchFieldsProgress op = new SearchFieldsProgress(transMeta, stepMeta, true);
        op.run();
        RowMetaInterface rowMetaInterface = op.getFields();

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < rowMetaInterface.size(); i++) {
            ValueMetaInterface v = rowMetaInterface.getValueMeta(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", v.getName());
            jsonArray.add(jsonObject);
        }

        return ok(Json.toJson(jsonArray));
    }

    /**
     * Given a certain mxGraph and a certain step name, it return the input or the output fields details
     * (depending on the before value; if true it returns the input fields else it returns the output fields).
     *
     * @return Json with all desired fields details.
     * @throws Exception
     */
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

    /**
     * Returns the step meta, given a certain step label and the actual transformation meta.
     *
     * @param transMeta Current transformation meta.
     * @param label     Step label.
     * @return Step meta.
     */
    public StepMeta getStep(TransMeta transMeta, String label) {
        List<StepMeta> list = transMeta.getSteps();
        for (int i = 0; i < list.size(); i++) {
            StepMeta step = list.get(i);
            if (label.equals(step.getName()))
                return step;
        }
        return null;
    }

    /**
     * Returns Component Step Schema by Name
     *
     * @param stepId Step Id
     * @return Step Schema
     */
    public Result getSchema(long stepId) {
        Step step = stepRepository.get(stepId);
        Component component = step.getComponent();

        if (component == null) {
            return notFound();
        }

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Component.class, new ComponentSerializer());
        module.addSerializer(ComponentProperty.class, new ComponentPropertySerializer());
        module.addSerializer(ComponentMetadata.class, new ComponentMetadataSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(component));
    }
}
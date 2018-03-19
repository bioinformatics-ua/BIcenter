package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.gson.Gson;
import com.google.inject.Inject;
import diSdk.task.TaskDecoder;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;
import models.*;
import org.hibernate.Hibernate;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.*;
import serializers.component.ComponentMetadataSerializer;
import serializers.component.ComponentPropertySerializer;
import serializers.component.ComponentSerializer;
import serializers.component.MetadataSerializer;
import serializers.hop.HopSerializer;
import serializers.step.*;
import utils.SearchFieldsProgress;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller that manages all steps of the task.
 */
public class StepController extends Controller {
    private final TaskRepository taskRepository;
    private final HopRepository hopRepository;
    private final StepRepository stepRepository;
    private final StepPropertyRepository stepPropertyRepository;
    private final ComponentRepository componentRepository;
    private final ComponentPropertyRepository componentPropertyRepository;

    @Inject
    public StepController(TaskRepository taskRepository, HopRepository hopRepository, StepRepository stepRepository, StepPropertyRepository stepPropertyRepository, ComponentRepository componentRepository, ComponentPropertyRepository componentPropertyRepository) {
        this.taskRepository = taskRepository;
        this.hopRepository = hopRepository;
        this.stepRepository = stepRepository;
        this.stepPropertyRepository = stepPropertyRepository;
        this.componentRepository = componentRepository;
        this.componentPropertyRepository = componentPropertyRepository;
    }

    /**
     * Edit Step page
     *
     * @param graphId
     * @param stepId
     * @return
     */
    public Result configure(long graphId, long stepId) {
        return ok(views.html.index.render());
    }

    /**
     * Step Input Fields page
     *
     * @param graphId
     * @param stepId
     * @return
     */
    public Result showStepInput(long graphId, long stepId) {
        return ok(views.html.index.render());
    }

    /**
     * Step Output Fields page
     *
     * @param graphId
     * @param stepId
     * @return
     */
    public Result showStepOutput(long graphId, long stepId) {
        return ok(views.html.index.render());
    }

    public Result inputStepsName(long stepId) {
        List<String> stepsName = stepRepository.getSourceSteps(stepId)
                .stream().map(s -> s.getLabel()).collect(Collectors.toList());

        JSONArray jsonArray = new JSONArray();
        stepsName.stream()
                .forEach(name -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", name);
                    jsonArray.add(jsonObject);
                });
        return ok(Json.toJson(jsonArray));
    }

    /**
     * Returns the input fields name of a given step.
     *
     * @return
     * @throws Exception
     */
    public Result inputFieldsName(long stepId) throws Exception {
        Step step = stepRepository.get(stepId);
        Task task = step.getTaskSteps();
        TransMeta transMeta = TaskDecoder.decode(task);

        StepMeta stepMeta = getStep(transMeta, step.getLabel());
        SearchFieldsProgress op = new SearchFieldsProgress(transMeta, stepMeta, true);
        op.run();
        RowMetaInterface rowMetaInterface = op.getFields();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ValueMetaInterface.class, new ValueMetaInterfaceInputFields());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(rowMetaInterface.getValueMetaList()));
    }

    /**
     * Returns the labels of the output steps.
     * @param stepId
     * @return
     * @throws Exception
     */
    public Result outputStepsName(long stepId) throws Exception {
        List<String> stepsName = stepRepository.getDestinySteps(stepId)
                .stream().map(s -> s.getLabel()).collect(Collectors.toList());

        JSONArray jsonArray = new JSONArray();
        stepsName.stream()
                .forEach(name -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", name);
                    jsonArray.add(jsonObject);
                });
        return ok(Json.toJson(jsonArray));
    }

    /**
     * Given a certain mxGraph and a certain step name, it return the input or the output fields details
     * (depending on the before value; if true it returns the input fields else it returns the output fields).
     *
     * @return Json with all desired fields details.
     * @throws Exception
     */
    public Result inputOutputFields(long stepId, boolean before) throws Exception {
        Step step = stepRepository.get(stepId);
        Task task = step.getTaskSteps();
        TransMeta transMeta = TaskDecoder.decode(task);

        StepMeta stepMeta = getStep(transMeta, step.getLabel());
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
     * Returns Component Step Schema by Step Id
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

    public Result getStepName(long stepId) {
        Step step = stepRepository.get(stepId);
        return ok(step.getLabel());
    }

    /**
     * Return Step by Id
     *
     * @param stepId
     * @return
     */
    public Result getStep(long stepId) {
        Step step = stepRepository.get(stepId);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Step.class, new StepSerializer());
        module.addSerializer(Hop.class, new HopSerializer());
        module.addSerializer(Cell.class, new CellSerializer());
        module.addSerializer(Component.class, new ComponentSerializer());
        module.addSerializer(ComponentProperty.class, new ComponentPropertySerializer(stepId));
        module.addSerializer(StepProperty.class, new StepPropertySerializer());
        module.addSerializer(ComponentMetadata.class, new ComponentMetadataSerializer());
        module.addSerializer(Metadata.class, new MetadataSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(step));
    }

    /**
     * Apply changes to the given step.
     *
     * @return
     */
    public Result applyChanges(long stepId) {
        JsonNode formData = request().body().asJson();

        formData.fields().forEachRemaining(
            (node) ->
            {
                try {
                    String value = node.getValue() instanceof TextNode ? node.getValue().asText() : node.getValue().toString();
                    long componentPropertyId = Long.parseLong(node.getKey().toString());
                    StepProperty stepProperty = stepPropertyRepository.getByStepAndComponentProperty(stepId,componentPropertyId);

                    ComponentProperty componentProperty = componentPropertyRepository.get(componentPropertyId);
                    if (stepProperty == null) {
                        stepProperty = new StepProperty(value);
                        stepProperty.setComponentProperty(componentProperty);
                        stepProperty.setStep(stepRepository.get(stepId));
                        stepPropertyRepository.add(stepProperty);
                    } else {
                        stepProperty.setValue(value);
                        stepPropertyRepository.add(stepProperty);
                    }

                    if(componentProperty.getShortName().equals("stepName")){
                        Step step = stepRepository.get(stepId);
                        step.setLabel(value);
                        stepRepository.add(step);
                    }
                }
                catch(Exception e){ }
            }
        );

        return ok();
    }

    /**
     * Return ComponentProperty Ids of table elements.
     *
     * @param stepId
     * @return
     */
    public Result getTables(long stepId) {
        Step step = stepRepository.get(stepId);
        Component component = step.getComponent();
        List<ComponentProperty> componentProperties = component.getComponentProperties()
                .stream()
                .filter(cp -> cp.getType().equals("table"))
                .collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ComponentProperty.class, new TableSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(componentProperties));
    }

    /**
     * Get table value.
     *
     * @param stepId
     * @param componentId
     * @return
     */
    public Result getTableValue(long stepId, long componentId) {
        Step step = stepRepository.get(stepId);
        StepProperty stepProperty = stepPropertyRepository.getByStepAndComponentProperty(stepId, componentId);
        ObjectNode jsonObject = Json.newObject();

        JsonNode value = Json.toJson(new String[0]);
        try { value = Json.parse(stepProperty.getValue()); }
        catch(NullPointerException e){ }

        jsonObject.put("data", value);
        return ok(jsonObject);
    }

    /**
     * Return ComponentProperty Ids of condition elements.
     *
     * @param stepId
     * @return
     */
    public Result getConditions(long stepId) {
        Step step = stepRepository.get(stepId);
        Component component = step.getComponent();
        List<ComponentProperty> componentProperties = component.getComponentProperties()
                .stream()
                .filter(cp -> cp.getType().equals("condition"))
                .collect(Collectors.toList());

        JSONArray jsonArray = new JSONArray();
        for (ComponentProperty componentProperty : componentProperties) {
            JSONObject record = new JSONObject();
            record.put("id", componentProperty.getId());
            jsonArray.add(record);
        }

        return ok(Json.toJson(jsonArray));
    }

    /**
     * Get condition value.
     *
     * @param stepId
     * @param componentId
     * @return
     */
    public Result getConditionValue(long stepId, long componentId) {
        try{
            Step step = stepRepository.get(stepId);
            StepProperty stepProperty = stepPropertyRepository.getByStepAndComponentProperty(stepId, componentId);
            return ok(Json.parse(stepProperty.getValue()));
        }
        catch(NullPointerException e){
            return ok();
        }
    }

    public Result getByComponentAndShortName(long componentId, String shortName) {
        long componentPropertyId = componentPropertyRepository.getByComponentAndShortName(componentId,shortName).getId();
        return ok(String.valueOf(componentPropertyId));
    }

    public Result getInstitution(long stepId) {
        Step step = stepRepository.get(stepId);
        Long institutionId = step.getTaskSteps().getInstitution().getId();
        return ok(String.valueOf(institutionId));
    }
}
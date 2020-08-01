package controllers;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.inject.Inject;
import controllers.login.Secured;
import controllers.rbac.annotation.CheckPermission;
import diSdk.task.TaskDecoder;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;
import models.*;
import models.rbac.Category;
import models.rbac.Operation;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import play.api.libs.Files;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
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
    private final ComponentMetadataRepository componentMetadataRepository;

    @Inject
    public StepController(TaskRepository taskRepository, HopRepository hopRepository, StepRepository stepRepository, StepPropertyRepository stepPropertyRepository, ComponentRepository componentRepository, ComponentPropertyRepository componentPropertyRepository, ComponentMetadataRepository componentMetadataRepository) {
        this.taskRepository = taskRepository;
        this.hopRepository = hopRepository;
        this.stepRepository = stepRepository;
        this.stepPropertyRepository = stepPropertyRepository;
        this.componentRepository = componentRepository;
        this.componentPropertyRepository = componentPropertyRepository;
        this.componentMetadataRepository = componentMetadataRepository;
    }

    /**
     * Edit Step page
     *
     * @param graphId
     * @param stepId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.UPDATE})
    public Result configure(long institutionId, long graphId, long stepId) {
        return ok(views.html.index.render());
    }

    /**
     * Step Input Fields page
     *
     * @param graphId
     * @param stepId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result showStepInput(long institutionId, long graphId, long stepId) {
        return ok(views.html.index.render());
    }

    /**
     * Step Output Fields page
     *
     * @param graphId
     * @param stepId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result showStepOutput(long institutionId, long graphId, long stepId) {
        return ok(views.html.index.render());
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result inputStepsName(long institutionId, long stepId) {
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
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result inputFieldsName(long institutionId, long stepId) throws Exception {
        Step step = stepRepository.get(stepId);
        Task task = step.getTaskSteps();
        TransMeta transMeta = TaskDecoder.decode(task);

        StepMeta stepMeta = getStep(transMeta, step.getLabel());
        SearchFieldsProgress op = new SearchFieldsProgress(transMeta, stepMeta, true);
        op.run();
        Map<String,RowMetaInterface> rowMetaMap = op.getFields();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ValueMetaInterface.class, new ValueMetaInterfaceInputFields());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(rowMetaMap));
    }

    /**
     * Returns the labels of the output steps.
     * @param stepId
     * @return
     * @throws Exception
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result outputStepsName(long institutionId, long stepId) throws Exception {
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
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result inputOutputFields(long institutionId, long stepId, boolean before) throws Exception {
        Step step = stepRepository.get(stepId);
        Task task = step.getTaskSteps();
        TransMeta transMeta = TaskDecoder.decode(task);

        StepMeta stepMeta = getStep(transMeta, step.getLabel());
        SearchFieldsProgress op = new SearchFieldsProgress(transMeta, stepMeta, before);
        op.run();
        Map<String,RowMetaInterface> rowMetaMap = op.getFields();

        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<String, RowMetaInterface> entry : rowMetaMap.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); i++) {
                ValueMetaInterface v = entry.getValue().getValueMeta(i);
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
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getSchema(long institutionId, long stepId) {
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getStepName(long institutionId, long stepId) {
        Step step = stepRepository.get(stepId);
        return ok(step.getLabel());
    }

    /**
     * Return Step by Id
     *
     * @param stepId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getStep(long institutionId, long stepId) {
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
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.UPDATE})
    public Result applyChanges(long institutionId, long stepId) {
        Http.MultipartFormData body = request().body().asMultipartFormData();

        Map<String, Object> map = new HashMap<>();
        Map<String, String[]> bodyParams = body.asFormUrlEncoded();
        for (Map.Entry<String, String[]> entry : bodyParams.entrySet()) {
            map.put(entry.getKey(), entry.getValue()[0]);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode formData = mapper.valueToTree(map);

        Http.MultipartFormData.FilePart<Files.TemporaryFile> csvFile = body.getFile("file");

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
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getTables(long institutionId, long stepId) {
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
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getTableValue(long institutionId, long stepId, long componentId) {
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
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getConditions(long institutionId, long stepId) {
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
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getConditionValue(long institutionId, long stepId, long componentId) {
        try{
            Step step = stepRepository.get(stepId);
            StepProperty stepProperty = stepPropertyRepository.getByStepAndComponentProperty(stepId, componentId);
            return ok(Json.parse(stepProperty.getValue()));
        }
        catch(NullPointerException e){
            return ok();
        }
    }

    /**
     * Return ComponentProperty Ids of multi-select elements.
     *
     * @param stepId
     * @return
     */
    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getMultiSelects(long institutionId, long stepId) {
        Step step = stepRepository.get(stepId);
        Component component = step.getComponent();
        List<ComponentProperty> componentProperties = component.getComponentProperties()
                .stream()
                .filter(cp -> cp.getType().equals("multi-select"))
                .collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ComponentProperty.class, new TableSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(componentProperties));
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getByComponentAndShortName(long institutionId, long componentId, String shortName) {
        long componentPropertyId = componentPropertyRepository.getByComponentAndShortName(componentId,shortName).getId();
        return ok(String.valueOf(componentPropertyId));
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getMetadataByComponentAndShortName(long institutionId, long componentId, String shortName) {
        long componentMetadataId = componentMetadataRepository.getMetadataByComponentAndShortName(componentId,shortName).getId();
        return ok(String.valueOf(componentMetadataId));
    }


    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.TASK, needs = {Operation.GET})
    public Result getInstitution(long institutionId, long stepId) {
        Step step = stepRepository.get(stepId);
        Long institution = step.getTaskSteps().getInstitution().getId();
        return ok(String.valueOf(institution));
    }
}
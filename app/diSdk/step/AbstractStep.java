package diSdk.step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.ComponentMetadata;
import models.ComponentProperty;
import models.Step;
import models.StepProperty;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.w3c.dom.Element;
import play.libs.Json;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractStep implements StepEncoder, StepDecoder {
    /**
     * Converts a step vertex into a Pentaho step.
     *
     * @param step
     * @return
     * @throws Exception
     */
    @Override
    public StepMeta decodeStep(Step step, List<DatabaseMeta> databases) throws Exception {
        String stepid = step.getComponent().getName();
        String stepname = step.getLabel();

        // Get StepMetaInterface based on the Step Type.
        PluginRegistry registry = PluginRegistry.getInstance();
        PluginInterface sp = registry.findPluginWithId(StepPluginType.class, stepid);
        StepMetaInterface stepMetaInterface = (StepMetaInterface) registry.loadClass(sp);

        System.out.println(stepname);

        if (stepMetaInterface != null) {
            // Initialize the step with the default values.
            stepMetaInterface.setDefault();

            List<StepProperty> stepProperties = step.getStepProperties();

            // Get StepMetaInterface set methods.
            Method[] methods = stepMetaInterface.getClass().getMethods();
            methods = Arrays.stream(methods)
                    .filter(method -> method.getName().startsWith("set") || method.getName().equals("getStepIOMeta"))
                    .filter(method -> method.getParameterCount() == 1 || method.getName().equals("getStepIOMeta"))
                    .toArray(Method[]::new);

            for (Method method : methods) {
                if (method.getName().equals("getStepIOMeta")) {
                    List<StreamInterface> targetStreams = stepMetaInterface.getStepIOMeta().getTargetStreams();
                    if (!targetStreams.isEmpty()) {
                        stepProperties.forEach(stepProperty -> {
                            String shortName = stepProperty.getComponentProperty().getShortName();
                            if (shortName.startsWith("stream")) {
                                int streamIdx = Integer.parseInt(shortName.replace("stream", ""));
                                targetStreams.get(streamIdx).setSubject(stepProperty.getValue());
                            }
                        });
                    }

                    List<StreamInterface> infoStreams = stepMetaInterface.getStepIOMeta().getInfoStreams();
                    if (!infoStreams.isEmpty()) {
                        stepProperties.forEach(stepProperty -> {
                            String shortName = stepProperty.getComponentProperty().getShortName();
                            if (shortName.startsWith("stream")) {
                                int streamIdx = Integer.parseInt(shortName.replace("stream", ""));
                                infoStreams.get(streamIdx).setSubject(stepProperty.getValue());
                            }
                        });
                    }
                } else {
                    // Find StepProperty that holds the value of the current StepMetaInterface method.
                    String shortName = method.getName().substring(3);
                    Optional<StepProperty> optStepProperty = stepProperties.stream()
                            .filter(stepProperty -> stepProperty.getComponentProperty().getShortName().equalsIgnoreCase(shortName))
                            .findFirst();

                    System.out.println(shortName);

                    if (!optStepProperty.isPresent()) {
                        System.out.println("NOT FOUND\n");

                        // If dealing with CSVFileInput get the input fields and define them
                        if (shortName.equals("InputFields")) {
                            Optional<StepProperty> fileNameStepProperty = stepProperties.stream()
                                    .filter(stepProperty -> stepProperty.getComponentProperty().getShortName().equalsIgnoreCase("Filename"))
                                    .findFirst();

                            if (!fileNameStepProperty.isPresent())
                                continue;
                            String fileName = fileNameStepProperty.get().getValue();

                            Optional<StepProperty> delimiterStepProperty = stepProperties.stream()
                                    .filter(stepProperty -> stepProperty.getComponentProperty().getShortName().equalsIgnoreCase("Delimiter"))
                                    .findFirst();

                            if (!delimiterStepProperty.isPresent())
                                continue;
                            String delimiter = delimiterStepProperty.get().getValue();

                            try{
                                BufferedReader br = new BufferedReader(new FileReader(fileName));
                                String header = br.readLine();

                                String[] fields = new String[0];
                                if (header != null) {
                                    fields = header.split(delimiter);
                                }

                                TextFileInputField[] value = new TextFileInputField[fields.length];
                                for(int i = 0 ; i < fields.length ; i++){
                                    String field = fields[i];
                                    value[i] = new TextFileInputField();
                                    value[i].setName(field);
                                    System.out.println(field);
                                }

                                // Invoke the current method with the StepProperty value.
                                invokeMethod(stepMetaInterface, method, value, databases);

                                stepProperties.remove(fileNameStepProperty.get());
                                stepProperties.remove(delimiterStepProperty.get());


                            }catch (FileNotFoundException e){

                            }

                        }

                        continue;
                    }

                    System.out.println("FOUND");

                    //Check if the value is a checkbox. In that case, swap the values from on to true and off to false
                    String tmp = optStepProperty.get().getValue();
                    Object value = tmp;

                    if (optStepProperty.get().getComponentProperty().getType().equals("checkbox")) {
                        if (optStepProperty.get().getValue().equals("on")) {
                            value = "true";
                        } else {
                            value = "false";
                        }
                    }

                    // Check if the value is an array. In this case, properly parse it.
                    if (tmp.length() > 0 && tmp.charAt(0) == '[' && tmp.charAt(tmp.length() - 1) == ']') {
                        tmp = tmp.substring(1, tmp.length() - 1);
                        value = Arrays.asList(tmp.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")).stream()
                                .map(v -> v.replaceAll("\"", ""))
                                .collect(Collectors.toList());
                    }

                    System.out.println("VALUE - " + value.toString() + "\n");

                    // Invoke the current method with the StepProperty value.
                    invokeMethod(stepMetaInterface, method, value, databases);

                    if(!shortName.equals("Delimiter") && !shortName.equals("Filename"))
                        stepProperties.remove(optStepProperty.get());
                }
            }

            // Iterate over special Properties (Json value), such as tables.
            for (StepProperty stepProperty : stepProperties) {

                // Get all StepProperties with ComponentMetadatas.
                List<ComponentMetadata> componentMetadataList = stepProperty.getComponentProperty().getComponentMetadatas();
                if (componentMetadataList == null || componentMetadataList.isEmpty()) continue;

                // Parse Json value.
                JsonNode json = Json.parse(stepProperty.getValue());
                if (json instanceof ArrayNode) {
                    List<String> metadatasName = componentMetadataList.stream()
                            .map(metadata -> String.valueOf(metadata.getId()))
                            .collect(Collectors.toList());

                    Map<String, List<String>> metadatasMap = new HashMap<>();
                    metadatasName.forEach(name -> metadatasMap.put(name, new ArrayList<>()));

                    // Populate the ArrayList with the values of the ComponentMetadata.
                    json.iterator()
                            .forEachRemaining(node -> {
                                metadatasMap.forEach((key, list) -> {
                                    if (node.get(key) != null) {
                                        list.add(node.get(key).asText());
                                    }
                                });
                            });

                    for (Method method : methods) {
                        // Find value for the current method.
                        String shortName = method.getName().substring(3);
                        Optional<ComponentMetadata> optComponentMetadata = componentMetadataList.stream()
                                .filter(componentMetadata -> componentMetadata.getShortName().equalsIgnoreCase(shortName))
                                .findFirst();

                        if (!optComponentMetadata.isPresent()) continue;

                        // Invoke the current method with the StepProperty value.
                        String key = String.valueOf(optComponentMetadata.get().getId());
                        invokeMethod(stepMetaInterface, method, metadatasMap.get(key), databases);
                    }
                }
            }

            System.out.println("\n\n");

            StepMeta stepMeta = new StepMeta(stepid, stepname, stepMetaInterface);
            stepMeta.setLocation(step.getCell().getX(), step.getCell().getY());

            return stepMeta;
        }

        return null;
    }

    /**
     * Invoke a specific method.
     *
     * @param stepMetaInterface The object.
     * @param method            The method name.
     * @param value             The parameter value.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void invokeMethod(StepMetaInterface stepMetaInterface, Method method, Object value, List<DatabaseMeta> databases) throws InvocationTargetException, IllegalAccessException {
        // Parse method parameters.
        Class parameterType = method.getParameterTypes()[0];
        Object parameter;
        if (parameterType == Boolean.class || parameterType == boolean.class) {
            parameter = value.toString().equalsIgnoreCase("Y");
        } else if (parameterType == int.class || parameterType == Integer.class) {
            parameter = Integer.parseInt(value.toString());
        } else if (parameterType == (new String[0]).getClass()) {
            parameter = ((List<String>) value).stream().toArray(String[]::new);
        } else if (parameterType == (new boolean[0]).getClass()) {
            boolean[] tmp = new boolean[((List<String>) value).size()];
            int idx = 0;

            for (String val : (List<String>) value) {
                tmp[idx++] = val.toString().equalsIgnoreCase("Y");
            }
            parameter = tmp;
        } else if (parameterType == (new int[1]).getClass()) {
            int[] tmp = new int[((List<String>) value).size()];
            int idx = 0;

            for (String val : (List<String>) value) {
                if (!val.toString().equals(""))
                    tmp[idx++] = Integer.parseInt(val.toString());
                else
                    idx++;
            }
            parameter = tmp;
        } else if (parameterType == Condition.class) {
            try {
                parameter = buildCondition(getOperator("NONE"), (ObjectNode) Json.parse((String) value));
            } catch (Exception e) {
                parameter = null;
            }
        } else if (method.getParameterTypes()[0] == DatabaseMeta.class) {
            // Search for the database connection within the institution.
            parameter = DatabaseMeta.findDatabase(databases, value.toString());
        } else {
            parameter = parameterType.cast(value);
        }

        // Invoke method.
        method.invoke(stepMetaInterface, parameter);
    }

    /**
     * Initialize Boolean Condition.
     *
     * @param operator
     * @param json
     * @return
     */
    private Condition buildCondition(int operator, ObjectNode json) {
        Condition condition = new Condition();

        if (json.get("rules") == null) {
            condition.setLeftValuename(json.get("field").asText());
            condition.setFunction(getFunction(json.get("operator").asText()));

            ValueMetaAndData value = new ValueMetaAndData();
            ValueMeta meta = new ValueMeta(json.get("field").asText(), ValueMetaInterface.TYPE_STRING);
            value.setValueMeta(meta);
            value.setValueData(json.get("value").asText());
            condition.setRightExact(value);
        } else {
            ArrayNode children = (ArrayNode) json.get("rules");
            for (int i = 0; i < children.size(); i++) {
                ObjectNode child = (ObjectNode) children.get(i);

                Condition subCondition;
                if (i != 0)
                    subCondition = buildCondition(getOperator(json.get("condition").asText()), child);
                else
                    subCondition = buildCondition(getOperator("NONE"), child);

                condition.addCondition(subCondition);
                condition.setCondition(i, subCondition);
            }
        }

        if (operator != getOperator("NONE"))
            condition.setOperator(operator);

        return condition;
    }

    public int getOperator(String operator) {
        int operatorCode;
        switch (operator) {
            case "NOT":
                operatorCode = Condition.OPERATOR_NOT;
                break;
            case "OR":
                operatorCode = Condition.OPERATOR_OR;
                break;
            case "OR NOT":
                operatorCode = Condition.OPERATOR_OR_NOT;
                break;
            case "AND":
                operatorCode = Condition.OPERATOR_AND;
                break;
            case "AND NOT":
                operatorCode = Condition.OPERATOR_AND_NOT;
                break;
            case "XOR":
                operatorCode = Condition.OPERATOR_XOR;
                break;
            default:
                operatorCode = Condition.OPERATOR_NONE;
                break;
        }
        return operatorCode;
    }

    public int getFunction(String function) {
        int functionCode;
        switch (function) {
            case "=":
                functionCode = Condition.FUNC_EQUAL;
                break;
            case "<>":
                functionCode = Condition.FUNC_NOT_EQUAL;
                break;
            case "<":
                functionCode = Condition.FUNC_SMALLER;
                break;
            case "<=":
                functionCode = Condition.FUNC_SMALLER_EQUAL;
                break;
            case ">":
                functionCode = Condition.FUNC_LARGER;
                break;
            case ">=":
                functionCode = Condition.FUNC_LARGER_EQUAL;
                break;
            case "REGEXP":
                functionCode = Condition.FUNC_REGEXP;
                break;
            case "IS NULL":
                functionCode = Condition.FUNC_NULL;
                break;
            case "IS NOT NULL":
                functionCode = Condition.FUNC_NOT_NULL;
                break;
            case "IN LIST":
                functionCode = Condition.FUNC_IN_LIST;
                break;
            case "CONTAINS":
                functionCode = Condition.FUNC_CONTAINS;
                break;
            case "STARTS WITH":
                functionCode = Condition.FUNC_STARTS_WITH;
                break;
            case "ENDS WITH":
                functionCode = Condition.FUNC_ENDS_WITH;
                break;
            case "LIKE":
                functionCode = Condition.FUNC_LIKE;
                break;
            default:
                functionCode = Condition.FUNC_TRUE;
        }
        return functionCode;
    }

    /**
     * Converts a Pentaho step into a graph vertex.
     *
     * @param stepMeta Pentaho step.
     * @return
     * @throws Exception
     */
    @Override
    public Element encodeStep(StepMeta stepMeta) throws Exception {
        Element e = encode(stepMeta.getStepMetaInterface());
        return e;
    }

    public abstract void decode(StepMetaInterface stepMetaInterface, Step step) throws Exception;

    public abstract Element encode(StepMetaInterface stepMetaInterface) throws Exception;
}

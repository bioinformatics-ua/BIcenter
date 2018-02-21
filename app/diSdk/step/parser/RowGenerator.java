package diSdk.step.parser;

import com.google.common.base.Stopwatch;
import diSdk.step.AbstractStep;
import models.Step;
import models.StepProperty;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta;
import org.w3c.dom.Element;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RowGenerator extends AbstractStep {
    @Override
    public void decode(StepMetaInterface stepMetaInterface, Step step) throws Exception {
        RowGeneratorMeta rowGeneratorMeta = (RowGeneratorMeta) stepMetaInterface;
        rowGeneratorMeta.setDefault();

        List<StepProperty> stepProperties = step.getStepProperties();

        // Get RowGeneratorMeta set methods.
        Method[] methods = RowGeneratorMeta.class.getMethods();
        methods = Arrays.stream(methods)
                .filter(method -> method.getName().startsWith("set"))
                .filter(method -> method.getParameterCount() == 1)
                .toArray(Method[]::new);

        for (Method method : methods) {
            // Find StepProperty.
            String shortName = method.getName().substring(3);
            Optional<StepProperty> optStepProperty = stepProperties.stream()
                    .filter(stepProperty -> stepProperty.getComponentProperty().getShortName().equalsIgnoreCase(shortName))
                    .findFirst();

            if (!optStepProperty.isPresent()) continue;

            // Parse method parameters.
            Class[] parameterTypes = method.getParameterTypes();
            Object parameter;
            if (parameterTypes[0] == Boolean.class) {
                parameter = optStepProperty.get().getValue().equalsIgnoreCase("Y");
            } else {
                parameter = parameterTypes[0].cast(optStepProperty.get().getValue());
            }

            // Invoke method.
            method.invoke(rowGeneratorMeta, parameter);
        }

//        for(StepProperty stepProperty: stepProperties) {
//            switch (stepProperty.getComponentProperty().getShortName()) {
//                case "intervalInMs": {
//                    rowGeneratorMeta.setIntervalInMs(stepProperty.getValue());
//                    break;
//                }
//                case "rowLimit": {
//                    rowGeneratorMeta.setRowLimit(stepProperty.getValue());
//                    break;
//                }
//                case "rowTimeField": {
//                    rowGeneratorMeta.setRowTimeField(stepProperty.getValue());
//                    break;
//                }
//                case "lastTimeField": {
//                    rowGeneratorMeta.setLastTimeField(stepProperty.getValue());
//                    break;
//                }
//                case "neverEnding": {
//                    rowGeneratorMeta.setNeverEnding("Y".equalsIgnoreCase(stepProperty.getValue()));
//                    break;
//                }
//                case "fields": {
//                    String fields = stepProperty.getValue();
//                    JSONArray jsonArray = JSONArray.fromObject(fields);
//                    String[] fieldName = new String[jsonArray.size()];
//                    String[] fieldType = new String[jsonArray.size()];
//                    String[] fieldFormat = new String[jsonArray.size()];
//                    String[] currency = new String[jsonArray.size()];
//                    String[] decimal = new String[jsonArray.size()];
//                    String[] group = new String[jsonArray.size()];
//                    String[] value = new String[jsonArray.size()];
//                    int[] fieldLength = new int[jsonArray.size()];
//                    int[] fieldPrecision = new int[jsonArray.size()];
//                    boolean[] setEmptyString = new boolean[jsonArray.size()];
//                    for(int i=0; i<jsonArray.size(); i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        fieldName[i] = jsonObject.optString("name");
//                        fieldType[i] = jsonObject.optString("type");
//                        fieldFormat[i] = jsonObject.optString("format");
//                        currency[i] = jsonObject.optString("currencyType");
//                        decimal[i] = jsonObject.optString("decimal");
//                        group[i] = jsonObject.optString("group");
//                        value[i] = jsonObject.optString("value");
//                        fieldLength[i] = jsonObject.optInt("length", -1);
//                        fieldPrecision[i] = jsonObject.optInt("precision", -1);
//                        setEmptyString[i] = jsonObject.optBoolean("nullable");
//                    }
//                    rowGeneratorMeta.setFieldName(fieldName);
//                    rowGeneratorMeta.setFieldType(fieldType);
//                    rowGeneratorMeta.setFieldFormat(fieldFormat);
//                    rowGeneratorMeta.setCurrency(currency);
//                    rowGeneratorMeta.setDecimal(decimal);
//                    rowGeneratorMeta.setGroup(group);
//                    rowGeneratorMeta.setValue(value);
//                    rowGeneratorMeta.setFieldLength(fieldLength);
//                    rowGeneratorMeta.setFieldPrecision(fieldPrecision);
//                    rowGeneratorMeta.setSetEmptyString(setEmptyString);
//                }
//            }
//        }
    }

    @Override
    public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
        return null;
    }
}

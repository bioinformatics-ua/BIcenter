package diSdk.step.parser;

import diSdk.step.AbstractStep;
import diSdk.utils.JSONArray;
import diSdk.utils.JSONObject;
import models.Step;
import models.StepProperty;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta;
import org.w3c.dom.Element;

import java.util.List;

public class RowGenerator extends AbstractStep {
    @Override
    public void decode(StepMetaInterface stepMetaInterface, Step step) throws Exception {
        RowGeneratorMeta rowGeneratorMeta = (RowGeneratorMeta) stepMetaInterface;

        List<StepProperty> stepProperties = step.getStepProperties();
        for(StepProperty stepProperty: stepProperties) {
            switch (stepProperty.getComponentProperty().getShortName()) {
                case "intervalInMs": {
                    rowGeneratorMeta.setIntervalInMs(stepProperty.getValue());
                    break;
                }
                case "rowLimit": {
                    rowGeneratorMeta.setRowLimit(stepProperty.getValue());
                    break;
                }
                case "rowTimeField": {
                    rowGeneratorMeta.setRowTimeField(stepProperty.getValue());
                    break;
                }
                case "lastTimeField": {
                    rowGeneratorMeta.setLastTimeField(stepProperty.getValue());
                    break;
                }
                case "neverEnding": {
                    rowGeneratorMeta.setNeverEnding("Y".equalsIgnoreCase(stepProperty.getValue()));
                    break;
                }
                case "fields": {
                    String fields = stepProperty.getValue();
                    JSONArray jsonArray = JSONArray.fromObject(fields);
                    String[] fieldName = new String[jsonArray.size()];
                    String[] fieldType = new String[jsonArray.size()];
                    String[] fieldFormat = new String[jsonArray.size()];
                    String[] currency = new String[jsonArray.size()];
                    String[] decimal = new String[jsonArray.size()];
                    String[] group = new String[jsonArray.size()];
                    String[] value = new String[jsonArray.size()];
                    int[] fieldLength = new int[jsonArray.size()];
                    int[] fieldPrecision = new int[jsonArray.size()];
                    boolean[] setEmptyString = new boolean[jsonArray.size()];
                    for(int i=0; i<jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        fieldName[i] = jsonObject.optString("name");
                        fieldType[i] = jsonObject.optString("type");
                        fieldFormat[i] = jsonObject.optString("format");
                        currency[i] = jsonObject.optString("currencyType");
                        decimal[i] = jsonObject.optString("decimal");
                        group[i] = jsonObject.optString("group");
                        value[i] = jsonObject.optString("value");
                        fieldLength[i] = jsonObject.optInt("length", -1);
                        fieldPrecision[i] = jsonObject.optInt("precision", -1);
                        setEmptyString[i] = jsonObject.optBoolean("nullable");
                    }
                    rowGeneratorMeta.setFieldName(fieldName);
                    rowGeneratorMeta.setFieldType(fieldType);
                    rowGeneratorMeta.setFieldFormat(fieldFormat);
                    rowGeneratorMeta.setCurrency(currency);
                    rowGeneratorMeta.setDecimal(decimal);
                    rowGeneratorMeta.setGroup(group);
                    rowGeneratorMeta.setValue(value);
                    rowGeneratorMeta.setFieldLength(fieldLength);
                    rowGeneratorMeta.setFieldPrecision(fieldPrecision);
                    rowGeneratorMeta.setSetEmptyString(setEmptyString);
                }
            }
        }
    }

    @Override
    public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
        return null;
    }
}

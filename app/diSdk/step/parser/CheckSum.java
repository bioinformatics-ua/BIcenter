package diSdk.step.parser;

import com.mxgraph.util.mxUtils;
import diSdk.step.AbstractStep;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;
import models.Step;
import models.StepProperty;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.checksum.CheckSumMeta;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class CheckSum extends AbstractStep {

    @Override
    public void decode(StepMetaInterface stepMetaInterface, Step step) throws Exception {
        CheckSumMeta checkSumMeta = (CheckSumMeta) stepMetaInterface;
        List<StepProperty> stepProperties = step.getStepProperties();
        for(StepProperty stepProperty: stepProperties){
            switch(stepProperty.getComponentProperty().getShortName()){
                case "checkSumType": {
                    checkSumMeta.setCheckSumType(Integer.parseInt(stepProperty.getValue()));
                    break;
                }
                case "resultfieldName": {
                    checkSumMeta.setResultFieldName(stepProperty.getValue());
                    break;
                }
                case "compatibilityMode": {
                    checkSumMeta.setCompatibilityMode("true".equals(stepProperty.getValue()));
                    break;
                }
                case "fields": {
                    String fields = stepProperty.getValue();
                    JSONArray jsonArray = JSONArray.fromObject(fields);
                    String[] fieldName = new String[jsonArray.size()];
                    for(int i=0; i<jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        fieldName[i] = jsonObject.optString("name");
                    }
                    checkSumMeta.setFieldName(fieldName);
                }
            }
        }
    }

    @Override
    public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
        CheckSumMeta checkSumMeta = (CheckSumMeta) stepMetaInterface;
        Document doc = mxUtils.createDocument();
        Element e = doc.createElement("Step");
        return e;
    }

}


package kettleExt.trans.steps;

import java.util.List;

import kettleExt.trans.step.AbstractStep;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.checksum.CheckSumMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

public class CheckSum extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
            CheckSumMeta checkSumMeta = (CheckSumMeta) stepMetaInterface;

            checkSumMeta.setCheckSumType(Integer.parseInt(cell.getAttribute("checkSumType")));
            checkSumMeta.setResultFieldName(cell.getAttribute("resultfieldName"));
            checkSumMeta.setCompatibilityMode("true".equals(cell.getAttribute("compatibilityMode")));

            String fields = cell.getAttribute("fields");
            JSONArray jsonArray = JSONArray.fromObject(fields);
            String[] fieldName = new String[jsonArray.size()];
            for(int i=0; i<jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                fieldName[i] = jsonObject.optString("name");
            }
            checkSumMeta.setFieldName(fieldName);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
            CheckSumMeta checkSumMeta = (CheckSumMeta) stepMetaInterface;
            Document doc = mxUtils.createDocument();
            Element e = doc.createElement("Step");

            e.setAttribute("checkSumType", String.valueOf(checkSumMeta.getTypeByDesc()));
            e.setAttribute("resultType", String.valueOf(checkSumMeta.getResultType()));
            e.setAttribute("resultfieldName", checkSumMeta.getResultFieldName());
            e.setAttribute("compatibilityMode", String.valueOf(checkSumMeta.isCompatibilityMode()));

            JSONArray jsonArray = new JSONArray();
            String[] fieldName = checkSumMeta.getFieldName();
            for(int j=0; j<fieldName.length; j++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", fieldName[j]);
                jsonArray.add(jsonObject);
            }

            e.setAttribute("fields", jsonArray.toString());

            return e;
	}

}

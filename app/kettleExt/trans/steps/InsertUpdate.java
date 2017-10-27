package kettleExt.trans.steps;

import java.util.List;

import kettleExt.trans.step.AbstractStep;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.insertupdate.InsertUpdateMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

public class InsertUpdate extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		InsertUpdateMeta insertUpdateMeta = (InsertUpdateMeta) stepMetaInterface;
		
		insertUpdateMeta.setDatabaseMeta(DatabaseMeta.findDatabase(databases, cell.getAttribute("connection")));
		insertUpdateMeta.setSchemaName(cell.getAttribute("schema"));
		insertUpdateMeta.setTableName(cell.getAttribute("table"));
		insertUpdateMeta.setCommitSize(cell.getAttribute("commit"));
		insertUpdateMeta.setUpdateBypassed("Y".equalsIgnoreCase(cell.getAttribute("update_bypassed")));
		
		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("key"));
		String[] keyLookup = new String[jsonArray.size()];
		String[] keyCondition = new String[jsonArray.size()];
		String[] keyStream1 = new String[jsonArray.size()];
		String[] keyStream2 = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			keyLookup[i] = jsonObject.optString("keyLookup");
			keyCondition[i] = jsonObject.optString("keyCondition");
			keyStream1[i] = jsonObject.optString("keyStream1");
			keyStream2[i] = jsonObject.optString("keyStream2");
		}

		insertUpdateMeta.setKeyLookup(keyLookup);
		insertUpdateMeta.setKeyCondition(keyCondition);
		insertUpdateMeta.setKeyStream(keyStream1);
		insertUpdateMeta.setKeyStream2(keyStream2);
		
		jsonArray = JSONArray.fromObject(cell.getAttribute("value"));
		String[] updateLookup = new String[jsonArray.size()];
		String[] updateStream = new String[jsonArray.size()];
		Boolean[] update = new Boolean[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			updateLookup[i] = jsonObject.optString("updateLookup");
			updateStream[i] = jsonObject.optString("updateStream");
			update[i] = jsonObject.optBoolean("update");
		}

		insertUpdateMeta.setUpdateLookup(updateLookup);
		insertUpdateMeta.setUpdateStream(updateStream);
		insertUpdateMeta.setUpdate(update);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		InsertUpdateMeta insertUpdateMeta = (InsertUpdateMeta) stepMetaInterface;
		
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement("Step");
		
		DatabaseMeta databaseMeta = insertUpdateMeta.getDatabaseMeta();
		e.setAttribute("connection", databaseMeta == null ? "" : databaseMeta.getName());
		e.setAttribute("schema", insertUpdateMeta.getSchemaName());
		e.setAttribute("table", insertUpdateMeta.getTableName());
		e.setAttribute("commit", insertUpdateMeta.getCommitSizeVar());
		e.setAttribute("update_bypassed", insertUpdateMeta.isUpdateBypassed() ? "Y" : "N");
		
		JSONArray jsonArray = new JSONArray();
		String[] keyStream1 = insertUpdateMeta.getKeyStream();
		String[] keyStream2 = insertUpdateMeta.getKeyStream2();
		String[] keyLookup = insertUpdateMeta.getKeyLookup();
		String[] keyCondition = insertUpdateMeta.getKeyCondition();
		for(int i=0; i<keyStream1.length; i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("keyLookup", keyLookup[i]);
			jsonObject.put("keyCondition", keyCondition[i]);
			jsonObject.put("keyStream1", keyStream1[i]);
			jsonObject.put("keyStream2", keyStream2[i]);
			jsonArray.add(jsonObject);
		}
		e.setAttribute("key", jsonArray.toString());
		
		jsonArray = new JSONArray();
		Boolean[] update = insertUpdateMeta.getUpdate();
		String[] updateLookup = insertUpdateMeta.getUpdateLookup();
		String[] updateStream = insertUpdateMeta.getUpdateStream();
		for(int i=0; i<updateLookup.length; i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("updateLookup", updateLookup[i]);
			jsonObject.put("updateStream", updateStream[i]);
			jsonObject.put("update", update[i]);
			jsonArray.add(jsonObject);
		}
		e.setAttribute("value", jsonArray.toString());
		
		return e;
	}

}

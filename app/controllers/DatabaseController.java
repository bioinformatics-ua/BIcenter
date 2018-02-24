package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import diSdk.utils.JSONArray;
import diSdk.utils.JSONObject;
import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.PluginTypeListener;
import org.springframework.core.io.ClassPathResource;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.util.*;

/**
 * Controller that manages task database connections.
 */
public class DatabaseController extends Controller {
    /*
    public static final SortedMap<String, DatabaseInterface> connectionMap = new TreeMap<String, DatabaseInterface>();
    public static final Map<String, String> connectionNametoID = new HashMap<String, String>();

    static {
        PluginRegistry registry = PluginRegistry.getInstance();

        List<PluginInterface> plugins = registry.getPlugins(DatabasePluginType.class);

        PluginTypeListener databaseTypeListener = new DatabaseTypeListener(registry) {
            public void databaseTypeAdded(String pluginName, DatabaseInterface databaseInterface) {
                connectionMap.put(pluginName, databaseInterface);
                connectionNametoID.put(pluginName, databaseInterface.getPluginId());
            }

            public void databaseTypeRemoved(String pluginName) {
                connectionMap.remove(pluginName);
                connectionNametoID.remove(pluginName);
            }
        };

        registry.addPluginListener(DatabasePluginType.class, databaseTypeListener);
        for (PluginInterface plugin : plugins) {
            databaseTypeListener.pluginAdded(plugin);
        }

    }

    private static abstract class DatabaseTypeListener implements PluginTypeListener {
        private final PluginRegistry registry;

        public DatabaseTypeListener(PluginRegistry registry) {
            this.registry = registry;
        }

        @Override
        public void pluginAdded(Object serviceObject) {
            PluginInterface plugin = (PluginInterface) serviceObject;
            String pluginName = plugin.getName();
            try {
                DatabaseInterface databaseInterface = (DatabaseInterface) registry.loadClass(plugin);
                databaseInterface.setPluginId(plugin.getIds()[0]);
                databaseInterface.setName(pluginName);
                databaseTypeAdded(pluginName, databaseInterface);
            } catch (KettleException e) {
                System.out.println("Could not create connection entry for "
                        + pluginName + ".  "
                        + e.getCause().getClass().getName());
                LogChannel.GENERAL
                        .logError("Could not create connection entry for "
                                + pluginName + ".  "
                                + e.getCause().getClass().getName());
            }
        }

        public abstract void databaseTypeAdded(String pluginName, DatabaseInterface databaseInterface);

        @Override
        public void pluginRemoved(Object serviceObject) {
            PluginInterface plugin = (PluginInterface) serviceObject;
            String pluginName = plugin.getName();
            databaseTypeRemoved(pluginName);
        }

        public abstract void databaseTypeRemoved(String pluginName);

        @Override
        public void pluginChanged(Object serviceObject) {
            pluginRemoved(serviceObject);
            pluginAdded(serviceObject);
        }

    }

    public Result loadAccessData() throws IOException {

        JSONArray jsonArray = new JSONArray();
        for ( String value : connectionMap.keySet() ) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", connectionNametoID.get(value));
            jsonObject.put("text", value);
            jsonArray.add(jsonObject);
        }
        return ok(Json.toJson(jsonArray));
    }

    public Result loadAccessMethod(@RequestParam String accessData) throws IOException {
        Iterator<Map.Entry<String, String>> iter = connectionNametoID.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            if(accessData.equals(entry.getValue())) {
                accessData = entry.getKey();
                break;
            }
        }

        DatabaseInterface database = connectionMap.get( accessData );
        int[] acc = database.getAccessTypeList();

        JSONArray jsonArray = new JSONArray();
        for ( int value : acc ) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", value);
            jsonObject.put("text", DatabaseMeta.getAccessTypeDescLong( value ));
            jsonArray.add(jsonObject);
        }

        return ok(Json.toJson(jsonArray));

    }

    public JsonNode loadAccessSettings(@RequestParam int accessMethod) throws IOException {
        String fragment = "";
        switch ( accessMethod ) {
            case DatabaseMeta.TYPE_ACCESS_JNDI:
                ClassPathResource cpr = new ClassPathResource("common_jndi.json", getClass());
                fragment = FileUtils.readFileToString(cpr.getFile(), "utf-8");
                break;
            case DatabaseMeta.TYPE_ACCESS_NATIVE:
                cpr = new ClassPathResource("common_native.json", getClass());
                fragment = FileUtils.readFileToString(cpr.getFile(), "utf-8");
                break;
            case DatabaseMeta.TYPE_ACCESS_ODBC:
                cpr = new ClassPathResource("common_odbc.json", getClass());
                fragment = FileUtils.readFileToString(cpr.getFile(), "utf-8");
                break;
        }

        return(Json.toJson(fragment));

    }
    */
}

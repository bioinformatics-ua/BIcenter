package diSdk.step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.ComponentMetadata;
import models.Step;
import models.StepProperty;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Element;
import play.libs.Json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collector;
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
    public StepMeta decodeStep(Step step) throws Exception {
        String stepid = step.getComponent().getName();
        String stepname = step.getLabel();

        PluginRegistry registry = PluginRegistry.getInstance();
        PluginInterface sp = registry.findPluginWithId(StepPluginType.class, stepid);
        StepMetaInterface stepMetaInterface = (StepMetaInterface) registry.loadClass(sp);

        if (stepMetaInterface != null) {
            stepMetaInterface.setDefault();

            List<StepProperty> stepProperties = step.getStepProperties();

            // Get RowGeneratorMeta set methods.
            Method[] methods = stepMetaInterface.getClass().getMethods();
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

                String tmp = optStepProperty.get().getValue();
                Object value = tmp;
                if (tmp.charAt(0) == '[' && tmp.charAt(tmp.length() - 1) == ']') {
                    tmp = tmp.substring(1, tmp.length() - 1);
                    value = Arrays.asList(tmp.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")).stream()
                            .map(v -> v.replaceAll("\"", ""))
                            .collect(Collectors.toList());
                }
                invokeMethod(stepMetaInterface, method, value);

                stepProperties.remove(optStepProperty.get());
            }

            for (StepProperty stepProperty : stepProperties) {
                List<ComponentMetadata> componentMetadataList = stepProperty.getComponentProperty().getComponentMetadatas();
                if (componentMetadataList == null || componentMetadataList.isEmpty()) continue;

                JsonNode json = Json.parse(stepProperty.getValue());
                if (json instanceof ArrayNode) {
                    List<String> metadatasName = componentMetadataList.stream()
                            .map(metadata -> String.valueOf(metadata.getId()))
                            .collect(Collectors.toList());

                    Map<String, List<String>> metadatasMap = new HashMap<>();
                    metadatasName.forEach(name -> metadatasMap.put(name, new ArrayList<>()));

                    json.iterator()
                            .forEachRemaining(node -> {
                                metadatasMap.forEach((key, list) -> {
                                    if (node.get(key) != null) {
                                        list.add(node.get(key).asText());
                                    }
                                });
                            });

                    for (Method method : methods) {
                        // Find
                        String shortName = method.getName().substring(3);
                        Optional<ComponentMetadata> optComponentMetadata = componentMetadataList.stream()
                                .filter(componentMetadata -> componentMetadata.getShortName().equalsIgnoreCase(shortName))
                                .findFirst();

                        if (!optComponentMetadata.isPresent()) continue;

                        String key = String.valueOf(optComponentMetadata.get().getId());
                        invokeMethod(stepMetaInterface, method, metadatasMap.get(key));
                    }
                }
            }


            StepMeta stepMeta = new StepMeta(stepid, stepname, stepMetaInterface);
            /* Handle info general to all step types... */
            /*
            stepMeta.setDescription(cell.getAttribute("description"));
            stepMeta.setCopiesString(cell.getAttribute("copies"));
            String sdistri = cell.getAttribute( "distribute" );

            if ( sdistri == null ) {
                stepMeta.setDistributes(true);
            } else {
                stepMeta.setDistributes("Y".equalsIgnoreCase( sdistri ));
            }

            // Determine the row distribution
            String rowDistributionCode = cell.getAttribute( "custom_distribution" );
            stepMeta.setRowDistribution(PluginRegistry.getInstance().loadClass(RowDistributionPluginType.class, rowDistributionCode, RowDistributionInterface.class ));
            */

            // Handle GUI information: location & drawstep?
            stepMeta.setLocation(step.getCell().getX(), step.getCell().getY());
            //stepMeta.setDraw("Y".equalsIgnoreCase( cell.getAttribute( "draw" ) ));

            //stepMeta.setClusterSchemaName(cell.getAttribute( "cluster_schema" ));

            return stepMeta;
        }

        return null;
    }

    private void invokeMethod(StepMetaInterface stepMetaInterface, Method method, Object value) throws InvocationTargetException, IllegalAccessException {
        // Parse method parameters.
        Class parameterType = method.getParameterTypes()[0];
        Object parameter;
        if (parameterType == Boolean.class || parameterType == boolean.class) {
            parameter = value.toString().equalsIgnoreCase("Y");
        } else if (parameterType == int.class || parameterType == Integer.class) {
            parameter = Integer.parseInt(value.toString());
        } else if (parameterType == (new String[1]).getClass()) {
            parameter = ((List<String>) value).stream().toArray(String[]::new);
        } else if (parameterType == (new boolean[1]).getClass()) {
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
                tmp[idx++] = Integer.parseInt(val.toString());
            }
            parameter = tmp;
        } else {
            parameter = parameterType.cast(value);
        }


        // Invoke method.
        method.invoke(stepMetaInterface, parameter);
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

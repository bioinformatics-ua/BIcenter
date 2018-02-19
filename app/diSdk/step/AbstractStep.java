package diSdk.step;

import models.Step;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Element;

public abstract class AbstractStep implements StepEncoder, StepDecoder {
    /**
     * Converts a step vertex into a Pentaho step.
     * @param step
     * @return
     * @throws Exception
     */
    @Override
    public StepMeta decodeStep(Step step) throws Exception {
        String stepid = step.getComponent().getName();
        String stepname = step.getLabel();

        PluginRegistry registry = PluginRegistry.getInstance();
        PluginInterface sp = registry.findPluginWithId( StepPluginType.class, stepid );
        StepMetaInterface stepMetaInterface = (StepMetaInterface) registry.loadClass( sp );

        if(stepMetaInterface != null) {
            decode(stepMetaInterface,step);

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

    /**
     * Converts a Pentaho step into a graph vertex.
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

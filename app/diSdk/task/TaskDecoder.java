package diSdk.task;

import diSdk.PluginFactory;
import diSdk.step.StepDecoder;
import models.Hop;
import models.Step;
import models.Task;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.missing.MissingTrans;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Responsible for decoding tasks to transformations.
 * @author leonardo
 */
public class TaskDecoder {
    /**
     * Convert a Task (JPA) into a pentaho transaction.
     * @param task
     * @return
     * @throws Exception
     */
    public static TransMeta decode(Task task) throws Exception {
        // Assign transformation settings.
        TransMeta transMeta = new TransMeta();
        transMeta.setName(task.getName());
        transMeta.setDescription(task.getDescription());

        // Instantiate all steps.
        initSteps(task,transMeta);

        // Instantiate all hops.
        initHops(task,transMeta);

        return transMeta;
    }

    private static void initSteps(Task task, TransMeta transMeta) throws Exception {
        List<Step> steps = task.getSteps();
        for(Step step : steps){
            StepDecoder stepDecoder = (StepDecoder) PluginFactory.getBean(step.getComponent().getName());
            StepMeta stepMeta = stepDecoder.decodeStep(step);
            stepMeta.setParentTransMeta( transMeta );
            if (stepMeta.isMissing()) {
                transMeta.addMissingTrans((MissingTrans) stepMeta.getStepMetaInterface());
            }

            StepMeta check = transMeta.findStep(stepMeta.getName());
            if (check != null) {
                if (!check.isShared()) {
                    // Don't overwrite shared objects
                    transMeta.addOrReplaceStep(stepMeta);
                } else {
                    check.setDraw(stepMeta.isDrawn()); // Just keep the drawn flag and location
                    check.setLocation(stepMeta.getLocation());
                }
            } else {
                transMeta.addStep(stepMeta); // Simply add it.
            }
        }

        // Have all StreamValueLookups, etc. reference the correct source steps...
        for (int i = 0; i < transMeta.nrSteps(); i++) {
            StepMeta stepMeta = transMeta.getStep(i);
            StepMetaInterface sii = stepMeta.getStepMetaInterface();
            if (sii != null) {
                sii.searchInfoAndTargetSteps(transMeta.getSteps());
            }
        }
    }

    private static void initHops(Task task, TransMeta transMeta){
        List<Hop> hops = task.getHops();
        for(Hop hop : hops){
            Step source = hop.getSource();
            Step target = hop.getDestiny();

            TransHopMeta hopinf = new TransHopMeta(null, null, true);
            String[] stepNames = transMeta.getStepNames();
            for (int j = 0; j < stepNames.length; j++) {
                if (stepNames[j].equalsIgnoreCase(source.getLabel()))
                    hopinf.setFromStep(transMeta.getStep(j));
                if (stepNames[j].equalsIgnoreCase(target.getLabel()))
                    hopinf.setToStep(transMeta.getStep(j));
            }
            transMeta.addTransHop(hopinf);
        }
    }
}

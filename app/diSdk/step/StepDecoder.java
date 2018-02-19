package diSdk.step;

import models.Step;
import org.pentaho.di.trans.step.StepMeta;

public interface StepDecoder {
    StepMeta decodeStep(Step step) throws Exception;
}

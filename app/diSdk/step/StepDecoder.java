package diSdk.step;

import models.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMeta;

import java.util.List;

public interface StepDecoder {
    StepMeta decodeStep(Step step, List<DatabaseMeta> databases) throws Exception;
}

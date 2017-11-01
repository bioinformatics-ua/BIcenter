package kettleExt.trans.step;

import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Element;

public interface StepEncoder {

	Element encodeStep(StepMeta stepMeta) throws Exception;
	
}

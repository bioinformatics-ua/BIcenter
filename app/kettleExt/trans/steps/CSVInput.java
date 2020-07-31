package kettleExt.trans.steps;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import kettleExt.trans.step.AbstractStep;
import kettleExt.utils.StringEscapeHelper;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.csvinput.CsvInputMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class CSVInput extends AbstractStep {

    @Override
    public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
        CsvInputMeta csvInputMeta = (CsvInputMeta) stepMetaInterface;

        csvInputMeta.setDelimiter(cell.getAttribute("delimiter"));
        csvInputMeta.setDelimiter(cell.getAttribute("enclosure"));

        csvInputMeta.setHeaderPresent("true".equalsIgnoreCase(cell.getAttribute( "headerRow" )));
    }

    @Override
    public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
        //TODO: Analyze this...It seems to not be doing anything...
        CsvInputMeta csvInputMeta = (CsvInputMeta) stepMetaInterface;

        Document doc = mxUtils.createDocument();
        Element e = doc.createElement("Step");

        e.setAttribute("filename", csvInputMeta.getFilename());

        e.setAttribute("delimiter", csvInputMeta.getDelimiter());
        e.setAttribute("enclosure", csvInputMeta.getEnclosure());

        e.setAttribute("lazy_conversion_active", Boolean.toString(csvInputMeta.isLazyConversionActive()));
        e.setAttribute("header_row_present", Boolean.toString(csvInputMeta.isHeaderPresent()));


        return e;
    }

}

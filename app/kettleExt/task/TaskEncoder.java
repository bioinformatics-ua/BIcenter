package kettleExt.task;

import com.google.inject.Inject;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import kettleExt.utils.SvgImageUrl;
import models.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import repositories.*;

import java.util.HashMap;
import java.util.List;

public class TaskEncoder {
    /**
     * Convert a JPA Task into a mxGraph.
     * @param task Task object.
     * @return mxGraph.
     * @throws Exception
     */
    public static mxGraph encode(Task task) throws Exception {
        mxGraph graph = new mxGraph();
        graph.getModel().beginUpdate();

        try {
            mxCell parent = (mxCell) graph.getDefaultParent();
            Document doc = mxUtils.createDocument();

            HashMap<Long, Object> cells = new HashMap<Long, Object>();

            List<Step> steps = task.getSteps();
            for(Step step : steps){
                Element stepValue = doc.createElement("Step");
                stepValue.setAttribute("stepId", Long.toString(step.getId()));
                stepValue.setAttribute("label", step.getLabel());
                stepValue.setAttribute("vertexId", Integer.toString(step.getGraphId()));
                Component component = step.getComponent();
                stepValue.setAttribute("component", component.getName());

                Cell stepCell = step.getCell();
                Object cell = graph.insertVertex(parent, null, stepValue, stepCell.getX(), stepCell.getY(), stepCell.getWidth(), stepCell.getHeight(), "icon;image=" + SvgImageUrl.getUrl(component.getName(), SvgImageUrl.Size_Middle));
                cells.put(step.getId(), cell);
            }

            List<Hop> hops = task.getHops();
            for(Hop hop : hops) {
                Element hopValue = doc.createElement("Step");
                hopValue.setAttribute("hopId", Long.toString(hop.getId()));
                long source = hop.getSource().getId();
                long destiny = hop.getDestiny().getId();
                graph.insertEdge(parent, null, hopValue, cells.get(source), cells.get(destiny));
            }
        }
        finally {
            graph.getModel().endUpdate();
        }

        return graph;
    }
}

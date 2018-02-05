package kettleExt.task;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import models.Task;
import org.w3c.dom.Document;

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
        }
        finally {
            graph.getModel().endUpdate();
        }

        return graph;
    }
}

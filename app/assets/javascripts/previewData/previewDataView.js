define('PreviewDataView', ['View', 'jsRoutes', 'Router', 'Task', 'Execution'], function (View,jsRoutes,Router,Task,Execution) {
    var PreviewDataView = function (controller) {
        View.call(this, controller, 'previewData');
    };

    // Inheritance from super class
    PreviewDataView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    PreviewDataView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    PreviewDataView.prototype.loadTask = function(execution,task) {
        var html = JST['previewData']({
            execution: execution,
            task: task.name,
        });
        this.$container.html(html);
        this._loadViewComponents();

        // Render the transformation with a success or failure icon on each step.
        this.renderGraph(task.id, this.$elements.preview_graph[0]);
    };

    /**
     * Render a mxGraph that only allows cell selection.
     * @param taskId
     * @param container The mxGraph div.
     */
    PreviewDataView.prototype.renderGraph = function(taskId, container){
        // Checks if the browser is supported
        if (!mxClient.isBrowserSupported())
        {
            // Displays an error message if the browser is not supported.
            mxUtils.error('Browser is not supported!', 200, false);
        }
        else
        {
            // Disables the built-in context menu
            mxEvent.disableContextMenu(container);

            // Creates the graph inside the given container
            var graph = this.graph = new mxGraph(container);
            var node = mxUtils.load('/assets/lib/mxgraph2/style/default-style.xml').getDocumentElement();
            var dec = new mxCodec(node.ownerDocument);
            dec.decode(node, graph.getStylesheet());

            graph.setConnectable(false);
            graph.setCellsEditable(false);
            graph.setCellsDeletable(false);
            graph.setCellsMovable(false);
            graph.setCellsResizable(false);

            var context = this;
            Task.loadTask(taskId, function (graphModel) {
                var doc = mxUtils.parseXml(graphModel);
                var codec = new mxCodec(doc);
                codec.decode(doc.documentElement, graph.getModel());

                graph.resizeContainer = false;
                this.graph = graph;
                graph.getSelectionModel().addListener(mxEvent.UNDO, function(sender, evt) {
                    var cell = evt.getProperty('edit').changes[0].removed[0];
                    if (cell.isVertex()) {
                        var stepId = context.stepMap[cell.value];
                        var executionId = context.controller.executionId;
                        var configStepUrl = jsRoutes.controllers.ExecutionController.previewStep(context.controller.graphId,executionId,stepId).url;
                        Router.navigate(configStepUrl);
                    }
                });
                context.requestResults();
            });
        }
    };

    PreviewDataView.prototype.requestResults = function() {
        // Request for transformation results.
        var context = this;
        Execution.result(context.controller.executionId,
            function (data) {
                if (JSON.parse(data)['finished'] == true) {
                    var headerController = app.modules.HeaderModule.controllers.HeaderController;
                    headerController.view.removeTransNotification(context.executionId);
                    context.updateStatus(context.graph, JSON.parse(data)['stepStatus']);
                }
                context.data = JSON.parse(data).dataRows;
            }
        );
    };

    /**
     * Draws a success/failure icon for each transformation step.
     * @param stepStatus All step status, stating the execution output.
     */
    PreviewDataView.prototype.updateStatus = function(graph,status){
        var edges = graph.getModel().getChildCells(graph.getDefaultParent(), false, true);
        _.each(edges, function (edge) { graph.cellLabelChanged(edge,''); });

        var context = this;
        var vertexes = graph.getModel().getChildCells(graph.getDefaultParent(), true, false);
        _.each(status, function(stat){
            _.each(vertexes, function(vertex){
                if(vertex.getAttribute('label') == stat.stepName) {

                    context.stepMap = context.stepMap || {};
                    context.stepMap[vertex.value.getAttribute('label')] = vertex.value.getAttribute('stepId');
                    graph.cellLabelChanged(vertex,vertex.value.getAttribute('label'));

                    var overlays = graph.getCellOverlays(vertex) || [];

                    for(var k=0; k<overlays.length; k++) {
                        var overlay = overlays[k];

                        if(overlay.align == mxConstants.ALIGN_RIGHT && overlay.verticalAlign == mxConstants.ALIGN_TOP
                            && overlay.offset.x == 0 && overlay.offset.y == 0) {
                            graph.removeCellOverlay(vertex, overlay);
                        }
                    }

                    if(stat.status == 1) {
                        var overlay = new mxCellOverlay(new mxImage('/assets/lib/mxgraph2/editors/images/overlays/false.png', 16, 16), stat.logText, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP);
                        graph.addCellOverlay(vertex, overlay);
                    }
                    else if(stat.status == 0) {
                        var overlay = new mxCellOverlay(new mxImage('/assets/lib/mxgraph2/editors/images/overlays/true.png', 16, 16), null, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP);
                        graph.addCellOverlay(vertex, overlay);
                    }
                    else{
                        var overlay = new mxCellOverlay(new mxImage('/assets/lib/mxgraph2/editors/images/overlays/unknown.png', 16, 16), null, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP);
                        graph.addCellOverlay(vertex, overlay);
                    }
                    return;
                }
            });
        });
    }

    return PreviewDataView;
});
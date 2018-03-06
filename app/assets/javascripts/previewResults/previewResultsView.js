define('PreviewResultsView', ['View','Task','Execution'], function (View,Task,Execution) {
    var PreviewResultsView = function (controller) {
        View.call(this, controller, 'previewResults');
    };

    // Inheritance from super class
    PreviewResultsView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    PreviewResultsView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        var headerController = app.modules.HeaderModule.controllers.HeaderController;
        this.transName = headerController.transName;
        this.executionId = headerController.executionId;
        this.$elements.preview_title.text("Preview "+this.transName);

        // Render the transformation with a success icon on each step.
        this.renderGraph(this.$elements.preview_graph[0]);
    };

    /**
     * Render a mxGraph that only allows cell selection.
     * @param container The mxGraph div.
     */
    PreviewResultsView.prototype.renderGraph = function(container){
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
            Task.getTask(this.transName, function (task) {
                Task.loadTask(task.id, function (graphModel) {
                    var doc = mxUtils.parseXml(graphModel);
                    var codec = new mxCodec(doc);
                    codec.decode(doc.documentElement, graph.getModel());

                    graph.resizeContainer = false;
                    graph.getSelectionModel().addListener(mxEvent.UNDO, function(sender, evt)
                    {
                        context.$elements.preview_table.hide();
                        var cell = evt.getProperty('edit').changes[0].removed[0]
                        context.$elements.preview_data.show();
                        context.$elements.preview_data_title.text(cell.value);

                        context.$elements.data_table.empty();

                        var currentRows = context.data.filter(function (el) {
                            return el.stepName == cell.value;
                        });

                        var columns = currentRows[0].keyValues.map(x => x.key);
                        var $thead = $('<thead>');
                        var $tr = $('<tr>');

                        for(var i=0; i<columns.length; i++) {
                            $tr.append(
                                $('<th>').text(columns[i])
                            );
                        }
                        $thead.append($tr);
                        context.$elements.data_table.append($thead);

                        var $tbody = $('<tbody>');
                        for(var i=0; i<currentRows.length; i++) {
                            var $tr = $('<tr>');
                            for(var j=0; j<columns.length; j++) {
                                $tr.append(
                                    $('<td>').text(currentRows[i].keyValues[j].value)
                                );
                            }
                            $tbody.append($tr);
                        }
                        context.$elements.data_table.append($tbody);
                        context.$elements.data_table.DataTable();
                    });

                    this.graph = graph;
                    context.requestResults();
                });
            });
        }
    }

    /**
     * Draws a success/failure icon for each transformation step.
     * @param stepStatus All step status, stating the execution output.
     */
    PreviewResultsView.prototype.updateStatus = function(graph,status){
        for(var i=0; i<status.length; i++) {
            var cells = graph.getModel().getChildCells(graph.getDefaultParent(), true, false);
            for(var j=0; j<cells.length; j++) {
                var cell = cells[j];
                if(cell.getAttribute('label') == status[i].stepName) {
                    graph.cellLabelChanged(cell,cell.getAttribute('label',''))
                    var overlays = graph.getCellOverlays(cell) || [];
                    for(var k=0; k<overlays.length; k++) {
                        var overlay = overlays[k];

                        if(overlay.align == mxConstants.ALIGN_RIGHT && overlay.verticalAlign == mxConstants.ALIGN_TOP
                            && overlay.offset.x == 0 && overlay.offset.y == 0) {
                            graph.removeCellOverlay(cell, overlay);
                        }
                    }

                    if(status[i].stepStatus > 0) {
                        var overlay = new mxCellOverlay(new mxImage('/assets/lib/mxgraph2/editors/images/overlays/false.png', 16, 16), status[i].logText, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP);
                        graph.addCellOverlay(cell, overlay);
                    } else {
                        var overlay = new mxCellOverlay(new mxImage('/assets/lib/mxgraph2/editors/images/overlays/true.png', 16, 16), null, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP);
                        graph.addCellOverlay(cell, overlay);
                    }
                    break;
                }
            }
        }
    }

    PreviewResultsView.prototype.requestResults = function(){
        // Request for transformation results.
        var context = this;
        Execution.result(context.executionId,
            function(data){
                if(JSON.parse(data)['finished'] == true){
                    var headerController = app.modules.HeaderModule.controllers.HeaderController;
                    headerController.view.removeTransNotification(context.executionId);
                    context.updateStatus(context.graph,JSON.parse(data)['stepStatus']);
                }
                context.data = JSON.parse(data).dataRows;

                context.$elements.preview_log.append(JSON.parse(data)['logText']);

                context.$elements.steps_measures.empty();

                var $thead =
                    $('<thead>').append(
                        $('<tr>').append(
                            $('<th>').text("Step Name"),
                            $('<th>').text("Number of records"),
                            $('<th>').text("Read"),
                            $('<th>').text("Write"),
                            $('<th>').text("Enter"),
                            $('<th>').text("Output"),
                            $('<th>').text("Update"),
                            $('<th>').text("Refuse"),
                            $('<th>').text("Error"),
                            $('<th>').text("State"),
                            $('<th>').text("Time"),
                            $('<th>').text("Speed (record/second)"),
                            $('<th>').text("Pri/in/out")
                        )
                    );
                context.$elements.steps_measures.append($thead);

                var rows = JSON.parse(data)['stepMetrics'];
                var $tbody = $('<tbody>');
                for(var i=0; i<rows.length; i++){
                    var item = rows[i];
                    var $tr = $('<tr>').append(
                        $('<td>').text(item.stepName),
                        $('<td>').text(item.nRecords),
                        $('<td>').text(item.read),
                        $('<td>').text(item.write),
                        $('<td>').text(item.enter),
                        $('<td>').text(item.output),
                        $('<td>').text(item.update),
                        $('<td>').text(item.refuse),
                        $('<td>').text(item.error),
                        $('<td>').text(item.state),
                        $('<td>').text(item.time),
                        $('<td>').text(item.speed),
                        $('<td>').text(item.priInOut)
                    );
                    $tbody.append($tr);
                }
                context.$elements.steps_measures.append($tbody);

                context.$elements.steps_measures.DataTable();
            }
        );
    }

    return PreviewResultsView;
});
define('PreviewResultsView', ['View'], function (View) {
    var PreviewResultsView = function (controller) {
        View.call(this, controller, 'previewResults');
    };

    // Inheritance from super class
    PreviewResultsView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    PreviewResultsView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        this.transName = MainModule.controllers.HeaderController.transName;
        this.executionId = MainModule.controllers.HeaderController.executionId;
        this.$elements.preview_title.text("Preview "+this.transName);

        // Render the transformation with a success icon on each step.
        this.renderGraph(this.$elements.preview_graph[0]);

        // Request for transformation results.
        this.data;
        var context = this;
        $.get("/graph/result",{execution: context.executionId},
            function(data){
                if(JSON.parse(data)['finished'] == true){
                    MainModule.controllers.HeaderController.view.removeTransNotification(context.executionId);
                    context.updateStatus(context.graph,JSON.parse(data)['stepStatus']);
                }
                context.data = JSON.parse(data).previewData;

                context.$elements.preview_log.append(JSON.parse(data)['log']);

                context.$elements.preview_steps_measures.empty();
                var $tr = $('<tr>').append(
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
                    $('<th>').text("Speed (note/second)"),
                    $('<th>').text("Pri/in/out")
                );
                context.$elements.preview_steps_measures.append($tr[0]);

                var rows = JSON.parse(data)['stepMeasure'];
                for(var i=0; i<rows.length; i++){
                    var item = rows[i];
                    var $tr = $('<tr>').append(
                        $('<td>').text(item[0]),
                        $('<td>').text(item[1]),
                        $('<td>').text(item[2]),
                        $('<td>').text(item[3]),
                        $('<td>').text(item[4]),
                        $('<td>').text(item[5]),
                        $('<td>').text(item[6]),
                        $('<td>').text(item[7]),
                        $('<td>').text(item[8]),
                        $('<td>').text(item[9]),
                        $('<td>').text(item[10]),
                        $('<td>').text(item[11]),
                        $('<td>').text(item[12])
                    );
                    context.$elements.preview_steps_measures.append($tr[0]);
                }
            }
        );
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
            var node = mxUtils.load('assets/lib/mxgraph2/style/default-style.xml').getDocumentElement();
            var dec = new mxCodec(node.ownerDocument);
            dec.decode(node, graph.getStylesheet());

            graph.setConnectable(false);
            graph.setCellsEditable(false);
            graph.setCellsDeletable(false);
            graph.setCellsMovable(false);
            graph.setCellsResizable(false);

            var enc = new mxCodec();
            var xml = enc.encode(global_editor.graph.getModel());
            var decoder = new mxCodec(xml);

            decoder.decode(xml, graph.getModel());
            graph.resizeContainer = false;

            var context = this;
            graph.getSelectionModel().addListener(mxEvent.UNDO, function(sender, evt)
            {
                context.$elements.preview_table.hide();
                var cell = evt.getProperty('edit').changes[0].removed[0]
                context.$elements.preview_data.show();
                context.$elements.preview_data_title.text(cell.value);

                context.$elements.preview_data_table.empty();

                var columns = context.data[cell.value].columns;
                var $tr = '<tr>';
                for(var i=0; i<columns.length; i++)
                    $tr += '<th>'+columns[i]['header']+'</th>';
                $tr += '</tr>';
                context.$elements.preview_data_table.append($tr);

                var rows = context.data[cell.value].firstRecords;
                for(var i=0; i<rows.length; i++) {
                    var $tr = '<tr>';
                    for(var j=0; j<columns.length; j++) {
                        $tr += '<td>' + rows[i][columns[j]['header']] + '</td>';
                    }
                    $tr += '</tr>';
                    context.$elements.preview_data_table.append($tr);
                }

            });

            this.graph = graph;
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
                        var overlay = new mxCellOverlay(new mxImage('assets/lib/mxgraph2/editors/images/overlays/false.png', 16, 16), status[i].logText, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP);
                        graph.addCellOverlay(cell, overlay);
                    } else {
                        var overlay = new mxCellOverlay(new mxImage('assets/lib/mxgraph2/editors/images/overlays/true.png', 16, 16), null, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP);
                        graph.addCellOverlay(cell, overlay);
                    }
                    break;
                }
            }
        }
    }

    return PreviewResultsView;
});
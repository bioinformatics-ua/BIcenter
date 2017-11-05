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
        var context = this;
        $.get("/graph/result",{execution: context.executionId},
            function(data){

                if(JSON.parse(data)['finished'] == true){
                    MainModule.controllers.HeaderController.view.removeTransNotification(context.executionId);
                }

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
            graph.setEnabled(false);
            graph.setCellsEditable(false);
            graph.setCellsDeletable(false);
            graph.setCellsMovable(false);
            graph.setCellsResizable(false);

            var enc = new mxCodec();
            var xml = enc.encode(global_editor.graph.getModel());
            var decoder = new mxCodec(xml);

            decoder.decode(xml, graph.getModel());
            graph.resizeContainer = false;
        }
    }

    return PreviewResultsView;
});
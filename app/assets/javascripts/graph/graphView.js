define('GraphView', ['View', 'Task', 'jquery.slimscroll', 'jquery-ui'], function (View, Task) {
    var GraphView = function (controller) {
        View.call(this, controller, 'graph');
    };

    // Inheritance from super class
    GraphView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    GraphView.prototype.initialize = function ($container) {
        var data = {
            canRun: this.controller.institutionId ? true : false
        };

        _super_.initialize.call(this, $container, data);
        var context = this;

        // Load the mxEditor after elements rendering.
        this.editor = this.controller.createEditor('/assets/editor/diagrameditor.xml');

        // Update editor with the loaded graphModel.
        if (this.controller.graphId) {
            Task.loadTask(this.controller.institutionId, this.controller.graphId, function (graphModel) {
                var doc = mxUtils.parseXml(graphModel);
                var codec = new mxCodec(doc);
                codec.decode(doc.documentElement, context.editor.graph.getModel());
            });
        }

        this.$elements.graph.droppable({
            scope: 'components',
            drop: function (event, ui) {
                var component = ui.helper.find('#graphComponent').children().first().get(0);
                var shortName = component.getAttribute('component');

                var offset = context.$elements.graph.offset();
                var top = ui.position.top - offset.top;
                var left = ui.position.left - offset.left;

                var graph = context.editor.graph;
                graph.getModel().beginUpdate();
                graph.insertVertex(graph.getDefaultParent(), null, component, left, top, 40, 40, 'icon;image=/middle/' + shortName + '.svg');
                graph.getModel().endUpdate();
            }
        });

        this.$elements.toolbar.slimscroll({
            axis: 'x',
            width: 'auto',
            height: '80px',
            alwaysVisible: true,
            wrapperClass: 'toolbar'
        });

        this.$elements.board.resizable();
    };

    /**
     * Returns back to the pipeline, after XML import.
     */
    GraphView.prototype.loadXml = function () {
        this.$elements.source.click();
        this.$elements.board.css({'height': '70vh'});
        this.$elements.xmlBtn.css("display", "none");
    }

    return GraphView;
});
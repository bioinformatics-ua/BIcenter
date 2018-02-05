define('GraphController', ['Controller', 'GraphView', 'Task'], function (Controller, GraphView,Task) {
    var GraphController = function (module) {
        Controller.call(this, module, new GraphView(this));
    };

    // Inheritance from super class
    GraphController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    GraphController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        // Load the mxEditor after elements rendering.
        createEditor('/assets/editor/diagrameditor.xml');

        if(global_editor!=null){
            this.view.$elements.source.click();
            this.view.$elements.source.click();
        }
    };

    GraphController.prototype.createTask = function() {
        var $tab =
            $('<li class="active">').append(
                $('<a href="javascript:;">').append(
                    $('<button class="close" type="button">').text("x"),
                    $('<input style="height: 68%;">').keypress(function( event ) {
                        if ( event.which == 13 ) {
                            event.preventDefault();
                            Task.new_task(this.value, function (task) {
                                console.log(task);
                            });
                        }
                    })
                )
            );
        this.view.$elements.graph_tabs.append($tab);
    }

    return GraphController;
});
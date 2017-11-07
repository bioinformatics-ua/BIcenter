define('GraphController', ['Controller', 'GraphView', 'editor'], function (Controller, GraphView) {
    var GraphController = function (module) {
        Controller.call(this, module, new GraphView(this));
    };

    // Inheritance from super class
    GraphController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    GraphController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        // Load the mxEditor after elements rendering.
        createEditor('assets/editor/diagrameditor.xml');

        if(global_editor!=null){
            this.view.$elements.source.click();
            this.view.$elements.source.click();
        }
    };

    return GraphController;
});
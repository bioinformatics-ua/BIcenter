define('GraphController', ['Controller', 'GraphView'], function (Controller, GraphView) {
    var GraphController = function (module) {
        Controller.call(this, module, new GraphView(this));
    };

    // Inheritance from super class
    GraphController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    GraphController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    GraphController.prototype.testClick = function () {
        console.log("Testing clicks from controller");
    };

    return GraphController;
});
define('ContainerController', ['Controller', 'ContainerView'], function (Controller, ContainerView) {
    var ContainerController = function (module) {
        Controller.call(this, module, new ContainerView(this));
    };

    // Inheritance from super class
    ContainerController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    ContainerController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        this.loadController('GraphController');
    };

    ContainerController.prototype.loadController = function (controller) {
        this.view.loadController(controller);
    };

    return ContainerController;
});
define('RunOptionsController', ['Controller', 'RunOptionsView'], function (Controller, RunOptionsView) {
    var RunOptionsController = function (module) {
        Controller.call(this, module, new RunOptionsView(this));
    };

    // Inheritance from super class
    RunOptionsController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    RunOptionsController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    RunOptionsController.prototype.testClick = function () {
        console.log("Testing clicks from controller");
    };

    RunOptionsController.prototype.cancelClick = function(){
        var controller = 'GraphController';
        var containerController = this.module.controllers.ContainerController;
        if (!containerController) {
            console.err('Container controller not found!');
        }

        containerController.loadController(controller);
    }

    return RunOptionsController;
});
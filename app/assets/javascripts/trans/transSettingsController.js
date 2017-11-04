define('TransSettingsController', ['Controller', 'TransSettingsView'], function (Controller, TransSettingsView) {
    var TransSettingsController = function (module) {
        Controller.call(this, module, new TransSettingsView(this));
    };

    // Inheritance from super class
    TransSettingsController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    TransSettingsController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    /**
     * Returns to the pipeline view.
     */
    TransSettingsController.prototype.cancelClick = function(){
        var controller = 'GraphController';
        var containerController = this.module.controllers.ContainerController;
        if (!containerController) {
            console.err('Container controller not found!');
        }

        containerController.loadController(controller);
    }

    return TransSettingsController;
});
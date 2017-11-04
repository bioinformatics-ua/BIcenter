define('ChecksumController', ['Controller', 'ChecksumView'], function (Controller, ChecksumView) {
    var ChecksumController = function (module) {
        Controller.call(this, module, new ChecksumView(this));
    };

    // Inheritance from super class
    ChecksumController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    ChecksumController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    /**
     * Returns to the pipeline view.
     */
    ChecksumController.prototype.cancelClick = function(){
        var controller = 'GraphController';
        var containerController = this.module.controllers.ContainerController;
        if (!containerController) {
            console.err('Container controller not found!');
        }

        containerController.loadController(controller);
    }

    return ChecksumController;
});
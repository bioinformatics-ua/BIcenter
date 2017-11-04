define('SequenceController', ['Controller', 'SequenceView'], function (Controller, SequenceView) {
    var SequenceController = function (module) {
        Controller.call(this, module, new SequenceView(this));
    };

    // Inheritance from super class
    SequenceController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    SequenceController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    /**
     * Returns to the pipeline view.
     */
    SequenceController.prototype.cancelClick = function(){
        var controller = 'GraphController';
        var containerController = this.module.controllers.ContainerController;
        if (!containerController) {
            console.err('Container controller not found!');
        }

        containerController.loadController(controller);
    }

    return SequenceController;
});
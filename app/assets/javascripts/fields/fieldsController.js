define('FieldsController', ['Controller', 'FieldsView'], function (Controller, FieldsView) {
    var FieldsController = function (module) {
        Controller.call(this, module, new FieldsView(this));
    };

    // Inheritance from super class
    FieldsController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    FieldsController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        if(this.stepId){
            this.view.loadStep(this.stepId,this.before);
        }
    };

    /**
     * Returns to the pipeline view.
     */
    FieldsController.prototype.ok = function(){
        var controller = 'GraphController';
        var containerController = this.module.controllers.ContainerController;
        if (!containerController) {
            console.err('Container controller not found!');
        }

        containerController.loadController(controller);
    }

    return FieldsController;
});
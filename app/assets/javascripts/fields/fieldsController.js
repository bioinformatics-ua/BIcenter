define('FieldsController', ['Controller','FieldsView','Step','Router'], function (Controller,FieldsView,Step,Router) {
    var FieldsController = function (module) {
        Controller.call(this, module, new FieldsView(this));
    };

    // Inheritance from super class
    FieldsController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    FieldsController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        if(this.stepId){
            var context = this;
            Step.getStep(this.institutionId,this.stepId,function (step) {
                context.view.loadStep(step,context.before);
            })
        }
    };

    /**
     * Returns to the pipeline view.
     */
    FieldsController.prototype.ok = function(){
        Router.navigatePrevious();
    }

    return FieldsController;
});
define('PreviewStepController', ['Controller', 'PreviewStepView', 'Router', 'Execution'], function (Controller, PreviewStepView, Router, Execution) {
    var PreviewStepController = function (module) {
        Controller.call(this, module, new PreviewStepView(this));
    };

    // Inheritance from super class
    PreviewStepController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    PreviewStepController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        var context = this;
        if(this.executionId && this.stepId){
            Execution.getStepData(this.institutionId,this.executionId, this.stepId, function(task){
                context.view.loadTask(context.executionId,task);
            });
        }
    };

    /**
     * Returns to the pipeline view.
     */
    PreviewStepController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    return PreviewStepController;
});
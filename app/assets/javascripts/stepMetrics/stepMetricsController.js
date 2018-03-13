define('StepMetricsController', ['Controller', 'StepMetricsView', 'Router', 'Execution'], function (Controller, StepMetricsView, Router, Execution) {
    var StepMetricsController = function (module) {
        Controller.call(this, module, new StepMetricsView(this));
    };

    // Inheritance from super class
    StepMetricsController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    StepMetricsController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        var context = this;
        if(this.executionId){
            Execution.getMetrics(this.executionId, function(task){
                context.view.loadTask(context.executionId,task);
            });
        }
    };

    /**
     * Returns to the pipeline view.
     */
    StepMetricsController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    return StepMetricsController;
});
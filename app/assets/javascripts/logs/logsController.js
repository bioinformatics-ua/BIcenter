define('LogsController', ['Controller', 'LogsView', 'Router', 'Execution'], function (Controller, LogsView, Router, Execution) {
    var LogsController = function (module) {
        Controller.call(this, module, new LogsView(this));
    };

    // Inheritance from super class
    LogsController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    LogsController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        var context = this;
        if(this.executionId){
            Execution.getLogs(this.institutionId,this.executionId, function(task){
                context.view.loadTask(context.executionId,task);
            });
        }
    };

    /**
     * Returns to the pipeline view.
     */
    LogsController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    return LogsController;
});
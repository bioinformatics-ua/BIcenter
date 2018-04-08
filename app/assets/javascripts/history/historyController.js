define('HistoryController', ['Controller', 'HistoryView','jsRoutes', 'Router', 'Task'], function (Controller, HistoryView, jsRoutes, Router, Task) {
    var HistoryController = function (module) {
        Controller.call(this, module, new HistoryView(this));
    };

    // Inheritance from super class
    HistoryController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    HistoryController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        var context = this;
        if(this.graphId){
            Task.getExecutions(this.institutionId, this.graphId, function(task){
                context.view.loadTask(task);
            });
        }
    };

    HistoryController.prototype.showLogs = function(executionId){
        var configStepUrl = jsRoutes.controllers.ExecutionController.logs(this.institutionId,this.graphId,executionId).url;
        Router.navigate(configStepUrl);
    }

    HistoryController.prototype.showMetrics = function(executionId){
        var configStepUrl = jsRoutes.controllers.ExecutionController.metrics(this.institutionId,this.graphId,executionId).url;
        Router.navigate(configStepUrl);
    }

    HistoryController.prototype.showData = function(executionId){
        var configStepUrl = jsRoutes.controllers.ExecutionController.previewData(this.institutionId,this.graphId,executionId).url;
        Router.navigate(configStepUrl);
    }

    /**
     * Returns to the pipeline view.
     */
    HistoryController.prototype.cancelClick = function(){
        Router.navigate('/');
    }

    return HistoryController;
});
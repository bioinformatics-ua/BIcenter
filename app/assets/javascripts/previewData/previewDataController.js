define('PreviewDataController', ['Controller', 'PreviewDataView', 'Router', 'Execution'], function (Controller, PreviewDataView, Router, Execution) {
    var PreviewDataController = function (module) {
        Controller.call(this, module, new PreviewDataView(this));
    };

    // Inheritance from super class
    PreviewDataController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    PreviewDataController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        var context = this;
        if(this.executionId){
            Execution.getData(this.institutionId,this.executionId, function(task){
                context.view.loadTask(context.executionId,task);
            });
        }
    };

    /**
     * Returns to the pipeline view.
     */
    PreviewDataController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    return PreviewDataController;
});
define('RemoteExecutionController', ['Controller', 'RemoteExecutionView', 'Router', 'Execution'], function (Controller, RemoteExecutionView, Router, Execution) {
    var RemoteExecutionController = function (module) {
        Controller.call(this, module, new RemoteExecutionView(this));
    };

    // Inheritance from super class
    RemoteExecutionController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    RemoteExecutionController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        // Add button to save data
        this.view.addButtons({
            save: {
                label: '<i class="fa fa-bolt"></i> Run',
                className: 'btn-success',
                callback: 'controller.remoteExecution()'
            }
        });
    };

    RemoteExecutionController.prototype.remoteExecution = function() {
        var graphController = this.module.controllers['GraphController'];
        var graphId = graphController.graphId;

        var $form = this.view.$elements.remoteExecution;
        var formValues = $form.serializeForm();
        formValues["dateTime"] = this.view.$elements.startDateTime.data('date');

        var serverId = this.view.$elements.server.val();
        Execution.remoteExecution(graphId,serverId,formValues, function(returnedData){

        });

        this.view.hide();
    };

    /**
     * Returns to the pipeline view.
     */
    RemoteExecutionController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    };

    return RemoteExecutionController;
});
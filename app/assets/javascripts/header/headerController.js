define('HeaderController', ['Controller','HeaderView','jsRoutes','Router','Task','Execution', 'Alert'], function (Controller, HeaderView, jsRoutes, Router,Task,Execution,Alert) {
    var HeaderController = function (module) {
        Controller.call(this, module, new HeaderView(this));
    };

    // Inheritance from super class
    HeaderController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    HeaderController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        this.executions = [];
    };

    /**
     * Shows the step configuration dialog, of a given step type.
     * @param stepType
     * @param i id of the step within the current transformation.
     */
    HeaderController.prototype.showStepDialog = function (stepId) {
        var configStepUrl = jsRoutes.controllers.StepController.configure(stepId).url;
        Router.navigate(configStepUrl);
    };

    /**
     * Shows the transaction settings configuration dialog, for the current transaction.
     * @param i id of the step within the current transformation.
     */
    HeaderController.prototype.transSettingsDialog = function (i) {
        this.setGlobalStep(i);

        var controller = 'TransSettingsController';
        var containerController = this.module.controllers.ContainerController;
        if (!containerController) {
            console.err('Container controller not found!');
        }

        containerController.loadController(controller);
    }

    /**
     * Sets the current select step.
     * @param i id of the step within the current transformation.
     */
    HeaderController.prototype.setGlobalStep = function (i) {
        this.global_step = this.view.global_nodes[i];
    }

    /**
     * Returns the input fields of a given step.
     * @param stepId
     */
    HeaderController.prototype.showStepInput = function (stepId) {
        var configStepUrl = jsRoutes.controllers.StepController.showStepInput(stepId).url;
        Router.navigate(configStepUrl);
    }

    /**
     * Returns the output fields of a given step.
     * @param stepId
     */
    HeaderController.prototype.showStepOutput = function (stepId) {
        var configStepUrl = jsRoutes.controllers.StepController.showStepOutput(stepId).url;
        Router.navigate(configStepUrl);
    }

    /**
     * Shows the run settings configuration dialog.
     */
    HeaderController.prototype.showRunOptions = function () {
        var controller = 'RunOptionsController';
        var containerController = this.module.controllers.ContainerController;
        if (!containerController) {
            console.err('Container controller not found!');
        }

        containerController.loadController(controller);
    }

    /**
     * Calls the API that runs the transformation.
     * @param method execute transformation locally, remotely or in cluster mode.
     */
    HeaderController.prototype.runTransformation = function (method) {

        var exec_method = new Object();
        exec_method.execMethod = method;
        exec_method.remoteServer = "master1";

        var details = new Object();

        details.safeModeEnabled = "on"
        details.gatheringMetrics = "on";
        details.clearingLog = "on";
        details.logLevel = 3;

        var execution = new Object();
        execution.executeMethod = exec_method;
        execution.details = details;
        var execution_configuration = JSON.stringify(execution);

        var context = this;
        var graphController = app.modules.MainModule.controllers.GraphController;
        Execution.run(graphController.view.taskId,execution_configuration,
            function(returnedData){
                // Submit notification of transformation Execution.
                data = JSON.parse(returnedData);
                context.transName = data['transName'];
                context.executionId = data['executionId'];
                context.executions = context.executions.filter(function (exec) {
                    return exec['transName'] !== data['transName'];
                });
                context.executions.push(data);
                context.view.transSubmissionNotification(context.transName, context.executionId, "Running");

                // Monitor transformation's execution.
                var interval = setInterval(
                    function () {
                        Execution.result(context.executionId,function(data){
                            if (JSON.parse(data)['finished'] == true) {
                                context.view.transSubmissionNotification(context.transName, context.executionId, "Finished");
                                clearInterval(interval);

                                // Not sure if this notification is helpful for the user...
                                //Alert.flash(ALERT_TYPE.SUCCESS, 'Task', 'Task \'' + context.transName + '\' finished execution successfuly!');
                            }
                        });
                    }, 500
                );
            }
        );
    }

    /**
     * Shows the preview results transformation dialog.
     */
    HeaderController.prototype.showPreviewResults = function (taskName, executionId) {
        Task.getTask(taskName,function(task){
            var configStepUrl = jsRoutes.controllers.TransGraphController.previewResults(task.id).url;
            Router.navigate(configStepUrl);
        });
    }

    /**
     * Create new task.
     */
    HeaderController.prototype.createTask = function(){
        var graphController = app.modules.MainModule.controllers.GraphController;
        graphController.createTask();
    }

    return HeaderController;
});
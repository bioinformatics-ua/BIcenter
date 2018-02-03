define('HeaderController', ['Controller', 'HeaderView', 'jsRoutes', 'Router'], function (Controller, HeaderView, jsRoutes, Router) {
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
    HeaderController.prototype.showStepDialog = function (stepType, i) {
        this.setGlobalStep(i);

        var configStepUrl = jsRoutes.controllers.StepController.configure(1, stepType).url;
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
     * Returns the input or the output fields of a given step.
     * @param stepName step label.
     * @param before if true returns step input fields, else returns step output fields.
     */
    HeaderController.prototype.showFieldsDialog = function (stepName, before) {
        this.stepName = stepName;
        this.before = before;

        var controller = 'FieldsController';
        var containerController = this.module.controllers.ContainerController;
        if (!containerController) {
            console.err('Container controller not found!');
        }

        containerController.loadController(controller);
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
        var enc = new mxCodec(mxUtils.createXmlDocument());
        var node = enc.encode(global_editor.graph.getModel());

        var exec_method = new Object();
        exec_method.execMethod = method;
        exec_method.remoteServer = "master1";

        var details = new Object();

        details.safeModeEnabled = "on"
        details.gatheringMetrics = "on";
        details.clearingLog = "on";
        details.logLevel = 3;
        details.parameters = global_editor['graph'].getDefaultParent().getAttribute('parameters');
        details.variables = global_editor['graph'].getDefaultParent().getAttribute('variables');

        var execution = new Object();
        execution.executeMethod = exec_method;
        execution.details = details;
        var execution_configuration = JSON.stringify(execution);

        this.transName;
        this.executionId;

        var context = this;
        $.post('/graph/run', {graph: mxUtils.getPrettyXml(node), execution: execution_configuration},
            function (returnedData) {
                console.log(returnedData);
                data = JSON.parse(returnedData);
                context.transName = data['transName'];
                context.executionId = data['executionId'];
                context.executions = context.executions.filter(function (exec) {
                    return exec['transName'] !== data['transName'];
                });
                context.executions.push(data);
                MainModule.controllers.HeaderController.view.transSubmissionNotification(context.transName, context.executionId, "Running");
            });

        var interval = setInterval(
            function () {
                $.get("/graph/result", {execution: context.executionId},
                    function (data) {
                        if (JSON.parse(data)['finished'] == true) {
                            MainModule.controllers.HeaderController.view.transSubmissionNotification(context.transName, context.executionId, "Finished");
                            clearInterval(interval);
                        }
                    }
                );
            }, 500
        );
    }

    /**
     * Shows the preview results transformation dialog.
     */
    HeaderController.prototype.showPreviewResults = function (transName, executionId) {
        this.transName = transName;
        this.executionId = executionId;

        var controller = 'PreviewResultsController';
        var containerController = this.module.controllers.ContainerController;
        if (!containerController) {
            console.err('Container controller not found!');
        }

        containerController.loadController(controller);
    }

    return HeaderController;
});
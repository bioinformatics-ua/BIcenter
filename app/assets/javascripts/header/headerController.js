define('HeaderController', ['Controller', 'HeaderView', 'jsRoutes', 'Router', 'Task', 'Execution', 'Alert', 'User'], function (Controller, HeaderView, jsRoutes, Router, Task, Execution, Alert, User) {
    var HeaderController = function (module) {
        Controller.call(this, module, new HeaderView(this));
    };

    // Inheritance from super class
    HeaderController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    HeaderController.prototype.initialize = function ($container) {
        var self = this;
        this.getUser(function (user) {
            _super_.initialize.call(self, $container, user);
        });

        this.executions = [];
    };

    HeaderController.prototype.getUser = function (callback) {
        User.getLoggedIn(function (user) {
            callback && callback(user);
        });
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
     * Shows the preview results transformation dialog.
     */
    HeaderController.prototype.showPreviewResults = function (taskName, executionId) {
        Task.getTask(taskName, function (task) {
            var configStepUrl = jsRoutes.controllers.TransGraphController.previewResults(task.institution,task.id).url;
            Router.navigate(configStepUrl);
        });
    }

    /**
     * Create new task.
     */
    HeaderController.prototype.createTask = function () {
        var graphController = app.modules.MainModule.controllers.GraphController;
        graphController.createTask();
    }

    return HeaderController;
});

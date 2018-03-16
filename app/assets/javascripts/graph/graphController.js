define('GraphController', ['Controller', 'GraphView', 'Router', 'Task', 'Execution', 'Alert'], function (Controller, GraphView, Router, Task, Execution, Alert) {
    var GraphController = function (module) {
        Controller.call(this, module, new GraphView(this));
    };

    // Inheritance from super class
    GraphController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    GraphController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        // Load the mxEditor after elements rendering.
        this.createEditor('/assets/editor/diagrameditor.xml');

        if(this.graphId){
            var context = this;
            Task.loadTask(this.graphId, function (graph) {
                context.view.$elements.source.click();
                context.view.$elements.xml.val(graph);
                context.view.$elements.source.click();
            });
        }

        if (global_editor != null) {
            this.view.$elements.source.click();
            this.view.$elements.source.click();
        }
    };

    var editor = null;
    /**
     * Constructs a new application (returns an mxEditor instance)
     */
    GraphController.prototype.createEditor = function (config) {
        var controller = this;

        var hideSplash = function () {
            // Fades-out the splash screen
            var splash = document.getElementById('splash');

            if (splash != null) {
                try {
                    mxEvent.release(splash);
                    mxEffects.fadeOut(splash, 100, true);
                }
                catch (e) {
                    splash.parentNode.removeChild(splash);
                }
            }
        };

        try {
            if (!mxClient.isBrowserSupported()) {
                mxUtils.error('Browser is not supported!', 200, false);
            }
            else {
                mxObjectCodec.allowEval = true;
                var node = mxUtils.load(config).getDocumentElement();
                editor = new mxEditor(node);

                editor.graph.addListener(mxEvent.CELLS_ADDED, function (sender, evt) {
                    controller.addCell(evt);
                });

                editor.graph.addListener(mxEvent.CELLS_MOVED, function (sender, evt) {
                    controller.updateCell(evt);
                });

                editor.graph.addListener(mxEvent.CELLS_REMOVED, function (sender, evt) {
                    controller.removeCell(evt);
                });

                mxObjectCodec.allowEval = false;

                // Adds active border for panning inside the container
                editor.graph.createPanningManager = function () {
                    var pm = new mxPanningManager(this);
                    pm.border = 30;

                    return pm;
                };

                editor.graph.allowAutoPanning = true;
                editor.graph.timerAutoScroll = true;

                // Updates the window title after opening new files
                var title = document.title;
                var funct = function (sender) {
                    document.title = title + ' - ' + sender.getTitle();
                };

                editor.addListener(mxEvent.OPEN, funct);

                // Prints the current root in the window title if the
                // current root of the graph changes (drilling).
                editor.addListener(mxEvent.ROOT, funct);
                funct(editor);

                // Displays version in statusbar
                editor.setStatus('mxGraph ' + mxClient.VERSION);

                // Shows the application
                hideSplash();
            }
        }
        catch (e) {
            hideSplash();

            // Shows an error message if the editor cannot start
            console.log('Cannot start application: ' + e.message);
        }

        return editor;
    }

    /**
     * Add step or hop to the task, based on a given graph event.
     * @param evt
     */
    GraphController.prototype.addCell = function (evt) {
        // Add event.
        var cell = evt.properties.cells[0];

        if (cell.isVertex()) {
            var stepMeta = new Object();

            stepMeta.x = cell.getGeometry().x;
            stepMeta.y = cell.getGeometry().y;
            stepMeta.width = cell.getGeometry().width;
            stepMeta.height = cell.getGeometry().height;

            stepMeta.component = cell.getValue().getAttribute("component");
            stepMeta.label = cell.getValue().getAttribute("label");
            stepMeta.graphId = cell.getId();

            Task.addStep(this.view.taskId, stepMeta, function (stepId) {
                cell.value.setAttribute("stepId",stepId);
            });
        }
        else {
            var hopMeta = new Object();

            hopMeta.graphId = cell.getId();
            hopMeta.source = cell.source.value.getAttribute("stepId");
            hopMeta.target = cell.target.value.getAttribute("stepId");

            Task.addHop(this.view.taskId, hopMeta);
        }
    }

    /**
     * Add step or hop to the task, based on a given graph event.
     * @param evt
     */
    GraphController.prototype.updateCell = function (evt) {
        var cells = evt.properties.cells;

        for (var i = 0; i < cells.length; i++) {
            if (cells[i].value.hasAttribute("stepId")) {
                var stepId = cells[i].value.getAttribute("stepId");

                var coord = new Object();
                coord.x = cells[i].getGeometry().x;
                coord.y = cells[i].getGeometry().y;

                Task.updateStep(stepId,coord);
            }
        }
    }

    /**
     * Remove step or hop to the task, based on a given graph event.
     * @param evt
     */
    GraphController.prototype.removeCell = function (evt) {
        var cells = evt.properties.cells;

        for (var i = 0; i < cells.length; i++) {
            if (cells[i].value.hasAttribute("stepId")) {
                var stepId = cells[i].value.getAttribute("stepId");
                Task.removeStep(stepId, function (reponse) {
                });
            }
            else {
                var hopId = cells[i].value.getAttribute("hopId");
                Task.removeHop(hopId, function (reponse) {
                });
            }
        }
    }

    /**
     * Shows the step configuration dialog, of a given step type.
     * @param stepType
     * @param i id of the step within the current transformation.
     */
    GraphController.prototype.showStepDialog = function () {
        var stepId = global_editor.graph.getSelectionCell().value.getAttribute("stepId");
        var configStepUrl = jsRoutes.controllers.StepController.configure(this.graphId,stepId).url;
        Router.navigate(configStepUrl);
    };

    /**
     * Returns the input fields of a given step.
     * @param stepId
     */
    GraphController.prototype.showStepInput = function (stepId) {
        var stepId = global_editor.graph.getSelectionCell().value.getAttribute("stepId");
        var configStepUrl = jsRoutes.controllers.StepController.showStepInput(this.graphId,stepId).url;
        Router.navigate(configStepUrl);
    }

    /**
     * Returns the output fields of a given step.
     * @param stepId
     */
    GraphController.prototype.showStepOutput = function (stepId) {
        var stepId = global_editor.graph.getSelectionCell().value.getAttribute("stepId");
        var configStepUrl = jsRoutes.controllers.StepController.showStepOutput(this.graphId,stepId).url;
        Router.navigate(configStepUrl);
    }

    GraphController.prototype.remoteExecution = function(){
        var modalController = this.module.controllers['ServerController'];
        modalController.view.show();
    }

    /**
     * Calls the API that runs the transformation.
     * @param method execute transformation locally, remotely or in cluster mode.
     */
    GraphController.prototype.runTransformation = function (method) {

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

        var headerController = app.modules.HeaderModule.controllers.HeaderController;
        Execution.run(this.graphId,execution_configuration,
            function(returnedData){
                // Submit notification of transformation Execution.
                data = JSON.parse(returnedData);
                headerController.transName = data['transName'];
                headerController.executionId = data['executionId'];
                headerController.executions = headerController.executions.filter(function (exec) {
                    return exec['transName'] !== data['transName'];
                });
                headerController.executions.push(data);
                headerController.view.transSubmissionNotification(headerController.transName, headerController.executionId, "Running");

                // Monitor transformation's execution.
                var interval = setInterval(
                    function () {
                        Execution.result(headerController.executionId,function(data){
                            if (JSON.parse(data)['finished'] == true) {
                                headerController.view.transSubmissionNotification(headerController.transName, headerController.executionId, "Finished");
                                clearInterval(interval);
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
    GraphController.prototype.showHistory = function () {
        var configStepUrl = jsRoutes.controllers.TransGraphController.history(this.graphId).url;
        Router.navigate(configStepUrl);
    }

    return GraphController;
});
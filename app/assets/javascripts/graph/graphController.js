define('GraphController', ['Controller', 'GraphView', 'Task', 'Alert'], function (Controller, GraphView, Task, Alert) {
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

        if (global_editor != null) {
            this.view.$elements.source.click();
            this.view.$elements.source.click();
        }
    };

    /**
     * Create new task.
     */
    GraphController.prototype.createTask = function () {
        var context = this;
        var $tab =
            $('<li class="graphTab" view-click="controller.clickTab()">').append(
                $('<a href="javascript:;">').append(
                    $('<button class="close" type="button" view-click="controller.closeTab()" >').text("x"),
                    $('<input style="height: 68%;">').keypress(function (event) {
                        if (event.which == 13) {
                            event.preventDefault();

                            var taskName = this.value;
                            this.replaceWith(taskName);

                            Task.getTask(taskName, function (task) {
                                if (task == "not found") {
                                    Task.newTask(taskName, function (task) {
                                        context.view.taskId = task.id;
                                        Task.loadTask(task.id, function (graph) {
                                            context.view.$elements.source.click();
                                            context.view.$elements.xml.val(graph);
                                            context.view.$elements.source.click();

                                            Alert.flash(ALERT_TYPE.SUCCESS, 'Task', 'Task \'' + taskName + '\' was created successfully!');
                                        });
                                    });
                                }
                                else {
                                    context.view.taskId = task.id;
                                    Task.loadTask(task.id, function (graph) {
                                        context.view.$elements.source.click();
                                        context.view.$elements.xml.val(graph);
                                        context.view.$elements.source.click();

                                        Alert.flash(ALERT_TYPE.DANGER, 'Task', 'Task \'' + taskName + '\' already exists!');
                                    });
                                }
                            });
                        }
                    })
                )
            );
        this.view.$elements.graph_tabs.append($tab);
        this.view._loadViewComponents();
    };

    /**
     * Listener for task tab close button.
     */
    GraphController.prototype.closeTab = function (event, $element) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        Task.closeTab(this.view.taskId, function () {
            var prevTab = $element.closest('li').prev();
            $element.closest('li').remove();
            prevTab.trigger('click').addClass("active");
        });
    };

    /**
     * Listener for task tab selection.
     */
    GraphController.prototype.clickTab = function (event, $element) {
        var context = this;
        var taskName = $element.text().slice(1);

        if (taskName) {
            Task.getTask(taskName, function (task) {
                context.view.taskId = task.id;
                Task.loadTask(task.id, function (graph) {
                    context.view.$elements.source.click();
                    context.view.$elements.xml.val(graph);
                    context.view.$elements.source.click();
                });
            });
        }

        $(".graphTab").removeClass("active");
        $(this).addClass("active");
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

            Task.addStep(this.view.taskId, stepMeta);
        }
        else {
            var hopMeta = new Object();

            hopMeta.graphId = cell.getId();
            hopMeta.source = cell.source.getId();
            hopMeta.target = cell.target.getId();

            Task.addHop(this.view.taskId, hopMeta);
        }
    }

    /**
     * Remove step or hop to the task, based on a given graph event.
     * @param evt
     */
    GraphController.prototype.removeCell = function (evt) {
        // Add event.
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

    return GraphController;
});
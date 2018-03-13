define('BreadcrumbView', ['View', 'async', 'Task', 'jsRoutes', 'Router', 'Execution', 'Step'], function (View, async, Task, jsRoutes, Router, Execution, Step) {
    var BreadcrumbView = function (controller) {
        View.call(this, controller, 'breadcrumb');
    };

    // Inheritance from super class
    BreadcrumbView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    BreadcrumbView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        var context = this;

        async.series([
            function (callback) {
                if (context.controller.executionId) {
                    Execution.getTask(context.controller.executionId, function (task) {
                        context.task = task;
                        callback();
                    });
                } else {
                    callback();
                }
            },
            function (callback) {
                if (context.task) callback();
                else {
                    Task.getTaskDetails(context.controller.graphId, function (task) {
                        context.task = task;
                        callback();
                    });
                }
            },
            function (callback) {
                if (context.controller.stepId) {
                    Step.getStepName(context.controller.stepId, function (stepName) {
                        context.stepName = stepName;
                        callback();
                    });
                } else {
                    callback();
                }
            },
            function (callback) {
                var breadcrumbs = [];

                breadcrumbs.push({
                    name: context.task.name,
                    url: '/',
                    icon: 'fa fa-exchange'
                });

                switch (context.controller.section) {
                    case 'previewData':
                        breadcrumbs.push({
                            name: 'History',
                            url: jsRoutes.controllers.TransGraphController.history(context.task.id).url
                        });

                        breadcrumbs.push({
                            name: context.controller.executionId,
                            icon: 'fa fa-flash'
                        });

                        breadcrumbs.push({
                            name: 'Preview data',
                            url: jsRoutes.controllers.ExecutionController.getData(context.controller.executionId).url
                        });
                        break;
                    case 'previewStep':
                        breadcrumbs.push({
                            name: 'History',
                            url: jsRoutes.controllers.TransGraphController.history(context.task.id).url
                        });

                        breadcrumbs.push({
                            name: context.controller.executionId,
                            icon: 'fa fa-flash'
                        });

                        breadcrumbs.push({
                            name: 'Preview data',
                            url: jsRoutes.controllers.ExecutionController.getData(context.controller.executionId).url
                        });

                        breadcrumbs.push({
                            name: context.stepName,
                            icon: 'fa fa-circle-o',
                            url: jsRoutes.controllers.ExecutionController.getStepData(context.controller.executionId,context.controller.stepId).url
                        });

                        break;
                    case 'logs':
                        breadcrumbs.push({
                            name: 'History',
                            url: jsRoutes.controllers.TransGraphController.history(context.task.id).url
                        });

                        breadcrumbs.push({
                            name: context.controller.executionId,
                            icon: 'fa fa-flash'
                        });

                        breadcrumbs.push({
                            name: 'Logs',
                            url: jsRoutes.controllers.ExecutionController.getLogs(context.controller.executionId).url
                        });
                        break;
                    case 'metrics':
                        breadcrumbs.push({
                            name: 'History',
                            url: jsRoutes.controllers.TransGraphController.history(context.task.id).url
                        });

                        breadcrumbs.push({
                            name: context.controller.executionId,
                            icon: 'fa fa-flash'
                        });

                        breadcrumbs.push({
                            name: 'Step Measures',
                            url: jsRoutes.controllers.ExecutionController.getMetrics(context.controller.executionId).url
                        });
                        break;
                    case 'history':
                        breadcrumbs.push({
                            name: 'History'
                        });
                        break;
                }

                context.setBreadcrumbs(breadcrumbs);
                callback();
            }
        ]);


    };

    BreadcrumbView.prototype.setBreadcrumbs = function (breadcrumbs) {
        var context = this;
        _.each(breadcrumbs, function (bc) {
            var $elem;
            if (bc.url) {
                $elem = $('<li><a view-router href="' + bc.url + '">' + bc.name + '</a></li>');
            } else {
                $elem = $('<li>' + bc.name + '</li>');
            }

            if (bc.icon) {
                $elem.prepend('<i class="' + bc.icon + '"></i> ')
            }

            context.$elements.breadcrumb.append($elem);
        });
        Router.updatePageLinks();
    };

    return BreadcrumbView;
});
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
               if(context.controller.graphId) {
                    Task.getTaskDetails(context.controller.institutionId, context.controller.graphId, function (task) {
                        context.task = task;
                        callback();
                    });
                }
                else {
                   callback();
               }
            },
            function (callback) {
                if (context.controller.stepId) {
                    Step.getStepName(context.controller.institutionId, context.controller.stepId, function (stepName) {
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
                    url: jsRoutes.controllers.TransGraphController.selectTask(context.controller.institutionId, context.controller.graphId).url,
                    icon: 'fa fa-exchange'
                });

                switch (context.controller.section) {
                    case 'previewData':
                        breadcrumbs.push({
                            name: 'History',
                            url: jsRoutes.controllers.TransGraphController.history(context.controller.institutionId, context.controller.graphId).url
                        });

                        breadcrumbs.push({
                            name: context.controller.executionId,
                            icon: 'fa fa-flash'
                        });

                        breadcrumbs.push({
                            name: 'Preview data',
                            url: jsRoutes.controllers.ExecutionController.previewData(context.controller.institutionId, context.controller.executionId).url
                        });
                        break;
                    case 'previewStep':
                        breadcrumbs.push({
                            name: 'History',
                            url: jsRoutes.controllers.TransGraphController.history(context.controller.institutionId, context.controller.graphId).url
                        });

                        breadcrumbs.push({
                            name: context.controller.executionId,
                            icon: 'fa fa-flash'
                        });

                        breadcrumbs.push({
                            name: 'Preview data',
                            url: jsRoutes.controllers.ExecutionController.previewData(context.controller.institutionId, context.controller.graphId,context.controller.executionId).url
                        });

                        breadcrumbs.push({
                            name: context.stepName,
                            icon: 'fa fa-circle-o',
                            url: jsRoutes.controllers.ExecutionController.previewStep(context.controller.institutionId, context.controller.graphId,context.controller.executionId,context.controller.stepId).url
                        });

                        break;
                    case 'logs':
                        breadcrumbs.push({
                            name: 'History',
                            url: jsRoutes.controllers.TransGraphController.history(context.controller.institutionId, context.controller.graphId).url
                        });

                        breadcrumbs.push({
                            name: context.controller.executionId,
                            icon: 'fa fa-flash'
                        });

                        breadcrumbs.push({
                            name: 'Logs',
                            url: jsRoutes.controllers.ExecutionController.logs(context.controller.institutionId, context.controller.graphId,context.controller.executionId).url
                        });
                        break;
                    case 'metrics':
                        breadcrumbs.push({
                            name: 'History',
                            url: jsRoutes.controllers.TransGraphController.history(context.controller.institutionId, context.controller.graphId).url
                        });

                        breadcrumbs.push({
                            name: context.controller.executionId,
                            icon: 'fa fa-flash'
                        });

                        breadcrumbs.push({
                            name: 'Step Measures',
                            url: jsRoutes.controllers.ExecutionController.metrics(context.controller.institutionId, context.controller.graphId,context.controller.executionId).url
                        });
                        break;
                    case 'history':
                        breadcrumbs.push({
                            name: 'History'
                        });
                        break;
                    case 'configure':
                        breadcrumbs.push({
                            name: 'Edit'
                        });

                        breadcrumbs.push({
                            name: context.stepName,
                            icon: 'fa fa-circle-o',
                            url: jsRoutes.controllers.StepController.configure(context.controller.institutionId, context.controller.graphId,context.controller.stepId).url
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
        _.each(breadcrumbs, function (bc, idx) {
            var $elem;
            if (bc.url) {
                $elem = $('<li style="float: left"><a view-router href="' + bc.url + '">' + bc.name + '</a></li>');
            } else {
                $elem = $('<li>' + bc.name + '</li>');
            }

            if (bc.icon) {
                $elem.prepend('<i class="' + bc.icon + '"></i> ')
            }

            if (idx === 0) {
				context.$elements.breadcrumb.prepend($elem);
			} else {
				context.$elements.breadcrumb.append($elem);
			}
        });
        Router.updatePageLinks();
    };

    return BreadcrumbView;
});

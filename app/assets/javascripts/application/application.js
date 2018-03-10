define('Application', ['jquery', 'Router', 'Module', 'jsRoutes', 'adminLTE', 'custom.jquery'], function ($, Router, Module, jsRoutes) {
    var Application = function (mainModuleName) {
        this.mainModuleName = mainModuleName || 'MainModule';
        this.modules = {};

        var self = this;
        $(document).ready(function () {
            self.initialize();
        });
    };

    Application.prototype.initialize = function () {
        var self = this;

        // Configure router
        Router.config({mode: 'history'});

        // Add routes
        Router
            .add(new RegExp(jsRoutes.controllers.StepController.showStepInput('(.*)').url.substr(1), 'i'), function (stepId) {
                console.log("Show Input Fields of Step "+stepId);
                var opts = { stepId:stepId, before:true };
                self.loadController('MainModule','FieldsController',opts);
            })
            .add(new RegExp(jsRoutes.controllers.StepController.showStepOutput('(.*)').url.substr(1), 'i'), function (stepId) {
                console.log("Show Output Fields of Step "+stepId);
                var opts = { stepId:stepId, before: false };
                self.loadController('MainModule','FieldsController',opts);
            })
            .add(new RegExp(jsRoutes.controllers.TransGraphController.previewResults('(.*)').url.substr(1), 'i'), function (graphId) {
                console.log("Preview Results of Graph "+graphId);
                var opts = { graphId: graphId }
                self.loadController('MainModule','PreviewResultsController',opts);
            })
            .add(new RegExp(jsRoutes.controllers.TransGraphController.history('(.*)').url.substr(1), 'i'), function (graphId) {
                console.log("History of Graph "+graphId);
                var opts = { graphId: graphId }
                self.loadController('MainModule','HistoryController',opts);
            })
            .add(new RegExp(jsRoutes.controllers.StepController.configure('(.*)').url.substr(1), 'i'), function (stepId) {
                console.log('Edit step',stepId);

                var opts = {
                    stepId: stepId
                };
                self.loadControllers('MainModule', ['StepController', 'StepModalController'], opts);
            })
            .add(new RegExp(jsRoutes.controllers.TransGraphController.selectTask('(.*)').url.substr(1), 'i'), function (graphId) {
                console.log("Graph "+graphId+" has been selected");
                var opts = { graphId: graphId }
                self.loadController('MainModule','GraphController',opts);
            })
            .add(function () {
                console.log('homepage');
                self.loadController('MainModule', 'GraphController');
            });

        // Start listening
        Router.listen();

        $(document).find('[module]').each(function () {
            var $element = $(this);
            var name = $element.attr('module');

            // Create module and initialize it
            var module = new Module(name);
            module.initialize($element);
            self.modules[name] = module;
        });

        if (!this.modules[this.mainModuleName]) {
            console.error('Main module [' + this.mainModuleName + '] not found!')
        }

        // Set the current state
        Router.check();
    };

    Application.prototype.loadControllers = function (moduleName, controllerNames, options, keepControllers) {
        var self = this;

        _.each(controllerNames, function (controller, i) {
            if (i === 0) {
                self.loadController(moduleName, controller, options, keepControllers);
            } else {
                self.loadController(moduleName, controller, options, true);
            }
        });
    };

    Application.prototype.loadController = function (moduleName, controllerName, options, keepControllers) {
        var module = moduleName ? this.modules[moduleName] : this.modules[this.mainModuleName];

        if (!module) {
            return;
        }

        var $container = module.$container;

        // Transform options object into string
        var init_options = [];
        _.each(options, function (item, key) {
            init_options.push(key + ':' + item);
        });

        // Create html for controller
        var html = '<div controller="' + controllerName +
            '" controller-init="' + init_options.join(',') +
            '"></div>';
        var $controller = $(html);

        // Keep controllers already present or remove them
        if (keepControllers) {
            module.loadController($controller);
            $container.append($controller);
        } else {
            // Destroy
            module.destroy();

            // Inject html
            $container.empty();
            $container.html(html);

            // Re-initialize module
            module = new Module(moduleName);
            module.initialize($container);
            this.modules[moduleName] = module;
        }
    };

    return Application;
});
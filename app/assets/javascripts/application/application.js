define('Application', ['jquery', 'Router', 'Module', 'jsRoutes', 'adminLTE'], function ($, Router, Module, jsRoutes) {
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
        // .add(/patient\/(.*)\/foo\/(.*)/, function (patientId, fooId) {
        //     .add(new RegExp(jsRoutes.controllers.patient.Patients.viewPatient('(.*)').url.substr(1), 'i'), function (patientId) {
        //         console.log('patientId', patientId);
        //         var opts = {
        //             patientId: patientId
        //         };
        //         self.loadControllers('MainModule', ['PatientController', 'PatientFormsController', 'PatientExamsController'], opts)
        //     })
        //     .add(/graph\/(.*)\/step\/(.*)/, function(graphId, stepName) {
            .add(new RegExp(jsRoutes.controllers.StepController.configure('(.*)', '(.*)').url.substr(1), 'i'), function (graphId, stepName) {
                console.log('Editar step', stepName, 'do graph', graphId);

                var opts = {
                    graphId: graphId,
                    stepName: stepName
                };
                self.loadController('MainModule', 'StepController', opts);
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
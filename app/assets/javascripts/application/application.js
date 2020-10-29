define('Application', ['jquery', 'Router', 'Module', 'jsRoutes', 'Svg', 'Institution', 'adminLTE', 'custom.jquery'], function ($, Router, Module, jsRoutes, Svg, Institution) {
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
			.add(new RegExp(jsRoutes.controllers.login.Login.index().url.substr(1), 'i'), function () {
				console.log("LOGIN PAGE");
			})
			.add(new RegExp(jsRoutes.controllers.StepController.showStepInput('(.*)', '(.*)', '(.*)').url.substr(1), 'i'), function (institutionId, graphId, stepId) {
				console.log("Show Input Fields of Step " + stepId);

				var opts = {
					institutionId: institutionId,
					graphId: graphId,
					stepId: stepId,
					before: true
				};

				self.loadController('MainModule', 'FieldsController', opts);

			})
			.add(new RegExp(jsRoutes.controllers.StepController.showStepOutput('(.*)', '(.*)', '(.*)').url.substr(1), 'i'), function (institutionId, graphId, stepId) {
				console.log("Show Output Fields of Step " + stepId);

				var opts = {
					institutionId: institutionId,
					graphId: graphId,
					stepId: stepId,
					before: false
				};

				self.loadController('MainModule', 'FieldsController', opts);
				self.loadControllers('SidebarModule', ['SidebarController'], opts);
			})
			.add(new RegExp(jsRoutes.controllers.TransGraphController.history('(.*)', '(.*)').url.substr(1), 'i'), function (institutionId, graphId) {
				console.log("History of Graph " + graphId);

				var opts = {
					institutionId: institutionId,
					graphId: graphId,
					section: 'history'
				}

				self.loadController('MainModule', 'HistoryController', opts);
				self.loadControllers('SidebarModule', ['SidebarController'], opts);
				self.loadController('BreadcrumbModule', 'BreadcrumbController', opts);
			})
			.add(new RegExp(jsRoutes.controllers.StepController.configure('(.*)', '(.*)', '(.*)').url.substr(1), 'i'), function (institutionId, graphId, stepId) {
				console.log('Edit step', stepId);

				var opts = {
					institutionId: institutionId,
					graphId: graphId,
					stepId: stepId,
					section: 'configure'
				};
				self.loadControllers('MainModule', ['StepController', 'StepModalController'], opts);
				self.loadControllers('SidebarModule', ['SidebarController'], opts);
				self.loadController('BreadcrumbModule', 'BreadcrumbController', opts);
			})
			.add(new RegExp(jsRoutes.controllers.ExecutionController.logs('(.*)', '(.*)', '(.*)').url.substr(1), 'i'), function (institutionId, graphId, executionId) {
				console.log('Execution', executionId, 'logs');

				var opts = {
					institutionId: institutionId,
					graphId: graphId,
					executionId: executionId,
					section: 'logs'
				};
				self.loadController('MainModule', 'LogsController', opts);
				self.loadControllers('SidebarModule', ['SidebarController'], opts);
				self.loadController('BreadcrumbModule', 'BreadcrumbController', opts);
			})
			.add(new RegExp(jsRoutes.controllers.ExecutionController.metrics('(.*)', '(.*)', '(.*)').url.substr(1), 'i'), function (institutionId, graphId, executionId) {
				console.log('Execution', executionId, 'Steps Metrics');

				var opts = {
					institutionId: institutionId,
					graphId: graphId,
					executionId: executionId,
					section: 'metrics'
				};
				self.loadController('MainModule', 'StepMetricsController', opts);
				self.loadControllers('SidebarModule', ['SidebarController'], opts);
				self.loadController('BreadcrumbModule', 'BreadcrumbController', opts);
			})
			.add(new RegExp(jsRoutes.controllers.ExecutionController.previewStep('(.*)', '(.*)', '(.*)', '(.*)').url.substr(1), 'i'), function (institutionId, graphId, executionId, stepId) {
				console.log('Execution', executionId, 'Step', stepId, 'Preview Step Data');

				var opts = {
					institutionId: institutionId,
					graphId: graphId,
					executionId: executionId,
					stepId: stepId,
					section: 'previewStep'
				};
				self.loadController('MainModule', 'PreviewStepController', opts);
				self.loadControllers('SidebarModule', ['SidebarController'], opts);
				self.loadController('BreadcrumbModule', 'BreadcrumbController', opts);
			})
			.add(new RegExp(jsRoutes.controllers.ExecutionController.previewData('(.*)', '(.*)', '(.*)').url.substr(1), 'i'), function (institutionId, graphId, executionId) {
				console.log('Execution', executionId, 'Preview Data');

				var opts = {
					institutionId: institutionId,
					graphId: graphId,
					executionId: executionId,
					section: 'previewData'
				};
				self.loadController('MainModule', 'PreviewDataController', opts);
				self.loadControllers('SidebarModule', ['SidebarController'], opts);
				self.loadController('BreadcrumbModule', 'BreadcrumbController', opts);
			})
			.add(new RegExp(jsRoutes.controllers.TransGraphController.selectTask('(.*)', '(.*)').url.substr(1), 'i'), function (institutionId, graphId) {
				console.log("Graph " + graphId + " has been selected");

				var opts = {
					institutionId: institutionId,
					graphId: graphId,
					section: 'selectTask'
				}
				self.loadControllers('MainModule', ['GraphController', 'RemoteExecutionController'], opts);
				self.loadControllers('SidebarModule', ['SidebarController'], opts);
				self.loadController('BreadcrumbModule', 'BreadcrumbController', opts);
			})
			.add(new RegExp(jsRoutes.controllers.InstitutionController.scheduler('(.*)').url.substr(1), 'i'), function (institutionId) {
				console.log("Institution " + institutionId + " Scheduler");

				const opts = {
					institutionId: institutionId
				}
				self.loadControllers('MainModule', ['SchedulerController'], opts);
				self.loadControllers('SidebarModule', ['SidebarController'], opts);
			})
			.add(function () {
				console.log("homepage");

				self.loadControllers('MainModule', ['HomeController', 'InstitutionController', 'TaskController', 'ServerController', 'DataSourceController']);
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

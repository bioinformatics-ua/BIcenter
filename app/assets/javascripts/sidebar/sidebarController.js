define('SidebarController', ['Controller', 'SidebarView', 'Router', 'Institution', 'Task', 'Alert', 'underscore', 'jsRoutes'], function (Controller, SidebarView, Router, Institution, Task, Alert, _, jsRoutes) {
	var SidebarController = function (module) {
		Controller.call(this, module, new SidebarView(this));
	};

	// Inheritance from super class
	SidebarController.prototype = Object.create(Controller.prototype);
	var _super_ = Controller.prototype;

	SidebarController.prototype.initialize = function ($container) {
		_super_.initialize.call(this, $container);
		this.getComponents();

	};

	SidebarController.prototype.getComponents = function () {
		var context = this;
		Institution.getComponents(function (components) {
			_.each(components, function (category) {
				_.each(category.components, function (component) {
					component.image = jsRoutes.controllers.SvgController.getImage('small', component.name + '.svg').url;
				});
			});
			context.view.loadComponents(components);
		});
	};

	return SidebarController;
});

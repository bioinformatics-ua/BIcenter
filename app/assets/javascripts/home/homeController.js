define('HomeController', ['Controller', 'HomeView', 'Institution', 'jquery', 'jsRoutes', 'jquery-cookie'], function (Controller, HomeView, Institution, $, jsRoutes) {
	const HomeController = function (module) {
		Controller.call(this, module, new HomeView(this));
	};

	// Inheritance from super class
	HomeController.prototype = Object.create(Controller.prototype);
	const _super_ = Controller.prototype;

	HomeController.prototype.initialize = function ($container) {
		_super_.initialize.call(this, $container);

		const context = this;
		Institution.getInstitutions(function (institutions) {
			context.view.loadInstitutions(institutions);
		});
	};

	return HomeController;
});

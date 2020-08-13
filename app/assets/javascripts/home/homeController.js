define('HomeController', ['Controller', 'HomeView', 'User', 'jquery', 'jsRoutes', 'jquery-cookie'], function (Controller, HomeView, $, jsRoutes) {
	const HomeController = function (module) {
		Controller.call(this, module, new HomeView(this));
	};

	// Inheritance from super class
	HomeController.prototype = Object.create(Controller.prototype);
	const _super_ = Controller.prototype;

	HomeController.prototype.initialize = function ($container) {
		_super_.initialize.call(this, $container);
	};

	return HomeController;
});

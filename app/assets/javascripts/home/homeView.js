define('HomeView', ['jquery', 'View'], function ($, View) {
	const HomeView = function (controller) {
		View.call(this, controller, 'home');
	};

	// Inheritance from super class
	HomeView.prototype = Object.create(View.prototype);
	const _super_ = View.prototype;

	HomeView.prototype.initialize = function ($container) {
		_super_.initialize.call(this, $container);
	};

	return HomeView;
});

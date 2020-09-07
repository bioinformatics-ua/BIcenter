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

	HomeView.prototype.loadInstitutions = function (institutions) {
		const html = JST['home']({
			institutions: institutions
		});
		console.log(institutions);
		this.$container.html(html);
		this._loadViewComponents();
	};

	return HomeView;
});

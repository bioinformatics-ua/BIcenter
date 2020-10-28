define('SidebarView', ['View', 'jsRoutes', 'jquery-ui', 'templates'], function (View, jsRoutes) {
	var SidebarView = function (controller) {
		View.call(this, controller, 'sidebar');
	};

	// Inheritance from super class
	SidebarView.prototype = Object.create(View.prototype);
	var _super_ = View.prototype;

	SidebarView.prototype.initialize = function ($container) {
		_super_.initialize.call(this, $container);
	};

	SidebarView.prototype.loadComponents = function (components) {
		let html = JST['sidebar']({
			categories: components
		});
		this.$container.html(html);
		this._loadViewComponents();

		this.$elements.components
			.find('ul')
			.find('li')
			.draggable({
				cursorAt: {left: 5, top: 5},
				helper: function () {
					var $el = $(this);
					var data = {
						shortName: $el.data('shortname'),
						name: $el.data('name'),
						image: jsRoutes.controllers.SvgController.getImage('middle', $el.data('shortname') + '.svg').url
					};
					return JST['draggable-component-helper'](data);
				},
				stack: '.content-wrapper',
				revert: 'invalid',
				zIndex: 1000000,
				appendTo: 'body',
				scope: 'components'
			});

		this.$elements.components.show();
		this.$elements.componentsBtn.addClass('selected');
	};

	return SidebarView;
});

define('SidebarView', ['View', 'jquery-ui', 'templates'], function (View) {
    var SidebarView = function (controller) {
        View.call(this, controller, 'sidebar');
    };

    // Inheritance from super class
    SidebarView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    SidebarView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        this.$elements.componentsBtn.addClass('selected');
    };

    SidebarView.prototype.loadInstitutions = function (institutions) {
        var html = JST['sidebar']({
            institutions: institutions
        });
        this.$container.html(html);
        this._loadViewComponents();
        this.$elements.components.hide();

        this.$elements.componentsBtn.removeClass('selected');
        this.$elements.tasksBtn.addClass('selected');
    };

    SidebarView.prototype.loadComponents = function (components) {
        var html = JST['sidebar-components']({categories: components});
        this.$elements.components.html(html);
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
                        name: $el.data('name')
                    };
                    return JST['draggable-component-helper'](data);
                },
                stack: '.content-wrapper',
                revert: 'invalid',
                zIndex: 1000000,
                appendTo: 'body',
                scope: 'components'
            });

        this.$elements.institutions.hide();
        this.$elements.components.show();

        this.$elements.tasksBtn.removeClass('selected');
        this.$elements.componentsBtn.addClass('selected');
    };

    return SidebarView;
});
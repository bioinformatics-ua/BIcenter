define('SidebarView', ['View'], function (View) {
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

    SidebarView.prototype.loadComponents = function () {
        this.$elements.institutions.hide();
        this.$elements.components.show();

        this.$elements.tasksBtn.removeClass('selected');
        this.$elements.componentsBtn.addClass('selected');
    };

    return SidebarView;
});
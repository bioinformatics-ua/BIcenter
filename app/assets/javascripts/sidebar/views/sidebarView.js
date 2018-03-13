define('SidebarView', ['View'], function (View) {
    var SidebarView = function (controller) {
        View.call(this, controller, 'sidebar');
    };

    // Inheritance from super class
    SidebarView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    SidebarView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    SidebarView.prototype.loadTasks = function (tasks) {
        var html = JST['sidebar']({
            tasks: tasks
        });
        this.$container.html(html);
        this._loadViewComponents();
        this.$elements.components.hide();

        this.$elements.componentsBtn.removeClass('selected');
        this.$elements.tasksBtn.addClass('selected');
    };

    SidebarView.prototype.loadComponents = function () {
        this.$elements.tasks.hide();
        this.$elements.components.show();

        this.$elements.tasksBtn.removeClass('selected');
        this.$elements.componentsBtn.addClass('selected');
    };

    return SidebarView;
});
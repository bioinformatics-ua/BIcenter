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

    return SidebarView;
});
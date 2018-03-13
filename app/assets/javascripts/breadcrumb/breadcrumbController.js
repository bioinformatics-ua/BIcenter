define('BreadcrumbController', ['Controller', 'BreadcrumbView'], function (Controller, BreadcrumbView) {
    var BreadcrumbController = function (module) {
        Controller.call(this, module, new BreadcrumbView(this));
    };

    // Inheritance from super class
    BreadcrumbController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    BreadcrumbController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    return BreadcrumbController;
});
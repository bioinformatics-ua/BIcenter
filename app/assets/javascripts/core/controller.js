define('Controller', [], function () {
    var Controller = function (module, view) {
        this.module = module;
        this.view = view;
        this.data = {};
    };

    Controller.prototype.initialize = function ($container, data) {
        this.view.initialize($container, data);
    };

    return Controller;
});

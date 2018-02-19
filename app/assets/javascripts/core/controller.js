define('Controller', [], function () {
    var Controller = function (module, view) {
        this.module = module;
        this.view = view;
        this.data = {};
    };

    Controller.prototype.initialize = function ($container, data) {
        this.view.initialize($container, data);
    };

    Controller.prototype.destroy = function () {
        this.view.destroy();
        this.view = null;
        delete this.view;

        this.data = null;
        delete this.data;

        this.module = null;
        delete this.module;
    };

    return Controller;
});

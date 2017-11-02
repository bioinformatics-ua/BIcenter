define('TransSettingsController', ['Controller', 'TransSettingsView'], function (Controller, TransSettingsView) {
    var TransSettingsController = function (module) {
        Controller.call(this, module, new TransSettingsView(this));
    };

    // Inheritance from super class
    TransSettingsController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    TransSettingsController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    TransSettingsController.prototype.testClick = function () {
        console.log("Testing clicks from controller");
    };

    return TransSettingsController;
});
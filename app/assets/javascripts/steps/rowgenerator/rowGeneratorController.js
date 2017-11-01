define('RowGeneratorController', ['Controller', 'RowGeneratorView'], function (Controller, RowGeneratorView) {
    var RowGeneratorController = function (module) {
        Controller.call(this, module, new RowGeneratorView(this));
    };

    // Inheritance from super class
    RowGeneratorController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    RowGeneratorController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    RowGeneratorController.prototype.testClick = function () {
        console.log("Testing clicks from controller");
    };

    return RowGeneratorController;
});
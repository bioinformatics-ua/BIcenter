define('ResultController', ['Controller', 'ResultView'], function (Controller, ResultView) {
    var ResultController = function (module) {
        Controller.call(this, module, new ResultView(this));
    };

    // Inheritance from super class
    ResultController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    ResultController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    ResultController.prototype.testClick = function () {
        console.log("Testing clicks from controller");
    };

    return ResultController;
});
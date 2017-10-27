define('TestController', ['Controller', 'TestView'], function (Controller, TestView) {
    var TestController = function (module) {
        Controller.call(this, module, new TestView(this));
    };

    // Inheritance from super class
    TestController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    TestController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    TestController.prototype.testClick = function () {
        console.log("Testing clicks from controller");
    };

    return TestController;
});
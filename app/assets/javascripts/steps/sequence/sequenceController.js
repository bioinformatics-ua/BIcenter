define('SequenceController', ['Controller', 'SequenceView'], function (Controller, SequenceView) {
    var SequenceController = function (module) {
        Controller.call(this, module, new SequenceView(this));
    };

    // Inheritance from super class
    SequenceController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    SequenceController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    SequenceController.prototype.testClick = function () {
        console.log("Testing clicks from controller");
    };

    return SequenceController;
});
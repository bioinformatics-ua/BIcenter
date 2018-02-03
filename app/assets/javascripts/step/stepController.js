define('StepController', ['Controller', 'StepView', 'Step', 'Router'], function (Controller, StepView, Step, Router) {
    var StepController = function (module) {
        Controller.call(this, module, new StepView(this));
    };

    // Inheritance from super class
    StepController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    StepController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        if (this.stepName) {
            this.getSchema(this.stepName);
        }
    };

    StepController.prototype.getSchema = function (stepType) {
        this.stepType = stepType;

        var self = this;
        Step.getSchema(this.stepType, function (step) {
            console.log(step);
            self.view.loadStep(step);
        });
    };

    /**
     * Returns to the pipeline view.
     */
    StepController.prototype.cancelClick = function () {
        Router.navigate('/');
    };

    return StepController;
});
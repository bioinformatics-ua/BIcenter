define('StepController', ['Controller', 'StepView', 'Step', 'Router'], function (Controller, StepView, Step, Router) {
    var StepController = function (module) {
        Controller.call(this, module, new StepView(this));
    };

    // Inheritance from super class
    StepController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    StepController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        if (this.stepId) {
            this.getStep(this.stepId);
        }
    };

    StepController.prototype.getStep = function (stepId) {
        this.stepId = stepId;

        var self = this;
        Step.getStep(this.stepId, function (step) {
            console.log(step);
            self.step = step;
            self.formName = step.component.shortName+"_form";
            self.view.loadStep(step);
        });
    };

    /**
     * Returns to the pipeline view.
     */
    StepController.prototype.cancelClick = function () {
        Router.navigate('/');
    };

    /**
     * Apply step configuration changes.
     */
    StepController.prototype.submitClick = function () {
        var shortName = this.step.component.shortName;
        var $form = this.view.$elements[shortName + '_form'];
        var formValues = getFormData($form);
        console.log(formValues);

        Step.applyChanges(this.stepId,formValues,function(step){
            console.log("Step",this.stepId,"has been updated!");
        });

        Router.navigate('/');
    };

    /**
     * Convert form data to json object.
     * @param $form
     * @returns {{}}
     */
    function getFormData($form){
        var unindexed_array = $form.serializeArray();
        var indexed_array = {};

        $.map(unindexed_array, function(n, i){
            indexed_array[n['name']] = n['value'];
        });

        return indexed_array;
    }

    return StepController;
});
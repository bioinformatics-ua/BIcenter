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
            this.getSchema(this.stepId);
        }
    };

    StepController.prototype.getSchema = function (stepId) {
        this.stepId = stepId;

        var self = this;
        Step.getSchema(this.stepId, function (step) {
            console.log(step);
            self.formName = step.shortName+"_form";
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
        /*
        var $form = this.view.$elements.checksum_form;
        var formValues = getFormData($form);

        Step.applyChanges(this.stepId,function(step){

        });
        Router.navigate('/');
        */
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
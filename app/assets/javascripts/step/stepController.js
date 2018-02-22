define('StepController', ['Controller', 'StepView', 'Step', 'Router', 'underscore'], function (Controller, StepView, Step, Router, _) {
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
        var formValues = $form.serializeForm();

        var context = this;
        _.each(this.view.tables, function (table) {
            var tableId = table.id;
            var table = context.view.$elements[tableId].DataTable();
            formValues[tableId] = table.rows().data().toArray();
        });

        console.log(formValues);

        Step.applyChanges(this.stepId,formValues,function(step){
            console.log("Step",this.stepId,"has been updated!");
        });

        Router.navigate('/');
    };

    StepController.prototype.updateTableRow = function (tableId, rowId, formValues) {
        var table = this.view.$elements[tableId].DataTable();
        table.row(rowId).data(formValues);

        this.view._loadViewComponents();
    };

    return StepController;
});
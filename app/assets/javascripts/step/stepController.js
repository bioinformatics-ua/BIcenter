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
        var context = this;
        Step.getStep(stepId, function (step) {
            console.log(step);
            context.step = step;
            context.formName = step.component.shortName+"_form";
            Step.inputFieldsName(context.step.id, function(input){
                console.log(input);
                context.input = input;
                context.view.loadStep(step,input);
            })
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

    /**
     * Add table row after modal submission.
     * @param tableId
     * @param rowId
     * @param formValues
     */
    StepController.prototype.addTableRow = function (tableId, formValues) {
        var table = this.view.$elements[tableId].DataTable();
        table.row.add(formValues).draw( false );

        this.view._loadViewComponents();
    };

    /**
     * Update table row after modal submission.
     * @param tableId
     * @param rowId
     * @param formValues
     */
    StepController.prototype.updateTableRow = function (tableId, rowId, formValues) {
        var table = this.view.$elements[tableId].DataTable();
        table.row(rowId).data(formValues);

        this.view._loadViewComponents();
    };

    return StepController;
});
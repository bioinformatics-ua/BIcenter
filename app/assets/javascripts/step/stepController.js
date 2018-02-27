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
            Step.inputFieldsName(context.step.id, function(inputFields){
                console.log(inputFields);
                context.inputFields = inputFields;
                Step.outputStepsName(context.step.id, function(outputSteps) {
                    console.log(outputSteps);
                    context.outputSteps = outputSteps;
                    context.view.loadStep(step,inputFields,outputSteps);
                });
            });
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

        // Get condition values
        if(this.view.conditions) {
            _.each(this.view.conditions, function (condition) {
                formValues[condition.id] = context.view.$elements[condition.id].queryBuilder('getRules');
            });
        }

        // Get table values
        if(this.view.tables) {
            _.each(this.view.tables, function (table) {
                var tableId = table.id;
                var table = context.view.$elements[tableId].DataTable();
                formValues[tableId] = table.rows().data().toArray();
            });
        }

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

    StepController.prototype.resetCondition = function(){
        $('#builder-basic').queryBuilder('reset');
    }

    StepController.prototype.toggleSenseCondition = function(){
        var btn = $('#senseBtn');
        btn.toggleClass('btn-success');
        btn.toggleClass('btn-danger');
        var label = btn.text();
        if(label=="Negative") btn.text("Positive").show();
        else btn.text("Negative").show();
    }

    return StepController;
});
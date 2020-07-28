define('StepController', ['Controller', 'StepView', 'Institution', 'Step', 'Router', 'underscore', 'async'], function (Controller, StepView, Institution, Step, Router, _, async) {
    var StepController = function (module) {
        Controller.call(this, module, new StepView(this));
    };

    // Inheritance from super class
    StepController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    StepController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        this.data = {};
        if(this.stepId){
            this.getStep(this.stepId);
        }
    };

    StepController.prototype.getStep = function (stepId) {
        var context = this;

        async.parallel([
            function (callback) {
                Step.getStep(context.institutionId, stepId, function (step) {
                    context.step = step;
                    context.formName = step.component.shortName + "_form";

                    callback();
                });
            },
            function (callback) {
                Step.inputStepsName(context.institutionId, stepId, function (inputSteps) {
                    context.inputSteps = inputSteps;

                    callback();
                });
            },
            function (callback) {
                Step.inputFieldsName(context.institutionId, stepId, function (inputFields) {
                    if(_.keys(inputFields).length == 1){
                        _.each(inputFields, function(field){
                            context.inputFields = field.valueMetaList;
                        });
                    }
                    else{
                        context.streamFields = {};
                        _.each(_.keys(inputFields), function(key) {
                           context.streamFields[key] = inputFields[key].valueMetaList;
                        });
                    }

                    callback();
                });
            },
            function (callback) {
                Step.outputStepsName(context.institutionId, stepId, function (outputSteps) {
                    context.outputSteps = outputSteps;

                    callback();
                });
            },
            function (callback) {
                Step.getInstitution(context.institutionId, stepId, function(institutionId) {
                    Institution.getDataSources(institutionId, function (dataSources) {
                        context.dataSources = dataSources;
                        callback();
                    });
                });
            }
        ], function (err) {
            if (err) {
                console.error(err);
                return;
            }

            context.view.loadStep(context.step, context.inputSteps, context.inputFields, context.outputSteps, context.dataSources);
        });
    };

    /**
     * Returns to the pipeline view.
     */
    StepController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    StepController.prototype.updateColumn = function (tableId, name, component, $element) {
        var table = this.view.dataTables[tableId].clear().draw();

        var selectedVal;
        try{
            selectedVal = this.view.$elements[component].val();
        }
        catch(err) {
            try{
                selectedVal = component.val();
            }
            catch(err) {
                selectedVal = $element.val();
            }
        }

        if (selectedVal) {
            this.data[name] = this.streamFields[selectedVal];
        }
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
        if (this.view.conditions) {
            _.each(this.view.conditions, function (condition) {
                formValues[condition.id] = context.view.$elements[condition.id].queryBuilder('getRules');
            });
        }

        // Get table values
        if (this.view.tables) {
            _.each(this.view.tables, function (table) {
                var tableId = table.id;
                var table = context.view.$elements[tableId].DataTable();
                formValues[tableId] = table.rows().data().toArray();
            });
        }

        // Get files
        _.each($form[0], function(element){
            if(element.type === "file" && element.files.length > 0){
                var fileObject = element.files[0];

                fileObject = {
                    lastModified: fileObject.lastModified,
                    name: fileObject.name,
                    size: fileObject.size,
                    type: fileObject.type,
                    webkitRelativePath: fileObject.webkitRelativePath,
                }

                formValues[element.name] = fileObject;
            }
        });

        Step.applyChanges(this.institutionId, this.stepId, formValues, function (step) {
            console.log("Step", this.stepId, "has been updated!");
            Router.navigatePrevious();
        });
    };

    /**
     * Add table row after modal submission.
     * @param tableId
     * @param rowId
     * @param formValues
     */
    StepController.prototype.addTableRow = function (tableId, formValues) {
        var table = this.view.$elements[tableId].DataTable();
        table.row.add(formValues).draw(false);

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

    StepController.prototype.resetCondition = function () {
        $('#builder-basic').queryBuilder('reset');
    }

    StepController.prototype.toggleSenseCondition = function () {
        var btn = $('#senseBtn');
        btn.toggleClass('btn-success');
        btn.toggleClass('btn-danger');
        var label = btn.text();
        if (label == "Negative") btn.text("Positive").show();
        else btn.text("Negative").show();
    }

    return StepController;
});
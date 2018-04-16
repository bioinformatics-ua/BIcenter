define('StepModalController', ['Controller', 'StepModalView'], function (Controller, StepModalView) {
    var StepModalController = function (module) {
        Controller.call(this, module, new StepModalView(this));
    };

    // Inheritance from super class
    StepModalController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    StepModalController.prototype.initialize = function ($container) {
        var self = this;
        _super_.initialize.call(this, $container);

        // Add button to save data
        this.view.addButtons({
            save: {
                label: '<i class="fa fa-check-circle"></i> Save',
                className: 'btn-success',
                callback: 'controller.add()'
            }
        });
    };

    StepModalController.prototype.updateLengthPrecision = function (select) {
        this.field = this.view.$elements[select].val();
        var fields = this.module.controllers.StepController.inputFields;

        var context = this;
        _.each(fields, function(f) {
            if (f.name == context.field) {
                context.length = f.length;
                context.precision = f.precision;
            }
        });
        this.view.$elements[this.view.length].val(context.length);
        this.view.$elements[this.view.precision].val(context.precision);
    }

    /**
     * Update table.
     * @param event
     */
    StepModalController.prototype.add = function (event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        var tableId = this.view.tableId,
            rowId = this.view.rowId;

        var $form = null;
        if(rowId == undefined) $form = this.view.$elements[tableId + '_'];
        else $form = this.view.$elements[tableId + '_' + rowId];
        var formValues = $form.serializeForm();

        var stepController = this.module.controllers['StepController'];
        if (stepController) {
            if(rowId == undefined) stepController.addTableRow(tableId, formValues);
            else stepController.updateTableRow(tableId, rowId, formValues);
        }

        this.view.hide();
    }

    return StepModalController;
});
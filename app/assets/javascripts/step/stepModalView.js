define('StepModalView', ['Modal','Step'], function (Modal,Step) {
    var StepModalView = function (controller) {
        Modal.call(this, controller, 'stepModal', 'Edit Entry');
    };

    // Inheritance from super class
    StepModalView.prototype = Object.create(Modal.prototype);
    var _super_ = Modal.prototype;

    StepModalView.prototype.initialize = function ($container, data) {
        _super_.initialize.call(this, $container, data);
    };

    StepModalView.prototype.clear = function () {

    };

    StepModalView.prototype.show = function (tableId,rowId,data) {
        // Clear modal before show
        this.clear();
        this.tableId = tableId;
        this.rowId = rowId;

        // Show modal
        _super_.show.call(this);

        var html = JST['stepModalContent']({table:tableId,row:rowId,fields: data});
        this.$elements.insideContainer.html(html);
        this._loadViewComponents();

        var context = this;
        _.each(data, function(field) {
            if(field.source == "inputLength@selectName"){
                context.length = field.id;
            }
            else if(field.source == "inputPrecision@selectName"){
                context.precision = field.id;
            }
        });

        _.each(data, function(field){
           if(field.type == 'select') {
               context.controller.updateLengthPrecision(field.id);
           }
        });
    };

    return StepModalView;
});
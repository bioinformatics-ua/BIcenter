define('StepModalView', ['Modal'], function (Modal) {
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

        console.log({row: data});
        var html = JST['stepModalContent']({table:tableId,row:rowId,fields: data});
        this.$elements.insideContainer.html(html);
        this._loadViewComponents();
    };

    return StepModalView;
});
define('TaskView', ['Modal'], function (Modal) {

    var TaskView = function (controller) {
        Modal.call(this, controller, 'taskModal', 'Edit Task');
    };

    // Inheritance from super class
    TaskView.prototype = Object.create(Modal.prototype);
    var _super_ = Modal.prototype;

    TaskView.prototype.clear = function () {

    };

    TaskView.prototype.show = function (task,institutions) {
        // Clear modal before show
        this.clear();

        // Show modal
        _super_.show.call(this);

        var html = JST['taskModalContent']({task: task, institutions: institutions});
        this.$elements.insideContainer.html(html);
        this._loadViewComponents();
    };

    return TaskView;
});
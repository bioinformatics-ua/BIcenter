define('LogsView', ['View'], function (View) {
    var LogsView = function (controller) {
        View.call(this, controller, 'logs');
    };

    // Inheritance from super class
    LogsView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    LogsView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    LogsView.prototype.loadTask = function(execution,task) {
        var html = JST['logs']({
            execution: execution,
            task: task,
        });
        this.$container.html(html);
        this._loadViewComponents();

        // Render the transformation with a success or failure icon on each step.
        this.renderGraph(task.id, this.$elements.preview_graph[0]);
    };

    return LogsView;
});
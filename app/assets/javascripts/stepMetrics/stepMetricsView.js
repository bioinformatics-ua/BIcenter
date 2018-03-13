define('StepMetricsView', ['View', 'dataTables'], function (View) {
    var StepMetricsView = function (controller) {
        View.call(this, controller, 'stepMetrics');
    };

    // Inheritance from super class
    StepMetricsView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    StepMetricsView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    StepMetricsView.prototype.loadTask = function(execution,task) {
        var html = JST['stepMetrics']({
            execution: execution,
            task: task.name,
            columns: _.allKeys(task.execution[0].stepMetrics[0]),
            data:task.execution[0].stepMetrics
        });

        this.$container.html(html);
        this._loadViewComponents();
        this.$elements.table.DataTable();
    };

    return StepMetricsView;
});
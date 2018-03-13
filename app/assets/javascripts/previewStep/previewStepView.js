define('PreviewStepView', ['View', 'dataTables'], function (View) {
    var PreviewStepView = function (controller) {
        View.call(this, controller, 'previewStep');
    };

    // Inheritance from super class
    PreviewStepView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    PreviewStepView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    PreviewStepView.prototype.loadTask = function(execution,task) {
        var html = JST['previewStep']({
            execution: execution,
            task: task.name,
            step: task.execution[0].stepStatus[0].stepName
        });
        this.$container.html(html);
        this._loadViewComponents();

        this.$elements.table.DataTable(
        {
            dom: "frtip",
            order: [[1, 'asc']],
            columns: _.map(task.execution[0].dataRows[0].keyValues, function(kv){ return {"title": kv.key} }),
            data: _.map(task.execution[0].dataRows, function(row){return _.map(row.keyValues, function(kv){return kv.value}) })
        })
    };

    return PreviewStepView;
});
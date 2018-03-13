define('HistoryView', ['View'], function (View) {
    var HistoryView = function (controller) {
        View.call(this, controller, 'history');
    };

    // Inheritance from super class
    HistoryView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    HistoryView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    HistoryView.prototype.loadTask = function(task) {
        var dates = _.map(task.executions, function(execution){ return {"date":execution.startDate} })
        dates = _.uniq(dates, true, function(x){return x.date});
        var html = JST['history']({
            dates: dates,
            task: task
        });
        this.$container.html(html);
        this._loadViewComponents();
    };

    return HistoryView;
});
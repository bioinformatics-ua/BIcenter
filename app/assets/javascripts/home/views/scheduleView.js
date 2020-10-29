define('SchedulerView', ['View','dataTables'], function (View) {
    var SchedulerView = function (controller) {
        View.call(this, controller, 'scheduler');
    };

    // Inheritance from super class
    SchedulerView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    SchedulerView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    SchedulerView.prototype.show = function(institution, schedules) {
        var columns = ["Task","Server","Start","Type"];
        var html = JST['scheduler']({
            institution: institution,
            columns: columns,
            data: schedules
        });
        this.$container.html(html);
        this._loadViewComponents();
        this.$elements.table.DataTable();
    };

    return SchedulerView;
});
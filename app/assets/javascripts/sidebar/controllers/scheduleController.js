define('SchedulerController', ['Controller', 'async', 'SchedulerView', 'Router', 'Institution', 'Execution'], function (Controller, async, SchedulerView, Router, Institution, Execution) {
    var SchedulerController = function (module) {
        Controller.call(this, module, new SchedulerView(this));
    };

    // Inheritance from super class
    SchedulerController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    SchedulerController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        var context = this;
        async.parallel(
            [
                function (callback) {
                    if(context.institutionId) {
                        Institution.getInstitutionName(context.institutionId, function(name) {
                            context.institutionName = name;
                            callback();
                        });
                    }
                },
                function (callback) {
                    if(context.institutionId) {
                        Institution.getSchedules(context.institutionId, function(schedules) {
                            context.schedules = schedules;
                            callback();
                        });
                    }
                }
            ],
            function (err) {
                if (err) {
                    console.error(err);
                    return;
                }

                context.view.show(context.institutionName,context.schedules);
            }
        );
    };

    SchedulerController.prototype.deleteSchedule = function(taskId,scheduleId,rowId){
        var table = this.view.$elements.table.DataTable();
        table.row(rowId).remove().draw();

        Execution.deleteSchedule(this.institutionId,taskId,scheduleId, function(unscheduled){

        });
    };

    /**
     * Returns to the pipeline view.
     */
    SchedulerController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    return SchedulerController;
});
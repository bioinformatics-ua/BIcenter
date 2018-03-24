define('SchedulerController', ['Controller', 'SchedulerView', 'Router', 'Institution'], function (Controller, SchedulerView, Router, Institution) {
    var SchedulerController = function (module) {
        Controller.call(this, module, new SchedulerView(this));
    };

    // Inheritance from super class
    SchedulerController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    SchedulerController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        var context = this;
        if(this.institutionId){
            Institution.getSchedules(this.institutionId, function(schedules) {
                context.view.show(schedules);
            });
        }
    };

    /**
     * Returns to the pipeline view.
     */
    SchedulerController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    return SchedulerController;
});
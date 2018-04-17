define('TaskController', ['Controller', 'TaskView', 'async', 'Router', 'Institution', 'Task'], function (Controller, TaskView, async, Router, Institution, Task) {
    var TaskController = function (module) {
        Controller.call(this, module, new TaskView(this));
    };

    // Inheritance from super class
    TaskController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    TaskController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        // Add button to save data
        this.view.addButtons({
            save: {
                label: '<i class="fa fa-check-circle"></i> Save',
                className: 'btn-success',
                callback: 'controller.updateTask()'
            },
            delete: {
                label: '<i class="fa fa-times-circle"></i> Delete',
                className: 'btn-danger pull-left',
                callback: 'controller.deleteTask()'
            }
        });
    };

    TaskController.prototype.loadTask = function(institutionId,taskId){
        this.institutionId = institutionId;

        var context = this;
        async.parallel(
            [
                function (callback) {
                    Task.getTaskDetails(institutionId,taskId, function(task){
                        context.task = task;
                        callback();
                    });
                },
                function (callback) {
                    Institution.getInstitutions(function(institutions){
                        context.institutions = institutions;
                        callback();
                    });
                }
            ],
            function (err) {
                if (err) {
                    console.error(err);
                    return;
                }

                context.view.show(context.task,context.institutions);
            }
        );
    }

    TaskController.prototype.updateTask = function(event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        var $form = this.view.$elements[this.task.id];
        var formValues = $form.serializeForm();

        var context = this;
        Task.updateTask(this.institutionId, this.task.id, formValues, function (task) {
            console.log("Task", task.id, "has been updated!");
            var sidebarController = context.module.controllers['SidebarController'];
            sidebarController.getTasks();
        });

        this.view.hide();
    };

    TaskController.prototype.deleteTask = function(event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        var context = this;
        Task.deleteTask(this.institutionId, this.task.id, function(){
            context.view.hide();
            var sidebarController = context.module.controllers['SidebarController'];
            sidebarController.getTasks();
        });
    }

    /**
     * Returns to the pipeline view.
     */
    TaskController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    return TaskController;
});
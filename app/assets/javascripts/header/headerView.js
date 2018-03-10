define('HeaderView', ['View','Task','underscore'], function (View,Task,_) {
    var HeaderView = function (controller) {
        View.call(this, controller, 'header');
    };

    // Inheritance from super class
    HeaderView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    HeaderView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        this.notification_counter = 0;
    };

    /**
     * Allows to write a transformation xml, to be converted in a mxGraph.
     */
    HeaderView.prototype.importXml = function () {
        var graphController = app.modules.MainModule.controllers.GraphController;
        graphController.view.$elements.source.click();
        graphController.view.$elements.board.css({'height': '65vh'});
        graphController.view.$elements.xmlBtn.css("display", "block");
    }

    /**
     * Returns the steps of the actual transformation.
     */
    HeaderView.prototype.getSteps = function(){
        var graphController = app.modules.MainModule.controllers.GraphController;
        var context = this;

        Task.getSteps(graphController.view.taskId,
            function (task) {
                context.$elements.steps.empty();

                var taskName = task.name;
                var $item =
                    $('<li class="dropdown-submenu">').append(
                        $('<a tabindex="-1">').text(taskName),
                        $('<ul class="dropdown-menu">').append(
                            $('<li>').append(
                                $('<a tabindex="-1">').text("Edit Transformation")
                            )
                        )
                    );
                context.$elements.steps.append($item);

                var steps = task.steps;
                for (var i = 0; i < steps.length; i++) {
                    var stepId = steps[i].id;
                    var vertexId = steps[i].graphId;
                    var stepLabel = steps[i].label;

                    var $item =
                        $('<li class="dropdown-submenu">').append(
                            $('<a tabindex="-1">').text(vertexId+": "+stepLabel),
                            $('<ul class="dropdown-menu">').append(
                                $('<li>').append($('<a tabindex="-1" view-click="controller.showStepDialog('+stepId+')">').text("Edit Step")),
                                $('<li>').append($('<a tabindex="-1">').text("Edit Step Description")),
                                $('<li class="divider">'),
                                $('<li>').append($('<a tabindex="-1" view-click="controller.showStepInput('+stepId+')">').text("Show Input Fields")),
                                $('<li>').append($('<a tabindex="-1" view-click="controller.showStepOutput('+stepId+')">').text("Show Output Fields"))
                            )
                        )
                    context.$elements.steps.append($item);
                }

                context._loadViewComponents();
            }
        );
    }

    /**
     * Retrieves all existing transformations.
     */
    HeaderView.prototype.getExecutions = function(){
        var executions = this.controller.executions;

        this.$elements.executions.empty();
        for (var i = 0; i < executions.length; i++) {
            var $item = $('<li>').append(
                $('<a href="#" view-click="controller.showPreviewResults('+executions[i]['transName']+','+executions[i]['executionId']+');">').text(executions[i]['transName'])
            );
            this.$elements.executions.append($item);
        }
        this._loadViewComponents();
    }

    /**
     * Retrieves all existing transformations.
     */
    HeaderView.prototype.getTransformations = function(){
        var context = this;
        this.$elements.history.empty();

        // Fetch transformations.
        Task.getTasks(function(tasks){
            _.each(tasks, function (task) {
                var $item = $('<li>').append(
                    $('<a view-click="controller.showHistory('+task.id+');">').text(task.name)
                );
                context.$elements.history.append($item);
            });
            context._loadViewComponents();
        });
    }

    /**
     * Sends a notification when a transformation is submitted.
     * @param name Transformation name.
     * @param id Execution id.
     * @param state State of the transformation's execution.
     */
    HeaderView.prototype.transSubmissionNotification = function(name,id,state){
        if(state=="Finished"){
            this.notification_counter--;
            this.$elements.notification_counter.text(this.notification_counter);
            $('#notification_'+id).remove();
        }
        var $li = $('<li>').append(
            $("<a id='notification_"+id+"' href=\"#\">").append(
                $("<ul class='menu'>").append(
                    $("<li>").append(
                        $("<strong>").text("Transaction: "),
                        name
                    ),
                    $("<li>").append(
                        $("<strong>").text("State: "),
                        state
                    )
                )
            )
        );
        this.$elements.notification_list.append($li);
        this.notification_counter++;
        this.$elements.notification_counter.text(this.notification_counter);
    }

    /**
     * Remove transformation execution notification, since the results were already seen.
     * @param executionId Transformation execution Id.
     */
    HeaderView.prototype.removeTransNotification = function(executionId){
        jQuery.fn.exists = function(){ return this.length > 0; }
        if ($('#notification_'+executionId).exists()) {
            this.notification_counter--;
            this.$elements.notification_counter.text(this.notification_counter);
            $('#notification_'+executionId).remove();
        }
    }

    return HeaderView;
});
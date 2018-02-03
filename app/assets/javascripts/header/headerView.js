define('HeaderView', ['View'], function (View) {
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

        graphController.view.$elements.source.click();
        var transXml = graphController.view.$elements.xml.val();
        graphController.view.$elements.source.click();

        var node = (new DOMParser()).parseFromString(transXml, "text/xml").documentElement;

        var nodes = node.querySelectorAll("*");

        this.$elements.steps.empty();
        for (var i = 0; i < nodes.length; i++) {
            // Append the transaction node to the nodes list.
            if (nodes[i].tagName == "Step" && nodes[i].hasAttribute('name')){
                var transName = nodes[i].getAttribute("name");
                var transId = nodes[i].getAttribute("id");
                var transLabel = transId + ": " + transName;

                var $item =
                    $('<li class="dropdown-submenu">').append(
                        $('<a tabindex="-1" href="#">').text(transLabel),
                        $('<ul class="dropdown-menu">').append(
                            $('<li>').append(
                                $('<a tabindex="-1" href="#" view-click="controller.transSettingsDialog('+i+');">').text("Edit Transformation")
                            )
                        )
                    );
                this.$elements.steps.append($item);
            }
            // Append each step to the nodes list.
            else if (nodes[i].tagName == "Step" && nodes[i].hasAttribute('ctype')) {
                var stepType = nodes[i].getAttribute("ctype");
                var stepName = nodes[i].getAttribute("label");
                var stepId = nodes[i].getAttribute("id");
                var stepLabel = stepId + ": " + stepName;

                var item = '<li class="dropdown-submenu">';
                item += '<a tabindex="-1" href="#">';
                item += stepLabel;
                item += '</a>';
                item += '<ul class="dropdown-menu">';
                item += '<li><a tabindex="-1" href="#" view-click="controller.showStepDialog('+nodes[i].getAttribute('ctype')+","+i+')">Edit Step</a></li>';
                item += '<li><a tabindex="-1" href="#">Edit Step Description</a></li>';
                item += '<li class="divider"></li>';
                item += '<li><a tabindex="-1" href="#" view-click="controller.showFieldsDialog('+stepName+","+true+')">Show Input Fields</a></li>';
                item += '<li><a tabindex="-1" href="#" view-click="controller.showFieldsDialog('+stepName+","+false+')">Show Output Fields</a></li>';
                item += '</ul>';
                item += '</li>';

                this.$elements.steps.append(item);
            }
        }
        this.global_nodes = nodes;
        this._loadViewComponents();
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
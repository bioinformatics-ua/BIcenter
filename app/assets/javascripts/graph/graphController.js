define('GraphController', ['Controller', 'GraphView', 'Task'], function (Controller, GraphView,Task) {
    var GraphController = function (module) {
        Controller.call(this, module, new GraphView(this));
    };

    // Inheritance from super class
    GraphController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    GraphController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        // Load the mxEditor after elements rendering.
        createEditor('/assets/editor/diagrameditor.xml');

        if(global_editor!=null){
            this.view.$elements.source.click();
            this.view.$elements.source.click();
        }
    };

    GraphController.prototype.createTask = function() {
        var context = this;
        var $tab =
            $('<li class="graphTab">').append(
                $('<a href="javascript:;">').append(
                    $('<button class="close closeTab" type="button" >').text("x"),
                    $('<input style="height: 68%;">').keypress(function( event ) {
                        if ( event.which == 13 ) {
                            event.preventDefault();
                            Task.new_task(this.value, function (task) {
                                Task.load_task(task.id, function(graph) {
                                    context.view.$elements.source.click();
                                    context.view.$elements.xml.val(graph);
                                    context.view.$elements.source.click();
                                });
                            });
                            var taskName = this.value;
                            this.replaceWith(taskName);

                            var $alert =
                                $('<div id="newTask" class="col-sm-3" style="position:absolute; z-index:2; right:0;">').append(
                                    $('<div class="alert alert-success alert-dismissible">').append(
                                        $('<button type="button" class="close" data-dismiss="alert" aria-hidden="true">').append("x"),
                                        $('<h4>').append(
                                            $('<i class="icon fa fa-check">').append("New task")
                                        ),
                                        "Task '"+taskName+"' was created successfully!"
                                    )
                                )
                            setTimeout(function() {
                                $('#newTask').fadeOut('fast');
                            }, 5000);
                            context.view.$container.append($alert);
                        }
                    })
                )
            );
        this.view.$elements.graph_tabs.append($tab);
        this.registerCloseEvent();
        this.registerTabClick();
    }

    /**
     * Listener for task tab close button.
     */
    GraphController.prototype.registerCloseEvent = function() {
        $(".closeTab").click(function () {
            //close the li closest to the close button.
            var tabContentId = $(this).parent().attr("href");
            $(this).parent().parent().remove(); //remove li of tab
            $('#myTab a:last').tab('show'); // Select first tab
            $(tabContentId).remove(); //remove respective tab content

        });
    }

    /**
     * Listener for task tab selection.
     */
    GraphController.prototype.registerTabClick = function() {
        var context = this;
        $(".graphTab").click(function () {
            var taskName = $(this).text().slice(1);
            if(taskName) {
                Task.get_task($(this).text().slice(1), function (task) {
                    Task.load_task(task.id, function (graph) {
                        if(context.view==undefined) debugger;
                        context.view.$elements.source.click();
                        context.view.$elements.xml.val(graph);
                        context.view.$elements.source.click();
                    });
                });
            }

            $(".graphTab").removeClass("active");
            $(this).addClass("active");
        });
    }

    return GraphController;
});
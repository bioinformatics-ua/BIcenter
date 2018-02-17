define('GraphView', ['View', 'Task'], function (View, Task) {
    var GraphView = function (controller) {
        View.call(this, controller, 'graph');
    };

    // Inheritance from super class
    GraphView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    GraphView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        this._loadViewComponents();

        // Initialize tab list if it doesn't exists yet.
        var controller = app.modules.MainModule.controllers.GraphController;
        this.tabs = (controller != undefined) ? controller.view.tabs : [];

        // Load and convert the default transformation.
        var context = this;
        Task.getOpenTabs(function (tabs) {
            context.tabs = JSON.parse(tabs);
            for(var i=0; i<context.tabs.length; i++){
                var $tab =
                    $('<li class="graphTab">').append(
                        $('<a href="javascript:;">').append(
                            $('<button class="close closeTab" type="button" >').text("x"),
                            context.tabs[i]
                        )
                    );
                context.$elements.graph_tabs.append($tab);

                if(i == context.tabs.length-1){
                    Task.getTask(context.tabs[i], function(task) {
                        context.taskId = task.id;
                        Task.loadTask(task.id, function (graph) {
                            context.$elements.source.click();
                            context.$elements.xml.val(graph);
                            context.$elements.source.click();
                        });
                    });
                }
            }
            context._loadViewComponents();
            context.registerCloseEvent();
            context.registerTabClick();
        })
    };

    /**
     * Returns back to the pipeline, after XML import.
     */
    GraphView.prototype.loadXml = function(){
        this.$elements.source.click();
        this.$elements.board.css({'height': '70vh'});
        this.$elements.xmlBtn.css("display","none");
    }

    /**
     * Listener for task tab close button.
     */
    GraphView.prototype.registerCloseEvent = function() {
        var context = this;
        $(".closeTab").click(function () {
            //close the li closest to the close button.
            var tabContentId = $(this).parent().attr("href");
            $(this).parent().parent().remove(); //remove li of tab
            $('#myTab a:last').tab('show'); // Select first tab
            Task.closeTab(context.taskId,function () {})
        });
    }

    /**
     * Listener for task tab selection.
     */
    GraphView.prototype.registerTabClick = function() {
        var context = this;
        this.taskId;
        $(".graphTab").click(function () {
            var taskName = $(this).text().slice(1);
            if(taskName) {
                Task.getTask($(this).text().slice(1), function (task) {
                    context.taskId = task.id;
                    Task.loadTask(task.id, function (graph) {
                        context.$elements.source.click();
                        context.$elements.xml.val(graph);
                        context.$elements.source.click();
                    });
                });
            }

            $(".graphTab").removeClass("active");
            $(this).addClass("active");
        });
    }

    return GraphView;
});
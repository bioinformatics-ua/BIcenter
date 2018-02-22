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

        // Load open tabs.
        var context = this;
        Task.getOpenTabs(function (tabs) {
            context.tabs = JSON.parse(tabs);
            for(var i=0; i<context.tabs.length; i++){
                var $tab =
                    $('<li class="graphTab" view-click="controller.clickTab()">').append(
                        $('<a href="javascript:;">').append(
                            $('<button class="close closeTab" type="button" view-click="controller.closeTab()">').text("x"),
                            context.tabs[i]
                        )
                    );
                if(i == context.tabs.length-1) $tab.addClass("active");
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
        });
    };

    /**
     * Returns back to the pipeline, after XML import.
     */
    GraphView.prototype.loadXml = function(){
        this.$elements.source.click();
        this.$elements.board.css({'height': '70vh'});
        this.$elements.xmlBtn.css("display","none");
    }

    return GraphView;
});
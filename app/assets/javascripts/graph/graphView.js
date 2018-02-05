define('GraphView', ['View'], function (View) {
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
        if(global_editor==null)
        {
            $.get("/graph/load",
                function (transXml) {
                    context.$elements.source.click();
                    context.$elements.xml.val(transXml);
                    context.$elements.source.click();

                    var node = (new DOMParser()).parseFromString(transXml, "text/xml").documentElement;
                    var nodes = node.querySelectorAll("*");
                    var name = null;
                    for (var i = 0; i < nodes.length; i++) {
                        // Append the transaction node to the nodes list.
                        if (nodes[i].tagName == "Step" && nodes[i].hasAttribute('name')) {
                            name = nodes[i].getAttribute('name');
                            break;
                        }
                    }

                    var $tab =
                        $('<li class="active">').append(
                            $('<a href="javascript:;">').append(
                                $('<button class="close" type="button">').text("x"),
                                name
                            )
                        );
                    context.$elements.graph_tabs.append($tab);

                    // Add the loaded graph to the tab list.
                    context.tabs.push(name);
                    context._loadViewComponents();
                }
            );
        }
        // Draw the current open tabs.
        else{
            for(var i=0; i<this.tabs.length; i++){
                var $tab =
                    $('<li class="active">').append(
                        $('<a href="javascript:;">').append(
                            $('<button class="close" type="button">').text("x"),
                            this.tabs[i]
                        )
                    );
                this.$elements.graph_tabs.append($tab);
            }
        }
        this._loadViewComponents();
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
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

        // Load and convert the default transformation.
        var context = this;
        $.get("/graph/load",
            function(transXml){
                context.$elements.source.click();
                context.$elements.xml.val(transXml);
                context.$elements.source.click();
            }
        );
    };


    /**
     * Returns back to the pipeline, after XML import.
     */
    GraphView.prototype.loadXml = function(){
        this.$elements.source.click();
        this.$elements.board.css({'height': '80vh'});
        this.$elements.xmlBtn.css("display","none");
    }

    return GraphView;
});
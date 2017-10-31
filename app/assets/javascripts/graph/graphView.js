define('GraphView', ['View'], function (View) {
    var GraphView = function (controller) {
        View.call(this, controller, 'graph');
    };

    // Inheritance from super class
    GraphView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    GraphView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        // Load and convert the default transformation.
        $.get("/graph/load",
            function(transXml){
                $("#source").click();
                $("#xml").val(transXml);
                $("#source").click();
            }
        );
    };

    return GraphView;
});
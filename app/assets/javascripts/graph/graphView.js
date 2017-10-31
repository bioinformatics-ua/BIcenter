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
        var xhttp = new XMLHttpRequest();
        var transXml;
        var dom = this.$elements;
        xhttp.onreadystatechange = function () {
            if (this.readyState == 4 && this.status == 200) {
                transXml = this.responseText;
                document.getElementById("source").click();
                document.getElementById("xml").value = transXml;
                document.getElementById("source").click();
            }
        };
        xhttp.open("GET", "/graph/load", true);
        xhttp.send();
    };

    return GraphView;
});
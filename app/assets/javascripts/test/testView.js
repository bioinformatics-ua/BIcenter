define('TestView', ['View'], function (View) {
    var TestView = function (controller) {
        View.call(this, controller, 'test');
    };

    // Inheritance from super class
    TestView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    TestView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

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

    TestView.prototype.testClick = function () {
        console.log("Testing clicks from view");

        //var template = JST[]();
        //this.$elements.container.html(template);
        //this._loadViewComponents();
    };

    return TestView;
});
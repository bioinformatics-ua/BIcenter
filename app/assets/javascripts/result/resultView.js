define('ResultView', ['View'], function (View) {
    var ResultView = function (controller) {
        View.call(this, controller, 'result');
    };

    // Inheritance from super class
    ResultView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    ResultView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    ResultView.prototype.testClick = function () {
        console.log("Testing clicks from view");

        //var template = JST[]();
        //this.$elements.container.html(template);
        //this._loadViewComponents();
    };

    return ResultView;
});
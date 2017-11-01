define('RowGeneratorView', ['View'], function (View) {
    var RowGeneratorView = function (controller) {
        View.call(this, controller, 'steps/rowGenerator');
    };

    // Inheritance from super class
    RowGeneratorView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    RowGeneratorView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        $('#rowgen_cancel')[0].onclick =
            function () {
                $('#RowGenerator').hide();
                $('#main_page').show();
            }
    };

    RowGeneratorView.prototype.testClick = function () {
        console.log("Testing clicks from view");

        //var template = JST[]();
        //this.$elements.container.html(template);
        //this._loadViewComponents();
    };

    return RowGeneratorView;
});
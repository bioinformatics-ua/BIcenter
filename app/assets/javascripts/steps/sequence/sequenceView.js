define('SequenceView', ['View'], function (View) {
    var SequenceView = function (controller) {
        View.call(this, controller, 'steps/sequence');
    };

    // Inheritance from super class
    SequenceView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    SequenceView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        /*
        $('#rowgen_cancel')[0].onclick =
            function () {
                $('#RowGenerator').hide();
                $('#main_page').show();
            }
         */
    };

    SequenceView.prototype.testClick = function () {
        console.log("Testing clicks from view");

        //var template = JST[]();
        //this.$elements.container.html(template);
        //this._loadViewComponents();
    };

    return SequenceView;
});
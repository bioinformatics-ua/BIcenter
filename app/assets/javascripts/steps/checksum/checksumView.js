define('ChecksumView', ['View'], function (View) {
    var ChecksumView = function (controller) {
        View.call(this, controller, 'steps/checksum');
    };

    // Inheritance from super class
    ChecksumView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    ChecksumView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        $('#checksum_cancel')[0].onclick =
            function () {
                $('#Checksum').hide();
                $('#main_page').show();
            }
    };

    ChecksumView.prototype.testClick = function () {
        console.log("Testing clicks from view");

        //var template = JST[]();
        //this.$elements.container.html(template);
        //this._loadViewComponents();
    };

    return ChecksumView;
});
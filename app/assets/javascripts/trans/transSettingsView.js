define('TransSettingsView', ['View'], function (View) {
    var TransSettingsView = function (controller) {
        View.call(this, controller, 'trans/transSettings');
    };

    // Inheritance from super class
    TransSettingsView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    TransSettingsView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        $('#trans_settings_cancel')[0].onclick =
            function () {
                $('#TransSettings').hide();
                $('#main_page').show();
            }
    };

    TransSettingsView.prototype.testClick = function () {
        console.log("Testing clicks from view");

        //var template = JST[]();
        //this.$elements.container.html(template);
        //this._loadViewComponents();
    };

    return TransSettingsView;
});
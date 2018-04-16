define('LoginView', ['jquery', 'View'], function ($, View) {
    var LoginView = function (controller) {
        View.call(this, controller, 'login');
    };

    // Inheritance from super class
    LoginView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    LoginView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        var self = this;

    };

    LoginView.prototype.submitForm = function (e, $element) {
        e.stopImmediatePropagation();
        e.stopPropagation();
        e.preventDefault();

        var data = $element.serializeForm();

        this.$elements.sign.attr('disabled', true);
        $element.find('input').each(function (i, input) {
            $(input).attr('disabled', true);
        });
        this.controller.login(data);
    };

    return LoginView;
});
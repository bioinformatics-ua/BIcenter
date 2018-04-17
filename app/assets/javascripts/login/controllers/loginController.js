define('LoginController', ['Controller', 'LoginView', 'User', 'jquery', 'jsRoutes', 'jquery-cookie'], function (Controller, LoginView, User, $, jsRoutes) {
    var LoginController = function (module) {
        Controller.call(this, module, new LoginView(this));
    };

    // Inheritance from super class
    LoginController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    LoginController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    LoginController.prototype.login = function (data) {
        var context = this;
        User.authenticate(data, function (r) {
            var previousURL = $.cookie('previousURL');
            if (previousURL) {
                $.removeCookie('previousURL');
                document.location.href = previousURL;
            } else {
                document.location.href = jsRoutes.controllers.HomeController.index().url;
            }
        }, function () {
            context.view.$elements.sign.attr('disabled', false);
            context.view.$elements.form.find('input').each(function (i, input) {
                $(input).attr('disabled', false);
            });
        });
    };

    return LoginController;
});
define('LoginController', ['Controller', 'LoginView', 'User', 'jquery', 'jquery-cookie'], function (Controller, LoginView, User, $) {
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
        User.authenticate(data, function (r) {
            var previousURL = $.cookie('previousURL');
            if (previousURL) {
                $.removeCookie('previousURL');
                document.location.href = previousURL;
            } else {
                document.location.href = '/';
            }
        }, function () {
            context.view.$elements.sign.removeClass('disabled');
        });
    };

    return LoginController;
});
define('User', ['jsRoutes', 'Alert', 'messages'], function (jsRoutes, Alert, Messages) {
    var User = User || {};

    User.authenticate = function (data, callback, ecallback) {
        jsRoutes.controllers.login.Login.login().ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data),
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                DEBUG && console.log(response);
                if (response.status >= 500) {
                    Alert.flash(ALERT_TYPE.DANGER, Messages('common.entities.user'), Messages('common.messages.problem'), true);
                } else {
                    Alert.flash(ALERT_TYPE.WARNING, Messages('common.entities.user'), response.responseText, false);
                }

                if(ecallback){
                    callback();
                }
            }
        });
    };

    User.getLoggedIn = function (callback) {
        jsRoutes.controllers.rbac.Users.getLoggedInUser().ajax({
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                DEBUG && console.log(response);
                if (response.status >= 500) {
                    Alert.flash(ALERT_TYPE.DANGER, Messages('common.entities.user'), Messages('common.messages.problem'), true);
                } else {
                    Alert.flash(ALERT_TYPE.WARNING, Messages('common.entities.user'), response.responseText, false);
                }
            }
        });
    };

    User.getAllUsers = function(callback) {
        jsRoutes.controllers.rbac.Users.getAllUsers().ajax({
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                DEBUG && console.log(response);
                if (response.status >= 500) {
                    Alert.flash(ALERT_TYPE.DANGER, Messages('common.entities.user'), Messages('common.messages.problem'), true);
                } else {
                    Alert.flash(ALERT_TYPE.WARNING, Messages('common.entities.user'), response.responseText, false);
                }
            }
        });
    };

    return User;
});
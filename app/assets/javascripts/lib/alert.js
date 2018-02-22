define('Alert', ['jquery', 'messages', 'pnotify', 'pnotify.nonblock'], function ($, Messages, PNotify) {
    var Alert = Alert || {};
    window.ALERT_TYPE = Object.freeze({INFO: 0, SUCCESS: 1, WARNING: 2, DANGER: 3});

    Alert.flash = function (type, title, message, withContact) {
        var t = null;
        switch (type) {
            case ALERT_TYPE.INFO:
                t = 'info';
                break;
            case ALERT_TYPE.SUCCESS:
                t = 'success';
                break;
            case ALERT_TYPE.WARNING:
                t = 'warning';
                break;
            case ALERT_TYPE.DANGER:
                t = 'error';
                break;
            default:
                t = 'info';
                break;
        }

        if (withContact === true) {
            message += '\nPlease contact our maintenance team at <a href="mailto:info@bmd-software.com" target="_blank" class="alert-link">info@bmd-software.com</a>.';
        }

        new PNotify({
            type: t,
            title: title,
            title_escape: true,
            text: message,
            text_escape: false,
            styling: "fontawesome",
            opacity: 0.93,
            buttons: {
                show_on_nonblock: true
            }
        });
    };

    Alert.sticky = function (type, title, message, withContact) {
        var t = null;
        switch (type) {
            case ALERT_TYPE.INFO:
                t = 'info';
                break;
            case ALERT_TYPE.SUCCESS:
                t = 'success';
                break;
            case ALERT_TYPE.WARNING:
                t = 'warning';
                break;
            case ALERT_TYPE.DANGER:
                t = 'error';
                break;
            default:
                t = 'info';
                break;
        }

        if (withContact === true) {
            message += '\nPlease contact our maintenance team at <a href="mailto:info@bmd-software.com" target="_blank" class="alert-link">info@bmd-software.com</a>.';
        }

        new PNotify({
            type: t,
            title: title,
            title_escape: true,
            text: message,
            text_escape: false,
            styling: "fontawesome",
            opacity: 0.93,
            buttons: {
                show_on_nonblock: true
            },
            hide: false
        });
    };

    Alert.removeAll = function () {
        PNotify.removeAll();
    };

    return Alert;
});
define('RemoteExecutionView', ['Modal', 'boostrap-datetimepicker', 'jquery-ui', 'iCheck'], function (Modal) {

    var RemoteExecutionView = function (controller) {
        Modal.call(this, controller, 'run/remoteExecutionModal', 'Remote Execution');
    };

    // Inheritance from super class
    RemoteExecutionView.prototype = Object.create(Modal.prototype);
    var _super_ = Modal.prototype;

    RemoteExecutionView.prototype.clear = function () {

    };

    RemoteExecutionView.prototype.show = function (servers) {
        // Clear modal before show
        this.clear();

        // Show modal
        _super_.show.call(this);

        var context = this;

        var html = JST['run/remoteExecutionModalContent']({
            servers: servers
        });
        this.$elements.insideContainer.html(html);
        this._loadViewComponents();

        this.$elements.startDateTime.datetimepicker({
            inline: true,
            sideBySide: true
        });

        this.$elements.schedule.iCheck({
            checkboxClass: 'icheckbox_flat-blue',
            radioClass: 'iradio_flat-blue'
        }).on('ifChanged', function () {
            if ($(this).is(':checked')) {
                context.showCalendar();
            } else {
                context.hideCalendar();
            }
        });

        this.$elements.periodic.iCheck({
            checkboxClass: 'icheckbox_flat-blue',
            radioClass: 'iradio_flat-blue'
        }).on('ifChanged', function () {
            if ($(this).is(':checked')) {
                context.showIntervals();
            } else {
                context.hideIntervals();
            }
        });
    };

    RemoteExecutionView.prototype.showCalendar = function () {
        this.$elements.startDateTime_form.show();
        this.$elements.periodic_form.show();
    };

    RemoteExecutionView.prototype.hideCalendar = function () {
        this.$elements.startDateTime_form.hide();
        this.$elements.periodic_form.hide();
        this.$elements.periodic.iCheck('uncheck');
        this.$elements.intervals_form.hide();
    };

    RemoteExecutionView.prototype.showIntervals = function () {
        this.$elements.intervals_form.show();
    };

    RemoteExecutionView.prototype.hideIntervals = function () {
        this.$elements.intervals_form.hide();
    };

    return RemoteExecutionView;
});
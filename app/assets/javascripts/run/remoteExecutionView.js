define('RemoteExecutionView', ['Modal'], function (Modal) {

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

        var html = JST['run/remoteExecutionModalContent']({
            servers: servers
        });
        this.$elements.insideContainer.html(html);
        this._loadViewComponents();
    };

    return RemoteExecutionView;
});
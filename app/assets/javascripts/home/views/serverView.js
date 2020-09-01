define('ServerView', ['Modal'], function (Modal) {

    var ServerView = function (controller) {
        Modal.call(this, controller, 'serverModal', 'Edit Remote Server');
    };

    // Inheritance from super class
    ServerView.prototype = Object.create(Modal.prototype);
    var _super_ = Modal.prototype;

    ServerView.prototype.clear = function () {

    };

    ServerView.prototype.show = function (server,institutions) {
        // Clear modal before show
        this.clear();

        // Show modal
        _super_.show.call(this);

        var html = JST['serverModalContent']({server: server, institutions: institutions});
        this.$elements.insideContainer.html(html);
        this._loadViewComponents();
    };

    return ServerView;
});
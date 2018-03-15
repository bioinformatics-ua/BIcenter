define('ServerView', ['Modal'], function (Modal) {

    var ServerView = function (controller) {
        Modal.call(this, controller, 'serverModal', 'Remote Server');
    };

    // Inheritance from super class
    ServerView.prototype = Object.create(Modal.prototype);
    var _super_ = Modal.prototype;

    ServerView.prototype.clear = function () {

    };

    ServerView.prototype.show = function () {
        // Clear modal before show
        this.clear();

        // Show modal
        _super_.show.call(this);

        var html = JST['serverModalContent']();
        this.$elements.insideContainer.html(html);
        this._loadViewComponents();
    };

    return ServerView;
});
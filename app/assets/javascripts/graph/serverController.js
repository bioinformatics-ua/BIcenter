define('ServerController', ['Controller', 'ServerView', 'Router', 'Execution'], function (Controller, ServerView, Router, Execution) {
    var ServerController = function (module) {
        Controller.call(this, module, new ServerView(this));
    };

    // Inheritance from super class
    ServerController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    ServerController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    /**
     * Returns to the pipeline view.
     */
    ServerController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    return ServerController;
});
define('SidebarController', ['Controller', 'SidebarView', 'Router', 'Institution', 'Task'], function (Controller, SidebarView,Router,Institution,Task) {
    var SidebarController = function (module) {
        Controller.call(this, module, new SidebarView(this));
    };

    // Inheritance from super class
    SidebarController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    SidebarController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    SidebarController.prototype.getTasks = function () {
        var context = this;
        Institution.getInstitutions(function(institutions){
            context.view.loadInstitutions(institutions);
        });
    };

    SidebarController.prototype.getComponents = function () {
        this.view.loadComponents();
    };

    SidebarController.prototype.selectTask = function (taskId) {
        var configStepUrl = jsRoutes.controllers.TransGraphController.selectTask(taskId).url;
        Router.navigate(configStepUrl);
    };

    return SidebarController;
});
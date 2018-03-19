define('SidebarController', ['Controller', 'SidebarView', 'Router', 'Institution', 'Task','Alert'], function (Controller, SidebarView,Router,Institution,Task,Alert) {
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

    SidebarController.prototype.createTask = function(institution, event){
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        var $form = this.view.$elements[institution+'_newTask'];
        var formValues = $form.serializeForm();
        var taskName = formValues['taskName'];

        var context = this;
        context.institution = institution;
        Task.getTask(taskName, function (task) {
            if (task == "not found") {
                Task.newTask(context.institution,taskName, function (task) {
                    // Open new task.
                    var configStepUrl = jsRoutes.controllers.TransGraphController.selectTask(task.id).url;
                    Router.navigate(configStepUrl);

                    Alert.flash(ALERT_TYPE.SUCCESS, 'Task', 'Task \'' + taskName + '\' was created successfully!');
                });
            }
            else {
                // Open already existent task.
                var configStepUrl = jsRoutes.controllers.TransGraphController.selectTask(task.id).url;
                Router.navigate(configStepUrl);

                Alert.flash(ALERT_TYPE.DANGER, 'Task', 'Task \'' + taskName + '\' already exists!');
            }
        });
    };

    SidebarController.prototype.createServer = function(institution, event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        var $form = this.view.$elements[institution+'_newServer'];
        var formValues = $form.serializeForm();
        var serverName = formValues['serverName'];

        var context = this;
        context.institution = institution;
        Institution.newServer(context.institution,serverName, function (server) {
            debugger;
        });
    };

    SidebarController.prototype.editServer = function(server){
        var modalController = this.module.controllers['ServerController'];
        modalController.loadServer(server);
    }

    SidebarController.prototype.createDataSource = function(institution, event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        var $form = this.view.$elements[institution+'_newDataSource'];
        var formValues = $form.serializeForm();
        var dataSourceName = formValues['dataSourceName'];

        var context = this;
        context.institution = institution;
        Institution.newDataSource(context.institution,dataSourceName, function (dataSource) {
            debugger;
        });
    };

    SidebarController.prototype.editDataSource = function(dataSource) {
        var modalController = this.module.controllers['DataSourceController'];
        modalController.loadDataSource(dataSource);
    };

    return SidebarController;
});
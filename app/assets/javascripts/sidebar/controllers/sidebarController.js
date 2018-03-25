define('SidebarController', ['Controller', 'SidebarView', 'Router', 'Institution', 'Task','Alert'], function (Controller, SidebarView,Router,Institution,Task,Alert) {
    var SidebarController = function (module) {
        Controller.call(this, module, new SidebarView(this));
    };

    // Inheritance from super class
    SidebarController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    SidebarController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        if(this.tab && this.tab=="resources"){
            this.getTasks();
        }
        else{
            this.getComponents();
        }
    };

    SidebarController.prototype.getTasks = function () {
        var context = this;
        Institution.getInstitutions(function(institutions){
            context.view.loadInstitutions(institutions);
        });
    };

    SidebarController.prototype.getComponents = function () {
        var context = this;
        Institution.getComponents(function(components){
            context.view.loadComponents(components);
        });
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

                    Alert.flash(ALERT_TYPE.SUCCESS, 'Task', 'Task \'' + taskName + '\' was successfully created!');
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
        this.serverName = formValues['serverName'];

        var context = this;
        context.institution = institution;
        Institution.newServer(context.institution,this.serverName, function (server) {
            if(server == "not found"){
                Alert.flash(ALERT_TYPE.DANGER, 'Server', 'Server \'' + context.serverName + '\' already exists!');
            }
            else{
                Alert.flash(ALERT_TYPE.SUCCESS, 'Server', 'Server \'' + context.serverName + '\' was successfully created!');
            }
            context.getTasks();
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
        this.dataSourceName = formValues['dataSourceName'];

        var context = this;
        context.institution = institution;
        Institution.newDataSource(context.institution,this.dataSourceName, function (dataSource) {
            if(dataSource == "not found"){
                Alert.flash(ALERT_TYPE.DANGER, 'Data Source', 'Data Source \'' + context.dataSourceName + '\' already exists!');
            }
            else{
                Alert.flash(ALERT_TYPE.SUCCESS, 'Data Source', 'Data Source \'' + context.dataSourceName + '\' was successfully created!');
            }
            context.getTasks();
        });
    };

    SidebarController.prototype.editDataSource = function(dataSource) {
        var modalController = this.module.controllers['DataSourceController'];
        modalController.loadDataSource(dataSource);
    };

    SidebarController.prototype.showSchedule = function(institution) {
        var configStepUrl = jsRoutes.controllers.InstitutionController.scheduler(institution).url;
        Router.navigate(configStepUrl);
    };

    return SidebarController;
});
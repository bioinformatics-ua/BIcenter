define('HomeController', ['Controller', 'HomeView', 'Router', 'Institution', 'Task', 'Alert', 'jquery', 'jsRoutes', 'jquery-cookie'], function (Controller, HomeView, Router, Institution, Task, Alert, $, jsRoutes) {
	const HomeController = function (module) {
		Controller.call(this, module, new HomeView(this));
	};

	// Inheritance from super class
	HomeController.prototype = Object.create(Controller.prototype);
	const _super_ = Controller.prototype;

	HomeController.prototype.initialize = function ($container) {
		_super_.initialize.call(this, $container);

		const context = this;
		context.getTasks();
	};

	HomeController.prototype.getTasks = function () {
		const context = this;
		Institution.getInstitutions(function (institutions) {
			context.view.loadInstitutions(institutions);
		});
	};

	// SCHEDULER //
	HomeController.prototype.showSchedule = function (institution) {
		const configStepUrl = jsRoutes.controllers.InstitutionController.scheduler(institution).url;
		Router.navigate(configStepUrl);
	};

	// TASKS //
	// TODO: check the faulty render of the task page
	HomeController.prototype.selectTask = function (institutionId, taskId) {
		const configStepUrl = jsRoutes.controllers.TransGraphController.selectTask(institutionId, taskId).url;
		Router.navigate(configStepUrl);
	};

	HomeController.prototype.createTask = function (institution, event) {
		if (event) {
			event.preventDefault && event.preventDefault();
			event.stopPropagation && event.stopPropagation();
			event.stopImmediatePropagation && event.stopImmediatePropagation();
		}

		const $form = this.view.$elements[institution + '_newTask'];
		const formValues = $form.serializeForm();
		const taskName = formValues['taskName'];

		const context = this;
		context.institution = institution;

		Task.getTask(institution, taskName, task => {
			if (task === "not found") {
				Task.newTask(context.institution, taskName, () => {
					// Open new task.
					const configStepUrl = jsRoutes.controllers.TransGraphController.selectTask(context.institution, task.id).url;
					Router.navigate(configStepUrl);

					Alert.flash(ALERT_TYPE.SUCCESS, 'Task', 'Task \'' + taskName + '\' was successfully created!');
				});
			} else {
				// Open already existent task.
				const configStepUrl = jsRoutes.controllers.TransGraphController.selectTask(context.institution, task.id).url;
				Router.navigate(configStepUrl);

				Alert.flash(ALERT_TYPE.DANGER, 'Task', 'Task \'' + taskName + '\' already exists!');
			}
		});
	};

	HomeController.prototype.editTask = function (institution, task, event) {
		if (event) {
			event.preventDefault && event.preventDefault();
			event.stopPropagation && event.stopPropagation();
			event.stopImmediatePropagation && event.stopImmediatePropagation();
		}

		const modalController = this.module.controllers['TaskController'];
		modalController.loadTask(institution, task);
	}

	// SERVERS //
	HomeController.prototype.createServer = function (institution, event) {
		if (event) {
			event.preventDefault && event.preventDefault();
			event.stopPropagation && event.stopPropagation();
			event.stopImmediatePropagation && event.stopImmediatePropagation();
		}

		const $form = this.view.$elements[institution + '_newServer'];
		const formValues = $form.serializeForm();
		this.serverName = formValues['serverName'];

		const context = this;
		context.institution = institution;

		Institution.newServer(context.institution, this.serverName, server => {
			if (server === "not found") {
				Alert.flash(ALERT_TYPE.DANGER, 'Server', 'Server \'' + context.serverName + '\' already exists!');
			} else {
				Alert.flash(ALERT_TYPE.SUCCESS, 'Server', 'Server \'' + context.serverName + '\' was successfully created!');
			}
			context.getTasks();
		});
	};

	HomeController.prototype.editServer = function (institution, server) {
		const modalController = this.module.controllers['ServerController'];
		modalController.loadServer(institution, server);
	}

	// DATA SOURCES //
	HomeController.prototype.createDataSource = function (institution, event) {
		if (event) {
			event.preventDefault && event.preventDefault();
			event.stopPropagation && event.stopPropagation();
			event.stopImmediatePropagation && event.stopImmediatePropagation();
		}

		const $form = this.view.$elements[institution + '_newDataSource'];
		const formValues = $form.serializeForm();
		this.dataSourceName = formValues['dataSourceName'];

		const context = this;
		context.institution = institution;
		Institution.newDataSource(context.institution, this.dataSourceName, dataSource => {
			if (dataSource === "not found") {
				Alert.flash(ALERT_TYPE.DANGER, 'Data Source', 'Data Source \'' + context.dataSourceName + '\' already exists!');
			} else {
				Alert.flash(ALERT_TYPE.SUCCESS, 'Data Source', 'Data Source \'' + context.dataSourceName + '\' was successfully created!');
			}
			context.getTasks();
		});
	};

	HomeController.prototype.editDataSource = function (institutionId, dataSource) {
		const modalController = this.module.controllers['DataSourceController'];
		modalController.loadDataSource(institutionId, dataSource);
	};

	return HomeController;
});

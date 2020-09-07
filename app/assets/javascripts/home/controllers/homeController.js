define('HomeController', ['Controller', 'HomeView', 'Router', 'Institution', 'Task', 'Alert', 'jquery', 'jsRoutes', 'jquery-cookie'], function (Controller, HomeView, Router, Institution, Task, Alert, $, jsRoutes) {
	const HomeController = function (module) {
		Controller.call(this, module, new HomeView(this));
	};

	// Inheritance from super class
	HomeController.prototype = Object.create(Controller.prototype);
	const _super_ = Controller.prototype;

	HomeController.prototype.initialize = function ($container) {
		_super_.initialize.call(this, $container);

		this.getTasks();
	};

	HomeController.prototype.getTasks = function () {
		const context = this;
		Institution.getInstitutions(function (institutions) {
			context.view.loadInstitutions(institutions);
		});
	};

	// INSTITUTIONS //
	HomeController.prototype.addInstitutionInfo = function (event) {
		if (event) {
			event.preventDefault && event.preventDefault();
			event.stopPropagation && event.stopPropagation();
			event.stopImmediatePropagation && event.stopImmediatePropagation();
		}

		const $form = this.view.$elements['_newInstitution'];
		const formValues = $form.serializeForm();
		this.institutionName = formValues['institutionName'];

		const modalController = this.module.controllers["InstitutionController"];
		modalController.loadNewInstitutionForm(this.institutionName);
	}

	// TODO: check with @joaorafaelalmeida the underlying logic of this operation
	HomeController.prototype.createInstitution = function (event) {
		if (event) {
			event.preventDefault && event.preventDefault();
			event.stopPropagation && event.stopPropagation();
			event.stopImmediatePropagation && event.stopImmediatePropagation();
		}

		const $form = this.view.$elements['_newInstitution'];
		const formValues = $form.serializeForm();
		this.institutionName = formValues['institutionName'];

		const context = this;

		Institution.newInstitution(this.institutionName, institution => {
			if (institution === "already exists") {
				Alert.flash(ALERT_TYPE.DANGER, 'Institution', 'Institution \'' + context.institutionName + '\' already exists!');
			} else {
				Alert.flash(ALERT_TYPE.SUCCESS, 'Institution', 'Institution \'' + context.institutionName + '\' was successfully created!');
			}
			context.getTasks();
		});
	};

	// SCHEDULER //
	HomeController.prototype.showSchedule = function (institution) {
		const configStepUrl = jsRoutes.controllers.InstitutionController.scheduler(institution).url;
		Router.navigate(configStepUrl);
	};

	// TASKS //
	HomeController.prototype.selectTask = function (institutionId, taskId) {
		document.location.href = jsRoutes.controllers.TransGraphController.selectTask(institutionId, taskId).url;
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
				Task.newTask(context.institution, taskName, (newTask) => {
					// Open new task.
					document.location.href = jsRoutes.controllers.TransGraphController.selectTask(context.institution, newTask.id).url;

					Alert.flash(ALERT_TYPE.SUCCESS, 'Task', 'Task \'' + taskName + '\' was successfully created!');
				});
			} else {
				// Open already existent task.
				document.location.href = jsRoutes.controllers.TransGraphController.selectTask(context.institution, task.id).url;

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

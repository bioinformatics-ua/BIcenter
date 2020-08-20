define('ServerController', ['Controller', 'ServerView', 'async', 'Router', 'Institution'], function (Controller, ServerView, async, Router, Institution) {
    var ServerController = function (module) {
        Controller.call(this, module, new ServerView(this));
    };

    // Inheritance from super class
    ServerController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    ServerController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        // Add button to save data
        this.view.addButtons({
            save: {
                label: '<i class="fa fa-check-circle"></i> Save',
                className: 'btn-success',
                callback: 'controller.updateServer()'
            },
            delete: {
                label: '<i class="fa fa-times-circle"></i> Delete',
                className: 'btn-danger pull-left',
                callback: 'controller.deleteServer()'
            }
        });
    };

    ServerController.prototype.loadServer = function(institutionId,serverId){
        this.institutionId = institutionId;

        var context = this;
        async.parallel(
            [
                function (callback) {
                    Institution.getServer(institutionId,serverId, function(server){
                        context.server = server;
                        callback();
                    });
                },
                function (callback) {
                    Institution.getInstitutions(function(institutions){
                        context.institutions = institutions;
                        callback();
                    });
                }
            ],
            function (err) {
				if (err) {
					console.error(err);
					return;
				}

				context.view.show(context.server, context.institutions);
			}
		);
	}

	DataSourceController.prototype.reloadInstitutionInfo = function () {
		let institutionDisplayController;

		const sidebarController = this.module.controllers['SidebarController'];
		if (sidebarController !== undefined) {
			institutionDisplayController = sidebarController;
		} else {
			institutionDisplayController = this.module.controllers['HomeController'];
		}

		institutionDisplayController.getTasks();
	};

	ServerController.prototype.updateServer = function (event) {
		if (event) {
			event.preventDefault && event.preventDefault();
			event.stopPropagation && event.stopPropagation();
			event.stopImmediatePropagation && event.stopImmediatePropagation();
		}

		const $form = this.view.$elements[this.server.id];
		const formValues = $form.serializeForm();

		const context = this;
		Institution.updateServer(this.institutionId, this.server.id, formValues, function (server) {
			console.log("Server", server.id, "has been updated!");
			context.reloadInstitutionInfo();
		});

		this.view.hide();
	};

    ServerController.prototype.deleteServer = function(event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

		const context = this;
        Institution.deleteServer(this.institutionId, this.server.id, function() {
			context.view.hide();
			context.reloadInstitutionInfo();
		});
    }

    /**
     * Returns to the pipeline view.
     */
    ServerController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    return ServerController;
});

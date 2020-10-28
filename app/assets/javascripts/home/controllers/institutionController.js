define('InstitutionController', ['Controller', 'InstitutionView', 'async', 'Alert', 'Router', 'Institution', 'User'], function (Controller, InstitutionView, async, Alert, Router, Institution, User) {
    var InstitutionController = function (module) {
        Controller.call(this, module, new InstitutionView(this));
    };

    // Inheritance from super class
    InstitutionController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    InstitutionController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        // Add button to save data
        this.view.addButtons({
            save: {
                label: '<i class="fa fa-check-circle"></i> Save',
                id: "createBtn",
                className: 'btn-success',
                callback: 'controller.createInstitution()'
            },
            update: {
                label: '<i class="fa fa-check-circle"></i> Update',
                id: "updateBtn",
                className: 'btn-success',
                callback: 'controller.updateInstitution()'
            },
            delete: {
                label: '<i class="fa fa-times-circle"></i> Delete',
                id: "deleteBtn",
                className: 'btn-danger pull-left',
                callback: 'controller.deleteInstitution()'
            }
        });
    };

    InstitutionController.prototype.reloadInstitutionInfo = function () {
        let institutionDisplayController = this.module.controllers["HomeController"];
        institutionDisplayController.getTasks();
    };

    InstitutionController.prototype.loadNewInstitutionForm = function (institutionName) {
        this.institutionName = institutionName;
        const context = this;

        User.getAllUsers(function (users) {
            context.view.showNewInstitution(users, context.institutionName, "Create New Institution");
        });
    }

    InstitutionController.prototype.createInstitution = function (event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        const formValues = {
            name: this.view.$elements["name"].val(),
            users: this.view.$elements["users"].val()
        };

        const context = this;

        Institution.newInstitution(formValues, function (institution) {
            if (institution === "already exists") {
                Alert.flash(ALERT_TYPE.DANGER, 'Institution', 'Institution \'' + context.institutionName + '\' already exists!');
            } else {
                Alert.flash(ALERT_TYPE.SUCCESS, 'Institution', 'Institution \'' + context.institutionName + '\' was successfully created!');
            }
            context.reloadInstitutionInfo();
        });

        this.view.hide();
    };

    InstitutionController.prototype.updateInstitution = function (event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        const formValues = {
            name: this.view.$elements["name"].val(),
            users: this.view.$elements["users"].val()
        };

        console.log(formValues);

        const context = this;

        Institution.updateInstitution(this.institutionId, formValues, function (institution) {
            if (institution === "already exists") {
                Alert.flash(ALERT_TYPE.DANGER, 'Institution', 'Institution \'' + formValues.name + '\' already exists!');
            } else {
                Alert.flash(ALERT_TYPE.SUCCESS, 'Institution', 'Institution \'' + formValues.name + '\' was successfully created!');
            }
            context.reloadInstitutionInfo();
        });

        this.view.hide();
    };

    InstitutionController.prototype.loadInstitution = function (institutionId) {
        this.institutionId = institutionId
        const context = this;

        async.parallel(
            [
                function (callback) {
                    User.getAllUsers( function(users){
                        context.users = users;
                        callback();
                    });
                },
                function (callback) {
                    Institution.getInstitution(context.institutionId, function(institution){
                        context.institution = institution;
                        callback();
                    });
                }
            ],
            function (err) {
                if (err) {
                    console.error(err);
                    return;
                }

                context.view.show(context.users, context.institution, "Edit Institution");
            }
        );
    }

    InstitutionController.prototype.deleteInstitution = function (event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        const context = this;
        Institution.deleteInstitution(this.institutionId, function () {
            context.view.hide();
            context.reloadInstitutionInfo();
        });
    }

    /**
     * Returns to the pipeline view.
     */
    InstitutionController.prototype.cancelClick = function () {
        Router.navigatePrevious();
    }

    return InstitutionController;
});

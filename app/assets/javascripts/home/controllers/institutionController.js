define('InstitutionController', ['Controller', 'InstitutionView', 'async', 'Router', 'Institution', 'User'], function (Controller, InstitutionView, async, Router, Institution, User) {
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
                className: 'btn-success',
                callback: 'controller.createInstitution()'
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
            context.view.show(users, context.institutionName);
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
            console.log(institution);
            console.log("Institution", institution.id, "was successfully created!");
            context.reloadInstitutionInfo();
        });

        this.view.hide();


    };

    InstitutionController.prototype.deleteInstitution = function (event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        const context = this;
        Institution.deleteServer(this.institutionId, this.server.id, function () {
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

define('InstitutionView', ['Modal'], function (Modal) {

    const InstitutionView = function (controller) {
        Modal.call(this, controller, "institutionModal", "Create New Institution");
    };

    // Inheritance from super class
    InstitutionView.prototype = Object.create(Modal.prototype);
    const _super_ = Modal.prototype;

    InstitutionView.prototype.clear = function () {
    };

    InstitutionView.prototype.showNewInstitution = function (users, institutionName, modalTitle=null) {
        // Clear modal before show
        this.clear();

        // update modalTitle
        if (modalTitle !== null) {
            _super_.addTitle.call(this, modalTitle);
        }

        _super_.hide.call(this, ".btn-danger");

        // Show modal
        _super_.show.call(this);

        let html = JST['newInstitutionModalContent']({name: institutionName, users: users});
        this.$elements.insideContainer.html(html);
        this._loadViewComponents();
    };

    InstitutionView.prototype.show = function (users, institution, modalTitle=null) {
        // Clear modal before show
        this.clear();

        // update modalTitle
        if (modalTitle !== null) {
            _super_.addTitle.call(this, modalTitle);
        }

        _super_.show.call(this, ".btn-danger");

        // Show modal
        _super_.show.call(this);

        institution.users = institution.users.map((user) => { return user.id });

        let html = JST['institutionModalContent']({institution: institution, users: users});
        this.$elements.insideContainer.html(html);
        this._loadViewComponents();
    };

    return InstitutionView;
});
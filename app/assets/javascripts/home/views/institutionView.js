define('InstitutionView', ['Modal'], function (Modal) {

    const InstitutionView = function (controller) {
        Modal.call(this, controller, "institutionModal", "Create New Institution");
    };

    // Inheritance from super class
    InstitutionView.prototype = Object.create(Modal.prototype);
    const _super_ = Modal.prototype;

    InstitutionView.prototype.clear = function () {
    };

    InstitutionView.prototype.show = function (users, institutionName, modalTitle=null) {
        // Clear modal before show
        this.clear();

        // update modalTitle
        if (modalTitle !== null) {
            _super_.addTitle.call(this, modalTitle);
        }

        // Show modal
        _super_.show.call(this);

        console.log(users);

        let html = JST['institutionModalContent']({name: institutionName, users: users});
        this.$elements.insideContainer.html(html);
        this._loadViewComponents();
    };

    return InstitutionView;
});
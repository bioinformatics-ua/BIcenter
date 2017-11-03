define('TransSettingsView', ['View'], function (View) {
    var TransSettingsView = function (controller) {
        View.call(this, controller, 'trans/transSettings');
    };

    // Inheritance from super class
    TransSettingsView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    TransSettingsView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        var controllers = this.controller.module.controllers;
        var headerController = controllers.HeaderController;

        this.$elements.trans_settings_name.val(headerController.global_step.getAttribute("name"));
        this.$elements.trans_settings_file.val(headerController.global_step.getAttribute("fileName"));
        this.$elements.trans_settings_description.val(headerController.global_step.getAttribute("description"));
        this.$elements.trans_settings_status.val(headerController.global_step.getAttribute("trans_status"));
        this.$elements.trans_settings_version.val(headerController.global_step.getAttribute("trans_version"));
        this.$elements.trans_settings_directory.val(headerController.global_step.getAttribute("trans_directory"));
        this.$elements.trans_settings_creator.val(headerController.global_step.getAttribute("created_user"));
        this.$elements.trans_settings_date.val(headerController.global_step.getAttribute("created_date"));
        this.$elements.trans_settings_modified_user.val(headerController.global_step.getAttribute("modified_user"));
        this.$elements.trans_settings_modified_date.val(headerController.global_step.getAttribute("modified_date"));
    };

    return TransSettingsView;
});
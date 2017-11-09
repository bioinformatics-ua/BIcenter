define('SequenceView', ['View'], function (View) {
    var SequenceView = function (controller) {
        View.call(this, controller, 'steps/sequence');
    };

    // Inheritance from super class
    SequenceView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    /**
     * Fill form with Sequence settings.
     * @param $container Form container.
     */
    SequenceView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        this._loadViewComponents();

        var controllers = this.controller.module.controllers;
        var headerController = controllers.HeaderController;

        this.$elements.sequence_step_name.val(headerController.global_step.getAttribute("label"));
        this.$elements.sequence_value_name.val(headerController.global_step.getAttribute("valuename"));

        if(headerController.global_step.getAttribute("use_database") == "Y"){
            this.$elements.sequence_database.prop( "checked", true );
        }
        else{
            this.$elements.sequence_database.prop( "checked", false );
        }

        if(headerController.global_step.getAttribute("use_counter") == "Y"){
            this.$elements.sequence_counter.prop( "checked", true );
        }
        else{
            this.$elements.sequence_counter.prop( "checked", false );
        }
        this.$elements.sequence_counter_name.val(headerController.global_step.getAttribute("counter_name"));
        this.$elements.sequence_start_value.val(headerController.global_step.getAttribute("start_at"));
        this.$elements.sequence_increment.val(headerController.global_step.getAttribute("increment_by"));
        this.$elements.sequence_max_value.val(headerController.global_step.getAttribute("max_value"));
    };

    return SequenceView;
});
define('StepView', ['View', 'templates'], function (View) {
    var StepView = function (controller) {
        View.call(this, controller);
    };

    // Inheritance from super class
    StepView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    /**
     * Fill form with Step settings.
     * @param $container Form container.
     */
    StepView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

    };

    StepView.prototype.loadStep = function (step) {
        var html = JST['step']({step: step});
        this.$container.html(html);
    };

    return StepView;
});
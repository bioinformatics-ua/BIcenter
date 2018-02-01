define('ContainerView', ['View'], function (View) {
    var ContainerView= function (controller) {
        View.call(this, controller, null);
    };

    // Inheritance from super class
    ContainerView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    ContainerView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };
    
    ContainerView.prototype.loadController =  function (controller) {
        this.$elements.container.hide();
        this.$elements.container.empty();
        this.$elements.container.attr('controller', controller);
        this._loadViewComponents();
        this.$elements.container.show();
    };

    return ContainerView;
});
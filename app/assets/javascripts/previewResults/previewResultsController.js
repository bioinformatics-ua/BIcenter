define('PreviewResultsController', ['Controller', 'PreviewResultsView'], function (Controller, PreviewResultsView) {
    var PreviewResultsController = function (module) {
        Controller.call(this, module, new PreviewResultsView(this));
    };

    // Inheritance from super class
    PreviewResultsController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    PreviewResultsController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    /**
     * Returns to the pipeline view.
     */
    PreviewResultsController.prototype.cancelClick = function(){
        if(this.view.$elements.preview_data.is(":visible")){
            this.view.$elements.preview_data.hide();
            this.view.$elements.preview_table.show();
        }
        else {
            var controller = 'GraphController';
            var containerController = this.module.controllers.ContainerController;
            if (!containerController) {
                console.err('Container controller not found!');
            }
            containerController.loadController(controller);
        }
    }

    return PreviewResultsController;
});
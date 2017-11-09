define('SequenceController', ['Controller', 'SequenceView'], function (Controller, SequenceView) {
    var SequenceController = function (module) {
        Controller.call(this, module, new SequenceView(this));
    };

    // Inheritance from super class
    SequenceController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    SequenceController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    /**
     * Returns to the pipeline view.
     */
    SequenceController.prototype.cancelClick = function(){
        var controller = 'GraphController';
        var containerController = this.module.controllers.ContainerController;
        if (!containerController) {
            console.err('Container controller not found!');
        }

        containerController.loadController(controller);
    }

    /**
     * Save sequence settings changes.
     */
    SequenceController.prototype.submitClick = function(){
        var cell_name = MainModule.controllers.HeaderController.global_step.getAttribute("ctype");
        var cell;
        for(var i=0; i<Object.keys(global_editor.graph.getModel().cells).length; i++)
            if(global_editor.graph.getModel().cells[i].getAttribute("ctype") == cell_name)
                cell = global_editor.graph.getModel().cells[i];

        var $form = this.view.$elements.sequence_form;
        var formValues = getFormData($form);

        global_editor.graph.getModel().beginUpdate();
        try
        {
            formValues.use_database = formValues.use_database ? "Y" : "N";
            formValues.use_counter = formValues.use_counter ? "Y" : "N";

            for(var fieldName in formValues) {
                var edit = new mxCellAttributeChange(cell, fieldName, formValues[fieldName]);
                global_editor.graph.getModel().execute(edit);
            }
        }
        finally
        {
            global_editor.graph.getModel().endUpdate();
        }

        // In order to return to the graph view.
        this.cancelClick();
    }

    /**
     * Convert form data to json object.
     * @param $form
     * @returns {{}}
     */
    function getFormData($form){
        var unindexed_array = $form.serializeArray();
        var indexed_array = {};

        $.map(unindexed_array, function(n, i){
            indexed_array[n['name']] = n['value'];
        });

        return indexed_array;
    }

    return SequenceController;
});
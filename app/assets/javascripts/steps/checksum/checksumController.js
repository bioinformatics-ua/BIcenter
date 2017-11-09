define('ChecksumController', ['Controller', 'ChecksumView'], function (Controller, ChecksumView) {
    var ChecksumController = function (module) {
        Controller.call(this, module, new ChecksumView(this));
    };

    // Inheritance from super class
    ChecksumController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    ChecksumController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
    };

    /**
     * Returns to the pipeline view.
     */
    ChecksumController.prototype.cancelClick = function(){
        var controller = 'GraphController';
        var containerController = this.module.controllers.ContainerController;
        if (!containerController) {
            console.err('Container controller not found!');
        }

        containerController.loadController(controller);
    }

    /**
     * Save checksum settings changes.
     */
    ChecksumController.prototype.submitClick = function(){
        var cell_name = MainModule.controllers.HeaderController.global_step.getAttribute("ctype");
        var cell;
        for(var i=0; i<Object.keys(global_editor.graph.getModel().cells).length; i++)
            if(global_editor.graph.getModel().cells[i].getAttribute("ctype") == cell_name)
                cell = global_editor.graph.getModel().cells[i];

        var $form = this.view.$elements.checksum_form;
        var formValues = getFormData($form);

        formValues.compatibilityMode = formValues.compatibilityMode ? true : false;

        global_editor.graph.getModel().beginUpdate();
        try
        {
            for(var fieldName in formValues) {
                var edit = new mxCellAttributeChange(cell, fieldName, formValues[fieldName]);
                global_editor.graph.getModel().execute(edit);
            }
        }
        finally
        {
            global_editor.graph.getModel().endUpdate();
        }

        var fields = $('#checksum_fields option:selected').map(function(){ return this.value }).get();
        var fields_list = [];
        for(var i=0; i<fields.length; i++){
            fields_list.push({"name":fields[i]});
        }

        var edit = new mxCellAttributeChange(cell, 'fields', JSON.stringify(fields_list));
        global_editor.graph.getModel().execute(edit);

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

    return ChecksumController;
});
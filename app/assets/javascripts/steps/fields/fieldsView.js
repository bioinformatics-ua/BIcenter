define('FieldsView', ['View'], function (View) {
    var FieldsView = function (controller) {
        View.call(this, controller, 'steps/fields');
    };

    // Inheritance from super class
    FieldsView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    FieldsView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        this._loadViewComponents();

        var controllers = this.controller.module.controllers;
        var headerController = controllers.HeaderController;

        var enc = new mxCodec(mxUtils.createXmlDocument());
        var node = enc.encode(global_editor.graph.getModel());
        var context = this;
        $.post('/step/inputOutputFields', { stepName:headerController.stepName, graph: mxUtils.getPrettyXml(node), before:headerController.before },
            function(returnedData){

                context.$elements.fields_header.empty();
                context.$elements.fields_header.append( "Step fields and its source: <strong>"+headerController.stepName+"</strong>" );

                context.$elements.fields_table_body.empty();
                context.$elements.fields_error.empty();

                if(returnedData.length == 0){
                    context.$elements.fields_error.text(headerController.stepName+" is the first step!");
                }
                else {
                    var header = "<tr id='fields_table_header'>";
                    var columns = Object.keys(returnedData[0]);
                    for (var i = 0; i < columns.length; i++) {
                        header += "<th>" + columns[i] + "</th>";
                    }
                    header += "</tr>"
                    context.$elements.fields_table_body.append(header);

                    for (var i = 0; i < returnedData.length; i++) {
                        var values = Object.values(returnedData[i]);
                        var row = "<tr>";
                        for (var j = 0; j < values.length; j++) {
                            row += "<td>" + values[j] + "</td>";
                        }
                        row += "</tr>";
                        context.$elements.fields_table_body.append(row);
                    }
                }
            }
        );
    };

    return FieldsView;
});
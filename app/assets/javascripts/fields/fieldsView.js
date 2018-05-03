define('FieldsView', ['View','Step'], function (View,Step) {
    var FieldsView = function (controller) {
        View.call(this, controller, 'fields');
    };

    // Inheritance from super class
    FieldsView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    FieldsView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        this._loadViewComponents();

        /*
        var controllers = this.controller.module.controllers;
        var headerController = controllers.HeaderController;

        var enc = new mxCodec(mxUtils.createXmlDocument());
        var node = enc.encode(global_editor.graph.getModel());
        var context = this;
        $.post('/step/inputOutputFields', { stepName:headerController.stepName, graph: mxUtils.getPrettyXml(node), before:headerController.before },
            function(fields){

                context.$elements.fields_header.empty();
                context.$elements.fields_header.append( "Step fields and its source: <strong>"+headerController.stepName+"</strong>" );

                context.$elements.fields_table_body.empty();
                context.$elements.fields_error.empty();

                if(fields.length == 0){
                    context.$elements.fields_error.text(headerController.stepName+" is the first step!");
                }
                else {
                    var header = "<tr id='fields_table_header'>";
                    var columns = Object.keys(fields[0]);
                    for (var i = 0; i < columns.length; i++) {
                        header += "<th>" + columns[i] + "</th>";
                    }
                    header += "</tr>"
                    context.$elements.fields_table_body.append(header);

                    for (var i = 0; i < fields.length; i++) {
                        var values = Object.values(fields[i]);
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
        */
    };

    FieldsView.prototype.loadStep = function (step,before) {
        var context = this;
        context.step = step;
        if(before == "true"){
            Step.showStepInput(this.controller.institutionId, step.id, function (fields) {
                context.loadFields(context.step,fields);
            });
        }
        else{
            Step.showStepOutput(this.controller.institutionId, step.id, function (fields) {
                context.loadFields(context.step,fields);
            })
        }
    };
    
    FieldsView.prototype.loadFields = function(step,fields) {
        this.$elements.fields_header.empty();
        this.$elements.fields_header.append( "Step fields and its source: <strong>"+step.label+"</strong>" );

        this.$elements.fields_table_body.empty();
        this.$elements.fields_error.empty();

        if(fields.length == 0){
            this.$elements.fields_error.text(step.label+" is the first step!");
        }
        else {
            var header = "<tr id='fields_table_header'>";
            var columns = Object.keys(fields[0]);
            for (var i = 0; i < columns.length; i++) {
                header += "<th>" + columns[i] + "</th>";
            }
            header += "</tr>"
            this.$elements.fields_table_body.append(header);

            for (var i = 0; i < fields.length; i++) {
                var values = Object.values(fields[i]);
                var row = "<tr>";
                for (var j = 0; j < values.length; j++) {
                    row += "<td>" + values[j] + "</td>";
                }
                row += "</tr>";
                this.$elements.fields_table_body.append(row);
            }
        }
    }

    return FieldsView;
});
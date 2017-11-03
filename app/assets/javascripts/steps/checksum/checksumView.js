define('ChecksumView', ['View'], function (View) {
    var ChecksumView = function (controller) {
        View.call(this, controller, 'steps/checksum');
    };

    // Inheritance from super class
    ChecksumView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    ChecksumView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        var controllers = this.controller.module.controllers;
        var headerController = controllers.HeaderController;

        this.$elements.checksum_step_name.val(headerController.global_step.getAttribute("label"));

        var index = headerController.global_step.getAttribute('checkSumType');
        this.$elements.checksum_type.val(index);

        var index = headerController.global_step.getAttribute('resultType');
        this.$elements.checksum_result_type.val(index);

        this.$elements.checksum_result_field.val(headerController.global_step.getAttribute("resultfieldName"));

        if(headerController.global_step.getAttribute("compatibilityMode") == "true"){
            this.$elements.checksum_mode.prop( "checked", true );
        }
        else{
            this.$elements.checksum_mode.prop( "checked", false );
        }

        var enc = new mxCodec(mxUtils.createXmlDocument());
        var node = enc.encode(global_editor.graph.getModel())
        var context = this;
        $.post('/step/inputFieldsName', { stepName:headerController.global_step.getAttribute("label"), graph: mxUtils.getPrettyXml(node) },
            function(returnedData){
                var fields = Object.values(returnedData);
                context.$elements.checksum_fields.empty();

                var checksum_fields = Object.values(JSON.parse(headerController.global_step.getAttribute("fields")));
                var names = [];
                for(var i=0; i<checksum_fields.length; i++){
                    names.push(checksum_fields[i]['name']);
                }

                for(var i=0; i<fields.length; i++) {
                    var option = '<option ';
                    if (names.indexOf(fields[i]['name']) >= 0)
                        option += 'selected';
                    option += '>';
                    option += fields[i]['name'];
                    option += '</option>';
                    context.$elements.checksum_fields.append(option);
                }
            }
        );
    };

    return ChecksumView;
});
define('RowGeneratorView', ['View'], function (View) {
    var RowGeneratorView = function (controller) {
        View.call(this, controller, 'steps/rowGenerator');
    };

    // Inheritance from super class
    RowGeneratorView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    RowGeneratorView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        this._loadViewComponents();

        var controllers = this.controller.module.controllers;
        var headerController = controllers.HeaderController;

        this.$elements.rowgen_step_name.val(headerController.global_step.getAttribute("label"));
        this.$elements.rowgen_step_limit.val(headerController.global_step.getAttribute("rowLimit"));

        if(headerController.global_step.getAttribute("neverEnding") == "Y"){
            this.$elements.rowgen_never_ending.prop( "checked", true );
        }
        else{
            this.$elements.rowgen_never_ending.prop( "checked", false );
        }

        this.$elements.rowgenerator_table_body.empty();

        var $tr = $('<tr>').append(
            $('<th>').text("name"),
            $('<th>').text("type"),
            $('<th>').text("format"),
            $('<th>').text("length"),
            $('<th>').text("precision"),
            $('<th>').text("currency"),
            $('<th>').text("decimal"),
            $('<th>').text("grouping"),
            $('<th>').text("value"),
            $('<th>').text("Set empty String?")
        );
        this.$elements.rowgenerator_table_body.append($tr[0]);

        var rows = $.parseJSON(headerController.global_step.getAttribute("fields"));
        for(var i=0; i<rows.length; i++){
            var item = rows[i];
            var $tr = $('<tr>').append(
                $('<td>').text(item.name),
                $('<td>').text(item.type),
                $('<td>').text(item.format),
                $('<td>').text(item.length),
                $('<td>').text(item.precision),
                $('<td>').text(item.currency),
                $('<td>').text(item.decimal),
                $('<td>').text(item.grouping),
                $('<td>').text(item.value),
                $('<td>').text(item.nullable)
            );
            this.$elements.rowgenerator_table_body.append($tr[0]);
        }
    };

    return RowGeneratorView;
});
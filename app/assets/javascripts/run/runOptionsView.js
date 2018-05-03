define('RunOptionsView', ['View'], function (View) {
    var RunOptionsView = function (controller) {
        View.call(this, controller, 'runOptions');
    };

    // Inheritance from super class
    RunOptionsView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    RunOptionsView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        var controllers = this.controller.module.controllers;
        var headerController = controllers.HeaderController;

        this.$elements.runoptions_table_parameters.empty();

        var $tr = $('<tr>').append(
            $('<th>').text("Parameter"),
            $('<th>').text("Value"),
            $('<th>').text("Defaults")
        );
        this.$elements.runoptions_table_parameters.append($tr[0]);

        var rows = $.parseJSON(global_editor['graph'].getDefaultParent().getAttribute('parameters'));
        for(var i=0; i<rows.length; i++) {
            var item = rows[i];
            var $tr = $('<tr>').append(
                $('<td>').text(item.param_name),
                $('<td>').text(item.param_value),
                $('<td>').text(item.param_defaults)
            );
            this.$elements.runoptions_table_parameters.append($tr[0]);
        }

        this.$elements.runoptions_table_variables.empty();

        var $tr = $('<tr>').append(
            $('<th>').text("Variable"),
            $('<th>').text("Value")
        );
        this.$elements.runoptions_table_variables.append($tr[0]);

        var rows = $.parseJSON(global_editor['graph'].getDefaultParent().getAttribute('variables'));
        for(var i=0; i<rows.length; i++) {
            var item = rows[i];
            var $tr = $('<tr>').append(
                $('<td>').text(item.var_name),
                $('<td>').text(item.var_value)
            );
            this.$elements.runoptions_table_variables.append($tr[0]);
        }
    };

    return RunOptionsView;
});
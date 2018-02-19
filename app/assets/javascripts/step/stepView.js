define('StepView', ['View', 'Step', 'jsRoutes', 'underscore', 'templates'], function (View, Step, jsRoutes, _) {
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
        var html = JST['step']({component: step.component});
        this.$container.html(html);
        this._loadViewComponents();

        var context = this;
        $.noConflict();

        Step.getTables(step.id, function (result) {
            var tables = JSON.parse(result);
            context.tables = tables;

            _.each(tables, function (table) {
                var tableId = table.id;

                var columns = [];
                _.each(table.fields, function (field) {
                    columns.push({data: field.name});
                });

                columns.push(
                    {
                        'data': function (row, type, val, meta) {
                            var rowId = meta.row;
                            var html = '';
                            html += '<button type="button" class="btn btn-warning btn-xs" view-click="editRow(' + tableId + ',' + rowId + ')"><i class="fa fa-pencil fa-fw"></i>Edit</button> ';
                            html += '<button type="button" class="btn btn-danger btn-xs" view-click="deleteRow(' + tableId + ',' + rowId + ')"><i class="fa fa-times-circle fa-fw"></i>Delete</button>';
                            return html;
                        },
                        'orderable': false
                    }
                );

                $('#' + table.id).DataTable(
                    {
                        dom: "frtip",
                        ajax: jsRoutes.controllers.StepController.getTableValue(step.id, table.id).url,
                        order: [[1, 'asc']],
                        columns: columns,
                        'drawCallback': function (o) {
                            context._loadViewComponents();
                        }
                    });

            });
        });
    };

    StepView.prototype.editRow = function (tableId, rowId) {
        var table = $('#' + tableId).DataTable();
        var data = table.row(rowId).data();

        var tableData = _.findWhere(this.tables, {id: parseInt(tableId)});
        if (tableData) {

            var finalData = [];
            _.each(tableData.fields, function (field) {
                var obj = {
                    id: field.name,
                    label: field.label,
                    value: data[field.name]
                };
                finalData.push(obj);
            });

            var modalController = this.controller.module.controllers['SampleModalController'];
            modalController.view.show(tableId,rowId,finalData);
        }
    };

    StepView.prototype.deleteRow = function (table, row) {
        console.log('Delete row', row);
    };

    return StepView;
});

define('StepView', ['View', 'Step', 'jsRoutes', 'underscore', 'templates', 'dataTables', 'query-builder'], function (View, Step, jsRoutes, _) {
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

    StepView.prototype.loadStep = function (step, inputSteps, inputFields, outputSteps) {
        this.inputFields = inputFields;

        var html = JST['step']({
            component: step.component,
            inputSteps: inputSteps,
            inputFields: inputFields,
            outputSteps: outputSteps
        });
        this.$container.html(html);
        this._loadViewComponents();

        this.renderConditions(step, inputFields);
        this.renderTables(step);
    };

    /**
     * Initialize all QueryBuilder widgets.
     * @param step
     * @param inputFields
     */
    StepView.prototype.renderConditions = function (step, inputFields) {
        var context = this;
        context.step = step;

        this.filters = [];
        _.each(inputFields, function (input) {
            context.filters.push({id: input.name, label: input.name, type: "string"});
        });

        Step.getConditions(step.id, function (conditions) {
            context.conditions = conditions;

            _.each(conditions, function (condition) {
                var stepId = context.step.id;
                var componentId = condition.id;
                Step.getConditionValue(stepId, componentId, function (rules) {
                    context.$elements[condition.id].queryBuilder({
                        plugins: ['bt-tooltip-errors'],
                        conditions: ["OR", "AND", "OR NOT", "AND NOT", "XOR"],
                        operators: [
                            {
                                type: '=',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: '<>',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: '<',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: '<=',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: '>',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: '>=',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: 'REGEXP',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: 'IS NULL',
                                nb_inputs: 0,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: 'IS NOT NULL',
                                nb_inputs: 0,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: 'IN LIST',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: 'CONTAINS',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: 'STARTS WITH',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: 'ENDS WITH',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: 'LIKE',
                                nb_inputs: 1,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            },
                            {
                                type: 'TRUE',
                                nb_inputs: 0,
                                multiple: false,
                                apply_to: ['string', 'number', 'datetime', 'boolean']
                            }
                        ],
                        filters: context.filters,
                        rules: rules
                    });
                })
            });
        });
    };

    /**
     * Initialize all DataTables.
     * @param step
     */
    StepView.prototype.renderTables = function (step) {
        this.dataTables = {};
        var context = this;
        Step.getTables(step.id, function (tables) {
            // var tables = JSON.parse(result);
            context.tables = tables;

            _.each(tables, function (table) {
                var tableId = table.id;

                var columns = [];
                _.each(table.fields, function (field) {
                    columns.push({data: field.name});

                    if (field.source) {
                        var tmpArr = field.source.split('@');
                        if (tmpArr.length > 1) {
                            var shortName = tmpArr[1];

                            Step.getByComponentAndShortName(step.component.id, shortName, function (component) {
                                console.log(component)
                                context.$elements[component].attr('view-change', 'controller.cenas(' + tableId + ',' + field.name + ')');
                            });
                        }
                    }
                });

                context._loadViewComponents();

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

                context.dataTables[table.id] = context.$elements[table.id].DataTable(
                    {
                        dom: "Bfrtip",
                        ajax: jsRoutes.controllers.StepController.getTableValue(step.id, table.id).url,
                        order: [[1, 'asc']],
                        columns: columns,
                        'drawCallback': function (o) {
                            context._loadViewComponents();
                        },
                        buttons: [
                            {
                                text: 'Add',
                                action: function (e, dt, node, config) {
                                    var tableData = _.findWhere(context.tables, {id: parseInt(table.id)});
                                    if (tableData) {

                                        var finalData = [];
                                        _.each(tableData.fields, function (field) {
                                            var obj = {
                                                id: field.name,
                                                label: field.label,
                                                value: "",
                                                type: 'input',
                                                source: field.source
                                            };

                                            if (field.source) {
                                                if (field.source == "metadata") {
                                                    _.extend(obj, {
                                                        type: 'select',
                                                        options: field.metadatas
                                                    });
                                                }
                                                else if (field.source.indexOf('@') > -1) {
                                                    _.extend(obj, {
                                                        type: 'select',
                                                        options: context.controller.data[field.name]
                                                    });
                                                }
                                                else {
                                                    _.extend(obj, {
                                                        type: 'select',
                                                        options: context.inputFields
                                                    });
                                                }
                                            }

                                            finalData.push(obj);
                                        });

                                        var modalController = context.controller.module.controllers['StepModalController'];
                                        modalController.view.show(tableId, undefined, finalData);
                                    }
                                }
                            }
                        ]
                    });
            });
        });
    };

    StepView.prototype.editRow = function (tableId, rowId) {
        var table = this.$elements[tableId].DataTable();
        var data = table.row(rowId).data();
        var context = this;

        var tableData = _.findWhere(this.tables, {id: parseInt(tableId)});
        if (tableData) {

            var finalData = [];
            _.each(tableData.fields, function (field) {
                var obj = {
                    id: field.name,
                    label: field.label,
                    source: field.source,
                    value: data[field.name],
                    type: 'input'
                };

                if (field.source) {
                    if (field.source == "metadata") {
                        _.extend(obj, {
                            type: 'select',
                            options: field.metadatas
                        });
                    }
                    else if (field.source.indexOf('@') > -1) {
                        _.extend(obj, {
                            type: 'select',
                            options: context.controller.data[field.name]
                        });
                    }
                    else {
                        _.extend(obj, {
                            type: 'select',
                            options: context.inputFields
                        });
                    }
                }

                finalData.push(obj);
            });

            var modalController = this.controller.module.controllers['StepModalController'];
            modalController.view.show(tableId, rowId, finalData);
        }
    };

    StepView.prototype.deleteRow = function (tableId, rowId) {
        var table = this.$elements[tableId].DataTable();
        table.row(rowId).remove().draw();
    };

    return StepView;
});

define('StepView', ['View', 'Step', 'jsRoutes', 'underscore', 'templates', 'dataTables', 'query-builder', 'select2'], function (View, Step, jsRoutes, _) {
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

    StepView.prototype.loadStep = function (step, inputSteps, inputFields, outputSteps, dataSources) {
        this.inputFields = inputFields;

        var html = JST['step']({
            component: step.component,
            inputSteps: inputSteps,
            inputFields: inputFields,
            outputSteps: outputSteps,
            dataSources: dataSources
        });
        this.$container.html(html);
        this._loadViewComponents();

        this.renderConditions(step, inputFields);
        this.renderTables(step);
        this.renderMultiSelects(step);
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

        Step.getConditions(this.controller.institutionId, step.id, function (conditions) {
            context.conditions = conditions;

            _.each(conditions, function (condition) {
                var stepId = context.step.id;
                var componentId = condition.id;
                Step.getConditionValue(context.controller.institutionId, stepId, componentId, function (rules) {
                    context.$elements[condition.id].queryBuilder({
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

        Step.getTables(this.controller.institutionId, step.id, function (tables) {
            // var tables = JSON.parse(result);
            context.tables = tables;

            _.each(tables, function (table) {
                var tableId = table.id;

                var columns = [];
                _.each(table.fields, function (field) {
                    columns.push({data: field.name});

                    if (field.source) {
                        var tmpArr = field.source.split('@');
                        if ( tmpArr.length > 1 && (tmpArr[0] != "inputLength" && tmpArr[0] != "inputPrecision") ) {
                            var shortName = tmpArr[1];

                            Step.getByComponentAndShortName(context.controller.institutionId, step.component.id, shortName, function (component) {
                                context.controller.updateColumn(tableId, field.name, component);
                                context.$elements[component].attr('view-change', 'controller.updateColumn(' + tableId + ',' + field.name + ')');
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
                        ajax: jsRoutes.controllers.StepController.getTableValue(context.controller.institutionId, step.id, table.id).url,
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
                                                if (field.source == "dataSources") {
                                                    _.extend(obj, {
                                                        type: 'select',
                                                        options: context.controller.dataSources
                                                    });
                                                }
                                                else if (field.source == "metadata") {
                                                    _.extend(obj, {
                                                        type: 'select',
                                                        options: field.metadatas
                                                    });
                                                }
                                                else if (field.source.indexOf('@') > -1) {
                                                    if(field.source.split('@')[0] == "inputLength"){
                                                        _.extend(obj, {
                                                            type: 'number'
                                                        });
                                                    }
                                                    else if(field.source.split('@')[0] == "inputPrecision"){
                                                        _.extend(obj, {
                                                            type: 'number'
                                                        });
                                                    }
                                                    else {
                                                        _.extend(obj, {
                                                            type: 'select',
                                                            options: context.controller.data[field.name]
                                                        });
                                                    }
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
                                        modalController.view.show(tableId, undefined, finalData, context.step);
                                    }
                                }
                            }
                        ]
                    });
            });
        });
    };

    /**
     * Initialize all Multi-Selects.
     * @param step
     */
    StepView.prototype.renderMultiSelects = function (step) {
        var context = this;
        Step.getMultiSelects(this.controller.institutionId, step.id, function (selects) {
            _.each(selects, function (select) {
                var selectId = select.id;
                context.$elements[selectId].select2({
                    placeholder: '',
                    multiple: true,
                    closeOnSelect: false
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
                    if (field.source == "dataSources") {
                        _.extend(obj, {
                            type: 'select',
                            options: context.controller.dataSources
                        });
                    }
                    else if (field.source == "metadata") {
                        _.extend(obj, {
                            type: 'select',
                            options: field.metadatas
                        });
                    }
                    else if (field.source.indexOf('@') > -1) {
                        if(field.source.split('@')[0] == "inputLength"){
                            var shortName = field.source.split('@')[1];
                        }
                        else if(field.source.split('@')[0] == "inputPrecision"){
                            var shortName = field.source.split('@')[1];
                        }
                        else {
                            _.extend(obj, {
                                type: 'select',
                                options: context.controller.data[field.name]
                            });
                        }
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

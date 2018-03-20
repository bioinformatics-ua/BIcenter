define('DataSourceController', ['Controller', 'DataSourceView', 'async', 'Router', 'Institution'], function (Controller, DataSourceView, async, Router, Institution) {
    var DataSourceController = function (module) {
        Controller.call(this, module, new DataSourceView(this));
    };

    // Inheritance from super class
    DataSourceController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    DataSourceController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        // Add button to save data
        this.view.addButtons({
            save: {
                label: '<i class="fa fa-check-circle"></i> Save',
                className: 'btn-success',
                callback: 'controller.updateDataSource()'
            }
        });
    };

    DataSourceController.prototype.loadDataSource = function(dataSourceId){
        var context = this;
        async.parallel(
            [
                function (callback) {
                    Institution.getDataSource(dataSourceId, function(dataSource){
                        context.dataSource = dataSource;
                        callback();
                    });
                },
                function (callback) {
                    Institution.getInstitutions(function(institutions){
                        context.institutions = institutions;
                        callback();
                    });
                }
            ],
            function (err) {
                if (err) {
                    console.error(err);
                    return;
                }

                context.view.show(context.dataSource,context.institutions);
            }
        );
    }

    DataSourceController.prototype.updateDataSource = function(event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        var $form = this.view.$elements[this.dataSource.id];
        var formValues = $form.serializeForm();

        var context = this;
        Institution.updateDataSource(this.dataSource.id, formValues, function (dataSource) {
            console.log("DataSource", dataSource.id, "has been updated!");
        });

        this.view.hide();
    };

    /**
     * Returns to the pipeline view.
     */
    DataSourceController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    return DataSourceController;
});
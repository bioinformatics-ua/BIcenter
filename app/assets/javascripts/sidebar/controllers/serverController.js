define('ServerController', ['Controller', 'ServerView', 'async', 'Router', 'Institution'], function (Controller, ServerView, async, Router, Institution) {
    var ServerController = function (module) {
        Controller.call(this, module, new ServerView(this));
    };

    // Inheritance from super class
    ServerController.prototype = Object.create(Controller.prototype);
    var _super_ = Controller.prototype;

    ServerController.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        // Add button to save data
        this.view.addButtons({
            save: {
                label: '<i class="fa fa-check-circle"></i> Save',
                className: 'btn-success',
                callback: 'controller.updateServer()'
            }
        });
    };

    ServerController.prototype.loadServer = function(institutionId,serverId){
        this.institutionId = institutionId;

        var context = this;
        async.parallel(
            [
                function (callback) {
                    Institution.getServer(institutionId,serverId, function(server){
                        context.server = server;
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

                context.view.show(context.server,context.institutions);
            }
        );
    }

    ServerController.prototype.updateServer = function(event) {
        if (event) {
            event.preventDefault && event.preventDefault();
            event.stopPropagation && event.stopPropagation();
            event.stopImmediatePropagation && event.stopImmediatePropagation();
        }

        var $form = this.view.$elements[this.server.id];
        var formValues = $form.serializeForm();

        var context = this;
        Institution.updateServer(this.institutionId, this.server.id, formValues, function (server) {
            console.log("Server", server.id, "has been updated!");
        });

        this.view.hide();
    };

    /**
     * Returns to the pipeline view.
     */
    ServerController.prototype.cancelClick = function(){
        Router.navigatePrevious();
    }

    return ServerController;
});
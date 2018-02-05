define('Task', ['jsRoutes', 'messages'], function (jsRoutes, Messages) {
    var Task = Task || {};

    Task.new_task = function (name, callback) {
        jsRoutes.controllers.TransGraphController.new_task(name).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Step service', response);
            }
        })
    };

    Task.load_task = function(graphId, callback) {
        jsRoutes.controllers.TransGraphController.load_task(graphId).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Step service', response);
            }
        })
    };

    Task.get_task = function(name, callback) {
        jsRoutes.controllers.TransGraphController.get_task(name).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Step service', response);
            }
        })
    };

    return Task;
});
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
                console.error('Error in Task service', response);
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
                console.error('Error in Task service', response);
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
                console.error('Error in Task service', response);
            }
        })
    };

    Task.add_step = function(graphId, stepMeta, callback){
        jsRoutes.controllers.TransGraphController.add_step(graphId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(stepMeta),
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Task service', response);
            }
        })
    }

    Task.removeStep = function(stepId, callback){
        jsRoutes.controllers.TransGraphController.remove_step(stepId).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Task service', response);
            }
        })
    }

    Task.get_steps = function(taskId, callback){
        jsRoutes.controllers.TransGraphController.get_steps(taskId).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Task service', response);
            }
        })
    }

    Task.add_hop = function(graphId, hopMeta, callback){
        jsRoutes.controllers.TransGraphController.add_hop(graphId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(hopMeta),
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Task service', response);
            }
        })
    }

    Task.removeHop = function(hopId, callback){
        jsRoutes.controllers.TransGraphController.remove_hop(hopId).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Task service', response);
            }
        })
    }

    return Task;
});
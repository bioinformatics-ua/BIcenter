define('Task', ['jsRoutes', 'messages'], function (jsRoutes, Messages) {
    var Task = Task || {};

    Task.newTask = function (name, callback) {
        jsRoutes.controllers.TransGraphController.newTask(name).ajax({
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

    Task.loadTask = function(graphId, callback) {
        jsRoutes.controllers.TransGraphController.loadTask(graphId).ajax({
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

    Task.getTask = function(name, callback) {
        jsRoutes.controllers.TransGraphController.getTask(name).ajax({
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

    Task.addStep = function(graphId, stepMeta, callback){
        jsRoutes.controllers.TransGraphController.addStep(graphId).ajax({
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

    Task.getSteps = function(taskId, callback){
        jsRoutes.controllers.TransGraphController.getSteps(taskId).ajax({
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

    Task.addHop = function(graphId, hopMeta, callback){
        jsRoutes.controllers.TransGraphController.addHop(graphId).ajax({
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

    Task.closeTab = function(taskId, callback){
        jsRoutes.controllers.TransGraphController.closeTab(taskId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({}),
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

    Task.getOpenTabs = function(callback){
        jsRoutes.controllers.TransGraphController.getOpenTabs().ajax({
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
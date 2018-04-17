define('Task', ['jsRoutes', 'messages'], function (jsRoutes, Messages) {
    var Task = Task || {};

    // Task.getTasks = function (callback) {
    //     jsRoutes.controllers.TransGraphController.getTasks().ajax({
    //         contentType: 'application/json; charset=utf-8',
    //         success: function (response) {
    //             if (callback) {
    //                 callback(response);
    //             }
    //         },
    //         error: function (response) {
    //             console.error('Error in Task service', response);
    //         }
    //     })
    // };

    Task.deleteTask = function (institutionId, taskId, callback) {
        jsRoutes.controllers.TransGraphController.deleteTask(institutionId, taskId).ajax({
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

    Task.updateTask = function (institutionId, taskId, formValues, callback) {
        jsRoutes.controllers.TransGraphController.updateTask(institutionId, taskId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(formValues),
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

    Task.newTask = function (institution,name, callback) {
        jsRoutes.controllers.TransGraphController.newTask(institution,name).ajax({
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

    Task.loadTask = function(institution, graphId, callback) {
        jsRoutes.controllers.TransGraphController.loadTask(institution, graphId).ajax({
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

    Task.getTask = function(institution, name, callback) {
        jsRoutes.controllers.TransGraphController.getTask(institution, name).ajax({
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

    Task.getTaskDetails = function(institution, taskId, callback) {
        jsRoutes.controllers.TransGraphController.getTaskDetails(institution, taskId).ajax({
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

    Task.addStep = function(institution, graphId, stepMeta, callback){
        jsRoutes.controllers.TransGraphController.addStep(institution, graphId).ajax({
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

    Task.updateStep = function(institution, stepId,coord, callback){
        jsRoutes.controllers.TransGraphController.updateStep(institution, stepId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(coord),
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

    Task.removeStep = function(institution, stepId, callback){
        jsRoutes.controllers.TransGraphController.removeStep(institution, stepId).ajax({
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

    // Task.getSteps = function(taskId, callback){
    //     jsRoutes.controllers.TransGraphController.getSteps(taskId).ajax({
    //         contentType: 'application/json; charset=utf-8',
    //         success: function (response) {
    //             if (callback) {
    //                 callback(response);
    //             }
    //         },
    //         error: function (response) {
    //             console.error('Error in Task service', response);
    //         }
    //     })
    // }

    Task.addHop = function(institution, graphId, hopMeta, callback){
        jsRoutes.controllers.TransGraphController.addHop(institution,graphId).ajax({
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

    Task.removeHop = function(institution, hopId, callback){
        jsRoutes.controllers.TransGraphController.removeHop(institution, hopId).ajax({
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

    Task.getExecutions = function(institution, taskId, callback){
        jsRoutes.controllers.TransGraphController.getExecutions(institution, taskId).ajax({
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

    Task.getServers = function(institution, taskId, callback){
        jsRoutes.controllers.TransGraphController.getServers(institution, taskId).ajax({
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
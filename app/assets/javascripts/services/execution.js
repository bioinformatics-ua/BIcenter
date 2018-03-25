define('Execution', ['jsRoutes', 'messages'], function (jsRoutes, Messages) {
    var Execution = Execution || {};

    Execution.localExecution = function (taskId,callback) {
        jsRoutes.controllers.ExecutionController.localExecution(taskId).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Execution service', response);
            }
        })
    };

    Execution.remoteExecution = function (taskId,serverId,details,callback) {
        jsRoutes.controllers.ExecutionController.remoteExecution(taskId,serverId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(details),
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Execution service', response);
            }
        })
    };

    Execution.result = function (execution,callback) {
        jsRoutes.controllers.ExecutionController.result(execution).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Execution service', response);
            }
        })
    };

    Execution.getTask = function (execution,callback) {
        jsRoutes.controllers.ExecutionController.getTask(execution).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Execution service', response);
            }
        })
    };

    Execution.getLogs = function (execution,callback) {
        jsRoutes.controllers.ExecutionController.getLogs(execution).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Execution service', response);
            }
        })
    };

    Execution.getMetrics = function (execution,callback) {
        jsRoutes.controllers.ExecutionController.getMetrics(execution).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Execution service', response);
            }
        })
    };

    Execution.getData = function (execution,callback) {
        jsRoutes.controllers.ExecutionController.getData(execution).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Execution service', response);
            }
        })
    };

    Execution.getStepData = function (execution,step,callback) {
        jsRoutes.controllers.ExecutionController.getStepData(execution,step).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Execution service', response);
            }
        })
    };

    Execution.deleteSchedule = function (task,schedule,callback) {
        jsRoutes.controllers.ExecutionController.deleteSchedule(task,schedule).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Execution service', response);
            }
        })
    };

    return Execution;
});
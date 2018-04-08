define('Execution', ['jsRoutes', 'messages'], function (jsRoutes, Messages) {
    var Execution = Execution || {};

    Execution.localExecution = function (intitutionId,taskId,callback) {
        jsRoutes.controllers.ExecutionController.localExecution(intitutionId,taskId).ajax({
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

    Execution.remoteExecution = function (intitutionId,taskId,serverId,details,callback) {
        jsRoutes.controllers.ExecutionController.remoteExecution(intitutionId,taskId,serverId).ajax({
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

    Execution.result = function (intitutionId,execution,callback) {
        jsRoutes.controllers.ExecutionController.result(intitutionId,execution).ajax({
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

    Execution.getTask = function (intitutionId,execution,callback) {
        jsRoutes.controllers.ExecutionController.getTask(intitutionId,execution).ajax({
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

    Execution.getLogs = function (intitutionId,execution,callback) {
        jsRoutes.controllers.ExecutionController.getLogs(intitutionId,execution).ajax({
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

    Execution.getMetrics = function (intitutionId,execution,callback) {
        jsRoutes.controllers.ExecutionController.getMetrics(intitutionId,execution).ajax({
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

    Execution.getData = function (intitutionId,execution,callback) {
        jsRoutes.controllers.ExecutionController.getData(intitutionId,execution).ajax({
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

    Execution.getStepData = function (intitutionId,execution,step,callback) {
        jsRoutes.controllers.ExecutionController.getStepData(intitutionId,execution,step).ajax({
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

    Execution.deleteSchedule = function (intitutionId,task,schedule,callback) {
        jsRoutes.controllers.ExecutionController.deleteSchedule(intitutionId,task,schedule).ajax({
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
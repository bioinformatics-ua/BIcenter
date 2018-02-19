define('Execution', ['jsRoutes', 'messages'], function (jsRoutes, Messages) {
    var Execution = Execution || {};

    Execution.run = function (taskId,execution,callback) {
        jsRoutes.controllers.ExecutionController.run(taskId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(execution),
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

    return Execution;
});
define('Step', ['jsRoutes', 'messages'], function (jsRoutes, Messages) {
    var Step = Step || {};

    Step.getSchema = function (stepId, callback) {
        jsRoutes.controllers.StepController.getSchema(stepId).ajax({
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

    Step.getStep = function (stepId, callback) {
        jsRoutes.controllers.StepController.getStep(stepId).ajax({
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

    Step.applyChanges = function (stepId,formData,callback) {
        jsRoutes.controllers.StepController.applyChanges(stepId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(formData),
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

    return Step;
});
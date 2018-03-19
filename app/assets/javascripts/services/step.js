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

    Step.getStepName = function (stepId, callback) {
        jsRoutes.controllers.StepController.getStepName(stepId).ajax({
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

    Step.getTables = function (stepId, callback) {
        jsRoutes.controllers.StepController.getTables(stepId).ajax({
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

    Step.getConditions = function (stepId, callback) {
        jsRoutes.controllers.StepController.getConditions(stepId).ajax({
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

    Step.getConditionValue = function (stepId, componentId, callback) {
        jsRoutes.controllers.StepController.getConditionValue(stepId,componentId).ajax({
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

    Step.showStepInput = function(stepId, callback) {
        jsRoutes.controllers.StepController.inputOutputFields(stepId,true).ajax({
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

    Step.showStepOutput = function(stepId,callback) {
        jsRoutes.controllers.StepController.inputOutputFields(stepId,false).ajax({
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

    Step.inputStepsName = function(stepId,callback) {
        jsRoutes.controllers.StepController.inputStepsName(stepId).ajax({
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

    Step.inputFieldsName = function(stepId,callback) {
        jsRoutes.controllers.StepController.inputFieldsName(stepId).ajax({
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

    Step.outputStepsName = function(stepId,callback) {
        jsRoutes.controllers.StepController.outputStepsName(stepId).ajax({
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

    Step.getByComponentAndShortName = function(componentId, shortName, callback) {
        jsRoutes.controllers.StepController.getByComponentAndShortName(componentId, shortName).ajax({
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

    Step.getInstitution = function(stepId,callback) {
        jsRoutes.controllers.StepController.getInstitution(stepId).ajax({
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

    return Step;
});
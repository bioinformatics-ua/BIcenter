define('Step', ['jsRoutes', 'messages'], function (jsRoutes, Messages) {
    var Step = Step || {};

    Step.getSchema = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.getSchema(institutionId, stepId).ajax({
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

    Step.getStepName = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.getStepName(institutionId, stepId).ajax({
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

    Step.getStep = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.getStep(institutionId, stepId).ajax({
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

    Step.applyChanges = function (institutionId, stepId, formData, fileData, callback) {
        jsRoutes.controllers.StepController.applyChanges(institutionId, stepId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(formData),
            success: function (response) {
                // If there's a file to upload, upload it
                if (fileData != null) {
                    jsRoutes.controllers.StepController.uploadFile(institutionId, stepId).ajax({
                        data: fileData,
                        type: 'POST',
                        contentType: false,
                        processData: false,

                        success: function (response) {
                            if (callback) {
                                callback(response);
                            }
                        },
                        error: function (response) {
                            console.error('Error in Step service when uploading file', response);
                        }
                    })
                } else {
                    if (callback) {
                        callback(response);
                    }
                }
            },
            error: function (response) {
                console.error('Error in Step service when updating step', response);
            }
        })
    };


    Step.getTables = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.getTables(institutionId, stepId).ajax({
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

    Step.getConditions = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.getConditions(institutionId, stepId).ajax({
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

    Step.getConditionValue = function (institutionId, stepId, componentId, callback) {
        jsRoutes.controllers.StepController.getConditionValue(institutionId, stepId, componentId).ajax({
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

    Step.getMultiSelects = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.getMultiSelects(institutionId, stepId).ajax({
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

    Step.showStepInput = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.inputOutputFields(institutionId, stepId, true).ajax({
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

    Step.showStepOutput = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.inputOutputFields(institutionId, stepId, false).ajax({
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

    Step.inputStepsName = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.inputStepsName(institutionId, stepId).ajax({
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

    Step.inputFieldsName = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.inputFieldsName(institutionId, stepId).ajax({
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

    Step.outputStepsName = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.outputStepsName(institutionId, stepId).ajax({
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

    Step.getByComponentAndShortName = function (institutionId, componentId, shortName, callback) {
        jsRoutes.controllers.StepController.getByComponentAndShortName(institutionId, componentId, shortName).ajax({
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

    Step.getMetadataByComponentAndShortName = function (institutionId, componentId, shortName, callback) {
        jsRoutes.controllers.StepController.getMetadataByComponentAndShortName(institutionId, componentId, shortName).ajax({
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

    Step.getInstitution = function (institutionId, stepId, callback) {
        jsRoutes.controllers.StepController.getInstitution(institutionId, stepId).ajax({
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
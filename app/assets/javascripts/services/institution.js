define('Institution', ['jsRoutes', 'messages'], function (jsRoutes, Messages) {
    var Institution = Institution || {};

    Institution.getInstitutionName = function (institution, callback) {
        jsRoutes.controllers.InstitutionController.getInstitutionName(institution).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.getInstitutions = function (callback) {
        jsRoutes.controllers.InstitutionController.getInstitutions().ajax({
            contentType: 'application/json; charset=utf-8',
			success: function (response) {
				if (callback) {
					callback(response);
				}
			},
			error: function (response) {
				console.error('Error in Institution service', response);
			}
		})
	};

    Institution.getInstitution = function (institution, callback) {
        jsRoutes.controllers.InstitutionController.getInstitution(institution).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.deleteInstitution = function (institution, callback) {
        jsRoutes.controllers.InstitutionController.deleteInstitution(institution).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

	Institution.newInstitution = function (formValues, callback) {
		jsRoutes.controllers.InstitutionController.newInstitution().ajax({
			contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(formValues),
            success: function (response) {
				if (callback) {
					callback(response);
				}
			},
			error: function (response) {
				console.error('Error in Institution service', response);
			}
		})
	};

    Institution.updateInstitution = function (institutionId, formValues, callback) {
        jsRoutes.controllers.InstitutionController.updateInstitution(institutionId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(formValues),
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

	Institution.newServer = function (institution, name, callback) {
		jsRoutes.controllers.InstitutionController.newServer(institution, name).ajax({
			contentType: 'application/json; charset=utf-8',
			success: function (response) {
				if (callback) {
					callback(response);
				}
			},
			error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.deleteServer = function (institutionId, serverId, callback) {
        jsRoutes.controllers.InstitutionController.deleteServer(institutionId, serverId).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.getServer = function (institutionId, serverId, callback) {
        jsRoutes.controllers.InstitutionController.getServer(institutionId, serverId).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.updateServer = function (institutionId, serverId, formValues, callback) {
        jsRoutes.controllers.InstitutionController.updateServer(institutionId, serverId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(formValues),
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.newDataSource = function (institution,name, callback) {
        jsRoutes.controllers.InstitutionController.newDataSource(institution,name).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.getDataSource = function (institutionId, dataSourceId, callback) {
        jsRoutes.controllers.InstitutionController.getDataSource(institutionId, dataSourceId).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.deleteDataSource = function (institutionId, dataSourceId, callback) {
        jsRoutes.controllers.InstitutionController.deleteDataSource(institutionId, dataSourceId).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.updateDataSource = function (institutionId, dataSourceId, formValues, callback) {
        jsRoutes.controllers.InstitutionController.updateDataSource(institutionId, dataSourceId).ajax({
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(formValues),
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.getDataSources = function (institutionId, callback) {
        jsRoutes.controllers.InstitutionController.getDataSources(institutionId).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.getComponents = function (callback) {
        jsRoutes.controllers.InstitutionController.getComponents().ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    Institution.getSchedules = function (institutionId, callback) {
        jsRoutes.controllers.InstitutionController.getSchedules(institutionId).ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Institution service', response);
            }
        })
    };

    return Institution;
});

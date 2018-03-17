define('Institution', ['jsRoutes', 'messages'], function (jsRoutes, Messages) {
    var Institution = Institution || {};

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

    Institution.newServer = function (institution,name, callback) {
        jsRoutes.controllers.InstitutionController.newServer(institution,name).ajax({
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

    Institution.getServer = function (serverId, callback) {
        jsRoutes.controllers.InstitutionController.getServer(serverId).ajax({
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

    Institution.updateServer = function (serverId, formValues, callback) {
        jsRoutes.controllers.InstitutionController.updateServer(serverId).ajax({
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

    return Institution;
});
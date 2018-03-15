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

    return Institution;
});
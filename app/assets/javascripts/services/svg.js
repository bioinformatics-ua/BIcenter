define('Svg', ['jsRoutes', 'messages'], function (jsRoutes, Messages) {
    var Svg = Svg || {};

    Svg.getMiddleImage = function (image, callback) {
        jsRoutes.controllers.SvgController.getMiddleImage(image+".svg").ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Svg service', response);
            }
        })
    };

    Svg.getSmallImage = function (image, callback) {
        jsRoutes.controllers.SvgController.getSmallImage(image+".svg").ajax({
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (callback) {
                    callback(response);
                }
            },
            error: function (response) {
                console.error('Error in Svg service', response);
            }
        })
    };

    return Svg;
});
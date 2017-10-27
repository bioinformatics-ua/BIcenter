define('Utils', ['underscore'], function (_) {
    var Utils = Utils || {};

    Utils.getKeyValuePairs = function (text) {
        var pairs = [];
        if (text.indexOf(',') > -1) {
            pairs = text.split(',');
        } else {
            pairs.push(text);
        }

        var ret = [];
        _.each(pairs, function (pair) {
            var args = pair.split(':');
            if (args.length != 2) {
                throw new Error('Wrong init format on: ' + v);
            }

            var key = args[0].trim();
            var value = args[1].trim();

            ret.push({
                key: key,
                value: value
            });
        });

        return ret;
    };

    return Utils;
});

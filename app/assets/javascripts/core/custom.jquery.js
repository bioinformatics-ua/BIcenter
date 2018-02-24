require(['jquery'], function ($) {
    $.fn.extend({
        serializeForm: function() {
            var unindexed_array = $(this).serializeArray();
            var indexed_array = {};

            $.map(unindexed_array, function (n, i) {
                var item = app.modules.MainModule.controllers.StepController.view.$elements[n['name']];
                if (item.is('[multiple]')) {
                    if (!indexed_array[n['name']]) indexed_array[n['name']] = [];
                    indexed_array[n['name']].push(n['value']);
                }
                else {
                    indexed_array[n['name']] = n['value'];
                }
            });

            return indexed_array;
        }
    });
});
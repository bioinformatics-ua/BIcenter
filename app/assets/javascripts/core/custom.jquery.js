require(['jquery'], function ($) {
    $.fn.extend({
        serializeForm: function () {
            var unindexed_array = $(this).serializeArray();
            var indexed_array = {};

            // Gambiarra FTW!
            var module = app.modules.MainModule;
            var controller;
            if (module) controller = module.controllers.StepController;

            $.map(unindexed_array, function (n, i) {
                var item;
                if (controller) item = controller.view.$elements[n['name']];
                if (item != undefined && item.is('[multiple]')) {
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
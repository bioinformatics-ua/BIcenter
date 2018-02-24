require(['jquery'], function ($) {
    $.fn.extend({
        serializeForm: function() {
            var unindexed_array = $(this).serializeArray();
            var indexed_array = {};

            $.map(unindexed_array, function (n, i) {
                if (indexed_array[n['name']]) {
                    if(!(indexed_array[n['name']] instanceof Array)){
                        indexed_array[n['name']] = [indexed_array[n['name']]].concat(n['value']);
                    }
                    else indexed_array[n['name']] = indexed_array[n['name']].concat(n['value']);
                } else {
                    indexed_array[n['name']] = n['value'];
                }
            });

            return indexed_array;
        }
    });
});
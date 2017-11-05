define('PreviewResultsView', ['View'], function (View) {
    var PreviewResultsView = function (controller) {
        View.call(this, controller, 'previewResults');
    };

    // Inheritance from super class
    PreviewResultsView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    PreviewResultsView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);
        var transName = MainModule.controllers.HeaderController.transName;
        var executionId = MainModule.controllers.HeaderController.executionId;
        this.$elements.preview_title.text("Preview "+transName);

        // Request for transformation results.
        var context = this;
        $.get("/graph/result",{execution: executionId},
            function(data){
                console.log(data);
                context.$elements.preview_log.append(JSON.parse(data)['log']);

                context.$elements.preview_steps_measures.empty();
                var $tr = $('<tr>').append(
                    $('<th>').text("Step Name"),
                    $('<th>').text("Number of records"),
                    $('<th>').text("Read"),
                    $('<th>').text("Write"),
                    $('<th>').text("Enter"),
                    $('<th>').text("Output"),
                    $('<th>').text("Update"),
                    $('<th>').text("Refuse"),
                    $('<th>').text("Error"),
                    $('<th>').text("State"),
                    $('<th>').text("Time"),
                    $('<th>').text("Speed (note/second)"),
                    $('<th>').text("Pri/in/out")
                );
                context.$elements.preview_steps_measures.append($tr[0]);

                var rows = JSON.parse(data)['stepMeasure'];
                for(var i=0; i<rows.length; i++){
                    var item = rows[i];
                    var $tr = $('<tr>').append(
                        $('<td>').text(item[0]),
                        $('<td>').text(item[1]),
                        $('<td>').text(item[2]),
                        $('<td>').text(item[3]),
                        $('<td>').text(item[4]),
                        $('<td>').text(item[5]),
                        $('<td>').text(item[6]),
                        $('<td>').text(item[7]),
                        $('<td>').text(item[8]),
                        $('<td>').text(item[9]),
                        $('<td>').text(item[10]),
                        $('<td>').text(item[11]),
                        $('<td>').text(item[12])
                    );
                    context.$elements.preview_steps_measures.append($tr[0]);
                }
            }
        );
    };

    return PreviewResultsView;
});
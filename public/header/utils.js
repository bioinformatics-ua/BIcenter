/**
 *  Allows to write a transformation xml, to be converted in a mxGraph.
 */
function importXml(){
    $('#source').click();
    $('#board').css({'height': '75vh'});
    $('#xmlBtn').css("display","block");
}

/**
 *  Returns the steps of the actual transformation.
 */
var global_nodes;
function getSteps(){
    $('#source').click();
    var transXml = $($('#xml')[0]).val();
    $('#source').click();

    var node = (new DOMParser()).parseFromString(transXml, "text/xml").documentElement;

    var nodes = node.querySelectorAll("*");

    $("#steps").empty();
    for (var i = 0; i < nodes.length; i++) {
        // Append the transaction node to the nodes list.
        if (nodes[i].tagName == "Step" && nodes[i].hasAttribute('name')){
            var transName = nodes[i].getAttribute("name");
            var trans = nodes[i]['id'] + ": " + transName;
            var first_method = "setGlobalStep('"+i+"')";
            var second_method = "transSettingsDialog()";

            var item = '<li class="dropdown-submenu">';
            item += '<a tabindex="-1" href="#">';
            item += trans;
            item += '</a>';
            item += '<ul class="dropdown-menu">';
            item += '<li><a tabindex="-1" href="#" onclick="';
            item += first_method;
            item += ";";
            item += second_method;
            item += '">Edit Transformation</a></li>';
            item += '</ul>';
            item += '</li>';

            $("#steps").append(item);
        }
        // Append each step to the nodes list.
        else if (nodes[i].tagName == "Step" && nodes[i].hasAttribute('ctype')) {
            var stepName = nodes[i].getAttribute("label");
            var step = nodes[i].getAttribute("id") + ": " + stepName;
            var first_method = "inputOutputFields('"+stepName+"',"+true+")";
            var second_method = "inputOutputFields('"+stepName+"',"+false+")";
            var third_method = "stepDialog('"+nodes[i].getAttribute('ctype')+"')";
            var fourth_method = "setGlobalStep('"+i+"')";

            var item = '<li class="dropdown-submenu">';
            item += '<a tabindex="-1" href="#">';
            item += step;
            item += '</a>';
            item += '<ul class="dropdown-menu">';
            item += '<li><a tabindex="-1" href="#" onclick="';
            item += fourth_method;
            item += ";";
            item += third_method;
            item += '">Edit Step</a></li>';
            item += '<li><a tabindex="-1" href="#">Edit Step Description</a></li>';
            item += '<li class="divider"></li>';
            item += '<li><a tabindex="-1" href="#" onclick="'
            item += first_method;
            item += '">Show Input Fields</a></li>';
            item += '<li><a tabindex="-1" href="#" onclick="'
            item += second_method;
            item += '">Show Output Fields</a></li>';
            item += '</ul>';
            item += '</li>';

            $("#steps").append(item);
        }

        global_nodes = nodes;
    }
}

/**
 *  Returns the input or the output fields of a given step.
 */
function inputOutputFields(stepName,before){
    var enc = new mxCodec(mxUtils.createXmlDocument());
    var node = enc.encode(global_editor.graph.getModel())
    $.post('/step/inputOutputFields', { stepName:stepName, graph: mxUtils.getPrettyXml(node), before:before },
        function(returnedData){
            $('#main_page').hide();

            $('#fields_header').empty();
            $('#fields_header').append( "Step fields and its source: <strong>"+stepName+"</strong>" );

            $("#fields_table_body").empty();
            $("#fields_error").empty();

            if(returnedData.length == 0){
                $("#fields_error").text(stepName+" is the first step!");
            }
            else {
                var header = "<tr id='fields_table_header'>";
                var columns = Object.keys(returnedData[0]);
                for (var i = 0; i < columns.length; i++) {
                    header += "<th>" + columns[i] + "</th>";
                }
                header += "</tr>"
                $('#fields_table_body').append(header);

                for (var i = 0; i < returnedData.length; i++) {
                    var values = Object.values(returnedData[i]);
                    var row = "<tr>";
                    for (var j = 0; j < values.length; j++) {
                        row += "<td>" + values[j] + "</td>";
                    }
                    row += "</tr>";
                    $('#fields_table_body').append(row);
                }
            }

            $('#fields_page').show();
        });
}

/**
 * Sets the current select step.
 */
var global_step;
function setGlobalStep(i){
    global_step = global_nodes[i];
}

/**
 * Shows the transaction settings configuration dialog, for the current transaction.
 */
function transSettingsDialog(){
    $('#main_page').hide();
    $('#TransSettings').show(
        function () {
            $($("#trans_settings_name")[0]).val(global_step.getAttribute("name"));
            $($("#trans_settings_file")[0]).val(global_step.getAttribute("fileName"));
            $($("#trans_settings_description")[0]).val(global_step.getAttribute("description"));
            $($("#trans_settings_status")[0]).val(global_step.getAttribute("trans_status"));
            $($("#trans_settings_version")[0]).val(global_step.getAttribute("trans_version"));
            $($("#trans_settings_directory")[0]).val(global_step.getAttribute("trans_directory"));
            $($("#trans_settings_creator")[0]).val(global_step.getAttribute("created_user"));
            $($("#trans_settings_date")[0]).val(global_step.getAttribute("created_date"));
            $($("#trans_settings_modified_user")[0]).val(global_step.getAttribute("modified_user"));
            $($("#trans_settings_modified_date")[0]).val(global_step.getAttribute("modified_date"));
        }
    );
}

/**
 * Shows the step configuration dialog, for a specific step type.
 * @param stepType
 */
function stepDialog(stepType){
    $('#main_page').hide();

    if(stepType == "RowGenerator") {
        $('#RowGenerator').show(
            function () {
                $($("#rowgen_step_name")[0]).val(global_step.getAttribute("label"));
                $($("#rowgen_step_limit")[0]).val(global_step.getAttribute("rowLimit"));

                if(global_step.getAttribute("neverEnding") == "Y"){
                    $('#rowgen_never_ending').prop( "checked", true );
                }
                else{
                    $('#rowgen_never_ending').prop( "checked", false );
                }

                $('#rowgenerator_table_body').empty();

                var $tr = $('<tr>').append(
                    $('<th>').text("name"),
                    $('<th>').text("type"),
                    $('<th>').text("format"),
                    $('<th>').text("length"),
                    $('<th>').text("precision"),
                    $('<th>').text("currency"),
                    $('<th>').text("decimal"),
                    $('<th>').text("grouping"),
                    $('<th>').text("value"),
                    $('<th>').text("Set empty String?")
                );
                $('#rowgenerator_table_body').append($tr[0]);

                var rows = $.parseJSON(global_step.getAttribute("fields"));
                for(var i=0; i<rows.length; i++){
                    var item = rows[i];
                    var $tr = $('<tr>').append(
                        $('<td>').text(item.name),
                        $('<td>').text(item.type),
                        $('<td>').text(item.format),
                        $('<td>').text(item.length),
                        $('<td>').text(item.precision),
                        $('<td>').text(item.currency),
                        $('<td>').text(item.decimal),
                        $('<td>').text(item.grouping),
                        $('<td>').text(item.value),
                        $('<td>').text(item.nullable)
                    );
                    $('#rowgenerator_table_body').append($tr[0]);
                }
            }
        );
    }
    else if(stepType == "CheckSum") {
        $("#Checksum").show(
            function(){
                $($("#checksum_step_name")[0]).val(global_step.getAttribute("label"));

                var index = global_step.getAttribute('checkSumType');
                $('#checksum_type option').eq(index).prop('selected', true);

                var index = global_step.getAttribute('resultType');
                $('#checksum_result_type option').eq(index).prop('selected', true);

                $($("#checksum_result_field")[0]).val(global_step.getAttribute("resultfieldName"));

                if(global_step.getAttribute("compatibilityMode") == "true"){
                    $('#checksum_mode').prop( "checked", true );
                }
                else{
                    $('#checksum_mode').prop( "checked", false );
                }

                var enc = new mxCodec(mxUtils.createXmlDocument());
                var node = enc.encode(global_editor.graph.getModel())
                $.post('/step/inputFieldsName', { stepName:global_step.getAttribute("label"), graph: mxUtils.getPrettyXml(node) },
                    function(returnedData){
                        var fields = Object.values(returnedData);
                        $('#checksum_fields select').empty();

                        var checksum_fields = Object.values(JSON.parse(global_step.getAttribute("fields")));
                        var names = [];
                        for(var i=0; i<checksum_fields.length; i++){
                            names.push(checksum_fields[i]['name']);
                        }

                        for(var i=0; i<fields.length; i++) {
                            var option = '<option ';
                            if (names.indexOf(fields[i]['name']) >= 0)
                                option += 'selected';
                            option += '>';
                            option += fields[i]['name'];
                            option += '</option>';
                            $('#checksum_fields select').append(option);
                        }
                    }
                );
            }
        );
    }
    else if(stepType == "Sequence") {
        $("#Sequence").show(
            function () {
                $($("#sequence_step_name")[0]).val(global_step.getAttribute("label"));
                $($("#checksum_value_name")[0]).val(global_step.getAttribute("valuename"));

                if(global_step.getAttribute("use_database") == "Y"){
                    $('#sequence_database').prop( "checked", true );
                }
                else{
                    $('#sequence_database').prop( "checked", false );
                }

                if(global_step.getAttribute("use_counter") == "Y"){
                    $('#sequence_counter').prop( "checked", true );
                }
                else{
                    $('#sequence_counter').prop( "checked", false );
                }
                $($("#sequence_counter_name")[0]).val(global_step.getAttribute("counter_name"));
                $($("#sequence_start_value")[0]).val(global_step.getAttribute("start_at"));
                $($("#sequence_increment")[0]).val(global_step.getAttribute("increment_by"));
                $($("#sequence_max_value")[0]).val(global_step.getAttribute("max_value"));
            }
        )
    }
}

/**
 *  Calls the API that runs the transformation.
 */
function runTransformation(method){
    var enc = new mxCodec(mxUtils.createXmlDocument());
    var node = enc.encode(global_editor.graph.getModel())

    var exec_method = new Object();
    exec_method.execMethod = method;

    var details = new Object();

    if($('#runoptions_safemode').is('checked'))
        details.safeModeEnabled = "on"
    if($('#runoptions_performance').is(':checked'))
        details.gatheringMetrics = "on";
    if($('#runoptions_clear_log').is(':checked'))
        details.clearingLog = "on";
    details.logLevel = $("#runoptions_log_level").prop('selectedIndex');
    details.parameters = global_editor['graph'].getDefaultParent().getAttribute('parameters');
    details.variables = global_editor['graph'].getDefaultParent().getAttribute('variables');

    var execution = new Object();
    execution.executeMethod = exec_method;
    execution.details = details
    var execution_configuration= JSON.stringify(execution);

    $.post('/graph/run', { graph: mxUtils.getPrettyXml(node), execution:execution_configuration },
        function(returnedData){
            console.log(returnedData);
        });
}

/**
 * Shows the run settings configuration dialog.
 */
function showRunOptions(){
    $('#main_page').hide();
    $('#RunOptions').show(
        function(){
            $('#runoptions_table_parameters').empty();

            var $tr = $('<tr>').append(
                $('<th>').text("Parameter"),
                $('<th>').text("Value"),
                $('<th>').text("Defaults")
            );
            $('#runoptions_table_parameters').append($tr[0]);

            var rows = $.parseJSON(global_editor['graph'].getDefaultParent().getAttribute('parameters'));
            for(var i=0; i<rows.length; i++) {
                var item = rows[i];
                var $tr = $('<tr>').append(
                    $('<td>').text(item.param_name),
                    $('<td>').text(item.param_value),
                    $('<td>').text(item.param_defaults)
                );
                $('#runoptions_table_parameters').append($tr[0]);
            }

            $('#runoptions_table_variables').empty();

            var $tr = $('<tr>').append(
                $('<th>').text("Variable"),
                $('<th>').text("Value")
            );
            $('#runoptions_table_variables').append($tr[0]);

            var rows = $.parseJSON(global_editor['graph'].getDefaultParent().getAttribute('variables'));
            for(var i=0; i<rows.length; i++) {
                var item = rows[i];
                var $tr = $('<tr>').append(
                    $('<td>').text(item.var_name),
                    $('<td>').text(item.var_value)
                );
                $('#runoptions_table_variables').append($tr[0]);
            }
        }
    );
}

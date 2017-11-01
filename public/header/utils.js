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
        if (nodes[i].tagName == "Step" && nodes[i].hasAttribute('ctype')) {
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

var global_step;
function setGlobalStep(i){
    global_step = global_nodes[i];
}

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
}

/**
 *  Calls the API that runs the transformation.
 */
function runTransformation(){
    var enc = new mxCodec(mxUtils.createXmlDocument());
    var node = enc.encode(global_editor.graph.getModel())

    $.post('/graph/run', { graph: mxUtils.getPrettyXml(node), execution:"" },
        function(returnedData){
            console.log(returnedData);
        });
}
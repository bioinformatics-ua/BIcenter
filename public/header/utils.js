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

            var item = '<li class="dropdown-submenu">';
            item += '<a tabindex="-1" href="#">';
            item += step;
            item += '</a>';
            item += '<ul class="dropdown-menu">';
            item += '<li><a tabindex="-1" href="#">Edit Step</a></li>';
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
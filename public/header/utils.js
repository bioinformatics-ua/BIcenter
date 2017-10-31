function importXml(){
    $('#source').click();
    $('#board').css({'height': '75vh'});
    $('#xmlBtn').css("display","block");
}

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

function inputOutputFields(stepName,before){
    var enc = new mxCodec(mxUtils.createXmlDocument());
    var node = enc.encode(global_editor.graph.getModel())
    $.post('/step/inputOutputFields', { stepName:stepName, graph: mxUtils.getPrettyXml(node), before:before },
        function(returnedData){
            console.log(returnedData);
        });
}

function runTransformation(){
    var enc = new mxCodec(mxUtils.createXmlDocument());
    var node = enc.encode(global_editor.graph.getModel())

    $.post('/graph/run', { graph: mxUtils.getPrettyXml(node), execution:"" },
        function(returnedData){
            console.log(returnedData);
        });
}
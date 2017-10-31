function importXml(){
    $('#source').click();
    $('#board').css({'height': '75vh'});
    $('#xmlBtn').css("display","block");
}

function runTransformation(){
    var enc = new mxCodec(mxUtils.createXmlDocument());
    var node = enc.encode(global_editor.graph.getModel())

    $.post('/graph/run', { graph: mxUtils.getPrettyXml(node), execution:"" },
        function(returnedData){
            console.log(returnedData);
        });
}
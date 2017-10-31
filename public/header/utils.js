function importXml(){
    document.getElementById('source').click();
    document.getElementById('board').style.height = '75vh';
    document.getElementById('xmlBtn').style.display = 'block';
}

function runTransformation(){
    var enc = new mxCodec(mxUtils.createXmlDocument());
    var node = enc.encode(global_editor.graph.getModel())

    $.post('/graph/run', { graph: mxUtils.getPrettyXml(node), execution:"" },
        function(returnedData){
            console.log(returnedData);
        });
}
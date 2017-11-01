/**
 * Returns back to the pipeline, after XML import.
 */
function loadXml(){
    $('#source').click();
    $('#board').css({'height': '80vh'});
    $('#xmlBtn').css("display","none");
}

/**
 * Returns back to the transformation pipeline.
 */
function return2Pipeline(page){
    $('#'+page).hide();
    $('#main_page').show();
}
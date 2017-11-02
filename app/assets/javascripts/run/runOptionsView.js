define('RunOptionsView', ['View'], function (View) {
    var RunOptionsView = function (controller) {
        View.call(this, controller, 'runOptions');
    };

    // Inheritance from super class
    RunOptionsView.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    RunOptionsView.prototype.initialize = function ($container) {
        _super_.initialize.call(this, $container);

        $('#runoptions_cancel')[0].onclick =
            function () {
                $('#RunOptions').hide();
                $('#main_page').show();
            }
    };

    RunOptionsView.prototype.testClick = function () {
        console.log("Testing clicks from view");

        //var template = JST[]();
        //this.$elements.container.html(template);
        //this._loadViewComponents();
    };

    return RunOptionsView;
});
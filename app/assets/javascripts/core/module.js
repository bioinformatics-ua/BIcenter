define('Module', ['jquery', 'underscore', 'Utils'], function ($, _, Utils) {
    var Module = function (name) {
        this.name = name;
        this.$container = null;
        this.controllers = {};
    };

    Module.prototype.initialize = function ($container) {
        this.$container = $container;
        var self = this, v;


        if ((v = this.$container.attr('controller'))) {
            self._setController(this.$container, v);
        }

        this.$container.find('[controller]').each(function () {
            var $element = $(this);
            var name = $element.attr('controller');

            self._setController($element, name);
        });
    };

    Module.prototype.loadController = function ($controller) {
        var self = this, v;

        if ((v = $controller.attr('controller'))) {
            self._setController($controller, v);
        }
    };

    Module.prototype.destroy = function () {
        _.each(this.controllers, function (controller) {
            controller.destroy();
        });

        if (this.$container) {
            this.$container.off();
            this.$container.empty();
        }
        this.$container = null;
        delete this.$container;
    };

    Module.prototype._setController = function ($element, name) {
        var self = this;

        require(
            [name],
            function (c) {
                // Create controller
                var controller = new c(self);

                // Get init variables
                var v;
                if ((v = $element.attr('controller-init'))) {
                    self._setKeyValuePairs(v, controller);
                }

                controller.initialize($element);
                self.controllers[name] = controller;
            },
            function (err) {
                console.error(err);
                throw new Error('Controller ' + name + ' not found.');
            }
        );
    };

    Module.prototype._setKeyValuePairs = function (text, controller) {
        var pairs = Utils.getKeyValuePairs(text);
        _.each(pairs, function (pair) {
            controller[pair.key] = pair.value;
        });
    };

    return Module;
});

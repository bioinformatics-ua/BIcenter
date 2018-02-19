define('View', ['jquery', 'underscore', 'Utils', 'messages', 'templates'], function ($, _, Utils, Messages) {
    var View = function (controller, template) {
        this.controller = controller;
        this.template = template;
        this.$container = null;
        this.$elements = {}
    };

    View.prototype.initialize = function ($container, data) {
        // Set container
        this.$container = $container;

        // Add view template
        if (this.template) {
            // Append messages to data
            data = data || {};
            data.messages = Messages.messages;

            // Render template
            this.$container.html(JST[this.template](data));
        }

        // Get view components
        this._loadViewComponents();
    };

    View.prototype._loadViewComponents = function () {
        var self = this;
        var v;

        this.$elements = this.$elements || {};

        if ((v = this.$container.attr('view-init'))) {
            this._setKeyValuePairs(v);
        }

        this.$container.find('[view-init]').each(function () {
            self._setKeyValuePairs(name);
        });

        this.$container.find('[view-element]').each(function () {
            var $element = $(this);
            var name = $element.attr('view-element');
            self.$elements[name] = $element;

            // Clean up to avoid conflicts
            $element.removeAttr('view-element');
        });

        this.$container.find('[view-click]').each(function () {
            var $element = $(this);
            var functionCall = $element.attr('view-click');

            var json = self._getFunctionContextNameAndArguments(functionCall);
            var name = json.name;
            var args = json.args;
            var context = json.context;

            if ($element.is('a')) {
                $element.attr('href', 'javascript:void(0)');
            }

            $element.off('click');
            $element.on('click', function (event) {
                //context[name](args);
                args.push(event);
                args.push($element);
                context[name].apply(context, args);
            });

            // Clean up to avoid conflicts
            $element.removeAttr('view-click');
        });

        this.$container.find('[view-change]').each(function () {
            var $element = $(this);
            var functionCall = $element.attr('view-change');

            var json = self._getFunctionContextNameAndArguments(functionCall);
            var name = json.name;
            var args = json.args;
            var context = json.context;

            $element.off('change');
            $element.on('change', function (event) {
                args.push($element);
                context[name].apply(context, args);
            });

            // Clean up to avoid conflicts
            $element.removeAttr('view-change');
        });

        this.$container.find('[view-submit]').each(function () {
            var $element = $(this);
            var functionCall = $element.attr('view-submit');

            var json = self._getFunctionContextNameAndArguments(functionCall);
            var name = json.name;
            var args = json.args;
            var context = json.context;

            $element.attr('action', 'javascript:void(0);');

            $element.off();
            $element.on('submit', function (event) {
                if (event.preventDefault())
                    event.preventDefault();
                if (event.stopPropagation())
                    event.stopPropagation();
                if (event.stopImmediatePropagation())
                    event.stopImmediatePropagation();

                // Validate form if possible
                var bv = $element.data('bootstrapValidator');
                if (bv) {
                    var valid = bv.isValid();
                    if (valid == false) {
                        bv.validate();
                        if (!bv.isValid()) {
                            return false;
                        }
                    }
                }
                //context[name](args);
                context[name].apply(context, args);
                return false;
            });

            // Press Enter to submit form
            $element.on('keypress', function (event) {
                // Enter
                if (event.keyCode == 13) {
                    if (event.preventDefault())
                        event.preventDefault();
                    if (event.stopPropagation())
                        event.stopPropagation();
                    if (event.stopImmediatePropagation())
                        event.stopImmediatePropagation();

                    $element.submit();
                }
            });

            // Clean up to avoid conflicts
            $element.removeAttr('view-submit');
        });

        this.$container.find('[controller]').each(function () {
            var $element = $(this);
            var name = $element.attr('controller');

            self.controller.module._setController($element, name);
        });
    };

    View.prototype._setKeyValuePairs = function (text) {
        var self = this;

        var pairs = Utils.getKeyValuePairs(text);
        _.each(pairs, function (pair) {
            self[pair.key] = pair.value;
        });
    };

    View.prototype.show = function () {
        this.$container.removeClass('hidden');
        this.$container.show();
    };

    View.prototype.hide = function () {
        this.$container.addClass('hidden');
        this.$container.hide();
    };

    View.show = function ($element) {
        $element.removeClass('hidden');
        $element.show();
    };
    View.hide = function ($element) {
        $element.addClass('hidden');
        $element.hide();
    };

    View.prototype._getFunctionContextNameAndArguments = function (functionCall) {
        var name = functionCall;
        var args = [];
        var context = null;

        if (functionCall.indexOf('view.') > -1) {
            context = this;
        } else if (functionCall.indexOf('controller.') > -1) {
            context = this.controller;
        }

        if (functionCall.indexOf('(') > -1) {

            var idx = 2;
            var nameRegex = /(view|controller)\.(.*)\(/g;
            if (_.isNull(context)) {
                idx = 1;
                nameRegex = /(.*)\(/g;
            }

            // Get name
            var matches = nameRegex.exec(functionCall);
            name = matches[idx];

            // Get arguments
            var argumentsRegex = /\((.*)\)/g;
            matches = argumentsRegex.exec(functionCall);
            var argumentsText = matches[1];

            if (argumentsText) {
                args = argumentsText.split(',');
            }

            args = _.map(args, function (arg) {
                return arg.trim();
            })
        }

        // Check if functions exists on view or context
        if (_.isNull(context)) {
            if (_.isFunction(this[name])) {
                context = this;
            } else if (_.isFunction(this.controller[name])) {
                context = this.controller;
            } else {
                throw new Error('Function ' + name + ' does not exist.');
            }
        }

        return {
            context: context,
            name: name,
            args: args
        };
    };

    View.prototype.destroy = function () {
        this.controller = null;
        delete this.controller;

        this.template = null;
        delete this.template;

        _.each(this.$elements, function ($el) {
            $el.off();
            $el.empty();
        });
        this.$elements = null;
        delete this.$elements;

        if (this.$container) {
            this.$container.off();
            this.$container.empty();
        }
        this.$container = null;
        delete this.$container;
    };

    return View;
});
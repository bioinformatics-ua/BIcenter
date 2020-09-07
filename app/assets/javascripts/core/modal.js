define('Modal', ['View', 'jquery', 'underscore', 'messages', 'bootbox', 'templates'], function (View, $, _, Messages, bootbox) {
    var Modal = function (controller, template, title) {
        View.call(this, controller, template);
        this.title = title;
    };

    // Inheritance from super class
    Modal.prototype = Object.create(View.prototype);
    var _super_ = View.prototype;

    Modal.prototype.initialize = function ($container) {
        // initalize modal
        this.initModal();

        // Set container
        this.$container = this.modal;

        // load components
        this._loadViewComponents();
    };

    Modal.prototype.initModal = function () {
        var self = this;

        // Set data
        var data = {
            messages: Messages.messages
        };

        // Init and show modal
        this.modal = bootbox.dialog({
            title: this.title,
            message: JST[this.template](data),
            show: false,
            // className: 'modal-large',
            buttons: {
                close: {
                    label: '<i class="fa fa-times-circle"></i> Close',
                    className: 'btn-default',
                    callback: function () {
                        self.hide();
                        return false;
                    }
                }
            },
            /* in order to dismiss dialog on escape key. (lib bug) */
            onEscape: function () {
            },
            backdrop: 'static'
        });

        this.modal.off('hidden.bs.modal');
        this.modal.on('hide.bs.modal', function (e) {
            // Prevent misbehaviour when closing 'datepicker' inside modal
            if ($(e.currentTarget).is($(e.target))) {
                var f;
                if ((f = $(this).find('form'))) {
                    f.bootstrapValidator && f.bootstrapValidator('resetForm', true);
                }
            }
        });
    };

    Modal.prototype.addTitle = function (title) {
        let $title = this.modal.find(".modal-title");
        $title.text(title);
    }

    Modal.prototype.addButtons = function (buttons) {
        var $footer = this.modal.find('.modal-footer');

        // Stores initialized buttons, to be added to footer in order.
        var $btns = [];
        _.each(buttons, function (button) {
            // Create button
            var $btn = $('<a class="btn ' + button.className + '">' + button.label + '</a>');

            // Add callback
            if (button.callback) {
                $btn.attr('view-click', button.callback);
            }

            // Add to array of initialized buttons
            $btns.push($btn);
        });

        // Append to footer
        $footer.prepend($btns);

        this._loadViewComponents();
    };

    Modal.prototype.show = function () {
        if (!this.modal) {
            // Init and show modal
            this.initModal();
        } else {
            this.modal.modal('show');
        }
    };

    Modal.prototype.hide = function () {
        if (this.modal) {
            // Hide modal
            this.modal.modal('hide');
        }
    };

    return Modal;
});
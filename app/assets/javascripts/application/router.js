define('Router', [], function () {
    var Router = Router || {
        routes: [],
        mode: null,
        root: '/',
        active: false,

        clearSlashes: function (path) {
            return path.toString().replace(/\/$/, '').replace(/^\//, '');
        },
        config: function (options) {
            this.mode = options && options.mode && options.mode == 'history' && !!(history.pushState) ? 'history' : 'hash';
            this.root = options && options.root ? '/' + this.clearSlashes(options.root) + '/' : '/';
            return this;
        },
        getFragment: function () {
            var fragment = '';
            if (this.mode === 'history') {
                fragment = this.clearSlashes(decodeURI(location.pathname + location.search));
                fragment = fragment.replace(/\?(.*)$/, '');
                fragment = this.root != '/' ? fragment.replace(this.root, '') : fragment;
            } else {
                var match = window.location.href.match(/#(.*)$/);
                fragment = match ? match[1] : '';
            }
            return this.clearSlashes(fragment);
        },
        add: function (re, handler) {
            if (typeof re == 'function') {
                handler = re;
                re = '';
            }
            this.routes.push({re: re, handler: handler});
            return this;
        },
        remove: function (param) {
            for (var i = 0, r; i < this.routes.length, r = this.routes[i]; i++) {
                if (r.handler === param || r.re.toString() === param.toString()) {
                    this.routes.splice(i, 1);
                    return this;
                }
            }
            return this;
        },
        flush: function () {
            this.routes = [];
            this.mode = null;
            this.root = '/';
            this.active = false;
            return this;
        },
        check: function (f) {
            var fragment = f || this.getFragment();
            for (var i = 0; i < this.routes.length; i++) {
                var match = fragment.match(this.routes[i].re);
                if (match) {
                    match.shift();
                    this.routes[i].handler.apply({}, match);
                    return this;
                }
            }
            return this;
        },
        listen: function () {
            this.active = true;
            var self = this;

            if (this.mode === 'history') {
                window.addEventListener('popstate', function () {
                    self.check();
                });
            } else {
                var current = self.getFragment(),
                    fn = function () {
                        if (current !== self.getFragment()) {
                            current = self.getFragment();
                            self.check(current);
                        }
                    };

                clearInterval(this.interval);
                this.interval = setInterval(fn, 50);
            }

            return this;
        },
        navigate: function (path) {
            path = path ? path : '';
            if (this.mode === 'history') {
                history.pushState(null, null, this.root + this.clearSlashes(path));
                this.check();
            } else {
                window.location.href = window.location.href.replace(/#(.*)$/, '') + '#' + path;
            }
            return this;
        },
        navigatePrevious: function () {
            if (this.mode === 'history') {
                history.back();
            }
        },
        updatePageLinks: function () {
            var self = this;

            if (typeof document === 'undefined') return;

            var links = [].slice.call(document.querySelectorAll('[view-router]'));
            links.forEach(function (link) {
                if (!link.hasListenerAttached) {
                    link.addEventListener('click', function (e) {
                        var path = link.pathname || link.getAttribute('href');

                        if (self.active) {
                            e.preventDefault();
                            self.navigate(self.clearSlashes(path));
                        }
                    });
                    link.hasListenerAttached = true;
                }
            });
        }
    };

    return Router;
});
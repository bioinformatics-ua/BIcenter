requirejs.config({
    baseUrl: '/assets/javascripts',
    paths: {
        'templates': '../templates',

        // Libs
        'requirejs': '../../requirejs/require',
        'jquery': '../lib/jquery/jquery',
        'jquery.slimscroll': '../lib/slimscroll/jquery.slimscroll.min',
        'underscore': '../lib/underscorejs/underscore-min',
        'bootstrap': '../lib/bootstrap/js/bootstrap',
        'adminLTE': '../lib/adminLTE/js/app.min',
        'handlebars.runtime': '../lib/handlebars/handlebars.runtime',
        'handlebarsHelpers': 'core/handlebarsHelpers',
        'editor': '../editor/editor',
        'bootbox': 'lib/bootbox.min',
        'datatables.net': '../lib/datatables/jquery.dataTables.min',
        'dataTables': '../lib/datatables/dataTables.bootstrap.min',
        'pnotify': '../lib/pnotify/pnotify.custom',
        'pnotify.nonblock': '../lib/pnotify/pnotify.nonblock',
        'Alert': 'lib/alert',

        // Core
        'Application': 'application/application',
        'Router': 'application/router',
        'Module': 'core/module',
        'Controller': 'core/controller',
        'View': 'core/view',
        'Modal': 'core/modal',
        'Color': 'core/color',
        'custom.jquery': 'core/custom.jquery',
        'Utils': 'core/utils',
        'jsRoutes': 'jsroutes',

        // services
        'Execution': 'services/execution',
        'Step': 'services/step',
        'Task': 'services/task',

        // Header
        'HeaderController': 'header/headerController',
        'HeaderView': 'header/headerView',

        // Main Container
        'ContainerController': 'container/containerController',
        'ContainerView': 'container/containerView',

        // Graph
        'GraphController': 'graph/graphController',
        'GraphView': 'graph/graphView',

        // Input and Ouput fields Dialog
        'FieldsController': 'steps/fields/fieldsController',
        'FieldsView': 'steps/fields/fieldsView',

        // Run Options Dialog
        'RunOptionsController': 'run/runOptionsController',
        'RunOptionsView': 'run/runOptionsView',

        // Edit Transformation Dialog
        'TransSettingsController': 'trans/transSettingsController',
        'TransSettingsView': 'trans/transSettingsView',

        // Edit Step Dialogs
        'StepController': 'step/stepController',
        'StepView': 'step/stepView',

        'SampleModalController': 'step/sampleModalController',
        'SampleModalView': 'step/sampleModalView',

        // Fields Step page
        'FieldsController': 'fields/fieldsController',
        'FieldsView': 'fields/fieldsView',

        // PreviewResults
        'PreviewResultsController': 'previewResults/previewResultsController',
        'PreviewResultsView': 'previewResults/previewResultsView',

        // Result
        'ResultController': 'result/resultController',
        'ResultView': 'result/resultView',

        // Sidebar
        'SidebarController': 'sidebar/controllers/sidebarController',
        'SidebarView': 'sidebar/views/sidebarView'
    },
    shim: {
        'messages': {
            exports: 'Messages'
        },
        'jsRoutes': {
            deps: [],
            // it's not a RequireJS module, so we have to tell it what var is returned
            exports: 'jsRoutes'
        },
        'bootstrap': {
            deps: ['jquery']
        },
        'handlebars.runtime': {
            exports: 'Handlebars'
        },
        'underscore': {
            deps: ['jquery'],
            exports: '_'
        },
        'underscore.deepclone': {
            deps: ['underscore'],
            exports: '_'
        },
        'jquery.slimscroll': {
            deps: ['jquery']
        },
        'adminLTE': {
            deps: ['jquery', 'bootstrap', 'jquery.slimscroll']
        },
        'datatables.net': {
            deps: ['jquery', 'bootstrap']
        },
        'dataTables': {
            deps: ['jquery', 'bootstrap']
        },
        'templates': {
            deps: ['handlebarsHelpers']
        },
        'pnotify': {
            exports: 'PNotify'
        }
    }
});

var DEBUG = true;


require(['Application'], function (Application) {
    window.app = new Application();
});
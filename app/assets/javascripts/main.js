requirejs.config({
    baseUrl: '/assets/javascripts',
    paths: {
        'templates': '../templates',

        // Libs
        'requirejs': '../../requirejs/require',
        'jquery': '../lib/jquery/jquery',
        'jquery.slimscroll': '../lib/slimscroll/jquery.slimscroll',
        'underscore': '../lib/underscorejs/underscore-min',
        'bootstrap': '../lib/bootstrap/js/bootstrap',
        'adminLTE': '../lib/adminLTE/js/app.min',
        'handlebars.runtime': '../lib/handlebars/handlebars.runtime',
        'handlebarsHelpers': 'core/handlebarsHelpers',
        'editor': '../editor/editor',
        'bootbox': 'lib/bootbox.min',
        'datatables.buttons': '../lib/datatables/dataTables.buttons.min',
        'datatables.net': '../lib/datatables/jquery.dataTables.min',
        'dataTables': '../lib/datatables/dataTables.bootstrap.min',
        'pnotify': '../lib/pnotify/pnotify',
        'pnotify.nonblock': '../lib/pnotify/pnotify.nonblock',
        'Alert': 'lib/alert',
        'moment': 'lib/moment',
        'async': 'lib/async.min',
        'jquery-cookie': 'lib/jquery.cookie.min',
        'md5': '../lib/md5/md5.min',

        // QueryBuilder
        'query-builder': '../lib/jQuery-QueryBuilder/js/query-builder.min',
        'jquery-extendext': '../lib/jquery-extendext/jQuery.extendext.min',
        'dot/doT': '../lib/doT/doT',

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
        'jquery-ui': '../lib/jquery-ui/jquery-ui',

        // services
        'Institution': 'services/institution',
        'Execution': 'services/execution',
        'Step': 'services/step',
        'Task': 'services/task',
        'User': 'services/user',

        'LoginController': 'login/controllers/loginController',
        'LoginView': 'login/views/loginView',

        // Header
        'HeaderController': 'header/headerController',
        'HeaderView': 'header/headerView',

        // Main Container
        'ContainerController': 'container/containerController',
        'ContainerView': 'container/containerView',

        // Graph
        'GraphController': 'graph/graphController',
        'GraphView': 'graph/graphView',

        // Remote Execution
        'RemoteExecutionController': 'run/remoteExecutionController',
        'RemoteExecutionView': 'run/remoteExecutionView',

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

        'StepModalController': 'step/stepModalController',
        'StepModalView': 'step/stepModalView',

        // Fields Step page
        'FieldsController': 'fields/fieldsController',
        'FieldsView': 'fields/fieldsView',

        // PreviewResults
        'PreviewResultsController': 'previewResults/previewResultsController',
        'PreviewResultsView': 'previewResults/previewResultsView',

        // Breadcrumb
        'BreadcrumbController': 'breadcrumb/breadcrumbController',
        'BreadcrumbView': 'breadcrumb/breadcrumbView',

        // Executions History
        'HistoryController': 'history/historyController',
        'HistoryView': 'history/historyView',

        // Execution Logs
        'LogsController': 'logs/logsController',
        'LogsView': 'logs/logsView',

        // Execution Steps Metrics
        'StepMetricsController': 'stepMetrics/stepMetricsController',
        'StepMetricsView': 'stepMetrics/stepMetricsView',

        // Execution Preview Data
        'PreviewDataController': 'previewData/previewDataController',
        'PreviewDataView': 'previewData/previewDataView',

        'PreviewStepController': 'previewStep/previewStepController',
        'PreviewStepView': 'previewStep/previewStepView',

        // Result
        'ResultController': 'result/resultController',
        'ResultView': 'result/resultView',

        // Sidebar
        'SidebarController': 'sidebar/controllers/sidebarController',
        'SidebarView': 'sidebar/views/sidebarView',

        // Server Settings Page
        'ServerController': 'sidebar/controllers/serverController',
        'ServerView': 'sidebar/views/serverView',
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
        'datatables.buttons': {
            deps: ['jquery', 'bootstrap']
        },
        'datatables.net': {
            deps: ['jquery', 'bootstrap']
        },
        'dataTables': {
            deps: ['jquery', 'bootstrap', 'datatables.buttons']
        },
        'templates': {
            deps: ['handlebarsHelpers']
        }
    }
});

var DEBUG = true;


require(['Application'], function (Application) {
    window.app = new Application();
});
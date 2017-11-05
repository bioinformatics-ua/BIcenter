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
        'editor':'../editor/editor',

        // Core
        'Module': 'core/module',
        'Controller': 'core/controller',
        'View': 'core/view',
        'Modal': 'core/modal',
        'Color': 'core/color',

        // Header
        'HeaderController':'header/headerController',
        'HeaderView':'header/headerView',

        // Main Container
        'ContainerController':'container/containerController',
        'ContainerView':'container/containerView',

        // Graph
        'GraphController': 'graph/graphController',
        'GraphView': 'graph/graphView',

        // Input and Ouput fields Dialog
        'FieldsController': 'steps/fields/fieldsController',
        'FieldsView': 'steps/fields/fieldsView',

        // Run Options Dialog
        'RunOptionsController':'run/runOptionsController',
        'RunOptionsView':'run/runOptionsView',

        // Edit Transformation Dialog
        'TransSettingsController': 'trans/transSettingsController',
        'TransSettingsView': 'trans/transSettingsView',

        // Edit Step Dialogs
        // Row Generator
        'RowGeneratorController': 'steps/rowgenerator/rowGeneratorController',
        'RowGeneratorView': 'steps/rowgenerator/rowGeneratorView',

        // Checksum
        'ChecksumController': 'steps/checksum/checksumController',
        'ChecksumView': 'steps/checksum/checksumView',

        // Sequence
        'SequenceController': 'steps/sequence/sequenceController',
        'SequenceView': 'steps/sequence/sequenceView',

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
        'datatables': {
            deps: ['jquery', 'bootstrap']
        },
        'datatables-bootstrap': {
            deps: ['jquery', 'bootstrap', 'datatables']
        }
    }
});

var DEBUG = true;

require(['jquery', 'Module', 'adminLTE'], function ($, Module) {
    $(document).ready(function () {
        $(this).find('[module]').each(function () {
            var $element = $(this);
            var name = $element.attr('module');

            // Create module and initialize it
            var module = new Module(name);
            module.initialize($element);

            if (DEBUG) {
                window[name] = module;
            }

        });
    });
});
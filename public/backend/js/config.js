/**
 * INSPINIA - Responsive Admin Theme
 * Copyright 2015 Webapplayers.com
 *
 * Inspinia theme use AngularUI Router to manage routing and views
 * Each view are defined as state.
 * Initial there are written state for all view in theme.
 *
 */
function config($stateProvider, $urlRouterProvider, $ocLazyLoadProvider) {
    $urlRouterProvider.otherwise("/dashboards/dashboard_1");

    $ocLazyLoadProvider.config({
        // Set to true if you want to see what and when is dynamically loaded
        debug: false
    });

    $stateProvider

        .state('dashboards', {
            abstract: true,
            url: "/dashboards",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('dashboards.dashboard_1', {
            url: "/dashboard_1",
            templateUrl: "/assets/backend/views/dashboard_1.html",
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {

                            serie: true,
                            name: 'angular-flot',
                            files: [ '/assets/backend/js/plugins/flot/jquery.flot.js', '/assets/backend/js/plugins/flot/jquery.flot.time.js', '/assets/backend/js/plugins/flot/jquery.flot.tooltip.min.js', '/assets/backend/js/plugins/flot/jquery.flot.spline.js', '/assets/backend/js/plugins/flot/jquery.flot.resize.js', '/assets/backend/js/plugins/flot/jquery.flot.pie.js', '/assets/backend/js/plugins/flot/curvedLines.js', '/assets/backend/js/plugins/flot/angular-flot.js', ]
                        },
                        {
                            name: 'angles',
                            files: ['/assets/backend/js/plugins/chartJs/angles.js', '/assets/backend/js/plugins/chartJs/Chart.min.js']
                        },
                        {
                            name: 'angular-peity',
                            files: ['/assets/backend/js/plugins/peity/jquery.peity.min.js', '/assets/backend/js/plugins/peity/angular-peity.js']
                        }
                    ]);
                }
            }
        })
        .state('products', {
            abstract: true,
            url: "/products",
            templateUrl: "/assets/backend/views/common/content.html"
        })
        .state('products.productlist', {
            url: "/productlist",
            templateUrl: "/assets/backend/views/productlist.html",
            data: { pageTitle: 'Your Tools' }
        })
        .state('products.show', {
            url: "/show/:productId",
            templateUrl: "/assets/backend/views/productshow.html",
            params: {productId: null },
            data: { pageTitle: 'Your Tools' }
        })
        .state('products.edit', {
            url: "/edit/:productId",
            templateUrl: "/assets/backend/views/productedit.html",
            params: {productId: null },
            data: { pageTitle: 'Edit This Tool' }
        })
        .state('products.delete', {
            url: "/delete/:productId",
            templateUrl: "/assets/backend/views/productdelete.html",
            params: {productId: null },
            data: { pageTitle: 'Delete This Tool' }
        })
        .state('products.add', {
            url: "/add",
            templateUrl: "/assets/backend/views/productadd.html",
            data: { pageTitle: 'Add New Tools' }
        })

        .state('categories', {
            abstract: true,
            url: "/categories",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('categories.list', {
            url: "/list",
            templateUrl: "/assets/backend/views/categorylist.html",
            data: { pageTitle: 'System Categories' }
        })
        .state('categories.show', {
            url: "/show/:categoryId",
            templateUrl: "/assets/backend/views/categoryshow.html",
            params: {categoryId: null },
            data: { pageTitle: 'Show Category' }
        })
        .state('categories.edit', {
            url: "/edit/:categoryId",
            templateUrl: "/assets/backend/views/categoryedit.html",
            params: {categoryId: null },
            data: { pageTitle: 'Edit Category' }
        })
        .state('categories.delete', {
            url: "/delete/:categoryId",
            templateUrl: "/assets/backend/views/categorydelete.html",
            params: {categoryId: null },
            data: { pageTitle: 'Delete Category' }
        })
        .state('categories.add', {
            url: "/add",
            templateUrl: "/assets/backend/views/categoryadd.html",
            data: { pageTitle: 'Add Category' }
        })

        // users section
        .state('users', {
            abstract: true,
            url: "/users",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('users.list', {
            url: "/list",
            templateUrl: "/assets/backend/views/userlist.html",
            data: { pageTitle: 'Users' }
        })
        .state('users.show', {
            url: "/show/:userId",
            templateUrl: "/assets/backend/views/usershow.html",
            params: {userId: null },
            data: { pageTitle: 'Show User' }
        })
        .state('users.edit', {
            url: "/edit/:userId",
            templateUrl: "/assets/backend/views/useredit.html",
            params: {userId: null },
            data: { pageTitle: 'Edit User' }
        })
        .state('users.delete', {
            url: "/delete/:userId",
            templateUrl: "/assets/backend/views/userdelete.html",
            params: {userId: null },
            data: { pageTitle: 'Delete User' }
        })
        .state('users.add', {
            url: "/add",
            templateUrl: "/assets/backend/views/useradd.html",
            data: { pageTitle: 'Add User' }
        })


        .state('rentals', {
            abstract: true,
            url: "/rentals",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('rentals.rentals', {
            url: "/pastrentals",
            templateUrl: "/assets/backend/views/empty_page.html",
            data: { pageTitle: 'Your Past Rentals' }
        })
        .state('rentals.upcoming', {
            url: "/futurerentals",
            templateUrl: "/assets/backend/views/empty_page.html",
            data: { pageTitle: 'Your Future Rentals' }
        })

        .state('app', {
            abstract: true,
            url: "/app",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('app.faq', {
            url: "/faq",
            templateUrl: "/assets/backend/views/faq.html",
            data: { pageTitle: 'FAQ' }
        })

        //.state('layouts', {
        //    url: "/layouts",
        //    templateUrl: "/assets/backend/views/layouts.html",
        //    data: { pageTitle: 'Layouts' },
        //})
        //.state('charts', {
        //    abstract: true,
        //    url: "/charts",
        //    templateUrl: "/assets/backend/views/common/content.html",
        //})
        //.state('charts.flot_chart', {
        //    url: "/flot_chart",
        //    templateUrl: "/assets/backend/views/graph_flot.html",
        //    data: { pageTitle: 'Flot chart' },
        //    resolve: {
        //        loadPlugin: function ($ocLazyLoad) {
        //            return $ocLazyLoad.load([
        //                {
        //                    serie: true,
        //                    name: 'angular-flot',
        //                    files: [ '/assets/backend/js/plugins/flot/jquery.flot.js',
        //                        '/assets/backend/js/plugins/flot/jquery.flot.time.js',
        //                        '/assets/backend/js/plugins/flot/jquery.flot.tooltip.min.js',
        //                        '/assets/backend/js/plugins/flot/jquery.flot.spline.js',
        //                        '/assets/backend/js/plugins/flot/jquery.flot.resize.js',
        //                        '/assets/backend/js/plugins/flot/jquery.flot.pie.js',
        //                        '/assets/backend/js/plugins/flot/curvedLines.js',
        //                        '/assets/backend/js/plugins/flot/angular-flot.js' ]
        //                }
        //            ]);
        //        }
        //    }
        //})
        //.state('charts.rickshaw_chart', {
        //    url: "/rickshaw_chart",
        //    templateUrl: "/assets/backend/views/graph_rickshaw.html",
        //    data: { pageTitle: 'Rickshaw chart' },
        //    resolve: {
        //        loadPlugin: function ($ocLazyLoad) {
        //            return $ocLazyLoad.load([
        //                {
        //                    reconfig: true,
        //                    serie: true,
        //                    files: ['/assets/backend/js/plugins/rickshaw/vendor/d3.v3.js','/assets/backend/js/plugins/rickshaw/rickshaw.min.js']
        //                },
        //                {
        //                    reconfig: true,
        //                    name: 'angular-rickshaw',
        //                    files: ['/assets/backend/js/plugins/rickshaw/angular-rickshaw.js']
        //                }
        //            ]);
        //        }
        //    }
        //})
        //.state('charts.peity_chart', {
        //    url: "/peity_chart",
        //    templateUrl: "/assets/backend/views/graph_peity.html",
        //    data: { pageTitle: 'Peity graphs' },
        //    resolve: {
        //        loadPlugin: function ($ocLazyLoad) {
        //            return $ocLazyLoad.load([
        //                {
        //                    name: 'angular-peity',
        //                    files: ['/assets/backend/js/plugins/peity/jquery.peity.min.js', '/assets/backend/js/plugins/peity/angular-peity.js']
        //                }
        //            ]);
        //        }
        //    }
        //})
        //.state('charts.sparkline_chart', {
        //    url: "/sparkline_chart",
        //    templateUrl: "/assets/backend/views/graph_sparkline.html",
        //    data: { pageTitle: 'Sparkline chart' },
        //    resolve: {
        //        loadPlugin: function ($ocLazyLoad) {
        //            return $ocLazyLoad.load([
        //                {
        //                    files: ['/assets/backend/js/plugins/sparkline/jquery.sparkline.min.js']
        //                }
        //            ]);
        //        }
        //    }
        //})
        //.state('charts.chartjs_chart', {
        //    url: "/chartjs_chart",
        //    templateUrl: "/assets/backend/views/chartjs.html",
        //    data: { pageTitle: 'Chart.js' },
        //    resolve: {
        //        loadPlugin: function ($ocLazyLoad) {
        //            return $ocLazyLoad.load([
        //                {
        //                    files: ['/assets/backend/js/plugins/chartJs/Chart.min.js']
        //                },
        //                {
        //                    name: 'angles',
        //                    files: ['/assets/backend/js/plugins/chartJs/angles.js']
        //                }
        //            ]);
        //        }
        //    }
        //})
        .state('mailbox', {
            abstract: true,
            url: "/mailbox",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('mailbox.inbox', {
            url: "/inbox",
            templateUrl: "/assets/backend/views/mailbox.html",
            data: { pageTitle: 'Mail Inbox' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            files: ['/assets/backend/css/plugins/iCheck/custom.css','/assets/backend/js/plugins/iCheck/icheck.min.js']
                        }
                    ]);
                }
            }
        })
        .state('mailbox.email_view', {
            url: "/email_view",
            templateUrl: "/assets/backend/views/mail_detail.html",
            data: { pageTitle: 'Mail detail' }
        })
        .state('mailbox.email_compose', {
            url: "/email_compose",
            templateUrl: "/assets/backend/views/mail_compose.html",
            data: { pageTitle: 'Mail compose' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            files: ['/assets/backend/css/plugins/summernote/summernote.css','/assets/backend/css/plugins/summernote/summernote-bs3.css','/assets/backend/js/plugins/summernote/summernote.min.js']
                        },
                        {
                            name: 'summernote',
                            files: ['/assets/backend/css/plugins/summernote/summernote.css','/assets/backend/css/plugins/summernote/summernote-bs3.css','/assets/backend/js/plugins/summernote/summernote.min.js','/assets/backend/js/plugins/summernote/angular-summernote.min.js']
                        }
                    ]);
                }
            }
        })
        .state('mailbox.email_template', {
            url: "/email_template",
            templateUrl: "/assets/backend/views/email_template.html",
            data: { pageTitle: 'Mail compose' }
        })
        .state('widgets', {
            url: "/widgets",
            templateUrl: "/assets/backend/views/widgets.html",
            data: { pageTitle: 'Widhets' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            name: 'angular-flot',
                            files: [ '/assets/backend/js/plugins/flot/jquery.flot.js', '/assets/backend/js/plugins/flot/jquery.flot.time.js', '/assets/backend/js/plugins/flot/jquery.flot.tooltip.min.js', '/assets/backend/js/plugins/flot/jquery.flot.spline.js', '/assets/backend/js/plugins/flot/jquery.flot.resize.js', '/assets/backend/js/plugins/flot/jquery.flot.pie.js', '/assets/backend/js/plugins/flot/curvedLines.js', '/assets/backend/js/plugins/flot/angular-flot.js', ]
                        },
                        {
                            files: ['/assets/backend/css/plugins/iCheck/custom.css','/assets/backend/js/plugins/iCheck/icheck.min.js']
                        },
                        {
                            files: ['/assets/backend/js/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js','/assets/backend/js/plugins/jvectormap/jquery-jvectormap-world-mill-en.js']
                        },
                        {
                            name: 'ui.checkbox',
                            files: ['/assets/backend/js/bootstrap/angular-bootstrap-checkbox.js']
                        }
                    ]);
                }
            }
        })
        .state('forms', {
            abstract: true,
            url: "/forms",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('forms.basic_form', {
            url: "/basic_form",
            templateUrl: "/assets/backend/views/form_basic.html",
            data: { pageTitle: 'Basic form' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            files: ['/assets/backend/css/plugins/iCheck/custom.css','/assets/backend/js/plugins/iCheck/icheck.min.js']
                        }
                    ]);
                }
            }
        })
        .state('forms.advanced_plugins', {
            url: "/advanced_plugins",
            templateUrl: "/assets/backend/views/form_advanced.html",
            data: { pageTitle: 'Advanced form' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            name: 'ui.knob',
                            files: ['/assets/backend/js/plugins/jsKnob/jquery.knob.js','/assets/backend/js/plugins/jsKnob/angular-knob.js']
                        },
                        {
                            files: ['/assets/backend/css/plugins/ionRangeSlider/ion.rangeSlider.css','/assets/backend/css/plugins/ionRangeSlider/ion.rangeSlider.skinFlat.css','/assets/backend/js/plugins/ionRangeSlider/ion.rangeSlider.min.js']
                        },
                        {
                            insertBefore: '#loadBefore',
                            name: 'localytics.directives',
                            files: ['/assets/backend/css/plugins/chosen/chosen.css','/assets/backend/js/plugins/chosen/chosen.jquery.js','/assets/backend/js/plugins/chosen/chosen.js']
                        },
                        {
                            name: 'nouislider',
                            files: ['/assets/backend/css/plugins/nouslider/jquery.nouislider.css','/assets/backend/js/plugins/nouslider/jquery.nouislider.min.js','/assets/backend/js/plugins/nouslider/angular-nouislider.js']
                        },
                        {
                            name: 'datePicker',
                            files: ['/assets/backend/css/plugins/datapicker/angular-datapicker.css','/assets/backend/js/plugins/datapicker/datePicker.js']
                        },
                        {
                            files: ['/assets/backend/js/plugins/jasny/jasny-bootstrap.min.js']
                        },
                        {
                            name: 'ui.switchery',
                            files: ['/assets/backend/css/plugins/switchery/switchery.css','/assets/backend/js/plugins/switchery/switchery.js','/assets/backend/js/plugins/switchery/ng-switchery.js']
                        },
                        {
                            name: 'colorpicker.module',
                            files: ['/assets/backend/css/plugins/colorpicker/colorpicker.css','/assets/backend/js/plugins/colorpicker/bootstrap-colorpicker-module.js']
                        },
                        {
                            name: 'ngImgCrop',
                            files: ['/assets/backend/js/plugins/ngImgCrop/ng-img-crop.js','/assets/backend/css/plugins/ngImgCrop/ng-img-crop.css']
                        }

                    ]);
                }
            }
        })
        .state('forms.wizard', {
            url: "/wizard",
            templateUrl: "/assets/backend/views/form_wizard.html",
            controller: wizardCtrl,
            data: { pageTitle: 'Wizard form' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            files: ['/assets/backend/css/plugins/steps/jquery.steps.css']
                        }
                    ]);
                }
            }
        })
        .state('forms.wizard.step_one', {
            url: '/step_one',
            templateUrl: '/assets/backend/views/wizard/step_one.html',
            data: { pageTitle: 'Wizard form' }
        })
        .state('forms.wizard.step_two', {
            url: '/step_two',
            templateUrl: '/assets/backend/views/wizard/step_two.html',
            data: { pageTitle: 'Wizard form' }
        })
        .state('forms.wizard.step_three', {
            url: '/step_three',
            templateUrl: '/assets/backend/views/wizard/step_three.html',
            data: { pageTitle: 'Wizard form' }
        })
        .state('forms.file_upload', {
            url: "/file_upload",
            templateUrl: "/assets/backend/views/form_file_upload.html",
            data: { pageTitle: 'File upload' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            files: ['/assets/backend/css/plugins/dropzone/basic.css','/assets/backend/css/plugins/dropzone/dropzone.css','/assets/backend/js/plugins/dropzone/dropzone.js']
                        }
                    ]);
                }
            }
        })
        .state('forms.text_editor', {
            url: "/text_editor",
            templateUrl: "/assets/backend/views/form_editors.html",
            data: { pageTitle: 'Text editor' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            name: 'summernote',
                            files: ['/assets/backend/css/plugins/summernote/summernote.css','/assets/backend/css/plugins/summernote/summernote-bs3.css','/assets/backend/js/plugins/summernote/summernote.min.js','/assets/backend/js/plugins/summernote/angular-summernote.min.js']
                        }
                    ]);
                }
            }
        })
        .state('app.contacts', {
            url: "/contacts",
            templateUrl: "/assets/backend/views/contacts.html",
            data: { pageTitle: 'Contacts' }
        })
        .state('app.profile', {
            url: "/profile",
            templateUrl: "/assets/backend/views/profile.html",
            data: { pageTitle: 'Profile' }
        })
        .state('app.projects', {
            url: "/projects",
            templateUrl: "/assets/backend/views/projects.html",
            data: { pageTitle: 'Projects' }
        })
        .state('app.project_detail', {
            url: "/project_detail",
            templateUrl: "/assets/backend/views/project_detail.html",
            data: { pageTitle: 'Project detail' }
        })
        .state('app.file_manager', {
            url: "/file_manager",
            templateUrl: "/assets/backend/views/file_manager.html",
            data: { pageTitle: 'File manager' }
        })
        .state('app.calendar', {
            url: "/calendar",
            templateUrl: "/assets/backend/views/calendar.html",
            data: { pageTitle: 'Calendar' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            insertBefore: '#loadBefore',
                            files: ['/assets/backend/css/plugins/fullcalendar/fullcalendar.css','/assets/backend/js/plugins/fullcalendar/fullcalendar.min.js','/assets/backend/js/plugins/fullcalendar/gcal.js']
                        },
                        {
                            name: 'ui.calendar',
                            files: ['/assets/backend/js/plugins/fullcalendar/calendar.js']
                        }
                    ]);
                }
            }
        })
        .state('app.timeline', {
            url: "/timeline",
            templateUrl: "/assets/backend/views/timeline.html",
            data: { pageTitle: 'Timeline' }
        })
        .state('app.pin_board', {
            url: "/pin_board",
            templateUrl: "/assets/backend/views/pin_board.html",
            data: { pageTitle: 'Pin board' }
        })
        .state('app.invoice', {
            url: "/invoice",
            templateUrl: "/assets/backend/views/invoice.html",
            data: { pageTitle: 'Invoice' }
        })
        .state('pages', {
            abstract: true,
            url: "/pages",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('pages.search_results', {
            url: "/search_results",
            templateUrl: "/assets/backend/views/search_results.html",
            data: { pageTitle: 'Search results' }
        })
        .state('pages.empy_page', {
            url: "/empy_page",
            templateUrl: "/assets/backend/views/empty_page.html",
            data: { pageTitle: 'Empty page' }
        })
        .state('ui', {
            abstract: true,
            url: "/ui",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('ui.typography', {
            url: "/typography",
            templateUrl: "/assets/backend/views/typography.html",
            data: { pageTitle: 'Typography' }
        })
        .state('ui.icons', {
            url: "/icons",
            templateUrl: "/assets/backend/views/icons.html",
            data: { pageTitle: 'Icons' }
        })
        .state('ui.buttons', {
            url: "/buttons",
            templateUrl: "/assets/backend/views/buttons.html",
            data: { pageTitle: 'Buttons' }
        })
        .state('ui.tabs_panels', {
            url: "/tabs_panels",
            templateUrl: "/assets/backend/views/tabs_panels.html",
            data: { pageTitle: 'Tabs and panels' }
        })
        .state('ui.notifications_tooltips', {
            url: "/notifications_tooltips",
            templateUrl: "/assets/backend/views/notifications.html",
            data: { pageTitle: 'Notifications and tooltips' }
        })
        .state('ui.badges_labels', {
            url: "/badges_labels",
            templateUrl: "/assets/backend/views/badges_labels.html",
            data: { pageTitle: 'Badges and labels and progress' }
        })
        .state('ui.video', {
            url: "/video",
            templateUrl: "/assets/backend/views/video.html",
            data: { pageTitle: 'Responsible Video' }
        })
        .state('grid_options', {
            url: "/grid_options",
            templateUrl: "/assets/backend/views/grid_options.html",
            data: { pageTitle: 'Grid options' }
        })
        .state('miscellaneous', {
            abstract: true,
            url: "/miscellaneous",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('miscellaneous.google_maps', {
            url: "/google_maps",
            templateUrl: "/assets/backend/views/google_maps.html",
            data: { pageTitle: 'Google maps' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            name: 'ui.event',
                            files: ['/assets/backend/js/plugins/uievents/event.js']
                        },
                        {
                            name: 'ui.map',
                            files: ['/assets/backend/js/plugins/uimaps/ui-map.js']
                        },
                    ]);
                }
            }
        })
        .state('miscellaneous.code_editor', {
            url: "/code_editor",
            templateUrl: "/assets/backend/views/code_editor.html",
            data: { pageTitle: 'Code Editor' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: ['/assets/backend/css/plugins/codemirror/codemirror.css','/assets/backend/css/plugins/codemirror/ambiance.css','/assets/backend/js/plugins/codemirror/codemirror.js','/assets/backend/js/plugins/codemirror/mode/javascript/javascript.js']
                        },
                        {
                            name: 'ui.codemirror',
                            files: ['/assets/backend/js/plugins/ui-codemirror/ui-codemirror.min.js']
                        }
                    ]);
                }
            }
        })
        .state('miscellaneous.modal_window', {
            url: "/modal_window",
            templateUrl: "/assets/backend/views/modal_window.html",
            data: { pageTitle: 'Modal window' }
        })
        .state('miscellaneous.chat_view', {
            url: "/chat_view",
            templateUrl: "/assets/backend/views/chat_view.html",
            data: { pageTitle: 'Chat view' }
        })
        .state('miscellaneous.nestable_list', {
            url: "/nestable_list",
            templateUrl: "/assets/backend/views/nestable_list.html",
            data: { pageTitle: 'Nestable List' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            name: 'ui.tree',
                            files: ['/assets/backend/css/plugins/uiTree/angular-ui-tree.min.css','/assets/backend/js/plugins/uiTree/angular-ui-tree.min.js']
                        },
                    ]);
                }
            }
        })
        .state('miscellaneous.notify', {
            url: "/notify",
            templateUrl: "/assets/backend/views/notify.html",
            data: { pageTitle: 'Notifications for angularJS' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            name: 'cgNotify',
                            files: ['/assets/backend/css/plugins/angular-notify/angular-notify.min.css','/assets/backend/js/plugins/angular-notify/angular-notify.min.js']
                        }
                    ]);
                }
            }
        })
        .state('miscellaneous.timeline_2', {
            url: "/timeline_2",
            templateUrl: "/assets/backend/views/timeline_2.html",
            data: { pageTitle: 'Timeline version 2' }
        })
        .state('miscellaneous.forum_view', {
            url: "/forum_view",
            templateUrl: "/assets/backend/views/forum_view.html",
            data: { pageTitle: 'Forum - general view' }
        })
        .state('miscellaneous.forum_post_view', {
            url: "/forum_post_view",
            templateUrl: "/assets/backend/views/forum_post_view.html",
            data: { pageTitle: 'Forum - post view' }
        })
        .state('tables', {
            abstract: true,
            url: "/tables",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('tables.static_table', {
            url: "/static_table",
            templateUrl: "/assets/backend/views/table_basic.html",
            data: { pageTitle: 'Static table' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            name: 'angular-peity',
                            files: ['/assets/backend/js/plugins/peity/jquery.peity.min.js', '/assets/backend/js/plugins/peity/angular-peity.js']
                        },
                        {
                            files: ['/assets/backend/css/plugins/iCheck/custom.css','/assets/backend/js/plugins/iCheck/icheck.min.js']
                        }
                    ]);
                }
            }
        })
        .state('tables.data_tables', {
            url: "/data_tables",
            templateUrl: "/assets/backend/views/table_data_tables.html",
            data: { pageTitle: 'Data Tables' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            seria: true,
                            files: ['/assets/backend/css/plugins/dataTables/dataTables.bootstrap.css','/assets/backend/js/plugins/dataTables/jquery.dataTables.js','/assets/backend/js/plugins/dataTables/dataTables.bootstrap.js']
                        },
                        {
                            name: 'datatables',
                            files: ['/assets/backend/js/plugins/dataTables/angular-datatables.min.js']
                        }
                    ]);
                }
            }
        })
        .state('tables.nggrid', {
            url: "/nggrid",
            templateUrl: "/assets/backend/views/nggrid.html",
            data: { pageTitle: 'ng Grid' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            name: 'ngGrid',
                            files: ['/assets/backend/js/plugins/nggrid/ng-grid-2.0.3.min.js']
                        },
                        {
                            insertBefore: '#loadBefore',
                            files: ['/assets/backend/js/plugins/nggrid/ng-grid.css']
                        }
                    ]);
                }
            }
        })
        .state('gallery', {
            abstract: true,
            url: "/gallery",
            templateUrl: "/assets/backend/views/common/content.html",
        })
        .state('gallery.basic_gallery', {
            url: "/basic_gallery",
            templateUrl: "/assets/backend/views/basic_gallery.html",
            data: { pageTitle: 'Lightbox Gallery' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            files: ['/assets/backend/js/plugins/blueimp/jquery.blueimp-gallery.min.js','/assets/backend/css/plugins/blueimp/css/blueimp-gallery.min.css']
                        }
                    ]);
                }
            }
        })
        .state('gallery.bootstrap_carousel', {
            url: "/bootstrap_carousel",
            templateUrl: "/assets/backend/views/carousel.html",
            data: { pageTitle: 'Bootstrap carousel' }
        })
        .state('css_animations', {
            url: "/css_animations",
            templateUrl: "/assets/backend/views/css_animation.html",
            data: { pageTitle: 'CSS Animations' },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            reconfig: true,
                            serie: true,
                            files: ['/assets/backend/js/plugins/rickshaw/vendor/d3.v3.js','/assets/backend/js/plugins/rickshaw/rickshaw.min.js']
                        },
                        {
                            reconfig: true,
                            name: 'angular-rickshaw',
                            files: ['/assets/backend/js/plugins/rickshaw/angular-rickshaw.js']
                        }
                    ]);
                }
            }

        });
}
angular
    .module('inspinia')
    .config(config)
    .run(function($rootScope, $state) {
        $rootScope.$state = $state;
    });

angular.module("app.categories.app", ["ngResource", 'ngRoute', 'app.category.controller'])
    .factory('categoriesLookupFactory', ['$resource', function($resource) {
        return $resource("/api/categories", {}, {
            get: {method: 'GET', cache: false, isArray: true}
        });
    }])
    .factory('categoryLookupFactory', ['$resource', function($resource) {
        return $resource("/api/category/:id", {}, {
            get: {method: 'GET', cache: false, isArray: false},
            post: {method: 'POST', cache: false, isArray: false},
            delete: {method: 'DELETE', cache: false, isArray: false}
        });
    }])
    .factory('allCategoriesLookupFactory', ['$resource', function($resource) {
        return $resource("/api/categoriesAll", {}, {
            get: {method: 'GET', cache: false, isArray: true}
        });
    }])
    .config(['$routeProvider', function($routeProvider){
        $routeProvider
            .when('/', {
                templateUrl: '/assets/views/categories/list.html',
                controller: 'categoryListController',
                controllerAs: 'categoryListCtrl'
            })
            .when('/edit/:id', {
                templateUrl: '/assets/views/categories/edit.html',
                controller: 'categoryEditController',
                controllerAs: 'categoryCtrl'
            })
            .when('/show/:id', {
                templateUrl: '/assets/views/categories/show.html',
                controller: 'categoryEditController',
                controllerAs: 'categoryCtrl'
            })
            .when('/add', {
                templateUrl: '/assets/views/categories/add.html',
                controller: 'categoryEditController',
                controllerAs: 'categoryCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    } ] )
;

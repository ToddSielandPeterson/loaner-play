angular.module("app.userproduct.app", ["ngResource", 'ngRoute', 'app.userProduct.controller'])
    .factory('userProductFactory', ['$resource', function($resource) {
        return $resource("/api/u/product/:id", {}, {
            get: {method: 'GET', cache: false, isArray: false},
            post: {method: 'POST', cache: false, isArray: true},
            delete: {method: 'DELETE', cache: false, isArray: true}
        });
    }])
    .factory('userProductsFactory', ['$resource', function($resource) {
        return $resource("/api/u/products", {}, {
            get: {method: 'GET', cache: false, isArray: true} //,
            //post: {method: 'POST', cache: false, isArray: true},
            //delete: {method: 'DELETE', cache: false, isArray: true}
        });
    }])
    .factory('categoriesLookupFactory', ['$resource', function($resource) {
        return $resource("/api/categories", {}, {
            get: {method: 'GET', cache: false, isArray: true},
            post: {method: 'POST', cache: false, isArray: true},
            delete: {method: 'DELETE', cache: false, isArray: true}
        });
    }])
    .factory('categoryLookupFactory', ['$resource', function($resource) {
        return $resource("/api/category/:id", {}, {
            get: {method: 'GET', cache: false, isArray: false}
        });
    }])
    .config(['$routeProvider', function($routeProvider){
        $routeProvider
            .when('/', {
                templateUrl: '/assets/views/userproduct/list.html',
                controller: 'userProductListController',
                controllerAs: 'prodListCtrl'
            })
            .when('/edit/:id', {
                templateUrl: '/assets/views/userproduct/edit.html',
                controller: 'userProductEditController',
                controllerAs: 'prodCtrl'
            })
            .when('/show/:id', {
                templateUrl: '/assets/views/userproduct/show.html',
                controller: 'userProductEditController',
                controllerAs: 'prodCtrl'
            })
            .when('/delete/:id', {
                templateUrl: '/assets/views/userproduct/delete.html'
            })
            .when('/add', {
                templateUrl: '/assets/views/userproduct/add.html',
                controller: 'userProductEditController',
                controllerAs: 'prodCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    } ] )
;

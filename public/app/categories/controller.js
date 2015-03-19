angular.module("app.category.controller", ["ngResource", "ngRoute"])
    .controller("categoryEditController", ['$scope', '$location', '$routeParams', '$http', 'categoriesLookupFactory', 'categoryLookupFactory', 'allCategoriesLookupFactory',
            function($scope, $location, $routeParams, $http, categoriesLookupFactory, categoryLookupFactory, allCategoriesLookupFactory) {
        $scope.categories = {};
        $scope.category = {};

        $scope.categoriesFn = function () {
            allCategoriesLookupFactory.get({},
                function success(data) {
                    $scope.categories = data;
                }, function error(errorMessage) {
                }
            )};

        $scope.categoryFn = function () {
            categoryLookupFactory.get({'id':  $routeParams.id},
                function success(data) {
                    $scope.category = data;
                }, function error(errorMessage) {
                }
            )};

        $scope.submitTheForm = function() {
            var responsePromise = $http.post("/api/category/" + $scope.category.categoryId, $scope.category);
            responsePromise.success(function(dataFromServer, status, headers, config) {
                console.log(dataFromServer.title);
            });
            responsePromise.error(function(data, status, headers, config) {
                alert("Submitting form failed!");
            });
        };

        // $scope.addProduct = function() {
        //    var responsePromise = $http.put("/api/u/product", $scope.product);
        //    responsePromise.success(function(dataFromServer, status, headers, config) {
        //        console.log(dataFromServer.title);
        //        $location.path("/" + id);
        //    });
        //    responsePromise.error(function(data, status, headers, config) {
        //        alert("Submitting form failed!");
        //    });
        //};

        $scope.goToEdit = function(id){
            $location.path("/edit/" + id);
        };

        $scope.goToShow = function(id){
            $location.path("/show/" + id);
        };

        $scope.backToShow = function(){
            $scope.goToShow($scope.product.productId);
        };

        $scope.goToDelete = function(id){
            $location.path("/delete/" + id);
        };
    }])
    .controller("categoryListController",['$scope', '$resource', '$location', '$routeParams', 'categoriesLookupFactory',
        function($scope, $resource, $location, $routeParams, categoriesLookupFactory) {
        $scope.categories = [];

            $scope.categoriesFn = function () {
                categoriesLookupFactory.get({},
                    function success(data) {
                        $scope.categories = data;
                    }, function error(errorMessage) {
                    }
                )};

        $scope.goToEdit = function(id){
            $location.path("/edit/" + id);
        };

        $scope.goToAdd = function(){
            $location.path("/add");
        };

        $scope.goToShow = function(id){
            $location.path("/show/" + id);
        };

        $scope.goToDelete = function(){
            categoryLookupFactory.delete({'id':  $routeParams.id},
                function success(data) {
                }, function error(errorMessage) {
                }
            )};
    }])
;
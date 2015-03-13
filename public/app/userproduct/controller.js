angular.module("app.userProduct.controller", ["ngResource", "ngRoute"])
    .controller("userProductEditController", ['$scope', '$location', '$routeParams', 'categoriesLookupFactory', 'categoryLookupFactory', 'userProductFactory',
            function($scope, $location, $routeParams, categoriesLookupFactory, categoryLookupFactory, userProductFactory) {
        $scope.product = {};
        $scope.categories = [{}];
        $scope.category = {};

        $scope.categoriesFn = function () {
            categoriesLookupFactory.get({},
                function success(data) {
                    $scope.categories = data;
                }, function error(errorMessage) {
                }
            )};

        $scope.categoryFn = function () {
            categoryLookupFactory.get({id: $scope.product.categoryId},
                function success(data) {
                    $scope.category = data;
                }, function error(errorMessage) {
                }
            )};

        $scope.userProductFn = function () {
            userProductFactory.get( {'id':  $routeParams.id},
                function success(data) {
                    $scope.product = data;
                    $scope.categoryFn();
                }, function error(errorMessage) {
                }
            )};
        $scope.submitTheForm = function() {
            var dataObject = {
              //productId = store.product.productId,
              //userId = store.product.userId
            };
            var responsePromise = $http.post("/edit/", dataObject, {});
            responsePromise.success(function(dataFromServer, status, headers, config) {
                console.log(dataFromServer.title);
            });
            responsePromise.error(function(data, status, headers, config) {
                alert("Submitting form failed!");
            });
        };

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
    .controller("userProductListController",['$scope', '$resource', '$location', 'userProductsFactory', function($scope, $resource, $location, userProductsFactory) {
        $scope.categories = [];
        $scope.products = [{}];

        $scope.userProductsFn = function () {
            userProductsFactory.get( {},
                function success(data) {
                    $scope.products = data;
                }, function error(errorMessage) {

                }
            )};

        $scope.goToEdit = function(id){
            $location.path("/edit/" + id);
        };

        $scope.goToShow = function(id){
            $location.path("/show/" + id);
        };

        $scope.goToDelete = function(id){
            $location.path("/delete/" + id);
        };
    }])
;

var app = angular.module("app", ["ngResource"])
	//.constant("apiUrl", "http://localhost:9000\:9000/api") // to tell AngularJS that 9000 is not a dynamic parameter
	//.config(["$routeProvider", function($routeProvider) {
	//	return $routeProvider.when("/", {
	//		templateUrl: "/views/main",
	//		controller: "ListCtrl"
	//	}).when("/create", {
	//		templateUrl: "/views/detail",
	//		controller: "CreateCtrl"
	//    }).when("/edit/:id", {
	//		templateUrl: "/views/detail",
	//		controller: "EditCtrl"
	//    }).otherwise({
	//		redirectTo: "/"
	//	});
	//}
	//]).config([
	//"$locationProvider", function($locationProvider) {
	//	return $locationProvider.html5Mode(true).hashPrefix("!"); // enable the new HTML5 routing and histoty API
	//}
//])
;

// the global controller
app.controller("AppCtrl", ["$scope", "$location", function($scope, $location) {
	// the very sweet go function is inherited to all other controllers
	$scope.go = function (path) {
		$location.path(path);
	};
}]);

app.controller("UserProductListController",['$scope', '$http', function($scope, $http) {
    var store = this;
    store.products = [];

    $http.get("/api/u/products").success( function(data) {
        store.products = data
        }
    );

    //this.productList = [
    //    {
    //        productId:"1",
    //        name:"big hammer",
    //        secondLine:"a really big hammer",
    //        dateAdded:new Date(),
    //        lastEditDate: new Date()
    //    },
    //    {
    //        productId:"2",
    //        name:"small hammer",
    //        secondLine:"a really small hammer",
    //        dateAdded:new Date(),
    //        lastEditDate: new Date()
    //    }
    //];
}]);

app.directive("productTabs", function() {
    return {
        restrict: 'E',
        templateUrl: 'product-tabs.html',
        controller: function() {
            this.tab = 1;

            this.isSet = function(checkTab) {
                return this.tab === checkTab;
            };

            this.setTab = function(setTab) {
                this.tab = setTab;
            };
        },
        controllerAs:'tab'
    };
});

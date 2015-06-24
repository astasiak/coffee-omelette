var myApp = angular.module('helloApp', ['ngResource']);

myApp.controller('HelloController', ['$scope', 'HelloRest', function($scope, HelloRest) {
	HelloRest.query(function(data) {
	  $scope.cards = data;
	});
    $scope.spice = 'very';

    $scope.chiliSpicy = function() {
        $scope.spice = 'chili';
    };

    $scope.jalapenoSpicy = function() {
        $scope.spice = 'jalapeño';
    };
}]);

myApp.factory("HelloRest", function ($resource) {
    return $resource('/cards');
});
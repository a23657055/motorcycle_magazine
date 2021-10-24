var myApp = angular.module('myApp', []).controller('getAppController', getAppController);

angular.element(document).ready(function() {
    angular.element(window).keydown(function() {
        if (event.keyCode == 13) {
            event.preventDefault();
            return false;
        }
    });
});

function getAppController($scope, $http, $window) {

}

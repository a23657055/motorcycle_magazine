var myApp = angular.module('myApp', [ ]).controller('getAppController', getAppController);
angular.element(document).ready(function() {
	angular.element(window).keydown(function() {
		if (event.keyCode == 13) {
			event.preventDefault();
			return false;
		}
	});
});

function getAppController($rootScope, $scope, $http, $window) {
	$scope.updateData = function() {
		bootbox.dialog({
			closeButton: false,
			message : '<i class="fas fa-fw fa-circle-notch fa-spin"></i>' + dataUpdating 
		});
		if ($scope.OldCode == "")
			$scope.OldCode = null;
		if ($scope.NewCode == "")
			$scope.NewCode = null;
		var request = {
			OldCode : btoa($scope.oldCode),
			NewCode : btoa($scope.newCode)
		};
		$http.post('./api/code/update', request, csrf_config).then(function(response) {
			if (response.data.success == true) {
				$scope.oldCode = "";
				$scope.newCode = "";
				$scope.newCodeAgain = "";
				bootbox.hideAll();
				bootbox.alert({
					message : response.data.msg,
					buttons : {
						ok : {
							label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
							className : 'btn-primary',
						}
					},
					callback: function() { location.replace(location.href.split('sys')[0] + "pub/")}
				});
			} else {
				bootbox.hideAll();
				bootbox.alert({
					message : response.data.msg,
					buttons : {
						ok : {
							label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
							className : 'btn-danger'
						}
					},
					callback: function() {	}
				});
			}
		}).catch(function() {
			bootbox.hideAll();
			bootbox.alert({
				message : globalResetCodeFail,
				buttons : {
					ok : {
						label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
						className : 'btn-danger'
					}
				},
				callback: function() {	}
			});
		});
	}
	
	$scope.getPasswordLength = function() {		
		var request = {};
		$http
		.post('./api/code/getPasswordLength', request, csrf_config)
		.then(function(response) {
			$scope.passwordLength = response.data[0]			  		    			 
			$scope.regex = new RegExp('^(?=.*\\d)(?=.*[a-zA-Z])(?=.*\\W).{' + response.data[0] + ',}$');		
		})
	}
	$scope.getPasswordLength();
	
}
var myApp = angular.module('myApp', []).controller('getAppController', getAppController);

angular.element(document).ready(function() {
	angular.element(window).keydown(function() {
		if (event.keyCode == 13) {
			event.preventDefault();
			return false;
		}
	});
});

function getAppController($scope, $http, $window, $timeout) {
	$scope.userName = "";
	$scope.userEmail = "";
	
	$scope.resend = function() {
		bootbox.dialog({
			closeButton: false,
			message : '<i class="fas fa-fw fa-circle-notch fa-spin"></i>' + loginResend 
		});
		var request = 'account=' + $scope.userName + '&email=' + $scope.userEmail;
		$http.post('./public/api/resend', request, csrf_config_form).then(function(response) {
			
			if (response.data.success == true) {
				$scope.userName = "";
				$scope.userEmail = "";
				bootbox.hideAll();
				bootbox.alert({
					message : response.data.msg,
					buttons : {
						ok : {
							label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
							className : 'btn-primary'
						}
					},
					callback: function() {
						window.location.href = "./";
					}
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
					callback: function() {
						$timeout(function() {
							$window.document.getElementById("userName").focus();
						}, 0); 
					}
				});
			}

		}).catch(function() {
			bootbox.hideAll();
			bootbox.alert({
				message : loginResendFail,
				buttons : {
					ok : {
						label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
						className : 'btn-danger'
					}
				}
			});
		});
	}
}

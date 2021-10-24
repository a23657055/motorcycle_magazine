var myApp = angular.module('myApp', []).controller(
		'getAppController', getAppController);

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
	$scope.newCode = "";
	$scope.newCodeAgain = "";
	$scope.code = $window.location.search.substring(1);
	
	$scope.reset = function() {
		bootbox.dialog({
			closeButton: false,
			message : '<i class="fas fa-fw fa-circle-notch fa-spin"></i>' + loginReset 
		});
		var request = 'account=' + $scope.userName + '&code=' + btoa($scope.newCode) + '&id=' + $scope.code;
		$http.post('./public/api/reset', request, csrf_config_form).then(function(response) {
			if (response.data.success == true) {
				$scope.userName = "";
				$scope.newCode = "";
				$scope.newCodeAgain = "";
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
				message : loginResetFail,
				buttons : {
					ok : {
						label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
						className : 'btn-danger'
					}
				}
			});
		});
	}
	
	$scope.getPasswordLength = function() {		
		var request = {};
		$http.post('./public/api/getPasswordLength', request, csrf_config).then(function(response) {
			$scope.passwordLength = response.data[0];
			$scope.regex = new RegExp('^(?=.*\\d)(?=.*[a-zA-Z])(?=.*\\W).{' + response.data[0] + ',}$');		
		});
	}
	$scope.getPasswordLength();
}

$(document).ready(function() {
	var refresh = setInterval(function() {
		location.reload();
	}, 1000 * 60 * 10);

	$("#btnLogin").hide();
	$("#btnIndex").show();
	$("#btnLogin2").hide();
	$("#btnIndex2").show();
});
function toggleLogin() {
	location.href = "./";
}
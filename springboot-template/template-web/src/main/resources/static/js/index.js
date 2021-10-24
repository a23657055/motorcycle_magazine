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
	
    // 登入
	$scope.userName = localStorage.getItem("userName");
	localStorage.removeItem("userName");
	$scope.userName = "";
	$scope.userCode = "";
	// $scope.otpCode = "";
	$scope.showTwoFactor = false;
	// angular.element("#divotpCode").removeAttr("style");
	angular.element("#divgtpCode").removeAttr("style");
	angular.element("#divVerify").removeAttr("style");
	
	$scope.login = function() {
		var grecaptcha = $("#g-recaptcha-response").val();
		if (grecaptcha == '' && $scope.showTwoFactor) return;
		bootbox.dialog({
            closeButton: false,
            message: '<i class="fas fa-fw fa-circle-notch fa-spin"></i>' + loginLogin
        });
		var request = 'account=' + $scope.userName + '&code=' + btoa($scope.userCode) + '&otp=' + $scope.otpCode + '&gtp=' + grecaptcha;
		console.log("request="+request);
		$http.post('./public/api/login', request, csrf_config_form).then(function(response) {
			console.log("response="+JSON.stringify(response));

			if (response.data.success == true) {
				if (response.data.url != "") {
					$window.location = response.data.url;
				} else {
					bootbox.hideAll();
					// $scope.otpCode = "";
					$scope.showTwoFactor = true;
				}
				$timeout(function() {
					// $window.document.getElementById("otpCode").focus();
				}, 0); 
			} else {
				bootbox.hideAll();
				$scope.userName = "";
				$scope.userCode = "";
				// $scope.otpCode = "";
				$scope.showTwoFactor = false;
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
			console.log("login catch");

			bootbox.hideAll();
			$scope.userCode = "";
			// $scope.otpCode = "";
			$scope.showTwoFactor = false;
			bootbox.alert({
				message : loginFail,
				buttons : {
					ok : {
						label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
						className : 'btn-danger'
					}
				}
			});
		});
	};
}

$(document).ready(function() {
	var refresh = setInterval(function() {
		localStorage.userName = $("#userName").val();
		location.reload();
	}, 1000 * 60 * 10);
});
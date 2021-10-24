var myApp = angular.module('myApp', ['ng-pros.directive.autocomplete']).controller(
		'getAppController', getAppController);

angular.element(document).ready(function() {
	angular.element(window).keydown(function() {
		if (event.keyCode == 13) {
			event.preventDefault();
			return false;
		}
	});
	
	var myInput = document.getElementById('memberAccount');
	angular.element('myInput').onpaste = function(e) {
	   e.preventDefault();
	 }
});

function getAppController($scope, $http, $window, $timeout) {
	$scope.pdplIsAgree = false;
	
	$scope.memberAccountVerifyFail = false;
	
	$scope.orgName = "";
	$scope.orgCity = "";
	$scope.orgTown = "";
	$scope.orgAddress = "";
	$scope.parentOrgId = null;
	$scope.memberAccount = "";
	$scope.memberName = "";
	$scope.memberEmail = "";
	$scope.memberMobilePhone = "";
	
	$scope.inputModel = '';
	$scope.options = {
		url: './public/api/getHealthcares',
		delay: 0,
		minlength: 2,
		nameAttr: 'name',
		dataHolder: 'items',
		limitParam: 'per_page',
		searchParam: 'q',
		highlightExactSearch: false,
		programmaticallyLoad: true,
		hasSelectionClass: '',
		noResultsMessage: noResultsMessage,
		itemTemplate: '<button type="button" ng-class="getItemClasses($index)" ng-mouseenter="onItemMouseenter($index)" ng-repeat="item in searchResults" ng-click="select(item)">' +
			'<div>' +
			'<h5><strong ng-bind="item.name"></strong></h5>' +
			'<span ng-bind="item.id"></span>' +
			'</div>' +
			'</button>'
	};

	$scope.programmaticallyLoad = function() {
		$scope.autoModel = 'np-autocomplete';
	};
	
	$scope.getAuthorOrgs = function() {
		var request = {
			
		};
		$http.post('./public/api/getAuthorOrgs', request, csrf_config).then(function(response) {
			if (response.data.success == true) {
				$scope.authorOrgs = response.data.datatable;
			} else {
				$scope.authorOrgs = [];
			}		
		}).catch(function() {
			$scope.authorOrgs = [];
		});
	};
	$scope.getAuthorOrgs();
	
	$scope.checkMemberAccount = function() {
		$scope.memberAccountVerifyFail = false;
		if ($scope.myForm.memberAccount.$valid && $scope.myForm.memberAccount.$dirty) {
			var memberAccount = $window.document.getElementById("memberAccount").value;
			$scope.memberAccountVerifyFail = false;
			$scope.myForm.memberAccount.$valid = false;
			var request = {
				memberAccount : $scope.memberAccount
			}
			$http.post('./public/api/checkAccount', request, csrf_config).then(function(response) {
				if (response.data.success == true) {
					$scope.memberAccountVerifyFail = false;
					$scope.myForm.memberAccount.$valid = false;
				} else {
					$scope.memberAccountVerifyFail = true;
					$scope.myForm.memberAccount.$valid = true;
				}
			}).catch(function() {
				$scope.memberAccountVerifyFail = false;
				$scope.myForm.memberAccount.$valid = false;
			});
		}
	};
	
	$scope.pdplAgree = function() {
		$scope.pdplIsAgree =  true;
		$window.document.getElementById("sign").style.display = "block";
		$window.document.getElementById("pdpl").style.display = "none";
	}
	
	$scope.pdplDisagree = function() {
		$scope.pdplIsAgree =  false;
		$window.document.getElementById("sign").style.display = "block";
		$window.document.getElementById("pdpl").style.display = "none";
	}
	
	$scope.pdplShow = function() {
		$window.document.getElementById("sign").style.display = "none";
		$window.document.getElementById("pdpl").style.display = "block";
	}
	
	$scope.signup = function() {
		bootbox.dialog({
			closeButton: false,
			message : '<i class="fas fa-fw fa-circle-notch fa-spin"></i>' + memberSignUp 
		});
		var request = {
				orgCode : $scope.orgCode,
				orgName : $scope.orgName,
				orgCity : $scope.orgCity,
				orgTown : $scope.orgTown,
				orgAddress : $scope.orgAddress,
				parentOrgId : $scope.parentOrgId,
				memberAccount : $scope.memberAccount,
				memberName : $scope.memberName,
				memberEmail : $scope.memberEmail,
				memberMobilePhone : $scope.memberMobilePhone
		};
		$http.post('./public/api/sign_up', request, csrf_config).then(function(response) {
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
				message : memberSignUpFail,
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

$(document).ready(function() {
	var refresh = setInterval(function() {
		location.reload();
	}, 1000 * 60 * 10);
	$(".city").change(function() {
		$("#orgCity").val($(this).val());
		$("#orgTown").val($(".town").val());
		$("#orgCity").trigger('input');
		$("#orgCity").trigger('change');
		$("#orgTown").trigger('input');
		$("#orgTown").trigger('change');
	});
	$(".town").change(function() {
		$("#orgTown").val($(this).val());
		$("#orgTown").trigger('input');
		$("#orgTown").trigger('change');
	});
});
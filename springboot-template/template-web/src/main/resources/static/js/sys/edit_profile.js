var myApp = angular.module('myApp', [ ]).controller('getAppController', getAppController);
angular.element(document).ready(function() {
	angular.element(window).keydown(function() {
		if (event.keyCode == 13) {
			event.preventDefault();
			return false;
		}
	});
});

function getAppController($rootScope, $scope, $http) {
	$scope.quertData = function() {
		bootbox.dialog({
			closeButton: false,
			message : '<i class="fas fa-fw fa-circle-notch fa-spin"></i>' + dataLoading 
		});
		var request = {};
		$http.post('./api/profile/query', request, csrf_config).then(function(response) {
			$scope.memberName = response.data[0].Name;
			$scope.memberEmail = response.data[0].Email;
			$scope.memberSpareEmail = response.data[0].SpareEmail;
			$scope.memberMobilePhone = response.data[0].MobilePhone;
			$scope.memberCityPhone = response.data[0].CityPhone;
			$scope.memberFaxPhone = response.data[0].FaxPhone;
			$scope.memberAddress = response.data[0].Address;
			$scope.memberDepartment = response.data[0].Department;
			$scope.memberTitle = response.data[0].Title;
			bootbox.hideAll();
		}).catch(function() {
			bootbox.hideAll();
			bootbox.alert({
				message : globalReadDataFail,
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
	$scope.quertData();
	
	$scope.updateData = function() {
		bootbox.dialog({
			closeButton: false,
			message : '<i class="fas fa-fw fa-circle-notch fa-spin"></i>' + dataUpdating 
		});
		var request = {
			Name : $scope.memberName,
			Email : $scope.memberEmail,
			SpareEmail : $scope.memberSpareEmail,
			MobilePhone : $scope.memberMobilePhone,
			CityPhone : $scope.memberCityPhone,
			FaxPhone : $scope.memberFaxPhone,
			Address : $scope.memberAddress,
			Department : $scope.memberDepartment,
			Title : $scope.memberTitle
		};
		$http.post('./api/profile/update', request, csrf_config).then(function(response) {
			if (response.data.success == true) {
				bootbox.hideAll();
				bootbox.alert({
					message : response.data.msg,
					buttons : {
						ok : {
							label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
							className : 'btn-primary',
						}
					},
					callback: function() { }
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
				message : globalUpdateDataFail,
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
}
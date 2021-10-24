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
	$scope.queryData = function() {
		bootbox.dialog({
			closeButton: false,
			message : '<i class="fas fa-fw fa-circle-notch fa-spin"></i>' + dataLoading 
		});
		var request = {};
		$http.post('./api/profile/org/query', request, csrf_config).then(function(response) {
		
			$scope.Id = response.data[0].Id;
			$scope.Name = response.data[0].Name;
			$scope.Code= response.data[0].Code;
			$scope.OrgType= response.data[0].OrgType;
			$scope.AuthType= response.data[0].AuthType;
			$scope.Tel= response.data[0].Tel;
			$scope.Fax= response.data[0].Fax;
			$scope.City= response.data[0].City;
			$scope.Town= response.data[0].Town;
			$scope.Address= response.data[0].Address;
			
			
			$('#twzipcode').twzipcode('destroy');
			$('#twzipcode').twzipcode({
				"zipcodeIntoDistrict" : true,
				"css" : ["city form-control",
 					"town form-control"],
					"countyName" : "city",
					"districtName" : "town",	
			    "countySel": $scope.City,
			    "districtSel": $scope.Town
			});
			
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
	$scope.queryData();
	
	$scope.updateData = function() {
		bootbox.dialog({
			closeButton: false,
			message : '<i class="fas fa-fw fa-circle-notch fa-spin"></i>' + dataUpdating 
		});
		var request = {
			Name : $scope.Name,
			Code : $scope.Code,
			OrgType : $scope.OrgType,
			AuthType : $scope.AuthType,
			Tel : $scope.Tel,
			Fax : $scope.Fax,
			City : $scope.City,
			Town : $scope.Town,
			Address : $scope.Address

		};
		$http.post('./api/profile/org/update', request, csrf_config).then(function(response) {
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


$(document).ready(function() {
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

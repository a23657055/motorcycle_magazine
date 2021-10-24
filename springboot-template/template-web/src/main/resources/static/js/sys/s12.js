var myApp = angular.module('myApp', [ 'ngCookies', 'bw.paging', 'ui.toggle' ]).controller(
		'getAppController', getAppController);

angular.element(document).ready(function() {
	angular.element(window).keydown(function() {
		if (event.keyCode == 13) {
			event.preventDefault();
			return false;
		}
	});
});

myApp.directive('integerOnly', function() {
	return {
		require : 'ngModel',
		link : function(scope, elm, attrs, ctrl) {
			ctrl.$validators.integerOnly = function(modelValue, viewValue) {
				if (ctrl.$isEmpty(viewValue)) {
					return true;
				}
				var pattern = /^[0-9]+$/;
				if (pattern.test(viewValue)) {
					return true;
				} else {
					return false;
				}
			};
		}
	};
});


function getAppController($rootScope, $scope, $http, $cookieStore) {
	
	$scope.getrole = function() {
		var data = {};
		$http.post('./api/s12/getrole', data, csrf_config).then(function(response) {
			$scope.roles = response.data;
			//console.log("______s12/getrole------"+JSON.stringify($scope.roles))
		}).catch(function() {
			bootbox.alert({
				message : globalReadDataFail,
				buttons : {
					ok : {
						label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
						className : 'btn-danger',
					}
				},
				callback: function() { }
			});
		}).finally(function() { });
	};
	$scope.getrole();
	
	$scope.changeRole = function() {
		document.getElementById("selectReadAllorNone").checked = false;
		document.getElementById("selectInsAllorNone").checked = false;
		document.getElementById("selectUpdAllorNone").checked = false;
		document.getElementById("selectDelAllorNone").checked = false;
		document.getElementById("selectSignAllorNone").checked = false;
		$("#imgLoading").show();
		$("#imgLoading2").show();
		$scope.showInsert = false;
		$scope.disableSelectReadAllorNone = true;
		$scope.disableSelectInsAllorNone = true;
		$scope.disableSelectUpdAllorNone = true;
		$scope.disableSelectDelAllorNone = true;
		$scope.disableSelectSignAllorNone = true;
		angular.forEach($scope.RoleForm, function(item) {			
			var strFormId = "disableSelectRead_" + item.FormId;
			$scope[strFormId] = true;
			var strFormId = "disableSelectIns_" + item.FormId;
			$scope[strFormId] = true;
			var strFormId = "disableSelectUpd_" + item.FormId;
			$scope[strFormId] = true;
			var strFormId = "disableSelectDel_" + item.FormId;
			$scope[strFormId] = true;
			var strFormId = "disableSelectSign_" + item.FormId;
			$scope[strFormId] = true;
		});
		var data = {RoleId:$scope.RoleId};
		$http.post('./api/s12/query', data, csrf_config).then(function(response) {
			$scope.RoleForm = response.data;
			if ($scope.RoleId != null) {
				$scope.showInsert = true;
				$scope.disableSelectReadAllorNone = false;
				$scope.disableSelectInsAllorNone = false;
				$scope.disableSelectUpdAllorNone = false;
				$scope.disableSelectDelAllorNone = false;
				$scope.disableSelectSignAllorNone = false;
				angular.forEach($scope.RoleForm, function(item) {
					var strFormId = "disableSelectRead_" + item.FormId;
					$scope[strFormId] = false;
					var strFormId = "disableSelectIns_" + item.FormId;
					$scope[strFormId] = false;
					var strFormId = "disableSelectUpd_" + item.FormId;
					$scope[strFormId] = false;
					var strFormId = "disableSelectDel_" + item.FormId;
					$scope[strFormId] = false;
					var strFormId = "disableSelectSign_" + item.FormId;
					$scope[strFormId] = false;
				});
			} else {
				$scope.showInsert = false;
				$scope.disableSelectReadAllorNone = true;
				$scope.disableSelectInsAllorNone = true;
				$scope.disableSelectUpdAllorNone = true;
				$scope.disableSelectDelAllorNone = true;
				$scope.disableSelectSignAllorNone = true;
				angular.forEach($scope.RoleForm, function(item) {			
					var strFormId = "disableSelectRead_" + item.FormId;
					$scope[strFormId] = true;
					var strFormId = "disableSelectIns_" + item.FormId;
					$scope[strFormId] = true;
					var strFormId = "disableSelectUpd_" + item.FormId;
					$scope[strFormId] = true;
					var strFormId = "disableSelectDel_" + item.FormId;
					$scope[strFormId] = true;
					var strFormId = "disableSelectSign_" + item.FormId;
					$scope[strFormId] = true;
				});
			}
		}).catch(function() {
			bootbox.alert({
				message : globalReadDataFail,
				buttons : {
					ok : {
						label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
						className : 'btn-danger',
					}
				},
				callback: function() { }
			});
		}).finally(function() {
			$("#imgLoading").hide();
			$("#imgLoading2").hide();
		});
	};
	$scope.changeRole();
	
	$scope.changeSelectAllorNone = function(id) {
		angular.forEach($scope.RoleForm, function(item) {
			if (id == 1)
				item.ActionRead = $scope.selectReadAllorNone;
			else if (id == 2)
				item.ActionCreate = $scope.selectInsAllorNone;
			else if (id == 3)
				item.ActionUpdate = $scope.selectUpdAllorNone;
			else if (id == 4)
				item.ActionDelete = $scope.selectDelAllorNone;
			else if (id == 5)
				item.ActionSign = $scope.selectSignAllorNone;
			item.InsertOrUpdate = true;
		});
	};
	
	$scope.changeSelect = function(formid) {
		angular.forEach($scope.RoleForm, function(item) {
			if(formid == item.FormId)
				item.InsertOrUpdate = true;
		});
		
	};
	
	$scope.btnUpdate = function() {
		$("#imgLoading").show();
		$("#imgLoading2").show();
		$scope.showInsert = false;
		$scope.disableSelectReadAllorNone = true;
		$scope.disableSelectInsAllorNone = true;
		$scope.disableSelectUpdAllorNone = true;
		$scope.disableSelectDelAllorNone = true;
		$scope.disableSelectSignAllorNone = true;
		angular.forEach($scope.RoleForm, function(item) {			
			var strFormId = "disableSelectRead_" + item.FormId;
			$scope[strFormId] = true;
			var strFormId = "disableSelectIns_" + item.FormId;
			$scope[strFormId] = true;
			var strFormId = "disableSelectUpd_" + item.FormId;
			$scope[strFormId] = true;
			var strFormId = "disableSelectDel_" + item.FormId;
			$scope[strFormId] = true;
			var strFormId = "disableSelectSign_" + item.FormId;
			$scope[strFormId] = true;
		});
		
		var data = {
			RoleId : $scope.RoleId,
			RoleForm : $scope.RoleForm
		};
		$http.post('./api/s12/createOrupdate', data, csrf_config).then(function(response) {
			if (response.data.success) {
				bootbox.alert({
					message : response.data.msg,
					buttons : {
						ok : {
							label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
							className : 'btn-success',
						}
					},
					callback: function() {
						
					}
				});
			} else {
				bootbox.alert({
					message : response.data.msg,
					buttons : {
						ok : {
							label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
							className : 'btn-danger',
						}
					},
					callback: function() { }
				});				
			}
		}).catch(function() {
			bootbox.alert({
				message : globalUpdateDataFail,
				buttons : {
					ok : {
						label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
						className : 'btn-danger',
					}
				},
				callback: function() { }
			});			
		}).finally(function() {
			$scope.showInsert = true;
			$scope.disableSelectReadAllorNone = false;
			$scope.disableSelectInsAllorNone = false;
			$scope.disableSelectUpdAllorNone = false;
			$scope.disableSelectDelAllorNone = false;
			$scope.disableSelectSignAllorNone = false;
			angular.forEach($scope.RoleForm, function(item) {
				var strFormId = "disableSelectRead_" + item.FormId;
				$scope[strFormId] = false;
				var strFormId = "disableSelectIns_" + item.FormId;
				$scope[strFormId] = false;
				var strFormId = "disableSelectUpd_" + item.FormId;
				$scope[strFormId] = false;
				var strFormId = "disableSelectDel_" + item.FormId;
				$scope[strFormId] = false;
				var strFormId = "disableSelectSign_" + item.FormId;
				$scope[strFormId] = false;
				item.InsertOrUpdate = false;
			});
			$("#imgLoading").hide();
			$("#imgLoading2").hide();
		});
	};
	
	$rootScope.pageHeader = "角色權限資料維護";
	$scope.account_management_active = true;
	$scope.func_active = true;
	$scope.user_id = $cookieStore.get('user_id');
	$scope.func_role = $cookieStore.get('func_role');

	//$scope.btnSel = $scope.func_role.action_log.Sel;
	//$scope.btnIns = $scope.func_role.action_log.Ins;
	//$scope.btnUpd = $scope.func_role.action_log.Upd;
	//$scope.btnDel = $scope.func_role.action_log.Del;
	$scope.btnSel = false;
	$scope.btnIns = false;
	$scope.btnUpd = false;
	$scope.btnDel = false;
}

	
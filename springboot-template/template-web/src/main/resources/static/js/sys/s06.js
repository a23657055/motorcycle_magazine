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


function getAppController($rootScope, $scope, $http, $cookieStore, $anchorScroll, $location) {
	$scope.LocalActionCreate = actionCreateSaveBtn;
	$scope.LocalActionUpdate = actionUpdateSaveBtn;
	
	$scope.changeOrgType = function() {
		$scope.authType0 = false;
		$scope.authType1 = false;
		$scope.authType2 = false;
		if ($scope.OrgType == '0' || $scope.OrgType == '1' || $scope.OrgType == '3') {
			$scope.AuthType = '0';
			$scope.authType0 = true;
			$scope.authType1 = false;
			$scope.authType2 = false;
		} else if ($scope.OrgType == '2') {
			$scope.AuthType = '1';
			$scope.authType0 = false;
			$scope.authType1 = true;
			$scope.authType2 = true;
		}
	};
	$scope.changeOrgType();
	
	$scope.sendQueryData = function() {
		$scope.currentPage = 1;
		$scope.start = 0;
		$scope.queryData();
	};
	
	// Paging Start
	$scope.start = 0;
	$scope.currentPage = 1;
	$scope.maxRows = 5;
	$scope.maxPages = 0;
	$scope.total = 0;
	$scope.sorttype = "id";
	$scope.sortreverse = false;

	$scope.setSortName = function(sorttype) {
		$scope.sortreverse = (sorttype !== null && $scope.sorttype === sorttype) ? !$scope.sortreverse
				: false;
		$scope.sorttype = sorttype;
		$scope.currentPage = 1;
		$scope.start = 0;
		$scope.queryData();
	};

	$scope.maxRowsChange = function() {
		$scope.start = 0;
		$scope.currentPage = 1;
		$scope.queryData();
	};
	// Paging End

	// Clear Query Data Start
	$scope.clearData = function() {
		$scope.QueryId=null;
		$scope.QueryName=null;
		$scope.QueryCode=null;
		$scope.QueryOrgType=null;
		$scope.QueryAuthType=null;
		$scope.QueryTel=null;
		$scope.QueryFax=null;
		$scope.QueryCity=null;
		$scope.QueryTown=null;
		$scope.QueryAddress=null;
		$scope.QueryIsEnable=null;

		$scope.btnIns = false;
		$scope.btnUpd = false;
	};
	$scope.clearData();
	// Clear Query Data End
	
	// Query Data Start
	$scope.queryData = function(page) {
		$("#imgLoading").show();
		if (page) {
			$scope.start = (page - 1) * $scope.maxRows
		}
		else {
            $scope.start = 0;
            $scope.currentPage = 1;
        }
		if ($scope.QueryId == "")
			$scope.QueryId = null;
		if ($scope.QueryName == "")
			$scope.QueryName = null;
		if ($scope.QueryCode == "")
			$scope.QueryCode = null;
		if ($scope.QueryOrgType == "")
			$scope.QueryOrgType = null;
		if ($scope.QueryAuthType == "")
			$scope.QueryAuthType = null;
		if ($scope.QueryTel == "")
			$scope.QueryTel = null;
		if ($scope.QueryFax == "")
			$scope.QueryFax = null;
		if ($scope.QueryCity == "")
			$scope.QueryCity = null;
		if ($scope.QueryTown == "")
			$scope.QueryTown = null;
		if ($scope.QueryAddress == "")
			$scope.QueryAddress = null;
		if ($scope.QueryIsEnable == "")
			$scope.QueryIsEnable = null;
		
		var request = {
			start : $scope.start,
			maxRows : $scope.maxRows,
			sort : $scope.sorttype,
			dir : $scope.sortreverse,
			Id : $scope.QueryId,
			Name : $scope.QueryName,
			Code : $scope.QueryCode,
			OrgType : $scope.QueryOrgType,
			AuthType : $scope.QueryAuthType,
			Tel : $scope.QueryTel,
			Fax : $scope.QueryFax,
			City : $scope.QueryCity,
			Town : $scope.QueryTown,
			Address : $scope.QueryAddress,
			IsEnable : $scope.QueryIsEnable
			
		};
		$http.post('./api/s06/query', request, csrf_config).then(function(response) {
			$scope.allitems = response.data.datatable;
			$scope.total = response.data.total;
			$scope.maxPages = parseInt($scope.total / $scope.maxRows);
			$scope.pageRows = $scope.total % $scope.maxRows;
			if ($scope.pageRows != 0)
				$scope.maxPages++;
			$scope.returnTotal = true;
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
		});
	};
	$scope.queryData();
	// Query Data End
	
	// Switch to Edit(Insert) Mode Start
	$scope.openEdit  = function () {
		$("#divEdit").show();
		$("#divQuery").hide();
		 
		$scope.btnIns = true;
		$scope.btnUpd = false;
		
		$scope.Id = null;
		$scope.Name = null;
		$scope.Code= null;
		$scope.OrgType= '3';		 
		$scope.AuthType= '0';
		$scope.Tel= null;
		$scope.Fax= null;
		$scope.City= null;
		$scope.Town= null;
		$scope.Address= null;
		$scope.IsEnable= false;
		
		$("#twzipcode").twzipcode('reset');
	};
	// Switch to Edit(Insert) Mode End
	 
	// Switch to Query Mode Start
	$scope.closeEdit = function() {
		$("#divEdit").hide();
		$("#divQuery").show();
		$scope.btnIns = false;
		$scope.btnUpd = false;
	};
	// Switch to Query Mode End
	
	// Switch to Edit(Update) Mode Start
	$scope.editData = function(id) {

		bootbox.dialog({
			closeButton: false,
			message : '<i class="fas fa-fw fa-circle-notch fa-spin"></i>' + dataLoading 
		});
		var request = {
			Id : id
		};
		
		$http.post('./api/s06/query/id', request, csrf_config).then(function(response) {
			$scope.openEdit();
			$scope.btnIns = false;
			$scope.btnUpd = true;
			$scope.Id = response.data[0].Id;
			$scope.Name = response.data[0].Name;
			$scope.Code= response.data[0].Code;
			$scope.OrgType= response.data[0].OrgType;
			$scope.changeOrgType();
			$scope.AuthType= response.data[0].AuthType;
			$scope.Tel= response.data[0].Tel;
			$scope.Fax= response.data[0].Fax;
			$scope.City= response.data[0].City;
			$scope.Town= response.data[0].Town;
			$scope.Address= response.data[0].Address;
			$scope.IsEnable= response.data[0].IsEnable;

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
				callback: function() { }
			});
		}).finally(function() { });
	};
	// Switch to Edit(Update) Mode End
	
	// Insert Item Start
	$scope.createData = function() {
		var request = {
			Id : $scope.Id,
			Name : $scope.Name,
			Code : $scope.Code,
			OrgType : $scope.OrgType,
			AuthType : $scope.AuthType,
			Tel : $scope.Tel,
			Fax : $scope.Fax,
			City : $scope.City,
			Town : $scope.Town,
			Address : $scope.Address,
			IsEnable : $scope.IsEnable
		};
		$http.post('./api/s06/create', request, csrf_config).then(function(response) {
			if (response.data.success) {
				$scope.queryData($scope.currentPage);
				bootbox.alert({
					message : response.data.msg,
					buttons : {
						ok : {
							label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
							className : 'btn-success',
						}
					},
					callback: function() {
						$scope.closeEdit();
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
				message : globalInsertDataFail,
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
	// Insert Data End
	
	// Update Data Start
	$scope.updateData = function() {
		var request = {
				Id : $scope.Id,
				Name : $scope.Name,
				Code : $scope.Code,
				OrgType : $scope.OrgType,
				AuthType : $scope.AuthType,
				Tel : $scope.Tel,
				Fax : $scope.Fax,
				City : $scope.City,
				Town : $scope.Town,
				Address : $scope.Address,
				IsEnable : $scope.IsEnable
				
		};
//		console.log("datatable="+JSON.stringify(request));
		$http.post('./api/s06/update', request, csrf_config).then(function(response) {
			if (response.data.success) {
				$scope.queryData($scope.currentPage);
				bootbox.alert({
					message : response.data.msg,
					buttons : {
						ok : {
							label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
							className : 'btn-success',
						}
					},
					callback: function() {
						$scope.closeEdit();
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
		}).finally(function() { });
	};
	// Update Data End
	
	// Delete Item Start
	$scope.deleteData = function(id) {
		bootbox.confirm({
			message: globalSureDeleteItem,
			buttons: {
				confirm: {
					label : '<i class="fas fa-fw fa-check"></i>' + btnSure,
					className : 'btn-success'
				},
				cancel: {
					label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
					className : 'btn-default'
				}
			},
			callback: function(result) {
				if (result) {
					var request = {
						Id: id
					};
					$http.post('./api/s06/delete', request, csrf_config).then(function(response) {
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
									if ($scope.pageRows == 1 && $scope.currentPage > 1) {
										$scope.currentPage = $scope.currentPage - 1;
									}
									$scope.queryData($scope.currentPage);
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
							message : globalDeleteDataFail,
							buttons : {
								ok : {
									label : '<i class="fas fa-fw fa-times"></i>' + btnClose,
									className : 'btn-danger',
								}
							},
							callback: function() { }
						});
					}).finally(function() { });
				}
			}
		});
	};
	// Delete Item End
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


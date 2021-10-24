var myApp = angular.module('myApp', []).controller('getAppController',
		getAppController);

angular.element(document).ready(function() {
	angular.element(window).keydown(function() {
		if (event.keyCode == 13) {
			event.preventDefault();
			return false;
		}
	});
});

function getAppController($scope, $http, $window) {
	// Query Data Start
	$scope.queryData = function(page) {
		
		var request = {
			IsEnable : true,
			IsShow : $scope.QueryIsShow,
			Code : $scope.QueryCode,
			sort : "sort"
		};
		
		$http.post('./api/r00/query', request, csrf_config).then(function(response) {
			
			$scope.allitems = response.data.datatable;
			$scope.total = response.data.total;
	
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
		})
	};
	$scope.queryData();
    // Query Data End
    
    // Query Image Start
	$scope.queryImage = function() {
				
		var request = {
			dataType : "I",
			enable : "Y",
			title : "Images"
		};
		
		$http.post('./api/r00/queryImage', request, csrf_config).then(function(response) {
			
			for (var i = 0; i < response.data.datatable.length; i++) {
				if(response.data.datatable[i].location == "h1"){
            		$("#homepageBig").attr("src", response.data.datatable[i].content);
            	}
            	if(response.data.datatable[i].location == "c1"){
            		$("#change1").attr("src", response.data.datatable[i].content);
            	}
            	if(response.data.datatable[i].location == "c2"){
            		$("#change2").attr("src", response.data.datatable[i].content);
            	}
            	if(response.data.datatable[i].location == "c3"){
            		$("#change3").attr("src", response.data.datatable[i].content);
            	}
        	}
			
				//$("#homepageBig").attr("src", response.data.datatable[0].content);
			
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
		})
	};
	$scope.queryImage();
    // Query Image End
};
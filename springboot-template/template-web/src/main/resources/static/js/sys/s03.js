var myApp = angular.module('myApp', [ 'ngCookies', 'bw.paging', 'ui.toggle' ]).controller(
		'getAppController', getAppController);

angular.element(document).ready(function() {
	angular.element(window).keydown(function() {

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
	
	// Query Data Start
	$scope.queryDataList = function() {
		
		var request = {
			sort : "sort"
		};
		
		$http.post('./api/s03/queryList', request, csrf_config).then(function(response) {
			
			$scope.allitems = response.data.datatable;
			
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
	$scope.queryDataList();
    // Query Data End
    
    // Query location Start
	$scope.queryLocation = function() {
		var request = {
			functionCode : "IDM"
		};
		
		$http.post('./api/s03/queryLocation', request, csrf_config).then(function(response) {
			
			$scope.allLocation = response.data.datatable;
			
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
	$scope.queryLocation();
    // Query location End
    
    //Submit start
	$scope.submit = function(dataType) {
	
		$("#div1").hide();
		
		var enable = "";
		if($scope.IsEnable==true){
			enable = "Y";
		}else{
			enable = "N";
		}
		
		if(dataType=="A"){
			var request = {
				locationCode : $("#category").val(),
				title : $("#title").val(),
				dataType : dataType,
				releaseTime : $("#releaseTimeArticle").val(),
				enable : enable,
				content : editor.txt.html()
			};
		}else if(dataType=="I"){
			var request = {
				locationCode : $("#location").val(),
				dataType : dataType,
				releaseTime : $("#releaseTimeImage").val(),
				enable : enable,
				content : $("#img_upload_base").val()
			};
		}
		
		$http.post('./api/s03/submit', request, csrf_config).then(function(response) {
			if (response.data.success) {
				bootbox.alert({
					message : "發表成功",
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
					message : "發表失敗",
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
				message : "發表失敗",
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
	//Submit end
	
	$scope.closeEdit = function() {
		$("#div1").show();
	};
	
		// Query Data Start
	$scope.queryArticle = function() {
		
		var request = {
			category : "全球新車"
		};
		
		$http.post('./api/s03/queryArticle', request, csrf_config).then(function(response) {
			
			$scope.allitems = response.data.datatable;
			
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
	$scope.queryDataList();
    // Query Data End
    
    const E = window.wangEditor;
	const editor = new E("#div1");
	editor.config.lang = "en";
	editor.i18next = window.i18next;
	editor.config.height = 500
	
	//不需要的功能
    editor.config.excludeMenus = [
    	'strikeThrough','head','backColor','fontName',
        'foreColor','link','todo','indent','quote',
  		'video','table','code','splitLine'
    ]
	//全屏功能顯示
	editor.config.showFullScreen = false
	//上傳檔名
	editor.config.uploadFileName = 'file';
	//base64上傳圖片
	editor.config.uploadImgShowBase64 = true
	
	//editor.config.uploadImgShowBase64 = true
	
	//editor.config.uploadImgMaxSize = 2 * 1024 * 1024
	
	/*//自己定義上傳的方法
	editor.config.customUploadImg = function (resultFiles, insertImgFn) {
        // resultFiles 是 input 中選中的檔案列表
        // insertImgFn 是獲取圖片 url 後，插入到編輯器的方法
    	
    	//建立 FormData 物件來處理資料：鍵值對的形式，鍵可以重複
        var formData = new FormData();
        for (var i = 0; i < resultFiles.length; i++) {
            //將表單中的資料取出來，新增到file中
            formData.append("file", resultFiles[i]);
        }
    	
        //上傳圖片
        $http.post('./api/s03/uploadImg', file, csrf_config).then(function(response) {
        	if (response.data.errno == 0) {
        		for (var j = 0; j < response.data.length; j++) {
                        // 上傳圖片，返回結果，將圖片插入到編輯器中
                        insertImgFn(res.data[j]);
                 }
        	}else{
        		alert(response.data);
                return;
        	}
        })
        //使用AJAX上傳圖片
        $.ajax({
            url: 'http://localhost:8081/sys/api/s03/uploadImg',
            type: "POST",
            data: formData,
            async: false,//不傳送非同步請求
            cache: false,//瀏覽器將不快取此頁面
            //不設定Content-Type時，瀏覽器的值: application/x-www-form-urlencoded; charset=UTF-8
            //設定contentType: false時，瀏覽器的值：application/json
            contentType: false,
            processData: false,//我們需要傳送DOM資訊
            headers : csrf_config.headers,
            success: function (res) {
                //處理成功後
                if (res.errno == 0) {
                    for (var j = 0; j < res.data.length; j++) {
                        // 上傳圖片，返回結果，將圖片插入到編輯器中
                        insertImgFn(res.data[j]);
                    }
                } else {
                    alert(res.msg);
                    return;
                }
            }
        });
    }*/
    
	editor.create();
	
	$scope.changeDiv = function(divId) {
		if(divId=="article"){
			$("#divArticle").hide();
			$("#divImage").show();
		}else if(divId=="image"){
			$("#divImage").hide();
			$("#divArticle").show();
		}
	}
	
	$("#img_upload_file").change(function() {
        var file = this.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(file);//呼叫自帶方法進行轉換
        reader.onload = function(e) {
            $("#img_upload_show").attr("src", this.result);//將轉換後的編碼存入src完成預覽
            $("#img_upload_base").val(this.result);//將轉換後的編碼儲存到input供後臺使用
        }; 
    });
	
	
	
	
	
	
	
	
	
}
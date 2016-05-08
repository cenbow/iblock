app.controller('SignupFormControll', 
	[ '$scope','$stateParams','$cookies','get', 'post', 'Dict','$interval','LxNotificationService',
	function($scope, $stateParams, $cookie, get, post, Dict, $interval, LxNotificationService){
		$scope.input ={};
		$scope.options = {};
		$scope.options.gender = [1,2];
		$scope.input.role = 1;
		if($stateParams.role === "manager"){
			$scope.input.role = 2;
		}
		$scope.loadOptions= function(area){
        	var list = [];
        	if(Dict[area]){
            for(key in Dict[area]){
                list.push(Number(key));
            	}
        	}
        return list;
    	}
		$scope.lastSend = 30;
		$scope.options.province = $scope.loadOptions("province");
	    $scope.options.education = $scope.loadOptions("education");
	    $scope.options.city = $scope.loadOptions("city");
	    $scope.options.district = $scope.loadOptions("district");


		var validateCodeDelay;
		$scope.validateMobile= function(mobile){return mobile && mobile.match(/^1\d{10}$/);}
		$scope.validateValidationCode= function(code){return code && code.match(/^\w{4}$/);}
		$scope.sendValidationCode = function(code){
			$scope.allowValidateCode = true;
			$scope.lastSend =0;
			/*post("/user/send", $scope.input, function(response){
        		//TODO: add login alert
        		LxNotificationService.alert('注册成功', 
        			'注册成功，请登录',"确认",function(){

        				window.location.href = "/login.html";

        			});*/
			validateCodeDelay = $interval(function(){
				if($scope.lastSend > 30){
					$scope.stopInterval();
				}
				$interval.lastSend ++;
				console.log("test");
				$scope.lastSend = $scope.lastSend+1;
			},1000);

		}
		$scope.validatePassword = function(password){return password && 
			password.match(/^[\S]{8,}$/) && 
				password.match(/\d+/) && password.match(/[a-z]+/);}

		$scope.validateCorporateName = function(name){
			if(name===null || name=== undefined){return false;}
			if(name.trim().length <5)return false;
			return true;
		}
		$scope.validateCorporateBio= function(name){
			if(name===null || name=== undefined){return false;}
			if(name.trim().length < 20)return false;
			return true;
		}
		$scope.validateUsername = function(username){return username && username.trim().length >= 2 };
		$scope.stopInterval = function(){
			if(angular.isDefined(validateCodeDelay)){
				$interval.cancel(validateCodeDelay);
				validateCodeDelay = undefined;
			}
		}
		$scope.validateForm = function(){
			var inputdata = $scope.input;
			var valid = 

				$scope.validateUsername(inputdata.username) &&
				$scope.validatePassword(inputdata.password) &&
				$scope.validateMobile(inputdata.mobile) &&
				$scope.validateValidationCode(inputdata.validationCode) &&
				inputdata.gender!==undefined && inputdata.province!==undefined && inputdata.city!==undefined;

			if($scope.input.role===2){
				valid =valid && $scope.validateCorporateBio(inputdata.corporateBio) && 
				$scope.validateCorporateName(inputdata.corporateName);
			}
			return valid;
		}
		$scope.$on('$destroy', function() {
          // Make sure that the interval is destroyed too
          $scope.stopInterval();
        });
        $scope.signup = function(){
        	$scope.input.location = "";
        	post("/user/signup", $scope.input, function(response){
        		//TODO: add login alert
        		LxNotificationService.alert('注册成功', 
        			'注册成功，请登录',"确认",function(){

        				window.location.href = "/login.html";

        			});
        	},function(status, failmsg){
        		$scope.errorMessage = failmsg;
        	},function(status, error){
        		$scope.errorMessage = "注册失败，请刷新重新尝试";
        	})
        }
	}
]);
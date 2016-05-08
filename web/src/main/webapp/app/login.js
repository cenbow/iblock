
'use strict';



/* App Module */
var login = angular.module('login', 
    //Modules
    [
        'lumx',
        'ngCookies',
        'commonFilters',
        'rest'
     ]);


login.controller('LoginFormControll',  ['$scope', 'post', '$http', '$cookies','$filter',function ($scope,post,$http,$cookies, $filter) {
        $scope.input = {};
       
       if(!$filter('isEmptyOrNull')($cookies.get('userId'))) {
             window.location.href = '/';
       }


        //Check tokends and login information, if already logged in, redirect to the home page
        $scope.login = function(){
            post("user/login",
                { "userName":$scope.input.username ,"passwd": $scope.input.password},
                function(data){
                    var today = new Date();
                    var expireDate = new Date(today.valueOf() + 3*24*60*1000);
                    /*$cookies.put('userId', data.userId, {'expires': expireDate});
                    $cookies.put('userName', data.userName, {'expires': expireDate});
                    $cookies.put('role', data.role,{'expires': expireDate});*/
                    window.location.href = '/';
                },function(status, msg){
                    if(status === 403){
                        $scope.errorMessage = "用户名或密码不正确";
                    }else if (status && msg){
                        $scope.errorMessage = msg;
                    }else{
                        $scope.errorMessage = "登录失败，请重新尝试";
                    }

            });
        };

        $scope.forgetPassword = function(){
            window.location.href = 'forgetPassword.html';
        
        };

}]);

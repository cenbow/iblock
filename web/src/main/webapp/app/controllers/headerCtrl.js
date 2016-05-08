app.controller('HeaderController',  ['$scope','$stateParams', '$cookies',function ($scope,$stateParams,$cookies) {
        $scope.test = "abc";
        $scope.role = $cookies.get('role');
        $scope.username =$cookies.get('userName');
        $scope.search = {};
        if($stateParams.keyword){
            $scope.search.keyword = $stateParams.keyword;
        }
        $scope.logout = function(){
        	/*$cookies.remove('token');
            $cookies.remove('userId');
            $cookies.remove('role');*/
        	window.location.href = "/login.html";
        };
        $scope.searchKeyword= function(statename){
            if(statename==="app.designer"){
                window.location.href = "/designer?keyword="+ $scope.search.keyword;
            }else{
                window.location.href = "/projects?keyword=" + $scope.search.keyword;
            }
        }
    }]);
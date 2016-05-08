app.controller('ProjectListController',  ['$scope','$stateParams', 'LoadOptions','$filter','$http','post','get',
    function ($scope, $stateParams, LoadOptions,$filter,$http,post,get) {

    $scope.search = {"keyword":$stateParams.keyword,
                        "minPay": parseInt($stateParams.minPay),
                        "maxPay":parseInt($stateParams.maxPay),
                        "city":$stateParams.city?$stateParams.city.split(",").map(function(c){return parseInt(c);}):[],
                        "industry":$stateParams.industry?$stateParams.industry.split(",").map(function(c){return parseInt(c);}):[],
                        "pageSize":20,"orderBy":"startTime","order":"desc"
                    };

    $scope.options ={};
    $scope.options.city = LoadOptions("city");
    $scope.options.industry = LoadOptions("industry");

    $scope.onProjectsReturn = function(data){
        $scope.projects = data.result;
    };
    $scope.onProjectsLoadFailure =function (status, msg) {
        $scope.errorMessage =  "系统开了点小差，未返回结果, 请刷新";
        if(msg){
            $scope.errorMessage = msg;
        }
    }

    $scope.searchProject=function(pageNo,keyword){
        if(keyword && keyword.trim().length===0){
            $scope.search.keyword = keyword;   
        }
        $scope.search.pageNo = pageNo;
        post("project/search",$scope.search,$scope.onProjectsReturn,$scope.onProjectsLoadFailure);

    };

    $scope.searchProject(1,"");

}]);

app.controller('NewProjectController',['$scope','$filter','post',function ($scope, $filter,post) {
    $scope.vm = {}, $scope.project = {};
    $scope.options={};
    $scope.options.regions=[ {id:"JX",name:"江西",cities:[{"id":225,"name":"景德镇市"},{"id":163,"name":"南昌市"}]},
                            {id:"SH",name:"上海",cities:[{"id":289,"name":"上海市"}]}];
    $scope.options.industries = [{id:1, name:"建筑"},{id:2, name:"室内设计"},{id:3,name:"城市规划"}];
    $scope.validate = function(project){
        if(!project)return false;
        if($filter('isEmptyOrNull')(project.title) || $filter('isEmptyOrNull')(project.city)
            || $filter('isEmptyOrNull')(project.industry)||$filter('isEmptyOrNull')(project.description)){
            return false;
        }
        if (!$filter('withinLength')(project.description, 50,10000)){
            return false;
        }
        return true;

    }
    $scope.onProjectCreated = function (data) {
        if(data.id && data.id instanceof Number){
            window.location.href = '/project/'+data.id;
        }else{
            $scope.onProjectCreateFailed(); 
        }
    }

    $scope.onProjectCreateFailed = function(status,msg){
        //TODO
    }
    $scope.add = function(project){
        if($scope.validate(project)){
            var projectParam = 
                { "name":project.title ,"city": project.city.id,"industry":project.industry.id,
                "isLongTerm":project.isLongTerm,"desc":project.description};
            post("project/new",projectParam, $scope.onProjectCreated,$scope.onProjectCreateFailed);
        }
    }
}]);
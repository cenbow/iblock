app.controller('UserInfoController', [ '$scope','$http','$cookies','LxDialogService','EditModel','get', 'post', 'Dict',
    function ($scope, $http, $cookies, LxDialogService,EditModel,get, post, Dict){
    $scope.loadOptions= function(area){
        var list = [];
        if(Dict[area]){
            for(key in Dict[area]){
                list.push(Number(key));
            }
        }
        return list;
    }

    $scope.userId = $cookies.get('userId');

    $scope.options = {};
    $scope.options.province = $scope.loadOptions("province");
    $scope.options.education = $scope.loadOptions("education");
    $scope.options.industry = $scope.loadOptions("industry");
    $scope.options.city = $scope.loadOptions("city");
    $scope.options.skill = $scope.loadOptions("skill");
    $scope.exp ={};
   
    $scope.selectSearchFilter = function(newValue,area){
        var list = [];
        if(!newValue||newValue.length==0){
            $scope.options[area] =  $scope.loadOptions(area);
            return;
        }
        for(key in Dict[area]){
            if( Dict[area][key].toLowerCase().indexOf(newValue.toLowerCase()) >= 0){
                list.push(Number(key));   
            }
        }
        $scope.options[area] = list;
    }
    $scope.onUserInfoUpdateSuccess = function(type){console.info(type + " updated successfully!")};
    $scope.status = EditModel("user/updateUserInfo","status",function(){alert("failed")},$scope.onUserInfoUpdateSuccess);
    $scope.education = EditModel("user/updateUserInfo","education",function(){alert("failed")},$scope.onUserInfoUpdateSuccess);
    $scope.skills = EditModel("user/updateUserInfo","skills",function(){alert("failed")}, $scope.onUserInfoUpdateSuccess);
    $scope.geo = EditModel("user/updateUserInfo","geo",function(){alert("failed")},$scope.onUserInfoUpdateSuccess);
    $scope.industries = EditModel("user/updateWorkPrefs","industries");
    $scope.cities = EditModel("user/updateWorkPrefs","cities");
    $scope.minPay = EditModel("user/updateWorkPrefs","minPay");
    $scope.maxPay = EditModel("user/updateWorkPrefs","maxPay");

    $scope.editExperience = function(item){
            LxDialogService.open('experienceDialog');
            $scope.exp.errmsg = "";
            $scope.exp.current = {};
            $scope.exp.currentraw = item;
            for (p in item){
                 $scope.exp.current[p] = item[p];
                }
            $scope.exp.mode = "edit";
    };
    $scope.addExperience = function(){
            LxDialogService.open('experienceDialog');
            $scope.exp.errmsg = "";
            $scope.exp.current = {"time":0};
            $scope.exp.mode="add";
    };
    $scope.deleteExperience = function(index,item){
        if(item.id==null || item.id==""){
            return;
        }
        post("user/deleteWorkExperience",{"id":item.id},function(response){
            console.log($scope.exp.all);
            $scope.exp.all.splice(index,1);
        });
    };
    $scope.submitExpChange = function(item){
        if(!$scope.validateExpInput(item)){
                if($scope.exp.mode==="add"){
                    $scope.exp.mode="add2";
                }
                return;
        }
        if($scope.exp.mode==="edit"){
            post("user/updateWorkExperience",item,function(response){
                //option 1: set the item back to the all items
                for (p in item){
                 $scope.exp.currentraw[p] = item[p];
                }
                LxDialogService.close('experienceDialog');
                //option2: repull the data
                //get("user/getWorkExperience",null, function(response){
                //    $scope.exp.all = response.experience;
                //});
            },function(response){
                $scope.exp.errmsg =  "修改失败";
            });
        }else if ($scope.exp.mode === "add" || $scope.exp.mode === "add2" ){
            post("user/addWorkExperience", item,function(response){
                item.id = response.id;
                $scope.exp.all.push(item);
                LxDialogService.close('experienceDialog');
            },function(response){
                $scope.exp.errmsg =  "添加失败";
        })
        }
    };
    $scope.validateExpInput = function(item){
        if(!item.corporate || item.corporate.trim()===""){
            return false;
        }
        if(!item.time || isNaN(item.time)){
            return false;
        }
        if(!item.industry){
            return false;
        }
        if(!item.desc || item.desc.trim()===""){
            return false;
        }
        return true;
    };
    ($scope.userInfoRefresh = function(){
        $scope.isComplete = true;
        get("user/getUserInfo/"+$scope.userId,null,function(response){
            $scope.education.load(response.education);
            $scope.skills.load(response.skills);
            $scope.geo.load(response.geo);
            $scope.username =response.username;
            $scope.role = response.role;
            $scope.contactPhone = response.contactPhone;
            $scope.status.load(response.status);
        });
        if($cookies.get('role')=="1"){
            get("user/getWorkPrefs/"+$scope.userId,null,function(response){
                $scope.industries.load(response.industries);
                $scope.cities.load(response.cities);
                $scope.minPay.load(response.minPay);
                $scope.maxPay.load(response.maxPay);
            });
            get("user/getWorkExperience/"+$scope.userId,null, function(response){
                $scope.exp.all = response.experience;

            }); 
        }
    })();
    
}]);

app.controller('DesignerListController',  ['$scope', '$stateParams','$http', '$cookies', 'LoadOptions','$location',function ($scope,$stateParams, $http,$cookies,LoadOptions,$location) {
    $scope.search = {"keyword":$stateParams.keyword,
                        "minPay": parseInt($stateParams.minPay),
                        "maxPay":parseInt($stateParams.maxPay),
                        "city":$stateParams.city?$stateParams.city.split(",").map(function(c){return parseInt(c);}):[],
                        "industry":$stateParams.industry?$stateParams.industry.split(",").map(function(c){return parseInt(c);}):[],
                        "skill":$stateParams.skill?$stateParams.skill.split(",").map(function(c){return parseInt(c);}):[],
                        "pageSize":20,"orderBy":"startTime","order":"desc"
                    };

    $scope.options ={};
    $scope.options.city = LoadOptions("city");
    $scope.options.industry = LoadOptions("industry");
    $scope.options.skill = LoadOptions("skill");
    $scope.pageNo = 1;
    $scope.pageSize = 1;
    $scope.pageFirst = [1,2,3];
    $scope.pageLast = [20];
    $scope.pageMiddle = [10,11,12];
    $scope.getURL = function(params){
        var queryStr = "?";
        for(key in params){
            if(params[key]){
                queryStr += key+"="+params[key] + "&";
            }
        }
       return queryStr.slice(0,-1);

    }
    $scope.organizePagination = function(){

        $scope.pageLast = [];
        $scope.pageMiddle = [];
         $scope.pageFirst = [];
        if($scope.pageSize <= 5){
            $scope.firstDot = false;
            $scope.secondDot = false;
            for(var i = 1; i <= $scope.pageSize ; i++){
                $scope.pageFirst.push(i);
            }
            return;
        }

        $scope.firstDot = true;
        $scope.secondDot = true;

        if($scope.pageNo <= 3){
            $scope.pageFirst = [];
            for(var i = 1; i <= $scope.pageNo; i++){
                $scope.pageFirst.push(i);
            }
            $scope.firstDot = false;
        }else{
            $scope.pageFirst = [1];
        }
        if($scope.pageNo >= $scope.pageSize -2){

            $scope.pageLast = [];
            for(var i = $scope.pageNo; i <= $scope.pageSize; i++){
                $scope.pageLast.push(i);
            }
            $scope.secondDot = false;

        }else{
            $scope.pageLast = [$scope.pageSize];
        }
        if ($scope.pageNo > 3 && $scope.pageNo < $scope.pageSize -2){
            $scope.pageMiddle = [$scope.pageNo-1, $scope.pageNo, $scope.pageNo+1];
        }
    }
     $scope.searchUser=function(pageNo){
        
        $scope.search.pageNo = pageNo;
        $http.post(
                    "service/user/searchDesigner",$scope.search
                ).then(function(response){
            if(response!=null && response.data && response.data.status === 0){
                $scope.errorMessage = "";
               $scope.users = response.data.data.result;
               //$location.path($location.path()+ $scope.getURL($scope.search));
                $scope.pageNo = response.data.data.pageNo;
                $scope.pageSize = response.data.data.pageSize;
                $scope.organizePagination();
            }else if(response ==null){
                $scope.errorMessage =  "系统开了点小差，未返回结果, 请刷新";
            } else {
                 $scope.errorMessage = response.msg;
            }
                //Success

        },function(response){

        });
    }
    $scope.searchUser(1);
    $scope.employ=function(id){
        

    }
        
}]);

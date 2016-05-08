var commonFilters = angular.module('commonFilters', []);

commonFilters.filter('trimToEmpty', function(){
  return function(input){
  	if(typeof input === "undefined"){
  		return "";
  	}
  	if(input === null){
  		return "";
  	}
  	return input.trim();
  };
  });


commonFilters.filter('isEmptyOrNull', function(){
  return function(input){
  	if(typeof input === "undefined"){
  		return true;
  	}
  	if(input === null){
  		return true;
  	}
    if (input instanceof String){
      return input.trim()===""; 
    }
    return false;
  }
});
commonFilters.filter('isNaN',function(){
  return function(input){
    return isNaN(input);
  }
})

commonFilters.filter('withinLength', function(){
  return function(input, min, max){
    if(typeof input === "undefined"){
      return false;
    }
    if(input === null){
      return false;
    };
    if(min && input.trim().length <min)return false;
    if(max && input.trim().length > max)return false;
    return true;
  }
});
commonFilters.filter('negative',function(){
  return function(input){
    return !input;
  }
});

commonFilters.value("Dict",{
  "education":{
    1:"专科",
    2:"本科",
    3:"硕士研究生",
    4:"博士研究生"
  },
  "skill":{
    1:"CAD",
    2:"Photoshop",
    3:"园林设计",
    4:"环卫工程",
    5:"Maya"
  },
  "city":{
    1:"上海",
    2:"北京",
    3:"景德镇",
    4:"新余"
  },
  "province":{
    1:"上海",
    2:"北京",
    3:"江西"
  },
  "district":{
    1:"浦东新区",
    2:"杨浦区",
    3:"徐汇区",
    4:"朝阳区",
    5:"昌平区",
    6:"昌江区",
    7:"珠山区"
  },
  "role":{
    1:"经纪人",
    2:"设计师",
    3:"项目经理",
    4:"管理员"
  },
  "industry":{
    1:"土木工程",
    2:"建筑设计",
    3:"园林设计",
    4:"环卫工程"
  },
  "gender":{
    1:"女",
    2:"男"
  }
});
commonFilters.value("GeoMap",
{"cities":{
  1:[1],
  2:[2],
  3:[3,4]
},"districts":{
  1:[1,2,3],
  2:[4,5],
  3:[6],
  4:[7]
}});
commonFilters.factory("LoadOptions",["Dict",function(Dict){
  return function(area){
        var list = [];
        if(Dict[area]){
            for(key in Dict[area]){
                list.push(Number(key));
            }
        }
        return list;
    }

  }]);
commonFilters.factory("SelectSearchFilter",["Dict","LoadOptions",function(Dict,LoadOptions){
  return function(newValue,area,options){
        var list = [];
        if(!newValue||newValue.length==0){
            options[area] =  LoadOptions(area);
            return;
        }
        for(key in Dict[area]){
            if( Dict[area][key].toLowerCase().indexOf(newValue.toLowerCase()) >= 0){
                list.push(Number(key));   
            }
        }
        options[area] = list;
    }
}]);
commonFilters.filter('getName',["Dict",function(Dict){
  return function(input,area){
    if(Dict[area]){
      return Dict[area][input]; 
    }
  }
}]);
commonFilters.filter('getCities',["GeoMap",function(GeoMap){
  return function(province){
    return GeoMap.cities[province];
  }
}]);
commonFilters.filter('getDistricts',["GeoMap",function(GeoMap){
  return function(city){
    return GeoMap.districts[city];
  }
}])

var iblockFilters = angular.module('iblockFilters',[]);
var projectStatus = ["创建中", "审核中","招募中","进行中","阶段验收中","收尾中","归档中","已完成"];
iblockFilters.filter('projectStatusName',function(){
  return function(input){
    if(input < projectStatus.length){
      return "项目" + projectStatus[input];
    }
    return "";
  }
})
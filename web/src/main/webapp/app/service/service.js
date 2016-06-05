var userServices = angular.module('userServices', ['ngResource']);
var projectServices = angular.module('projectServices', ['ngResource']);

userServices.factory('Login', ['$resource',
  function($resource){
    return $resource('service/user/logon');
  }]);

projectServices.factory('Projects',['$resource',
	function($resource){
		return $resource('service/project/search');
	}
]);

var restServices = angular.module('rest',[]);
//restServices.value("servieBasePath","http://192.168.147.106:8080/");
restServices.value("servieBasePath","/");

restServices.factory('post', ['$http','servieBasePath',function($http, servieBasePath){
	return function(servicePath,urlObject,success,fail,error){
		$http.post(servieBasePath + servicePath,urlObject,{"withCredentials":true})
		.then(
			function successCallback(response){
				if(response&&response.data){
					if(response.data.status === 0){
	                 	if(success){
	                 		success(response.data.data);
	                 	}
	                 	return;
	                 }
	                 if(fail){
	                 	fail(response.data.status,response.data.msg);
	                 }
                 }
			},function errorCallback(response){
				if(error){
					error();
				}else if(fail){
					fail();
			}
		});
	};
}]);

restServices.factory('get', ['$http','servieBasePath',function($http, servieBasePath){
	return function(servicePath,urlObject,success,fail,error){
		$http.get(servieBasePath + servicePath,urlObject).then(
			function successCallback(response){
				if(response&&response.data){
					if(response.data.status === 0){
	                 	if(success){
	                 		success(response.data.data);
	                 	}
	                 	return;
	                 }
	                 if(fail){
	                 	fail(response.data.status,response.data.msg);
	                 }
                 }
			},function errorCallback(response){
				if(error){
					error();
				}else if(fail){
					fail();
			}
		});
	};
}]);

function EditableModel(type){
 	this.metaname = type;
    this.editMode = false;
    //this.value = value;
    //this.editvalue = this.value;
}

var definedServices =  angular.module("DefinedServices",[]);
definedServices.factory("EditModel",["post",function(post){
	return function(urlPath, type,failCallback,updateCallback){
		var model = new EditableModel(type);
		model.load = function(value){
			this.value = value;
			if(!value||value.length === 0 ){
				this.editMode = true;
			}
		}
		model.edit = function(){
			this.editMode = true;
			if(this.value instanceof Array){
				this.editvalue = this.value.slice(0);
			}else if(this.value instanceof String){
				this.editvalue = this.value;
			}else if(this.value instanceof Object){
				this.editvalue = {};
				for (p in this.value){
					this.editvalue[p] = this.value[p];
				}
			}else{
				this.editvalue = this.value;	
			}
		}
		model.cancel =function(){
			this.editMode = false;
		}
		model.noeditUpdate = function(){
			var param = {};
			var self = this;
			param[this.metaname] = this.value;
			post(urlPath, param, function(){
				if(updateCallback){
					updateCallback(self.metaname);
				}
			},failCallback);
		}
		model.update = function(){
			var self = this;
			if(self.editvalue == self.value){
				self.editMode = false;
				return;
			}
			var param = {};
			param[self.metaname] = self.editvalue;
			post(urlPath, param, function(){

				if(this.value instanceof Array){
					self.value = self.value.slice(0);
				}else{
					self.value = self.editvalue;	
				}
				self.editMode = false;
				if(updateCallback){
					updateCallback(self.metaname);
				}
			},failCallback);
		}
		model.delete = function(index){
			if(this.value instanceof Array){
				this.editvalue = this.value.slice(0);
				this.editvalue.splice(index,1);
				this.update();
			}
		}
		return model;
	}
}]);


/*
modelService.service("UserModel", ["get","post", function(get,post){
	return {
		create:function(type,value,userid,servicePath,updateCallback){
			var model = new EditableModel(type,value,isArray);
			model.update = function(){
				post(servicePath,{
						"id":userid,
						type:this.editvalue
					},function(){
					this.value = 
					this.value = this.editvalue;
					updateCallback();
				});

			};
			model.delete = function(index){
				if(this.value instanceof Array){
					var tmpValue = this.value;
					var tmpValue.remove(index);
					post(servicePath,{
						"id":userid,
						type:tmpValue
					},function(){
						this.value = this.tmpValue;
						this.editvalue = this.value;
						updateCallback();
					});
				}
			};
		}
	}
}]);*/
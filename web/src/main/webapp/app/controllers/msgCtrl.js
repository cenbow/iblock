app.controller('MsgController', [ '$scope','$cookies','EditModel','get', 'post', 'Dict',
	function($scope,$cookies,EditModel,get,post,Dict){
		$scope.messages = [{
			"status": 0,
			"sendTime":"2015-12-12 12:01:21",
			"type":1,//0: 只读, 1:任务
			"msg":"{贾翕|user?id=12} 请求解除 {项目名称|project?id=12} 阶段二与 {方璐|user?id=11} 的合约",
			"service":"project/step//acceptRemoveHiring",
			"postparams":{
				"hireid":12
				}
			},
			{
			"status": 1,
			"sendTime":"2015-12-12 12:01:21",
			"type":1,//0: 只读, 1:任务
			"msg":"{贾翕|user?id=12} 请求解除 {项目名称|project?id=12} 阶段二与 {方璐|user?id=11} 的合约",
			"service":"project/step//acceptRemoveHiring",
			"postparams":{
				"hireid":12
			}
		}];
		$scope.accept = function(msg){
			var params = msg.postparams;
			params.msgid = msg.id;
			//params.
			post(msg.service,msg.postparams,function(){

			})
		}
		$scope.pageNo = 1;

		$scope.getMsg = function(pageNo, status){
			if(pageNo===undefined){
				var pageNo = 1;
			}
			var query = {"pageNo":pageNo,"pageSize":20,"orderBy":"startTime","order":"desc"};
	
			if(status === 0 || status ===1){
				query.status = status
			}
			get("user/messages",query,function(response){
				$scope.messages = response.results;
				$scope.pageNo = response.pageNo;

			});
		}
	
}]);

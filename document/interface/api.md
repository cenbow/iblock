<!-- MarkdownTOC -->

- [用户 /user](#用户-user)
    - [管理用户 /user/admin \(权限仅限管理员\)](#管理用户-useradmin-权限仅限管理员)
        - [管理员登录 /user/admin/login](#管理员登录-useradminlogin)
        - [添加用户 /user/admin/add](#添加用户-useradminadd)
        - [删除用户 /user/admin/delete/:id](#删除用户-useradmindeleteid)
        - [冻结 /user/admin/freeze/:id](#冻结-useradminfreezeid)
        - [解冻 /user/admin/unfreeze/:id](#解冻-useradminunfreezeid)
    - [注册 /user/signup](#注册-usersignup)
        - [项目经理注册 /user/signup/manager](#项目经理注册-usersignupmanager)
        - [设计师注册 /user/signup/designer](#设计师注册-usersignupdesigner)
    - [登录 /user/login](#登录-userlogin)
    - [基本信息 /user/info/](#基本信息-userinfo)
        - [获取基本信息 /user/info/:id](#获取基本信息-userinfoid)
        - [更新基本信息 /user/updateUserInfo](#更新基本信息-userupdateuserinfo)
    - [工作经历 /user/workexp](#工作经历-userworkexp)
        - [获取工作经历 /user/workexp/:id](#获取工作经历-userworkexpid)
        - [添加工作经历 /user/addWorkexp](#添加工作经历-useraddworkexp)
        - [删除工作经历 /user/deleteWorkexp](#删除工作经历-userdeleteworkexp)
    - [工作偏好 /user/workpref](#工作偏好-userworkpref)
        - [获取基本信息 /user/workpref/:id](#获取基本信息-userworkprefid)
        - [更新基本信息 /user/updateWorkpref](#更新基本信息-userupdateworkpref)
    - [账户/user/account](#账户useraccount)
        - [修改手机号 /user/account/resetMobile](#修改手机号-useraccountresetmobile)
        - [修改密码 /user/account/resetPassword](#修改密码-useraccountresetpassword)
        - [修改头像 /user/account/changeAvatar](#修改头像-useraccountchangeavatar)
- [用户列表 /users](#用户列表-users)
    - [搜索设计师 /users/designers/search](#搜索设计师-usersdesignerssearch)
    - [推荐设计师 /users/designers/recommended](#推荐设计师-usersdesignersrecommended)
    - [热门设计师 /users/designers/hot](#热门设计师-usersdesignershot)
    - [员工用户 /users/admin](#员工用户-usersadmin)
    - [冻结用户 /users/freezed](#冻结用户-usersfreezed)
- [元数据](#元数据)
    - [城市 /meta/city/search?q=:keyword](#城市-metacitysearchqkeyword)
    - [区域 /meta/district/:citycode](#区域-metadistrictcitycode)
    - [技能 /meta/skill/all](#技能-metaskillall)
        - [/meta/skill/add](#metaskilladd)
        - [/meta/skill/delete](#metaskilldelete)
    - [行业 /meta/industry/all](#行业-metaindustryall)
        - [/meta/industry/add](#metaindustryadd)
        - [/meta/industry/delete](#metaindustrydelete)
- [项目 /project/](#项目-project)
    - [创建项目 /project/new](#创建项目-projectnew)
    - [获取项目 /project/details/:id](#获取项目-projectdetailsid)
    - [修改项目 /project/update/:id](#修改项目-projectupdateid)
    - [评分 /project/rate](#评分-projectrate)
    - [项目状态](#项目状态)
        - [分配经纪人 /project/addbroker](#分配经纪人-projectaddbroker)
        - [审批项目  /project/approve](#审批项目--projectapprove)
        - [提交项目  /project/resubmit](#提交项目--projectresubmit)
        - [启动项目  /project/start](#启动项目--projectstart)
        - [结束项目  /project/end](#结束项目--projectend)
        - [终止项目  /project/terminate](#终止项目--projectterminate)
- [项目列表 /projects/](#项目列表-projects)
    - [搜索项目 /projects/search](#搜索项目-projectssearch)
    - [推荐项目 /projects/recommended](#推荐项目-projectsrecommended)
    - [最新项目 /projects/latest](#最新项目-projectslatest)
    - [我的项目 /projects/mine](#我的项目-projectsmine)
    - [已终止项目 /projects/terminated \(Admin only\)](#已终止项目-projectsterminated-admin-only)
- [作品列表 /works](#作品列表-works)
    - [获取作品 /works/:userid  \(GET\) \(权限:所有已登陆用户\)](#获取作品-worksuserid--get-权限所有已登陆用户)
- [作品 /work](#作品-work)
    - [删除作品 /work/delete/:workid](#删除作品-workdeleteworkid)
    - [添加作品 /work/add](#添加作品-workadd)
- [文件 /file](#文件-file)
    - [上传图片 /file/image/new](#上传图片-fileimagenew)
    - [删除图片 /file/image/delete](#删除图片-fileimagedelete)
- [消息列表 /messages/](#消息列表-messages)
    - [消息未读数 /messages/unread](#消息未读数-messagesunread)
    - [未处理消息 /messages/unprocessed](#未处理消息-messagesunprocessed)
    - [历史消息 /messages/history](#历史消息-messageshistory)
- [消息 /message/](#消息-message)
    - [标记已读 /message/markasread/:msgid](#标记已读-messagemarkasreadmsgid)
    - [公告消息 /message/admin/new](#公告消息-messageadminnew)

<!-- /MarkdownTOC -->


# 用户 /user

## 管理用户 /user/admin (权限仅限管理员)

### 管理员登录 /user/admin/login


Request
```json
{
    "username":""
    "password":""
}
```

Response

```json
{
    "status":"",
    "msg":"",
      "data":{
        "role":1,
        "username":"方璐",
        "id":1
      }
}
```


### 添加用户 /user/admin/add

**ROLE: 管理员**

Request:
```json
{
    "mobile":"",
    "username":"",
    "password":"",
    "role":3
}
```


Response:
```json
{
    "status": 0,
    "msg":"添加成功",
    "data":{
        "id":1,
        "mobile":"",
        "username":"",
        "password":"",
        "role":3
    }
}
```


失败情况: 1) mobile已存在 409  2) 参数错误 406(密码不合格) 3) 内部错误,添加失败 500 4) 403 权限

3 表示经纪人 4 表示管理员 不允许添加项目经理或设计师)

管理员不需要添加手机号(TBD)


### 删除用户 /user/admin/delete/:id

Request body empty

Response:
```json
{
    "status": 0,
    "msg":"删除成功"
}
```

Failures: 404:删除id不存在 , 500 


### 冻结 /user/admin/freeze/:id
Response
```json
{
    "status": 0,
    "msg":"冻结成功"
}
```

失败情况: 1) 用户不存在 404 2) 用户已处冻结状态 412 3) 内部错误 500


### 解冻 /user/admin/unfreeze/:id

Response
```json
{
    "status": 0,
    "msg":"解冻成功"
}
```

失败情况: 1) 用户不存在 404  2) 用户未处冻结状态 412 3) 内部错误 500


## 注册 /user/signup

### 项目经理注册 /user/signup/manager
```json
{
    "username":"",
    "password":"",
    "mobile":"",
    "gender":0,  // 0为女，1为男
    "verifyCode":"",
    "geo": {
      "city":{"id":202,"name":"上海市"},
      "district": "杨浦区",
      "address": "五角场",
      "lat":"",
      "lng":""
    },
   "corporateName":"设计有限公司",
   "corporateBio":"公司简介"
}
```

### 设计师注册 /user/signup/designer

Request:

```json
{
    "username":"",
    "password":"",
    "mobile":"",
    "gender":1， // 0为女，1为男
    "verifyCode":"",
    "geo": {
      "city":{"id":202,"name":"上海市"},
      "district": "杨浦区",
      "address": "五角场",
      "lat":"",
      "lng":""
    }
}
```

Response:
```json
{
    "status":0,
    "msg":"注册成功"
}
```

Failures: 409:手机号已注册, 406:密码不合格, 403:验证码不正确


## 登录 /user/login

Request-header

```json
{
    "mobile":""
    "password":""
}
```

Response

```json
{
    "status":""
    "msg":"",
    "data":{
        "role":1,
        "username":"方璐",
        "id":1
      }
}
```

Failures: 403:密码或用户名不正确


## 基本信息 /user/info/
### 获取基本信息 /user/info/:id
### 更新基本信息 /user/updateUserInfo

## 工作经历 /user/workexp
### 获取工作经历 /user/workexp/:id
### 添加工作经历 /user/addWorkexp
### 删除工作经历 /user/deleteWorkexp

## 工作偏好 /user/workpref
### 获取基本信息 /user/workpref/:id
### 更新基本信息 /user/updateWorkpref

## 账户/user/account

### 修改手机号 /user/account/resetMobile

Request:
```json

{
    "mobile":"",
    'verifyCode':"",
    "password":""
}

```

Response:
1) 403:验证码不正确 2) 密码不正确 

### 修改密码 /user/account/resetPassword
Request:
```json
{
    "oldpassword":"",
    "newpassword":""
}
```

### 修改头像 /user/account/changeAvatar

```json
{
    "avatar":"/img/head2.jpg"
}
```

# 用户列表 /users

## 搜索设计师 /users/designers/search
```json
{
    "role":,
    "pageNo":
    "pageSize":   
}
```

## 推荐设计师 /users/designers/recommended
## 热门设计师 /users/designers/hot
## 员工用户 /users/admin
## 冻结用户 /users/freezed




# 元数据

## 城市 /meta/city/search?q=:keyword
## 区域 /meta/district/:citycode
## 技能 /meta/skill/all
### /meta/skill/add
### /meta/skill/delete
## 行业 /meta/industry/all
### /meta/industry/add
### /meta/industry/delete


# 项目 /project/
## 创建项目 /project/new
## 获取项目 /project/details/:id 
## 修改项目 /project/update/:id 
## 评分 /project/rate

## 项目状态
### 分配经纪人 /project/addbroker
### 审批项目  /project/approve
### 提交项目  /project/resubmit
### 启动项目  /project/start
### 结束项目  /project/end
### 终止项目  /project/terminate 

# 项目列表 /projects/
## 搜索项目 /projects/search
## 推荐项目 /projects/recommended
## 最新项目 /projects/latest
## 我的项目 /projects/mine
## 已终止项目 /projects/terminated (Admin only)


# 作品列表 /work
## 获取作品 /work/:userid  (GET) (权限:所有已登陆用户)

RESPONSE:
```json
{
    "status":0,
    "message":"获取成功",
    "data": {
        "pageNo": 1,
        "pageSize": 20,
        "totalCount":100,
        "result":[{
        "id":1,
          "title":"The Golden Gate",
          "desc":"Los Angelas",
          "imgUrl":"/img/works_1.jpg"
        },{
        "id":2,
          "title":"Material Design ",
          "desc":"Design work for the material style",
          "imgUrl":"/img/works_2.jpg"
        }]
    }
}

```

# 作品 /work
## 删除作品 /work/delete/:workid
Response:
```json
{
    "status":0,
    "msg":""
}
```

Failures: 404:作品不存在, 403:没有权限, 500:内部错误

## 添加作品 /work/add
Request:
```json
{
    "title":"Material Design ",
    "desc":"Design work for the material style",
    "imgUrl":"/img/works_2.jpg"
}
```

Response:
```json
{
    "status":0,
    "msg":""
}
```

Failures:  403:没有权限, 500:内部错误


# 文件 /file

## 上传图片 /file/image/new
Request:

File Form Data:
"file":file object

Response:
```json
{
    "status":0,
    "msg":""
}
```

Failures: 500 上传失败

## 删除图片 /file/image/delete
```json
{
    "imgUrl":""
}
```

Failures: 403:没有权限, 500:内部错误


# 消息列表 /messages/

## 消息未读数 /messages/unread

Response
```json
{
    "status":0,
    "msg":"",
    "data":{
        "count":15
    }
}
```

## 未处理消息 /messages/unprocessed

Response
```json
{
    "status":0,
    "msg":"",
    "data":{
        "pageNo": 1,
        "pageSize": 20,
        "totalCount":22,
        "result":[{
            "status": 0,
            "readStatus":0,
            "sendTime":"2015-12-12 12:01:21",
            "type":2,
            "msg":"[评分]请为项目 {项目名称|project/12}  的 经纪人 {贾翕|user/12} 进行评分",
            "service":"project/rate",
            "postparams":{
                "userid":3
            }
        },{
            "status": 1,
            "readStatus":1,
            "sendTime":"2015-12-12 12:01:21",
            "type":2,
            "msg":"[评分]您为项目 {项目名称|project/12}  的 经纪人 {贾翕|user/12} 已评分",
            "data":2
        },{
            "status": 0,
            "readStatus":0,
            "sendTime":"2015-12-12 12:01:21",
            "type":1,
            "msg":"[任务]{贾翕|user/12} 创建了项目 {项目名称|project/12}，请审批该项目",
            "service":"project/approve",
            "inputParam":{
                "name":"reason",
                "label":"理由"
            },
            "postparams":{
                "id":12
            }
        },{
            "status": 1,
            "readStatus":1,
            "sendTime":"2015-12-12 12:01:21",
            "type":1,
            "msg":"[任务]您已通过了{贾翕|user/12} 创建的项目 {项目名称|project/12}",
            "data":"accepted"
        },{
            "status": 0,
            "readStatus":0,
            "sendTime":"2015-12-12 12:01:21",
            "type":0,
            "msg":"[消息]公告：系统后台于本周五 13:00-21:00 进行维护和更新，敬请谅解"

        }]
    }
}

```

type: 0:只读消息 1:接受/拒绝 任务 2:评分
readStatus: 0:未读 1:已读,
status: 0:未处理 1:已处理

任务后台更新任务／评分状态和内容

## 历史消息 /messages/history
同上

# 消息 /message/
## 标记已读 /message/markasread/:msgid
```json
{
    "status":0,
    "msg":""
}
```

更新为已读

## 公告消息 /message/admin/new
POST URL: /message/admin/new

ROLE:管理员

Request:
```json
{
    "role":1,
    "content":"dsfsdf\nfsd\n"
}

```

Response:
```json
{
    "status":0,
    "message":"发送成功"
}
```

0:发送给所有人 , 1-3 分别发送给各自角色

Failures: 1) 403:无权限 

<!-- MarkdownTOC -->

- [1 概述 （admin\)](#1-概述-（admin)
- [2 用户管理 （admin/user\)](#2-用户管理-（adminuser)
    - [2.1 添加管理员或经纪人](#21-添加管理员或经纪人)
    - [2.2 删除管理员或经纪人](#22-删除管理员或经纪人)
    - [2.3 查看用户状态](#23-查看用户状态)
    - [2.4 冻结用户](#24-冻结用户)
    - [2.5 解冻用户](#25-解冻用户)
- [3 项目管理 \(admin/project\)](#3-项目管理-adminproject)
    - [3.1 查看新创建项目列表](#31-查看新创建项目列表)
    - [3.2 分配项目经纪人](#32-分配项目经纪人)
    - [3.3 查看所有冻结项目](#33-查看所有冻结项目)
    - [3.4 冻结项目](#34-冻结项目)
    - [3.5 解冻项目](#35-解冻项目)
- [4 字典管理 \(admin/meta\)](#4-字典管理-adminmeta)
    - [4.1 查看所有元数据 （似乎和user/skills , user/industries 重复了?\)](#41-查看所有元数据-（似乎和userskills--userindustries-重复了)
    - [4.2 添加元数据](#42-添加元数据)
    - [4.3 删除元数据](#43-删除元数据)
- [5 站内信 \(admin/message\)](#5-站内信-adminmessage)
    - [5.1 发送全站站内信](#51-发送全站站内信)

<!-- /MarkdownTOC -->

# 1 概述 （admin)
后台仅管理员可登录，管理员职责： 增删管理员、经纪人，冻结解冻用户
分配经纪人项目，冻结解冻删除项目，数据字典管理


# 2 用户管理 （admin/user)
## 2.1 添加管理员或经纪人
URL: admin/user/add
POST
Request:
```json
{
    "userid":"",
    "username":"",
    "password":"",
    "role":3
}
```

Response:
```json
{
    "status": 0,
    "msg":"添加成功"
}
```


失败情况: 1) userid已存在  2) 参数错误 3) 内部错误,添加失败

3 表示经纪人 4 表示管理员 （不允许添加项目经理或设计师)

经纪人通过前台登录，个人用户信息和项目经理除了公司资料外一致。可以查看我的项目。(项目审核由经纪人负责)

## 2.2 删除管理员或经纪人
POST URL: admin/user/delete/:id


Request body empty

Response:
```json
{
    "status": 0,
    "msg":"删除成功"
}
```

## 2.3 查看用户状态
GET URL: admin/user/status/:id


Response
```json
{
    "status": 0,
    "msg":"返回成功",
    "data":{
        "status":0
    }
}
```

0:正常 1:冻结
?? 用户状态和在线状态?
失败情况: 1) 用户不存在 2) 内部错误


## 2.4 冻结用户

POST URL: admin/user/freeze/:id


Response
```json
{
    "status": 0,
    "msg":"冻结成功"
}
```

失败情况: 1) 用户不存在 2) 用户已处冻结状态 3) 内部错误

## 2.5 解冻用户

POST URL: admin/user/unfreeze/:id


Response
```json
{
    "status": 0,
    "msg":"解冻成功"
}
```

失败情况: 1) 用户不存在  2) 用户未处冻结状态 3) 内部错误


# 3 项目管理 (admin/project)

## 3.1 查看新创建项目列表

POST URL: admin/project/allnew


Requeset
```json
{
    "pageNo": 1, 
    "pageSize": 20
}
```

Response
```json
{
    "status":0,
    "message":"返回成功",
    "data": {
        "pageNo": 1, 
        "pageSize": 20, 
        "totalCount":5, 
        "result":[{
          "id":13, 
          "manager":{"id":12,"username":"方璐","avatar":"/img/head.jpg"},
          "name": "自由女神像",
          "shortDesc":"自由女神像（英文：Statue Of Liberty），全名为“自由女神铜像国家纪念碑”，正式名称是“自由照耀世界（Liberty Enlightening the World）”，位于美国纽约海港内自由岛的哈德逊河口附近。是法国于1876年为纪念美国独立战争期间的美法联盟赠送给美国的礼物，1886年10月28日铜像落成",
          "status": 0,
          "startDate": "2016-1-1",
          "city":{"id":5,"name":"新乡"},
          "industry": {"id":5,"name":"公建景观"},
          "headcount":5,
          "hired":2,
          "image":"/img/Liberty.jpg"            
          }]
    }
}
```



分页显示


## 3.2 分配项目经纪人

POST URL: admin/project/addbroker



Request:
```json
{
    "id":1,
    "broker":3
}
```

Response
```json
{
    "status": 0,
    "msg":"分配成功"
}
```

1) 经纪人不存在 2）项目不存在 3）内部错误

## 3.3 查看所有冻结项目

POST URL: admin/project/allfreezed



Request, Response 同3.1


## 3.4 冻结项目

POST URL: admin/project/freeze/:id



Request: no body

Response
```json
{
    "status": 0,
    "msg":"冻结成功"
}
```

1) 项目不存在 2) 项目已冻结 3）内部错误

## 3.5 解冻项目

POST URL: admin/project/unfreeze/:id



Request: no body

Response
```json
{
    "status": 0,
    "msg":"解冻成功"
}
```

1) 项目不存在 2) 项目未冻结 3）内部错误

# 4 字典管理 (admin/meta)
## 4.1 查看所有元数据 （似乎和user/skills , user/industries 重复了?)

GET URL: admin/meta/all/:type



1.admin/meta/all/skill
2.admin/meta/all/industry

Response:

```json
{
    "status":0,
    "message":"返回成功",
    "data": [{
            "id":1,
            "name":"CAD"
        },{
            "id":2,
            "name":"Photoshop"
    }]
}
```

## 4.2 添加元数据

POST URL: admin/meta/:type/add


Request:
```json
{
    "name":"Maya"
}
```

Response:
```json
{
    "status":0,
    "message":"添加成功",
    "data":{
            "id":3,
            "name":"Maya"
        }
}
```

1) 已有相同名称存在，添加失败 

## 4.3 删除元数据

POST URL: admin/meta/:type/delete/:id


Request: No body

Response: 
```json
{
    "status":0,
    "message":"删除成功",
}
```


1)id不存在，删除失败 2）考虑删除安全问题???  已经使用的不允许删除


# 5 站内信 (admin/message)

## 5.1 发送全站站内信

POST URL: admin/message/announce


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

如果role===0 则发送给全站

1)发送失败, 请选择正确发送对象 


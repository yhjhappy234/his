# 系统管理模块接口文档

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06  
> **基础路径**: /api/system/v1

---

## 1. 认证接口

### 1.1 用户登录

**接口**: `POST /auth/login`

**描述**: 用户登录认证，获取访问令牌

**请求参数**:
```json
{
  "loginName": "admin",
  "password": "encrypted_password",
  "loginType": "PASSWORD"
}
```

| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| loginName | String | 是 | 登录账号 |
| password | String | 是 | 密码(加密) |
| loginType | String | 否 | 登录方式(PASSWORD/CERTIFICATE/FINGERPRINT) |

**响应示例**:
```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "userId": "USR001",
    "userName": "管理员",
    "realName": "系统管理员",
    "deptId": "D001",
    "deptName": "信息科",
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "tokenExpiry": "2026-04-06T18:00:00",
    "permissions": [
      "system:user:view",
      "system:user:add",
      "system:user:edit"
    ],
    "roles": [
      {"roleId": "R001", "roleName": "系统管理员"}
    ],
    "dataScope": "全院"
  },
  "timestamp": "2026-04-06T10:30:00Z"
}
```

**错误码**:
| 错误码 | 描述 |
|--------|------|
| 3001 | 用户名或密码错误 |
| 3002 | 账号已被锁定 |
| 3003 | 账号已被停用 |

---

### 1.2 用户退出

**接口**: `POST /auth/logout`

**描述**: 用户退出登录，注销令牌

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "退出成功"
}
```

---

### 1.3 刷新令牌

**接口**: `POST /auth/refresh`

**描述**: 刷新访问令牌

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "tokenExpiry": "2026-04-06T18:00:00"
  }
}
```

---

### 1.4 获取当前用户信息

**接口**: `GET /auth/userinfo`

**描述**: 获取当前登录用户信息

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "userId": "USR001",
    "userName": "管理员",
    "realName": "系统管理员",
    "deptId": "D001",
    "deptName": "信息科",
    "phone": "13800138000",
    "email": "admin@hospital.com",
    "roles": ["系统管理员"],
    "permissions": ["system:user:view", "system:user:add"]
  }
}
```

---

## 2. 用户管理接口

### 2.1 获取用户列表

**接口**: `GET /users`

**描述**: 分页获取用户列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认20 |
| userName | String | 否 | 用户名(模糊查询) |
| loginName | String | 否 | 登录账号 |
| deptId | String | 否 | 科室ID |
| status | String | 否 | 状态(正常/停用/锁定) |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "id": "USR001",
        "userName": "管理员",
        "loginName": "admin",
        "realName": "系统管理员",
        "deptId": "D001",
        "deptName": "信息科",
        "phone": "13800138000",
        "email": "admin@hospital.com",
        "status": "正常",
        "lastLoginTime": "2026-04-06T09:30:00",
        "createTime": "2024-01-01T00:00:00"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 5
  }
}
```

---

### 2.2 获取用户详情

**接口**: `GET /users/{id}`

**描述**: 根据ID获取用户详情

**路径参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| id | String | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "id": "USR001",
    "userName": "管理员",
    "loginName": "admin",
    "realName": "系统管理员",
    "employeeId": "EMP001",
    "deptId": "D001",
    "deptName": "信息科",
    "phone": "13800138000",
    "email": "admin@hospital.com",
    "idCard": "110101199001011234",
    "userType": "管理员",
    "status": "正常",
    "passwordExpiry": "2026-07-06",
    "lastLoginTime": "2026-04-06T09:30:00",
    "lastLoginIp": "192.168.1.100",
    "roles": [
      {"roleId": "R001", "roleName": "系统管理员"}
    ],
    "createTime": "2024-01-01T00:00:00"
  }
}
```

---

### 2.3 创建用户

**接口**: `POST /users`

**描述**: 创建新用户

**请求参数**:
```json
{
  "userName": "张三",
  "loginName": "zhangsan",
  "password": "encrypted_password",
  "realName": "张三",
  "employeeId": "EMP002",
  "deptId": "D002",
  "phone": "13800138001",
  "email": "zhangsan@hospital.com",
  "idCard": "110101199002021234",
  "userType": "医生",
  "roleIds": ["R002"]
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "创建成功",
  "data": {
    "id": "USR002",
    "userName": "张三",
    "loginName": "zhangsan"
  }
}
```

---

### 2.4 更新用户

**接口**: `PUT /users/{id}`

**描述**: 更新用户信息

**路径参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| id | String | 是 | 用户ID |

**请求参数**:
```json
{
  "userName": "张三",
  "realName": "张三",
  "deptId": "D002",
  "phone": "13800138001",
  "email": "zhangsan@hospital.com",
  "roleIds": ["R002", "R003"]
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "更新成功"
}
```

---

### 2.5 删除用户

**接口**: `DELETE /users/{id}`

**描述**: 删除用户(逻辑删除)

**响应示例**:
```json
{
  "code": 0,
  "message": "删除成功"
}
```

---

### 2.6 重置密码

**接口**: `POST /users/{id}/reset-password`

**描述**: 重置用户密码

**请求参数**:
```json
{
  "newPassword": "encrypted_password"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "密码重置成功"
}
```

---

### 2.7 修改密码

**接口**: `POST /users/change-password`

**描述**: 用户修改自己的密码

**请求参数**:
```json
{
  "oldPassword": "encrypted_old_password",
  "newPassword": "encrypted_new_password"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "密码修改成功"
}
```

---

## 3. 角色管理接口

### 3.1 获取角色列表

**接口**: `GET /roles`

**描述**: 获取角色列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| roleName | String | 否 | 角色名称(模糊查询) |
| status | String | 否 | 状态 |

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "id": "R001",
      "roleCode": "ADMIN",
      "roleName": "系统管理员",
      "description": "系统管理员角色",
      "status": "正常",
      "userCount": 2,
      "createTime": "2024-01-01T00:00:00"
    }
  ]
}
```

---

### 3.2 获取角色权限

**接口**: `GET /roles/{id}/permissions`

**描述**: 获取角色的权限列表

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "permId": "P001",
      "permCode": "system:user:view",
      "permName": "用户查看"
    },
    {
      "permId": "P002",
      "permCode": "system:user:add",
      "permName": "用户新增"
    }
  ]
}
```

---

## 4. 权限管理接口

### 4.1 获取权限树

**接口**: `GET /permissions/tree`

**描述**: 获取权限树形结构

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "id": "P001",
      "permCode": "system",
      "permName": "系统管理",
      "children": [
        {
          "id": "P002",
          "permCode": "system:user",
          "permName": "用户管理",
          "children": [
            {"id": "P003", "permCode": "system:user:view", "permName": "用户查看"},
            {"id": "P004", "permCode": "system:user:add", "permName": "用户新增"}
          ]
        }
      ]
    }
  ]
}
```

---

## 5. 科室管理接口

### 5.1 获取科室树

**接口**: `GET /departments/tree`

**描述**: 获取科室树形结构

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "id": "D001",
      "deptCode": "XXK",
      "deptName": "信息科",
      "deptType": "行政",
      "parentId": null,
      "children": []
    },
    {
      "id": "D002",
      "deptCode": "NK",
      "deptName": "内科",
      "deptType": "临床",
      "parentId": null,
      "children": [
        {"id": "D003", "deptCode": "HXNK", "deptName": "呼吸内科", "deptType": "临床"},
        {"id": "D004", "deptCode": "XHNK", "deptName": "消化内科", "deptType": "临床"}
      ]
    }
  ]
}
```

---

### 5.2 获取科室列表

**接口**: `GET /departments`

**描述**: 获取科室列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| deptType | String | 否 | 科室类型(临床/行政/医技) |
| status | String | 否 | 状态 |

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "id": "D001",
      "deptCode": "NK",
      "deptName": "内科",
      "deptType": "临床",
      "parentId": null,
      "parentName": null,
      "status": "正常"
    }
  ]
}
```

---

## 6. 数据字典接口

### 6.1 获取字典类型列表

**接口**: `GET /dicts/types`

**描述**: 获取字典类型列表

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {"dictType": "gender", "dictTypeName": "性别"},
    {"dictType": "blood_type", "dictTypeName": "血型"},
    {"dictType": "nursing_level", "dictTypeName": "护理等级"}
  ]
}
```

---

### 6.2 获取字典项

**接口**: `GET /dicts/{type}`

**描述**: 根据字典类型获取字典项

**路径参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| type | String | 是 | 字典类型 |

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {"dictCode": "1", "dictName": "男", "sortOrder": 1, "isDefault": false},
    {"dictCode": "2", "dictName": "女", "sortOrder": 2, "isDefault": false},
    {"dictCode": "9", "dictName": "未知", "sortOrder": 3, "isDefault": true}
  ]
}
```

---

## 7. 系统参数接口

### 7.1 获取参数列表

**接口**: `GET /parameters`

**描述**: 获取系统参数列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| paramGroup | String | 否 | 参数分组 |

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "id": "P001",
      "paramCode": "hospital_name",
      "paramName": "医院名称",
      "paramValue": "示例医院",
      "paramType": "业务参数",
      "paramGroup": "system",
      "isEditable": true
    }
  ]
}
```

---

### 7.2 更新参数

**接口**: `PUT /parameters/{id}`

**描述**: 更新系统参数

**请求参数**:
```json
{
  "paramValue": "新医院名称"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "更新成功"
}
```

---

## 8. 操作日志接口

### 8.1 获取操作日志

**接口**: `GET /logs/operations`

**描述**: 分页获取操作日志

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |
| userId | String | 否 | 用户ID |
| operationModule | String | 否 | 操作模块 |
| startTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "id": "LOG001",
        "userId": "USR001",
        "userName": "管理员",
        "operationType": "新增",
        "operationModule": "用户管理",
        "operationContent": "新增用户张三",
        "operationResult": "成功",
        "operationTime": "2026-04-06T10:30:00",
        "clientIp": "192.168.1.100"
      }
    ],
    "total": 1000,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

---

**文档维护**: HIS Platform Team
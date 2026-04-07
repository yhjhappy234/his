# HIS API 文档索引

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06

---

## API 概述

HIS 系统提供 RESTful API 接口，支持 JSON 格式数据交互。

### 基础信息

```yaml
基础路径: /api/{module}/v1/{resource}
版本控制: v1
认证方式: JWT Token
请求格式: application/json
响应格式: application/json
```

### 请求头

```http
Authorization: Bearer {token}
Content-Type: application/json
X-Request-ID: {uuid}
X-Client-ID: {client_id}
```

### 响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "timestamp": "2026-04-06T10:30:00Z",
  "traceId": "abc123"
}
```

### 分页响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 5
  }
}
```

### 错误码定义

| 错误码 | 描述 |
|--------|------|
| 0 | 成功 |
| 1001 | 参数错误 |
| 1002 | 参数缺失 |
| 2001 | 业务异常 |
| 2002 | 数据不存在 |
| 2003 | 数据已存在 |
| 3001 | 认证失败 |
| 3002 | Token 过期 |
| 3003 | 权限不足 |
| 5001 | 系统异常 |
| 5002 | 服务不可用 |

---

## API 模块索引

| 模块 | 文档路径 | 核心接口 |
|------|----------|----------|
| 系统管理 | [system-api.md](./system-api.md) | 用户认证、权限管理、系统配置 |
| 门诊管理 | [outpatient-api.md](./outpatient-api.md) | 预约挂号、就诊、处方、收费 |
| 住院管理 | [inpatient-api.md](./inpatient-api.md) | 入院、床位、医嘱、出院 |
| 药房管理 | [pharmacy-api.md](./pharmacy-api.md) | 药品管理、库存、发药 |

---

## 认证说明

### 获取 Token

```http
POST /api/system/v1/auth/login
Content-Type: application/json

{
  "loginName": "admin",
  "password": "encrypted_password"
}
```

**响应**:
```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "userId": "USR001",
    "userName": "管理员",
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "tokenExpiry": "2026-04-06T18:00:00",
    "permissions": ["system:user:view", "system:user:add"],
    "roles": [{"roleId": "R001", "roleName": "系统管理员"}]
  }
}
```

### Token 刷新

```http
POST /api/system/v1/auth/refresh
Authorization: Bearer {token}
```

### 退出登录

```http
POST /api/system/v1/auth/logout
Authorization: Bearer {token}
```

---

## 接口调用示例

### cURL 示例

```bash
# 登录获取Token
curl -X POST http://localhost:8080/api/system/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginName":"admin","password":"admin123"}'

# 获取用户列表
curl -X GET http://localhost:8080/api/system/v1/users \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

### JavaScript 示例

```javascript
// 登录
const login = async () => {
  const response = await fetch('/api/system/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ loginName: 'admin', password: 'admin123' })
  });
  const data = await response.json();
  localStorage.setItem('token', data.data.token);
};

// 获取数据
const getUsers = async () => {
  const token = localStorage.getItem('token');
  const response = await fetch('/api/system/v1/users', {
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.json();
};
```

---

## 接口列表汇总

### 系统管理模块

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/system/v1/auth/login | 用户登录 |
| POST | /api/system/v1/auth/logout | 用户退出 |
| GET | /api/system/v1/users | 获取用户列表 |
| POST | /api/system/v1/users | 创建用户 |
| PUT | /api/system/v1/users/{id} | 更新用户 |
| DELETE | /api/system/v1/users/{id} | 删除用户 |
| GET | /api/system/v1/roles | 获取角色列表 |
| GET | /api/system/v1/permissions | 获取权限列表 |
| GET | /api/system/v1/dicts/{type} | 获取数据字典 |

### 门诊管理模块

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/outpatient/v1/schedules | 获取排班列表 |
| POST | /api/outpatient/v1/appointments | 创建预约 |
| PUT | /api/outpatient/v1/appointments/{id}/cancel | 取消预约 |
| GET | /api/outpatient/v1/registrations | 获取挂号列表 |
| POST | /api/outpatient/v1/registrations | 现场挂号 |
| GET | /api/outpatient/v1/queue | 获取排队信息 |
| POST | /api/outpatient/v1/prescriptions | 开立处方 |
| GET | /api/outpatient/v1/records | 获取病历列表 |
| POST | /api/outpatient/v1/billing/settle | 收费结算 |

### 住院管理模块

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/inpatient/v1/admissions | 入院登记 |
| GET | /api/inpatient/v1/admissions | 获取住院列表 |
| GET | /api/inpatient/v1/beds | 获取床位列表 |
| POST | /api/inpatient/v1/beds/{id}/assign | 分配床位 |
| POST | /api/inpatient/v1/orders | 开立医嘱 |
| POST | /api/inpatient/v1/orders/{id}/audit | 审核医嘱 |
| POST | /api/inpatient/v1/orders/{id}/execute | 执行医嘱 |
| POST | /api/inpatient/v1/discharge/apply | 申请出院 |
| POST | /api/inpatient/v1/discharge/settle | 出院结算 |

### 药房管理模块

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/pharmacy/v1/drugs | 获取药品列表 |
| GET | /api/pharmacy/v1/inventory | 查询库存 |
| GET | /api/pharmacy/v1/dispense/pending | 获取待发药处方 |
| POST | /api/pharmacy/v1/dispense/{id}/audit | 处方审核 |
| POST | /api/pharmacy/v1/dispense/{id}/confirm | 发药确认 |

---

**文档维护**: HIS Platform Team
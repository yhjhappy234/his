# 系统管理与安全模块需求说明书

## 1. 模块概述与目标

### 1.1 模块定位
系统管理与安全模块是HIS系统的基础支撑模块，负责用户管理、权限控制、系统配置、数据安全、日志审计等，保障系统安全稳定运行。

### 1.2 业务目标
- 实现系统用户统一管理
- 规范权限控制机制
- 保障系统数据安全
- 提供系统运维支持
- 满足等保三级要求

### 1.3 用户角色
- 系统管理员
- 安全管理员
- 审计管理员
- 普通用户

---

## 2. 功能清单

### 2.1 用户管理

#### 2.1.1 用户基本管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 用户注册 | 用户账号注册申请 | 高 |
| 用户审核 | 用户账号审核开通 | 高 |
| 用户信息维护 | 用户信息修改维护 | 高 |
| 用户停用 | 用户账号停用处理 | 高 |
| 用户注销 | 用户账号注销删除 | 高 |
| 用户查询 | 用户信息查询检索 | 高 |

#### 2.1.2 用户信息
```
用户信息 User:
- userId: string, 用户ID
- userName: string, 用户名
- loginName: string, 登录账号
- password: string, 密码(加密存储)
- employeeId: string, 关联员工ID
- deptId: string, 所属科室
- phone: string, 联系电话
- email: string, 邮箱
- loginType: enum, 登录方式
- lastLoginTime: datetime, 最后登录时间
- lastLoginIp: string, 最后登录IP
- passwordExpiry: date, 密码有效期
- status: enum, 状态, [正常/停用/锁定]
```

#### 2.1.3 密码管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 密码设置 | 用户密码设置 | 高 |
| 密码修改 | 用户密码修改 | 高 |
| 密码重置 | 管理员密码重置 | 高 |
| 密码策略 | 密码策略配置 | 高 |

#### 2.1.4 密码策略
```
密码策略 PasswordPolicy:
- 最小长度: >=8位
- 复杂度要求: 大小写字母+数字+特殊字符
- 有效期: 90天
- 历史密码: 不能与前5次相同
- 错误锁定: 连续5次错误锁定账号
- 锁定时长: 30分钟
```

### 2.2 角色权限管理

#### 2.2.1 权限类型
```
权限类型 PermissionType:
- 菜单权限: 系统菜单访问权限
- 功能权限: 功能操作权限
- 数据权限: 数据访问范围权限
- 接口权限: API接口访问权限

权限粒度:
- 查看(view)
- 新增(add)
- 修改(edit)
- 删除(delete)
- 审核(approve)
- 导出(export)
- 打印(print)
```

#### 2.2.2 权限管理功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 角色定义 | 系统角色定义配置 | 高 |
| 权限定义 | 权限项目定义 | 高 |
| 权限分配 | 角色权限分配 | 高 |
| 用户授权 | 用户角色授权 | 高 |
| 权限继承 | 权限继承规则 | 中 |
| 权限回收 | 权限回收处理 | 高 |

#### 2.2.3 数据权限
```
数据权限级别 DataPermissionLevel:
- 全院数据: 可访问全院数据
- 科室数据: 仅访问本科室数据
- 个人数据: 仅访问个人相关数据

数据权限规则:
- 科室主任: 本科室全部数据
- 科室医生: 本科室患者数据
- 护士: 本病区患者数据
- 收费员: 本收费窗口数据
```

### 2.3 科室管理

#### 2.3.1 科室功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 科室信息 | 科室基本信息维护 | 高 |
| 科室层级 | 科室层级架构管理 | 高 |
| 科室类型 | 科室类型设置 | 高 |
| 科室人员 | 科室人员配置 | 高 |

### 2.4 系统参数配置

#### 2.4.1 参数管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 参数设置 | 系统参数配置设置 | 高 |
| 参数分组 | 参数分组分类管理 | 高 |
| 参数导入 | 参数批量导入 | 中 |
| 参数导出 | 参数批量导出 | 中 |

#### 2.4.2 参数类型
```
系统参数类型:
- 业务参数: 业务规则参数
- 系统参数: 系统运行参数
- 接口参数: 接口配置参数
- 安全参数: 安全策略参数
- 显示参数: 界面显示参数

示例参数:
- hospital_name: 医院名称
- registration_quota: 号源上限
- prescription_limit_days: 处方限量天数
- critical_value_notify: 危急值通知方式
- session_timeout: 会话超时时间
```

### 2.5 数据字典管理

#### 2.5.1 字典功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 字典类型 | 字典类型管理 | 高 |
| 字典项维护 | 字典项信息维护 | 高 |
| 字典排序 | 字典项排序设置 | 中 |
| 字典导入 | 字典批量导入 | 中 |

#### 2.5.2 字典类型示例
```
数据字典类型示例:
- ICD10: 国际疾病分类编码
- drug_form: 药品剂型
- nursing_level: 护理等级
- admission_type: 入院类型
- discharge_type: 出院类型
- payment_method: 支付方式
- gender: 性别
- blood_type: 血型
- education: 学历
- title: 职称
```

### 2.6 操作日志管理

#### 2.6.1 日志功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 操作记录 | 用户操作记录 | 高 |
| 日志查询 | 操作日志查询 | 高 |
| 日志统计 | 日志统计分析 | 中 |
| 日志导出 | 日志数据导出 | 高 |
| 日志归档 | 日志数据归档 | 高 |

#### 2.6.2 日志内容
```
操作日志 OperationLog:
- logId: string, 日志ID
- userId: string, 操作用户ID
- userName: string, 用户名
- loginName: string, 登录账号
- operationType: string, 操作类型
- operationModule: string, 操作模块
- operationContent: string, 操作内容
- operationData: json, 操作数据
- operationResult: enum, 结果, [成功/失败]
- errorMessage: string, 错误信息
- operationTime: datetime, 操作时间
- clientIp: string, 客户端IP
- serverIp: string, 服务端IP
```

### 2.7 审计日志管理

#### 2.7.1 审计功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 审计记录 | 安全审计记录 | 高 |
| 审计查询 | 审计日志查询 | 高 |
| 审计分析 | 审计数据分析 | 中 |
| 审计告警 | 异常行为告警 | 高 |
| 审计报告 | 审计报告生成 | 中 |

#### 2.7.2 审计类型
```
审计类型 AuditType:
- 登录审计: 用户登录/退出审计
- 权限审计: 权限变更审计
- 数据审计: 数据修改/删除审计
- 系统审计: 系统配置变更审计
- 安全审计: 安全事件审计

审计重点:
- 用户登录异常(异地登录、频繁登录)
- 权限变更记录
- 病历数据修改记录
- 财务数据变更记录
- 系统配置修改记录
```

### 2.8 数据备份与恢复

#### 2.8.1 备份功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 备份策略 | 备份策略配置 | 高 |
| 自动备份 | 定时自动备份 | 高 |
| 手动备份 | 手动触发备份 | 高 |
| 备份监控 | 备份状态监控 | 高 |
| 备份日志 | 备份日志记录 | 高 |

#### 2.8.2 备份策略
```
备份策略 BackupPolicy:
- 全量备份: 每周一次
- 增量备份: 每日一次
- 备份保留: 全量保留12个月，增量保留3个月
- 备份验证: 每月验证备份有效性
- 异地备份: 备份数据异地存储

备份内容:
- 数据库数据
- 系统配置文件
- 影像文件
- 日志文件
```

#### 2.8.3 恢复功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 数据恢复 | 数据恢复操作 | 高 |
| 恢复验证 | 恢复数据验证 | 高 |
| 恢复日志 | 恢复操作记录 | 高 |

### 2.9 系统监控

#### 2.9.1 监控功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 性能监控 | 系统性能监控 | 高 |
- CPU使用率
- 内存使用率
- 磁盘空间
- 网络流量
| 服务监控 | 服务运行状态监控 | 高 |
| 告警通知 | 异常告警通知 | 高 |
| 监控报表 | 监控数据报表 | 中 |

#### 2.9.2 告警规则
```
告警规则 AlertRule:
- CPU使用率>80%: 告警
- 内存使用率>80%: 告警
- 磁盘空间<20%: 告警
- 服务停止: 立即告警
- 登录异常: 安全告警

告警方式:
- 系统通知
- 短信通知
- 邮件通知
```

### 2.10 接口管理

#### 2.10.1 接口功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 接口定义 | API接口定义管理 | 高 |
| 接口文档 | 接口文档管理 | 高 |
| 接口监控 | 接口调用监控 | 中 |
| 接口日志 | 接口调用日志 | 高 |

#### 2.10.2 外部接口
```
外部接口 ExternalInterface:
- 医保接口: 医保结算接口
- 支付接口: 微信/支付宝支付接口
- 短信接口: 短信发送接口
- 自助机接口: 自助设备接口
- 政务接口: 电子政务接口
- 数据上报接口: 数据上报接口
```

### 2.11 安全策略管理

#### 2.11.1 安全功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 安全策略 | 安全策略配置 | 高 |
| 登录策略 | 登录安全策略 | 高 |
| 数据加密 | 数据加密策略 | 高 |
| 访问控制 | 访问控制策略 | 高 |
| 安全审计 | 安全审计配置 | 高 |

#### 2.11.2 等保三级要求
```
等保三级安全要求:

1. 身份鉴别:
   - 用户身份唯一标识
   - 双因子认证支持
   - 密码复杂度要求
   - 登录失败处理

2. 访问控制:
   - 基于角色的访问控制
   - 权限最小化原则
   - 数据权限隔离

3. 安全审计:
   - 用户行为审计
   - 安全事件审计
   - 审计日志保护
   - 审计日志保存>=6个月

4. 数据完整性:
   - 数据传输加密
   - 数据存储加密
   - 数据完整性校验

5. 数据保密性:
   - 敏感数据加密存储
   - 数据脱敏展示
   - 数据访问控制

6. 数据备份:
   - 定期数据备份
   - 异地备份存储
   - 备份数据加密

7. 网络安全:
   - 网络边界防护
   - 入侵检测
   - 安全通信协议

8. 系统安全:
   - 漏洞管理
   - 补丁更新
   - 安全配置
```

### 2.12 单点登录(SSO)

#### 2.12.1 SSO功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 统一认证 | 统一身份认证 | 高 |
| 统一登录 | 多系统统一登录 | 高 |
| 统一退出 | 统一退出处理 | 高 |
| 会话管理 | 统一会话管理 | 高 |

#### 2.12.2 认证方式
```
认证方式 AuthenticationMethod:
- 用户名密码认证
- 数字证书认证
- 生物特征认证(指纹/人脸)
- 短信验证码认证
- 二维码认证

SSO协议支持:
- OAuth2.0
- CAS
- SAML
```

---

## 3. 数据实体定义

### 3.0 默认数据初始化

系统启动时自动初始化以下默认数据：

```
默认用户账户:
- 登录账号: admin
- 默认密码: (通过配置文件设置，建议使用环境变量)
- 角色: 系统管理员 (ADMIN)
- 数据权限: 全院数据 (ALL)
- 所属科室: 系统管理部

默认角色:
- ADMIN: 系统管理员角色，拥有所有权限
- DOCTOR: 医生角色，科室级数据权限

默认科室:
- 系统管理部 (SYS): 行政管理类科室
- 内科 (IM): 临床类科室

初始化时机:
- 应用启动时 DataInitializer 自动执行
- 检测数据不存在时创建，已存在则跳过
- 可通过配置 his.default.username 和 his.default.password 自定义
```

### 3.1 核心实体

#### 3.1.1 用户信息 User
```sql
CREATE TABLE user (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '用户ID',
    user_name           VARCHAR(50) NOT NULL COMMENT '用户名',
    login_name          VARCHAR(30) NOT NULL UNIQUE COMMENT '登录账号',
    password            VARCHAR(100) NOT NULL COMMENT '密码(加密)',

    employee_id         VARCHAR(20) COMMENT '关联员工ID',
    real_name           VARCHAR(50) COMMENT '真实姓名',
    dept_id             VARCHAR(20) COMMENT '所属科室ID',
    dept_name           VARCHAR(100) COMMENT '科室名称',

    phone               VARCHAR(20) COMMENT '联系电话',
    email               VARCHAR(50) COMMENT '邮箱',
    id_card             VARCHAR(18) COMMENT '身份证号',

    user_type           VARCHAR(20) COMMENT '用户类型',
    login_type          VARCHAR(20) COMMENT '登录方式',

    password_expiry     DATE COMMENT '密码有效期',
    password_update_time DATETIME COMMENT '密码修改时间',

    last_login_time     DATETIME COMMENT '最后登录时间',
    last_login_ip       VARCHAR(50) COMMENT '最后登录IP',
    login_fail_count    INT DEFAULT 0 COMMENT '登录失败次数',
    lock_time           DATETIME COMMENT '锁定时间',

    session_timeout     INT DEFAULT 30 COMMENT '会话超时(分钟)',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态(正常/停用/锁定)',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_login_name (login_name),
    INDEX idx_employee (employee_id),
    INDEX idx_status (status)
);
```

#### 3.1.2 系统参数 SystemParameter
```sql
CREATE TABLE system_parameter (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '参数ID',
    param_code          VARCHAR(50) NOT NULL UNIQUE COMMENT '参数编码',
    param_name          VARCHAR(100) NOT NULL COMMENT '参数名称',
    param_value         VARCHAR(500) COMMENT '参数值',
    param_type          VARCHAR(20) COMMENT '参数类型',
    param_group         VARCHAR(50) COMMENT '参数分组',

    description         VARCHAR(200) COMMENT '参数描述',

    is_system           TINYINT DEFAULT 0 COMMENT '是否系统参数',
    is_editable         TINYINT DEFAULT 1 COMMENT '是否可编辑',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_code (param_code),
    INDEX idx_group (param_group)
);
```

#### 3.1.3 数据字典 DataDictionary
```sql
CREATE TABLE data_dictionary (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '字典ID',
    dict_type           VARCHAR(50) NOT NULL COMMENT '字典类型',
    dict_code           VARCHAR(50) NOT NULL COMMENT '字典编码',
    dict_name           VARCHAR(100) NOT NULL COMMENT '字典名称',
    dict_value          VARCHAR(200) COMMENT '字典值',

    parent_code         VARCHAR(50) COMMENT '父级编码',
    dict_level          INT COMMENT '层级',

    sort_order          INT COMMENT '排序号',
    is_enabled          TINYINT DEFAULT 1 COMMENT '是否启用',
    is_default          TINYINT DEFAULT 0 COMMENT '是否默认值',

    description         VARCHAR(200) COMMENT '描述',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_type_code (dict_type, dict_code),
    INDEX idx_type (dict_type)
);
```

#### 3.1.4 操作日志 OperationLog
```sql
CREATE TABLE operation_log (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '日志ID',
    log_no              VARCHAR(30) COMMENT '日志编号',

    user_id             VARCHAR(20) COMMENT '用户ID',
    login_name          VARCHAR(30) COMMENT '登录账号',
    real_name           VARCHAR(50) COMMENT '真实姓名',
    dept_id             VARCHAR(20) COMMENT '科室ID',
    dept_name           VARCHAR(100) COMMENT '科室名称',

    operation_type      VARCHAR(20) COMMENT '操作类型',
    operation_module    VARCHAR(50) COMMENT '操作模块',
    operation_func      VARCHAR(50) COMMENT '操作功能',
    operation_desc      VARCHAR(200) COMMENT '操作描述',

    request_method      VARCHAR(10) COMMENT '请求方法',
    request_url         VARCHAR(200) COMMENT '请求URL',
    request_param       TEXT COMMENT '请求参数',
    response_data       TEXT COMMENT '响应数据',

    operation_result    VARCHAR(20) COMMENT '操作结果(成功/失败)',
    error_msg           TEXT COMMENT '错误信息',

    operation_time      DATETIME NOT NULL COMMENT '操作时间',
    duration            INT COMMENT '耗时(ms)',

    client_ip           VARCHAR(50) COMMENT '客户端IP',
    server_ip           VARCHAR(50) COMMENT '服务端IP',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_user_time (user_id, operation_time),
    INDEX idx_module_time (operation_module, operation_time),
    INDEX idx_operation_time (operation_time)
);
```

#### 3.1.5 审计日志 AuditLog
```sql
CREATE TABLE audit_log (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '审计ID',
    audit_type          VARCHAR(20) NOT NULL COMMENT '审计类型',

    user_id             VARCHAR(20) COMMENT '用户ID',
    login_name          VARCHAR(30) COMMENT '登录账号',
    real_name           VARCHAR(50) COMMENT '真实姓名',

    audit_event         VARCHAR(50) COMMENT '审计事件',
    audit_desc          VARCHAR(200) COMMENT '审计描述',

    audit_level         VARCHAR(20) COMMENT '审计级别(正常/警告/严重)',

    before_data         TEXT COMMENT '变更前数据',
    after_data          TEXT COMMENT '变更后数据',

    client_ip           VARCHAR(50) COMMENT '客户端IP',
    audit_time          DATETIME NOT NULL COMMENT '审计时间',

    is_alerted          TINYINT COMMENT '是否已告警',
    alert_time          DATETIME COMMENT '告警时间',
    alert_way           VARCHAR(20) COMMENT '告警方式',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_user_time (user_id, audit_time),
    INDEX idx_type_time (audit_type, audit_time),
    INDEX idx_level (audit_level)
);
```

---

## 4. 业务流程

### 4.1 用户登录流程
```
┌─────────────────────────────────────────────────────────────────┐
│                       用户登录流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 输入账号  │───>│ 密码验证  │───>│ 账号状态  │                │
│   │ 密码      │    │          │    │ 校验      │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │ 失败          │                       │
│                         ▼               ▼                       │
│                   ┌──────────┐    ┌──────────┐                │
│                   │ 失败计数  │    │ 权限加载  │                │
│                   │ 锁定检查  │    │          │                │
│                   └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │ 超限          │                       │
│                         ▼               ▼                       │
│                   ┌──────────┐    ┌──────────┐                │
│                   │ 账号锁定  │    │ 登录成功  │                │
│                   └──────────┘    │ 记录日志  │                │
│                                   └──────────┘                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 5. 接口定义

### 5.1 用户管理接口

#### 5.1.1 用户登录
```
POST /api/system/user/login

Request:
{
    "loginName": "admin",
    "password": "encrypted_password",
    "loginType": "PASSWORD"
}

Response:
{
    "code": 0,
    "message": "登录成功",
    "data": {
        "userId": "USR001",
        "userName": "管理员",
        "realName": "系统管理员",
        "deptId": "D001",
        "token": "jwt_token",
        "tokenExpiry": "2024-01-15T18:00:00",
        "permissions": [...],
        "roles": [...]
    }
}
```

#### 5.1.2 用户退出
```
POST /api/system/user/logout

Response:
{
    "code": 0,
    "message": "退出成功"
}
```

### 5.2 权限管理接口

#### 5.2.1 获取用户权限
```
GET /api/system/permission/user?userId=USR001

Response:
{
    "code": 0,
    "data": {
        "roles": [
            {"roleId": "R001", "roleName": "系统管理员"}
        ],
        "permissions": [
            {"permCode": "system:user:view", "permName": "用户查看"},
            {"permCode": "system:user:add", "permName": "用户新增"}
        ],
        "dataScope": "全院"
    }
}
```

---

## 6. 业务规则与约束

### 6.1 安全规则
| 规则编码 | 规则描述 |
|----------|----------|
| SEC001 | 密码长度>=8位 |
| SEC002 | 密码必须包含大小写字母、数字、特殊字符 |
| SEC003 | 密码有效期90天 |
| SEC004 | 连续5次登录失败锁定账号 |
| SEC005 | 账号锁定时长30分钟 |
| SEC006 | 会话超时30分钟自动退出 |
| SEC007 | 审计日志保存>=6个月 |

### 6.2 权限规则
| 规则编码 | 规则描述 |
|----------|----------|
| PER001 | 用户必须有角色才能访问系统 |
| PER002 | 角色必须有权限才能操作功能 |
| PER003 | 权限变更需要审批流程 |
| PER004 | 系统角色不可删除修改 |

---

## 7. 模块交互关系

### 7.1 依赖关系
- 所有业务模块依赖本模块进行用户认证和权限验证

---

## 8. 性能与安全要求

### 8.1 性能要求
| 指标 | 要求 |
|------|------|
| 登录响应时间 | < 2秒 |
| 权限验证时间 | < 100ms |
| 日志查询时间 | < 5秒 |

### 8.2 安全要求
- 等保三级合规
- 数据加密传输
- 敏感数据加密存储
- 完整审计日志
- 定期数据备份
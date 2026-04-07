# 人力资源管理模块需求说明书

## 1. 模块概述与目标

### 1.1 模块定位
人力资源管理模块是HIS系统的支持模块，负责医院员工信息管理、排班管理、考勤管理、绩效管理等，为医院运营提供人员保障。

### 1.2 业务目标
- 实现员工信息电子化管理
- 规范排班考勤管理流程
- 提供绩效考核数据支持
- 规范权限角色管理
- 支持医院运营决策

### 1.3 用户角色
- 人事管理员
- 科室主任
- 普通员工
- 系统管理员

---

## 2. 功能清单

### 2.1 员工信息管理

#### 2.1.1 基本信息管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 员工档案 | 员工基本信息档案 | 高 |
| 员工录入 | 员工信息录入登记 | 高 |
| 员工修改 | 员工信息变更管理 | 高 |
| 员工离职 | 员工离职流程管理 | 高 |
| 员工查询 | 员工信息查询检索 | 高 |

#### 2.1.2 员工信息字段
```
员工信息 Employee:
- employeeId: string, 员工ID
- employeeNo: string, 员工工号
- employeeName: string, 姓名
- gender: enum, 性别
- birthDate: date, 出生日期
- idCardNo: string, 身份证号
- phone: string, 联系电话
- email: string, 邮箱
- education: string, 学历
- specialty: string, 专业
- title: string, 职称
- deptId: string, 所属科室
- position: string, 职位
- employmentDate: date, 入职日期
- employmentType: enum, 类型, [全职/兼职/临时]
- status: enum, 状态, [在职/离职]
```

#### 2.1.3 证件资质管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 执业证书 | 医师执业证书管理 | 高 |
| 资格证书 | 各类资格证书管理 | 高 |
| 学历证书 | 学历学位证书管理 | 中 |
| 继续教育 | 继续教育学分管理 | 高 |

### 2.2 科室组织架构

#### 2.2.1 科室管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 科室信息 | 科室基本信息维护 | 高 |
| 科室层级 | 科室层级架构设置 | 高 |
| 科室人员 | 科室人员配置 | 高 |
| 科室合并 | 科室合并调整 | 中 |
| 科室撤销 | 科室撤销处理 | 中 |

#### 2.2.2 科室信息
```
科室信息 Department:
- deptId: string, 科室ID
- deptCode: string, 科室编码
- deptName: string, 科室名称
- deptType: enum, 科室类型, [临床/医技/行政/后勤]
- parentId: string, 上级科室ID
- deptLevel: int, 科室层级
- deptLeader: string, 科室负责人
- deptPhone: string, 科室电话
- deptLocation: string, 科室位置
- bedCount: int, 床位数(病区)
- status: enum, 状态
```

### 2.3 排班管理

#### 2.3.1 排班功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 排班规则 | 排班规则设置 | 高 |
| 排班模板 | 排班模板配置 | 高 |
| 排班制定 | 制定排班计划 | 高 |
| 排班调整 | 排班调整变更 | 高 |
| 排班查询 | 排班信息查询 | 高 |
| 排班统计 | 排班数据统计 | 中 |

#### 2.3.2 排班类型
```
排班类型 ScheduleType:
- 白班: 08:00-16:00
- 夜班: 16:00-24:00
- 晚班: 00:00-08:00
- 全天班: 08:00-24:00
- 休息: 非工作时间
- 节假日: 特殊排班
```

#### 2.3.3 排班规则
```
排班规则设置:
- 每周工作时长: 40小时
- 每周休息天数: >=2天
- 夜班间隔: 夜班后休息>=24小时
- 连续夜班上限: <=3天
- 节假日轮班规则
```

### 2.4 考勤管理

#### 2.4.1 考勤功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 考勤打卡 | 考勤打卡记录 | 高 |
| 考勤统计 | 考勤数据统计 | 高 |
| 请假申请 | 请假申请提交 | 高 |
| 请假审批 | 请假申请审批 | 高 |
| 调休管理 | 调休记录管理 | 高 |
| 加班管理 | 加班记录管理 | 高 |

#### 2.4.2 请假类型
```
请假类型 LeaveType:
- 事假: 个人事务请假
- 病假: 因病请假
- 年假: 年度休假
- 产假: 女职工产假
- 婚假: 结婚休假
- 丧假: 丧事请假
- 公假: 公务请假
```

#### 2.4.3 考勤状态
```
考勤状态 AttendanceStatus:
- 正常: 按时打卡
- 迟到: 迟到打卡
- 早退: 早退打卡
- 旷工: 未打卡
- 请假: 请假状态
- 调休: 调休状态
- 加班: 加班状态
```

### 2.5 绩效考核

#### 2.5.1 绩效功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 绩效指标 | 绩效考核指标设置 | 高 |
| 绩效方案 | 绩效考核方案配置 | 高 |
| 绩效评分 | 绩效评分录入 | 高 |
| 绩效审核 | 绩效评分审核 | 高 |
| 绩效查询 | 绩效结果查询 | 高 |
| 绩效统计 | 绩效数据统计 | 中 |

#### 2.5.2 绩效指标
```
绩效指标类型:
- 工作量指标: 工作数量统计
- 质量指标: 工作质量评价
- 效率指标: 工作效率统计
- 服务指标: 患者满意度
- 考勤指标: 考勤表现统计
- 科研指标: 科研成果统计
- 教学指标: 教学工作统计

医生绩效指标示例:
-门诊量
- 住院收治量
- 手术量
- 病历质量评分
- 患者满意度
- 检查检验合理率
```

### 2.6 薪资管理

#### 2.6.1 薪资功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 薪资结构 | 薪资项目结构设置 | 高 |
| 薪资标准 | 各岗位薪资标准 | 高 |
| 薪资计算 | 月度薪资计算 | 高 |
| 薪资审核 | 薪资审核确认 | 高 |
| 薪资发放 | 薪资发放记录 | 高 |
| 薪资查询 | 员工薪资查询 | 高 |

#### 2.6.2 薪资结构
```
薪资结构 SalaryStructure:
基本工资 + 岗位工资 + 绩效工资 + 津贴补贴 - 扣款项

薪资项目:
- 基本工资: 固定工资
- 岗位工资: 岗位级别工资
- 绩效工资: 绩效考核工资
- 工龄工资: 工龄补贴
- 夜班津贴: 夜班补贴
- 加班费: 加班工资
- 节假日费: 节假日加班费
- 扣款: 各类扣款项
```

### 2.7 培训管理

#### 2.7.1 培训功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 培训计划 | 培训计划制定 | 中 |
| 培训课程 | 培训课程管理 | 中 |
| 培训记录 | 员工培训记录 | 中 |
| 培训考核 | 培训考核管理 | 中 |
| 培训统计 | 培训数据统计 | 中 |

### 2.8 权限角色管理

#### 2.8.1 权限功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 角色定义 | 系统角色定义 | 高 |
| 权限配置 | 角色权限配置 | 高 |
| 用户授权 | 用户角色授权 | 高 |
| 数据权限 | 数据权限设置 | 高 |
| 权限审计 | 权限变更审计 | 高 |

#### 2.8.2 角色定义
```
预设角色 PredefinedRole:
- 系统管理员: 全系统权限
- 医院管理员: 系统配置权限
- 科室主任: 科室管理权限
- 门诊医生: 门诊工作站权限
- 住院医生: 住院工作站权限
- 护士: 护理工作站权限
- 药师: 药房工作站权限
- 收费员: 收费站权限
- 普通员工: 基础权限
```

---

## 3. 数据实体定义

### 3.1 核心实体

#### 3.1.1 员工信息 Employee
```sql
CREATE TABLE employee (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '员工ID',
    employee_no         VARCHAR(20) NOT NULL UNIQUE COMMENT '工号',
    employee_name       VARCHAR(50) NOT NULL COMMENT '姓名',
    gender              CHAR(1) COMMENT '性别',
    birth_date          DATE COMMENT '出生日期',
    id_card_no          VARCHAR(18) COMMENT '身份证号',
    phone               VARCHAR(20) COMMENT '联系电话',
    email               VARCHAR(50) COMMENT '邮箱',
    photo               VARCHAR(200) COMMENT '照片URL',

    education           VARCHAR(20) COMMENT '学历',
    school              VARCHAR(100) COMMENT '毕业院校',
    specialty           VARCHAR(50) COMMENT '专业',
    title               VARCHAR(20) COMMENT '职称',
    title_date          DATE COMMENT '职称取得日期',

    dept_id             VARCHAR(20) NOT NULL COMMENT '所属科室ID',
    dept_name           VARCHAR(100) COMMENT '科室名称',
    position            VARCHAR(50) COMMENT '职位',
    position_level      INT COMMENT '职位级别',

    employment_date     DATE NOT NULL COMMENT '入职日期',
    employment_type     VARCHAR(20) COMMENT '类型(全职/兼职/临时)',
    work_years          INT COMMENT '工龄',

    bank_account        VARCHAR(30) COMMENT '工资卡号',
    bank_name           VARCHAR(50) COMMENT '开户银行',

    base_salary         DECIMAL(10,2) COMMENT '基本工资',
    position_salary     DECIMAL(10,2) COMMENT '岗位工资',

    status              VARCHAR(20) NOT NULL DEFAULT '在职' COMMENT '状态',
    leave_date          DATE COMMENT '离职日期',
    leave_reason        VARCHAR(200) COMMENT '离职原因',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_employee_no (employee_no),
    INDEX idx_dept (dept_id),
    INDEX idx_status (status)
);
```

#### 3.1.2 科室信息 Department
```sql
CREATE TABLE department (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '科室ID',
    dept_code           VARCHAR(20) NOT NULL UNIQUE COMMENT '科室编码',
    dept_name           VARCHAR(100) NOT NULL COMMENT '科室名称',
    dept_alias          VARCHAR(50) COMMENT '科室别名',
    dept_type           VARCHAR(20) NOT NULL COMMENT '科室类型',
    dept_category       VARCHAR(20) COMMENT '科室分类',

    parent_id           VARCHAR(36) COMMENT '上级科室ID',
    dept_level          INT COMMENT '科室层级',
    dept_path           VARCHAR(200) COMMENT '科室路径',

    dept_leader_id      VARCHAR(20) COMMENT '负责人ID',
    dept_leader_name    VARCHAR(50) COMMENT '负责人姓名',
    dept_phone          VARCHAR(20) COMMENT '科室电话',
    dept_location       VARCHAR(100) COMMENT '科室位置',

    bed_count           INT COMMENT '床位数',
    outpatient_room     VARCHAR(20) COMMENT '门诊诊室',

    sort_order          INT COMMENT '排序号',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_code (dept_code),
    INDEX idx_type (dept_type),
    INDEX idx_parent (parent_id)
);
```

#### 3.1.3 排班信息 Schedule
```sql
CREATE TABLE schedule (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '排班ID',
    employee_id         VARCHAR(20) NOT NULL COMMENT '员工ID',
    employee_no         VARCHAR(20) COMMENT '工号',
    employee_name       VARCHAR(50) COMMENT '姓名',
    dept_id             VARCHAR(20) NOT NULL COMMENT '科室ID',
    dept_name           VARCHAR(100) COMMENT '科室名称',

    schedule_date       DATE NOT NULL COMMENT '排班日期',
    schedule_type       VARCHAR(20) NOT NULL COMMENT '班次类型',
    start_time          TIME COMMENT '上班时间',
    end_time            TIME COMMENT '下班时间',

    location            VARCHAR(50) COMMENT '工作地点',

    status              VARCHAR(20) COMMENT '状态',

    creator_id          VARCHAR(20) COMMENT '创建人ID',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_employee_date (employee_id, schedule_date),
    INDEX idx_date (schedule_date),
    INDEX idx_dept_date (dept_id, schedule_date)
);
```

#### 3.1.4 考勤记录 Attendance
```sql
CREATE TABLE attendance (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '考勤ID',
    employee_id         VARCHAR(20) NOT NULL COMMENT '员工ID',
    employee_no         VARCHAR(20) COMMENT '工号',
    employee_name       VARCHAR(50) COMMENT '姓名',
    dept_id             VARCHAR(20) COMMENT '科室ID',

    attendance_date     DATE NOT NULL COMMENT '考勤日期',

    clock_in_time       TIME COMMENT '签到时间',
    clock_out_time      TIME COMMENT '签退时间',

    schedule_type       VARCHAR(20) COMMENT '应到班次',
    schedule_start      TIME COMMENT '应到时间',
    schedule_end        TIME COMMENT '应退时间',

    attendance_status   VARCHAR(20) COMMENT '考勤状态',
    late_minutes        INT COMMENT '迟到分钟',
    early_minutes       INT COMMENT '早退分钟',

    leave_type          VARCHAR(20) COMMENT '请假类型',
    leave_id            VARCHAR(36) COMMENT '请假申请ID',
    overtime_hours      DECIMAL(4,1) COMMENT '加班时长',

    remark              VARCHAR(200) COMMENT '备注',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_employee_date (employee_id, attendance_date),
    INDEX idx_date (attendance_date)
);
```

#### 3.1.5 请假申请 LeaveRequest
```sql
CREATE TABLE leave_request (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '请假ID',
    request_no          VARCHAR(30) NOT NULL UNIQUE COMMENT '申请单号',

    employee_id         VARCHAR(20) NOT NULL COMMENT '员工ID',
    employee_no         VARCHAR(20) COMMENT '工号',
    employee_name       VARCHAR(50) COMMENT '姓名',
    dept_id             VARCHAR(20) COMMENT '科室ID',
    dept_name           VARCHAR(100) COMMENT '科室名称',

    leave_type          VARCHAR(20) NOT NULL COMMENT '请假类型',
    leave_reason        VARCHAR(500) COMMENT '请假原因',

    start_date          DATE NOT NULL COMMENT '开始日期',
    end_date            DATE NOT NULL COMMENT '结束日期',
    leave_days          DECIMAL(4,1) COMMENT '请假天数',

    apply_time          DATETIME NOT NULL COMMENT '申请时间',

    approver_id         VARCHAR(20) COMMENT '审批人ID',
    approver_name       VARCHAR(50) COMMENT '审批人姓名',
    approve_time        DATETIME COMMENT '审批时间',
    approve_status      VARCHAR(20) COMMENT '审批状态',
    approve_remark      VARCHAR(200) COMMENT '审批意见',

    status              VARCHAR(20) NOT NULL DEFAULT '待审批' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_employee (employee_id),
    INDEX idx_status (status)
);
```

#### 3.1.6 角色信息 Role
```sql
CREATE TABLE role (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '角色ID',
    role_code           VARCHAR(20) NOT NULL UNIQUE COMMENT '角色编码',
    role_name           VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_description    VARCHAR(200) COMMENT '角色描述',

    is_system           TINYINT DEFAULT 0 COMMENT '是否系统角色',
    is_enabled          TINYINT DEFAULT 1 COMMENT '是否启用',

    sort_order          INT COMMENT '排序号',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_code (role_code)
);
```

#### 3.1.7 权限信息 Permission
```sql
CREATE TABLE permission (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '权限ID',
    permission_code     VARCHAR(50) NOT NULL UNIQUE COMMENT '权限编码',
    permission_name     VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_type     VARCHAR(20) COMMENT '权限类型(菜单/功能/数据)',

    parent_id           VARCHAR(36) COMMENT '上级权限ID',
    permission_path     VARCHAR(200) COMMENT '权限路径',

    resource_type       VARCHAR(20) COMMENT '资源类型',
    resource_path       VARCHAR(200) COMMENT '资源路径',

    sort_order          INT COMMENT '排序号',

    is_enabled          TINYINT DEFAULT 1 COMMENT '是否启用',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_code (permission_code),
    INDEX idx_type (permission_type)
);
```

#### 3.1.8 用户角色关联 UserRole
```sql
CREATE TABLE user_role (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '关联ID',
    user_id             VARCHAR(20) NOT NULL COMMENT '用户ID',
    role_id             VARCHAR(36) NOT NULL COMMENT '角色ID',

    grant_time          DATETIME COMMENT '授权时间',
    grant_by            VARCHAR(20) COMMENT '授权人ID',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user (user_id),
    INDEX idx_role (role_id)
);
```

---

## 4. 业务流程

### 4.1 请假流程
```
┌─────────────────────────────────────────────────────────────────┐
│                       请假申请流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 提交申请  │───>│ 科室审批  │───>│ 人事审批  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │ 不通过        │ 通过                  │
│                         ▼               ▼                       │
│                   ┌──────────┐    ┌──────────┐                │
│                   │ 退回修改  │    │ 考勤更新  │                │
│                   └──────────┘    └──────────┘                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 5. 接口定义

### 5.1 员工管理接口

#### 5.1.1 员工列表查询
```
GET /api/hr/employee/list?deptId=D001&status=在职

Response:
{
    "code": 0,
    "data": {
        "total": 50,
        "list": [
            {
                "employeeId": "EMP001",
                "employeeNo": "E001",
                "employeeName": "张医生",
                "gender": "男",
                "title": "主任医师",
                "deptName": "内科",
                "position": "科室主任",
                "status": "在职"
            }
        ]
    }
}
```

### 5.2 排班管理接口

#### 5.2.1 获取排班列表
```
GET /api/hr/schedule/list?deptId=D001&startDate=2024-01-01&endDate=2024-01-31

Response:
{
    "code": 0,
    "data": [
        {
            "scheduleId": "SCH001",
            "employeeId": "EMP001",
            "employeeName": "张医生",
            "scheduleDate": "2024-01-15",
            "scheduleType": "白班",
            "startTime": "08:00",
            "endTime": "16:00"
        }
    ]
}
```

---

## 6. 业务规则与约束

### 6.1 排班规则
| 规则编码 | 规则描述 |
|----------|----------|
| SCH001 | 每人每周至少休息2天 |
| SCH002 | 夜班后需休息>=24小时 |
| SCH003 | 连续夜班不超过3天 |

### 6.2 考勤规则
| 规则编码 | 规则描述 |
|----------|----------|
| ATT001 | 迟到超过15分钟记迟到 |
| ATT002 | 早退超过15分钟记早退 |
| ATT003 | 未打卡记旷工 |

---

## 7. 模块交互关系

### 7.1 下游调用
- 所有业务模块都需要用户权限验证

---

## 8. 性能与安全要求

### 8.1 安全要求
- 权限数据严格控制
- 权限变更审计日志
- 角色权限不可随意修改
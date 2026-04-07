# 门诊管理模块需求说明书

## 1. 模块概述与目标

### 1.1 模块定位
门诊管理模块是HIS系统的核心业务模块之一，负责医院门诊全流程管理，包括预约挂号、分诊就诊、处方开立、收费结算等业务环节。

### 1.2 业务目标
- 实现门诊患者全流程信息化管理
- 优化就诊流程，减少患者等待时间
- 提高门诊医疗服务质量和效率
- 规范门诊医疗行为，保障医疗安全
- 实现门诊数据统计分析与决策支持

### 1.3 用户角色
- 患者及家属
- 挂号员
- 分诊护士
- 门诊医生
- 收费员
- 门诊管理员

---

## 2. 功能清单

### 2.1 预约挂号管理

#### 2.1.1 号源管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 排班设置 | 设置医生出诊时间、号源数量、诊室安排 | 高 |
| 号源池管理 | 管理各科室、医生的号源配额 | 高 |
| 停诊管理 | 医生停诊申请、审核、通知 | 高 |
| 加号管理 | 医生临时加号申请与管理 | 中 |
| 号源锁定 | 防止超售，锁定机制 | 高 |

#### 2.1.2 预约挂号
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 现场挂号 | 窗口挂号、自助机挂号 | 高 |
| 线上预约 | APP、微信、网站预约挂号 | 高 |
| 电话预约 | 电话预约挂号登记 | 中 |
| 预约改签 | 修改预约时间、医生 | 高 |
| 预约取消 | 取消预约、释放号源 | 高 |
| 预约提醒 | 短信/微信就诊提醒 | 中 |
| 黑名单管理 | 屡次爽约患者管理 | 中 |

#### 2.1.3 号源规则
- 支持按时间段设置号源（如每15分钟一个号）
- 支持不同号源类型（普通、专家、特需）
- 支持复诊预约、跨科预约
- 支持预约周期设置（如可提前7天预约）

### 2.2 患者登记管理

#### 2.2.1 患者建档
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 基本信息录入 | 姓名、性别、出生日期、身份证号、联系电话等 | 高 |
| 身份证识别 | 身份证读卡器自动识别录入 | 高 |
| 医保卡识别 | 医保卡读卡获取患者信息 | 高 |
| 健康卡管理 | 居民健康卡绑定与管理 | 中 |
| 照片采集 | 患者照片采集与存储 | 中 |
| 信息修改 | 患者基本信息变更记录 | 高 |

#### 2.2.2 患者信息字段规范
```
患者基本信息实体 PatientInfo:
- patientId: string, 患者唯一标识, 主键, 长度20, 必填
- idCardNo: string, 身份证号, 长度18, 唯一索引
- name: string, 姓名, 长度50, 必填
- gender: enum, 性别, [男,女,未知], 必填
- birthDate: date, 出生日期, 必填
- phone: string, 联系电话, 长度20
- address: string, 现住址, 长度200
- emergencyContact: string, 紧急联系人, 长度50
- emergencyPhone: string, 紧急联系电话, 长度20
- bloodType: enum, 血型, [A,B,AB,O,未知]
- allergyHistory: text, 过敏史
- medicalHistory: text, 病史
- createTime: datetime, 创建时间
- updateTime: datetime, 更新时间
- status: enum, 状态, [正常,注销], 默认正常
```

### 2.3 分诊排队管理

#### 2.3.1 分诊功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 分诊登记 | 患者到达确认、分诊到具体诊室 | 高 |
| 优先就诊 | 老弱病残孕优先排队设置 | 高 |
| 复诊分诊 | 检查后复诊直接分诊 | 高 |
| 转诊分诊 | 科室间转诊分诊处理 | 中 |
| 分诊叫号 | 语音叫号、屏显叫号 | 高 |
| 队列调整 | 插队、调整顺序 | 中 |

#### 2.3.2 排队规则
- 先到先服务原则（FIFO）
- 预约优先规则（预约患者优先于现场挂号）
- 复诊优先规则（当日复诊优先）
- 特殊人群优先（老年人、残疾人、孕妇等）
- 急诊优先规则

#### 2.3.3 叫号规则
```
叫号序列规则:
1. 显示格式: [号码] [患者姓名] [诊室号]
2. 语音播报: 请[号码]号患者[姓名]到[诊室号]诊室就诊
3. 屏幕刷新: 实时更新当前号、等候号、过号列表
4. 过号处理: 过号患者重新排队，排到当前队列末尾或指定位置
```

### 2.4 门诊医生工作站

#### 2.4.1 接诊功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 患者列表 | 当前待诊患者列表展示 | 高 |
| 叫号操作 | 医生点击叫号患者就诊 | 高 |
| 患者信息查看 | 查看患者基本信息、历史就诊记录 | 高 |
| 诊间排班 | 查看本人排班信息 | 中 |

#### 2.4.2 病历书写
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 病历模板 | 常用病历模板调用 | 高 |
| 主诉录入 | 患者主诉信息录入 | 高 |
| 现病史录入 | 现病史详细描述 | 高 |
| 既往史录入 | 既往病史、过敏史等 | 高 |
| 体格检查 | 体格检查结果录入 | 高 |
| 辅助检查 | 查看检验检查结果 | 高 |
| 诊断录入 | 诊断信息录入，支持ICD-10编码 | 高 |
| 处理意见 | 治疗方案、注意事项等 | 高 |
| 病历保存 | 病历保存、暂存、提交 | 高 |
| 病历打印 | 打印病历给患者 | 中 |

#### 2.4.3 处方开立
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 西药处方 | 西药/中成药处方开立 | 高 |
| 中药处方 | 中草药处方开立 | 高 |
| 处方模板 | 常用处方模板、个人处方模板 | 高 |
| 药品检索 | 按名称、拼音码检索药品 | 高 |
| 用法用量 | 药品用法用量设置 | 高 |
| 配伍审核 | 药物相互作用、禁忌审核 | 高 |
| 医保提示 | 医保用药范围提示 | 高 |
| 处方限量 | 按规定限制处方数量和天数 | 高 |
| 电子签名 | 处方电子签名 | 高 |
| 处方作废 | 处方作废流程 | 高 |

#### 2.4.4 检查检验申请
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 检验申请 | 开立检验项目申请单 | 高 |
| 检查申请 | 开立检查项目申请单（影像等） | 高 |
| 申请模板 | 常用申请模板 | 中 |
| 项目组合 | 检验项目组合包 | 中 |
| 急诊标识 | 急诊检查标识 | 高 |
| 临床诊断 | 申请单关联诊断 | 高 |

#### 2.4.5 诊断证明
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 诊断证明开具 | 开具诊断证明书 | 高 |
| 休假证明 | 开具病假证明 | 高 |
| 证明打印 | 打印各类证明 | 高 |
| 证明查询 | 历史证明查询 | 中 |

### 2.5 门诊收费管理

#### 2.5.1 收费结算
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 挂号收费 | 挂号费、诊查费收费 | 高 |
| 处方收费 | 药品费用收费 | 高 |
| 检查检验收费 | 检查检验项目收费 | 高 |
| 费用合并 | 多笔费用合并结算 | 高 |
| 费用拆分 | 按支付方式拆分结算 | 中 |
| 医保结算 | 医保实时结算 | 高 |
| 自费结算 | 自费患者结算 | 高 |
| 混合结算 | 医保+自费混合结算 | 高 |

#### 2.5.2 支付方式
- 现金支付
- 银行卡支付
- 微信支付
- 支付宝支付
- 医保卡支付
- 医院预交金支付

#### 2.5.3 票据管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 发票打印 | 打印医疗收费票据 | 高 |
| 发票重打 | 发票丢失后重新打印 | 中 |
| 发票作废 | 发票作废处理 | 高 |
| 退费处理 | 退费流程管理 | 高 |
| 电子发票 | 生成电子发票 | 中 |

### 2.6 门诊报表统计

#### 2.6.1 业务统计
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 门诊量统计 | 日/周/月/年门诊量统计 | 高 |
| 科室工作量 | 各科室接诊量统计 | 高 |
| 医生工作量 | 医生个人工作量统计 | 高 |
| 挂号来源分析 | 预约渠道分布统计 | 中 |
| 患者来源分析 | 患者地域、年龄等分布 | 中 |
| 疾病谱分析 | 诊断疾病分布统计 | 中 |

#### 2.6.2 财务统计
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 收入统计 | 门诊收入统计 | 高 |
| 科室收入 | 各科室收入统计 | 高 |
| 医生收入 | 医生业务收入统计 | 中 |
| 医保收入 | 医保结算统计 | 高 |
| 费用构成 | 药品/检查/治疗费用构成 | 中 |

---

## 3. 数据实体定义

### 3.1 核心实体

#### 3.1.1 挂号记录 Registration
```sql
CREATE TABLE registration (
    id              VARCHAR(36) PRIMARY KEY COMMENT '挂号ID',
    patient_id      VARCHAR(20) NOT NULL COMMENT '患者ID',
    patient_name    VARCHAR(50) NOT NULL COMMENT '患者姓名',
    id_card_no      VARCHAR(18) COMMENT '身份证号',
    gender          CHAR(1) NOT NULL COMMENT '性别',
    age             INT COMMENT '年龄',
    phone           VARCHAR(20) COMMENT '联系电话',

    dept_id         VARCHAR(20) NOT NULL COMMENT '科室ID',
    dept_name       VARCHAR(100) COMMENT '科室名称',
    doctor_id       VARCHAR(20) COMMENT '医生ID',
    doctor_name     VARCHAR(50) COMMENT '医生姓名',
    schedule_id     VARCHAR(36) COMMENT '排班ID',
    schedule_date   DATE NOT NULL COMMENT '就诊日期',
    time_period     VARCHAR(20) COMMENT '时间段(上午/下午)',
    queue_no        INT COMMENT '排队序号',
    visit_no        VARCHAR(30) COMMENT '就诊序号',

    registration_type VARCHAR(20) COMMENT '挂号类型(普通/专家/特需)',
    registration_fee DECIMAL(10,2) COMMENT '挂号费',
    diagnosis_fee   DECIMAL(10,2) COMMENT '诊查费',
    total_fee       DECIMAL(10,2) COMMENT '总费用',

    status          VARCHAR(20) NOT NULL COMMENT '状态(已预约/已挂号/已就诊/已退号)',
    visit_status    VARCHAR(20) COMMENT '就诊状态(待诊/就诊中/已完成)',

    source          VARCHAR(20) COMMENT '来源(现场/微信/APP/电话)',
    booking_time    DATETIME COMMENT '预约时间',
    check_in_time   DATETIME COMMENT '签到时间',
    start_time      DATETIME COMMENT '就诊开始时间',
    end_time        DATETIME COMMENT '就诊结束时间',

    operator_id     VARCHAR(20) COMMENT '操作员ID',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_patient (patient_id),
    INDEX idx_schedule (schedule_date, dept_id),
    INDEX idx_status (status, schedule_date)
);
```

#### 3.1.2 排班信息 Schedule
```sql
CREATE TABLE schedule (
    id              VARCHAR(36) PRIMARY KEY COMMENT '排班ID',
    dept_id         VARCHAR(20) NOT NULL COMMENT '科室ID',
    doctor_id       VARCHAR(20) NOT NULL COMMENT '医生ID',
    schedule_date   DATE NOT NULL COMMENT '排班日期',
    time_period     VARCHAR(20) NOT NULL COMMENT '时间段(上午/下午/全天)',
    start_time      TIME COMMENT '开始时间',
    end_time        TIME COMMENT '结束时间',

    total_quota     INT NOT NULL DEFAULT 0 COMMENT '总号源数',
    booked_quota    INT NOT NULL DEFAULT 0 COMMENT '已预约数',
    available_quota INT NOT NULL DEFAULT 0 COMMENT '剩余号源数',

    registration_type VARCHAR(20) COMMENT '挂号类型',
    registration_fee DECIMAL(10,2) COMMENT '挂号费',
    diagnosis_fee   DECIMAL(10,2) COMMENT '诊查费',

    status          VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态(正常/停诊)',
    stop_reason     VARCHAR(200) COMMENT '停诊原因',

    clinic_room     VARCHAR(50) COMMENT '诊室',

    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_schedule (doctor_id, schedule_date, time_period),
    INDEX idx_date (schedule_date),
    INDEX idx_dept_date (dept_id, schedule_date)
);
```

#### 3.1.3 门诊病历 OutpatientRecord
```sql
CREATE TABLE outpatient_record (
    id              VARCHAR(36) PRIMARY KEY COMMENT '病历ID',
    registration_id VARCHAR(36) NOT NULL COMMENT '挂号ID',
    patient_id      VARCHAR(20) NOT NULL COMMENT '患者ID',
    visit_no        VARCHAR(30) COMMENT '就诊序号',

    dept_id         VARCHAR(20) NOT NULL COMMENT '科室ID',
    doctor_id       VARCHAR(20) NOT NULL COMMENT '医生ID',
    visit_date      DATE NOT NULL COMMENT '就诊日期',

    chief_complaint TEXT COMMENT '主诉',
    present_illness TEXT COMMENT '现病史',
    past_history    TEXT COMMENT '既往史',
    allergy_history TEXT COMMENT '过敏史',
    personal_history TEXT COMMENT '个人史',
    family_history  TEXT COMMENT '家族史',

    temperature     DECIMAL(4,1) COMMENT '体温',
    pulse           INT COMMENT '脉搏',
    respiration     INT COMMENT '呼吸',
    blood_pressure  VARCHAR(20) COMMENT '血压',
    height          INT COMMENT '身高(cm)',
    weight          DECIMAL(5,1) COMMENT '体重(kg)',

    physical_exam   TEXT COMMENT '体格检查',
    auxiliary_exam  TEXT COMMENT '辅助检查',

    diagnosis_code  VARCHAR(50) COMMENT '诊断编码(ICD-10)',
    diagnosis_name  VARCHAR(200) COMMENT '诊断名称',
    diagnosis_type  VARCHAR(20) COMMENT '诊断类型(主要/次要)',

    treatment_plan  TEXT COMMENT '治疗方案',
    medical_advice  TEXT COMMENT '医嘱/注意事项',

    status          VARCHAR(20) NOT NULL DEFAULT '草稿' COMMENT '状态(草稿/已提交/已作废)',

    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME ON UPDATE CURRENT_TIMESTAMP,
    submit_time     DATETIME COMMENT '提交时间',

    INDEX idx_registration (registration_id),
    INDEX idx_patient (patient_id, visit_date),
    INDEX idx_doctor (doctor_id, visit_date)
);
```

#### 3.1.4 门诊处方 OutpatientPrescription
```sql
CREATE TABLE outpatient_prescription (
    id              VARCHAR(36) PRIMARY KEY COMMENT '处方ID',
    prescription_no VARCHAR(30) NOT NULL COMMENT '处方号',
    registration_id VARCHAR(36) NOT NULL COMMENT '挂号ID',
    patient_id      VARCHAR(20) NOT NULL COMMENT '患者ID',
    patient_name    VARCHAR(50) NOT NULL COMMENT '患者姓名',
    gender          CHAR(1) COMMENT '性别',
    age             INT COMMENT '年龄',

    dept_id         VARCHAR(20) NOT NULL COMMENT '科室ID',
    doctor_id       VARCHAR(20) NOT NULL COMMENT '开方医生ID',
    doctor_name     VARCHAR(50) COMMENT '开方医生姓名',

    prescription_type VARCHAR(20) NOT NULL COMMENT '处方类型(西药/中药)',
    prescription_date DATE NOT NULL COMMENT '处方日期',

    diagnosis_code  VARCHAR(50) COMMENT '诊断编码',
    diagnosis_name  VARCHAR(200) COMMENT '诊断名称',

    total_amount    DECIMAL(10,2) COMMENT '处方总金额',
    pay_status      VARCHAR(20) DEFAULT '未收费' COMMENT '收费状态(未收费/已收费/已退费)',
    pay_time        DATETIME COMMENT '收费时间',

    status          VARCHAR(20) NOT NULL DEFAULT '有效' COMMENT '状态(有效/已作废/已退费)',
    audit_status    VARCHAR(20) COMMENT '审核状态(待审核/审核通过/审核不通过)',
    auditor_id      VARCHAR(20) COMMENT '审核人ID',
    audit_time      DATETIME COMMENT '审核时间',
    audit_remark    VARCHAR(500) COMMENT '审核备注',

    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_prescription_no (prescription_no),
    INDEX idx_registration (registration_id),
    INDEX idx_patient (patient_id),
    INDEX idx_status (status, prescription_date)
);
```

#### 3.1.5 处方明细 PrescriptionDetail
```sql
CREATE TABLE prescription_detail (
    id              VARCHAR(36) PRIMARY KEY COMMENT '明细ID',
    prescription_id VARCHAR(36) NOT NULL COMMENT '处方ID',

    drug_id         VARCHAR(20) NOT NULL COMMENT '药品ID',
    drug_name       VARCHAR(100) NOT NULL COMMENT '药品名称',
    drug_spec       VARCHAR(50) COMMENT '药品规格',
    drug_unit       VARCHAR(20) COMMENT '单位',
    drug_form       VARCHAR(20) COMMENT '剂型',

    quantity        DECIMAL(10,2) NOT NULL COMMENT '数量',
    dosage          VARCHAR(50) COMMENT '用法',
    frequency       VARCHAR(50) COMMENT '使用频率',
    days            INT COMMENT '用药天数',
    route           VARCHAR(50) COMMENT '给药途径',

    unit_price      DECIMAL(10,4) COMMENT '单价',
    amount          DECIMAL(10,2) COMMENT '金额',

    group_no        INT COMMENT '组号(用于输液分组)',
    skin_test       VARCHAR(10) COMMENT '皮试要求',
    skin_test_result VARCHAR(20) COMMENT '皮试结果',

    is_essential    TINYINT COMMENT '是否基药',
    is_medical_insurance TINYINT COMMENT '是否医保',

    remark          VARCHAR(200) COMMENT '备注',

    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_prescription (prescription_id),
    INDEX idx_drug (drug_id)
);
```

---

## 4. 业务流程

### 4.1 挂号就诊流程
```
┌─────────────────────────────────────────────────────────────────┐
│                        门诊就诊流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 预约挂号  │───>│ 到院签到  │───>│ 分诊排队  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                                        │                        │
│                                        ▼                        │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 收费结算  │<───│ 开立处方  │<───│ 医生接诊  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│        │                │                │                      │
│        │                ▼                ▼                      │
│        │         ┌──────────┐    ┌──────────┐                │
│        │         │ 开立检查  │───>│ 检查检验  │                │
│        │         │ 检验申请  │    └──────────┘                │
│        │         └──────────┘         │                       │
│        │                              ▼                       │
│        │                       ┌──────────┐                   │
│        └──────────────────────>│ 取药/治疗 │                   │
│                                └──────────┘                   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 退号流程
```
1. 患者申请退号
2. 检查是否已就诊
   - 已就诊：不允许退号
   - 未就诊：继续
3. 检查是否已收费
   - 已收费：先办理退费
   - 未收费：直接退号
4. 释放号源
5. 更新挂号状态为"已退号"
6. 记录退号日志
```

### 4.3 处方流程
```
1. 医生开立处方
2. 系统自动校验
   - 配伍禁忌检查
   - 药品库存检查
   - 医保范围检查
   - 处方限量检查
3. 处方审核（如需要）
4. 患者缴费
5. 药房发药
6. 处方归档
```

---

## 5. 接口定义

### 5.1 预约挂号接口

#### 5.1.1 创建预约
```
POST /api/outpatient/appointment/create

Request:
{
    "patientId": "P12345678",
    "deptId": "D001",
    "doctorId": "DOC001",
    "scheduleDate": "2024-01-15",
    "timePeriod": "上午",
    "registrationType": "专家",
    "source": "APP",
    "remark": "复诊"
}

Response:
{
    "code": 0,
    "message": "预约成功",
    "data": {
        "appointmentId": "APT202401150001",
        "queueNo": 15,
        "scheduleTime": "09:30-10:00",
        "registrationFee": 50.00,
        "diagnosisFee": 20.00,
        "totalFee": 70.00
    }
}
```

#### 5.1.2 取消预约
```
POST /api/outpatient/appointment/cancel

Request:
{
    "appointmentId": "APT202401150001",
    "cancelReason": "临时有事"
}

Response:
{
    "code": 0,
    "message": "取消成功"
}
```

#### 5.1.3 获取排班列表
```
GET /api/outpatient/schedule/list?deptId=D001&startDate=2024-01-15&endDate=2024-01-21

Response:
{
    "code": 0,
    "data": [
        {
            "scheduleId": "SCH001",
            "doctorId": "DOC001",
            "doctorName": "张医生",
            "doctorTitle": "主任医师",
            "scheduleDate": "2024-01-15",
            "timePeriod": "上午",
            "totalQuota": 30,
            "availableQuota": 5,
            "registrationFee": 50.00,
            "diagnosisFee": 20.00
        }
    ]
}
```

### 5.2 分诊排队接口

#### 5.2.1 签到
```
POST /api/outpatient/checkin

Request:
{
    "registrationId": "REG202401150001",
    "patientId": "P12345678"
}

Response:
{
    "code": 0,
    "message": "签到成功",
    "data": {
        "queueNo": 15,
        "waitCount": 3,
        "estimatedWaitTime": 15,
        "clinicRoom": "101"
    }
}
```

#### 5.2.2 获取排队信息
```
GET /api/outpatient/queue/info?clinicRoom=101

Response:
{
    "code": 0,
    "data": {
        "currentNo": 12,
        "currentPatient": "张*明",
        "waitingList": [
            {"queueNo": 13, "patientName": "李*华", "status": "等候中"},
            {"queueNo": 14, "patientName": "王*伟", "status": "等候中"},
            {"queueNo": 15, "patientName": "陈*芳", "status": "等候中"}
        ],
        "passedList": [
            {"queueNo": 10, "patientName": "赵*强", "passTime": "09:15"}
        ]
    }
}
```

### 5.3 医生工作站接口

#### 5.3.1 获取待诊患者列表
```
GET /api/outpatient/doctor/pending?doctorId=DOC001&date=2024-01-15

Response:
{
    "code": 0,
    "data": {
        "total": 30,
        "visited": 10,
        "waiting": 15,
        "passed": 5,
        "patients": [
            {
                "registrationId": "REG001",
                "queueNo": 11,
                "patientId": "P001",
                "patientName": "李明",
                "gender": "男",
                "age": 35,
                "status": "候诊中",
                "waitTime": 20,
                "visitCount": 3,
                "lastVisitDate": "2024-01-10"
            }
        ]
    }
}
```

#### 5.3.2 叫号
```
POST /api/outpatient/doctor/call

Request:
{
    "registrationId": "REG001",
    "doctorId": "DOC001",
    "clinicRoom": "101"
}

Response:
{
    "code": 0,
    "message": "叫号成功"
}
```

#### 5.3.3 保存病历
```
POST /api/outpatient/record/save

Request:
{
    "registrationId": "REG001",
    "patientId": "P001",
    "chiefComplaint": "头痛、发热3天",
    "presentIllness": "患者3天前无明显诱因出现头痛...",
    "pastHistory": "高血压病史5年",
    "allergyHistory": "青霉素过敏",
    "physicalExam": "T:38.5°C, P:88次/分...",
    "diagnosisCode": "J06.900",
    "diagnosisName": "急性上呼吸道感染",
    "treatmentPlan": "1. 休息，多饮水\n2. 对症治疗",
    "medicalAdvice": "注意休息，多饮水，不适随诊"
}

Response:
{
    "code": 0,
    "message": "保存成功",
    "data": {
        "recordId": "REC202401150001"
    }
}
```

#### 5.3.4 开立处方
```
POST /api/outpatient/prescription/create

Request:
{
    "registrationId": "REG001",
    "patientId": "P001",
    "prescriptionType": "西药",
    "diagnosisCode": "J06.900",
    "diagnosisName": "急性上呼吸道感染",
    "details": [
        {
            "drugId": "DRUG001",
            "drugName": "布洛芬缓释胶囊",
            "drugSpec": "0.3g*20粒",
            "quantity": 1,
            "dosage": "每次1粒",
            "frequency": "每日2次",
            "days": 5,
            "route": "口服"
        },
        {
            "drugId": "DRUG002",
            "drugName": "阿莫西林胶囊",
            "drugSpec": "0.5g*24粒",
            "quantity": 2,
            "dosage": "每次2粒",
            "frequency": "每日3次",
            "days": 5,
            "route": "口服",
            "skinTest": "需皮试"
        }
    ]
}

Response:
{
    "code": 0,
    "message": "处方开立成功",
    "data": {
        "prescriptionId": "PRE202401150001",
        "prescriptionNo": "RX2024011500001",
        "totalAmount": 58.50,
        "warnings": [
            "药品[阿莫西林胶囊]需皮试"
        ]
    }
}
```

### 5.4 收费接口

#### 5.4.1 获取待收费项目
```
GET /api/outpatient/billing/pending?registrationId=REG001

Response:
{
    "code": 0,
    "data": {
        "patientInfo": {
            "patientId": "P001",
            "patientName": "李明",
            "gender": "男",
            "age": 35
        },
        "items": [
            {
                "itemId": "ITEM001",
                "itemType": "处方",
                "itemNo": "RX2024011500001",
                "description": "布洛芬缓释胶囊等2种药品",
                "amount": 58.50
            },
            {
                "itemId": "ITEM002",
                "itemType": "检查",
                "itemNo": "EXAM2024011500001",
                "description": "血常规",
                "amount": 25.00
            }
        ],
        "totalAmount": 83.50
    }
}
```

#### 5.4.2 结算收费
```
POST /api/outpatient/billing/settle

Request:
{
    "registrationId": "REG001",
    "patientId": "P001",
    "itemIds": ["ITEM001", "ITEM002"],
    "payments": [
        {
            "payMethod": "MEDICAL_INSURANCE",
            "amount": 50.00,
            "insuranceCard": "1234567890"
        },
        {
            "payMethod": "WECHAT",
            "amount": 33.50,
            "transactionId": "wx123456"
        }
    ]
}

Response:
{
    "code": 0,
    "message": "收费成功",
    "data": {
        "billId": "BILL202401150001",
        "invoiceNo": "INV202401150001",
        "totalAmount": 83.50,
        "insuranceAmount": 50.00,
        "selfPayAmount": 33.50,
        "invoiceUrl": "/api/outpatient/billing/invoice/INV202401150001"
    }
}
```

---

## 6. 业务规则与约束

### 6.1 挂号规则
| 规则编码 | 规则描述 | 规则值 |
|----------|----------|--------|
| REG001 | 预约提前天数上限 | 7天 |
| REG002 | 取消预约提前时间 | 就诊前2小时 |
| REG003 | 爽约次数上限（进入黑名单） | 3次 |
| REG004 | 黑名单限制天数 | 30天 |
| REG005 | 号源锁定时间 | 5分钟 |
| REG006 | 单次挂号最大数量 | 3个号 |

### 6.2 处方规则
| 规则编码 | 规则描述 | 规则值 |
|----------|----------|--------|
| RX001 | 急诊处方限量 | 3天 |
| RX002 | 普通门诊处方限量 | 7天 |
| RX003 | 慢性病处方限量 | 14-30天 |
| RX004 | 精神类药品限量 | 严格按照国家规定 |
| RX005 | 处方有效期 | 24小时（急诊）/ 3天（普通） |

### 6.3 配伍禁忌检查
- 同一成分药品重复使用检查
- 药物相互作用检查
- 药物与诊断禁忌检查
- 药物与患者过敏史检查
- 剂量超限检查
- 给药途径合理性检查

### 6.4 数据校验规则
```yaml
患者信息校验:
  - 身份证号: 18位，符合校验规则
  - 姓名: 2-50字符，不含特殊符号
  - 电话: 11位手机号或固话格式
  - 年龄: 0-150岁

挂号信息校验:
  - 科室: 必须为有效科室
  - 医生: 必须有有效排班
  - 号源: 剩余号源>0
  - 时间: 排班日期>=当前日期

处方校验:
  - 药品: 必须为有效药品
  - 数量: >0，不超过库存
  - 用法: 必须填写
  - 频次: 必须填写
  - 天数: 1-30天
```

---

## 7. 模块交互关系

### 7.1 上游依赖
- **系统管理模块**: 用户认证、权限验证、科室信息、员工信息
- **基础数据模块**: 数据字典、价表信息

### 7.2 下游调用
- **住院管理模块**: 门诊收入院
- **药房管理模块**: 处方发药
- **检验管理模块**: 检验申请
- **影像管理模块**: 检查申请
- **电子病历模块**: 病历归档
- **财务收费模块**: 费用结算

### 7.3 外部接口
- **医保接口**: 医保结算、医保审核
- **支付接口**: 微信/支付宝/银行卡支付
- **短信接口**: 预约提醒、就诊提醒
- **自助机接口**: 自助挂号、自助缴费

---

## 8. 性能与安全要求

### 8.1 性能要求
| 指标 | 要求 |
|------|------|
| 挂号响应时间 | < 2秒 |
| 查询响应时间 | < 1秒 |
| 处方保存时间 | < 3秒 |
| 并发挂号用户 | >= 1000人/分钟 |
| 系统可用性 | >= 99.9% |
| 数据备份频率 | 每日增量，每周全量 |

### 8.2 安全要求
1. **身份认证**
   - 用户登录认证（用户名+密码）
   - 支持多因素认证
   - 会话超时控制（30分钟无操作自动退出）

2. **权限控制**
   - 基于角色的访问控制（RBAC）
   - 数据权限（科室数据隔离）
   - 操作权限（功能授权）

3. **数据安全**
   - 敏感数据加密存储
   - 传输数据HTTPS加密
   - 操作日志完整记录
   - 数据脱敏展示

4. **审计要求**
   - 所有操作记录审计日志
   - 敏感操作实时告警
   - 日志保存>=3年

### 8.3 数据隐私
- 患者信息最小化原则
- 数据访问授权控制
- 数据导出审批流程
- 数据脱敏处理

---

## 9. 附录

### 9.1 状态流转图

#### 挂号状态
```
已预约 -> 已挂号 -> 已签到 -> 就诊中 -> 已完成
   │         │        │         │
   └────已退号┴────────┴────────┘
```

#### 处方状态
```
草稿 -> 有效 -> 已收费 -> 已发药
  │      │
  └────作废
```

### 9.2 错误码定义
| 错误码 | 错误描述 |
|--------|----------|
| 10001 | 号源已满 |
| 10002 | 号源已过期 |
| 10003 | 患者信息不完整 |
| 10004 | 排班不存在 |
| 10005 | 预约已取消 |
| 10006 | 未到预约时间 |
| 10007 | 爽约次数超限 |
| 20001 | 处方配伍禁忌 |
| 20002 | 药品库存不足 |
| 20003 | 处方已收费 |
| 20004 | 处方已作废 |
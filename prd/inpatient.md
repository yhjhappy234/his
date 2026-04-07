# 住院管理模块需求说明书

## 1. 模块概述与目标

### 1.1 模块定位
住院管理模块是HIS系统的核心业务模块，负责住院患者从入院到出院全流程管理，包括入院登记、床位管理、护理管理、医嘱执行、费用管理等业务。

### 1.2 业务目标
- 实现住院患者全流程闭环管理
- 提高床位周转率和使用效率
- 规范医疗行为，保障医疗安全
- 实现住院费用精细化管理
- 提供临床决策支持

### 1.3 用户角色
- 入院处工作人员
- 病区护士
- 护士长
- 住院医生
- 主治医生
- 病房管理员
- 财务人员

---

## 2. 功能清单

### 2.1 入院管理

#### 2.1.1 入院登记
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 入院申请 | 医生开具入院证 | 高 |
| 入院预约 | 预约入院床位 | 高 |
| 入院登记 | 患者信息登记、床位分配 | 高 |
| 入院评估 | 入院护理评估 | 高 |
| 病历建立 | 建立住院病历 | 高 |
| 医保登记 | 医保入院登记 | 高 |
| 预交金收取 | 收取住院预交金 | 高 |

#### 2.1.2 入院信息字段
```
入院登记信息 AdmissionInfo:
- admissionId: string, 住院号, 主键, 长度20
- patientId: string, 患者ID, 必填
- admissionTime: datetime, 入院时间, 必填
- admissionType: enum, 入院类型, [急诊/门诊/转院], 必填
- admissionDept: string, 入院科室, 必填
- admissionWard: string, 入院病区, 必填
- bedNo: string, 床位号
- admittingDoctor: string, 接诊医生
- admittingNurse: string, 接诊护士
- diagnosis: string, 入院诊断
- diagnosisCode: string, 诊断编码(ICD-10)
- nursingLevel: enum, 护理等级, [特级/一级/二级/三级]
- dietType: enum, 饮食类型, [普食/软食/流食/禁食等]
- deposit: decimal, 预交金金额
- insuranceType: string, 医保类型
- insuranceNo: string, 医保卡号
- contactPerson: string, 联系人
- contactPhone: string, 联系电话
- status: enum, 状态, [待入院/在院/已出院]
```

### 2.2 床位管理

#### 2.2.1 床位基础管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 病区设置 | 病区信息维护、科室关联 | 高 |
| 病房设置 | 病房信息、床位数量 | 高 |
| 床位设置 | 床位属性、床位费等级 | 高 |
| 床位状态 | 空床/占用/维修/预留 | 高 |
| 床位配置 | 床位设施配置（氧气、呼叫器等） | 中 |

#### 2.2.2 床位分配
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 床位选择 | 手动/自动分配床位 | 高 |
| 床位预留 | 为预约患者预留床位 | 高 |
| 床位调换 | 住院期间床位调换 | 高 |
| 床位释放 | 出院/转科时释放床位 | 高 |
| 床位查询 | 床位占用情况查询 | 高 |
| 床位统计 | 床位使用率统计 | 中 |

#### 2.2.3 床位状态定义
```
床位状态枚举 BedStatus:
- VACANT: 空床, 可分配
- OCCUPIED: 占用, 有患者
- RESERVED: 预留, 已预约未入住
- MAINTENANCE: 维修, 暂停使用
- ISOLATION: 隔离, 感染控制
- CLEANING: 打扫, 准备中
```

### 2.3 病区管理

#### 2.3.1 病区信息管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 病区基本信息 | 病区名称、位置、科室 | 高 |
| 病区床位统计 | 总床位、空床、占用统计 | 高 |
| 病区人员配置 | 护士长、责任护士配置 | 高 |
| 病区设备管理 | 病区设备登记 | 中 |

#### 2.3.2 护士站管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 患者一览表 | 病区患者列表展示 | 高 |
| 护理白板 | 护理工作看板 | 高 |
| 交班管理 | 护士交接班记录 | 高 |
| 探视管理 | 探视人员登记 | 中 |
| 护理排班 | 护士排班管理 | 中 |

### 2.4 医嘱管理

#### 2.4.1 医嘱类型
- 长期医嘱：持续执行的医嘱
- 临时医嘱：一次性执行的医嘱
- 出院医嘱：出院时执行的医嘱

#### 2.4.2 医嘱开立
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 医嘱录入 | 医生开立医嘱 | 高 |
| 医嘱模板 | 常用医嘱模板 | 高 |
| 医嘱复制 | 复制历史医嘱 | 中 |
| 医嘱校验 | 合理性校验 | 高 |
| 电子签名 | 医嘱电子签名 | 高 |

#### 2.4.3 医嘱执行
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 医嘱审核 | 护士审核医嘱 | 高 |
| 医嘱执行 | 护士执行医嘱 | 高 |
| 执行记录 | 记录执行情况 | 高 |
| 执行反馈 | 执行结果反馈 | 高 |
| 停止医嘱 | 停止长期医嘱 | 高 |

#### 2.4.4 医嘱生命周期
```
医嘱状态流转:
开立 -> 审核 -> 执行中 -> 已完成
  │       │
  │       └── 驳回
  │
  └── 作废

长期医嘱:
开立 -> 审核 -> 执行中 -> 停止 -> 已停止
```

### 2.5 护理管理

#### 2.5.1 护理评估
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 入院评估 | 入院护理评估 | 高 |
| 跌倒评估 | 跌倒风险评估 | 高 |
| 压疮评估 | 压疮风险评估 | 高 |
| 疼痛评估 | 疼痛程度评估 | 高 |
| 营养评估 | 营养状况评估 | 中 |
| 自理能力评估 | 日常生活能力评估 | 中 |

#### 2.5.2 护理记录
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 护理记录单 | 护理过程记录 | 高 |
| 生命体征 | 体温、脉搏、呼吸、血压记录 | 高 |
| 出入量记录 | 出入量统计 | 高 |
| 护理措施 | 护理措施记录 | 高 |
| 健康宣教 | 健康宣教记录 | 中 |

#### 2.5.3 护理等级
| 等级 | 护理要求 | 巡视频次 |
|------|----------|----------|
| 特级护理 | 24小时专人护理 | 持续监护 |
| 一级护理 | 重点护理 | 每小时巡视 |
| 二级护理 | 一般护理 | 每2小时巡视 |
| 三级护理 | 基础护理 | 每3小时巡视 |

### 2.6 转科管理

#### 2.6.1 转科流程
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 转科申请 | 医生发起转科申请 | 高 |
| 转科审核 | 接收科室审核 | 高 |
| 床位安排 | 接收科室安排床位 | 高 |
| 转科交接 | 护理交接记录 | 高 |
| 病历转递 | 病历资料转递 | 高 |

### 2.7 出院管理

#### 2.7.1 出院流程
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 出院医嘱 | 医生开具出院医嘱 | 高 |
| 出院申请 | 申请办理出院 | 高 |
| 费用结算 | 住院费用结算 | 高 |
| 出院小结 | 出院小结书写 | 高 |
| 出院带药 | 出院带药处方 | 高 |
| 出院宣教 | 出院健康指导 | 中 |
| 床位释放 | 释放床位 | 高 |

#### 2.7.2 出院记录
```
出院记录信息 DischargeInfo:
- dischargeId: string, 出院ID
- admissionId: string, 住院号
- patientId: string, 患者ID
- dischargeTime: datetime, 出院时间
- dischargeType: enum, 出院类型, [治愈/好转/未愈/死亡/转院/自动出院]
- admissionDiagnosis: string, 入院诊断
- dischargeDiagnosis: string, 出院诊断
- mainDiagnosis: string, 主要诊断
- secondaryDiagnosis: string, 其他诊断
- operationInfo: string, 手术情况
- treatmentSummary: string, 治疗经过
- dischargeAdvice: string, 出院医嘱
- followUpDate: date, 复诊日期
- totalDays: int, 住院天数
- totalCost: decimal, 总费用
- insurancePayment: decimal, 医保支付
- selfPayment: decimal, 自付金额
```

### 2.8 费用管理

#### 2.8.1 费用录入
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 医嘱计费 | 医嘱自动生成费用 | 高 |
| 手工计费 | 手工录入费用 | 高 |
| 退费处理 | 费用退费申请 | 高 |
| 费用查询 | 患者费用明细查询 | 高 |
| 一日清单 | 每日费用清单打印 | 高 |

#### 2.8.2 预交金管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 预交金缴纳 | 缴纳预交金 | 高 |
| 预交金查询 | 查询预交金余额 | 高 |
| 预交金催缴 | 预交金不足提醒 | 高 |
| 预交金结算 | 出院时结算预交金 | 高 |

---

## 3. 数据实体定义

### 3.1 核心实体

#### 3.1.1 住院记录 InpatientAdmission
```sql
CREATE TABLE inpatient_admission (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '住院ID',
    admission_no        VARCHAR(20) NOT NULL UNIQUE COMMENT '住院号',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',
    patient_name        VARCHAR(50) NOT NULL COMMENT '患者姓名',
    id_card_no          VARCHAR(18) COMMENT '身份证号',
    gender              CHAR(1) NOT NULL COMMENT '性别',
    birth_date          DATE COMMENT '出生日期',
    age                 INT COMMENT '年龄',
    phone               VARCHAR(20) COMMENT '联系电话',
    address             VARCHAR(200) COMMENT '住址',

    admission_time      DATETIME NOT NULL COMMENT '入院时间',
    admission_type      VARCHAR(20) NOT NULL COMMENT '入院类型(急诊/门诊/转院)',
    admission_source    VARCHAR(50) COMMENT '入院来源',

    dept_id             VARCHAR(20) NOT NULL COMMENT '科室ID',
    dept_name           VARCHAR(100) COMMENT '科室名称',
    ward_id             VARCHAR(20) NOT NULL COMMENT '病区ID',
    ward_name           VARCHAR(100) COMMENT '病区名称',
    room_no             VARCHAR(20) COMMENT '病房号',
    bed_no              VARCHAR(20) COMMENT '床位号',

    doctor_id           VARCHAR(20) NOT NULL COMMENT '主治医生ID',
    doctor_name         VARCHAR(50) COMMENT '主治医生姓名',
    nurse_id            VARCHAR(20) COMMENT '责任护士ID',
    nurse_name          VARCHAR(50) COMMENT '责任护士姓名',

    admission_diagnosis VARCHAR(500) COMMENT '入院诊断',
    admission_diagnosis_code VARCHAR(50) COMMENT '入院诊断编码',
    discharge_diagnosis VARCHAR(500) COMMENT '出院诊断',
    discharge_diagnosis_code VARCHAR(50) COMMENT '出院诊断编码',

    nursing_level       VARCHAR(20) COMMENT '护理等级',
    diet_type           VARCHAR(20) COMMENT '饮食类型',
    allergy_info        TEXT COMMENT '过敏信息',

    insurance_type      VARCHAR(50) COMMENT '医保类型',
    insurance_no        VARCHAR(50) COMMENT '医保卡号',

    deposit             DECIMAL(12,2) DEFAULT 0 COMMENT '预交金总额',
    total_cost          DECIMAL(12,2) DEFAULT 0 COMMENT '费用总额',
    settled_cost        DECIMAL(12,2) DEFAULT 0 COMMENT '已结算金额',

    status              VARCHAR(20) NOT NULL DEFAULT '在院' COMMENT '状态(待入院/在院/转科中/已出院)',
    discharge_time      DATETIME COMMENT '出院时间',
    discharge_type      VARCHAR(20) COMMENT '出院类型',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_patient (patient_id),
    INDEX idx_admission_time (admission_time),
    INDEX idx_dept_status (dept_id, status),
    INDEX idx_bed (ward_id, bed_no)
);
```

#### 3.1.2 床位信息 Bed
```sql
CREATE TABLE bed (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '床位ID',
    bed_no              VARCHAR(20) NOT NULL COMMENT '床位号',
    ward_id             VARCHAR(20) NOT NULL COMMENT '病区ID',
    ward_name           VARCHAR(100) COMMENT '病区名称',
    room_no             VARCHAR(20) NOT NULL COMMENT '病房号',
    bed_type            VARCHAR(20) COMMENT '床位类型(普通/VIP/ICU)',
    bed_level           VARCHAR(20) COMMENT '床位等级',
    daily_rate          DECIMAL(10,2) COMMENT '床位费/天',

    status              VARCHAR(20) NOT NULL DEFAULT '空床' COMMENT '状态(空床/占用/预留/维修/隔离)',
    admission_id        VARCHAR(36) COMMENT '住院ID',
    patient_id          VARCHAR(20) COMMENT '患者ID',
    patient_name        VARCHAR(50) COMMENT '患者姓名',

    reserved_time       DATETIME COMMENT '预留时间',
    reserved_patient_id VARCHAR(20) COMMENT '预留患者ID',

    facilities          TEXT COMMENT '设施配置(JSON)',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_ward_bed (ward_id, bed_no),
    INDEX idx_status (status)
);
```

#### 3.1.3 医嘱信息 MedicalOrder
```sql
CREATE TABLE medical_order (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '医嘱ID',
    order_no            VARCHAR(30) NOT NULL COMMENT '医嘱编号',
    admission_id        VARCHAR(36) NOT NULL COMMENT '住院ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    order_type          VARCHAR(20) NOT NULL COMMENT '医嘱类型(长期/临时)',
    order_category      VARCHAR(20) NOT NULL COMMENT '医嘱分类(药品/检查/检验/治疗/护理/饮食)',
    order_content       TEXT NOT NULL COMMENT '医嘱内容',
    order_detail        TEXT COMMENT '医嘱详情(JSON)',

    start_time          DATETIME COMMENT '开始时间',
    end_time            DATETIME COMMENT '结束时间',
    execute_time        VARCHAR(50) COMMENT '执行时间描述',
    frequency           VARCHAR(50) COMMENT '执行频次',

    doctor_id           VARCHAR(20) NOT NULL COMMENT '开立医生ID',
    doctor_name         VARCHAR(50) COMMENT '开立医生姓名',
    order_time          DATETIME NOT NULL COMMENT '医嘱时间',

    nurse_id            VARCHAR(20) COMMENT '审核护士ID',
    nurse_name          VARCHAR(50) COMMENT '审核护士姓名',
    audit_time          DATETIME COMMENT '审核时间',

    status              VARCHAR(20) NOT NULL DEFAULT '新开' COMMENT '状态(新开/审核/执行中/停止/完成/作废)',

    stop_doctor_id      VARCHAR(20) COMMENT '停止医生ID',
    stop_doctor_name    VARCHAR(50) COMMENT '停止医生姓名',
    stop_time           DATETIME COMMENT '停止时间',
    stop_reason         VARCHAR(200) COMMENT '停止原因',

    group_no            INT COMMENT '组号(成组医嘱)',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_admission (admission_id),
    INDEX idx_patient (patient_id),
    INDEX idx_status (status)
);
```

#### 3.1.4 医嘱执行记录 OrderExecution
```sql
CREATE TABLE order_execution (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '执行ID',
    order_id            VARCHAR(36) NOT NULL COMMENT '医嘱ID',
    admission_id        VARCHAR(36) NOT NULL COMMENT '住院ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    execute_time        DATETIME NOT NULL COMMENT '执行时间',
    execute_nurse_id    VARCHAR(20) NOT NULL COMMENT '执行护士ID',
    execute_nurse_name  VARCHAR(50) COMMENT '执行护士姓名',

    execute_result      TEXT COMMENT '执行结果',
    execute_detail      TEXT COMMENT '执行详情(JSON)',

    status              VARCHAR(20) NOT NULL COMMENT '执行状态(已执行/已跳过/异常)',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_order (order_id),
    INDEX idx_admission (admission_id),
    INDEX idx_execute_time (execute_time)
);
```

#### 3.1.5 护理记录 NursingRecord
```sql
CREATE TABLE nursing_record (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '记录ID',
    admission_id        VARCHAR(36) NOT NULL COMMENT '住院ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    record_time         DATETIME NOT NULL COMMENT '记录时间',
    record_type         VARCHAR(20) NOT NULL COMMENT '记录类型(生命体征/护理记录/评估记录)',

    temperature         DECIMAL(4,1) COMMENT '体温(℃)',
    pulse               INT COMMENT '脉搏(次/分)',
    respiration         INT COMMENT '呼吸(次/分)',
    blood_pressure_systolic INT COMMENT '收缩压',
    blood_pressure_diastolic INT COMMENT '舒张压',
    spo2                INT COMMENT '血氧饱和度(%)',
    weight              DECIMAL(5,1) COMMENT '体重(kg)',
    height              INT COMMENT '身高(cm)',

    intake              DECIMAL(8,2) COMMENT '入量(ml)',
    output              DECIMAL(8,2) COMMENT '出量(ml)',
    urine               DECIMAL(8,2) COMMENT '尿量(ml)',
    stool               VARCHAR(20) COMMENT '大便情况',

    nursing_content     TEXT COMMENT '护理内容',
    nursing_measures    TEXT COMMENT '护理措施',

    nurse_id            VARCHAR(20) NOT NULL COMMENT '记录护士ID',
    nurse_name          VARCHAR(50) COMMENT '记录护士姓名',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_admission (admission_id),
    INDEX idx_patient (patient_id),
    INDEX idx_record_time (record_time)
);
```

#### 3.1.6 住院费用 InpatientFee
```sql
CREATE TABLE inpatient_fee (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '费用ID',
    admission_id        VARCHAR(36) NOT NULL COMMENT '住院ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    fee_date            DATE NOT NULL COMMENT '费用日期',
    fee_category        VARCHAR(20) NOT NULL COMMENT '费用分类(床位/药品/检查/检验/治疗/护理/材料)',
    fee_item_code       VARCHAR(20) NOT NULL COMMENT '项目编码',
    fee_item_name       VARCHAR(100) NOT NULL COMMENT '项目名称',
    fee_spec            VARCHAR(50) COMMENT '规格',
    fee_unit            VARCHAR(20) COMMENT '单位',
    fee_price           DECIMAL(10,4) COMMENT '单价',
    fee_quantity        DECIMAL(10,2) COMMENT '数量',
    fee_amount          DECIMAL(10,2) COMMENT '金额',

    order_id            VARCHAR(36) COMMENT '关联医嘱ID',
    order_no            VARCHAR(30) COMMENT '关联医嘱号',

    dept_id             VARCHAR(20) COMMENT '执行科室ID',
    dept_name           VARCHAR(100) COMMENT '执行科室名称',

    is_insurance        TINYINT DEFAULT 1 COMMENT '是否医保',
    pay_status          VARCHAR(20) DEFAULT '未结算' COMMENT '结算状态(未结算/已结算/已退费)',

    operator_id         VARCHAR(20) COMMENT '操作员ID',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_admission (admission_id),
    INDEX idx_patient (patient_id),
    INDEX idx_fee_date (fee_date),
    INDEX idx_fee_category (fee_category)
);
```

---

## 4. 业务流程

### 4.1 入院流程
```
┌─────────────────────────────────────────────────────────────────┐
│                          入院流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 入院申请  │───>│ 床位预约  │───>│ 入院登记  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                                        │                        │
│                                        ▼                        │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 护理评估  │<───│ 预交金缴纳 │<───│ 医保登记  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│        │                                                        │
│        ▼                                                        │
│   ┌──────────┐                                                  │
│   │ 入院宣教  │                                                  │
│   └──────────┘                                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 医嘱执行流程
```
┌─────────────────────────────────────────────────────────────────┐
│                        医嘱执行流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 医生开立  │───>│ 护士审核  │───>│ 执行医嘱  │                │
│   │ 医嘱      │    │ 医嘱      │    │          │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         ▼               ▼                       │
│                   ┌──────────┐    ┌──────────┐                │
│                   │ 驳回修改  │    │ 记录执行  │                │
│                   └──────────┘    │ 结果      │                │
│                                   └──────────┘                │
│                                                                 │
│   长期医嘱: 开立->审核->执行中->停止->已停止                       │
│   临时医嘱: 开立->审核->执行->完成                                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.3 出院流程
```
┌─────────────────────────────────────────────────────────────────┐
│                          出院流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 出院医嘱  │───>│ 出院小结  │───>│ 出院带药  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                                        │                        │
│                                        ▼                        │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 床位释放  │<───│ 出院结算  │<───│ 费用审核  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 5. 接口定义

### 5.1 入院管理接口

#### 5.1.1 入院登记
```
POST /api/inpatient/admission/register

Request:
{
    "patientId": "P12345678",
    "admissionType": "门诊",
    "deptId": "D001",
    "wardId": "W001",
    "bedNo": "101-1",
    "doctorId": "DOC001",
    "admissionDiagnosis": "肺炎",
    "admissionDiagnosisCode": "J18.900",
    "nursingLevel": "二级",
    "dietType": "普食",
    "insuranceType": "城镇职工医保",
    "insuranceNo": "1234567890",
    "deposit": 5000.00,
    "contactPerson": "李明",
    "contactPhone": "13800138000"
}

Response:
{
    "code": 0,
    "message": "入院登记成功",
    "data": {
        "admissionId": "IP202401150001",
        "admissionNo": "ZY202401150001",
        "bedNo": "101-1",
        "admissionTime": "2024-01-15 10:30:00"
    }
}
```

#### 5.1.2 获取床位列表
```
GET /api/inpatient/bed/list?wardId=W001&status=空床

Response:
{
    "code": 0,
    "data": {
        "total": 50,
        "vacant": 10,
        "occupied": 38,
        "maintenance": 2,
        "beds": [
            {
                "bedId": "BED001",
                "bedNo": "101-1",
                "roomNo": "101",
                "bedType": "普通",
                "dailyRate": 50.00,
                "status": "空床",
                "facilities": ["氧气", "呼叫器", "电视"]
            }
        ]
    }
}
```

#### 5.1.3 分配床位
```
POST /api/inpatient/bed/assign

Request:
{
    "admissionId": "IP202401150001",
    "wardId": "W001",
    "bedNo": "101-1"
}

Response:
{
    "code": 0,
    "message": "床位分配成功"
}
```

### 5.2 医嘱管理接口

#### 5.2.1 开立医嘱
```
POST /api/inpatient/order/create

Request:
{
    "admissionId": "IP202401150001",
    "patientId": "P12345678",
    "orderType": "长期",
    "orderCategory": "药品",
    "orderContent": "0.9%氯化钠注射液 250ml 静滴 每日一次",
    "orderDetail": {
        "drugId": "DRUG001",
        "drugName": "0.9%氯化钠注射液",
        "spec": "250ml",
        "dosage": "250ml",
        "route": "静滴",
        "frequency": "每日一次"
    },
    "startTime": "2024-01-15 10:00:00",
    "executeTime": "每日上午",
    "frequency": "QD"
}

Response:
{
    "code": 0,
    "message": "医嘱开立成功",
    "data": {
        "orderId": "ORD202401150001",
        "orderNo": "MO202401150001"
    }
}
```

#### 5.2.2 审核医嘱
```
POST /api/inpatient/order/audit

Request:
{
    "orderId": "ORD202401150001",
    "nurseId": "NUR001",
    "auditResult": "通过",
    "auditRemark": ""
}

Response:
{
    "code": 0,
    "message": "审核成功"
}
```

#### 5.2.3 执行医嘱
```
POST /api/inpatient/order/execute

Request:
{
    "orderId": "ORD202401150001",
    "admissionId": "IP202401150001",
    "executeTime": "2024-01-15 09:30:00",
    "executeNurseId": "NUR001",
    "executeResult": "已执行"
}

Response:
{
    "code": 0,
    "message": "执行记录成功"
}
```

#### 5.2.4 停止医嘱
```
POST /api/inpatient/order/stop

Request:
{
    "orderId": "ORD202401150001",
    "stopReason": "患者病情好转"
}

Response:
{
    "code": 0,
    "message": "医嘱已停止"
}
```

### 5.3 护理管理接口

#### 5.3.1 录入生命体征
```
POST /api/inpatient/nursing/vital-signs

Request:
{
    "admissionId": "IP202401150001",
    "patientId": "P12345678",
    "recordTime": "2024-01-15 08:00:00",
    "temperature": 36.5,
    "pulse": 76,
    "respiration": 18,
    "bloodPressureSystolic": 120,
    "bloodPressureDiastolic": 80,
    "spo2": 98
}

Response:
{
    "code": 0,
    "message": "录入成功"
}
```

#### 5.3.2 护理评估
```
POST /api/inpatient/nursing/assessment

Request:
{
    "admissionId": "IP202401150001",
    "patientId": "P12345678",
    "assessmentType": "跌倒评估",
    "assessmentResult": {
        "fallHistory": 0,
        "mentalStatus": 0,
        "vision": 0,
        "mobility": 1,
        "totalScore": 1,
        "riskLevel": "低风险"
    }
}

Response:
{
    "code": 0,
    "message": "评估记录成功"
}
```

### 5.4 出院管理接口

#### 5.4.1 出院申请
```
POST /api/inpatient/discharge/apply

Request:
{
    "admissionId": "IP202401150001",
    "dischargeType": "好转",
    "dischargeDiagnosis": "肺炎",
    "dischargeDiagnosisCode": "J18.900",
    "dischargeAdvice": "注意休息，按时服药，一周后复诊",
    "followUpDate": "2024-01-22"
}

Response:
{
    "code": 0,
    "message": "出院申请成功"
}
```

#### 5.4.2 出院结算
```
POST /api/inpatient/discharge/settle

Request:
{
    "admissionId": "IP202401150001",
    "payments": [
        {
            "payMethod": "MEDICAL_INSURANCE",
            "amount": 8000.00
        },
        {
            "payMethod": "WECHAT",
            "amount": 1500.00
        }
    ]
}

Response:
{
    "code": 0,
    "message": "结算成功",
    "data": {
        "settleId": "SET202401150001",
        "invoiceNo": "INV202401150001",
        "totalDays": 7,
        "totalCost": 9500.00,
        "depositUsed": 5000.00,
        "insurancePayment": 8000.00,
        "selfPayment": 1500.00,
        "refund": 3500.00
    }
}
```

---

## 6. 业务规则与约束

### 6.1 入院规则
| 规则编码 | 规则描述 |
|----------|----------|
| ADM001 | 入院前必须完成入院登记 |
| ADM002 | 分配床位前必须确认床位状态为空 |
| ADM003 | 预交金不足时提醒缴纳 |
| ADM004 | 同一患者不能重复入院 |

### 6.2 医嘱规则
| 规则编码 | 规则描述 |
|----------|----------|
| ORD001 | 医嘱必须经过护士审核后才能执行 |
| ORD002 | 长期医嘱需要明确开始和结束时间 |
| ORD003 | 停止医嘱需记录停止原因 |
| ORD004 | 成组医嘱必须同时停止 |
| ORD005 | 药品医嘱需进行配伍禁忌检查 |
| ORD006 | 医嘱修改必须作废原医嘱重新开立 |

### 6.3 床位规则
| 规则编码 | 规则描述 |
|----------|----------|
| BED001 | 床位状态变更需记录操作日志 |
| BED002 | 转科前必须确认目标科室有空床 |
| BED003 | 出院后床位自动释放 |
| BED004 | 维修中床位不可分配 |

### 6.4 出院规则
| 规则编码 | 规则描述 |
|----------|----------|
| DIS001 | 出院前必须完成出院小结 |
| DIS002 | 出院前必须完成费用结算 |
| DIS003 | 出院前必须完成出院带药 |
| DIS004 | 出院后自动释放床位 |

---

## 7. 模块交互关系

### 7.1 上游依赖
- **系统管理模块**: 用户认证、权限验证、科室信息
- **基础数据模块**: 数据字典、价表信息
- **门诊管理模块**: 门诊收入院

### 7.2 下游调用
- **药房管理模块**: 药品医嘱发药
- **检验管理模块**: 检验医嘱
- **影像管理模块**: 检查医嘱
- **电子病历模块**: 病历书写
- **财务收费模块**: 费用结算

---

## 8. 性能与安全要求

### 8.1 性能要求
| 指标 | 要求 |
|------|------|
| 医嘱保存时间 | < 2秒 |
| 查询响应时间 | < 1秒 |
| 并发用户数 | >= 500 |
| 系统可用性 | >= 99.9% |

### 8.2 安全要求
- 医嘱操作电子签名
- 关键操作审计日志
- 敏感数据加密存储
- 数据权限隔离

---

## 9. 附录

### 9.1 医嘱状态流转
```
新开 -> 审核 -> 执行中 -> 完成
  │      │
  │      └── 驳回
  │
  └── 作废

长期医嘱:
新开 -> 审核 -> 执行中 -> 停止
```

### 9.2 护理等级定义
| 等级 | 适用对象 | 护理要求 |
|------|----------|----------|
| 特级 | 危重患者、大手术后 | 24小时专人护理 |
| 一级 | 重症患者 | 每小时巡视 |
| 二级 | 病情稳定者 | 每2小时巡视 |
| 三级 | 康复期患者 | 每3小时巡视 |
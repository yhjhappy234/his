# 电子病历模块需求说明书

## 1. 模块概述与目标

### 1.1 模块定位
电子病历模块（EMR）是HIS系统的核心临床模块，负责患者病历信息的录入、存储、管理、质控和共享，是临床诊疗活动的信息载体。

### 1.2 业务目标
- 实现病历电子化、规范化管理
- 提高病历书写效率和质量
- 保障病历数据完整性和安全性
- 实现病历信息共享和互联互通
- 满足病历质控和评审要求

### 1.3 用户角色
- 门诊医生
- 住院医生
- 护士
- 病历质控员
- 病案管理员

---

## 2. 功能清单

### 2.1 病历模板管理

#### 2.1.1 模板分类
| 分类 | 模板类型 | 描述 |
|------|----------|------|
| 门诊 | 初诊病历模板 | 门诊首次就诊病历 |
| 门诊 | 复诊病历模板 | 门诊复诊病历模板 |
| 住院 | 入院记录模板 | 入院病历模板 |
| 住院 | 病程记录模板 | 住院病程记录模板 |
| 住院 | 出院记录模板 | 出院小结模板 |
| 手术 | 手术记录模板 | 手术过程记录模板 |
| 护理 | 护理记录模板 | 护理文书模板 |

#### 2.1.2 模板功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 模板创建 | 创建病历模板 | 高 |
| 模板编辑 | 编辑模板内容 | 高 |
| 模板分类 | 模板分类管理 | 高 |
| 模板共享 | 科室/个人模板共享 | 中 |
| 模板导入 | 导入标准模板 | 中 |
| 模板审核 | 模板审核流程 | 中 |

### 2.2 门诊病历管理

#### 2.2.1 门诊病历书写
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 初诊病历 | 首诊病历书写 | 高 |
| 复诊病历 | 复诊病历书写 | 高 |
| 病历模板调用 | 调用模板快速书写 | 高 |
| 主诉录入 | 患者主诉信息 | 高 |
| 现病史录入 | 现病史详细描述 | 高 |
| 既往史录入 | 既往病史、手术史等 | 高 |
| 过敏史录入 | 药物/食物过敏史 | 高 |
| 体格检查 | 体格检查结果 | 高 |
| 辅助检查 | 查看检验检查结果 | 高 |
| 诊断录入 | 初诊/确诊诊断 | 高 |
| 处理意见 | 治疗方案和医嘱 | 高 |

#### 2.2.2 门诊病历字段
```
门诊病历信息 OutpatientEMR:
- recordId: string, 病历ID
- visitId: string, 就诊ID
- patientId: string, 患者ID
- visitDate: date, 就诊日期
- deptId: string, 科室ID
- doctorId: string, 医生ID

- chiefComplaint: text, 主诉（必填，50-200字）
- presentIllness: text, 现病史（必填，200字以上）
- pastHistory: text, 既往史
- personalHistory: text, 个人史
- familyHistory: text, 家族史
- allergyHistory: text, 过敏史（必填）

- physicalExam: text, 体格检查（必填）
  - temperature: decimal, 体温
  - pulse: int, 脉搏
  - respiration: int, 呼吸
  - bloodPressure: string, 血压
  - generalExam: text, 一般检查
  - specialistExam: text, 专科检查

- auxiliaryExam: text, 辅助检查结果

- diagnosis: text, 诊断（必填，ICD-10编码）
  - primaryDiagnosis: string, 主要诊断
  - secondaryDiagnosis: string,次要诊断

- treatmentPlan: text, 治疗方案
- medicalAdvice: text, 医嘱/注意事项

- status: enum, 状态, [草稿/已提交/已审核]
- createTime: datetime
- updateTime: datetime
- submitTime: datetime
```

### 2.3 住院病历管理

#### 2.3.1 入院记录
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 入院记录书写 | 入院病历书写 | 高 |
| 一般情况录入 | 姓名、性别、年龄等基本信息 | 高 |
| 主诉录入 | 入院主诉 | 高 |
| 现病史录入 | 详细现病史 | 高 |
| 既往史录入 | 既往疾病史 | 高 |
| 个人史录入 | 生活习惯、职业等 | 高 |
| 婚育史录入 | 婚姻生育史 | 中 |
| 家族史录入 | 家族遗传病史 | 中 |
| 体格检查 | 入院体检记录 | 高 |
| 辅助检查 | 入院前检查结果 | 高 |
| 入院诊断 | 入院初步诊断 | 高 |
| 诊疗计划 | 诊疗计划描述 | 高 |

#### 2.3.2 病程记录
| 记录类型 | 描述 | 书写要求 |
|----------|------|----------|
| 首次病程记录 | 入院后首次病程 | 入院后8小时内 |
| 日常病程记录 | 每日病情变化 | 根据护理等级 |
| 上级医师查房记录 | 上级医师查房记录 | 每周至少一次 |
| 疑难病例讨论记录 | 疑难病例讨论 | 必须记录 |
| 会诊记录 | 科间会诊记录 | 会诊后24小时内 |
| 转科记录 | 转科交接记录 | 转科时记录 |
| 手术记录 | 手术过程记录 | 术后24小时内 |
| 交接班记录 | 医师交接班记录 | 交接班时 |

#### 2.3.3 病程记录时限要求
```yaml
病程记录时限:
  首次病程记录: 入院后8小时内
  日常病程记录:
    特级护理: 每日至少一次
    一级护理: 每日至少一次
    二级护理: 每2日至少一次
    三级护理: 每3日至少一次
  上级医师查房记录:
    主任医师: 每周至少一次
    副主任医师: 每周至少2次
  出院记录: 出院前24小时内
  死亡记录: 死亡后24小时内
  手术记录: 术后24小时内
```

#### 2.3.4 出院记录
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 出院小结书写 | 出院总结 | 高 |
| 入院情况回顾 | 入院时情况回顾 | 高 |
| 诊疗经过总结 | 治疗过程总结 | 高 |
| 出院诊断 | 最终诊断结论 | 高 |
| 出院医嘱 | 出院注意事项 | 高 |
| 出院带药 | 出院用药指导 | 高 |
| 随诊计划 | 随诊安排 | 高 |

### 2.4 手术病历管理

#### 2.4.1 手术相关记录
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 术前讨论记录 | 术前讨论内容 | 高 |
| 手术审批单 | 手术审批流程 | 高 |
| 手术知情同意书 | 手术同意书签署 | 高 |
| 手术记录书写 | 手术过程记录 | 高 |
| 术后首次病程 | 术后首次病程记录 | 高 |
| 麻醉记录 | 麻醉过程记录 | 高 |

#### 2.4.2 手术记录字段
```
手术记录 OperationRecord:
- operationId: string, 手术ID
- admissionId: string, 住院ID
- patientId: string, 患者ID
- operationDate: date, 手术日期
- startTime: datetime, 开始时间
- endTime: datetime, 结束时间
- duration: int, 持续时间(分钟)

- preoperativeDiagnosis: string, 术前诊断
- postoperativeDiagnosis: string, 术后诊断
- operationName: string, 手术名称
- operationCode: string, 手术编码(ICD-9-CM)

- surgeonId: string, 主刀医生ID
- surgeonName: string, 主刀医生姓名
- assistantIds: array, 助手医生列表
- anesthesiologistId: string, 麻醉医生ID
- anesthesiaMethod: string, 麻醉方式

- incision: string, 切口描述
- procedureDetail: text, 手术过程详述（必填）
- findings: text, 手术所见（必填）
- specimens: text, 标本处理
- complications: text, 并发症/不良反应

- bloodLoss: int, 术中出血量(ml)
- transfusion: text, 输血情况
- implantUse: text, 植入物使用

- status: enum, 状态
- createTime: datetime
```

### 2.5 知情同意书管理

#### 2.5.1 同意书类型
| 类型 | 描述 | 签署要求 |
|------|------|----------|
| 入院知情同意书 | 入院须知告知 | 入院时签署 |
| 手术知情同意书 | 手术风险告知 | 术前签署 |
| 麻醉知情同意书 | 麻醉风险告知 | 术前签署 |
| 输血知情同意书 | 输血风险告知 | 输血前签署 |
| 特殊治疗同意书 | 特殊治疗风险 | 治疗前签署 |
| 化疗知情同意书 | 化疗风险告知 | 化疗前签署 |

#### 2.5.2 同意书管理功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 同意书模板 | 同意书模板管理 | 高 |
| 同意书填写 | 填写同意书内容 | 高 |
| 电子签名 | 医生/患者电子签名 | 高 |
| 同意书打印 | 打印同意书 | 高 |
| 同意书归档 | 同意书归档管理 | 高 |

### 2.6 病历质控管理

#### 2.6.1 质控规则
| 规则类型 | 检查内容 | 质控级别 |
|----------|----------|----------|
| 时限质控 | 病历书写时限检查 | 甲级/乙级/丙级 |
| 内容质控 | 病历内容完整性检查 | 甲级/乙级/丙级 |
| 逻辑质控 | 病历逻辑合理性检查 | 甲级/乙级/丙级 |
| 格式质控 | 病历格式规范性检查 | 甲级/乙级/丙级 |

#### 2.6.2 质控评分
```yaml
病历等级评定:
  甲级病历: 评分>=90分
    - 时限合格
    - 内容完整
    - 格式规范
    - 逻辑正确

  乙级病历: 评分>=75分
    - 时限基本合格
    - 内容基本完整
    - 格式基本规范

  丙级病历: 评分<75分
    - 存在严重缺陷
    - 需要整改

  缺陷项目:
    - 缺项: 扣10分
    - 时限超时: 扣5-10分
    - 内容不完整: 扣5分
    - 格式不规范: 扣3分
    - 逻辑错误: 扣10分
```

#### 2.6.3 质控流程
```
┌─────────────────────────────────────────────────────────────────┐
│                        病历质控流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 病历提交  │───>│ 系统质控  │───>│ 质控评分  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │               │                       │
│                         ▼               ▼                       │
│                   ┌──────────┐    ┌──────────┐                │
│                   │ 缺陷标记  │    │ 人工复核  │                │
│                   └──────────┘    └──────────┘                │
│                                         │                       │
│                                         ▼                       │
│                                   ┌──────────┐                 │
│                                   │ 整改通知  │                 │
│                                   └──────────┘                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.7 病历归档管理

#### 2.7.1 归档功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 病历审核归档 | 彄案归档审核 | 高 |
| 归档编号 | 病案编号管理 | 高 |
| 影像扫描 | 纸质病历扫描 | 中 |
| 电子归档 | 电子病历归档 | 高 |
| 病案借阅 | 病案借阅管理 | 中 |
| 病案统计 | 病案统计报表 | 中 |

---

## 3. 数据实体定义

### 3.1 核心实体

#### 3.1.1 门诊病历 OutpatientEMR
```sql
CREATE TABLE outpatient_emr (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '病历ID',
    visit_id            VARCHAR(36) NOT NULL COMMENT '就诊ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',
    visit_date          DATE NOT NULL COMMENT '就诊日期',
    visit_no            VARCHAR(30) COMMENT '就诊序号',

    dept_id             VARCHAR(20) NOT NULL COMMENT '科室ID',
    dept_name           VARCHAR(100) COMMENT '科室名称',
    doctor_id           VARCHAR(20) NOT NULL COMMENT '医生ID',
    doctor_name         VARCHAR(50) COMMENT '医生姓名',

    chief_complaint     TEXT NOT NULL COMMENT '主诉',
    present_illness     TEXT NOT NULL COMMENT '现病史',
    past_history        TEXT COMMENT '既往史',
    personal_history    TEXT COMMENT '个人史',
    family_history      TEXT COMMENT '家族史',
    allergy_history     TEXT COMMENT '过敏史',

    temperature         DECIMAL(4,1) COMMENT '体温',
    pulse               INT COMMENT '脉搏',
    respiration         INT COMMENT '呼吸',
    blood_pressure      VARCHAR(20) COMMENT '血压',
    weight              DECIMAL(5,1) COMMENT '体重',
    height              INT COMMENT '身高',

    general_exam        TEXT COMMENT '一般检查',
    specialist_exam     TEXT COMMENT '专科检查',
    auxiliary_exam      TEXT COMMENT '辅助检查',

    primary_diagnosis_code VARCHAR(50) COMMENT '主要诊断编码',
    primary_diagnosis_name VARCHAR(200) COMMENT '主要诊断名称',
    secondary_diagnosis TEXT COMMENT '次要诊断',

    treatment_plan      TEXT COMMENT '治疗方案',
    medical_advice      TEXT COMMENT '医嘱/注意事项',

    template_id         VARCHAR(36) COMMENT '模板ID',

    status              VARCHAR(20) NOT NULL DEFAULT '草稿' COMMENT '状态(草稿/已提交/已审核)',
    qc_score            INT COMMENT '质控评分',
    qc_level            VARCHAR(20) COMMENT '质控等级(甲级/乙级/丙级)',
    qc_defects          TEXT COMMENT '缺陷列表(JSON)',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,
    submit_time         DATETIME COMMENT '提交时间',
    audit_time          DATETIME COMMENT '审核时间',

    INDEX idx_visit (visit_id),
    INDEX idx_patient (patient_id),
    INDEX idx_visit_date (visit_date),
    INDEX idx_status (status)
);
```

#### 3.1.2 入院记录 AdmissionRecord
```sql
CREATE TABLE admission_record (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '记录ID',
    admission_id        VARCHAR(36) NOT NULL COMMENT '住院ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',
    patient_name        VARCHAR(50) COMMENT '患者姓名',
    gender              CHAR(1) COMMENT '性别',
    age                 INT COMMENT '年龄',
    admission_date      DATE NOT NULL COMMENT '入院日期',

    dept_id             VARCHAR(20) NOT NULL COMMENT '科室ID',
    ward_id             VARCHAR(20) COMMENT '病区ID',
    bed_no              VARCHAR(20) COMMENT '床位号',

    chief_complaint     TEXT NOT NULL COMMENT '主诉',
    present_illness     TEXT NOT NULL COMMENT '现病史',
    past_history        TEXT COMMENT '既往史',
    personal_history    TEXT COMMENT '个人史',
    marriage_history    TEXT COMMENT '婚育史',
    family_history      TEXT COMMENT '家族史',
    allergy_history     TEXT COMMENT '过敏史',

    temperature         DECIMAL(4,1) COMMENT '体温',
    pulse               INT COMMENT '脉搏',
    respiration         INT COMMENT '呼吸',
    blood_pressure      VARCHAR(20) COMMENT '血压',
    weight              DECIMAL(5,1) COMMENT '体重',
    height              INT COMMENT '身高',

    general_exam        TEXT COMMENT '一般检查',
    specialist_exam     TEXT COMMENT '专科检查',
    auxiliary_exam      TEXT COMMENT '辅助检查',

    admission_diagnosis_code VARCHAR(50) COMMENT '入院诊断编码',
    admission_diagnosis_name VARCHAR(200) COMMENT '入院诊断名称',

    treatment_plan      TEXT COMMENT '诊疗计划',

    doctor_id           VARCHAR(20) NOT NULL COMMENT '书写医生ID',
    doctor_name         VARCHAR(50) COMMENT '书写医生姓名',
    record_time         DATETIME NOT NULL COMMENT '书写时间',

    status              VARCHAR(20) NOT NULL DEFAULT '草稿' COMMENT '状态',
    qc_score            INT COMMENT '质控评分',
    qc_level            VARCHAR(20) COMMENT '质控等级',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_admission (admission_id),
    INDEX idx_patient (patient_id)
);
```

#### 3.1.3 病程记录 ProgressRecord
```sql
CREATE TABLE progress_record (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '记录ID',
    admission_id        VARCHAR(36) NOT NULL COMMENT '住院ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    record_type         VARCHAR(20) NOT NULL COMMENT '记录类型',
    record_title        VARCHAR(100) COMMENT '记录标题',
    record_date         DATE NOT NULL COMMENT '记录日期',
    record_time         DATETIME NOT NULL COMMENT '记录时间',

    record_content      TEXT NOT NULL COMMENT '记录内容',

    doctor_id           VARCHAR(20) NOT NULL COMMENT '书写医生ID',
    doctor_name         VARCHAR(50) COMMENT '书写医生姓名',
    doctor_title        VARCHAR(20) COMMENT '医生职称',

    reviewer_id         VARCHAR(20) COMMENT '审核医生ID',
    reviewer_name       VARCHAR(50) COMMENT '审核医生姓名',
    review_time         DATETIME COMMENT '审核时间',

    status              VARCHAR(20) NOT NULL DEFAULT '草稿' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_admission (admission_id),
    INDEX idx_patient (patient_id),
    INDEX idx_type_date (record_type, record_date)
);
```

#### 3.1.4 出院记录 DischargeRecord
```sql
CREATE TABLE discharge_record (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '记录ID',
    admission_id        VARCHAR(36) NOT NULL COMMENT '住院ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    admission_date      DATE COMMENT '入院日期',
    discharge_date      DATE COMMENT '出院日期',
    hospital_days       INT COMMENT '住院天数',

    admission_situation TEXT COMMENT '入院时情况',
    treatment_process   TEXT COMMENT '诊疗经过',
    discharge_diagnosis_code VARCHAR(50) COMMENT '出院诊断编码',
    discharge_diagnosis_name VARCHAR(200) COMMENT '出院诊断名称',
    discharge_condition TEXT COMMENT '出院时情况',

    discharge_advice    TEXT COMMENT '出院医嘱',
    discharge_medication TEXT COMMENT '出院带药',
    follow_up_date      DATE COMMENT '复诊日期',
    follow_up_dept      VARCHAR(100) COMMENT '复诊科室',

    doctor_id           VARCHAR(20) NOT NULL COMMENT '医生ID',
    doctor_name         VARCHAR(50) COMMENT '医生姓名',

    status              VARCHAR(20) NOT NULL DEFAULT '草稿' COMMENT '状态',
    qc_score            INT COMMENT '质控评分',
    qc_level            VARCHAR(20) COMMENT '质控等级',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_admission (admission_id),
    INDEX idx_patient (patient_id)
);
```

#### 3.1.5 手术记录 OperationRecord
```sql
CREATE TABLE operation_record (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '记录ID',
    admission_id        VARCHAR(36) NOT NULL COMMENT '住院ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    operation_date      DATE NOT NULL COMMENT '手术日期',
    start_time          DATETIME COMMENT '开始时间',
    end_time            DATETIME COMMENT '结束时间',
    operation_duration  INT COMMENT '手术时长(分钟)',

    pre_op_diagnosis    VARCHAR(200) COMMENT '术前诊断',
    post_op_diagnosis   VARCHAR(200) COMMENT '术后诊断',
    operation_name      VARCHAR(100) NOT NULL COMMENT '手术名称',
    operation_code      VARCHAR(50) COMMENT '手术编码',

    surgeon_id          VARCHAR(20) NOT NULL COMMENT '主刀医生ID',
    surgeon_name        VARCHAR(50) COMMENT '主刀医生姓名',
    assistants          TEXT COMMENT '助手列表(JSON)',
    anesthesiologist_id VARCHAR(20) COMMENT '麻醉医生ID',
    anesthesiologist_name VARCHAR(50) COMMENT '麻醉医生姓名',
    anesthesia_method   VARCHAR(50) COMMENT '麻醉方式',

    incision            TEXT COMMENT '切口描述',
    procedure_detail    TEXT NOT NULL COMMENT '手术过程',
    operation_findings  TEXT COMMENT '手术所见',
    specimens           TEXT COMMENT '标本处理',
    complications       TEXT COMMENT '并发症',

    blood_loss          INT COMMENT '出血量',
    transfusion         TEXT COMMENT '输血情况',
    implants            TEXT COMMENT '植入物(JSON)'

    status              VARCHAR(20) NOT NULL DEFAULT '草稿' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_admission (admission_id),
    INDEX idx_patient (patient_id),
    INDEX idx_operation_date (operation_date)
);
```

#### 3.1.6 知情同意书 InformedConsent
```sql
CREATE TABLE informed_consent (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '同意书ID',
    admission_id        VARCHAR(36) COMMENT '住院ID',
    visit_id            VARCHAR(36) COMMENT '就诊ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    consent_type        VARCHAR(20) NOT NULL COMMENT '同意书类型',
    consent_name        VARCHAR(100) COMMENT '同意书名称',

    consent_content     TEXT COMMENT '同意书内容',
    risk_description    TEXT COMMENT '风险说明',

    doctor_id           VARCHAR(20) NOT NULL COMMENT '告知医生ID',
    doctor_name         VARCHAR(50) COMMENT '告知医生姓名',
    doctor_signature    VARCHAR(200) COMMENT '医生签名(图片URL)',
    sign_time           DATETIME COMMENT '医生签署时间',

    patient_signature   VARCHAR(200) COMMENT '患者签名(图片URL)',
    patient_sign_time   DATETIME COMMENT '患者签署时间',
    agent_name          VARCHAR(50) COMMENT '代理人姓名',
    agent_signature     VARCHAR(200) COMMENT '代理人签名',

    status              VARCHAR(20) NOT NULL DEFAULT '待签署' COMMENT '状态(待签署/已签署/已拒绝)',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_admission (admission_id),
    INDEX idx_patient (patient_id),
    INDEX idx_type (consent_type)
);
```

---

## 4. 业务流程

### 4.1 病历书写流程
```
┌─────────────────────────────────────────────────────────────────┐
│                        病历书写流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 选择模板  │───>│ 录入内容  │───>│ 保存草稿  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │               ▼                       │
│                         │         ┌──────────┐                │
│                         │         │ 继续编辑  │                │
│                         │         └──────────┘                │
│                         │               │                       │
│                         ▼               ▼                       │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 质控反馈  │<───│ 质控检查  │<───│ 提交病历  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │ 合格          │                       │
│                         ▼               ▼                       │
│                   ┌──────────┐    ┌──────────┐                │
│                   │ 整改修改  │    │ 病历归档  │                │
│                   └──────────┘    └──────────┘                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 病历质控流程
```
1. 医生提交病历
2. 系统自动质控
   - 时限检查
   - 内容完整性检查
   - 格式规范性检查
3. 计算质控评分
4. 标记缺陷项目
5. 通知医生整改（如有缺陷）
6. 质控员人工复核
7. 确认病历等级
8. 归档入库
```

---

## 5. 接口定义

### 5.1 病历书写接口

#### 5.1.1 获取病历模板
```
GET /api/emr/template/list?type=入院记录&deptId=D001

Response:
{
    "code": 0,
    "data": [
        {
            "templateId": "TPL001",
            "templateName": "内科入院记录模板",
            "templateType": "入院记录",
            "templateContent": "模板内容...",
            "isPublic": true,
            "creatorName": "张主任"
        }
    ]
}
```

#### 5.1.2 保存门诊病历
```
POST /api/emr/outpatient/save

Request:
{
    "visitId": "VIS001",
    "patientId": "P001",
    "deptId": "D001",
    "doctorId": "DOC001",
    "chiefComplaint": "头痛、发热3天",
    "presentIllness": "患者3天前无明显诱因出现头痛...",
    "pastHistory": "高血压病史5年",
    "allergyHistory": "青霉素过敏",
    "temperature": 38.5,
    "pulse": 88,
    "respiration": 20,
    "bloodPressure": "130/85",
    "generalExam": "神志清楚，精神可...",
    "specialistExam": "咽部充血，扁桃体II度肿大...",
    "primaryDiagnosisCode": "J06.900",
    "primaryDiagnosisName": "急性上呼吸道感染",
    "treatmentPlan": "1. 休息，多饮水\n2. 对症治疗",
    "medicalAdvice": "注意休息，多饮水，不适随诊"
}

Response:
{
    "code": 0,
    "message": "保存成功",
    "data": {
        "recordId": "EMR202401150001"
    }
}
```

#### 5.1.3 提交病历
```
POST /api/emr/submit

Request:
{
    "recordId": "EMR202401150001",
    "recordType": "门诊病历"
}

Response:
{
    "code": 0,
    "message": "提交成功",
    "data": {
        "qcScore": 95,
        "qcLevel": "甲级",
        "defects": []
    }
}
```

### 5.2 病历质控接口

#### 5.2.1 获取质控结果
```
GET /api/emr/qc/result?recordId=EMR202401150001

Response:
{
    "code": 0,
    "data": {
        "recordId": "EMR202401150001",
        "qcScore": 85,
        "qcLevel": "乙级",
        "qcTime": "2024-01-15 11:00:00",
        "defects": [
            {
                "defectCode": "QC001",
                "defectName": "现病史字数不足",
                "defectType": "内容",
                "score": -5,
                "description": "现病史应不少于200字，当前仅150字",
                "isRequired": true
            }
        ]
    }
}
```

---

## 6. 业务规则与约束

### 6.1 时限规则
| 规则编码 | 规则描述 | 时限要求 |
|----------|----------|----------|
| EMR001 | 入院记录完成时限 | 入院后24小时内 |
| EMR002 | 首次病程记录时限 | 入院后8小时内 |
| EMR003 | 日常病程记录时限 | 按护理等级 |
| EMR004 | 出院记录时限 | 出院后24小时内 |
| EMR005 | 手术记录时限 | 术后24小时内 |
| EMR006 | 会诊记录时限 | 会诊后24小时内 |
| EMR007 | 死亡记录时限 | 死亡后24小时内 |

### 6.2 内容规则
| 规则编码 | 规则描述 | 要求 |
|----------|----------|------|
| EMR101 | 主诉必填 | 50-200字 |
| EMR102 | 现病史必填 | >=200字 |
| EMR103 | 过敏史必填 | 如无须填写"未发现" |
| EMR104 | 体格检查必填 | 包含一般检查+专科检查 |
| EMR105 | 诊断必填 | 必须有主要诊断 |

### 6.3 签名规则
- 所有病历必须医生电子签名
- 病程记录上级医师需审核签名
- 手术记录需主刀医生签名
- 知情同意书需医患双方签名

---

## 7. 模块交互关系

### 7.1 上游依赖
- **门诊管理模块**: 就诊信息
- **住院管理模块**: 住院信息
- **系统管理模块**: 用户认证、科室信息

### 7.2 下游调用
- **检验管理模块**: 查看检验结果
- **影像管理模块**: 查看影像报告
- **药房管理模块**: 查看用药信息

---

## 8. 性能与安全要求

### 8.1 性能要求
| 指标 | 要求 |
|------|------|
| 病历保存时间 | < 2秒 |
| 模板加载时间 | < 1秒 |
| 质控检查时间 | < 3秒 |

### 8.2 安全要求
- 病历数据加密存储
- 病历修改审计日志
- 病历访问权限控制
- 病历数据备份机制
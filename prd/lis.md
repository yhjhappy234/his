# 检验管理模块需求说明书

## 1. 模块概述与目标

### 1.1 模块定位
检验管理模块（LIS）是HIS系统的医技模块，负责检验申请、样本管理、检验执行、结果报告等全流程管理，为临床诊断提供检验数据支持。

### 1.2 业务目标
- 实现检验流程信息化、自动化
- 提高检验工作效率和准确性
- 规范检验报告管理
- 实现危急值及时预警
- 保障检验质量控制

### 1.3 用户角色
- 临床医生
- 检验技师
- 检验主任
- 护士（采样）
- 报告接收者

---

## 2. 功能清单

### 2.1 检验项目管理

#### 2.1.1 检验项目设置
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 检验项目维护 | 检验项目基本信息 | 高 |
| 项目分类管理 | 检验项目分类（生化/免疫/血常规等） | 高 |
| 项目组合管理 | 检验项目组合包设置 | 高 |
| 参考值设置 | 检验项目参考值范围 | 高 |
| 危急值设置 | 检验项目危急值界限 | 高 |
| 价格设置 | 检验项目价格管理 | 高 |

#### 2.1.2 检验项目信息
```
检验项目 TestItem:
- itemId: string, 项目ID
- itemCode: string, 项目编码
- itemName: string, 项目名称
- itemNameEn: string, 英文名称
- category: enum, 分类, [生化/免疫/血常规/尿常规/凝血/微生物等]
- specimenType: string, 标本类型, [血液/尿液/粪便/痰液等]
- testMethod: string, 检测方法
- unit: string, 结果单位
- referenceMin: decimal, 参考值下限
- referenceMax: decimal, 参考值上限
- criticalLow: decimal, 危急值下限
- criticalHigh: decimal, 危急值上限
- price: decimal, 价格
- turnaroundTime: int, 报告时限(小时)
- instrumentId: string, 检测仪器
- status: enum, 状态, [正常/停用]
```

### 2.2 检验申请管理

#### 2.2.1 申请功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 申请开立 | 医生开立检验申请 | 高 |
| 申请模板 | 常用检验申请模板 | 高 |
| 项目组合 | 检验项目组合选择 | 高 |
| 急诊标识 | 急诊检验申请 | 高 |
| 临床诊断 | 申请关联诊断 | 高 |
| 申请查询 | 检验申请状态查询 | 高 |

#### 2.2.2 申请信息
```
检验申请 TestRequest:
- requestId: string, 申请ID
- requestNo: string, 申请单号
- patientId: string, 患者ID
- patientName: string, 患者姓名
- gender: string, 性别
- age: int, 年龄
- visitType: enum, 类型, [门诊/住院]
- visitId: string, 就诊/住院ID
- deptId: string, 科室ID
- doctorId: string, 申请医生ID
- requestTime: datetime, 申请时间
- clinicalDiagnosis: string, 临床诊断
- testItems: array, 检验项目列表
- isEmergency: boolean, 是否急诊
- status: enum, 状态, [申请/采样/检测/审核/发布]
```

### 2.3 样本管理

#### 2.3.1 样本采集
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 采集登记 | 样本采集登记 | 高 |
| 采集打印 | 打印样本标签 | 高 |
| 采集核对 | 样本信息核对 | 高 |
| 采集时间 | 记录采集时间 | 高 |
| 采集人员 | 记录采集人员 | 高 |

#### 2.3.2 样本核收
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 样本核收 | 检验科接收样本 | 高 |
| 样本拒收 | 不合格样本拒收 | 高 |
| 拒收原因 | 记录拒收原因 | 高 |
| 样本分发 | 样本分发到各检验组 | 高 |

#### 2.3.3 样本信息
```
样本信息 Sample:
- sampleId: string, 样本ID
- sampleNo: string, 样本编号
- requestId: string, 申请ID
- patientId: string, 患者ID
- specimenType: string, 标本类型
- collectionTime: datetime, 采集时间
- collectorId: string, 采集人ID
- collectorName: string, 采集人姓名
- receiveTime: datetime, 接收时间
- receiverId: string, 接收人ID
- receiverName: string, 接收人姓名
- sampleStatus: enum, 状态, [待采集/已采集/已核收/已拒收]
- rejectReason: string, 拒收原因
- storageLocation: string, 存放位置
```

### 2.4 检验执行管理

#### 2.4.1 检验操作
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 工作列表 | 待检样本工作列表 | 高 |
| 样本排序 | 检验样本排序 | 中 |
| 检验登记 | 开始检验登记 | 高 |
| 结果录入 | 手工录入检验结果 | 高 |
| 仪器接口 | 自动接收仪器结果 | 高 |
| 结果修改 | 检验结果修改 | 高 |

#### 2.4.2 仪器接口
```
仪器接口类型:
- RS232串口通信
- TCP/IP网络通信
- ASTM协议
- HL7协议
- DICOM协议

仪器数据接收:
- 实时接收
- 批量接收
- 双向通信（支持样本编号查询）
```

### 2.5 检验结果管理

#### 2.5.1 结果处理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 结果审核 | 检验结果审核 | 高 |
| 结果标记 | 异常结果标记 | 高 |
| 历史对比 | 与历史结果对比 | 中 |
| 结果修改 | 结果修正处理 | 高 |
| 结果查询 | 检验结果查询 | 高 |

#### 2.5.2 结果信息
```
检验结果 TestResult:
- resultId: string, 结果ID
- sampleId: string, 样本ID
- requestId: string, 申请ID
- itemId: string, 项目ID
- itemName: string, 项目名称
- testValue: string, 检测值
- unit: string, 单位
- resultFlag: enum, 结果标识, [正常/偏高/偏低/危急]
- referenceRange: string, 参考范围
- testTime: datetime, 检测时间
- testerId: string, 检测人ID
- testerName: string, 检测人姓名
- auditTime: datetime, 审核时间
- auditorId: string, 审核人ID
- auditorName: string, 审核人姓名
- instrumentId: string, 仪器ID
- reagentLot: string, 试剂批号
```

### 2.6 检验报告管理

#### 2.6.1 报告功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 报告生成 | 生成检验报告 | 高 |
| 报告审核 | 检验报告审核 | 高 |
| 报告发布 | 报告发布推送 | 高 |
| 报告打印 | 打印检验报告 | 高 |
| 报告查询 | 报告历史查询 | 高 |
| 报告修改 | 报告修正流程 | 高 |

#### 2.6.2 报告状态
```
报告状态流转:
草稿 -> 待审核 -> 已审核 -> 已发布
              │
              └── 需复核 -> 已复核 -> 已审核
```

### 2.7 危急值管理

#### 2.7.1 危急值处理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 危急值识别 | 自动识别危急值 | 高 |
| 危急值预警 | 危急值实时预警 | 高 |
| 危急值通知 | 通知临床科室 | 高 |
| 危急值确认 | 临床确认接收 | 高 |
| 危急值记录 | 危急值处理记录 | 高 |
| 危急值追踪 | 危急值处理追踪 | 高 |

#### 2.7.2 危急值流程
```
┌─────────────────────────────────────────────────────────────────┐
│                       危急值处理流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 结果检测  │───>│ 危急值识别 │───>│ 系统预警  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │               ▼                       │
│                         │         ┌──────────┐                │
│                         │         │ 双重审核  │                │
│                         │         └──────────┘                │
│                         │               │                       │
│                         ▼               ▼                       │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 临床处理  │<───│ 接收确认  │<───│ 电话通知  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │                                       │
│                         ▼                                       │
│                   ┌──────────┐                                 │
│                   │ 记录归档  │                                 │
│                   └──────────┘                                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.8 质控管理

#### 2.8.1 室内质控
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 质控品管理 | 质控品信息管理 | 高 |
| 质控计划 | 质控检测计划 | 高 |
| 质控检测 | 日常质控检测 | 高 |
| 质控图绘制 | Levey-Jennings质控图 | 高 |
| 质控分析 | 质控数据分析 | 高 |
| 质控失控 | 失控处理流程 | 高 |

#### 2.8.2 质控规则
```
质控规则 Westgard规则:
- 1-2s规则: 1个质控值超过±2s（警告）
- 1-3s规则: 1个质控值超过±3s（失控）
- 2-2s规则: 连续2个质控值超过±2s同侧（失控）
- R-4s规则: 连续2个质控值超过±2s异侧（失控）
- 4-1s规则: 连续4个质控值超过±1s同侧（失控）
- 10x规则: 连续10个质控值在同一侧（失控）
```

### 2.9 报表统计

#### 2.9.1 统计报表
| 报表名称 | 描述 | 频率 |
|----------|------|------|
| 检验工作量统计 | 各项目检测数量统计 | 日/月 |
| 急诊检验统计 | 急诊检验响应时间统计 | 日 |
| 危急值统计 | 危急值发生和处理统计 | 月 |
| 质控统计 | 质控合格率统计 | 月 |
| 仪器使用统计 | 仪器运行状态统计 | 日 |

---

## 3. 数据实体定义

### 3.1 核心实体

#### 3.1.1 检验项目 TestItem
```sql
CREATE TABLE test_item (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '项目ID',
    item_code           VARCHAR(20) NOT NULL UNIQUE COMMENT '项目编码',
    item_name           VARCHAR(100) NOT NULL COMMENT '项目名称',
    item_name_en        VARCHAR(100) COMMENT '英文名称',
    pinyin_code         VARCHAR(50) COMMENT '拼音码',

    category            VARCHAR(20) NOT NULL COMMENT '分类',
    specimen_type       VARCHAR(20) NOT NULL COMMENT '标本类型',
    test_method         VARCHAR(50) COMMENT '检测方法',
    unit                VARCHAR(20) COMMENT '结果单位',

    reference_min       DECIMAL(10,4) COMMENT '参考值下限',
    reference_max       DECIMAL(10,4) COMMENT '参考值上限',
    reference_text      VARCHAR(100) COMMENT '参考值文本描述',

    critical_low        DECIMAL(10,4) COMMENT '危急值下限',
    critical_high       DECIMAL(10,4) COMMENT '危急值上限',
    is_critical         TINYINT DEFAULT 0 COMMENT '是否有危急值',

    price               DECIMAL(10,2) COMMENT '价格',
    turnaround_time     INT COMMENT '报告时限(小时)',

    instrument_id       VARCHAR(20) COMMENT '默认仪器ID',
    reagent_id          VARCHAR(20) COMMENT '默认试剂ID',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_category (category),
    INDEX idx_code (item_code)
);
```

#### 3.1.2 检验申请 TestRequest
```sql
CREATE TABLE test_request (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '申请ID',
    request_no          VARCHAR(30) NOT NULL UNIQUE COMMENT '申请单号',

    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',
    patient_name        VARCHAR(50) COMMENT '患者姓名',
    gender              CHAR(1) COMMENT '性别',
    age                 INT COMMENT '年龄',
    id_card_no          VARCHAR(18) COMMENT '身份证号',

    visit_type          VARCHAR(20) NOT NULL COMMENT '就诊类型',
    visit_id            VARCHAR(36) COMMENT '就诊ID',
    admission_id        VARCHAR(36) COMMENT '住院ID',

    dept_id             VARCHAR(20) NOT NULL COMMENT '申请科室ID',
    dept_name           VARCHAR(100) COMMENT '科室名称',
    doctor_id           VARCHAR(20) NOT NULL COMMENT '申请医生ID',
    doctor_name         VARCHAR(50) COMMENT '医生姓名',

    clinical_diagnosis  VARCHAR(200) COMMENT '临床诊断',
    clinical_info       TEXT COMMENT '临床信息',

    request_time        DATETIME NOT NULL COMMENT '申请时间',
    is_emergency        TINYINT DEFAULT 0 COMMENT '是否急诊',
    emergency_level     VARCHAR(20) COMMENT '急诊级别',

    sample_status       VARCHAR(20) COMMENT '样本状态',
    report_status       VARCHAR(20) COMMENT '报告状态',

    total_amount        DECIMAL(10,2) COMMENT '总金额',
    pay_status          VARCHAR(20) COMMENT '收费状态',

    status              VARCHAR(20) NOT NULL DEFAULT '申请' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_patient (patient_id),
    INDEX idx_visit (visit_id),
    INDEX idx_request_time (request_time),
    INDEX idx_status (status)
);
```

#### 3.1.3 检验申请明细 TestRequestItem
```sql
CREATE TABLE test_request_item (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '明细ID',
    request_id          VARCHAR(36) NOT NULL COMMENT '申请ID',

    item_id             VARCHAR(36) NOT NULL COMMENT '项目ID',
    item_code           VARCHAR(20) COMMENT '项目编码',
    item_name           VARCHAR(100) COMMENT '项目名称',
    specimen_type       VARCHAR(20) COMMENT '标本类型',

    price               DECIMAL(10,2) COMMENT '价格',

    sample_id           VARCHAR(36) COMMENT '样本ID',
    result_status       VARCHAR(20) COMMENT '结果状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_request (request_id),
    INDEX idx_item (item_id)
);
```

#### 3.1.4 样本信息 Sample
```sql
CREATE TABLE sample (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '样本ID',
    sample_no           VARCHAR(30) NOT NULL UNIQUE COMMENT '样本编号',
    request_id          VARCHAR(36) NOT NULL COMMENT '申请ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    specimen_type       VARCHAR(20) NOT NULL COMMENT '标本类型',
    specimen_container  VARCHAR(20) COMMENT '容器类型',

    collection_time     DATETIME COMMENT '采集时间',
    collector_id        VARCHAR(20) COMMENT '采集人ID',
    collector_name      VARCHAR(50) COMMENT '采集人姓名',
    collection_location VARCHAR(50) COMMENT '采集地点',

    receive_time        DATETIME COMMENT '接收时间',
    receiver_id         VARCHAR(20) COMMENT '接收人ID',
    receiver_name       VARCHAR(50) COMMENT '接收人姓名',

    sample_status       VARCHAR(20) NOT NULL DEFAULT '待采集' COMMENT '状态',
    reject_reason       VARCHAR(200) COMMENT '拒收原因',
    reject_time         DATETIME COMMENT '拒收时间',

    storage_location    VARCHAR(50) COMMENT '存放位置',
    test_group          VARCHAR(20) COMMENT '检验组',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_request (request_id),
    INDEX idx_patient (patient_id),
    INDEX idx_sample_no (sample_no),
    INDEX idx_status (sample_status)
);
```

#### 3.1.5 检验结果 TestResult
```sql
CREATE TABLE test_result (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '结果ID',
    request_id          VARCHAR(36) NOT NULL COMMENT '申请ID',
    sample_id           VARCHAR(36) NOT NULL COMMENT '样本ID',
    request_item_id     VARCHAR(36) COMMENT '申请明细ID',

    item_id             VARCHAR(36) NOT NULL COMMENT '项目ID',
    item_code           VARCHAR(20) COMMENT '项目编码',
    item_name           VARCHAR(100) COMMENT '项目名称',
    unit                VARCHAR(20) COMMENT '单位',

    test_value          VARCHAR(100) NOT NULL COMMENT '检测值',
    text_result         TEXT COMMENT '文本结果',

    result_flag         VARCHAR(20) COMMENT '结果标识',
    abnormal_flag       TINYINT COMMENT '是否异常',
    critical_flag       TINYINT COMMENT '是否危急值',

    reference_min       DECIMAL(10,4) COMMENT '参考值下限',
    reference_max       DECIMAL(10,4) COMMENT '参考值上限',
    reference_range     VARCHAR(100) COMMENT '参考范围描述',

    instrument_id       VARCHAR(20) COMMENT '仪器ID',
    instrument_name     VARCHAR(100) COMMENT '仪器名称',
    reagent_lot         VARCHAR(50) COMMENT '试剂批号',

    test_time           DATETIME COMMENT '检测时间',
    tester_id           VARCHAR(20) COMMENT '检测人ID',
    tester_name         VARCHAR(50) COMMENT '检测人姓名',

    audit_time          DATETIME COMMENT '审核时间',
    auditor_id          VARCHAR(20) COMMENT '审核人ID',
    auditor_name        VARCHAR(50) COMMENT '审核人姓名',

    modify_reason       VARCHAR(200) COMMENT '修改原因',
    modify_time         DATETIME COMMENT '修改时间',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_request (request_id),
    INDEX idx_sample (sample_id),
    INDEX idx_item (item_id),
    INDEX idx_critical (critical_flag)
);
```

#### 3.1.6 检验报告 TestReport
```sql
CREATE TABLE test_report (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '报告ID',
    report_no           VARCHAR(30) NOT NULL UNIQUE COMMENT '报告编号',
    request_id          VARCHAR(36) NOT NULL COMMENT '申请ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',
    sample_id           VARCHAR(36) COMMENT '样本ID',

    report_type         VARCHAR(20) COMMENT '报告类型',
    report_category     VARCHAR(20) COMMENT '报告分类',

    report_time         DATETIME NOT NULL COMMENT '报告时间',
    tester_id           VARCHAR(20) COMMENT '检验人ID',
    tester_name         VARCHAR(50) COMMENT '检验人姓名',

    audit_time          DATETIME COMMENT '审核时间',
    auditor_id          VARCHAR(20) COMMENT '审核人ID',
    auditor_name        VARCHAR(50) COMMENT '审核人姓名',

    publish_time        DATETIME COMMENT '发布时间',
    publisher_id        VARCHAR(20) COMMENT '发布人ID',
    publisher_name      VARCHAR(50) COMMENT '发布人姓名',

    critical_report     TINYINT COMMENT '危急值报告',
    critical_notify_time DATETIME COMMENT '危急值通知时间',
    critical_confirm_time DATETIME COMMENT '危急值确认时间',
    critical_receiver   VARCHAR(50) COMMENT '危急值接收人',

    status              VARCHAR(20) NOT NULL DEFAULT '草稿' COMMENT '状态',

    print_count         INT DEFAULT 0 COMMENT '打印次数',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_request (request_id),
    INDEX idx_patient (patient_id),
    INDEX idx_report_no (report_no),
    INDEX idx_status (status)
);
```

#### 3.1.7 危急值记录 CriticalValue
```sql
CREATE TABLE critical_value (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '记录ID',
    request_id          VARCHAR(36) NOT NULL COMMENT '申请ID',
    sample_id           VARCHAR(36) COMMENT '样本ID',
    result_id           VARCHAR(36) COMMENT '结果ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    item_id             VARCHAR(36) NOT NULL COMMENT '项目ID',
    item_name           VARCHAR(100) COMMENT '项目名称',
    test_value          VARCHAR(100) COMMENT '检测值',
    critical_level      VARCHAR(20) COMMENT '危急级别(高/低)',
    critical_range      VARCHAR(100) COMMENT '危急值范围',

    detect_time         DATETIME COMMENT '发现时间',
    detecter_id         VARCHAR(20) COMMENT '发现人ID',
    detecter_name       VARCHAR(50) COMMENT '发现人姓名',

    notify_time         DATETIME COMMENT '通知时间',
    notify_method       VARCHAR(20) COMMENT '通知方式(电话/系统)',
    notifier_id         VARCHAR(20) COMMENT '通知人ID',
    notifier_name       VARCHAR(50) COMMENT '通知人姓名',

    receive_time        DATETIME COMMENT '接收确认时间',
    receiver_dept       VARCHAR(100) COMMENT '接收科室',
    receiver_name       VARCHAR(50) COMMENT '接收人姓名',
    receiver_phone      VARCHAR(20) COMMENT '接收电话',

    handle_time         DATETIME COMMENT '处理时间',
    handle_result       TEXT COMMENT '处理结果',

    status              VARCHAR(20) NOT NULL DEFAULT '待处理' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_request (request_id),
    INDEX idx_patient (patient_id),
    INDEX idx_status (status)
);
```

---

## 4. 业务流程

### 4.1 检验流程
```
┌─────────────────────────────────────────────────────────────────┐
│                        检验业务流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 医生申请  │───>│ 样本采集  │───>│ 样本核收  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │               │                       │
│                         ▼               ▼                       │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 报告发布  │<───│ 报告审核  │<───│ 检验检测  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │                                       │
│                         │ 危急值                                 │
│                         ▼                                       │
│                   ┌──────────┐                                 │
│                   │ 危急值通知 │                                 │
│                   └──────────┘                                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 急诊检验流程
```
1. 医生开立急诊检验申请（标识急诊）
2. 护士立即采样
3. 样本优先核收
4. 检验科优先检测
5. 结果优先审核
6. 报告立即发布
7. 急诊检验时限：常规项目30分钟内
```

---

## 5. 接口定义

### 5.1 检验申请接口

#### 5.1.1 开立检验申请
```
POST /api/lis/request/create

Request:
{
    "patientId": "P001",
    "visitType": "门诊",
    "visitId": "VIS001",
    "deptId": "D001",
    "doctorId": "DOC001",
    "clinicalDiagnosis": "上呼吸道感染",
    "isEmergency": false,
    "items": [
        {"itemId": "ITEM001", "itemName": "血常规"},
        {"itemId": "ITEM002", "itemName": "C反应蛋白"}
    ]
}

Response:
{
    "code": 0,
    "message": "申请成功",
    "data": {
        "requestId": "REQ202401150001",
        "requestNo": "LS202401150001"
    }
}
```

#### 5.1.2 查询检验结果
```
GET /api/lis/result/query?requestId=REQ001

Response:
{
    "code": 0,
    "data": {
        "requestId": "REQ001",
        "requestNo": "LS202401150001",
        "patientName": "张三",
        "reportStatus": "已发布",
        "reportTime": "2024-01-15 14:30:00",
        "results": [
            {
                "itemName": "白细胞计数",
                "testValue": "8.5",
                "unit": "10^9/L",
                "referenceRange": "4-10",
                "resultFlag": "正常"
            },
            {
                "itemName": "C反应蛋白",
                "testValue": "25",
                "unit": "mg/L",
                "referenceRange": "0-10",
                "resultFlag": "偏高"
            }
        ]
    }
}
```

### 5.2 样本管理接口

#### 5.2.1 样本采集登记
```
POST /api/lis/sample/collect

Request:
{
    "requestId": "REQ001",
    "collectorId": "NUR001",
    "collectionTime": "2024-01-15 09:30:00"
}

Response:
{
    "code": 0,
    "message": "采集登记成功",
    "data": {
        "sampleId": "SAM202401150001",
        "sampleNo": "BL202401150001",
        "labelContent": "张三 男 35岁 血常规"
    }
}
```

#### 5.2.2 样本核收
```
POST /api/lis/sample/receive

Request:
{
    "sampleNo": "BL202401150001",
    "receiverId": "TEC001"
}

Response:
{
    "code": 0,
    "message": "核收成功"
}
```

---

## 6. 业务规则与约束

### 6.1 时限规则
| 规则编码 | 规则描述 | 时限要求 |
|----------|----------|----------|
| LIS001 | 急诊血常规报告时限 | 30分钟 |
| LIS002 | 急诊生化报告时限 | 1小时 |
| LIS003 | 常规检验报告时限 | 24小时 |
| LIS004 | 特殊检验报告时限 | 48-72小时 |

### 6.2 危急值规则
| 项目 | 危急值下限 | 危急值上限 |
|------|------------|------------|
| 血钾 | <2.5 mmol/L | >6.5 mmol/L |
| 血钙 | <1.5 mmol/L | >3.5 mmol/L |
| 血糖 | <2.2 mmol/L | >27.8 mmol/L |
| 血钠 | <120 mmol/L | >160 mmol/L |
| 白细胞 | <1.0 10^9/L | >30 10^9/L |
| 血小板 | <20 10^9/L | >1000 10^9/L |
| 血红蛋白 | <50 g/L | >200 g/L |

### 6.3 质控规则
- 每日开机前必须进行质控检测
- 质控合格后方可检测患者样本
- 失控必须分析原因并记录
- 质控数据保存至少2年

---

## 7. 模块交互关系

### 7.1 上游依赖
- **门诊管理模块**: 检验申请
- **住院管理模块**: 医嘱检验
- **财务收费模块**: 检验收费

### 7.2 下游调用
- **电子病历模块**: 检验结果查看

### 7.3 外部接口
- **检验仪器**: 仪器数据接口
- **医保接口**: 检验医保结算

---

## 8. 性能与安全要求

### 8.1 性能要求
| 指标 | 要求 |
|------|------|
| 仪器数据接收延迟 | < 5秒 |
| 报告生成时间 | < 3秒 |
| 危急值预警延迟 | < 1分钟 |

### 8.2 安全要求
- 危急值处理完整记录
- 结果修改审计日志
- 质控数据不可删除
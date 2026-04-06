# 财务收费模块需求说明书

## 1. 模块概述与目标

### 1.1 模块定位
财务收费模块是HIS系统的核心管理模块，负责医院费用管理、结算收费、医保对接、财务报表等功能，保障医院财务运营。

### 1.2 业务目标
- 实现费用精细化管理
- 规范收费结算流程
- 保障医保结算准确性
- 提供财务决策支持
- 满足财务合规要求

### 1.3 用户角色
- 收费员
- 财务会计
- 财务管理员
- 医保结算员
- 系统管理员

---

## 2. 功能清单

### 2.1 价表管理

#### 2.1.1 价表维护
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 价格项目维护 | 收费项目基本信息 | 高 |
| 项目分类管理 | 收费项目分类（药品/检查/治疗等） | 高 |
| 价格版本管理 | 价格版本控制 | 高 |
| 调价管理 | 价格调整审批流程 | 高 |
| 价格查询 | 价格项目查询 | 高 |

#### 2.1.2 价表信息
```
收费项目 PriceItem:
- itemId: string, 项目ID
- itemCode: string, 项目编码
- itemName: string, 项目名称
- itemCategory: enum, 分类, [药品/检查/检验/治疗/床位/护理/材料/其他]
- itemUnit: string, 单位
- standardPrice: decimal, 标准价格
- retailPrice: decimal, 零售价格
- wholesalePrice: decimal, 批发价格
- insuranceType: enum, 医保类型, [甲类/乙类/丙类/自费]
- insuranceCode: string, 医保编码
- insurancePrice: decimal, 医保价格
- effectiveDate: date, 生效日期
- status: enum, 状态
```

### 2.2 医保政策管理

#### 2.2.1 医保政策配置
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 医保类型配置 | 医保类型设置 | 高 |
| 报销比例设置 | 各类型报销比例 | 高 |
| 起付线设置 | 医保起付线配置 | 高 |
| 封顶线设置 | 医保封顶线配置 | 高 |
| 医保目录维护 | 医保三大目录管理 | 高 |
| 医保规则配置 | 医保结算规则配置 | 高 |

#### 2.2.2 医保类型
```
医保类型 InsuranceType:
- 城镇职工医保
- 城镇居民医保
- 新农合
- 公费医疗
- 商业保险
- 自费

医保报销规则:
- 起付线: 低于此金额不予报销
- 报销比例: 甲类100%，乙类80-90%，丙类不报销
- 封顶线: 年度报销上限
- 大病保险: 超过封顶线后大病保险报销
```

### 2.3 门诊收费管理

#### 2.3.1 收费功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 挂号收费 | 挂号费收取 | 高 |
| 处方收费 | 药品费用收取 | 高 |
| 检查检验收费 | 检查检验费用收取 | 高 |
| 费用合并 | 多项目费用合并结算 | 高 |
| 医保结算 | 医保实时结算 | 高 |
| 自费结算 | 自费患者结算 | 高 |

#### 2.3.2 支付方式
```
支付方式 PaymentMethod:
- CASH: 现金
- CARD: 银行卡
- WECHAT: 微信支付
- ALIPAY: 支付宝
- MEDICAL_INSURANCE: 医保
- PREPAID: 预交金
- MIXED: 混合支付
```

### 2.4 住院收费管理

#### 2.4.1 预交金管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 预交金收取 | 收取住院预交金 | 高 |
| 预交金充值 | 续交预交金 | 高 |
| 预交金查询 | 查询预交金余额 | 高 |
| 预交金提醒 | 预交金不足提醒 | 高 |
| 预交金退还 | 出院退还预交金 | 高 |

#### 2.4.2 费用管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 费用录入 | 费用项目录入 | 高 |
| 自动计费 | 医嘱自动计费 | 高 |
| 费用审核 | 费用合理性审核 | 高 |
| 一日清单 | 每日费用清单 | 高 |
| 费用查询 | 患者费用查询 | 高 |

### 2.5 出院结算管理

#### 2.5.1 结算功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 费用汇总 | 住院费用汇总 | 高 |
| 医保结算 | 医保出院结算 | 高 |
| 自费结算 | 自费部分结算 | 高 |
| 预交金结算 | 预交金抵扣结算 | 高 |
| 补交/退还 | 费用补交或退还 | 高 |
| 结算清单 | 生成结算清单 | 高 |

#### 2.5.2 结算流程
```
┌─────────────────────────────────────────────────────────────────┐
│                       出院结算流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 费用汇总  │───>│ 费用审核  │───>│ 医保结算  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │               │                       │
│                         ▼               ▼                       │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 结束结算  │<───│ 补交/退还  │<───│ 预交金结算 │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │                                       │
│                         ▼                                       │
│                   ┌──────────┐                                 │
│                   │ 打印发票  │                                 │
│                   └──────────┘                                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.6 发票管理

#### 2.6.1 发票功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 发票开具 | 开具医疗收费发票 | 高 |
| 发票打印 | 打印纸质发票 | 高 |
| 电子发票 | 生成电子发票 | 中 |
| 发票重打 | 发票遗失重新打印 | 高 |
| 发票作废 | 发票作废处理 | 高 |
| 发票查询 | 发票历史查询 | 高 |

#### 2.6.2 发票类型
```
发票类型 InvoiceType:
- 医疗收费发票
- 预交金收据
- 结算清单
- 电子发票

发票信息:
- 发票代码
- 发票号码
- 开票日期
- 患者信息
- 项目明细
- 合计金额
- 医保支付
- 个人支付
```

### 2.7 退费管理

#### 2.7.1 退费功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 退费申请 | 退费申请提交 | 高 |
| 退费审核 | 退费申请审核 | 高 |
| 退费处理 | 退费执行处理 | 高 |
| 退费记录 | 退费记录查询 | 高 |

#### 2.7.2 退费规则
```yaml
退费规则:
  门诊退费:
    - 药品已发药：不支持退费
    - 检查已完成：需审批退费
    - 未执行项目：支持退费

  住院退费:
    - 费用录入错误：支持退费
    - 检查检验未执行：支持退费
    - 已执行项目：需审批退费

  退费审批:
    - 小额退费：收费员直接办理
    - 大额退费：需要审批流程
    - 医保退费：需要医保确认
```

### 2.8 欠费管理

#### 2.8.1 欠费功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 欠费登记 | 欠费患者登记 | 高 |
| 欠费催缴 | 欠费催缴提醒 | 高 |
| 欠费还款 | 欠费还款处理 | 高 |
| 欠费查询 | 欠费记录查询 | 高 |
| 欠费统计 | 欠费统计分析 | 中 |

### 2.9 日结管理

#### 2.9.1 日结功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 收款日结 | 收款员日结算 | 高 |
| 收入日结 | 财务收入日结 | 高 |
| 退费日结 | 退费日结算 | 高 |
| 日结报表 | 日结报表生成 | 高 |
| 日结核对 | 日结核对确认 | 高 |

### 2.10 财务报表

#### 2.10.1 报表功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 收入日报 | 日收入报表 | 高 |
| 收入月报 | 月收入报表 | 高 |
| 科室收入 | 科室收入统计 | 高 |
| 医保统计 | 医保结算统计 | 高 |
| 项目收入 | 项目收入分析 | 中 |
| 对账报表 | 与医保/银行对账 | 高 |

---

## 3. 数据实体定义

### 3.1 核心实体

#### 3.1.1 收费项目 PriceItem
```sql
CREATE TABLE price_item (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '项目ID',
    item_code           VARCHAR(20) NOT NULL UNIQUE COMMENT '项目编码',
    item_name           VARCHAR(100) NOT NULL COMMENT '项目名称',
    item_category       VARCHAR(20) NOT NULL COMMENT '项目分类',
    item_unit           VARCHAR(20) COMMENT '单位',
    item_spec           VARCHAR(50) COMMENT '规格',

    standard_price      DECIMAL(10,4) COMMENT '标准价',
    retail_price        DECIMAL(10,4) NOT NULL COMMENT '零售价',
    wholesale_price     DECIMAL(10,4) COMMENT '批发价',

    insurance_type      VARCHAR(20) COMMENT '医保类型',
    insurance_code      VARCHAR(50) COMMENT '医保编码',
    insurance_price     DECIMAL(10,4) COMMENT '医保价',
    reimbursement_ratio DECIMAL(5,2) COMMENT '报销比例',

    effective_date      DATE COMMENT '生效日期',
    expire_date         DATE COMMENT '失效日期',
    version_no          VARCHAR(10) COMMENT '版本号',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_code (item_code),
    INDEX idx_category (item_category),
    INDEX idx_insurance (insurance_type)
);
```

#### 3.1.2 门诊收费记录 OutpatientBilling
```sql
CREATE TABLE outpatient_billing (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '收费ID',
    billing_no          VARCHAR(30) NOT NULL UNIQUE COMMENT '收费单号',
    invoice_no          VARCHAR(30) COMMENT '发票号',

    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',
    patient_name        VARCHAR(50) COMMENT '患者姓名',
    visit_id            VARCHAR(36) COMMENT '就诊ID',
    visit_no            VARCHAR(30) COMMENT '就诊序号',

    dept_id             VARCHAR(20) COMMENT '科室ID',
    dept_name           VARCHAR(100) COMMENT '科室名称',

    billing_date        DATE NOT NULL COMMENT '收费日期',
    billing_time        DATETIME NOT NULL COMMENT '收费时间',

    total_amount        DECIMAL(12,2) COMMENT '总金额',
    discount_amount     DECIMAL(12,2) COMMENT '优惠金额',
    insurance_amount    DECIMAL(12,2) COMMENT '医保支付',
    self_pay_amount     DECIMAL(12,2) COMMENT '自付金额',

    insurance_type      VARCHAR(20) COMMENT '医保类型',
    insurance_card_no   VARCHAR(50) COMMENT '医保卡号',

    payments            TEXT COMMENT '支付明细(JSON)',

    operator_id         VARCHAR(20) COMMENT '收费员ID',
    operator_name       VARCHAR(50) COMMENT '收费员姓名',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态',
    refund_status       VARCHAR(20) COMMENT '退费状态',
    refund_time         DATETIME COMMENT '退费时间',
    refund_operator_id  VARCHAR(20) COMMENT '退费操作员ID',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_patient (patient_id),
    INDEX idx_visit (visit_id),
    INDEX idx_billing_date (billing_date),
    INDEX idx_invoice (invoice_no)
);
```

#### 3.1.3 门诊收费明细 OutpatientBillingItem
```sql
CREATE TABLE outpatient_billing_item (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '明细ID',
    billing_id          VARCHAR(36) NOT NULL COMMENT '收费ID',

    item_id             VARCHAR(36) NOT NULL COMMENT '项目ID',
    item_code           VARCHAR(20) COMMENT '项目编码',
    item_name           VARCHAR(100) COMMENT '项目名称',
    item_category       VARCHAR(20) COMMENT '项目分类',
    item_unit           VARCHAR(20) COMMENT '单位',

    quantity            DECIMAL(10,2) COMMENT '数量',
    unit_price          DECIMAL(10,4) COMMENT '单价',
    amount              DECIMAL(10,2) COMMENT '金额',

    insurance_type      VARCHAR(20) COMMENT '医保类型',
    insurance_amount    DECIMAL(10,2) COMMENT '医保支付',
    self_pay_amount     DECIMAL(10,2) COMMENT '自付金额',

    prescription_id     VARCHAR(36) COMMENT '处方ID',
    request_id          VARCHAR(36) COMMENT '申请ID',

    status              VARCHAR(20) COMMENT '状态',
    refund_status       VARCHAR(20) COMMENT '退费状态',
    refund_amount       DECIMAL(10,2) COMMENT '退费金额',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_billing (billing_id),
    INDEX idx_item (item_id)
);
```

#### 3.1.4 住院预交金 Prepayment
```sql
CREATE TABLE prepayment (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '预交金ID',
    prepayment_no       VARCHAR(30) NOT NULL UNIQUE COMMENT '预交金单号',
    receipt_no          VARCHAR(30) COMMENT '收据号',

    admission_id        VARCHAR(36) NOT NULL COMMENT '住院ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',
    patient_name        VARCHAR(50) COMMENT '患者姓名',

    deposit_type        VARCHAR(20) NOT NULL COMMENT '类型(缴纳/退还)',
    deposit_amount      DECIMAL(12,2) NOT NULL COMMENT '金额',

    payment_method      VARCHAR(20) COMMENT '支付方式',

    balance_before      DECIMAL(12,2) COMMENT '操作前余额',
    balance_after       DECIMAL(12,2) COMMENT '操作后余额',

    operator_id         VARCHAR(20) COMMENT '操作员ID',
    operator_name       VARCHAR(50) COMMENT '操作员姓名',
    operate_time        DATETIME NOT NULL COMMENT '操作时间',

    remark              VARCHAR(200) COMMENT '备注',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_admission (admission_id),
    INDEX idx_patient (patient_id),
    INDEX idx_operate_time (operate_time)
);
```

#### 3.1.5 住院结算 InpatientSettlement
```sql
CREATE TABLE inpatient_settlement (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '结算ID',
    settlement_no       VARCHAR(30) NOT NULL UNIQUE COMMENT '结算单号',
    invoice_no          VARCHAR(30) COMMENT '发票号',

    admission_id        VARCHAR(36) NOT NULL COMMENT '住院ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',
    patient_name        VARCHAR(50) COMMENT '患者姓名',

    admission_date      DATE COMMENT '入院日期',
    discharge_date      DATE COMMENT '出院日期',
    hospital_days       INT COMMENT '住院天数',

    total_amount        DECIMAL(12,2) COMMENT '总费用',
    bed_fee             DECIMAL(12,2) COMMENT '床位费',
    drug_fee            DECIMAL(12,2) COMMENT '药品费',
    exam_fee            DECIMAL(12,2) COMMENT '检查费',
    test_fee            DECIMAL(12,2) COMMENT '检验费',
    treatment_fee       DECIMAL(12,2) COMMENT '治疗费',
    material_fee        DECIMAL(12,2) COMMENT '材料费',
    nursing_fee         DECIMAL(12,2) COMMENT '护理费',
    other_fee           DECIMAL(12,2) COMMENT '其他费',

    total_deposit       DECIMAL(12,2) COMMENT '预交金总额',
    insurance_amount    DECIMAL(12,2) COMMENT '医保支付',
    self_pay_amount     DECIMAL(12,2) COMMENT '自付金额',
    refund_amount       DECIMAL(12,2) COMMENT '退还金额',
    supplement_amount   DECIMAL(12,2) COMMENT '补交金额',

    insurance_type      VARCHAR(20) COMMENT '医保类型',
    insurance_card_no   VARCHAR(50) COMMENT '医保卡号',
    insurance_claim_no  VARCHAR(50) COMMENT '医保申报号',

    payments            TEXT COMMENT '支付明细(JSON)',

    settlement_time     DATETIME NOT NULL COMMENT '结算时间',
    operator_id         VARCHAR(20) COMMENT '结算员ID',
    operator_name       VARCHAR(50) COMMENT '结算员姓名',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_admission (admission_id),
    INDEX idx_patient (patient_id),
    INDEX idx_settlement_time (settlement_time)
);
```

#### 3.1.6 日结记录 DailySettlement
```sql
CREATE TABLE daily_settlement (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '日结ID',
    settlement_no       VARCHAR(30) NOT NULL UNIQUE COMMENT '日结单号',

    settlement_date     DATE NOT NULL COMMENT '日结日期',
    operator_id         VARCHAR(20) NOT NULL COMMENT '收费员ID',
    operator_name       VARCHAR(50) COMMENT '收费员姓名',

    cash_amount         DECIMAL(12,2) COMMENT '现金收入',
    card_amount         DECIMAL(12,2) COMMENT '银行卡收入',
    wechat_amount       DECIMAL(12,2) COMMENT '微信收入',
    alipay_amount       DECIMAL(12,2) COMMENT '支付宝收入',
    insurance_amount    DECIMAL(12,2) COMMENT '医保收入',
    prepaid_amount      DECIMAL(12,2) COMMENT '预交金收入',

    total_income        DECIMAL(12,2) COMMENT '总收入',
    total_refund        DECIMAL(12,2) COMMENT '总退费',
    net_income          DECIMAL(12,2) COMMENT '净收入',

    billing_count       INT COMMENT '收费笔数',
    refund_count        INT COMMENT '退费笔数',

    settlement_time     DATETIME COMMENT '日结时间',
    confirm_time        DATETIME COMMENT '确认时间',
    confirmer_id        VARCHAR(20) COMMENT '确认人ID',

    status              VARCHAR(20) NOT NULL DEFAULT '待确认' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_date_operator (settlement_date, operator_id),
    INDEX idx_status (status)
);
```

---

## 4. 业务流程

### 4.1 门诊收费流程
```
┌─────────────────────────────────────────────────────────────────┐
│                       门诊收费流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 获取待收 │───>│ 医保结算  │───>│ 计算费用  │                │
│   │ 费项目   │    │ (医保患者)│    │          │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │               ▼                       │
│                         │         ┌──────────┐                │
│                         │         │ 选择支付  │                │
│                         │         │ 方式      │                │
│                         │         └──────────┘                │
│                         │               │                       │
│                         ▼               ▼                       │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 完成收费  │<───│ 打印发票  │<───│ 确认支付  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 出院结算流程
```
1. 出院申请
2. 费用汇总统计
3. 费用审核核对
4. 医保结算申报
5. 预交金抵扣计算
6. 补交/退还处理
7. 开具发票
8. 完成结算
```

---

## 5. 接口定义

### 5.1 门诊收费接口

#### 5.1.1 获取待收费项目
```
GET /api/finance/outpatient/pending?visitId=VIS001

Response:
{
    "code": 0,
    "data": {
        "patientInfo": {...},
        "items": [
            {
                "itemId": "ITEM001",
                "itemType": "处方",
                "itemNo": "RX001",
                "description": "布洛芬缓释胶囊等",
                "amount": 58.50
            }
        ],
        "totalAmount": 83.50
    }
}
```

#### 5.1.2 结算收费
```
POST /api/finance/outpatient/settle

Request:
{
    "visitId": "VIS001",
    "patientId": "P001",
    "insuranceType": "城镇职工医保",
    "insuranceCardNo": "1234567890",
    "payments": [
        {"payMethod": "MEDICAL_INSURANCE", "amount": 50.00},
        {"payMethod": "WECHAT", "amount": 33.50}
    ]
}

Response:
{
    "code": 0,
    "message": "收费成功",
    "data": {
        "billingId": "BIL202401150001",
        "invoiceNo": "INV202401150001",
        "totalAmount": 83.50,
        "insuranceAmount": 50.00,
        "selfPayAmount": 33.50
    }
}
```

### 5.2 住院收费接口

#### 5.2.1 预交金缴纳
```
POST /api/finance/inpatient/prepayment

Request:
{
    "admissionId": "IP001",
    "depositAmount": 5000.00,
    "paymentMethod": "WECHAT"
}

Response:
{
    "code": 0,
    "message": "缴纳成功",
    "data": {
        "prepaymentNo": "PRE202401150001",
        "receiptNo": "REC202401150001",
        "balanceAfter": 5000.00
    }
}
```

#### 5.2.2 出院结算
```
POST /api/finance/inpatient/settle

Request:
{
    "admissionId": "IP001",
    "payments": [
        {"payMethod": "MEDICAL_INSURANCE", "amount": 8000.00},
        {"payMethod": "PREPAID", "amount": 5000.00}
    ]
}

Response:
{
    "code": 0,
    "message": "结算成功",
    "data": {
        "settlementId": "SET202401150001",
        "invoiceNo": "INV202401150001",
        "totalAmount": 9500.00,
        "insuranceAmount": 8000.00,
        "prepaidUsed": 5000.00,
        "refundAmount": 3500.00
    }
}
```

---

## 6. 业务规则与约束

### 6.1 收费规则
| 规则编码 | 规则描述 |
|----------|----------|
| FIN001 | 未收费项目才能收费 |
| FIN002 | 已收费项目不能重复收费 |
| FIN003 | 医保结算需实时对接医保系统 |
| FIN004 | 发票号必须连续且唯一 |

### 6.2 退费规则
| 规则编码 | 规则描述 |
|----------|----------|
| FIN101 | 药品已发药不能退费 |
| FIN102 | 检查已执行需审批退费 |
| FIN103 | 医保退费需医保确认 |
| FIN104 | 大额退费需审批 |

### 6.3 医保规则
| 规则编码 | 规则描述 |
|----------|----------|
| INS001 | 甲类药品全额纳入医保 |
| INS002 | 乙类药品部分纳入医保 |
| INS003 | 丙类药品自费 |
| INS004 | 超限价部分自费 |

---

## 7. 模块交互关系

### 7.1 上游依赖
- **门诊管理模块**: 门诊费用信息
- **住院管理模块**: 住院费用信息
- **药房管理模块**: 药品费用
- **检验管理模块**: 检验费用
- **影像管理模块**: 检查费用

### 7.2 外部接口
- **医保接口**: 医保实时结算
- **支付接口**: 微信/支付宝/银行卡支付

---

## 8. 性能与安全要求

### 8.1 性能要求
| 指标 | 要求 |
|------|------|
| 收费响应时间 | < 3秒 |
| 医保结算时间 | < 5秒 |
| 日结处理时间 | < 30秒 |

### 8.2 安全要求
- 财务数据严格权限控制
- 发票管理审计日志
- 财务数据加密存储
- 日结数据不可修改
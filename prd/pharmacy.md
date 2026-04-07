# 药房管理模块需求说明书

## 1. 模块概述与目标

### 1.1 模块定位
药房管理模块是HIS系统的重要组成部分，负责药品的采购、库存、发药、用药审核等全流程管理，保障临床用药安全，提高药房工作效率。

### 1.2 业务目标
- 实现药品全生命周期管理
- 保障药品库存安全与供应
- 规范发药流程，确保用药安全
- 提供药品信息支持临床决策
- 实现药房精细化管理

### 1.3 用户角色
- 药房管理员
- 药师
- 发药员
- 采购员
- 库管员

---

## 2. 功能清单

### 2.1 药品信息管理

#### 2.1.1 药品基础信息
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 药品目录维护 | 药品基本信息维护 | 高 |
| 药品分类管理 | 药品分类（西药/中成药/中草药） | 高 |
| 药品规格管理 | 药品规格、剂型、单位 | 高 |
| 药品价格管理 | 药品价格维护、调价管理 | 高 |
| 药品编码管理 | 药品编码规则设置 | 高 |
| 药品属性管理 | 处方药/OTC、医保属性等 | 高 |

#### 2.1.2 药品信息字段
```
药品基本信息 DrugInfo:
- drugId: string, 药品ID, 主键
- drugCode: string, 药品编码, 唯一
- drugName: string, 药品名称, 必填
- genericName: string, 通用名
- tradeName: string, 商品名
- drugCategory: enum, 药品分类, [西药/中成药/中草药/生物制品]
- drugForm: string, 剂型, [片剂/胶囊/注射剂/口服液等]
- drugSpec: string, 规格
- drugUnit: string, 最小单位
- packageUnit: string, 包装单位
- packageQuantity: int, 包装数量
- manufacturer: string, 生产厂家
- origin: string, 产地
- approvalNo: string, 批准文号
- retailPrice: decimal, 零售价
- purchasePrice: decimal, 进价
- isPrescription: boolean, 是否处方药
- isOtc: boolean, 是否OTC
- isEssential: boolean, 是否基药
- isInsurance: boolean, 是否医保
- insuranceCode: string, 医保编码
- storageCondition: string, 储存条件
- shelfLife: int, 有效期(月)
- status: enum, 状态, [正常/停用]
```

### 2.2 药品库存管理

#### 2.2.1 库存基础管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 库存查询 | 实时库存查询、批次查询 | 高 |
| 库存预警 | 库存上下限预警 | 高 |
| 效期预警 | 药品效期预警管理 | 高 |
| 批次管理 | 药品批次信息管理 | 高 |
| 库存盘点 | 定期盘点、差异处理 | 高 |
| 库存调拨 | 药房之间库存调拨 | 中 |

#### 2.2.2 库存操作类型
```
库存操作类型 InventoryOperationType:
- IN_PURCHASE: 采购入库
- IN_RETURN: 退药入库
- IN_ADJUST: 盘盈入库
- OUT_DISPENSE: 发药出库
- OUT_RETURN: 退货出库
- OUT_EXPIRE: 过期报损
- OUT_ADJUST: 盘亏出库
- TRANSFER_IN: 调拨入库
- TRANSFER_OUT: 调拨出库
```

#### 2.2.3 库存信息
```
库存信息 InventoryInfo:
- inventoryId: string, 库存ID
- drugId: string, 药品ID
- batchNo: string, 批号
- productionDate: date, 生产日期
- expiryDate: date, 有效期
- quantity: decimal, 库存数量
- lockedQuantity: decimal, 锁定数量
- availableQuantity: decimal, 可用数量
- location: string, 库位
- purchasePrice: decimal, 进价
- retailPrice: decimal, 零售价
- supplierId: string, 供应商ID
- supplierName: string, 供应商名称
- status: enum, 状态, [正常/停用/过期]
```

### 2.3 门诊药房发药

#### 2.3.1 发药流程
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 处方接收 | 接收门诊处方信息 | 高 |
| 处方审核 | 药师审核处方合理性 | 高 |
| 处方调配 | 按处方调配药品 | 高 |
| 发药核对 | 核对处方与药品 | 高 |
| 发药确认 | 发药给患者确认 | 高 |
| 发药打印 | 打印发药清单 | 高 |

#### 2.3.2 发药审核规则
```yaml
处方审核规则:
  基本审核:
    - 处方是否有效
    - 处方是否已收费
    - 处方是否已发药
    - 药品库存是否充足

  合理用药审核:
    - 配伍禁忌检查
    - 药物相互作用
    - 剂量合理性
    - 用法合理性
    - 给药途径合理性
    - 重复用药检查
    - 过敏史检查
    - 禁忌症检查

  特殊药品审核:
    - 精神类药品审核
    - 麻醉药品审核
    - 抗菌药物分级审核
    - 儿童用药审核
    - 孕妇用药审核
```

### 2.4 住院药房发药

#### 2.4.1 发药模式
| 模式 | 描述 | 适用场景 |
|------|------|----------|
| 临时医嘱发药 | 按临时医嘱即时发药 | 临时用药 |
| 长期医嘱发药 | 按长期医嘱批量发药 | 常规用药 |
| 单剂量发药 | 单剂量包装发药 | 单剂量调配 |
| 病区领药 | 病区统一领药 | 常备药品 |

#### 2.4.2 住院发药流程
```
┌─────────────────────────────────────────────────────────────────┐
│                      住院药房发药流程                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 医嘱接收  │───>│ 药师审核  │───>│ 药品调配  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                                        │                        │
│                                        ▼                        │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 病区接收  │<───│ 发药核对  │<───│ 发药打包  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.5 药品采购管理

#### 2.5.1 采购流程
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 采购申请 | 根据库存生成采购申请 | 高 |
| 采购审核 | 采购申请审核 | 高 |
| 采购订单 | 生成采购订单 | 高 |
| 订单跟踪 | 跟踪订单执行状态 | 中 |
| 入库验收 | 药品入库验收 | 高 |
| 入库确认 | 入库确认及库存更新 | 高 |

#### 2.5.2 采购订单信息
```
采购订单 PurchaseOrder:
- orderId: string, 订单ID
- orderNo: string, 订单号
- supplierId: string, 供应商ID
- supplierName: string, 供应商名称
- orderDate: date, 订单日期
- deliveryDate: date, 预计到货日期
- totalAmount: decimal, 订单金额
- status: enum, 状态, [待审核/已审核/已发货/已入库/已取消]
- items: 采购明细列表

采购明细 PurchaseOrderItem:
- itemId: string, 明细ID
- drugId: string, 药品ID
- drugName: string, 药品名称
- drugSpec: string, 规格
- quantity: decimal, 数量
- purchasePrice: decimal, 进价
- amount: decimal, 金额
```

### 2.6 供应商管理

#### 2.6.1 供应商信息
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 供应商档案 | 供应商基本信息 | 高 |
| 资质管理 | 营业执照、许可证等资质 | 高 |
| 合同管理 | 采购合同管理 | 中 |
| 供应商评估 | 供应商绩效评估 | 中 |
| 黑名单管理 | 供应商黑名单管理 | 中 |

### 2.7 药品盘点管理

#### 2.7.1 盘点流程
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 盘点计划 | 制定盘点计划 | 高 |
| 盘点录入 | 录入盘点数量 | 高 |
| 盘点核对 | 盘点结果核对 | 高 |
| 差异处理 | 盘盈盘亏处理 | 高 |
| 盘点报告 | 盘点报告生成 | 中 |

### 2.8 效期管理

#### 2.8.1 效期预警规则
| 预警级别 | 条件 | 处理方式 |
|----------|------|----------|
| 红色预警 | 有效期<1个月 | 立即促销/报损 |
| 黄色预警 | 有效期<3个月 | 优先使用 |
| 蓝色预警 | 有效期<6个月 | 关注提醒 |

### 2.9 用药审核

#### 2.9.1 审核规则库
```
配伍禁忌审核:
- 物理配伍禁忌: 混合后发生沉淀、变色等
- 化学配伍禁忌: 化学反应影响药效
- 药理配伍禁忌: 药效相互拮抗或增强

药物相互作用:
- 协同作用: 药效增强
- 拮抗作用: 药效减弱
- 增加毒性: 副作用增强

剂量审核:
- 单次剂量范围
- 日剂量范围
- 极量检查
- 儿童剂量调整
- 肾功能剂量调整

特殊人群审核:
- 孕妇禁用/慎用药品
- 哺乳期禁用/慎用药品
- 儿童禁用/慎用药品
- 老年人用药调整
- 肝肾功能不全调整
```

### 2.10 报表统计

#### 2.10.1 统计报表
| 报表名称 | 描述 | 频率 |
|----------|------|------|
| 药品销售统计 | 药品销售数量、金额统计 | 日/月 |
| 库存报表 | 当前库存明细 | 实时 |
| 效期报表 | 效期预警药品列表 | 日 |
| 采购报表 | 采购入库统计 | 月 |
| 盘点报表 | 盘点差异统计 | 盘点后 |
| 用药分析 | 药品使用排名、趋势 | 月 |

---

## 3. 数据实体定义

### 3.1 核心实体

#### 3.1.1 药品信息 Drug
```sql
CREATE TABLE drug (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '药品ID',
    drug_code           VARCHAR(30) NOT NULL UNIQUE COMMENT '药品编码',
    drug_name           VARCHAR(100) NOT NULL COMMENT '药品名称',
    generic_name        VARCHAR(100) COMMENT '通用名',
    trade_name          VARCHAR(100) COMMENT '商品名',
    pinyin_code         VARCHAR(50) COMMENT '拼音码',
    custom_code         VARCHAR(50) COMMENT '自定义码',

    drug_category       VARCHAR(20) NOT NULL COMMENT '药品分类(西药/中成药/中草药/生物制品)',
    drug_form           VARCHAR(20) COMMENT '剂型',
    drug_spec           VARCHAR(50) COMMENT '规格',
    drug_unit           VARCHAR(20) COMMENT '最小单位',
    package_unit        VARCHAR(20) COMMENT '包装单位',
    package_quantity    INT COMMENT '包装数量',

    manufacturer        VARCHAR(100) COMMENT '生产厂家',
    origin              VARCHAR(50) COMMENT '产地',
    approval_no         VARCHAR(50) COMMENT '批准文号',

    purchase_price      DECIMAL(10,4) COMMENT '进价',
    retail_price        DECIMAL(10,4) NOT NULL COMMENT '零售价',
    price_date         DATE COMMENT '调价日期',

    is_prescription     TINYINT DEFAULT 0 COMMENT '是否处方药',
    is_otc              TINYINT DEFAULT 0 COMMENT '是否OTC',
    is_essential        TINYINT DEFAULT 0 COMMENT '是否基药',
    is_insurance        TINYINT DEFAULT 0 COMMENT '是否医保',
    insurance_code      VARCHAR(50) COMMENT '医保编码',
    insurance_type      VARCHAR(20) COMMENT '医保类型(甲类/乙类)',

    storage_condition   VARCHAR(50) COMMENT '储存条件',
    shelf_life          INT COMMENT '有效期(月)',
    alert_days          INT DEFAULT 180 COMMENT '效期预警天数',

    min_stock           DECIMAL(10,2) COMMENT '库存下限',
    max_stock           DECIMAL(10,2) COMMENT '库存上限',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态(正常/停用)',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_drug_name (drug_name),
    INDEX idx_pinyin (pinyin_code),
    INDEX idx_category (drug_category)
);
```

#### 3.1.2 药品库存 DrugInventory
```sql
CREATE TABLE drug_inventory (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '库存ID',
    drug_id             VARCHAR(36) NOT NULL COMMENT '药品ID',
    drug_code           VARCHAR(30) NOT NULL COMMENT '药品编码',
    drug_name           VARCHAR(100) COMMENT '药品名称',
    drug_spec           VARCHAR(50) COMMENT '规格',
    drug_unit           VARCHAR(20) COMMENT '单位',

    pharmacy_id         VARCHAR(20) NOT NULL COMMENT '药房ID',
    pharmacy_name       VARCHAR(100) COMMENT '药房名称',

    batch_no            VARCHAR(50) NOT NULL COMMENT '批号',
    production_date     DATE COMMENT '生产日期',
    expiry_date         DATE NOT NULL COMMENT '有效期',

    quantity            DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '库存数量',
    locked_quantity     DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '锁定数量',
    available_quantity  DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '可用数量',

    location            VARCHAR(50) COMMENT '库位',

    purchase_price      DECIMAL(10,4) COMMENT '进价',
    retail_price        DECIMAL(10,4) COMMENT '零售价',

    supplier_id         VARCHAR(20) COMMENT '供应商ID',
    supplier_name       VARCHAR(100) COMMENT '供应商名称',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态(正常/过期/停用)',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_drug_batch_pharmacy (drug_id, batch_no, pharmacy_id),
    INDEX idx_drug (drug_id),
    INDEX idx_expiry (expiry_date),
    INDEX idx_pharmacy (pharmacy_id)
);
```

#### 3.1.3 库存流水 InventoryTransaction
```sql
CREATE TABLE inventory_transaction (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '流水ID',
    transaction_no      VARCHAR(30) NOT NULL COMMENT '流水号',
    transaction_type    VARCHAR(20) NOT NULL COMMENT '操作类型',

    drug_id             VARCHAR(36) NOT NULL COMMENT '药品ID',
    drug_code           VARCHAR(30) COMMENT '药品编码',
    drug_name           VARCHAR(100) COMMENT '药品名称',
    drug_spec           VARCHAR(50) COMMENT '规格',
    drug_unit           VARCHAR(20) COMMENT '单位',

    pharmacy_id         VARCHAR(20) NOT NULL COMMENT '药房ID',
    batch_no            VARCHAR(50) COMMENT '批号',
    expiry_date         DATE COMMENT '有效期',

    quantity_before     DECIMAL(10,2) COMMENT '变动前数量',
    quantity_change     DECIMAL(10,2) NOT NULL COMMENT '变动数量(正数入库/负数出库)',
    quantity_after      DECIMAL(10,2) COMMENT '变动后数量',

    retail_price        DECIMAL(10,4) COMMENT '零售价',
    purchase_price      DECIMAL(10,4) COMMENT '进价',
    amount              DECIMAL(10,2) COMMENT '金额',

    related_id          VARCHAR(36) COMMENT '关联单据ID',
    related_no          VARCHAR(30) COMMENT '关联单据号',
    reason              VARCHAR(200) COMMENT '原因',

    operator_id         VARCHAR(20) COMMENT '操作员ID',
    operator_name       VARCHAR(50) COMMENT '操作员姓名',
    operate_time        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',

    INDEX idx_drug (drug_id),
    INDEX idx_pharmacy (pharmacy_id),
    INDEX idx_type_time (transaction_type, operate_time),
    INDEX idx_related (related_id)
);
```

#### 3.1.4 发药记录 DispenseRecord
```sql
CREATE TABLE dispense_record (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '发药ID',
    dispense_no         VARCHAR(30) NOT NULL COMMENT '发药单号',
    prescription_id     VARCHAR(36) COMMENT '处方ID',
    prescription_no     VARCHAR(30) COMMENT '处方号',

    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',
    patient_name        VARCHAR(50) COMMENT '患者姓名',
    gender              CHAR(1) COMMENT '性别',
    age                 INT COMMENT '年龄',

    visit_type          VARCHAR(20) NOT NULL COMMENT '就诊类型(门诊/住院)',
    admission_id        VARCHAR(36) COMMENT '住院ID',

    dept_id             VARCHAR(20) COMMENT '科室ID',
    dept_name           VARCHAR(100) COMMENT '科室名称',
    doctor_id           VARCHAR(20) COMMENT '医生ID',
    doctor_name         VARCHAR(50) COMMENT '医生姓名',

    pharmacy_id         VARCHAR(20) NOT NULL COMMENT '药房ID',
    pharmacy_name       VARCHAR(100) COMMENT '药房名称',

    total_amount        DECIMAL(10,2) COMMENT '总金额',

    audit_status        VARCHAR(20) DEFAULT '待审核' COMMENT '审核状态(待审核/审核通过/审核不通过)',
    auditor_id          VARCHAR(20) COMMENT '审核人ID',
    auditor_name        VARCHAR(50) COMMENT '审核人姓名',
    audit_time          DATETIME COMMENT '审核时间',
    audit_remark        VARCHAR(500) COMMENT '审核意见',

    dispense_status     VARCHAR(20) DEFAULT '待发药' COMMENT '发药状态(待发药/已发药/已退药)',
    dispenser_id        VARCHAR(20) COMMENT '发药人ID',
    dispenser_name      VARCHAR(50) COMMENT '发药人姓名',
    dispense_time       DATETIME COMMENT '发药时间',

    receive_confirm     TINYINT DEFAULT 0 COMMENT '患者确认接收',
    receive_time        DATETIME COMMENT '接收确认时间',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_dispense_no (dispense_no),
    INDEX idx_prescription (prescription_id),
    INDEX idx_patient (patient_id),
    INDEX idx_dispense_time (dispense_time)
);
```

#### 3.1.5 发药明细 DispenseDetail
```sql
CREATE TABLE dispense_detail (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '明细ID',
    dispense_id         VARCHAR(36) NOT NULL COMMENT '发药ID',

    drug_id             VARCHAR(36) NOT NULL COMMENT '药品ID',
    drug_code           VARCHAR(30) COMMENT '药品编码',
    drug_name           VARCHAR(100) COMMENT '药品名称',
    drug_spec           VARCHAR(50) COMMENT '规格',
    drug_unit           VARCHAR(20) COMMENT '单位',

    batch_no            VARCHAR(50) COMMENT '批号',
    expiry_date         DATE COMMENT '有效期',

    quantity            DECIMAL(10,2) NOT NULL COMMENT '发药数量',
    retail_price        DECIMAL(10,4) COMMENT '零售价',
    amount              DECIMAL(10,2) COMMENT '金额',

    dosage              VARCHAR(50) COMMENT '用法',
    frequency           VARCHAR(50) COMMENT '频次',
    days                INT COMMENT '天数',
    route               VARCHAR(50) COMMENT '给药途径',

    prescription_detail_id VARCHAR(36) COMMENT '处方明细ID',

    audit_result        VARCHAR(20) COMMENT '审核结果(通过/不通过)',
    audit_remark        VARCHAR(500) COMMENT '审核说明',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_dispense (dispense_id),
    INDEX idx_drug (drug_id)
);
```

#### 3.1.6 采购订单 PurchaseOrder
```sql
CREATE TABLE purchase_order (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '订单ID',
    order_no            VARCHAR(30) NOT NULL COMMENT '订单号',

    supplier_id         VARCHAR(20) NOT NULL COMMENT '供应商ID',
    supplier_name       VARCHAR(100) COMMENT '供应商名称',

    order_date          DATE NOT NULL COMMENT '订单日期',
    expected_date       DATE COMMENT '预计到货日期',

    total_quantity      DECIMAL(10,2) COMMENT '总数量',
    total_amount        DECIMAL(12,2) COMMENT '总金额',

    status              VARCHAR(20) NOT NULL DEFAULT '待审核' COMMENT '状态(待审核/已审核/已发货/部分入库/已入库/已取消)',

    applicant_id        VARCHAR(20) COMMENT '申请人ID',
    applicant_name      VARCHAR(50) COMMENT '申请人姓名',
    apply_time          DATETIME COMMENT '申请时间',

    auditor_id          VARCHAR(20) COMMENT '审核人ID',
    auditor_name        VARCHAR(50) COMMENT '审核人姓名',
    audit_time          DATETIME COMMENT '审核时间',
    audit_remark        VARCHAR(500) COMMENT '审核意见',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_supplier (supplier_id),
    INDEX idx_status (status)
);
```

---

## 4. 业务流程

### 4.1 门诊发药流程
```
┌─────────────────────────────────────────────────────────────────┐
│                      门诊发药流程                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 处方收费  │───>│ 处方传递  │───>│ 药师审核  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │               │ 不通过               │
│                         │               ▼                       │
│                         │         ┌──────────┐                │
│                         │         │ 退回医生  │                │
│                         │         └──────────┘                │
│                         │               │ 通过                 │
│                         ▼               ▼                       │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 发药确认  │<───│ 发药核对  │<───│ 药品调配  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │                                       │
│                         ▼                                       │
│                   ┌──────────┐                                 │
│                   │ 库存扣减  │                                 │
│                   └──────────┘                                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 采购入库流程
```
┌─────────────────────────────────────────────────────────────────┐
│                      采购入库流程                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 采购申请  │───>│ 采购审核  │───>│ 生成订单  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                                        │                        │
│                                        ▼                        │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 库存更新  │<───│ 入库确认  │<───│ 到货验收  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.3 退药流程
```
1. 患者申请退药
2. 核实退药原因
3. 检查药品状态（未拆封、在有效期内）
4. 药师审核
5. 退药入库
6. 费用退费
```

---

## 5. 接口定义

### 5.1 药品查询接口

#### 5.1.1 药品列表查询
```
GET /api/pharmacy/drug/list?keyword=阿莫西林&category=西药

Response:
{
    "code": 0,
    "data": {
        "total": 100,
        "list": [
            {
                "drugId": "DRUG001",
                "drugCode": "XL001",
                "drugName": "阿莫西林胶囊",
                "genericName": "阿莫西林",
                "drugSpec": "0.5g*24粒",
                "drugForm": "胶囊剂",
                "drugUnit": "粒",
                "manufacturer": "XX制药",
                "retailPrice": 25.00,
                "isPrescription": true,
                "isInsurance": true,
                "insuranceType": "甲类",
                "stockQuantity": 1000
            }
        ]
    }
}
```

#### 5.1.2 库存查询
```
GET /api/pharmacy/inventory/query?drugId=DRUG001&pharmacyId=PH001

Response:
{
    "code": 0,
    "data": {
        "drugId": "DRUG001",
        "drugName": "阿莫西林胶囊",
        "drugSpec": "0.5g*24粒",
        "totalQuantity": 1000,
        "availableQuantity": 950,
        "lockedQuantity": 50,
        "batches": [
            {
                "batchNo": "20240101",
                "quantity": 500,
                "expiryDate": "2025-12-31",
                "location": "A-01-01"
            },
            {
                "batchNo": "20240201",
                "quantity": 500,
                "expiryDate": "2026-01-31",
                "location": "A-01-02"
            }
        ]
    }
}
```

### 5.2 发药接口

#### 5.2.1 获取待发药处方
```
GET /api/pharmacy/dispense/pending?pharmacyId=PH001

Response:
{
    "code": 0,
    "data": {
        "total": 15,
        "list": [
            {
                "dispenseId": "DIS001",
                "dispenseNo": "FY202401150001",
                "prescriptionNo": "RX2024011500001",
                "patientId": "P001",
                "patientName": "张三",
                "gender": "男",
                "age": 35,
                "deptName": "内科",
                "doctorName": "李医生",
                "totalAmount": 58.50,
                "status": "待发药",
                "createTime": "2024-01-15 10:30:00"
            }
        ]
    }
}
```

#### 5.2.2 处方审核
```
POST /api/pharmacy/dispense/audit

Request:
{
    "dispenseId": "DIS001",
    "auditorId": "PHA001",
    "auditResult": "通过",
    "auditRemark": "",
    "details": [
        {
            "detailId": "DET001",
            "drugId": "DRUG001",
            "auditResult": "通过"
        }
    ]
}

Response:
{
    "code": 0,
    "message": "审核完成",
    "data": {
        "warnings": [],
        "errors": []
    }
}
```

#### 5.2.3 发药确认
```
POST /api/pharmacy/dispense/confirm

Request:
{
    "dispenseId": "DIS001",
    "dispenserId": "PHA001",
    "details": [
        {
            "detailId": "DET001",
            "drugId": "DRUG001",
            "batchNo": "20240101",
            "quantity": 1
        }
    ]
}

Response:
{
    "code": 0,
    "message": "发药成功"
}
```

### 5.3 库存管理接口

#### 5.3.1 效期预警查询
```
GET /api/pharmacy/inventory/expiry?days=180

Response:
{
    "code": 0,
    "data": [
        {
            "drugId": "DRUG001",
            "drugName": "阿莫西林胶囊",
            "drugSpec": "0.5g*24粒",
            "batchNo": "20230101",
            "quantity": 100,
            "expiryDate": "2024-06-30",
            "daysRemaining": 30,
            "alertLevel": "红色"
        }
    ]
}
```

#### 5.3.2 库存预警查询
```
GET /api/pharmacy/inventory/alert

Response:
{
    "code": 0,
    "data": {
        "lowStock": [
            {
                "drugId": "DRUG002",
                "drugName": "布洛芬片",
                "drugSpec": "0.1g*100片",
                "currentQuantity": 50,
                "minStock": 100,
                "suggestedPurchase": 500
            }
        ],
        "highStock": [
            {
                "drugId": "DRUG003",
                "drugName": "维生素C片",
                "drugSpec": "0.1g*100片",
                "currentQuantity": 5000,
                "maxStock": 3000,
                "suggestedQuantity": 2000
            }
        ]
    }
}
```

---

## 6. 业务规则与约束

### 6.1 库存规则
| 规则编码 | 规则描述 |
|----------|----------|
| INV001 | 库存出库遵循先进先出原则（按效期） |
| INV002 | 效期<30天药品禁止发药 |
| INV003 | 库存不足时禁止发药 |
| INV004 | 盘点期间锁定库存操作 |
| INV005 | 过期药品及时报损处理 |

### 6.2 发药规则
| 规则编码 | 规则描述 |
|----------|----------|
| DSP001 | 处方必须收费后才能发药 |
| DSP002 | 处方必须经过药师审核 |
| DSP003 | 发药必须核对患者信息 |
| DSP004 | 精神类药品需要特殊审批 |
| DSP005 | 麻醉药品需要双人复核 |
| DSP006 | 退药需要检查药品状态 |

### 6.3 审核规则
```yaml
配伍禁忌:
  - 类型: 物理
    级别: 严重
    处理: 禁止发药，返回医生修改

  - 类型: 化学
    级别: 严重
    处理: 禁止发药，返回医生修改

  - 类型: 药理
    级别: 警告
    处理: 提示医生，可强制通过

剂量审核:
  - 单次剂量超限: 警告
  - 日剂量超限: 禁止
  - 超过极量: 禁止并记录

特殊药品:
  - 精神类: 需要审批
  - 麻醉类: 需要审批+双人复核
  - 抗菌药物: 分级管理
```

---

## 7. 模块交互关系

### 7.1 上游依赖
- **系统管理模块**: 用户认证、权限验证
- **基础数据模块**: 药品目录、供应商信息
- **门诊管理模块**: 门诊处方
- **住院管理模块**: 医嘱信息

### 7.2 下游调用
- **财务收费模块**: 药品费用
- **库存物资模块**: 库存管理

### 7.3 外部接口
- **医保接口**: 医保药品审核
- **供应商系统**: 订单传输

---

## 8. 性能与安全要求

### 8.1 性能要求
| 指标 | 要求 |
|------|------|
| 发药响应时间 | < 3秒 |
| 库存查询时间 | < 1秒 |
| 并发发药用户 | >= 100 |
| 系统可用性 | >= 99.9% |

### 8.2 安全要求
- 麻醉药品操作审计
- 库存操作日志记录
- 效期药品特殊管理
- 精神药品权限控制

---

## 9. 附录

### 9.1 药品分类
| 分类 | 说明 |
|------|------|
| 西药 | 化学药品 |
| 中成药 | 中药制剂 |
| 中草药 | 中药材 |
| 生物制品 | 疫苗、血液制品 |

### 9.2 效期预警级别
| 级别 | 颜色 | 条件 | 处理 |
|------|------|------|------|
| 正常 | 绿色 | 有效期>180天 | 正常管理 |
| 关注 | 蓝色 | 有效期90-180天 | 关注提醒 |
| 预警 | 黄色 | 有效期30-90天 | 优先使用 |
| 紧急 | 红色 | 有效期<30天 | 立即处理 |
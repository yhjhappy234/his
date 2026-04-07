# 库存物资模块需求说明书

## 1. 模块概述与目标

### 1.1 模块定位
库存物资模块是HIS系统的后勤管理模块，负责医院物资的采购、入库、出库、盘点、库存管理等全流程，保障医疗物资供应。

### 1.2 业务目标
- 实现物资信息化管理
- 提高物资供应效率
- 降低物资库存成本
- 规范物资使用管理
- 实现物资数据统计

### 1.3 用户角色
- 采购员
- 库管员
- 物资管理员
- 科室领料员
- 财务人员

---

## 2. 功能清单

### 2.1 物资分类管理

#### 2.1.1 分类体系
```
物资分类层级:
一级分类:
- 医疗耗材
- 办公用品
- 设备配件
- 维修材料
- 生活用品
- 其他物资

二级分类示例(医疗耗材):
- 一次性耗材
- 注射器/输液器
- 检验耗材
- 手术耗材
- 敷料耗材
- 其他耗材
```

#### 2.1.2 分类功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 分类设置 | 物资分类层级设置 | 高 |
| 分类维护 | 分类信息维护 | 高 |
| 分类查询 | 分类信息查询 | 高 |

### 2.2 物资信息管理

#### 2.2.1 物资维护
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 物资登记 | 物资基本信息登记 | 高 |
| 物资属性 | 物资属性设置（规格、单位等） | 高 |
| 物资价格 | 物资价格信息维护 | 高 |
| 物资编码 | 物资编码规则设置 | 高 |
| 物资查询 | 物资信息查询 | 高 |

#### 2.2.2 物资信息
```
物资信息 Material:
- materialId: string, 物资ID
- materialCode: string, 物资编码
- materialName: string, 物资名称
- materialCategory: string, 物资分类
- materialSpec: string, 规格
- materialUnit: string, 单位
- manufacturer: string, 生产厂家
- brand: string, 品牌
- purchasePrice: decimal, 进价
- retailPrice: decimal, 零售价
- minStock: decimal, 库存下限
- maxStock: decimal, 库存上限
- shelfLife: int, 有效期(月)
- storageCondition: string, 储存条件
- status: enum, 状态
```

### 2.3 库房管理

#### 2.3.1 库房设置
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 库房信息 | 库房基本信息维护 | 高 |
| 库房分类 | 库房类型设置 | 高 |
| 库位设置 | 库房库位管理 | 中 |
| 库房权限 | 库房操作权限设置 | 高 |

#### 2.3.2 库房类型
```
库房类型 WarehouseType:
- 总库: 全院物资总库
- 科室库: 科室二级库
- 药房库: 药品专用库
- 手术室库: 手术室专用库
- 急诊库: 急诊专用库
```

### 2.4 入库管理

#### 2.4.1 入库类型
```
入库类型 InboundType:
- PURCHASE: 采购入库
- RETURN: 退货入库
- TRANSFER: 调拨入库
- ADJUST: 盘盈入库
```

#### 2.4.2 入库功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 入库登记 | 物资入库登记 | 高 |
| 入库审核 | 入库单审核 | 高 |
| 入库确认 | 入库确认操作 | 高 |
| 入库打印 | 打印入库单 | 高 |
| 入库查询 | 入库记录查询 | 高 |

#### 2.4.3 入库流程
```
┌─────────────────────────────────────────────────────────────────┐
│                       物资入库流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 入库申请  │───>│ 入库审核  │───>│ 入库验收  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │               ▼                       │
│                         │         ┌──────────┐                │
│                         │         │ 入库登记  │                │
│                         │         └──────────┘                │
│                         │               │                       │
│                         ▼               ▼                       │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 库存更新  │<───│ 入库确认  │<───│ 分配库位  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.5 出库管理

#### 2.5.1 出库类型
```
出库类型 OutboundType:
- ISSUE: 领用出库
- TRANSFER: 调拨出库
- RETURN: 退库出库
- ADJUST: 盘亏出库
- DISCARD: 报损出库
```

#### 2.5.2 出库功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 出库申请 | 物资出库申请 | 高 |
| 出库审核 | 出库单审核 | 高 |
| 出库发放 | 物资出库发放 | 高 |
| 出库确认 | 出库确认操作 | 高 |
| 出库打印 | 打印出库单 | 高 |
| 出库查询 | 出库记录查询 | 高 |

### 2.6 物资申领

#### 2.6.1 申领功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 领料申请 | 科室领料申请 | 高 |
| 申请审批 | 领料申请审批 | 高 |
| 领料发放 | 物资发放处理 | 高 |
| 领料确认 | 科室确认接收 | 高 |
| 领料查询 | 领料记录查询 | 高 |

#### 2.6.2 申领流程
```
1. 科室提交领料申请
2. 仓库审核申请
3. 确认库存可用
4. 物资出库发放
5. 科室确认接收
6. 更新科室库存
```

### 2.7 库存盘点

#### 2.7.1 盘点功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 盘点计划 | 制定盘点计划 | 高 |
| 盘点任务 | 分配盘点任务 | 高 |
| 盘点录入 | 录入盘点数量 | 高 |
| 盘点核对 | 盘点结果核对 | 高 |
| 差异处理 | 盘盈盘亏处理 | 高 |
| 盘点报告 | 生成盘点报告 | 高 |

#### 2.7.2 盘点类型
```
盘点类型 InventoryCheckType:
- 定期盘点: 月度/季度/年度盘点
- 随机抽查: 不定期抽查盘点
- 全盘: 全库盘点
- 抽盘: 部分物资盘点
```

### 2.8 库存预警

#### 2.8.1 预警功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 库存下限预警 | 库存低于下限提醒 | 高 |
| 库存上限预警 | 库存超过上限提醒 | 中 |
| 效期预警 | 物资效期预警 | 高 |
| 周转预警 | 物资周转异常提醒 | 中 |

### 2.9 供应商管理

#### 2.9.1 供应商功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 供应商档案 | 供应商基本信息 | 高 |
| 供应商资质 | 供应商资质管理 | 高 |
| 合同管理 | 采购合同管理 | 中 |
| 供应商评估 | 供应商绩效评估 | 中 |

### 2.10 采购计划

#### 2.10.1 采购功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 采购申请 | 采购申请提交 | 高 |
| 采购审批 | 采购申请审批 | 高 |
| 采购订单 | 采购订单生成 | 高 |
| 订单跟踪 | 采购订单跟踪 | 中 |
| 采购统计 | 采购数据统计 | 中 |

### 2.11 报表统计

#### 2.11.1 统计报表
| 报表名称 | 描述 | 频率 |
|----------|------|------|
| 库存明细表 | 当前库存明细 | 实时 |
| 出入库统计 | 出入库汇总统计 | 月 |
| 科室消耗统计 | 科室物资消耗统计 | 月 |
| 盘点报表 | 盘点差异统计 | 盘点后 |
| 采购统计 | 采购汇总统计 | 月 |

---

## 3. 数据实体定义

### 3.1 核心实体

#### 3.1.1 物资信息 Material
```sql
CREATE TABLE material (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '物资ID',
    material_code       VARCHAR(30) NOT NULL UNIQUE COMMENT '物资编码',
    material_name       VARCHAR(100) NOT NULL COMMENT '物资名称',
    material_spec       VARCHAR(50) COMMENT '规格',
    material_unit       VARCHAR(20) COMMENT '单位',
    material_category   VARCHAR(20) COMMENT '分类',
    category_name       VARCHAR(50) COMMENT '分类名称',

    manufacturer        VARCHAR(100) COMMENT '生产厂家',
    brand               VARCHAR(50) COMMENT '品牌',
    origin              VARCHAR(50) COMMENT '产地',

    purchase_price      DECIMAL(10,4) COMMENT '进价',
    retail_price        DECIMAL(10,4) COMMENT '零售价',
    price_date          DATE COMMENT '调价日期',

    min_stock           DECIMAL(10,2) COMMENT '库存下限',
    max_stock           DECIMAL(10,2) COMMENT '库存上限',
    safety_stock        DECIMAL(10,2) COMMENT '安全库存',

    shelf_life          INT COMMENT '有效期(月)',
    storage_condition   VARCHAR(50) COMMENT '储存条件',

    is_medical          TINYINT COMMENT '是否医疗耗材',
    is_sterile          TINYINT COMMENT '是否无菌',
    is_reusable         TINYINT COMMENT '是否可复用',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_code (material_code),
    INDEX idx_category (material_category),
    INDEX idx_name (material_name)
);
```

#### 3.1.2 库房信息 Warehouse
```sql
CREATE TABLE warehouse (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '库房ID',
    warehouse_code      VARCHAR(20) NOT NULL UNIQUE COMMENT '库房编码',
    warehouse_name      VARCHAR(100) NOT NULL COMMENT '库房名称',
    warehouse_type      VARCHAR(20) COMMENT '库房类型',

    dept_id             VARCHAR(20) COMMENT '所属科室ID',
    dept_name           VARCHAR(100) COMMENT '科室名称',

    location            VARCHAR(100) COMMENT '位置',
    manager_id          VARCHAR(20) COMMENT '管理员ID',
    manager_name        VARCHAR(50) COMMENT '管理员姓名',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_type (warehouse_type),
    INDEX idx_dept (dept_id)
);
```

#### 3.1.3 物资库存 MaterialInventory
```sql
CREATE TABLE material_inventory (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '库存ID',
    material_id         VARCHAR(36) NOT NULL COMMENT '物资ID',
    material_code       VARCHAR(30) COMMENT '物资编码',
    material_name       VARCHAR(100) COMMENT '物资名称',
    material_spec       VARCHAR(50) COMMENT '规格',
    material_unit       VARCHAR(20) COMMENT '单位',

    warehouse_id        VARCHAR(36) NOT NULL COMMENT '库房ID',
    warehouse_name      VARCHAR(100) COMMENT '库房名称',

    batch_no            VARCHAR(50) COMMENT '批号',
    expiry_date         DATE COMMENT '有效期',

    quantity            DECIMAL(10,2) NOT NULL COMMENT '库存数量',
    locked_quantity     DECIMAL(10,2) COMMENT '锁定数量',
    available_quantity  DECIMAL(10,2) COMMENT '可用数量',

    location            VARCHAR(50) COMMENT '库位',

    purchase_price      DECIMAL(10,4) COMMENT '进价',
    retail_price        DECIMAL(10,4) COMMENT '零售价',

    supplier_id         VARCHAR(20) COMMENT '供应商ID',
    supplier_name       VARCHAR(100) COMMENT '供应商名称',

    inbound_time        DATETIME COMMENT '入库时间',

    status              VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_material_batch_warehouse (material_id, batch_no, warehouse_id),
    INDEX idx_material (material_id),
    INDEX idx_warehouse (warehouse_id),
    INDEX idx_expiry (expiry_date)
);
```

#### 3.1.4 入库记录 MaterialInbound
```sql
CREATE TABLE material_inbound (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '入库ID',
    inbound_no          VARCHAR(30) NOT NULL UNIQUE COMMENT '入库单号',
    inbound_type        VARCHAR(20) NOT NULL COMMENT '入库类型',

    warehouse_id        VARCHAR(36) NOT NULL COMMENT '库房ID',
    warehouse_name      VARCHAR(100) COMMENT '库房名称',

    supplier_id         VARCHAR(20) COMMENT '供应商ID',
    supplier_name       VARCHAR(100) COMMENT '供应商名称',

    inbound_date        DATE NOT NULL COMMENT '入库日期',
    inbound_time        DATETIME NOT NULL COMMENT '入库时间',

    total_quantity      DECIMAL(10,2) COMMENT '总数量',
    total_amount        DECIMAL(12,2) COMMENT '总金额',

    applicant_id        VARCHAR(20) COMMENT '申请人ID',
    applicant_name      VARCHAR(50) COMMENT '申请人姓名',
    apply_time          DATETIME COMMENT '申请时间',

    auditor_id          VARCHAR(20) COMMENT '审核人ID',
    auditor_name        VARCHAR(50) COMMENT '审核人姓名',
    audit_time          DATETIME COMMENT '审核时间',

    operator_id         VARCHAR(20) COMMENT '入库人ID',
    operator_name       VARCHAR(50) COMMENT '入库人姓名',

    remark              VARCHAR(200) COMMENT '备注',

    status              VARCHAR(20) NOT NULL DEFAULT '待审核' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_inbound_no (inbound_no),
    INDEX idx_warehouse (warehouse_id),
    INDEX idx_inbound_date (inbound_date)
);
```

#### 3.1.5 出库记录 MaterialOutbound
```sql
CREATE TABLE material_outbound (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '出库ID',
    outbound_no         VARCHAR(30) NOT NULL UNIQUE COMMENT '出库单号',
    outbound_type       VARCHAR(20) NOT NULL COMMENT '出库类型',

    warehouse_id        VARCHAR(36) NOT NULL COMMENT '出库库房ID',
    warehouse_name      VARCHAR(100) COMMENT '库房名称',
    target_warehouse_id VARCHAR(36) COMMENT '目标库房ID(调拨)',
    target_dept_id      VARCHAR(20) COMMENT '目标科室ID',

    outbound_date       DATE NOT NULL COMMENT '出库日期',
    outbound_time       DATETIME NOT NULL COMMENT '出库时间',

    total_quantity      DECIMAL(10,2) COMMENT '总数量',
    total_amount        DECIMAL(12,2) COMMENT '总金额',

    applicant_id        VARCHAR(20) COMMENT '申请人ID',
    applicant_name      VARCHAR(50) COMMENT '申请人姓名',
    apply_time          DATETIME COMMENT '申请时间',

    auditor_id          VARCHAR(20) COMMENT '审核人ID',
    auditor_name        VARCHAR(50) COMMENT '审核人姓名',
    audit_time          DATETIME COMMENT '审核时间',

    operator_id         VARCHAR(20) COMMENT '出库人ID',
    operator_name       VARCHAR(50) COMMENT '出库人姓名',

    receiver_id         VARCHAR(20) COMMENT '接收人ID',
    receiver_name       VARCHAR(50) COMMENT '接收人姓名',
    receive_time        DATETIME COMMENT '接收时间',

    remark              VARCHAR(200) COMMENT '备注',

    status              VARCHAR(20) NOT NULL DEFAULT '待审核' COMMENT '状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_outbound_no (outbound_no),
    INDEX idx_warehouse (warehouse_id),
    INDEX idx_outbound_date (outbound_date)
);
```

---

## 4. 业务流程

### 4.1 物资领用流程
```
┌─────────────────────────────────────────────────────────────────┐
│                       物资领用流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 科室申请  │───>│ 仓库审核  │───>│ 物资准备  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │               ▼                       │
│                         │         ┌──────────┐                │
│                         │         │ 库存扣减  │                │
│                         │         └──────────┘                │
│                         │               │                       │
│                         ▼               ▼                       │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 科室入库  │<───│ 科室接收  │<───│ 物资发放  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 5. 接口定义

### 5.1 物资查询接口

#### 5.1.1 物资库存查询
```
GET /api/material/inventory/query?materialId=MAT001

Response:
{
    "code": 0,
    "data": {
        "materialId": "MAT001",
        "materialName": "一次性注射器",
        "materialSpec": "5ml",
        "totalQuantity": 10000,
        "availableQuantity": 9500,
        "minStock": 500,
        "warehouses": [
            {
                "warehouseId": "WH001",
                "warehouseName": "总库",
                "quantity": 8000,
                "batches": [...]
            }
        ]
    }
}
```

### 5.2 领料申请接口

#### 5.2.1 提交领料申请
```
POST /api/material/issue/apply

Request:
{
    "warehouseId": "WH001",
    "deptId": "D001",
    "applicantId": "USR001",
    "items": [
        {
            "materialId": "MAT001",
            "materialName": "一次性注射器",
            "quantity": 500
        }
    ],
    "remark": "科室日常使用"
}

Response:
{
    "code": 0,
    "message": "申请成功",
    "data": {
        "applicationId": "APP202401150001",
        "applicationNo": "ML202401150001"
    }
}
```

---

## 6. 业务规则与约束

### 6.1 库存规则
| 规则编码 | 规则描述 |
|----------|----------|
| INV001 | 库存出库遵循先进先出原则 |
| INV002 | 库存不足时禁止出库 |
| INV003 | 盘点期间锁定库存操作 |
| INV004 | 过期物资禁止发放使用 |

### 6.2 审批规则
| 规则编码 | 规则描述 |
|----------|----------|
| APP001 | 大额出库需审批 |
| APP002 | 调拨出库需审批 |
| APP003 | 报损出库需审批 |

---

## 7. 模块交互关系

### 7.1 上游依赖
- **系统管理模块**: 用户认证、科室信息
- **财务收费模块**: 物资收费

### 7.2 下游调用
- **药房管理模块**: 药品库存

---

## 8. 性能与安全要求

### 8.1 性能要求
| 指标 | 要求 |
|------|------|
| 库存查询时间 | < 1秒 |
| 出入库处理时间 | < 3秒 |

### 8.2 安全要求
- 库存操作审计日志
- 库存数据备份机制
- 库存权限严格控制
# 药房管理模块接口文档

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06  
> **基础路径**: /api/pharmacy/v1

---

## 1. 药品信息接口

### 1.1 获取药品列表

**接口**: `GET /drugs`

**描述**: 获取药品列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |
| keyword | String | 否 | 关键词(名称/编码/拼音码) |
| drugCategory | String | 否 | 药品分类 |
| status | String | 否 | 状态 |

**响应示例**:
```json
{
  "code": 0,
  "data": {
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
        "stockQuantity": 1000,
        "status": "正常"
      }
    ],
    "total": 500,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

---

### 1.2 获取药品详情

**接口**: `GET /drugs/{id}`

**描述**: 获取药品详情

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "drugId": "DRUG001",
    "drugCode": "XL001",
    "drugName": "阿莫西林胶囊",
    "genericName": "阿莫西林",
    "tradeName": "阿莫西林",
    "pinyinCode": "AMXLJN",
    "drugCategory": "西药",
    "drugForm": "胶囊剂",
    "drugSpec": "0.5g*24粒",
    "drugUnit": "粒",
    "packageUnit": "盒",
    "packageQuantity": 24,
    "manufacturer": "XX制药有限公司",
    "origin": "北京",
    "approvalNo": "国药准字H12345678",
    "purchasePrice": 18.00,
    "retailPrice": 25.00,
    "isPrescription": true,
    "isOtc": false,
    "isEssential": true,
    "isInsurance": true,
    "insuranceCode": "YB001",
    "insuranceType": "甲类",
    "storageCondition": "阴凉干燥处保存",
    "shelfLife": 24,
    "minStock": 100,
    "maxStock": 1000,
    "status": "正常"
  }
}
```

---

### 1.3 创建药品

**接口**: `POST /drugs`

**描述**: 创建药品信息

**请求参数**:
```json
{
  "drugCode": "XL002",
  "drugName": "布洛芬缓释胶囊",
  "genericName": "布洛芬",
  "drugCategory": "西药",
  "drugForm": "胶囊剂",
  "drugSpec": "0.3g*20粒",
  "drugUnit": "粒",
  "packageUnit": "盒",
  "packageQuantity": 20,
  "manufacturer": "XX制药",
  "purchasePrice": 15.00,
  "retailPrice": 22.00,
  "isPrescription": false,
  "isOtc": true,
  "isEssential": true,
  "isInsurance": true,
  "insuranceType": "乙类",
  "shelfLife": 24
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "创建成功",
  "data": {
    "drugId": "DRUG002"
  }
}
```

---

## 2. 库存管理接口

### 2.1 查询库存

**接口**: `GET /inventory/query`

**描述**: 查询药品库存

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| drugId | String | 是 | 药品ID |
| pharmacyId | String | 否 | 药房ID |

**响应示例**:
```json
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
        "batchNo": "20260101",
        "quantity": 500,
        "expiryDate": "2027-12-31",
        "location": "A-01-01"
      },
      {
        "batchNo": "20260201",
        "quantity": 500,
        "expiryDate": "2028-01-31",
        "location": "A-01-02"
      }
    ]
  }
}
```

---

### 2.2 效期预警查询

**接口**: `GET /inventory/expiry`

**描述**: 获取效期预警药品

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| days | Integer | 否 | 预警天数，默认180 |
| pharmacyId | String | 否 | 药房ID |

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "drugId": "DRUG001",
      "drugName": "阿莫西林胶囊",
      "drugSpec": "0.5g*24粒",
      "batchNo": "20230101",
      "quantity": 100,
      "expiryDate": "2026-05-30",
      "daysRemaining": 54,
      "alertLevel": "红色",
      "pharmacyName": "门诊药房"
    }
  ]
}
```

---

### 2.3 库存预警查询

**接口**: `GET /inventory/alert`

**描述**: 获取库存预警药品

**响应示例**:
```json
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

### 2.4 库存盘点

**接口**: `POST /inventory/check`

**描述**: 库存盘点录入

**请求参数**:
```json
{
  "pharmacyId": "PH001",
  "checkDate": "2026-04-06",
  "items": [
    {
      "drugId": "DRUG001",
      "batchNo": "20260101",
      "systemQuantity": 500,
      "actualQuantity": 498,
      "difference": -2,
      "differenceReason": "破损"
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "盘点录入成功",
  "data": {
    "checkId": "CHK202604060001"
  }
}
```

---

## 3. 发药管理接口

### 3.1 获取待发药处方

**接口**: `GET /dispense/pending`

**描述**: 获取待发药处方列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| pharmacyId | String | 是 | 药房ID |
| visitType | String | 否 | 就诊类型(门诊/住院) |
| status | String | 否 | 状态 |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "dispenseId": "DIS001",
        "dispenseNo": "FY202604060001",
        "prescriptionNo": "RX2026040600001",
        "patientId": "P001",
        "patientName": "张三",
        "gender": "男",
        "age": 35,
        "visitType": "门诊",
        "deptName": "内科",
        "doctorName": "李医生",
        "diagnosisName": "急性上呼吸道感染",
        "totalAmount": 58.50,
        "auditStatus": "待审核",
        "dispenseStatus": "待发药",
        "createTime": "2026-04-06T10:30:00"
      }
    ],
    "total": 15
  }
}
```

---

### 3.2 获取发药详情

**接口**: `GET /dispense/{id}`

**描述**: 获取发药详情

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "dispenseId": "DIS001",
    "dispenseNo": "FY202604060001",
    "prescriptionNo": "RX2026040600001",
    "patientId": "P001",
    "patientName": "张三",
    "gender": "男",
    "age": 35,
    "allergyInfo": "青霉素过敏",
    "visitType": "门诊",
    "deptName": "内科",
    "doctorName": "李医生",
    "diagnosisName": "急性上呼吸道感染",
    "totalAmount": 58.50,
    "auditStatus": "待审核",
    "details": [
      {
        "detailId": "DET001",
        "drugId": "DRUG001",
        "drugName": "布洛芬缓释胶囊",
        "drugSpec": "0.3g*20粒",
        "drugUnit": "盒",
        "quantity": 1,
        "retailPrice": 22.00,
        "amount": 22.00,
        "dosage": "每次1粒",
        "frequency": "每日2次",
        "days": 5,
        "route": "口服"
      },
      {
        "detailId": "DET002",
        "drugId": "DRUG002",
        "drugName": "阿莫西林胶囊",
        "drugSpec": "0.5g*24粒",
        "drugUnit": "盒",
        "quantity": 2,
        "retailPrice": 25.00,
        "amount": 50.00,
        "dosage": "每次2粒",
        "frequency": "每日3次",
        "days": 5,
        "route": "口服",
        "skinTest": "需皮试"
      }
    ]
  }
}
```

---

### 3.3 处方审核

**接口**: `POST /dispense/{id}/audit`

**描述**: 药师审核处方

**请求参数**:
```json
{
  "auditorId": "PHA001",
  "auditResult": "通过",
  "auditRemark": "",
  "details": [
    {
      "detailId": "DET001",
      "drugId": "DRUG001",
      "auditResult": "通过"
    },
    {
      "detailId": "DET002",
      "drugId": "DRUG002",
      "auditResult": "通过",
      "auditRemark": "已确认皮试阴性"
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "审核完成",
  "data": {
    "warnings": [],
    "errors": []
  }
}
```

---

### 3.4 发药确认

**接口**: `POST /dispense/{id}/confirm`

**描述**: 发药确认

**请求参数**:
```json
{
  "dispenserId": "PHA001",
  "details": [
    {
      "detailId": "DET001",
      "drugId": "DRUG001",
      "batchNo": "20260101",
      "quantity": 1
    },
    {
      "detailId": "DET002",
      "drugId": "DRUG002",
      "batchNo": "20260201",
      "quantity": 2
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "发药成功"
}
```

---

### 3.5 退药处理

**接口**: `POST /dispense/{id}/return`

**描述**: 退药处理

**请求参数**:
```json
{
  "returnReason": "患者用药后出现不良反应",
  "details": [
    {
      "detailId": "DET001",
      "drugId": "DRUG001",
      "quantity": 1,
      "batchNo": "20260101"
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "退药处理成功"
}
```

---

## 4. 采购管理接口

### 4.1 获取采购订单列表

**接口**: `GET /purchase/orders`

**描述**: 获取采购订单列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |
| supplierId | String | 否 | 供应商ID |
| status | String | 否 | 状态 |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "orderId": "PO001",
        "orderNo": "PO202604060001",
        "supplierName": "XX医药公司",
        "orderDate": "2026-04-06",
        "expectedDate": "2026-04-10",
        "totalQuantity": 1000,
        "totalAmount": 25000.00,
        "status": "已审核",
        "applicantName": "张采购"
      }
    ],
    "total": 20
  }
}
```

---

### 4.2 创建采购订单

**接口**: `POST /purchase/orders`

**描述**: 创建采购订单

**请求参数**:
```json
{
  "supplierId": "SUP001",
  "expectedDate": "2026-04-10",
  "items": [
    {
      "drugId": "DRUG001",
      "drugName": "阿莫西林胶囊",
      "drugSpec": "0.5g*24粒",
      "quantity": 500,
      "purchasePrice": 18.00
    },
    {
      "drugId": "DRUG002",
      "drugName": "布洛芬缓释胶囊",
      "drugSpec": "0.3g*20粒",
      "quantity": 500,
      "purchasePrice": 15.00
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "采购订单创建成功",
  "data": {
    "orderId": "PO202604060001",
    "orderNo": "PO202604060001"
  }
}
```

---

### 4.3 入库验收

**接口**: `POST /purchase/orders/{id}/receive`

**描述**: 采购入库验收

**请求参数**:
```json
{
  "pharmacyId": "PH001",
  "items": [
    {
      "drugId": "DRUG001",
      "batchNo": "20260401",
      "productionDate": "2026-04-01",
      "expiryDate": "2028-04-01",
      "quantity": 500,
      "purchasePrice": 18.00,
      "retailPrice": 25.00,
      "location": "A-01-01"
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "入库验收成功"
}
```

---

## 5. 供应商管理接口

### 5.1 获取供应商列表

**接口**: `GET /suppliers`

**描述**: 获取供应商列表

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "supplierId": "SUP001",
      "supplierCode": "GYS001",
      "supplierName": "XX医药有限公司",
      "contactPerson": "张经理",
      "contactPhone": "13800138000",
      "address": "北京市朝阳区XX路XX号",
      "status": "正常"
    }
  ]
}
```

---

## 6. 报表统计接口

### 6.1 药品销售统计

**接口**: `GET /reports/sales`

**描述**: 药品销售统计

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| startDate | String | 是 | 开始日期 |
| endDate | String | 是 | 结束日期 |
| pharmacyId | String | 否 | 药房ID |
| drugCategory | String | 否 | 药品分类 |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "totalAmount": 150000.00,
    "totalQuantity": 5000,
    "byCategory": [
      {"category": "西药", "amount": 100000.00, "quantity": 3000},
      {"category": "中成药", "amount": 50000.00, "quantity": 2000}
    ],
    "topDrugs": [
      {"drugName": "阿莫西林胶囊", "quantity": 500, "amount": 12500.00},
      {"drugName": "布洛芬缓释胶囊", "quantity": 400, "amount": 8800.00}
    ]
  }
}
```

---

**文档维护**: HIS Platform Team
# 住院管理模块接口文档

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06  
> **基础路径**: /api/inpatient/v1

---

## 1. 入院管理接口

### 1.1 入院登记

**接口**: `POST /admissions`

**描述**: 办理入院登记

**请求参数**:
```json
{
  "patientId": "P12345678",
  "patientName": "张三",
  "idCardNo": "110101199001011234",
  "gender": "男",
  "age": 35,
  "phone": "13800138000",
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
  "contactPhone": "13900139000"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "入院登记成功",
  "data": {
    "admissionId": "IP202604060001",
    "admissionNo": "ZY202604060001",
    "bedNo": "101-1",
    "admissionTime": "2026-04-06T10:30:00"
  }
}
```

---

### 1.2 获取住院列表

**接口**: `GET /admissions`

**描述**: 获取住院患者列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |
| patientId | String | 否 | 患者ID |
| patientName | String | 否 | 患者姓名 |
| deptId | String | 否 | 科室ID |
| wardId | String | 否 | 病区ID |
| status | String | 否 | 状态(在院/已出院) |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "admissionId": "IP001",
        "admissionNo": "ZY202604060001",
        "patientId": "P001",
        "patientName": "张三",
        "gender": "男",
        "age": 35,
        "deptName": "内科",
        "wardName": "内科一病区",
        "bedNo": "101-1",
        "doctorName": "李医生",
        "nurseName": "王护士",
        "admissionTime": "2026-04-06T10:30:00",
        "admissionDiagnosis": "肺炎",
        "nursingLevel": "二级",
        "deposit": 5000.00,
        "totalCost": 2500.00,
        "status": "在院"
      }
    ],
    "total": 50,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

---

### 1.3 获取住院详情

**接口**: `GET /admissions/{id}`

**描述**: 获取住院患者详情

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "admissionId": "IP001",
    "admissionNo": "ZY202604060001",
    "patientId": "P001",
    "patientName": "张三",
    "gender": "男",
    "age": 35,
    "phone": "13800138000",
    "deptId": "D001",
    "deptName": "内科",
    "wardId": "W001",
    "wardName": "内科一病区",
    "bedNo": "101-1",
    "doctorId": "DOC001",
    "doctorName": "李医生",
    "nurseId": "NUR001",
    "nurseName": "王护士",
    "admissionTime": "2026-04-06T10:30:00",
    "admissionType": "门诊",
    "admissionDiagnosis": "肺炎",
    "admissionDiagnosisCode": "J18.900",
    "nursingLevel": "二级",
    "dietType": "普食",
    "allergyInfo": "青霉素过敏",
    "insuranceType": "城镇职工医保",
    "deposit": 5000.00,
    "totalCost": 2500.00,
    "balance": 2500.00,
    "status": "在院"
  }
}
```

---

## 2. 床位管理接口

### 2.1 获取床位列表

**接口**: `GET /beds`

**描述**: 获取床位列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| wardId | String | 是 | 病区ID |
| status | String | 否 | 状态(空床/占用/维修) |

**响应示例**:
```json
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
      },
      {
        "bedId": "BED002",
        "bedNo": "101-2",
        "roomNo": "101",
        "bedType": "普通",
        "dailyRate": 50.00,
        "status": "占用",
        "patientId": "P001",
        "patientName": "张三"
      }
    ]
  }
}
```

---

### 2.2 分配床位

**接口**: `POST /beds/assign`

**描述**: 分配床位

**请求参数**:
```json
{
  "admissionId": "IP202604060001",
  "wardId": "W001",
  "bedNo": "101-1"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "床位分配成功"
}
```

---

### 2.3 调换床位

**接口**: `POST /beds/transfer`

**描述**: 调换床位

**请求参数**:
```json
{
  "admissionId": "IP202604060001",
  "newBedNo": "102-1",
  "transferReason": "患者要求"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "床位调换成功"
}
```

---

## 3. 医嘱管理接口

### 3.1 开立医嘱

**接口**: `POST /orders`

**描述**: 开立医嘱

**请求参数**:
```json
{
  "admissionId": "IP202604060001",
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
  "startTime": "2026-04-06T10:00:00",
  "executeTime": "每日上午",
  "frequency": "QD"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "医嘱开立成功",
  "data": {
    "orderId": "ORD202604060001",
    "orderNo": "MO202604060001"
  }
}
```

---

### 3.2 获取医嘱列表

**接口**: `GET /orders`

**描述**: 获取患者医嘱列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| admissionId | String | 是 | 住院ID |
| orderType | String | 否 | 医嘱类型(长期/临时) |
| status | String | 否 | 状态 |

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "orderId": "ORD001",
      "orderNo": "MO202604060001",
      "orderType": "长期",
      "orderCategory": "药品",
      "orderContent": "0.9%氯化钠注射液 250ml 静滴 每日一次",
      "doctorName": "李医生",
      "orderTime": "2026-04-06T10:00:00",
      "status": "执行中",
      "startTime": "2026-04-06T10:00:00"
    }
  ]
}
```

---

### 3.3 审核医嘱

**接口**: `POST /orders/{id}/audit`

**描述**: 护士审核医嘱

**请求参数**:
```json
{
  "nurseId": "NUR001",
  "auditResult": "通过",
  "auditRemark": ""
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "审核成功"
}
```

---

### 3.4 执行医嘱

**接口**: `POST /orders/{id}/execute`

**描述**: 执行医嘱

**请求参数**:
```json
{
  "admissionId": "IP202604060001",
  "executeTime": "2026-04-06T09:30:00",
  "executeNurseId": "NUR001",
  "executeResult": "已执行"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "执行记录成功"
}
```

---

### 3.5 停止医嘱

**接口**: `POST /orders/{id}/stop`

**描述**: 停止长期医嘱

**请求参数**:
```json
{
  "stopReason": "患者病情好转"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "医嘱已停止"
}
```

---

## 4. 护理管理接口

### 4.1 录入生命体征

**接口**: `POST /nursing/vital-signs`

**描述**: 录入生命体征

**请求参数**:
```json
{
  "admissionId": "IP202604060001",
  "patientId": "P12345678",
  "recordTime": "2026-04-06T08:00:00",
  "temperature": 36.5,
  "pulse": 76,
  "respiration": 18,
  "bloodPressureSystolic": 120,
  "bloodPressureDiastolic": 80,
  "spo2": 98,
  "weight": 65.0,
  "height": 175
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "录入成功"
}
```

---

### 4.2 获取生命体征记录

**接口**: `GET /nursing/vital-signs`

**描述**: 获取生命体征记录

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| admissionId | String | 是 | 住院ID |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "recordId": "NR001",
      "recordTime": "2026-04-06T08:00:00",
      "temperature": 36.5,
      "pulse": 76,
      "respiration": 18,
      "bloodPressure": "120/80",
      "spo2": 98,
      "nurseName": "王护士"
    }
  ]
}
```

---

### 4.3 护理评估

**接口**: `POST /nursing/assessment`

**描述**: 护理评估

**请求参数**:
```json
{
  "admissionId": "IP202604060001",
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
```

**响应示例**:
```json
{
  "code": 0,
  "message": "评估记录成功"
}
```

---

### 4.4 护理记录

**接口**: `POST /nursing/records`

**描述**: 书写护理记录

**请求参数**:
```json
{
  "admissionId": "IP202604060001",
  "patientId": "P12345678",
  "recordTime": "2026-04-06T10:00:00",
  "recordType": "护理记录",
  "nursingContent": "患者神志清，精神可，生命体征平稳",
  "nursingMeasures": "协助翻身，观察病情变化",
  "intake": 500,
  "output": 300,
  "urine": 250
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "记录保存成功",
  "data": {
    "recordId": "NR202604060001"
  }
}
```

---

## 5. 转科管理接口

### 5.1 转科申请

**接口**: `POST /transfers`

**描述**: 发起转科申请

**请求参数**:
```json
{
  "admissionId": "IP202604060001",
  "targetDeptId": "D002",
  "transferReason": "专科治疗需要"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "转科申请已提交"
}
```

---

### 5.2 转科审核

**接口**: `POST /transfers/{id}/audit`

**描述**: 接收科室审核

**请求参数**:
```json
{
  "auditResult": "同意",
  "targetWardId": "W002",
  "targetBedNo": "201-1"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "审核完成"
}
```

---

## 6. 出院管理接口

### 6.1 出院申请

**接口**: `POST /discharge/apply`

**描述**: 申请出院

**请求参数**:
```json
{
  "admissionId": "IP202604060001",
  "dischargeType": "好转",
  "dischargeDiagnosis": "肺炎",
  "dischargeDiagnosisCode": "J18.900",
  "dischargeAdvice": "注意休息，按时服药，一周后复诊",
  "followUpDate": "2026-04-13"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "出院申请成功"
}
```

---

### 6.2 获取出院小结

**接口**: `GET /discharge/summary/{admissionId}`

**描述**: 获取出院小结

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "admissionId": "IP001",
    "patientName": "张三",
    "admissionNo": "ZY202604060001",
    "deptName": "内科",
    "doctorName": "李医生",
    "admissionTime": "2026-04-06T10:30:00",
    "dischargeTime": "2026-04-13T10:00:00",
    "totalDays": 7,
    "admissionDiagnosis": "肺炎",
    "dischargeDiagnosis": "肺炎",
    "treatmentSummary": "入院后完善相关检查，给予抗感染治疗...",
    "dischargeAdvice": "注意休息，按时服药，一周后复诊",
    "followUpDate": "2026-04-20"
  }
}
```

---

### 6.3 出院结算

**接口**: `POST /discharge/settle`

**描述**: 出院费用结算

**请求参数**:
```json
{
  "admissionId": "IP202604060001",
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
```

**响应示例**:
```json
{
  "code": 0,
  "message": "结算成功",
  "data": {
    "settleId": "SET202604060001",
    "invoiceNo": "INV202604060001",
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

## 7. 费用管理接口

### 7.1 获取费用明细

**接口**: `GET /fees`

**描述**: 获取住院费用明细

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| admissionId | String | 是 | 住院ID |
| feeDate | String | 否 | 费用日期 |
| feeCategory | String | 否 | 费用分类 |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "totalCost": 2500.00,
    "deposit": 5000.00,
    "balance": 2500.00,
    "categories": [
      {"name": "床位费", "amount": 350.00},
      {"name": "药品费", "amount": 1200.00},
      {"name": "检查费", "amount": 500.00},
      {"name": "治疗费", "amount": 450.00}
    ],
    "details": [
      {
        "feeDate": "2026-04-06",
        "feeCategory": "床位费",
        "feeItemName": "床位费(普通)",
        "feeQuantity": 1,
        "feePrice": 50.00,
        "feeAmount": 50.00
      }
    ]
  }
}
```

---

### 7.2 预交金缴纳

**接口**: `POST /deposits`

**描述**: 缴纳预交金

**请求参数**:
```json
{
  "admissionId": "IP202604060001",
  "amount": 3000.00,
  "payMethod": "WECHAT",
  "transactionId": "wx123456"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "缴纳成功",
  "data": {
    "depositId": "DEP001",
    "totalDeposit": 8000.00
  }
}
```

---

**文档维护**: HIS Platform Team
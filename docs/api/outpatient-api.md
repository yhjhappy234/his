# 门诊管理模块接口文档

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06  
> **基础路径**: /api/outpatient/v1

---

## 1. 排班管理接口

### 1.1 获取排班列表

**接口**: `GET /schedules`

**描述**: 获取医生排班列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| deptId | String | 否 | 科室ID |
| doctorId | String | 否 | 医生ID |
| startDate | String | 是 | 开始日期 |
| endDate | String | 是 | 结束日期 |

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "scheduleId": "SCH001",
      "doctorId": "DOC001",
      "doctorName": "张医生",
      "doctorTitle": "主任医师",
      "deptId": "D001",
      "deptName": "内科",
      "scheduleDate": "2026-04-06",
      "timePeriod": "上午",
      "totalQuota": 30,
      "bookedQuota": 25,
      "availableQuota": 5,
      "registrationFee": 50.00,
      "diagnosisFee": 20.00,
      "status": "正常"
    }
  ]
}
```

---

### 1.2 创建排班

**接口**: `POST /schedules`

**描述**: 创建医生排班

**请求参数**:
```json
{
  "deptId": "D001",
  "doctorId": "DOC001",
  "scheduleDate": "2026-04-07",
  "timePeriod": "上午",
  "totalQuota": 30,
  "registrationFee": 50.00,
  "diagnosisFee": 20.00,
  "clinicRoom": "101"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "排班创建成功",
  "data": {
    "scheduleId": "SCH002"
  }
}
```

---

### 1.3 停诊处理

**接口**: `PUT /schedules/{id}/stop`

**描述**: 医生停诊

**请求参数**:
```json
{
  "stopReason": "临时有事"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "停诊处理成功"
}
```

---

## 2. 预约挂号接口

### 2.1 创建预约

**接口**: `POST /appointments`

**描述**: 创建预约挂号

**请求参数**:
```json
{
  "patientId": "P12345678",
  "deptId": "D001",
  "doctorId": "DOC001",
  "scheduleDate": "2026-04-07",
  "timePeriod": "上午",
  "registrationType": "专家",
  "source": "APP",
  "remark": "复诊"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "预约成功",
  "data": {
    "appointmentId": "APT202604070001",
    "queueNo": 15,
    "scheduleTime": "09:30-10:00",
    "registrationFee": 50.00,
    "diagnosisFee": 20.00,
    "totalFee": 70.00
  }
}
```

---

### 2.2 取消预约

**接口**: `PUT /appointments/{id}/cancel`

**描述**: 取消预约

**请求参数**:
```json
{
  "cancelReason": "临时有事"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "取消成功"
}
```

---

### 2.3 获取预约列表

**接口**: `GET /appointments`

**描述**: 获取预约列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| patientId | String | 否 | 患者ID |
| scheduleDate | String | 否 | 预约日期 |
| status | String | 否 | 状态 |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "appointmentId": "APT001",
        "patientId": "P001",
        "patientName": "张三",
        "deptName": "内科",
        "doctorName": "李医生",
        "scheduleDate": "2026-04-07",
        "timePeriod": "上午",
        "queueNo": 15,
        "status": "已预约"
      }
    ],
    "total": 10
  }
}
```

---

## 3. 挂号管理接口

### 3.1 现场挂号

**接口**: `POST /registrations`

**描述**: 现场挂号

**请求参数**:
```json
{
  "patientId": "P12345678",
  "patientName": "张三",
  "idCardNo": "110101199001011234",
  "gender": "男",
  "age": 35,
  "phone": "13800138000",
  "deptId": "D001",
  "doctorId": "DOC001",
  "scheduleId": "SCH001",
  "scheduleDate": "2026-04-06",
  "timePeriod": "上午",
  "registrationType": "专家",
  "source": "现场"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "挂号成功",
  "data": {
    "registrationId": "REG202604060001",
    "visitNo": "MZ202604060001",
    "queueNo": 26,
    "registrationFee": 50.00,
    "diagnosisFee": 20.00,
    "totalFee": 70.00
  }
}
```

---

### 3.2 获取挂号列表

**接口**: `GET /registrations`

**描述**: 获取挂号列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |
| patientId | String | 否 | 患者ID |
| scheduleDate | String | 否 | 就诊日期 |
| deptId | String | 否 | 科室ID |
| status | String | 否 | 状态 |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "registrationId": "REG001",
        "regNo": "MZ202604060001",
        "patientId": "P001",
        "patientName": "张三",
        "gender": "男",
        "age": 35,
        "deptName": "内科",
        "doctorName": "李医生",
        "scheduleDate": "2026-04-06",
        "timePeriod": "上午",
        "queueNo": 26,
        "status": "已挂号",
        "visitStatus": "候诊中"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

---

### 3.3 退号

**接口**: `PUT /registrations/{id}/cancel`

**描述**: 退号处理

**响应示例**:
```json
{
  "code": 0,
  "message": "退号成功"
}
```

---

## 4. 分诊排队接口

### 4.1 签到

**接口**: `POST /checkin`

**描述**: 患者签到

**请求参数**:
```json
{
  "registrationId": "REG202604060001",
  "patientId": "P12345678"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "签到成功",
  "data": {
    "queueNo": 26,
    "waitCount": 5,
    "estimatedWaitTime": 25,
    "clinicRoom": "101"
  }
}
```

---

### 4.2 获取排队信息

**接口**: `GET /queue`

**描述**: 获取诊室排队信息

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| clinicRoom | String | 是 | 诊室号 |
| deptId | String | 否 | 科室ID |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "currentNo": 25,
    "currentPatient": "张*明",
    "doctorName": "李医生",
    "waitingList": [
      {"queueNo": 26, "patientName": "李*华", "status": "等候中"},
      {"queueNo": 27, "patientName": "王*伟", "status": "等候中"}
    ],
    "passedList": [
      {"queueNo": 20, "patientName": "赵*强", "passTime": "09:15"}
    ]
  }
}
```

---

## 5. 医生工作站接口

### 5.1 获取待诊患者列表

**接口**: `GET /doctor/pending`

**描述**: 获取医生待诊患者列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| doctorId | String | 是 | 医生ID |
| date | String | 是 | 日期 |

**响应示例**:
```json
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
        "lastVisitDate": "2026-04-01"
      }
    ]
  }
}
```

---

### 5.2 叫号

**接口**: `POST /doctor/call`

**描述**: 医生叫号

**请求参数**:
```json
{
  "registrationId": "REG001",
  "doctorId": "DOC001",
  "clinicRoom": "101"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "叫号成功"
}
```

---

### 5.3 开始就诊

**接口**: `POST /doctor/start-visit`

**描述**: 开始就诊

**请求参数**:
```json
{
  "registrationId": "REG001"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "开始就诊"
}
```

---

### 5.4 结束就诊

**接口**: `POST /doctor/end-visit`

**描述**: 结束就诊

**请求参数**:
```json
{
  "registrationId": "REG001"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "就诊结束"
}
```

---

## 6. 病历接口

### 6.1 保存病历

**接口**: `POST /records`

**描述**: 保存门诊病历

**请求参数**:
```json
{
  "registrationId": "REG001",
  "patientId": "P001",
  "deptId": "D001",
  "doctorId": "DOC001",
  "visitDate": "2026-04-06",
  "chiefComplaint": "头痛、发热3天",
  "presentIllness": "患者3天前无明显诱因出现头痛...",
  "pastHistory": "高血压病史5年",
  "allergyHistory": "青霉素过敏",
  "temperature": 38.5,
  "pulse": 88,
  "respiration": 18,
  "bloodPressure": "120/80",
  "physicalExam": "咽部充血，扁桃体I度肿大",
  "diagnosisCode": "J06.900",
  "diagnosisName": "急性上呼吸道感染",
  "treatmentPlan": "1. 休息，多饮水\n2. 对症治疗",
  "medicalAdvice": "注意休息，多饮水，不适随诊"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "保存成功",
  "data": {
    "recordId": "REC202604060001"
  }
}
```

---

### 6.2 获取病历

**接口**: `GET /records/{registrationId}`

**描述**: 获取门诊病历

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "recordId": "REC001",
    "registrationId": "REG001",
    "patientId": "P001",
    "patientName": "张三",
    "deptName": "内科",
    "doctorName": "李医生",
    "visitDate": "2026-04-06",
    "chiefComplaint": "头痛、发热3天",
    "presentIllness": "患者3天前无明显诱因出现头痛...",
    "diagnosisName": "急性上呼吸道感染",
    "status": "已提交"
  }
}
```

---

### 6.3 获取患者历史病历

**接口**: `GET /records/history`

**描述**: 获取患者历史病历

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| patientId | String | 是 | 患者ID |
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "recordId": "REC001",
        "visitDate": "2026-04-06",
        "deptName": "内科",
        "doctorName": "李医生",
        "diagnosisName": "急性上呼吸道感染"
      }
    ],
    "total": 5
  }
}
```

---

## 7. 处方接口

### 7.1 开立处方

**接口**: `POST /prescriptions`

**描述**: 开立门诊处方

**请求参数**:
```json
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
```

**响应示例**:
```json
{
  "code": 0,
  "message": "处方开立成功",
  "data": {
    "prescriptionId": "PRE202604060001",
    "prescriptionNo": "RX2026040600001",
    "totalAmount": 58.50,
    "warnings": [
      "药品[阿莫西林胶囊]需皮试"
    ]
  }
}
```

---

### 7.2 获取处方列表

**接口**: `GET /prescriptions`

**描述**: 获取处方列表

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| registrationId | String | 否 | 挂号ID |
| patientId | String | 否 | 患者ID |
| status | String | 否 | 状态 |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "prescriptionId": "PRE001",
        "prescriptionNo": "RX2026040600001",
        "prescriptionType": "西药",
        "totalAmount": 58.50,
        "payStatus": "未收费",
        "status": "有效",
        "createTime": "2026-04-06T10:30:00"
      }
    ],
    "total": 1
  }
}
```

---

### 7.3 作废处方

**接口**: `PUT /prescriptions/{id}/cancel`

**描述**: 作废处方

**请求参数**:
```json
{
  "cancelReason": "患者要求修改"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "处方已作废"
}
```

---

## 8. 收费接口

### 8.1 获取待收费项目

**接口**: `GET /billing/pending`

**描述**: 获取待收费项目

**请求参数**:
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| registrationId | String | 是 | 挂号ID |

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "patientInfo": {
      "patientId": "P001",
      "patientName": "张三",
      "gender": "男",
      "age": 35
    },
    "items": [
      {
        "itemId": "ITEM001",
        "itemType": "挂号费",
        "description": "专家挂号",
        "amount": 70.00
      },
      {
        "itemId": "ITEM002",
        "itemType": "处方",
        "itemNo": "RX2026040600001",
        "description": "布洛芬缓释胶囊等2种药品",
        "amount": 58.50
      }
    ],
    "totalAmount": 128.50
  }
}
```

---

### 8.2 结算收费

**接口**: `POST /billing/settle`

**描述**: 结算收费

**请求参数**:
```json
{
  "registrationId": "REG001",
  "patientId": "P001",
  "itemIds": ["ITEM001", "ITEM002"],
  "payments": [
    {
      "payMethod": "MEDICAL_INSURANCE",
      "amount": 80.00,
      "insuranceCard": "1234567890"
    },
    {
      "payMethod": "WECHAT",
      "amount": 48.50,
      "transactionId": "wx123456"
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "收费成功",
  "data": {
    "billId": "BILL202604060001",
    "invoiceNo": "INV202604060001",
    "totalAmount": 128.50,
    "insuranceAmount": 80.00,
    "selfPayAmount": 48.50,
    "invoiceUrl": "/api/outpatient/v1/billing/invoice/INV202604060001"
  }
}
```

---

### 8.3 获取发票

**接口**: `GET /billing/invoice/{invoiceNo}`

**描述**: 获取发票信息

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "invoiceNo": "INV202604060001",
    "patientName": "张三",
    "totalAmount": 128.50,
    "items": [
      {"name": "专家挂号费", "amount": 70.00},
      {"name": "药品费", "amount": 58.50}
    ],
    "insuranceAmount": 80.00,
    "selfPayAmount": 48.50,
    "printTime": "2026-04-06T10:45:00"
  }
}
```

---

**文档维护**: HIS Platform Team
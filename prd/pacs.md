# 影像管理模块需求说明书

## 1. 模块概述与目标

### 1.1 模块定位
影像管理模块（PACS/RIS）是HIS系统的医技模块，负责医学影像的采集、存储、浏览、诊断和管理，支持放射、超声、内镜等多种影像类型。

### 1.2 业务目标
- 实现影像数字化存储与管理
- 提高影像诊断工作效率
- 实现影像信息共享
- 支持远程会诊功能
- 规范影像质量管理

### 1.3 用户角色
- 影像技师
- 影像诊断医生
- 临床医生
- 影像管理员

---

## 2. 功能清单

### 2.1 检查预约登记

#### 2.1.1 预约管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 检查申请接收 | 接收临床检查申请 | 高 |
| 预约安排 | 安排检查时间 | 高 |
| 预约查询 | 查询预约信息 | 高 |
| 预约修改 | 修改预约时间 | 高 |
| 预约取消 | 取消预约检查 | 高 |

#### 2.1.2 登记管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 检查登记 | 患者检查登记 | 高 |
| 信息核对 | 核对患者信息 | 高 |
| 检查编号 | 生成检查编号 | 高 |
| 打印检查单 | 打印检查信息单 | 高 |

### 2.2 检查项目管理

#### 2.2.1 检查项目设置
```
检查项目 ExamItem:
- itemId: string, 项目ID
- itemCode: string, 项目编码
- itemName: string, 项目名称
- examType: enum, 检查类型, [X线/CT/MRI/超声/内镜/核医学等]
- examPart: string, 检查部位
- examMethod: string, 检查方法
- price: decimal, 价格
- turnaroundTime: int, 报告时限(小时)
- equipmentType: string, 设备类型
- status: enum, 状态
```

### 2.3 影像采集管理

#### 2.3.1 采集功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 设备连接 | DICOM设备连接配置 | 高 |
| 影像接收 | 自动接收DICOM影像 | 高 |
| 影像采集 | 手动采集影像 | 高 |
| 影像上传 | 影像上传存储 | 高 |
| 影像预处理 | 影像预处理（标注等） | 中 |

#### 2.3.2 DICOM标准
```
DICOM标准支持:
- DICOM 3.0标准
- 存储服务(SCU/SCP)
- 查询检索服务(Q/R SCU/SCP)
- 工作列表服务(MWL SCP)
- 打印服务
- 影像压缩(JPEG/Lossless)

影像传输:
- 标准DICOM传输
- HTTP/Web传输
- 影像推送/拉取
```

### 2.4 影像存储管理

#### 2.4.1 存储功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 影像存储 | 影像文件存储 | 高 |
| 影像归档 | 影像长期归档 | 高 |
| 影像检索 | 影像快速检索 | 高 |
| 影像删除 | 影像删除管理 | 中 |
| 存储监控 | 存储空间监控 | 中 |

#### 2.4.2 存储架构
```
影像存储架构:
- 在线存储: 最近3个月影像，快速访问
- 近线存储: 3-12个月影像，一般访问
- 离线存储: 12个月以上影像，归档存储

存储格式:
- DICOM原始格式
- 压缩格式(JPEG2000/JPEG_LS)
- 浏览格式(JPEG/PNG)
```

### 2.5 影像浏览处理

#### 2.5.1 浏览功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 影像查看 | 查看DICOM影像 | 高 |
| 影像调阅 | 远程调阅历史影像 | 高 |
| 多序列显示 | 多序列同时显示 | 高 |
| 对比浏览 | 新旧影像对比浏览 | 高 |
| 三维重建 | 3D影像重建显示 | 中 |

#### 2.5.2 图像处理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 窗宽窗位调整 | 调整显示参数 | 高 |
| 放大缩小 | 影像缩放功能 | 高 |
| 测量工具 | 距离、面积测量 | 高 |
| ROI工具 | 感兴趣区域标注 | 高 |
| 窗格布局 | 自定义显示布局 | 中 |
| 图像标注 | 影像标注功能 | 中 |

### 2.6 诊断报告管理

#### 2.6.1 报告书写
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 报告模板 | 诊断报告模板管理 | 高 |
| 报告录入 | 影像诊断报告录入 | 高 |
| 影像引用 | 报告引用关键影像 | 高 |
| 结构化报告 | 结构化报告填写 | 高 |
| AI辅助诊断 | AI辅助诊断建议 | 中 |

#### 2.6.2 报告审核
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 报告审核 | 诊断报告审核 | 高 |
| 报告修改 | 报告内容修改 | 高 |
| 双签名 | 审核签名机制 | 高 |
| 报告历史 | 报告修改历史记录 | 高 |

#### 2.6.3 报告发布
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 报告发布 | 发布诊断报告 | 高 |
| 报告推送 | 推送报告到临床 | 高 |
| 报告打印 | 打印诊断报告 | 高 |
| 报告查询 | 报告历史查询 | 高 |

### 2.7 远程会诊

#### 2.7.1 会诊功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 会诊申请 | 申请远程影像会诊 | 中 |
| 影像传输 | 传输会诊影像 | 中 |
| 远程浏览 | 远程调阅影像 | 中 |
| 会诊意见 | 会诊意见录入 | 中 |
| 会诊报告 | 会诊报告生成 | 中 |

### 2.8 设备机房管理

#### 2.8.1 设备管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 设备信息 | 设备基本信息管理 | 高 |
| 设备状态 | 设备运行状态监控 | 高 |
| 设备排班 | 设备使用排班 | 中 |
| 设备维护 | 设备维护记录 | 中 |

#### 2.8.2 机房管理
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 机房排班 | 机房检查排班 | 高 |
| 工作量统计 | 机房工作量统计 | 高 |
| 设备利用率 | 设备利用率分析 | 中 |

### 2.9 报表统计

#### 2.9.1 统计报表
| 报表名称 | 描述 | 频率 |
|----------|------|------|
| 检查工作量统计 | 各类检查数量统计 | 日/月 |
| 设备使用统计 | 设备使用率统计 | 月 |
| 报告时限统计 | 报告完成时限统计 | 月 |
| 阳性率统计 | 检查阳性率统计 | 月 |

---

## 3. 数据实体定义

### 3.1 核心实体

#### 3.1.1 检查申请 ExamRequest
```sql
CREATE TABLE exam_request (
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
    doctor_id           VARCHAR(20) COMMENT '申请医生ID',
    doctor_name         VARCHAR(50) COMMENT '医生姓名',

    clinical_diagnosis  VARCHAR(200) COMMENT '临床诊断',
    clinical_info       TEXT COMMENT '临床信息',
    exam_purpose        VARCHAR(100) COMMENT '检查目的',

    item_id             VARCHAR(36) NOT NULL COMMENT '检查项目ID',
    item_code           VARCHAR(20) COMMENT '项目编码',
    item_name           VARCHAR(100) COMMENT '项目名称',
    exam_type           VARCHAR(20) COMMENT '检查类型',
    exam_part           VARCHAR(50) COMMENT '检查部位',
    exam_method         VARCHAR(50) COMMENT '检查方法',

    request_time        DATETIME NOT NULL COMMENT '申请时间',
    is_emergency        TINYINT COMMENT '是否急诊',
    emergency_level     VARCHAR(20) COMMENT '急诊级别',

    schedule_time       DATETIME COMMENT '预约时间',
    exam_time           DATETIME COMMENT '检查时间',
    report_time         DATETIME COMMENT '报告时间',

    status              VARCHAR(20) NOT NULL DEFAULT '申请' COMMENT '状态',

    total_amount        DECIMAL(10,2) COMMENT '费用',
    pay_status          VARCHAR(20) COMMENT '收费状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_patient (patient_id),
    INDEX idx_request_time (request_time),
    INDEX idx_status (status)
);
```

#### 3.1.2 检查记录 ExamRecord
```sql
CREATE TABLE exam_record (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '检查ID',
    exam_no             VARCHAR(30) NOT NULL UNIQUE COMMENT '检查编号',
    request_id          VARCHAR(36) NOT NULL COMMENT '申请ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    accession_no        VARCHAR(50) COMMENT 'DICOM检查号',
    study_id            VARCHAR(50) COMMENT 'Study ID',

    exam_type           VARCHAR(20) NOT NULL COMMENT '检查类型',
    exam_part           VARCHAR(50) COMMENT '检查部位',
    modality            VARCHAR(20) COMMENT '设备类型',

    equipment_id        VARCHAR(20) COMMENT '设备ID',
    equipment_name      VARCHAR(100) COMMENT '设备名称',
    room_no             VARCHAR(20) COMMENT '机房号',

    technician_id       VARCHAR(20) COMMENT '技师ID',
    technician_name     VARCHAR(50) COMMENT '技师姓名',
    exam_time           DATETIME NOT NULL COMMENT '检查时间',
    exam_duration       INT COMMENT '检查时长(分钟)',

    series_count        INT COMMENT '序列数量',
    image_count         INT COMMENT '影像数量',
    storage_path        VARCHAR(200) COMMENT '存储路径',

    contrast_agent      VARCHAR(50) COMMENT '造影剂',
    contrast_dose       DECIMAL(10,2) COMMENT '造影剂剂量',
    radiation_dose      DECIMAL(10,2) COMMENT '辐射剂量',

    exam_status         VARCHAR(20) NOT NULL DEFAULT '检查中' COMMENT '状态',
    report_status       VARCHAR(20) COMMENT '报告状态',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_request (request_id),
    INDEX idx_patient (patient_id),
    INDEX idx_exam_no (exam_no),
    INDEX idx_accession (accession_no)
);
```

#### 3.1.3 影像序列 ExamSeries
```sql
CREATE TABLE exam_series (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '序列ID',
    exam_id             VARCHAR(36) NOT NULL COMMENT '检查ID',
    series_no           VARCHAR(50) NOT NULL COMMENT '序列号(Series Number)',
    series_uid          VARCHAR(100) COMMENT 'Series UID',

    series_description  VARCHAR(100) COMMENT '序列描述',
    modality            VARCHAR(20) COMMENT '设备类型',
    body_part           VARCHAR(50) COMMENT '检查部位',

    image_count         INT COMMENT '影像数量',
    storage_path        VARCHAR(200) COMMENT '存储路径',

    scan_date           DATE COMMENT '扫描日期',
    scan_time           TIME COMMENT '扫描时间',

    kvp                 DECIMAL(10,2) COMMENT '管电压',
    mas                 DECIMAL(10,2) COMMENT '管电流',
    slice_thickness     DECIMAL(10,2) COMMENT '层厚',
    pixel_spacing       VARCHAR(50) COMMENT '像素间距',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_exam (exam_id),
    INDEX idx_series_uid (series_uid)
);
```

#### 3.1.4 影像文件 ExamImage
```sql
CREATE TABLE exam_image (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '影像ID',
    series_id           VARCHAR(36) NOT NULL COMMENT '序列ID',
    exam_id             VARCHAR(36) COMMENT '检查ID',

    image_no            INT COMMENT '影像编号',
    image_uid           VARCHAR(100) COMMENT 'Image UID',
    sop_uid             VARCHAR(100) COMMENT 'SOP Instance UID',

    image_path          VARCHAR(200) COMMENT '文件路径',
    thumbnail_path      VARCHAR(200) COMMENT '缩略图路径',

    image_width         INT COMMENT '影像宽度',
    image_height        INT COMMENT '影像高度',
    bits_allocated      INT COMMENT '分配位数',
    bits_stored         INT COMMENT '存储位数',

    window_center       DECIMAL(10,2) COMMENT '窗位',
    window_width        DECIMAL(10,2) COMMENT '窗宽',

    is_key_image        TINYINT COMMENT '是否关键影像',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_series (series_id),
    INDEX idx_exam (exam_id),
    INDEX idx_image_uid (image_uid)
);
```

#### 3.1.5 诊断报告 ExamReport
```sql
CREATE TABLE exam_report (
    id                  VARCHAR(36) PRIMARY KEY COMMENT '报告ID',
    report_no           VARCHAR(30) NOT NULL UNIQUE COMMENT '报告编号',
    exam_id             VARCHAR(36) NOT NULL COMMENT '检查ID',
    request_id          VARCHAR(36) COMMENT '申请ID',
    patient_id          VARCHAR(20) NOT NULL COMMENT '患者ID',

    report_type         VARCHAR(20) COMMENT '报告类型',
    report_template_id  VARCHAR(36) COMMENT '模板ID',

    exam_description    TEXT COMMENT '检查所见',
    diagnosis_result    TEXT COMMENT '诊断结论',
    diagnosis_code      VARCHAR(50) COMMENT '诊断编码',
    diagnosis_name      VARCHAR(200) COMMENT '诊断名称',

    key_images          TEXT COMMENT '关键影像(JSON)',

    report_status       VARCHAR(20) NOT NULL DEFAULT '草稿' COMMENT '状态',

    writer_id           VARCHAR(20) COMMENT '书写医生ID',
    writer_name         VARCHAR(50) COMMENT '书写医生姓名',
    write_time          DATETIME COMMENT '书写时间',

    reviewer_id         VARCHAR(20) COMMENT '审核医生ID',
    reviewer_name       VARCHAR(50) COMMENT '审核医生姓名',
    review_time         DATETIME COMMENT '审核时间',

    publish_time        DATETIME COMMENT '发布时间',
    publisher_id        VARCHAR(20) COMMENT '发布人ID',

    modify_history      TEXT COMMENT '修改历史(JSON)',

    print_count         INT DEFAULT 0 COMMENT '打印次数',

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_exam (exam_id),
    INDEX idx_patient (patient_id),
    INDEX idx_report_no (report_no),
    INDEX idx_status (report_status)
);
```

---

## 4. 业务流程

### 4.1 检查流程
```
┌─────────────────────────────────────────────────────────────────┐
│                        影像检查流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 申请开立  │───>│ 预约登记  │───>│ 影像采集  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │               │                       │
│                         ▼               ▼                       │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 报告发布  │<───│ 报告审核  │<───│ 诊断报告  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │                                       │
│                         ▼                                       │
│                   ┌──────────┐                                 │
│                   │ 临床查看  │                                 │
│                   └──────────┘                                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 DICOM工作流程
```
1. 设备从PACS获取工作列表(MWL)
2. 技师选择患者执行检查
3. 设备采集影像
4. 影像自动发送到PACS存储
5. PACS接收并解析影像
6. 生成缩略图
7. 影像可被诊断工作站调阅
```

---

## 5. 接口定义

### 5.1 检查申请接口

#### 5.1.1 开立检查申请
```
POST /api/pacs/request/create

Request:
{
    "patientId": "P001",
    "visitType": "门诊",
    "visitId": "VIS001",
    "deptId": "D001",
    "doctorId": "DOC001",
    "clinicalDiagnosis": "肺部感染",
    "itemId": "ITEM001",
    "itemName": "胸部CT",
    "examPart": "胸部",
    "isEmergency": false
}

Response:
{
    "code": 0,
    "message": "申请成功",
    "data": {
        "requestId": "REQ202401150001",
        "requestNo": "XR202401150001"
    }
}
```

#### 5.1.2 查询检查报告
```
GET /api/pacs/report/query?requestId=REQ001

Response:
{
    "code": 0,
    "data": {
        "reportId": "REP001",
        "reportNo": "RP202401150001",
        "examType": "胸部CT",
        "examDescription": "双肺纹理清晰...",
        "diagnosisResult": "双肺未见明显异常",
        "diagnosisName": "未见异常",
        "reportTime": "2024-01-15 15:00:00",
        "writerName": "张医生",
        "reviewerName": "李主任",
        "keyImages": [
            {"imageUid": "1.2.3...", "thumbnailPath": "/thumb/1.jpg"}
        ]
    }
}
```

### 5.2 DICOM接口

#### 5.2.1 DICOM存储服务
```
DICOM SCP服务配置:
- 端口: 104
- AE Title: PACS_ARCHIVE
- 支持SOP类:
  - CT Image Storage
  - MR Image Storage
  - X-Ray Image Storage
  - Ultrasound Image Storage
  - 等

影像接收流程:
1. 设备发送C-STORE请求
2. PACS接收DICOM影像
3. 解析影像信息
4. 存储影像文件
5. 生成数据库记录
6. 返回成功响应
```

#### 5.2.2 DICOM查询检索
```
DICOM Q/R SCP服务:
- 支持C-FIND查询
- 支持C-GET/C-MOVE检索
- 查询级别: Patient/Study/Series/Image

查询参数:
- Patient ID
- Patient Name
- Study Date
- Modality
- Study Description
```

---

## 6. 业务规则与约束

### 6.1 时限规则
| 规则编码 | 规则描述 | 时限要求 |
|----------|----------|----------|
| PACS001 | 急诊检查报告时限 | 30分钟 |
| PACS002 | 平诊检查报告时限 | 24小时 |
| PACS003 | 特殊检查报告时限 | 48小时 |

### 6.2 存储规则
- 影像原始数据必须完整存储
- 影像数据不可删除（归档转移）
- 关键影像必须标注保留
- 存储容量预警机制

### 6.3 报告规则
- 报告需双人签名（书写+审核）
- 报告修改需保留历史版本
- 阳性报告需重点标注

---

## 7. 模块交互关系

### 7.1 上游依赖
- **门诊管理模块**: 检查申请
- **住院管理模块**: 检查医嘱
- **财务收费模块**: 检查收费

### 7.2 下游调用
- **电子病历模块**: 报告查看

### 7.3 外部接口
- **DICOM设备**: 影像采集
- **远程会诊系统**: 影像传输

---

## 8. 性能与安全要求

### 8.1 性能要求
| 指标 | 要求 |
|------|------|
| 影像接收延迟 | < 10秒 |
| 影像调阅时间 | < 5秒 |
| 大影像加载时间 | < 30秒 |
| 存储容量 | >= 5TB在线 |

### 8.2 安全要求
- 影像数据加密传输
- 影像访问权限控制
- 影像操作审计日志
- 影像数据备份机制
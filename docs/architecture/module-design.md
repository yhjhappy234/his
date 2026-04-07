# HIS 模块详细设计

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06

---

## 1. 模块设计原则

### 1.1 模块边界

- 每个模块独立 Maven 子工程
- 模块内实现完整业务闭环
- 跨模块调用必须通过 API 接口
- 禁止跨模块直接访问数据库

### 1.2 模块依赖

```
his-module-{name}/
├── pom.xml                    # 模块依赖配置
├── src/main/java/
│   └── com/yhj/his/module/{name}/
│       ├── controller/        # 控制层
│       ├── service/           # 服务层
│       │   └── impl/          # 服务实现
│       ├── repository/        # 数据访问层
│       ├── entity/            # 实体类
│       ├── dto/               # 数据传输对象
│       ├── vo/                # 视图对象
│       ├── config/            # 配置类
│       └── util/              # 工具类
└── src/test/java/             # 测试代码
```

---

## 2. 系统管理模块 (System)

### 2.1 模块定位

系统管理模块是 HIS 系统的基础支撑模块，负责用户管理、权限控制、系统配置、数据安全、日志审计等。

### 2.2 核心功能

| 功能 | 描述 |
|------|------|
| 用户管理 | 用户注册、审核、信息维护、停用、注销 |
| 角色权限管理 | 角色定义、权限分配、用户授权、数据权限 |
| 科室管理 | 科室信息维护、层级架构、类型设置 |
| 系统参数配置 | 参数设置、分组管理、导入导出 |
| 数据字典管理 | 字典类型、字典项维护、排序设置 |
| 操作日志管理 | 操作记录、日志查询、统计分析 |
| 审计日志管理 | 安全审计、异常告警、审计报告 |
| 数据备份与恢复 | 备份策略、自动备份、恢复验证 |

### 2.3 核心实体

#### 用户信息 User
```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private String id;              // 用户ID
    private String userName;        // 用户名
    private String loginName;       // 登录账号
    private String password;        // 密码(加密)
    private String employeeId;      // 关联员工ID
    private String deptId;          // 所属科室
    private String phone;           // 联系电话
    private String email;           // 邮箱
    private Date passwordExpiry;    // 密码有效期
    private String status;          // 状态(正常/停用/锁定)
}
```

#### 数据字典 DataDictionary
```java
@Entity
@Table(name = "data_dictionary")
public class DataDictionary {
    @Id
    private String id;              // 字典ID
    private String dictType;        // 字典类型
    private String dictCode;        // 字典编码
    private String dictName;        // 字典名称
    private String dictValue;       // 字典值
    private Integer sortOrder;      // 排序号
    private Boolean isEnabled;      // 是否启用
}
```

---

## 3. 门诊管理模块 (Outpatient)

### 3.1 模块定位

门诊管理模块负责医院门诊全流程管理，包括预约挂号、分诊就诊、处方开立、收费结算等。

### 3.2 核心功能

| 功能 | 描述 |
|------|------|
| 预约挂号管理 | 号源管理、排班设置、预约挂号、取消预约 |
| 患者登记管理 | 患者建档、身份证识别、医保卡识别 |
| 分诊排队管理 | 分诊登记、优先就诊、叫号管理 |
| 门诊医生工作站 | 接诊功能、病历书写、处方开立、检查申请 |
| 门诊收费管理 | 挂号收费、处方收费、医保结算、票据管理 |
| 门诊报表统计 | 业务统计、财务统计 |

### 3.3 核心实体

#### 挂号记录 Registration
```java
@Entity
@Table(name = "registration")
public class Registration {
    @Id
    private String id;              // 挂号ID
    private String patientId;       // 患者ID
    private String patientName;     // 患者姓名
    private String deptId;          // 科室ID
    private String doctorId;        // 医生ID
    private Date scheduleDate;      // 就诊日期
    private String timePeriod;      // 时间段
    private Integer queueNo;        // 排队序号
    private String registrationType;// 挂号类型
    private BigDecimal totalFee;    // 总费用
    private String status;          // 状态
}
```

#### 门诊处方 OutpatientPrescription
```java
@Entity
@Table(name = "outpatient_prescription")
public class OutpatientPrescription {
    @Id
    private String id;              // 处方ID
    private String prescriptionNo;  // 处方号
    private String registrationId;  // 挂号ID
    private String patientId;       // 患者ID
    private String prescriptionType;// 处方类型
    private String diagnosisCode;   // 诊断编码
    private BigDecimal totalAmount; // 处方总金额
    private String status;          // 状态
}
```

### 3.4 业务流程

```
预约挂号 -> 到院签到 -> 分诊排队 -> 医生接诊 -> 开立处方/检查 -> 收费结算 -> 取药/治疗
```

---

## 4. 住院管理模块 (Inpatient)

### 4.1 模块定位

住院管理模块负责住院患者从入院到出院全流程管理，包括入院登记、床位管理、护理管理、医嘱执行、费用管理等。

### 4.2 核心功能

| 功能 | 描述 |
|------|------|
| 入院管理 | 入院申请、预约、登记、评估、医保登记 |
| 床位管理 | 病区设置、床位分配、床位调换、床位释放 |
| 病区管理 | 病区信息、护士站管理、交班管理 |
| 医嘱管理 | 医嘱开立、审核、执行、停止 |
| 护理管理 | 护理评估、护理记录、生命体征 |
| 转科管理 | 转科申请、审核、交接 |
| 出院管理 | 出院医嘱、申请、结算、小结 |
| 费用管理 | 医嘱计费、预交金管理 |

### 4.3 核心实体

#### 住院记录 InpatientAdmission
```java
@Entity
@Table(name = "inpatient_admission")
public class InpatientAdmission {
    @Id
    private String id;              // 住院ID
    private String admissionNo;     // 住院号
    private String patientId;       // 患者ID
    private Date admissionTime;     // 入院时间
    private String admissionType;   // 入院类型
    private String deptId;          // 科室ID
    private String wardId;          // 病区ID
    private String bedNo;           // 床位号
    private String doctorId;        // 主治医生ID
    private String nurseId;         // 责任护士ID
    private String nursingLevel;    // 护理等级
    private BigDecimal deposit;     // 预交金总额
    private String status;          // 状态
}
```

#### 医嘱信息 MedicalOrder
```java
@Entity
@Table(name = "medical_order")
public class MedicalOrder {
    @Id
    private String id;              // 医嘱ID
    private String orderNo;         // 医嘱编号
    private String admissionId;     // 住院ID
    private String orderType;       // 医嘱类型(长期/临时)
    private String orderCategory;   // 医嘱分类
    private String orderContent;    // 医嘱内容
    private String doctorId;        // 开立医生ID
    private Date orderTime;         // 医嘱时间
    private String status;          // 状态
}
```

### 4.4 医嘱生命周期

```
开立 -> 审核 -> 执行中 -> 完成
  │      │
  │      └── 驳回
  │
  └── 作废

长期医嘱: 开立 -> 审核 -> 执行中 -> 停止 -> 已停止
```

---

## 5. 药房管理模块 (Pharmacy)

### 5.1 模块定位

药房管理模块负责药品的采购、库存、发药、用药审核等全流程管理。

### 5.2 核心功能

| 功能 | 描述 |
|------|------|
| 药品信息管理 | 药品目录、分类、规格、价格、编码管理 |
| 药品库存管理 | 库存查询、预警、批次管理、盘点 |
| 门诊药房发药 | 处方接收、审核、调配、发药 |
| 住院药房发药 | 医嘱接收、审核、调配、病区发药 |
| 药品采购管理 | 采购申请、审核、订单、入库 |
| 供应商管理 | 供应商档案、资质、合同、评估 |
| 效期管理 | 效期预警、过期报损 |
| 用药审核 | 配伍禁忌、药物相互作用、剂量审核 |

### 5.3 核心实体

#### 药品信息 Drug
```java
@Entity
@Table(name = "drug")
public class Drug {
    @Id
    private String id;              // 药品ID
    private String drugCode;        // 药品编码
    private String drugName;        // 药品名称
    private String genericName;     // 通用名
    private String drugCategory;    // 药品分类
    private String drugForm;        // 剂型
    private String drugSpec;        // 规格
    private BigDecimal retailPrice; // 零售价
    private Boolean isPrescription; // 是否处方药
    private Boolean isInsurance;    // 是否医保
    private String status;          // 状态
}
```

#### 药品库存 DrugInventory
```java
@Entity
@Table(name = "drug_inventory")
public class DrugInventory {
    @Id
    private String id;              // 库存ID
    private String drugId;          // 药品ID
    private String pharmacyId;      // 药房ID
    private String batchNo;         // 批号
    private Date expiryDate;        // 有效期
    private BigDecimal quantity;    // 库存数量
    private BigDecimal lockedQty;   // 锁定数量
    private BigDecimal availableQty;// 可用数量
    private String status;          // 状态
}
```

### 5.4 发药流程

```
处方收费 -> 处方传递 -> 药师审核 -> 药品调配 -> 发药核对 -> 发药确认 -> 库存扣减
```

---

## 6. 检验管理模块 (LIS)

### 6.1 模块定位

检验管理模块负责检验申请、样本管理、检验执行、报告管理等。

### 6.2 核心功能

- 检验项目管理
- 检验申请管理
- 样本采集管理
- 检验执行管理
- 报告审核发布
- 标本流转追踪

---

## 7. 影像管理模块 (PACS)

### 7.1 模块定位

影像管理模块负责检查预约、影像采集、影像浏览、诊断报告等。

### 7.2 核心功能

- 检查项目管理
- 检查预约管理
- 影像采集存储
- 影像浏览诊断
- 报告审核发布
- 设备管理

---

## 8. 财务收费模块 (Finance)

### 8.1 模块定位

财务收费模块负责价表管理、费用结算、医保结算、发票管理等。

### 8.2 核心功能

- 价表管理
- 费用计算
- 医保结算
- 收费结算
- 发票管理
- 财务报表

---

## 9. 电子病历模块 (EMR)

### 9.1 模块定位

电子病历模块负责病历书写、病历模板、病历质控、病历归档等。

### 9.2 核心功能

- 病历模板管理
- 病历书写录入
- 病历质控审核
- 病历归档存储
- 病历查阅管理

---

## 10. 语音呼叫模块 (Voice)

### 10.1 模块定位

语音呼叫模块负责叫号播报、报告通知、寻人广播、全员通知等。

### 10.2 核心功能

- 诊室叫号播报
- 药房取药通知
- 检验报告通知
- 寻人广播
- 紧急广播
- 多音频设备管理

### 10.3 语音播报场景

```yaml
门诊场景:
  - 诊室叫号: "请15号患者张明到101诊室就诊"
  - 过号提醒: "请10号患者李华，听到广播后到102诊室"
  
药房场景:
  - 取药叫号: "请张明到药房1号窗口取药"
  
检验场景:
  - 报告完成: "张明，您的检验报告已完成"
  
住院场景:
  - 危急值通知: "内科3床患者检验危急值，请立即处理"
```

---

## 11. 模块交互关系图

```
┌─────────────────────────────────────────────────────────────────────┐
│                        模块交互关系                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────┐     ┌─────────┐     ┌─────────┐                      │
│  │ 系统管理 │────>│ 门诊    │────>│ 药房    │                      │
│  │ (用户)   │     │ (处方)  │     │ (发药)  │                      │
│  └────┬────┘     └────┬────┘     └────┬────┘                      │
│       │               │               │                             │
│       │               │               │                             │
│       ▼               ▼               ▼                             │
│  ┌─────────┐     ┌─────────┐     ┌─────────┐                      │
│  │ 人力资源 │     │ 检验    │     │ 财务    │                      │
│  │ (排班)   │     │ (申请)  │     │ (收费)  │                      │
│  └────┬────┘     └────┬────┘     └────┬────┘                      │
│       │               │               │                             │
│       │               │               │                             │
│       ▼               ▼               ▼                             │
│  ┌─────────┐     ┌─────────┐     ┌─────────┐                      │
│  │ 住院    │────>│ 影像    │────>│ 语音    │                      │
│  │ (医嘱)  │     │ (检查)  │     │ (播报)  │                      │
│  └────┬────┘     └────┬────┘     └─────────┘                      │
│       │               │                                             │
│       ▼               ▼                                             │
│  ┌─────────┐     ┌─────────┐                                      │
│  │ 电子病历 │     │ 库存    │                                      │
│  │ (病历)  │     │ (物资)  │                                      │
│  └─────────┘     └─────────┘                                      │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

**文档维护**: HIS Platform Team
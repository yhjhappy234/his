# HIS 数据库设计

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06  
> **数据库**: SQLite3

---

## 1. 数据库架构概述

HIS 系统采用 SQLite3 作为主要数据库，实现轻量化、便携化部署。数据库架构分为全局数据库和模块独立数据库两部分。

### 1.1 数据库架构设计

```yaml
数据库架构设计:
  全局数据库(Global DB):
    路径: /data/his_global.db
    用途: 
      - 患者主索引(EMPI)
      - 系统配置
      - 全局字典
      - 用户权限
      - 跨模块关联数据
    大小限制: 建议 < 2GB

  模块独立数据库(Module DB):
    门诊模块: /data/modules/outpatient.db
    住院模块: /data/modules/inpatient.db
    药房模块: /data/modules/pharmacy.db
    检验模块: /data/modules/lis.db
    影像模块: /data/modules/pacs.db
    财务模块: /data/modules/finance.db
    库存模块: /data/modules/inventory.db
    人力模块: /data/modules/hr.db
    语音模块: /data/modules/voice.db
    病历模块: /data/modules/emr.db

  数据库特性:
    - 支持WAL模式(Write-Ahead Logging)
    - 支持加密(SQLCipher)
    - 支持全文搜索(FTS5)
    - 自动定期VACUUM
    - 支持JSON扩展
```

---

## 2. 表命名规范

### 2.1 基本规范

```yaml
表命名规范:
  表名: snake_case复数形式 (如 patients, orders)
  主键: id (UUID字符串)
  外键: {table}_id
  创建时间: created_at
  更新时间: updated_at
  状态: status
  删除标记: deleted (逻辑删除)
```

### 2.2 字段类型映射

```yaml
字段类型映射:
  Java String     -> TEXT
  Java Integer    -> INTEGER
  Java Long       -> INTEGER
  Java BigDecimal -> REAL/DECIMAL
  Java Date       -> TEXT (ISO格式)
  Java Boolean    -> INTEGER (0/1)
```

---

## 3. 全局数据库设计

### 3.1 患者主索引表 (EMPI)

```sql
CREATE TABLE patient_master (
    id                  TEXT PRIMARY KEY,
    empi_id             TEXT UNIQUE NOT NULL,
    name                TEXT NOT NULL,
    gender              TEXT NOT NULL,
    birth_date          DATE,
    id_type             TEXT,
    id_no               TEXT UNIQUE,
    phone               TEXT,
    status              TEXT DEFAULT 'active',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_patient_empi ON patient_master(empi_id);
CREATE INDEX idx_patient_id_no ON patient_master(id_no);
CREATE INDEX idx_patient_name ON patient_master(name);
```

### 3.2 用户表

```sql
CREATE TABLE sys_user (
    id                  TEXT PRIMARY KEY,
    username            TEXT UNIQUE NOT NULL,
    password_hash       TEXT NOT NULL,
    real_name           TEXT,
    dept_id             TEXT,
    status              TEXT DEFAULT 'active',
    last_login_time     DATETIME,
    last_login_ip       TEXT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_username ON sys_user(username);
CREATE INDEX idx_user_dept ON sys_user(dept_id);
```

### 3.3 科室表

```sql
CREATE TABLE sys_department (
    id                  TEXT PRIMARY KEY,
    dept_code           TEXT UNIQUE NOT NULL,
    dept_name           TEXT NOT NULL,
    dept_type           TEXT,
    parent_id           TEXT,
    status              TEXT DEFAULT 'active',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_dept_code ON sys_department(dept_code);
CREATE INDEX idx_dept_parent ON sys_department(parent_id);
```

### 3.4 角色表

```sql
CREATE TABLE sys_role (
    id                  TEXT PRIMARY KEY,
    role_code           TEXT UNIQUE NOT NULL,
    role_name           TEXT NOT NULL,
    description         TEXT,
    status              TEXT DEFAULT 'active',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 3.5 用户角色关联表

```sql
CREATE TABLE sys_user_role (
    id                  TEXT PRIMARY KEY,
    user_id             TEXT NOT NULL,
    role_id             TEXT NOT NULL,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id)
);

CREATE INDEX idx_user_role_user ON sys_user_role(user_id);
CREATE INDEX idx_user_role_role ON sys_user_role(role_id);
```

### 3.6 权限表

```sql
CREATE TABLE sys_permission (
    id                  TEXT PRIMARY KEY,
    perm_code           TEXT UNIQUE NOT NULL,
    perm_name           TEXT NOT NULL,
    perm_type           TEXT,
    resource            TEXT,
    parent_id           TEXT,
    status              TEXT DEFAULT 'active',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 3.7 角色权限关联表

```sql
CREATE TABLE sys_role_permission (
    id                  TEXT PRIMARY KEY,
    role_id             TEXT NOT NULL,
    permission_id       TEXT NOT NULL,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, permission_id)
);
```

### 3.8 数据字典表

```sql
CREATE TABLE sys_dictionary (
    id                  TEXT PRIMARY KEY,
    dict_type           TEXT NOT NULL,
    dict_code           TEXT NOT NULL,
    dict_name           TEXT NOT NULL,
    dict_value          TEXT,
    parent_code         TEXT,
    dict_level          INTEGER,
    sort_order          INTEGER,
    is_enabled          INTEGER DEFAULT 1,
    is_default          INTEGER DEFAULT 0,
    description         TEXT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(dict_type, dict_code)
);

CREATE INDEX idx_dict_type ON sys_dictionary(dict_type);
CREATE INDEX idx_dict_type_code ON sys_dictionary(dict_type, dict_code);
```

### 3.9 系统参数表

```sql
CREATE TABLE sys_parameter (
    id                  TEXT PRIMARY KEY,
    param_code          TEXT UNIQUE NOT NULL,
    param_name          TEXT,
    param_value         TEXT,
    param_type          TEXT,
    param_group         TEXT,
    description         TEXT,
    is_system           INTEGER DEFAULT 0,
    is_editable         INTEGER DEFAULT 1,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_param_code ON sys_parameter(param_code);
CREATE INDEX idx_param_group ON sys_parameter(param_group);
```

### 3.10 全局序列号表

```sql
CREATE TABLE sys_sequence (
    seq_name            TEXT PRIMARY KEY,
    current_value       INTEGER DEFAULT 0,
    prefix              TEXT,
    date_format         TEXT
);
```

---

## 4. 门诊模块数据库设计

### 4.1 挂号记录表

```sql
CREATE TABLE registration (
    id                  TEXT PRIMARY KEY,
    reg_no              TEXT UNIQUE NOT NULL,
    patient_id          TEXT NOT NULL,
    patient_name        TEXT,
    id_card_no          TEXT,
    gender              TEXT NOT NULL,
    age                 INTEGER,
    phone               TEXT,
    
    dept_id             TEXT NOT NULL,
    dept_name           TEXT,
    doctor_id           TEXT,
    doctor_name         TEXT,
    schedule_id         TEXT,
    schedule_date       DATE NOT NULL,
    time_period         TEXT,
    queue_no            INTEGER,
    visit_no            TEXT,
    
    registration_type   TEXT,
    registration_fee    DECIMAL(10,2),
    diagnosis_fee       DECIMAL(10,2),
    total_fee           DECIMAL(10,2),
    
    status              TEXT NOT NULL,
    visit_status        TEXT,
    
    source              TEXT,
    booking_time        DATETIME,
    check_in_time       DATETIME,
    start_time          DATETIME,
    end_time            DATETIME,
    
    operator_id         TEXT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reg_patient ON registration(patient_id);
CREATE INDEX idx_reg_date ON registration(schedule_date);
CREATE INDEX idx_reg_status ON registration(status, schedule_date);
CREATE INDEX idx_reg_dept_date ON registration(dept_id, schedule_date);
```

### 4.2 排班表

```sql
CREATE TABLE schedule (
    id                  TEXT PRIMARY KEY,
    dept_id             TEXT NOT NULL,
    doctor_id           TEXT NOT NULL,
    schedule_date       DATE NOT NULL,
    time_period         TEXT NOT NULL,
    start_time          TEXT,
    end_time            TEXT,
    
    total_quota         INTEGER DEFAULT 0,
    booked_quota        INTEGER DEFAULT 0,
    available_quota     INTEGER DEFAULT 0,
    
    registration_type   TEXT,
    registration_fee    DECIMAL(10,2),
    diagnosis_fee       DECIMAL(10,2),
    
    status              TEXT DEFAULT 'open',
    stop_reason         TEXT,
    
    clinic_room         TEXT,
    
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(doctor_id, schedule_date, time_period)
);

CREATE INDEX idx_schedule_date ON schedule(schedule_date);
CREATE INDEX idx_schedule_dept ON schedule(dept_id, schedule_date);
CREATE INDEX idx_schedule_doctor ON schedule(doctor_id, schedule_date);
```

### 4.3 门诊病历表

```sql
CREATE TABLE outpatient_record (
    id                  TEXT PRIMARY KEY,
    registration_id     TEXT NOT NULL,
    patient_id          TEXT NOT NULL,
    visit_no            TEXT,
    
    dept_id             TEXT NOT NULL,
    doctor_id           TEXT NOT NULL,
    visit_date          DATE NOT NULL,
    
    chief_complaint     TEXT,
    present_illness     TEXT,
    past_history        TEXT,
    allergy_history     TEXT,
    personal_history    TEXT,
    family_history      TEXT,
    
    temperature         DECIMAL(4,1),
    pulse               INTEGER,
    respiration         INTEGER,
    blood_pressure      TEXT,
    height              INTEGER,
    weight              DECIMAL(5,1),
    
    physical_exam       TEXT,
    auxiliary_exam      TEXT,
    
    diagnosis_code      TEXT,
    diagnosis_name      TEXT,
    diagnosis_type      TEXT,
    
    treatment_plan      TEXT,
    medical_advice      TEXT,
    
    status              TEXT DEFAULT 'draft',
    
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    submit_time         DATETIME
);

CREATE INDEX idx_record_reg ON outpatient_record(registration_id);
CREATE INDEX idx_record_patient ON outpatient_record(patient_id, visit_date);
CREATE INDEX idx_record_doctor ON outpatient_record(doctor_id, visit_date);
```

### 4.4 门诊处方表

```sql
CREATE TABLE outpatient_prescription (
    id                  TEXT PRIMARY KEY,
    prescription_no     TEXT UNIQUE NOT NULL,
    registration_id     TEXT NOT NULL,
    patient_id          TEXT NOT NULL,
    patient_name        TEXT,
    gender              TEXT,
    age                 INTEGER,
    
    dept_id             TEXT NOT NULL,
    doctor_id           TEXT NOT NULL,
    doctor_name         TEXT,
    
    prescription_type   TEXT NOT NULL,
    prescription_date   DATE NOT NULL,
    
    diagnosis_code      TEXT,
    diagnosis_name      TEXT,
    
    total_amount        DECIMAL(10,2),
    pay_status          TEXT DEFAULT 'unpaid',
    pay_time            DATETIME,
    
    status              TEXT DEFAULT 'valid',
    audit_status        TEXT,
    auditor_id          TEXT,
    audit_time          DATETIME,
    audit_remark        TEXT,
    
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pres_reg ON outpatient_prescription(registration_id);
CREATE INDEX idx_pres_patient ON outpatient_prescription(patient_id);
CREATE INDEX idx_pres_status ON outpatient_prescription(status, prescription_date);
```

### 4.5 处方明细表

```sql
CREATE TABLE prescription_detail (
    id                  TEXT PRIMARY KEY,
    prescription_id     TEXT NOT NULL,
    
    drug_id             TEXT NOT NULL,
    drug_name           TEXT,
    drug_spec           TEXT,
    drug_unit           TEXT,
    drug_form           TEXT,
    
    quantity            DECIMAL(10,2) NOT NULL,
    dosage              TEXT,
    frequency           TEXT,
    days                INTEGER,
    route               TEXT,
    
    unit_price          DECIMAL(10,4),
    amount              DECIMAL(10,2),
    
    group_no            INTEGER,
    skin_test           TEXT,
    skin_test_result    TEXT,
    
    is_essential        INTEGER,
    is_insurance        INTEGER,
    
    remark              TEXT,
    
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pres_detail ON prescription_detail(prescription_id);
CREATE INDEX idx_pres_drug ON prescription_detail(drug_id);
```

---

## 5. 住院模块数据库设计

### 5.1 住院记录表

```sql
CREATE TABLE inpatient_admission (
    id                  TEXT PRIMARY KEY,
    admission_no        TEXT UNIQUE NOT NULL,
    patient_id          TEXT NOT NULL,
    patient_name        TEXT,
    id_card_no          TEXT,
    gender              TEXT NOT NULL,
    birth_date          DATE,
    age                 INTEGER,
    phone               TEXT,
    address             TEXT,
    
    admission_time      DATETIME NOT NULL,
    admission_type      TEXT NOT NULL,
    admission_source    TEXT,
    
    dept_id             TEXT NOT NULL,
    dept_name           TEXT,
    ward_id             TEXT NOT NULL,
    ward_name           TEXT,
    room_no             TEXT,
    bed_no              TEXT,
    
    doctor_id           TEXT NOT NULL,
    doctor_name         TEXT,
    nurse_id            TEXT,
    nurse_name          TEXT,
    
    admission_diagnosis TEXT,
    admission_diagnosis_code TEXT,
    discharge_diagnosis TEXT,
    discharge_diagnosis_code TEXT,
    
    nursing_level       TEXT,
    diet_type           TEXT,
    allergy_info        TEXT,
    
    insurance_type      TEXT,
    insurance_no        TEXT,
    
    deposit             DECIMAL(12,2) DEFAULT 0,
    total_cost          DECIMAL(12,2) DEFAULT 0,
    settled_cost        DECIMAL(12,2) DEFAULT 0,
    
    status              TEXT DEFAULT 'in_hospital',
    discharge_time      DATETIME,
    discharge_type      TEXT,
    
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_admission_patient ON inpatient_admission(patient_id);
CREATE INDEX idx_admission_time ON inpatient_admission(admission_time);
CREATE INDEX idx_admission_dept_status ON inpatient_admission(dept_id, status);
CREATE INDEX idx_admission_bed ON inpatient_admission(ward_id, bed_no);
```

### 5.2 床位信息表

```sql
CREATE TABLE bed (
    id                  TEXT PRIMARY KEY,
    bed_no              TEXT NOT NULL,
    ward_id             TEXT NOT NULL,
    ward_name           TEXT,
    room_no             TEXT NOT NULL,
    bed_type            TEXT,
    bed_level           TEXT,
    daily_rate          DECIMAL(10,2),
    
    status              TEXT DEFAULT 'vacant',
    admission_id        TEXT,
    patient_id          TEXT,
    patient_name        TEXT,
    
    reserved_time       DATETIME,
    reserved_patient_id TEXT,
    
    facilities          TEXT,
    
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(ward_id, bed_no)
);

CREATE INDEX idx_bed_status ON bed(status);
CREATE INDEX idx_bed_ward ON bed(ward_id);
```

### 5.3 医嘱信息表

```sql
CREATE TABLE medical_order (
    id                  TEXT PRIMARY KEY,
    order_no            TEXT UNIQUE NOT NULL,
    admission_id        TEXT NOT NULL,
    patient_id          TEXT NOT NULL,
    
    order_type          TEXT NOT NULL,
    order_category      TEXT NOT NULL,
    order_content       TEXT NOT NULL,
    order_detail        TEXT,
    
    start_time          DATETIME,
    end_time            DATETIME,
    execute_time        TEXT,
    frequency           TEXT,
    
    doctor_id           TEXT NOT NULL,
    doctor_name         TEXT,
    order_time          DATETIME NOT NULL,
    
    nurse_id            TEXT,
    nurse_name          TEXT,
    audit_time          DATETIME,
    
    status              TEXT DEFAULT 'new',
    
    stop_doctor_id      TEXT,
    stop_doctor_name    TEXT,
    stop_time           DATETIME,
    stop_reason         TEXT,
    
    group_no            INTEGER,
    
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_admission ON medical_order(admission_id);
CREATE INDEX idx_order_patient ON medical_order(patient_id);
CREATE INDEX idx_order_status ON medical_order(status);
```

### 5.4 护理记录表

```sql
CREATE TABLE nursing_record (
    id                  TEXT PRIMARY KEY,
    admission_id        TEXT NOT NULL,
    patient_id          TEXT NOT NULL,
    
    record_time         DATETIME NOT NULL,
    record_type         TEXT NOT NULL,
    
    temperature         DECIMAL(4,1),
    pulse               INTEGER,
    respiration         INTEGER,
    blood_pressure_systolic INTEGER,
    blood_pressure_diastolic INTEGER,
    spo2                INTEGER,
    weight              DECIMAL(5,1),
    height              INTEGER,
    
    intake              DECIMAL(8,2),
    output              DECIMAL(8,2),
    urine               DECIMAL(8,2),
    stool               TEXT,
    
    nursing_content     TEXT,
    nursing_measures    TEXT,
    
    nurse_id            TEXT NOT NULL,
    nurse_name          TEXT,
    
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_nursing_admission ON nursing_record(admission_id);
CREATE INDEX idx_nursing_patient ON nursing_record(patient_id);
CREATE INDEX idx_nursing_time ON nursing_record(record_time);
```

---

## 6. 药房模块数据库设计

### 6.1 药品信息表

```sql
CREATE TABLE drug (
    id                  TEXT PRIMARY KEY,
    drug_code           TEXT UNIQUE NOT NULL,
    drug_name           TEXT NOT NULL,
    generic_name        TEXT,
    trade_name          TEXT,
    pinyin_code         TEXT,
    
    drug_category       TEXT NOT NULL,
    drug_form           TEXT,
    drug_spec           TEXT,
    drug_unit           TEXT,
    package_unit        TEXT,
    package_quantity    INTEGER,
    
    manufacturer        TEXT,
    origin              TEXT,
    approval_no         TEXT,
    
    purchase_price      DECIMAL(10,4),
    retail_price        DECIMAL(10,4) NOT NULL,
    price_date          DATE,
    
    is_prescription     INTEGER DEFAULT 0,
    is_otc              INTEGER DEFAULT 0,
    is_essential        INTEGER DEFAULT 0,
    is_insurance        INTEGER DEFAULT 0,
    insurance_code      TEXT,
    insurance_type      TEXT,
    
    storage_condition   TEXT,
    shelf_life          INTEGER,
    alert_days          INTEGER DEFAULT 180,
    
    min_stock           DECIMAL(10,2),
    max_stock           DECIMAL(10,2),
    
    status              TEXT DEFAULT 'active',
    
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_drug_name ON drug(drug_name);
CREATE INDEX idx_drug_pinyin ON drug(pinyin_code);
CREATE INDEX idx_drug_category ON drug(drug_category);
```

### 6.2 药品库存表

```sql
CREATE TABLE drug_inventory (
    id                  TEXT PRIMARY KEY,
    drug_id             TEXT NOT NULL,
    drug_code           TEXT,
    drug_name           TEXT,
    drug_spec           TEXT,
    drug_unit           TEXT,
    
    pharmacy_id         TEXT NOT NULL,
    pharmacy_name       TEXT,
    
    batch_no            TEXT NOT NULL,
    production_date     DATE,
    expiry_date         DATE NOT NULL,
    
    quantity            DECIMAL(10,2) DEFAULT 0,
    locked_quantity     DECIMAL(10,2) DEFAULT 0,
    available_quantity  DECIMAL(10,2) DEFAULT 0,
    
    location            TEXT,
    
    purchase_price      DECIMAL(10,4),
    retail_price        DECIMAL(10,4),
    
    supplier_id         TEXT,
    supplier_name       TEXT,
    
    status              TEXT DEFAULT 'active',
    
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(drug_id, batch_no, pharmacy_id)
);

CREATE INDEX idx_inventory_drug ON drug_inventory(drug_id);
CREATE INDEX idx_inventory_expiry ON drug_inventory(expiry_date);
CREATE INDEX idx_inventory_pharmacy ON drug_inventory(pharmacy_id);
```

### 6.3 库存流水表

```sql
CREATE TABLE inventory_transaction (
    id                  TEXT PRIMARY KEY,
    transaction_no      TEXT UNIQUE NOT NULL,
    transaction_type    TEXT NOT NULL,
    
    drug_id             TEXT NOT NULL,
    drug_code           TEXT,
    drug_name           TEXT,
    drug_spec           TEXT,
    drug_unit           TEXT,
    
    pharmacy_id         TEXT NOT NULL,
    batch_no            TEXT,
    expiry_date         DATE,
    
    quantity_before     DECIMAL(10,2),
    quantity_change     DECIMAL(10,2) NOT NULL,
    quantity_after      DECIMAL(10,2),
    
    retail_price        DECIMAL(10,4),
    purchase_price      DECIMAL(10,4),
    amount              DECIMAL(10,2),
    
    related_id          TEXT,
    related_no          TEXT,
    reason              TEXT,
    
    operator_id         TEXT,
    operator_name       TEXT,
    operate_time        DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_trans_drug ON inventory_transaction(drug_id);
CREATE INDEX idx_trans_pharmacy ON inventory_transaction(pharmacy_id);
CREATE INDEX idx_trans_type_time ON inventory_transaction(transaction_type, operate_time);
CREATE INDEX idx_trans_related ON inventory_transaction(related_id);
```

---

## 7. 数据库访问规范

### 7.1 访问原则

```yaml
访问原则:
  - 模块只能访问自己的数据库和全局数据库
  - 禁止跨模块直接访问数据库
  - 跨模块数据交互通过API调用
```

### 7.2 连接配置

```yaml
连接配置:
  - 每个模块维护自己的数据库连接池
  - 启用WAL模式提高并发性能
  - 定期执行VACUUM和ANALYZE
```

### 7.3 数据同步

```yaml
数据同步:
  - 关键业务数据同步到全局数据库
  - 使用事件机制通知数据变更
  - 支持离线操作，恢复后自动同步
```

### 7.4 备份策略

```yaml
备份策略:
  - 每日增量备份
  - 每周全量备份
  - 备份文件加密存储
```

---

## 8. 数据库初始化脚本

```sql
-- 启用WAL模式
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;
PRAGMA foreign_keys = ON;

-- 创建全局数据库表
-- (参见上文各表定义)

-- 初始化数据字典
INSERT INTO sys_dictionary (id, dict_type, dict_code, dict_name, sort_order) VALUES
('D001', 'gender', '1', '男', 1),
('D002', 'gender', '2', '女', 2),
('D003', 'gender', '9', '未知', 3),
('D004', 'blood_type', 'A', 'A型', 1),
('D005', 'blood_type', 'B', 'B型', 2),
('D006', 'blood_type', 'AB', 'AB型', 3),
('D007', 'blood_type', 'O', 'O型', 4);

-- 初始化系统参数
INSERT INTO sys_parameter (id, param_code, param_name, param_value, param_group) VALUES
('P001', 'hospital_name', '医院名称', '示例医院', 'system'),
('P002', 'registration_quota', '号源上限', '30', 'outpatient'),
('P003', 'prescription_limit_days', '处方限量天数', '7', 'outpatient'),
('P004', 'session_timeout', '会话超时时间', '30', 'security');
```

---

**文档维护**: HIS Platform Team
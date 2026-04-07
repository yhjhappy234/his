-- ============================================
-- HIS Inpatient Module - Initial Schema Migration
-- Version: V1
-- Description: Create inpatient management tables
-- ============================================

-- Bed table (床位信息表)
CREATE TABLE IF NOT EXISTS bed (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    bed_no VARCHAR(20) NOT NULL,
    ward_id VARCHAR(20) NOT NULL,
    ward_name VARCHAR(100),
    room_no VARCHAR(20) NOT NULL,
    bed_type VARCHAR(20),
    bed_level VARCHAR(20),
    daily_rate DECIMAL(10, 2),
    status VARCHAR(20) NOT NULL DEFAULT 'VACANT',
    admission_id VARCHAR(36),
    patient_id VARCHAR(20),
    patient_name VARCHAR(50),
    reserved_time DATETIME,
    reserved_patient_id VARCHAR(20),
    facilities TEXT,
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (ward_id, bed_no)
);

CREATE INDEX IF NOT EXISTS idx_bed_status ON bed(status);

-- Inpatient Admission table (住院记录表)
CREATE TABLE IF NOT EXISTS inpatient_admission (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    admission_no VARCHAR(20) NOT NULL UNIQUE,
    patient_id VARCHAR(20) NOT NULL,
    patient_name VARCHAR(50) NOT NULL,
    id_card_no VARCHAR(18),
    gender VARCHAR(1) NOT NULL,
    birth_date DATE,
    age INTEGER,
    phone VARCHAR(20),
    address VARCHAR(200),
    admission_time DATETIME NOT NULL,
    admission_type VARCHAR(20) NOT NULL,
    admission_source VARCHAR(50),
    dept_id VARCHAR(20) NOT NULL,
    dept_name VARCHAR(100),
    ward_id VARCHAR(20) NOT NULL,
    ward_name VARCHAR(100),
    room_no VARCHAR(20),
    bed_no VARCHAR(20),
    doctor_id VARCHAR(20) NOT NULL,
    doctor_name VARCHAR(50),
    nurse_id VARCHAR(20),
    nurse_name VARCHAR(50),
    admission_diagnosis VARCHAR(500),
    admission_diagnosis_code VARCHAR(50),
    discharge_diagnosis VARCHAR(500),
    discharge_diagnosis_code VARCHAR(50),
    nursing_level VARCHAR(20),
    diet_type VARCHAR(20),
    allergy_info TEXT,
    insurance_type VARCHAR(50),
    insurance_no VARCHAR(50),
    deposit DECIMAL(12, 2) DEFAULT 0,
    total_cost DECIMAL(12, 2) DEFAULT 0,
    settled_cost DECIMAL(12, 2) DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    discharge_time DATETIME,
    discharge_type VARCHAR(20),
    contact_person VARCHAR(50),
    contact_phone VARCHAR(20),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_admission_patient ON inpatient_admission(patient_id);
CREATE INDEX IF NOT EXISTS idx_admission_time ON inpatient_admission(admission_time);
CREATE INDEX IF NOT EXISTS idx_dept_status ON inpatient_admission(dept_id, status);
CREATE INDEX IF NOT EXISTS idx_bed ON inpatient_admission(ward_id, bed_no);

-- Medical Order table (医嘱信息表)
CREATE TABLE IF NOT EXISTS medical_order (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    order_no VARCHAR(30) NOT NULL UNIQUE,
    admission_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(20) NOT NULL,
    order_type VARCHAR(20) NOT NULL,
    order_category VARCHAR(20) NOT NULL,
    order_content TEXT NOT NULL,
    order_detail TEXT,
    start_time DATETIME,
    end_time DATETIME,
    execute_time VARCHAR(50),
    frequency VARCHAR(50),
    doctor_id VARCHAR(20) NOT NULL,
    doctor_name VARCHAR(50),
    order_time DATETIME NOT NULL,
    nurse_id VARCHAR(20),
    nurse_name VARCHAR(50),
    audit_time DATETIME,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    stop_doctor_id VARCHAR(20),
    stop_doctor_name VARCHAR(50),
    stop_time DATETIME,
    stop_reason VARCHAR(200),
    group_no INTEGER,
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_order_no ON medical_order(order_no);
CREATE INDEX IF NOT EXISTS idx_admission ON medical_order(admission_id);
CREATE INDEX IF NOT EXISTS idx_patient ON medical_order(patient_id);
CREATE INDEX IF NOT EXISTS idx_status ON medical_order(status);

-- Nursing Record table (护理记录表)
CREATE TABLE IF NOT EXISTS nursing_record (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    admission_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(20) NOT NULL,
    record_time DATETIME NOT NULL,
    record_type VARCHAR(20) NOT NULL,
    temperature DECIMAL(4, 1),
    pulse INTEGER,
    respiration INTEGER,
    blood_pressure_systolic INTEGER,
    blood_pressure_diastolic INTEGER,
    spo2 INTEGER,
    weight DECIMAL(5, 1),
    height INTEGER,
    intake DECIMAL(8, 2),
    output DECIMAL(8, 2),
    urine DECIMAL(8, 2),
    stool VARCHAR(20),
    nursing_content TEXT,
    nursing_measures TEXT,
    nurse_id VARCHAR(20) NOT NULL,
    nurse_name VARCHAR(50),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_nursing_admission ON nursing_record(admission_id);
CREATE INDEX IF NOT EXISTS idx_nursing_patient ON nursing_record(patient_id);
CREATE INDEX IF NOT EXISTS idx_record_time ON nursing_record(record_time);

-- Order Execution table (医嘱执行表)
CREATE TABLE IF NOT EXISTS order_execution (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    admission_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(20) NOT NULL,
    execution_time DATETIME NOT NULL,
    executor_id VARCHAR(20),
    executor_name VARCHAR(50),
    execution_result VARCHAR(20),
    execution_content TEXT,
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_exec_order ON order_execution(order_id);
CREATE INDEX IF NOT EXISTS idx_exec_admission ON order_execution(admission_id);

-- Inpatient Fee table (住院费用表)
CREATE TABLE IF NOT EXISTS inpatient_fee (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    admission_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(20) NOT NULL,
    fee_date DATE NOT NULL,
    fee_type VARCHAR(20) NOT NULL,
    fee_name VARCHAR(100),
    amount DECIMAL(10, 2),
    quantity DECIMAL(10, 2),
    unit_price DECIMAL(10, 4),
    fee_status VARCHAR(20),
    settlement_id VARCHAR(36),
    remark VARCHAR(200),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_fee_admission ON inpatient_fee(admission_id);
CREATE INDEX IF NOT EXISTS idx_fee_patient ON inpatient_fee(patient_id);
CREATE INDEX IF NOT EXISTS idx_fee_date ON inpatient_fee(fee_date);
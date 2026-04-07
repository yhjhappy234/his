-- ============================================
-- HIS Outpatient Module - Initial Schema Migration
-- Version: V1
-- Description: Create outpatient management tables
-- ============================================

-- Patient table (患者信息表)
CREATE TABLE IF NOT EXISTS patient (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    patient_id VARCHAR(20) NOT NULL UNIQUE,
    id_card_no VARCHAR(18) UNIQUE,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    birth_date DATE NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(200),
    emergency_contact VARCHAR(50),
    emergency_phone VARCHAR(20),
    blood_type VARCHAR(10),
    allergy_history TEXT,
    medical_history TEXT,
    medical_insurance_no VARCHAR(30),
    status VARCHAR(20) NOT NULL DEFAULT '正常',
    no_show_count INTEGER NOT NULL DEFAULT 0,
    is_blacklist BOOLEAN NOT NULL DEFAULT FALSE,
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Registration table (挂号记录表)
CREATE TABLE IF NOT EXISTS registration (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    patient_id VARCHAR(20) NOT NULL,
    patient_name VARCHAR(50) NOT NULL,
    id_card_no VARCHAR(18),
    gender VARCHAR(10) NOT NULL,
    age INTEGER,
    phone VARCHAR(20),
    dept_id VARCHAR(20) NOT NULL,
    dept_name VARCHAR(100),
    doctor_id VARCHAR(20),
    doctor_name VARCHAR(50),
    schedule_id VARCHAR(36),
    schedule_date DATE NOT NULL,
    time_period VARCHAR(20),
    queue_no INTEGER,
    visit_no VARCHAR(30),
    registration_type VARCHAR(20),
    registration_fee DECIMAL(10, 2),
    diagnosis_fee DECIMAL(10, 2),
    total_fee DECIMAL(10, 2),
    status VARCHAR(20) NOT NULL,
    visit_status VARCHAR(20),
    source VARCHAR(20),
    booking_time DATETIME,
    check_in_time DATETIME,
    start_time DATETIME,
    end_time DATETIME,
    operator_id VARCHAR(20),
    operator_name VARCHAR(50),
    clinic_room VARCHAR(50),
    cancel_reason VARCHAR(200),
    cancel_time DATETIME,
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_reg_patient ON registration(patient_id);
CREATE INDEX IF NOT EXISTS idx_reg_schedule ON registration(schedule_date, dept_id);
CREATE INDEX IF NOT EXISTS idx_reg_status ON registration(status, schedule_date);

-- Schedule table (排班信息表)
CREATE TABLE IF NOT EXISTS schedule (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    dept_id VARCHAR(20) NOT NULL,
    dept_name VARCHAR(100),
    doctor_id VARCHAR(20) NOT NULL,
    doctor_name VARCHAR(50),
    doctor_title VARCHAR(50),
    schedule_date DATE NOT NULL,
    time_period VARCHAR(20) NOT NULL,
    start_time TIME,
    end_time TIME,
    total_quota INTEGER NOT NULL DEFAULT 0,
    booked_quota INTEGER NOT NULL DEFAULT 0,
    available_quota INTEGER NOT NULL DEFAULT 0,
    registration_type VARCHAR(20),
    registration_fee DECIMAL(10, 2),
    diagnosis_fee DECIMAL(10, 2),
    status VARCHAR(20) NOT NULL DEFAULT '正常',
    stop_reason VARCHAR(200),
    clinic_room VARCHAR(50),
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (doctor_id, schedule_date, time_period)
);

CREATE INDEX IF NOT EXISTS idx_schedule_date ON schedule(schedule_date);
CREATE INDEX IF NOT EXISTS idx_schedule_dept_date ON schedule(dept_id, schedule_date);

-- Outpatient Prescription table (门诊处方表)
CREATE TABLE IF NOT EXISTS outpatient_prescription (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    prescription_no VARCHAR(30) NOT NULL UNIQUE,
    registration_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(20) NOT NULL,
    patient_name VARCHAR(50) NOT NULL,
    gender VARCHAR(10),
    age INTEGER,
    dept_id VARCHAR(20) NOT NULL,
    dept_name VARCHAR(100),
    doctor_id VARCHAR(20) NOT NULL,
    doctor_name VARCHAR(50),
    prescription_type VARCHAR(20) NOT NULL,
    prescription_date DATE NOT NULL,
    diagnosis_code VARCHAR(50),
    diagnosis_name VARCHAR(200),
    total_amount DECIMAL(10, 2),
    pay_status VARCHAR(20) DEFAULT '未收费',
    pay_time DATETIME,
    status VARCHAR(20) NOT NULL DEFAULT '有效',
    audit_status VARCHAR(20),
    auditor_id VARCHAR(20),
    auditor_name VARCHAR(50),
    audit_time DATETIME,
    audit_remark VARCHAR(500),
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pres_registration ON outpatient_prescription(registration_id);
CREATE INDEX IF NOT EXISTS idx_pres_patient ON outpatient_prescription(patient_id);
CREATE INDEX IF NOT EXISTS idx_pres_status ON outpatient_prescription(status, prescription_date);

-- Prescription Detail table (处方明细表)
CREATE TABLE IF NOT EXISTS prescription_detail (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    prescription_id VARCHAR(36) NOT NULL,
    drug_id VARCHAR(20) NOT NULL,
    drug_name VARCHAR(100) NOT NULL,
    drug_spec VARCHAR(50),
    drug_unit VARCHAR(20),
    drug_form VARCHAR(20),
    quantity DECIMAL(10, 2) NOT NULL,
    dosage VARCHAR(50),
    frequency VARCHAR(50),
    days INTEGER,
    route VARCHAR(50),
    unit_price DECIMAL(10, 4),
    amount DECIMAL(10, 2),
    group_no INTEGER,
    skin_test VARCHAR(10),
    skin_test_result VARCHAR(20),
    is_essential BOOLEAN,
    is_medical_insurance BOOLEAN,
    remark VARCHAR(200),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_detail_prescription ON prescription_detail(prescription_id);
CREATE INDEX IF NOT EXISTS idx_detail_drug ON prescription_detail(drug_id);

-- Queue table (排队信息表)
CREATE TABLE IF NOT EXISTS queue (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    queue_no INTEGER NOT NULL,
    registration_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(20) NOT NULL,
    patient_name VARCHAR(50),
    dept_id VARCHAR(20),
    dept_name VARCHAR(100),
    doctor_id VARCHAR(20),
    doctor_name VARCHAR(50),
    clinic_room VARCHAR(50),
    status VARCHAR(20) NOT NULL,
    call_time DATETIME,
    wait_time INTEGER,
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_queue_registration ON queue(registration_id);
CREATE INDEX IF NOT EXISTS idx_queue_status ON queue(status, dept_id);

-- Outpatient Record table (门诊就诊记录表)
CREATE TABLE IF NOT EXISTS outpatient_record (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    registration_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(20) NOT NULL,
    patient_name VARCHAR(50),
    dept_id VARCHAR(20),
    dept_name VARCHAR(100),
    doctor_id VARCHAR(20),
    doctor_name VARCHAR(50),
    visit_time DATETIME NOT NULL,
    chief_complaint TEXT,
    present_illness TEXT,
    past_history TEXT,
    physical_exam TEXT,
    diagnosis TEXT,
    diagnosis_code VARCHAR(100),
    treatment_plan TEXT,
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_out_rec_registration ON outpatient_record(registration_id);
CREATE INDEX IF NOT EXISTS idx_out_rec_patient ON outpatient_record(patient_id);

-- Billing Item table (收费项目表)
CREATE TABLE IF NOT EXISTS billing_item (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    registration_id VARCHAR(36) NOT NULL,
    item_code VARCHAR(30) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    item_type VARCHAR(20),
    unit_price DECIMAL(10, 4),
    quantity DECIMAL(10, 2),
    amount DECIMAL(10, 2),
    pay_status VARCHAR(20),
    remark VARCHAR(200),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_bill_item_registration ON billing_item(registration_id);

-- Billing Settlement table (收费结算表)
CREATE TABLE IF NOT EXISTS billing_settlement (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    settlement_no VARCHAR(30) NOT NULL UNIQUE,
    registration_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(20) NOT NULL,
    patient_name VARCHAR(50),
    total_amount DECIMAL(10, 2),
    pay_method VARCHAR(20),
    pay_time DATETIME,
    operator_id VARCHAR(20),
    operator_name VARCHAR(50),
    status VARCHAR(20),
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_bill_settlement_registration ON billing_settlement(registration_id);
CREATE INDEX IF NOT EXISTS idx_bill_settlement_patient ON billing_settlement(patient_id);

-- Examination Request table (检查申请表)
CREATE TABLE IF NOT EXISTS examination_request (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    request_no VARCHAR(30) NOT NULL UNIQUE,
    registration_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(20) NOT NULL,
    patient_name VARCHAR(50),
    exam_type VARCHAR(20) NOT NULL,
    exam_name VARCHAR(100),
    clinical_diagnosis TEXT,
    exam_purpose TEXT,
    request_doctor_id VARCHAR(20),
    request_doctor_name VARCHAR(50),
    request_time DATETIME NOT NULL,
    urgency VARCHAR(20),
    status VARCHAR(20),
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_exam_req_registration ON examination_request(registration_id);
CREATE INDEX IF NOT EXISTS idx_exam_req_patient ON examination_request(patient_id);
CREATE INDEX IF NOT EXISTS idx_exam_req_status ON examination_request(status);
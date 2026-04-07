-- ============================================
-- HIS Pharmacy Module - Initial Schema Migration
-- Version: V1
-- Description: Create pharmacy management tables
-- ============================================

-- Drug table (药品信息表)
CREATE TABLE IF NOT EXISTS drug (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    drug_code VARCHAR(30) NOT NULL UNIQUE,
    drug_name VARCHAR(100) NOT NULL,
    generic_name VARCHAR(100),
    trade_name VARCHAR(100),
    pinyin_code VARCHAR(50),
    custom_code VARCHAR(50),
    drug_category VARCHAR(20) NOT NULL,
    drug_form VARCHAR(20),
    drug_spec VARCHAR(50),
    drug_unit VARCHAR(20),
    package_unit VARCHAR(20),
    package_quantity INTEGER,
    manufacturer VARCHAR(100),
    origin VARCHAR(50),
    approval_no VARCHAR(50),
    purchase_price DECIMAL(10, 4),
    retail_price DECIMAL(10, 4) NOT NULL,
    price_date DATE,
    is_prescription BOOLEAN NOT NULL DEFAULT FALSE,
    is_otc BOOLEAN NOT NULL DEFAULT FALSE,
    is_essential BOOLEAN NOT NULL DEFAULT FALSE,
    is_insurance BOOLEAN NOT NULL DEFAULT FALSE,
    insurance_code VARCHAR(50),
    insurance_type VARCHAR(20),
    storage_condition VARCHAR(50),
    shelf_life INTEGER,
    alert_days INTEGER DEFAULT 180,
    min_stock DECIMAL(10, 2),
    max_stock DECIMAL(10, 2),
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_drug_code ON drug(drug_code);
CREATE INDEX IF NOT EXISTS idx_drug_name ON drug(drug_name);
CREATE INDEX IF NOT EXISTS idx_pinyin_code ON drug(pinyin_code);
CREATE INDEX IF NOT EXISTS idx_drug_category ON drug(drug_category);

-- Drug Inventory table (药品库存表)
CREATE TABLE IF NOT EXISTS drug_inventory (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    drug_id VARCHAR(36) NOT NULL,
    drug_code VARCHAR(30) NOT NULL,
    drug_name VARCHAR(100),
    drug_spec VARCHAR(50),
    drug_unit VARCHAR(20),
    pharmacy_id VARCHAR(20) NOT NULL,
    pharmacy_name VARCHAR(100),
    batch_no VARCHAR(50) NOT NULL,
    production_date DATE,
    expiry_date DATE NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL DEFAULT 0,
    locked_quantity DECIMAL(10, 2) NOT NULL DEFAULT 0,
    available_quantity DECIMAL(10, 2) NOT NULL DEFAULT 0,
    location VARCHAR(50),
    purchase_price DECIMAL(10, 4),
    retail_price DECIMAL(10, 4),
    supplier_id VARCHAR(20),
    supplier_name VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (drug_id, batch_no, pharmacy_id)
);

CREATE INDEX IF NOT EXISTS idx_inv_drug_id ON drug_inventory(drug_id);
CREATE INDEX IF NOT EXISTS idx_inv_expiry_date ON drug_inventory(expiry_date);
CREATE INDEX IF NOT EXISTS idx_inv_pharmacy_id ON drug_inventory(pharmacy_id);

-- Dispense Record table (发药记录表)
CREATE TABLE IF NOT EXISTS dispense_record (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    dispense_no VARCHAR(30) NOT NULL UNIQUE,
    prescription_id VARCHAR(36),
    prescription_no VARCHAR(30),
    patient_id VARCHAR(20) NOT NULL,
    patient_name VARCHAR(50),
    gender VARCHAR(1),
    age INTEGER,
    visit_type VARCHAR(20) NOT NULL,
    admission_id VARCHAR(36),
    dept_id VARCHAR(20),
    dept_name VARCHAR(100),
    doctor_id VARCHAR(20),
    doctor_name VARCHAR(50),
    pharmacy_id VARCHAR(20) NOT NULL,
    pharmacy_name VARCHAR(100),
    total_amount DECIMAL(10, 2),
    audit_status VARCHAR(20) DEFAULT 'PENDING',
    auditor_id VARCHAR(20),
    auditor_name VARCHAR(50),
    audit_time DATETIME,
    audit_remark VARCHAR(500),
    dispense_status VARCHAR(20) DEFAULT 'PENDING',
    dispenser_id VARCHAR(20),
    dispenser_name VARCHAR(50),
    dispense_time DATETIME,
    receive_confirm BOOLEAN NOT NULL DEFAULT FALSE,
    receive_time DATETIME,
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_dispense_no ON dispense_record(dispense_no);
CREATE INDEX IF NOT EXISTS idx_prescription_id ON dispense_record(prescription_id);
CREATE INDEX IF NOT EXISTS idx_patient_id ON dispense_record(patient_id);
CREATE INDEX IF NOT EXISTS idx_dispense_time ON dispense_record(dispense_time);

-- Dispense Detail table (发药明细表)
CREATE TABLE IF NOT EXISTS dispense_detail (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    dispense_id VARCHAR(36) NOT NULL,
    drug_id VARCHAR(36) NOT NULL,
    drug_code VARCHAR(30),
    drug_name VARCHAR(100),
    drug_spec VARCHAR(50),
    drug_unit VARCHAR(20),
    batch_no VARCHAR(50),
    expiry_date DATE,
    quantity DECIMAL(10, 2) NOT NULL,
    retail_price DECIMAL(10, 4),
    amount DECIMAL(10, 2),
    dosage VARCHAR(50),
    frequency VARCHAR(50),
    days INTEGER,
    route VARCHAR(50),
    prescription_detail_id VARCHAR(36),
    audit_result VARCHAR(20),
    audit_remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_detail_dispense_id ON dispense_detail(dispense_id);
CREATE INDEX IF NOT EXISTS idx_detail_drug_id ON dispense_detail(drug_id);

-- Purchase Order table (采购订单表)
CREATE TABLE IF NOT EXISTS purchase_order (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    order_no VARCHAR(30) NOT NULL UNIQUE,
    supplier_id VARCHAR(20) NOT NULL,
    supplier_name VARCHAR(100),
    order_date DATE NOT NULL,
    expected_date DATE,
    total_quantity DECIMAL(10, 2),
    total_amount DECIMAL(12, 2),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    applicant_id VARCHAR(20),
    applicant_name VARCHAR(50),
    apply_time DATETIME,
    auditor_id VARCHAR(20),
    auditor_name VARCHAR(50),
    audit_time DATETIME,
    audit_remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_order_no ON purchase_order(order_no);
CREATE INDEX IF NOT EXISTS idx_supplier_id ON purchase_order(supplier_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON purchase_order(status);

-- Purchase Order Item table (采购订单明细表)
CREATE TABLE IF NOT EXISTS purchase_order_item (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    drug_id VARCHAR(36) NOT NULL,
    drug_code VARCHAR(30),
    drug_name VARCHAR(100),
    drug_spec VARCHAR(50),
    drug_unit VARCHAR(20),
    quantity DECIMAL(10, 2) NOT NULL,
    received_quantity DECIMAL(10, 2) DEFAULT 0,
    purchase_price DECIMAL(10, 4),
    amount DECIMAL(10, 2),
    remark VARCHAR(200),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_item_order_id ON purchase_order_item(order_id);
CREATE INDEX IF NOT EXISTS idx_item_drug_id ON purchase_order_item(drug_id);

-- Inventory Transaction table (库存交易表)
CREATE TABLE IF NOT EXISTS inventory_transaction (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    transaction_no VARCHAR(30) NOT NULL UNIQUE,
    transaction_type VARCHAR(20) NOT NULL,
    drug_id VARCHAR(36) NOT NULL,
    drug_code VARCHAR(30),
    drug_name VARCHAR(100),
    pharmacy_id VARCHAR(20) NOT NULL,
    pharmacy_name VARCHAR(100),
    batch_no VARCHAR(50),
    quantity DECIMAL(10, 2) NOT NULL,
    before_quantity DECIMAL(10, 2),
    after_quantity DECIMAL(10, 2),
    transaction_time DATETIME NOT NULL,
    operator_id VARCHAR(20),
    operator_name VARCHAR(50),
    related_id VARCHAR(36),
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_trans_no ON inventory_transaction(transaction_no);
CREATE INDEX IF NOT EXISTS idx_trans_drug ON inventory_transaction(drug_id);
CREATE INDEX IF NOT EXISTS idx_trans_pharmacy ON inventory_transaction(pharmacy_id);
CREATE INDEX IF NOT EXISTS idx_trans_time ON inventory_transaction(transaction_time);
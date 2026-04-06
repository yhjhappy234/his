-- HIS全局数据库初始化脚本
-- his_global.db

-- 患者主索引表 (EMPI)
CREATE TABLE IF NOT EXISTS patient_master (
    id                  TEXT PRIMARY KEY,
    empi_id             TEXT UNIQUE NOT NULL,
    name                TEXT NOT NULL,
    gender              TEXT NOT NULL,
    birth_date          DATE,
    id_type             TEXT,
    id_no               TEXT UNIQUE,
    phone               TEXT,
    address             TEXT,
    emergency_contact   TEXT,
    emergency_phone     TEXT,
    blood_type          TEXT,
    allergy_history     TEXT,
    status              TEXT DEFAULT 'active',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id                  TEXT PRIMARY KEY,
    user_name           TEXT,
    login_name          TEXT UNIQUE NOT NULL,
    password_hash       TEXT NOT NULL,
    real_name           TEXT,
    employee_id         TEXT,
    dept_id             TEXT,
    dept_name           TEXT,
    phone               TEXT,
    email               TEXT,
    id_card             TEXT,
    user_type           TEXT,
    login_type          TEXT,
    password_expiry     DATE,
    password_update_time DATETIME,
    last_login_time     DATETIME,
    last_login_ip       TEXT,
    login_fail_count    INTEGER DEFAULT 0,
    lock_time           DATETIME,
    session_timeout     INTEGER DEFAULT 30,
    status              TEXT DEFAULT '正常',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 科室表
CREATE TABLE IF NOT EXISTS sys_department (
    id                  TEXT PRIMARY KEY,
    dept_code           TEXT UNIQUE NOT NULL,
    dept_name           TEXT NOT NULL,
    dept_alias          TEXT,
    dept_type           TEXT NOT NULL,
    dept_category       TEXT,
    parent_id           TEXT,
    dept_level          INTEGER,
    dept_path           TEXT,
    dept_leader_id      TEXT,
    dept_leader_name    TEXT,
    dept_phone          TEXT,
    dept_location       TEXT,
    bed_count           INTEGER,
    outpatient_room     TEXT,
    sort_order          INTEGER,
    status              TEXT DEFAULT '正常',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id                  TEXT PRIMARY KEY,
    role_code           TEXT UNIQUE NOT NULL,
    role_name           TEXT NOT NULL,
    role_description    TEXT,
    is_system           INTEGER DEFAULT 0,
    is_enabled          INTEGER DEFAULT 1,
    sort_order          INTEGER,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id                  TEXT PRIMARY KEY,
    permission_code     TEXT UNIQUE NOT NULL,
    permission_name     TEXT NOT NULL,
    permission_type     TEXT,
    parent_id           TEXT,
    permission_path     TEXT,
    resource_type       TEXT,
    resource_path       TEXT,
    sort_order          INTEGER,
    is_enabled          INTEGER DEFAULT 1,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id                  TEXT PRIMARY KEY,
    user_id             TEXT NOT NULL,
    role_id             TEXT NOT NULL,
    grant_time          DATETIME,
    grant_by            TEXT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id)
);

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id                  TEXT PRIMARY KEY,
    role_id             TEXT NOT NULL,
    permission_id       TEXT NOT NULL,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, permission_id)
);

-- 数据字典表
CREATE TABLE IF NOT EXISTS sys_dictionary (
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
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(dict_type, dict_code)
);

-- 系统参数表
CREATE TABLE IF NOT EXISTS sys_parameter (
    id                  TEXT PRIMARY KEY,
    param_code          TEXT UNIQUE NOT NULL,
    param_name          TEXT NOT NULL,
    param_value         TEXT,
    param_type          TEXT,
    param_group         TEXT,
    description         TEXT,
    is_system           INTEGER DEFAULT 0,
    is_editable         INTEGER DEFAULT 1,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 全局序列号表
CREATE TABLE IF NOT EXISTS sys_sequence (
    seq_name            TEXT PRIMARY KEY,
    current_value       INTEGER DEFAULT 0,
    prefix              TEXT,
    date_format         TEXT
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id                  TEXT PRIMARY KEY,
    log_no              TEXT,
    user_id             TEXT,
    login_name          TEXT,
    real_name           TEXT,
    dept_id             TEXT,
    dept_name           TEXT,
    operation_type      TEXT,
    operation_module    TEXT,
    operation_func      TEXT,
    operation_desc      TEXT,
    request_method      TEXT,
    request_url         TEXT,
    request_param       TEXT,
    response_data       TEXT,
    operation_result    TEXT,
    error_msg           TEXT,
    operation_time      DATETIME NOT NULL,
    duration            INTEGER,
    client_ip           TEXT,
    server_ip           TEXT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 审计日志表
CREATE TABLE IF NOT EXISTS sys_audit_log (
    id                  TEXT PRIMARY KEY,
    audit_type          TEXT NOT NULL,
    user_id             TEXT,
    login_name          TEXT,
    real_name           TEXT,
    audit_event         TEXT,
    audit_desc          TEXT,
    audit_level         TEXT,
    before_data         TEXT,
    after_data          TEXT,
    client_ip           TEXT,
    audit_time          DATETIME NOT NULL,
    is_alerted          INTEGER,
    alert_time          DATETIME,
    alert_way           TEXT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_user_login_name ON sys_user(login_name);
CREATE INDEX IF NOT EXISTS idx_user_employee ON sys_user(employee_id);
CREATE INDEX IF NOT EXISTS idx_user_status ON sys_user(status);
CREATE INDEX IF NOT EXISTS idx_dept_code ON sys_department(dept_code);
CREATE INDEX IF NOT EXISTS idx_dept_type ON sys_department(dept_type);
CREATE INDEX IF NOT EXISTS idx_dict_type ON sys_dictionary(dict_type);
CREATE INDEX IF NOT EXISTS idx_param_code ON sys_parameter(param_code);
CREATE INDEX IF NOT EXISTS idx_operation_time ON sys_operation_log(operation_time);
CREATE INDEX IF NOT EXISTS idx_audit_time ON sys_audit_log(audit_time);

-- 初始化系统参数
INSERT INTO sys_parameter (id, param_code, param_name, param_value, param_type, param_group, description, is_system, is_editable)
VALUES
    ('1', 'hospital_name', '医院名称', '智慧医院', 'system', 'basic', '医院名称', 1, 1),
    ('2', 'registration_quota', '号源上限', '50', 'business', 'outpatient', '每医生每日号源上限', 0, 1),
    ('3', 'prescription_limit_days', '处方限量天数', '7', 'business', 'pharmacy', '普通门诊处方限量天数', 0, 1),
    ('4', 'session_timeout', '会话超时时间', '30', 'security', 'auth', '会话超时时间(分钟)', 0, 1);

-- 初始化字典类型
INSERT INTO sys_dictionary (id, dict_type, dict_code, dict_name, sort_order, is_enabled)
VALUES
    ('1', 'gender', 'M', '男', 1, 1),
    ('2', 'gender', 'F', '女', 2, 1),
    ('3', 'gender', 'U', '未知', 3, 1),
    ('4', 'blood_type', 'A', 'A型', 1, 1),
    ('5', 'blood_type', 'B', 'B型', 2, 1),
    ('6', 'blood_type', 'AB', 'AB型', 3, 1),
    ('7', 'blood_type', 'O', 'O型', 4, 1),
    ('8', 'blood_type', 'U', '未知', 5, 1),
    ('9', 'status', '1', '正常', 1, 1),
    ('10', 'status', '0', '停用', 2, 1),
    ('11', 'visit_type', 'outpatient', '门诊', 1, 1),
    ('12', 'visit_type', 'inpatient', '住院', 2, 1),
    ('13', 'visit_type', 'emergency', '急诊', 3, 1),
    ('14', 'nursing_level', 'special', '特级护理', 1, 1),
    ('15', 'nursing_level', 'first', '一级护理', 2, 1),
    ('16', 'nursing_level', 'second', '二级护理', 3, 1),
    ('17', 'nursing_level', 'third', '三级护理', 4, 1);

-- 初始化序列
INSERT INTO sys_sequence (seq_name, current_value, prefix, date_format)
VALUES
    ('patient', 0, 'P', 'yyyyMMdd'),
    ('registration', 0, 'REG', 'yyyyMMdd'),
    ('prescription', 0, 'RX', 'yyyyMMdd'),
    ('admission', 0, 'ZY', 'yyyyMMdd'),
    ('order', 0, 'MO', 'yyyyMMdd'),
    ('sample', 0, 'BL', 'yyyyMMdd'),
    ('report', 0, 'RP', 'yyyyMMdd'),
    ('invoice', 0, 'INV', 'yyyyMMdd'),
    ('prepayment', 0, 'PRE', 'yyyyMMdd'),
    ('settlement', 0, 'SET', 'yyyyMMdd');
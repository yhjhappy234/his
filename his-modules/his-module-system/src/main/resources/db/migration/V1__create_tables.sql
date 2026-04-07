-- ============================================
-- HIS System Module - Initial Schema Migration
-- Version: V1
-- Description: Create system management tables
-- ============================================

-- User table (用户表)
CREATE TABLE IF NOT EXISTS sys_user (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    login_name VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    employee_id VARCHAR(20),
    real_name VARCHAR(50),
    dept_id VARCHAR(36),
    dept_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(50),
    id_card VARCHAR(18),
    user_type VARCHAR(20),
    login_type VARCHAR(20),
    password_expiry DATE,
    password_update_time DATETIME,
    last_login_time DATETIME,
    last_login_ip VARCHAR(50),
    login_fail_count INTEGER NOT NULL DEFAULT 0,
    lock_time DATETIME,
    session_timeout INTEGER NOT NULL DEFAULT 30,
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    data_scope VARCHAR(20),
    avatar VARCHAR(200),
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_login_name ON sys_user(login_name);
CREATE INDEX IF NOT EXISTS idx_employee_id ON sys_user(employee_id);
CREATE INDEX IF NOT EXISTS idx_status ON sys_user(status);
CREATE INDEX IF NOT EXISTS idx_dept_id ON sys_user(dept_id);

-- Role table (角色表)
CREATE TABLE IF NOT EXISTS sys_role (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    role_code VARCHAR(30) NOT NULL UNIQUE,
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    data_scope VARCHAR(20),
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_role_code ON sys_role(role_code);

-- Permission table (权限表)
CREATE TABLE IF NOT EXISTS sys_permission (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    perm_code VARCHAR(100) NOT NULL UNIQUE,
    perm_name VARCHAR(50) NOT NULL,
    perm_type VARCHAR(20) NOT NULL,
    parent_id VARCHAR(36),
    path VARCHAR(200),
    icon VARCHAR(100),
    sort_order INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_perm_code ON sys_permission(perm_code);
CREATE INDEX IF NOT EXISTS idx_parent_id ON sys_permission(parent_id);

-- Department table (科室表)
CREATE TABLE IF NOT EXISTS sys_department (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    dept_code VARCHAR(20) NOT NULL UNIQUE,
    dept_name VARCHAR(100) NOT NULL,
    short_name VARCHAR(50),
    parent_id VARCHAR(36),
    dept_level INTEGER NOT NULL DEFAULT 1,
    dept_type VARCHAR(20) NOT NULL,
    leader_id VARCHAR(36),
    leader_name VARCHAR(50),
    phone VARCHAR(20),
    address VARCHAR(200),
    sort_order INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_dept_code ON sys_department(dept_code);
CREATE INDEX IF NOT EXISTS idx_parent_id ON sys_department(parent_id);
CREATE INDEX IF NOT EXISTS idx_dept_type ON sys_department(dept_type);

-- Data Dictionary table (数据字典表)
CREATE TABLE IF NOT EXISTS sys_dictionary (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    dict_type VARCHAR(50) NOT NULL,
    dict_code VARCHAR(50) NOT NULL,
    dict_name VARCHAR(100) NOT NULL,
    dict_value VARCHAR(200),
    parent_code VARCHAR(50),
    dict_level INTEGER NOT NULL DEFAULT 1,
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    description VARCHAR(200),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (dict_type, dict_code)
);

CREATE INDEX IF NOT EXISTS idx_dict_type ON sys_dictionary(dict_type);
CREATE INDEX IF NOT EXISTS idx_parent_code ON sys_dictionary(parent_code);

-- User Role Association table (用户角色关联表)
CREATE TABLE IF NOT EXISTS sys_user_role (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (user_id, role_id)
);

CREATE INDEX IF NOT EXISTS idx_user_id ON sys_user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_role_id ON sys_user_role(role_id);

-- Role Permission Association table (角色权限关联表)
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    role_id VARCHAR(36) NOT NULL,
    perm_id VARCHAR(36) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (role_id, perm_id)
);

CREATE INDEX IF NOT EXISTS idx_rp_role_id ON sys_role_permission(role_id);
CREATE INDEX IF NOT EXISTS idx_rp_perm_id ON sys_role_permission(perm_id);

-- Audit Log table (审计日志表)
CREATE TABLE IF NOT EXISTS audit_log (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    operation_type VARCHAR(50) NOT NULL,
    module VARCHAR(50),
    business_type VARCHAR(50),
    request_method VARCHAR(10),
    request_url VARCHAR(200),
    request_params TEXT,
    response_result TEXT,
    operator_id VARCHAR(20),
    operator_name VARCHAR(50),
    operator_ip VARCHAR(50),
    execution_time INTEGER,
    status VARCHAR(20),
    error_msg TEXT,
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_audit_module ON audit_log(module);
CREATE INDEX IF NOT EXISTS idx_audit_operator ON audit_log(operator_id);
CREATE INDEX IF NOT EXISTS idx_audit_time ON audit_log(create_time);

-- Operation Log table (操作日志表)
CREATE TABLE IF NOT EXISTS operation_log (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    log_type VARCHAR(20) NOT NULL,
    title VARCHAR(100),
    content TEXT,
    business_id VARCHAR(36),
    operator_id VARCHAR(20),
    operator_name VARCHAR(50),
    dept_id VARCHAR(36),
    dept_name VARCHAR(100),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_op_log_type ON operation_log(log_type);
CREATE INDEX IF NOT EXISTS idx_op_log_operator ON operation_log(operator_id);

-- System Parameter table (系统参数表)
CREATE TABLE IF NOT EXISTS system_parameter (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    param_code VARCHAR(50) NOT NULL UNIQUE,
    param_name VARCHAR(100) NOT NULL,
    param_value TEXT,
    param_type VARCHAR(20),
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_param_code ON system_parameter(param_code);
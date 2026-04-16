-- ============================================
-- HIS System Module - Initial Data Migration
-- Version: V2
-- Description: Insert default users and roles
-- ============================================

-- Insert default admin user (password: 123456, hashed with BCrypt)
-- BCrypt hash for '123456': $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH
INSERT INTO sys_user (id, user_name, login_name, password, real_name, dept_id, dept_name, user_type, login_type, session_timeout, status, data_scope, create_time, deleted)
VALUES (
    'user-admin-001',
    '系统管理员',
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',
    '系统管理员',
    'dept-admin',
    '系统管理部',
    'ADMIN',
    'PASSWORD',
    60,
    'NORMAL',
    'ALL',
    datetime('now', 'localtime'),
    0
) ON CONFLICT(login_name) DO NOTHING;

-- Insert default admin role
INSERT INTO sys_role (id, role_code, role_name, description, data_scope, sort_order, is_system, status, create_time, deleted)
VALUES (
    'role-admin-001',
    'ADMIN',
    '系统管理员',
    '系统管理员角色，拥有所有权限',
    'ALL',
    1,
    1,
    'NORMAL',
    datetime('now', 'localtime'),
    0
) ON CONFLICT(role_code) DO NOTHING;

-- Insert default user role association
INSERT INTO sys_user_role (id, user_id, role_id, create_time, deleted)
VALUES (
    'ur-admin-001',
    'user-admin-001',
    'role-admin-001',
    datetime('now', 'localtime'),
    0
) ON CONFLICT(user_id, role_id) DO NOTHING;

-- Insert test doctor user (password: 123456)
INSERT INTO sys_user (id, user_name, login_name, password, real_name, dept_id, dept_name, user_type, login_type, session_timeout, status, data_scope, create_time, deleted)
VALUES (
    'user-doctor-001',
    '测试医生',
    'test_doctor',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',
    '张医生',
    'dept-im',
    '内科',
    'DOCTOR',
    'PASSWORD',
    30,
    'NORMAL',
    'DEPT',
    datetime('now', 'localtime'),
    0
) ON CONFLICT(login_name) DO NOTHING;

-- Insert doctor role
INSERT INTO sys_role (id, role_code, role_name, description, data_scope, sort_order, is_system, status, create_time, deleted)
VALUES (
    'role-doctor-001',
    'DOCTOR',
    '医生',
    '医生角色',
    'DEPT',
    2,
    0,
    'NORMAL',
    datetime('now', 'localtime'),
    0
) ON CONFLICT(role_code) DO NOTHING;

-- Insert doctor role association
INSERT INTO sys_user_role (id, user_id, role_id, create_time, deleted)
VALUES (
    'ur-doctor-001',
    'user-doctor-001',
    'role-doctor-001',
    datetime('now', 'localtime'),
    0
) ON CONFLICT(user_id, role_id) DO NOTHING;

-- Insert default department (系统管理部)
INSERT INTO sys_department (id, dept_code, dept_name, short_name, dept_level, dept_type, status, create_time, deleted)
VALUES (
    'dept-admin',
    'SYS',
    '系统管理部',
    '管理部',
    1,
    'ADMIN',
    'NORMAL',
    datetime('now', 'localtime'),
    0
) ON CONFLICT(dept_code) DO NOTHING;

-- Insert default department (内科)
INSERT INTO sys_department (id, dept_code, dept_name, short_name, dept_level, dept_type, status, create_time, deleted)
VALUES (
    'dept-im',
    'IM',
    '内科',
    '内科',
    1,
    'CLINICAL',
    'NORMAL',
    datetime('now', 'localtime'),
    0
) ON CONFLICT(dept_code) DO NOTHING;

-- Insert basic permissions
INSERT INTO sys_permission (id, perm_code, perm_name, perm_type, path, sort_order, status, create_time, deleted)
VALUES
    ('perm-sys-001', 'sys:user:view', '用户查看', 'MENU', '/system/user', 1, 'NORMAL', datetime('now', 'localtime'), 0),
    ('perm-sys-002', 'sys:user:edit', '用户编辑', 'BUTTON', '/system/user/edit', 2, 'NORMAL', datetime('now', 'localtime'), 0),
    ('perm-sys-003', 'sys:role:view', '角色查看', 'MENU', '/system/role', 3, 'NORMAL', datetime('now', 'localtime'), 0),
    ('perm-sys-004', 'sys:dept:view', '科室查看', 'MENU', '/system/dept', 4, 'NORMAL', datetime('now', 'localtime'), 0),
    ('perm-sys-005', 'sys:dict:view', '字典查看', 'MENU', '/system/dict', 5, 'NORMAL', datetime('now', 'localtime'), 0)
ON CONFLICT(perm_code) DO NOTHING;

-- Assign all permissions to admin role
INSERT INTO sys_role_permission (id, role_id, perm_id, create_time, deleted)
VALUES
    ('rp-001', 'role-admin-001', 'perm-sys-001', datetime('now', 'localtime'), 0),
    ('rp-002', 'role-admin-001', 'perm-sys-002', datetime('now', 'localtime'), 0),
    ('rp-003', 'role-admin-001', 'perm-sys-003', datetime('now', 'localtime'), 0),
    ('rp-004', 'role-admin-001', 'perm-sys-004', datetime('now', 'localtime'), 0),
    ('rp-005', 'role-admin-001', 'perm-sys-005', datetime('now', 'localtime'), 0)
ON CONFLICT(role_id, perm_id) DO NOTHING;
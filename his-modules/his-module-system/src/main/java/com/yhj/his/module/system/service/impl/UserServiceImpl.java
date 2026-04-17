package com.yhj.his.module.system.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.system.dto.*;
import com.yhj.his.module.system.entity.*;
import com.yhj.his.module.system.enums.AuditLevel;
import com.yhj.his.module.system.enums.AuditType;
import com.yhj.his.module.system.enums.DataScopeLevel;
import com.yhj.his.module.system.enums.UserStatus;
import com.yhj.his.module.system.repository.*;
import com.yhj.his.module.system.repository.SystemDepartmentRepository;
import com.yhj.his.module.system.service.*;
import com.yhj.his.module.system.vo.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final SystemDepartmentRepository departmentRepository;
    private final AuditLogService auditLogService;

    // JWT密钥(实际应从配置读取)
    private static final String JWT_SECRET = "his-hospital-information-system-secret-key-2024";
    // Token过期时间(小时)
    private static final int TOKEN_EXPIRY_HOURS = 8;
    // 最大登录失败次数
    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    // 锁定时长(分钟)
    private static final int LOCK_DURATION_MINUTES = 30;

    @Override
    public Result<LoginVO> login(LoginDTO dto, String clientIp) {
        // 查找用户
        User user = userRepository.findByLoginNameAndDeletedFalse(dto.getLoginName())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_ERROR, "账号不存在"));

        // 检查账号状态
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }
        if (user.getStatus() == UserStatus.LOCKED) {
            // 检查锁定是否过期
            if (user.getLockTime() != null &&
                LocalDateTime.now().isBefore(user.getLockTime().plusMinutes(LOCK_DURATION_MINUTES))) {
                throw new BusinessException(ErrorCode.ACCOUNT_LOCKED, "账号已锁定，请稍后再试");
            } else {
                // 解锁账号
                user.setStatus(UserStatus.NORMAL);
                user.setLoginFailCount(0);
                user.setLockTime(null);
            }
        }

        // 验证密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            // 记录失败次数
            user.setLoginFailCount(user.getLoginFailCount() + 1);
            if (user.getLoginFailCount() >= MAX_LOGIN_FAIL_COUNT) {
                user.setStatus(UserStatus.LOCKED);
                user.setLockTime(LocalDateTime.now());
                userRepository.save(user);

                // 记录审计日志
                recordAuditLog(user, AuditType.LOGIN, "账号锁定", AuditLevel.WARNING,
                        "连续登录失败" + MAX_LOGIN_FAIL_COUNT + "次，账号已锁定", clientIp);

                throw new BusinessException(ErrorCode.ACCOUNT_LOCKED, "密码错误次数过多，账号已锁定30分钟");
            }
            userRepository.save(user);
            throw new BusinessException(ErrorCode.PASSWORD_ERROR,
                    "密码错误，剩余尝试次数: " + (MAX_LOGIN_FAIL_COUNT - user.getLoginFailCount()));
        }

        // 登录成功
        user.setLoginFailCount(0);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(clientIp);
        userRepository.save(user);

        // 生成Token
        String token = generateToken(user);
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS);

        // 获取用户角色和权限
        List<RoleVO> roles = getUserRoleList(user.getId());
        List<String> permissions = getUserPermissionCodes(user.getId());

        // 构建返回结果
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUserName(user.getUserName());
        vo.setRealName(user.getRealName());
        vo.setLoginName(user.getLoginName());
        vo.setDeptId(user.getDeptId());
        vo.setDeptName(user.getDeptName());
        vo.setAvatar(user.getAvatar());
        vo.setToken(token);
        vo.setTokenExpiry(tokenExpiry);
        vo.setRoles(roles);
        vo.setPermissions(permissions);
        vo.setDataScope(user.getDataScope() != null ? user.getDataScope().getCode() : null);

        // 记录审计日志
        recordAuditLog(user, AuditType.LOGIN, "登录成功", AuditLevel.NORMAL,
                "用户登录成功，IP: " + clientIp, clientIp);

        return Result.success("登录成功", vo);
    }

    @Override
    public Result<Void> logout(String userId) {
        // 记录审计日志
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            recordAuditLog(user, AuditType.LOGIN, "退出登录", AuditLevel.NORMAL,
                    "用户退出登录", user.getLastLoginIp());
        }
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<UserVO> create(UserDTO dto) {
        // 检查登录账号是否存在
        if (dto.getLoginName() != null && userRepository.existsByLoginNameAndDeletedFalse(dto.getLoginName())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "登录账号已存在");
        }

        // 检查员工ID是否存在
        if (dto.getEmployeeId() != null && userRepository.existsByEmployeeIdAndDeletedFalse(dto.getEmployeeId())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "员工ID已绑定其他账号");
        }

        // 创建用户
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setLoginName(dto.getLoginName());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setEmployeeId(dto.getEmployeeId());
        user.setRealName(dto.getRealName());
        user.setDeptId(dto.getDeptId());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setIdCard(dto.getIdCard());
        user.setUserType(dto.getUserType());
        user.setLoginType(dto.getLoginType());
        user.setSessionTimeout(dto.getSessionTimeout() != null ? dto.getSessionTimeout() : 30);
        user.setAvatar(dto.getAvatar());
        user.setRemark(dto.getRemark());

        // 设置科室名称
        if (dto.getDeptId() != null) {
            Department dept = departmentRepository.findById(dto.getDeptId()).orElse(null);
            if (dept != null) {
                user.setDeptName(dept.getDeptName());
            }
        }

        // 设置数据权限级别
        if (dto.getDataScope() != null) {
            user.setDataScope(DataScopeLevel.fromCode(dto.getDataScope()));
        }

        user.setStatus(UserStatus.NORMAL);
        user.setPasswordUpdateTime(LocalDateTime.now());

        user = userRepository.save(user);

        // 授权角色
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            authorizeUserRoles(user.getId(), dto.getRoleIds());
        }

        // 记录审计日志
        recordAuditLog(user, AuditType.SYSTEM, "创建用户", AuditLevel.NORMAL,
                "创建用户: " + user.getLoginName(), null);

        return Result.success("创建成功", convertToVO(user));
    }

    @Override
    @Transactional
    public Result<UserVO> update(UserDTO dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在"));

        if (user.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户已删除");
        }

        // 更新基本信息
        user.setUserName(dto.getUserName());
        user.setEmployeeId(dto.getEmployeeId());
        user.setRealName(dto.getRealName());
        user.setDeptId(dto.getDeptId());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setIdCard(dto.getIdCard());
        user.setUserType(dto.getUserType());
        user.setLoginType(dto.getLoginType());
        user.setSessionTimeout(dto.getSessionTimeout());
        user.setAvatar(dto.getAvatar());
        user.setRemark(dto.getRemark());

        // 更新科室名称
        if (dto.getDeptId() != null) {
            Department dept = departmentRepository.findById(dto.getDeptId()).orElse(null);
            if (dept != null) {
                user.setDeptName(dept.getDeptName());
            }
        }

        // 更新数据权限级别
        if (dto.getDataScope() != null) {
            user.setDataScope(DataScopeLevel.fromCode(dto.getDataScope()));
        }

        // 更新密码
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(dto.getPassword()));
            user.setPasswordUpdateTime(LocalDateTime.now());
        }

        user = userRepository.save(user);

        // 更新角色授权
        if (dto.getRoleIds() != null) {
            userRoleRepository.deleteByUserId(user.getId());
            if (!dto.getRoleIds().isEmpty()) {
                authorizeUserRoles(user.getId(), dto.getRoleIds());
            }
        }

        // 记录审计日志
        recordAuditLog(user, AuditType.SYSTEM, "更新用户", AuditLevel.NORMAL,
                "更新用户: " + user.getLoginName(), null);

        return Result.success("更新成功", convertToVO(user));
    }

    @Override
    @Transactional
    public Result<Void> delete(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在"));

        if (user.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户已删除");
        }

        // 逻辑删除
        user.setDeleted(true);
        userRepository.save(user);

        // 删除用户角色关联
        userRoleRepository.deleteByUserId(userId);

        // 记录审计日志
        recordAuditLog(user, AuditType.SYSTEM, "删除用户", AuditLevel.WARNING,
                "删除用户: " + user.getLoginName(), null);

        return Result.successVoid();
    }

    @Override
    public Result<UserVO> getById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在"));

        if (user.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户已删除");
        }

        UserVO vo = convertToVO(user);
        vo.setRoles(getUserRoleList(userId));
        return Result.success(vo);
    }

    @Override
    public Result<PageResult<UserVO>> page(String userName, String loginName, String deptId,
                                           String status, String phone, Integer pageNum, Integer pageSize) {
        UserStatus userStatus = status != null ? UserStatus.fromCode(status) : null;
        Page<User> page = userRepository.findByCondition(userName, loginName, deptId, userStatus, phone,
                PageUtils.of(pageNum, pageSize));

        List<UserVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<UserPermissionVO> getUserPermission(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在"));

        UserPermissionVO vo = new UserPermissionVO();
        vo.setUserId(userId);
        vo.setUserName(user.getUserName());
        vo.setRoles(getUserRoleList(userId));
        vo.setPermissions(getUserPermissionCodes(userId));
        vo.setDataScope(user.getDataScope() != null ? user.getDataScope().getCode() : null);

        return Result.success(vo);
    }

    @Override
    @Transactional
    public Result<Void> changePassword(String userId, PasswordChangeDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在"));

        // 验证旧密码
        if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, "旧密码错误");
        }

        // 验证新密码和确认密码一致
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "新密码和确认密码不一致");
        }

        // 更新密码
        user.setPassword(BCrypt.hashpw(dto.getNewPassword()));
        user.setPasswordUpdateTime(LocalDateTime.now());
        userRepository.save(user);

        // 记录审计日志
        recordAuditLog(user, AuditType.SECURITY, "修改密码", AuditLevel.WARNING,
                "用户修改密码", null);

        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> resetPassword(String userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在"));

        user.setPassword(BCrypt.hashpw(newPassword));
        user.setPasswordUpdateTime(LocalDateTime.now());
        user.setLoginFailCount(0);
        user.setStatus(UserStatus.NORMAL);
        user.setLockTime(null);
        userRepository.save(user);

        // 记录审计日志
        recordAuditLog(user, AuditType.SECURITY, "重置密码", AuditLevel.WARNING,
                "管理员重置用户密码", null);

        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> updateStatus(String userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在"));

        UserStatus newStatus = UserStatus.fromCode(status);
        if (newStatus == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "无效的状态值");
        }

        user.setStatus(newStatus);
        if (newStatus == UserStatus.NORMAL) {
            user.setLoginFailCount(0);
            user.setLockTime(null);
        }
        userRepository.save(user);

        // 记录审计日志
        recordAuditLog(user, AuditType.SECURITY, "更新状态", AuditLevel.WARNING,
                "用户状态变更为: " + newStatus.getName(), null);

        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> authorizeRoles(UserAuthorizationDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在"));

        // 删除原有角色
        userRoleRepository.deleteByUserId(dto.getUserId());

        // 授权新角色
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            authorizeUserRoles(dto.getUserId(), dto.getRoleIds());
        }

        // 记录审计日志
        recordAuditLog(user, AuditType.PERMISSION, "用户授权", AuditLevel.WARNING,
                "用户角色授权: " + dto.getRoleIds(), null);

        return Result.successVoid();
    }

    @Override
    public Result<List<UserVO>> listByDeptId(String deptId) {
        List<User> users = userRepository.findByDeptIdAndDeletedFalse(deptId);
        List<UserVO> list = users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    /**
     * 生成JWT Token
     */
    private String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getId())
                .claim("loginName", user.getLoginName())
                .claim("userName", user.getUserName())
                .issuedAt(Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()))
                .expiration(Date.from(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS)
                        .atZone(java.time.ZoneId.systemDefault()).toInstant()))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))
                .compact();
    }

    /**
     * 获取用户角色列表
     */
    private List<RoleVO> getUserRoleList(String userId) {
        List<String> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Role> roles = roleRepository.findByIdInAndDeletedFalse(roleIds);
        return roles.stream()
                .map(this::convertRoleToVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户权限编码列表
     */
    private List<String> getUserPermissionCodes(String userId) {
        List<String> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> permIds = rolePermissionRepository.findPermIdsByRoleIds(roleIds);
        if (permIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Permission> permissions = permissionRepository.findByIdInAndDeletedFalse(permIds);
        return permissions.stream()
                .map(Permission::getPermCode)
                .collect(Collectors.toList());
    }

    /**
     * 授权用户角色
     */
    private void authorizeUserRoles(String userId, List<String> roleIds) {
        for (String roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleRepository.save(userRole);
        }
    }

    /**
     * 记录审计日志
     */
    private void recordAuditLog(User user, AuditType type, String event, AuditLevel level,
                                String desc, String clientIp) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditType(type);
        auditLog.setUserId(user.getId());
        auditLog.setLoginName(user.getLoginName());
        auditLog.setRealName(user.getRealName());
        auditLog.setAuditEvent(event);
        auditLog.setAuditDesc(desc);
        auditLog.setAuditLevel(level);
        auditLog.setClientIp(clientIp);
        auditLog.setAuditTime(LocalDateTime.now());
        auditLogService.log(auditLog);
    }

    /**
     * 转换User实体到VO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUserName(user.getUserName());
        vo.setLoginName(user.getLoginName());
        vo.setEmployeeId(user.getEmployeeId());
        vo.setRealName(user.getRealName());
        vo.setDeptId(user.getDeptId());
        vo.setDeptName(user.getDeptName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setIdCard(user.getIdCard());
        vo.setUserType(user.getUserType());
        vo.setLoginType(user.getLoginType());
        vo.setPasswordExpiry(user.getPasswordExpiry());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setLastLoginIp(user.getLastLoginIp());
        vo.setLoginFailCount(user.getLoginFailCount());
        vo.setSessionTimeout(user.getSessionTimeout());
        vo.setStatus(user.getStatus().getCode());
        vo.setDataScope(user.getDataScope() != null ? user.getDataScope().getCode() : null);
        vo.setAvatar(user.getAvatar());
        vo.setRemark(user.getRemark());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }

    /**
     * 转换Role实体到VO
     */
    private RoleVO convertRoleToVO(Role role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleName(role.getRoleName());
        vo.setDescription(role.getDescription());
        vo.setDataScope(role.getDataScope() != null ? role.getDataScope().getCode() : null);
        vo.setSortOrder(role.getSortOrder());
        vo.setIsSystem(role.getIsSystem());
        vo.setStatus(role.getStatus());
        vo.setRemark(role.getRemark());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }
}
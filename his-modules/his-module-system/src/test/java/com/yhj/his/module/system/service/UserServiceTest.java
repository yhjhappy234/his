package com.yhj.his.module.system.service;

import cn.hutool.crypto.digest.BCrypt;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.system.dto.*;
import com.yhj.his.module.system.entity.*;
import com.yhj.his.module.system.enums.DataScopeLevel;
import com.yhj.his.module.system.enums.UserStatus;
import com.yhj.his.module.system.repository.*;
import com.yhj.his.module.system.service.impl.UserServiceImpl;
import com.yhj.his.module.system.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 * 目标覆盖率: 90%+
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;
    private LoginDTO testLoginDTO;
    private Role testRole;
    private Department testDepartment;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new User();
        testUser.setId("user-001");
        testUser.setUserName("测试用户");
        testUser.setLoginName("testuser");
        testUser.setPassword(BCrypt.hashpw("password123"));
        testUser.setRealName("张三");
        testUser.setDeptId("dept-001");
        testUser.setDeptName("测试科室");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setStatus(UserStatus.NORMAL);
        testUser.setLoginFailCount(0);
        testUser.setSessionTimeout(30);
        testUser.setDataScope(DataScopeLevel.ALL);
        testUser.setDeleted(false);
        testUser.setCreateTime(LocalDateTime.now());

        // 初始化测试DTO
        testUserDTO = new UserDTO();
        testUserDTO.setUserName("新用户");
        testUserDTO.setLoginName("newuser");
        testUserDTO.setPassword("newpassword123");
        testUserDTO.setRealName("李四");
        testUserDTO.setDeptId("dept-001");
        testUserDTO.setPhone("13900139000");
        testUserDTO.setEmail("new@example.com");
        testUserDTO.setDataScope("ALL");
        testUserDTO.setRoleIds(Arrays.asList("role-001"));

        // 初始化登录DTO
        testLoginDTO = new LoginDTO();
        testLoginDTO.setLoginName("testuser");
        testLoginDTO.setPassword("password123");

        // 初始化测试角色
        testRole = new Role();
        testRole.setId("role-001");
        testRole.setRoleCode("ADMIN");
        testRole.setRoleName("管理员");
        testRole.setStatus("NORMAL");
        testRole.setDeleted(false);

        // 初始化测试科室
        testDepartment = new Department();
        testDepartment.setId("dept-001");
        testDepartment.setDeptCode("DEPT001");
        testDepartment.setDeptName("测试科室");
        testDepartment.setDeleted(false);

        // 初始化测试权限
        testPermission = new Permission();
        testPermission.setId("perm-001");
        testPermission.setPermCode("sys:user:view");
        testPermission.setPermName("用户查看");
        testPermission.setDeleted(false);
    }

    @Nested
    @DisplayName("登录功能测试")
    class LoginTests {

        @Test
        @DisplayName("登录成功")
        void loginSuccess() {
            // Given
            when(userRepository.findByLoginNameAndDeletedFalse("testuser"))
                    .thenReturn(Optional.of(testUser));

            // When
            Result<LoginVO> result = userService.login(testLoginDTO, "192.168.1.1");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals("登录成功", result.getMessage());
            assertNotNull(result.getData());
            assertEquals("user-001", result.getData().getUserId());
            assertEquals("testuser", result.getData().getLoginName());

            // Verify
            verify(userRepository).save(any(User.class));
            verify(auditLogService).log(any(AuditLog.class));
        }

        @Test
        @DisplayName("账号不存在")
        void loginAccountNotFound() {
            // Given
            when(userRepository.findByLoginNameAndDeletedFalse("nonexistent"))
                    .thenReturn(Optional.empty());

            LoginDTO dto = new LoginDTO();
            dto.setLoginName("nonexistent");
            dto.setPassword("password");

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.login(dto, "192.168.1.1"));
            assertEquals(ErrorCode.AUTH_ERROR.getCode(), exception.getCode());
            assertEquals("账号不存在", exception.getMessage());
        }

        @Test
        @DisplayName("账号已禁用")
        void loginAccountDisabled() {
            // Given
            testUser.setStatus(UserStatus.DISABLED);
            when(userRepository.findByLoginNameAndDeletedFalse("testuser"))
                    .thenReturn(Optional.of(testUser));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.login(testLoginDTO, "192.168.1.1"));
            assertEquals(ErrorCode.ACCOUNT_DISABLED.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("账号已锁定且未过期")
        void loginAccountLockedNotExpired() {
            // Given
            testUser.setStatus(UserStatus.LOCKED);
            testUser.setLockTime(LocalDateTime.now().minusMinutes(10));
            when(userRepository.findByLoginNameAndDeletedFalse("testuser"))
                    .thenReturn(Optional.of(testUser));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.login(testLoginDTO, "192.168.1.1"));
            assertEquals(ErrorCode.ACCOUNT_LOCKED.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("账号锁定已过期自动解锁")
        void loginAccountLockExpired() {
            // Given
            testUser.setStatus(UserStatus.LOCKED);
            testUser.setLockTime(LocalDateTime.now().minusMinutes(60));
            when(userRepository.findByLoginNameAndDeletedFalse("testuser"))
                    .thenReturn(Optional.of(testUser));

            // When
            Result<LoginVO> result = userService.login(testLoginDTO, "192.168.1.1");

            // Then
            assertEquals(0, result.getCode());
            // Account unlocked and login successful, user saved once
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("密码错误")
        void loginPasswordError() {
            // Given
            LoginDTO wrongPasswordDTO = new LoginDTO();
            wrongPasswordDTO.setLoginName("testuser");
            wrongPasswordDTO.setPassword("wrongpassword");

            when(userRepository.findByLoginNameAndDeletedFalse("testuser"))
                    .thenReturn(Optional.of(testUser));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.login(wrongPasswordDTO, "192.168.1.1"));
            assertEquals(ErrorCode.PASSWORD_ERROR.getCode(), exception.getCode());
            assertTrue(exception.getMessage().contains("密码错误"));

            // Verify login fail count increased
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("密码错误达到上限账号锁定")
        void loginPasswordErrorMaxAttempts() {
            // Given
            testUser.setLoginFailCount(4);
            LoginDTO wrongPasswordDTO = new LoginDTO();
            wrongPasswordDTO.setLoginName("testuser");
            wrongPasswordDTO.setPassword("wrongpassword");

            when(userRepository.findByLoginNameAndDeletedFalse("testuser"))
                    .thenReturn(Optional.of(testUser));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.login(wrongPasswordDTO, "192.168.1.1"));
            assertEquals(ErrorCode.ACCOUNT_LOCKED.getCode(), exception.getCode());
        }
    }

    @Nested
    @DisplayName("退出登录测试")
    class LogoutTests {

        @Test
        @DisplayName("退出成功")
        void logoutSuccess() {
            // Given
            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));

            // When
            Result<Void> result = userService.logout("user-001");

            // Then
            assertEquals(0, result.getCode());
            verify(auditLogService).log(any(AuditLog.class));
        }

        @Test
        @DisplayName("用户不存在时退出")
        void logoutUserNotFound() {
            // Given
            when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When
            Result<Void> result = userService.logout("nonexistent");

            // Then
            assertEquals(0, result.getCode());
            verify(auditLogService, never()).log(any(AuditLog.class));
        }
    }

    @Nested
    @DisplayName("创建用户测试")
    class CreateTests {

        @Test
        @DisplayName("创建用户成功")
        void createSuccess() {
            // Given
            when(userRepository.existsByLoginNameAndDeletedFalse("newuser")).thenReturn(false);
            when(userRepository.existsByEmployeeIdAndDeletedFalse(any())).thenReturn(false);
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId("user-new");
                return user;
            });
            when(roleRepository.findByIdInAndDeletedFalse(anyList())).thenReturn(Arrays.asList(testRole));

            // When
            Result<UserVO> result = userService.create(testUserDTO);

            // Then
            assertEquals(0, result.getCode());
            assertEquals("创建成功", result.getMessage());
            assertNotNull(result.getData());
            assertEquals("测试科室", result.getData().getDeptName());

            // Verify
            verify(userRepository).save(any(User.class));
            verify(userRoleRepository, times(1)).save(any(UserRole.class));
            verify(auditLogService).log(any(AuditLog.class));
        }

        @Test
        @DisplayName("登录账号已存在")
        void createLoginNameExists() {
            // Given
            when(userRepository.existsByLoginNameAndDeletedFalse("newuser")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.create(testUserDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), exception.getCode());
            assertEquals("登录账号已存在", exception.getMessage());
        }

        @Test
        @DisplayName("员工ID已绑定")
        void createEmployeeIdExists() {
            // Given
            testUserDTO.setEmployeeId("emp-001");
            when(userRepository.existsByLoginNameAndDeletedFalse("newuser")).thenReturn(false);
            when(userRepository.existsByEmployeeIdAndDeletedFalse("emp-001")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.create(testUserDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), exception.getCode());
            assertEquals("员工ID已绑定其他账号", exception.getMessage());
        }

        @Test
        @DisplayName("创建用户无角色授权")
        void createWithoutRoles() {
            // Given
            testUserDTO.setRoleIds(null);
            when(userRepository.existsByLoginNameAndDeletedFalse("newuser")).thenReturn(false);
            when(userRepository.existsByEmployeeIdAndDeletedFalse(any())).thenReturn(false);
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId("user-new");
                return user;
            });

            // When
            Result<UserVO> result = userService.create(testUserDTO);

            // Then
            assertEquals(0, result.getCode());
            verify(userRoleRepository, never()).save(any(UserRole.class));
        }
    }

    @Nested
    @DisplayName("更新用户测试")
    class UpdateTests {

        @Test
        @DisplayName("更新用户成功")
        void updateSuccess() {
            // Given
            testUserDTO.setId("user-001");
            testUserDTO.setRoleIds(Arrays.asList("role-001"));

            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(roleRepository.findByIdInAndDeletedFalse(anyList())).thenReturn(Arrays.asList(testRole));

            // When
            Result<UserVO> result = userService.update(testUserDTO);

            // Then
            assertEquals(0, result.getCode());
            assertEquals("更新成功", result.getMessage());
            assertNotNull(result.getData());

            verify(userRepository).save(any(User.class));
            verify(userRoleRepository).deleteByUserId("user-001");
            verify(auditLogService).log(any(AuditLog.class));
        }

        @Test
        @DisplayName("用户不存在")
        void updateUserNotFound() {
            // Given
            testUserDTO.setId("nonexistent");
            when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.update(testUserDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("用户不存在", exception.getMessage());
        }

        @Test
        @DisplayName("用户已删除")
        void updateUserDeleted() {
            // Given
            testUserDTO.setId("user-001");
            testUser.setDeleted(true);
            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.update(testUserDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("用户已删除", exception.getMessage());
        }

        @Test
        @DisplayName("更新密码")
        void updateWithPassword() {
            // Given
            testUserDTO.setId("user-001");
            testUserDTO.setPassword("newpassword456");

            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            Result<UserVO> result = userService.update(testUserDTO);

            // Then
            assertEquals(0, result.getCode());
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("删除用户测试")
    class DeleteTests {

        @Test
        @DisplayName("删除用户成功")
        void deleteSuccess() {
            // Given
            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            Result<Void> result = userService.delete("user-001");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(testUser.getDeleted());

            verify(userRepository).save(any(User.class));
            verify(userRoleRepository).deleteByUserId("user-001");
            verify(auditLogService).log(any(AuditLog.class));
        }

        @Test
        @DisplayName("删除用户不存在")
        void deleteUserNotFound() {
            // Given
            when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.delete("nonexistent"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("用户不存在", exception.getMessage());
        }

        @Test
        @DisplayName("删除已删除用户")
        void deleteAlreadyDeleted() {
            // Given
            testUser.setDeleted(true);
            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.delete("user-001"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("用户已删除", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("获取用户详情测试")
    class GetByIdTests {

        @Test
        @DisplayName("获取用户成功")
        void getByIdSuccess() {
            // Given
            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
            when(userRoleRepository.findRoleIdsByUserId("user-001")).thenReturn(Arrays.asList("role-001"));
            when(roleRepository.findByIdInAndDeletedFalse(anyList())).thenReturn(Arrays.asList(testRole));

            // When
            Result<UserVO> result = userService.getById("user-001");

            // Then
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("user-001", result.getData().getId());
            assertNotNull(result.getData().getRoles());
        }

        @Test
        @DisplayName("获取用户不存在")
        void getByIdNotFound() {
            // Given
            when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.getById("nonexistent"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("获取用户已删除")
        void getByIdDeleted() {
            // Given
            testUser.setDeleted(true);
            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.getById("user-001"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        }
    }

    @Nested
    @DisplayName("分页查询用户测试")
    class PageTests {

        @Test
        @DisplayName("分页查询成功")
        void pageSuccess() {
            // Given
            List<User> users = Arrays.asList(testUser);
            Page<User> page = new PageImpl<>(users);
            when(userRepository.findByCondition(any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // When
            Result<PageResult<UserVO>> result = userService.page("测试", null, null, null, null, 1, 10);

            // Then
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getList().size());
            assertEquals(1L, result.getData().getTotal());
        }

        @Test
        @DisplayName("分页查询空结果")
        void pageEmpty() {
            // Given
            Page<User> emptyPage = new PageImpl<>(Collections.emptyList());
            when(userRepository.findByCondition(any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // When
            Result<PageResult<UserVO>> result = userService.page(null, null, null, null, null, 1, 10);

            // Then
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().getList().isEmpty());
            assertEquals(0L, result.getData().getTotal());
        }
    }

    @Nested
    @DisplayName("获取用户权限测试")
    class GetUserPermissionTests {

        @Test
        @DisplayName("获取用户权限成功")
        void getUserPermissionSuccess() {
            // Given
            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
            when(userRoleRepository.findRoleIdsByUserId("user-001")).thenReturn(Arrays.asList("role-001"));
            when(roleRepository.findByIdInAndDeletedFalse(anyList())).thenReturn(Arrays.asList(testRole));
            when(rolePermissionRepository.findPermIdsByRoleIds(anyList())).thenReturn(Arrays.asList("perm-001"));
            when(permissionRepository.findByIdInAndDeletedFalse(anyList())).thenReturn(Arrays.asList(testPermission));

            // When
            Result<UserPermissionVO> result = userService.getUserPermission("user-001");

            // Then
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("user-001", result.getData().getUserId());
            assertNotNull(result.getData().getRoles());
            assertNotNull(result.getData().getPermissions());
        }

        @Test
        @DisplayName("用户无角色")
        void getUserPermissionNoRoles() {
            // Given
            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
            when(userRoleRepository.findRoleIdsByUserId("user-001")).thenReturn(Collections.emptyList());

            // When
            Result<UserPermissionVO> result = userService.getUserPermission("user-001");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().getRoles().isEmpty());
            assertTrue(result.getData().getPermissions().isEmpty());
        }
    }

    @Nested
    @DisplayName("修改密码测试")
    class ChangePasswordTests {

        @Test
        @DisplayName("修改密码成功")
        void changePasswordSuccess() {
            // Given
            PasswordChangeDTO dto = new PasswordChangeDTO();
            dto.setOldPassword("password123");
            dto.setNewPassword("newpassword456");
            dto.setConfirmPassword("newpassword456");

            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            Result<Void> result = userService.changePassword("user-001", dto);

            // Then
            assertEquals(0, result.getCode());
            verify(userRepository).save(any(User.class));
            verify(auditLogService).log(any(AuditLog.class));
        }

        @Test
        @DisplayName("旧密码错误")
        void changePasswordOldPasswordError() {
            // Given
            PasswordChangeDTO dto = new PasswordChangeDTO();
            dto.setOldPassword("wrongpassword");
            dto.setNewPassword("newpassword456");
            dto.setConfirmPassword("newpassword456");

            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.changePassword("user-001", dto));
            assertEquals(ErrorCode.PASSWORD_ERROR.getCode(), exception.getCode());
            assertEquals("旧密码错误", exception.getMessage());
        }

        @Test
        @DisplayName("新密码和确认密码不一致")
        void changePasswordMismatch() {
            // Given
            PasswordChangeDTO dto = new PasswordChangeDTO();
            dto.setOldPassword("password123");
            dto.setNewPassword("newpassword456");
            dto.setConfirmPassword("differentpassword");

            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.changePassword("user-001", dto));
            assertEquals(ErrorCode.PARAM_ERROR.getCode(), exception.getCode());
            assertEquals("新密码和确认密码不一致", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("重置密码测试")
    class ResetPasswordTests {

        @Test
        @DisplayName("重置密码成功")
        void resetPasswordSuccess() {
            // Given
            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            Result<Void> result = userService.resetPassword("user-001", "resetpassword");

            // Then
            assertEquals(0, result.getCode());
            assertEquals(0, testUser.getLoginFailCount());
            assertEquals(UserStatus.NORMAL, testUser.getStatus());
            assertNull(testUser.getLockTime());

            verify(userRepository).save(any(User.class));
            verify(auditLogService).log(any(AuditLog.class));
        }

        @Test
        @DisplayName("重置密码用户不存在")
        void resetPasswordUserNotFound() {
            // Given
            when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.resetPassword("nonexistent", "resetpassword"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        }
    }

    @Nested
    @DisplayName("更新用户状态测试")
    class UpdateStatusTests {

        @Test
        @DisplayName("更新状态成功")
        void updateStatusSuccess() {
            // Given
            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            Result<Void> result = userService.updateStatus("user-001", "DISABLED");

            // Then
            assertEquals(0, result.getCode());
            assertEquals(UserStatus.DISABLED, testUser.getStatus());
            verify(auditLogService).log(any(AuditLog.class));
        }

        @Test
        @DisplayName("更新状态为正常时清除锁定信息")
        void updateStatusNormalClearsLock() {
            // Given
            testUser.setStatus(UserStatus.LOCKED);
            testUser.setLoginFailCount(5);
            testUser.setLockTime(LocalDateTime.now());

            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            Result<Void> result = userService.updateStatus("user-001", "NORMAL");

            // Then
            assertEquals(0, result.getCode());
            assertEquals(UserStatus.NORMAL, testUser.getStatus());
            assertEquals(0, testUser.getLoginFailCount());
            assertNull(testUser.getLockTime());
        }

        @Test
        @DisplayName("无效状态值")
        void updateStatusInvalid() {
            // Given
            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.updateStatus("user-001", "INVALID"));
            assertEquals(ErrorCode.PARAM_ERROR.getCode(), exception.getCode());
            assertEquals("无效的状态值", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("授权用户角色测试")
    class AuthorizeRolesTests {

        @Test
        @DisplayName("授权角色成功")
        void authorizeRolesSuccess() {
            // Given
            UserAuthorizationDTO dto = new UserAuthorizationDTO();
            dto.setUserId("user-001");
            dto.setRoleIds(Arrays.asList("role-001", "role-002"));

            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));
            when(roleRepository.findByIdInAndDeletedFalse(anyList())).thenReturn(Arrays.asList(testRole));

            // When
            Result<Void> result = userService.authorizeRoles(dto);

            // Then
            assertEquals(0, result.getCode());
            verify(userRoleRepository).deleteByUserId("user-001");
            verify(userRoleRepository, times(2)).save(any(UserRole.class));
            verify(auditLogService).log(any(AuditLog.class));
        }

        @Test
        @DisplayName("清空用户角色")
        void authorizeRolesEmpty() {
            // Given
            UserAuthorizationDTO dto = new UserAuthorizationDTO();
            dto.setUserId("user-001");
            dto.setRoleIds(Collections.emptyList());

            when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));

            // When
            Result<Void> result = userService.authorizeRoles(dto);

            // Then
            assertEquals(0, result.getCode());
            verify(userRoleRepository).deleteByUserId("user-001");
            verify(userRoleRepository, never()).save(any(UserRole.class));
        }
    }

    @Nested
    @DisplayName("获取科室用户列表测试")
    class ListByDeptIdTests {

        @Test
        @DisplayName("获取科室用户成功")
        void listByDeptIdSuccess() {
            // Given
            when(userRepository.findByDeptIdAndDeletedFalse("dept-001"))
                    .thenReturn(Arrays.asList(testUser));

            // When
            Result<List<UserVO>> result = userService.listByDeptId("dept-001");

            // Then
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("科室无用户")
        void listByDeptIdEmpty() {
            // Given
            when(userRepository.findByDeptIdAndDeletedFalse("dept-001"))
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<UserVO>> result = userService.listByDeptId("dept-001");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }
}
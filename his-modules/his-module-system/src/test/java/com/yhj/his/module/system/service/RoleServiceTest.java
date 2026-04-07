package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.system.dto.*;
import com.yhj.his.module.system.entity.*;
import com.yhj.his.module.system.enums.DataScopeLevel;
import com.yhj.his.module.system.enums.PermissionType;
import com.yhj.his.module.system.repository.*;
import com.yhj.his.module.system.service.impl.RoleServiceImpl;
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
 * RoleService 单元测试
 * 目标覆盖率: 90%+
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("角色服务测试")
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role testRole;
    private RoleDTO testRoleDTO;
    private Permission testPermission;
    private RoleAuthorizationDTO testAuthorizationDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试角色
        testRole = new Role();
        testRole.setId("role-001");
        testRole.setRoleCode("ADMIN");
        testRole.setRoleName("管理员");
        testRole.setDescription("系统管理员角色");
        testRole.setDataScope(DataScopeLevel.ALL);
        testRole.setSortOrder(1);
        testRole.setIsSystem(false);
        testRole.setStatus("NORMAL");
        testRole.setDeleted(false);
        testRole.setCreateTime(LocalDateTime.now());

        // 初始化测试DTO
        testRoleDTO = new RoleDTO();
        testRoleDTO.setRoleCode("TEST_ROLE");
        testRoleDTO.setRoleName("测试角色");
        testRoleDTO.setDescription("测试角色描述");
        testRoleDTO.setDataScope("ALL");
        testRoleDTO.setSortOrder(2);
        testRoleDTO.setIsSystem(false);
        testRoleDTO.setStatus("NORMAL");
        testRoleDTO.setPermIds(Arrays.asList("perm-001"));

        // 初始化测试权限
        testPermission = new Permission();
        testPermission.setId("perm-001");
        testPermission.setPermCode("sys:user:view");
        testPermission.setPermName("用户查看");
        testPermission.setPermType(PermissionType.MENU);
        testPermission.setSortOrder(1);
        testPermission.setStatus("NORMAL");
        testPermission.setDeleted(false);
        testPermission.setCreateTime(LocalDateTime.now());

        // 初始化授权DTO
        testAuthorizationDTO = new RoleAuthorizationDTO();
        testAuthorizationDTO.setRoleId("role-001");
        testAuthorizationDTO.setPermIds(Arrays.asList("perm-001", "perm-002"));
    }

    @Nested
    @DisplayName("创建角色测试")
    class CreateTests {

        @Test
        @DisplayName("创建角色成功")
        void createSuccess() {
            // Given
            when(roleRepository.existsByRoleCodeAndDeletedFalse("TEST_ROLE")).thenReturn(false);
            when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
                Role role = invocation.getArgument(0);
                role.setId("role-new");
                return role;
            });

            // When
            Result<RoleVO> result = roleService.create(testRoleDTO);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals("创建成功", result.getMessage());
            assertNotNull(result.getData());

            verify(roleRepository).save(any(Role.class));
            verify(rolePermissionRepository, times(1)).save(any(RolePermission.class));
        }

        @Test
        @DisplayName("创建角色成功无权限授权")
        void createSuccessWithoutPermissions() {
            // Given
            testRoleDTO.setPermIds(null);
            when(roleRepository.existsByRoleCodeAndDeletedFalse("TEST_ROLE")).thenReturn(false);
            when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
                Role role = invocation.getArgument(0);
                role.setId("role-new");
                return role;
            });

            // When
            Result<RoleVO> result = roleService.create(testRoleDTO);

            // Then
            assertEquals(0, result.getCode());
            verify(rolePermissionRepository, never()).save(any(RolePermission.class));
        }

        @Test
        @DisplayName("角色编码已存在")
        void createRoleCodeExists() {
            // Given
            when(roleRepository.existsByRoleCodeAndDeletedFalse("TEST_ROLE")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.create(testRoleDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), exception.getCode());
            assertEquals("角色编码已存在", exception.getMessage());
        }

        @Test
        @DisplayName("创建角色带数据权限级别")
        void createWithDataScope() {
            // Given
            testRoleDTO.setDataScope("DEPARTMENT");
            when(roleRepository.existsByRoleCodeAndDeletedFalse("TEST_ROLE")).thenReturn(false);
            when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
                Role role = invocation.getArgument(0);
                role.setId("role-new");
                assertEquals(DataScopeLevel.DEPARTMENT, role.getDataScope());
                return role;
            });

            // When
            Result<RoleVO> result = roleService.create(testRoleDTO);

            // Then
            assertEquals(0, result.getCode());
        }
    }

    @Nested
    @DisplayName("更新角色测试")
    class UpdateTests {

        @Test
        @DisplayName("更新角色成功")
        void updateSuccess() {
            // Given
            testRoleDTO.setId("role-001");

            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));
            when(roleRepository.save(any(Role.class))).thenReturn(testRole);
            when(permissionRepository.findByIdInAndDeletedFalse(anyList())).thenReturn(Arrays.asList(testPermission));

            // When
            Result<RoleVO> result = roleService.update(testRoleDTO);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals("更新成功", result.getMessage());
            assertNotNull(result.getData());

            verify(roleRepository).save(any(Role.class));
            verify(rolePermissionRepository).deleteByRoleId("role-001");
        }

        @Test
        @DisplayName("角色不存在")
        void updateRoleNotFound() {
            // Given
            testRoleDTO.setId("nonexistent");
            when(roleRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.update(testRoleDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("角色不存在", exception.getMessage());
        }

        @Test
        @DisplayName("角色已删除")
        void updateRoleDeleted() {
            // Given
            testRoleDTO.setId("role-001");
            testRole.setDeleted(true);
            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.update(testRoleDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("角色已删除", exception.getMessage());
        }

        @Test
        @DisplayName("系统角色不可修改")
        void updateSystemRole() {
            // Given
            testRoleDTO.setId("role-001");
            testRole.setIsSystem(true);
            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.update(testRoleDTO));
            assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
            assertEquals("系统角色不可修改", exception.getMessage());
        }

        @Test
        @DisplayName("更新角色清空权限")
        void updateClearPermissions() {
            // Given
            testRoleDTO.setId("role-001");
            testRoleDTO.setPermIds(Collections.emptyList());

            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));
            when(roleRepository.save(any(Role.class))).thenReturn(testRole);

            // When
            Result<RoleVO> result = roleService.update(testRoleDTO);

            // Then
            assertEquals(0, result.getCode());
            verify(rolePermissionRepository).deleteByRoleId("role-001");
            verify(rolePermissionRepository, never()).save(any(RolePermission.class));
        }
    }

    @Nested
    @DisplayName("删除角色测试")
    class DeleteTests {

        @Test
        @DisplayName("删除角色成功")
        void deleteSuccess() {
            // Given
            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));
            when(userRoleRepository.findByRoleId("role-001")).thenReturn(Collections.emptyList());
            when(roleRepository.save(any(Role.class))).thenReturn(testRole);

            // When
            Result<Void> result = roleService.delete("role-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertTrue(testRole.getDeleted());

            verify(roleRepository).save(any(Role.class));
            verify(rolePermissionRepository).deleteByRoleId("role-001");
        }

        @Test
        @DisplayName("删除角色不存在")
        void deleteRoleNotFound() {
            // Given
            when(roleRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.delete("nonexistent"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("角色不存在", exception.getMessage());
        }

        @Test
        @DisplayName("删除已删除角色")
        void deleteAlreadyDeleted() {
            // Given
            testRole.setDeleted(true);
            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.delete("role-001"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("角色已删除", exception.getMessage());
        }

        @Test
        @DisplayName("系统角色不可删除")
        void deleteSystemRole() {
            // Given
            testRole.setIsSystem(true);
            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.delete("role-001"));
            assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
            assertEquals("系统角色不可删除", exception.getMessage());
        }

        @Test
        @DisplayName("角色已绑定用户不可删除")
        void deleteRoleWithUsers() {
            // Given
            UserRole userRole = new UserRole();
            userRole.setUserId("user-001");
            userRole.setRoleId("role-001");

            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));
            when(userRoleRepository.findByRoleId("role-001")).thenReturn(Arrays.asList(userRole));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.delete("role-001"));
            assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
            assertTrue(exception.getMessage().contains("该角色已授权给"));
        }
    }

    @Nested
    @DisplayName("获取角色详情测试")
    class GetByIdTests {

        @Test
        @DisplayName("获取角色成功")
        void getByIdSuccess() {
            // Given
            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));
            when(rolePermissionRepository.findPermIdsByRoleId("role-001")).thenReturn(Arrays.asList("perm-001"));
            when(permissionRepository.findByIdInAndDeletedFalse(anyList())).thenReturn(Arrays.asList(testPermission));

            // When
            Result<RoleVO> result = roleService.getById("role-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("role-001", result.getData().getId());
            assertEquals("ADMIN", result.getData().getRoleCode());
            assertNotNull(result.getData().getPermissions());
        }

        @Test
        @DisplayName("获取角色不存在")
        void getByIdNotFound() {
            // Given
            when(roleRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.getById("nonexistent"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("获取角色已删除")
        void getByIdDeleted() {
            // Given
            testRole.setDeleted(true);
            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.getById("role-001"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("角色已删除", exception.getMessage());
        }

        @Test
        @DisplayName("获取角色无权限")
        void getByIdNoPermissions() {
            // Given
            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));
            when(rolePermissionRepository.findPermIdsByRoleId("role-001")).thenReturn(Collections.emptyList());

            // When
            Result<RoleVO> result = roleService.getById("role-001");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().getPermissions().isEmpty());
        }
    }

    @Nested
    @DisplayName("分页查询角色测试")
    class PageTests {

        @Test
        @DisplayName("分页查询成功")
        void pageSuccess() {
            // Given
            List<Role> roles = Arrays.asList(testRole);
            Page<Role> page = new PageImpl<>(roles);
            when(roleRepository.findByCondition(any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // When
            Result<PageResult<RoleVO>> result = roleService.page("管理员", "ADMIN", "NORMAL", 1, 10);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getList().size());
            assertEquals(1L, result.getData().getTotal());
        }

        @Test
        @DisplayName("分页查询空结果")
        void pageEmpty() {
            // Given
            Page<Role> emptyPage = new PageImpl<>(Collections.emptyList());
            when(roleRepository.findByCondition(any(), any(), any(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // When
            Result<PageResult<RoleVO>> result = roleService.page(null, null, null, 1, 10);

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().getList().isEmpty());
            assertEquals(0L, result.getData().getTotal());
        }
    }

    @Nested
    @DisplayName("获取所有角色列表测试")
    class ListAllTests {

        @Test
        @DisplayName("获取所有角色成功")
        void listAllSuccess() {
            // Given
            when(roleRepository.findByDeletedFalseOrderBySortOrderAsc()).thenReturn(Arrays.asList(testRole));

            // When
            Result<List<RoleVO>> result = roleService.listAll();

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals("ADMIN", result.getData().get(0).getRoleCode());
        }

        @Test
        @DisplayName("获取所有角色空列表")
        void listAllEmpty() {
            // Given
            when(roleRepository.findByDeletedFalseOrderBySortOrderAsc()).thenReturn(Collections.emptyList());

            // When
            Result<List<RoleVO>> result = roleService.listAll();

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("授权角色权限测试")
    class AuthorizePermissionsTests {

        @Test
        @DisplayName("授权权限成功")
        void authorizePermissionsSuccess() {
            // Given
            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));
            when(permissionRepository.findByIdInAndDeletedFalse(anyList())).thenReturn(Arrays.asList(testPermission));

            // When
            Result<Void> result = roleService.authorizePermissions(testAuthorizationDTO);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(rolePermissionRepository).deleteByRoleId("role-001");
            verify(rolePermissionRepository, times(2)).save(any(RolePermission.class));
        }

        @Test
        @DisplayName("授权权限角色不存在")
        void authorizePermissionsRoleNotFound() {
            // Given
            testAuthorizationDTO.setRoleId("nonexistent");
            when(roleRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.authorizePermissions(testAuthorizationDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("角色不存在", exception.getMessage());
        }

        @Test
        @DisplayName("系统角色权限不可修改")
        void authorizePermissionsSystemRole() {
            // Given
            testRole.setIsSystem(true);
            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> roleService.authorizePermissions(testAuthorizationDTO));
            assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
            assertEquals("系统角色权限不可修改", exception.getMessage());
        }

        @Test
        @DisplayName("清空角色权限")
        void authorizePermissionsEmpty() {
            // Given
            testAuthorizationDTO.setPermIds(Collections.emptyList());
            when(roleRepository.findById("role-001")).thenReturn(Optional.of(testRole));

            // When
            Result<Void> result = roleService.authorizePermissions(testAuthorizationDTO);

            // Then
            assertEquals(0, result.getCode());
            verify(rolePermissionRepository).deleteByRoleId("role-001");
            verify(rolePermissionRepository, never()).save(any(RolePermission.class));
        }
    }

    @Nested
    @DisplayName("获取角色权限列表测试")
    class GetRolePermissionsTests {

        @Test
        @DisplayName("获取角色权限成功")
        void getRolePermissionsSuccess() {
            // Given
            when(rolePermissionRepository.findPermIdsByRoleId("role-001")).thenReturn(Arrays.asList("perm-001"));
            when(permissionRepository.findByIdInAndDeletedFalse(anyList())).thenReturn(Arrays.asList(testPermission));

            // When
            Result<List<PermissionVO>> result = roleService.getRolePermissions("role-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals("sys:user:view", result.getData().get(0).getPermCode());
        }

        @Test
        @DisplayName("角色无权限")
        void getRolePermissionsEmpty() {
            // Given
            when(rolePermissionRepository.findPermIdsByRoleId("role-001")).thenReturn(Collections.emptyList());

            // When
            Result<List<PermissionVO>> result = roleService.getRolePermissions("role-001");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("获取用户角色列表测试")
    class GetUserRolesTests {

        @Test
        @DisplayName("获取用户角色成功")
        void getUserRolesSuccess() {
            // Given
            when(userRoleRepository.findRoleIdsByUserId("user-001")).thenReturn(Arrays.asList("role-001"));
            when(roleRepository.findByIdInAndDeletedFalse(anyList())).thenReturn(Arrays.asList(testRole));

            // When
            Result<List<RoleVO>> result = roleService.getUserRoles("user-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals("ADMIN", result.getData().get(0).getRoleCode());
        }

        @Test
        @DisplayName("用户无角色")
        void getUserRolesEmpty() {
            // Given
            when(userRoleRepository.findRoleIdsByUserId("user-001")).thenReturn(Collections.emptyList());

            // When
            Result<List<RoleVO>> result = roleService.getUserRoles("user-001");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }
}
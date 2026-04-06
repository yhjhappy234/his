package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.system.dto.PermissionDTO;
import com.yhj.his.module.system.entity.Permission;
import com.yhj.his.module.system.entity.RolePermission;
import com.yhj.his.module.system.enums.PermissionType;
import com.yhj.his.module.system.repository.PermissionRepository;
import com.yhj.his.module.system.repository.RolePermissionRepository;
import com.yhj.his.module.system.service.impl.PermissionServiceImpl;
import com.yhj.his.module.system.vo.PermissionVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PermissionService 单元测试
 * 目标覆盖率: 90%+
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限服务测试")
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission testPermission;
    private Permission testChildPermission;
    private PermissionDTO testPermissionDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试权限（父级）
        testPermission = new Permission();
        testPermission.setId("perm-001");
        testPermission.setPermCode("sys:user");
        testPermission.setPermName("用户管理");
        testPermission.setPermType(PermissionType.MENU);
        testPermission.setParentId(null);
        testPermission.setPath("/system/user");
        testPermission.setIcon("user");
        testPermission.setSortOrder(1);
        testPermission.setStatus("NORMAL");
        testPermission.setDeleted(false);
        testPermission.setCreateTime(LocalDateTime.now());

        // 初始化子权限
        testChildPermission = new Permission();
        testChildPermission.setId("perm-002");
        testChildPermission.setPermCode("sys:user:view");
        testChildPermission.setPermName("用户查看");
        testChildPermission.setPermType(PermissionType.FUNCTION);
        testChildPermission.setParentId("perm-001");
        testChildPermission.setPath("/system/user/view");
        testChildPermission.setIcon(null);
        testChildPermission.setSortOrder(1);
        testChildPermission.setStatus("NORMAL");
        testChildPermission.setDeleted(false);
        testChildPermission.setCreateTime(LocalDateTime.now());

        // 初始化测试DTO
        testPermissionDTO = new PermissionDTO();
        testPermissionDTO.setPermCode("sys:test");
        testPermissionDTO.setPermName("测试权限");
        testPermissionDTO.setPermType("MENU");
        testPermissionDTO.setParentId(null);
        testPermissionDTO.setPath("/system/test");
        testPermissionDTO.setIcon("test");
        testPermissionDTO.setSortOrder(2);
        testPermissionDTO.setStatus("NORMAL");
    }

    @Nested
    @DisplayName("创建权限测试")
    class CreateTests {

        @Test
        @DisplayName("创建权限成功")
        void createSuccess() {
            // Given
            when(permissionRepository.existsByPermCodeAndDeletedFalse("sys:test")).thenReturn(false);
            when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> {
                Permission perm = invocation.getArgument(0);
                perm.setId("perm-new");
                return perm;
            });

            // When
            Result<PermissionVO> result = permissionService.create(testPermissionDTO);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals("创建成功", result.getMessage());
            assertNotNull(result.getData());

            verify(permissionRepository).save(any(Permission.class));
        }

        @Test
        @DisplayName("权限编码已存在")
        void createPermCodeExists() {
            // Given
            when(permissionRepository.existsByPermCodeAndDeletedFalse("sys:test")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> permissionService.create(testPermissionDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), exception.getCode());
            assertEquals("权限编码已存在", exception.getMessage());
        }

        @Test
        @DisplayName("创建功能权限")
        void createFunctionPermission() {
            // Given
            testPermissionDTO.setPermType("FUNCTION");
            testPermissionDTO.setParentId("perm-001");

            when(permissionRepository.existsByPermCodeAndDeletedFalse("sys:test")).thenReturn(false);
            when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> {
                Permission perm = invocation.getArgument(0);
                perm.setId("perm-new");
                assertEquals(PermissionType.FUNCTION, perm.getPermType());
                assertEquals("perm-001", perm.getParentId());
                return perm;
            });

            // When
            Result<PermissionVO> result = permissionService.create(testPermissionDTO);

            // Then
            assertEquals(0, result.getCode());
        }

        @Test
        @DisplayName("创建API权限")
        void createApiPermission() {
            // Given
            testPermissionDTO.setPermType("API");
            testPermissionDTO.setPath("/api/user/list");

            when(permissionRepository.existsByPermCodeAndDeletedFalse("sys:test")).thenReturn(false);
            when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> {
                Permission perm = invocation.getArgument(0);
                assertEquals(PermissionType.API, perm.getPermType());
                return perm;
            });

            // When
            Result<PermissionVO> result = permissionService.create(testPermissionDTO);

            // Then
            assertEquals(0, result.getCode());
        }
    }

    @Nested
    @DisplayName("更新权限测试")
    class UpdateTests {

        @Test
        @DisplayName("更新权限成功")
        void updateSuccess() {
            // Given
            testPermissionDTO.setId("perm-001");
            testPermissionDTO.setPermCode("sys:user");

            when(permissionRepository.findById("perm-001")).thenReturn(Optional.of(testPermission));
            when(permissionRepository.save(any(Permission.class))).thenReturn(testPermission);

            // When
            Result<PermissionVO> result = permissionService.update(testPermissionDTO);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals("更新成功", result.getMessage());
            assertNotNull(result.getData());

            verify(permissionRepository).save(any(Permission.class));
        }

        @Test
        @DisplayName("权限不存在")
        void updatePermissionNotFound() {
            // Given
            testPermissionDTO.setId("nonexistent");
            when(permissionRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> permissionService.update(testPermissionDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("权限不存在", exception.getMessage());
        }

        @Test
        @DisplayName("权限已删除")
        void updatePermissionDeleted() {
            // Given
            testPermissionDTO.setId("perm-001");
            testPermission.setDeleted(true);
            when(permissionRepository.findById("perm-001")).thenReturn(Optional.of(testPermission));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> permissionService.update(testPermissionDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("权限已删除", exception.getMessage());
        }

        @Test
        @DisplayName("更新权限类型")
        void updatePermissionType() {
            // Given
            testPermissionDTO.setId("perm-001");
            testPermissionDTO.setPermType("FUNCTION");

            when(permissionRepository.findById("perm-001")).thenReturn(Optional.of(testPermission));
            when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> {
                Permission perm = invocation.getArgument(0);
                assertEquals(PermissionType.FUNCTION, perm.getPermType());
                return perm;
            });

            // When
            Result<PermissionVO> result = permissionService.update(testPermissionDTO);

            // Then
            assertEquals(0, result.getCode());
        }
    }

    @Nested
    @DisplayName("删除权限测试")
    class DeleteTests {

        @Test
        @DisplayName("删除权限成功")
        void deleteSuccess() {
            // Given
            when(permissionRepository.findById("perm-001")).thenReturn(Optional.of(testPermission));
            when(permissionRepository.findByParentIdAndDeletedFalseOrderBySortOrderAsc("perm-001"))
                    .thenReturn(Collections.emptyList());
            when(rolePermissionRepository.findByPermId("perm-001")).thenReturn(Collections.emptyList());
            when(permissionRepository.save(any(Permission.class))).thenReturn(testPermission);

            // When
            Result<Void> result = permissionService.delete("perm-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertTrue(testPermission.getDeleted());

            verify(permissionRepository).save(any(Permission.class));
        }

        @Test
        @DisplayName("删除权限不存在")
        void deletePermissionNotFound() {
            // Given
            when(permissionRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> permissionService.delete("nonexistent"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("权限不存在", exception.getMessage());
        }

        @Test
        @DisplayName("删除已删除权限")
        void deleteAlreadyDeleted() {
            // Given
            testPermission.setDeleted(true);
            when(permissionRepository.findById("perm-001")).thenReturn(Optional.of(testPermission));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> permissionService.delete("perm-001"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("权限已删除", exception.getMessage());
        }

        @Test
        @DisplayName("权限有子权限不可删除")
        void deletePermissionWithChildren() {
            // Given
            when(permissionRepository.findById("perm-001")).thenReturn(Optional.of(testPermission));
            when(permissionRepository.findByParentIdAndDeletedFalseOrderBySortOrderAsc("perm-001"))
                    .thenReturn(Arrays.asList(testChildPermission));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> permissionService.delete("perm-001"));
            assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
            assertTrue(exception.getMessage().contains("子权限"));
        }

        @Test
        @DisplayName("权限已授权给角色不可删除")
        void deletePermissionWithRoles() {
            // Given
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId("role-001");
            rolePermission.setPermId("perm-001");

            when(permissionRepository.findById("perm-001")).thenReturn(Optional.of(testPermission));
            when(permissionRepository.findByParentIdAndDeletedFalseOrderBySortOrderAsc("perm-001"))
                    .thenReturn(Collections.emptyList());
            when(rolePermissionRepository.findByPermId("perm-001")).thenReturn(Arrays.asList(rolePermission));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> permissionService.delete("perm-001"));
            assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
            assertTrue(exception.getMessage().contains("已授权给"));
        }
    }

    @Nested
    @DisplayName("获取权限详情测试")
    class GetByIdTests {

        @Test
        @DisplayName("获取权限成功")
        void getByIdSuccess() {
            // Given
            when(permissionRepository.findById("perm-001")).thenReturn(Optional.of(testPermission));

            // When
            Result<PermissionVO> result = permissionService.getById("perm-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("perm-001", result.getData().getId());
            assertEquals("sys:user", result.getData().getPermCode());
            assertEquals("MENU", result.getData().getPermType());
        }

        @Test
        @DisplayName("获取权限不存在")
        void getByIdNotFound() {
            // Given
            when(permissionRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> permissionService.getById("nonexistent"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("获取权限已删除")
        void getByIdDeleted() {
            // Given
            testPermission.setDeleted(true);
            when(permissionRepository.findById("perm-001")).thenReturn(Optional.of(testPermission));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> permissionService.getById("perm-001"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("权限已删除", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("分页查询权限测试")
    class PageTests {

        @Test
        @DisplayName("分页查询成功")
        void pageSuccess() {
            // Given
            List<Permission> permissions = Arrays.asList(testPermission);
            Page<Permission> page = new PageImpl<>(permissions);
            when(permissionRepository.findByCondition(any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // When
            Result<PageResult<PermissionVO>> result = permissionService.page(
                    "用户管理", "sys:user", "MENU", "NORMAL", 1, 10);

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
            Page<Permission> emptyPage = new PageImpl<>(Collections.emptyList());
            when(permissionRepository.findByCondition(any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // When
            Result<PageResult<PermissionVO>> result = permissionService.page(
                    null, null, null, null, 1, 10);

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().getList().isEmpty());
            assertEquals(0L, result.getData().getTotal());
        }

        @Test
        @DisplayName("按权限类型查询")
        void pageByPermissionType() {
            // Given
            List<Permission> menuPermissions = Arrays.asList(testPermission);
            Page<Permission> page = new PageImpl<>(menuPermissions);
            when(permissionRepository.findByCondition(any(), any(), eq(PermissionType.MENU), any(), any(Pageable.class)))
                    .thenReturn(page);

            // When
            Result<PageResult<PermissionVO>> result = permissionService.page(
                    null, null, "MENU", null, 1, 10);

            // Then
            assertEquals(0, result.getCode());
            assertEquals("MENU", result.getData().getList().get(0).getPermType());
        }
    }

    @Nested
    @DisplayName("获取所有权限列表测试")
    class ListAllTests {

        @Test
        @DisplayName("获取所有权限成功")
        void listAllSuccess() {
            // Given
            when(permissionRepository.findByDeletedFalseOrderBySortOrderAsc())
                    .thenReturn(Arrays.asList(testPermission, testChildPermission));

            // When
            Result<List<PermissionVO>> result = permissionService.listAll();

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(2, result.getData().size());
        }

        @Test
        @DisplayName("获取所有权限空列表")
        void listAllEmpty() {
            // Given
            when(permissionRepository.findByDeletedFalseOrderBySortOrderAsc())
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<PermissionVO>> result = permissionService.listAll();

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("获取菜单权限树测试")
    class GetMenuTreeTests {

        @Test
        @DisplayName("获取菜单权限树成功")
        void getMenuTreeSuccess() {
            // Given
            when(permissionRepository.findByPermTypeAndDeletedFalseOrderBySortOrderAsc(PermissionType.MENU))
                    .thenReturn(Arrays.asList(testPermission));

            // When
            Result<List<PermissionVO>> result = permissionService.getMenuTree();

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("获取菜单权限树带子权限")
        void getMenuTreeWithChildren() {
            // Given
            Permission parentMenu = testPermission;
            Permission childMenu = new Permission();
            childMenu.setId("perm-child");
            childMenu.setPermCode("sys:user:child");
            childMenu.setPermName("用户子菜单");
            childMenu.setPermType(PermissionType.MENU);
            childMenu.setParentId("perm-001");
            childMenu.setSortOrder(1);
            childMenu.setDeleted(false);

            when(permissionRepository.findByPermTypeAndDeletedFalseOrderBySortOrderAsc(PermissionType.MENU))
                    .thenReturn(Arrays.asList(parentMenu, childMenu));

            // When
            Result<List<PermissionVO>> result = permissionService.getMenuTree();

            // Then
            assertEquals(0, result.getCode());
            assertEquals(1, result.getData().size());
            assertNotNull(result.getData().get(0).getChildren());
        }

        @Test
        @DisplayName("获取菜单权限树空")
        void getMenuTreeEmpty() {
            // Given
            when(permissionRepository.findByPermTypeAndDeletedFalseOrderBySortOrderAsc(PermissionType.MENU))
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<PermissionVO>> result = permissionService.getMenuTree();

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("获取用户菜单权限测试")
    class GetUserMenusTests {

        @Test
        @DisplayName("获取用户菜单权限成功")
        void getUserMenusSuccess() {
            // Given
            when(permissionRepository.findByPermTypeAndDeletedFalseOrderBySortOrderAsc(PermissionType.MENU))
                    .thenReturn(Arrays.asList(testPermission));

            // When
            Result<List<PermissionVO>> result = permissionService.getUserMenus("user-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
        }
    }

    @Nested
    @DisplayName("获取子权限列表测试")
    class ListByParentIdTests {

        @Test
        @DisplayName("获取子权限成功")
        void listByParentIdSuccess() {
            // Given
            when(permissionRepository.findByParentIdAndDeletedFalseOrderBySortOrderAsc("perm-001"))
                    .thenReturn(Arrays.asList(testChildPermission));

            // When
            Result<List<PermissionVO>> result = permissionService.listByParentId("perm-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals("perm-002", result.getData().get(0).getId());
        }

        @Test
        @DisplayName("无子权限")
        void listByParentIdEmpty() {
            // Given
            when(permissionRepository.findByParentIdAndDeletedFalseOrderBySortOrderAsc("perm-001"))
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<PermissionVO>> result = permissionService.listByParentId("perm-001");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }
}
package com.yhj.his.module.system.config;

import com.yhj.his.module.system.entity.Department;
import com.yhj.his.module.system.entity.Role;
import com.yhj.his.module.system.entity.User;
import com.yhj.his.module.system.entity.UserRole;
import com.yhj.his.module.system.repository.DepartmentRepository;
import com.yhj.his.module.system.repository.RoleRepository;
import com.yhj.his.module.system.repository.UserRepository;
import com.yhj.his.module.system.repository.UserRoleRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DataInitializer 单元测试
 * 目标覆盖率: 90%+
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("数据初始化器测试")
class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        // 设置默认值
        ReflectionTestUtils.setField(dataInitializer, "defaultUsername", "admin");
        ReflectionTestUtils.setField(dataInitializer, "defaultPassword", "123456");
    }

    @Nested
    @DisplayName("初始化数据测试")
    class InitDataTests {

        @Test
        @DisplayName("初始化新系统-创建所有默认数据")
        void initNewSystem() {
            // Given - 系统中没有任何数据
            when(departmentRepository.existsByDeptCodeAndDeletedFalse("SYS")).thenReturn(false);
            when(departmentRepository.existsByDeptCodeAndDeletedFalse("IM")).thenReturn(false);
            when(roleRepository.existsByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(false);
            when(roleRepository.existsByRoleCodeAndDeletedFalse("DOCTOR")).thenReturn(false);
            when(userRepository.existsByLoginNameAndDeletedFalse("admin")).thenReturn(false);
            when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(userRoleRepository.save(any(UserRole.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(roleRepository.findByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(java.util.Optional.of(new Role()));

            // When
            dataInitializer.run(null);

            // Then
            verify(departmentRepository, times(2)).save(any(Department.class));
            verify(roleRepository, times(2)).save(any(Role.class));
            verify(userRepository, times(1)).save(any(User.class));
            verify(userRoleRepository, times(1)).save(any(UserRole.class));
        }

        @Test
        @DisplayName("系统已有数据-不重复创建")
        void initWithExistingData() {
            // Given - 系统已有所有默认数据
            when(departmentRepository.existsByDeptCodeAndDeletedFalse("SYS")).thenReturn(true);
            when(departmentRepository.existsByDeptCodeAndDeletedFalse("IM")).thenReturn(true);
            when(roleRepository.existsByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(true);
            when(roleRepository.existsByRoleCodeAndDeletedFalse("DOCTOR")).thenReturn(true);
            when(userRepository.existsByLoginNameAndDeletedFalse("admin")).thenReturn(true);

            // When
            dataInitializer.run(null);

            // Then - 不创建任何新数据
            verify(departmentRepository, never()).save(any(Department.class));
            verify(roleRepository, never()).save(any(Role.class));
            verify(userRepository, never()).save(any(User.class));
            verify(userRoleRepository, never()).save(any(UserRole.class));
        }

        @Test
        @DisplayName("部分数据存在-只创建缺失数据")
        void initWithPartialData() {
            // Given - 部分数据存在
            when(departmentRepository.existsByDeptCodeAndDeletedFalse("SYS")).thenReturn(true);
            when(departmentRepository.existsByDeptCodeAndDeletedFalse("IM")).thenReturn(false);
            when(roleRepository.existsByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(true);
            when(roleRepository.existsByRoleCodeAndDeletedFalse("DOCTOR")).thenReturn(false);
            when(userRepository.existsByLoginNameAndDeletedFalse("admin")).thenReturn(true);
            when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            dataInitializer.run(null);

            // Then - 只创建缺失的数据
            verify(departmentRepository, times(1)).save(any(Department.class)); // 只创建内科
            verify(roleRepository, times(1)).save(any(Role.class)); // 只创建医生角色
        }

        @Test
        @DisplayName("初始化过程中异常-记录日志")
        void initWithException() {
            // Given
            when(departmentRepository.existsByDeptCodeAndDeletedFalse(any())).thenThrow(new RuntimeException("Database error"));

            // When
            dataInitializer.run(null);

            // Then - 不抛出异常，记录日志
            // 验证没有抛出异常，方法正常完成
        }
    }

    @Nested
    @DisplayName("初始化用户测试")
    class InitUserTests {

        @Test
        @DisplayName("创建默认用户并分配角色")
        void createUserWithRole() {
            // Given
            when(departmentRepository.existsByDeptCodeAndDeletedFalse(any())).thenReturn(true);
            when(roleRepository.existsByRoleCodeAndDeletedFalse(any())).thenReturn(true);
            when(userRepository.existsByLoginNameAndDeletedFalse("admin")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId("user-admin-001");
                return user;
            });

            Role adminRole = new Role();
            adminRole.setId("role-admin-001");
            when(roleRepository.findByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(java.util.Optional.of(adminRole));
            when(userRoleRepository.save(any(UserRole.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            dataInitializer.run(null);

            // Then
            verify(userRepository).save(any(User.class));
            verify(userRoleRepository).save(any(UserRole.class));
        }

        @Test
        @DisplayName("用户已存在-不创建")
        void userAlreadyExists() {
            // Given
            when(departmentRepository.existsByDeptCodeAndDeletedFalse(any())).thenReturn(true);
            when(roleRepository.existsByRoleCodeAndDeletedFalse(any())).thenReturn(true);
            when(userRepository.existsByLoginNameAndDeletedFalse("admin")).thenReturn(true);

            // When
            dataInitializer.run(null);

            // Then
            verify(userRepository, never()).save(any(User.class));
            verify(userRoleRepository, never()).save(any(UserRole.class));
        }

        @Test
        @DisplayName("角色不存在时-不分配角色")
        void createUserWithoutRole() {
            // Given
            when(departmentRepository.existsByDeptCodeAndDeletedFalse(any())).thenReturn(true);
            when(roleRepository.existsByRoleCodeAndDeletedFalse(any())).thenReturn(true);
            when(userRepository.existsByLoginNameAndDeletedFalse("admin")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId("user-admin-001");
                return user;
            });
            when(roleRepository.findByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(java.util.Optional.empty());

            // When
            dataInitializer.run(null);

            // Then
            verify(userRepository).save(any(User.class));
            verify(userRoleRepository, never()).save(any(UserRole.class));
        }
    }

    @Nested
    @DisplayName("配置参数测试")
    class ConfigTests {

        @Test
        @DisplayName("使用自定义用户名密码")
        void useCustomCredentials() {
            // Given
            ReflectionTestUtils.setField(dataInitializer, "defaultUsername", "custom_admin");
            ReflectionTestUtils.setField(dataInitializer, "defaultPassword", "custom_pass");
            when(departmentRepository.existsByDeptCodeAndDeletedFalse(any())).thenReturn(true);
            when(roleRepository.existsByRoleCodeAndDeletedFalse(any())).thenReturn(true);
            when(userRepository.existsByLoginNameAndDeletedFalse("custom_admin")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(roleRepository.findByRoleCodeAndDeletedFalse(any())).thenReturn(java.util.Optional.of(new Role()));

            // When
            dataInitializer.run(null);

            // Then
            verify(userRepository).save(any(User.class));
        }
    }
}
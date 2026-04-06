package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.system.dto.DepartmentDTO;
import com.yhj.his.module.system.entity.Department;
import com.yhj.his.module.system.repository.DepartmentRepository;
import com.yhj.his.module.system.repository.UserRepository;
import com.yhj.his.module.system.service.impl.DepartmentServiceImpl;
import com.yhj.his.module.system.vo.DepartmentVO;
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
 * DepartmentService 单元测试
 * 目标覆盖率: 90%+
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("科室服务测试")
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department testDepartment;
    private Department testChildDepartment;
    private DepartmentDTO testDepartmentDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试科室（父级）
        testDepartment = new Department();
        testDepartment.setId("dept-001");
        testDepartment.setDeptCode("DEPT001");
        testDepartment.setDeptName("内科");
        testDepartment.setShortName("内科");
        testDepartment.setParentId(null);
        testDepartment.setDeptLevel(1);
        testDepartment.setDeptType("临床");
        testDepartment.setLeaderId("user-001");
        testDepartment.setLeaderName("张医生");
        testDepartment.setPhone("010-12345678");
        testDepartment.setAddress("一楼东区");
        testDepartment.setSortOrder(1);
        testDepartment.setStatus("NORMAL");
        testDepartment.setDeleted(false);
        testDepartment.setCreateTime(LocalDateTime.now());

        // 初始化子科室
        testChildDepartment = new Department();
        testChildDepartment.setId("dept-002");
        testChildDepartment.setDeptCode("DEPT002");
        testChildDepartment.setDeptName("心血管内科");
        testChildDepartment.setShortName("心内科");
        testChildDepartment.setParentId("dept-001");
        testChildDepartment.setDeptLevel(2);
        testChildDepartment.setDeptType("临床");
        testChildDepartment.setSortOrder(1);
        testChildDepartment.setStatus("NORMAL");
        testChildDepartment.setDeleted(false);
        testChildDepartment.setCreateTime(LocalDateTime.now());

        // 初始化测试DTO
        testDepartmentDTO = new DepartmentDTO();
        testDepartmentDTO.setDeptCode("DEPT_NEW");
        testDepartmentDTO.setDeptName("新科室");
        testDepartmentDTO.setShortName("新科");
        testDepartmentDTO.setDeptType("临床");
        testDepartmentDTO.setSortOrder(2);
        testDepartmentDTO.setStatus("NORMAL");
    }

    @Nested
    @DisplayName("创建科室测试")
    class CreateTests {

        @Test
        @DisplayName("创建科室成功")
        void createSuccess() {
            // Given
            when(departmentRepository.existsByDeptCodeAndDeletedFalse("DEPT_NEW")).thenReturn(false);
            when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> {
                Department dept = invocation.getArgument(0);
                dept.setId("dept-new");
                return dept;
            });

            // When
            Result<DepartmentVO> result = departmentService.create(testDepartmentDTO);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals("创建成功", result.getMessage());
            assertNotNull(result.getData());

            verify(departmentRepository).save(any(Department.class));
        }

        @Test
        @DisplayName("创建科室带父级科室")
        void createWithParent() {
            // Given
            testDepartmentDTO.setParentId("dept-001");

            when(departmentRepository.existsByDeptCodeAndDeletedFalse("DEPT_NEW")).thenReturn(false);
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));
            when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> {
                Department dept = invocation.getArgument(0);
                dept.setId("dept-new");
                assertEquals(2, dept.getDeptLevel());
                return dept;
            });

            // When
            Result<DepartmentVO> result = departmentService.create(testDepartmentDTO);

            // Then
            assertEquals(0, result.getCode());
        }

        @Test
        @DisplayName("创建科室父级不存在")
        void createWithNonExistentParent() {
            // Given
            testDepartmentDTO.setParentId("nonexistent");

            when(departmentRepository.existsByDeptCodeAndDeletedFalse("DEPT_NEW")).thenReturn(false);
            when(departmentRepository.findById("nonexistent")).thenReturn(Optional.empty());
            when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> {
                Department dept = invocation.getArgument(0);
                assertEquals(1, dept.getDeptLevel());
                return dept;
            });

            // When
            Result<DepartmentVO> result = departmentService.create(testDepartmentDTO);

            // Then
            assertEquals(0, result.getCode());
        }

        @Test
        @DisplayName("科室编码已存在")
        void createDeptCodeExists() {
            // Given
            when(departmentRepository.existsByDeptCodeAndDeletedFalse("DEPT_NEW")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> departmentService.create(testDepartmentDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), exception.getCode());
            assertEquals("科室编码已存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("更新科室测试")
    class UpdateTests {

        @Test
        @DisplayName("更新科室成功")
        void updateSuccess() {
            // Given
            testDepartmentDTO.setId("dept-001");
            testDepartmentDTO.setDeptCode("DEPT001");

            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));
            when(departmentRepository.save(any(Department.class))).thenReturn(testDepartment);

            // When
            Result<DepartmentVO> result = departmentService.update(testDepartmentDTO);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals("更新成功", result.getMessage());
            assertNotNull(result.getData());

            verify(departmentRepository).save(any(Department.class));
        }

        @Test
        @DisplayName("科室不存在")
        void updateDepartmentNotFound() {
            // Given
            testDepartmentDTO.setId("nonexistent");
            when(departmentRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> departmentService.update(testDepartmentDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("科室不存在", exception.getMessage());
        }

        @Test
        @DisplayName("科室已删除")
        void updateDepartmentDeleted() {
            // Given
            testDepartmentDTO.setId("dept-001");
            testDepartment.setDeleted(true);
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> departmentService.update(testDepartmentDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("科室已删除", exception.getMessage());
        }

        @Test
        @DisplayName("更新科室父级")
        void updateParent() {
            // Given
            testDepartmentDTO.setId("dept-002");
            testDepartmentDTO.setParentId("dept-001");

            Department newParent = new Department();
            newParent.setId("dept-001");
            newParent.setDeptLevel(1);

            when(departmentRepository.findById("dept-002")).thenReturn(Optional.of(testChildDepartment));
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(newParent));
            when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> {
                Department dept = invocation.getArgument(0);
                assertEquals(2, dept.getDeptLevel());
                return dept;
            });

            // When
            Result<DepartmentVO> result = departmentService.update(testDepartmentDTO);

            // Then
            assertEquals(0, result.getCode());
        }

        @Test
        @DisplayName("不能将科室设为自己的子科室")
        void updateSelfAsParent() {
            // Given
            testDepartmentDTO.setId("dept-001");
            testDepartmentDTO.setParentId("dept-001");

            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> departmentService.update(testDepartmentDTO));
            assertEquals(ErrorCode.PARAM_ERROR.getCode(), exception.getCode());
            assertEquals("不能将科室设为自己的子科室", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("删除科室测试")
    class DeleteTests {

        @Test
        @DisplayName("删除科室成功")
        void deleteSuccess() {
            // Given
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));
            when(departmentRepository.countByParentIdAndDeletedFalse("dept-001")).thenReturn(0L);
            when(userRepository.countByDeptIdAndDeletedFalse("dept-001")).thenReturn(0L);
            when(departmentRepository.save(any(Department.class))).thenReturn(testDepartment);

            // When
            Result<Void> result = departmentService.delete("dept-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertTrue(testDepartment.getDeleted());

            verify(departmentRepository).save(any(Department.class));
        }

        @Test
        @DisplayName("删除科室不存在")
        void deleteDepartmentNotFound() {
            // Given
            when(departmentRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> departmentService.delete("nonexistent"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("科室不存在", exception.getMessage());
        }

        @Test
        @DisplayName("删除已删除科室")
        void deleteAlreadyDeleted() {
            // Given
            testDepartment.setDeleted(true);
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> departmentService.delete("dept-001"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("科室已删除", exception.getMessage());
        }

        @Test
        @DisplayName("科室有子科室不可删除")
        void deleteDepartmentWithChildren() {
            // Given
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));
            when(departmentRepository.countByParentIdAndDeletedFalse("dept-001")).thenReturn(2L);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> departmentService.delete("dept-001"));
            assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
            assertTrue(exception.getMessage().contains("子科室"));
        }

        @Test
        @DisplayName("科室有用户不可删除")
        void deleteDepartmentWithUsers() {
            // Given
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));
            when(departmentRepository.countByParentIdAndDeletedFalse("dept-001")).thenReturn(0L);
            when(userRepository.countByDeptIdAndDeletedFalse("dept-001")).thenReturn(5L);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> departmentService.delete("dept-001"));
            assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
            assertTrue(exception.getMessage().contains("用户"));
        }
    }

    @Nested
    @DisplayName("获取科室详情测试")
    class GetByIdTests {

        @Test
        @DisplayName("获取科室成功")
        void getByIdSuccess() {
            // Given
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));

            // When
            Result<DepartmentVO> result = departmentService.getById("dept-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("dept-001", result.getData().getId());
            assertEquals("内科", result.getData().getDeptName());
        }

        @Test
        @DisplayName("获取科室不存在")
        void getByIdNotFound() {
            // Given
            when(departmentRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> departmentService.getById("nonexistent"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("获取科室已删除")
        void getByIdDeleted() {
            // Given
            testDepartment.setDeleted(true);
            when(departmentRepository.findById("dept-001")).thenReturn(Optional.of(testDepartment));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> departmentService.getById("dept-001"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("科室已删除", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("分页查询科室测试")
    class PageTests {

        @Test
        @DisplayName("分页查询成功")
        void pageSuccess() {
            // Given
            List<Department> departments = Arrays.asList(testDepartment);
            Page<Department> page = new PageImpl<>(departments);
            when(departmentRepository.findByCondition(any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // When
            Result<PageResult<DepartmentVO>> result = departmentService.page(
                    "内科", "DEPT001", "临床", "NORMAL", null, 1, 10);

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
            Page<Department> emptyPage = new PageImpl<>(Collections.emptyList());
            when(departmentRepository.findByCondition(any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // When
            Result<PageResult<DepartmentVO>> result = departmentService.page(
                    null, null, null, null, null, 1, 10);

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().getList().isEmpty());
            assertEquals(0L, result.getData().getTotal());
        }

        @Test
        @DisplayName("按科室类型查询")
        void pageByDeptType() {
            // Given
            List<Department> clinicalDepts = Arrays.asList(testDepartment);
            Page<Department> page = new PageImpl<>(clinicalDepts);
            when(departmentRepository.findByCondition(any(), any(), eq("临床"), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // When
            Result<PageResult<DepartmentVO>> result = departmentService.page(
                    null, null, "临床", null, null, 1, 10);

            // Then
            assertEquals(0, result.getCode());
            assertEquals("临床", result.getData().getList().get(0).getDeptType());
        }
    }

    @Nested
    @DisplayName("获取所有科室列表测试")
    class ListAllTests {

        @Test
        @DisplayName("获取所有科室成功")
        void listAllSuccess() {
            // Given
            when(departmentRepository.findByDeletedFalseOrderBySortOrderAsc())
                    .thenReturn(Arrays.asList(testDepartment, testChildDepartment));

            // When
            Result<List<DepartmentVO>> result = departmentService.listAll();

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(2, result.getData().size());
        }

        @Test
        @DisplayName("获取所有科室空列表")
        void listAllEmpty() {
            // Given
            when(departmentRepository.findByDeletedFalseOrderBySortOrderAsc())
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<DepartmentVO>> result = departmentService.listAll();

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("获取科室树测试")
    class GetTreeTests {

        @Test
        @DisplayName("获取科室树成功")
        void getTreeSuccess() {
            // Given
            when(departmentRepository.findByDeletedFalseOrderBySortOrderAsc())
                    .thenReturn(Arrays.asList(testDepartment, testChildDepartment));

            // When
            Result<List<DepartmentVO>> result = departmentService.getTree();

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals("内科", result.getData().get(0).getDeptName());
            assertNotNull(result.getData().get(0).getChildren());
            assertEquals(1, result.getData().get(0).getChildren().size());
        }

        @Test
        @DisplayName("获取科室树空")
        void getTreeEmpty() {
            // Given
            when(departmentRepository.findByDeletedFalseOrderBySortOrderAsc())
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<DepartmentVO>> result = departmentService.getTree();

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("获取子科室列表测试")
    class ListByParentIdTests {

        @Test
        @DisplayName("获取子科室成功")
        void listByParentIdSuccess() {
            // Given
            when(departmentRepository.findByParentIdAndDeletedFalseOrderBySortOrderAsc("dept-001"))
                    .thenReturn(Arrays.asList(testChildDepartment));

            // When
            Result<List<DepartmentVO>> result = departmentService.listByParentId("dept-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals("心血管内科", result.getData().get(0).getDeptName());
        }

        @Test
        @DisplayName("无子科室")
        void listByParentIdEmpty() {
            // Given
            when(departmentRepository.findByParentIdAndDeletedFalseOrderBySortOrderAsc("dept-001"))
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<DepartmentVO>> result = departmentService.listByParentId("dept-001");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("按科室类型查询测试")
    class ListByTypeTests {

        @Test
        @DisplayName("按类型查询成功")
        void listByTypeSuccess() {
            // Given
            when(departmentRepository.findByDeptTypeAndDeletedFalseOrderBySortOrderAsc("临床"))
                    .thenReturn(Arrays.asList(testDepartment, testChildDepartment));

            // When
            Result<List<DepartmentVO>> result = departmentService.listByType("临床");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals(2, result.getData().size());
        }

        @Test
        @DisplayName("按类型查询空结果")
        void listByTypeEmpty() {
            // Given
            when(departmentRepository.findByDeptTypeAndDeletedFalseOrderBySortOrderAsc("行政"))
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<DepartmentVO>> result = departmentService.listByType("行政");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }
}
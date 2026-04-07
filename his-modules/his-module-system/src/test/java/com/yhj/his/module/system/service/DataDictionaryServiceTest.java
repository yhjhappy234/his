package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.system.dto.DataDictionaryDTO;
import com.yhj.his.module.system.entity.DataDictionary;
import com.yhj.his.module.system.repository.DataDictionaryRepository;
import com.yhj.his.module.system.service.impl.DataDictionaryServiceImpl;
import com.yhj.his.module.system.vo.DataDictionaryVO;
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
 * DataDictionaryService 单元测试
 * 目标覆盖率: 90%+
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("数据字典服务测试")
class DataDictionaryServiceTest {

    @Mock
    private DataDictionaryRepository dataDictionaryRepository;

    @InjectMocks
    private DataDictionaryServiceImpl dataDictionaryService;

    private DataDictionary testDictionary;
    private DataDictionary testChildDictionary;
    private DataDictionaryDTO testDictionaryDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试字典（父级）
        testDictionary = new DataDictionary();
        testDictionary.setId("dict-001");
        testDictionary.setDictType("gender");
        testDictionary.setDictCode("MALE");
        testDictionary.setDictName("男");
        testDictionary.setDictValue("1");
        testDictionary.setParentCode(null);
        testDictionary.setDictLevel(1);
        testDictionary.setSortOrder(1);
        testDictionary.setIsEnabled(true);
        testDictionary.setIsDefault(true);
        testDictionary.setDescription("性别-男");
        testDictionary.setDeleted(false);
        testDictionary.setCreateTime(LocalDateTime.now());

        // 初始化子字典
        testChildDictionary = new DataDictionary();
        testChildDictionary.setId("dict-002");
        testChildDictionary.setDictType("gender");
        testChildDictionary.setDictCode("FEMALE");
        testChildDictionary.setDictName("女");
        testChildDictionary.setDictValue("2");
        testChildDictionary.setParentCode(null);
        testChildDictionary.setDictLevel(1);
        testChildDictionary.setSortOrder(2);
        testChildDictionary.setIsEnabled(true);
        testChildDictionary.setIsDefault(false);
        testChildDictionary.setDescription("性别-女");
        testChildDictionary.setDeleted(false);
        testChildDictionary.setCreateTime(LocalDateTime.now());

        // 初始化测试DTO
        testDictionaryDTO = new DataDictionaryDTO();
        testDictionaryDTO.setDictType("status");
        testDictionaryDTO.setDictCode("ACTIVE");
        testDictionaryDTO.setDictName("启用");
        testDictionaryDTO.setDictValue("1");
        testDictionaryDTO.setSortOrder(1);
        testDictionaryDTO.setIsEnabled(true);
        testDictionaryDTO.setIsDefault(true);
    }

    @Nested
    @DisplayName("创建字典项测试")
    class CreateTests {

        @Test
        @DisplayName("创建字典项成功")
        void createSuccess() {
            // Given
            when(dataDictionaryRepository.existsByDictTypeAndDictCodeAndDeletedFalse("status", "ACTIVE"))
                    .thenReturn(false);
            when(dataDictionaryRepository.save(any(DataDictionary.class))).thenAnswer(invocation -> {
                DataDictionary dict = invocation.getArgument(0);
                dict.setId("dict-new");
                return dict;
            });

            // When
            Result<DataDictionaryVO> result = dataDictionaryService.create(testDictionaryDTO);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals("创建成功", result.getMessage());
            assertNotNull(result.getData());

            verify(dataDictionaryRepository).save(any(DataDictionary.class));
        }

        @Test
        @DisplayName("创建字典项带父级")
        void createWithParent() {
            // Given
            testDictionaryDTO.setParentCode("MALE");

            DataDictionary parentDict = new DataDictionary();
            parentDict.setDictType("status");
            parentDict.setDictCode("MALE");
            parentDict.setDictLevel(1);

            when(dataDictionaryRepository.existsByDictTypeAndDictCodeAndDeletedFalse("status", "ACTIVE"))
                    .thenReturn(false);
            when(dataDictionaryRepository.findByDictTypeAndDictCodeAndDeletedFalse("status", "MALE"))
                    .thenReturn(Optional.of(parentDict));
            when(dataDictionaryRepository.save(any(DataDictionary.class))).thenAnswer(invocation -> {
                DataDictionary dict = invocation.getArgument(0);
                assertEquals(2, dict.getDictLevel());
                return dict;
            });

            // When
            Result<DataDictionaryVO> result = dataDictionaryService.create(testDictionaryDTO);

            // Then
            assertEquals(0, result.getCode());
        }

        @Test
        @DisplayName("创建字典项父级不存在")
        void createWithNonExistentParent() {
            // Given
            testDictionaryDTO.setParentCode("NONEXISTENT");

            when(dataDictionaryRepository.existsByDictTypeAndDictCodeAndDeletedFalse("status", "ACTIVE"))
                    .thenReturn(false);
            when(dataDictionaryRepository.findByDictTypeAndDictCodeAndDeletedFalse("status", "NONEXISTENT"))
                    .thenReturn(Optional.empty());
            when(dataDictionaryRepository.save(any(DataDictionary.class))).thenAnswer(invocation -> {
                DataDictionary dict = invocation.getArgument(0);
                assertEquals(1, dict.getDictLevel());
                return dict;
            });

            // When
            Result<DataDictionaryVO> result = dataDictionaryService.create(testDictionaryDTO);

            // Then
            assertEquals(0, result.getCode());
        }

        @Test
        @DisplayName("字典编码已存在")
        void createDictCodeExists() {
            // Given
            when(dataDictionaryRepository.existsByDictTypeAndDictCodeAndDeletedFalse("status", "ACTIVE"))
                    .thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> dataDictionaryService.create(testDictionaryDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), exception.getCode());
            assertEquals("字典编码已存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("更新字典项测试")
    class UpdateTests {

        @Test
        @DisplayName("更新字典项成功")
        void updateSuccess() {
            // Given
            testDictionaryDTO.setId("dict-001");
            testDictionaryDTO.setDictType("gender");

            when(dataDictionaryRepository.findById("dict-001")).thenReturn(Optional.of(testDictionary));
            when(dataDictionaryRepository.save(any(DataDictionary.class))).thenReturn(testDictionary);

            // When
            Result<DataDictionaryVO> result = dataDictionaryService.update(testDictionaryDTO);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals("更新成功", result.getMessage());
            assertNotNull(result.getData());

            verify(dataDictionaryRepository).save(any(DataDictionary.class));
        }

        @Test
        @DisplayName("字典项不存在")
        void updateDictionaryNotFound() {
            // Given
            testDictionaryDTO.setId("nonexistent");
            when(dataDictionaryRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> dataDictionaryService.update(testDictionaryDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("字典项不存在", exception.getMessage());
        }

        @Test
        @DisplayName("字典项已删除")
        void updateDictionaryDeleted() {
            // Given
            testDictionaryDTO.setId("dict-001");
            testDictionaryDTO.setDictType("gender");
            testDictionary.setDeleted(true);
            when(dataDictionaryRepository.findById("dict-001")).thenReturn(Optional.of(testDictionary));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> dataDictionaryService.update(testDictionaryDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("字典项已删除", exception.getMessage());
        }

        @Test
        @DisplayName("更新字典项父级")
        void updateParent() {
            // Given
            testDictionaryDTO.setId("dict-001");
            testDictionaryDTO.setDictType("gender");
            testDictionaryDTO.setParentCode("MALE");

            DataDictionary parentDict = new DataDictionary();
            parentDict.setDictType("gender");
            parentDict.setDictCode("MALE");
            parentDict.setDictLevel(1);

            when(dataDictionaryRepository.findById("dict-001")).thenReturn(Optional.of(testDictionary));
            when(dataDictionaryRepository.findByDictTypeAndDictCodeAndDeletedFalse("gender", "MALE"))
                    .thenReturn(Optional.of(parentDict));
            when(dataDictionaryRepository.save(any(DataDictionary.class))).thenAnswer(invocation -> {
                DataDictionary dict = invocation.getArgument(0);
                assertEquals(2, dict.getDictLevel());
                return dict;
            });

            // When
            Result<DataDictionaryVO> result = dataDictionaryService.update(testDictionaryDTO);

            // Then
            assertEquals(0, result.getCode());
        }
    }

    @Nested
    @DisplayName("删除字典项测试")
    class DeleteTests {

        @Test
        @DisplayName("删除字典项成功")
        void deleteSuccess() {
            // Given
            when(dataDictionaryRepository.findById("dict-001")).thenReturn(Optional.of(testDictionary));
            when(dataDictionaryRepository.findByDictTypeAndParentCodeAndDeletedFalseOrderBySortOrderAsc("gender", "MALE"))
                    .thenReturn(Collections.emptyList());
            when(dataDictionaryRepository.save(any(DataDictionary.class))).thenReturn(testDictionary);

            // When
            Result<Void> result = dataDictionaryService.delete("dict-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertTrue(testDictionary.getDeleted());

            verify(dataDictionaryRepository).save(any(DataDictionary.class));
        }

        @Test
        @DisplayName("删除字典项不存在")
        void deleteDictionaryNotFound() {
            // Given
            when(dataDictionaryRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> dataDictionaryService.delete("nonexistent"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("字典项不存在", exception.getMessage());
        }

        @Test
        @DisplayName("删除已删除字典项")
        void deleteAlreadyDeleted() {
            // Given
            testDictionary.setDeleted(true);
            when(dataDictionaryRepository.findById("dict-001")).thenReturn(Optional.of(testDictionary));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> dataDictionaryService.delete("dict-001"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("字典项已删除", exception.getMessage());
        }

        @Test
        @DisplayName("字典项有子项不可删除")
        void deleteDictionaryWithChildren() {
            // Given
            when(dataDictionaryRepository.findById("dict-001")).thenReturn(Optional.of(testDictionary));
            when(dataDictionaryRepository.findByDictTypeAndParentCodeAndDeletedFalseOrderBySortOrderAsc("gender", "MALE"))
                    .thenReturn(Arrays.asList(testChildDictionary));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> dataDictionaryService.delete("dict-001"));
            assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
            assertTrue(exception.getMessage().contains("子项"));
        }
    }

    @Nested
    @DisplayName("获取字典项详情测试")
    class GetByIdTests {

        @Test
        @DisplayName("获取字典项成功")
        void getByIdSuccess() {
            // Given
            when(dataDictionaryRepository.findById("dict-001")).thenReturn(Optional.of(testDictionary));

            // When
            Result<DataDictionaryVO> result = dataDictionaryService.getById("dict-001");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("dict-001", result.getData().getId());
            assertEquals("gender", result.getData().getDictType());
            assertEquals("MALE", result.getData().getDictCode());
        }

        @Test
        @DisplayName("获取字典项不存在")
        void getByIdNotFound() {
            // Given
            when(dataDictionaryRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> dataDictionaryService.getById("nonexistent"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("获取字典项已删除")
        void getByIdDeleted() {
            // Given
            testDictionary.setDeleted(true);
            when(dataDictionaryRepository.findById("dict-001")).thenReturn(Optional.of(testDictionary));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> dataDictionaryService.getById("dict-001"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
            assertEquals("字典项已删除", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("分页查询字典项测试")
    class PageTests {

        @Test
        @DisplayName("分页查询成功")
        void pageSuccess() {
            // Given
            List<DataDictionary> dicts = Arrays.asList(testDictionary, testChildDictionary);
            Page<DataDictionary> page = new PageImpl<>(dicts);
            when(dataDictionaryRepository.findByCondition(any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // When
            Result<PageResult<DataDictionaryVO>> result = dataDictionaryService.page(
                    "gender", "男", "MALE", true, 1, 10);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(2, result.getData().getList().size());
            assertEquals(2L, result.getData().getTotal());
        }

        @Test
        @DisplayName("分页查询空结果")
        void pageEmpty() {
            // Given
            Page<DataDictionary> emptyPage = new PageImpl<>(Collections.emptyList());
            when(dataDictionaryRepository.findByCondition(any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // When
            Result<PageResult<DataDictionaryVO>> result = dataDictionaryService.page(
                    null, null, null, null, 1, 10);

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().getList().isEmpty());
            assertEquals(0L, result.getData().getTotal());
        }
    }

    @Nested
    @DisplayName("按类型查询字典项测试")
    class ListByTypeTests {

        @Test
        @DisplayName("按类型查询成功")
        void listByTypeSuccess() {
            // Given
            when(dataDictionaryRepository.findByDictTypeAndDeletedFalseOrderBySortOrderAsc("gender"))
                    .thenReturn(Arrays.asList(testDictionary, testChildDictionary));

            // When
            Result<List<DataDictionaryVO>> result = dataDictionaryService.listByType("gender");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals(2, result.getData().size());
        }

        @Test
        @DisplayName("按类型查询空结果")
        void listByTypeEmpty() {
            // Given
            when(dataDictionaryRepository.findByDictTypeAndDeletedFalseOrderBySortOrderAsc("nonexistent"))
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<DataDictionaryVO>> result = dataDictionaryService.listByType("nonexistent");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("按类型查询启用的字典项测试")
    class ListEnabledByTypeTests {

        @Test
        @DisplayName("查询启用字典项成功")
        void listEnabledByTypeSuccess() {
            // Given
            when(dataDictionaryRepository.findByDictTypeAndIsEnabledTrueAndDeletedFalseOrderBySortOrderAsc("gender"))
                    .thenReturn(Arrays.asList(testDictionary, testChildDictionary));

            // When
            Result<List<DataDictionaryVO>> result = dataDictionaryService.listEnabledByType("gender");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals(2, result.getData().size());
        }

        @Test
        @DisplayName("查询启用字典项空结果")
        void listEnabledByTypeEmpty() {
            // Given
            when(dataDictionaryRepository.findByDictTypeAndIsEnabledTrueAndDeletedFalseOrderBySortOrderAsc("nonexistent"))
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<DataDictionaryVO>> result = dataDictionaryService.listEnabledByType("nonexistent");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("获取所有字典类型测试")
    class ListAllTypesTests {

        @Test
        @DisplayName("获取所有字典类型成功")
        void listAllTypesSuccess() {
            // Given
            when(dataDictionaryRepository.findAllDictTypes())
                    .thenReturn(Arrays.asList("gender", "status", "type"));

            // When
            Result<List<String>> result = dataDictionaryService.listAllTypes();

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals(3, result.getData().size());
            assertTrue(result.getData().contains("gender"));
        }

        @Test
        @DisplayName("获取所有字典类型空结果")
        void listAllTypesEmpty() {
            // Given
            when(dataDictionaryRepository.findAllDictTypes())
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<String>> result = dataDictionaryService.listAllTypes();

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("获取字典树测试")
    class GetTreeTests {

        @Test
        @DisplayName("获取字典树成功")
        void getTreeSuccess() {
            // Given
            DataDictionary parentDict = new DataDictionary();
            parentDict.setId("dict-parent");
            parentDict.setDictType("status");
            parentDict.setDictCode("STATUS");
            parentDict.setDictName("状态");
            parentDict.setParentCode(null);
            parentDict.setDictLevel(1);
            parentDict.setSortOrder(1);
            parentDict.setDeleted(false);

            DataDictionary childDict = new DataDictionary();
            childDict.setId("dict-child");
            childDict.setDictType("status");
            childDict.setDictCode("ACTIVE");
            childDict.setDictName("启用");
            childDict.setParentCode("STATUS");
            childDict.setDictLevel(2);
            childDict.setSortOrder(1);
            childDict.setDeleted(false);

            when(dataDictionaryRepository.findByDictTypeAndDeletedFalseOrderBySortOrderAsc("status"))
                    .thenReturn(Arrays.asList(parentDict, childDict));

            // When
            Result<List<DataDictionaryVO>> result = dataDictionaryService.getTree("status");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals(1, result.getData().size());
            assertEquals("STATUS", result.getData().get(0).getDictCode());
            assertNotNull(result.getData().get(0).getChildren());
            assertEquals(1, result.getData().get(0).getChildren().size());
        }

        @Test
        @DisplayName("获取字典树空结果")
        void getTreeEmpty() {
            // Given
            when(dataDictionaryRepository.findByDictTypeAndDeletedFalseOrderBySortOrderAsc("nonexistent"))
                    .thenReturn(Collections.emptyList());

            // When
            Result<List<DataDictionaryVO>> result = dataDictionaryService.getTree("nonexistent");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("获取默认值测试")
    class GetDefaultTests {

        @Test
        @DisplayName("获取默认值成功")
        void getDefaultSuccess() {
            // Given
            when(dataDictionaryRepository.findByDictTypeAndIsEnabledTrueAndIsDefaultTrueAndDeletedFalse("gender"))
                    .thenReturn(Optional.of(testDictionary));

            // When
            Result<DataDictionaryVO> result = dataDictionaryService.getDefault("gender");

            // Then
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("MALE", result.getData().getDictCode());
        }

        @Test
        @DisplayName("获取默认值不存在")
        void getDefaultNotFound() {
            // Given
            when(dataDictionaryRepository.findByDictTypeAndIsEnabledTrueAndIsDefaultTrueAndDeletedFalse("nonexistent"))
                    .thenReturn(Optional.empty());

            // When
            Result<DataDictionaryVO> result = dataDictionaryService.getDefault("nonexistent");

            // Then
            assertEquals(0, result.getCode());
            assertNull(result.getData());
        }
    }
}
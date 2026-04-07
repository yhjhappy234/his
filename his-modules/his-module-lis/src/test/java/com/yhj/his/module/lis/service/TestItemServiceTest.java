package com.yhj.his.module.lis.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestItemCreateDTO;
import com.yhj.his.module.lis.dto.TestItemUpdateDTO;
import com.yhj.his.module.lis.entity.TestItem;
import com.yhj.his.module.lis.enums.TestItemCategory;
import com.yhj.his.module.lis.enums.TestItemStatus;
import com.yhj.his.module.lis.enums.SpecimenType;
import com.yhj.his.module.lis.repository.TestItemRepository;
import com.yhj.his.module.lis.service.impl.TestItemServiceImpl;
import com.yhj.his.module.lis.vo.TestItemVO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * TestItemService单元测试
 * 测试检验项目管理功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("检验项目服务测试")
class TestItemServiceTest {

    @Mock
    private TestItemRepository testItemRepository;

    @InjectMocks
    private TestItemServiceImpl testItemService;

    private TestItem testItem;
    private TestItemCreateDTO createDTO;
    private TestItemUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试检验项目
        testItem = new TestItem();
        testItem.setId("test-item-001");
        testItem.setItemCode("GLU");
        testItem.setItemName("血糖");
        testItem.setItemNameEn("Glucose");
        testItem.setPinyinCode("XT");
        testItem.setCategory(TestItemCategory.BIOCHEMISTRY);
        testItem.setSpecimenType(SpecimenType.BLOOD);
        testItem.setTestMethod("酶法");
        testItem.setUnit("mmol/L");
        testItem.setReferenceMin(new BigDecimal("3.9"));
        testItem.setReferenceMax(new BigDecimal("6.1"));
        testItem.setReferenceText("3.9-6.1 mmol/L");
        testItem.setCriticalLow(new BigDecimal("2.8"));
        testItem.setCriticalHigh(new BigDecimal("28.0"));
        testItem.setCritical(true);
        testItem.setPrice(new BigDecimal("10.00"));
        testItem.setTurnaroundTime(2);
        testItem.setInstrumentId("instrument-001");
        testItem.setReagentId("reagent-001");
        testItem.setStatus(TestItemStatus.NORMAL);
        testItem.setRemark("空腹血糖检测");
        testItem.setCreateTime(LocalDateTime.now());
        testItem.setUpdateTime(LocalDateTime.now());

        // 初始化创建DTO
        createDTO = new TestItemCreateDTO();
        createDTO.setItemCode("GLU");
        createDTO.setItemName("血糖");
        createDTO.setItemNameEn("Glucose");
        createDTO.setPinyinCode("XT");
        createDTO.setCategory("BIOCHEMISTRY");
        createDTO.setSpecimenType("BLOOD");
        createDTO.setTestMethod("酶法");
        createDTO.setUnit("mmol/L");
        createDTO.setReferenceMin(new BigDecimal("3.9"));
        createDTO.setReferenceMax(new BigDecimal("6.1"));
        createDTO.setReferenceText("3.9-6.1 mmol/L");
        createDTO.setCriticalLow(new BigDecimal("2.8"));
        createDTO.setCriticalHigh(new BigDecimal("28.0"));
        createDTO.setCritical(true);
        createDTO.setPrice(new BigDecimal("10.00"));
        createDTO.setTurnaroundTime(2);
        createDTO.setInstrumentId("instrument-001");
        createDTO.setReagentId("reagent-001");
        createDTO.setRemark("空腹血糖检测");

        // 初始化更新DTO
        updateDTO = new TestItemUpdateDTO();
        updateDTO.setItemName("血糖(更新)");
        updateDTO.setUnit("mmol/L");
        updateDTO.setPrice(new BigDecimal("15.00"));
    }

    @Nested
    @DisplayName("创建检验项目测试")
    class CreateTestItemTests {

        @Test
        @DisplayName("成功创建检验项目")
        void createSuccessfully() {
            // Given
            when(testItemRepository.existsByItemCode(anyString())).thenReturn(false);
            when(testItemRepository.save(any(TestItem.class))).thenReturn(testItem);

            // When
            TestItemVO result = testItemService.create(createDTO);

            // Then
            assertNotNull(result);
            assertEquals("GLU", result.getItemCode());
            assertEquals("血糖", result.getItemName());
            assertEquals("BIOCHEMISTRY", result.getCategory());
            assertEquals("BLOOD", result.getSpecimenType());
            assertEquals(new BigDecimal("10.00"), result.getPrice());
            assertEquals("NORMAL", result.getStatus());
            assertTrue(result.getCritical());

            verify(testItemRepository).existsByItemCode("GLU");
            verify(testItemRepository).save(any(TestItem.class));
        }

        @Test
        @DisplayName("创建失败-项目编码已存在")
        void createFailedWithDuplicateCode() {
            // Given
            when(testItemRepository.existsByItemCode("GLU")).thenReturn(true);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testItemService.create(createDTO)
            );
            assertEquals("项目编码已存在: GLU", exception.getMessage());

            verify(testItemRepository).existsByItemCode("GLU");
            verify(testItemRepository, never()).save(any(TestItem.class));
        }

        @Test
        @DisplayName("创建检验项目-无危急值设置")
        void createWithoutCriticalFlag() {
            // Given
            createDTO.setCritical(null);
            TestItem savedItem = new TestItem();
            savedItem.setId("test-item-002");
            savedItem.setItemCode("ALT");
            savedItem.setItemName("谷丙转氨酶");
            savedItem.setCategory(TestItemCategory.BIOCHEMISTRY);
            savedItem.setSpecimenType(SpecimenType.BLOOD);
            savedItem.setStatus(TestItemStatus.NORMAL);
            savedItem.setCritical(false);
            savedItem.setCreateTime(LocalDateTime.now());
            savedItem.setUpdateTime(LocalDateTime.now());

            when(testItemRepository.existsByItemCode(anyString())).thenReturn(false);
            when(testItemRepository.save(any(TestItem.class))).thenReturn(savedItem);

            // When
            TestItemVO result = testItemService.create(createDTO);

            // Then
            assertNotNull(result);
            assertFalse(result.getCritical());
        }
    }

    @Nested
    @DisplayName("更新检验项目测试")
    class UpdateTestItemTests {

        @Test
        @DisplayName("成功更新检验项目")
        void updateSuccessfully() {
            // Given
            when(testItemRepository.findById("test-item-001")).thenReturn(Optional.of(testItem));
            when(testItemRepository.save(any(TestItem.class))).thenReturn(testItem);

            // When
            TestItemVO result = testItemService.update("test-item-001", updateDTO);

            // Then
            assertNotNull(result);
            assertEquals("血糖(更新)", result.getItemName());
            assertEquals(new BigDecimal("15.00"), result.getPrice());

            verify(testItemRepository).findById("test-item-001");
            verify(testItemRepository).save(any(TestItem.class));
        }

        @Test
        @DisplayName("更新失败-项目不存在")
        void updateFailedWithNotFound() {
            // Given
            when(testItemRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testItemService.update("non-existent", updateDTO)
            );
            assertEquals("检验项目不存在: non-existent", exception.getMessage());

            verify(testItemRepository).findById("non-existent");
            verify(testItemRepository, never()).save(any(TestItem.class));
        }

        @Test
        @DisplayName("更新检验项目-部分字段更新")
        void updatePartialFields() {
            // Given
            TestItemUpdateDTO partialDTO = new TestItemUpdateDTO();
            partialDTO.setPrice(new BigDecimal("20.00"));

            when(testItemRepository.findById("test-item-001")).thenReturn(Optional.of(testItem));
            when(testItemRepository.save(any(TestItem.class))).thenReturn(testItem);

            // When
            TestItemVO result = testItemService.update("test-item-001", partialDTO);

            // Then
            assertNotNull(result);
            assertEquals("血糖", result.getItemName()); // 名称未更新
            assertEquals(new BigDecimal("20.00"), result.getPrice());
        }
    }

    @Nested
    @DisplayName("查询检验项目测试")
    class QueryTestItemTests {

        @Test
        @DisplayName("根据ID查询检验项目")
        void getByIdSuccessfully() {
            // Given
            when(testItemRepository.findById("test-item-001")).thenReturn(Optional.of(testItem));

            // When
            TestItemVO result = testItemService.getById("test-item-001");

            // Then
            assertNotNull(result);
            assertEquals("test-item-001", result.getId());
            assertEquals("GLU", result.getItemCode());
            assertEquals("血糖", result.getItemName());
        }

        @Test
        @DisplayName("根据ID查询失败-项目不存在")
        void getByIdNotFound() {
            // Given
            when(testItemRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> testItemService.getById("non-existent"));
        }

        @Test
        @DisplayName("根据项目编码查询检验项目")
        void getByItemCodeSuccessfully() {
            // Given
            when(testItemRepository.findByItemCode("GLU")).thenReturn(Optional.of(testItem));

            // When
            TestItemVO result = testItemService.getByItemCode("GLU");

            // Then
            assertNotNull(result);
            assertEquals("GLU", result.getItemCode());
        }

        @Test
        @DisplayName("根据项目编码查询失败-编码不存在")
        void getByItemCodeNotFound() {
            // Given
            when(testItemRepository.findByItemCode("UNKNOWN")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> testItemService.getByItemCode("UNKNOWN"));
        }

        @Test
        @DisplayName("分页查询检验项目")
        void listWithPageable() {
            // Given
            List<TestItem> items = Arrays.asList(testItem);
            Page<TestItem> page = new PageImpl<>(items, PageRequest.of(0, 10), 1);

            when(testItemRepository.findAll(any(Pageable.class))).thenReturn(page);

            // When
            PageResult<TestItemVO> result = testItemService.list(PageRequest.of(0, 10));

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
            assertEquals(0, result.getPageNum());
        }

        @Test
        @DisplayName("根据关键词和状态搜索检验项目")
        void searchWithKeywordAndStatus() {
            // Given
            List<TestItem> items = Arrays.asList(testItem);
            Page<TestItem> page = new PageImpl<>(items);
            Pageable pageable = PageRequest.of(0, 10);

            when(testItemRepository.findByKeywordAndStatus(anyString(), eq(TestItemStatus.NORMAL), eq(pageable)))
                .thenReturn(page);

            // When
            PageResult<TestItemVO> result = testItemService.search("血糖", TestItemStatus.NORMAL, pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
        }

        @Test
        @DisplayName("根据分类查询检验项目")
        void listByCategory() {
            // Given
            when(testItemRepository.findByCategory(TestItemCategory.BIOCHEMISTRY))
                .thenReturn(Arrays.asList(testItem));

            // When
            List<TestItemVO> result = testItemService.listByCategory(TestItemCategory.BIOCHEMISTRY);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("BIOCHEMISTRY", result.get(0).getCategory());
        }

        @Test
        @DisplayName("根据状态查询检验项目")
        void listByStatus() {
            // Given
            when(testItemRepository.findByStatusOrderByItemName(TestItemStatus.NORMAL))
                .thenReturn(Arrays.asList(testItem));

            // When
            List<TestItemVO> result = testItemService.listByStatus(TestItemStatus.NORMAL);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("NORMAL", result.get(0).getStatus());
        }

        @Test
        @DisplayName("查询有危急值的检验项目")
        void listCriticalItems() {
            // Given
            when(testItemRepository.findByCriticalTrueAndStatus(TestItemStatus.NORMAL))
                .thenReturn(Arrays.asList(testItem));

            // When
            List<TestItemVO> result = testItemService.listCriticalItems();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.get(0).getCritical());
        }
    }

    @Nested
    @DisplayName("检验项目状态管理测试")
    class StatusManagementTests {

        @Test
        @DisplayName("启用检验项目")
        void enableSuccessfully() {
            // Given
            TestItem disabledItem = new TestItem();
            disabledItem.setId("test-item-001");
            disabledItem.setItemCode("GLU");
            disabledItem.setItemName("血糖");
            disabledItem.setStatus(TestItemStatus.DISABLED);
            disabledItem.setCreateTime(LocalDateTime.now());
            disabledItem.setUpdateTime(LocalDateTime.now());

            when(testItemRepository.findById("test-item-001")).thenReturn(Optional.of(disabledItem));
            when(testItemRepository.save(any(TestItem.class))).thenReturn(testItem);

            // When
            TestItemVO result = testItemService.enable("test-item-001");

            // Then
            assertNotNull(result);
            assertEquals("NORMAL", result.getStatus());
        }

        @Test
        @DisplayName("停用检验项目")
        void disableSuccessfully() {
            // Given
            TestItem disabledItem = new TestItem();
            disabledItem.setId("test-item-001");
            disabledItem.setItemCode("GLU");
            disabledItem.setItemName("血糖");
            disabledItem.setCategory(TestItemCategory.BIOCHEMISTRY);
            disabledItem.setSpecimenType(SpecimenType.BLOOD);
            disabledItem.setStatus(TestItemStatus.DISABLED);
            disabledItem.setCreateTime(LocalDateTime.now());
            disabledItem.setUpdateTime(LocalDateTime.now());

            when(testItemRepository.findById("test-item-001")).thenReturn(Optional.of(testItem));
            when(testItemRepository.save(any(TestItem.class))).thenReturn(disabledItem);

            // When
            TestItemVO result = testItemService.disable("test-item-001");

            // Then
            assertNotNull(result);
            assertEquals("DISABLED", result.getStatus());
        }
    }

    @Nested
    @DisplayName("删除检验项目测试")
    class DeleteTestItemTests {

        @Test
        @DisplayName("成功删除检验项目(逻辑删除)")
        void deleteSuccessfully() {
            // Given
            when(testItemRepository.findById("test-item-001")).thenReturn(Optional.of(testItem));
            when(testItemRepository.save(any(TestItem.class))).thenReturn(testItem);

            // When
            testItemService.delete("test-item-001");

            // Then
            verify(testItemRepository).findById("test-item-001");
            verify(testItemRepository).save(any(TestItem.class));
        }

        @Test
        @DisplayName("删除失败-项目不存在")
        void deleteFailedWithNotFound() {
            // Given
            when(testItemRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> testItemService.delete("non-existent"));
            verify(testItemRepository, never()).save(any(TestItem.class));
        }
    }

    @Nested
    @DisplayName("检验项目编码检查测试")
    class ItemCodeCheckTests {

        @Test
        @DisplayName("项目编码已存在")
        void existsByItemCodeTrue() {
            // Given
            when(testItemRepository.existsByItemCode("GLU")).thenReturn(true);

            // When
            boolean result = testItemService.existsByItemCode("GLU");

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("项目编码不存在")
        void existsByItemCodeFalse() {
            // Given
            when(testItemRepository.existsByItemCode("UNKNOWN")).thenReturn(false);

            // When
            boolean result = testItemService.existsByItemCode("UNKNOWN");

            // Then
            assertFalse(result);
        }
    }
}
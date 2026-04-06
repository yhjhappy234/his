package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.dto.PriceAdjustDTO;
import com.yhj.his.module.finance.dto.PriceItemCreateDTO;
import com.yhj.his.module.finance.dto.PriceItemUpdateDTO;
import com.yhj.his.module.finance.entity.PriceItem;
import com.yhj.his.module.finance.repository.PriceItemRepository;
import com.yhj.his.module.finance.service.impl.PriceItemServiceImpl;
import com.yhj.his.module.finance.vo.PriceItemVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
 * PriceItemService Unit Tests
 *
 * Tests cover price management operations including:
 * - Create, update, delete price items
 * - Query operations (by ID, code, category)
 * - Price adjustment
 * - Status management
 * - Batch import
 *
 * Target coverage: 90%+
 */
@ExtendWith(MockitoExtension.class)
class PriceItemServiceTest {

    @Mock
    private PriceItemRepository priceItemRepository;

    @InjectMocks
    private PriceItemServiceImpl priceItemService;

    private PriceItemCreateDTO createDTO;
    private PriceItemUpdateDTO updateDTO;
    private PriceItem priceItem;

    @BeforeEach
    void setUp() {
        // Setup create DTO
        createDTO = new PriceItemCreateDTO();
        createDTO.setItemCode("ITEM001");
        createDTO.setItemName("Test Item");
        createDTO.setItemCategory("DRUG");
        createDTO.setItemUnit("盒");
        createDTO.setItemSpec("10mg*10片");
        createDTO.setStandardPrice(BigDecimal.valueOf(50.00));
        createDTO.setRetailPrice(BigDecimal.valueOf(60.00));
        createDTO.setWholesalePrice(BigDecimal.valueOf(55.00));
        createDTO.setInsuranceType("A");
        createDTO.setInsuranceCode("INS001");
        createDTO.setInsurancePrice(BigDecimal.valueOf(58.00));
        createDTO.setReimbursementRatio(BigDecimal.valueOf(80));
        createDTO.setEffectiveDate(LocalDate.now());
        createDTO.setRemark("Test remark");

        // Setup update DTO
        updateDTO = new PriceItemUpdateDTO();
        updateDTO.setId("item-123");
        updateDTO.setItemName("Updated Item Name");
        updateDTO.setRetailPrice(BigDecimal.valueOf(70.00));

        // Setup entity
        priceItem = new PriceItem();
        priceItem.setId("item-123");
        priceItem.setItemCode("ITEM001");
        priceItem.setItemName("Test Item");
        priceItem.setItemCategory(PriceItem.ItemCategory.DRUG);
        priceItem.setItemUnit("盒");
        priceItem.setItemSpec("10mg*10片");
        priceItem.setStandardPrice(BigDecimal.valueOf(50.00));
        priceItem.setRetailPrice(BigDecimal.valueOf(60.00));
        priceItem.setWholesalePrice(BigDecimal.valueOf(55.00));
        priceItem.setInsuranceType(PriceItem.InsuranceType.A);
        priceItem.setInsuranceCode("INS001");
        priceItem.setInsurancePrice(BigDecimal.valueOf(58.00));
        priceItem.setReimbursementRatio(BigDecimal.valueOf(80));
        priceItem.setEffectiveDate(LocalDate.now());
        priceItem.setVersionNo("V1");
        priceItem.setStatus(PriceItem.PriceItemStatus.ACTIVE);
        priceItem.setDeleted(false);
    }

    @Nested
    @DisplayName("Create Price Item Tests")
    class CreatePriceItemTests {

        @Test
        @DisplayName("Should create price item successfully with all fields")
        void shouldCreatePriceItemSuccessfully() {
            // Given
            when(priceItemRepository.existsByItemCode(anyString())).thenReturn(false);
            when(priceItemRepository.save(any(PriceItem.class))).thenReturn(priceItem);

            // When
            PriceItemVO result = priceItemService.create(createDTO);

            // Then
            assertNotNull(result);
            assertEquals("ITEM001", result.getItemCode());
            assertEquals("Test Item", result.getItemName());
            assertEquals("DRUG", result.getItemCategory());
            assertEquals(BigDecimal.valueOf(60.00), result.getRetailPrice());
            assertEquals("A", result.getInsuranceType());
            assertEquals("ACTIVE", result.getStatus());

            verify(priceItemRepository).existsByItemCode("ITEM001");
            verify(priceItemRepository).save(any(PriceItem.class));
        }

        @Test
        @DisplayName("Should throw exception when item code already exists")
        void shouldThrowExceptionWhenCodeExists() {
            // Given
            when(priceItemRepository.existsByItemCode("ITEM001")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> priceItemService.create(createDTO));
            assertTrue(exception.getMessage().contains("项目编码已存在"));

            verify(priceItemRepository).existsByItemCode("ITEM001");
            verify(priceItemRepository, never()).save(any(PriceItem.class));
        }

        @Test
        @DisplayName("Should set default values when optional fields are null")
        void shouldSetDefaultValuesWhenNull() {
            // Given
            createDTO.setEffectiveDate(null);
            createDTO.setVersionNo(null);
            when(priceItemRepository.existsByItemCode(anyString())).thenReturn(false);
            when(priceItemRepository.save(any(PriceItem.class))).thenAnswer(invocation -> {
                PriceItem savedItem = invocation.getArgument(0);
                savedItem.setId("new-id");
                return savedItem;
            });

            // When
            PriceItemVO result = priceItemService.create(createDTO);

            // Then
            assertNotNull(result);
            ArgumentCaptor<PriceItem> captor = ArgumentCaptor.forClass(PriceItem.class);
            verify(priceItemRepository).save(captor.capture());
            PriceItem savedItem = captor.getValue();
            assertEquals(LocalDate.now(), savedItem.getEffectiveDate());
            assertEquals("V1", savedItem.getVersionNo());
            assertEquals(PriceItem.PriceItemStatus.ACTIVE, savedItem.getStatus());
        }

        @Test
        @DisplayName("Should create price item without insurance info")
        void shouldCreateWithoutInsuranceInfo() {
            // Given
            createDTO.setInsuranceType(null);
            createDTO.setInsuranceCode(null);
            createDTO.setInsurancePrice(null);
            createDTO.setReimbursementRatio(null);
            when(priceItemRepository.existsByItemCode(anyString())).thenReturn(false);
            when(priceItemRepository.save(any(PriceItem.class))).thenAnswer(invocation -> {
                PriceItem savedItem = invocation.getArgument(0);
                savedItem.setId("new-id");
                return savedItem;
            });

            // When
            PriceItemVO result = priceItemService.create(createDTO);

            // Then
            assertNotNull(result);
            assertNull(result.getInsuranceType());
        }
    }

    @Nested
    @DisplayName("Update Price Item Tests")
    class UpdatePriceItemTests {

        @Test
        @DisplayName("Should update price item successfully")
        void shouldUpdatePriceItemSuccessfully() {
            // Given
            when(priceItemRepository.findById("item-123")).thenReturn(Optional.of(priceItem));
            when(priceItemRepository.save(any(PriceItem.class))).thenReturn(priceItem);

            // When
            PriceItemVO result = priceItemService.update(updateDTO);

            // Then
            assertNotNull(result);
            assertEquals("Updated Item Name", result.getItemName());
            assertEquals(BigDecimal.valueOf(70.00), result.getRetailPrice());

            verify(priceItemRepository).findById("item-123");
            verify(priceItemRepository).save(any(PriceItem.class));
        }

        @Test
        @DisplayName("Should throw exception when item not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            when(priceItemRepository.findById("item-123")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> priceItemService.update(updateDTO));
            assertTrue(exception.getMessage().contains("收费项目不存在"));

            verify(priceItemRepository).findById("item-123");
            verify(priceItemRepository, never()).save(any(PriceItem.class));
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            // Given
            PriceItemUpdateDTO partialUpdate = new PriceItemUpdateDTO();
            partialUpdate.setId("item-123");
            partialUpdate.setItemName("Only Name Update");

            when(priceItemRepository.findById("item-123")).thenReturn(Optional.of(priceItem));
            when(priceItemRepository.save(any(PriceItem.class))).thenReturn(priceItem);

            // When
            PriceItemVO result = priceItemService.update(partialUpdate);

            // Then
            assertNotNull(result);
            assertEquals("Only Name Update", priceItem.getItemName());
            // Other fields should remain unchanged
            assertEquals("ITEM001", priceItem.getItemCode());
            assertEquals(BigDecimal.valueOf(60.00), priceItem.getRetailPrice());
        }

        @Test
        @DisplayName("Should update all fields when all provided")
        void shouldUpdateAllFields() {
            // Given
            PriceItemUpdateDTO fullUpdate = new PriceItemUpdateDTO();
            fullUpdate.setId("item-123");
            fullUpdate.setItemName("Full Update");
            fullUpdate.setItemCategory("EXAM");
            fullUpdate.setItemUnit("次");
            fullUpdate.setItemSpec("新规格");
            fullUpdate.setStandardPrice(BigDecimal.valueOf(100.00));
            fullUpdate.setRetailPrice(BigDecimal.valueOf(120.00));
            fullUpdate.setWholesalePrice(BigDecimal.valueOf(110.00));
            fullUpdate.setInsuranceType("B");
            fullUpdate.setInsuranceCode("INS002");
            fullUpdate.setInsurancePrice(BigDecimal.valueOf(115.00));
            fullUpdate.setReimbursementRatio(BigDecimal.valueOf(70));
            fullUpdate.setEffectiveDate(LocalDate.now().plusDays(1));
            fullUpdate.setExpireDate(LocalDate.now().plusYears(1));
            fullUpdate.setVersionNo("V2");
            fullUpdate.setStatus("INACTIVE");
            fullUpdate.setRemark("Updated remark");

            when(priceItemRepository.findById("item-123")).thenReturn(Optional.of(priceItem));
            when(priceItemRepository.save(any(PriceItem.class))).thenReturn(priceItem);

            // When
            PriceItemVO result = priceItemService.update(fullUpdate);

            // Then
            assertNotNull(result);
            assertEquals("Full Update", priceItem.getItemName());
            assertEquals(PriceItem.ItemCategory.EXAM, priceItem.getItemCategory());
            assertEquals("次", priceItem.getItemUnit());
            assertEquals(BigDecimal.valueOf(120.00), priceItem.getRetailPrice());
            assertEquals(PriceItem.InsuranceType.B, priceItem.getInsuranceType());
            assertEquals(PriceItem.PriceItemStatus.INACTIVE, priceItem.getStatus());
        }
    }

    @Nested
    @DisplayName("Delete Price Item Tests")
    class DeletePriceItemTests {

        @Test
        @DisplayName("Should delete price item (logical delete) successfully")
        void shouldDeletePriceItemSuccessfully() {
            // Given
            when(priceItemRepository.findById("item-123")).thenReturn(Optional.of(priceItem));
            when(priceItemRepository.save(any(PriceItem.class))).thenReturn(priceItem);

            // When
            priceItemService.delete("item-123");

            // Then
            assertTrue(priceItem.getDeleted());
            verify(priceItemRepository).findById("item-123");
            verify(priceItemRepository).save(any(PriceItem.class));
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent item")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            // Given
            when(priceItemRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> priceItemService.delete("non-existent"));
            assertTrue(exception.getMessage().contains("收费项目不存在"));

            verify(priceItemRepository).findById("non-existent");
            verify(priceItemRepository, never()).save(any(PriceItem.class));
        }
    }

    @Nested
    @DisplayName("Query Price Item Tests")
    class QueryPriceItemTests {

        @Test
        @DisplayName("Should get price item by ID successfully")
        void shouldGetByIdSuccessfully() {
            // Given
            when(priceItemRepository.findById("item-123")).thenReturn(Optional.of(priceItem));

            // When
            PriceItemVO result = priceItemService.getById("item-123");

            // Then
            assertNotNull(result);
            assertEquals("item-123", result.getId());
            assertEquals("ITEM001", result.getItemCode());
            verify(priceItemRepository).findById("item-123");
        }

        @Test
        @DisplayName("Should throw exception when getting non-existent item by ID")
        void shouldThrowExceptionWhenGetByIdNotFound() {
            // Given
            when(priceItemRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class, () -> priceItemService.getById("non-existent"));
        }

        @Test
        @DisplayName("Should get price item by code successfully")
        void shouldGetByCodeSuccessfully() {
            // Given
            when(priceItemRepository.findByItemCode("ITEM001")).thenReturn(Optional.of(priceItem));

            // When
            PriceItemVO result = priceItemService.getByCode("ITEM001");

            // Then
            assertNotNull(result);
            assertEquals("ITEM001", result.getItemCode());
            verify(priceItemRepository).findByItemCode("ITEM001");
        }

        @Test
        @DisplayName("Should throw exception when getting non-existent item by code")
        void shouldThrowExceptionWhenGetByCodeNotFound() {
            // Given
            when(priceItemRepository.findByItemCode("NONEXISTENT")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class, () -> priceItemService.getByCode("NONEXISTENT"));
        }

        @Test
        @DisplayName("Should return paginated list successfully")
        void shouldReturnPaginatedList() {
            // Given
            List<PriceItem> items = Arrays.asList(priceItem);
            Page<PriceItem> page = new PageImpl<>(items);
            when(priceItemRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            // When
            PageResult<PriceItemVO> result = priceItemService.pageList("Test", "DRUG", "ACTIVE", 1, 10);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
            assertEquals(1, result.getPageNum());
            assertEquals(10, result.getPageSize());
        }

        @Test
        @DisplayName("Should return empty page when no items match criteria")
        void shouldReturnEmptyPageWhenNoMatch() {
            // Given
            Page<PriceItem> emptyPage = new PageImpl<>(Collections.emptyList());
            when(priceItemRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

            // When
            PageResult<PriceItemVO> result = priceItemService.pageList("NonExistent", "DRUG", "ACTIVE", 1, 10);

            // Then
            assertNotNull(result);
            assertTrue(result.getList().isEmpty());
            assertEquals(0L, result.getTotal());
        }

        @Test
        @DisplayName("Should return list by category")
        void shouldReturnListByCategory() {
            // Given
            List<PriceItem> items = Arrays.asList(priceItem);
            when(priceItemRepository.findByItemCategoryAndStatus(eq(PriceItem.ItemCategory.DRUG), eq(PriceItem.PriceItemStatus.ACTIVE)))
                    .thenReturn(items);

            // When
            List<PriceItemVO> result = priceItemService.listByCategory("DRUG");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("DRUG", result.get(0).getItemCategory());
        }

        @Test
        @DisplayName("Should return effective items list")
        void shouldReturnEffectiveItems() {
            // Given
            List<PriceItem> items = Arrays.asList(priceItem);
            when(priceItemRepository.findEffectiveItems(eq(PriceItem.PriceItemStatus.ACTIVE), any(LocalDate.class)))
                    .thenReturn(items);

            // When
            List<PriceItemVO> result = priceItemService.listEffectiveItems();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should search by name successfully")
        void shouldSearchByName() {
            // Given
            List<PriceItem> items = Arrays.asList(priceItem);
            when(priceItemRepository.findByItemNameContaining("Test")).thenReturn(items);

            // When
            List<PriceItemVO> result = priceItemService.searchByName("Test");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.get(0).getItemName().contains("Test"));
        }
    }

    @Nested
    @DisplayName("Price Adjustment Tests")
    class PriceAdjustmentTests {

        @Test
        @DisplayName("Should adjust price successfully")
        void shouldAdjustPriceSuccessfully() {
            // Given
            BigDecimal newPrice = BigDecimal.valueOf(65.00);
            String reason = "Market price adjustment";

            when(priceItemRepository.findById("item-123")).thenReturn(Optional.of(priceItem));
            when(priceItemRepository.save(any(PriceItem.class))).thenReturn(priceItem);

            // When
            PriceAdjustDTO result = priceItemService.adjustPrice("item-123", newPrice, reason);

            // Then
            assertNotNull(result);
            assertEquals("item-123", result.getItemId());
            assertEquals("ITEM001", result.getItemCode());
            assertEquals(BigDecimal.valueOf(60.00), result.getOldPrice());
            assertEquals(BigDecimal.valueOf(65.00), result.getNewPrice());
            assertEquals(reason, result.getReason());
            assertEquals("V1", result.getOldVersionNo());
            assertEquals("V2", result.getNewVersionNo());

            // Verify entity was updated
            assertEquals(BigDecimal.valueOf(65.00), priceItem.getRetailPrice());
            assertEquals("V2", priceItem.getVersionNo());
        }

        @Test
        @DisplayName("Should increment version number correctly")
        void shouldIncrementVersionCorrectly() {
            // Given
            priceItem.setVersionNo("V5");
            BigDecimal newPrice = BigDecimal.valueOf(70.00);

            when(priceItemRepository.findById("item-123")).thenReturn(Optional.of(priceItem));
            when(priceItemRepository.save(any(PriceItem.class))).thenReturn(priceItem);

            // When
            PriceAdjustDTO result = priceItemService.adjustPrice("item-123", newPrice, "Test");

            // Then
            assertEquals("V5", result.getOldVersionNo());
            assertEquals("V6", result.getNewVersionNo());
            assertEquals("V6", priceItem.getVersionNo());
        }

        @Test
        @DisplayName("Should throw exception when adjusting non-existent item")
        void shouldThrowExceptionWhenAdjustingNonExistent() {
            // Given
            when(priceItemRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> priceItemService.adjustPrice("non-existent", BigDecimal.valueOf(100), "test"));
        }
    }

    @Nested
    @DisplayName("Status Management Tests")
    class StatusManagementTests {

        @Test
        @DisplayName("Should update status to ACTIVE successfully")
        void shouldUpdateStatusToActive() {
            // Given
            priceItem.setStatus(PriceItem.PriceItemStatus.INACTIVE);
            when(priceItemRepository.findById("item-123")).thenReturn(Optional.of(priceItem));
            when(priceItemRepository.save(any(PriceItem.class))).thenReturn(priceItem);

            // When
            PriceItemVO result = priceItemService.updateStatus("item-123", "ACTIVE");

            // Then
            assertNotNull(result);
            assertEquals("ACTIVE", result.getStatus());
            assertEquals(PriceItem.PriceItemStatus.ACTIVE, priceItem.getStatus());
        }

        @Test
        @DisplayName("Should update status to INACTIVE successfully")
        void shouldUpdateStatusToInactive() {
            // Given
            when(priceItemRepository.findById("item-123")).thenReturn(Optional.of(priceItem));
            when(priceItemRepository.save(any(PriceItem.class))).thenReturn(priceItem);

            // When
            PriceItemVO result = priceItemService.updateStatus("item-123", "INACTIVE");

            // Then
            assertNotNull(result);
            assertEquals("INACTIVE", result.getStatus());
            assertEquals(PriceItem.PriceItemStatus.INACTIVE, priceItem.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when updating status of non-existent item")
        void shouldThrowExceptionWhenUpdatingStatusNonExistent() {
            // Given
            when(priceItemRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> priceItemService.updateStatus("non-existent", "ACTIVE"));
        }
    }

    @Nested
    @DisplayName("Batch Import Tests")
    class BatchImportTests {

        @Test
        @DisplayName("Should batch import items successfully")
        void shouldBatchImportSuccessfully() {
            // Given
            PriceItemCreateDTO item1 = new PriceItemCreateDTO();
            item1.setItemCode("ITEM001");
            item1.setItemName("Item 1");
            item1.setItemCategory("DRUG");
            item1.setRetailPrice(BigDecimal.valueOf(50));

            PriceItemCreateDTO item2 = new PriceItemCreateDTO();
            item2.setItemCode("ITEM002");
            item2.setItemName("Item 2");
            item2.setItemCategory("EXAM");
            item2.setRetailPrice(BigDecimal.valueOf(100));

            List<PriceItemCreateDTO> items = Arrays.asList(item1, item2);

            when(priceItemRepository.existsByItemCode("ITEM001")).thenReturn(false);
            when(priceItemRepository.existsByItemCode("ITEM002")).thenReturn(false);
            when(priceItemRepository.save(any(PriceItem.class))).thenReturn(priceItem);

            // When
            List<PriceItemVO> result = priceItemService.batchImport(items);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should skip duplicate items during batch import")
        void shouldSkipDuplicatesDuringBatchImport() {
            // Given
            PriceItemCreateDTO item1 = new PriceItemCreateDTO();
            item1.setItemCode("ITEM001");
            item1.setItemName("Item 1");
            item1.setItemCategory("DRUG");
            item1.setRetailPrice(BigDecimal.valueOf(50));

            PriceItemCreateDTO item2 = new PriceItemCreateDTO();
            item2.setItemCode("ITEM001"); // Duplicate code
            item2.setItemName("Item 2");
            item2.setItemCategory("EXAM");
            item2.setRetailPrice(BigDecimal.valueOf(100));

            List<PriceItemCreateDTO> items = Arrays.asList(item1, item2);

            when(priceItemRepository.existsByItemCode("ITEM001")).thenReturn(false);
            when(priceItemRepository.save(any(PriceItem.class))).thenReturn(priceItem);

            // When
            List<PriceItemVO> result = priceItemService.batchImport(items);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size()); // Only first item succeeds
        }

        @Test
        @DisplayName("Should return empty list when all imports fail")
        void shouldReturnEmptyListWhenAllFail() {
            // Given
            PriceItemCreateDTO item1 = new PriceItemCreateDTO();
            item1.setItemCode("ITEM001");
            item1.setItemName("Item 1");
            item1.setItemCategory("DRUG");
            item1.setRetailPrice(BigDecimal.valueOf(50));

            when(priceItemRepository.existsByItemCode("ITEM001")).thenReturn(true);

            // When
            List<PriceItemVO> result = priceItemService.batchImport(Arrays.asList(item1));

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle empty batch import list")
        void shouldHandleEmptyBatchImport() {
            // When
            List<PriceItemVO> result = priceItemService.batchImport(new ArrayList<>());

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(priceItemRepository, never()).save(any(PriceItem.class));
        }
    }

    @Nested
    @DisplayName("VO Conversion Tests")
    class VOConversionTests {

        @Test
        @DisplayName("Should correctly convert entity to VO with all fields")
        void shouldConvertEntityToVOCorrectly() {
            // Given
            when(priceItemRepository.findById("item-123")).thenReturn(Optional.of(priceItem));

            // When
            PriceItemVO vo = priceItemService.getById("item-123");

            // Then
            assertNotNull(vo);
            assertEquals("item-123", vo.getId());
            assertEquals("ITEM001", vo.getItemCode());
            assertEquals("Test Item", vo.getItemName());
            assertEquals("DRUG", vo.getItemCategory());
            assertEquals("药品", vo.getItemCategoryDesc());
            assertEquals("盒", vo.getItemUnit());
            assertEquals("10mg*10片", vo.getItemSpec());
            assertEquals(BigDecimal.valueOf(50.00), vo.getStandardPrice());
            assertEquals(BigDecimal.valueOf(60.00), vo.getRetailPrice());
            assertEquals(BigDecimal.valueOf(55.00), vo.getWholesalePrice());
            assertEquals("A", vo.getInsuranceType());
            assertEquals("甲类", vo.getInsuranceTypeDesc());
            assertEquals("INS001", vo.getInsuranceCode());
            assertEquals(BigDecimal.valueOf(58.00), vo.getInsurancePrice());
            assertEquals(BigDecimal.valueOf(80), vo.getReimbursementRatio());
            assertEquals("ACTIVE", vo.getStatus());
            assertEquals("启用", vo.getStatusDesc());
        }

        @Test
        @DisplayName("Should handle null enum values in VO conversion")
        void shouldHandleNullEnumValues() {
            // Given
            priceItem.setInsuranceType(null);
            priceItem.setItemCategory(null);
            priceItem.setStatus(null);
            when(priceItemRepository.findById("item-123")).thenReturn(Optional.of(priceItem));

            // When
            PriceItemVO vo = priceItemService.getById("item-123");

            // Then
            assertNotNull(vo);
            assertNull(vo.getInsuranceType());
            assertNull(vo.getInsuranceTypeDesc());
            assertNull(vo.getItemCategory());
            assertNull(vo.getItemCategoryDesc());
            assertNull(vo.getStatus());
            assertNull(vo.getStatusDesc());
        }
    }
}
package com.yhj.his.module.pharmacy.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.InventoryInDTO;
import com.yhj.his.module.pharmacy.dto.InventoryQueryDTO;
import com.yhj.his.module.pharmacy.entity.Drug;
import com.yhj.his.module.pharmacy.entity.DrugInventory;
import com.yhj.his.module.pharmacy.entity.InventoryTransaction;
import com.yhj.his.module.pharmacy.enums.DrugCategory;
import com.yhj.his.module.pharmacy.enums.DrugStatus;
import com.yhj.his.module.pharmacy.enums.InventoryStatus;
import com.yhj.his.module.pharmacy.repository.DrugInventoryRepository;
import com.yhj.his.module.pharmacy.repository.DrugRepository;
import com.yhj.his.module.pharmacy.repository.InventoryTransactionRepository;
import com.yhj.his.module.pharmacy.service.impl.DrugInventoryServiceImpl;
import com.yhj.his.module.pharmacy.vo.ExpiryAlertVO;
import com.yhj.his.module.pharmacy.vo.InventoryVO;
import com.yhj.his.module.pharmacy.vo.StockAlertVO;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DrugInventoryService Unit Tests
 * Covers Inventory management and stock alert operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DrugInventoryService Unit Tests")
class DrugInventoryServiceTest {

    @Mock
    private DrugInventoryRepository inventoryRepository;

    @Mock
    private DrugRepository drugRepository;

    @Mock
    private InventoryTransactionRepository transactionRepository;

    @InjectMocks
    private DrugInventoryServiceImpl inventoryService;

    private Drug testDrug;
    private DrugInventory testInventory;
    private InventoryInDTO inDTO;
    private InventoryQueryDTO queryDTO;

    @BeforeEach
    void setUp() {
        testDrug = createTestDrug();
        testInventory = createTestInventory();
        inDTO = createTestInboundDTO();
        queryDTO = createTestQueryDTO();
    }

    private Drug createTestDrug() {
        Drug drug = new Drug();
        drug.setId("drug-001");
        drug.setDrugCode("DRUG001");
        drug.setDrugName("Aspirin");
        drug.setDrugSpec("100mg");
        drug.setDrugUnit("tablet");
        drug.setPurchasePrice(new BigDecimal("5.00"));
        drug.setRetailPrice(new BigDecimal("10.00"));
        drug.setMinStock(new BigDecimal("50"));
        drug.setMaxStock(new BigDecimal("500"));
        drug.setDrugCategory(DrugCategory.WESTERN);
        drug.setStatus(DrugStatus.NORMAL);
        drug.setDeleted(false);
        return drug;
    }

    private DrugInventory createTestInventory() {
        DrugInventory inventory = new DrugInventory();
        inventory.setId("inv-001");
        inventory.setDrugId("drug-001");
        inventory.setDrugCode("DRUG001");
        inventory.setDrugName("Aspirin");
        inventory.setDrugSpec("100mg");
        inventory.setDrugUnit("tablet");
        inventory.setPharmacyId("pharmacy-001");
        inventory.setPharmacyName("Main Pharmacy");
        inventory.setBatchNo("BATCH001");
        inventory.setProductionDate(LocalDate.now().minusMonths(6));
        inventory.setExpiryDate(LocalDate.now().plusMonths(18));
        inventory.setQuantity(new BigDecimal("100"));
        inventory.setLockedQuantity(new BigDecimal("10"));
        inventory.setAvailableQuantity(new BigDecimal("90"));
        inventory.setLocation("A-01");
        inventory.setPurchasePrice(new BigDecimal("5.00"));
        inventory.setRetailPrice(new BigDecimal("10.00"));
        inventory.setSupplierId("supplier-001");
        inventory.setSupplierName("Supplier A");
        inventory.setStatus(InventoryStatus.NORMAL);
        inventory.setDeleted(false);
        inventory.setCreateTime(LocalDateTime.now());
        return inventory;
    }

    private InventoryInDTO createTestInboundDTO() {
        InventoryInDTO dto = new InventoryInDTO();
        dto.setDrugId("drug-001");
        dto.setPharmacyId("pharmacy-001");
        dto.setBatchNo("BATCH002");
        dto.setProductionDate(LocalDate.now());
        dto.setExpiryDate(LocalDate.now().plusYears(2));
        dto.setQuantity(new BigDecimal("50"));
        dto.setPurchasePrice(new BigDecimal("6.00"));
        dto.setRetailPrice(new BigDecimal("12.00"));
        dto.setLocation("B-01");
        dto.setSupplierId("supplier-002");
        dto.setSupplierName("Supplier B");
        dto.setRemark("Test inbound");
        return dto;
    }

    private InventoryQueryDTO createTestQueryDTO() {
        InventoryQueryDTO dto = new InventoryQueryDTO();
        dto.setPharmacyId("pharmacy-001");
        dto.setKeyword("Aspirin");
        dto.setPageNum(1);
        dto.setPageSize(10);
        return dto;
    }

    @Nested
    @DisplayName("Inbound Operations Tests")
    class InboundTests {

        @Test
        @DisplayName("Should inbound inventory successfully when drug exists")
        void shouldInboundSuccessfully() {
            // Arrange
            when(drugRepository.findById(anyString())).thenReturn(Optional.of(testDrug));
            when(inventoryRepository.save(any(DrugInventory.class))).thenReturn(testInventory);
            when(transactionRepository.save(any(InventoryTransaction.class))).thenReturn(new InventoryTransaction());

            // Act
            Result<InventoryVO> result = inventoryService.inbound(inDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("inv-001", result.getData().getInventoryId());
            assertEquals("DRUG001", result.getData().getDrugCode());

            verify(drugRepository).findById("drug-001");
            verify(inventoryRepository).save(any(DrugInventory.class));
            verify(transactionRepository).save(any(InventoryTransaction.class));
        }

        @Test
        @DisplayName("Should return error when drug not found for inbound")
        void shouldReturnErrorWhenDrugNotFound() {
            // Arrange
            when(drugRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<InventoryVO> result = inventoryService.inbound(inDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("药品不存在"));

            verify(drugRepository).findById("drug-001");
            verify(inventoryRepository, never()).save(any(DrugInventory.class));
        }

        @Test
        @DisplayName("Should use drug default prices when not provided in DTO")
        void shouldUseDrugDefaultPrices() {
            // Arrange
            inDTO.setPurchasePrice(null);
            inDTO.setRetailPrice(null);
            when(drugRepository.findById(anyString())).thenReturn(Optional.of(testDrug));
            when(inventoryRepository.save(any(DrugInventory.class))).thenReturn(testInventory);
            when(transactionRepository.save(any(InventoryTransaction.class))).thenReturn(new InventoryTransaction());

            // Act
            Result<InventoryVO> result = inventoryService.inbound(inDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            verify(inventoryRepository).save(any(DrugInventory.class));
        }
    }

    @Nested
    @DisplayName("Outbound Operations Tests")
    class OutboundTests {

        @Test
        @DisplayName("Should outbound inventory successfully when sufficient stock available")
        void shouldOutboundSuccessfully() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));
            when(inventoryRepository.save(any(DrugInventory.class))).thenReturn(testInventory);
            when(transactionRepository.save(any(InventoryTransaction.class))).thenReturn(new InventoryTransaction());

            // Act
            Result<InventoryVO> result = inventoryService.outbound("inv-001",
                    new BigDecimal("20"), "Test outbound", "operator-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(inventoryRepository).findById("inv-001");
            verify(inventoryRepository).save(any(DrugInventory.class));
            verify(transactionRepository).save(any(InventoryTransaction.class));
        }

        @Test
        @DisplayName("Should return error when inventory not found for outbound")
        void shouldReturnErrorWhenInventoryNotFound() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<InventoryVO> result = inventoryService.outbound("non-existent",
                    new BigDecimal("20"), "Test outbound", "operator-001");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("库存不存在"));

            verify(inventoryRepository).findById("non-existent");
            verify(inventoryRepository, never()).save(any(DrugInventory.class));
        }

        @Test
        @DisplayName("Should return error when insufficient stock for outbound")
        void shouldReturnErrorWhenInsufficientStock() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));

            // Act - trying to outbound more than available
            Result<InventoryVO> result = inventoryService.outbound("inv-001",
                    new BigDecimal("200"), "Test outbound", "operator-001");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("可用库存不足"));

            verify(inventoryRepository).findById("inv-001");
            verify(inventoryRepository, never()).save(any(DrugInventory.class));
        }
    }

    @Nested
    @DisplayName("Get Inventory Tests")
    class GetInventoryTests {

        @Test
        @DisplayName("Should get inventory by ID successfully")
        void shouldGetInventoryByIdSuccessfully() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));

            // Act
            Result<InventoryVO> result = inventoryService.getInventoryById("inv-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("inv-001", result.getData().getInventoryId());

            verify(inventoryRepository).findById("inv-001");
        }

        @Test
        @DisplayName("Should return error when inventory not found by ID")
        void shouldReturnErrorWhenInventoryNotFoundById() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<InventoryVO> result = inventoryService.getInventoryById("non-existent");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("库存不存在"));

            verify(inventoryRepository).findById("non-existent");
        }

        @Test
        @DisplayName("Should get drug inventory successfully")
        void shouldGetDrugInventorySuccessfully() {
            // Arrange
            when(inventoryRepository.findByDrugId(anyString()))
                    .thenReturn(Arrays.asList(testInventory));

            // Act
            Result<List<InventoryVO>> result = inventoryService.getDrugInventory("drug-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            verify(inventoryRepository).findByDrugId("drug-001");
        }
    }

    @Nested
    @DisplayName("Query Inventory Tests")
    class QueryInventoryTests {

        @Test
        @DisplayName("Should query inventory with pagination successfully")
        void shouldQueryInventorySuccessfully() {
            // Arrange
            List<DrugInventory> inventories = Arrays.asList(testInventory);
            Page<DrugInventory> page = new PageImpl<>(inventories);
            when(inventoryRepository.queryInventory(any(), any(), any(),
                    any(), any(InventoryStatus.class), any(Pageable.class))).thenReturn(page);

            // Act
            Result<PageResult<InventoryVO>> result = inventoryService.queryInventory(queryDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getTotal());
            assertEquals(1, result.getData().getList().size());

            verify(inventoryRepository).queryInventory(any(), any(), any(),
                    any(), any(InventoryStatus.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when no inventory found")
        void shouldReturnEmptyPageWhenNoInventoryFound() {
            // Arrange
            Page<DrugInventory> emptyPage = new PageImpl<>(Collections.emptyList());
            when(inventoryRepository.queryInventory(any(), any(), any(),
                    any(), any(InventoryStatus.class), any(Pageable.class))).thenReturn(emptyPage);

            // Act
            Result<PageResult<InventoryVO>> result = inventoryService.queryInventory(queryDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(0, result.getData().getTotal());
            assertTrue(result.getData().getList().isEmpty());
        }

        @Test
        @DisplayName("Should get pharmacy inventory successfully")
        void shouldGetPharmacyInventorySuccessfully() {
            // Arrange
            Page<DrugInventory> page = new PageImpl<>(Arrays.asList(testInventory));
            when(inventoryRepository.findByPharmacyIdAndKeyword(anyString(), any(),
                    any(Pageable.class))).thenReturn(page);

            // Act
            Result<PageResult<InventoryVO>> result = inventoryService.getPharmacyInventory(
                    "pharmacy-001", "Aspirin", 1, 10);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getList().size());

            verify(inventoryRepository).findByPharmacyIdAndKeyword(anyString(), any(),
                    any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Lock/Unlock Inventory Tests")
    class LockUnlockTests {

        @Test
        @DisplayName("Should lock inventory successfully when sufficient available stock")
        void shouldLockInventorySuccessfully() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));
            when(inventoryRepository.save(any(DrugInventory.class))).thenReturn(testInventory);

            // Act
            Result<Void> result = inventoryService.lockInventory("inv-001", new BigDecimal("20"));

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(inventoryRepository).findById("inv-001");
            verify(inventoryRepository).save(any(DrugInventory.class));
        }

        @Test
        @DisplayName("Should return error when insufficient available stock for locking")
        void shouldReturnErrorWhenInsufficientStockForLocking() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));

            // Act - trying to lock more than available
            Result<Void> result = inventoryService.lockInventory("inv-001", new BigDecimal("150"));

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("可用库存不足"));

            verify(inventoryRepository).findById("inv-001");
            verify(inventoryRepository, never()).save(any(DrugInventory.class));
        }

        @Test
        @DisplayName("Should unlock inventory successfully")
        void shouldUnlockInventorySuccessfully() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));
            when(inventoryRepository.save(any(DrugInventory.class))).thenReturn(testInventory);

            // Act
            Result<Void> result = inventoryService.unlockInventory("inv-001", new BigDecimal("5"));

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(inventoryRepository).findById("inv-001");
            verify(inventoryRepository).save(any(DrugInventory.class));
        }

        @Test
        @DisplayName("Should return error when unlock quantity exceeds locked quantity")
        void shouldReturnErrorWhenUnlockExceedsLocked() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));

            // Act - trying to unlock more than locked
            Result<Void> result = inventoryService.unlockInventory("inv-001", new BigDecimal("50"));

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("锁定数量不足"));

            verify(inventoryRepository).findById("inv-001");
            verify(inventoryRepository, never()).save(any(DrugInventory.class));
        }
    }

    @Nested
    @DisplayName("Stock Alerts Tests")
    class StockAlertsTests {

        @Test
        @DisplayName("Should get expiry alerts successfully")
        void shouldGetExpiryAlertsSuccessfully() {
            // Arrange
            when(inventoryRepository.findByExpiryDateBefore(any(LocalDate.class)))
                    .thenReturn(Arrays.asList(testInventory));

            // Act
            Result<List<ExpiryAlertVO>> result = inventoryService.getExpiryAlerts(30);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            verify(inventoryRepository).findByExpiryDateBefore(any(LocalDate.class));
        }

        @Test
        @DisplayName("Should return empty list when no expiry alerts")
        void shouldReturnEmptyListWhenNoExpiryAlerts() {
            // Arrange
            when(inventoryRepository.findByExpiryDateBefore(any(LocalDate.class)))
                    .thenReturn(Collections.emptyList());

            // Act
            Result<List<ExpiryAlertVO>> result = inventoryService.getExpiryAlerts(30);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
        }

        @Test
        @DisplayName("Should get low stock alerts successfully")
        void shouldGetLowStockAlertsSuccessfully() {
            // Arrange
            testInventory.setQuantity(new BigDecimal("30")); // Below min stock of 50
            testInventory.setDrug(testDrug);
            when(inventoryRepository.findLowStockInventory())
                    .thenReturn(Arrays.asList(testInventory));

            // Act
            Result<List<StockAlertVO>> result = inventoryService.getLowStockAlerts();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            verify(inventoryRepository).findLowStockInventory();
        }

        @Test
        @DisplayName("Should get over stock alerts successfully")
        void shouldGetOverStockAlertsSuccessfully() {
            // Arrange
            testInventory.setQuantity(new BigDecimal("600")); // Above max stock of 500
            testInventory.setDrug(testDrug);
            when(inventoryRepository.findOverStockInventory())
                    .thenReturn(Arrays.asList(testInventory));

            // Act
            Result<List<StockAlertVO>> result = inventoryService.getOverStockAlerts();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            verify(inventoryRepository).findOverStockInventory();
        }

        @Test
        @DisplayName("Should filter alerts when drug is null")
        void shouldFilterAlertsWhenDrugIsNull() {
            // Arrange
            testInventory.setDrug(null);
            when(inventoryRepository.findLowStockInventory())
                    .thenReturn(Arrays.asList(testInventory));

            // Act
            Result<List<StockAlertVO>> result = inventoryService.getLowStockAlerts();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty()); // Filtered out due to null drug

            verify(inventoryRepository).findLowStockInventory();
        }
    }

    @Nested
    @DisplayName("Update Inventory Status Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update inventory status successfully")
        void shouldUpdateInventoryStatusSuccessfully() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));
            when(inventoryRepository.save(any(DrugInventory.class))).thenReturn(testInventory);

            // Act
            Result<Void> result = inventoryService.updateInventoryStatus("inv-001", InventoryStatus.EXPIRED);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(inventoryRepository).findById("inv-001");
            verify(inventoryRepository).save(any(DrugInventory.class));
        }

        @Test
        @DisplayName("Should return error when inventory not found for status update")
        void shouldReturnErrorWhenInventoryNotFoundForStatusUpdate() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<Void> result = inventoryService.updateInventoryStatus("non-existent", InventoryStatus.EXPIRED);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("库存不存在"));

            verify(inventoryRepository).findById("non-existent");
            verify(inventoryRepository, never()).save(any(DrugInventory.class));
        }
    }

    @Nested
    @DisplayName("Adjust Inventory Tests")
    class AdjustInventoryTests {

        @Test
        @DisplayName("Should adjust inventory successfully")
        void shouldAdjustInventorySuccessfully() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));
            when(inventoryRepository.save(any(DrugInventory.class))).thenReturn(testInventory);
            when(transactionRepository.save(any(InventoryTransaction.class))).thenReturn(new InventoryTransaction());

            // Act
            Result<InventoryVO> result = inventoryService.adjustInventory("inv-001",
                    new BigDecimal("120"), "Inventory adjustment", "operator-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(inventoryRepository).findById("inv-001");
            verify(inventoryRepository).save(any(DrugInventory.class));
            verify(transactionRepository).save(any(InventoryTransaction.class));
        }

        @Test
        @DisplayName("Should return error when inventory not found for adjustment")
        void shouldReturnErrorWhenInventoryNotFoundForAdjustment() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<InventoryVO> result = inventoryService.adjustInventory("non-existent",
                    new BigDecimal("120"), "Inventory adjustment", "operator-001");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("库存不存在"));

            verify(inventoryRepository).findById("non-existent");
            verify(inventoryRepository, never()).save(any(DrugInventory.class));
        }

        @Test
        @DisplayName("Should handle negative adjustment (reduction)")
        void shouldHandleNegativeAdjustment() {
            // Arrange
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));
            when(inventoryRepository.save(any(DrugInventory.class))).thenReturn(testInventory);
            when(transactionRepository.save(any(InventoryTransaction.class))).thenReturn(new InventoryTransaction());

            // Act
            Result<InventoryVO> result = inventoryService.adjustInventory("inv-001",
                    new BigDecimal("50"), "Stock reduction", "operator-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(inventoryRepository).save(any(DrugInventory.class));
            verify(transactionRepository).save(any(InventoryTransaction.class));
        }
    }

    @Nested
    @DisplayName("Days Remaining Calculation Tests")
    class DaysRemainingTests {

        @Test
        @DisplayName("Should calculate days remaining correctly")
        void shouldCalculateDaysRemainingCorrectly() {
            // Arrange
            testInventory.setExpiryDate(LocalDate.now().plusDays(30));
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));

            // Act
            Result<InventoryVO> result = inventoryService.getInventoryById("inv-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData().getDaysRemaining());
            assertEquals(30, result.getData().getDaysRemaining());
        }

        @Test
        @DisplayName("Should handle null expiry date")
        void shouldHandleNullExpiryDate() {
            // Arrange
            testInventory.setExpiryDate(null);
            when(inventoryRepository.findById(anyString())).thenReturn(Optional.of(testInventory));

            // Act
            Result<InventoryVO> result = inventoryService.getInventoryById("inv-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNull(result.getData().getDaysRemaining());
        }
    }
}
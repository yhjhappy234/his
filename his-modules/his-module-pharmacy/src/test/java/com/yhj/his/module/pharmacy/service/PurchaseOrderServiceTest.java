package com.yhj.his.module.pharmacy.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.PurchaseApplyDTO;
import com.yhj.his.module.pharmacy.dto.PurchaseAuditDTO;
import com.yhj.his.module.pharmacy.dto.PurchaseQueryDTO;
import com.yhj.his.module.pharmacy.dto.ReceiveConfirmDTO;
import com.yhj.his.module.pharmacy.entity.PurchaseOrder;
import com.yhj.his.module.pharmacy.entity.PurchaseOrderItem;
import com.yhj.his.module.pharmacy.enums.PurchaseOrderStatus;
import com.yhj.his.module.pharmacy.repository.PurchaseOrderItemRepository;
import com.yhj.his.module.pharmacy.repository.PurchaseOrderRepository;
import com.yhj.his.module.pharmacy.service.impl.PurchaseOrderServiceImpl;
import com.yhj.his.module.pharmacy.vo.PurchaseOrderVO;
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
 * PurchaseOrderService Unit Tests
 * Covers Purchase order processing operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PurchaseOrderService Unit Tests")
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Mock
    private DrugInventoryService drugInventoryService;

    @InjectMocks
    private PurchaseOrderServiceImpl purchaseOrderService;

    private PurchaseOrder testOrder;
    private PurchaseOrderItem testItem;
    private PurchaseApplyDTO applyDTO;
    private PurchaseAuditDTO auditDTO;
    private PurchaseQueryDTO queryDTO;
    private ReceiveConfirmDTO receiveDTO;

    @BeforeEach
    void setUp() {
        testOrder = createTestPurchaseOrder();
        testItem = createTestPurchaseOrderItem();
        applyDTO = createTestApplyDTO();
        auditDTO = createTestAuditDTO();
        queryDTO = createTestQueryDTO();
        receiveDTO = createTestReceiveDTO();
    }

    private PurchaseOrder createTestPurchaseOrder() {
        PurchaseOrder order = new PurchaseOrder();
        order.setId("order-001");
        order.setOrderNo("PO20240101001");
        order.setSupplierId("supplier-001");
        order.setSupplierName("Supplier A");
        order.setOrderDate(LocalDate.now());
        order.setExpectedDate(LocalDate.now().plusDays(7));
        order.setTotalQuantity(new BigDecimal("100"));
        order.setTotalAmount(new BigDecimal("500.00"));
        order.setStatus(PurchaseOrderStatus.DRAFT);
        order.setApplicantId("applicant-001");
        order.setApplicantName("Applicant Name");
        order.setDeleted(false);
        order.setCreateTime(LocalDateTime.now());
        return order;
    }

    private PurchaseOrderItem createTestPurchaseOrderItem() {
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setId("item-001");
        item.setOrderId("order-001");
        item.setDrugId("drug-001");
        item.setDrugCode("DRUG001");
        item.setDrugName("Aspirin");
        item.setDrugSpec("100mg");
        item.setDrugUnit("tablet");
        item.setQuantity(new BigDecimal("100"));
        item.setReceivedQuantity(new BigDecimal("0"));
        item.setPurchasePrice(new BigDecimal("5.00"));
        item.setAmount(new BigDecimal("500.00"));
        item.setRemark("Test item");
        return item;
    }

    private PurchaseApplyDTO createTestApplyDTO() {
        PurchaseApplyDTO dto = new PurchaseApplyDTO();
        dto.setSupplierId("supplier-001");
        dto.setSupplierName("Supplier A");
        dto.setExpectedDate(LocalDate.now().plusDays(7));
        dto.setApplicantId("applicant-001");
        dto.setApplicantName("Applicant Name");

        PurchaseApplyDTO.PurchaseItemDTO itemDTO = new PurchaseApplyDTO.PurchaseItemDTO();
        itemDTO.setDrugId("drug-001");
        itemDTO.setDrugCode("DRUG001");
        itemDTO.setDrugName("Aspirin");
        itemDTO.setDrugSpec("100mg");
        itemDTO.setDrugUnit("tablet");
        itemDTO.setQuantity(new BigDecimal("100"));
        itemDTO.setPurchasePrice(new BigDecimal("5.00"));
        dto.setItems(Arrays.asList(itemDTO));
        return dto;
    }

    private PurchaseAuditDTO createTestAuditDTO() {
        PurchaseAuditDTO dto = new PurchaseAuditDTO();
        dto.setOrderId("order-001");
        dto.setAuditorId("auditor-001");
        dto.setAuditorName("Auditor Name");
        dto.setAuditResult("通过");
        dto.setAuditRemark("Approved");
        return dto;
    }

    private PurchaseQueryDTO createTestQueryDTO() {
        PurchaseQueryDTO dto = new PurchaseQueryDTO();
        dto.setSupplierId("supplier-001");
        dto.setPageNum(1);
        dto.setPageSize(10);
        return dto;
    }

    private ReceiveConfirmDTO createTestReceiveDTO() {
        ReceiveConfirmDTO dto = new ReceiveConfirmDTO();

        ReceiveConfirmDTO.ReceiveItemDTO itemDTO = new ReceiveConfirmDTO.ReceiveItemDTO();
        itemDTO.setItemId("item-001");
        itemDTO.setQuantity(new BigDecimal("50"));
        dto.setItems(Arrays.asList(itemDTO));
        return dto;
    }

    @Nested
    @DisplayName("Create Purchase Order Tests")
    class CreatePurchaseOrderTests {

        @Test
        @DisplayName("Should create purchase order successfully")
        void shouldCreatePurchaseOrderSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);
            when(purchaseOrderItemRepository.saveAll(anyList())).thenReturn(Arrays.asList(testItem));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.createPurchaseOrder(applyDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("order-001", result.getData().getOrderId());
            assertEquals("PO20240101001", result.getData().getOrderNo());
            assertEquals(PurchaseOrderStatus.DRAFT, result.getData().getStatus());

            verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
            verify(purchaseOrderItemRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Should calculate totals correctly when creating order")
        void shouldCalculateTotalsCorrectly() {
            // Arrange
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);
            when(purchaseOrderItemRepository.saveAll(anyList())).thenReturn(Arrays.asList(testItem));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.createPurchaseOrder(applyDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData().getTotalQuantity());
            assertNotNull(result.getData().getTotalAmount());
        }

        @Test
        @DisplayName("Should handle empty items list")
        void shouldHandleEmptyItemsList() {
            // Arrange
            applyDTO.setItems(null);
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.createPurchaseOrder(applyDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        }
    }

    @Nested
    @DisplayName("Update Purchase Order Tests")
    class UpdatePurchaseOrderTests {

        @Test
        @DisplayName("Should update purchase order successfully when in DRAFT status")
        void shouldUpdatePurchaseOrderSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            doNothing().when(purchaseOrderItemRepository).deleteByOrderId(anyString());
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);
            when(purchaseOrderItemRepository.saveAll(anyList())).thenReturn(Arrays.asList(testItem));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.updatePurchaseOrder("order-001", applyDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(purchaseOrderRepository).findById("order-001");
            verify(purchaseOrderItemRepository).deleteByOrderId("order-001");
            verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should return error when purchase order not found for update")
        void shouldReturnErrorWhenPurchaseOrderNotFoundForUpdate() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.updatePurchaseOrder("non-existent", applyDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("采购订单不存在"));

            verify(purchaseOrderRepository).findById("non-existent");
            verify(purchaseOrderRepository, never()).save(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should return error when trying to update non-DRAFT order")
        void shouldReturnErrorWhenTryingToUpdateNonDraftOrder() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.PENDING);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.updatePurchaseOrder("order-001", applyDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("只有草稿状态的订单可以修改"));

            verify(purchaseOrderRepository).findById("order-001");
            verify(purchaseOrderRepository, never()).save(any(PurchaseOrder.class));
        }
    }

    @Nested
    @DisplayName("Delete Purchase Order Tests")
    class DeletePurchaseOrderTests {

        @Test
        @DisplayName("Should delete purchase order successfully when in DRAFT status")
        void shouldDeletePurchaseOrderSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            doNothing().when(purchaseOrderItemRepository).deleteByOrderId(anyString());
            doNothing().when(purchaseOrderRepository).delete(any(PurchaseOrder.class));

            // Act
            Result<Void> result = purchaseOrderService.deletePurchaseOrder("order-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(purchaseOrderRepository).findById("order-001");
            verify(purchaseOrderItemRepository).deleteByOrderId("order-001");
            verify(purchaseOrderRepository).delete(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should delete purchase order when in CANCELLED status")
        void shouldDeletePurchaseOrderWhenCancelled() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.CANCELLED);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            doNothing().when(purchaseOrderItemRepository).deleteByOrderId(anyString());
            doNothing().when(purchaseOrderRepository).delete(any(PurchaseOrder.class));

            // Act
            Result<Void> result = purchaseOrderService.deletePurchaseOrder("order-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            verify(purchaseOrderRepository).delete(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should return error when purchase order not found for deletion")
        void shouldReturnErrorWhenPurchaseOrderNotFoundForDeletion() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<Void> result = purchaseOrderService.deletePurchaseOrder("non-existent");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("采购订单不存在"));
        }

        @Test
        @DisplayName("Should return error when trying to delete non-DRAFT/CANCELLED order")
        void shouldReturnErrorWhenTryingToDeleteNonDraftCancelledOrder() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.PENDING);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));

            // Act
            Result<Void> result = purchaseOrderService.deletePurchaseOrder("order-001");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("只有草稿或已取消的订单可以删除"));
        }
    }

    @Nested
    @DisplayName("Get Purchase Order Tests")
    class GetPurchaseOrderTests {

        @Test
        @DisplayName("Should get purchase order by ID successfully")
        void shouldGetPurchaseOrderByIdSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            when(purchaseOrderItemRepository.findByOrderId(anyString()))
                    .thenReturn(Arrays.asList(testItem));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.getPurchaseOrderById("order-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("order-001", result.getData().getOrderId());

            verify(purchaseOrderRepository).findById("order-001");
            verify(purchaseOrderItemRepository).findByOrderId("order-001");
        }

        @Test
        @DisplayName("Should return error when purchase order not found by ID")
        void shouldReturnErrorWhenPurchaseOrderNotFoundById() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.getPurchaseOrderById("non-existent");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("采购订单不存在"));
        }

        @Test
        @DisplayName("Should get purchase order by order number successfully")
        void shouldGetPurchaseOrderByNoSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findByOrderNo(anyString())).thenReturn(Optional.of(testOrder));
            when(purchaseOrderItemRepository.findByOrderId(anyString()))
                    .thenReturn(Arrays.asList(testItem));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.getPurchaseOrderByNo("PO20240101001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("PO20240101001", result.getData().getOrderNo());

            verify(purchaseOrderRepository).findByOrderNo("PO20240101001");
        }

        @Test
        @DisplayName("Should return error when purchase order not found by order number")
        void shouldReturnErrorWhenPurchaseOrderNotFoundByNo() {
            // Arrange
            when(purchaseOrderRepository.findByOrderNo(anyString())).thenReturn(Optional.empty());

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.getPurchaseOrderByNo("INVALID");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("采购订单不存在"));
        }
    }

    @Nested
    @DisplayName("Query Purchase Orders Tests")
    class QueryPurchaseOrdersTests {

        @Test
        @DisplayName("Should query purchase orders with pagination successfully")
        void shouldQueryPurchaseOrdersSuccessfully() {
            // Arrange
            List<PurchaseOrder> orders = Arrays.asList(testOrder);
            Page<PurchaseOrder> page = new PageImpl<>(orders);
            when(purchaseOrderRepository.queryOrders(any(), any(),
                    any(PurchaseOrderStatus.class), any(LocalDate.class), any(LocalDate.class),
                    any(Pageable.class))).thenReturn(page);
            when(purchaseOrderItemRepository.findByOrderId(anyString()))
                    .thenReturn(Arrays.asList(testItem));

            // Act
            Result<PageResult<PurchaseOrderVO>> result = purchaseOrderService.queryPurchaseOrders(queryDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getTotal());
            assertEquals(1, result.getData().getList().size());

            verify(purchaseOrderRepository).queryOrders(any(), any(),
                    any(PurchaseOrderStatus.class), any(LocalDate.class), any(LocalDate.class),
                    any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when no orders found")
        void shouldReturnEmptyPageWhenNoOrdersFound() {
            // Arrange
            Page<PurchaseOrder> emptyPage = new PageImpl<>(Collections.emptyList());
            when(purchaseOrderRepository.queryOrders(any(), any(),
                    any(PurchaseOrderStatus.class), any(LocalDate.class), any(LocalDate.class),
                    any(Pageable.class))).thenReturn(emptyPage);

            // Act
            Result<PageResult<PurchaseOrderVO>> result = purchaseOrderService.queryPurchaseOrders(queryDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(0, result.getData().getTotal());
            assertTrue(result.getData().getList().isEmpty());
        }
    }

    @Nested
    @DisplayName("Submit Purchase Order Tests")
    class SubmitPurchaseOrderTests {

        @Test
        @DisplayName("Should submit purchase order successfully when in DRAFT status")
        void shouldSubmitPurchaseOrderSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);
            when(purchaseOrderItemRepository.findByOrderId(anyString()))
                    .thenReturn(Arrays.asList(testItem));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.submitPurchaseOrder(
                    "order-001", "applicant-001", "Applicant Name");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(purchaseOrderRepository).findById("order-001");
            verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should return error when purchase order not found for submission")
        void shouldReturnErrorWhenPurchaseOrderNotFoundForSubmission() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.submitPurchaseOrder(
                    "non-existent", "applicant-001", "Applicant Name");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("采购订单不存在"));
        }

        @Test
        @DisplayName("Should return error when trying to submit non-DRAFT order")
        void shouldReturnErrorWhenTryingToSubmitNonDraftOrder() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.PENDING);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.submitPurchaseOrder(
                    "order-001", "applicant-001", "Applicant Name");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("只有草稿状态的订单可以提交"));
        }
    }

    @Nested
    @DisplayName("Audit Purchase Order Tests")
    class AuditPurchaseOrderTests {

        @Test
        @DisplayName("Should audit purchase order successfully with approval")
        void shouldAuditPurchaseOrderSuccessfullyWithApproval() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.PENDING);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);
            when(purchaseOrderItemRepository.findByOrderId(anyString()))
                    .thenReturn(Arrays.asList(testItem));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.auditPurchaseOrder("order-001", auditDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(purchaseOrderRepository).findById("order-001");
            verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should audit purchase order successfully with rejection")
        void shouldAuditPurchaseOrderSuccessfullyWithRejection() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.PENDING);
            auditDTO.setAuditResult("不通过");
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);
            when(purchaseOrderItemRepository.findByOrderId(anyString()))
                    .thenReturn(Arrays.asList(testItem));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.auditPurchaseOrder("order-001", auditDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should return error when purchase order not found for audit")
        void shouldReturnErrorWhenPurchaseOrderNotFoundForAudit() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.auditPurchaseOrder("non-existent", auditDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("采购订单不存在"));
        }

        @Test
        @DisplayName("Should return error when trying to audit non-PENDING order")
        void shouldReturnErrorWhenTryingToAuditNonPendingOrder() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.APPROVED);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.auditPurchaseOrder("order-001", auditDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("只有待审核状态的订单可以审核"));
        }
    }

    @Nested
    @DisplayName("Cancel Purchase Order Tests")
    class CancelPurchaseOrderTests {

        @Test
        @DisplayName("Should cancel purchase order successfully")
        void shouldCancelPurchaseOrderSuccessfully() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.PENDING);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

            // Act
            Result<Void> result = purchaseOrderService.cancelPurchaseOrder("order-001", "Supplier issue");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(purchaseOrderRepository).findById("order-001");
            verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should return error when purchase order not found for cancellation")
        void shouldReturnErrorWhenPurchaseOrderNotFoundForCancellation() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<Void> result = purchaseOrderService.cancelPurchaseOrder("non-existent", "Supplier issue");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("采购订单不存在"));
        }

        @Test
        @DisplayName("Should return error when trying to cancel COMPLETED order")
        void shouldReturnErrorWhenTryingToCancelCompletedOrder() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.COMPLETED);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));

            // Act
            Result<Void> result = purchaseOrderService.cancelPurchaseOrder("order-001", "Supplier issue");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("已完成或部分入库的订单不能取消"));
        }

        @Test
        @DisplayName("Should return error when trying to cancel PARTIAL_RECEIVED order")
        void shouldReturnErrorWhenTryingToCancelPartialReceivedOrder() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.PARTIAL_RECEIVED);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));

            // Act
            Result<Void> result = purchaseOrderService.cancelPurchaseOrder("order-001", "Supplier issue");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("已完成或部分入库的订单不能取消"));
        }
    }

    @Nested
    @DisplayName("Confirm Receive Tests")
    class ConfirmReceiveTests {

        @Test
        @DisplayName("Should confirm receive successfully when APPROVED status")
        void shouldConfirmReceiveSuccessfully() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.APPROVED);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            when(purchaseOrderItemRepository.findByOrderId(anyString()))
                    .thenReturn(Arrays.asList(testItem));
            when(purchaseOrderItemRepository.save(any(PurchaseOrderItem.class))).thenReturn(testItem);
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.confirmReceive("order-001", receiveDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(purchaseOrderRepository).findById("order-001");
            verify(purchaseOrderItemRepository).save(any(PurchaseOrderItem.class));
            verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should confirm receive successfully when PARTIAL_RECEIVED status")
        void shouldConfirmReceiveWhenPartialReceived() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.PARTIAL_RECEIVED);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            when(purchaseOrderItemRepository.findByOrderId(anyString()))
                    .thenReturn(Arrays.asList(testItem));
            when(purchaseOrderItemRepository.save(any(PurchaseOrderItem.class))).thenReturn(testItem);
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.confirmReceive("order-001", receiveDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should set status to COMPLETED when all items received")
        void shouldSetStatusCompletedWhenAllItemsReceived() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.APPROVED);
            testItem.setQuantity(new BigDecimal("50"));
            testItem.setReceivedQuantity(new BigDecimal("50"));
            receiveDTO.getItems().get(0).setQuantity(new BigDecimal("0"));

            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            when(purchaseOrderItemRepository.findByOrderId(anyString()))
                    .thenReturn(Arrays.asList(testItem));
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.confirmReceive("order-001", receiveDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should return error when purchase order not found for receive confirmation")
        void shouldReturnErrorWhenPurchaseOrderNotFoundForReceive() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.confirmReceive("non-existent", receiveDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("采购订单不存在"));
        }

        @Test
        @DisplayName("Should return error when trying to receive non-APPROVED/PARTIAL_RECEIVED order")
        void shouldReturnErrorWhenTryingToReceiveInvalidStatusOrder() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.PENDING);
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));

            // Act
            Result<PurchaseOrderVO> result = purchaseOrderService.confirmReceive("order-001", receiveDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("只有已审核或部分入库的订单可以确认收货"));
        }
    }

    @Nested
    @DisplayName("Get Pending Audit Orders Tests")
    class GetPendingAuditOrdersTests {

        @Test
        @DisplayName("Should get pending audit orders successfully")
        void shouldGetPendingAuditOrdersSuccessfully() {
            // Arrange
            testOrder.setStatus(PurchaseOrderStatus.PENDING);
            when(purchaseOrderRepository.findByStatus(any(PurchaseOrderStatus.class)))
                    .thenReturn(Arrays.asList(testOrder));
            when(purchaseOrderItemRepository.findByOrderId(anyString()))
                    .thenReturn(Arrays.asList(testItem));

            // Act
            Result<List<PurchaseOrderVO>> result = purchaseOrderService.getPendingAuditOrders();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            verify(purchaseOrderRepository).findByStatus(PurchaseOrderStatus.PENDING);
        }

        @Test
        @DisplayName("Should return empty list when no pending audit orders")
        void shouldReturnEmptyListWhenNoPendingAuditOrders() {
            // Arrange
            when(purchaseOrderRepository.findByStatus(any(PurchaseOrderStatus.class)))
                    .thenReturn(Collections.emptyList());

            // Act
            Result<List<PurchaseOrderVO>> result = purchaseOrderService.getPendingAuditOrders();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Orders By Supplier Tests")
    class GetOrdersBySupplierTests {

        @Test
        @DisplayName("Should get orders by supplier successfully")
        void shouldGetOrdersBySupplierSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findBySupplierId(anyString()))
                    .thenReturn(Arrays.asList(testOrder));
            when(purchaseOrderItemRepository.findByOrderId(anyString()))
                    .thenReturn(Arrays.asList(testItem));

            // Act
            Result<List<PurchaseOrderVO>> result = purchaseOrderService.getOrdersBySupplier("supplier-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            verify(purchaseOrderRepository).findBySupplierId("supplier-001");
        }

        @Test
        @DisplayName("Should return empty list when no orders for supplier")
        void shouldReturnEmptyListWhenNoOrdersForSupplier() {
            // Arrange
            when(purchaseOrderRepository.findBySupplierId(anyString()))
                    .thenReturn(Collections.emptyList());

            // Act
            Result<List<PurchaseOrderVO>> result = purchaseOrderService.getOrdersBySupplier("supplier-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("Update Order Status Tests")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("Should update order status successfully")
        void shouldUpdateOrderStatusSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

            // Act
            Result<Void> result = purchaseOrderService.updateOrderStatus("order-001", PurchaseOrderStatus.APPROVED);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(purchaseOrderRepository).findById("order-001");
            verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        }

        @Test
        @DisplayName("Should return error when order not found for status update")
        void shouldReturnErrorWhenOrderNotFoundForStatusUpdate() {
            // Arrange
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<Void> result = purchaseOrderService.updateOrderStatus("non-existent", PurchaseOrderStatus.APPROVED);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("采购订单不存在"));
        }
    }

    @Nested
    @DisplayName("Purchase Order Workflow Tests")
    class PurchaseOrderWorkflowTests {

        @Test
        @DisplayName("Should complete full purchase order workflow")
        void shouldCompleteFullPurchaseOrderWorkflow() {
            // Step 1: Create order (DRAFT)
            when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);
            when(purchaseOrderItemRepository.saveAll(anyList())).thenReturn(Arrays.asList(testItem));
            Result<PurchaseOrderVO> createResult = purchaseOrderService.createPurchaseOrder(applyDTO);
            assertEquals(0, createResult.getCode());
            assertEquals(PurchaseOrderStatus.DRAFT, createResult.getData().getStatus());

            // Step 2: Submit order (DRAFT -> PENDING)
            when(purchaseOrderRepository.findById(anyString())).thenReturn(Optional.of(testOrder));
            when(purchaseOrderItemRepository.findByOrderId(anyString())).thenReturn(Arrays.asList(testItem));
            Result<PurchaseOrderVO> submitResult = purchaseOrderService.submitPurchaseOrder("order-001", "applicant-001", "Applicant");
            assertEquals(0, submitResult.getCode());

            // Step 3: Audit order (PENDING -> APPROVED)
            testOrder.setStatus(PurchaseOrderStatus.PENDING);
            auditDTO.setAuditResult("通过");
            Result<PurchaseOrderVO> auditResult = purchaseOrderService.auditPurchaseOrder("order-001", auditDTO);
            assertEquals(0, auditResult.getCode());

            // Step 4: Confirm receive (APPROVED -> COMPLETED)
            testOrder.setStatus(PurchaseOrderStatus.APPROVED);
            testItem.setQuantity(new BigDecimal("50"));
            receiveDTO.getItems().get(0).setQuantity(new BigDecimal("50"));
            Result<PurchaseOrderVO> receiveResult = purchaseOrderService.confirmReceive("order-001", receiveDTO);
            assertEquals(0, receiveResult.getCode());
        }
    }
}
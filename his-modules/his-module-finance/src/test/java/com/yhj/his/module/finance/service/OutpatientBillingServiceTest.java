package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.dto.OutpatientRefundDTO;
import com.yhj.his.module.finance.dto.OutpatientSettleDTO;
import com.yhj.his.module.finance.dto.PaymentDTO;
import com.yhj.his.module.finance.entity.Invoice;
import com.yhj.his.module.finance.entity.OutpatientBilling;
import com.yhj.his.module.finance.entity.OutpatientBillingItem;
import com.yhj.his.module.finance.entity.PriceItem;
import com.yhj.his.module.finance.repository.OutpatientBillingItemRepository;
import com.yhj.his.module.finance.repository.OutpatientBillingRepository;
import com.yhj.his.module.finance.repository.PriceItemRepository;
import com.yhj.his.module.finance.service.impl.OutpatientBillingServiceImpl;
import com.yhj.his.module.finance.vo.OutpatientBillingVO;
import com.yhj.his.module.finance.vo.PendingBillingVO;
import com.yhj.his.module.finance.vo.SettlementResultVO;
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
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

/**
 * OutpatientBillingService Unit Tests
 *
 * Tests cover outpatient billing operations including:
 * - Settlement operations
 * - Refund operations
 * - Query operations (by billing no, invoice no, patient, visit)
 * - Fee calculation
 * - Invoice printing
 *
 * Target coverage: 90%+
 */
@ExtendWith(MockitoExtension.class)
class OutpatientBillingServiceTest {

    @Mock
    private OutpatientBillingRepository billingRepository;

    @Mock
    private OutpatientBillingItemRepository billingItemRepository;

    @Mock
    private PriceItemRepository priceItemRepository;

    @Mock
    private InsurancePolicyService insurancePolicyService;

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private OutpatientBillingServiceImpl outpatientBillingService;

    private OutpatientSettleDTO settleDTO;
    private OutpatientRefundDTO refundDTO;
    private OutpatientBilling billing;
    private OutpatientBillingItem billingItem;

    @BeforeEach
    void setUp() {
        // Setup settle DTO
        OutpatientSettleDTO.PaymentDTO payment = new OutpatientSettleDTO.PaymentDTO();
        payment.setPayMethod("CASH");
        payment.setAmount(BigDecimal.valueOf(100));

        settleDTO = new OutpatientSettleDTO();
        settleDTO.setVisitId("visit-123");
        settleDTO.setPatientId("patient-123");
        settleDTO.setPatientName("Test Patient");
        settleDTO.setVisitNo("V20240101001");
        settleDTO.setDeptId("dept-001");
        settleDTO.setDeptName("内科");
        settleDTO.setInsuranceType("URBAN_EMPLOYEE");
        settleDTO.setInsuranceCardNo("INS123456789");
        settleDTO.setPayments(Arrays.asList(payment));

        // Setup refund DTO
        refundDTO = new OutpatientRefundDTO();
        refundDTO.setBillingNo("BIL2024010100001");
        refundDTO.setRefundReason("患者要求退费");
        refundDTO.setItemIds(Arrays.asList("item-001"));

        // Setup billing entity
        billing = new OutpatientBilling();
        billing.setId("billing-123");
        billing.setBillingNo("BIL2024010100001");
        billing.setInvoiceNo("INV2024010100001");
        billing.setPatientId("patient-123");
        billing.setPatientName("Test Patient");
        billing.setVisitId("visit-123");
        billing.setVisitNo("V20240101001");
        billing.setDeptId("dept-001");
        billing.setDeptName("内科");
        billing.setBillingDate(LocalDate.now());
        billing.setBillingTime(LocalDateTime.now());
        billing.setTotalAmount(BigDecimal.valueOf(100));
        billing.setInsuranceAmount(BigDecimal.ZERO);
        billing.setSelfPayAmount(BigDecimal.valueOf(100));
        billing.setStatus(OutpatientBilling.BillingStatus.NORMAL);
        billing.setRefundStatus(OutpatientBilling.RefundStatus.NONE);
        billing.setOperatorId("operator-001");
        billing.setOperatorName("Admin");
        billing.setDeleted(false);

        // Setup billing item
        billingItem = new OutpatientBillingItem();
        billingItem.setId("item-001");
        billingItem.setBillingId("billing-123");
        billingItem.setItemId("price-001");
        billingItem.setItemCode("ITEM001");
        billingItem.setItemName("Test Item");
        billingItem.setItemCategory(PriceItem.ItemCategory.DRUG);
        billingItem.setItemUnit("盒");
        billingItem.setQuantity(BigDecimal.valueOf(2));
        billingItem.setUnitPrice(BigDecimal.valueOf(50));
        billingItem.setAmount(BigDecimal.valueOf(100));
        billingItem.setInsuranceType(PriceItem.InsuranceType.A);
        billingItem.setInsuranceAmount(BigDecimal.valueOf(80));
        billingItem.setSelfPayAmount(BigDecimal.valueOf(20));
        billingItem.setStatus(OutpatientBillingItem.BillingItemStatus.NORMAL);
        billingItem.setDeleted(false);
    }

    @Nested
    @DisplayName("Get Pending Items Tests")
    class GetPendingItemsTests {

        @Test
        @DisplayName("Should return pending items for visit")
        void shouldReturnPendingItemsForVisit() {
            // When
            PendingBillingVO result = outpatientBillingService.getPendingItems("visit-123");

            // Then
            assertNotNull(result);
            assertEquals("visit-123", result.getVisitId());
            assertNotNull(result.getItems());
            assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        }

        @Test
        @DisplayName("Should return empty pending items for new visit")
        void shouldReturnEmptyPendingItemsForNewVisit() {
            // When
            PendingBillingVO result = outpatientBillingService.getPendingItems("new-visit");

            // Then
            assertNotNull(result);
            assertEquals("new-visit", result.getVisitId());
            assertTrue(result.getItems().isEmpty());
        }
    }

    @Nested
    @DisplayName("Settlement Tests")
    class SettlementTests {

        @Test
        @DisplayName("Should settle outpatient billing successfully")
        void shouldSettleSuccessfully() {
            // Given
            when(billingRepository.save(any(OutpatientBilling.class))).thenAnswer(invocation -> {
                OutpatientBilling saved = invocation.getArgument(0);
                saved.setId("billing-123");
                return saved;
            });

            // Mock invoice creation
            com.yhj.his.module.finance.vo.InvoiceVO invoiceVO = new com.yhj.his.module.finance.vo.InvoiceVO();
            invoiceVO.setInvoiceNo("INV2024010100001");
            when(invoiceService.createInvoice(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(invoiceVO);

            // When
            SettlementResultVO result = outpatientBillingService.settle(
                    settleDTO, "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertNotNull(result.getBillingNo());
            assertEquals(BigDecimal.valueOf(100), result.getTotalAmount());
            assertEquals(BigDecimal.ZERO, result.getInsuranceAmount());
            assertEquals(BigDecimal.valueOf(100), result.getSelfPayAmount());
            assertNotNull(result.getSettlementTime());

            verify(billingRepository, times(2)).save(any(OutpatientBilling.class));
            verify(invoiceService).createInvoice(anyString(), eq(Invoice.BillingType.OUTPATIENT.name()), anyString(), anyString());
        }

        @Test
        @DisplayName("Should settle with insurance information")
        void shouldSettleWithInsurance() {
            // Given
            settleDTO.setInsuranceType("URBAN_EMPLOYEE");
            settleDTO.setInsuranceCardNo("INS123456789");

            when(billingRepository.save(any(OutpatientBilling.class))).thenAnswer(invocation -> {
                OutpatientBilling saved = invocation.getArgument(0);
                saved.setId("billing-123");
                return saved;
            });

            com.yhj.his.module.finance.vo.InvoiceVO invoiceVO = new com.yhj.his.module.finance.vo.InvoiceVO();
            invoiceVO.setInvoiceNo("INV2024010100001");
            when(invoiceService.createInvoice(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(invoiceVO);

            // When
            SettlementResultVO result = outpatientBillingService.settle(
                    settleDTO, "operator-001", "Admin");

            // Then
            assertNotNull(result);
            verify(billingRepository, times(2)).save(any(OutpatientBilling.class));
        }

        @Test
        @DisplayName("Should settle with multiple payment methods")
        void shouldSettleWithMultiplePayments() {
            // Given
            OutpatientSettleDTO.PaymentDTO payment1 = new OutpatientSettleDTO.PaymentDTO();
            payment1.setPayMethod("CASH");
            payment1.setAmount(BigDecimal.valueOf(50));

            OutpatientSettleDTO.PaymentDTO payment2 = new OutpatientSettleDTO.PaymentDTO();
            payment2.setPayMethod("WECHAT");
            payment2.setAmount(BigDecimal.valueOf(50));

            settleDTO.setPayments(Arrays.asList(payment1, payment2));

            when(billingRepository.save(any(OutpatientBilling.class))).thenAnswer(invocation -> {
                OutpatientBilling saved = invocation.getArgument(0);
                saved.setId("billing-123");
                return saved;
            });
            com.yhj.his.module.finance.vo.InvoiceVO invoiceVO = new com.yhj.his.module.finance.vo.InvoiceVO();
            invoiceVO.setInvoiceNo("INV2024010100001");
            when(invoiceService.createInvoice(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(invoiceVO);

            // When
            SettlementResultVO result = outpatientBillingService.settle(
                    settleDTO, "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(100), result.getTotalAmount());
        }

        @Test
        @DisplayName("Should generate billing number correctly")
        void shouldGenerateBillingNumberCorrectly() {
            // Given
            when(billingRepository.save(any(OutpatientBilling.class))).thenAnswer(invocation -> {
                OutpatientBilling saved = invocation.getArgument(0);
                saved.setId("billing-123");
                return saved;
            });

            com.yhj.his.module.finance.vo.InvoiceVO invoiceVO = new com.yhj.his.module.finance.vo.InvoiceVO();
            invoiceVO.setInvoiceNo("INV2024010100001");
            when(invoiceService.createInvoice(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(invoiceVO);

            // When
            SettlementResultVO result = outpatientBillingService.settle(
                    settleDTO, "operator-001", "Admin");

            // Then
            assertNotNull(result.getBillingNo());
            assertTrue(result.getBillingNo().startsWith("BIL"));
        }
    }

    @Nested
    @DisplayName("Refund Tests")
    class RefundTests {

        @Test
        @DisplayName("Should refund all items successfully")
        void shouldRefundAllItemsSuccessfully() {
            // Given
            refundDTO.setItemIds(null); // Full refund

            List<OutpatientBillingItem> items = Arrays.asList(billingItem);
            when(billingRepository.findByBillingNo("BIL2024010100001")).thenReturn(Optional.of(billing));
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(items);
            when(billingItemRepository.save(any(OutpatientBillingItem.class))).thenReturn(billingItem);
            when(billingRepository.save(any(OutpatientBilling.class))).thenReturn(billing);

            // When
            SettlementResultVO result = outpatientBillingService.refund(
                    refundDTO, "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(100), result.getRefundAmount());
            assertEquals(OutpatientBilling.BillingStatus.REFUNDED, billing.getStatus());
            assertEquals(OutpatientBilling.RefundStatus.COMPLETED, billing.getRefundStatus());
            assertNotNull(billing.getRefundTime());

            verify(billingRepository).findByBillingNo("BIL2024010100001");
            verify(billingItemRepository).findByBillingId("billing-123");
            verify(billingItemRepository).save(any(OutpatientBillingItem.class));
            verify(billingRepository).save(any(OutpatientBilling.class));
        }

        @Test
        @DisplayName("Should refund partial items successfully")
        void shouldRefundPartialItemsSuccessfully() {
            // Given
            OutpatientBillingItem item2 = new OutpatientBillingItem();
            item2.setId("item-002");
            item2.setBillingId("billing-123");
            item2.setAmount(BigDecimal.valueOf(50));
            item2.setStatus(OutpatientBillingItem.BillingItemStatus.NORMAL);

            billing.setTotalAmount(BigDecimal.valueOf(150));
            refundDTO.setItemIds(Arrays.asList("item-001"));

            List<OutpatientBillingItem> items = Arrays.asList(billingItem, item2);
            when(billingRepository.findByBillingNo("BIL2024010100001")).thenReturn(Optional.of(billing));
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(items);
            when(billingItemRepository.save(any(OutpatientBillingItem.class))).thenReturn(billingItem);
            when(billingRepository.save(any(OutpatientBilling.class))).thenReturn(billing);

            // When
            SettlementResultVO result = outpatientBillingService.refund(
                    refundDTO, "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(100), result.getRefundAmount());
            assertEquals(OutpatientBilling.BillingStatus.PARTIAL_REFUND, billing.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when billing not found for refund")
        void shouldThrowExceptionWhenBillingNotFoundForRefund() {
            // Given
            when(billingRepository.findByBillingNo("NONEXISTENT")).thenReturn(Optional.empty());
            refundDTO.setBillingNo("NONEXISTENT");

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> outpatientBillingService.refund(refundDTO, "operator-001", "Admin"));
            assertTrue(exception.getMessage().contains("收费记录不存在"));
        }

        @Test
        @DisplayName("Should throw exception when billing already refunded")
        void shouldThrowExceptionWhenAlreadyRefunded() {
            // Given
            billing.setStatus(OutpatientBilling.BillingStatus.REFUNDED);
            when(billingRepository.findByBillingNo("BIL2024010100001")).thenReturn(Optional.of(billing));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> outpatientBillingService.refund(refundDTO, "operator-001", "Admin"));
            assertTrue(exception.getMessage().contains("已全部退费"));
        }

        @Test
        @DisplayName("Should skip already refunded items during partial refund")
        void shouldSkipAlreadyRefundedItemsDuringPartialRefund() {
            // Given
            billingItem.setStatus(OutpatientBillingItem.BillingItemStatus.REFUNDED);
            OutpatientBillingItem item2 = new OutpatientBillingItem();
            item2.setId("item-002");
            item2.setBillingId("billing-123");
            item2.setAmount(BigDecimal.valueOf(50));
            item2.setStatus(OutpatientBillingItem.BillingItemStatus.NORMAL);

            List<OutpatientBillingItem> items = Arrays.asList(billingItem, item2);
            when(billingRepository.findByBillingNo("BIL2024010100001")).thenReturn(Optional.of(billing));
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(items);
            // billingItemRepository.save is not called because item-001 is already refunded
            lenient().when(billingItemRepository.save(any(OutpatientBillingItem.class))).thenReturn(item2);
            when(billingRepository.save(any(OutpatientBilling.class))).thenReturn(billing);

            // When
            SettlementResultVO result = outpatientBillingService.refund(
                    refundDTO, "operator-001", "Admin");

            // Then
            // item-001 is already refunded, so refund should be 0
            assertEquals(BigDecimal.ZERO, result.getRefundAmount());
        }

        @Test
        @DisplayName("Should record refund operator and reason")
        void shouldRecordRefundOperatorAndReason() {
            // Given
            refundDTO.setItemIds(null);
            refundDTO.setRefundReason("Medical error correction");
            List<OutpatientBillingItem> items = Arrays.asList(billingItem);
            when(billingRepository.findByBillingNo("BIL2024010100001")).thenReturn(Optional.of(billing));
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(items);
            when(billingItemRepository.save(any(OutpatientBillingItem.class))).thenReturn(billingItem);
            when(billingRepository.save(any(OutpatientBilling.class))).thenReturn(billing);

            // When
            SettlementResultVO result = outpatientBillingService.refund(
                    refundDTO, "operator-002", "Admin2");

            // Then
            assertEquals("operator-002", billing.getRefundOperatorId());
            assertEquals("Medical error correction", billing.getRefundReason());
        }
    }

    @Nested
    @DisplayName("Query Operations Tests")
    class QueryOperationsTests {

        @Test
        @DisplayName("Should get billing by billing number")
        void shouldGetByBillingNo() {
            // Given
            when(billingRepository.findByBillingNo("BIL2024010100001")).thenReturn(Optional.of(billing));
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(Arrays.asList(billingItem));

            // When
            OutpatientBillingVO result = outpatientBillingService.getByBillingNo("BIL2024010100001");

            // Then
            assertNotNull(result);
            assertEquals("BIL2024010100001", result.getBillingNo());
            assertEquals("Test Patient", result.getPatientName());
            assertNotNull(result.getItems());
        }

        @Test
        @DisplayName("Should throw exception when billing number not found")
        void shouldThrowExceptionWhenBillingNoNotFound() {
            // Given
            when(billingRepository.findByBillingNo("NONEXISTENT")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> outpatientBillingService.getByBillingNo("NONEXISTENT"));
        }

        @Test
        @DisplayName("Should get billing by invoice number")
        void shouldGetByInvoiceNo() {
            // Given
            when(billingRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(billing));
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(Arrays.asList(billingItem));

            // When
            OutpatientBillingVO result = outpatientBillingService.getByInvoiceNo("INV2024010100001");

            // Then
            assertNotNull(result);
            assertEquals("INV2024010100001", result.getInvoiceNo());
        }

        @Test
        @DisplayName("Should throw exception when invoice number not found")
        void shouldThrowExceptionWhenInvoiceNoNotFound() {
            // Given
            when(billingRepository.findByInvoiceNo("NONEXISTENT")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> outpatientBillingService.getByInvoiceNo("NONEXISTENT"));
        }

        @Test
        @DisplayName("Should list billings by visit ID")
        void shouldListByVisitId() {
            // Given
            List<OutpatientBilling> billings = Arrays.asList(billing);
            when(billingRepository.findByVisitId("visit-123")).thenReturn(billings);
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(Arrays.asList(billingItem));

            // When
            List<OutpatientBillingVO> result = outpatientBillingService.listByVisitId("visit-123");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("visit-123", result.get(0).getVisitId());
        }

        @Test
        @DisplayName("Should return empty list when visit has no billings")
        void shouldReturnEmptyListWhenVisitHasNoBillings() {
            // Given
            when(billingRepository.findByVisitId("empty-visit")).thenReturn(Collections.emptyList());

            // When
            List<OutpatientBillingVO> result = outpatientBillingService.listByVisitId("empty-visit");

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should list billings by patient ID")
        void shouldListByPatientId() {
            // Given
            List<OutpatientBilling> billings = Arrays.asList(billing);
            when(billingRepository.findByPatientId("patient-123")).thenReturn(billings);
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(Arrays.asList(billingItem));

            // When
            List<OutpatientBillingVO> result = outpatientBillingService.listByPatientId("patient-123");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("patient-123", result.get(0).getPatientId());
        }

        @Test
        @DisplayName("Should return paginated billing list")
        void shouldReturnPaginatedBillingList() {
            // Given
            List<OutpatientBilling> billings = Arrays.asList(billing);
            Page<OutpatientBilling> page = new PageImpl<>(billings);
            when(billingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(Arrays.asList(billingItem));

            // When
            PageResult<OutpatientBillingVO> result = outpatientBillingService.pageList(
                    "patient-123", "2024-01-01", "NORMAL", 1, 10);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("Should return empty page when no billings match criteria")
        void shouldReturnEmptyPageWhenNoBillingsMatch() {
            // Given
            Page<OutpatientBilling> emptyPage = new PageImpl<>(Collections.emptyList());
            when(billingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

            // When
            PageResult<OutpatientBillingVO> result = outpatientBillingService.pageList(
                    "nonexistent-patient", null, null, 1, 10);

            // Then
            assertNotNull(result);
            assertTrue(result.getList().isEmpty());
        }
    }

    @Nested
    @DisplayName("Fee Calculation Tests")
    class FeeCalculationTests {

        @Test
        @DisplayName("Should calculate fee for visit")
        void shouldCalculateFeeForVisit() {
            // When
            PendingBillingVO result = outpatientBillingService.calculateFee("visit-123", "URBAN_EMPLOYEE");

            // Then
            assertNotNull(result);
            assertEquals("visit-123", result.getVisitId());
        }

        @Test
        @DisplayName("Should calculate fee without insurance")
        void shouldCalculateFeeWithoutInsurance() {
            // When
            PendingBillingVO result = outpatientBillingService.calculateFee("visit-123", null);

            // Then
            assertNotNull(result);
            assertEquals("visit-123", result.getVisitId());
        }
    }

    @Nested
    @DisplayName("Print Invoice Tests")
    class PrintInvoiceTests {

        @Test
        @DisplayName("Should print invoice successfully")
        void shouldPrintInvoiceSuccessfully() {
            // Given
            when(billingRepository.findByBillingNo("BIL2024010100001")).thenReturn(Optional.of(billing));
            when(invoiceService.printInvoice("INV2024010100001")).thenReturn(new com.yhj.his.module.finance.vo.InvoiceVO());
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(Arrays.asList(billingItem));

            // When
            OutpatientBillingVO result = outpatientBillingService.printInvoice("BIL2024010100001");

            // Then
            assertNotNull(result);
            verify(invoiceService).printInvoice("INV2024010100001");
        }

        @Test
        @DisplayName("Should throw exception when printing invoice for non-existent billing")
        void shouldThrowExceptionWhenPrintingInvoiceForNonExistentBilling() {
            // Given
            when(billingRepository.findByBillingNo("NONEXISTENT")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> outpatientBillingService.printInvoice("NONEXISTENT"));
        }
    }

    @Nested
    @DisplayName("VO Conversion Tests")
    class VOConversionTests {

        @Test
        @DisplayName("Should correctly convert billing entity to VO")
        void shouldConvertBillingEntityToVO() {
            // Given
            when(billingRepository.findByBillingNo("BIL2024010100001")).thenReturn(Optional.of(billing));
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(Arrays.asList(billingItem));

            // When
            OutpatientBillingVO vo = outpatientBillingService.getByBillingNo("BIL2024010100001");

            // Then
            assertNotNull(vo);
            assertEquals("billing-123", vo.getId());
            assertEquals("BIL2024010100001", vo.getBillingNo());
            assertEquals("INV2024010100001", vo.getInvoiceNo());
            assertEquals("patient-123", vo.getPatientId());
            assertEquals("Test Patient", vo.getPatientName());
            assertEquals("visit-123", vo.getVisitId());
            assertEquals("V20240101001", vo.getVisitNo());
            assertEquals("dept-001", vo.getDeptId());
            assertEquals("内科", vo.getDeptName());
            assertEquals(BigDecimal.valueOf(100), vo.getTotalAmount());
            assertEquals("NORMAL", vo.getStatus());
            assertEquals("正常", vo.getStatusDesc());
            assertEquals("NONE", vo.getRefundStatus());
            assertEquals("未退费", vo.getRefundStatusDesc());
            assertNotNull(vo.getItems());
            assertEquals(1, vo.getItems().size());
        }

        @Test
        @DisplayName("Should correctly convert billing item entity to VO")
        void shouldConvertBillingItemEntityToVO() {
            // Given
            when(billingRepository.findByBillingNo("BIL2024010100001")).thenReturn(Optional.of(billing));
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(Arrays.asList(billingItem));

            // When
            OutpatientBillingVO vo = outpatientBillingService.getByBillingNo("BIL2024010100001");

            // Then
            assertNotNull(vo.getItems());
            assertEquals(1, vo.getItems().size());

            com.yhj.his.module.finance.vo.OutpatientBillingItemVO itemVO = vo.getItems().get(0);
            assertEquals("item-001", itemVO.getId());
            assertEquals("price-001", itemVO.getItemId());
            assertEquals("ITEM001", itemVO.getItemCode());
            assertEquals("Test Item", itemVO.getItemName());
            assertEquals("DRUG", itemVO.getItemCategory());
            assertEquals("药品", itemVO.getItemCategoryDesc());
            assertEquals("盒", itemVO.getItemUnit());
            assertEquals(BigDecimal.valueOf(2), itemVO.getQuantity());
            assertEquals(BigDecimal.valueOf(50), itemVO.getUnitPrice());
            assertEquals(BigDecimal.valueOf(100), itemVO.getAmount());
            assertEquals("A", itemVO.getInsuranceType());
        }

        @Test
        @DisplayName("Should handle null enums in VO conversion")
        void shouldHandleNullEnumsInVOConversion() {
            // Given
            billing.setInsuranceType(null);
            billing.setStatus(null);
            billing.setRefundStatus(null);
            billingItem.setItemCategory(null);
            billingItem.setInsuranceType(null);

            when(billingRepository.findByBillingNo("BIL2024010100001")).thenReturn(Optional.of(billing));
            when(billingItemRepository.findByBillingId("billing-123")).thenReturn(Arrays.asList(billingItem));

            // When
            OutpatientBillingVO vo = outpatientBillingService.getByBillingNo("BIL2024010100001");

            // Then
            assertNotNull(vo);
            assertNull(vo.getInsuranceType());
            assertNull(vo.getInsuranceTypeDesc());
            assertNull(vo.getStatus());
            assertNull(vo.getStatusDesc());
            assertNull(vo.getRefundStatus());
            assertNull(vo.getRefundStatusDesc());

            assertNull(vo.getItems().get(0).getItemCategory());
            assertNull(vo.getItems().get(0).getItemCategoryDesc());
        }
    }
}
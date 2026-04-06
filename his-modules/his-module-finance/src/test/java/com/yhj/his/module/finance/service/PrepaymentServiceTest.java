package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.dto.PrepaymentDTO;
import com.yhj.his.module.finance.entity.Invoice;
import com.yhj.his.module.finance.entity.Prepayment;
import com.yhj.his.module.finance.repository.PrepaymentRepository;
import com.yhj.his.module.finance.service.impl.PrepaymentServiceImpl;
import com.yhj.his.module.finance.vo.PrepaymentBalanceVO;
import com.yhj.his.module.finance.vo.PrepaymentVO;
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
 * PrepaymentService Unit Tests
 *
 * Tests cover prepayment management operations including:
 * - Deposit operations
 * - Refund operations
 * - Balance queries
 * - Prepayment record queries
 * - Balance warning checks
 *
 * Target coverage: 90%+
 */
@ExtendWith(MockitoExtension.class)
class PrepaymentServiceTest {

    @Mock
    private PrepaymentRepository prepaymentRepository;

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private PrepaymentServiceImpl prepaymentService;

    private PrepaymentDTO depositDTO;
    private Prepayment prepayment;

    @BeforeEach
    void setUp() {
        // Setup deposit DTO
        depositDTO = new PrepaymentDTO();
        depositDTO.setAdmissionId("admission-123");
        depositDTO.setPatientId("patient-123");
        depositDTO.setPatientName("Test Patient");
        depositDTO.setDepositAmount(BigDecimal.valueOf(1000));
        depositDTO.setPaymentMethod("CASH");
        depositDTO.setRemark("Initial deposit");

        // Setup prepayment entity
        prepayment = new Prepayment();
        prepayment.setId("prepayment-123");
        prepayment.setPrepaymentNo("PRE2024010100001");
        prepayment.setReceiptNo("INV2024010100001");
        prepayment.setAdmissionId("admission-123");
        prepayment.setPatientId("patient-123");
        prepayment.setPatientName("Test Patient");
        prepayment.setDepositType(Prepayment.DepositType.DEPOSIT);
        prepayment.setDepositAmount(BigDecimal.valueOf(1000));
        prepayment.setPaymentMethod(Prepayment.PaymentMethod.CASH);
        prepayment.setBalanceBefore(BigDecimal.ZERO);
        prepayment.setBalanceAfter(BigDecimal.valueOf(1000));
        prepayment.setOperatorId("operator-001");
        prepayment.setOperatorName("Admin");
        prepayment.setOperateTime(LocalDateTime.now());
        prepayment.setStatus(Prepayment.PrepaymentStatus.NORMAL);
        prepayment.setRemark("Initial deposit");
        prepayment.setDeleted(false);
    }

    @Nested
    @DisplayName("Deposit Operations Tests")
    class DepositOperationsTests {

        @Test
        @DisplayName("Should deposit prepayment successfully")
        void shouldDepositSuccessfully() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.ZERO);
            when(prepaymentRepository.save(any(Prepayment.class))).thenAnswer(invocation -> {
                Prepayment saved = invocation.getArgument(0);
                saved.setId("prepayment-123");
                return saved;
            });

            com.yhj.his.module.finance.vo.InvoiceVO invoiceVO = new com.yhj.his.module.finance.vo.InvoiceVO();
            invoiceVO.setInvoiceNo("INV2024010100001");
            when(invoiceService.createInvoice(anyString(), eq(Invoice.BillingType.PREPAYMENT.name()), anyString(), anyString()))
                    .thenReturn(invoiceVO);

            // When
            PrepaymentVO result = prepaymentService.deposit(depositDTO, "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertNotNull(result.getPrepaymentNo());
            assertEquals(BigDecimal.valueOf(1000), result.getDepositAmount());
            assertEquals("DEPOSIT", result.getDepositType());
            assertEquals(BigDecimal.ZERO, result.getBalanceBefore());
            assertEquals(BigDecimal.valueOf(1000), result.getBalanceAfter());
            assertEquals("CASH", result.getPaymentMethod());

            verify(prepaymentRepository, times(2)).save(any(Prepayment.class));
            verify(invoiceService).createInvoice(anyString(), eq(Invoice.BillingType.PREPAYMENT.name()), anyString(), anyString());
        }

        @Test
        @DisplayName("Should deposit with existing balance")
        void shouldDepositWithExistingBalance() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(500));
            when(prepaymentRepository.save(any(Prepayment.class))).thenAnswer(invocation -> {
                Prepayment saved = invocation.getArgument(0);
                saved.setId("prepayment-123");
                return saved;
            });

            com.yhj.his.module.finance.vo.InvoiceVO invoiceVO = new com.yhj.his.module.finance.vo.InvoiceVO();
            invoiceVO.setInvoiceNo("INV2024010100001");
            when(invoiceService.createInvoice(anyString(), eq(Invoice.BillingType.PREPAYMENT.name()), anyString(), anyString()))
                    .thenReturn(invoiceVO);

            // When
            PrepaymentVO result = prepaymentService.deposit(depositDTO, "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(500), result.getBalanceBefore());
            assertEquals(BigDecimal.valueOf(1500), result.getBalanceAfter());
        }

        @Test
        @DisplayName("Should deposit with different payment methods")
        void shouldDepositWithDifferentPaymentMethods() {
            // Given
            depositDTO.setPaymentMethod("WECHAT");
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.ZERO);
            when(prepaymentRepository.save(any(Prepayment.class))).thenAnswer(invocation -> {
                Prepayment saved = invocation.getArgument(0);
                saved.setId("prepayment-123");
                return saved;
            });

            com.yhj.his.module.finance.vo.InvoiceVO invoiceVO = new com.yhj.his.module.finance.vo.InvoiceVO();
            invoiceVO.setInvoiceNo("INV2024010100001");
            when(invoiceService.createInvoice(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(invoiceVO);

            // When
            PrepaymentVO result = prepaymentService.deposit(depositDTO, "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals("WECHAT", result.getPaymentMethod());
            assertEquals("微信支付", result.getPaymentMethodDesc());
        }

        @Test
        @DisplayName("Should handle null deposit sum")
        void shouldHandleNullDepositSum() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(null);
            when(prepaymentRepository.save(any(Prepayment.class))).thenAnswer(invocation -> {
                Prepayment saved = invocation.getArgument(0);
                saved.setId("prepayment-123");
                return saved;
            });

            com.yhj.his.module.finance.vo.InvoiceVO invoiceVO = new com.yhj.his.module.finance.vo.InvoiceVO();
            invoiceVO.setInvoiceNo("INV2024010100001");
            when(invoiceService.createInvoice(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(invoiceVO);

            // When
            PrepaymentVO result = prepaymentService.deposit(depositDTO, "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result.getBalanceBefore());
        }

        @Test
        @DisplayName("Should generate prepayment number")
        void shouldGeneratePrepaymentNumber() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.ZERO);
            when(prepaymentRepository.save(any(Prepayment.class))).thenAnswer(invocation -> {
                Prepayment saved = invocation.getArgument(0);
                saved.setId("prepayment-123");
                return saved;
            });

            com.yhj.his.module.finance.vo.InvoiceVO invoiceVO = new com.yhj.his.module.finance.vo.InvoiceVO();
            invoiceVO.setInvoiceNo("INV2024010100001");
            when(invoiceService.createInvoice(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(invoiceVO);

            // When
            PrepaymentVO result = prepaymentService.deposit(depositDTO, "operator-001", "Admin");

            // Then
            assertNotNull(result.getPrepaymentNo());
            assertTrue(result.getPrepaymentNo().startsWith("PRE"));
        }
    }

    @Nested
    @DisplayName("Refund Operations Tests")
    class RefundOperationsTests {

        @Test
        @DisplayName("Should refund prepayment successfully")
        void shouldRefundSuccessfully() {
            // Given
            BigDecimal refundAmount = BigDecimal.valueOf(500);
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(1000));
            when(prepaymentRepository.save(any(Prepayment.class))).thenAnswer(invocation -> {
                Prepayment saved = invocation.getArgument(0);
                saved.setId("prepayment-123");
                return saved;
            });

            // When
            PrepaymentVO result = prepaymentService.refund(
                    "admission-123", refundAmount, "CASH", "Patient request refund",
                    "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals("REFUND", result.getDepositType());
            assertEquals("退还", result.getDepositTypeDesc());
            assertEquals(BigDecimal.valueOf(500), result.getDepositAmount());
            assertEquals(BigDecimal.valueOf(1000), result.getBalanceBefore());
            assertEquals(BigDecimal.valueOf(500), result.getBalanceAfter());
            assertEquals("Patient request refund", result.getRemark());

            verify(prepaymentRepository).save(any(Prepayment.class));
        }

        @Test
        @DisplayName("Should throw exception when refund amount exceeds balance")
        void shouldThrowExceptionWhenRefundExceedsBalance() {
            // Given
            BigDecimal refundAmount = BigDecimal.valueOf(2000);
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(1000));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> prepaymentService.refund("admission-123", refundAmount, "CASH",
                            "Too much refund", "operator-001", "Admin"));
            assertTrue(exception.getMessage().contains("预交金余额不足"));

            verify(prepaymentRepository, never()).save(any(Prepayment.class));
        }

        @Test
        @DisplayName("Should refund all balance")
        void shouldRefundAllBalance() {
            // Given
            BigDecimal refundAmount = BigDecimal.valueOf(1000);
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(1000));
            when(prepaymentRepository.save(any(Prepayment.class))).thenAnswer(invocation -> {
                Prepayment saved = invocation.getArgument(0);
                saved.setId("prepayment-123");
                return saved;
            });

            // When
            PrepaymentVO result = prepaymentService.refund(
                    "admission-123", refundAmount, "CARD", "Full refund",
                    "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(1000), result.getBalanceBefore());
            assertEquals(BigDecimal.ZERO, result.getBalanceAfter());
        }

        @Test
        @DisplayName("Should handle zero balance refund attempt")
        void shouldHandleZeroBalanceRefundAttempt() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.ZERO);

            // When & Then
            assertThrows(BusinessException.class,
                    () -> prepaymentService.refund("admission-123", BigDecimal.valueOf(100),
                            "CASH", "Attempt refund", "operator-001", "Admin"));
        }

        @Test
        @DisplayName("Should refund with different payment methods")
        void shouldRefundWithDifferentPaymentMethods() {
            // Given
            BigDecimal refundAmount = BigDecimal.valueOf(500);
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(1000));
            when(prepaymentRepository.save(any(Prepayment.class))).thenAnswer(invocation -> {
                Prepayment saved = invocation.getArgument(0);
                saved.setId("prepayment-123");
                return saved;
            });

            // When
            PrepaymentVO result = prepaymentService.refund(
                    "admission-123", refundAmount, "ALIPAY", "Refund via Alipay",
                    "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals("ALIPAY", result.getPaymentMethod());
            assertEquals("支付宝", result.getPaymentMethodDesc());
        }
    }

    @Nested
    @DisplayName("Balance Query Tests")
    class BalanceQueryTests {

        @Test
        @DisplayName("Should get balance successfully")
        void shouldGetBalanceSuccessfully() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(2000));

            // When
            PrepaymentBalanceVO result = prepaymentService.getBalance("admission-123");

            // Then
            assertNotNull(result);
            assertEquals("admission-123", result.getAdmissionId());
            assertEquals(BigDecimal.valueOf(2000), result.getTotalDeposit());
            assertEquals(BigDecimal.valueOf(2000), result.getCurrentBalance());
            assertEquals("SUFFICIENT", result.getBalanceStatus());
        }

        @Test
        @DisplayName("Should return insufficient status when balance below threshold")
        void shouldReturnInsufficientStatusWhenBelowThreshold() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(500));

            // When
            PrepaymentBalanceVO result = prepaymentService.getBalance("admission-123");

            // Then
            assertNotNull(result);
            assertEquals("INSUFFICIENT", result.getBalanceStatus());
        }

        @Test
        @DisplayName("Should return warning status when balance near threshold")
        void shouldReturnWarningStatusWhenNearThreshold() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(1500));

            // When
            PrepaymentBalanceVO result = prepaymentService.getBalance("admission-123");

            // Then
            assertNotNull(result);
            assertEquals("WARNING", result.getBalanceStatus());
        }

        @Test
        @DisplayName("Should handle null balance sum")
        void shouldHandleNullBalanceSum() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(null);

            // When
            PrepaymentBalanceVO result = prepaymentService.getBalance("admission-123");

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result.getTotalDeposit());
            assertEquals("INSUFFICIENT", result.getBalanceStatus());
        }

        @Test
        @DisplayName("Should calculate total deposit correctly")
        void shouldCalculateTotalDepositCorrectly() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(3000));

            // When
            BigDecimal result = prepaymentService.calculateTotalDeposit("admission-123");

            // Then
            assertEquals(BigDecimal.valueOf(3000), result);
        }

        @Test
        @DisplayName("Should return zero when calculating total deposit for null result")
        void shouldReturnZeroForNullDepositSum() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("new-admission")).thenReturn(null);

            // When
            BigDecimal result = prepaymentService.calculateTotalDeposit("new-admission");

            // Then
            assertEquals(BigDecimal.ZERO, result);
        }
    }

    @Nested
    @DisplayName("Deposit Warning Tests")
    class DepositWarningTests {

        @Test
        @DisplayName("Should return true when deposit warning needed")
        void shouldReturnTrueWhenWarningNeeded() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(500));
            BigDecimal estimatedCost = BigDecimal.valueOf(2000);

            // When
            boolean result = prepaymentService.checkDepositWarning("admission-123", estimatedCost);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when deposit sufficient")
        void shouldReturnFalseWhenDepositSufficient() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(3000));
            BigDecimal estimatedCost = BigDecimal.valueOf(2000);

            // When
            boolean result = prepaymentService.checkDepositWarning("admission-123", estimatedCost);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return true when deposit exactly at warning threshold")
        void shouldReturnTrueWhenAtWarningThreshold() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(BigDecimal.valueOf(1000));
            BigDecimal estimatedCost = BigDecimal.valueOf(2000);

            // When
            boolean result = prepaymentService.checkDepositWarning("admission-123", estimatedCost);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle null deposit in warning check")
        void shouldHandleNullDepositInWarningCheck() {
            // Given
            when(prepaymentRepository.sumDepositByAdmissionId("admission-123")).thenReturn(null);
            BigDecimal estimatedCost = BigDecimal.valueOf(2000);

            // When
            boolean result = prepaymentService.checkDepositWarning("admission-123", estimatedCost);

            // Then
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("Query Prepayment Records Tests")
    class QueryPrepaymentRecordsTests {

        @Test
        @DisplayName("Should list prepayments by admission ID")
        void shouldListByAdmissionId() {
            // Given
            List<Prepayment> prepayments = Arrays.asList(prepayment);
            when(prepaymentRepository.findByAdmissionIdAndStatus("admission-123", Prepayment.PrepaymentStatus.NORMAL))
                    .thenReturn(prepayments);

            // When
            List<PrepaymentVO> result = prepaymentService.listByAdmissionId("admission-123");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("admission-123", result.get(0).getAdmissionId());
        }

        @Test
        @DisplayName("Should return empty list when admission has no prepayments")
        void shouldReturnEmptyListWhenNoPrepayments() {
            // Given
            when(prepaymentRepository.findByAdmissionIdAndStatus("empty-admission", Prepayment.PrepaymentStatus.NORMAL))
                    .thenReturn(Collections.emptyList());

            // When
            List<PrepaymentVO> result = prepaymentService.listByAdmissionId("empty-admission");

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should list prepayments by patient ID")
        void shouldListByPatientId() {
            // Given
            List<Prepayment> prepayments = Arrays.asList(prepayment);
            when(prepaymentRepository.findByPatientId("patient-123")).thenReturn(prepayments);

            // When
            List<PrepaymentVO> result = prepaymentService.listByPatientId("patient-123");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("patient-123", result.get(0).getPatientId());
        }

        @Test
        @DisplayName("Should filter deleted prepayments in patient list")
        void shouldFilterDeletedPrepaymentsInPatientList() {
            // Given
            Prepayment deletedPrepayment = new Prepayment();
            deletedPrepayment.setId("deleted-123");
            deletedPrepayment.setDeleted(true);

            List<Prepayment> prepayments = Arrays.asList(prepayment, deletedPrepayment);
            when(prepaymentRepository.findByPatientId("patient-123")).thenReturn(prepayments);

            // When
            List<PrepaymentVO> result = prepaymentService.listByPatientId("patient-123");

            // Then
            assertEquals(1, result.size()); // Deleted one filtered out
        }

        @Test
        @DisplayName("Should get prepayment by prepayment number")
        void shouldGetByPrepaymentNo() {
            // Given
            when(prepaymentRepository.findByPrepaymentNo("PRE2024010100001")).thenReturn(Optional.of(prepayment));

            // When
            PrepaymentVO result = prepaymentService.getByPrepaymentNo("PRE2024010100001");

            // Then
            assertNotNull(result);
            assertEquals("PRE2024010100001", result.getPrepaymentNo());
        }

        @Test
        @DisplayName("Should throw exception when prepayment number not found")
        void shouldThrowExceptionWhenPrepaymentNoNotFound() {
            // Given
            when(prepaymentRepository.findByPrepaymentNo("NONEXISTENT")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> prepaymentService.getByPrepaymentNo("NONEXISTENT"));
        }

        @Test
        @DisplayName("Should return paginated prepayment list")
        void shouldReturnPaginatedPrepaymentList() {
            // Given
            List<Prepayment> prepayments = Arrays.asList(prepayment);
            Page<Prepayment> page = new PageImpl<>(prepayments);
            when(prepaymentRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            // When
            PageResult<PrepaymentVO> result = prepaymentService.pageList(
                    "patient-123", "admission-123", "DEPOSIT", 1, 10);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("Should return empty page when no prepayments match criteria")
        void shouldReturnEmptyPageWhenNoPrepaymentsMatch() {
            // Given
            Page<Prepayment> emptyPage = new PageImpl<>(Collections.emptyList());
            when(prepaymentRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

            // When
            PageResult<PrepaymentVO> result = prepaymentService.pageList(
                    "nonexistent-patient", null, null, 1, 10);

            // Then
            assertNotNull(result);
            assertTrue(result.getList().isEmpty());
        }
    }

    @Nested
    @DisplayName("VO Conversion Tests")
    class VOConversionTests {

        @Test
        @DisplayName("Should correctly convert prepayment entity to VO")
        void shouldConvertPrepaymentEntityToVO() {
            // Given
            when(prepaymentRepository.findByPrepaymentNo("PRE2024010100001")).thenReturn(Optional.of(prepayment));

            // When
            PrepaymentVO vo = prepaymentService.getByPrepaymentNo("PRE2024010100001");

            // Then
            assertNotNull(vo);
            assertEquals("prepayment-123", vo.getId());
            assertEquals("PRE2024010100001", vo.getPrepaymentNo());
            assertEquals("INV2024010100001", vo.getReceiptNo());
            assertEquals("admission-123", vo.getAdmissionId());
            assertEquals("patient-123", vo.getPatientId());
            assertEquals("Test Patient", vo.getPatientName());
            assertEquals("DEPOSIT", vo.getDepositType());
            assertEquals("缴纳", vo.getDepositTypeDesc());
            assertEquals(BigDecimal.valueOf(1000), vo.getDepositAmount());
            assertEquals("CASH", vo.getPaymentMethod());
            assertEquals("现金", vo.getPaymentMethodDesc());
            assertEquals(BigDecimal.ZERO, vo.getBalanceBefore());
            assertEquals(BigDecimal.valueOf(1000), vo.getBalanceAfter());
            assertEquals("operator-001", vo.getOperatorId());
            assertEquals("Admin", vo.getOperatorName());
            assertEquals("Initial deposit", vo.getRemark());
            assertEquals("NORMAL", vo.getStatus());
        }

        @Test
        @DisplayName("Should handle null enums in VO conversion")
        void shouldHandleNullEnumsInVOConversion() {
            // Given
            prepayment.setDepositType(null);
            prepayment.setPaymentMethod(null);
            prepayment.setStatus(null);

            when(prepaymentRepository.findByPrepaymentNo("PRE2024010100001")).thenReturn(Optional.of(prepayment));

            // When
            PrepaymentVO vo = prepaymentService.getByPrepaymentNo("PRE2024010100001");

            // Then
            assertNotNull(vo);
            assertNull(vo.getDepositType());
            assertNull(vo.getDepositTypeDesc());
            assertNull(vo.getPaymentMethod());
            assertNull(vo.getPaymentMethodDesc());
            assertNull(vo.getStatus());
            assertNull(vo.getStatusDesc());
        }

        @Test
        @DisplayName("Should convert refund prepayment correctly")
        void shouldConvertRefundPrepaymentCorrectly() {
            // Given
            prepayment.setDepositType(Prepayment.DepositType.REFUND);
            prepayment.setDepositAmount(BigDecimal.valueOf(500));
            prepayment.setBalanceAfter(BigDecimal.valueOf(500));

            when(prepaymentRepository.findByPrepaymentNo("PRE2024010100001")).thenReturn(Optional.of(prepayment));

            // When
            PrepaymentVO vo = prepaymentService.getByPrepaymentNo("PRE2024010100001");

            // Then
            assertNotNull(vo);
            assertEquals("REFUND", vo.getDepositType());
            assertEquals("退还", vo.getDepositTypeDesc());
            assertEquals(BigDecimal.valueOf(500), vo.getDepositAmount());
        }
    }
}
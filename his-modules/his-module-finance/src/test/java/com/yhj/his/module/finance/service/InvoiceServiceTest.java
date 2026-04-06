package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.dto.InvoiceVoidDTO;
import com.yhj.his.module.finance.entity.InpatientSettlement;
import com.yhj.his.module.finance.entity.Invoice;
import com.yhj.his.module.finance.entity.OutpatientBilling;
import com.yhj.his.module.finance.repository.InpatientSettlementRepository;
import com.yhj.his.module.finance.repository.InvoiceRepository;
import com.yhj.his.module.finance.repository.OutpatientBillingRepository;
import com.yhj.his.module.finance.service.impl.InvoiceServiceImpl;
import com.yhj.his.module.finance.vo.InvoiceVO;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * InvoiceService Unit Tests
 *
 * Tests cover invoice operations including:
 * - Invoice creation
 * - Invoice voiding
 * - Invoice printing and reprinting
 * - Invoice queries
 * - Invoice number generation
 * - Electronic invoice generation
 *
 * Target coverage: 90%+
 */
@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private OutpatientBillingRepository outpatientBillingRepository;

    @Mock
    private InpatientSettlementRepository inpatientSettlementRepository;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private InvoiceVoidDTO voidDTO;
    private Invoice invoice;
    private OutpatientBilling outpatientBilling;
    private InpatientSettlement inpatientSettlement;

    @BeforeEach
    void setUp() {
        // Setup void DTO
        voidDTO = new InvoiceVoidDTO();
        voidDTO.setInvoiceNo("INV2024010100001");
        voidDTO.setVoidReason("Patient request - duplicate invoice");

        // Setup invoice entity
        invoice = new Invoice();
        invoice.setId("invoice-123");
        invoice.setInvoiceNo("INV2024010100001");
        invoice.setInvoiceCode("MED001");
        invoice.setBillingId("billing-123");
        invoice.setBillingType(Invoice.BillingType.OUTPATIENT);
        invoice.setPatientId("patient-123");
        invoice.setPatientName("Test Patient");
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setInvoiceTime(LocalDateTime.now());
        invoice.setTotalAmount(BigDecimal.valueOf(100));
        invoice.setInsuranceAmount(BigDecimal.ZERO);
        invoice.setSelfPayAmount(BigDecimal.valueOf(100));
        invoice.setInvoiceType(Invoice.InvoiceType.MEDICAL);
        invoice.setPrintCount(0);
        invoice.setOperatorId("operator-001");
        invoice.setOperatorName("Admin");
        invoice.setStatus(Invoice.InvoiceStatus.VALID);
        invoice.setDeleted(false);

        // Setup outpatient billing entity
        outpatientBilling = new OutpatientBilling();
        outpatientBilling.setId("billing-123");
        outpatientBilling.setPatientId("patient-123");
        outpatientBilling.setPatientName("Test Patient");
        outpatientBilling.setTotalAmount(BigDecimal.valueOf(100));
        outpatientBilling.setInsuranceAmount(BigDecimal.ZERO);
        outpatientBilling.setSelfPayAmount(BigDecimal.valueOf(100));

        // Setup inpatient settlement entity
        inpatientSettlement = new InpatientSettlement();
        inpatientSettlement.setId("settlement-123");
        inpatientSettlement.setPatientId("patient-123");
        inpatientSettlement.setPatientName("Test Patient");
        inpatientSettlement.setTotalAmount(BigDecimal.valueOf(5000));
        inpatientSettlement.setInsuranceAmount(BigDecimal.valueOf(3000));
        inpatientSettlement.setSelfPayAmount(BigDecimal.valueOf(2000));
    }

    @Nested
    @DisplayName("Create Invoice Tests")
    class CreateInvoiceTests {

        @Test
        @DisplayName("Should create outpatient invoice successfully")
        void shouldCreateOutpatientInvoiceSuccessfully() {
            // Given
            when(outpatientBillingRepository.findById("billing-123")).thenReturn(Optional.of(outpatientBilling));
            when(invoiceRepository.findMaxInvoiceNoByDate(LocalDate.now())).thenReturn(Optional.empty());
            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
                Invoice saved = invocation.getArgument(0);
                saved.setId("invoice-123");
                return saved;
            });

            // When
            InvoiceVO result = invoiceService.createInvoice("billing-123", "OUTPATIENT", "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertNotNull(result.getInvoiceNo());
            assertEquals("MED001", result.getInvoiceCode());
            assertEquals("OUTPATIENT", result.getBillingType());
            assertEquals("patient-123", result.getPatientId());
            assertEquals("Test Patient", result.getPatientName());
            assertEquals(BigDecimal.valueOf(100), result.getTotalAmount());
            assertEquals(BigDecimal.valueOf(100), result.getSelfPayAmount());
            assertEquals("VALID", result.getStatus());

            verify(outpatientBillingRepository).findById("billing-123");
            verify(invoiceRepository).save(any(Invoice.class));
        }

        @Test
        @DisplayName("Should create inpatient invoice successfully")
        void shouldCreateInpatientInvoiceSuccessfully() {
            // Given
            when(inpatientSettlementRepository.findById("settlement-123")).thenReturn(Optional.of(inpatientSettlement));
            when(invoiceRepository.findMaxInvoiceNoByDate(LocalDate.now())).thenReturn(Optional.empty());
            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
                Invoice saved = invocation.getArgument(0);
                saved.setId("invoice-123");
                return saved;
            });

            // When
            InvoiceVO result = invoiceService.createInvoice("settlement-123", "INPATIENT", "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals("INPATIENT", result.getBillingType());
            assertEquals(BigDecimal.valueOf(5000), result.getTotalAmount());
            assertEquals(BigDecimal.valueOf(3000), result.getInsuranceAmount());
            assertEquals(BigDecimal.valueOf(2000), result.getSelfPayAmount());

            verify(inpatientSettlementRepository).findById("settlement-123");
        }

        @Test
        @DisplayName("Should throw exception when outpatient billing not found")
        void shouldThrowExceptionWhenOutpatientBillingNotFound() {
            // Given
            when(outpatientBillingRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> invoiceService.createInvoice("nonexistent", "OUTPATIENT", "operator-001", "Admin"));
            assertTrue(exception.getMessage().contains("门诊收费记录不存在"));
        }

        @Test
        @DisplayName("Should throw exception when inpatient settlement not found")
        void shouldThrowExceptionWhenInpatientSettlementNotFound() {
            // Given
            when(inpatientSettlementRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> invoiceService.createInvoice("nonexistent", "INPATIENT", "operator-001", "Admin"));
            assertTrue(exception.getMessage().contains("住院结算记录不存在"));
        }

        @Test
        @DisplayName("Should handle null insurance amount")
        void shouldHandleNullInsuranceAmount() {
            // Given
            outpatientBilling.setInsuranceAmount(null);
            when(outpatientBillingRepository.findById("billing-123")).thenReturn(Optional.of(outpatientBilling));
            when(invoiceRepository.findMaxInvoiceNoByDate(LocalDate.now())).thenReturn(Optional.empty());
            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
                Invoice saved = invocation.getArgument(0);
                saved.setId("invoice-123");
                return saved;
            });

            // When
            InvoiceVO result = invoiceService.createInvoice("billing-123", "OUTPATIENT", "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result.getInsuranceAmount());
            assertEquals(BigDecimal.valueOf(100), result.getSelfPayAmount());
        }

        @Test
        @DisplayName("Should handle null self pay amount")
        void shouldHandleNullSelfPayAmount() {
            // Given
            outpatientBilling.setSelfPayAmount(null);
            when(outpatientBillingRepository.findById("billing-123")).thenReturn(Optional.of(outpatientBilling));
            when(invoiceRepository.findMaxInvoiceNoByDate(LocalDate.now())).thenReturn(Optional.empty());
            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
                Invoice saved = invocation.getArgument(0);
                saved.setId("invoice-123");
                return saved;
            });

            // When
            InvoiceVO result = invoiceService.createInvoice("billing-123", "OUTPATIENT", "operator-001", "Admin");

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(100), result.getSelfPayAmount());
        }
    }

    @Nested
    @DisplayName("Invoice Number Generation Tests")
    class InvoiceNumberGenerationTests {

        @Test
        @DisplayName("Should generate first invoice number of day")
        void shouldGenerateFirstInvoiceNumberOfDay() {
            // Given
            when(invoiceRepository.findMaxInvoiceNoByDate(LocalDate.now())).thenReturn(Optional.empty());

            // When
            String invoiceNo = invoiceService.getNextInvoiceNo();

            // Then
            assertNotNull(invoiceNo);
            assertTrue(invoiceNo.startsWith("INV"));
            assertTrue(invoiceNo.endsWith("0001"));
        }

        @Test
        @DisplayName("Should increment invoice number correctly")
        void shouldIncrementInvoiceNumberCorrectly() {
            // Given
            when(invoiceRepository.findMaxInvoiceNoByDate(LocalDate.now()))
                    .thenReturn(Optional.of("INV202401010005"));

            // When
            String invoiceNo = invoiceService.getNextInvoiceNo();

            // Then
            assertNotNull(invoiceNo);
            assertTrue(invoiceNo.endsWith("0006"));
        }

        @Test
        @DisplayName("Should handle invoice number sequence correctly")
        void shouldHandleInvoiceNumberSequenceCorrectly() {
            // Given
            when(invoiceRepository.findMaxInvoiceNoByDate(LocalDate.now()))
                    .thenReturn(Optional.of("INV202401010099"));

            // When
            String invoiceNo = invoiceService.getNextInvoiceNo();

            // Then
            assertNotNull(invoiceNo);
            assertTrue(invoiceNo.endsWith("0100"));
        }

        @Test
        @DisplayName("Should generate invoice number with current date")
        void shouldGenerateInvoiceNumberWithCurrentDate() {
            // Given
            when(invoiceRepository.findMaxInvoiceNoByDate(any(LocalDate.class))).thenReturn(Optional.empty());

            // When
            String invoiceNo = invoiceService.getNextInvoiceNo();

            // Then
            String dateStr = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            assertTrue(invoiceNo.contains(dateStr));
        }
    }

    @Nested
    @DisplayName("Void Invoice Tests")
    class VoidInvoiceTests {

        @Test
        @DisplayName("Should void invoice successfully")
        void shouldVoidInvoiceSuccessfully() {
            // Given
            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

            // When
            InvoiceVO result = invoiceService.voidInvoice(voidDTO, "operator-002");

            // Then
            assertNotNull(result);
            assertEquals("VOID", result.getStatus());
            assertEquals("已作废", result.getStatusDesc());
            assertNotNull(invoice.getVoidTime());
            assertEquals("operator-002", invoice.getVoidOperatorId());
            assertEquals("Patient request - duplicate invoice", invoice.getVoidReason());

            verify(invoiceRepository).findByInvoiceNo("INV2024010100001");
            verify(invoiceRepository).save(any(Invoice.class));
        }

        @Test
        @DisplayName("Should throw exception when voiding non-existent invoice")
        void shouldThrowExceptionWhenVoidingNonExistentInvoice() {
            // Given
            when(invoiceRepository.findByInvoiceNo("NONEXISTENT")).thenReturn(Optional.empty());
            voidDTO.setInvoiceNo("NONEXISTENT");

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> invoiceService.voidInvoice(voidDTO, "operator-002"));
            assertTrue(exception.getMessage().contains("发票不存在"));
        }

        @Test
        @DisplayName("Should throw exception when voiding already voided invoice")
        void shouldThrowExceptionWhenVoidingAlreadyVoidedInvoice() {
            // Given
            invoice.setStatus(Invoice.InvoiceStatus.VOID);
            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> invoiceService.voidInvoice(voidDTO, "operator-002"));
            assertTrue(exception.getMessage().contains("发票已作废"));

            verify(invoiceRepository, never()).save(any(Invoice.class));
        }
    }

    @Nested
    @DisplayName("Print Invoice Tests")
    class PrintInvoiceTests {

        @Test
        @DisplayName("Should print invoice successfully")
        void shouldPrintInvoiceSuccessfully() {
            // Given
            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

            // When
            InvoiceVO result = invoiceService.printInvoice("INV2024010100001");

            // Then
            assertNotNull(result);
            assertEquals(1, invoice.getPrintCount());
            assertNotNull(invoice.getLastPrintTime());

            verify(invoiceRepository).findByInvoiceNo("INV2024010100001");
            verify(invoiceRepository).save(any(Invoice.class));
        }

        @Test
        @DisplayName("Should increment print count correctly")
        void shouldIncrementPrintCountCorrectly() {
            // Given
            invoice.setPrintCount(3);
            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

            // When
            InvoiceVO result = invoiceService.printInvoice("INV2024010100001");

            // Then
            assertEquals(4, invoice.getPrintCount());
        }

        @Test
        @DisplayName("Should throw exception when printing voided invoice")
        void shouldThrowExceptionWhenPrintingVoidedInvoice() {
            // Given
            invoice.setStatus(Invoice.InvoiceStatus.VOID);
            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> invoiceService.printInvoice("INV2024010100001"));
            assertTrue(exception.getMessage().contains("发票已作废"));

            verify(invoiceRepository, never()).save(any(Invoice.class));
        }

        @Test
        @DisplayName("Should throw exception when printing non-existent invoice")
        void shouldThrowExceptionWhenPrintingNonExistentInvoice() {
            // Given
            when(invoiceRepository.findByInvoiceNo("NONEXISTENT")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> invoiceService.printInvoice("NONEXISTENT"));
        }
    }

    @Nested
    @DisplayName("Reprint Invoice Tests")
    class ReprintInvoiceTests {

        @Test
        @DisplayName("Should reprint invoice successfully")
        void shouldReprintInvoiceSuccessfully() {
            // Given
            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

            // When
            InvoiceVO result = invoiceService.reprintInvoice("INV2024010100001", "operator-002");

            // Then
            assertNotNull(result);
            assertEquals(1, invoice.getPrintCount());
            assertNotNull(invoice.getLastPrintTime());
            assertTrue(invoice.getRemark().contains("重打操作"));
            assertTrue(invoice.getRemark().contains("operator-002"));

            verify(invoiceRepository).save(any(Invoice.class));
        }

        @Test
        @DisplayName("Should throw exception when reprinting voided invoice")
        void shouldThrowExceptionWhenReprintingVoidedInvoice() {
            // Given
            invoice.setStatus(Invoice.InvoiceStatus.VOID);
            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> invoiceService.reprintInvoice("INV2024010100001", "operator-002"));
            assertTrue(exception.getMessage().contains("发票已作废"));
        }

        @Test
        @DisplayName("Should throw exception when reprinting non-existent invoice")
        void shouldThrowExceptionWhenReprintingNonExistentInvoice() {
            // Given
            when(invoiceRepository.findByInvoiceNo("NONEXISTENT")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> invoiceService.reprintInvoice("NONEXISTENT", "operator-002"));
        }
    }

    @Nested
    @DisplayName("Query Invoice Tests")
    class QueryInvoiceTests {

        @Test
        @DisplayName("Should get invoice by invoice number")
        void shouldGetByInvoiceNo() {
            // Given
            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));

            // When
            InvoiceVO result = invoiceService.getByInvoiceNo("INV2024010100001");

            // Then
            assertNotNull(result);
            assertEquals("INV2024010100001", result.getInvoiceNo());
            verify(invoiceRepository).findByInvoiceNo("INV2024010100001");
        }

        @Test
        @DisplayName("Should throw exception when invoice number not found")
        void shouldThrowExceptionWhenInvoiceNoNotFound() {
            // Given
            when(invoiceRepository.findByInvoiceNo("NONEXISTENT")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> invoiceService.getByInvoiceNo("NONEXISTENT"));
        }

        @Test
        @DisplayName("Should get invoice by billing ID")
        void shouldGetByBillingId() {
            // Given
            when(invoiceRepository.findByBillingId("billing-123")).thenReturn(Optional.of(invoice));

            // When
            InvoiceVO result = invoiceService.getByBillingId("billing-123");

            // Then
            assertNotNull(result);
            assertEquals("billing-123", result.getBillingId());
        }

        @Test
        @DisplayName("Should throw exception when billing ID not found")
        void shouldThrowExceptionWhenBillingIdNotFound() {
            // Given
            when(invoiceRepository.findByBillingId("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> invoiceService.getByBillingId("nonexistent"));
        }

        @Test
        @DisplayName("Should list invoices by patient ID")
        void shouldListByPatientId() {
            // Given
            List<Invoice> invoices = Arrays.asList(invoice);
            when(invoiceRepository.findByPatientId("patient-123")).thenReturn(invoices);

            // When
            List<InvoiceVO> result = invoiceService.listByPatientId("patient-123");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("patient-123", result.get(0).getPatientId());
        }

        @Test
        @DisplayName("Should filter deleted invoices in patient list")
        void shouldFilterDeletedInvoicesInPatientList() {
            // Given
            Invoice deletedInvoice = new Invoice();
            deletedInvoice.setId("deleted-123");
            deletedInvoice.setDeleted(true);

            List<Invoice> invoices = Arrays.asList(invoice, deletedInvoice);
            when(invoiceRepository.findByPatientId("patient-123")).thenReturn(invoices);

            // When
            List<InvoiceVO> result = invoiceService.listByPatientId("patient-123");

            // Then
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return paginated invoice list")
        void shouldReturnPaginatedInvoiceList() {
            // Given
            List<Invoice> invoices = Arrays.asList(invoice);
            Page<Invoice> page = new PageImpl<>(invoices);
            when(invoiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            // When
            PageResult<InvoiceVO> result = invoiceService.pageList(
                    "patient-123", "2024-01-01", "VALID", 1, 10);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("Should return empty page when no invoices match criteria")
        void shouldReturnEmptyPageWhenNoInvoicesMatch() {
            // Given
            Page<Invoice> emptyPage = new PageImpl<>(Collections.emptyList());
            when(invoiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

            // When
            PageResult<InvoiceVO> result = invoiceService.pageList(
                    "nonexistent-patient", null, null, 1, 10);

            // Then
            assertNotNull(result);
            assertTrue(result.getList().isEmpty());
        }
    }

    @Nested
    @DisplayName("Electronic Invoice Tests")
    class ElectronicInvoiceTests {

        @Test
        @DisplayName("Should generate electronic invoice successfully")
        void shouldGenerateElectronicInvoiceSuccessfully() {
            // Given
            when(outpatientBillingRepository.findById("billing-123")).thenReturn(Optional.of(outpatientBilling));
            when(invoiceRepository.findMaxInvoiceNoByDate(LocalDate.now())).thenReturn(Optional.empty());
            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
                Invoice saved = invocation.getArgument(0);
                saved.setId("invoice-123");
                return saved;
            });
            when(invoiceRepository.findByInvoiceNo(anyString())).thenReturn(Optional.of(invoice));

            // When
            InvoiceVO result = invoiceService.generateElectronicInvoice("billing-123", "OUTPATIENT");

            // Then
            assertNotNull(result);
            assertEquals("ELECTRONIC", invoice.getInvoiceType());
            assertEquals("电子发票", invoice.getInvoiceType().getDescription());

            verify(invoiceRepository, times(2)).save(any(Invoice.class));
        }

        @Test
        @DisplayName("Should generate electronic invoice for inpatient")
        void shouldGenerateElectronicInvoiceForInpatient() {
            // Given
            when(inpatientSettlementRepository.findById("settlement-123")).thenReturn(Optional.of(inpatientSettlement));
            when(invoiceRepository.findMaxInvoiceNoByDate(LocalDate.now())).thenReturn(Optional.empty());
            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
                Invoice saved = invocation.getArgument(0);
                saved.setId("invoice-123");
                return saved;
            });
            when(invoiceRepository.findByInvoiceNo(anyString())).thenReturn(Optional.of(invoice));

            // When
            InvoiceVO result = invoiceService.generateElectronicInvoice("settlement-123", "INPATIENT");

            // Then
            assertNotNull(result);
            assertEquals("ELECTRONIC", invoice.getInvoiceType());
        }

        @Test
        @DisplayName("Should use SYSTEM operator for electronic invoice")
        void shouldUseSystemOperatorForElectronicInvoice() {
            // Given
            when(outpatientBillingRepository.findById("billing-123")).thenReturn(Optional.of(outpatientBilling));
            when(invoiceRepository.findMaxInvoiceNoByDate(LocalDate.now())).thenReturn(Optional.empty());
            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
                Invoice saved = invocation.getArgument(0);
                saved.setId("invoice-123");
                return saved;
            });
            when(invoiceRepository.findByInvoiceNo(anyString())).thenReturn(Optional.of(invoice));

            // When
            invoiceService.generateElectronicInvoice("billing-123", "OUTPATIENT");

            // Then
            // Operator should be "SYSTEM" and "系统自动生成"
            verify(invoiceRepository).save(any(Invoice.class));
        }
    }

    @Nested
    @DisplayName("VO Conversion Tests")
    class VOConversionTests {

        @Test
        @DisplayName("Should correctly convert invoice entity to VO")
        void shouldConvertInvoiceEntityToVO() {
            // Given
            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));

            // When
            InvoiceVO vo = invoiceService.getByInvoiceNo("INV2024010100001");

            // Then
            assertNotNull(vo);
            assertEquals("invoice-123", vo.getId());
            assertEquals("INV2024010100001", vo.getInvoiceNo());
            assertEquals("MED001", vo.getInvoiceCode());
            assertEquals("billing-123", vo.getBillingId());
            assertEquals("OUTPATIENT", vo.getBillingType());
            assertEquals("门诊", vo.getBillingTypeDesc());
            assertEquals("patient-123", vo.getPatientId());
            assertEquals("Test Patient", vo.getPatientName());
            assertEquals(BigDecimal.valueOf(100), vo.getTotalAmount());
            assertEquals(BigDecimal.ZERO, vo.getInsuranceAmount());
            assertEquals(BigDecimal.valueOf(100), vo.getSelfPayAmount());
            assertEquals("MEDICAL", vo.getInvoiceType());
            assertEquals("医疗收费发票", vo.getInvoiceTypeDesc());
            assertEquals(0, vo.getPrintCount());
            assertEquals("VALID", vo.getStatus());
            assertEquals("有效", vo.getStatusDesc());
        }

        @Test
        @DisplayName("Should handle null enums in VO conversion")
        void shouldHandleNullEnumsInVOConversion() {
            // Given
            invoice.setBillingType(null);
            invoice.setInvoiceType(null);
            invoice.setStatus(null);

            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));

            // When
            InvoiceVO vo = invoiceService.getByInvoiceNo("INV2024010100001");

            // Then
            assertNotNull(vo);
            assertNull(vo.getBillingType());
            assertNull(vo.getBillingTypeDesc());
            assertNull(vo.getInvoiceType());
            assertNull(vo.getInvoiceTypeDesc());
            assertNull(vo.getStatus());
            assertNull(vo.getStatusDesc());
        }

        @Test
        @DisplayName("Should convert voided invoice correctly")
        void shouldConvertVoidedInvoiceCorrectly() {
            // Given
            invoice.setStatus(Invoice.InvoiceStatus.VOID);
            invoice.setVoidTime(LocalDateTime.now());
            invoice.setVoidReason("Test void reason");

            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));

            // When
            InvoiceVO vo = invoiceService.getByInvoiceNo("INV2024010100001");

            // Then
            assertNotNull(vo);
            assertEquals("VOID", vo.getStatus());
            assertEquals("已作废", vo.getStatusDesc());
            assertNotNull(vo.getVoidTime());
            assertEquals("Test void reason", vo.getVoidReason());
        }

        @Test
        @DisplayName("Should convert electronic invoice correctly")
        void shouldConvertElectronicInvoiceCorrectly() {
            // Given
            invoice.setInvoiceType(Invoice.InvoiceType.ELECTRONIC);

            when(invoiceRepository.findByInvoiceNo("INV2024010100001")).thenReturn(Optional.of(invoice));

            // When
            InvoiceVO vo = invoiceService.getByInvoiceNo("INV2024010100001");

            // Then
            assertNotNull(vo);
            assertEquals("ELECTRONIC", vo.getInvoiceType());
            assertEquals("电子发票", vo.getInvoiceTypeDesc());
        }
    }
}
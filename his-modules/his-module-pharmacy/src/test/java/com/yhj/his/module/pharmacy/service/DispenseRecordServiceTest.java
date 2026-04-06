package com.yhj.his.module.pharmacy.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.DispenseConfirmDTO;
import com.yhj.his.module.pharmacy.dto.DispenseQueryDTO;
import com.yhj.his.module.pharmacy.dto.PrescriptionAuditDTO;
import com.yhj.his.module.pharmacy.entity.DispenseDetail;
import com.yhj.his.module.pharmacy.entity.DispenseRecord;
import com.yhj.his.module.pharmacy.enums.AuditStatus;
import com.yhj.his.module.pharmacy.enums.DispenseStatus;
import com.yhj.his.module.pharmacy.enums.VisitType;
import com.yhj.his.module.pharmacy.repository.DispenseDetailRepository;
import com.yhj.his.module.pharmacy.repository.DispenseRecordRepository;
import com.yhj.his.module.pharmacy.service.impl.DispenseRecordServiceImpl;
import com.yhj.his.module.pharmacy.vo.DispenseRecordVO;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DispenseRecordService Unit Tests
 * Covers Dispensing workflow operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DispenseRecordService Unit Tests")
class DispenseRecordServiceTest {

    @Mock
    private DispenseRecordRepository dispenseRecordRepository;

    @Mock
    private DispenseDetailRepository dispenseDetailRepository;

    @Mock
    private DrugInventoryService drugInventoryService;

    @InjectMocks
    private DispenseRecordServiceImpl dispenseRecordService;

    private DispenseRecord testRecord;
    private DispenseDetail testDetail;
    private DispenseQueryDTO queryDTO;
    private PrescriptionAuditDTO auditDTO;
    private DispenseConfirmDTO confirmDTO;

    @BeforeEach
    void setUp() {
        testRecord = createTestDispenseRecord();
        testDetail = createTestDispenseDetail();
        queryDTO = createTestQueryDTO();
        auditDTO = createTestAuditDTO();
        confirmDTO = createTestConfirmDTO();
    }

    private DispenseRecord createTestDispenseRecord() {
        DispenseRecord record = new DispenseRecord();
        record.setId("dispense-001");
        record.setDispenseNo("DN20240101001");
        record.setPrescriptionId("prescription-001");
        record.setPrescriptionNo("PN20240101001");
        record.setPatientId("patient-001");
        record.setPatientName("John Doe");
        record.setGender("M");
        record.setAge(45);
        record.setVisitType(VisitType.OUTPATIENT);
        record.setDeptId("dept-001");
        record.setDeptName("Internal Medicine");
        record.setDoctorId("doctor-001");
        record.setDoctorName("Dr. Smith");
        record.setPharmacyId("pharmacy-001");
        record.setPharmacyName("Main Pharmacy");
        record.setTotalAmount(new BigDecimal("100.00"));
        record.setAuditStatus(AuditStatus.PENDING);
        record.setDispenseStatus(DispenseStatus.PENDING);
        record.setDeleted(false);
        record.setCreateTime(LocalDateTime.now());
        return record;
    }

    private DispenseDetail createTestDispenseDetail() {
        DispenseDetail detail = new DispenseDetail();
        detail.setId("detail-001");
        detail.setDispenseId("dispense-001");
        detail.setDrugId("drug-001");
        detail.setDrugCode("DRUG001");
        detail.setDrugName("Aspirin");
        detail.setDrugSpec("100mg");
        detail.setDrugUnit("tablet");
        detail.setQuantity(new BigDecimal("10"));
        detail.setRetailPrice(new BigDecimal("10.00"));
        detail.setAmount(new BigDecimal("100.00"));
        detail.setDosage("1 tablet");
        detail.setFrequency("TID");
        detail.setDays(7);
        detail.setRoute("Oral");
        return detail;
    }

    private DispenseQueryDTO createTestQueryDTO() {
        DispenseQueryDTO dto = new DispenseQueryDTO();
        dto.setPharmacyId("pharmacy-001");
        dto.setPatientId("patient-001");
        dto.setPageNum(1);
        dto.setPageSize(10);
        return dto;
    }

    private PrescriptionAuditDTO createTestAuditDTO() {
        PrescriptionAuditDTO dto = new PrescriptionAuditDTO();
        dto.setDispenseId("dispense-001");
        dto.setAuditorId("auditor-001");
        dto.setAuditorName("Auditor Name");
        dto.setAuditResult("通过");
        dto.setAuditRemark("Approved");
        return dto;
    }

    private DispenseConfirmDTO createTestConfirmDTO() {
        DispenseConfirmDTO dto = new DispenseConfirmDTO();
        dto.setDispenseId("dispense-001");
        dto.setDispenserId("dispenser-001");
        dto.setDispenserName("Dispenser Name");

        DispenseConfirmDTO.DetailConfirmDTO detailConfirm = new DispenseConfirmDTO.DetailConfirmDTO();
        detailConfirm.setDetailId("detail-001");
        detailConfirm.setDrugId("drug-001");
        detailConfirm.setBatchNo("BATCH001");
        detailConfirm.setQuantity(new BigDecimal("10"));
        dto.setDetails(Arrays.asList(detailConfirm));
        return dto;
    }

    @Nested
    @DisplayName("Get Dispense Record Tests")
    class GetDispenseRecordTests {

        @Test
        @DisplayName("Should get dispense record by ID successfully")
        void shouldGetDispenseRecordByIdSuccessfully() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.getDispenseRecordById("dispense-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("dispense-001", result.getData().getDispenseId());
            assertEquals("DN20240101001", result.getData().getDispenseNo());

            verify(dispenseRecordRepository).findById("dispense-001");
            verify(dispenseDetailRepository).findByDispenseId("dispense-001");
        }

        @Test
        @DisplayName("Should return error when dispense record not found by ID")
        void shouldReturnErrorWhenDispenseRecordNotFoundById() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.getDispenseRecordById("non-existent");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("发药记录不存在"));

            verify(dispenseRecordRepository).findById("non-existent");
            verify(dispenseDetailRepository, never()).findByDispenseId(anyString());
        }

        @Test
        @DisplayName("Should get dispense record by dispense number successfully")
        void shouldGetDispenseRecordByNoSuccessfully() {
            // Arrange
            when(dispenseRecordRepository.findByDispenseNo(anyString()))
                    .thenReturn(Optional.of(testRecord));
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.getDispenseRecordByNo("DN20240101001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("DN20240101001", result.getData().getDispenseNo());

            verify(dispenseRecordRepository).findByDispenseNo("DN20240101001");
        }

        @Test
        @DisplayName("Should return error when dispense record not found by dispense number")
        void shouldReturnErrorWhenDispenseRecordNotFoundByNo() {
            // Arrange
            when(dispenseRecordRepository.findByDispenseNo(anyString())).thenReturn(Optional.empty());

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.getDispenseRecordByNo("INVALID");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("发药记录不存在"));

            verify(dispenseRecordRepository).findByDispenseNo("INVALID");
        }
    }

    @Nested
    @DisplayName("Query Dispense Records Tests")
    class QueryDispenseRecordsTests {

        @Test
        @DisplayName("Should query dispense records with pagination successfully")
        void shouldQueryDispenseRecordsSuccessfully() {
            // Arrange
            List<DispenseRecord> records = Arrays.asList(testRecord);
            Page<DispenseRecord> page = new PageImpl<>(records);
            when(dispenseRecordRepository.queryRecords(anyString(), anyString(), anyString(),
                    any(AuditStatus.class), any(DispenseStatus.class), any(LocalDateTime.class),
                    any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));

            // Act
            Result<PageResult<DispenseRecordVO>> result = dispenseRecordService.queryDispenseRecords(queryDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getTotal());
            assertEquals(1, result.getData().getList().size());

            verify(dispenseRecordRepository).queryRecords(anyString(), anyString(), anyString(),
                    any(AuditStatus.class), any(DispenseStatus.class), any(LocalDateTime.class),
                    any(LocalDateTime.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when no records found")
        void shouldReturnEmptyPageWhenNoRecordsFound() {
            // Arrange
            Page<DispenseRecord> emptyPage = new PageImpl<>(Collections.emptyList());
            when(dispenseRecordRepository.queryRecords(anyString(), anyString(), anyString(),
                    any(AuditStatus.class), any(DispenseStatus.class), any(LocalDateTime.class),
                    any(LocalDateTime.class), any(Pageable.class))).thenReturn(emptyPage);

            // Act
            Result<PageResult<DispenseRecordVO>> result = dispenseRecordService.queryDispenseRecords(queryDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(0, result.getData().getTotal());
            assertTrue(result.getData().getList().isEmpty());
        }
    }

    @Nested
    @DisplayName("Pending Dispense Tests")
    class PendingDispenseTests {

        @Test
        @DisplayName("Should get pending dispense by prescription successfully")
        void shouldGetPendingDispenseByPrescriptionSuccessfully() {
            // Arrange
            when(dispenseRecordRepository.findByPrescriptionIdAndDispenseStatus(anyString(),
                    any(DispenseStatus.class))).thenReturn(Optional.of(testRecord));
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.getPendingDispenseByPrescription("prescription-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(dispenseRecordRepository).findByPrescriptionIdAndDispenseStatus("prescription-001",
                    DispenseStatus.PENDING);
        }

        @Test
        @DisplayName("Should return error when no pending dispense found")
        void shouldReturnErrorWhenNoPendingDispenseFound() {
            // Arrange
            when(dispenseRecordRepository.findByPrescriptionIdAndDispenseStatus(anyString(),
                    any(DispenseStatus.class))).thenReturn(Optional.empty());

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.getPendingDispenseByPrescription("prescription-001");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("未找到待发药记录"));
        }

        @Test
        @DisplayName("Should get pending audit records successfully")
        void shouldGetPendingAuditRecordsSuccessfully() {
            // Arrange
            when(dispenseRecordRepository.findByPharmacyIdAndAuditStatus(anyString(),
                    any(AuditStatus.class))).thenReturn(Arrays.asList(testRecord));
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));

            // Act
            Result<List<DispenseRecordVO>> result = dispenseRecordService.getPendingAuditRecords("pharmacy-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("Should get pending dispense records successfully")
        void shouldGetPendingDispenseRecordsSuccessfully() {
            // Arrange
            testRecord.setAuditStatus(AuditStatus.APPROVED);
            when(dispenseRecordRepository.findByPharmacyIdAndAuditStatusAndDispenseStatus(
                    anyString(), any(AuditStatus.class), any(DispenseStatus.class)))
                    .thenReturn(Arrays.asList(testRecord));
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));

            // Act
            Result<List<DispenseRecordVO>> result = dispenseRecordService.getPendingDispenseRecords("pharmacy-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
        }
    }

    @Nested
    @DisplayName("Audit Prescription Tests")
    class AuditPrescriptionTests {

        @Test
        @DisplayName("Should audit prescription successfully with approval")
        void shouldAuditPrescriptionSuccessfullyWithApproval() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(dispenseRecordRepository.save(any(DispenseRecord.class))).thenReturn(testRecord);
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.auditPrescription("dispense-001", auditDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(dispenseRecordRepository).findById("dispense-001");
            verify(dispenseRecordRepository).save(any(DispenseRecord.class));
        }

        @Test
        @DisplayName("Should audit prescription successfully with rejection")
        void shouldAuditPrescriptionSuccessfullyWithRejection() {
            // Arrange
            auditDTO.setAuditResult("不通过");
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(dispenseRecordRepository.save(any(DispenseRecord.class))).thenReturn(testRecord);
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.auditPrescription("dispense-001", auditDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(dispenseRecordRepository).save(any(DispenseRecord.class));
        }

        @Test
        @DisplayName("Should return error when dispense record not found for audit")
        void shouldReturnErrorWhenDispenseRecordNotFoundForAudit() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.auditPrescription("non-existent", auditDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("发药记录不存在"));
        }

        @Test
        @DisplayName("Should return error when audit status is not pending")
        void shouldReturnErrorWhenAuditStatusIsNotPending() {
            // Arrange
            testRecord.setAuditStatus(AuditStatus.APPROVED);
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.auditPrescription("dispense-001", auditDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("只有待审核状态的记录可以审核"));
        }

        @Test
        @DisplayName("Should audit details when provided")
        void shouldAuditDetailsWhenProvided() {
            // Arrange
            PrescriptionAuditDTO.DetailAuditDTO detailAudit = new PrescriptionAuditDTO.DetailAuditDTO();
            detailAudit.setDetailId("detail-001");
            detailAudit.setAuditResult("Approved");
            auditDTO.setDetails(Arrays.asList(detailAudit));

            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(dispenseRecordRepository.save(any(DispenseRecord.class))).thenReturn(testRecord);
            when(dispenseDetailRepository.findById(anyString())).thenReturn(Optional.of(testDetail));
            when(dispenseDetailRepository.save(any(DispenseDetail.class))).thenReturn(testDetail);
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.auditPrescription("dispense-001", auditDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            verify(dispenseDetailRepository).findById("detail-001");
            verify(dispenseDetailRepository).save(any(DispenseDetail.class));
        }
    }

    @Nested
    @DisplayName("Confirm Dispense Tests")
    class ConfirmDispenseTests {

        @Test
        @DisplayName("Should confirm dispense successfully")
        void shouldConfirmDispenseSuccessfully() {
            // Arrange
            testRecord.setAuditStatus(AuditStatus.APPROVED);
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(dispenseRecordRepository.save(any(DispenseRecord.class))).thenReturn(testRecord);
            when(dispenseDetailRepository.findById(anyString())).thenReturn(Optional.of(testDetail));
            when(dispenseDetailRepository.save(any(DispenseDetail.class))).thenReturn(testDetail);
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));
            when(drugInventoryService.outbound(anyString(), any(BigDecimal.class),
                    anyString(), anyString())).thenReturn(Result.success(null));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.confirmDispense(confirmDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(dispenseRecordRepository).save(any(DispenseRecord.class));
            verify(drugInventoryService).outbound(anyString(), any(BigDecimal.class),
                    anyString(), anyString());
        }

        @Test
        @DisplayName("Should return error when audit status is not approved for dispense")
        void shouldReturnErrorWhenAuditStatusNotApproved() {
            // Arrange - audit status is PENDING, not APPROVED
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.confirmDispense(confirmDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("只有审核通过的记录可以发药"));
        }

        @Test
        @DisplayName("Should return error when dispense status is not pending")
        void shouldReturnErrorWhenDispenseStatusNotPending() {
            // Arrange
            testRecord.setAuditStatus(AuditStatus.APPROVED);
            testRecord.setDispenseStatus(DispenseStatus.DISPENSED);
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.confirmDispense(confirmDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("只有待发药状态的记录可以确认发药"));
        }

        @Test
        @DisplayName("Should return error when dispense record not found for confirmation")
        void shouldReturnErrorWhenDispenseRecordNotFoundForConfirmation() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.confirmDispense(confirmDTO);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("发药记录不存在"));
        }
    }

    @Nested
    @DisplayName("Cancel Dispense Tests")
    class CancelDispenseTests {

        @Test
        @DisplayName("Should cancel dispense successfully")
        void shouldCancelDispenseSuccessfully() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(dispenseRecordRepository.save(any(DispenseRecord.class))).thenReturn(testRecord);

            // Act
            Result<Void> result = dispenseRecordService.cancelDispense("dispense-001", "Patient request");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(dispenseRecordRepository).findById("dispense-001");
            verify(dispenseRecordRepository).save(any(DispenseRecord.class));
        }

        @Test
        @DisplayName("Should return error when dispense record not found for cancellation")
        void shouldReturnErrorWhenDispenseRecordNotFoundForCancellation() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<Void> result = dispenseRecordService.cancelDispense("non-existent", "Patient request");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("发药记录不存在"));
        }

        @Test
        @DisplayName("Should return error when trying to cancel dispensed record")
        void shouldReturnErrorWhenTryingToCancelDispensedRecord() {
            // Arrange
            testRecord.setDispenseStatus(DispenseStatus.DISPENSED);
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // Act
            Result<Void> result = dispenseRecordService.cancelDispense("dispense-001", "Patient request");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("已发药或已退药的记录不能取消"));
        }

        @Test
        @DisplayName("Should return error when trying to cancel returned record")
        void shouldReturnErrorWhenTryingToCancelReturnedRecord() {
            // Arrange
            testRecord.setDispenseStatus(DispenseStatus.RETURNED);
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // Act
            Result<Void> result = dispenseRecordService.cancelDispense("dispense-001", "Patient request");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("已发药或已退药的记录不能取消"));
        }
    }

    @Nested
    @DisplayName("Drug Return Tests")
    class DrugReturnTests {

        @Test
        @DisplayName("Should process drug return successfully")
        void shouldProcessDrugReturnSuccessfully() {
            // Arrange
            testRecord.setDispenseStatus(DispenseStatus.DISPENSED);
            testDetail.setBatchNo("BATCH001");
            testDetail.setQuantity(new BigDecimal("10"));

            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(dispenseRecordRepository.save(any(DispenseRecord.class))).thenReturn(testRecord);
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));
            when(drugInventoryService.inbound(any())).thenReturn(Result.success(null));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.processDrugReturn(
                    "dispense-001", "Patient request return", "operator-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(dispenseRecordRepository).save(any(DispenseRecord.class));
            verify(drugInventoryService).inbound(any());
        }

        @Test
        @DisplayName("Should return error when dispense record not found for return")
        void shouldReturnErrorWhenDispenseRecordNotFoundForReturn() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.processDrugReturn(
                    "non-existent", "Patient request", "operator-001");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("发药记录不存在"));
        }

        @Test
        @DisplayName("Should return error when dispense status is not dispensed for return")
        void shouldReturnErrorWhenDispenseStatusNotDispensedForReturn() {
            // Arrange
            testRecord.setDispenseStatus(DispenseStatus.PENDING);
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.processDrugReturn(
                    "dispense-001", "Patient request", "operator-001");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("只有已发药的记录可以退药"));
        }

        @Test
        @DisplayName("Should skip return when batch number is null")
        void shouldSkipReturnWhenBatchNumberIsNull() {
            // Arrange
            testRecord.setDispenseStatus(DispenseStatus.DISPENSED);
            testDetail.setBatchNo(null);

            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(dispenseRecordRepository.save(any(DispenseRecord.class))).thenReturn(testRecord);
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));

            // Act
            Result<DispenseRecordVO> result = dispenseRecordService.processDrugReturn(
                    "dispense-001", "Patient request", "operator-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            verify(drugInventoryService, never()).inbound(any());
        }
    }

    @Nested
    @DisplayName("Patient Dispense Records Tests")
    class PatientDispenseRecordsTests {

        @Test
        @DisplayName("Should get patient dispense records successfully")
        void shouldGetPatientDispenseRecordsSuccessfully() {
            // Arrange
            when(dispenseRecordRepository.findByPatientIdOrderByCreateTimeDesc(anyString()))
                    .thenReturn(Arrays.asList(testRecord));
            when(dispenseDetailRepository.findByDispenseId(anyString()))
                    .thenReturn(Arrays.asList(testDetail));

            // Act
            Result<List<DispenseRecordVO>> result = dispenseRecordService.getPatientDispenseRecords("patient-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            verify(dispenseRecordRepository).findByPatientIdOrderByCreateTimeDesc("patient-001");
        }

        @Test
        @DisplayName("Should return empty list when patient has no dispense records")
        void shouldReturnEmptyListWhenPatientHasNoDispenseRecords() {
            // Arrange
            when(dispenseRecordRepository.findByPatientIdOrderByCreateTimeDesc(anyString()))
                    .thenReturn(Collections.emptyList());

            // Act
            Result<List<DispenseRecordVO>> result = dispenseRecordService.getPatientDispenseRecords("patient-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("Update Status Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update audit status successfully")
        void shouldUpdateAuditStatusSuccessfully() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(dispenseRecordRepository.save(any(DispenseRecord.class))).thenReturn(testRecord);

            // Act
            Result<Void> result = dispenseRecordService.updateAuditStatus("dispense-001", AuditStatus.APPROVED);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(dispenseRecordRepository).findById("dispense-001");
            verify(dispenseRecordRepository).save(any(DispenseRecord.class));
        }

        @Test
        @DisplayName("Should update dispense status successfully")
        void shouldUpdateDispenseStatusSuccessfully() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(dispenseRecordRepository.save(any(DispenseRecord.class))).thenReturn(testRecord);

            // Act
            Result<Void> result = dispenseRecordService.updateDispenseStatus("dispense-001", DispenseStatus.DISPENSED);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(dispenseRecordRepository).findById("dispense-001");
            verify(dispenseRecordRepository).save(any(DispenseRecord.class));
        }

        @Test
        @DisplayName("Should return error when record not found for status update")
        void shouldReturnErrorWhenRecordNotFoundForStatusUpdate() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<Void> result = dispenseRecordService.updateAuditStatus("non-existent", AuditStatus.APPROVED);

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("发药记录不存在"));
        }
    }

    @Nested
    @DisplayName("Confirm Receive Tests")
    class ConfirmReceiveTests {

        @Test
        @DisplayName("Should confirm receive successfully")
        void shouldConfirmReceiveSuccessfully() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(dispenseRecordRepository.save(any(DispenseRecord.class))).thenReturn(testRecord);

            // Act
            Result<Void> result = dispenseRecordService.confirmReceive("dispense-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(dispenseRecordRepository).findById("dispense-001");
            verify(dispenseRecordRepository).save(any(DispenseRecord.class));
        }

        @Test
        @DisplayName("Should return error when record not found for receive confirmation")
        void shouldReturnErrorWhenRecordNotFoundForReceiveConfirmation() {
            // Arrange
            when(dispenseRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            Result<Void> result = dispenseRecordService.confirmReceive("non-existent");

            // Assert
            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("发药记录不存在"));
        }
    }
}
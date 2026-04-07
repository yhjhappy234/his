package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.dto.ProgressRecordSaveDTO;
import com.yhj.his.module.emr.entity.ProgressRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.ProgressRecordType;
import com.yhj.his.module.emr.repository.ProgressRecordRepository;
import com.yhj.his.module.emr.service.impl.ProgressRecordServiceImpl;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 病程记录服务单元测试
 * 覆盖病程记录管理核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProgressRecordService单元测试")
class ProgressRecordServiceTest {

    @Mock
    private ProgressRecordRepository recordRepository;

    @InjectMocks
    private ProgressRecordServiceImpl progressRecordService;

    private ProgressRecord testRecord;
    private ProgressRecordSaveDTO testSaveDTO;

    @BeforeEach
    void setUp() {
        testRecord = createTestRecord();
        testSaveDTO = createTestSaveDTO();
    }

    @Nested
    @DisplayName("病程记录创建测试")
    class CreateRecordTests {

        @Test
        @DisplayName("成功创建病程记录")
        void createRecord_success() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> {
                ProgressRecord saved = invocation.getArgument(0);
                saved.setId("PROG001");
                return saved;
            });

            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertNotNull(result);
            assertEquals("ADMSSION001", result.getAdmissionId());
            assertEquals("PATIENT001", result.getPatientId());
            assertEquals("张三", result.getPatientName());
            assertEquals(ProgressRecordType.DAILY_PROGRESS, result.getRecordType());
            assertEquals(EmrStatus.DRAFT, result.getStatus());
            assertNotNull(result.getRecordTime());
            assertEquals("患者今日病情稳定，体温正常，咳嗽减轻，继续抗感染治疗", result.getRecordContent());

            verify(recordRepository).save(any(ProgressRecord.class));
        }

        @Test
        @DisplayName("创建病程记录 - 包含医生信息")
        void createRecord_withDoctorInfo() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals("DOC001", result.getDoctorId());
            assertEquals("李医生", result.getDoctorName());
            assertEquals("主治医师", result.getDoctorTitle());
        }

        @Test
        @DisplayName("创建病程记录 - 包含记录标题")
        void createRecord_withTitle() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals("日常病程记录", result.getRecordTitle());
        }

        @Test
        @DisplayName("创建病程记录 - 包含记录日期")
        void createRecord_withRecordDate() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals(LocalDate.now(), result.getRecordDate());
        }
    }

    @Nested
    @DisplayName("首次病程记录测试")
    class FirstProgressRecordTests {

        @Test
        @DisplayName("成功创建首次病程记录")
        void createFirstProgressRecord_success() {
            when(recordRepository.findFirstByAdmissionIdAndRecordTypeAndDeletedFalse(
                    "ADMSSION001", ProgressRecordType.FIRST_PROGRESS)).thenReturn(Optional.empty());
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> {
                ProgressRecord saved = invocation.getArgument(0);
                saved.setId("PROG001");
                return saved;
            });

            ProgressRecordSaveDTO dto = new ProgressRecordSaveDTO();
            dto.setAdmissionId("ADMSSION001");
            dto.setPatientId("PATIENT001");
            dto.setPatientName("张三");
            dto.setRecordDate(LocalDate.now());
            dto.setRecordContent("首次病程记录内容");
            dto.setDoctorId("DOC001");
            dto.setDoctorName("李医生");

            ProgressRecord result = progressRecordService.createFirstProgressRecord(dto);

            assertNotNull(result);
            assertEquals(ProgressRecordType.FIRST_PROGRESS, result.getRecordType());
            assertEquals("首次病程记录", result.getRecordTitle());
        }

        @Test
        @DisplayName("创建首次病程记录 - 已存在抛出异常")
        void createFirstProgressRecord_alreadyExists_throwException() {
            ProgressRecord existingRecord = new ProgressRecord();
            existingRecord.setId("EXISTING_PROG");
            existingRecord.setRecordType(ProgressRecordType.FIRST_PROGRESS);

            when(recordRepository.findFirstByAdmissionIdAndRecordTypeAndDeletedFalse(
                    "ADMSSION001", ProgressRecordType.FIRST_PROGRESS)).thenReturn(Optional.of(existingRecord));

            ProgressRecordSaveDTO dto = new ProgressRecordSaveDTO();
            dto.setAdmissionId("ADMSSION001");
            dto.setRecordContent("首次病程记录内容");
            dto.setRecordDate(LocalDate.now());
            dto.setDoctorId("DOC001");

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> progressRecordService.createFirstProgressRecord(dto));

            assertEquals("该住院记录已存在首次病程记录", exception.getMessage());
            verify(recordRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("上级医师查房记录测试")
    class ChiefRoundRecordTests {

        @Test
        @DisplayName("成功创建上级医师查房记录")
        void createChiefRoundRecord_success() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> {
                ProgressRecord saved = invocation.getArgument(0);
                saved.setId("PROG001");
                return saved;
            });

            ProgressRecordSaveDTO dto = new ProgressRecordSaveDTO();
            dto.setAdmissionId("ADMSSION001");
            dto.setPatientId("PATIENT001");
            dto.setPatientName("张三");
            dto.setRecordDate(LocalDate.now());
            dto.setRecordContent("上级医师查房意见");
            dto.setDoctorId("DOC001");
            dto.setDoctorName("李医生");
            dto.setDoctorTitle("主任医师");

            ProgressRecord result = progressRecordService.createChiefRoundRecord(dto);

            assertNotNull(result);
            assertEquals(ProgressRecordType.CHIEF_ROUND, result.getRecordType());
            assertEquals("上级医师查房记录", result.getRecordTitle());
        }

        @Test
        @DisplayName("创建上级医师查房记录 - 可创建多条")
        void createChiefRoundRecord_multipleAllowed() {
            ProgressRecord existingRecord = new ProgressRecord();
            existingRecord.setId("EXISTING_PROG");
            existingRecord.setRecordType(ProgressRecordType.CHIEF_ROUND);

            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ProgressRecordSaveDTO dto = new ProgressRecordSaveDTO();
            dto.setAdmissionId("ADMSSION001");
            dto.setRecordContent("第二次上级医师查房");
            dto.setRecordDate(LocalDate.now());
            dto.setDoctorId("DOC001");

            ProgressRecord result = progressRecordService.createChiefRoundRecord(dto);

            assertNotNull(result);
            assertEquals(ProgressRecordType.CHIEF_ROUND, result.getRecordType());
        }
    }

    @Nested
    @DisplayName("病程记录更新测试")
    class UpdateRecordTests {

        @Test
        @DisplayName("成功更新草稿状态病程记录")
        void updateRecord_draft_success() {
            testRecord.setStatus(EmrStatus.DRAFT);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(ProgressRecord.class))).thenReturn(testRecord);

            ProgressRecordSaveDTO updateDTO = new ProgressRecordSaveDTO();
            updateDTO.setAdmissionId("ADMSSION001");
            updateDTO.setPatientId("PATIENT001");
            updateDTO.setRecordType(ProgressRecordType.DAILY_PROGRESS);
            updateDTO.setRecordDate(LocalDate.now());
            updateDTO.setRecordContent("更新后的病程记录内容");
            updateDTO.setDoctorId("DOC001");

            ProgressRecord result = progressRecordService.updateRecord("PROG001", updateDTO);

            assertEquals("更新后的病程记录内容", result.getRecordContent());
            verify(recordRepository).save(testRecord);
        }

        @Test
        @DisplayName("成功更新退回状态病程记录")
        void updateRecord_rejected_success() {
            testRecord.setStatus(EmrStatus.REJECTED);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(ProgressRecord.class))).thenReturn(testRecord);

            ProgressRecord result = progressRecordService.updateRecord("PROG001", testSaveDTO);

            assertNotNull(result);
            verify(recordRepository).save(testRecord);
        }

        @Test
        @DisplayName("更新已提交状态病程记录抛出异常")
        void updateRecord_submitted_throwException() {
            testRecord.setStatus(EmrStatus.SUBMITTED);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> progressRecordService.updateRecord("PROG001", testSaveDTO));

            assertEquals("只有草稿或退回状态的病程记录可以修改", exception.getMessage());
            verify(recordRepository, never()).save(any());
        }

        @Test
        @DisplayName("更新已审核状态病程记录抛出异常")
        void updateRecord_audited_throwException() {
            testRecord.setStatus(EmrStatus.AUDITED);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> progressRecordService.updateRecord("PROG001", testSaveDTO));

            assertEquals("只有草稿或退回状态的病程记录可以修改", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("病程记录删除测试")
    class DeleteRecordTests {

        @Test
        @DisplayName("成功删除草稿状态病程记录")
        void deleteRecord_draft_success() {
            testRecord.setStatus(EmrStatus.DRAFT);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(ProgressRecord.class))).thenReturn(testRecord);

            progressRecordService.deleteRecord("PROG001");

            assertTrue(testRecord.getDeleted());
            verify(recordRepository).save(testRecord);
        }

        @Test
        @DisplayName("删除已提交病程记录抛出异常")
        void deleteRecord_submitted_throwException() {
            testRecord.setStatus(EmrStatus.SUBMITTED);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> progressRecordService.deleteRecord("PROG001"));

            assertEquals("只有草稿状态的病程记录可以删除", exception.getMessage());
            verify(recordRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("病程记录查询测试")
    class QueryRecordTests {

        @Test
        @DisplayName("根据ID获取病程记录成功")
        void getRecordById_success() {
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));

            ProgressRecord result = progressRecordService.getRecordById("PROG001");

            assertNotNull(result);
            assertEquals("张三", result.getPatientName());
        }

        @Test
        @DisplayName("根据ID获取病程记录 - 不存在抛出异常")
        void getRecordById_notFound_throwException() {
            when(recordRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> progressRecordService.getRecordById("NONEXISTENT"));

            assertEquals("病程记录不存在: NONEXISTENT", exception.getMessage());
        }

        @Test
        @DisplayName("根据住院ID查询病程记录列表")
        void getRecordsByAdmissionId_success() {
            List<ProgressRecord> records = List.of(testRecord);
            when(recordRepository.findByAdmissionIdAndDeletedFalseOrderByRecordDateDescRecordTimeDesc("ADMSSION001"))
                    .thenReturn(records);

            List<ProgressRecord> result = progressRecordService.getRecordsByAdmissionId("ADMSSION001");

            assertEquals(1, result.size());
            assertEquals("ADMSSION001", result.get(0).getAdmissionId());
        }

        @Test
        @DisplayName("根据住院ID和记录类型查询")
        void getRecordsByAdmissionIdAndType_success() {
            List<ProgressRecord> records = List.of(testRecord);
            when(recordRepository.findByAdmissionIdAndRecordTypeAndDeletedFalseOrderByRecordDateDesc(
                    "ADMSSION001", ProgressRecordType.DAILY_PROGRESS)).thenReturn(records);

            List<ProgressRecord> result = progressRecordService.getRecordsByAdmissionIdAndType(
                    "ADMSSION001", ProgressRecordType.DAILY_PROGRESS);

            assertEquals(1, result.size());
            assertEquals(ProgressRecordType.DAILY_PROGRESS, result.get(0).getRecordType());
        }

        @Test
        @DisplayName("根据住院ID和日期查询")
        void getRecordsByAdmissionIdAndDate_success() {
            LocalDate recordDate = LocalDate.now();
            List<ProgressRecord> records = List.of(testRecord);
            when(recordRepository.findByAdmissionIdAndRecordDateAndDeletedFalse("ADMSSION001", recordDate))
                    .thenReturn(records);

            List<ProgressRecord> result = progressRecordService.getRecordsByAdmissionIdAndDate("ADMSSION001", recordDate);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("根据住院ID和日期范围查询")
        void getRecordsByAdmissionIdAndDateRange_success() {
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();
            List<ProgressRecord> records = List.of(testRecord);
            when(recordRepository.findByAdmissionIdAndDateRange("ADMSSION001", startDate, endDate))
                    .thenReturn(records);

            List<ProgressRecord> result = progressRecordService.getRecordsByAdmissionIdAndDateRange(
                    "ADMSSION001", startDate, endDate);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("分页查询病程记录列表")
        void listRecords_success() {
            Pageable pageable = PageRequest.of(0, 10);
            List<ProgressRecord> records = Arrays.asList(testRecord, createAnotherRecord());
            Page<ProgressRecord> page = new PageImpl<>(records);

            when(recordRepository.findAll(pageable)).thenReturn(page);

            Page<ProgressRecord> result = progressRecordService.listRecords(pageable);

            assertEquals(2, result.getContent().size());
        }

        @Test
        @DisplayName("分页查询 - 过滤已删除记录")
        void listRecords_filterDeleted() {
            ProgressRecord deletedRecord = createAnotherRecord();
            deletedRecord.setDeleted(true);

            Pageable pageable = PageRequest.of(0, 10);
            List<ProgressRecord> records = Arrays.asList(testRecord, deletedRecord);
            Page<ProgressRecord> page = new PageImpl<>(records);

            when(recordRepository.findAll(pageable)).thenReturn(page);

            Page<ProgressRecord> result = progressRecordService.listRecords(pageable);

            assertEquals(1, result.getContent().size());
            assertFalse(result.getContent().get(0).getDeleted());
        }

        @Test
        @DisplayName("根据患者ID分页查询")
        void getRecordsByPatientId_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProgressRecord> page = new PageImpl<>(List.of(testRecord));

            when(recordRepository.findByPatientIdAndDeletedFalse("PATIENT001", pageable)).thenReturn(page);

            Page<ProgressRecord> result = progressRecordService.getRecordsByPatientId("PATIENT001", pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("根据医生ID分页查询")
        void getRecordsByDoctorId_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProgressRecord> page = new PageImpl<>(List.of(testRecord));

            when(recordRepository.findByDoctorIdAndDeletedFalse("DOC001", pageable)).thenReturn(page);

            Page<ProgressRecord> result = progressRecordService.getRecordsByDoctorId("DOC001", pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("查询首次病程记录")
        void getFirstProgressRecord_success() {
            ProgressRecord firstRecord = new ProgressRecord();
            firstRecord.setId("FIRST_PROG");
            firstRecord.setRecordType(ProgressRecordType.FIRST_PROGRESS);
            firstRecord.setRecordTitle("首次病程记录");

            when(recordRepository.findFirstByAdmissionIdAndRecordTypeAndDeletedFalse(
                    "ADMSSION001", ProgressRecordType.FIRST_PROGRESS)).thenReturn(Optional.of(firstRecord));

            Optional<ProgressRecord> result = progressRecordService.getFirstProgressRecord("ADMSSION001");

            assertTrue(result.isPresent());
            assertEquals(ProgressRecordType.FIRST_PROGRESS, result.get().getRecordType());
        }

        @Test
        @DisplayName("查询首次病程记录 - 不存在")
        void getFirstProgressRecord_notFound() {
            when(recordRepository.findFirstByAdmissionIdAndRecordTypeAndDeletedFalse(
                    "ADMSSION001", ProgressRecordType.FIRST_PROGRESS)).thenReturn(Optional.empty());

            Optional<ProgressRecord> result = progressRecordService.getFirstProgressRecord("ADMSSION001");

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("根据状态分页查询")
        void getRecordsByStatus_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProgressRecord> page = new PageImpl<>(List.of(testRecord));

            when(recordRepository.findByStatusAndDeletedFalse(EmrStatus.DRAFT, pageable)).thenReturn(page);

            Page<ProgressRecord> result = progressRecordService.getRecordsByStatus(EmrStatus.DRAFT, pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("统计住院期间病程记录数")
        void countByAdmissionId_success() {
            when(recordRepository.countByAdmissionId("ADMSSION001")).thenReturn(5L);

            Long count = progressRecordService.countByAdmissionId("ADMSSION001");

            assertEquals(5L, count);
        }
    }

    @Nested
    @DisplayName("病程记录提交与审核测试")
    class SubmitAndAuditTests {

        @Test
        @DisplayName("成功提交草稿病程记录")
        void submitRecord_draft_success() {
            testRecord.setStatus(EmrStatus.DRAFT);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(ProgressRecord.class))).thenReturn(testRecord);

            EmrSubmitDTO submitDTO = new EmrSubmitDTO();
            submitDTO.setRecordId("PROG001");
            submitDTO.setRecordType("病程记录");

            ProgressRecord result = progressRecordService.submitRecord(submitDTO);

            assertEquals(EmrStatus.SUBMITTED, result.getStatus());
            verify(recordRepository).save(testRecord);
        }

        @Test
        @DisplayName("成功提交退回病程记录")
        void submitRecord_rejected_success() {
            testRecord.setStatus(EmrStatus.REJECTED);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(ProgressRecord.class))).thenReturn(testRecord);

            EmrSubmitDTO submitDTO = new EmrSubmitDTO();
            submitDTO.setRecordId("PROG001");
            submitDTO.setRecordType("病程记录");

            ProgressRecord result = progressRecordService.submitRecord(submitDTO);

            assertEquals(EmrStatus.SUBMITTED, result.getStatus());
        }

        @Test
        @DisplayName("提交已审核病程记录抛出异常")
        void submitRecord_audited_throwException() {
            testRecord.setStatus(EmrStatus.AUDITED);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));

            EmrSubmitDTO submitDTO = new EmrSubmitDTO();
            submitDTO.setRecordId("PROG001");
            submitDTO.setRecordType("病程记录");

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> progressRecordService.submitRecord(submitDTO));

            assertEquals("只有草稿或退回状态的病程记录可以提交", exception.getMessage());
        }

        @Test
        @DisplayName("审核通过病程记录")
        void auditRecord_approve_success() {
            testRecord.setStatus(EmrStatus.SUBMITTED);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(ProgressRecord.class))).thenReturn(testRecord);

            ProgressRecord result = progressRecordService.auditRecord("PROG001", true, "REV001", "审核医生");

            assertEquals(EmrStatus.AUDITED, result.getStatus());
            assertEquals("REV001", result.getReviewerId());
            assertEquals("审核医生", result.getReviewerName());
            assertNotNull(result.getReviewTime());
        }

        @Test
        @DisplayName("审核拒绝病程记录")
        void auditRecord_reject_success() {
            testRecord.setStatus(EmrStatus.SUBMITTED);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(ProgressRecord.class))).thenReturn(testRecord);

            ProgressRecord result = progressRecordService.auditRecord("PROG001", false, "REV001", "审核医生");

            assertEquals(EmrStatus.REJECTED, result.getStatus());
        }

        @Test
        @DisplayName("审核草稿病程记录抛出异常")
        void auditRecord_draft_throwException() {
            testRecord.setStatus(EmrStatus.DRAFT);
            when(recordRepository.findById("PROG001")).thenReturn(Optional.of(testRecord));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> progressRecordService.auditRecord("PROG001", true, "REV001", "审核医生"));

            assertEquals("只有已提交状态的病程记录可以审核", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("病程记录类型测试")
    class RecordTypeTests {

        @Test
        @DisplayName("创建日常病程记录")
        void createDailyProgressRecord() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            testSaveDTO.setRecordType(ProgressRecordType.DAILY_PROGRESS);
            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals(ProgressRecordType.DAILY_PROGRESS, result.getRecordType());
        }

        @Test
        @DisplayName("创建疑难病例讨论记录")
        void createDifficultCaseRecord() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            testSaveDTO.setRecordType(ProgressRecordType.DIFFICULT_CASE);
            testSaveDTO.setRecordTitle("疑难病例讨论记录");
            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals(ProgressRecordType.DIFFICULT_CASE, result.getRecordType());
        }

        @Test
        @DisplayName("创建会诊记录")
        void createConsultationRecord() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            testSaveDTO.setRecordType(ProgressRecordType.CONSULTATION);
            testSaveDTO.setConsultationId("CONS001");
            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals(ProgressRecordType.CONSULTATION, result.getRecordType());
            assertEquals("CONS001", result.getConsultationId());
        }

        @Test
        @DisplayName("创建术前讨论记录")
        void createPreOperationRecord() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            testSaveDTO.setRecordType(ProgressRecordType.PRE_OPERATION);
            testSaveDTO.setOperationId("OP001");
            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals(ProgressRecordType.PRE_OPERATION, result.getRecordType());
            assertEquals("OP001", result.getOperationId());
        }

        @Test
        @DisplayName("创建术后病程记录")
        void createPostOperationRecord() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            testSaveDTO.setRecordType(ProgressRecordType.POST_OPERATION);
            testSaveDTO.setOperationId("OP001");
            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals(ProgressRecordType.POST_OPERATION, result.getRecordType());
        }

        @Test
        @DisplayName("创建交接班记录")
        void createHandoverRecord() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            testSaveDTO.setRecordType(ProgressRecordType.HANDOVER);
            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals(ProgressRecordType.HANDOVER, result.getRecordType());
        }

        @Test
        @DisplayName("创建转科记录")
        void createTransferRecord() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            testSaveDTO.setRecordType(ProgressRecordType.TRANSFER);
            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals(ProgressRecordType.TRANSFER, result.getRecordType());
        }
    }

    @Nested
    @DisplayName("关联信息测试")
    class AssociationTests {

        @Test
        @DisplayName("创建病程记录 - 关联手术ID")
        void createRecord_withOperationId() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            testSaveDTO.setOperationId("OP001");
            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals("OP001", result.getOperationId());
        }

        @Test
        @DisplayName("创建病程记录 - 关联会诊ID")
        void createRecord_withConsultationId() {
            when(recordRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            testSaveDTO.setConsultationId("CONS001");
            ProgressRecord result = progressRecordService.createRecord(testSaveDTO);

            assertEquals("CONS001", result.getConsultationId());
        }
    }

    // Helper methods
    private ProgressRecord createTestRecord() {
        ProgressRecord record = new ProgressRecord();
        record.setId("PROG001");
        record.setAdmissionId("ADMSSION001");
        record.setPatientId("PATIENT001");
        record.setPatientName("张三");
        record.setRecordType(ProgressRecordType.DAILY_PROGRESS);
        record.setRecordTitle("日常病程记录");
        record.setRecordDate(LocalDate.now());
        record.setRecordTime(LocalDateTime.now());
        record.setRecordContent("患者今日病情稳定，体温正常，咳嗽减轻");
        record.setDoctorId("DOC001");
        record.setDoctorName("李医生");
        record.setDoctorTitle("主治医师");
        record.setStatus(EmrStatus.DRAFT);
        record.setDeleted(false);
        return record;
    }

    private ProgressRecord createAnotherRecord() {
        ProgressRecord record = new ProgressRecord();
        record.setId("PROG002");
        record.setAdmissionId("ADMSSION001");
        record.setPatientId("PATIENT001");
        record.setPatientName("张三");
        record.setRecordType(ProgressRecordType.CHIEF_ROUND);
        record.setRecordTitle("上级医师查房记录");
        record.setRecordDate(LocalDate.now());
        record.setDoctorId("DOC002");
        record.setStatus(EmrStatus.SUBMITTED);
        record.setDeleted(false);
        return record;
    }

    private ProgressRecordSaveDTO createTestSaveDTO() {
        ProgressRecordSaveDTO dto = new ProgressRecordSaveDTO();
        dto.setAdmissionId("ADMSSION001");
        dto.setPatientId("PATIENT001");
        dto.setPatientName("张三");
        dto.setRecordType(ProgressRecordType.DAILY_PROGRESS);
        dto.setRecordTitle("日常病程记录");
        dto.setRecordDate(LocalDate.now());
        dto.setRecordContent("患者今日病情稳定，体温正常，咳嗽减轻，继续抗感染治疗");
        dto.setDoctorId("DOC001");
        dto.setDoctorName("李医生");
        dto.setDoctorTitle("主治医师");
        return dto;
    }
}
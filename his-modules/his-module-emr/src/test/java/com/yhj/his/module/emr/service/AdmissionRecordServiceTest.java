package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.AdmissionRecordSaveDTO;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.entity.AdmissionRecord;
import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.TemplateType;
import com.yhj.his.module.emr.repository.AdmissionRecordRepository;
import com.yhj.his.module.emr.repository.EmrTemplateRepository;
import com.yhj.his.module.emr.service.impl.AdmissionRecordServiceImpl;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 入院记录服务单元测试
 * 覆盖住院病历管理核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdmissionRecordService单元测试")
class AdmissionRecordServiceTest {

    @Mock
    private AdmissionRecordRepository recordRepository;

    @Mock
    private EmrTemplateRepository templateRepository;

    @Mock
    private EmrTemplateService templateService;

    @InjectMocks
    private AdmissionRecordServiceImpl admissionRecordService;

    private AdmissionRecord testRecord;
    private AdmissionRecordSaveDTO testSaveDTO;
    private EmrTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testRecord = createTestRecord();
        testSaveDTO = createTestSaveDTO();
        testTemplate = createTestTemplate();
    }

    @Nested
    @DisplayName("入院记录创建测试")
    class CreateRecordTests {

        @Test
        @DisplayName("成功创建入院记录")
        void createRecord_success() {
            when(recordRepository.save(any(AdmissionRecord.class))).thenAnswer(invocation -> {
                AdmissionRecord saved = invocation.getArgument(0);
                saved.setId("ADM001");
                return saved;
            });

            AdmissionRecord result = admissionRecordService.createRecord(testSaveDTO);

            assertNotNull(result);
            assertEquals("ADMSSION001", result.getAdmissionId());
            assertEquals("PATIENT001", result.getPatientId());
            assertEquals("王五", result.getPatientName());
            assertEquals(EmrStatus.DRAFT, result.getStatus());
            assertNotNull(result.getRecordTime());
            assertEquals("发热咳嗽一周", result.getChiefComplaint());
            assertEquals("患者一周前出现发热，体温最高38.5度，伴咳嗽咳痰，痰为黄脓痰，不易咳出", result.getPresentIllness());

            verify(recordRepository).save(any(AdmissionRecord.class));
        }

        @Test
        @DisplayName("创建入院记录 - 包含患者基本信息")
        void createRecord_withPatientInfo() {
            when(recordRepository.save(any(AdmissionRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            AdmissionRecord result = admissionRecordService.createRecord(testSaveDTO);

            assertEquals("男", result.getGender());
            assertEquals(45, result.getAge());
        }

        @Test
        @DisplayName("创建入院记录 - 包含住院信息")
        void createRecord_withAdmissionInfo() {
            when(recordRepository.save(any(AdmissionRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            AdmissionRecord result = admissionRecordService.createRecord(testSaveDTO);

            assertEquals("WARD001", result.getWardId());
            assertEquals("BED001", result.getBedNo());
        }

        @Test
        @DisplayName("创建入院记录 - 包含体格检查")
        void createRecord_withPhysicalExam() {
            when(recordRepository.save(any(AdmissionRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            AdmissionRecord result = admissionRecordService.createRecord(testSaveDTO);

            assertEquals(BigDecimal.valueOf(38.5), result.getTemperature());
            assertEquals(85, result.getPulse());
            assertEquals(20, result.getRespiration());
            assertEquals("130/85", result.getBloodPressure());
            assertEquals(BigDecimal.valueOf(70.0), result.getWeight());
            assertEquals(175, result.getHeight());
        }

        @Test
        @DisplayName("创建入院记录 - 包含医生信息")
        void createRecord_withDoctorInfo() {
            when(recordRepository.save(any(AdmissionRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            AdmissionRecord result = admissionRecordService.createRecord(testSaveDTO);

            assertEquals("DOC001", result.getDoctorId());
            assertEquals("主治医师张医生", result.getDoctorName());
            assertEquals("SUP_DOC001", result.getSuperiorDoctorId());
            assertEquals("主任医师王主任", result.getSuperiorDoctorName());
        }

        @Test
        @DisplayName("创建入院记录 - 包含诊断信息")
        void createRecord_withDiagnosis() {
            when(recordRepository.save(any(AdmissionRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            AdmissionRecord result = admissionRecordService.createRecord(testSaveDTO);

            assertEquals("J18.9", result.getAdmissionDiagnosisCode());
            assertEquals("肺炎", result.getAdmissionDiagnosisName());
            assertNotNull(result.getTreatmentPlan());
        }
    }

    @Nested
    @DisplayName("从模板创建入院记录测试")
    class CreateFromTemplateTests {

        @Test
        @DisplayName("成功从模板创建入院记录")
        void createFromTemplate_success() {
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            doNothing().when(templateService).incrementUseCount("TEMPLATE001");
            when(recordRepository.save(any(AdmissionRecord.class))).thenAnswer(invocation -> {
                AdmissionRecord saved = invocation.getArgument(0);
                saved.setId("ADM001");
                return saved;
            });

            AdmissionRecord result = admissionRecordService.createFromTemplate("TEMPLATE001", testSaveDTO);

            assertNotNull(result);
            verify(templateRepository).findById("TEMPLATE001");
            verify(templateService).incrementUseCount("TEMPLATE001");
            verify(recordRepository).save(any(AdmissionRecord.class));
        }

        @Test
        @DisplayName("从模板创建 - 模板不存在抛出异常")
        void createFromTemplate_notFound_throwException() {
            when(templateRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> admissionRecordService.createFromTemplate("NONEXISTENT", testSaveDTO));

            assertEquals("模板不存在: NONEXISTENT", exception.getMessage());
        }

        @Test
        @DisplayName("从模板创建 - 已删除模板抛出异常")
        void createFromTemplate_deleted_throwException() {
            testTemplate.setDeleted(true);
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> admissionRecordService.createFromTemplate("TEMPLATE001", testSaveDTO));

            assertEquals("模板不存在: TEMPLATE001", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("入院记录更新测试")
    class UpdateRecordTests {

        @Test
        @DisplayName("成功更新草稿状态入院记录")
        void updateRecord_draft_success() {
            testRecord.setStatus(EmrStatus.DRAFT);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(AdmissionRecord.class))).thenReturn(testRecord);

            AdmissionRecordSaveDTO updateDTO = new AdmissionRecordSaveDTO();
            updateDTO.setAdmissionId("ADMSSION001");
            updateDTO.setPatientId("PATIENT001");
            updateDTO.setDeptId("DEPT001");
            updateDTO.setDoctorId("DOC001");
            updateDTO.setChiefComplaint("更新后的主诉");
            updateDTO.setPresentIllness("更新后的现病史");

            AdmissionRecord result = admissionRecordService.updateRecord("ADM001", updateDTO);

            assertEquals("更新后的主诉", result.getChiefComplaint());
            verify(recordRepository).save(testRecord);
        }

        @Test
        @DisplayName("成功更新退回状态入院记录")
        void updateRecord_rejected_success() {
            testRecord.setStatus(EmrStatus.REJECTED);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(AdmissionRecord.class))).thenReturn(testRecord);

            AdmissionRecord result = admissionRecordService.updateRecord("ADM001", testSaveDTO);

            assertNotNull(result);
            verify(recordRepository).save(testRecord);
        }

        @Test
        @DisplayName("更新已提交状态入院记录抛出异常")
        void updateRecord_submitted_throwException() {
            testRecord.setStatus(EmrStatus.SUBMITTED);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> admissionRecordService.updateRecord("ADM001", testSaveDTO));

            assertEquals("只有草稿或退回状态的入院记录可以修改", exception.getMessage());
            verify(recordRepository, never()).save(any());
        }

        @Test
        @DisplayName("更新已审核状态入院记录抛出异常")
        void updateRecord_audited_throwException() {
            testRecord.setStatus(EmrStatus.AUDITED);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> admissionRecordService.updateRecord("ADM001", testSaveDTO));

            assertEquals("只有草稿或退回状态的入院记录可以修改", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("入院记录删除测试")
    class DeleteRecordTests {

        @Test
        @DisplayName("成功删除草稿状态入院记录")
        void deleteRecord_draft_success() {
            testRecord.setStatus(EmrStatus.DRAFT);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(AdmissionRecord.class))).thenReturn(testRecord);

            admissionRecordService.deleteRecord("ADM001");

            assertTrue(testRecord.getDeleted());
            verify(recordRepository).save(testRecord);
        }

        @Test
        @DisplayName("删除已提交入院记录抛出异常")
        void deleteRecord_submitted_throwException() {
            testRecord.setStatus(EmrStatus.SUBMITTED);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> admissionRecordService.deleteRecord("ADM001"));

            assertEquals("只有草稿状态的入院记录可以删除", exception.getMessage());
            verify(recordRepository, never()).save(any());
        }

        @Test
        @DisplayName("删除已审核入院记录抛出异常")
        void deleteRecord_audited_throwException() {
            testRecord.setStatus(EmrStatus.AUDITED);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> admissionRecordService.deleteRecord("ADM001"));

            assertEquals("只有草稿状态的入院记录可以删除", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("入院记录查询测试")
    class QueryRecordTests {

        @Test
        @DisplayName("根据ID获取入院记录成功")
        void getRecordById_success() {
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));

            AdmissionRecord result = admissionRecordService.getRecordById("ADM001");

            assertNotNull(result);
            assertEquals("王五", result.getPatientName());
        }

        @Test
        @DisplayName("根据ID获取入院记录 - 不存在抛出异常")
        void getRecordById_notFound_throwException() {
            when(recordRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> admissionRecordService.getRecordById("NONEXISTENT"));

            assertEquals("入院记录不存在: NONEXISTENT", exception.getMessage());
        }

        @Test
        @DisplayName("根据ID获取入院记录 - 已删除抛出异常")
        void getRecordById_deleted_throwException() {
            testRecord.setDeleted(true);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> admissionRecordService.getRecordById("ADM001"));

            assertEquals("入院记录不存在: ADM001", exception.getMessage());
        }

        @Test
        @DisplayName("根据住院ID获取入院记录")
        void getRecordByAdmissionId_success() {
            when(recordRepository.findByAdmissionIdAndDeletedFalse("ADMSSION001"))
                    .thenReturn(Optional.of(testRecord));

            Optional<AdmissionRecord> result = admissionRecordService.getRecordByAdmissionId("ADMSSION001");

            assertTrue(result.isPresent());
            assertEquals("ADMSSION001", result.get().getAdmissionId());
        }

        @Test
        @DisplayName("根据患者ID查询入院记录列表")
        void getRecordsByPatientId_success() {
            List<AdmissionRecord> records = List.of(testRecord);
            when(recordRepository.findByPatientIdAndDeletedFalseOrderByAdmissionDateDesc("PATIENT001"))
                    .thenReturn(records);

            List<AdmissionRecord> result = admissionRecordService.getRecordsByPatientId("PATIENT001");

            assertEquals(1, result.size());
            assertEquals("PATIENT001", result.get(0).getPatientId());
        }

        @Test
        @DisplayName("分页查询入院记录列表")
        void listRecords_success() {
            Pageable pageable = PageRequest.of(0, 10);
            List<AdmissionRecord> records = Arrays.asList(testRecord, createAnotherRecord());
            Page<AdmissionRecord> page = new PageImpl<>(records);

            when(recordRepository.findAll(pageable)).thenReturn(page);

            Page<AdmissionRecord> result = admissionRecordService.listRecords(pageable);

            assertEquals(2, result.getContent().size());
        }

        @Test
        @DisplayName("分页查询 - 过滤已删除记录")
        void listRecords_filterDeleted() {
            AdmissionRecord deletedRecord = createAnotherRecord();
            deletedRecord.setDeleted(true);

            Pageable pageable = PageRequest.of(0, 10);
            List<AdmissionRecord> records = Arrays.asList(testRecord, deletedRecord);
            Page<AdmissionRecord> page = new PageImpl<>(records);

            when(recordRepository.findAll(pageable)).thenReturn(page);

            Page<AdmissionRecord> result = admissionRecordService.listRecords(pageable);

            assertEquals(1, result.getContent().size());
            assertFalse(result.getContent().get(0).getDeleted());
        }

        @Test
        @DisplayName("根据科室分页查询")
        void getRecordsByDeptId_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<AdmissionRecord> page = new PageImpl<>(List.of(testRecord));

            when(recordRepository.findByDeptIdAndDeletedFalse("DEPT001", pageable)).thenReturn(page);

            Page<AdmissionRecord> result = admissionRecordService.getRecordsByDeptId("DEPT001", pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("根据状态分页查询")
        void getRecordsByStatus_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<AdmissionRecord> page = new PageImpl<>(List.of(testRecord));

            when(recordRepository.findByStatusAndDeletedFalse(EmrStatus.DRAFT, pageable)).thenReturn(page);

            Page<AdmissionRecord> result = admissionRecordService.getRecordsByStatus(EmrStatus.DRAFT, pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("根据科室和状态查询")
        void getRecordsByDeptIdAndStatus_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<AdmissionRecord> page = new PageImpl<>(List.of(testRecord));

            when(recordRepository.findByDeptIdAndStatusAndDeletedFalse("DEPT001", EmrStatus.DRAFT, pageable))
                    .thenReturn(page);

            Page<AdmissionRecord> result = admissionRecordService.getRecordsByDeptIdAndStatus("DEPT001", EmrStatus.DRAFT, pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("查询患者最新入院记录")
        void getLatestRecordByPatientId_success() {
            when(recordRepository.findFirstByPatientIdAndDeletedFalseOrderByAdmissionDateDesc("PATIENT001"))
                    .thenReturn(Optional.of(testRecord));

            Optional<AdmissionRecord> result = admissionRecordService.getLatestRecordByPatientId("PATIENT001");

            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("根据患者姓名模糊查询")
        void searchByPatientName_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<AdmissionRecord> page = new PageImpl<>(List.of(testRecord));

            when(recordRepository.findByPatientNameContainingAndDeletedFalse("王", pageable)).thenReturn(page);

            Page<AdmissionRecord> result = admissionRecordService.searchByPatientName("王", pageable);

            assertEquals(1, result.getContent().size());
            assertTrue(result.getContent().get(0).getPatientName().contains("王"));
        }
    }

    @Nested
    @DisplayName("入院记录提交与审核测试")
    class SubmitAndAuditTests {

        @Test
        @DisplayName("成功提交草稿入院记录")
        void submitRecord_draft_success() {
            testRecord.setStatus(EmrStatus.DRAFT);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(AdmissionRecord.class))).thenReturn(testRecord);

            EmrSubmitDTO submitDTO = new EmrSubmitDTO();
            submitDTO.setRecordId("ADM001");
            submitDTO.setRecordType("入院记录");

            AdmissionRecord result = admissionRecordService.submitRecord(submitDTO);

            assertEquals(EmrStatus.SUBMITTED, result.getStatus());
            assertNotNull(result.getSubmitTime());
            verify(recordRepository).save(testRecord);
        }

        @Test
        @DisplayName("成功提交退回入院记录")
        void submitRecord_rejected_success() {
            testRecord.setStatus(EmrStatus.REJECTED);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(AdmissionRecord.class))).thenReturn(testRecord);

            EmrSubmitDTO submitDTO = new EmrSubmitDTO();
            submitDTO.setRecordId("ADM001");
            submitDTO.setRecordType("入院记录");

            AdmissionRecord result = admissionRecordService.submitRecord(submitDTO);

            assertEquals(EmrStatus.SUBMITTED, result.getStatus());
        }

        @Test
        @DisplayName("提交已审核入院记录抛出异常")
        void submitRecord_audited_throwException() {
            testRecord.setStatus(EmrStatus.AUDITED);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));

            EmrSubmitDTO submitDTO = new EmrSubmitDTO();
            submitDTO.setRecordId("ADM001");
            submitDTO.setRecordType("入院记录");

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> admissionRecordService.submitRecord(submitDTO));

            assertEquals("只有草稿或退回状态的入院记录可以提交", exception.getMessage());
        }

        @Test
        @DisplayName("审核通过入院记录")
        void auditRecord_approve_success() {
            testRecord.setStatus(EmrStatus.SUBMITTED);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(AdmissionRecord.class))).thenReturn(testRecord);

            AdmissionRecord result = admissionRecordService.auditRecord("ADM001", true, "AUDITOR001", "审核员", "审核通过");

            assertEquals(EmrStatus.AUDITED, result.getStatus());
            assertNotNull(result.getAuditTime());
        }

        @Test
        @DisplayName("审核拒绝入院记录")
        void auditRecord_reject_success() {
            testRecord.setStatus(EmrStatus.SUBMITTED);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(AdmissionRecord.class))).thenReturn(testRecord);

            AdmissionRecord result = admissionRecordService.auditRecord("ADM001", false, "AUDITOR001", "审核员", "需要补充病史");

            assertEquals(EmrStatus.REJECTED, result.getStatus());
        }

        @Test
        @DisplayName("审核草稿入院记录抛出异常")
        void auditRecord_draft_throwException() {
            testRecord.setStatus(EmrStatus.DRAFT);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> admissionRecordService.auditRecord("ADM001", true, "AUDITOR001", "审核员", "审核通过"));

            assertEquals("只有已提交状态的入院记录可以审核", exception.getMessage());
        }

        @Test
        @DisplayName("审核已审核入院记录抛出异常")
        void auditRecord_alreadyAudited_throwException() {
            testRecord.setStatus(EmrStatus.AUDITED);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> admissionRecordService.auditRecord("ADM001", true, "AUDITOR001", "审核员", "审核通过"));

            assertEquals("只有已提交状态的入院记录可以审核", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("病史记录完整性测试")
    class MedicalHistoryTests {

        @Test
        @DisplayName("创建入院记录 - 包含完整病史")
        void createRecord_completeHistory() {
            when(recordRepository.save(any(AdmissionRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

            AdmissionRecord result = admissionRecordService.createRecord(testSaveDTO);

            assertEquals("既往高血压病史5年，规律服用降压药物", result.getPastHistory());
            assertEquals("吸烟20年，每日约20支", result.getPersonalHistory());
            assertEquals("已婚，育有1子", result.getMarriageHistory());
            assertEquals("父亲有糖尿病，母亲体健", result.getFamilyHistory());
            assertEquals("对青霉素过敏，表现为皮疹", result.getAllergyHistory());
        }

        @Test
        @DisplayName("更新入院记录 - 更新病史信息")
        void updateRecord_updateHistory() {
            testRecord.setStatus(EmrStatus.DRAFT);
            when(recordRepository.findById("ADM001")).thenReturn(Optional.of(testRecord));
            when(recordRepository.save(any(AdmissionRecord.class))).thenReturn(testRecord);

            AdmissionRecordSaveDTO updateDTO = new AdmissionRecordSaveDTO();
            updateDTO.setAdmissionId("ADMSSION001");
            updateDTO.setPatientId("PATIENT001");
            updateDTO.setDeptId("DEPT001");
            updateDTO.setDoctorId("DOC001");
            updateDTO.setChiefComplaint("新主诉");
            updateDTO.setPresentIllness("新病史");
            updateDTO.setPastHistory("更新的既往史");
            updateDTO.setAllergyHistory("发现新过敏药物");

            AdmissionRecord result = admissionRecordService.updateRecord("ADM001", updateDTO);

            assertEquals("更新的既往史", result.getPastHistory());
            assertEquals("发现新过敏药物", result.getAllergyHistory());
        }
    }

    // Helper methods
    private AdmissionRecord createTestRecord() {
        AdmissionRecord record = new AdmissionRecord();
        record.setId("ADM001");
        record.setAdmissionId("ADMSSION001");
        record.setPatientId("PATIENT001");
        record.setPatientName("王五");
        record.setGender("男");
        record.setAge(45);
        record.setAdmissionDate(LocalDate.now());
        record.setDeptId("DEPT001");
        record.setDeptName("呼吸内科");
        record.setWardId("WARD001");
        record.setBedNo("BED001");
        record.setChiefComplaint("发热咳嗽一周");
        record.setPresentIllness("患者一周前出现发热，体温最高38.5度，伴咳嗽咳痰");
        record.setPastHistory("既往高血压病史5年");
        record.setPersonalHistory("吸烟20年");
        record.setMarriageHistory("已婚");
        record.setFamilyHistory("父亲有糖尿病");
        record.setAllergyHistory("对青霉素过敏");
        record.setTemperature(BigDecimal.valueOf(38.5));
        record.setPulse(85);
        record.setRespiration(20);
        record.setBloodPressure("130/85");
        record.setWeight(BigDecimal.valueOf(70.0));
        record.setHeight(175);
        record.setGeneralExam("神志清楚，精神欠佳");
        record.setSpecialistExam("双肺可闻及湿啰音");
        record.setAuxiliaryExam("胸部CT示双肺下叶炎症");
        record.setAdmissionDiagnosisCode("J18.9");
        record.setAdmissionDiagnosisName("肺炎");
        record.setTreatmentPlan("完善检查，抗感染治疗");
        record.setDoctorId("DOC001");
        record.setDoctorName("主治医师张医生");
        record.setSuperiorDoctorId("SUP_DOC001");
        record.setSuperiorDoctorName("主任医师王主任");
        record.setStatus(EmrStatus.DRAFT);
        record.setDeleted(false);
        return record;
    }

    private AdmissionRecord createAnotherRecord() {
        AdmissionRecord record = new AdmissionRecord();
        record.setId("ADM002");
        record.setAdmissionId("ADMSSION002");
        record.setPatientId("PATIENT002");
        record.setPatientName("赵六");
        record.setDeptId("DEPT002");
        record.setStatus(EmrStatus.SUBMITTED);
        record.setDeleted(false);
        return record;
    }

    private AdmissionRecordSaveDTO createTestSaveDTO() {
        AdmissionRecordSaveDTO dto = new AdmissionRecordSaveDTO();
        dto.setAdmissionId("ADMSSION001");
        dto.setPatientId("PATIENT001");
        dto.setPatientName("王五");
        dto.setGender("男");
        dto.setAge(45);
        dto.setDeptId("DEPT001");
        dto.setDeptName("呼吸内科");
        dto.setWardId("WARD001");
        dto.setBedNo("BED001");
        dto.setChiefComplaint("发热咳嗽一周");
        dto.setPresentIllness("患者一周前出现发热，体温最高38.5度，伴咳嗽咳痰，痰为黄脓痰，不易咳出");
        dto.setPastHistory("既往高血压病史5年，规律服用降压药物");
        dto.setPersonalHistory("吸烟20年，每日约20支");
        dto.setMarriageHistory("已婚，育有1子");
        dto.setFamilyHistory("父亲有糖尿病，母亲体健");
        dto.setAllergyHistory("对青霉素过敏，表现为皮疹");
        dto.setTemperature(BigDecimal.valueOf(38.5));
        dto.setPulse(85);
        dto.setRespiration(20);
        dto.setBloodPressure("130/85");
        dto.setWeight(BigDecimal.valueOf(70.0));
        dto.setHeight(175);
        dto.setGeneralExam("神志清楚，精神欠佳，面色潮红");
        dto.setSpecialistExam("双肺可闻及湿啰音，以右下肺为著");
        dto.setAuxiliaryExam("胸部CT示双肺下叶炎症，血常规示白细胞升高");
        dto.setAdmissionDiagnosisCode("J18.9");
        dto.setAdmissionDiagnosisName("肺炎");
        dto.setTreatmentPlan("完善相关检查，给予抗感染、止咳化痰治疗");
        dto.setDoctorId("DOC001");
        dto.setDoctorName("主治医师张医生");
        dto.setSuperiorDoctorId("SUP_DOC001");
        dto.setSuperiorDoctorName("主任医师王主任");
        return dto;
    }

    private EmrTemplate createTestTemplate() {
        EmrTemplate template = new EmrTemplate();
        template.setId("TEMPLATE001");
        template.setTemplateName("入院记录模板");
        template.setTemplateType(TemplateType.ADMISSION);
        template.setTemplateContent("模板内容");
        template.setDeleted(false);
        return template;
    }
}
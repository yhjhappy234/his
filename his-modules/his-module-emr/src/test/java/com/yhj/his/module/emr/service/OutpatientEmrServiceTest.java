package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.dto.OutpatientEmrSaveDTO;
import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.entity.OutpatientEmr;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.TemplateType;
import com.yhj.his.module.emr.repository.EmrTemplateRepository;
import com.yhj.his.module.emr.repository.OutpatientEmrRepository;
import com.yhj.his.module.emr.service.impl.OutpatientEmrServiceImpl;
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
 * 门诊病历服务单元测试
 * 覆盖门诊病历创建、管理、审核核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OutpatientEmrService单元测试")
class OutpatientEmrServiceTest {

    @Mock
    private OutpatientEmrRepository emrRepository;

    @Mock
    private EmrTemplateRepository templateRepository;

    @Mock
    private EmrTemplateService templateService;

    @InjectMocks
    private OutpatientEmrServiceImpl outpatientEmrService;

    private OutpatientEmr testEmr;
    private OutpatientEmrSaveDTO testSaveDTO;
    private EmrTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testEmr = createTestEmr();
        testSaveDTO = createTestSaveDTO();
        testTemplate = createTestTemplate();
    }

    @Nested
    @DisplayName("门诊病历创建测试")
    class CreateEmrTests {

        @Test
        @DisplayName("成功创建门诊病历")
        void createEmr_success() {
            when(emrRepository.save(any(OutpatientEmr.class))).thenAnswer(invocation -> {
                OutpatientEmr saved = invocation.getArgument(0);
                saved.setId("EMR001");
                return saved;
            });

            OutpatientEmr result = outpatientEmrService.createEmr(testSaveDTO);

            assertNotNull(result);
            assertEquals("VISIT001", result.getVisitId());
            assertEquals("PATIENT001", result.getPatientId());
            assertEquals("张三", result.getPatientName());
            assertEquals(EmrStatus.DRAFT, result.getStatus());
            assertNotNull(result.getVisitDate());
            assertEquals("头痛三天", result.getChiefComplaint());
            assertEquals("患者三天前无明显诱因出现头痛，疼痛位于双侧颞部，呈持续性钝痛", result.getPresentIllness());

            verify(emrRepository).save(any(OutpatientEmr.class));
        }

        @Test
        @DisplayName("创建病历 - 使用模板时增加使用次数")
        void createEmr_withTemplate_incrementUseCount() {
            testSaveDTO.setTemplateId("TEMPLATE001");
            when(emrRepository.save(any(OutpatientEmr.class))).thenReturn(testEmr);

            OutpatientEmr result = outpatientEmrService.createEmr(testSaveDTO);

            assertNotNull(result);
            verify(templateService).incrementUseCount("TEMPLATE001");
        }

        @Test
        @DisplayName("创建病历 - 不使用模板时不增加使用次数")
        void createEmr_withoutTemplate_noIncrement() {
            testSaveDTO.setTemplateId(null);
            when(emrRepository.save(any(OutpatientEmr.class))).thenReturn(testEmr);

            OutpatientEmr result = outpatientEmrService.createEmr(testSaveDTO);

            assertNotNull(result);
            verify(templateService, never()).incrementUseCount(anyString());
        }

        @Test
        @DisplayName("创建病历 - 记录体格检查信息")
        void createEmr_withPhysicalExam() {
            when(emrRepository.save(any(OutpatientEmr.class))).thenAnswer(invocation -> {
                OutpatientEmr saved = invocation.getArgument(0);
                return saved;
            });

            OutpatientEmr result = outpatientEmrService.createEmr(testSaveDTO);

            assertEquals(BigDecimal.valueOf(36.5), result.getTemperature());
            assertEquals(72, result.getPulse());
            assertEquals(18, result.getRespiration());
            assertEquals("120/80", result.getBloodPressure());
            assertEquals(BigDecimal.valueOf(65.0), result.getWeight());
            assertEquals(170, result.getHeight());
        }
    }

    @Nested
    @DisplayName("从模板创建病历测试")
    class CreateFromTemplateTests {

        @Test
        @DisplayName("成功从模板创建病历")
        void createFromTemplate_success() {
            testTemplate.setTemplateContent("模板内容");
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            doNothing().when(templateService).incrementUseCount("TEMPLATE001");
            when(emrRepository.save(any(OutpatientEmr.class))).thenAnswer(invocation -> {
                OutpatientEmr saved = invocation.getArgument(0);
                saved.setId("EMR001");
                return saved;
            });

            OutpatientEmr result = outpatientEmrService.createFromTemplate("TEMPLATE001", testSaveDTO);

            assertNotNull(result);
            assertEquals("TEMPLATE001", result.getTemplateId());
            verify(templateRepository).findById("TEMPLATE001");
            verify(emrRepository).save(any(OutpatientEmr.class));
        }

        @Test
        @DisplayName("从模板创建 - 模板不存在抛出异常")
        void createFromTemplate_templateNotFound_throwException() {
            when(templateRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> outpatientEmrService.createFromTemplate("NONEXISTENT", testSaveDTO));

            assertEquals("模板不存在: NONEXISTENT", exception.getMessage());
        }

        @Test
        @DisplayName("从模板创建 - 已删除模板抛出异常")
        void createFromTemplate_deletedTemplate_throwException() {
            testTemplate.setDeleted(true);
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> outpatientEmrService.createFromTemplate("TEMPLATE001", testSaveDTO));

            assertEquals("模板不存在: TEMPLATE001", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("门诊病历更新测试")
    class UpdateEmrTests {

        @Test
        @DisplayName("成功更新草稿状态病历")
        void updateEmr_draft_success() {
            testEmr.setStatus(EmrStatus.DRAFT);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));
            when(emrRepository.save(any(OutpatientEmr.class))).thenReturn(testEmr);

            OutpatientEmrSaveDTO updateDTO = new OutpatientEmrSaveDTO();
            updateDTO.setVisitId("VISIT001");
            updateDTO.setPatientId("PATIENT001");
            updateDTO.setDeptId("DEPT001");
            updateDTO.setDoctorId("DOC001");
            updateDTO.setChiefComplaint("更新后的主诉");
            updateDTO.setPresentIllness("更新后的现病史");

            OutpatientEmr result = outpatientEmrService.updateEmr("EMR001", updateDTO);

            assertEquals("更新后的主诉", result.getChiefComplaint());
            verify(emrRepository).save(testEmr);
        }

        @Test
        @DisplayName("成功更新退回状态病历")
        void updateEmr_rejected_success() {
            testEmr.setStatus(EmrStatus.REJECTED);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));
            when(emrRepository.save(any(OutpatientEmr.class))).thenReturn(testEmr);

            OutpatientEmr result = outpatientEmrService.updateEmr("EMR001", testSaveDTO);

            assertNotNull(result);
            verify(emrRepository).save(testEmr);
        }

        @Test
        @DisplayName("更新已提交状态病历抛出异常")
        void updateEmr_submitted_throwException() {
            testEmr.setStatus(EmrStatus.SUBMITTED);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> outpatientEmrService.updateEmr("EMR001", testSaveDTO));

            assertEquals("只有草稿或退回状态的病历可以修改", exception.getMessage());
            verify(emrRepository, never()).save(any());
        }

        @Test
        @DisplayName("更新已审核状态病历抛出异常")
        void updateEmr_audited_throwException() {
            testEmr.setStatus(EmrStatus.AUDITED);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> outpatientEmrService.updateEmr("EMR001", testSaveDTO));

            assertEquals("只有草稿或退回状态的病历可以修改", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("门诊病历删除测试")
    class DeleteEmrTests {

        @Test
        @DisplayName("成功删除草稿状态病历")
        void deleteEmr_draft_success() {
            testEmr.setStatus(EmrStatus.DRAFT);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));
            when(emrRepository.save(any(OutpatientEmr.class))).thenReturn(testEmr);

            outpatientEmrService.deleteEmr("EMR001");

            assertTrue(testEmr.getDeleted());
            verify(emrRepository).save(testEmr);
        }

        @Test
        @DisplayName("删除已提交病历抛出异常")
        void deleteEmr_submitted_throwException() {
            testEmr.setStatus(EmrStatus.SUBMITTED);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> outpatientEmrService.deleteEmr("EMR001"));

            assertEquals("只有草稿状态的病历可以删除", exception.getMessage());
            verify(emrRepository, never()).save(any());
        }

        @Test
        @DisplayName("删除已审核病历抛出异常")
        void deleteEmr_audited_throwException() {
            testEmr.setStatus(EmrStatus.AUDITED);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> outpatientEmrService.deleteEmr("EMR001"));

            assertEquals("只有草稿状态的病历可以删除", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("门诊病历查询测试")
    class QueryEmrTests {

        @Test
        @DisplayName("根据ID获取病历成功")
        void getEmrById_success() {
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));

            OutpatientEmr result = outpatientEmrService.getEmrById("EMR001");

            assertNotNull(result);
            assertEquals("张三", result.getPatientName());
        }

        @Test
        @DisplayName("根据ID获取病历 - 不存在抛出异常")
        void getEmrById_notFound_throwException() {
            when(emrRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> outpatientEmrService.getEmrById("NONEXISTENT"));

            assertEquals("门诊病历不存在: NONEXISTENT", exception.getMessage());
        }

        @Test
        @DisplayName("根据ID获取病历 - 已删除抛出异常")
        void getEmrById_deleted_throwException() {
            testEmr.setDeleted(true);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> outpatientEmrService.getEmrById("EMR001"));

            assertEquals("门诊病历不存在: EMR001", exception.getMessage());
        }

        @Test
        @DisplayName("根据就诊ID获取病历")
        void getEmrByVisitId_success() {
            when(emrRepository.findByVisitIdAndDeletedFalse("VISIT001")).thenReturn(Optional.of(testEmr));

            Optional<OutpatientEmr> result = outpatientEmrService.getEmrByVisitId("VISIT001");

            assertTrue(result.isPresent());
            assertEquals("VISIT001", result.get().getVisitId());
        }

        @Test
        @DisplayName("根据患者ID查询病历列表")
        void getEmrsByPatientId_success() {
            List<OutpatientEmr> emrs = List.of(testEmr);
            when(emrRepository.findByPatientIdAndDeletedFalseOrderByVisitDateDesc("PATIENT001"))
                    .thenReturn(emrs);

            List<OutpatientEmr> result = outpatientEmrService.getEmrsByPatientId("PATIENT001");

            assertEquals(1, result.size());
            assertEquals("PATIENT001", result.get(0).getPatientId());
        }

        @Test
        @DisplayName("分页查询病历列表")
        void listEmrs_success() {
            Pageable pageable = PageRequest.of(0, 10);
            List<OutpatientEmr> emrs = Arrays.asList(testEmr, createAnotherEmr());
            Page<OutpatientEmr> page = new PageImpl<>(emrs);

            when(emrRepository.findAll(pageable)).thenReturn(page);

            Page<OutpatientEmr> result = outpatientEmrService.listEmrs(pageable);

            assertEquals(2, result.getContent().size());
        }

        @Test
        @DisplayName("分页查询病历 - 过滤已删除记录")
        void listEmrs_filterDeleted() {
            OutpatientEmr deletedEmr = createAnotherEmr();
            deletedEmr.setDeleted(true);

            Pageable pageable = PageRequest.of(0, 10);
            List<OutpatientEmr> emrs = Arrays.asList(testEmr, deletedEmr);
            Page<OutpatientEmr> page = new PageImpl<>(emrs);

            when(emrRepository.findAll(pageable)).thenReturn(page);

            Page<OutpatientEmr> result = outpatientEmrService.listEmrs(pageable);

            assertEquals(1, result.getContent().size());
            assertFalse(result.getContent().get(0).getDeleted());
        }

        @Test
        @DisplayName("根据医生ID分页查询")
        void getEmrsByDoctorId_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<OutpatientEmr> page = new PageImpl<>(List.of(testEmr));

            when(emrRepository.findByDoctorIdAndDeletedFalse("DOC001", pageable)).thenReturn(page);

            Page<OutpatientEmr> result = outpatientEmrService.getEmrsByDoctorId("DOC001", pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("根据科室和日期查询")
        void getEmrsByDeptIdAndDate_success() {
            LocalDate visitDate = LocalDate.now();
            Pageable pageable = PageRequest.of(0, 10);
            Page<OutpatientEmr> page = new PageImpl<>(List.of(testEmr));

            when(emrRepository.findByDeptIdAndVisitDateAndDeletedFalse("DEPT001", visitDate, pageable))
                    .thenReturn(page);

            Page<OutpatientEmr> result = outpatientEmrService.getEmrsByDeptIdAndDate("DEPT001", visitDate, pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("根据状态分页查询")
        void getEmrsByStatus_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<OutpatientEmr> page = new PageImpl<>(List.of(testEmr));

            when(emrRepository.findByStatusAndDeletedFalse(EmrStatus.DRAFT, pageable)).thenReturn(page);

            Page<OutpatientEmr> result = outpatientEmrService.getEmrsByStatus(EmrStatus.DRAFT, pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("根据科室和状态查询")
        void getEmrsByDeptIdAndStatus_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<OutpatientEmr> page = new PageImpl<>(List.of(testEmr));

            when(emrRepository.findByDeptIdAndStatusAndDeletedFalse("DEPT001", EmrStatus.DRAFT, pageable))
                    .thenReturn(page);

            Page<OutpatientEmr> result = outpatientEmrService.getEmrsByDeptIdAndStatus("DEPT001", EmrStatus.DRAFT, pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("查询患者最新病历")
        void getLatestEmrByPatientId_success() {
            when(emrRepository.findFirstByPatientIdAndDeletedFalseOrderByVisitDateDesc("PATIENT001"))
                    .thenReturn(Optional.of(testEmr));

            Optional<OutpatientEmr> result = outpatientEmrService.getLatestEmrByPatientId("PATIENT001");

            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("根据患者姓名模糊查询")
        void searchByPatientName_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<OutpatientEmr> page = new PageImpl<>(List.of(testEmr));

            when(emrRepository.findByPatientNameContainingAndDeletedFalse("张", pageable)).thenReturn(page);

            Page<OutpatientEmr> result = outpatientEmrService.searchByPatientName("张", pageable);

            assertEquals(1, result.getContent().size());
            assertTrue(result.getContent().get(0).getPatientName().contains("张"));
        }

        @Test
        @DisplayName("统计科室某日期病历数")
        void countByDeptIdAndVisitDate_success() {
            when(emrRepository.countByDeptIdAndVisitDate("DEPT001", LocalDate.now())).thenReturn(5L);

            Long count = outpatientEmrService.countByDeptIdAndVisitDate("DEPT001", LocalDate.now());

            assertEquals(5L, count);
        }
    }

    @Nested
    @DisplayName("病历提交与审核测试")
    class SubmitAndAuditTests {

        @Test
        @DisplayName("成功提交草稿病历")
        void submitEmr_draft_success() {
            testEmr.setStatus(EmrStatus.DRAFT);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));
            when(emrRepository.save(any(OutpatientEmr.class))).thenReturn(testEmr);

            EmrSubmitDTO submitDTO = new EmrSubmitDTO();
            submitDTO.setRecordId("EMR001");
            submitDTO.setRecordType("门诊病历");

            OutpatientEmr result = outpatientEmrService.submitEmr(submitDTO);

            assertEquals(EmrStatus.SUBMITTED, result.getStatus());
            assertNotNull(result.getSubmitTime());
            verify(emrRepository).save(testEmr);
        }

        @Test
        @DisplayName("成功提交退回病历")
        void submitEmr_rejected_success() {
            testEmr.setStatus(EmrStatus.REJECTED);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));
            when(emrRepository.save(any(OutpatientEmr.class))).thenReturn(testEmr);

            EmrSubmitDTO submitDTO = new EmrSubmitDTO();
            submitDTO.setRecordId("EMR001");
            submitDTO.setRecordType("门诊病历");

            OutpatientEmr result = outpatientEmrService.submitEmr(submitDTO);

            assertEquals(EmrStatus.SUBMITTED, result.getStatus());
        }

        @Test
        @DisplayName("提交已审核病历抛出异常")
        void submitEmr_audited_throwException() {
            testEmr.setStatus(EmrStatus.AUDITED);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));

            EmrSubmitDTO submitDTO = new EmrSubmitDTO();
            submitDTO.setRecordId("EMR001");
            submitDTO.setRecordType("门诊病历");

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> outpatientEmrService.submitEmr(submitDTO));

            assertEquals("只有草稿或退回状态的病历可以提交", exception.getMessage());
        }

        @Test
        @DisplayName("审核通过病历")
        void auditEmr_approve_success() {
            testEmr.setStatus(EmrStatus.SUBMITTED);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));
            when(emrRepository.save(any(OutpatientEmr.class))).thenReturn(testEmr);

            OutpatientEmr result = outpatientEmrService.auditEmr("EMR001", true, "AUDITOR001", "审核员", "审核通过");

            assertEquals(EmrStatus.AUDITED, result.getStatus());
            assertEquals("AUDITOR001", result.getAuditorId());
            assertEquals("审核员", result.getAuditorName());
            assertEquals("审核通过", result.getAuditComment());
            assertNotNull(result.getAuditTime());
        }

        @Test
        @DisplayName("审核拒绝病历")
        void auditEmr_reject_success() {
            testEmr.setStatus(EmrStatus.SUBMITTED);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));
            when(emrRepository.save(any(OutpatientEmr.class))).thenReturn(testEmr);

            OutpatientEmr result = outpatientEmrService.auditEmr("EMR001", false, "AUDITOR001", "审核员", "内容不完整");

            assertEquals(EmrStatus.REJECTED, result.getStatus());
            assertEquals("内容不完整", result.getAuditComment());
        }

        @Test
        @DisplayName("审核草稿病历抛出异常")
        void auditEmr_draft_throwException() {
            testEmr.setStatus(EmrStatus.DRAFT);
            when(emrRepository.findById("EMR001")).thenReturn(Optional.of(testEmr));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> outpatientEmrService.auditEmr("EMR001", true, "AUDITOR001", "审核员", "审核通过"));

            assertEquals("只有已提交状态的病历可以审核", exception.getMessage());
        }
    }

    // Helper methods
    private OutpatientEmr createTestEmr() {
        OutpatientEmr emr = new OutpatientEmr();
        emr.setId("EMR001");
        emr.setVisitId("VISIT001");
        emr.setPatientId("PATIENT001");
        emr.setPatientName("张三");
        emr.setVisitDate(LocalDate.now());
        emr.setDeptId("DEPT001");
        emr.setDeptName("内科");
        emr.setDoctorId("DOC001");
        emr.setDoctorName("李医生");
        emr.setChiefComplaint("头痛三天");
        emr.setPresentIllness("患者三天前无明显诱因出现头痛");
        emr.setTemperature(BigDecimal.valueOf(36.5));
        emr.setPulse(72);
        emr.setRespiration(18);
        emr.setBloodPressure("120/80");
        emr.setWeight(BigDecimal.valueOf(65.0));
        emr.setHeight(170);
        emr.setPrimaryDiagnosisCode("R51");
        emr.setPrimaryDiagnosisName("头痛");
        emr.setStatus(EmrStatus.DRAFT);
        emr.setDeleted(false);
        return emr;
    }

    private OutpatientEmr createAnotherEmr() {
        OutpatientEmr emr = new OutpatientEmr();
        emr.setId("EMR002");
        emr.setVisitId("VISIT002");
        emr.setPatientId("PATIENT002");
        emr.setPatientName("李四");
        emr.setVisitDate(LocalDate.now());
        emr.setDeptId("DEPT001");
        emr.setDoctorId("DOC002");
        emr.setStatus(EmrStatus.SUBMITTED);
        emr.setDeleted(false);
        return emr;
    }

    private OutpatientEmrSaveDTO createTestSaveDTO() {
        OutpatientEmrSaveDTO dto = new OutpatientEmrSaveDTO();
        dto.setVisitId("VISIT001");
        dto.setPatientId("PATIENT001");
        dto.setPatientName("张三");
        dto.setDeptId("DEPT001");
        dto.setDeptName("内科");
        dto.setDoctorId("DOC001");
        dto.setDoctorName("李医生");
        dto.setChiefComplaint("头痛三天");
        dto.setPresentIllness("患者三天前无明显诱因出现头痛，疼痛位于双侧颞部，呈持续性钝痛");
        dto.setPastHistory("既往体健");
        dto.setPersonalHistory("无特殊");
        dto.setFamilyHistory("无特殊");
        dto.setAllergyHistory("无");
        dto.setTemperature(BigDecimal.valueOf(36.5));
        dto.setPulse(72);
        dto.setRespiration(18);
        dto.setBloodPressure("120/80");
        dto.setWeight(BigDecimal.valueOf(65.0));
        dto.setHeight(170);
        dto.setGeneralExam("神志清楚，精神可");
        dto.setSpecialistExam("神经系统检查未见异常");
        dto.setPrimaryDiagnosisCode("R51");
        dto.setPrimaryDiagnosisName("头痛");
        dto.setTreatmentPlan("给予止痛药物治疗");
        dto.setMedicalAdvice("注意休息，多饮水");
        return dto;
    }

    private EmrTemplate createTestTemplate() {
        EmrTemplate template = new EmrTemplate();
        template.setId("TEMPLATE001");
        template.setTemplateName("门诊初诊模板");
        template.setTemplateType(TemplateType.OUTPATIENT_FIRST);
        template.setTemplateContent("模板内容");
        template.setDeleted(false);
        return template;
    }
}
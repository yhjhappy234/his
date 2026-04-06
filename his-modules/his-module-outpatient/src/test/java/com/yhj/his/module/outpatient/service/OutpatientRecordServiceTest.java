package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.dto.RecordSaveRequest;
import com.yhj.his.module.outpatient.entity.OutpatientRecord;
import com.yhj.his.module.outpatient.entity.Registration;
import com.yhj.his.module.outpatient.repository.OutpatientRecordRepository;
import com.yhj.his.module.outpatient.repository.RegistrationRepository;
import com.yhj.his.module.outpatient.service.impl.OutpatientRecordServiceImpl;
import com.yhj.his.module.outpatient.vo.OutpatientRecordVO;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * OutpatientRecordService单元测试
 *
 * 测试范围：
 * - 病历保存（草稿）
 * - 病历提交
 * - 病历查询
 * - 病历作废
 * - 病历更新
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("门诊病历服务测试")
class OutpatientRecordServiceTest {

    @Mock
    private OutpatientRecordRepository outpatientRecordRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private OutpatientRecordServiceImpl outpatientRecordService;

    private Registration testRegistration;
    private OutpatientRecord testRecord;
    private RecordSaveRequest saveRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试挂号记录
        testRegistration = new Registration();
        testRegistration.setId("registration-id-001");
        testRegistration.setPatientId("PAT20260406001");
        testRegistration.setPatientName("张三");
        testRegistration.setVisitNo("VIS20260406001");
        testRegistration.setDeptId("DEPT001");
        testRegistration.setDeptName("内科");
        testRegistration.setDoctorId("DOC001");
        testRegistration.setDoctorName("李医生");
        testRegistration.setScheduleDate(LocalDate.now());
        testRegistration.setStatus("已签到");
        testRegistration.setVisitStatus("就诊中");

        // 初始化测试病历
        testRecord = new OutpatientRecord();
        testRecord.setId("record-id-001");
        testRecord.setRegistrationId("registration-id-001");
        testRecord.setPatientId("PAT20260406001");
        testRecord.setPatientName("张三");
        testRecord.setVisitNo("VIS20260406001");
        testRecord.setDeptId("DEPT001");
        testRecord.setDeptName("内科");
        testRecord.setDoctorId("DOC001");
        testRecord.setDoctorName("李医生");
        testRecord.setVisitDate(LocalDate.now());
        testRecord.setChiefComplaint("头痛、发热2天");
        testRecord.setPresentIllness("患者2天前无明显诱因出现头痛，伴发热...");
        testRecord.setPastHistory("既往体健");
        testRecord.setAllergyHistory("无");
        testRecord.setPersonalHistory("无吸烟饮酒史");
        testRecord.setFamilyHistory("父母健在");
        testRecord.setTemperature(BigDecimal.valueOf(38.5));
        testRecord.setPulse(85);
        testRecord.setRespiration(20);
        testRecord.setBloodPressure("120/80");
        testRecord.setHeight(175);
        testRecord.setWeight(BigDecimal.valueOf(70));
        testRecord.setPhysicalExam("咽部充血，扁桃体无肿大");
        testRecord.setAuxiliaryExam("血常规：白细胞轻度升高");
        testRecord.setDiagnosisCode("J00");
        testRecord.setDiagnosisName("急性上呼吸道感染");
        testRecord.setDiagnosisType("主要");
        testRecord.setTreatmentPlan("对症治疗，多休息多饮水");
        testRecord.setMedicalAdvice("注意休息，清淡饮食");
        testRecord.setStatus("已提交");
        testRecord.setSubmitTime(LocalDateTime.now());
        testRecord.setDeleted(false);

        // 初始化保存请求
        saveRequest = new RecordSaveRequest();
        saveRequest.setRegistrationId("registration-id-001");
        saveRequest.setPatientId("PAT20260406001");
        saveRequest.setChiefComplaint("头痛、发热2天");
        saveRequest.setPresentIllness("患者2天前无明显诱因出现头痛，伴发热...");
        saveRequest.setPastHistory("既往体健");
        saveRequest.setAllergyHistory("无");
        saveRequest.setPersonalHistory("无吸烟饮酒史");
        saveRequest.setFamilyHistory("父母健在");
        saveRequest.setTemperature(BigDecimal.valueOf(38.5));
        saveRequest.setPulse(85);
        saveRequest.setRespiration(20);
        saveRequest.setBloodPressure("120/80");
        saveRequest.setHeight(175);
        saveRequest.setWeight(BigDecimal.valueOf(70));
        saveRequest.setPhysicalExam("咽部充血，扁桃体无肿大");
        saveRequest.setAuxiliaryExam("血常规：白细胞轻度升高");
        saveRequest.setDiagnosisCode("J00");
        saveRequest.setDiagnosisName("急性上呼吸道感染");
        saveRequest.setDiagnosisType("主要");
        saveRequest.setTreatmentPlan("对症治疗，多休息多饮水");
        saveRequest.setMedicalAdvice("注意休息，清淡饮食");
    }

    @Nested
    @DisplayName("病历保存测试")
    class SaveDraftTests {

        @Test
        @DisplayName("成功保存病历草稿")
        void shouldSaveDraftSuccessfully() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(outpatientRecordRepository.findByRegistrationId(anyString())).thenReturn(Optional.empty());
            when(outpatientRecordRepository.save(any(OutpatientRecord.class))).thenReturn(testRecord);

            // When
            OutpatientRecordVO result = outpatientRecordService.saveDraft(saveRequest);

            // Then
            assertNotNull(result);
            assertEquals("草稿", testRecord.getStatus());
            assertEquals("头痛、发热2天", result.getChiefComplaint());
            assertEquals("急性上呼吸道感染", result.getDiagnosisName());
            verify(outpatientRecordRepository).save(any(OutpatientRecord.class));
        }

        @Test
        @DisplayName("更新已存在的病历草稿")
        void shouldUpdateExistingDraft() {
            // Given
            OutpatientRecord existingRecord = new OutpatientRecord();
            existingRecord.setId("record-id-001");
            existingRecord.setRegistrationId("registration-id-001");
            existingRecord.setStatus("草稿");

            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(outpatientRecordRepository.findByRegistrationId(anyString())).thenReturn(Optional.of(existingRecord));
            when(outpatientRecordRepository.save(any(OutpatientRecord.class))).thenReturn(existingRecord);

            // When
            OutpatientRecordVO result = outpatientRecordService.saveDraft(saveRequest);

            // Then
            assertNotNull(result);
            verify(outpatientRecordRepository).findByRegistrationId("registration-id-001");
        }

        @Test
        @DisplayName("挂号不存在时抛出异常")
        void shouldThrowExceptionWhenRegistrationNotFound() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> outpatientRecordService.saveDraft(saveRequest));

            assertEquals("挂号记录不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("病历提交测试")
    class SubmitRecordTests {

        @Test
        @DisplayName("成功提交病历")
        void shouldSubmitRecordSuccessfully() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(outpatientRecordRepository.findByRegistrationId(anyString())).thenReturn(Optional.empty());
            when(outpatientRecordRepository.save(any(OutpatientRecord.class))).thenReturn(testRecord);
            when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);

            // When
            OutpatientRecordVO result = outpatientRecordService.submitRecord(saveRequest);

            // Then
            assertNotNull(result);
            assertEquals("已提交", testRecord.getStatus());
            assertNotNull(testRecord.getSubmitTime());
            verify(registrationRepository).save(any(Registration.class));
            assertEquals("已就诊", testRegistration.getStatus());
            assertEquals("已完成", testRegistration.getVisitStatus());
        }

        @Test
        @DisplayName("提交病历时更新挂号状态")
        void shouldUpdateRegistrationStatusWhenSubmitting() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(outpatientRecordRepository.findByRegistrationId(anyString())).thenReturn(Optional.empty());
            when(outpatientRecordRepository.save(any(OutpatientRecord.class))).thenReturn(testRecord);
            when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> {
                Registration reg = invocation.getArgument(0);
                assertEquals("已就诊", reg.getStatus());
                assertEquals("已完成", reg.getVisitStatus());
                return reg;
            });

            // When
            OutpatientRecordVO result = outpatientRecordService.submitRecord(saveRequest);

            // Then
            assertNotNull(result);
            verify(registrationRepository).save(any(Registration.class));
        }

        @Test
        @DisplayName("提交病历时挂号不存在抛出异常")
        void shouldThrowExceptionWhenRegistrationNotFoundOnSubmit() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> outpatientRecordService.submitRecord(saveRequest));

            assertEquals("挂号记录不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("病历查询测试")
    class QueryTests {

        @Test
        @DisplayName("按ID查询病历")
        void shouldFindRecordById() {
            // Given
            when(outpatientRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // When
            Optional<OutpatientRecord> result = outpatientRecordService.findById("record-id-001");

            // Then
            assertTrue(result.isPresent());
            assertEquals("张三", result.get().getPatientName());
        }

        @Test
        @DisplayName("按挂号ID查询病历")
        void shouldFindByRegistrationId() {
            // Given
            when(outpatientRecordRepository.findByRegistrationId(anyString())).thenReturn(Optional.of(testRecord));

            // When
            Optional<OutpatientRecord> result = outpatientRecordService.findByRegistrationId("registration-id-001");

            // Then
            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("获取病历详情")
        void shouldGetRecordDetail() {
            // Given
            when(outpatientRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // When
            OutpatientRecordVO result = outpatientRecordService.getRecordDetail("record-id-001");

            // Then
            assertNotNull(result);
            assertEquals("record-id-001", result.getRecordId());
            assertEquals("张三", result.getPatientName());
            assertEquals("急性上呼吸道感染", result.getDiagnosisName());
        }

        @Test
        @DisplayName("获取不存在病历详情时抛出异常")
        void shouldThrowExceptionWhenRecordNotFound() {
            // Given
            when(outpatientRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> outpatientRecordService.getRecordDetail("non-existent-id"));

            assertEquals("病历不存在", exception.getMessage());
        }

        @Test
        @DisplayName("按挂号ID获取病历详情")
        void shouldGetRecordByRegistrationId() {
            // Given
            when(outpatientRecordRepository.findByRegistrationId(anyString())).thenReturn(Optional.of(testRecord));

            // When
            OutpatientRecordVO result = outpatientRecordService.getRecordByRegistrationId("registration-id-001");

            // Then
            assertNotNull(result);
            assertEquals("registration-id-001", result.getRegistrationId());
        }

        @Test
        @DisplayName("按挂号ID获取病历详情-病历不存在返回null")
        void shouldReturnNullWhenRecordNotExists() {
            // Given
            when(outpatientRecordRepository.findByRegistrationId(anyString())).thenReturn(Optional.empty());

            // When
            OutpatientRecordVO result = outpatientRecordService.getRecordByRegistrationId("registration-id-001");

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("分页查询病历列表")
        void shouldListRecordsWithPagination() {
            // Given
            List<OutpatientRecord> records = Arrays.asList(testRecord);
            Page<OutpatientRecord> page = new PageImpl<>(records);
            when(outpatientRecordRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            // When
            Pageable pageable = PageRequest.of(0, 10);
            PageResult<OutpatientRecordVO> result = outpatientRecordService.listRecords(
                    "PAT20260406001", "DOC001", "DEPT001",
                    LocalDate.now().minusDays(7), LocalDate.now(), pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
        }

        @Test
        @DisplayName("查询患者病历列表")
        void shouldListPatientRecords() {
            // Given
            when(outpatientRecordRepository.findByPatientIdOrderByVisitDateDesc(anyString()))
                    .thenReturn(Arrays.asList(testRecord));

            // When
            List<OutpatientRecordVO> result = outpatientRecordService.listPatientRecords("PAT20260406001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("查询患者历史病历")
        void shouldListRecentRecords() {
            // Given
            when(outpatientRecordRepository.findRecentRecords(anyString(), anyInt()))
                    .thenReturn(Arrays.asList(testRecord));

            // When
            List<OutpatientRecordVO> result = outpatientRecordService.listRecentRecords("PAT20260406001", 5);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(outpatientRecordRepository).findRecentRecords("PAT20260406001", 5);
        }
    }

    @Nested
    @DisplayName("病历作废测试")
    class VoidRecordTests {

        @Test
        @DisplayName("成功作废病历")
        void shouldVoidRecordSuccessfully() {
            // Given
            testRecord.setStatus("已提交");
            when(outpatientRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(outpatientRecordRepository.save(any(OutpatientRecord.class))).thenReturn(testRecord);

            // When
            outpatientRecordService.voidRecord("record-id-001", "诊断错误，需要重新诊断");

            // Then
            assertEquals("已作废", testRecord.getStatus());
            verify(outpatientRecordRepository).save(any(OutpatientRecord.class));
        }

        @Test
        @DisplayName("病历不存在时抛出异常")
        void shouldThrowExceptionWhenVoidingNonExistentRecord() {
            // Given
            when(outpatientRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> outpatientRecordService.voidRecord("non-existent-id", "原因"));

            assertEquals("病历不存在", exception.getMessage());
        }

        @Test
        @DisplayName("草稿状态病历无法作废")
        void shouldThrowExceptionWhenVoidingDraftRecord() {
            // Given
            testRecord.setStatus("草稿");
            when(outpatientRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> outpatientRecordService.voidRecord("record-id-001", "原因"));

            assertEquals("只能作废已提交的病历", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("病历更新测试")
    class UpdateRecordTests {

        @Test
        @DisplayName("成功更新病历")
        void shouldUpdateRecordSuccessfully() {
            // Given
            testRecord.setStatus("草稿");
            when(outpatientRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));
            when(outpatientRecordRepository.save(any(OutpatientRecord.class))).thenReturn(testRecord);

            // When
            OutpatientRecordVO result = outpatientRecordService.updateRecord("record-id-001", saveRequest);

            // Then
            assertNotNull(result);
            verify(outpatientRecordRepository).save(any(OutpatientRecord.class));
        }

        @Test
        @DisplayName("病历不存在时抛出异常")
        void shouldThrowExceptionWhenUpdatingNonExistentRecord() {
            // Given
            when(outpatientRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> outpatientRecordService.updateRecord("non-existent-id", saveRequest));

            assertEquals("病历不存在", exception.getMessage());
        }

        @Test
        @DisplayName("非草稿状态病历无法修改")
        void shouldThrowExceptionWhenUpdatingNonDraftRecord() {
            // Given
            testRecord.setStatus("已提交");
            when(outpatientRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> outpatientRecordService.updateRecord("record-id-001", saveRequest));

            assertEquals("只能修改草稿状态的病历", exception.getMessage());
        }

        @Test
        @DisplayName("已作废病历无法修改")
        void shouldThrowExceptionWhenUpdatingVoidedRecord() {
            // Given
            testRecord.setStatus("已作废");
            when(outpatientRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> outpatientRecordService.updateRecord("record-id-001", saveRequest));

            assertEquals("只能修改草稿状态的病历", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("VO转换测试")
    class VOConversionTests {

        @Test
        @DisplayName("正确转换病历VO")
        void shouldConvertToVOCorrectly() {
            // Given
            when(outpatientRecordRepository.findById(anyString())).thenReturn(Optional.of(testRecord));

            // When
            OutpatientRecordVO result = outpatientRecordService.getRecordDetail("record-id-001");

            // Then
            assertNotNull(result);
            assertEquals("record-id-001", result.getRecordId());
            assertEquals("registration-id-001", result.getRegistrationId());
            assertEquals("PAT20260406001", result.getPatientId());
            assertEquals("张三", result.getPatientName());
            assertEquals("VIS20260406001", result.getVisitNo());
            assertEquals("DEPT001", result.getDeptId());
            assertEquals("内科", result.getDeptName());
            assertEquals("DOC001", result.getDoctorId());
            assertEquals("李医生", result.getDoctorName());
            assertEquals(LocalDate.now(), result.getVisitDate());
            assertEquals("头痛、发热2天", result.getChiefComplaint());
            assertEquals("患者2天前无明显诱因出现头痛，伴发热...", result.getPresentIllness());
            assertEquals("既往体健", result.getPastHistory());
            assertEquals("无", result.getAllergyHistory());
            assertEquals("无吸烟饮酒史", result.getPersonalHistory());
            assertEquals("父母健在", result.getFamilyHistory());
            assertEquals(BigDecimal.valueOf(38.5), result.getTemperature());
            assertEquals(85, result.getPulse());
            assertEquals(20, result.getRespiration());
            assertEquals("120/80", result.getBloodPressure());
            assertEquals(175, result.getHeight());
            assertEquals(BigDecimal.valueOf(70), result.getWeight());
            assertEquals("咽部充血，扁桃体无肿大", result.getPhysicalExam());
            assertEquals("血常规：白细胞轻度升高", result.getAuxiliaryExam());
            assertEquals("J00", result.getDiagnosisCode());
            assertEquals("急性上呼吸道感染", result.getDiagnosisName());
            assertEquals("主要", result.getDiagnosisType());
            assertEquals("对症治疗，多休息多饮水", result.getTreatmentPlan());
            assertEquals("注意休息，清淡饮食", result.getMedicalAdvice());
            assertEquals("已提交", result.getStatus());
            assertNotNull(result.getCreateTime());
            assertNotNull(result.getSubmitTime());
        }
    }

    @Nested
    @DisplayName("病历创建测试")
    class CreateRecordTests {

        @Test
        @DisplayName("首次创建病历应初始化基本信息")
        void shouldInitializeBasicInfoWhenCreatingRecord() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(outpatientRecordRepository.findByRegistrationId(anyString())).thenReturn(Optional.empty());
            when(outpatientRecordRepository.save(any(OutpatientRecord.class))).thenAnswer(invocation -> {
                OutpatientRecord record = invocation.getArgument(0);
                assertEquals("registration-id-001", record.getRegistrationId());
                assertEquals("PAT20260406001", record.getPatientId());
                assertEquals("张三", record.getPatientName());
                assertEquals("VIS20260406001", record.getVisitNo());
                assertEquals("DEPT001", record.getDeptId());
                assertEquals("DOC001", record.getDoctorId());
                assertEquals(LocalDate.now(), record.getVisitDate());
                return record;
            });

            // When
            OutpatientRecordVO result = outpatientRecordService.saveDraft(saveRequest);

            // Then
            assertNotNull(result);
        }
    }
}
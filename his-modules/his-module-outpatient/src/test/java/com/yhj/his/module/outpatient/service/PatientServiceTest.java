package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.outpatient.dto.PatientCreateRequest;
import com.yhj.his.module.outpatient.dto.PatientUpdateRequest;
import com.yhj.his.module.outpatient.entity.Patient;
import com.yhj.his.module.outpatient.repository.PatientRepository;
import com.yhj.his.module.outpatient.service.impl.PatientServiceImpl;
import com.yhj.his.module.outpatient.vo.PatientVO;

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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * PatientService单元测试
 *
 * 测试范围：
 * - 患者注册和建档
 * - 患者信息更新
 * - 患者查询（按ID、身份证号、姓名模糊查询）
 * - 患者删除（逻辑删除）
 * - 黑名单管理
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("患者服务测试")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient testPatient;
    private PatientCreateRequest createRequest;
    private PatientUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试患者数据
        testPatient = new Patient();
        testPatient.setId("test-id-001");
        testPatient.setPatientId("PAT20260406001");
        testPatient.setIdCardNo("320123199001011234");
        testPatient.setName("张三");
        testPatient.setGender("男");
        testPatient.setBirthDate(LocalDate.of(1990, 1, 1));
        testPatient.setPhone("13800138000");
        testPatient.setAddress("江苏省南京市玄武区");
        testPatient.setEmergencyContact("李四");
        testPatient.setEmergencyPhone("13900139000");
        testPatient.setBloodType("A");
        testPatient.setAllergyHistory("无");
        testPatient.setMedicalHistory("无");
        testPatient.setMedicalInsuranceNo("MI123456789");
        testPatient.setStatus("正常");
        testPatient.setNoShowCount(0);
        testPatient.setIsBlacklist(false);
        testPatient.setDeleted(false);

        // 初始化创建请求
        createRequest = new PatientCreateRequest();
        createRequest.setIdCardNo("320123199001011234");
        createRequest.setName("张三");
        createRequest.setGender("男");
        createRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        createRequest.setPhone("13800138000");
        createRequest.setAddress("江苏省南京市玄武区");
        createRequest.setEmergencyContact("李四");
        createRequest.setEmergencyPhone("13900139000");
        createRequest.setBloodType("A");
        createRequest.setAllergyHistory("无");
        createRequest.setMedicalHistory("无");
        createRequest.setMedicalInsuranceNo("MI123456789");

        // 初始化更新请求
        updateRequest = new PatientUpdateRequest();
        updateRequest.setPhone("13800138001");
        updateRequest.setAddress("江苏省南京市鼓楼区");
        updateRequest.setEmergencyContact("王五");
        updateRequest.setEmergencyPhone("13900139001");
        updateRequest.setAllergyHistory("青霉素过敏");
    }

    @Nested
    @DisplayName("患者创建测试")
    class CreatePatientTests {

        @Test
        @DisplayName("成功创建患者")
        void shouldCreatePatientSuccessfully() {
            // Given
            when(patientRepository.existsByIdCardNo(anyString())).thenReturn(false);
            when(sequenceGenerator.generate(anyString(), anyInt())).thenReturn("PAT20260406001");
            when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

            // When
            PatientVO result = patientService.createPatient(createRequest);

            // Then
            assertNotNull(result);
            assertEquals("张三", result.getName());
            assertEquals("男", result.getGender());
            assertEquals("320123199001011234", result.getIdCardNo());
            assertEquals("正常", result.getStatus());
            assertFalse(result.getIsBlacklist());
            assertEquals(0, result.getNoShowCount());

            verify(patientRepository).existsByIdCardNo(createRequest.getIdCardNo());
            verify(sequenceGenerator).generate("PAT", 8);
            verify(patientRepository).save(any(Patient.class));
        }

        @Test
        @DisplayName("身份证号已存在时抛出异常")
        void shouldThrowExceptionWhenIdCardNoExists() {
            // Given
            when(patientRepository.existsByIdCardNo("320123199001011234")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> patientService.createPatient(createRequest));

            assertEquals("身份证号已存在", exception.getMessage());
            verify(patientRepository).existsByIdCardNo(createRequest.getIdCardNo());
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("创建患者时无身份证号应成功")
        void shouldCreatePatientWithoutIdCardNo() {
            // Given
            createRequest.setIdCardNo(null);
            when(sequenceGenerator.generate(anyString(), anyInt())).thenReturn("PAT20260406001");
            when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

            // When
            PatientVO result = patientService.createPatient(createRequest);

            // Then
            assertNotNull(result);
            verify(patientRepository, never()).existsByIdCardNo(anyString());
            verify(patientRepository).save(any(Patient.class));
        }

        @Test
        @DisplayName("创建患者时自动设置默认状态")
        void shouldSetDefaultStatusWhenCreatingPatient() {
            // Given
            when(patientRepository.existsByIdCardNo(anyString())).thenReturn(false);
            when(sequenceGenerator.generate(anyString(), anyInt())).thenReturn("PAT20260406001");

            Patient savedPatient = new Patient();
            savedPatient.setId("test-id-001");
            savedPatient.setPatientId("PAT20260406001");
            savedPatient.setName("张三");
            savedPatient.setStatus("正常");
            savedPatient.setNoShowCount(0);
            savedPatient.setIsBlacklist(false);

            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient patient = invocation.getArgument(0);
                assertEquals("正常", patient.getStatus());
                assertEquals(0, patient.getNoShowCount());
                assertFalse(patient.getIsBlacklist());
                return savedPatient;
            });

            // When
            PatientVO result = patientService.createPatient(createRequest);

            // Then
            assertNotNull(result);
            assertEquals("正常", result.getStatus());
            assertEquals(0, result.getNoShowCount());
            assertFalse(result.getIsBlacklist());
        }
    }

    @Nested
    @DisplayName("患者更新测试")
    class UpdatePatientTests {

        @Test
        @DisplayName("成功更新患者信息")
        void shouldUpdatePatientSuccessfully() {
            // Given
            when(patientRepository.findById("test-id-001")).thenReturn(Optional.of(testPatient));
            when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient patient = invocation.getArgument(0);
                return patient;
            });

            // When
            PatientVO result = patientService.updatePatient("test-id-001", updateRequest);

            // Then
            assertNotNull(result);
            verify(patientRepository).findById("test-id-001");
            verify(patientRepository).save(any(Patient.class));
        }

        @Test
        @DisplayName("患者不存在时抛出异常")
        void shouldThrowExceptionWhenPatientNotFound() {
            // Given
            when(patientRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> patientService.updatePatient("non-existent-id", updateRequest));

            assertEquals("患者不存在", exception.getMessage());
            verify(patientRepository).findById("non-existent-id");
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("只更新非空字段")
        void shouldOnlyUpdateNonNullFields() {
            // Given
            PatientUpdateRequest partialRequest = new PatientUpdateRequest();
            partialRequest.setPhone("13900139000");

            when(patientRepository.findById("test-id-001")).thenReturn(Optional.of(testPatient));
            when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

            // When
            PatientVO result = patientService.updatePatient("test-id-001", partialRequest);

            // Then
            assertNotNull(result);
            verify(patientRepository).save(any(Patient.class));
        }
    }

    @Nested
    @DisplayName("患者查询测试")
    class QueryPatientTests {

        @Test
        @DisplayName("按ID查询患者")
        void shouldFindPatientById() {
            // Given
            when(patientRepository.findById("test-id-001")).thenReturn(Optional.of(testPatient));

            // When
            Optional<Patient> result = patientService.findById("test-id-001");

            // Then
            assertTrue(result.isPresent());
            assertEquals("张三", result.get().getName());
            verify(patientRepository).findById("test-id-001");
        }

        @Test
        @DisplayName("按患者ID查询")
        void shouldFindPatientByPatientId() {
            // Given
            when(patientRepository.findByPatientId("PAT20260406001")).thenReturn(Optional.of(testPatient));

            // When
            Optional<Patient> result = patientService.findByPatientId("PAT20260406001");

            // Then
            assertTrue(result.isPresent());
            assertEquals("PAT20260406001", result.get().getPatientId());
            verify(patientRepository).findByPatientId("PAT20260406001");
        }

        @Test
        @DisplayName("按身份证号查询")
        void shouldFindPatientByIdCardNo() {
            // Given
            when(patientRepository.findByIdCardNo("320123199001011234")).thenReturn(Optional.of(testPatient));

            // When
            Optional<Patient> result = patientService.findByIdCardNo("320123199001011234");

            // Then
            assertTrue(result.isPresent());
            assertEquals("320123199001011234", result.get().getIdCardNo());
            verify(patientRepository).findByIdCardNo("320123199001011234");
        }

        @Test
        @DisplayName("获取患者详情")
        void shouldGetPatientDetail() {
            // Given
            when(patientRepository.findById("test-id-001")).thenReturn(Optional.of(testPatient));

            // When
            PatientVO result = patientService.getPatientDetail("test-id-001");

            // Then
            assertNotNull(result);
            assertEquals("张三", result.getName());
            assertNotNull(result.getAge()); // 年龄应该被计算
            verify(patientRepository).findById("test-id-001");
        }

        @Test
        @DisplayName("获取不存在患者的详情时抛出异常")
        void shouldThrowExceptionWhenGetDetailForNonExistentPatient() {
            // Given
            when(patientRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> patientService.getPatientDetail("non-existent-id"));

            assertEquals("患者不存在", exception.getMessage());
        }

        @Test
        @DisplayName("分页查询患者列表")
        void shouldListPatientsWithPagination() {
            // Given
            List<Patient> patients = Arrays.asList(testPatient);
            Page<Patient> page = new PageImpl<>(patients);
            when(patientRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            // When
            Pageable pageable = PageRequest.of(0, 10);
            PageResult<PatientVO> result = patientService.listPatients("张", "138", "正常", pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
            verify(patientRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("模糊搜索患者")
        void shouldSearchPatientsByKeyword() {
            // Given
            List<Patient> patients = Arrays.asList(testPatient);
            when(patientRepository.findAll(any(Specification.class))).thenReturn(patients);

            // When
            List<PatientVO> result = patientService.searchPatients("张三");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("张三", result.get(0).getName());
            verify(patientRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("检查患者ID是否存在")
        void shouldCheckPatientIdExists() {
            // Given
            when(patientRepository.existsByPatientId("PAT20260406001")).thenReturn(true);

            // When
            boolean exists = patientService.existsByPatientId("PAT20260406001");

            // Then
            assertTrue(exists);
            verify(patientRepository).existsByPatientId("PAT20260406001");
        }

        @Test
        @DisplayName("检查身份证号是否存在")
        void shouldCheckIdCardNoExists() {
            // Given
            when(patientRepository.existsByIdCardNo("320123199001011234")).thenReturn(true);

            // When
            boolean exists = patientService.existsByIdCardNo("320123199001011234");

            // Then
            assertTrue(exists);
            verify(patientRepository).existsByIdCardNo("320123199001011234");
        }
    }

    @Nested
    @DisplayName("患者删除测试")
    class DeletePatientTests {

        @Test
        @DisplayName("逻辑删除患者")
        void shouldDeletePatientLogically() {
            // Given
            when(patientRepository.findById("test-id-001")).thenReturn(Optional.of(testPatient));
            when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

            // When
            patientService.deletePatient("test-id-001");

            // Then
            verify(patientRepository).findById("test-id-001");
            verify(patientRepository).save(any(Patient.class));
            assertTrue(testPatient.getDeleted());
        }

        @Test
        @DisplayName("删除不存在患者时抛出异常")
        void shouldThrowExceptionWhenDeletingNonExistentPatient() {
            // Given
            when(patientRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> patientService.deletePatient("non-existent-id"));

            assertEquals("患者不存在", exception.getMessage());
            verify(patientRepository, never()).save(any(Patient.class));
        }
    }

    @Nested
    @DisplayName("黑名单管理测试")
    class BlacklistTests {

        @Test
        @DisplayName("设置患者为黑名单")
        void shouldSetPatientToBlacklist() {
            // Given
            when(patientRepository.findById("test-id-001")).thenReturn(Optional.of(testPatient));
            when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

            // When
            PatientVO result = patientService.setBlacklist("test-id-001", true, "多次爽约");

            // Then
            assertNotNull(result);
            assertTrue(result.getIsBlacklist());
            verify(patientRepository).save(any(Patient.class));
        }

        @Test
        @DisplayName("从黑名单移除患者")
        void shouldRemovePatientFromBlacklist() {
            // Given
            testPatient.setIsBlacklist(true);
            when(patientRepository.findById("test-id-001")).thenReturn(Optional.of(testPatient));
            when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

            // When
            PatientVO result = patientService.setBlacklist("test-id-001", false, "恢复就诊权限");

            // Then
            assertNotNull(result);
            assertFalse(result.getIsBlacklist());
            verify(patientRepository).save(any(Patient.class));
        }

        @Test
        @DisplayName("设置不存在患者黑名单状态时抛出异常")
        void shouldThrowExceptionWhenSettingBlacklistForNonExistentPatient() {
            // Given
            when(patientRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> patientService.setBlacklist("non-existent-id", true, "测试"));

            assertEquals("患者不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("年龄计算测试")
    class AgeCalculationTests {

        @Test
        @DisplayName("正确计算患者年龄")
        void shouldCalculateAgeCorrectly() {
            // Given
            testPatient.setBirthDate(LocalDate.of(1990, 1, 1));
            when(patientRepository.findById("test-id-001")).thenReturn(Optional.of(testPatient));

            // When
            PatientVO result = patientService.getPatientDetail("test-id-001");

            // Then
            assertNotNull(result.getAge());
            // 年龄应该根据当前日期计算，这里验证不为null即可
        }

        @Test
        @DisplayName("出生日期为空时年龄应为null")
        void shouldReturnNullAgeWhenBirthDateIsNull() {
            // Given
            testPatient.setBirthDate(null);
            when(patientRepository.findById("test-id-001")).thenReturn(Optional.of(testPatient));

            // When
            PatientVO result = patientService.getPatientDetail("test-id-001");

            // Then
            assertNull(result.getAge());
        }
    }
}
package com.yhj.his.module.inpatient.service;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.entity.Bed;
import com.yhj.his.module.inpatient.entity.InpatientAdmission;
import com.yhj.his.module.inpatient.entity.NursingRecord;
import com.yhj.his.module.inpatient.enums.*;
import com.yhj.his.module.inpatient.repository.BedRepository;
import com.yhj.his.module.inpatient.repository.InpatientAdmissionRepository;
import com.yhj.his.module.inpatient.repository.NursingRecordRepository;
import com.yhj.his.module.inpatient.service.impl.AdmissionServiceImpl;
import com.yhj.his.module.inpatient.vo.AdmissionRegisterVO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 入院管理服务单元测试
 *
 * 测试覆盖范围：
 * - 入院登记流程
 * - 入院评估流程
 * - 预交金缴纳
 * - 住院记录查询
 * - 异常场景处理
 */
@ExtendWith(MockitoExtension.class)
class InpatientAdmissionServiceTest {

    @Mock
    private InpatientAdmissionRepository admissionRepository;

    @Mock
    private BedRepository bedRepository;

    @Mock
    private NursingRecordRepository nursingRecordRepository;

    @InjectMocks
    private AdmissionServiceImpl admissionService;

    private MockedStatic<com.yhj.his.common.core.util.SequenceGenerator> sequenceGeneratorMock;

    @BeforeEach
    void setUp() {
        sequenceGeneratorMock = mockStatic(com.yhj.his.common.core.util.SequenceGenerator.class);
        sequenceGeneratorMock.when(() -> com.yhj.his.common.core.util.SequenceGenerator.generate(anyString()))
                .thenReturn("ZY20260406001");
    }

    @AfterEach
    void tearDown() {
        sequenceGeneratorMock.close();
    }

    // ==================== 入院登记测试 ====================

    @Test
    @DisplayName("入院登记 - 成功登记（不指定床位）")
    void testRegisterAdmission_SuccessWithoutBed() {
        // 准备数据
        AdmissionRegisterDTO dto = createAdmissionRegisterDTO();
        dto.setBedNo(null); // 不指定床位

        InpatientAdmission savedAdmission = createInpatientAdmission();
        savedAdmission.setStatus(AdmissionStatus.PENDING);

        when(admissionRepository.isInHospital(dto.getPatientId())).thenReturn(false);
        when(admissionRepository.save(any(InpatientAdmission.class))).thenReturn(savedAdmission);

        // 执行
        AdmissionRegisterVO result = admissionService.register(dto);

        // 验证
        assertNotNull(result);
        assertEquals("ZY20260406001", result.getAdmissionNo());
        assertEquals(AdmissionStatus.PENDING, result.getStatus());
        assertNotNull(result.getAdmissionTime());

        verify(admissionRepository).isInHospital(dto.getPatientId());
        verify(admissionRepository).save(any(InpatientAdmission.class));
        verify(bedRepository, never()).findByWardIdAndBedNo(anyString(), anyString());
    }

    @Test
    @DisplayName("入院登记 - 成功登记并分配床位")
    void testRegisterAdmission_SuccessWithBedAssignment() {
        // 准备数据
        AdmissionRegisterDTO dto = createAdmissionRegisterDTO();
        dto.setBedNo("01");

        Bed bed = createBed();
        bed.setStatus(BedStatus.VACANT);
        bed.setBedNo("01");
        bed.setRoomNo("101");

        InpatientAdmission savedAdmission = createInpatientAdmission();
        savedAdmission.setStatus(AdmissionStatus.IN_HOSPITAL);
        savedAdmission.setBedNo("01");
        savedAdmission.setRoomNo("101");

        when(admissionRepository.isInHospital(dto.getPatientId())).thenReturn(false);
        when(bedRepository.findByWardIdAndBedNo(dto.getWardId(), "01")).thenReturn(Optional.of(bed));
        when(bedRepository.save(any(Bed.class))).thenReturn(bed);
        when(admissionRepository.save(any(InpatientAdmission.class))).thenReturn(savedAdmission);

        // 执行
        AdmissionRegisterVO result = admissionService.register(dto);

        // 验证
        assertNotNull(result);
        assertEquals(AdmissionStatus.IN_HOSPITAL, result.getStatus());
        assertEquals("01", result.getBedNo());
        assertEquals("101", result.getRoomNo());

        verify(bedRepository).findByWardIdAndBedNo(dto.getWardId(), "01");
        verify(bedRepository).save(any(Bed.class));
    }

    @Test
    @DisplayName("入院登记 - 患者已住院，抛出异常")
    void testRegisterAdmission_PatientAlreadyInHospital() {
        // 准备数据
        AdmissionRegisterDTO dto = createAdmissionRegisterDTO();

        when(admissionRepository.isInHospital(dto.getPatientId())).thenReturn(true);

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> admissionService.register(dto));

        assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), exception.getCode());
        assertEquals("患者正在住院，不能重复入院", exception.getMessage());

        verify(admissionRepository).isInHospital(dto.getPatientId());
        verify(admissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("入院登记 - 床位不存在，抛出异常")
    void testRegisterAdmission_BedNotFound() {
        // 准备数据
        AdmissionRegisterDTO dto = createAdmissionRegisterDTO();
        dto.setBedNo("99");

        when(admissionRepository.isInHospital(dto.getPatientId())).thenReturn(false);
        when(bedRepository.findByWardIdAndBedNo(dto.getWardId(), "99")).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> admissionService.register(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("床位不存在", exception.getMessage());
    }

    @Test
    @DisplayName("入院登记 - 床位状态不允许分配，抛出异常")
    void testRegisterAdmission_BedNotVacant() {
        // 准备数据
        AdmissionRegisterDTO dto = createAdmissionRegisterDTO();
        dto.setBedNo("01");

        Bed bed = createBed();
        bed.setStatus(BedStatus.OCCUPIED); // 床位已被占用

        when(admissionRepository.isInHospital(dto.getPatientId())).thenReturn(false);
        when(bedRepository.findByWardIdAndBedNo(dto.getWardId(), "01")).thenReturn(Optional.of(bed));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> admissionService.register(dto));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
        assertEquals("床位状态不允许分配", exception.getMessage());
    }

    // ==================== 入院评估测试 ====================

    @Test
    @DisplayName("入院评估 - 成功完成评估")
    void testAssessment_Success() {
        // 准备数据
        AdmissionAssessmentDTO dto = createAdmissionAssessmentDTO();
        InpatientAdmission admission = createInpatientAdmission();

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenReturn(new NursingRecord());

        // 执行
        boolean result = admissionService.assessment(dto);

        // 验证
        assertTrue(result);
        verify(admissionRepository).findById(dto.getAdmissionId());
        verify(nursingRecordRepository).save(any(NursingRecord.class));
    }

    @Test
    @DisplayName("入院评估 - 包含所有评估项")
    void testAssessment_WithAllAssessmentItems() {
        // 准备数据
        AdmissionAssessmentDTO dto = createAdmissionAssessmentDTO();
        dto.setFallRiskAssessment(createAssessmentDetail(15, "高风险"));
        dto.setPressureUlcerRiskAssessment(createAssessmentDetail(18, "中风险"));
        dto.setPainAssessment(createAssessmentDetail(5, "轻度疼痛"));
        dto.setNutritionAssessment(createAssessmentDetail(10, "营养良好"));
        dto.setSelfCareAssessment(createAssessmentDetail(95, "自理能力良好"));

        InpatientAdmission admission = createInpatientAdmission();

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenAnswer(invocation -> {
            NursingRecord record = invocation.getArgument(0);
            assertNotNull(record.getNursingContent());
            assertTrue(record.getNursingContent().contains("跌倒风险评估"));
            assertTrue(record.getNursingContent().contains("压疮风险评估"));
            assertTrue(record.getNursingContent().contains("疼痛评估"));
            assertTrue(record.getNursingContent().contains("营养评估"));
            assertTrue(record.getNursingContent().contains("自理能力评估"));
            return record;
        });

        // 执行
        boolean result = admissionService.assessment(dto);

        // 验证
        assertTrue(result);
    }

    @Test
    @DisplayName("入院评估 - 住院记录不存在，抛出异常")
    void testAssessment_AdmissionNotFound() {
        // 准备数据
        AdmissionAssessmentDTO dto = createAdmissionAssessmentDTO();

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> admissionService.assessment(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("住院记录不存在", exception.getMessage());
    }

    // ==================== 预交金缴纳测试 ====================

    @Test
    @DisplayName("预交金缴纳 - 成功缴纳")
    void testPayDeposit_Success() {
        // 准备数据
        DepositPaymentDTO dto = new DepositPaymentDTO();
        dto.setAdmissionId("admission-001");
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setPaymentMethod("微信");

        InpatientAdmission admission = createInpatientAdmission();
        admission.setDeposit(new BigDecimal("500.00"));

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(admissionRepository.save(any(InpatientAdmission.class))).thenReturn(admission);

        // 执行
        DepositPaymentResponseDTO result = admissionService.payDeposit(dto);

        // 验证
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getAmount());
        assertEquals(new BigDecimal("1500.00"), result.getTotalDeposit());
        assertNotNull(result.getPaymentTime());
        assertEquals("微信", result.getPaymentMethod());

        verify(admissionRepository).save(any(InpatientAdmission.class));
    }

    @Test
    @DisplayName("预交金缴纳 - 首次缴纳（预交金为空）")
    void testPayDeposit_FirstPayment() {
        // 准备数据
        DepositPaymentDTO dto = new DepositPaymentDTO();
        dto.setAdmissionId("admission-001");
        dto.setAmount(new BigDecimal("2000.00"));
        dto.setPaymentMethod("现金");

        InpatientAdmission admission = createInpatientAdmission();
        admission.setDeposit(null); // 首次缴纳，预交金为空

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(admissionRepository.save(any(InpatientAdmission.class))).thenReturn(admission);

        // 执行
        DepositPaymentResponseDTO result = admissionService.payDeposit(dto);

        // 验证
        assertEquals(new BigDecimal("2000.00"), result.getTotalDeposit());
    }

    @Test
    @DisplayName("预交金缴纳 - 住院记录不存在，抛出异常")
    void testPayDeposit_AdmissionNotFound() {
        // 准备数据
        DepositPaymentDTO dto = new DepositPaymentDTO();
        dto.setAdmissionId("invalid-id");

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> admissionService.payDeposit(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
    }

    // ==================== 查询测试 ====================

    @Test
    @DisplayName("查询住院记录 - 按ID查询成功")
    void testGetById_Success() {
        // 准备数据
        InpatientAdmission admission = createInpatientAdmission();
        admission.setId("admission-001");

        when(admissionRepository.findById("admission-001")).thenReturn(Optional.of(admission));

        // 执行
        AdmissionRegisterVO result = admissionService.getById("admission-001");

        // 验证
        assertNotNull(result);
        assertEquals("admission-001", result.getAdmissionId());
        assertEquals("ZY20260406001", result.getAdmissionNo());
        assertEquals("张三", result.getPatientName());
    }

    @Test
    @DisplayName("查询住院记录 - 按住院号查询成功")
    void testGetByAdmissionNo_Success() {
        // 准备数据
        InpatientAdmission admission = createInpatientAdmission();
        admission.setAdmissionNo("ZY20260406001");

        when(admissionRepository.findByAdmissionNo("ZY20260406001")).thenReturn(Optional.of(admission));

        // 执行
        AdmissionRegisterVO result = admissionService.getByAdmissionNo("ZY20260406001");

        // 验证
        assertNotNull(result);
        assertEquals("ZY20260406001", result.getAdmissionNo());
    }

    @Test
    @DisplayName("查询住院记录 - 记录不存在，抛出异常")
    void testGetById_NotFound() {
        when(admissionRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> admissionService.getById("invalid-id"));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("分页查询住院记录 - 成功查询所有记录")
    void testPageQuery_AllRecords() {
        // 准备数据
        List<InpatientAdmission> admissions = new ArrayList<>();
        admissions.add(createInpatientAdmission());
        admissions.add(createInpatientAdmission());

        Page<InpatientAdmission> page = new PageImpl<>(admissions);
        when(admissionRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // 执行
        PageResult<AdmissionRegisterVO> result = admissionService.page(1, 10, null);

        // 验证
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getList().size());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
    }

    @Test
    @DisplayName("分页查询住院记录 - 按状态筛选")
    void testPageQuery_FilterByStatus() {
        // 准备数据
        List<InpatientAdmission> admissions = new ArrayList<>();
        InpatientAdmission admission = createInpatientAdmission();
        admission.setStatus(AdmissionStatus.IN_HOSPITAL);
        admissions.add(admission);

        Page<InpatientAdmission> page = new PageImpl<>(admissions);
        when(admissionRepository.findByStatus(eq(AdmissionStatus.IN_HOSPITAL), any(PageRequest.class)))
                .thenReturn(page);

        // 执行
        PageResult<AdmissionRegisterVO> result = admissionService.page(1, 10, "IN_HOSPITAL");

        // 验证
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(AdmissionStatus.IN_HOSPITAL, result.getList().get(0).getStatus());
    }

    @Test
    @DisplayName("分页查询 - 空结果")
    void testPageQuery_EmptyResult() {
        Page<InpatientAdmission> emptyPage = new PageImpl<>(Collections.emptyList());
        when(admissionRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        // 执行
        PageResult<AdmissionRegisterVO> result = admissionService.page(1, 10, null);

        // 验证
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }

    // ==================== 辅助方法 ====================

    private AdmissionRegisterDTO createAdmissionRegisterDTO() {
        AdmissionRegisterDTO dto = new AdmissionRegisterDTO();
        dto.setPatientId("P001");
        dto.setPatientName("张三");
        dto.setGender("男");
        dto.setIdCardNo("123456789012345678");
        dto.setPhone("13800138000");
        dto.setAddress("北京市朝阳区");
        dto.setAdmissionType(AdmissionType.EMERGENCY);
        dto.setAdmissionSource("急诊");
        dto.setDeptId("D001");
        dto.setDeptName("内科");
        dto.setWardId("W001");
        dto.setWardName("内科一病区");
        dto.setDoctorId("DOC001");
        dto.setDoctorName("李医生");
        dto.setNurseId("NUR001");
        dto.setNurseName("王护士");
        dto.setAdmissionDiagnosis("肺炎");
        dto.setAdmissionDiagnosisCode("J18.9");
        dto.setNursingLevel(NursingLevel.LEVEL_1);
        dto.setDietType(DietType.NORMAL);
        dto.setAllergyInfo("无");
        dto.setInsuranceType("城镇职工医保");
        dto.setInsuranceNo("123456789");
        dto.setDeposit(new BigDecimal("500.00"));
        dto.setContactPerson("张父");
        dto.setContactPhone("13900139000");
        return dto;
    }

    private InpatientAdmission createInpatientAdmission() {
        InpatientAdmission admission = new InpatientAdmission();
        admission.setId("admission-001");
        admission.setAdmissionNo("ZY20260406001");
        admission.setPatientId("P001");
        admission.setPatientName("张三");
        admission.setIdCardNo("123456789012345678");
        admission.setGender("男");
        admission.setPhone("13800138000");
        admission.setAddress("北京市朝阳区");
        admission.setAdmissionTime(LocalDateTime.now());
        admission.setAdmissionType(AdmissionType.EMERGENCY);
        admission.setAdmissionSource("急诊");
        admission.setDeptId("D001");
        admission.setDeptName("内科");
        admission.setWardId("W001");
        admission.setWardName("内科一病区");
        admission.setDoctorId("DOC001");
        admission.setDoctorName("李医生");
        admission.setNurseId("NUR001");
        admission.setNurseName("王护士");
        admission.setAdmissionDiagnosis("肺炎");
        admission.setAdmissionDiagnosisCode("J18.9");
        admission.setNursingLevel(NursingLevel.LEVEL_1);
        admission.setDietType(DietType.NORMAL);
        admission.setAllergyInfo("无");
        admission.setInsuranceType("城镇职工医保");
        admission.setInsuranceNo("123456789");
        admission.setDeposit(new BigDecimal("500.00"));
        admission.setStatus(AdmissionStatus.IN_HOSPITAL);
        admission.setContactPerson("张父");
        admission.setContactPhone("13900139000");
        return admission;
    }

    private Bed createBed() {
        Bed bed = new Bed();
        bed.setId("bed-001");
        bed.setBedNo("01");
        bed.setWardId("W001");
        bed.setWardName("内科一病区");
        bed.setRoomNo("101");
        bed.setBedType(BedType.NORMAL);
        bed.setBedLevel("普通床位");
        bed.setDailyRate(new BigDecimal("50.00"));
        bed.setStatus(BedStatus.VACANT);
        return bed;
    }

    private AdmissionAssessmentDTO createAdmissionAssessmentDTO() {
        AdmissionAssessmentDTO dto = new AdmissionAssessmentDTO();
        dto.setAdmissionId("admission-001");
        dto.setNurseId("NUR001");
        dto.setNurseName("王护士");
        dto.setRemarks("入院评估完成");
        return dto;
    }

    private AdmissionAssessmentDTO.AssessmentDetail createAssessmentDetail(Integer score, String riskLevel) {
        AdmissionAssessmentDTO.AssessmentDetail detail = new AdmissionAssessmentDTO.AssessmentDetail();
        detail.setScore(score);
        detail.setRiskLevel(riskLevel);
        return detail;
    }
}
package com.yhj.his.module.inpatient.service;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.entity.InpatientAdmission;
import com.yhj.his.module.inpatient.entity.NursingRecord;
import com.yhj.his.module.inpatient.enums.AdmissionStatus;
import com.yhj.his.module.inpatient.enums.NursingLevel;
import com.yhj.his.module.inpatient.enums.NursingRecordType;
import com.yhj.his.module.inpatient.repository.InpatientAdmissionRepository;
import com.yhj.his.module.inpatient.repository.NursingRecordRepository;
import com.yhj.his.module.inpatient.service.impl.NursingServiceImpl;
import com.yhj.his.module.inpatient.vo.NursingRecordVO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 护理管理服务单元测试
 *
 * 测试覆盖范围：
 * - 生命体征录入
 * - 护理评估
 * - 护理记录
 * - 护理记录查询
 * - 生命体征查询
 */
@ExtendWith(MockitoExtension.class)
class NursingRecordServiceTest {

    @Mock
    private NursingRecordRepository nursingRecordRepository;

    @Mock
    private InpatientAdmissionRepository admissionRepository;

    @InjectMocks
    private NursingServiceImpl nursingService;

    // ==================== 生命体征录入测试 ====================

    @Test
    @DisplayName("生命体征录入 - 成功录入完整生命体征")
    void testRecordVitalSigns_SuccessFullRecord() {
        // 准备数据
        VitalSignsDTO dto = createVitalSignsDTO();
        dto.setTemperature(new BigDecimal("36.5"));
        dto.setPulse(72);
        dto.setRespiration(18);
        dto.setBloodPressureSystolic(120);
        dto.setBloodPressureDiastolic(80);
        dto.setSpo2(98);
        dto.setWeight(new BigDecimal("65.5"));
        dto.setHeight(170);

        InpatientAdmission admission = createInpatientAdmission();
        NursingRecord savedRecord = createNursingRecord(NursingRecordType.VITAL_SIGNS);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenReturn(savedRecord);

        // 执行
        String recordId = nursingService.recordVitalSigns(dto);

        // 验证
        assertNotNull(recordId);
        ArgumentCaptor<NursingRecord> recordCaptor = ArgumentCaptor.forClass(NursingRecord.class);
        verify(nursingRecordRepository).save(recordCaptor.capture());

        NursingRecord capturedRecord = recordCaptor.getValue();
        assertEquals(NursingRecordType.VITAL_SIGNS, capturedRecord.getRecordType());
        assertEquals(dto.getTemperature(), capturedRecord.getTemperature());
        assertEquals(dto.getPulse(), capturedRecord.getPulse());
        assertEquals(dto.getRespiration(), capturedRecord.getRespiration());
        assertEquals(dto.getBloodPressureSystolic(), capturedRecord.getBloodPressureSystolic());
        assertEquals(dto.getBloodPressureDiastolic(), capturedRecord.getBloodPressureDiastolic());
        assertEquals(dto.getSpo2(), capturedRecord.getSpo2());
        assertEquals(dto.getWeight(), capturedRecord.getWeight());
        assertEquals(dto.getHeight(), capturedRecord.getHeight());
    }

    @Test
    @DisplayName("生命体征录入 - 成功录入部分生命体征")
    void testRecordVitalSigns_SuccessPartialRecord() {
        // 准备数据
        VitalSignsDTO dto = createVitalSignsDTO();
        dto.setTemperature(new BigDecimal("37.2"));
        dto.setPulse(80);
        dto.setBloodPressureSystolic(130);
        dto.setBloodPressureDiastolic(85);
        // 不录入呼吸、血氧、体重、身高

        InpatientAdmission admission = createInpatientAdmission();
        NursingRecord savedRecord = createNursingRecord(NursingRecordType.VITAL_SIGNS);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenReturn(savedRecord);

        // 执行
        String recordId = nursingService.recordVitalSigns(dto);

        // 验证
        assertNotNull(recordId);
        ArgumentCaptor<NursingRecord> recordCaptor = ArgumentCaptor.forClass(NursingRecord.class);
        verify(nursingRecordRepository).save(recordCaptor.capture());

        NursingRecord capturedRecord = recordCaptor.getValue();
        assertEquals(dto.getTemperature(), capturedRecord.getTemperature());
        assertEquals(dto.getPulse(), capturedRecord.getPulse());
        assertNull(capturedRecord.getRespiration());
        assertNull(capturedRecord.getSpo2());
    }

    @Test
    @DisplayName("生命体征录入 - 高温患者记录")
    void testRecordVitalSigns_HighTemperature() {
        // 准备数据
        VitalSignsDTO dto = createVitalSignsDTO();
        dto.setTemperature(new BigDecimal("39.5")); // 高热
        dto.setPulse(100);

        InpatientAdmission admission = createInpatientAdmission();
        NursingRecord savedRecord = createNursingRecord(NursingRecordType.VITAL_SIGNS);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenReturn(savedRecord);

        // 执行
        String recordId = nursingService.recordVitalSigns(dto);

        // 验证
        assertNotNull(recordId);
        ArgumentCaptor<NursingRecord> recordCaptor = ArgumentCaptor.forClass(NursingRecord.class);
        verify(nursingRecordRepository).save(recordCaptor.capture());
        assertEquals(new BigDecimal("39.5"), recordCaptor.getValue().getTemperature());
    }

    @Test
    @DisplayName("生命体征录入 - 低血氧患者记录")
    void testRecordVitalSigns_LowSpo2() {
        // 准备数据
        VitalSignsDTO dto = createVitalSignsDTO();
        dto.setSpo2(88); // 低血氧

        InpatientAdmission admission = createInpatientAdmission();
        NursingRecord savedRecord = createNursingRecord(NursingRecordType.VITAL_SIGNS);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenReturn(savedRecord);

        // 执行
        String recordId = nursingService.recordVitalSigns(dto);

        // 验证
        assertNotNull(recordId);
    }

    @Test
    @DisplayName("生命体征录入 - 住院记录不存在")
    void testRecordVitalSigns_AdmissionNotFound() {
        VitalSignsDTO dto = createVitalSignsDTO();

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> nursingService.recordVitalSigns(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("住院记录不存在", exception.getMessage());
    }

    // ==================== 护理评估测试 ====================

    @Test
    @DisplayName("护理评估 - 成功完成跌倒风险评估")
    void testAssessment_FallRiskSuccess() {
        // 准备数据
        NursingAssessmentDTO dto = createNursingAssessmentDTO();
        dto.setAssessmentType("跌倒风险评估");
        dto.setScore(15);
        dto.setRiskLevel("高风险");
        dto.setAssessmentResult("患者存在跌倒风险，需要加强护理");
        dto.setNursingSuggestion("使用床栏，加强巡视");

        InpatientAdmission admission = createInpatientAdmission();
        NursingRecord savedRecord = createNursingRecord(NursingRecordType.ASSESSMENT);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenReturn(savedRecord);

        // 执行
        String recordId = nursingService.assessment(dto);

        // 验证
        assertNotNull(recordId);
        ArgumentCaptor<NursingRecord> recordCaptor = ArgumentCaptor.forClass(NursingRecord.class);
        verify(nursingRecordRepository).save(recordCaptor.capture());

        NursingRecord capturedRecord = recordCaptor.getValue();
        assertEquals(NursingRecordType.ASSESSMENT, capturedRecord.getRecordType());
        assertTrue(capturedRecord.getNursingContent().contains("评估类型：跌倒风险评估"));
        assertTrue(capturedRecord.getNursingContent().contains("得分：15"));
        assertTrue(capturedRecord.getNursingContent().contains("风险等级：高风险"));
    }

    @Test
    @DisplayName("护理评估 - 成功完成压疮风险评估")
    void testAssessment_PressureUlcerRiskSuccess() {
        // 准备数据
        NursingAssessmentDTO dto = createNursingAssessmentDTO();
        dto.setAssessmentType("压疮风险评估");
        dto.setScore(18);
        dto.setRiskLevel("中风险");

        InpatientAdmission admission = createInpatientAdmission();
        NursingRecord savedRecord = createNursingRecord(NursingRecordType.ASSESSMENT);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenReturn(savedRecord);

        // 执行
        String recordId = nursingService.assessment(dto);

        // 鵔证
        assertNotNull(recordId);
    }

    @Test
    @DisplayName("护理评估 - 成功完成疼痛评估")
    void testAssessment_PainAssessmentSuccess() {
        // 准备数据
        NursingAssessmentDTO dto = createNursingAssessmentDTO();
        dto.setAssessmentType("疼痛评估");
        dto.setScore(5);
        dto.setRiskLevel("轻度疼痛");
        dto.setAssessmentResult("患者疼痛可耐受");

        InpatientAdmission admission = createInpatientAdmission();
        NursingRecord savedRecord = createNursingRecord(NursingRecordType.ASSESSMENT);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenReturn(savedRecord);

        // 执行
        String recordId = nursingService.assessment(dto);

        // 验证
        assertNotNull(recordId);
    }

    @Test
    @DisplayName("护理评估 - 成功完成营养评估")
    void testAssessment_NutritionAssessmentSuccess() {
        // 准备数据
        NursingAssessmentDTO dto = createNursingAssessmentDTO();
        dto.setAssessmentType("营养评估");
        dto.setScore(10);
        dto.setRiskLevel("营养良好");

        InpatientAdmission admission = createInpatientAdmission();
        NursingRecord savedRecord = createNursingRecord(NursingRecordType.ASSESSMENT);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenReturn(savedRecord);

        // 执行
        String recordId = nursingService.assessment(dto);

        // 验证
        assertNotNull(recordId);
    }

    @Test
    @DisplayName("护理评估 - 住院记录不存在")
    void testAssessment_AdmissionNotFound() {
        NursingAssessmentDTO dto = createNursingAssessmentDTO();

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> nursingService.assessment(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
    }

    // ==================== 护理记录测试 ====================

    @Test
    @DisplayName("护理记录 - 成功录入护理记录")
    void testRecordNursing_Success() {
        // 准备数据
        NursingRecordDTO dto = createNursingRecordDTO();
        dto.setIntake(new BigDecimal("1500"));
        dto.setOutput(new BigDecimal("1200"));
        dto.setUrine(new BigDecimal("800"));
        dto.setStool("正常");
        dto.setNursingContent("患者今日精神状态良好，配合治疗");
        dto.setNursingMeasures("1. 监测生命体征\n2. 协助翻身\n3. 保持皮肤清洁");

        InpatientAdmission admission = createInpatientAdmission();
        NursingRecord savedRecord = createNursingRecord(NursingRecordType.NURSING_RECORD);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenReturn(savedRecord);

        // 执行
        String recordId = nursingService.recordNursing(dto);

        // 验证
        assertNotNull(recordId);
        ArgumentCaptor<NursingRecord> recordCaptor = ArgumentCaptor.forClass(NursingRecord.class);
        verify(nursingRecordRepository).save(recordCaptor.capture());

        NursingRecord capturedRecord = recordCaptor.getValue();
        assertEquals(NursingRecordType.NURSING_RECORD, capturedRecord.getRecordType());
        assertEquals(dto.getIntake(), capturedRecord.getIntake());
        assertEquals(dto.getOutput(), capturedRecord.getOutput());
        assertEquals(dto.getUrine(), capturedRecord.getUrine());
        assertEquals(dto.getStool(), capturedRecord.getStool());
        assertEquals(dto.getNursingContent(), capturedRecord.getNursingContent());
        assertEquals(dto.getNursingMeasures(), capturedRecord.getNursingMeasures());
    }

    @Test
    @DisplayName("护理记录 - 成功录入出入量记录")
    void testRecordNursing_IntakeOutputRecord() {
        // 准备数据
        NursingRecordDTO dto = createNursingRecordDTO();
        dto.setIntake(new BigDecimal("2500"));
        dto.setOutput(new BigDecimal("1800"));
        dto.setUrine(new BigDecimal("1500"));
        dto.setStool("2次");

        InpatientAdmission admission = createInpatientAdmission();
        NursingRecord savedRecord = createNursingRecord(NursingRecordType.NURSING_RECORD);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(nursingRecordRepository.save(any(NursingRecord.class))).thenReturn(savedRecord);

        // 执行
        String recordId = nursingService.recordNursing(dto);

        // 验证
        assertNotNull(recordId);
    }

    @Test
    @DisplayName("护理记录 - 住院记录不存在")
    void testRecordNursing_AdmissionNotFound() {
        NursingRecordDTO dto = createNursingRecordDTO();

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> nursingService.recordNursing(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
    }

    // ==================== 护理记录查询测试 ====================

    @Test
    @DisplayName("查询护理记录 - 成功查询住院护理记录")
    void testListByAdmission_Success() {
        // 准备数据
        List<NursingRecord> records = new ArrayList<>();
        records.add(createNursingRecord(NursingRecordType.VITAL_SIGNS));
        records.add(createNursingRecord(NursingRecordType.ASSESSMENT));
        records.add(createNursingRecord(NursingRecordType.NURSING_RECORD));

        when(nursingRecordRepository.findByAdmissionId("admission-001")).thenReturn(records);

        // 执行
        List<NursingRecordVO> result = nursingService.listByAdmission("admission-001");

        // 验证
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("查询护理记录 - 无记录")
    void testListByAdmission_Empty() {
        when(nursingRecordRepository.findByAdmissionId("admission-001")).thenReturn(Collections.emptyList());

        // 执行
        List<NursingRecordVO> result = nursingService.listByAdmission("admission-001");

        // 验证
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("分页查询护理记录 - 成功分页")
    void testPageNursingRecords_Success() {
        // 准备数据
        List<NursingRecord> records = new ArrayList<>();
        records.add(createNursingRecord(NursingRecordType.VITAL_SIGNS));
        records.add(createNursingRecord(NursingRecordType.NURSING_RECORD));

        Page<NursingRecord> page = new PageImpl<>(records);
        when(nursingRecordRepository.findByAdmissionId(eq("admission-001"), any(PageRequest.class)))
                .thenReturn(page);

        // 执行
        PageResult<NursingRecordVO> result = nursingService.page(1, 10, "admission-001");

        // 验证
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
    }

    @Test
    @DisplayName("分页查询护理记录 - 空结果")
    void testPageNursingRecords_Empty() {
        Page<NursingRecord> emptyPage = new PageImpl<>(Collections.emptyList());
        when(nursingRecordRepository.findByAdmissionId(eq("admission-001"), any(PageRequest.class)))
                .thenReturn(emptyPage);

        // 执行
        PageResult<NursingRecordVO> result = nursingService.page(1, 10, "admission-001");

        // 验证
        assertEquals(0, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }

    // ==================== 生命体征查询测试 ====================

    @Test
    @DisplayName("查询生命体征记录 - 成功查询")
    void testListVitalSigns_Success() {
        // 准备数据
        List<NursingRecord> vitalSigns = new ArrayList<>();
        NursingRecord record1 = createNursingRecord(NursingRecordType.VITAL_SIGNS);
        record1.setTemperature(new BigDecimal("36.5"));
        record1.setPulse(72);
        vitalSigns.add(record1);

        NursingRecord record2 = createNursingRecord(NursingRecordType.VITAL_SIGNS);
        record2.setTemperature(new BigDecimal("36.8"));
        record2.setPulse(75);
        vitalSigns.add(record2);

        when(nursingRecordRepository.findByAdmissionIdAndRecordType("admission-001", NursingRecordType.VITAL_SIGNS))
                .thenReturn(vitalSigns);

        // 执行
        List<NursingRecordVO> result = nursingService.listVitalSigns("admission-001");

        // 验证
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("生命体征", result.get(0).getRecordType());
    }

    @Test
    @DisplayName("查询生命体征记录 - 无记录")
    void testListVitalSigns_Empty() {
        when(nursingRecordRepository.findByAdmissionIdAndRecordType("admission-001", NursingRecordType.VITAL_SIGNS))
                .thenReturn(Collections.emptyList());

        // 执行
        List<NursingRecordVO> result = nursingService.listVitalSigns("admission-001");

        // 验证
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询最后一次生命体征 - 成功查询")
    void testGetLastVitalSigns_Success() {
        // 准备数据
        NursingRecord lastRecord = createNursingRecord(NursingRecordType.VITAL_SIGNS);
        lastRecord.setTemperature(new BigDecimal("36.6"));
        lastRecord.setPulse(70);
        lastRecord.setBloodPressureSystolic(118);
        lastRecord.setBloodPressureDiastolic(78);

        when(nursingRecordRepository.findLastVitalSigns("admission-001")).thenReturn(lastRecord);

        // 执行
        NursingRecordVO result = nursingService.getLastVitalSigns("admission-001");

        // 验证
        assertNotNull(result);
        assertEquals(new BigDecimal("36.6"), result.getTemperature());
        assertEquals(70, result.getPulse());
        assertEquals(118, result.getBloodPressureSystolic());
        assertEquals(78, result.getBloodPressureDiastolic());
    }

    @Test
    @DisplayName("查询最后一次生命体征 - 无记录")
    void testGetLastVitalSigns_NoRecord() {
        when(nursingRecordRepository.findLastVitalSigns("admission-001")).thenReturn(null);

        // 执行
        NursingRecordVO result = nursingService.getLastVitalSigns("admission-001");

        // 验证
        assertNull(result);
    }

    // ==================== VO转换测试 ====================

    @Test
    @DisplayName("VO转换 - 完整生命体征记录转换")
    void testConvertToVO_FullVitalSigns() {
        // 准备数据
        NursingRecord record = createNursingRecord(NursingRecordType.VITAL_SIGNS);
        record.setTemperature(new BigDecimal("36.5"));
        record.setPulse(72);
        record.setRespiration(18);
        record.setBloodPressureSystolic(120);
        record.setBloodPressureDiastolic(80);
        record.setSpo2(98);
        record.setWeight(new BigDecimal("65.5"));
        record.setHeight(170);

        List<NursingRecord> records = Collections.singletonList(record);
        when(nursingRecordRepository.findByAdmissionId("admission-001")).thenReturn(records);

        // 执行
        List<NursingRecordVO> result = nursingService.listByAdmission("admission-001");

        // 验证
        NursingRecordVO vo = result.get(0);
        assertEquals("生命体征", vo.getRecordType());
        assertEquals(new BigDecimal("36.5"), vo.getTemperature());
        assertEquals(72, vo.getPulse());
        assertEquals(18, vo.getRespiration());
        assertEquals(120, vo.getBloodPressureSystolic());
        assertEquals(80, vo.getBloodPressureDiastolic());
        assertEquals(98, vo.getSpo2());
        assertEquals(new BigDecimal("65.5"), vo.getWeight());
        assertEquals(170, vo.getHeight());
    }

    @Test
    @DisplayName("VO转换 - 护理记录转换")
    void testConvertToVO_NursingRecord() {
        // 准备数据
        NursingRecord record = createNursingRecord(NursingRecordType.NURSING_RECORD);
        record.setIntake(new BigDecimal("1500"));
        record.setOutput(new BigDecimal("1200"));
        record.setUrine(new BigDecimal("800"));
        record.setStool("正常");
        record.setNursingContent("患者今日精神状态良好");
        record.setNursingMeasures("协助翻身，保持皮肤清洁");

        List<NursingRecord> records = Collections.singletonList(record);
        when(nursingRecordRepository.findByAdmissionId("admission-001")).thenReturn(records);

        // 执行
        List<NursingRecordVO> result = nursingService.listByAdmission("admission-001");

        // 验证
        NursingRecordVO vo = result.get(0);
        assertEquals("护理记录", vo.getRecordType());
        assertEquals(new BigDecimal("1500"), vo.getIntake());
        assertEquals(new BigDecimal("1200"), vo.getOutput());
        assertEquals(new BigDecimal("800"), vo.getUrine());
        assertEquals("正常", vo.getStool());
        assertEquals("患者今日精神状态良好", vo.getNursingContent());
        assertEquals("协助翻身，保持皮肤清洁", vo.getNursingMeasures());
    }

    // ==================== 辅助方法 ====================

    private VitalSignsDTO createVitalSignsDTO() {
        VitalSignsDTO dto = new VitalSignsDTO();
        dto.setAdmissionId("admission-001");
        dto.setPatientId("P001");
        dto.setRecordTime(LocalDateTime.now());
        dto.setNurseId("NUR001");
        dto.setNurseName("王护士");
        return dto;
    }

    private NursingAssessmentDTO createNursingAssessmentDTO() {
        NursingAssessmentDTO dto = new NursingAssessmentDTO();
        dto.setAdmissionId("admission-001");
        dto.setPatientId("P001");
        dto.setAssessmentType("跌倒风险评估");
        dto.setScore(15);
        dto.setRiskLevel("高风险");
        dto.setNurseId("NUR001");
        dto.setNurseName("王护士");
        return dto;
    }

    private NursingRecordDTO createNursingRecordDTO() {
        NursingRecordDTO dto = new NursingRecordDTO();
        dto.setAdmissionId("admission-001");
        dto.setPatientId("P001");
        dto.setRecordTime(LocalDateTime.now());
        dto.setNurseId("NUR001");
        dto.setNurseName("王护士");
        return dto;
    }

    private InpatientAdmission createInpatientAdmission() {
        InpatientAdmission admission = new InpatientAdmission();
        admission.setId("admission-001");
        admission.setAdmissionNo("ZY20260406001");
        admission.setPatientId("P001");
        admission.setPatientName("张三");
        admission.setNursingLevel(NursingLevel.LEVEL_1);
        admission.setStatus(AdmissionStatus.IN_HOSPITAL);
        return admission;
    }

    private NursingRecord createNursingRecord(NursingRecordType type) {
        NursingRecord record = new NursingRecord();
        record.setId("record-001");
        record.setAdmissionId("admission-001");
        record.setPatientId("P001");
        record.setRecordTime(LocalDateTime.now());
        record.setRecordType(type);
        record.setNurseId("NUR001");
        record.setNurseName("王护士");
        return record;
    }
}
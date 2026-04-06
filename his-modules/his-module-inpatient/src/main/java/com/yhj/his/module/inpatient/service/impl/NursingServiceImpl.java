package com.yhj.his.module.inpatient.service.impl;

import cn.hutool.json.JSONUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.entity.InpatientAdmission;
import com.yhj.his.module.inpatient.entity.NursingRecord;
import com.yhj.his.module.inpatient.enums.NursingRecordType;
import com.yhj.his.module.inpatient.repository.InpatientAdmissionRepository;
import com.yhj.his.module.inpatient.repository.NursingRecordRepository;
import com.yhj.his.module.inpatient.service.NursingService;
import com.yhj.his.module.inpatient.vo.NursingRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 护理管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NursingServiceImpl implements NursingService {

    private final NursingRecordRepository nursingRecordRepository;
    private final InpatientAdmissionRepository admissionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recordVitalSigns(VitalSignsDTO dto) {
        InpatientAdmission admission = admissionRepository.findById(dto.getAdmissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        NursingRecord record = new NursingRecord();
        record.setAdmissionId(dto.getAdmissionId());
        record.setPatientId(dto.getPatientId());
        record.setRecordTime(dto.getRecordTime());
        record.setRecordType(NursingRecordType.VITAL_SIGNS);
        record.setTemperature(dto.getTemperature());
        record.setPulse(dto.getPulse());
        record.setRespiration(dto.getRespiration());
        record.setBloodPressureSystolic(dto.getBloodPressureSystolic());
        record.setBloodPressureDiastolic(dto.getBloodPressureDiastolic());
        record.setSpo2(dto.getSpo2());
        record.setWeight(dto.getWeight());
        record.setHeight(dto.getHeight());
        record.setNurseId(dto.getNurseId());
        record.setNurseName(dto.getNurseName());

        record = nursingRecordRepository.save(record);
        log.info("生命体征录入成功，住院ID：{}，记录时间：{}", dto.getAdmissionId(), dto.getRecordTime());
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String assessment(NursingAssessmentDTO dto) {
        InpatientAdmission admission = admissionRepository.findById(dto.getAdmissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        NursingRecord record = new NursingRecord();
        record.setAdmissionId(dto.getAdmissionId());
        record.setPatientId(dto.getPatientId());
        record.setRecordTime(LocalDateTime.now());
        record.setRecordType(NursingRecordType.ASSESSMENT);
        record.setNurseId(dto.getNurseId());
        record.setNurseName(dto.getNurseName());

        // 构建评估内容
        StringBuilder content = new StringBuilder();
        content.append("评估类型：").append(dto.getAssessmentType()).append(";");
        content.append("得分：").append(dto.getScore()).append(";");
        content.append("风险等级：").append(dto.getRiskLevel()).append(";");
        if (dto.getAssessmentResult() != null) {
            content.append("评估结果：").append(dto.getAssessmentResult()).append(";");
        }
        if (dto.getNursingSuggestion() != null) {
            content.append("护理建议：").append(dto.getNursingSuggestion());
        }
        record.setNursingContent(content.toString());

        record = nursingRecordRepository.save(record);
        log.info("护理评估成功，住院ID：{}，评估类型：{}", dto.getAdmissionId(), dto.getAssessmentType());
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recordNursing(NursingRecordDTO dto) {
        InpatientAdmission admission = admissionRepository.findById(dto.getAdmissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        NursingRecord record = new NursingRecord();
        record.setAdmissionId(dto.getAdmissionId());
        record.setPatientId(dto.getPatientId());
        record.setRecordTime(dto.getRecordTime());
        record.setRecordType(NursingRecordType.NURSING_RECORD);
        record.setIntake(dto.getIntake());
        record.setOutput(dto.getOutput());
        record.setUrine(dto.getUrine());
        record.setStool(dto.getStool());
        record.setNursingContent(dto.getNursingContent());
        record.setNursingMeasures(dto.getNursingMeasures());
        record.setNurseId(dto.getNurseId());
        record.setNurseName(dto.getNurseName());

        record = nursingRecordRepository.save(record);
        log.info("护理记录成功，住院ID：{}，记录时间：{}", dto.getAdmissionId(), dto.getRecordTime());
        return record.getId();
    }

    @Override
    public List<NursingRecordVO> listByAdmission(String admissionId) {
        List<NursingRecord> records = nursingRecordRepository.findByAdmissionId(admissionId);
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<NursingRecordVO> listVitalSigns(String admissionId) {
        List<NursingRecord> records = nursingRecordRepository.findByAdmissionIdAndRecordType(
                admissionId, NursingRecordType.VITAL_SIGNS);
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public NursingRecordVO getLastVitalSigns(String admissionId) {
        NursingRecord record = nursingRecordRepository.findLastVitalSigns(admissionId);
        return record != null ? convertToVO(record) : null;
    }

    @Override
    public PageResult<NursingRecordVO> page(Integer pageNum, Integer pageSize, String admissionId) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "recordTime"));

        Page<NursingRecord> page = nursingRecordRepository.findByAdmissionId(admissionId, pageRequest);

        List<NursingRecordVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    /**
     * 转换为VO
     */
    private NursingRecordVO convertToVO(NursingRecord record) {
        NursingRecordVO vo = new NursingRecordVO();
        vo.setRecordId(record.getId());
        vo.setAdmissionId(record.getAdmissionId());
        vo.setPatientId(record.getPatientId());
        vo.setRecordTime(record.getRecordTime());
        vo.setRecordType(record.getRecordType().getCode());
        vo.setTemperature(record.getTemperature());
        vo.setPulse(record.getPulse());
        vo.setRespiration(record.getRespiration());
        vo.setBloodPressureSystolic(record.getBloodPressureSystolic());
        vo.setBloodPressureDiastolic(record.getBloodPressureDiastolic());
        vo.setSpo2(record.getSpo2());
        vo.setWeight(record.getWeight());
        vo.setHeight(record.getHeight());
        vo.setIntake(record.getIntake());
        vo.setOutput(record.getOutput());
        vo.setUrine(record.getUrine());
        vo.setStool(record.getStool());
        vo.setNursingContent(record.getNursingContent());
        vo.setNursingMeasures(record.getNursingMeasures());
        vo.setNurseName(record.getNurseName());
        return vo;
    }
}
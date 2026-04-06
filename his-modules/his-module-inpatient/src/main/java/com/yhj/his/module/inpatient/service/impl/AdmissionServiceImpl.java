package com.yhj.his.module.inpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.entity.Bed;
import com.yhj.his.module.inpatient.entity.InpatientAdmission;
import com.yhj.his.module.inpatient.entity.NursingRecord;
import com.yhj.his.module.inpatient.enums.AdmissionStatus;
import com.yhj.his.module.inpatient.enums.BedStatus;
import com.yhj.his.module.inpatient.enums.NursingRecordType;
import com.yhj.his.module.inpatient.repository.BedRepository;
import com.yhj.his.module.inpatient.repository.InpatientAdmissionRepository;
import com.yhj.his.module.inpatient.repository.NursingRecordRepository;
import com.yhj.his.module.inpatient.service.AdmissionService;
import com.yhj.his.module.inpatient.vo.AdmissionRegisterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 入院管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdmissionServiceImpl implements AdmissionService {

    private final InpatientAdmissionRepository admissionRepository;
    private final BedRepository bedRepository;
    private final NursingRecordRepository nursingRecordRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdmissionRegisterVO register(AdmissionRegisterDTO dto) {
        // 检查患者是否正在住院
        if (admissionRepository.isInHospital(dto.getPatientId())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "患者正在住院，不能重复入院");
        }

        // 创建住院记录
        InpatientAdmission admission = new InpatientAdmission();
        admission.setAdmissionNo(SequenceGenerator.generate("ZY"));
        admission.setPatientId(dto.getPatientId());
        admission.setPatientName(dto.getPatientName());
        admission.setIdCardNo(dto.getIdCardNo());
        admission.setGender(dto.getGender());
        admission.setPhone(dto.getPhone());
        admission.setAddress(dto.getAddress());
        admission.setAdmissionTime(LocalDateTime.now());
        admission.setAdmissionType(dto.getAdmissionType());
        admission.setAdmissionSource(dto.getAdmissionSource());
        admission.setDeptId(dto.getDeptId());
        admission.setDeptName(dto.getDeptName());
        admission.setWardId(dto.getWardId());
        admission.setWardName(dto.getWardName());
        admission.setDoctorId(dto.getDoctorId());
        admission.setDoctorName(dto.getDoctorName());
        admission.setNurseId(dto.getNurseId());
        admission.setNurseName(dto.getNurseName());
        admission.setAdmissionDiagnosis(dto.getAdmissionDiagnosis());
        admission.setAdmissionDiagnosisCode(dto.getAdmissionDiagnosisCode());
        admission.setNursingLevel(dto.getNursingLevel());
        admission.setDietType(dto.getDietType());
        admission.setAllergyInfo(dto.getAllergyInfo());
        admission.setInsuranceType(dto.getInsuranceType());
        admission.setInsuranceNo(dto.getInsuranceNo());
        admission.setDeposit(dto.getDeposit() != null ? dto.getDeposit() : BigDecimal.ZERO);
        admission.setContactPerson(dto.getContactPerson());
        admission.setContactPhone(dto.getContactPhone());
        admission.setStatus(AdmissionStatus.PENDING);

        // 如果指定了床位，进行床位分配
        if (dto.getBedNo() != null && !dto.getBedNo().isEmpty()) {
            assignBed(admission, dto.getWardId(), dto.getBedNo());
            admission.setStatus(AdmissionStatus.IN_HOSPITAL);
        }

        admission = admissionRepository.save(admission);
        log.info("入院登记成功，住院号：{}", admission.getAdmissionNo());

        return convertToVO(admission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assessment(AdmissionAssessmentDTO dto) {
        InpatientAdmission admission = admissionRepository.findById(dto.getAdmissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        // 创建入院评估护理记录
        NursingRecord record = new NursingRecord();
        record.setAdmissionId(dto.getAdmissionId());
        record.setPatientId(admission.getPatientId());
        record.setRecordTime(LocalDateTime.now());
        record.setRecordType(NursingRecordType.ASSESSMENT);
        record.setNurseId(dto.getNurseId());
        record.setNurseName(dto.getNurseName());

        // 构建评估详情JSON
        StringBuilder content = new StringBuilder();
        if (dto.getFallRiskAssessment() != null) {
            content.append("跌倒风险评估：得分=").append(dto.getFallRiskAssessment().getScore())
                    .append(",风险等级=").append(dto.getFallRiskAssessment().getRiskLevel()).append(";");
        }
        if (dto.getPressureUlcerRiskAssessment() != null) {
            content.append("压疮风险评估：得分=").append(dto.getPressureUlcerRiskAssessment().getScore())
                    .append(",风险等级=").append(dto.getPressureUlcerRiskAssessment().getRiskLevel()).append(";");
        }
        if (dto.getPainAssessment() != null) {
            content.append("疼痛评估：得分=").append(dto.getPainAssessment().getScore())
                    .append(",风险等级=").append(dto.getPainAssessment().getRiskLevel()).append(";");
        }
        if (dto.getNutritionAssessment() != null) {
            content.append("营养评估：得分=").append(dto.getNutritionAssessment().getScore())
                    .append(",风险等级=").append(dto.getNutritionAssessment().getRiskLevel()).append(";");
        }
        if (dto.getSelfCareAssessment() != null) {
            content.append("自理能力评估：得分=").append(dto.getSelfCareAssessment().getScore())
                    .append(",风险等级=").append(dto.getSelfCareAssessment().getRiskLevel()).append(";");
        }
        if (dto.getRemarks() != null) {
            content.append("备注：").append(dto.getRemarks());
        }
        record.setNursingContent(content.toString());

        nursingRecordRepository.save(record);
        log.info("入院评估完成，住院ID：{}", dto.getAdmissionId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DepositPaymentResponseDTO payDeposit(DepositPaymentDTO dto) {
        InpatientAdmission admission = admissionRepository.findById(dto.getAdmissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        // 更新预交金
        BigDecimal currentDeposit = admission.getDeposit() != null ? admission.getDeposit() : BigDecimal.ZERO;
        admission.setDeposit(currentDeposit.add(dto.getAmount()));
        admissionRepository.save(admission);

        DepositPaymentResponseDTO response = new DepositPaymentResponseDTO();
        response.setAdmissionId(dto.getAdmissionId());
        response.setAdmissionNo(admission.getAdmissionNo());
        response.setAmount(dto.getAmount());
        response.setTotalDeposit(admission.getDeposit());
        response.setPaymentTime(LocalDateTime.now().toString());
        response.setPaymentMethod(dto.getPaymentMethod());

        log.info("预交金缴纳成功，住院号：{}，金额：{}", admission.getAdmissionNo(), dto.getAmount());
        return response;
    }

    @Override
    public AdmissionRegisterVO getById(String admissionId) {
        InpatientAdmission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));
        return convertToVO(admission);
    }

    @Override
    public AdmissionRegisterVO getByAdmissionNo(String admissionNo) {
        InpatientAdmission admission = admissionRepository.findByAdmissionNo(admissionNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));
        return convertToVO(admission);
    }

    @Override
    public PageResult<AdmissionRegisterVO> page(Integer pageNum, Integer pageSize, String status) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<InpatientAdmission> page;
        if (status != null && !status.isEmpty()) {
            AdmissionStatus admissionStatus = AdmissionStatus.valueOf(status);
            page = admissionRepository.findByStatus(admissionStatus, pageRequest);
        } else {
            page = admissionRepository.findAll(pageRequest);
        }

        List<AdmissionRegisterVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    /**
     * 分配床位
     */
    private void assignBed(InpatientAdmission admission, String wardId, String bedNo) {
        Bed bed = bedRepository.findByWardIdAndBedNo(wardId, bedNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "床位不存在"));

        if (bed.getStatus() != BedStatus.VACANT) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "床位状态不允许分配");
        }

        bed.setStatus(BedStatus.OCCUPIED);
        bed.setAdmissionId(admission.getId());
        bed.setPatientId(admission.getPatientId());
        bed.setPatientName(admission.getPatientName());
        bedRepository.save(bed);

        admission.setBedNo(bedNo);
        admission.setRoomNo(bed.getRoomNo());
        log.info("床位分配成功，床位号：{}", bedNo);
    }

    /**
     * 转换为VO
     */
    private AdmissionRegisterVO convertToVO(InpatientAdmission admission) {
        AdmissionRegisterVO vo = new AdmissionRegisterVO();
        vo.setAdmissionId(admission.getId());
        vo.setAdmissionNo(admission.getAdmissionNo());
        vo.setPatientId(admission.getPatientId());
        vo.setPatientName(admission.getPatientName());
        vo.setBedNo(admission.getBedNo());
        vo.setRoomNo(admission.getRoomNo());
        vo.setWardName(admission.getWardName());
        vo.setDeptName(admission.getDeptName());
        vo.setAdmissionTime(admission.getAdmissionTime());
        vo.setAdmissionType(admission.getAdmissionType());
        vo.setDoctorName(admission.getDoctorName());
        vo.setNurseName(admission.getNurseName());
        vo.setAdmissionDiagnosis(admission.getAdmissionDiagnosis());
        vo.setNursingLevel(admission.getNursingLevel());
        vo.setDietType(admission.getDietType());
        vo.setDeposit(admission.getDeposit());
        vo.setStatus(admission.getStatus());
        return vo;
    }
}
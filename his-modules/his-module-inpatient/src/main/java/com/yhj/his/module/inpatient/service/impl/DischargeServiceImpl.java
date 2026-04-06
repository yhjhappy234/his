package com.yhj.his.module.inpatient.service.impl;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.entity.InpatientAdmission;
import com.yhj.his.module.inpatient.enums.AdmissionStatus;
import com.yhj.his.module.inpatient.repository.InpatientAdmissionRepository;
import com.yhj.his.module.inpatient.repository.InpatientFeeRepository;
import com.yhj.his.module.inpatient.service.BedService;
import com.yhj.his.module.inpatient.service.DischargeService;
import com.yhj.his.module.inpatient.service.InpatientFeeService;
import com.yhj.his.module.inpatient.vo.DischargeSummaryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 出院管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DischargeServiceImpl implements DischargeService {

    private final InpatientAdmissionRepository admissionRepository;
    private final InpatientFeeRepository feeRepository;
    private final InpatientFeeService feeService;
    private final BedService bedService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean apply(DischargeApplyDTO dto) {
        InpatientAdmission admission = admissionRepository.findById(dto.getAdmissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        if (admission.getStatus() != AdmissionStatus.IN_HOSPITAL) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "患者状态不允许出院申请");
        }

        // 检查是否有未结算的费用
        BigDecimal unsettled = feeRepository.sumUnsettledFeeByAdmissionId(dto.getAdmissionId());
        if (unsettled != null && unsettled.compareTo(BigDecimal.ZERO) > 0) {
            log.warn("患者有未结算费用：{}", unsettled);
        }

        // 更新出院信息
        admission.setDischargeDiagnosis(dto.getDischargeDiagnosis());
        admission.setDischargeDiagnosisCode(dto.getDischargeDiagnosisCode());
        admission.setDischargeType(dto.getDischargeType());

        admissionRepository.save(admission);
        log.info("出院申请成功，住院ID：{}", dto.getAdmissionId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DischargeSettleResponseDTO settle(DischargeSettleDTO dto) {
        InpatientAdmission admission = admissionRepository.findById(dto.getAdmissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        if (admission.getStatus() != AdmissionStatus.IN_HOSPITAL) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "患者状态不允许结算");
        }

        // 计算费用
        BigDecimal totalCost = feeRepository.sumFeeAmountByAdmissionId(dto.getAdmissionId());
        if (totalCost == null) {
            totalCost = BigDecimal.ZERO;
        }

        BigDecimal deposit = admission.getDeposit() != null ? admission.getDeposit() : BigDecimal.ZERO;
        BigDecimal insurancePayment = BigDecimal.ZERO;
        BigDecimal selfPayment = BigDecimal.ZERO;

        // 计算支付金额
        if (dto.getPayments() != null) {
            for (DischargeSettleDTO.PaymentDetail payment : dto.getPayments()) {
                if ("MEDICAL_INSURANCE".equals(payment.getPayMethod())) {
                    insurancePayment = insurancePayment.add(payment.getAmount());
                } else {
                    selfPayment = selfPayment.add(payment.getAmount());
                }
            }
        }

        // 计算退费金额
        BigDecimal totalPaid = deposit.add(insurancePayment).add(selfPayment);
        BigDecimal refund = totalPaid.subtract(totalCost).max(BigDecimal.ZERO);

        // 更新住院记录
        admission.setTotalCost(totalCost);
        admission.setSettledCost(totalCost);
        admission.setDischargeTime(LocalDateTime.now());
        admission.setStatus(AdmissionStatus.DISCHARGED);
        admissionRepository.save(admission);

        // 释放床位
        bedService.release(dto.getAdmissionId());

        // 构建响应
        DischargeSettleResponseDTO response = new DischargeSettleResponseDTO();
        response.setSettleId(SequenceGenerator.generate("SET"));
        response.setInvoiceNo(SequenceGenerator.generate("INV"));

        // 计算住院天数
        if (admission.getAdmissionTime() != null && admission.getDischargeTime() != null) {
            long days = ChronoUnit.DAYS.between(admission.getAdmissionTime(), admission.getDischargeTime()) + 1;
            response.setTotalDays((int) days);
        }

        response.setTotalCost(totalCost.setScale(2, RoundingMode.HALF_UP));
        response.setDepositUsed(deposit.min(totalCost).setScale(2, RoundingMode.HALF_UP));
        response.setInsurancePayment(insurancePayment.setScale(2, RoundingMode.HALF_UP));
        response.setSelfPayment(selfPayment.setScale(2, RoundingMode.HALF_UP));
        response.setRefund(refund.setScale(2, RoundingMode.HALF_UP));

        log.info("出院结算成功，住院ID：{}，总费用：{}", dto.getAdmissionId(), totalCost);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String summary(DischargeSummaryDTO dto) {
        InpatientAdmission admission = admissionRepository.findById(dto.getAdmissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        admission.setAdmissionDiagnosis(dto.getAdmissionDiagnosis());
        admission.setDischargeDiagnosis(dto.getDischargeDiagnosis());

        admissionRepository.save(admission);
        log.info("出院小结完成，住院ID：{}", dto.getAdmissionId());
        return admission.getId();
    }

    @Override
    public DischargeSummaryVO getSummary(String admissionId) {
        InpatientAdmission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        DischargeSummaryVO vo = new DischargeSummaryVO();
        vo.setAdmissionId(admission.getId());
        vo.setAdmissionNo(admission.getAdmissionNo());
        vo.setPatientName(admission.getPatientName());
        vo.setAdmissionTime(admission.getAdmissionTime());
        vo.setDischargeTime(admission.getDischargeTime());

        if (admission.getAdmissionTime() != null && admission.getDischargeTime() != null) {
            long days = ChronoUnit.DAYS.between(admission.getAdmissionTime(), admission.getDischargeTime()) + 1;
            vo.setTotalDays((int) days);
        }

        vo.setDischargeType(admission.getDischargeType());
        vo.setAdmissionDiagnosis(admission.getAdmissionDiagnosis());
        vo.setDischargeDiagnosis(admission.getDischargeDiagnosis());
        vo.setDoctorName(admission.getDoctorName());
        vo.setTotalCost(admission.getTotalCost());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelApply(String admissionId) {
        InpatientAdmission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        if (admission.getStatus() != AdmissionStatus.IN_HOSPITAL) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "患者状态不允许取消出院申请");
        }

        admission.setDischargeDiagnosis(null);
        admission.setDischargeDiagnosisCode(null);
        admission.setDischargeType(null);
        admissionRepository.save(admission);

        log.info("取消出院申请成功，住院ID：{}", admissionId);
        return true;
    }
}
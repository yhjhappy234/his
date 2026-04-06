package com.yhj.his.module.inpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.inpatient.entity.InpatientAdmission;
import com.yhj.his.module.inpatient.entity.InpatientFee;
import com.yhj.his.module.inpatient.enums.FeeCategory;
import com.yhj.his.module.inpatient.repository.InpatientAdmissionRepository;
import com.yhj.his.module.inpatient.repository.InpatientFeeRepository;
import com.yhj.his.module.inpatient.service.InpatientFeeService;
import com.yhj.his.module.inpatient.vo.InpatientFeeSummaryVO;
import com.yhj.his.module.inpatient.vo.InpatientFeeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 住院费用服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InpatientFeeServiceImpl implements InpatientFeeService {

    private final InpatientFeeRepository feeRepository;
    private final InpatientAdmissionRepository admissionRepository;

    @Override
    public List<InpatientFeeVO> listByAdmission(String admissionId) {
        List<InpatientFee> fees = feeRepository.findByAdmissionIdOrderByDate(admissionId);
        return fees.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public InpatientFeeSummaryVO getSummary(String admissionId) {
        InpatientAdmission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        InpatientFeeSummaryVO vo = new InpatientFeeSummaryVO();
        vo.setAdmissionId(admissionId);
        vo.setAdmissionNo(admission.getAdmissionNo());
        vo.setPatientName(admission.getPatientName());
        vo.setDeposit(admission.getDeposit());

        // 计算总费用
        BigDecimal totalCost = feeRepository.sumFeeAmountByAdmissionId(admissionId);
        vo.setTotalCost(totalCost != null ? totalCost : BigDecimal.ZERO);

        // 计算各分类费用
        vo.setBedFee(getCategoryFee(admissionId, FeeCategory.BED));
        vo.setDrugFee(getCategoryFee(admissionId, FeeCategory.DRUG));
        vo.setExaminationFee(getCategoryFee(admissionId, FeeCategory.EXAMINATION));
        vo.setLabTestFee(getCategoryFee(admissionId, FeeCategory.LAB_TEST));
        vo.setTreatmentFee(getCategoryFee(admissionId, FeeCategory.TREATMENT));
        vo.setNursingFee(getCategoryFee(admissionId, FeeCategory.NURSING));
        vo.setMaterialFee(getCategoryFee(admissionId, FeeCategory.MATERIAL));

        // 计算未结算金额
        BigDecimal unsettled = feeRepository.sumUnsettledFeeByAdmissionId(admissionId);
        vo.setUnsettledAmount(unsettled != null ? unsettled : BigDecimal.ZERO);
        vo.setSettledAmount(admission.getSettledCost() != null ? admission.getSettledCost() : BigDecimal.ZERO);

        return vo;
    }

    @Override
    public PageResult<InpatientFeeVO> page(Integer pageNum, Integer pageSize, String admissionId) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "feeDate", "createTime"));

        Page<InpatientFee> page = feeRepository.findByAdmissionId(admissionId, pageRequest);

        List<InpatientFeeVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public BigDecimal getUnsettledAmount(String admissionId) {
        BigDecimal unsettled = feeRepository.sumUnsettledFeeByAdmissionId(admissionId);
        return unsettled != null ? unsettled : BigDecimal.ZERO;
    }

    @Override
    public List<InpatientFeeVO> listByDate(String admissionId, String feeDate) {
        LocalDate date = LocalDate.parse(feeDate);
        List<InpatientFee> fees = feeRepository.findByAdmissionIdAndFeeDate(admissionId, date);
        return fees.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 获取分类费用
     */
    private BigDecimal getCategoryFee(String admissionId, FeeCategory category) {
        BigDecimal fee = feeRepository.sumFeeAmountByAdmissionIdAndCategory(admissionId, category);
        return fee != null ? fee : BigDecimal.ZERO;
    }

    /**
     * 转换为VO
     */
    private InpatientFeeVO convertToVO(InpatientFee fee) {
        InpatientFeeVO vo = new InpatientFeeVO();
        vo.setFeeId(fee.getId());
        vo.setAdmissionId(fee.getAdmissionId());
        vo.setPatientId(fee.getPatientId());
        vo.setFeeDate(fee.getFeeDate() != null ? fee.getFeeDate().toString() : null);
        vo.setFeeCategory(fee.getFeeCategory() != null ? fee.getFeeCategory().getCode() : null);
        vo.setFeeItemCode(fee.getFeeItemCode());
        vo.setFeeItemName(fee.getFeeItemName());
        vo.setFeeSpec(fee.getFeeSpec());
        vo.setFeeUnit(fee.getFeeUnit());
        vo.setFeePrice(fee.getFeePrice());
        vo.setFeeQuantity(fee.getFeeQuantity());
        vo.setFeeAmount(fee.getFeeAmount());
        vo.setOrderNo(fee.getOrderNo());
        vo.setDeptName(fee.getDeptName());
        vo.setIsInsurance(fee.getIsInsurance());
        vo.setPayStatus(fee.getPayStatus() != null ? fee.getPayStatus().getCode() : null);
        return vo;
    }
}
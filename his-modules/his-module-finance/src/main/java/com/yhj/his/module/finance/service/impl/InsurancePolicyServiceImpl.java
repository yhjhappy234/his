package com.yhj.his.module.finance.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.dto.InsurancePolicyCreateDTO;
import com.yhj.his.module.finance.dto.InsurancePolicyUpdateDTO;
import com.yhj.his.module.finance.entity.InsurancePolicy;
import com.yhj.his.module.finance.repository.InsurancePolicyRepository;
import com.yhj.his.module.finance.service.InsurancePolicyService;
import com.yhj.his.module.finance.vo.InsurancePolicyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 医保政策服务实现
 */
@Service
@RequiredArgsConstructor
public class InsurancePolicyServiceImpl implements InsurancePolicyService {

    private final InsurancePolicyRepository insurancePolicyRepository;

    @Override
    @Transactional
    public InsurancePolicyVO create(InsurancePolicyCreateDTO dto) {
        // 检查医保类型是否已配置
        if (insurancePolicyRepository.findByInsuranceType(InsurancePolicy.InsuranceTypeEnum.valueOf(dto.getInsuranceType())).isPresent()) {
            throw new BusinessException("该医保类型已配置政策: " + dto.getInsuranceType());
        }

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyName(dto.getPolicyName());
        policy.setInsuranceType(InsurancePolicy.InsuranceTypeEnum.valueOf(dto.getInsuranceType()));
        policy.setDeductibleLine(dto.getDeductibleLine());
        policy.setCapLine(dto.getCapLine());
        policy.setClassARatio(dto.getClassARatio());
        policy.setClassBRatio(dto.getClassBRatio());
        policy.setClassCRatio(dto.getClassCRatio());
        policy.setOutpatientRatio(dto.getOutpatientRatio());
        policy.setInpatientRatio(dto.getInpatientRatio());
        policy.setRemark(dto.getRemark());
        policy.setStatus(InsurancePolicy.InsurancePolicyStatus.ACTIVE);

        insurancePolicyRepository.save(policy);
        return toVO(policy);
    }

    @Override
    @Transactional
    public InsurancePolicyVO update(InsurancePolicyUpdateDTO dto) {
        InsurancePolicy policy = insurancePolicyRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("医保政策不存在: " + dto.getId()));

        if (dto.getPolicyName() != null) {
            policy.setPolicyName(dto.getPolicyName());
        }
        if (dto.getDeductibleLine() != null) {
            policy.setDeductibleLine(dto.getDeductibleLine());
        }
        if (dto.getCapLine() != null) {
            policy.setCapLine(dto.getCapLine());
        }
        if (dto.getClassARatio() != null) {
            policy.setClassARatio(dto.getClassARatio());
        }
        if (dto.getClassBRatio() != null) {
            policy.setClassBRatio(dto.getClassBRatio());
        }
        if (dto.getClassCRatio() != null) {
            policy.setClassCRatio(dto.getClassCRatio());
        }
        if (dto.getOutpatientRatio() != null) {
            policy.setOutpatientRatio(dto.getOutpatientRatio());
        }
        if (dto.getInpatientRatio() != null) {
            policy.setInpatientRatio(dto.getInpatientRatio());
        }
        if (dto.getRemark() != null) {
            policy.setRemark(dto.getRemark());
        }
        if (dto.getStatus() != null) {
            policy.setStatus(InsurancePolicy.InsurancePolicyStatus.valueOf(dto.getStatus()));
        }

        insurancePolicyRepository.save(policy);
        return toVO(policy);
    }

    @Override
    @Transactional
    public void delete(String id) {
        InsurancePolicy policy = insurancePolicyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("医保政策不存在: " + id));
        policy.setDeleted(true);
        insurancePolicyRepository.save(policy);
    }

    @Override
    public InsurancePolicyVO getById(String id) {
        InsurancePolicy policy = insurancePolicyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("医保政策不存在: " + id));
        return toVO(policy);
    }

    @Override
    public InsurancePolicyVO getByInsuranceType(String insuranceType) {
        InsurancePolicy policy = insurancePolicyRepository
                .findByInsuranceTypeAndStatus(InsurancePolicy.InsuranceTypeEnum.valueOf(insuranceType), InsurancePolicy.InsurancePolicyStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException("未找到启用的医保政策: " + insuranceType));
        return toVO(policy);
    }

    @Override
    public PageResult<InsurancePolicyVO> pageList(String policyName, String status, int pageNum, int pageSize) {
        Specification<InsurancePolicy> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (policyName != null && !policyName.isEmpty()) {
                predicates = cb.and(predicates, cb.like(root.get("policyName"), "%" + policyName + "%"));
            }
            if (status != null && !status.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), InsurancePolicy.InsurancePolicyStatus.valueOf(status)));
            }
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            return predicates;
        };

        Page<InsurancePolicy> page = insurancePolicyRepository.findAll(spec, PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending()));
        List<InsurancePolicyVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public List<InsurancePolicyVO> listAllActive() {
        List<InsurancePolicy> policies = insurancePolicyRepository.findAllActive();
        return policies.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InsurancePolicyVO updateStatus(String id, String status) {
        InsurancePolicy policy = insurancePolicyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("医保政策不存在: " + id));
        policy.setStatus(InsurancePolicy.InsurancePolicyStatus.valueOf(status));
        insurancePolicyRepository.save(policy);
        return toVO(policy);
    }

    @Override
    public BigDecimal calculateInsuranceAmount(String insuranceType, String itemInsuranceType, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 自费项目不报销
        if ("SELF".equals(itemInsuranceType) || "C".equals(itemInsuranceType)) {
            return BigDecimal.ZERO;
        }

        InsurancePolicy policy = insurancePolicyRepository
                .findByInsuranceTypeAndStatus(InsurancePolicy.InsuranceTypeEnum.valueOf(insuranceType), InsurancePolicy.InsurancePolicyStatus.ACTIVE)
                .orElse(null);

        if (policy == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal ratio;
        if ("A".equals(itemInsuranceType)) {
            ratio = policy.getClassARatio();
        } else if ("B".equals(itemInsuranceType)) {
            ratio = policy.getClassBRatio();
        } else {
            return BigDecimal.ZERO;
        }

        if (ratio == null || ratio.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return amount.multiply(ratio).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public InsuranceSettlementResult calculateSettlement(String insuranceType, BigDecimal totalAmount,
                                                          BigDecimal classAAmount, BigDecimal classBAmount, BigDecimal classCAmount) {
        InsurancePolicy policy = insurancePolicyRepository
                .findByInsuranceTypeAndStatus(InsurancePolicy.InsuranceTypeEnum.valueOf(insuranceType), InsurancePolicy.InsurancePolicyStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException("未找到启用的医保政策"));

        // 计算各类报销金额
        BigDecimal reimbursedClassA = classAAmount.multiply(policy.getClassARatio() != null ? policy.getClassARatio() : BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal reimbursedClassB = classBAmount.multiply(policy.getClassBRatio() != null ? policy.getClassBRatio() : BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal reimbursedClassC = BigDecimal.ZERO; // 丙类不报销

        // 计算总报销金额
        BigDecimal totalBeforeDeductible = reimbursedClassA.add(reimbursedClassB).add(reimbursedClassC);

        // 扣除起付线
        BigDecimal deductible = policy.getDeductibleLine() != null ? policy.getDeductibleLine() : BigDecimal.ZERO;
        BigDecimal amountAboveDeductible = totalBeforeDeductible.subtract(deductible);
        if (amountAboveDeductible.compareTo(BigDecimal.ZERO) < 0) {
            amountAboveDeductible = BigDecimal.ZERO;
        }

        // 计算个人自付
        BigDecimal selfPay = totalAmount.subtract(amountAboveDeductible);

        return new InsuranceSettlementResult(
                deductible,
                amountAboveDeductible,
                reimbursedClassA,
                reimbursedClassB,
                reimbursedClassC,
                amountAboveDeductible,
                selfPay
        );
    }

    /**
     * 实体转VO
     */
    private InsurancePolicyVO toVO(InsurancePolicy policy) {
        InsurancePolicyVO vo = new InsurancePolicyVO();
        vo.setId(policy.getId());
        vo.setPolicyName(policy.getPolicyName());
        vo.setInsuranceType(policy.getInsuranceType() != null ? policy.getInsuranceType().name() : null);
        vo.setInsuranceTypeDesc(policy.getInsuranceType() != null ? policy.getInsuranceType().getDescription() : null);
        vo.setDeductibleLine(policy.getDeductibleLine());
        vo.setCapLine(policy.getCapLine());
        vo.setClassARatio(policy.getClassARatio());
        vo.setClassBRatio(policy.getClassBRatio());
        vo.setClassCRatio(policy.getClassCRatio());
        vo.setOutpatientRatio(policy.getOutpatientRatio());
        vo.setInpatientRatio(policy.getInpatientRatio());
        vo.setRemark(policy.getRemark());
        vo.setStatus(policy.getStatus() != null ? policy.getStatus().name() : null);
        vo.setStatusDesc(policy.getStatus() != null ? policy.getStatus().getDescription() : null);
        return vo;
    }
}
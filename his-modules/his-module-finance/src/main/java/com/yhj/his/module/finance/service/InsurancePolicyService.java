package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.finance.dto.InsurancePolicyCreateDTO;
import com.yhj.his.module.finance.dto.InsurancePolicyUpdateDTO;
import com.yhj.his.module.finance.entity.InsurancePolicy;
import com.yhj.his.module.finance.vo.InsurancePolicyVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 医保政策服务接口
 */
public interface InsurancePolicyService {

    /**
     * 创建医保政策
     */
    InsurancePolicyVO create(InsurancePolicyCreateDTO dto);

    /**
     * 更新医保政策
     */
    InsurancePolicyVO update(InsurancePolicyUpdateDTO dto);

    /**
     * 删除医保政策
     */
    void delete(String id);

    /**
     * 根据ID查询
     */
    InsurancePolicyVO getById(String id);

    /**
     * 根据医保类型查询
     */
    InsurancePolicyVO getByInsuranceType(String insuranceType);

    /**
     * 分页查询
     */
    PageResult<InsurancePolicyVO> pageList(String policyName, String status, int pageNum, int pageSize);

    /**
     * 查询所有启用的医保政策
     */
    List<InsurancePolicyVO> listAllActive();

    /**
     * 启用/停用医保政策
     */
    InsurancePolicyVO updateStatus(String id, String status);

    /**
     * 计算医保报销金额
     *
     * @param insuranceType  医保类型
     * @param itemInsuranceType 项目医保类型(A/B/C/SELF)
     * @param amount        项目金额
     * @return  可报销金额
     */
    BigDecimal calculateInsuranceAmount(String insuranceType, String itemInsuranceType, BigDecimal amount);

    /**
     * 计算医保结算
     *
     * @param insuranceType  医保类型
     * @param totalAmount    总金额
     * @param classAAmount   甲类金额
     * @param classBAmount   乙类金额
     * @param classCAmount   丙类金额
     * @return  医保结算结果
     */
    InsuranceSettlementResult calculateSettlement(String insuranceType, BigDecimal totalAmount,
                                                    BigDecimal classAAmount, BigDecimal classBAmount, BigDecimal classCAmount);

    /**
     * 医保结算结果
     */
    record InsuranceSettlementResult(
            BigDecimal deductibleLine,       // 起付线
            BigDecimal amountAboveDeductible, // 超过起付线的金额
            BigDecimal classAAmount,         // 甲类报销金额
            BigDecimal classBAmount,         // 乙类报销金额
            BigDecimal classCAmount,         // 丙类报销金额(一般为0)
            BigDecimal totalInsuranceAmount, // 总医保报销金额
            BigDecimal selfPayAmount         // 个人自付金额
    ) {}
}
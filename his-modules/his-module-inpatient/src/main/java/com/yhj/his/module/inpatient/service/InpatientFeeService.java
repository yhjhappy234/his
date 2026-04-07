package com.yhj.his.module.inpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inpatient.vo.InpatientFeeSummaryVO;
import com.yhj.his.module.inpatient.vo.InpatientFeeVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 住院费用服务接口
 */
public interface InpatientFeeService {

    /**
     * 查询费用明细
     *
     * @param admissionId 住院ID
     * @return 费用明细列表
     */
    List<InpatientFeeVO> listByAdmission(String admissionId);

    /**
     * 查询费用汇总
     *
     * @param admissionId 住院ID
     * @return 费用汇总
     */
    InpatientFeeSummaryVO getSummary(String admissionId);

    /**
     * 分页查询费用
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param admissionId 住院ID
     * @return 分页结果
     */
    PageResult<InpatientFeeVO> page(Integer pageNum, Integer pageSize, String admissionId);

    /**
     * 查询未结算费用总额
     *
     * @param admissionId 住院ID
     * @return 未结算费用
     */
    BigDecimal getUnsettledAmount(String admissionId);

    /**
     * 查询每日费用
     *
     * @param admissionId 住院ID
     * @param feeDate 费用日期
     * @return 每日费用列表
     */
    List<InpatientFeeVO> listByDate(String admissionId, String feeDate);
}
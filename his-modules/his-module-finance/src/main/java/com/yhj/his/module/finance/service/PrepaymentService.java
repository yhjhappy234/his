package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.finance.dto.PrepaymentDTO;
import com.yhj.his.module.finance.vo.PrepaymentBalanceVO;
import com.yhj.his.module.finance.vo.PrepaymentVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 预交金服务接口
 */
public interface PrepaymentService {

    /**
     * 缴纳预交金
     *
     * @param dto 预交金缴纳请求
     * @param operatorId 操作员ID
     * @param operatorName 操作员姓名
     * @return 预交金记录
     */
    PrepaymentVO deposit(PrepaymentDTO dto, String operatorId, String operatorName);

    /**
     * 退还预交金
     *
     * @param admissionId 住院ID
     * @param refundAmount 退还金额
     * @param refundMethod 退还方式
     * @param reason 退还原因
     * @param operatorId 操作员ID
     * @param operatorName 操作员姓名
     * @return 预交金记录
     */
    PrepaymentVO refund(String admissionId, BigDecimal refundAmount, String refundMethod, String reason,
                         String operatorId, String operatorName);

    /**
     * 查询预交金余额
     *
     * @param admissionId 住院ID
     * @return 预交金余额
     */
    PrepaymentBalanceVO getBalance(String admissionId);

    /**
     * 根据住院ID查询预交金记录列表
     */
    List<PrepaymentVO> listByAdmissionId(String admissionId);

    /**
     * 根据患者ID查询预交金记录列表
     */
    List<PrepaymentVO> listByPatientId(String patientId);

    /**
     * 根据预交金单号查询
     */
    PrepaymentVO getByPrepaymentNo(String prepaymentNo);

    /**
     * 分页查询预交金记录
     */
    PageResult<PrepaymentVO> pageList(String patientId, String admissionId, String depositType, int pageNum, int pageSize);

    /**
     * 计算住院预交金总额
     */
    BigDecimal calculateTotalDeposit(String admissionId);

    /**
     * 预交金不足提醒
     *
     * @param admissionId 住院ID
     * @param estimatedCost 预计费用
     * @return 是否需要提醒
     */
    boolean checkDepositWarning(String admissionId, BigDecimal estimatedCost);
}
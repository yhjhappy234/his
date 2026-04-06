package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.finance.dto.InpatientSettleDTO;
import com.yhj.his.module.finance.vo.InpatientFeeSummaryVO;
import com.yhj.his.module.finance.vo.InpatientSettlementVO;
import com.yhj.his.module.finance.vo.SettlementResultVO;

import java.util.List;

/**
 * 住院结算服务接口
 */
public interface InpatientSettlementService {

    /**
     * 查询住院费用汇总
     *
     * @param admissionId 住院ID
     * @return 费用汇总
     */
    InpatientFeeSummaryVO getFeeSummary(String admissionId);

    /**
     * 出院结算
     *
     * @param dto 结算请求
     * @param operatorId 操作员ID
     * @param operatorName 操作员姓名
     * @return 结算结果
     */
    SettlementResultVO settle(InpatientSettleDTO dto, String operatorId, String operatorName);

    /**
     * 根据结算单号查询
     */
    InpatientSettlementVO getBySettlementNo(String settlementNo);

    /**
     * 根据发票号查询
     */
    InpatientSettlementVO getByInvoiceNo(String invoiceNo);

    /**
     * 根据住院ID查询结算记录
     */
    InpatientSettlementVO getByAdmissionId(String admissionId);

    /**
     * 根据患者ID查询结算记录列表
     */
    List<InpatientSettlementVO> listByPatientId(String patientId);

    /**
     * 分页查询结算记录
     */
    PageResult<InpatientSettlementVO> pageList(String patientId, String settlementDate, String status, int pageNum, int pageSize);

    /**
     * 计算结算金额
     *
     * @param admissionId 住院ID
     * @param insuranceType 医保类型
     * @return 费用计算结果
     */
    InpatientFeeSummaryVO calculateSettlement(String admissionId, String insuranceType);

    /**
     * 检查住院是否已结算
     */
    boolean isSettled(String admissionId);

    /**
     * 作废结算(需审批)
     */
    void voidSettlement(String settlementNo, String reason, String operatorId);
}
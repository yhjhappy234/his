package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.finance.dto.OutpatientRefundDTO;
import com.yhj.his.module.finance.dto.OutpatientSettleDTO;
import com.yhj.his.module.finance.vo.OutpatientBillingVO;
import com.yhj.his.module.finance.vo.PendingBillingVO;
import com.yhj.his.module.finance.vo.SettlementResultVO;

import java.util.List;

/**
 * 门诊收费服务接口
 */
public interface OutpatientBillingService {

    /**
     * 获取待收费项目
     *
     * @param visitId 就诊ID
     * @return 待收费项目汇总
     */
    PendingBillingVO getPendingItems(String visitId);

    /**
     * 收费结算
     *
     * @param dto 结算请求
     * @param operatorId 操作员ID
     * @param operatorName 操作员姓名
     * @return 结算结果
     */
    SettlementResultVO settle(OutpatientSettleDTO dto, String operatorId, String operatorName);

    /**
     * 退费处理
     *
     * @param dto 退费请求
     * @param operatorId 操作员ID
     * @param operatorName 操作员姓名
     * @return 退费结果
     */
    SettlementResultVO refund(OutpatientRefundDTO dto, String operatorId, String operatorName);

    /**
     * 根据收费单号查询
     */
    OutpatientBillingVO getByBillingNo(String billingNo);

    /**
     * 根据发票号查询
     */
    OutpatientBillingVO getByInvoiceNo(String invoiceNo);

    /**
     * 根据就诊ID查询收费记录
     */
    List<OutpatientBillingVO> listByVisitId(String visitId);

    /**
     * 根据患者ID查询收费记录
     */
    List<OutpatientBillingVO> listByPatientId(String patientId);

    /**
     * 分页查询收费记录
     */
    PageResult<OutpatientBillingVO> pageList(String patientId, String billingDate, String status, int pageNum, int pageSize);

    /**
     * 计算费用
     *
     * @param visitId 就诊ID
     * @param insuranceType 医保类型
     * @return 费用计算结果
     */
    PendingBillingVO calculateFee(String visitId, String insuranceType);

    /**
     * 打印发票
     *
     * @param billingNo 收费单号
     * @return 发票信息
     */
    OutpatientBillingVO printInvoice(String billingNo);
}
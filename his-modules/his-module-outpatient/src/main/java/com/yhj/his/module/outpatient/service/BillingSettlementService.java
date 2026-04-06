package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.dto.BillingSettleRequest;
import com.yhj.his.module.outpatient.entity.BillingSettlement;
import com.yhj.his.module.outpatient.vo.BillingResultVO;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 收费结算服务接口
 */
public interface BillingSettlementService {

    /**
     * 收费结算
     */
    BillingResultVO settle(BillingSettleRequest request);

    /**
     * 根据ID查询结算记录
     */
    Optional<BillingSettlement> findById(String id);

    /**
     * 根据结算单号查询
     */
    Optional<BillingSettlement> findBySettlementNo(String settlementNo);

    /**
     * 根据发票号查询
     */
    Optional<BillingSettlement> findByInvoiceNo(String invoiceNo);

    /**
     * 获取结算详情
     */
    BillingSettlement getSettlementDetail(String id);

    /**
     * 分页查询结算记录列表
     */
    PageResult<BillingSettlement> listSettlements(String patientId, String registrationId, String status, Pageable pageable);

    /**
     * 查询患者结算记录
     */
    List<BillingSettlement> listPatientSettlements(String patientId);

    /**
     * 退费
     */
    BillingResultVO refund(String settlementId, String reason);

    /**
     * 生成发票号
     */
    String generateInvoiceNo();

    /**
     * 生成结算单号
     */
    String generateSettlementNo();
}
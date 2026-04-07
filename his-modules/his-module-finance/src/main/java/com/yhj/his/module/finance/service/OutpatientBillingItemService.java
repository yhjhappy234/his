package com.yhj.his.module.finance.service;

import com.yhj.his.module.finance.entity.OutpatientBillingItem;
import com.yhj.his.module.finance.entity.OutpatientBillingItem.BillingItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 门诊收费明细服务接口
 */
public interface OutpatientBillingItemService {

    /**
     * 保存收费明细
     */
    OutpatientBillingItem save(OutpatientBillingItem outpatientBillingItem);

    /**
     * 根据ID查询
     */
    Optional<OutpatientBillingItem> findById(String id);

    /**
     * 根据收费记录ID查询明细
     */
    List<OutpatientBillingItem> findByBillingId(String billingId);

    /**
     * 根据收费记录ID和状态查询明细
     */
    List<OutpatientBillingItem> findByBillingIdAndStatus(String billingId, BillingItemStatus status);

    /**
     * 根据收费项目ID查询
     */
    List<OutpatientBillingItem> findByItemId(String itemId);

    /**
     * 根据处方ID查询
     */
    List<OutpatientBillingItem> findByPrescriptionId(String prescriptionId);

    /**
     * 根据申请ID查询
     */
    List<OutpatientBillingItem> findByRequestId(String requestId);

    /**
     * 统计收费记录的明细数量
     */
    Long countByBillingId(String billingId);

    /**
     * 统计收费记录的正常金额
     */
    BigDecimal sumAmountByBillingId(String billingId);

    /**
     * 统计收费记录的退费金额
     */
    BigDecimal sumRefundAmountByBillingId(String billingId);

    /**
     * 查询所有
     */
    List<OutpatientBillingItem> findAll();

    /**
     * 分页查询
     */
    Page<OutpatientBillingItem> findAll(Pageable pageable);

    /**
     * 删除收费明细(逻辑删除)
     */
    void deleteById(String id);

    /**
     * 删除收费记录的所有明细(逻辑删除)
     */
    void deleteByBillingId(String billingId);

    /**
     * 批量保存收费明细
     */
    List<OutpatientBillingItem> saveAll(List<OutpatientBillingItem> items);

    /**
     * 退费明细
     */
    OutpatientBillingItem refundItem(String id, BigDecimal refundAmount);
}
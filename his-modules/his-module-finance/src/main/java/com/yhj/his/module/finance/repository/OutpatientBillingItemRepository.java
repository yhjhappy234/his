package com.yhj.his.module.finance.repository;

import com.yhj.his.module.finance.entity.OutpatientBillingItem;
import com.yhj.his.module.finance.entity.OutpatientBillingItem.BillingItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 门诊收费明细Repository
 */
@Repository
public interface OutpatientBillingItemRepository extends JpaRepository<OutpatientBillingItem, String>, JpaSpecificationExecutor<OutpatientBillingItem> {

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
    @Query("SELECT COUNT(i) FROM OutpatientBillingItem i WHERE i.billingId = :billingId AND i.deleted = false")
    Long countByBillingId(@Param("billingId") String billingId);

    /**
     * 统计收费记录的正常金额
     */
    @Query("SELECT SUM(i.amount) FROM OutpatientBillingItem i WHERE i.billingId = :billingId AND i.status = 'NORMAL' AND i.deleted = false")
    java.math.BigDecimal sumAmountByBillingId(@Param("billingId") String billingId);

    /**
     * 统计收费记录的退费金额
     */
    @Query("SELECT SUM(i.refundAmount) FROM OutpatientBillingItem i WHERE i.billingId = :billingId AND i.status = 'REFUNDED' AND i.deleted = false")
    java.math.BigDecimal sumRefundAmountByBillingId(@Param("billingId") String billingId);

    /**
     * 删除收费记录的所有明细(逻辑删除)
     */
    @Query("UPDATE OutpatientBillingItem i SET i.deleted = true WHERE i.billingId = :billingId")
    void deleteByBillingId(@Param("billingId") String billingId);
}
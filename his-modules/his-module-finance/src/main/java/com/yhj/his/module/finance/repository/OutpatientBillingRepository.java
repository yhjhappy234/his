package com.yhj.his.module.finance.repository;

import com.yhj.his.module.finance.entity.OutpatientBilling;
import com.yhj.his.module.finance.entity.OutpatientBilling.BillingStatus;
import com.yhj.his.module.finance.entity.OutpatientBilling.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 门诊收费记录Repository
 */
@Repository
public interface OutpatientBillingRepository extends JpaRepository<OutpatientBilling, String>, JpaSpecificationExecutor<OutpatientBilling> {

    /**
     * 根据收费单号查询
     */
    Optional<OutpatientBilling> findByBillingNo(String billingNo);

    /**
     * 根据发票号查询
     */
    Optional<OutpatientBilling> findByInvoiceNo(String invoiceNo);

    /**
     * 根据患者ID查询
     */
    List<OutpatientBilling> findByPatientId(String patientId);

    /**
     * 根据就诊ID查询
     */
    List<OutpatientBilling> findByVisitId(String visitId);

    /**
     * 根据收费日期查询
     */
    List<OutpatientBilling> findByBillingDate(LocalDate billingDate);

    /**
     * 根据收费日期和操作员查询
     */
    List<OutpatientBilling> findByBillingDateAndOperatorId(LocalDate billingDate, String operatorId);

    /**
     * 根据状态查询
     */
    List<OutpatientBilling> findByStatus(BillingStatus status);

    /**
     * 查询指定日期范围内正常状态的收费记录
     */
    @Query("SELECT b FROM OutpatientBilling b WHERE b.billingDate BETWEEN :startDate AND :endDate AND b.status = :status AND b.deleted = false")
    List<OutpatientBilling> findByDateRangeAndStatus(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") BillingStatus status);

    /**
     * 查询指定操作员指定日期的收费记录
     */
    @Query("SELECT b FROM OutpatientBilling b WHERE b.operatorId = :operatorId AND b.billingDate = :date AND b.status != 'REFUNDED' AND b.deleted = false")
    List<OutpatientBilling> findByOperatorAndDate(@Param("operatorId") String operatorId, @Param("date") LocalDate date);

    /**
     * 统计指定操作员指定日期的收费金额
     */
    @Query("SELECT SUM(b.totalAmount) FROM OutpatientBilling b WHERE b.operatorId = :operatorId AND b.billingDate = :date AND b.status != 'REFUNDED' AND b.deleted = false")
    java.math.BigDecimal sumAmountByOperatorAndDate(@Param("operatorId") String operatorId, @Param("date") LocalDate date);

    /**
     * 统计指定操作员指定日期的退费金额
     */
    @Query("SELECT SUM(b.refundAmount) FROM OutpatientBilling b WHERE b.operatorId = :operatorId AND b.billingDate = :date AND b.refundStatus = 'COMPLETED' AND b.deleted = false")
    java.math.BigDecimal sumRefundByOperatorAndDate(@Param("operatorId") String operatorId, @Param("date") LocalDate date);

    /**
     * 统计指定操作员指定日期的收费笔数
     */
    @Query("SELECT COUNT(b) FROM OutpatientBilling b WHERE b.operatorId = :operatorId AND b.billingDate = :date AND b.status != 'REFUNDED' AND b.deleted = false")
    Long countBillingByOperatorAndDate(@Param("operatorId") String operatorId, @Param("date") LocalDate date);

    /**
     * 统计指定操作员指定日期的退费笔数
     */
    @Query("SELECT COUNT(b) FROM OutpatientBilling b WHERE b.operatorId = :operatorId AND b.billingDate = :date AND b.refundStatus = 'COMPLETED' AND b.deleted = false")
    Long countRefundByOperatorAndDate(@Param("operatorId") String operatorId, @Param("date") LocalDate date);

    /**
     * 检查收费单号是否存在
     */
    boolean existsByBillingNo(String billingNo);
}
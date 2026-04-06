package com.yhj.his.module.finance.repository;

import com.yhj.his.module.finance.entity.InpatientSettlement;
import com.yhj.his.module.finance.entity.InpatientSettlement.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 住院结算Repository
 */
@Repository
public interface InpatientSettlementRepository extends JpaRepository<InpatientSettlement, String>, JpaSpecificationExecutor<InpatientSettlement> {

    /**
     * 根据结算单号查询
     */
    Optional<InpatientSettlement> findBySettlementNo(String settlementNo);

    /**
     * 根据发票号查询
     */
    Optional<InpatientSettlement> findByInvoiceNo(String invoiceNo);

    /**
     * 根据住院ID查询
     */
    Optional<InpatientSettlement> findByAdmissionId(String admissionId);

    /**
     * 根据患者ID查询
     */
    List<InpatientSettlement> findByPatientId(String patientId);

    /**
     * 根据状态查询
     */
    List<InpatientSettlement> findByStatus(SettlementStatus status);

    /**
     * 查询指定日期范围内的结算记录
     */
    @Query("SELECT s FROM InpatientSettlement s WHERE s.settlementTime >= :startDate AND s.settlementTime < :endDate AND s.status = :status AND s.deleted = false")
    List<InpatientSettlement> findByDateRangeAndStatus(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") SettlementStatus status);

    /**
     * 查询指定操作员指定日期的结算记录
     */
    @Query("SELECT s FROM InpatientSettlement s WHERE s.operatorId = :operatorId AND DATE(s.settlementTime) = :date AND s.status = 'NORMAL' AND s.deleted = false")
    List<InpatientSettlement> findByOperatorAndDate(@Param("operatorId") String operatorId, @Param("date") LocalDate date);

    /**
     * 统计指定操作员指定日期的结算金额
     */
    @Query("SELECT SUM(s.totalAmount) FROM InpatientSettlement s WHERE s.operatorId = :operatorId AND DATE(s.settlementTime) = :date AND s.status = 'NORMAL' AND s.deleted = false")
    java.math.BigDecimal sumAmountByOperatorAndDate(@Param("operatorId") String operatorId, @Param("date") LocalDate date);

    /**
     * 检查住院是否已结算
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM InpatientSettlement s WHERE s.admissionId = :admissionId AND s.status = 'NORMAL' AND s.deleted = false")
    boolean isAdmissionSettled(@Param("admissionId") String admissionId);

    /**
     * 检查结算单号是否存在
     */
    boolean existsBySettlementNo(String settlementNo);
}
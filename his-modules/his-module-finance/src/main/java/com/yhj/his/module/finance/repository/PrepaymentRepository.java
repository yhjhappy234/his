package com.yhj.his.module.finance.repository;

import com.yhj.his.module.finance.entity.Prepayment;
import com.yhj.his.module.finance.entity.Prepayment.DepositType;
import com.yhj.his.module.finance.entity.Prepayment.PrepaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 住院预交金Repository
 */
@Repository
public interface PrepaymentRepository extends JpaRepository<Prepayment, String>, JpaSpecificationExecutor<Prepayment> {

    /**
     * 根据预交金单号查询
     */
    Optional<Prepayment> findByPrepaymentNo(String prepaymentNo);

    /**
     * 根据住院ID查询
     */
    List<Prepayment> findByAdmissionId(String admissionId);

    /**
     * 根据患者ID查询
     */
    List<Prepayment> findByPatientId(String patientId);

    /**
     * 根据住院ID和状态查询
     */
    List<Prepayment> findByAdmissionIdAndStatus(String admissionId, PrepaymentStatus status);

    /**
     * 计算住院预交金余额
     */
    @Query("SELECT COALESCE(SUM(p.depositAmount), 0) FROM Prepayment p WHERE p.admissionId = :admissionId AND p.status = 'NORMAL' AND p.deleted = false")
    BigDecimal sumDepositByAdmissionId(@Param("admissionId") String admissionId);

    /**
     * 计算缴纳的预交金总额
     */
    @Query("SELECT COALESCE(SUM(p.depositAmount), 0) FROM Prepayment p WHERE p.admissionId = :admissionId AND p.depositType = 'DEPOSIT' AND p.status = 'NORMAL' AND p.deleted = false")
    BigDecimal sumDepositAmountByAdmissionId(@Param("admissionId") String admissionId);

    /**
     * 计算退还的预交金总额
     */
    @Query("SELECT COALESCE(SUM(p.depositAmount), 0) FROM Prepayment p WHERE p.admissionId = :admissionId AND p.depositType = 'REFUND' AND p.status = 'NORMAL' AND p.deleted = false")
    BigDecimal sumRefundAmountByAdmissionId(@Param("admissionId") String admissionId);

    /**
     * 查询指定时间范围内的预交金记录
     */
    @Query("SELECT p FROM Prepayment p WHERE p.operateTime BETWEEN :startTime AND :endTime AND p.status = 'NORMAL' AND p.deleted = false")
    List<Prepayment> findByOperateTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 检查预交金单号是否存在
     */
    boolean existsByPrepaymentNo(String prepaymentNo);
}
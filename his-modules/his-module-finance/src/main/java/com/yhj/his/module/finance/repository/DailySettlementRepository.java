package com.yhj.his.module.finance.repository;

import com.yhj.his.module.finance.entity.DailySettlement;
import com.yhj.his.module.finance.entity.DailySettlement.DailySettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 日结记录Repository
 */
@Repository
public interface DailySettlementRepository extends JpaRepository<DailySettlement, String>, JpaSpecificationExecutor<DailySettlement> {

    /**
     * 根据日结单号查询
     */
    Optional<DailySettlement> findBySettlementNo(String settlementNo);

    /**
     * 根据日结日期查询
     */
    List<DailySettlement> findBySettlementDate(LocalDate settlementDate);

    /**
     * 根据日结日期和操作员查询
     */
    Optional<DailySettlement> findBySettlementDateAndOperatorId(LocalDate settlementDate, String operatorId);

    /**
     * 根据状态查询
     */
    List<DailySettlement> findByStatus(DailySettlementStatus status);

    /**
     * 查询指定日期范围内的日结记录
     */
    @Query("SELECT d FROM DailySettlement d WHERE d.settlementDate BETWEEN :startDate AND :endDate AND d.deleted = false")
    List<DailySettlement> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 查询指定操作员的日结记录
     */
    @Query("SELECT d FROM DailySettlement d WHERE d.operatorId = :operatorId AND d.deleted = false ORDER BY d.settlementDate DESC")
    List<DailySettlement> findByOperatorId(@Param("operatorId") String operatorId);

    /**
     * 检查指定日期操作员是否已日结
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DailySettlement d WHERE d.settlementDate = :date AND d.operatorId = :operatorId AND d.status != 'PENDING' AND d.deleted = false")
    boolean isSettled(@Param("date") LocalDate date, @Param("operatorId") String operatorId);

    /**
     * 检查指定日期操作员是否存在待确认的日结记录
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DailySettlement d WHERE d.settlementDate = :date AND d.operatorId = :operatorId AND d.status = 'PENDING' AND d.deleted = false")
    boolean hasPendingSettlement(@Param("date") LocalDate date, @Param("operatorId") String operatorId);

    /**
     * 检查日结单号是否存在
     */
    boolean existsBySettlementNo(String settlementNo);
}
package com.yhj.his.module.pharmacy.repository;

import com.yhj.his.module.pharmacy.entity.InventoryTransaction;
import com.yhj.his.module.pharmacy.enums.InventoryOperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 库存流水Repository
 */
@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, String>, JpaSpecificationExecutor<InventoryTransaction> {

    /**
     * 根据流水号查询
     */
    Optional<InventoryTransaction> findByTransactionNo(String transactionNo);

    /**
     * 根据药品ID查询流水
     */
    List<InventoryTransaction> findByDrugId(String drugId);

    /**
     * 根据药房ID查询流水
     */
    List<InventoryTransaction> findByPharmacyId(String pharmacyId);

    /**
     * 根据操作类型查询
     */
    List<InventoryTransaction> findByTransactionType(InventoryOperationType transactionType);

    /**
     * 根据关联单据ID查询
     */
    List<InventoryTransaction> findByRelatedId(String relatedId);

    /**
     * 查询指定时间范围的流水
     */
    @Query("SELECT t FROM InventoryTransaction t WHERE t.pharmacyId = :pharmacyId AND t.operateTime BETWEEN :startTime AND :endTime AND t.deleted = false ORDER BY t.operateTime DESC")
    List<InventoryTransaction> findByPharmacyIdAndTimeRange(@Param("pharmacyId") String pharmacyId,
                                                             @Param("startTime") LocalDateTime startTime,
                                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 综合查询流水
     */
    @Query("SELECT t FROM InventoryTransaction t WHERE t.deleted = false " +
           "AND (:drugId IS NULL OR t.drugId = :drugId) " +
           "AND (:pharmacyId IS NULL OR t.pharmacyId = :pharmacyId) " +
           "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
           "AND (:relatedId IS NULL OR t.relatedId = :relatedId) " +
           "AND (:startTime IS NULL OR t.operateTime >= :startTime) " +
           "AND (:endTime IS NULL OR t.operateTime <= :endTime) " +
           "ORDER BY t.operateTime DESC")
    Page<InventoryTransaction> queryTransactions(@Param("drugId") String drugId,
                                                  @Param("pharmacyId") String pharmacyId,
                                                  @Param("transactionType") InventoryOperationType transactionType,
                                                  @Param("relatedId") String relatedId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime,
                                                  Pageable pageable);

    /**
     * 查询最新流水号
     */
    @Query("SELECT MAX(t.transactionNo) FROM InventoryTransaction t WHERE t.transactionNo LIKE :prefix")
    String findMaxTransactionNo(@Param("prefix") String prefix);

    /**
     * 根据药品ID查询流水(按操作时间降序)
     */
    @Query("SELECT t FROM InventoryTransaction t WHERE t.drugId = :drugId AND t.deleted = false ORDER BY t.operateTime DESC")
    List<InventoryTransaction> findByDrugIdOrderByOperateTimeDesc(@Param("drugId") String drugId);

    /**
     * 查询药房流水(指定日期范围)
     */
    @Query("SELECT t FROM InventoryTransaction t WHERE t.pharmacyId = :pharmacyId AND t.deleted = false " +
           "AND (:startDate IS NULL OR t.operateTime >= :startDate) " +
           "AND (:endDate IS NULL OR t.operateTime <= :endDate) " +
           "ORDER BY t.operateTime DESC")
    Page<InventoryTransaction> findByPharmacyIdAndDateRange(@Param("pharmacyId") String pharmacyId,
                                                            @Param("startDate") LocalDateTime startDate,
                                                            @Param("endDate") LocalDateTime endDate,
                                                            Pageable pageable);

    /**
     * 查询药房流水(指定操作类型和日期范围)
     */
    @Query("SELECT t FROM InventoryTransaction t WHERE t.pharmacyId = :pharmacyId AND t.transactionType = :transactionType AND t.deleted = false " +
           "AND (:startDate IS NULL OR t.operateTime >= :startDate) " +
           "AND (:endDate IS NULL OR t.operateTime <= :endDate) " +
           "ORDER BY t.operateTime DESC")
    Page<InventoryTransaction> findByPharmacyIdAndTransactionTypeAndDateRange(@Param("pharmacyId") String pharmacyId,
                                                                              @Param("transactionType") InventoryOperationType transactionType,
                                                                              @Param("startDate") LocalDateTime startDate,
                                                                              @Param("endDate") LocalDateTime endDate,
                                                                              Pageable pageable);

    /**
     * 统计药房流水数量(指定操作类型和日期范围)
     */
    @Query("SELECT COUNT(t) FROM InventoryTransaction t WHERE t.pharmacyId = :pharmacyId AND t.transactionType = :transactionType AND t.deleted = false " +
           "AND (:startDate IS NULL OR t.operateTime >= :startDate) " +
           "AND (:endDate IS NULL OR t.operateTime <= :endDate)")
    Long countByPharmacyIdAndTransactionTypeAndDateRange(@Param("pharmacyId") String pharmacyId,
                                                         @Param("transactionType") InventoryOperationType transactionType,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

    /**
     * 统计药房流水金额(指定操作类型和日期范围)
     */
    @Query("SELECT SUM(t.amount) FROM InventoryTransaction t WHERE t.pharmacyId = :pharmacyId AND t.transactionType = :transactionType AND t.deleted = false " +
           "AND (:startDate IS NULL OR t.operateTime >= :startDate) " +
           "AND (:endDate IS NULL OR t.operateTime <= :endDate)")
    java.math.BigDecimal sumAmountByPharmacyIdAndTransactionTypeAndDateRange(@Param("pharmacyId") String pharmacyId,
                                                                              @Param("transactionType") InventoryOperationType transactionType,
                                                                              @Param("startDate") LocalDateTime startDate,
                                                                              @Param("endDate") LocalDateTime endDate);

    /**
     * 综合查询流水(简化版)
     */
    @Query("SELECT t FROM InventoryTransaction t WHERE t.deleted = false " +
           "AND (:pharmacyId IS NULL OR t.pharmacyId = :pharmacyId) " +
           "AND (:drugId IS NULL OR t.drugId = :drugId) " +
           "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
           "AND (:startDate IS NULL OR t.operateTime >= :startDate) " +
           "AND (:endDate IS NULL OR t.operateTime <= :endDate) " +
           "ORDER BY t.operateTime DESC")
    Page<InventoryTransaction> queryTransactions(@Param("pharmacyId") String pharmacyId,
                                                  @Param("drugId") String drugId,
                                                  @Param("transactionType") InventoryOperationType transactionType,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate,
                                                  Pageable pageable);
}
package com.yhj.his.module.pharmacy.repository;

import com.yhj.his.module.pharmacy.entity.DrugInventory;
import com.yhj.his.module.pharmacy.enums.InventoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 药品库存Repository
 */
@Repository
public interface DrugInventoryRepository extends JpaRepository<DrugInventory, String>, JpaSpecificationExecutor<DrugInventory> {

    /**
     * 根据药品ID和批号和药房ID查询
     */
    Optional<DrugInventory> findByDrugIdAndBatchNoAndPharmacyId(String drugId, String batchNo, String pharmacyId);

    /**
     * 根据药品ID查询所有库存
     */
    List<DrugInventory> findByDrugId(String drugId);

    /**
     * 根据药品ID和药房ID查询库存
     */
    List<DrugInventory> findByDrugIdAndPharmacyId(String drugId, String pharmacyId);

    /**
     * 根据药房ID查询库存
     */
    List<DrugInventory> findByPharmacyId(String pharmacyId);

    /**
     * 根据批号查询
     */
    List<DrugInventory> findByBatchNo(String batchNo);

    /**
     * 查询指定药品的可用库存总和
     */
    @Query("SELECT SUM(i.availableQuantity) FROM DrugInventory i WHERE i.drugId = :drugId AND i.pharmacyId = :pharmacyId AND i.status = 'NORMAL' AND i.deleted = false")
    BigDecimal sumAvailableQuantityByDrugIdAndPharmacyId(@Param("drugId") String drugId, @Param("pharmacyId") String pharmacyId);

    /**
     * 查询指定药品的总库存总和
     */
    @Query("SELECT SUM(i.quantity) FROM DrugInventory i WHERE i.drugId = :drugId AND i.pharmacyId = :pharmacyId AND i.status = 'NORMAL' AND i.deleted = false")
    BigDecimal sumQuantityByDrugIdAndPharmacyId(@Param("drugId") String drugId, @Param("pharmacyId") String pharmacyId);

    /**
     * 查询效期预警库存(按效期升序)
     */
    @Query("SELECT i FROM DrugInventory i WHERE i.pharmacyId = :pharmacyId AND i.expiryDate <= :expiryDate AND i.status = 'NORMAL' AND i.deleted = false ORDER BY i.expiryDate ASC")
    List<DrugInventory> findExpiryAlert(@Param("pharmacyId") String pharmacyId, @Param("expiryDate") LocalDate expiryDate);

    /**
     * 查询已过期库存
     */
    @Query("SELECT i FROM DrugInventory i WHERE i.expiryDate < :currentDate AND i.status != 'EXPIRED' AND i.deleted = false")
    List<DrugInventory> findExpiredInventory(@Param("currentDate") LocalDate currentDate);

    /**
     * 查询按效期排序的可发药库存(先进先出)
     */
    @Query("SELECT i FROM DrugInventory i WHERE i.drugId = :drugId AND i.pharmacyId = :pharmacyId AND i.availableQuantity > 0 AND i.status = 'NORMAL' AND i.deleted = false ORDER BY i.expiryDate ASC")
    List<DrugInventory> findAvailableInventoryFIFO(@Param("drugId") String drugId, @Param("pharmacyId") String pharmacyId);

    /**
     * 综合查询库存
     */
    @Query("SELECT i FROM DrugInventory i WHERE i.deleted = false " +
           "AND (:drugId IS NULL OR i.drugId = :drugId) " +
           "AND (:drugCode IS NULL OR i.drugCode LIKE %:drugCode%) " +
           "AND (:drugName IS NULL OR i.drugName LIKE %:drugName%) " +
           "AND (:pharmacyId IS NULL OR i.pharmacyId = :pharmacyId) " +
           "AND (:batchNo IS NULL OR i.batchNo = :batchNo) " +
           "AND (:status IS NULL OR i.status = :status)")
    Page<DrugInventory> queryInventory(@Param("drugId") String drugId,
                                        @Param("drugCode") String drugCode,
                                        @Param("drugName") String drugName,
                                        @Param("pharmacyId") String pharmacyId,
                                        @Param("batchNo") String batchNo,
                                        @Param("status") InventoryStatus status,
                                        Pageable pageable);

    /**
     * 查询低库存药品
     */
    @Query("SELECT i FROM DrugInventory i WHERE i.pharmacyId = :pharmacyId AND i.status = 'NORMAL' AND i.deleted = false")
    List<DrugInventory> findByPharmacyIdForStockAlert(@Param("pharmacyId") String pharmacyId);

    /**
     * 查询效期预警库存
     */
    @Query("SELECT i FROM DrugInventory i WHERE i.expiryDate <= :expiryDate AND i.status = 'NORMAL' AND i.deleted = false")
    List<DrugInventory> findByExpiryDateBefore(@Param("expiryDate") LocalDate expiryDate);

    /**
     * 查询库存不足预警
     */
    @Query("SELECT i FROM DrugInventory i JOIN Drug d ON i.drugId = d.id WHERE i.quantity < d.minStock AND i.status = 'NORMAL' AND i.deleted = false")
    List<DrugInventory> findLowStockInventory();

    /**
     * 查询库存过剩预警
     */
    @Query("SELECT i FROM DrugInventory i JOIN Drug d ON i.drugId = d.id WHERE i.quantity > d.maxStock AND i.status = 'NORMAL' AND i.deleted = false")
    List<DrugInventory> findOverStockInventory();

    /**
     * 综合查询库存(简化版)
     */
    @Query("SELECT i FROM DrugInventory i WHERE i.deleted = false " +
           "AND (:pharmacyId IS NULL OR i.pharmacyId = :pharmacyId) " +
           "AND (:drugId IS NULL OR i.drugId = :drugId) " +
           "AND (:keyword IS NULL OR i.drugName LIKE %:keyword% OR i.drugCode LIKE %:keyword%) " +
           "AND (:batchNo IS NULL OR i.batchNo LIKE %:batchNo%) " +
           "AND (:status IS NULL OR i.status = :status)")
    Page<DrugInventory> queryInventory(@Param("pharmacyId") String pharmacyId,
                                        @Param("drugId") String drugId,
                                        @Param("keyword") String keyword,
                                        @Param("batchNo") String batchNo,
                                        @Param("status") InventoryStatus status,
                                        Pageable pageable);

    /**
     * 查询药房库存(关键词搜索)
     */
    @Query("SELECT i FROM DrugInventory i WHERE i.pharmacyId = :pharmacyId AND i.deleted = false " +
           "AND (:keyword IS NULL OR i.drugName LIKE %:keyword% OR i.drugCode LIKE %:keyword%)")
    Page<DrugInventory> findByPharmacyIdAndKeyword(@Param("pharmacyId") String pharmacyId,
                                                    @Param("keyword") String keyword,
                                                    Pageable pageable);
}
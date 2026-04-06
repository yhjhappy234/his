package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.MaterialInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 物资库存Repository
 */
@Repository
public interface MaterialInventoryRepository extends JpaRepository<MaterialInventory, String> {

    /**
     * 根据物资ID和库房ID查询
     */
    List<MaterialInventory> findByMaterialIdAndWarehouseIdAndDeletedFalse(String materialId, String warehouseId);

    /**
     * 根据物资ID、批号、库房ID查询
     */
    Optional<MaterialInventory> findByMaterialIdAndBatchNoAndWarehouseIdAndDeletedFalse(
            String materialId, String batchNo, String warehouseId);

    /**
     * 根据库房ID查询
     */
    List<MaterialInventory> findByWarehouseIdAndDeletedFalse(String warehouseId);

    /**
     * 根据物资ID查询所有库存
     */
    List<MaterialInventory> findByMaterialIdAndDeletedFalse(String materialId);

    /**
     * 查询效期预警库存
     */
    @Query("SELECT mi FROM MaterialInventory mi WHERE mi.deleted = false AND mi.status = 1 " +
           "AND mi.expiryDate IS NOT NULL AND mi.expiryDate <= :expiryDate AND mi.quantity > 0")
    List<MaterialInventory> findByExpiryDateBefore(@Param("expiryDate") LocalDate expiryDate);

    /**
     * 查询某物资总库存
     */
    @Query("SELECT SUM(mi.quantity) FROM MaterialInventory mi WHERE mi.deleted = false " +
           "AND mi.materialId = :materialId AND mi.status = 1")
    BigDecimal sumQuantityByMaterialId(@Param("materialId") String materialId);

    /**
     * 查询某物资在某库房的总库存
     */
    @Query("SELECT SUM(mi.quantity) FROM MaterialInventory mi WHERE mi.deleted = false " +
           "AND mi.materialId = :materialId AND mi.warehouseId = :warehouseId AND mi.status = 1")
    BigDecimal sumQuantityByMaterialIdAndWarehouseId(@Param("materialId") String materialId,
                                                       @Param("warehouseId") String warehouseId);

    /**
     * 查询可用库存（按先进先出）
     */
    @Query("SELECT mi FROM MaterialInventory mi WHERE mi.deleted = false AND mi.status = 1 " +
           "AND mi.materialId = :materialId AND mi.warehouseId = :warehouseId " +
           "AND mi.availableQuantity > 0 AND (mi.expiryDate IS NULL OR mi.expiryDate > :today) " +
           "ORDER BY mi.expiryDate ASC, mi.inboundTime ASC")
    List<MaterialInventory> findAvailableInventoryOrderByExpiry(@Param("materialId") String materialId,
                                                                 @Param("warehouseId") String warehouseId,
                                                                 @Param("today") LocalDate today);

    /**
     * 查询低库存
     */
    @Query("SELECT mi FROM MaterialInventory mi WHERE mi.deleted = false AND mi.status = 1 AND mi.quantity > 0")
    List<MaterialInventory> findAllActiveInventory();

    /**
     * 分页查询未删除的库存
     */
    Page<MaterialInventory> findByDeletedFalse(Pageable pageable);
}
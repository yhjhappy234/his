package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.MaterialAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 库存预警Repository
 */
@Repository
public interface MaterialAlertRepository extends JpaRepository<MaterialAlert, String> {

    /**
     * 根据预警类型查询
     */
    List<MaterialAlert> findByAlertTypeAndDeletedFalse(String alertType);

    /**
     * 根据状态查询
     */
    List<MaterialAlert> findByStatusAndDeletedFalse(Integer status);

    /**
     * 根据物资ID查询
     */
    List<MaterialAlert> findByMaterialIdAndDeletedFalse(String materialId);

    /**
     * 根据库房ID查询
     */
    List<MaterialAlert> findByWarehouseIdAndDeletedFalse(String warehouseId);

    /**
     * 分页查询
     */
    Page<MaterialAlert> findByDeletedFalse(Pageable pageable);

    /**
     * 条件查询
     */
    @Query("SELECT ma FROM MaterialAlert ma WHERE ma.deleted = false " +
           "AND (:alertType IS NULL OR ma.alertType = :alertType) " +
           "AND (:status IS NULL OR ma.status = :status) " +
           "AND (:warehouseId IS NULL OR ma.warehouseId = :warehouseId) " +
           "AND (:materialId IS NULL OR ma.materialId = :materialId)")
    Page<MaterialAlert> search(@Param("alertType") String alertType,
                                @Param("status") Integer status,
                                @Param("warehouseId") String warehouseId,
                                @Param("materialId") String materialId,
                                Pageable pageable);

    /**
     * 查询未处理的预警
     */
    @Query("SELECT ma FROM MaterialAlert ma WHERE ma.deleted = false AND ma.status = 0 ORDER BY ma.createTime DESC")
    List<MaterialAlert> findUnhandledAlerts();

    /**
     * 查询某物资未处理的预警
     */
    @Query("SELECT ma FROM MaterialAlert ma WHERE ma.deleted = false AND ma.materialId = :materialId AND ma.status = 0")
    List<MaterialAlert> findUnhandledAlertsByMaterialId(@Param("materialId") String materialId);
}
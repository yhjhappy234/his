package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.Material;
import com.yhj.his.module.inventory.enums.MaterialStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 物资信息Repository
 */
@Repository
public interface MaterialRepository extends JpaRepository<Material, String> {

    /**
     * 根据物资编码查询
     */
    Optional<Material> findByMaterialCode(String materialCode);

    /**
     * 根据物资名称查询
     */
    List<Material> findByMaterialNameAndDeletedFalse(String materialName);

    /**
     * 根据分类ID查询
     */
    List<Material> findByCategoryIdAndDeletedFalse(String categoryId);

    /**
     * 根据状态查询
     */
    List<Material> findByStatusAndDeletedFalse(MaterialStatus status);

    /**
     * 分页查询
     */
    Page<Material> findByDeletedFalse(Pageable pageable);

    /**
     * 检查物资编码是否存在
     */
    boolean existsByMaterialCodeAndDeletedFalse(String materialCode);

    /**
     * 模糊搜索
     */
    @Query("SELECT m FROM Material m WHERE m.deleted = false AND " +
           "(m.materialCode LIKE %:keyword% OR m.materialName LIKE %:keyword% OR m.manufacturer LIKE %:keyword%)")
    Page<Material> search(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询医疗耗材
     */
    @Query("SELECT m FROM Material m WHERE m.deleted = false AND m.isMedical = true")
    List<Material> findMedicalMaterials();

    /**
     * 查询需要库存预警的物资
     */
    @Query("SELECT m FROM Material m WHERE m.deleted = false AND m.status = :status AND m.minStock IS NOT NULL")
    List<Material> findMaterialsForLowStockAlert(@Param("status") MaterialStatus status);
}
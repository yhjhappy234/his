package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.MaterialCheckItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 库存盘点明细Repository
 */
@Repository
public interface MaterialCheckItemRepository extends JpaRepository<MaterialCheckItem, String> {

    /**
     * 根据盘点记录ID查询
     */
    List<MaterialCheckItem> findByCheckId(String checkId);

    /**
     * 根据物资ID查询
     */
    List<MaterialCheckItem> findByMaterialId(String materialId);

    /**
     * 查询有差异的盘点明细
     */
    @Query("SELECT mci FROM MaterialCheckItem mci WHERE mci.check.id = :checkId AND mci.diffType IS NOT NULL AND mci.diffType != 'NONE'")
    List<MaterialCheckItem> findDiffItems(@Param("checkId") String checkId);

    /**
     * 查询未调整的盘点明细
     */
    @Query("SELECT mci FROM MaterialCheckItem mci WHERE mci.check.id = :checkId AND mci.adjusted = false AND mci.diffType IS NOT NULL AND mci.diffType != 'NONE'")
    List<MaterialCheckItem> findUnadjustedDiffItems(@Param("checkId") String checkId);
}
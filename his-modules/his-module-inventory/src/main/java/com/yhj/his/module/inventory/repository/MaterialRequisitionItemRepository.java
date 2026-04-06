package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.MaterialRequisitionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物资申领明细Repository
 */
@Repository
public interface MaterialRequisitionItemRepository extends JpaRepository<MaterialRequisitionItem, String> {

    /**
     * 根据申领记录ID查询
     */
    List<MaterialRequisitionItem> findByRequisitionId(String requisitionId);

    /**
     * 根据物资ID查询
     */
    List<MaterialRequisitionItem> findByMaterialId(String materialId);
}
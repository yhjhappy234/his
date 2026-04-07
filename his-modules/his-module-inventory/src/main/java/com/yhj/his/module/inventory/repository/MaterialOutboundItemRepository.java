package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.MaterialOutboundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 出库明细Repository
 */
@Repository
public interface MaterialOutboundItemRepository extends JpaRepository<MaterialOutboundItem, String> {

    /**
     * 根据出库记录ID查询
     */
    List<MaterialOutboundItem> findByOutboundId(String outboundId);

    /**
     * 根据物资ID查询
     */
    List<MaterialOutboundItem> findByMaterialId(String materialId);
}
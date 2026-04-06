package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.MaterialInboundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 入库明细Repository
 */
@Repository
public interface MaterialInboundItemRepository extends JpaRepository<MaterialInboundItem, String> {

    /**
     * 根据入库记录ID查询
     */
    List<MaterialInboundItem> findByInboundId(String inboundId);

    /**
     * 根据物资ID查询
     */
    List<MaterialInboundItem> findByMaterialId(String materialId);
}
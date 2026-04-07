package com.yhj.his.module.finance.repository;

import com.yhj.his.module.finance.entity.PriceItem;
import com.yhj.his.module.finance.entity.PriceItem.ItemCategory;
import com.yhj.his.module.finance.entity.PriceItem.InsuranceType;
import com.yhj.his.module.finance.entity.PriceItem.PriceItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 收费项目Repository
 */
@Repository
public interface PriceItemRepository extends JpaRepository<PriceItem, String>, JpaSpecificationExecutor<PriceItem> {

    /**
     * 根据项目编码查询
     */
    Optional<PriceItem> findByItemCode(String itemCode);

    /**
     * 检查项目编码是否存在
     */
    boolean existsByItemCode(String itemCode);

    /**
     * 根据项目分类查询
     */
    List<PriceItem> findByItemCategory(ItemCategory category);

    /**
     * 根据状态查询
     */
    List<PriceItem> findByStatus(PriceItemStatus status);

    /**
     * 根据项目分类和状态查询
     */
    List<PriceItem> findByItemCategoryAndStatus(ItemCategory category, PriceItemStatus status);

    /**
     * 查询生效的项目
     */
    @Query("SELECT p FROM PriceItem p WHERE p.status = :status AND p.effectiveDate <= :date AND (p.expireDate IS NULL OR p.expireDate >= :date)")
    List<PriceItem> findEffectiveItems(@Param("status") PriceItemStatus status, @Param("date") LocalDate date);

    /**
     * 根据医保类型查询
     */
    List<PriceItem> findByInsuranceType(InsuranceType insuranceType);

    /**
     * 根据名称模糊查询
     */
    @Query("SELECT p FROM PriceItem p WHERE p.itemName LIKE %:name% AND p.deleted = false")
    List<PriceItem> findByItemNameContaining(@Param("name") String name);

    /**
     * 查询未删除的所有项目
     */
    @Query("SELECT p FROM PriceItem p WHERE p.deleted = false")
    List<PriceItem> findAllActive();
}
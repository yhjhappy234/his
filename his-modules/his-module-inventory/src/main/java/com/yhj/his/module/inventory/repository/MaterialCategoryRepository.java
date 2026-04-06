package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.MaterialCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 物资分类Repository
 */
@Repository
public interface MaterialCategoryRepository extends JpaRepository<MaterialCategory, String> {

    /**
     * 根据分类编码查询
     */
    Optional<MaterialCategory> findByCategoryCode(String categoryCode);

    /**
     * 根据父ID查询子分类
     */
    List<MaterialCategory> findByParentIdAndDeletedFalse(String parentId);

    /**
     * 查询顶级分类
     */
    List<MaterialCategory> findByParentIdIsNullAndDeletedFalse();

    /**
     * 根据状态查询
     */
    List<MaterialCategory> findByStatusAndDeletedFalse(Integer status);

    /**
     * 检查分类编码是否存在
     */
    boolean existsByCategoryCodeAndDeletedFalse(String categoryCode);

    /**
     * 查询分类树
     */
    @Query("SELECT mc FROM MaterialCategory mc WHERE mc.deleted = false ORDER BY mc.level, mc.sortOrder")
    List<MaterialCategory> findAllOrderByLevelAndSortOrder();

    /**
     * 分页查询未删除的分类
     */
    Page<MaterialCategory> findByDeletedFalse(Pageable pageable);
}
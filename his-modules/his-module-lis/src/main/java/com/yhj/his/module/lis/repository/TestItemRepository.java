package com.yhj.his.module.lis.repository;

import com.yhj.his.module.lis.entity.TestItem;
import com.yhj.his.module.lis.enums.TestItemCategory;
import com.yhj.his.module.lis.enums.TestItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 检验项目Repository
 */
@Repository
public interface TestItemRepository extends JpaRepository<TestItem, String>, JpaSpecificationExecutor<TestItem> {

    /**
     * 根据项目编码查询
     */
    Optional<TestItem> findByItemCode(String itemCode);

    /**
     * 根据项目名称查询
     */
    Optional<TestItem> findByItemName(String itemName);

    /**
     * 根据分类查询
     */
    List<TestItem> findByCategory(TestItemCategory category);

    /**
     * 根据状态查询
     */
    List<TestItem> findByStatus(TestItemStatus status);

    /**
     * 查询正常状态的项目
     */
    List<TestItem> findByStatusOrderByItemName(TestItemStatus status);

    /**
     * 根据拼音码模糊查询
     */
    @Query("SELECT t FROM TestItem t WHERE t.pinyinCode LIKE :pinyinCode AND t.status = :status AND t.deleted = false")
    List<TestItem> findByPinyinCodeLikeAndStatus(@Param("pinyinCode") String pinyinCode, @Param("status") TestItemStatus status);

    /**
     * 根据名称或拼音码模糊查询
     */
    @Query("SELECT t FROM TestItem t WHERE (t.itemName LIKE :keyword OR t.pinyinCode LIKE :keyword OR t.itemCode LIKE :keyword) AND t.status = :status AND t.deleted = false")
    Page<TestItem> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") TestItemStatus status, Pageable pageable);

    /**
     * 查询有危急值的项目
     */
    @Query("SELECT t FROM TestItem t WHERE t.critical = true AND t.status = :status AND t.deleted = false")
    List<TestItem> findByCriticalTrueAndStatus(@Param("status") TestItemStatus status);

    /**
     * 检查项目编码是否存在
     */
    boolean existsByItemCode(String itemCode);
}
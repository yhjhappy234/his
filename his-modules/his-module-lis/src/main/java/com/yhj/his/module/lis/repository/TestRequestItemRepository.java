package com.yhj.his.module.lis.repository;

import com.yhj.his.module.lis.entity.TestRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 检验申请明细Repository
 */
@Repository
public interface TestRequestItemRepository extends JpaRepository<TestRequestItem, String>, JpaSpecificationExecutor<TestRequestItem> {

    /**
     * 根据申请ID查询所有明细
     */
    List<TestRequestItem> findByRequestId(String requestId);

    /**
     * 根据申请ID和项目ID查询
     */
    @Query("SELECT t FROM TestRequestItem t WHERE t.requestId = :requestId AND t.itemId = :itemId AND t.deleted = false")
    List<TestRequestItem> findByRequestIdAndItemId(@Param("requestId") String requestId, @Param("itemId") String itemId);

    /**
     * 根据样本ID查询
     */
    List<TestRequestItem> findBySampleId(String sampleId);

    /**
     * 根据申请ID删除所有明细
     */
    void deleteByRequestId(String requestId);

    /**
     * 统计申请的项目数量
     */
    @Query("SELECT COUNT(t) FROM TestRequestItem t WHERE t.requestId = :requestId AND t.deleted = false")
    long countByRequestId(@Param("requestId") String requestId);
}
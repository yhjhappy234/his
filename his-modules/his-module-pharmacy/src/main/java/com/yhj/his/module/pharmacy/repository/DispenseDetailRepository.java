package com.yhj.his.module.pharmacy.repository;

import com.yhj.his.module.pharmacy.entity.DispenseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 发药明细Repository
 */
@Repository
public interface DispenseDetailRepository extends JpaRepository<DispenseDetail, String>, JpaSpecificationExecutor<DispenseDetail> {

    /**
     * 根据发药ID查询明细
     */
    List<DispenseDetail> findByDispenseId(String dispenseId);

    /**
     * 根据药品ID查询明细
     */
    List<DispenseDetail> findByDrugId(String drugId);

    /**
     * 根据批号查询明细
     */
    List<DispenseDetail> findByBatchNo(String batchNo);

    /**
     * 查询指定发药记录的明细
     */
    @Query("SELECT d FROM DispenseDetail d WHERE d.dispenseId = :dispenseId AND d.deleted = false ORDER BY d.createTime ASC")
    List<DispenseDetail> findByDispenseIdOrderById(@Param("dispenseId") String dispenseId);

    /**
     * 删除指定发药记录的明细
     */
    @Query("DELETE FROM DispenseDetail d WHERE d.dispenseId = :dispenseId")
    void deleteByDispenseId(@Param("dispenseId") String dispenseId);
}
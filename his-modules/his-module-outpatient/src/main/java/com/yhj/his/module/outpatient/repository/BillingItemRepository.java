package com.yhj.his.module.outpatient.repository;

import com.yhj.his.module.outpatient.entity.BillingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 收费项目Repository
 */
@Repository
public interface BillingItemRepository extends JpaRepository<BillingItem, String>, JpaSpecificationExecutor<BillingItem> {

    /**
     * 根据挂号ID查询收费项目
     */
    List<BillingItem> findByRegistrationId(String registrationId);

    /**
     * 根据患者ID查询收费项目
     */
    List<BillingItem> findByPatientId(String patientId);

    /**
     * 根据挂号ID和收费状态查询
     */
    List<BillingItem> findByRegistrationIdAndPayStatus(String registrationId, String payStatus);

    /**
     * 根据收费状态查询
     */
    List<BillingItem> findByPayStatus(String payStatus);

    /**
     * 批量更新收费状态
     */
    @Modifying
    @Query("UPDATE BillingItem b SET b.payStatus = :payStatus WHERE b.id IN :ids")
    int updatePayStatusByIds(@Param("ids") List<String> ids, @Param("payStatus") String payStatus);
}
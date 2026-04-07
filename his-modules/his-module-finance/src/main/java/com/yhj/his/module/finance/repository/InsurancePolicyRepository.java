package com.yhj.his.module.finance.repository;

import com.yhj.his.module.finance.entity.InsurancePolicy;
import com.yhj.his.module.finance.entity.InsurancePolicy.InsurancePolicyStatus;
import com.yhj.his.module.finance.entity.InsurancePolicy.InsuranceTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 医保政策Repository
 */
@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, String>, JpaSpecificationExecutor<InsurancePolicy> {

    /**
     * 根据医保类型查询
     */
    Optional<InsurancePolicy> findByInsuranceType(InsuranceTypeEnum insuranceType);

    /**
     * 根据状态查询
     */
    List<InsurancePolicy> findByStatus(InsurancePolicyStatus status);

    /**
     * 根据医保类型和状态查询启用的政策
     */
    Optional<InsurancePolicy> findByInsuranceTypeAndStatus(InsuranceTypeEnum insuranceType, InsurancePolicyStatus status);

    /**
     * 查询所有启用的医保政策
     */
    @Query("SELECT i FROM InsurancePolicy i WHERE i.status = 'ACTIVE' AND i.deleted = false")
    List<InsurancePolicy> findAllActive();

    /**
     * 根据政策名称查询
     */
    Optional<InsurancePolicy> findByPolicyName(String policyName);
}
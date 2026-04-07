package com.yhj.his.module.outpatient.repository;

import com.yhj.his.module.outpatient.entity.BillingSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 收费结算Repository
 */
@Repository
public interface BillingSettlementRepository extends JpaRepository<BillingSettlement, String>, JpaSpecificationExecutor<BillingSettlement> {

    /**
     * 根据结算单号查询
     */
    Optional<BillingSettlement> findBySettlementNo(String settlementNo);

    /**
     * 根据发票号查询
     */
    Optional<BillingSettlement> findByInvoiceNo(String invoiceNo);

    /**
     * 根据挂号ID查询结算记录
     */
    Optional<BillingSettlement> findByRegistrationId(String registrationId);
}
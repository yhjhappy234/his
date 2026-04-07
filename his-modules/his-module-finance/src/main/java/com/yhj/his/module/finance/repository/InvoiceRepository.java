package com.yhj.his.module.finance.repository;

import com.yhj.his.module.finance.entity.Invoice;
import com.yhj.his.module.finance.entity.Invoice.InvoiceStatus;
import com.yhj.his.module.finance.entity.Invoice.InvoiceType;
import com.yhj.his.module.finance.entity.Invoice.BillingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 发票Repository
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String>, JpaSpecificationExecutor<Invoice> {

    /**
     * 根据发票号查询
     */
    Optional<Invoice> findByInvoiceNo(String invoiceNo);

    /**
     * 根据发票代码查询
     */
    Optional<Invoice> findByInvoiceCode(String invoiceCode);

    /**
     * 根据收费ID查询
     */
    Optional<Invoice> findByBillingId(String billingId);

    /**
     * 根据患者ID查询
     */
    List<Invoice> findByPatientId(String patientId);

    /**
     * 根据发票日期查询
     */
    List<Invoice> findByInvoiceDate(LocalDate invoiceDate);

    /**
     * 根据状态查询
     */
    List<Invoice> findByStatus(InvoiceStatus status);

    /**
     * 根据发票类型查询
     */
    List<Invoice> findByInvoiceType(InvoiceType invoiceType);

    /**
     * 根据收费类型查询
     */
    List<Invoice> findByBillingType(BillingType billingType);

    /**
     * 查询指定日期范围内的发票
     */
    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate AND i.status = :status AND i.deleted = false")
    List<Invoice> findByDateRangeAndStatus(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") InvoiceStatus status);

    /**
     * 查询指定操作员指定日期的发票
     */
    @Query("SELECT i FROM Invoice i WHERE i.operatorId = :operatorId AND i.invoiceDate = :date AND i.status = 'VALID' AND i.deleted = false")
    List<Invoice> findByOperatorAndDate(@Param("operatorId") String operatorId, @Param("date") LocalDate date);

    /**
     * 统计指定操作员指定日期的发票金额
     */
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.operatorId = :operatorId AND i.invoiceDate = :date AND i.status = 'VALID' AND i.deleted = false")
    java.math.BigDecimal sumAmountByOperatorAndDate(@Param("operatorId") String operatorId, @Param("date") LocalDate date);

    /**
     * 查询最大的发票号
     */
    @Query("SELECT MAX(i.invoiceNo) FROM Invoice i WHERE i.invoiceDate = :date")
    Optional<String> findMaxInvoiceNoByDate(@Param("date") LocalDate date);

    /**
     * 检查发票号是否存在
     */
    boolean existsByInvoiceNo(String invoiceNo);
}
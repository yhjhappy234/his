package com.yhj.his.module.inpatient.repository;

import com.yhj.his.module.inpatient.entity.InpatientFee;
import com.yhj.his.module.inpatient.enums.FeeCategory;
import com.yhj.his.module.inpatient.enums.PayStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 住院费用Repository
 */
@Repository
public interface InpatientFeeRepository extends JpaRepository<InpatientFee, String> {

    /**
     * 根据住院ID查询费用
     */
    List<InpatientFee> findByAdmissionId(String admissionId);

    /**
     * 根据住院ID分页查询费用
     */
    Page<InpatientFee> findByAdmissionId(String admissionId, Pageable pageable);

    /**
     * 根据住院ID和费用分类查询
     */
    List<InpatientFee> findByAdmissionIdAndFeeCategory(String admissionId, FeeCategory feeCategory);

    /**
     * 根据住院ID和结算状态查询
     */
    List<InpatientFee> findByAdmissionIdAndPayStatus(String admissionId, PayStatus payStatus);

    /**
     * 根据费用日期查询
     */
    List<InpatientFee> findByAdmissionIdAndFeeDate(String admissionId, LocalDate feeDate);

    /**
     * 统计住院总费用
     */
    @Query("SELECT SUM(f.feeAmount) FROM InpatientFee f WHERE f.admissionId = :admissionId")
    BigDecimal sumFeeAmountByAdmissionId(@Param("admissionId") String admissionId);

    /**
     * 统计未结算费用
     */
    @Query("SELECT SUM(f.feeAmount) FROM InpatientFee f WHERE f.admissionId = :admissionId AND f.payStatus = 'UNSETTLED'")
    BigDecimal sumUnsettledFeeByAdmissionId(@Param("admissionId") String admissionId);

    /**
     * 统计各分类费用
     */
    @Query("SELECT SUM(f.feeAmount) FROM InpatientFee f WHERE f.admissionId = :admissionId AND f.feeCategory = :category")
    BigDecimal sumFeeAmountByAdmissionIdAndCategory(
            @Param("admissionId") String admissionId,
            @Param("category") FeeCategory category);

    /**
     * 统计每日费用
     */
    @Query("SELECT SUM(f.feeAmount) FROM InpatientFee f WHERE f.admissionId = :admissionId AND f.feeDate = :feeDate")
    BigDecimal sumFeeAmountByAdmissionIdAndFeeDate(
            @Param("admissionId") String admissionId,
            @Param("feeDate") LocalDate feeDate);

    /**
     * 查询费用明细列表
     */
    @Query("SELECT f FROM InpatientFee f WHERE f.admissionId = :admissionId ORDER BY f.feeDate DESC, f.createTime DESC")
    List<InpatientFee> findByAdmissionIdOrderByDate(@Param("admissionId") String admissionId);
}
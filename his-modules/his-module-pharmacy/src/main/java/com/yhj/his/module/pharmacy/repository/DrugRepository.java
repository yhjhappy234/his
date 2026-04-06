package com.yhj.his.module.pharmacy.repository;

import com.yhj.his.module.pharmacy.entity.Drug;
import com.yhj.his.module.pharmacy.enums.DrugCategory;
import com.yhj.his.module.pharmacy.enums.DrugStatus;
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
 * 药品Repository
 */
@Repository
public interface DrugRepository extends JpaRepository<Drug, String>, JpaSpecificationExecutor<Drug> {

    /**
     * 根据药品编码查询
     */
    Optional<Drug> findByDrugCode(String drugCode);

    /**
     * 检查药品编码是否存在
     */
    boolean existsByDrugCode(String drugCode);

    /**
     * 根据药品名称模糊查询
     */
    List<Drug> findByDrugNameContaining(String drugName);

    /**
     * 根据拼音码模糊查询
     */
    List<Drug> findByPinyinCodeContaining(String pinyinCode);

    /**
     * 根据分类查询
     */
    List<Drug> findByDrugCategory(DrugCategory drugCategory);

    /**
     * 根据分类和状态查询
     */
    List<Drug> findByDrugCategoryAndStatus(DrugCategory drugCategory, DrugStatus status);

    /**
     * 根据状态查询
     */
    List<Drug> findByStatus(DrugStatus status);

    /**
     * 查询正常状态的药品
     */
    @Query("SELECT d FROM Drug d WHERE d.status = :status AND d.deleted = false")
    Page<Drug> findNormalDrugs(@Param("status") DrugStatus status, Pageable pageable);

    /**
     * 综合查询药品
     */
    @Query("SELECT d FROM Drug d WHERE d.deleted = false " +
           "AND (:keyword IS NULL OR d.drugName LIKE %:keyword% OR d.drugCode LIKE %:keyword% OR d.pinyinCode LIKE %:keyword%) " +
           "AND (:category IS NULL OR d.drugCategory = :category) " +
           "AND (:drugForm IS NULL OR d.drugForm = :drugForm) " +
           "AND (:isPrescription IS NULL OR d.isPrescription = :isPrescription) " +
           "AND (:isOtc IS NULL OR d.isOtc = :isOtc) " +
           "AND (:isInsurance IS NULL OR d.isInsurance = :isInsurance) " +
           "AND (:manufacturer IS NULL OR d.manufacturer LIKE %:manufacturer%) " +
           "AND (:status IS NULL OR d.status = :status)")
    Page<Drug> queryDrugs(@Param("keyword") String keyword,
                          @Param("category") DrugCategory category,
                          @Param("drugForm") String drugForm,
                          @Param("isPrescription") Boolean isPrescription,
                          @Param("isOtc") Boolean isOtc,
                          @Param("isInsurance") Boolean isInsurance,
                          @Param("manufacturer") String manufacturer,
                          @Param("status") DrugStatus status,
                          Pageable pageable);

    /**
     * 查询医保药品
     */
    @Query("SELECT d FROM Drug d WHERE d.isInsurance = true AND d.status = 'NORMAL' AND d.deleted = false")
    List<Drug> findInsuranceDrugs();

    /**
     * 查询处方药
     */
    @Query("SELECT d FROM Drug d WHERE d.isPrescription = true AND d.status = 'NORMAL' AND d.deleted = false")
    List<Drug> findPrescriptionDrugs();

    /**
     * 查询OTC药品
     */
    @Query("SELECT d FROM Drug d WHERE d.isOtc = true AND d.status = 'NORMAL' AND d.deleted = false")
    List<Drug> findOtcDrugs();
}
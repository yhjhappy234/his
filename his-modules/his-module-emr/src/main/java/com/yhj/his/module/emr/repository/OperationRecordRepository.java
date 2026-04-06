package com.yhj.his.module.emr.repository;

import com.yhj.his.module.emr.entity.OperationRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 手术记录Repository
 */
@Repository
public interface OperationRecordRepository extends JpaRepository<OperationRecord, String>, JpaSpecificationExecutor<OperationRecord> {

    /**
     * 根据住院ID查询手术记录列表
     */
    List<OperationRecord> findByAdmissionIdAndDeletedFalseOrderByOperationDateDesc(String admissionId);

    /**
     * 根据患者ID查询
     */
    List<OperationRecord> findByPatientIdAndDeletedFalseOrderByOperationDateDesc(String patientId);

    /**
     * 根据科室查询
     */
    Page<OperationRecord> findByDeptIdAndDeletedFalse(String deptId, Pageable pageable);

    /**
     * 根据主刀医生查询
     */
    Page<OperationRecord> findBySurgeonIdAndDeletedFalse(String surgeonId, Pageable pageable);

    /**
     * 根据手术日期查询
     */
    Page<OperationRecord> findByOperationDateAndDeletedFalse(LocalDate operationDate, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<OperationRecord> findByStatusAndDeletedFalse(EmrStatus status, Pageable pageable);

    /**
     * 根据患者姓名模糊查询
     */
    Page<OperationRecord> findByPatientNameContainingAndDeletedFalse(String patientName, Pageable pageable);

    /**
     * 根据手术名称模糊查询
     */
    Page<OperationRecord> findByOperationNameContainingAndDeletedFalse(String operationName, Pageable pageable);

    /**
     * 查询住院期间的手术记录
     */
    @Query("SELECT o FROM OperationRecord o WHERE o.admissionId = :admissionId " +
           "AND o.operationDate >= :startDate AND o.operationDate <= :endDate AND o.deleted = false " +
           "ORDER BY o.operationDate DESC")
    List<OperationRecord> findByAdmissionIdAndDateRange(
            @Param("admissionId") String admissionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 统计医生手术数量
     */
    @Query("SELECT COUNT(o) FROM OperationRecord o WHERE o.surgeonId = :surgeonId AND o.deleted = false")
    Long countBySurgeonId(@Param("surgeonId") String surgeonId);
}
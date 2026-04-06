package com.yhj.his.module.emr.repository;

import com.yhj.his.module.emr.entity.OutpatientEmr;
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
 * 门诊病历Repository
 */
@Repository
public interface OutpatientEmrRepository extends JpaRepository<OutpatientEmr, String>, JpaSpecificationExecutor<OutpatientEmr> {

    /**
     * 根据就诊ID查询
     */
    Optional<OutpatientEmr> findByVisitIdAndDeletedFalse(String visitId);

    /**
     * 根据患者ID查询病历列表
     */
    List<OutpatientEmr> findByPatientIdAndDeletedFalseOrderByVisitDateDesc(String patientId);

    /**
     * 根据医生ID查询病历
     */
    Page<OutpatientEmr> findByDoctorIdAndDeletedFalse(String doctorId, Pageable pageable);

    /**
     * 根据科室和日期查询
     */
    Page<OutpatientEmr> findByDeptIdAndVisitDateAndDeletedFalse(String deptId, LocalDate visitDate, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<OutpatientEmr> findByStatusAndDeletedFalse(EmrStatus status, Pageable pageable);

    /**
     * 查询患者的最新病历
     */
    Optional<OutpatientEmr> findFirstByPatientIdAndDeletedFalseOrderByVisitDateDesc(String patientId);

    /**
     * 根据科室ID和状态查询
     */
    Page<OutpatientEmr> findByDeptIdAndStatusAndDeletedFalse(String deptId, EmrStatus status, Pageable pageable);

    /**
     * 根据患者姓名模糊查询
     */
    Page<OutpatientEmr> findByPatientNameContainingAndDeletedFalse(String patientName, Pageable pageable);

    /**
     * 统计科室某日期的病历数
     */
    @Query("SELECT COUNT(e) FROM OutpatientEmr e WHERE e.deptId = :deptId " +
           "AND e.visitDate = :visitDate AND e.deleted = false")
    Long countByDeptIdAndVisitDate(@Param("deptId") String deptId, @Param("visitDate") LocalDate visitDate);

    /**
     * 查询待审核病历
     */
    @Query("SELECT e FROM OutpatientEmr e WHERE e.status = :status AND e.deleted = false")
    Page<OutpatientEmr> findPendingAudit(@Param("status") EmrStatus status, Pageable pageable);
}
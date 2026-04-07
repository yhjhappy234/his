package com.yhj.his.module.inpatient.repository;

import com.yhj.his.module.inpatient.entity.InpatientAdmission;
import com.yhj.his.module.inpatient.enums.AdmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 住院记录Repository
 */
@Repository
public interface InpatientAdmissionRepository extends JpaRepository<InpatientAdmission, String> {

    /**
     * 根据住院号查询
     */
    Optional<InpatientAdmission> findByAdmissionNo(String admissionNo);

    /**
     * 根据患者ID查询
     */
    List<InpatientAdmission> findByPatientId(String patientId);

    /**
     * 根据患者ID和状态查询
     */
    Optional<InpatientAdmission> findByPatientIdAndStatus(String patientId, AdmissionStatus status);

    /**
     * 根据科室ID和状态查询
     */
    List<InpatientAdmission> findByDeptIdAndStatus(String deptId, AdmissionStatus status);

    /**
     * 根据病区ID和状态查询
     */
    List<InpatientAdmission> findByWardIdAndStatus(String wardId, AdmissionStatus status);

    /**
     * 根据状态查询
     */
    List<InpatientAdmission> findByStatus(AdmissionStatus status);

    /**
     * 根据状态分页查询
     */
    Page<InpatientAdmission> findByStatus(AdmissionStatus status, Pageable pageable);

    /**
     * 统计科室住院人数
     */
    @Query("SELECT COUNT(a) FROM InpatientAdmission a WHERE a.deptId = :deptId AND a.status = :status")
    Long countByDeptIdAndStatus(@Param("deptId") String deptId, @Param("status") AdmissionStatus status);

    /**
     * 统计病区住院人数
     */
    @Query("SELECT COUNT(a) FROM InpatientAdmission a WHERE a.wardId = :wardId AND a.status = :status")
    Long countByWardIdAndStatus(@Param("wardId") String wardId, @Param("status") AdmissionStatus status);

    /**
     * 查询患者是否正在住院
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM InpatientAdmission a WHERE a.patientId = :patientId AND a.status IN ('IN_HOSPITAL', 'PENDING', 'TRANSFERRING')")
    boolean isInHospital(@Param("patientId") String patientId);

    /**
     * 根据床位号查询住院记录
     */
    Optional<InpatientAdmission> findByBedNoAndStatus(String bedNo, AdmissionStatus status);
}
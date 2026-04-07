package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.EmployeeCertificate;
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
 * 员工证件资质Repository
 */
@Repository
public interface EmployeeCertificateRepository extends JpaRepository<EmployeeCertificate, String>, JpaSpecificationExecutor<EmployeeCertificate> {

    /**
     * 根据员工ID查找证件列表
     */
    List<EmployeeCertificate> findByEmployeeIdAndDeletedFalse(String employeeId);

    /**
     * 根据员工ID和证件类型查找证件
     */
    Optional<EmployeeCertificate> findByEmployeeIdAndCertTypeAndDeletedFalse(String employeeId, String certType);

    /**
     * 根据证件类型查找证件列表
     */
    List<EmployeeCertificate> findByCertTypeAndDeletedFalse(String certType);

    /**
     * 根据状态查找证件列表
     */
    List<EmployeeCertificate> findByStatusAndDeletedFalse(String status);

    /**
     * 分页查询证件
     */
    @Query("SELECT c FROM EmployeeCertificate c WHERE c.deleted = false " +
           "AND (:employeeId IS NULL OR c.employeeId = :employeeId) " +
           "AND (:certType IS NULL OR c.certType = :certType) " +
           "AND (:status IS NULL OR c.status = :status)")
    Page<EmployeeCertificate> findByConditions(@Param("employeeId") String employeeId,
                                                @Param("certType") String certType,
                                                @Param("status") String status,
                                                Pageable pageable);

    /**
     * 查找即将过期的证件
     */
    @Query("SELECT c FROM EmployeeCertificate c WHERE c.deleted = false " +
           "AND c.validEndDate IS NOT NULL " +
           "AND c.status = '有效' " +
           "AND c.validEndDate <= CURRENT_DATE")
    List<EmployeeCertificate> findExpiringCertificates();

    /**
     * 检查证件编号是否存在
     */
    boolean existsByCertNo(String certNo);

    /**
     * 根据ID查找未删除的证件
     */
    Optional<EmployeeCertificate> findByIdAndDeletedFalse(String id);
}
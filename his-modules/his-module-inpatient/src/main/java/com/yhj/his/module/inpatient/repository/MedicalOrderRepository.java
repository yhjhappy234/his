package com.yhj.his.module.inpatient.repository;

import com.yhj.his.module.inpatient.entity.MedicalOrder;
import com.yhj.his.module.inpatient.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 医嘱Repository
 */
@Repository
public interface MedicalOrderRepository extends JpaRepository<MedicalOrder, String> {

    /**
     * 根据医嘱编号查询
     */
    Optional<MedicalOrder> findByOrderNo(String orderNo);

    /**
     * 根据住院ID查询医嘱
     */
    List<MedicalOrder> findByAdmissionId(String admissionId);

    /**
     * 根据住院ID分页查询医嘱
     */
    Page<MedicalOrder> findByAdmissionId(String admissionId, Pageable pageable);

    /**
     * 分页查询所有医嘱
     */
    Page<MedicalOrder> findAll(Pageable pageable);

    /**
     * 根据住院ID和状态查询医嘱
     */
    List<MedicalOrder> findByAdmissionIdAndStatus(String admissionId, OrderStatus status);

    /**
     * 根据患者ID查询医嘱
     */
    List<MedicalOrder> findByPatientId(String patientId);

    /**
     * 根据状态查询医嘱
     */
    List<MedicalOrder> findByStatus(OrderStatus status);

    /**
     * 查询正在执行的长期医嘱
     */
    @Query("SELECT o FROM MedicalOrder o WHERE o.admissionId = :admissionId AND o.status IN ('AUDITED', 'EXECUTING') AND o.orderType = 'LONG_TERM'")
    List<MedicalOrder> findActiveLongTermOrders(@Param("admissionId") String admissionId);

    /**
     * 查询待审核的医嘱
     */
    @Query("SELECT o FROM MedicalOrder o WHERE o.admissionId = :admissionId AND o.status = 'NEW'")
    List<MedicalOrder> findPendingAuditOrders(@Param("admissionId") String admissionId);

    /**
     * 统计住院医嘱数量
     */
    @Query("SELECT COUNT(o) FROM MedicalOrder o WHERE o.admissionId = :admissionId")
    Long countByAdmissionId(@Param("admissionId") String admissionId);

    /**
     * 根据组号查询医嘱
     */
    List<MedicalOrder> findByGroupNo(Integer groupNo);
}
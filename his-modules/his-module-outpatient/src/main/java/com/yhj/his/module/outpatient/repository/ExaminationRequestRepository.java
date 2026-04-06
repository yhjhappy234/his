package com.yhj.his.module.outpatient.repository;

import com.yhj.his.module.outpatient.entity.ExaminationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 检查检验申请Repository
 */
@Repository
public interface ExaminationRequestRepository extends JpaRepository<ExaminationRequest, String>, JpaSpecificationExecutor<ExaminationRequest> {

    /**
     * 根据申请单号查询
     */
    Optional<ExaminationRequest> findByRequestNo(String requestNo);

    /**
     * 根据挂号ID查询申请列表
     */
    List<ExaminationRequest> findByRegistrationId(String registrationId);

    /**
     * 根据患者ID查询申请列表
     */
    List<ExaminationRequest> findByPatientIdOrderByRequestDateDesc(String patientId);

    /**
     * 根据医生ID和日期查询申请列表
     */
    List<ExaminationRequest> findByDoctorIdAndRequestDate(String doctorId, LocalDate requestDate);

    /**
     * 根据状态查询申请列表
     */
    List<ExaminationRequest> findByStatus(String status);

    /**
     * 根据挂号ID和收费状态查询
     */
    List<ExaminationRequest> findByRegistrationIdAndPayStatus(String registrationId, String payStatus);
}
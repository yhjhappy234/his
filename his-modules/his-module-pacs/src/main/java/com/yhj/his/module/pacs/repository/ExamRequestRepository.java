package com.yhj.his.module.pacs.repository;

import com.yhj.his.module.pacs.entity.ExamRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRequestRepository extends JpaRepository<ExamRequest, String>, JpaSpecificationExecutor<ExamRequest> {

    Optional<ExamRequest> findByRequestNo(String requestNo);
    List<ExamRequest> findByPatientId(String patientId);
    List<ExamRequest> findByStatus(String status);
    List<ExamRequest> findByDeptId(String deptId);
    List<ExamRequest> findByExamType(String examType);
    List<ExamRequest> findByVisitType(String visitType);
    List<ExamRequest> findByIsEmergencyTrue();

    @Query("SELECT COUNT(e) FROM ExamRequest e WHERE DATE(e.requestTime) = DATE(:date)")
    Long countByRequestDate(@Param("date") LocalDateTime date);

    @Query("SELECT e.status, COUNT(e) FROM ExamRequest e GROUP BY e.status")
    List<Object[]> countByStatus();

    @Query("SELECT e FROM ExamRequest e WHERE e.deleted = false " +
           "AND (:requestNo IS NULL OR e.requestNo LIKE %:requestNo%) " +
           "AND (:patientId IS NULL OR e.patientId = :patientId) " +
           "AND (:patientName IS NULL OR e.patientName LIKE %:patientName%) " +
           "AND (:visitType IS NULL OR e.visitType = :visitType) " +
           "AND (:examType IS NULL OR e.examType = :examType) " +
           "AND (:status IS NULL OR e.status = :status) " +
           "AND (:deptId IS NULL OR e.deptId = :deptId) " +
           "AND (:isEmergency IS NULL OR e.isEmergency = :isEmergency) " +
           "AND (:requestTimeStart IS NULL OR e.requestTime >= :requestTimeStart) " +
           "AND (:requestTimeEnd IS NULL OR e.requestTime <= :requestTimeEnd)")
    Page<ExamRequest> findByConditions(@Param("requestNo") String requestNo,
                                        @Param("patientId") String patientId,
                                        @Param("patientName") String patientName,
                                        @Param("visitType") String visitType,
                                        @Param("examType") String examType,
                                        @Param("status") String status,
                                        @Param("deptId") String deptId,
                                        @Param("isEmergency") Boolean isEmergency,
                                        @Param("requestTimeStart") LocalDateTime requestTimeStart,
                                        @Param("requestTimeEnd") LocalDateTime requestTimeEnd,
                                        Pageable pageable);
}
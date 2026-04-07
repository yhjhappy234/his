package com.yhj.his.module.pacs.repository;

import com.yhj.his.module.pacs.entity.ExamReport;
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
public interface ExamReportRepository extends JpaRepository<ExamReport, String>, JpaSpecificationExecutor<ExamReport> {

    Optional<ExamReport> findByReportNo(String reportNo);
    Optional<ExamReport> findByExamId(String examId);
    Optional<ExamReport> findByRequestId(String requestId);
    List<ExamReport> findByPatientId(String patientId);
    List<ExamReport> findByReportStatus(String reportStatus);
    List<ExamReport> findByWriterId(String writerId);
    List<ExamReport> findByReviewerId(String reviewerId);

    @Query("SELECT r FROM ExamReport r WHERE r.reportStatus = '待审核'")
    List<ExamReport> findPendingReview();

    @Query("SELECT r.reportStatus, COUNT(r) FROM ExamReport r GROUP BY r.reportStatus")
    List<Object[]> countByReportStatus();

    @Query("SELECT r FROM ExamReport r WHERE " +
           "(:reportNo IS NULL OR r.reportNo LIKE :reportNo) AND " +
           "(:examId IS NULL OR r.examId = :examId) AND " +
           "(:patientId IS NULL OR r.patientId = :patientId) AND " +
           "(:patientName IS NULL OR r.patientName LIKE :patientName) AND " +
           "(:reportStatus IS NULL OR r.reportStatus = :reportStatus) AND " +
           "(:writerId IS NULL OR r.writerId = :writerId) AND " +
           "(:reviewerId IS NULL OR r.reviewerId = :reviewerId) AND " +
           "(:writeTimeStart IS NULL OR r.writeTime >= :writeTimeStart) AND " +
           "(:writeTimeEnd IS NULL OR r.writeTime <= :writeTimeEnd)")
    Page<ExamReport> findByConditions(
            @Param("reportNo") String reportNo,
            @Param("examId") String examId,
            @Param("patientId") String patientId,
            @Param("patientName") String patientName,
            @Param("reportStatus") String reportStatus,
            @Param("writerId") String writerId,
            @Param("reviewerId") String reviewerId,
            @Param("writeTimeStart") LocalDateTime writeTimeStart,
            @Param("writeTimeEnd") LocalDateTime writeTimeEnd,
            Pageable pageable);
}
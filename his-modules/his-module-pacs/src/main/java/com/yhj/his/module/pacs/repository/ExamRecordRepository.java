package com.yhj.his.module.pacs.repository;

import com.yhj.his.module.pacs.entity.ExamRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRecordRepository extends JpaRepository<ExamRecord, String>, JpaSpecificationExecutor<ExamRecord> {

    Optional<ExamRecord> findByExamNo(String examNo);
    Optional<ExamRecord> findByRequestId(String requestId);
    List<ExamRecord> findByPatientId(String patientId);
    List<ExamRecord> findByExamStatus(String examStatus);
    List<ExamRecord> findByReportStatus(String reportStatus);
    List<ExamRecord> findByEquipmentId(String equipmentId);

    @Query("SELECT e FROM ExamRecord e WHERE e.examStatus = '检查完成' AND (e.reportStatus IS NULL OR e.reportStatus = '待报告')")
    List<ExamRecord> findPendingReport();

    Long countByExamType(String examType);
}
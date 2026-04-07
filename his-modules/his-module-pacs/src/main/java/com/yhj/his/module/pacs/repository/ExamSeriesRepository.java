package com.yhj.his.module.pacs.repository;

import com.yhj.his.module.pacs.entity.ExamSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamSeriesRepository extends JpaRepository<ExamSeries, String> {

    List<ExamSeries> findByExamId(String examId);
    Optional<ExamSeries> findBySeriesUid(String seriesUid);
    List<ExamSeries> findByModality(String modality);

    @Query("SELECT COUNT(s) FROM ExamSeries s WHERE s.examId = :examId")
    Integer countByExamId(@Param("examId") String examId);
}
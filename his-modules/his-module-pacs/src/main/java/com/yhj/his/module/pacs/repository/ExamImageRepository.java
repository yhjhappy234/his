package com.yhj.his.module.pacs.repository;

import com.yhj.his.module.pacs.entity.ExamImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamImageRepository extends JpaRepository<ExamImage, String> {

    List<ExamImage> findBySeriesId(String seriesId);
    List<ExamImage> findByExamId(String examId);
    Optional<ExamImage> findByImageUid(String imageUid);
    List<ExamImage> findByIsKeyImageTrue();

    @Query("SELECT i FROM ExamImage i WHERE i.examId = :examId AND i.isKeyImage = true")
    List<ExamImage> findKeyImagesByExamId(@Param("examId") String examId);

    @Query("SELECT COUNT(i) FROM ExamImage i WHERE i.seriesId = :seriesId")
    Integer countBySeriesId(@Param("seriesId") String seriesId);

    @Query("SELECT COUNT(i) FROM ExamImage i WHERE i.examId = :examId")
    Integer countByExamId(@Param("examId") String examId);
}
package com.yhj.his.module.lis.repository;

import com.yhj.his.module.lis.entity.Sample;
import com.yhj.his.module.lis.enums.SampleStatus;
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

/**
 * 样本Repository
 */
@Repository
public interface SampleRepository extends JpaRepository<Sample, String>, JpaSpecificationExecutor<Sample> {

    /**
     * 根据样本编号查询
     */
    Optional<Sample> findBySampleNo(String sampleNo);

    /**
     * 根据申请ID查询
     */
    List<Sample> findByRequestId(String requestId);

    /**
     * 根据患者ID查询
     */
    List<Sample> findByPatientId(String patientId);

    /**
     * 根据状态查询
     */
    List<Sample> findBySampleStatus(SampleStatus status);

    /**
     * 查询待采集样本
     */
    @Query("SELECT s FROM Sample s WHERE s.sampleStatus = 'PENDING' AND s.deleted = false ORDER BY s.createTime ASC")
    List<Sample> findPendingSamples();

    /**
     * 查询待核收样本
     */
    @Query("SELECT s FROM Sample s WHERE s.sampleStatus = 'COLLECTED' AND s.deleted = false ORDER BY s.collectionTime ASC")
    List<Sample> findCollectedSamples();

    /**
     * 查询急诊样本（已采集待核收）
     */
    @Query("SELECT s FROM Sample s JOIN TestRequest r ON s.requestId = r.id WHERE s.sampleStatus = 'COLLECTED' AND r.emergency = true AND s.deleted = false ORDER BY s.collectionTime ASC")
    List<Sample> findEmergencyCollectedSamples();

    /**
     * 根据采集时间范围查询
     */
    @Query("SELECT s FROM Sample s WHERE s.collectionTime BETWEEN :startTime AND :endTime AND s.deleted = false")
    Page<Sample> findByCollectionTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);

    /**
     * 统计某状态的样本数量
     */
    @Query("SELECT COUNT(s) FROM Sample s WHERE s.sampleStatus = :status AND s.deleted = false")
    long countBySampleStatus(@Param("status") SampleStatus status);
}
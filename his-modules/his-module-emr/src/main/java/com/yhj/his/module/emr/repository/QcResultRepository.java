package com.yhj.his.module.emr.repository;

import com.yhj.his.module.emr.entity.QcResult;
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
 * 质控结果Repository
 */
@Repository
public interface QcResultRepository extends JpaRepository<QcResult, String>, JpaSpecificationExecutor<QcResult> {

    /**
     * 根据病历记录ID查询
     */
    Optional<QcResult> findByRecordIdAndDeletedFalse(String recordId);

    /**
     * 根据病历记录ID和记录类型查询
     */
    Optional<QcResult> findByRecordIdAndRecordTypeAndDeletedFalse(String recordId, String recordType);

    /**
     * 根据患者ID查询质控结果
     */
    List<QcResult> findByPatientIdAndDeletedFalseOrderByCreateTimeDesc(String patientId);

    /**
     * 查询需要整改的质控结果
     */
    @Query("SELECT q FROM QcResult q WHERE q.needRectification = true " +
           "AND q.rectificationStatus = '待整改' AND q.deleted = false")
    Page<QcResult> findPendingRectification(Pageable pageable);

    /**
     * 查询指定患者的整改记录
     */
    @Query("SELECT q FROM QcResult q WHERE q.patientId = :patientId " +
           "AND q.needRectification = true AND q.deleted = false")
    List<QcResult> findByPatientIdNeedRectification(@Param("patientId") String patientId);

    /**
     * 根据质控等级统计
     */
    @Query("SELECT COUNT(q) FROM QcResult q WHERE q.qcLevel = :level AND q.deleted = false")
    Long countByQcLevel(@Param("level") String level);

    /**
     * 根据质控人查询
     */
    Page<QcResult> findByQcUserIdAndDeletedFalse(String qcUserId, Pageable pageable);

    /**
     * 根据整改状态查询
     */
    Page<QcResult> findByRectificationStatusAndDeletedFalse(String status, Pageable pageable);

    /**
     * 根据ID查询未删除的质控结果
     */
    Optional<QcResult> findByIdAndDeletedFalse(String id);
}
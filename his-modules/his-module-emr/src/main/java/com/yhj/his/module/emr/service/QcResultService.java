package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.RectificationDTO;
import com.yhj.his.module.emr.entity.QcResult;
import com.yhj.his.module.emr.enums.QcLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 质控结果服务接口
 */
public interface QcResultService {

    /**
     * 创建质控结果
     */
    QcResult createQcResult(QcResult qcResult);

    /**
     * 更新质控结果
     */
    QcResult updateQcResult(String id, QcResult qcResult);

    /**
     * 删除质控结果
     */
    void deleteQcResult(String id);

    /**
     * 根据ID获取质控结果
     */
    QcResult getQcResultById(String id);

    /**
     * 根据病历记录ID查询质控结果
     */
    Optional<QcResult> getQcResultByRecordId(String recordId);

    /**
     * 根据病历记录ID和记录类型查询质控结果
     */
    Optional<QcResult> getQcResultByRecordIdAndType(String recordId, String recordType);

    /**
     * 根据患者ID查询质控结果
     */
    List<QcResult> getQcResultsByPatientId(String patientId);

    /**
     * 分页查询质控结果
     */
    Page<QcResult> listQcResults(Pageable pageable);

    /**
     * 查询需要整改的质控结果
     */
    Page<QcResult> getPendingRectifications(Pageable pageable);

    /**
     * 查询指定患者的整改记录
     */
    List<QcResult> getRectificationsByPatientId(String patientId);

    /**
     * 根据质控等级统计
     */
    Long countByQcLevel(QcLevel level);

    /**
     * 根据质控人查询
     */
    Page<QcResult> getQcResultsByQcUserId(String qcUserId, Pageable pageable);

    /**
     * 根据整改状态查询
     */
    Page<QcResult> getQcResultsByRectificationStatus(String status, Pageable pageable);

    /**
     * 执行质控检查
     */
    QcResult performQcCheck(String recordId, String recordType, String qcUserId, String qcUserName);

    /**
     * 发送整改通知
     */
    QcResult sendRectificationNotice(String id);

    /**
     * 完成整改
     */
    QcResult completeRectification(RectificationDTO dto);

    /**
     * 超期整改标记
     */
    QcResult markRectificationOverdue(String id);

    /**
     * 根据质控等级获取质控结果列表
     */
    Page<QcResult> getQcResultsByLevel(QcLevel qcLevel, Pageable pageable);
}
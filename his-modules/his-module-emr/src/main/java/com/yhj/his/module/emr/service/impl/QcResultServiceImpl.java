package com.yhj.his.module.emr.service.impl;

import com.yhj.his.module.emr.dto.RectificationDTO;
import com.yhj.his.module.emr.entity.QcResult;
import com.yhj.his.module.emr.enums.QcLevel;
import com.yhj.his.module.emr.repository.QcResultRepository;
import com.yhj.his.module.emr.service.QcResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 质控结果服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QcResultServiceImpl implements QcResultService {

    private final QcResultRepository qcResultRepository;

    @Override
    @Transactional
    public QcResult createQcResult(QcResult qcResult) {
        return qcResultRepository.save(qcResult);
    }

    @Override
    @Transactional
    public QcResult updateQcResult(String id, QcResult qcResult) {
        QcResult existing = getQcResultById(id);
        // 合并更新
        if (qcResult.getQcScore() != null) {
            existing.setQcScore(qcResult.getQcScore());
        }
        if (qcResult.getQcLevel() != null) {
            existing.setQcLevel(qcResult.getQcLevel());
        }
        if (qcResult.getDefectCount() != null) {
            existing.setDefectCount(qcResult.getDefectCount());
        }
        if (qcResult.getDefectDetails() != null) {
            existing.setDefectDetails(qcResult.getDefectDetails());
        }
        if (qcResult.getQcComment() != null) {
            existing.setQcComment(qcResult.getQcComment());
        }
        return qcResultRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteQcResult(String id) {
        QcResult qcResult = getQcResultById(id);
        qcResult.setDeleted(true);
        qcResultRepository.save(qcResult);
    }

    @Override
    public QcResult getQcResultById(String id) {
        return qcResultRepository.findById(id)
                .filter(q -> !q.getDeleted())
                .orElseThrow(() -> new RuntimeException("质控结果不存在: " + id));
    }

    @Override
    public Optional<QcResult> getQcResultByRecordId(String recordId) {
        return qcResultRepository.findByRecordIdAndDeletedFalse(recordId);
    }

    @Override
    public Optional<QcResult> getQcResultByRecordIdAndType(String recordId, String recordType) {
        return qcResultRepository.findByRecordIdAndRecordTypeAndDeletedFalse(recordId, recordType);
    }

    @Override
    public List<QcResult> getQcResultsByPatientId(String patientId) {
        return qcResultRepository.findByPatientIdAndDeletedFalseOrderByCreateTimeDesc(patientId);
    }

    @Override
    public Page<QcResult> listQcResults(Pageable pageable) {
        Page<QcResult> page = qcResultRepository.findAll(pageable);
        List<QcResult> filtered = page.getContent().stream()
                .filter(q -> !q.getDeleted())
                .toList();
        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public Page<QcResult> getPendingRectifications(Pageable pageable) {
        return qcResultRepository.findPendingRectification(pageable);
    }

    @Override
    public List<QcResult> getRectificationsByPatientId(String patientId) {
        return qcResultRepository.findByPatientIdNeedRectification(patientId);
    }

    @Override
    public Long countByQcLevel(QcLevel level) {
        return qcResultRepository.countByQcLevel(level.name());
    }

    @Override
    public Page<QcResult> getQcResultsByQcUserId(String qcUserId, Pageable pageable) {
        return qcResultRepository.findByQcUserIdAndDeletedFalse(qcUserId, pageable);
    }

    @Override
    public Page<QcResult> getQcResultsByRectificationStatus(String status, Pageable pageable) {
        return qcResultRepository.findByRectificationStatusAndDeletedFalse(status, pageable);
    }

    @Override
    @Transactional
    public QcResult performQcCheck(String recordId, String recordType, String qcUserId, String qcUserName) {
        // 检查是否已存在质控结果
        Optional<QcResult> existing = getQcResultByRecordIdAndType(recordId, recordType);
        if (existing.isPresent()) {
            log.info("记录 {} 已存在质控结果，将更新", recordId);
        }

        QcResult qcResult = existing.orElse(new QcResult());
        qcResult.setRecordId(recordId);
        qcResult.setRecordType(recordType);
        qcResult.setQcUserId(qcUserId);
        qcResult.setQcUserName(qcUserName);
        qcResult.setQcTime(LocalDateTime.now());

        // 执行质控检查逻辑（这里简化处理，实际应根据业务规则检查）
        int score = calculateQcScore(recordId, recordType);
        qcResult.setQcScore(score);
        qcResult.setQcLevel(QcLevel.fromScore(score));

        // 设置整改相关信息
        if (score < 90) {
            qcResult.setNeedRectification(true);
            qcResult.setRectificationStatus("待整改");
        } else {
            qcResult.setNeedRectification(false);
        }

        return qcResultRepository.save(qcResult);
    }

    @Override
    @Transactional
    public QcResult sendRectificationNotice(String id) {
        QcResult qcResult = getQcResultById(id);
        if (!Boolean.TRUE.equals(qcResult.getNeedRectification())) {
            throw new RuntimeException("该质控结果不需要整改");
        }
        qcResult.setNotifyTime(LocalDateTime.now());
        qcResult.setRectificationStatus("待整改");
        return qcResultRepository.save(qcResult);
    }

    @Override
    @Transactional
    public QcResult completeRectification(RectificationDTO dto) {
        QcResult qcResult = getQcResultById(dto.getQcResultId());
        if (!"待整改".equals(qcResult.getRectificationStatus())) {
            throw new RuntimeException("只有待整改状态的质控结果可以完成整改");
        }
        qcResult.setRectificationStatus("已整改");
        qcResult.setRectifyTime(LocalDateTime.now());
        qcResult.setRectifyComment(dto.getRectifyComment());
        return qcResultRepository.save(qcResult);
    }

    @Override
    @Transactional
    public QcResult markRectificationOverdue(String id) {
        QcResult qcResult = getQcResultById(id);
        if ("待整改".equals(qcResult.getRectificationStatus())) {
            qcResult.setRectificationStatus("已超期");
            return qcResultRepository.save(qcResult);
        }
        return qcResult;
    }

    @Override
    public Page<QcResult> getQcResultsByLevel(QcLevel qcLevel, Pageable pageable) {
        // 使用JpaSpecificationExecutor实现
        return qcResultRepository.findAll((root, query, cb) ->
                cb.and(cb.equal(root.get("qcLevel"), qcLevel),
                       cb.equal(root.get("deleted"), false)), pageable);
    }

    /**
     * 计算质控评分（简化实现）
     */
    private int calculateQcScore(String recordId, String recordType) {
        // 实际应根据业务规则进行复杂的质控评分计算
        // 这里返回一个默认分数
        log.info("计算质控评分: recordId={}, recordType={}", recordId, recordType);
        return 95; // 默认甲级
    }
}
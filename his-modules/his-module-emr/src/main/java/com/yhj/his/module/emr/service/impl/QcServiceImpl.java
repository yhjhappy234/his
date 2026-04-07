package com.yhj.his.module.emr.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.emr.dto.RectificationDTO;
import com.yhj.his.module.emr.entity.QcResult;
import com.yhj.his.module.emr.enums.DefectType;
import com.yhj.his.module.emr.enums.QcLevel;
import com.yhj.his.module.emr.repository.QcResultRepository;
import com.yhj.his.module.emr.service.QcService;
import com.yhj.his.module.emr.vo.DefectVO;
import com.yhj.his.module.emr.vo.QcResultVO;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 病历质控服务实现
 */
@Service
@RequiredArgsConstructor
public class QcServiceImpl implements QcService {

    private final QcResultRepository qcResultRepository;

    @Override
    @Transactional
    public QcResultVO performQcCheck(String recordId, String recordType) {
        List<DefectVO> defects = new ArrayList<>();
        int totalScore = 100;

        // 执行质控检查逻辑（简化实现）
        // 实际应根据具体规则检查时限、内容完整性等

        QcResult qcResult = new QcResult();
        qcResult.setRecordId(recordId);
        qcResult.setRecordType(recordType);
        qcResult.setQcScore(totalScore);
        qcResult.setQcLevel(QcLevel.fromScore(totalScore));
        qcResult.setDefectCount(defects.size());
        qcResult.setDefectDetails(JSONUtil.toJsonStr(defects));
        qcResult.setTimeLimitPassed(true);
        qcResult.setContentPassed(defects.isEmpty());
        qcResult.setQcTime(LocalDateTime.now());
        qcResult.setNeedRectification(totalScore < 75);
        qcResult.setRectificationStatus(totalScore < 75 ? "待整改" : null);

        qcResult = qcResultRepository.save(qcResult);
        return BeanUtil.copyProperties(qcResult, QcResultVO.class);
    }

    @Override
    public QcSubmitResultVO performQcCheckAndReturn(String recordId, String recordType) {
        QcResultVO qcResult = performQcCheck(recordId, recordType);

        QcSubmitResultVO result = new QcSubmitResultVO();
        result.setRecordId(recordId);
        result.setQcScore(qcResult.getQcScore());
        result.setQcLevel(qcResult.getQcLevel().getDescription());
        result.setPassed(qcResult.getQcScore() >= 75);

        if (StrUtil.isNotBlank(qcResult.getDefectDetails())) {
            result.setDefects(JSONUtil.toList(qcResult.getDefectDetails(), DefectVO.class));
        } else {
            result.setDefects(new ArrayList<>());
        }

        return result;
    }

    @Override
    public QcResultVO getQcResult(String recordId) {
        QcResult qcResult = qcResultRepository.findByRecordIdAndDeletedFalse(recordId)
                .orElseThrow(() -> new BusinessException("质控结果不存在"));
        return BeanUtil.copyProperties(qcResult, QcResultVO.class);
    }

    @Override
    public QcResultVO getQcResultById(String qcResultId) {
        QcResult qcResult = qcResultRepository.findByIdAndDeletedFalse(qcResultId)
                .orElseThrow(() -> new BusinessException("质控结果不存在"));
        return BeanUtil.copyProperties(qcResult, QcResultVO.class);
    }

    @Override
    public PageResult<QcResultVO> getPatientQcResults(String patientId, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<QcResult> page = qcResultRepository.findAll(pageable);
        List<QcResultVO> list = page.getContent().stream()
                .filter(q -> patientId.equals(q.getPatientId()) && !q.getDeleted())
                .map(q -> BeanUtil.copyProperties(q, QcResultVO.class))
                .collect(Collectors.toList());
        return PageResult.of(list, (long) list.size(), pageNum, pageSize);
    }

    @Override
    public PageResult<QcResultVO> getPendingRectificationList(Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.ASC, "notifyTime"));
        Page<QcResult> page = qcResultRepository.findPendingRectification(pageable);
        List<QcResultVO> list = page.getContent().stream()
                .map(q -> BeanUtil.copyProperties(q, QcResultVO.class))
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    @Transactional
    public void sendRectificationNotice(String qcResultId) {
        QcResult qcResult = qcResultRepository.findByIdAndDeletedFalse(qcResultId)
                .orElseThrow(() -> new BusinessException("质控结果不存在"));
        qcResult.setNotifyTime(LocalDateTime.now());
        qcResult.setRectificationStatus("待整改");
        qcResultRepository.save(qcResult);
    }

    @Override
    @Transactional
    public void completeRectification(RectificationDTO dto) {
        QcResult qcResult = qcResultRepository.findByIdAndDeletedFalse(dto.getQcResultId())
                .orElseThrow(() -> new BusinessException("质控结果不存在"));
        qcResult.setRectificationStatus("已整改");
        qcResult.setRectifyTime(LocalDateTime.now());
        qcResult.setRectifyComment(dto.getRectifyComment());
        qcResultRepository.save(qcResult);
    }

    @Override
    @Transactional
    public QcResultVO manualReview(String qcResultId, String qcUserId, String qcUserName,
                                    Integer score, String comment) {
        QcResult qcResult = qcResultRepository.findByIdAndDeletedFalse(qcResultId)
                .orElseThrow(() -> new BusinessException("质控结果不存在"));
        qcResult.setQcUserId(qcUserId);
        qcResult.setQcUserName(qcUserName);
        qcResult.setQcScore(score);
        qcResult.setQcLevel(QcLevel.fromScore(score));
        qcResult.setQcComment(comment);
        qcResult.setNeedRectification(score < 75);
        if (score < 75) {
            qcResult.setRectificationStatus("待整改");
        }
        qcResult = qcResultRepository.save(qcResult);
        return BeanUtil.copyProperties(qcResult, QcResultVO.class);
    }
}
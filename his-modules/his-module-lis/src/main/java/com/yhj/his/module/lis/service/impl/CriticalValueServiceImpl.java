package com.yhj.his.module.lis.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.CriticalValueConfirmDTO;
import com.yhj.his.module.lis.dto.CriticalValueHandleDTO;
import com.yhj.his.module.lis.dto.CriticalValueNotifyDTO;
import com.yhj.his.module.lis.entity.CriticalValue;
import com.yhj.his.module.lis.enums.CriticalValueStatus;
import com.yhj.his.module.lis.repository.CriticalValueRepository;
import com.yhj.his.module.lis.service.CriticalValueService;
import com.yhj.his.module.lis.vo.CriticalValueVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 危急值服务实现
 */
@Service
@RequiredArgsConstructor
public class CriticalValueServiceImpl implements CriticalValueService {

    private final CriticalValueRepository criticalValueRepository;

    @Override
    @Transactional
    public CriticalValueVO create(String requestId, String sampleId, String resultId, String patientId,
                                   String patientName, String itemId, String itemName, String testValue,
                                   String criticalLevel, String criticalRange, String detecterId, String detecterName) {
        CriticalValue cv = new CriticalValue();
        cv.setRequestId(requestId);
        cv.setSampleId(sampleId);
        cv.setResultId(resultId);
        cv.setPatientId(patientId);
        cv.setPatientName(patientName);
        cv.setItemId(itemId);
        cv.setItemName(itemName);
        cv.setTestValue(testValue);
        cv.setCriticalLevel(criticalLevel);
        cv.setCriticalRange(criticalRange);
        cv.setDetectTime(LocalDateTime.now());
        cv.setDetecterId(detecterId);
        cv.setDetecterName(detecterName);
        cv.setStatus(CriticalValueStatus.PENDING);

        CriticalValue saved = criticalValueRepository.save(cv);
        return toVO(saved);
    }

    @Override
    @Transactional
    public CriticalValueVO notify(CriticalValueNotifyDTO dto) {
        CriticalValue cv = criticalValueRepository.findById(dto.getCriticalValueId())
                .orElseThrow(() -> new IllegalArgumentException("危急值不存在: " + dto.getCriticalValueId()));

        cv.setNotifyMethod(dto.getNotifyMethod());
        cv.setNotifierId(dto.getNotifierId());
        cv.setNotifierName(dto.getNotifierName());
        cv.setNotifyTime(dto.getNotifyTime() != null ? dto.getNotifyTime() : LocalDateTime.now());
        cv.setStatus(CriticalValueStatus.NOTIFIED);
        cv.setRemark(dto.getRemark());

        CriticalValue saved = criticalValueRepository.save(cv);
        return toVO(saved);
    }

    @Override
    @Transactional
    public CriticalValueVO confirm(CriticalValueConfirmDTO dto) {
        CriticalValue cv = criticalValueRepository.findById(dto.getCriticalValueId())
                .orElseThrow(() -> new IllegalArgumentException("危急值不存在: " + dto.getCriticalValueId()));

        cv.setReceiverDept(dto.getReceiverDept());
        cv.setReceiverName(dto.getReceiverName());
        cv.setReceiverPhone(dto.getReceiverPhone());
        cv.setReceiveTime(dto.getReceiveTime() != null ? dto.getReceiveTime() : LocalDateTime.now());
        cv.setStatus(CriticalValueStatus.CONFIRMED);
        cv.setRemark(dto.getRemark());

        CriticalValue saved = criticalValueRepository.save(cv);
        return toVO(saved);
    }

    @Override
    @Transactional
    public CriticalValueVO handle(CriticalValueHandleDTO dto) {
        CriticalValue cv = criticalValueRepository.findById(dto.getCriticalValueId())
                .orElseThrow(() -> new IllegalArgumentException("危急值不存在: " + dto.getCriticalValueId()));

        cv.setHandlerId(dto.getHandlerId());
        cv.setHandlerName(dto.getHandlerName());
        cv.setHandleResult(dto.getHandleResult());
        cv.setHandleTime(dto.getHandleTime() != null ? dto.getHandleTime() : LocalDateTime.now());
        cv.setStatus(CriticalValueStatus.HANDLED);
        cv.setRemark(dto.getRemark());

        CriticalValue saved = criticalValueRepository.save(cv);
        return toVO(saved);
    }

    @Override
    @Transactional
    public CriticalValueVO close(String id) {
        CriticalValue cv = criticalValueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("危急值不存在: " + id));

        cv.setStatus(CriticalValueStatus.CLOSED);

        CriticalValue saved = criticalValueRepository.save(cv);
        return toVO(saved);
    }

    @Override
    public CriticalValueVO getById(String id) {
        CriticalValue cv = criticalValueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("危急值不存在: " + id));
        return toVO(cv);
    }

    @Override
    public CriticalValueVO getByResultId(String resultId) {
        CriticalValue cv = criticalValueRepository.findByResultId(resultId)
                .orElseThrow(() -> new IllegalArgumentException("危急值不存在: " + resultId));
        return toVO(cv);
    }

    @Override
    public PageResult<CriticalValueVO> list(Pageable pageable) {
        Page<CriticalValue> page = criticalValueRepository.findAll(pageable);
        List<CriticalValueVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<CriticalValueVO> listByRequestId(String requestId) {
        return criticalValueRepository.findByRequestId(requestId).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<CriticalValueVO> listByPatientId(String patientId) {
        return criticalValueRepository.findByPatientIdOrderByDetectTimeDesc(patientId).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<CriticalValueVO> listByStatus(CriticalValueStatus status) {
        return criticalValueRepository.findByStatus(status).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<CriticalValueVO> listPendingCriticalValues() {
        return criticalValueRepository.findPendingCriticalValues().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<CriticalValueVO> listNotifiedCriticalValues() {
        return criticalValueRepository.findNotifiedCriticalValues().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<CriticalValueVO> listByDetectTime(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Page<CriticalValue> page = criticalValueRepository.findByDetectTimeBetween(startTime, endTime, pageable);
        List<CriticalValueVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    @Transactional
    public void delete(String id) {
        CriticalValue cv = criticalValueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("危急值不存在: " + id));
        cv.setDeleted(true);
        criticalValueRepository.save(cv);
    }

    @Override
    public long countByStatus(CriticalValueStatus status) {
        return criticalValueRepository.countByStatus(status);
    }

    @Override
    public long countPendingByPatientId(String patientId) {
        return criticalValueRepository.countPendingByPatientId(patientId);
    }

    private CriticalValueVO toVO(CriticalValue cv) {
        CriticalValueVO vo = new CriticalValueVO();
        vo.setId(cv.getId());
        vo.setRequestId(cv.getRequestId());
        vo.setSampleId(cv.getSampleId());
        vo.setResultId(cv.getResultId());
        vo.setPatientId(cv.getPatientId());
        vo.setPatientName(cv.getPatientName());
        vo.setItemId(cv.getItemId());
        vo.setItemName(cv.getItemName());
        vo.setTestValue(cv.getTestValue());
        vo.setCriticalLevel(cv.getCriticalLevel());
        vo.setCriticalRange(cv.getCriticalRange());
        vo.setDetectTime(cv.getDetectTime());
        vo.setDetecterId(cv.getDetecterId());
        vo.setDetecterName(cv.getDetecterName());
        vo.setNotifyTime(cv.getNotifyTime());
        vo.setNotifyMethod(cv.getNotifyMethod());
        vo.setNotifierId(cv.getNotifierId());
        vo.setNotifierName(cv.getNotifierName());
        vo.setReceiveTime(cv.getReceiveTime());
        vo.setReceiverDept(cv.getReceiverDept());
        vo.setReceiverName(cv.getReceiverName());
        vo.setReceiverPhone(cv.getReceiverPhone());
        vo.setHandleTime(cv.getHandleTime());
        vo.setHandlerId(cv.getHandlerId());
        vo.setHandlerName(cv.getHandlerName());
        vo.setHandleResult(cv.getHandleResult());
        vo.setStatus(cv.getStatus().name());
        vo.setStatusDesc(cv.getStatus().getDescription());
        vo.setRemark(cv.getRemark());
        vo.setCreateTime(cv.getCreateTime());
        vo.setUpdateTime(cv.getUpdateTime());
        return vo;
    }
}
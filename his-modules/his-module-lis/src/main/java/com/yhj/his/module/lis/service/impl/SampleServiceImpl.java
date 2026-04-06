package com.yhj.his.module.lis.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.SampleCollectDTO;
import com.yhj.his.module.lis.dto.SampleReceiveDTO;
import com.yhj.his.module.lis.dto.SampleRejectDTO;
import com.yhj.his.module.lis.entity.Sample;
import com.yhj.his.module.lis.entity.TestRequest;
import com.yhj.his.module.lis.enums.SampleStatus;
import com.yhj.his.module.lis.enums.TestRequestStatus;
import com.yhj.his.module.lis.repository.SampleRepository;
import com.yhj.his.module.lis.repository.TestRequestRepository;
import com.yhj.his.module.lis.service.SampleService;
import com.yhj.his.module.lis.vo.SampleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 样本服务实现
 */
@Service
@RequiredArgsConstructor
public class SampleServiceImpl implements SampleService {

    private final SampleRepository sampleRepository;
    private final TestRequestRepository testRequestRepository;

    @Override
    @Transactional
    public SampleVO collect(SampleCollectDTO dto) {
        TestRequest request = testRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException("检验申请不存在: " + dto.getRequestId()));

        // 生成样本编号
        String sampleNo = generateSampleNo();

        Sample sample = new Sample();
        sample.setSampleNo(sampleNo);
        sample.setRequestId(dto.getRequestId());
        sample.setPatientId(request.getPatientId());
        sample.setPatientName(request.getPatientName());
        sample.setSpecimenType(request.getVisitType() != null ?
                com.yhj.his.module.lis.enums.SpecimenType.BLOOD :
                com.yhj.his.module.lis.enums.SpecimenType.BLOOD);
        sample.setCollectorId(dto.getCollectorId());
        sample.setCollectorName(dto.getCollectorName());
        sample.setCollectionTime(dto.getCollectionTime() != null ? dto.getCollectionTime() : LocalDateTime.now());
        sample.setCollectionLocation(dto.getCollectionLocation());
        sample.setSampleStatus(SampleStatus.COLLECTED);
        sample.setRemark(dto.getRemark());

        Sample saved = sampleRepository.save(sample);

        // 更新申请状态
        request.setStatus(TestRequestStatus.SAMPLED);
        request.setSampleStatus("COLLECTED");
        testRequestRepository.save(request);

        return toVO(saved);
    }

    @Override
    @Transactional
    public SampleVO receive(SampleReceiveDTO dto) {
        Sample sample = sampleRepository.findBySampleNo(dto.getSampleNo())
                .orElseThrow(() -> new IllegalArgumentException("样本不存在: " + dto.getSampleNo()));

        if (sample.getSampleStatus() != SampleStatus.COLLECTED) {
            throw new IllegalArgumentException("只有已采集的样本才能核收");
        }

        sample.setReceiverId(dto.getReceiverId());
        sample.setReceiverName(dto.getReceiverName());
        sample.setReceiveTime(dto.getReceiveTime() != null ? dto.getReceiveTime() : LocalDateTime.now());
        sample.setStorageLocation(dto.getStorageLocation());
        sample.setTestGroup(dto.getTestGroup());
        sample.setSampleStatus(SampleStatus.RECEIVED);
        sample.setRemark(dto.getRemark());

        Sample saved = sampleRepository.save(sample);

        // 更新申请状态
        TestRequest request = testRequestRepository.findById(sample.getRequestId()).orElse(null);
        if (request != null) {
            request.setStatus(TestRequestStatus.RECEIVED);
            request.setSampleStatus("RECEIVED");
            testRequestRepository.save(request);
        }

        return toVO(saved);
    }

    @Override
    @Transactional
    public SampleVO reject(SampleRejectDTO dto) {
        Sample sample = sampleRepository.findBySampleNo(dto.getSampleNo())
                .orElseThrow(() -> new IllegalArgumentException("样本不存在: " + dto.getSampleNo()));

        sample.setRejectReason(dto.getRejectReason());
        sample.setRejectUserId(dto.getRejectUserId());
        sample.setRejectTime(dto.getRejectTime() != null ? dto.getRejectTime() : LocalDateTime.now());
        sample.setSampleStatus(SampleStatus.REJECTED);
        sample.setRemark(dto.getRemark());

        Sample saved = sampleRepository.save(sample);

        // 更新申请状态
        TestRequest request = testRequestRepository.findById(sample.getRequestId()).orElse(null);
        if (request != null) {
            request.setSampleStatus("REJECTED");
            testRequestRepository.save(request);
        }

        return toVO(saved);
    }

    @Override
    public SampleVO getById(String id) {
        Sample sample = sampleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("样本不存在: " + id));
        return toVO(sample);
    }

    @Override
    public SampleVO getBySampleNo(String sampleNo) {
        Sample sample = sampleRepository.findBySampleNo(sampleNo)
                .orElseThrow(() -> new IllegalArgumentException("样本不存在: " + sampleNo));
        return toVO(sample);
    }

    @Override
    public PageResult<SampleVO> list(Pageable pageable) {
        Page<Sample> page = sampleRepository.findAll(pageable);
        List<SampleVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<SampleVO> listByRequestId(String requestId) {
        return sampleRepository.findByRequestId(requestId).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<SampleVO> listByPatientId(String patientId) {
        return sampleRepository.findByPatientId(patientId).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<SampleVO> listByStatus(SampleStatus status) {
        return sampleRepository.findBySampleStatus(status).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<SampleVO> listPendingSamples() {
        return sampleRepository.findPendingSamples().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<SampleVO> listCollectedSamples() {
        return sampleRepository.findCollectedSamples().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<SampleVO> listEmergencySamples() {
        return sampleRepository.findEmergencyCollectedSamples().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<SampleVO> listByCollectionTime(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Page<Sample> page = sampleRepository.findByCollectionTimeBetween(startTime, endTime, pageable);
        List<SampleVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    @Transactional
    public SampleVO updateStatus(String id, SampleStatus status) {
        Sample sample = sampleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("样本不存在: " + id));
        sample.setSampleStatus(status);
        Sample saved = sampleRepository.save(sample);
        return toVO(saved);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Sample sample = sampleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("样本不存在: " + id));
        sample.setDeleted(true);
        sampleRepository.save(sample);
    }

    @Override
    public long countByStatus(SampleStatus status) {
        return sampleRepository.countBySampleStatus(status);
    }

    @Override
    public String generateLabel(String sampleId) {
        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new IllegalArgumentException("样本不存在: " + sampleId));
        TestRequest request = testRequestRepository.findById(sample.getRequestId()).orElse(null);

        StringBuilder label = new StringBuilder();
        label.append("样本编号: ").append(sample.getSampleNo()).append("\n");
        label.append("患者: ").append(sample.getPatientName()).append("\n");
        if (request != null) {
            label.append("申请单号: ").append(request.getRequestNo()).append("\n");
        }
        label.append("采集时间: ").append(sample.getCollectionTime()).append("\n");
        label.append("采集人: ").append(sample.getCollectorName());

        return label.toString();
    }

    private String generateSampleNo() {
        return "SMP" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private SampleVO toVO(Sample sample) {
        SampleVO vo = new SampleVO();
        vo.setId(sample.getId());
        vo.setSampleNo(sample.getSampleNo());
        vo.setRequestId(sample.getRequestId());

        TestRequest request = testRequestRepository.findById(sample.getRequestId()).orElse(null);
        if (request != null) {
            vo.setRequestNo(request.getRequestNo());
        }

        vo.setPatientId(sample.getPatientId());
        vo.setPatientName(sample.getPatientName());
        vo.setSpecimenType(sample.getSpecimenType() != null ? sample.getSpecimenType().name() : null);
        vo.setSpecimenTypeDesc(sample.getSpecimenType() != null ? sample.getSpecimenType().getDescription() : null);
        vo.setSpecimenContainer(sample.getSpecimenContainer());
        vo.setCollectionTime(sample.getCollectionTime());
        vo.setCollectorId(sample.getCollectorId());
        vo.setCollectorName(sample.getCollectorName());
        vo.setCollectionLocation(sample.getCollectionLocation());
        vo.setReceiveTime(sample.getReceiveTime());
        vo.setReceiverId(sample.getReceiverId());
        vo.setReceiverName(sample.getReceiverName());
        vo.setSampleStatus(sample.getSampleStatus().name());
        vo.setSampleStatusDesc(sample.getSampleStatus().getDescription());
        vo.setRejectReason(sample.getRejectReason());
        vo.setRejectTime(sample.getRejectTime());
        vo.setRejectUserId(sample.getRejectUserId());
        vo.setStorageLocation(sample.getStorageLocation());
        vo.setTestGroup(sample.getTestGroup());
        vo.setRemark(sample.getRemark());
        vo.setCreateTime(sample.getCreateTime());
        vo.setUpdateTime(sample.getUpdateTime());
        return vo;
    }
}
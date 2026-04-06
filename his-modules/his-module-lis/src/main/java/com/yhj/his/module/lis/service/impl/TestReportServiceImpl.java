package com.yhj.his.module.lis.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestReportAuditDTO;
import com.yhj.his.module.lis.dto.TestReportGenerateDTO;
import com.yhj.his.module.lis.dto.TestReportPublishDTO;
import com.yhj.his.module.lis.entity.TestReport;
import com.yhj.his.module.lis.entity.TestRequest;
import com.yhj.his.module.lis.entity.TestResult;
import com.yhj.his.module.lis.enums.TestReportStatus;
import com.yhj.his.module.lis.enums.TestRequestStatus;
import com.yhj.his.module.lis.repository.TestReportRepository;
import com.yhj.his.module.lis.repository.TestRequestRepository;
import com.yhj.his.module.lis.repository.TestResultRepository;
import com.yhj.his.module.lis.service.TestReportService;
import com.yhj.his.module.lis.vo.TestReportVO;
import com.yhj.his.module.lis.vo.TestResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 检验报告服务实现
 */
@Service
@RequiredArgsConstructor
public class TestReportServiceImpl implements TestReportService {

    private final TestReportRepository testReportRepository;
    private final TestRequestRepository testRequestRepository;
    private final TestResultRepository testResultRepository;

    @Override
    @Transactional
    public TestReportVO generate(TestReportGenerateDTO dto) {
        TestRequest request = testRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException("检验申请不存在: " + dto.getRequestId()));

        // 检查是否已存在报告
        if (testReportRepository.findByRequestId(dto.getRequestId()).isPresent()) {
            throw new IllegalArgumentException("该申请已生成报告");
        }

        // 检查是否所有结果都已审核
        List<TestResult> results = testResultRepository.findByRequestId(dto.getRequestId());
        boolean allAudited = results.stream().allMatch(r -> r.getAuditorId() != null);
        if (!allAudited) {
            throw new IllegalArgumentException("部分结果尚未审核，无法生成报告");
        }

        String reportNo = generateReportNo();

        TestReport report = new TestReport();
        report.setReportNo(reportNo);
        report.setRequestId(dto.getRequestId());
        report.setPatientId(request.getPatientId());
        report.setPatientName(request.getPatientName());
        report.setSampleId(dto.getSampleId());
        report.setReportType(dto.getReportType());
        report.setReportCategory(dto.getReportCategory());
        report.setReportTime(dto.getReportTime() != null ? dto.getReportTime() : LocalDateTime.now());
        report.setTesterId(dto.getTesterId());
        report.setTesterName(dto.getTesterName());

        // 检查是否有危急值
        boolean hasCritical = results.stream().anyMatch(r -> Boolean.TRUE.equals(r.getCriticalFlag()));
        report.setCriticalReport(hasCritical);

        report.setStatus(TestReportStatus.PENDING_AUDIT);
        report.setRemark(dto.getRemark());

        TestReport saved = testReportRepository.save(report);

        // 更新申请状态
        request.setReportStatus("PENDING_AUDIT");
        testRequestRepository.save(request);

        return toVO(saved, results);
    }

    @Override
    @Transactional
    public TestReportVO audit(TestReportAuditDTO dto) {
        TestReport report = testReportRepository.findById(dto.getReportId())
                .orElseThrow(() -> new IllegalArgumentException("检验报告不存在: " + dto.getReportId()));

        if (report.getStatus() != TestReportStatus.PENDING_AUDIT) {
            throw new IllegalArgumentException("只有待审核的报告才能审核");
        }

        report.setAuditorId(dto.getAuditorId());
        report.setAuditorName(dto.getAuditorName());
        report.setAuditTime(dto.getAuditTime() != null ? dto.getAuditTime() : LocalDateTime.now());

        if (Boolean.TRUE.equals(dto.getApproved())) {
            report.setStatus(TestReportStatus.AUDITED);
        } else {
            report.setStatus(TestReportStatus.RETURNED);
            report.setReturnReason(dto.getReturnReason());
        }

        report.setRemark(dto.getRemark());

        TestReport saved = testReportRepository.save(report);

        // 更新申请状态
        TestRequest request = testRequestRepository.findById(report.getRequestId()).orElse(null);
        if (request != null) {
            request.setReportStatus(Boolean.TRUE.equals(dto.getApproved()) ? "AUDITED" : "RETURNED");
            testRequestRepository.save(request);
        }

        List<TestResult> results = testResultRepository.findByRequestId(saved.getRequestId());
        return toVO(saved, results);
    }

    @Override
    @Transactional
    public TestReportVO publish(TestReportPublishDTO dto) {
        TestReport report = testReportRepository.findById(dto.getReportId())
                .orElseThrow(() -> new IllegalArgumentException("检验报告不存在: " + dto.getReportId()));

        if (report.getStatus() != TestReportStatus.AUDITED) {
            throw new IllegalArgumentException("只有已审核的报告才能发布");
        }

        report.setPublisherId(dto.getPublisherId());
        report.setPublisherName(dto.getPublisherName());
        report.setPublishTime(dto.getPublishTime() != null ? dto.getPublishTime() : LocalDateTime.now());
        report.setStatus(TestReportStatus.PUBLISHED);
        report.setRemark(dto.getRemark());

        TestReport saved = testReportRepository.save(report);

        // 更新申请状态
        TestRequest request = testRequestRepository.findById(report.getRequestId()).orElse(null);
        if (request != null) {
            request.setStatus(TestRequestStatus.PUBLISHED);
            request.setReportStatus("PUBLISHED");
            testRequestRepository.save(request);
        }

        List<TestResult> results = testResultRepository.findByRequestId(saved.getRequestId());
        return toVO(saved, results);
    }

    @Override
    @Transactional
    public TestReportVO print(String id) {
        TestReport report = testReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验报告不存在: " + id));

        if (report.getStatus() != TestReportStatus.PUBLISHED) {
            throw new IllegalArgumentException("只有已发布的报告才能打印");
        }

        report.setPrintCount(report.getPrintCount() + 1);
        report.setLastPrintTime(LocalDateTime.now());

        TestReport saved = testReportRepository.save(report);
        List<TestResult> results = testResultRepository.findByRequestId(saved.getRequestId());
        return toVO(saved, results);
    }

    @Override
    public TestReportVO getById(String id) {
        TestReport report = testReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验报告不存在: " + id));
        List<TestResult> results = testResultRepository.findByRequestId(report.getRequestId());
        return toVO(report, results);
    }

    @Override
    public TestReportVO getByReportNo(String reportNo) {
        TestReport report = testReportRepository.findByReportNo(reportNo)
                .orElseThrow(() -> new IllegalArgumentException("检验报告不存在: " + reportNo));
        List<TestResult> results = testResultRepository.findByRequestId(report.getRequestId());
        return toVO(report, results);
    }

    @Override
    public TestReportVO getByRequestId(String requestId) {
        TestReport report = testReportRepository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("检验报告不存在: " + requestId));
        List<TestResult> results = testResultRepository.findByRequestId(report.getRequestId());
        return toVO(report, results);
    }

    @Override
    public PageResult<TestReportVO> list(Pageable pageable) {
        Page<TestReport> page = testReportRepository.findAll(pageable);
        List<TestReportVO> list = page.getContent().stream()
                .map(r -> {
                    List<TestResult> results = testResultRepository.findByRequestId(r.getRequestId());
                    return toVO(r, results);
                })
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<TestReportVO> listByPatientId(String patientId) {
        return testReportRepository.findByPatientIdOrderByReportTimeDesc(patientId).stream()
                .map(r -> {
                    List<TestResult> results = testResultRepository.findByRequestId(r.getRequestId());
                    return toVO(r, results);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TestReportVO> listByStatus(TestReportStatus status) {
        return testReportRepository.findByStatus(status).stream()
                .map(r -> {
                    List<TestResult> results = testResultRepository.findByRequestId(r.getRequestId());
                    return toVO(r, results);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TestReportVO> listPendingAuditReports() {
        return testReportRepository.findPendingAuditReports().stream()
                .map(r -> {
                    List<TestResult> results = testResultRepository.findByRequestId(r.getRequestId());
                    return toVO(r, results);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TestReportVO> listCriticalReports() {
        List<TestReportStatus> excludedStatuses = List.of(TestReportStatus.PUBLISHED, TestReportStatus.RETURNED);
        return testReportRepository.findCriticalReports(excludedStatuses).stream()
                .map(r -> {
                    List<TestResult> results = testResultRepository.findByRequestId(r.getRequestId());
                    return toVO(r, results);
                })
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<TestReportVO> listByReportTime(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Page<TestReport> page = testReportRepository.findByReportTimeBetween(startTime, endTime, pageable);
        List<TestReportVO> list = page.getContent().stream()
                .map(r -> {
                    List<TestResult> results = testResultRepository.findByRequestId(r.getRequestId());
                    return toVO(r, results);
                })
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    @Transactional
    public void delete(String id) {
        TestReport report = testReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验报告不存在: " + id));
        report.setDeleted(true);
        testReportRepository.save(report);
    }

    @Override
    public long countByStatus(TestReportStatus status) {
        return testReportRepository.countByStatus(status);
    }

    private String generateReportNo() {
        return "RPT" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private TestReportVO toVO(TestReport report, List<TestResult> results) {
        TestReportVO vo = new TestReportVO();
        vo.setId(report.getId());
        vo.setReportNo(report.getReportNo());
        vo.setRequestId(report.getRequestId());

        TestRequest request = testRequestRepository.findById(report.getRequestId()).orElse(null);
        if (request != null) {
            vo.setRequestNo(request.getRequestNo());
        }

        vo.setPatientId(report.getPatientId());
        vo.setPatientName(report.getPatientName());
        vo.setSampleId(report.getSampleId());
        vo.setReportType(report.getReportType());
        vo.setReportCategory(report.getReportCategory());
        vo.setReportTime(report.getReportTime());
        vo.setTesterId(report.getTesterId());
        vo.setTesterName(report.getTesterName());
        vo.setAuditTime(report.getAuditTime());
        vo.setAuditorId(report.getAuditorId());
        vo.setAuditorName(report.getAuditorName());
        vo.setPublishTime(report.getPublishTime());
        vo.setPublisherId(report.getPublisherId());
        vo.setPublisherName(report.getPublisherName());
        vo.setCriticalReport(report.getCriticalReport());
        vo.setCriticalNotifyTime(report.getCriticalNotifyTime());
        vo.setCriticalConfirmTime(report.getCriticalConfirmTime());
        vo.setCriticalReceiver(report.getCriticalReceiver());
        vo.setStatus(report.getStatus().name());
        vo.setStatusDesc(report.getStatus().getDescription());
        vo.setPrintCount(report.getPrintCount());
        vo.setLastPrintTime(report.getLastPrintTime());
        vo.setReturnReason(report.getReturnReason());
        vo.setRemark(report.getRemark());

        List<TestResultVO> resultVOs = results.stream().map(this::toResultVO).collect(Collectors.toList());
        vo.setResults(resultVOs);

        vo.setCreateTime(report.getCreateTime());
        vo.setUpdateTime(report.getUpdateTime());
        return vo;
    }

    private TestResultVO toResultVO(TestResult result) {
        TestResultVO vo = new TestResultVO();
        vo.setId(result.getId());
        vo.setRequestId(result.getRequestId());
        vo.setSampleId(result.getSampleId());
        vo.setItemId(result.getItemId());
        vo.setItemCode(result.getItemCode());
        vo.setItemName(result.getItemName());
        vo.setUnit(result.getUnit());
        vo.setTestValue(result.getTestValue());
        vo.setNumericValue(result.getNumericValue());
        vo.setTextResult(result.getTextResult());
        vo.setResultFlag(result.getResultFlag() != null ? result.getResultFlag().name() : null);
        vo.setResultFlagDesc(result.getResultFlag() != null ? result.getResultFlag().getDescription() : null);
        vo.setAbnormalFlag(result.getAbnormalFlag());
        vo.setCriticalFlag(result.getCriticalFlag());
        vo.setReferenceMin(result.getReferenceMin());
        vo.setReferenceMax(result.getReferenceMax());
        vo.setReferenceRange(result.getReferenceRange());
        vo.setTestTime(result.getTestTime());
        vo.setTesterId(result.getTesterId());
        vo.setTesterName(result.getTesterName());
        vo.setAuditTime(result.getAuditTime());
        vo.setAuditorId(result.getAuditorId());
        vo.setAuditorName(result.getAuditorName());
        vo.setRemark(result.getRemark());
        return vo;
    }
}
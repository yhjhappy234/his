package com.yhj.his.module.pacs.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.pacs.dto.*;
import com.yhj.his.module.pacs.entity.*;
import com.yhj.his.module.pacs.repository.*;
import com.yhj.his.module.pacs.service.*;
import com.yhj.his.module.pacs.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 诊断报告服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ExamReportRepository examReportRepository;
    private final ExamRecordRepository examRecordRepository;
    private final ExamRequestRepository examRequestRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    @Transactional
    public ExamReportVO writeReport(ReportWriteDTO dto) {
        ExamRecord record = examRecordRepository.findById(dto.getExamId())
                .orElseThrow(() -> new BusinessException("检查记录不存在"));

        ExamReport report;
        if (dto.getReportId() != null) {
            // 更新现有报告
            report = examReportRepository.findById(dto.getReportId())
                    .orElseThrow(() -> new BusinessException("报告不存在"));

            // 记录修改历史
            String modifyHistory = report.getModifyHistory();
            String newHistory = String.format("{\"time\":\"%s\",\"user\":\"%s\",\"action\":\"修改\"}",
                    LocalDateTime.now().format(DATE_FORMATTER),
                    dto.getWriterName());
            report.setModifyHistory(modifyHistory != null ? modifyHistory + "," + newHistory : "[" + newHistory + "]");
        } else {
            // 创建新报告
            report = new ExamReport();
            report.setReportNo(generateReportNo());
            report.setExamId(dto.getExamId());
            report.setRequestId(dto.getRequestId());
            report.setPatientId(dto.getPatientId() != null ? dto.getPatientId() : record.getPatientId());
            report.setPatientName(dto.getPatientName() != null ? dto.getPatientName() : record.getPatientName());
            report.setReportType(dto.getReportType());
            report.setReportTemplateId(dto.getReportTemplateId());
            report.setReportStatus("草稿");
        }

        report.setExamDescription(dto.getExamDescription());
        report.setDiagnosisResult(dto.getDiagnosisResult());
        report.setDiagnosisCode(dto.getDiagnosisCode());
        report.setDiagnosisName(dto.getDiagnosisName());
        report.setKeyImages(dto.getKeyImages());
        report.setWriterId(dto.getWriterId());
        report.setWriterName(dto.getWriterName());
        report.setWriteTime(LocalDateTime.now());
        report.setRemark(dto.getRemark());

        report = examReportRepository.save(report);
        log.info("保存报告成功: reportNo={}", report.getReportNo());

        return convertToVO(report);
    }

    @Override
    @Transactional
    public ExamReportVO submitForReview(String reportId) {
        ExamReport report = examReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException("报告不存在"));

        if (!"草稿".equals(report.getReportStatus()) && !"审核驳回".equals(report.getReportStatus())) {
            throw new BusinessException("报告状态不正确，无法提交审核");
        }

        report.setReportStatus("待审核");
        report = examReportRepository.save(report);

        // 更新检查记录状态
        ExamRecord record = examRecordRepository.findById(report.getExamId()).orElse(null);
        if (record != null) {
            record.setReportStatus("待审核");
            examRecordRepository.save(record);
        }

        log.info("提交报告审核成功: reportNo={}", report.getReportNo());

        return convertToVO(report);
    }

    @Override
    @Transactional
    public ExamReportVO reviewReport(ReportReviewDTO dto) {
        ExamReport report = examReportRepository.findById(dto.getReportId())
                .orElseThrow(() -> new BusinessException("报告不存在"));

        if (!"待审核".equals(report.getReportStatus())) {
            throw new BusinessException("报告状态不正确，无法审核");
        }

        if (dto.getApproved()) {
            report.setReportStatus("已审核");
            report.setReviewerId(dto.getReviewerId());
            report.setReviewerName(dto.getReviewerName());
            report.setReviewTime(LocalDateTime.now());
            report.setReviewComment(dto.getReviewComment());
        } else {
            report.setReportStatus("审核驳回");
            report.setReviewerId(dto.getReviewerId());
            report.setReviewerName(dto.getReviewerName());
            report.setReviewTime(LocalDateTime.now());
            report.setReviewComment(dto.getReviewComment());
        }

        report = examReportRepository.save(report);

        // 更新检查记录状态
        ExamRecord record = examRecordRepository.findById(report.getExamId()).orElse(null);
        if (record != null) {
            record.setReportStatus(dto.getApproved() ? "已审核" : "审核驳回");
            examRecordRepository.save(record);
        }

        log.info("审核报告完成: reportNo={}, approved={}", report.getReportNo(), dto.getApproved());

        return convertToVO(report);
    }

    @Override
    @Transactional
    public ExamReportVO publishReport(String reportId, String publisherId, String publisherName) {
        ExamReport report = examReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException("报告不存在"));

        if (!"已审核".equals(report.getReportStatus())) {
            throw new BusinessException("报告状态不正确，无法发布");
        }

        report.setReportStatus("已发布");
        report.setPublishTime(LocalDateTime.now());
        report.setPublisherId(publisherId);
        report.setPublisherName(publisherName);

        report = examReportRepository.save(report);

        // 更新检查记录状态
        ExamRecord record = examRecordRepository.findById(report.getExamId()).orElse(null);
        if (record != null) {
            record.setReportStatus("已发布");
            record.setExamStatus("检查完成");
            examRecordRepository.save(record);
        }

        // 更新申请状态
        if (report.getRequestId() != null) {
            ExamRequest request = examRequestRepository.findById(report.getRequestId()).orElse(null);
            if (request != null) {
                request.setStatus("已报告");
                request.setReportTime(LocalDateTime.now());
                examRequestRepository.save(request);
            }
        }

        log.info("发布报告成功: reportNo={}", report.getReportNo());

        return convertToVO(report);
    }

    @Override
    @Transactional
    public ExamReportVO printReport(String reportId) {
        ExamReport report = examReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException("报告不存在"));

        if (!"已发布".equals(report.getReportStatus())) {
            throw new BusinessException("报告未发布，无法打印");
        }

        report.setPrintCount(report.getPrintCount() + 1);
        report.setReportStatus("已打印");

        report = examReportRepository.save(report);
        log.info("打印报告成功: reportNo={}, printCount={}", report.getReportNo(), report.getPrintCount());

        return convertToVO(report);
    }

    @Override
    public ExamReportVO getReportById(String reportId) {
        ExamReport report = examReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException("报告不存在"));
        return convertToVO(report);
    }

    @Override
    public ExamReportVO getReportByExamId(String examId) {
        ExamReport report = examReportRepository.findByExamId(examId).orElse(null);
        return report != null ? convertToVO(report) : null;
    }

    @Override
    public ExamReportVO getReportByRequestId(String requestId) {
        ExamReport report = examReportRepository.findByRequestId(requestId).orElse(null);
        return report != null ? convertToVO(report) : null;
    }

    @Override
    public ExamReportVO getReportByNo(String reportNo) {
        ExamReport report = examReportRepository.findByReportNo(reportNo)
                .orElseThrow(() -> new BusinessException("报告不存在"));
        return convertToVO(report);
    }

    @Override
    public List<ExamReportVO> getReportsByPatientId(String patientId) {
        List<ExamReport> reports = examReportRepository.findByPatientId(patientId);
        return reports.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<ExamReportVO> queryReports(ReportQueryDTO queryDTO) {
        Page<ExamReport> page = examReportRepository.findByConditions(
                queryDTO.getReportNo(),
                queryDTO.getExamId(),
                queryDTO.getPatientId(),
                queryDTO.getPatientName(),
                queryDTO.getReportStatus(),
                queryDTO.getWriterId(),
                queryDTO.getReviewerId(),
                queryDTO.getWriteTimeStart(),
                queryDTO.getWriteTimeEnd(),
                PageUtils.toPageable(queryDTO.getPageNum(), queryDTO.getPageSize())
        );
        List<ExamReportVO> list = page.getContent().stream().map(this::convertToVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public List<ExamReportVO> getPendingReviewReports() {
        List<ExamReport> reports = examReportRepository.findPendingReview();
        return reports.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<ExamReportVO> getMyReports(String writerId) {
        List<ExamReport> reports = examReportRepository.findByWriterId(writerId);
        return reports.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<ExamReportVO> getMyReviewedReports(String reviewerId) {
        List<ExamReport> reports = examReportRepository.findByReviewerId(reviewerId);
        return reports.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public String getModifyHistory(String reportId) {
        ExamReport report = examReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException("报告不存在"));
        return report.getModifyHistory();
    }

    private String generateReportNo() {
        return "RP" + LocalDateTime.now().format(DATE_FORMATTER);
    }

    private ExamReportVO convertToVO(ExamReport report) {
        ExamReportVO vo = new ExamReportVO();
        vo.setId(report.getId());
        vo.setReportNo(report.getReportNo());
        vo.setExamId(report.getExamId());
        vo.setRequestId(report.getRequestId());
        vo.setPatientId(report.getPatientId());
        vo.setPatientName(report.getPatientName());
        vo.setReportType(report.getReportType());
        vo.setReportTemplateId(report.getReportTemplateId());
        vo.setExamDescription(report.getExamDescription());
        vo.setDiagnosisResult(report.getDiagnosisResult());
        vo.setDiagnosisCode(report.getDiagnosisCode());
        vo.setDiagnosisName(report.getDiagnosisName());
        vo.setKeyImages(report.getKeyImages());
        vo.setReportStatus(report.getReportStatus());
        vo.setWriterId(report.getWriterId());
        vo.setWriterName(report.getWriterName());
        vo.setWriteTime(report.getWriteTime());
        vo.setReviewerId(report.getReviewerId());
        vo.setReviewerName(report.getReviewerName());
        vo.setReviewTime(report.getReviewTime());
        vo.setPublishTime(report.getPublishTime());
        vo.setPublisherId(report.getPublisherId());
        vo.setPublisherName(report.getPublisherName());
        vo.setPrintCount(report.getPrintCount());
        vo.setReviewComment(report.getReviewComment());
        vo.setRemark(report.getRemark());
        vo.setCreateTime(report.getCreateTime());
        vo.setUpdateTime(report.getUpdateTime());
        return vo;
    }
}
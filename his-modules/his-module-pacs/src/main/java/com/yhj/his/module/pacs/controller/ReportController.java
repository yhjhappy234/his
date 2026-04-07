package com.yhj.his.module.pacs.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pacs.dto.*;
import com.yhj.his.module.pacs.service.*;
import com.yhj.his.module.pacs.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 诊断报告管理Controller
 */
@Tag(name = "诊断报告管理", description = "报告书写、报告审核、报告发布等接口")
@RestController
@RequestMapping("/api/pacs/v1/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "书写报告", description = "创建或保存诊断报告")
    @PostMapping("/write")
    public Result<ExamReportVO> writeReport(@Valid @RequestBody ReportWriteDTO dto) {
        ExamReportVO vo = reportService.writeReport(dto);
        return Result.success("保存成功", vo);
    }

    @Operation(summary = "提交审核", description = "提交报告等待审核")
    @PostMapping("/submit/{reportId}")
    public Result<ExamReportVO> submitForReview(@PathVariable String reportId) {
        ExamReportVO vo = reportService.submitForReview(reportId);
        return Result.success("提交成功", vo);
    }

    @Operation(summary = "审核报告", description = "审核诊断报告")
    @PostMapping("/review")
    public Result<ExamReportVO> reviewReport(@Valid @RequestBody ReportReviewDTO dto) {
        ExamReportVO vo = reportService.reviewReport(dto);
        return Result.success(dto.getApproved() ? "审核通过" : "审核驳回", vo);
    }

    @Operation(summary = "发布报告", description = "发布诊断报告")
    @PostMapping("/publish/{reportId}")
    public Result<ExamReportVO> publishReport(
            @PathVariable String reportId,
            @Parameter(description = "发布人ID") @RequestParam String publisherId,
            @Parameter(description = "发布人姓名") @RequestParam String publisherName) {
        ExamReportVO vo = reportService.publishReport(reportId, publisherId, publisherName);
        return Result.success("发布成功", vo);
    }

    @Operation(summary = "打印报告", description = "记录报告打印")
    @PostMapping("/print/{reportId}")
    public Result<ExamReportVO> printReport(@PathVariable String reportId) {
        ExamReportVO vo = reportService.printReport(reportId);
        return Result.success("打印成功", vo);
    }

    @Operation(summary = "查询报告详情", description = "根据ID查询报告详情")
    @GetMapping("/{reportId}")
    public Result<ExamReportVO> getReportById(@PathVariable String reportId) {
        ExamReportVO vo = reportService.getReportById(reportId);
        return Result.success(vo);
    }

    @Operation(summary = "根据检查ID查询报告", description = "根据检查ID查询报告")
    @GetMapping("/exam/{examId}")
    public Result<ExamReportVO> getReportByExamId(@PathVariable String examId) {
        ExamReportVO vo = reportService.getReportByExamId(examId);
        return Result.success(vo);
    }

    @Operation(summary = "根据申请ID查询报告", description = "根据申请ID查询报告")
    @GetMapping("/request/{requestId}")
    public Result<ExamReportVO> getReportByRequestId(@PathVariable String requestId) {
        ExamReportVO vo = reportService.getReportByRequestId(requestId);
        return Result.success(vo);
    }

    @Operation(summary = "根据报告编号查询", description = "根据报告编号查询报告")
    @GetMapping("/no/{reportNo}")
    public Result<ExamReportVO> getReportByNo(@PathVariable String reportNo) {
        ExamReportVO vo = reportService.getReportByNo(reportNo);
        return Result.success(vo);
    }

    @Operation(summary = "查询患者报告列表", description = "查询患者的所有报告")
    @GetMapping("/patient/{patientId}")
    public Result<List<ExamReportVO>> getReportsByPatientId(@PathVariable String patientId) {
        List<ExamReportVO> list = reportService.getReportsByPatientId(patientId);
        return Result.success(list);
    }

    @Operation(summary = "分页查询报告", description = "分页查询报告列表")
    @GetMapping("/query")
    public Result<PageResult<ExamReportVO>> queryReports(ReportQueryDTO queryDTO) {
        PageResult<ExamReportVO> result = reportService.queryReports(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "查询待审核报告", description = "查询所有待审核的报告")
    @GetMapping("/pending-review")
    public Result<List<ExamReportVO>> getPendingReviewReports() {
        List<ExamReportVO> list = reportService.getPendingReviewReports();
        return Result.success(list);
    }

    @Operation(summary = "查询我的报告", description = "查询我书写的报告")
    @GetMapping("/my/{writerId}")
    public Result<List<ExamReportVO>> getMyReports(@PathVariable String writerId) {
        List<ExamReportVO> list = reportService.getMyReports(writerId);
        return Result.success(list);
    }

    @Operation(summary = "查询我审核的报告", description = "查询我审核的报告")
    @GetMapping("/my-reviewed/{reviewerId}")
    public Result<List<ExamReportVO>> getMyReviewedReports(@PathVariable String reviewerId) {
        List<ExamReportVO> list = reportService.getMyReviewedReports(reviewerId);
        return Result.success(list);
    }

    @Operation(summary = "获取报告修改历史", description = "获取报告修改历史记录")
    @GetMapping("/history/{reportId}")
    public Result<String> getModifyHistory(@PathVariable String reportId) {
        String history = reportService.getModifyHistory(reportId);
        return Result.success(history);
    }
}
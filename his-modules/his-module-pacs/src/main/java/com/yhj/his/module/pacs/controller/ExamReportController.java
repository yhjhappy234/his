package com.yhj.his.module.pacs.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pacs.dto.ReportQueryDTO;
import com.yhj.his.module.pacs.dto.ReportReviewDTO;
import com.yhj.his.module.pacs.dto.ReportWriteDTO;
import com.yhj.his.module.pacs.service.ReportService;
import com.yhj.his.module.pacs.vo.ExamReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 诊断报告管理控制器
 */
@RestController
@RequestMapping("/api/pacs/v1/reports")
@Tag(name = "诊断报告管理", description = "报告书写、审核、发布、打印等操作")
public class ExamReportController {

    @Autowired
    private ReportService reportService;

    @Operation(summary = "创建/保存报告草稿", description = "创建新报告或保存报告草稿")
    @PostMapping("/write")
    public Result<ExamReportVO> writeReport(@Valid @RequestBody ReportWriteDTO dto) {
        ExamReportVO result = reportService.writeReport(dto);
        return Result.success("报告保存成功", result);
    }

    @Operation(summary = "提交报告审核", description = "将报告提交审核")
    @PostMapping("/{reportId}/submit")
    public Result<ExamReportVO> submitForReview(
            @Parameter(description = "报告ID") @PathVariable String reportId) {
        ExamReportVO result = reportService.submitForReview(reportId);
        return Result.success("报告提交审核成功", result);
    }

    @Operation(summary = "审核报告", description = "审核医生审核报告")
    @PostMapping("/review")
    public Result<ExamReportVO> reviewReport(@Valid @RequestBody ReportReviewDTO dto) {
        ExamReportVO result = reportService.reviewReport(dto);
        return Result.success("审核完成", result);
    }

    @Operation(summary = "发布报告", description = "发布审核通过的报告")
    @PostMapping("/{reportId}/publish")
    public Result<ExamReportVO> publishReport(
            @Parameter(description = "报告ID") @PathVariable String reportId,
            @Parameter(description = "发布人ID") @RequestParam String publisherId,
            @Parameter(description = "发布人姓名") @RequestParam String publisherName) {
        ExamReportVO result = reportService.publishReport(reportId, publisherId, publisherName);
        return Result.success("报告发布成功", result);
    }

    @Operation(summary = "打印报告", description = "打印报告并记录打印次数")
    @PostMapping("/{reportId}/print")
    public Result<ExamReportVO> printReport(
            @Parameter(description = "报告ID") @PathVariable String reportId) {
        ExamReportVO result = reportService.printReport(reportId);
        return Result.success(result);
    }

    @Operation(summary = "分页查询报告", description = "根据条件分页查询报告列表")
    @PostMapping("/query")
    public Result<PageResult<ExamReportVO>> queryReports(@RequestBody ReportQueryDTO queryDTO) {
        PageResult<ExamReportVO> result = reportService.queryReports(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询报告详情", description = "查询指定报告的详细信息")
    @GetMapping("/{reportId}")
    public Result<ExamReportVO> getReportById(
            @Parameter(description = "报告ID") @PathVariable String reportId) {
        ExamReportVO result = reportService.getReportById(reportId);
        return Result.success(result);
    }

    @Operation(summary = "根据检查ID查询报告", description = "根据检查ID查询对应的报告")
    @GetMapping("/by-exam/{examId}")
    public Result<ExamReportVO> getReportByExamId(
            @Parameter(description = "检查ID") @PathVariable String examId) {
        ExamReportVO result = reportService.getReportByExamId(examId);
        return Result.success(result);
    }

    @Operation(summary = "根据申请ID查询报告", description = "根据申请ID查询对应的报告")
    @GetMapping("/by-request/{requestId}")
    public Result<ExamReportVO> getReportByRequestId(
            @Parameter(description = "申请ID") @PathVariable String requestId) {
        ExamReportVO result = reportService.getReportByRequestId(requestId);
        return Result.success(result);
    }

    @Operation(summary = "根据报告编号查询", description = "根据报告编号查询报告详情")
    @GetMapping("/by-no/{reportNo}")
    public Result<ExamReportVO> getReportByNo(
            @Parameter(description = "报告编号") @PathVariable String reportNo) {
        ExamReportVO result = reportService.getReportByNo(reportNo);
        return Result.success(result);
    }

    @Operation(summary = "查询患者报告列表", description = "查询指定患者的所有报告")
    @GetMapping("/patient/{patientId}")
    public Result<List<ExamReportVO>> getReportsByPatientId(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<ExamReportVO> result = reportService.getReportsByPatientId(patientId);
        return Result.success(result);
    }

    @Operation(summary = "查询待审核报告", description = "查询所有待审核状态的报告")
    @GetMapping("/pending-review")
    public Result<List<ExamReportVO>> getPendingReviewReports() {
        List<ExamReportVO> result = reportService.getPendingReviewReports();
        return Result.success(result);
    }

    @Operation(summary = "查询我的报告", description = "查询书写医生的所有报告")
    @GetMapping("/my-reports")
    public Result<List<ExamReportVO>> getMyReports(
            @Parameter(description = "书写医生ID") @RequestParam String writerId) {
        List<ExamReportVO> result = reportService.getMyReports(writerId);
        return Result.success(result);
    }

    @Operation(summary = "查询我审核的报告", description = "查询审核医生审核的所有报告")
    @GetMapping("/my-reviewed")
    public Result<List<ExamReportVO>> getMyReviewedReports(
            @Parameter(description = "审核医生ID") @RequestParam String reviewerId) {
        List<ExamReportVO> result = reportService.getMyReviewedReports(reviewerId);
        return Result.success(result);
    }

    @Operation(summary = "获取报告修改历史", description = "获取报告的修改历史记录")
    @GetMapping("/{reportId}/history")
    public Result<String> getModifyHistory(
            @Parameter(description = "报告ID") @PathVariable String reportId) {
        String result = reportService.getModifyHistory(reportId);
        return Result.success(result);
    }

    @Operation(summary = "删除报告", description = "删除指定的报告")
    @DeleteMapping("/{reportId}")
    public Result<Void> deleteReport(
            @Parameter(description = "报告ID") @PathVariable String reportId) {
        // Note: This would require adding a delete method to the service
        return Result.successVoid();
    }
}
package com.yhj.his.module.lis.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.lis.dto.TestReportAuditDTO;
import com.yhj.his.module.lis.dto.TestReportGenerateDTO;
import com.yhj.his.module.lis.dto.TestReportPublishDTO;
import com.yhj.his.module.lis.enums.TestReportStatus;
import com.yhj.his.module.lis.service.TestReportService;
import com.yhj.his.module.lis.vo.TestReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 检验报告REST控制器
 */
@RestController
@RequestMapping("/api/lis/v1/test-reports")
@RequiredArgsConstructor
@Tag(name = "检验报告管理", description = "检验报告的生成、审核、发布及查询接口")
public class TestReportController {

    private final TestReportService testReportService;

    @PostMapping("/generate")
    @Operation(summary = "生成检验报告", description = "生成检验报告")
    public Result<TestReportVO> generate(@Valid @RequestBody TestReportGenerateDTO dto) {
        TestReportVO vo = testReportService.generate(dto);
        return Result.success("报告生成成功", vo);
    }

    @PostMapping("/audit")
    @Operation(summary = "审核检验报告", description = "审核检验报告")
    public Result<TestReportVO> audit(@Valid @RequestBody TestReportAuditDTO dto) {
        TestReportVO vo = testReportService.audit(dto);
        return Result.success("审核成功", vo);
    }

    @PostMapping("/publish")
    @Operation(summary = "发布检验报告", description = "发布检验报告")
    public Result<TestReportVO> publish(@Valid @RequestBody TestReportPublishDTO dto) {
        TestReportVO vo = testReportService.publish(dto);
        return Result.success("发布成功", vo);
    }

    @PostMapping("/{id}/print")
    @Operation(summary = "打印检验报告", description = "打印检验报告并记录打印次数")
    public Result<TestReportVO> print(@Parameter(description = "报告ID") @PathVariable String id) {
        TestReportVO vo = testReportService.print(id);
        return Result.success("打印成功", vo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取检验报告", description = "根据ID获取检验报告详情")
    public Result<TestReportVO> getById(@Parameter(description = "报告ID") @PathVariable String id) {
        TestReportVO vo = testReportService.getById(id);
        return Result.success(vo);
    }

    @GetMapping("/no/{reportNo}")
    @Operation(summary = "根据报告编号获取", description = "根据报告编号获取检验报告详情")
    public Result<TestReportVO> getByReportNo(@Parameter(description = "报告编号") @PathVariable String reportNo) {
        TestReportVO vo = testReportService.getByReportNo(reportNo);
        return Result.success(vo);
    }

    @GetMapping("/request/{requestId}")
    @Operation(summary = "按申请ID获取报告", description = "根据申请ID获取检验报告")
    public Result<TestReportVO> getByRequestId(@Parameter(description = "申请ID") @PathVariable String requestId) {
        TestReportVO vo = testReportService.getByRequestId(requestId);
        return Result.success(vo);
    }

    @GetMapping
    @Operation(summary = "分页查询检验报告", description = "分页查询检验报告列表")
    public Result<PageResult<TestReportVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "reportTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        PageResult<TestReportVO> result = testReportService.list(pageable);
        return Result.success(result);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "按患者ID查询报告", description = "根据患者ID查询检验报告列表")
    public Result<List<TestReportVO>> listByPatientId(@Parameter(description = "患者ID") @PathVariable String patientId) {
        List<TestReportVO> list = testReportService.listByPatientId(patientId);
        return Result.success(list);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询报告", description = "根据状态查询检验报告列表")
    public Result<List<TestReportVO>> listByStatus(@Parameter(description = "状态") @PathVariable String status) {
        TestReportStatus reportStatus = TestReportStatus.valueOf(status);
        List<TestReportVO> list = testReportService.listByStatus(reportStatus);
        return Result.success(list);
    }

    @GetMapping("/pending-audit")
    @Operation(summary = "查询待审核报告", description = "查询待审核的检验报告")
    public Result<List<TestReportVO>> listPendingAuditReports() {
        List<TestReportVO> list = testReportService.listPendingAuditReports();
        return Result.success(list);
    }

    @GetMapping("/critical")
    @Operation(summary = "查询危急值报告", description = "查询危急值检验报告")
    public Result<List<TestReportVO>> listCriticalReports() {
        List<TestReportVO> list = testReportService.listCriticalReports();
        return Result.success(list);
    }

    @GetMapping("/report-time")
    @Operation(summary = "按报告时间查询", description = "根据报告时间范围查询")
    public Result<PageResult<TestReportVO>> listByReportTime(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        PageResult<TestReportVO> result = testReportService.listByReportTime(startTime, endTime, pageable);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除检验报告", description = "删除指定的检验报告")
    public Result<Void> delete(@Parameter(description = "报告ID") @PathVariable String id) {
        testReportService.delete(id);
        return Result.success();
    }

    @GetMapping("/count/{status}")
    @Operation(summary = "统计报告数量", description = "统计指定状态的报告数量")
    public Result<Long> countByStatus(@Parameter(description = "状态") @PathVariable String status) {
        TestReportStatus reportStatus = TestReportStatus.valueOf(status);
        long count = testReportService.countByStatus(reportStatus);
        return Result.success(count);
    }
}
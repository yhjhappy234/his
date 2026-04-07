package com.yhj.his.module.lis.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.lis.dto.TestResultAuditDTO;
import com.yhj.his.module.lis.dto.TestResultInputDTO;
import com.yhj.his.module.lis.service.TestResultService;
import com.yhj.his.module.lis.vo.TestResultVO;
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
 * 检验结果REST控制器
 */
@RestController
@RequestMapping("/api/lis/v1/test-results")
@RequiredArgsConstructor
@Tag(name = "检验结果管理", description = "检验结果的录入、审核及查询接口")
public class TestResultController {

    private final TestResultService testResultService;

    @PostMapping("/input")
    @Operation(summary = "录入检验结果", description = "录入检验检测结果")
    public Result<TestResultVO> input(@Valid @RequestBody TestResultInputDTO dto) {
        TestResultVO vo = testResultService.input(dto);
        return Result.success("录入成功", vo);
    }

    @PutMapping("/{id}/modify")
    @Operation(summary = "修改检验结果", description = "修改检验检测结果")
    public Result<TestResultVO> modify(
            @Parameter(description = "结果ID") @PathVariable String id,
            @Parameter(description = "检测值") @RequestParam String testValue,
            @Parameter(description = "修改原因") @RequestParam String modifyReason) {
        TestResultVO vo = testResultService.modify(id, testValue, modifyReason);
        return Result.success("修改成功", vo);
    }

    @PostMapping("/audit")
    @Operation(summary = "审核检验结果", description = "审核检验检测结果")
    public Result<TestResultVO> audit(@Valid @RequestBody TestResultAuditDTO dto) {
        TestResultVO vo = testResultService.audit(dto);
        return Result.success("审核成功", vo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取检验结果", description = "根据ID获取检验结果详情")
    public Result<TestResultVO> getById(@Parameter(description = "结果ID") @PathVariable String id) {
        TestResultVO vo = testResultService.getById(id);
        return Result.success(vo);
    }

    @GetMapping("/request/{requestId}")
    @Operation(summary = "按申请ID查询结果", description = "根据申请ID查询所有检验结果")
    public Result<List<TestResultVO>> listByRequestId(@Parameter(description = "申请ID") @PathVariable String requestId) {
        List<TestResultVO> list = testResultService.listByRequestId(requestId);
        return Result.success(list);
    }

    @GetMapping("/sample/{sampleId}")
    @Operation(summary = "按样本ID查询结果", description = "根据样本ID查询所有检验结果")
    public Result<List<TestResultVO>> listBySampleId(@Parameter(description = "样本ID") @PathVariable String sampleId) {
        List<TestResultVO> list = testResultService.listBySampleId(sampleId);
        return Result.success(list);
    }

    @GetMapping("/request/{requestId}/item/{itemId}")
    @Operation(summary = "按申请和项目查询结果", description = "根据申请ID和项目ID查询检验结果")
    public Result<TestResultVO> getByRequestIdAndItemId(
            @Parameter(description = "申请ID") @PathVariable String requestId,
            @Parameter(description = "项目ID") @PathVariable String itemId) {
        TestResultVO vo = testResultService.getByRequestIdAndItemId(requestId, itemId);
        return Result.success(vo);
    }

    @GetMapping
    @Operation(summary = "分页查询检验结果", description = "分页查询检验结果列表")
    public Result<PageResult<TestResultVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "testTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        PageResult<TestResultVO> result = testResultService.list(pageable);
        return Result.success(result);
    }

    @GetMapping("/critical")
    @Operation(summary = "查询危急值结果", description = "查询危急值检验结果")
    public Result<List<TestResultVO>> listCriticalResults() {
        List<TestResultVO> list = testResultService.listCriticalResults();
        return Result.success(list);
    }

    @GetMapping("/abnormal")
    @Operation(summary = "查询异常结果", description = "查询异常检验结果")
    public Result<List<TestResultVO>> listAbnormalResults() {
        List<TestResultVO> list = testResultService.listAbnormalResults();
        return Result.success(list);
    }

    @GetMapping("/pending-audit")
    @Operation(summary = "查询待审核结果", description = "查询待审核的检验结果")
    public Result<List<TestResultVO>> listPendingAuditResults() {
        List<TestResultVO> list = testResultService.listPendingAuditResults();
        return Result.success(list);
    }

    @GetMapping("/test-time")
    @Operation(summary = "按检测时间查询", description = "根据检测时间范围查询结果")
    public Result<PageResult<TestResultVO>> listByTestTime(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        PageResult<TestResultVO> result = testResultService.listByTestTime(startTime, endTime, pageable);
        return Result.success(result);
    }

    @GetMapping("/history")
    @Operation(summary = "查询历史结果", description = "查询患者的历史检验结果")
    public Result<List<TestResultVO>> listHistoryResults(
            @Parameter(description = "患者ID") @RequestParam String patientId,
            @Parameter(description = "项目ID") @RequestParam String itemId) {
        List<TestResultVO> list = testResultService.listHistoryResults(patientId, itemId);
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除检验结果", description = "删除指定的检验结果")
    public Result<Void> delete(@Parameter(description = "结果ID") @PathVariable String id) {
        testResultService.delete(id);
        return Result.success();
    }

    @GetMapping("/count/{requestId}")
    @Operation(summary = "统计结果数量", description = "统计申请的结果数量")
    public Result<Long> countByRequestId(@Parameter(description = "申请ID") @PathVariable String requestId) {
        long count = testResultService.countByRequestId(requestId);
        return Result.success(count);
    }

    @GetMapping("/check-critical")
    @Operation(summary = "检查危急值", description = "检查检测结果是否为危急值")
    public Result<Boolean> checkCriticalValue(
            @Parameter(description = "项目ID") @RequestParam String itemId,
            @Parameter(description = "检测值") @RequestParam String testValue) {
        boolean isCritical = testResultService.checkCriticalValue(itemId, testValue);
        return Result.success(isCritical);
    }
}
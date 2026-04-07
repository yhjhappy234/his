package com.yhj.his.module.emr.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.emr.dto.RectificationDTO;
import com.yhj.his.module.emr.entity.QcResult;
import com.yhj.his.module.emr.enums.QcLevel;
import com.yhj.his.module.emr.service.QcResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 质控结果REST控制器
 */
@Tag(name = "质控结果管理", description = "质控结果的CRUD及质控审核操作")
@RestController
@RequestMapping("/api/emr/v1/qc-results")
@RequiredArgsConstructor
public class QcResultController {

    private final QcResultService qcResultService;

    @Operation(summary = "创建质控结果", description = "创建新的质控结果")
    @PostMapping
    public Result<QcResult> createQcResult(@RequestBody QcResult qcResult) {
        QcResult result = qcResultService.createQcResult(qcResult);
        return Result.success("质控结果创建成功", result);
    }

    @Operation(summary = "更新质控结果", description = "更新指定的质控结果")
    @PutMapping("/{id}")
    public Result<QcResult> updateQcResult(
            @Parameter(description = "质控结果ID") @PathVariable String id,
            @RequestBody QcResult qcResult) {
        QcResult result = qcResultService.updateQcResult(id, qcResult);
        return Result.success("质控结果更新成功", result);
    }

    @Operation(summary = "删除质控结果", description = "删除指定的质控结果")
    @DeleteMapping("/{id}")
    public Result<Void> deleteQcResult(@Parameter(description = "质控结果ID") @PathVariable String id) {
        qcResultService.deleteQcResult(id);
        return Result.successVoid();
    }

    @Operation(summary = "获取质控结果详情", description = "根据ID获取质控结果详情")
    @GetMapping("/{id}")
    public Result<QcResult> getQcResult(@Parameter(description = "质控结果ID") @PathVariable String id) {
        QcResult qcResult = qcResultService.getQcResultById(id);
        return Result.success(qcResult);
    }

    @Operation(summary = "根据病历记录ID查询质控结果", description = "获取病历记录的质控结果")
    @GetMapping("/record/{recordId}")
    public Result<QcResult> getQcResultByRecordId(
            @Parameter(description = "病历记录ID") @PathVariable String recordId) {
        Optional<QcResult> qcResult = qcResultService.getQcResultByRecordId(recordId);
        return qcResult.map(Result::success)
                .orElseGet(() -> Result.error(404, "未找到该病历记录的质控结果"));
    }

    @Operation(summary = "根据病历记录ID和类型查询质控结果", description = "获取指定类型病历记录的质控结果")
    @GetMapping("/record/{recordId}/type/{recordType}")
    public Result<QcResult> getQcResultByRecordIdAndType(
            @Parameter(description = "病历记录ID") @PathVariable String recordId,
            @Parameter(description = "记录类型") @PathVariable String recordType) {
        Optional<QcResult> qcResult = qcResultService.getQcResultByRecordIdAndType(recordId, recordType);
        return qcResult.map(Result::success)
                .orElseGet(() -> Result.error(404, "未找到该病历记录的质控结果"));
    }

    @Operation(summary = "根据患者ID查询质控结果", description = "获取患者的所有质控结果")
    @GetMapping("/patient/{patientId}")
    public Result<List<QcResult>> getQcResultsByPatientId(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<QcResult> qcResults = qcResultService.getQcResultsByPatientId(patientId);
        return Result.success(qcResults);
    }

    @Operation(summary = "分页查询质控结果", description = "分页查询所有质控结果")
    @GetMapping
    public Result<Page<QcResult>> listQcResults(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<QcResult> qcResults = qcResultService.listQcResults(pageable);
        return Result.success(qcResults);
    }

    @Operation(summary = "查询待整改质控结果", description = "分页查询所有待整改的质控结果")
    @GetMapping("/pending-rectification")
    public Result<Page<QcResult>> getPendingRectifications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QcResult> qcResults = qcResultService.getPendingRectifications(pageable);
        return Result.success(qcResults);
    }

    @Operation(summary = "查询患者整改记录", description = "获取患者所有需要整改的质控结果")
    @GetMapping("/patient/{patientId}/rectification")
    public Result<List<QcResult>> getRectificationsByPatientId(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<QcResult> qcResults = qcResultService.getRectificationsByPatientId(patientId);
        return Result.success(qcResults);
    }

    @Operation(summary = "根据质控等级统计", description = "统计指定质控等级的数量")
    @GetMapping("/count/level/{level}")
    public Result<Long> countByQcLevel(
            @Parameter(description = "质控等级") @PathVariable QcLevel level) {
        Long count = qcResultService.countByQcLevel(level);
        return Result.success(count);
    }

    @Operation(summary = "根据质控人查询", description = "分页查询质控人的质控结果")
    @GetMapping("/qc-user/{qcUserId}")
    public Result<Page<QcResult>> getQcResultsByQcUserId(
            @Parameter(description = "质控人ID") @PathVariable String qcUserId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QcResult> qcResults = qcResultService.getQcResultsByQcUserId(qcUserId, pageable);
        return Result.success(qcResults);
    }

    @Operation(summary = "根据整改状态查询", description = "分页查询指定整改状态的质控结果")
    @GetMapping("/rectification-status/{status}")
    public Result<Page<QcResult>> getQcResultsByRectificationStatus(
            @Parameter(description = "整改状态") @PathVariable String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QcResult> qcResults = qcResultService.getQcResultsByRectificationStatus(status, pageable);
        return Result.success(qcResults);
    }

    @Operation(summary = "执行质控检查", description = "对病历记录执行质控检查")
    @PostMapping("/check")
    public Result<QcResult> performQcCheck(
            @Parameter(description = "病历记录ID") @RequestParam String recordId,
            @Parameter(description = "记录类型") @RequestParam String recordType,
            @Parameter(description = "质控人ID") @RequestParam String qcUserId,
            @Parameter(description = "质控人姓名") @RequestParam String qcUserName) {
        QcResult qcResult = qcResultService.performQcCheck(recordId, recordType, qcUserId, qcUserName);
        return Result.success("质控检查完成", qcResult);
    }

    @Operation(summary = "发送整改通知", description = "发送整改通知给相关医生")
    @PostMapping("/{id}/notify-rectification")
    public Result<QcResult> sendRectificationNotice(
            @Parameter(description = "质控结果ID") @PathVariable String id) {
        QcResult qcResult = qcResultService.sendRectificationNotice(id);
        return Result.success("整改通知已发送", qcResult);
    }

    @Operation(summary = "完成整改", description = "标记整改已完成")
    @PostMapping("/complete-rectification")
    public Result<QcResult> completeRectification(@Valid @RequestBody RectificationDTO dto) {
        QcResult qcResult = qcResultService.completeRectification(dto);
        return Result.success("整改已完成", qcResult);
    }

    @Operation(summary = "标记整改超期", description = "标记整改已超期")
    @PostMapping("/{id}/mark-overdue")
    public Result<QcResult> markRectificationOverdue(
            @Parameter(description = "质控结果ID") @PathVariable String id) {
        QcResult qcResult = qcResultService.markRectificationOverdue(id);
        return Result.success("已标记整改超期", qcResult);
    }

    @Operation(summary = "根据质控等级查询", description = "分页查询指定质控等级的质控结果")
    @GetMapping("/level/{qcLevel}")
    public Result<Page<QcResult>> getQcResultsByLevel(
            @Parameter(description = "质控等级") @PathVariable QcLevel qcLevel,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QcResult> qcResults = qcResultService.getQcResultsByLevel(qcLevel, pageable);
        return Result.success(qcResults);
    }
}
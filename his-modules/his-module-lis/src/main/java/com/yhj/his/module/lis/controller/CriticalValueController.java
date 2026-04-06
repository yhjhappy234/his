package com.yhj.his.module.lis.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.lis.dto.CriticalValueConfirmDTO;
import com.yhj.his.module.lis.dto.CriticalValueHandleDTO;
import com.yhj.his.module.lis.dto.CriticalValueNotifyDTO;
import com.yhj.his.module.lis.enums.CriticalValueStatus;
import com.yhj.his.module.lis.service.CriticalValueService;
import com.yhj.his.module.lis.vo.CriticalValueVO;
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
 * 危急值REST控制器
 */
@RestController
@RequestMapping("/api/lis/v1/critical-values")
@RequiredArgsConstructor
@Tag(name = "危急值管理", description = "危急值的通知、确认、处理及查询接口")
public class CriticalValueController {

    private final CriticalValueService criticalValueService;

    @PostMapping("/notify")
    @Operation(summary = "通知危急值", description = "通知临床危急值")
    public Result<CriticalValueVO> notify(@Valid @RequestBody CriticalValueNotifyDTO dto) {
        CriticalValueVO vo = criticalValueService.notify(dto);
        return Result.success("通知成功", vo);
    }

    @PostMapping("/confirm")
    @Operation(summary = "确认危急值", description = "临床确认接收危急值")
    public Result<CriticalValueVO> confirm(@Valid @RequestBody CriticalValueConfirmDTO dto) {
        CriticalValueVO vo = criticalValueService.confirm(dto);
        return Result.success("确认成功", vo);
    }

    @PostMapping("/handle")
    @Operation(summary = "处理危急值", description = "处理危急值并记录处理结果")
    public Result<CriticalValueVO> handle(@Valid @RequestBody CriticalValueHandleDTO dto) {
        CriticalValueVO vo = criticalValueService.handle(dto);
        return Result.success("处理成功", vo);
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "关闭危急值", description = "关闭危急值记录")
    public Result<CriticalValueVO> close(@Parameter(description = "危急值ID") @PathVariable String id) {
        CriticalValueVO vo = criticalValueService.close(id);
        return Result.success("关闭成功", vo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取危急值", description = "根据ID获取危急值详情")
    public Result<CriticalValueVO> getById(@Parameter(description = "危急值ID") @PathVariable String id) {
        CriticalValueVO vo = criticalValueService.getById(id);
        return Result.success(vo);
    }

    @GetMapping("/result/{resultId}")
    @Operation(summary = "按结果ID获取危急值", description = "根据检验结果ID获取危急值")
    public Result<CriticalValueVO> getByResultId(@Parameter(description = "结果ID") @PathVariable String resultId) {
        CriticalValueVO vo = criticalValueService.getByResultId(resultId);
        return Result.success(vo);
    }

    @GetMapping
    @Operation(summary = "分页查询危急值", description = "分页查询危急值列表")
    public Result<PageResult<CriticalValueVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "detectTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        PageResult<CriticalValueVO> result = criticalValueService.list(pageable);
        return Result.success(result);
    }

    @GetMapping("/request/{requestId}")
    @Operation(summary = "按申请ID查询危急值", description = "根据申请ID查询危急值列表")
    public Result<List<CriticalValueVO>> listByRequestId(@Parameter(description = "申请ID") @PathVariable String requestId) {
        List<CriticalValueVO> list = criticalValueService.listByRequestId(requestId);
        return Result.success(list);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "按患者ID查询危急值", description = "根据患者ID查询危急值列表")
    public Result<List<CriticalValueVO>> listByPatientId(@Parameter(description = "患者ID") @PathVariable String patientId) {
        List<CriticalValueVO> list = criticalValueService.listByPatientId(patientId);
        return Result.success(list);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询危急值", description = "根据状态查询危急值列表")
    public Result<List<CriticalValueVO>> listByStatus(@Parameter(description = "状态") @PathVariable String status) {
        CriticalValueStatus cvStatus = CriticalValueStatus.valueOf(status);
        List<CriticalValueVO> list = criticalValueService.listByStatus(cvStatus);
        return Result.success(list);
    }

    @GetMapping("/pending")
    @Operation(summary = "查询待处理危急值", description = "查询待处理的危急值列表")
    public Result<List<CriticalValueVO>> listPendingCriticalValues() {
        List<CriticalValueVO> list = criticalValueService.listPendingCriticalValues();
        return Result.success(list);
    }

    @GetMapping("/notified")
    @Operation(summary = "查询未确认危急值", description = "查询已通知未确认的危急值")
    public Result<List<CriticalValueVO>> listNotifiedCriticalValues() {
        List<CriticalValueVO> list = criticalValueService.listNotifiedCriticalValues();
        return Result.success(list);
    }

    @GetMapping("/detect-time")
    @Operation(summary = "按发现时间查询", description = "根据发现时间范围查询危急值")
    public Result<PageResult<CriticalValueVO>> listByDetectTime(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        PageResult<CriticalValueVO> result = criticalValueService.listByDetectTime(startTime, endTime, pageable);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除危急值", description = "删除指定的危急值记录")
    public Result<Void> delete(@Parameter(description = "危急值ID") @PathVariable String id) {
        criticalValueService.delete(id);
        return Result.success();
    }

    @GetMapping("/count/{status}")
    @Operation(summary = "统计危急值数量", description = "统计指定状态的危急值数量")
    public Result<Long> countByStatus(@Parameter(description = "状态") @PathVariable String status) {
        CriticalValueStatus cvStatus = CriticalValueStatus.valueOf(status);
        long count = criticalValueService.countByStatus(cvStatus);
        return Result.success(count);
    }

    @GetMapping("/pending-count/{patientId}")
    @Operation(summary = "统计患者未处理危急值", description = "统计患者未处理的危急值数量")
    public Result<Long> countPendingByPatientId(@Parameter(description = "患者ID") @PathVariable String patientId) {
        long count = criticalValueService.countPendingByPatientId(patientId);
        return Result.success(count);
    }
}
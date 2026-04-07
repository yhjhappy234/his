package com.yhj.his.module.lis.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.lis.dto.TestRequestCreateDTO;
import com.yhj.his.module.lis.enums.TestRequestStatus;
import com.yhj.his.module.lis.service.TestRequestService;
import com.yhj.his.module.lis.vo.TestRequestVO;
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
 * 检验申请REST控制器
 */
@RestController
@RequestMapping("/api/lis/v1/test-requests")
@RequiredArgsConstructor
@Tag(name = "检验申请管理", description = "检验申请的增删改查及业务操作接口")
public class TestRequestController {

    private final TestRequestService testRequestService;

    @PostMapping
    @Operation(summary = "创建检验申请", description = "创建新的检验申请单")
    public Result<TestRequestVO> create(@Valid @RequestBody TestRequestCreateDTO dto) {
        TestRequestVO vo = testRequestService.create(dto);
        return Result.success("申请创建成功", vo);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消检验申请", description = "取消指定的检验申请")
    public Result<TestRequestVO> cancel(
            @Parameter(description = "申请ID") @PathVariable String id,
            @Parameter(description = "取消原因") @RequestParam String cancelReason,
            @Parameter(description = "取消人ID") @RequestParam String cancelUserId) {
        TestRequestVO vo = testRequestService.cancel(id, cancelReason, cancelUserId);
        return Result.success("取消成功", vo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取检验申请", description = "根据ID获取检验申请详情")
    public Result<TestRequestVO> getById(@Parameter(description = "申请ID") @PathVariable String id) {
        TestRequestVO vo = testRequestService.getById(id);
        return Result.success(vo);
    }

    @GetMapping("/no/{requestNo}")
    @Operation(summary = "根据申请单号获取", description = "根据申请单号获取检验申请详情")
    public Result<TestRequestVO> getByRequestNo(@Parameter(description = "申请单号") @PathVariable String requestNo) {
        TestRequestVO vo = testRequestService.getByRequestNo(requestNo);
        return Result.success(vo);
    }

    @GetMapping
    @Operation(summary = "分页查询检验申请", description = "分页查询检验申请列表")
    public Result<PageResult<TestRequestVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "requestTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        PageResult<TestRequestVO> result = testRequestService.list(pageable);
        return Result.success(result);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "按患者查询检验申请", description = "根据患者ID查询检验申请列表")
    public Result<List<TestRequestVO>> listByPatientId(@Parameter(description = "患者ID") @PathVariable String patientId) {
        List<TestRequestVO> list = testRequestService.listByPatientId(patientId);
        return Result.success(list);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询检验申请", description = "根据状态查询检验申请列表")
    public Result<List<TestRequestVO>> listByStatus(@Parameter(description = "状态") @PathVariable String status) {
        TestRequestStatus requestStatus = TestRequestStatus.valueOf(status);
        List<TestRequestVO> list = testRequestService.listByStatus(requestStatus);
        return Result.success(list);
    }

    @GetMapping("/visit/{visitId}")
    @Operation(summary = "按就诊ID查询", description = "根据就诊ID查询检验申请列表")
    public Result<List<TestRequestVO>> listByVisitId(@Parameter(description = "就诊ID") @PathVariable String visitId) {
        List<TestRequestVO> list = testRequestService.listByVisitId(visitId);
        return Result.success(list);
    }

    @GetMapping("/emergency")
    @Operation(summary = "查询急诊申请", description = "查询急诊检验申请列表")
    public Result<List<TestRequestVO>> listEmergencyRequests() {
        List<TestRequestVO> list = testRequestService.listEmergencyRequests();
        return Result.success(list);
    }

    @GetMapping("/dept/{deptId}")
    @Operation(summary = "按科室和时间查询", description = "根据科室和时间范围查询检验申请")
    public Result<PageResult<TestRequestVO>> listByDeptAndTime(
            @Parameter(description = "科室ID") @PathVariable String deptId,
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        PageResult<TestRequestVO> result = testRequestService.listByDeptAndTime(deptId, startTime, endTime, pageable);
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新申请状态", description = "更新检验申请的状态")
    public Result<TestRequestVO> updateStatus(
            @Parameter(description = "申请ID") @PathVariable String id,
            @Parameter(description = "状态") @RequestParam String status) {
        TestRequestStatus requestStatus = TestRequestStatus.valueOf(status);
        TestRequestVO vo = testRequestService.updateStatus(id, requestStatus);
        return Result.success("状态更新成功", vo);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除检验申请", description = "删除指定的检验申请")
    public Result<Void> delete(@Parameter(description = "申请ID") @PathVariable String id) {
        testRequestService.delete(id);
        return Result.success();
    }

    @GetMapping("/count/{status}")
    @Operation(summary = "统计申请数量", description = "统计指定状态的申请数量")
    public Result<Long> countByStatus(@Parameter(description = "状态") @PathVariable String status) {
        TestRequestStatus requestStatus = TestRequestStatus.valueOf(status);
        long count = testRequestService.countByStatus(requestStatus);
        return Result.success(count);
    }
}
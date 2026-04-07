package com.yhj.his.module.inpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.service.MedicalOrderService;
import com.yhj.his.module.inpatient.vo.MedicalOrderVO;
import com.yhj.his.module.inpatient.vo.OrderExecutionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医嘱管理控制器
 */
@Tag(name = "医嘱管理", description = "医嘱开立、审核、执行、停止等接口")
@RestController
@RequestMapping("/api/inpatient/v1/order")
@RequiredArgsConstructor
public class MedicalOrderController {

    private final MedicalOrderService orderService;

    @Operation(summary = "开立医嘱", description = "医生开立医嘱")
    @PostMapping("/create")
    public Result<String> create(@Valid @RequestBody OrderCreateDTO dto) {
        String orderId = orderService.create(dto);
        return Result.success("医嘱开立成功", orderId);
    }

    @Operation(summary = "审核医嘱", description = "护士审核医嘱")
    @PostMapping("/audit")
    public Result<Boolean> audit(@Valid @RequestBody OrderAuditDTO dto) {
        boolean result = orderService.audit(dto);
        return Result.success("审核成功", result);
    }

    @Operation(summary = "执行医嘱", description = "护士执行医嘱并记录执行结果")
    @PostMapping("/execute")
    public Result<String> execute(@Valid @RequestBody OrderExecuteDTO dto) {
        String executionId = orderService.execute(dto);
        return Result.success("执行记录成功", executionId);
    }

    @Operation(summary = "停止医嘱", description = "停止长期医嘱")
    @PostMapping("/stop")
    public Result<Boolean> stop(@Valid @RequestBody OrderStopDTO dto) {
        boolean result = orderService.stop(dto);
        return Result.success("医嘱已停止", result);
    }

    @Operation(summary = "作废医嘱", description = "作废未执行的医嘱")
    @PostMapping("/cancel/{orderId}")
    public Result<Boolean> cancel(
            @Parameter(description = "医嘱ID") @PathVariable String orderId,
            @Parameter(description = "作废原因") @RequestParam String reason) {
        boolean result = orderService.cancel(orderId, reason);
        return Result.success("医嘱已作废", result);
    }

    @Operation(summary = "查询医嘱详情", description = "根据医嘱ID查询医嘱详情")
    @GetMapping("/{orderId}")
    public Result<MedicalOrderVO> getById(
            @Parameter(description = "医嘱ID") @PathVariable String orderId) {
        MedicalOrderVO vo = orderService.getById(orderId);
        return Result.success(vo);
    }

    @Operation(summary = "查询住院医嘱列表", description = "查询指定住院患者的所有医嘱")
    @GetMapping("/list/{admissionId}")
    public Result<List<MedicalOrderVO>> listByAdmission(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<MedicalOrderVO> list = orderService.listByAdmission(admissionId);
        return Result.success(list);
    }

    @Operation(summary = "查询正在执行的医嘱", description = "查询正在执行的长期医嘱")
    @GetMapping("/active/{admissionId}")
    public Result<List<MedicalOrderVO>> listActiveOrders(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<MedicalOrderVO> list = orderService.listActiveOrders(admissionId);
        return Result.success(list);
    }

    @Operation(summary = "分页查询医嘱", description = "分页查询医嘱列表")
    @GetMapping("/page")
    public Result<PageResult<MedicalOrderVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "住院ID") @RequestParam(required = false) String admissionId) {
        PageResult<MedicalOrderVO> result = orderService.page(pageNum, pageSize, admissionId);
        return Result.success(result);
    }

    @Operation(summary = "查询医嘱执行记录", description = "查询医嘱的执行记录列表")
    @GetMapping("/execution/{orderId}")
    public Result<List<OrderExecutionVO>> listExecutions(
            @Parameter(description = "医嘱ID") @PathVariable String orderId) {
        List<OrderExecutionVO> list = orderService.listExecutions(orderId);
        return Result.success(list);
    }
}
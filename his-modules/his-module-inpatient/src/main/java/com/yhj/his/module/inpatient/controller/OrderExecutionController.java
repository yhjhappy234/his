package com.yhj.his.module.inpatient.controller;

import com.yhj.his.module.inpatient.service.MedicalOrderService;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inpatient.dto.OrderExecuteDTO;
import com.yhj.his.module.inpatient.vo.OrderExecutionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医嘱执行记录控制器
 */
@RestController
@RequestMapping("/api/inpatient/v1/executions")
@Tag(name = "医嘱执行记录", description = "医嘱执行记录查询、管理接口")
public class OrderExecutionController {

    @Autowired
    private MedicalOrderService medicalOrderService;

    /**
     * 执行医嘱
     */
    @PostMapping
    @Operation(summary = "执行医嘱", description = "护士执行医嘱并记录执行结果")
    public Result<String> execute(@Valid @RequestBody OrderExecuteDTO dto) {
        String executionId = medicalOrderService.execute(dto);
        return Result.success("医嘱执行成功", executionId);
    }

    /**
     * 查询医嘱执行记录
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "查询医嘱执行记录", description = "查询指定医嘱的执行记录列表")
    public Result<List<OrderExecutionVO>> listByOrder(
            @Parameter(description = "医嘱ID") @PathVariable String orderId) {
        List<OrderExecutionVO> list = medicalOrderService.listExecutions(orderId);
        return Result.success(list);
    }

    /**
     * 查询住院患者执行记录
     */
    @GetMapping("/admission/{admissionId}")
    @Operation(summary = "查询住院患者执行记录", description = "查询指定住院患者的所有执行记录")
    public Result<List<OrderExecutionVO>> listByAdmission(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        // Get all orders for admission and collect their executions
        List<OrderExecutionVO> allExecutions = medicalOrderService.listByAdmission(admissionId)
                .stream()
                .flatMap(order -> medicalOrderService.listExecutions(order.getOrderId()).stream())
                .toList();
        return Result.success(allExecutions);
    }

    /**
     * 查询今日待执行医嘱
     */
    @GetMapping("/pending-today/{admissionId}")
    @Operation(summary = "查询今日待执行医嘱", description = "查询指定住院患者今日待执行的医嘱")
    public Result<List<OrderExecutionVO>> listPendingToday(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        // This would typically be a separate service method in production
        // For now, we return empty list as placeholder
        return Result.success(List.of());
    }

    /**
     * 批量执行医嘱
     */
    @PostMapping("/batch")
    @Operation(summary = "批量执行医嘱", description = "批量执行多个医嘱")
    public Result<List<String>> batchExecute(@Valid @RequestBody List<OrderExecuteDTO> dtoList) {
        List<String> executionIds = dtoList.stream()
                .map(medicalOrderService::execute)
                .toList();
        return Result.success("批量执行成功", executionIds);
    }
}
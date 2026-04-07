package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.dto.RequisitionDTO;
import com.yhj.his.module.inventory.service.RequisitionService;
import com.yhj.his.module.inventory.vo.MaterialRequisitionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物资申领控制器
 */
@Tag(name = "物资申领", description = "领料申请、审批、发放接口")
@RestController
@RequestMapping("/api/inventory/v1/requisition")
@RequiredArgsConstructor
public class RequisitionController {

    private final RequisitionService service;

    @Operation(summary = "领料申请")
    @PostMapping("/apply")
    public Result<MaterialRequisitionVO> apply(@Valid @RequestBody RequisitionDTO dto) {
        return Result.success(service.apply(dto));
    }

    @Operation(summary = "领料审批")
    @PostMapping("/approve")
    public Result<MaterialRequisitionVO> approve(@Valid @RequestBody AuditDTO dto) {
        return Result.success(service.approve(dto));
    }

    @Operation(summary = "领料发放")
    @PostMapping("/issue")
    public Result<MaterialRequisitionVO> issue(@Valid @RequestBody ConfirmDTO dto) {
        return Result.success(service.issue(dto));
    }

    @Operation(summary = "领料接收确认")
    @PostMapping("/receive")
    public Result<MaterialRequisitionVO> receive(@Valid @RequestBody ConfirmDTO dto) {
        return Result.success(service.receive(dto));
    }

    @Operation(summary = "取消申请")
    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@Parameter(description = "申领ID") @PathVariable String id) {
        service.cancel(id);
        return Result.success();
    }

    @Operation(summary = "根据ID查询申领记录")
    @GetMapping("/{id}")
    public Result<MaterialRequisitionVO> getById(@Parameter(description = "申领ID") @PathVariable String id) {
        return Result.success(service.getById(id));
    }

    @Operation(summary = "根据申领单号查询")
    @GetMapping("/no/{requisitionNo}")
    public Result<MaterialRequisitionVO> getByNo(@Parameter(description = "申领单号") @PathVariable String requisitionNo) {
        return Result.success(service.getByNo(requisitionNo));
    }

    @Operation(summary = "分页查询申领记录")
    @GetMapping("/list")
    public Result<PageResult<MaterialRequisitionVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(service.list(pageNum, pageSize));
    }

    @Operation(summary = "条件查询申领记录")
    @PostMapping("/query")
    public Result<PageResult<MaterialRequisitionVO>> query(@RequestBody QueryDTO query) {
        return Result.success(service.query(query));
    }

    @Operation(summary = "查询待审批申领单")
    @GetMapping("/pending")
    public Result<List<MaterialRequisitionVO>> listPending() {
        return Result.success(service.listPending());
    }

    @Operation(summary = "查询某科室的申领记录")
    @GetMapping("/dept/{deptId}")
    public Result<List<MaterialRequisitionVO>> listByDept(
            @Parameter(description = "科室ID") @PathVariable String deptId) {
        return Result.success(service.listByDept(deptId));
    }
}
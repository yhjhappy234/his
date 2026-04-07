package com.yhj.his.module.outpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.outpatient.entity.BillingItem;
import com.yhj.his.module.outpatient.service.BillingItemService;
import com.yhj.his.module.outpatient.vo.PendingBillingVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收费项目管理控制器
 */
@RestController
@RequestMapping("/api/outpatient/v1/billing-items")
@RequiredArgsConstructor
@Tag(name = "收费项目管理", description = "收费项目的增删改查接口")
public class BillingItemController {

    private final BillingItemService billingItemService;

    @PostMapping
    @Operation(summary = "创建收费项目", description = "创建收费项目")
    public Result<BillingItem> createBillingItem(@RequestBody BillingItem item) {
        BillingItem saved = billingItemService.createBillingItem(item);
        return Result.success("创建收费项目成功", saved);
    }

    @PostMapping("/batch")
    @Operation(summary = "批量创建收费项目", description = "批量创建收费项目")
    public Result<List<BillingItem>> createBillingItems(@RequestBody List<BillingItem> items) {
        List<BillingItem> saved = billingItemService.createBillingItems(items);
        return Result.success("批量创建收费项目成功", saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取收费项目详情", description = "根据ID获取收费项目详细信息")
    public Result<BillingItem> getBillingItem(@Parameter(description = "收费项目ID") @PathVariable String id) {
        BillingItem item = billingItemService.getBillingItemDetail(id);
        return Result.success(item);
    }

    @GetMapping
    @Operation(summary = "查询收费项目列表", description = "分页查询收费项目列表")
    public Result<PageResult<BillingItem>> listBillingItems(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "挂号ID") @RequestParam(required = false) String registrationId,
            @Parameter(description = "收费状态") @RequestParam(required = false) String payStatus,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createTime").descending());
        PageResult<BillingItem> result = billingItemService.listBillingItems(patientId, registrationId, payStatus, pageable);
        return Result.success(result);
    }

    @GetMapping("/registration/{registrationId}")
    @Operation(summary = "查询挂号关联收费项目", description = "查询挂号关联的收费项目列表")
    public Result<List<BillingItem>> listBillingItemsByRegistration(
            @Parameter(description = "挂号ID") @PathVariable String registrationId) {
        List<BillingItem> items = billingItemService.listBillingItemsByRegistration(registrationId);
        return Result.success(items);
    }

    @GetMapping("/unpaid/{registrationId}")
    @Operation(summary = "查询待收费项目", description = "查询挂号关联的待收费项目")
    public Result<List<BillingItem>> listUnpaidItems(
            @Parameter(description = "挂号ID") @PathVariable String registrationId) {
        List<BillingItem> items = billingItemService.listUnpaidItems(registrationId);
        return Result.success(items);
    }

    @GetMapping("/pending/{registrationId}")
    @Operation(summary = "获取待收费汇总", description = "获取待收费项目汇总")
    public Result<PendingBillingVO> getPendingBilling(
            @Parameter(description = "挂号ID") @PathVariable String registrationId) {
        PendingBillingVO vo = billingItemService.getPendingBilling(registrationId);
        return Result.success(vo);
    }

    @GetMapping("/amount/{registrationId}")
    @Operation(summary = "计算待收费总金额", description = "计算待收费总金额")
    public Result<BigDecimal> calculateTotalAmount(
            @Parameter(description = "挂号ID") @PathVariable String registrationId) {
        BigDecimal amount = billingItemService.calculateTotalAmount(registrationId);
        return Result.success(amount);
    }

    @PutMapping("/{id}/pay-status")
    @Operation(summary = "更新收费状态", description = "更新收费项目收费状态")
    public Result<BillingItem> updatePayStatus(
            @Parameter(description = "收费项目ID") @PathVariable String id,
            @Parameter(description = "收费状态") @RequestParam String payStatus) {
        BillingItem item = billingItemService.updatePayStatus(id, payStatus);
        return Result.success("更新收费状态成功", item);
    }

    @PutMapping("/batch/pay-status")
    @Operation(summary = "批量更新收费状态", description = "批量更新收费项目收费状态")
    public Result<Void> batchUpdatePayStatus(
            @RequestBody List<String> ids,
            @Parameter(description = "收费状态") @RequestParam String payStatus) {
        billingItemService.batchUpdatePayStatus(ids, payStatus);
        return Result.successVoid();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新收费项目", description = "更新收费项目信息")
    public Result<BillingItem> updateBillingItem(
            @Parameter(description = "收费项目ID") @PathVariable String id,
            @RequestBody BillingItem item) {
        BillingItem saved = billingItemService.updateBillingItem(id, item);
        return Result.success("更新收费项目成功", saved);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除收费项目", description = "删除收费项目")
    public Result<Void> deleteBillingItem(@Parameter(description = "收费项目ID") @PathVariable String id) {
        billingItemService.deleteBillingItem(id);
        return Result.successVoid();
    }
}
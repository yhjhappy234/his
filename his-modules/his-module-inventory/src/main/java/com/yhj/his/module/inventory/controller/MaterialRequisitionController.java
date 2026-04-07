package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.RequisitionDTO;
import com.yhj.his.module.inventory.dto.RequisitionItemDTO;
import com.yhj.his.module.inventory.entity.MaterialRequisition;
import com.yhj.his.module.inventory.entity.MaterialRequisitionItem;
import com.yhj.his.module.inventory.enums.RequisitionStatus;
import com.yhj.his.module.inventory.service.MaterialRequisitionService;
import com.yhj.his.module.inventory.vo.MaterialRequisitionItemVO;
import com.yhj.his.module.inventory.vo.MaterialRequisitionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物资申领Controller
 */
@RestController
@RequestMapping("/api/inventory/v1/requisitions")
@Tag(name = "物资申领管理", description = "物资申领申请、审批、发放接口")
public class MaterialRequisitionController {

    @Autowired
    private MaterialRequisitionService materialRequisitionService;

    @PostMapping
    @Operation(summary = "领料申请", description = "创建物资申领申请")
    public Result<MaterialRequisitionVO> apply(@Valid @RequestBody RequisitionDTO dto) {
        MaterialRequisition entity = new MaterialRequisition();
        BeanUtils.copyProperties(dto, entity);
        entity.setRequisitionNo(materialRequisitionService.generateRequisitionNo());
        entity.setStatus(RequisitionStatus.PENDING);

        List<MaterialRequisitionItem> items = null;
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            items = dto.getItems().stream().map(this::convertItemDTOToEntity).collect(Collectors.toList());
        }

        MaterialRequisition saved = items != null ?
                materialRequisitionService.createWithItems(entity, items) :
                materialRequisitionService.create(entity);
        return Result.success(convertToVO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新申领单", description = "更新申领单信息(仅待审批状态)")
    public Result<MaterialRequisitionVO> update(
            @Parameter(description = "申领单ID") @PathVariable String id,
            @Valid @RequestBody RequisitionDTO dto) {
        MaterialRequisition entity = new MaterialRequisition();
        BeanUtils.copyProperties(dto, entity);
        MaterialRequisition updated = materialRequisitionService.update(id, entity);
        return Result.success(convertToVO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除申领单", description = "删除申领单(仅待审批状态)")
    public Result<Void> delete(@Parameter(description = "申领单ID") @PathVariable String id) {
        materialRequisitionService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询申领单详情", description = "根据ID查询申领单详情")
    public Result<MaterialRequisitionVO> getById(@Parameter(description = "申领单ID") @PathVariable String id) {
        return materialRequisitionService.findById(id)
                .map(entity -> {
                    MaterialRequisitionVO vo = convertToVO(entity);
                    List<MaterialRequisitionItem> items = materialRequisitionService.getItems(id);
                    vo.setItems(items.stream().map(this::convertItemToVO).collect(Collectors.toList()));
                    return Result.success(vo);
                })
                .orElse(Result.error("申领单不存在"));
    }

    @GetMapping("/no/{requisitionNo}")
    @Operation(summary = "根据申领单号查询", description = "根据申领单号查询申领单")
    public Result<MaterialRequisitionVO> getByNo(@Parameter(description = "申领单号") @PathVariable String requisitionNo) {
        return materialRequisitionService.findByRequisitionNo(requisitionNo)
                .map(entity -> {
                    MaterialRequisitionVO vo = convertToVO(entity);
                    List<MaterialRequisitionItem> items = materialRequisitionService.getItems(entity.getId());
                    vo.setItems(items.stream().map(this::convertItemToVO).collect(Collectors.toList()));
                    return Result.success(vo);
                })
                .orElse(Result.error("申领单不存在"));
    }

    @GetMapping
    @Operation(summary = "分页查询申领单", description = "分页查询所有申领单")
    public Result<PageResult<MaterialRequisitionVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
        Page<MaterialRequisition> page = materialRequisitionService.findAll(pageable);
        List<MaterialRequisitionVO> list = page.getContent().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @GetMapping("/pending")
    @Operation(summary = "查询待审批申领单", description = "查询所有待审批的申领单")
    public Result<List<MaterialRequisitionVO>> listPending() {
        List<MaterialRequisition> requisitions = materialRequisitionService.findPendingRequisitions();
        List<MaterialRequisitionVO> voList = requisitions.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "根据库房查询申领单", description = "根据库房ID查询申领单列表")
    public Result<List<MaterialRequisitionVO>> listByWarehouse(@Parameter(description = "库房ID") @PathVariable String warehouseId) {
        List<MaterialRequisition> requisitions = materialRequisitionService.findByWarehouseId(warehouseId);
        List<MaterialRequisitionVO> voList = requisitions.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/dept/{deptId}")
    @Operation(summary = "根据科室查询申领单", description = "根据申领科室ID查询申领单列表")
    public Result<List<MaterialRequisitionVO>> listByDept(@Parameter(description = "科室ID") @PathVariable String deptId) {
        List<MaterialRequisition> requisitions = materialRequisitionService.findByDeptIdOrderByDateDesc(deptId);
        List<MaterialRequisitionVO> voList = requisitions.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "提交申领申请", description = "提交申领申请进入审批流程")
    public Result<MaterialRequisitionVO> submit(@Parameter(description = "申领单ID") @PathVariable String id) {
        MaterialRequisition requisition = materialRequisitionService.submit(id);
        return Result.success(convertToVO(requisition));
    }

    @PostMapping("/approve")
    @Operation(summary = "审批申领申请", description = "审批申领申请(通过或驳回)")
    public Result<MaterialRequisitionVO> approve(@Valid @RequestBody AuditDTO dto) {
        boolean approved = "通过".equals(dto.getResult()) || "APPROVED".equals(dto.getResult());
        MaterialRequisition requisition = materialRequisitionService.approve(dto.getId(), dto.getAuditorId(),
                dto.getAuditorName(), approved, dto.getRemark());
        return Result.success(convertToVO(requisition));
    }

    @PostMapping("/issue")
    @Operation(summary = "发放物资", description = "发放申领物资")
    public Result<MaterialRequisitionVO> issue(@Valid @RequestBody ConfirmDTO dto) {
        MaterialRequisition requisition = materialRequisitionService.issue(dto.getId(), dto.getOperatorId(), dto.getOperatorName());
        return Result.success(convertToVO(requisition));
    }

    @PostMapping("/receive")
    @Operation(summary = "接收物资", description = "接收确认申领物资")
    public Result<MaterialRequisitionVO> receive(@Valid @RequestBody ConfirmDTO dto) {
        MaterialRequisition requisition = materialRequisitionService.receive(dto.getId(), dto.getOperatorId(), dto.getOperatorName());
        return Result.success(convertToVO(requisition));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消申领", description = "取消申领申请")
    public Result<MaterialRequisitionVO> cancel(@Parameter(description = "申领单ID") @PathVariable String id) {
        MaterialRequisition requisition = materialRequisitionService.cancel(id);
        return Result.success(convertToVO(requisition));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "添加申领明细", description = "向申领单添加明细项")
    public Result<MaterialRequisitionItemVO> addItem(
            @Parameter(description = "申领单ID") @PathVariable String id,
            @Valid @RequestBody RequisitionItemDTO itemDTO) {
        MaterialRequisitionItem item = convertItemDTOToEntity(itemDTO);
        MaterialRequisitionItem saved = materialRequisitionService.addItem(id, item);
        return Result.success(convertItemToVO(saved));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "删除申领明细", description = "删除申领单明细项")
    public Result<Void> deleteItem(
            @Parameter(description = "申领单ID") @PathVariable String id,
            @Parameter(description = "明细ID") @PathVariable String itemId) {
        materialRequisitionService.deleteItem(itemId);
        return Result.success();
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "查询申领明细", description = "查询申领单的所有明细")
    public Result<List<MaterialRequisitionItemVO>> getItems(@Parameter(description = "申领单ID") @PathVariable String id) {
        List<MaterialRequisitionItem> items = materialRequisitionService.getItems(id);
        List<MaterialRequisitionItemVO> voList = items.stream().map(this::convertItemToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/{id}/check-stock")
    @Operation(summary = "检查库存是否充足", description = "检查申领单所需库存是否充足")
    public Result<Boolean> checkStockAvailable(@Parameter(description = "申领单ID") @PathVariable String id) {
        boolean available = materialRequisitionService.checkStockAvailable(id);
        return Result.success(available);
    }

    @GetMapping("/search")
    @Operation(summary = "条件查询申领单", description = "根据条件查询申领单")
    public Result<PageResult<MaterialRequisitionVO>> search(
            @Parameter(description = "库房ID") @RequestParam(required = false) String warehouseId,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
        RequisitionStatus requisitionStatus = status != null ? RequisitionStatus.valueOf(status) : null;
        Page<MaterialRequisition> page = materialRequisitionService.search(warehouseId, deptId, requisitionStatus,
                startDate, endDate, pageable);
        List<MaterialRequisitionVO> list = page.getContent().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    private MaterialRequisitionVO convertToVO(MaterialRequisition entity) {
        MaterialRequisitionVO vo = new MaterialRequisitionVO();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getStatus() != null) {
            vo.setStatus(entity.getStatus().name());
        }
        return vo;
    }

    private MaterialRequisitionItemVO convertItemToVO(MaterialRequisitionItem entity) {
        MaterialRequisitionItemVO vo = new MaterialRequisitionItemVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private MaterialRequisitionItem convertItemDTOToEntity(RequisitionItemDTO dto) {
        MaterialRequisitionItem entity = new MaterialRequisitionItem();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
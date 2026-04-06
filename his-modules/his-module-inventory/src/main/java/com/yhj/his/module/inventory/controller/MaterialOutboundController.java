package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.OutboundDTO;
import com.yhj.his.module.inventory.dto.OutboundItemDTO;
import com.yhj.his.module.inventory.entity.MaterialOutbound;
import com.yhj.his.module.inventory.entity.MaterialOutboundItem;
import com.yhj.his.module.inventory.enums.OutboundStatus;
import com.yhj.his.module.inventory.enums.OutboundType;
import com.yhj.his.module.inventory.service.MaterialOutboundService;
import com.yhj.his.module.inventory.vo.MaterialOutboundItemVO;
import com.yhj.his.module.inventory.vo.MaterialOutboundVO;
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
 * 出库管理Controller
 */
@RestController
@RequestMapping("/api/inventory/v1/outbounds")
@Tag(name = "出库管理", description = "物资出库申请、审核、发放接口")
public class MaterialOutboundController {

    @Autowired
    private MaterialOutboundService materialOutboundService;

    @PostMapping
    @Operation(summary = "出库申请", description = "创建出库申请单")
    public Result<MaterialOutboundVO> apply(@Valid @RequestBody OutboundDTO dto) {
        MaterialOutbound entity = new MaterialOutbound();
        BeanUtils.copyProperties(dto, entity);
        entity.setOutboundNo(materialOutboundService.generateOutboundNo());
        entity.setOutboundType(OutboundType.valueOf(dto.getOutboundType()));
        entity.setStatus(OutboundStatus.PENDING);

        List<MaterialOutboundItem> items = null;
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            items = dto.getItems().stream().map(this::convertItemDTOToEntity).collect(Collectors.toList());
        }

        MaterialOutbound saved = items != null ?
                materialOutboundService.createWithItems(entity, items) :
                materialOutboundService.create(entity);
        return Result.success(convertToVO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新出库单", description = "更新出库单信息(仅待审核状态)")
    public Result<MaterialOutboundVO> update(
            @Parameter(description = "出库单ID") @PathVariable String id,
            @Valid @RequestBody OutboundDTO dto) {
        MaterialOutbound entity = new MaterialOutbound();
        BeanUtils.copyProperties(dto, entity);
        if (dto.getOutboundType() != null) {
            entity.setOutboundType(OutboundType.valueOf(dto.getOutboundType()));
        }
        MaterialOutbound updated = materialOutboundService.update(id, entity);
        return Result.success(convertToVO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除出库单", description = "删除出库单(仅待审核状态)")
    public Result<Void> delete(@Parameter(description = "出库单ID") @PathVariable String id) {
        materialOutboundService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询出库单详情", description = "根据ID查询出库单详情")
    public Result<MaterialOutboundVO> getById(@Parameter(description = "出库单ID") @PathVariable String id) {
        return materialOutboundService.findById(id)
                .map(entity -> {
                    MaterialOutboundVO vo = convertToVO(entity);
                    List<MaterialOutboundItem> items = materialOutboundService.getItems(id);
                    vo.setItems(items.stream().map(this::convertItemToVO).collect(Collectors.toList()));
                    return Result.success(vo);
                })
                .orElse(Result.error("出库单不存在"));
    }

    @GetMapping("/no/{outboundNo}")
    @Operation(summary = "根据出库单号查询", description = "根据出库单号查询出库单")
    public Result<MaterialOutboundVO> getByNo(@Parameter(description = "出库单号") @PathVariable String outboundNo) {
        return materialOutboundService.findByOutboundNo(outboundNo)
                .map(entity -> {
                    MaterialOutboundVO vo = convertToVO(entity);
                    List<MaterialOutboundItem> items = materialOutboundService.getItems(entity.getId());
                    vo.setItems(items.stream().map(this::convertItemToVO).collect(Collectors.toList()));
                    return Result.success(vo);
                })
                .orElse(Result.error("出库单不存在"));
    }

    @GetMapping
    @Operation(summary = "分页查询出库单", description = "分页查询所有出库单")
    public Result<PageResult<MaterialOutboundVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
        Page<MaterialOutbound> page = materialOutboundService.findAll(pageable);
        List<MaterialOutboundVO> list = page.getContent().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @GetMapping("/pending")
    @Operation(summary = "查询待审核出库单", description = "查询所有待审核的出库单")
    public Result<List<MaterialOutboundVO>> listPending() {
        List<MaterialOutbound> outbounds = materialOutboundService.findPendingOutbounds();
        List<MaterialOutboundVO> voList = outbounds.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "根据库房查询出库单", description = "根据库房ID查询出库单列表")
    public Result<List<MaterialOutboundVO>> listByWarehouse(@Parameter(description = "库房ID") @PathVariable String warehouseId) {
        List<MaterialOutbound> outbounds = materialOutboundService.findByWarehouseId(warehouseId);
        List<MaterialOutboundVO> voList = outbounds.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/dept/{deptId}")
    @Operation(summary = "根据目标科室查询出库单", description = "根据目标科室ID查询出库单列表")
    public Result<List<MaterialOutboundVO>> listByTargetDept(@Parameter(description = "目标科室ID") @PathVariable String deptId) {
        List<MaterialOutbound> outbounds = materialOutboundService.findByTargetDeptId(deptId);
        List<MaterialOutboundVO> voList = outbounds.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "根据出库类型查询", description = "根据出库类型查询出库单列表")
    public Result<List<MaterialOutboundVO>> listByType(@Parameter(description = "出库类型") @PathVariable String type) {
        OutboundType outboundType = OutboundType.valueOf(type);
        List<MaterialOutbound> outbounds = materialOutboundService.findByOutboundType(outboundType);
        List<MaterialOutboundVO> voList = outbounds.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "提交出库申请", description = "提交出库申请进入审核流程")
    public Result<MaterialOutboundVO> submit(@Parameter(description = "出库单ID") @PathVariable String id) {
        MaterialOutbound outbound = materialOutboundService.submit(id);
        return Result.success(convertToVO(outbound));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核出库申请", description = "审核出库申请(通过或驳回)")
    public Result<MaterialOutboundVO> audit(@Valid @RequestBody AuditDTO dto) {
        boolean approved = "通过".equals(dto.getResult()) || "APPROVED".equals(dto.getResult());
        MaterialOutbound outbound = materialOutboundService.audit(dto.getId(), dto.getAuditorId(),
                dto.getAuditorName(), approved, dto.getRemark());
        return Result.success(convertToVO(outbound));
    }

    @PostMapping("/issue")
    @Operation(summary = "发放出库", description = "发放出库物资")
    public Result<MaterialOutboundVO> issue(@Valid @RequestBody ConfirmDTO dto) {
        MaterialOutbound outbound = materialOutboundService.issue(dto.getId(), dto.getOperatorId(), dto.getOperatorName());
        return Result.success(convertToVO(outbound));
    }

    @PostMapping("/confirm")
    @Operation(summary = "确认出库", description = "接收确认出库物资")
    public Result<MaterialOutboundVO> confirm(@Valid @RequestBody ConfirmDTO dto) {
        MaterialOutbound outbound = materialOutboundService.confirm(dto.getId(), dto.getOperatorId(), dto.getOperatorName());
        return Result.success(convertToVO(outbound));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消出库", description = "取消出库申请")
    public Result<MaterialOutboundVO> cancel(@Parameter(description = "出库单ID") @PathVariable String id) {
        MaterialOutbound outbound = materialOutboundService.cancel(id);
        return Result.success(convertToVO(outbound));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "添加出库明细", description = "向出库单添加明细项")
    public Result<MaterialOutboundItemVO> addItem(
            @Parameter(description = "出库单ID") @PathVariable String id,
            @Valid @RequestBody OutboundItemDTO itemDTO) {
        MaterialOutboundItem item = convertItemDTOToEntity(itemDTO);
        MaterialOutboundItem saved = materialOutboundService.addItem(id, item);
        return Result.success(convertItemToVO(saved));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "删除出库明细", description = "删除出库单明细项")
    public Result<Void> deleteItem(
            @Parameter(description = "出库单ID") @PathVariable String id,
            @Parameter(description = "明细ID") @PathVariable String itemId) {
        materialOutboundService.deleteItem(itemId);
        return Result.success();
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "查询出库明细", description = "查询出库单的所有明细")
    public Result<List<MaterialOutboundItemVO>> getItems(@Parameter(description = "出库单ID") @PathVariable String id) {
        List<MaterialOutboundItem> items = materialOutboundService.getItems(id);
        List<MaterialOutboundItemVO> voList = items.stream().map(this::convertItemToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/{id}/check-stock")
    @Operation(summary = "检查库存是否充足", description = "检查出库单所需库存是否充足")
    public Result<Boolean> checkStockAvailable(@Parameter(description = "出库单ID") @PathVariable String id) {
        boolean available = materialOutboundService.checkStockAvailable(id);
        return Result.success(available);
    }

    @GetMapping("/search")
    @Operation(summary = "条件查询出库单", description = "根据条件查询出库单")
    public Result<PageResult<MaterialOutboundVO>> search(
            @Parameter(description = "库房ID") @RequestParam(required = false) String warehouseId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "出库类型") @RequestParam(required = false) String outboundType,
            @Parameter(description = "目标科室ID") @RequestParam(required = false) String targetDeptId,
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
        OutboundStatus outboundStatus = status != null ? OutboundStatus.valueOf(status) : null;
        OutboundType outboundTypeEnum = outboundType != null ? OutboundType.valueOf(outboundType) : null;
        Page<MaterialOutbound> page = materialOutboundService.search(warehouseId, outboundStatus, outboundTypeEnum,
                targetDeptId, startDate, endDate, pageable);
        List<MaterialOutboundVO> list = page.getContent().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    private MaterialOutboundVO convertToVO(MaterialOutbound entity) {
        MaterialOutboundVO vo = new MaterialOutboundVO();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getOutboundType() != null) {
            vo.setOutboundType(entity.getOutboundType().name());
        }
        if (entity.getStatus() != null) {
            vo.setStatus(entity.getStatus().name());
        }
        return vo;
    }

    private MaterialOutboundItemVO convertItemToVO(MaterialOutboundItem entity) {
        MaterialOutboundItemVO vo = new MaterialOutboundItemVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private MaterialOutboundItem convertItemDTOToEntity(OutboundItemDTO dto) {
        MaterialOutboundItem entity = new MaterialOutboundItem();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
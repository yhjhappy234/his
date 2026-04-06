package com.yhj.his.module.pacs.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pacs.dto.*;
import com.yhj.his.module.pacs.service.*;
import com.yhj.his.module.pacs.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 检查项目管理Controller
 */
@Tag(name = "检查项目管理", description = "检查项目CRUD接口")
@RestController
@RequestMapping("/api/pacs/v1/item")
@RequiredArgsConstructor
public class ExamItemController {

    private final ExamItemService examItemService;

    @Operation(summary = "创建检查项目", description = "创建新的检查项目")
    @PostMapping("/create")
    public Result<ExamItemVO> createItem(@Valid @RequestBody ExamItemDTO dto) {
        ExamItemVO vo = examItemService.createItem(dto);
        return Result.success("创建成功", vo);
    }

    @Operation(summary = "更新检查项目", description = "更新检查项目信息")
    @PostMapping("/update")
    public Result<ExamItemVO> updateItem(@Valid @RequestBody ExamItemDTO dto) {
        ExamItemVO vo = examItemService.updateItem(dto);
        return Result.success("更新成功", vo);
    }

    @Operation(summary = "删除检查项目", description = "删除检查项目")
    @DeleteMapping("/{itemId}")
    public Result<Void> deleteItem(@PathVariable String itemId) {
        examItemService.deleteItem(itemId);
        return Result.successVoid();
    }

    @Operation(summary = "启用检查项目", description = "启用检查项目")
    @PostMapping("/enable/{itemId}")
    public Result<ExamItemVO> enableItem(@PathVariable String itemId) {
        ExamItemVO vo = examItemService.enableItem(itemId);
        return Result.success("启用成功", vo);
    }

    @Operation(summary = "停用检查项目", description = "停用检查项目")
    @PostMapping("/disable/{itemId}")
    public Result<ExamItemVO> disableItem(@PathVariable String itemId) {
        ExamItemVO vo = examItemService.disableItem(itemId);
        return Result.success("停用成功", vo);
    }

    @Operation(summary = "查询项目详情", description = "根据ID查询项目详情")
    @GetMapping("/{itemId}")
    public Result<ExamItemVO> getItemById(@PathVariable String itemId) {
        ExamItemVO vo = examItemService.getItemById(itemId);
        return Result.success(vo);
    }

    @Operation(summary = "根据编码查询", description = "根据项目编码查询项目")
    @GetMapping("/code/{itemCode}")
    public Result<ExamItemVO> getItemByCode(@PathVariable String itemCode) {
        ExamItemVO vo = examItemService.getItemByCode(itemCode);
        return Result.success(vo);
    }

    @Operation(summary = "查询所有启用项目", description = "查询所有启用的检查项目")
    @GetMapping("/active")
    public Result<List<ExamItemVO>> getActiveItems() {
        List<ExamItemVO> list = examItemService.getActiveItems();
        return Result.success(list);
    }

    @Operation(summary = "根据类型查询项目", description = "根据检查类型查询项目")
    @GetMapping("/type/{examType}")
    public Result<List<ExamItemVO>> getItemsByExamType(@PathVariable String examType) {
        List<ExamItemVO> list = examItemService.getItemsByExamType(examType);
        return Result.success(list);
    }

    @Operation(summary = "分页查询项目", description = "分页查询检查项目")
    @GetMapping("/query")
    public Result<PageResult<ExamItemVO>> queryItems(
            @Parameter(description = "项目编码") @RequestParam(required = false) String itemCode,
            @Parameter(description = "项目名称") @RequestParam(required = false) String itemName,
            @Parameter(description = "检查类型") @RequestParam(required = false) String examType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<ExamItemVO> result = examItemService.queryItems(itemCode, itemName, examType, status, pageNum, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "查询造影项目", description = "查询需要造影的检查项目")
    @GetMapping("/contrast")
    public Result<List<ExamItemVO>> getContrastItems() {
        List<ExamItemVO> list = examItemService.getContrastItems();
        return Result.success(list);
    }

    @Operation(summary = "批量创建项目", description = "批量创建检查项目")
    @PostMapping("/batch-create")
    public Result<List<ExamItemVO>> batchCreateItems(@Valid @RequestBody List<ExamItemDTO> dtoList) {
        List<ExamItemVO> list = examItemService.batchCreateItems(dtoList);
        return Result.success("批量创建成功", list);
    }
}
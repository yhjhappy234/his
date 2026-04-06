package com.yhj.his.module.lis.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.lis.dto.TestItemCreateDTO;
import com.yhj.his.module.lis.dto.TestItemUpdateDTO;
import com.yhj.his.module.lis.enums.TestItemCategory;
import com.yhj.his.module.lis.enums.TestItemStatus;
import com.yhj.his.module.lis.service.TestItemService;
import com.yhj.his.module.lis.vo.TestItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 检验项目REST控制器
 */
@RestController
@RequestMapping("/api/lis/v1/test-items")
@RequiredArgsConstructor
@Tag(name = "检验项目管理", description = "检验项目的增删改查接口")
public class TestItemController {

    private final TestItemService testItemService;

    @PostMapping
    @Operation(summary = "创建检验项目", description = "创建新的检验项目")
    public Result<TestItemVO> create(@Valid @RequestBody TestItemCreateDTO dto) {
        TestItemVO vo = testItemService.create(dto);
        return Result.success("创建成功", vo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新检验项目", description = "更新检验项目信息")
    public Result<TestItemVO> update(
            @Parameter(description = "项目ID") @PathVariable String id,
            @Valid @RequestBody TestItemUpdateDTO dto) {
        TestItemVO vo = testItemService.update(id, dto);
        return Result.success("更新成功", vo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取检验项目", description = "根据ID获取检验项目详情")
    public Result<TestItemVO> getById(@Parameter(description = "项目ID") @PathVariable String id) {
        TestItemVO vo = testItemService.getById(id);
        return Result.success(vo);
    }

    @GetMapping("/code/{itemCode}")
    @Operation(summary = "根据编码获取检验项目", description = "根据项目编码获取检验项目详情")
    public Result<TestItemVO> getByItemCode(@Parameter(description = "项目编码") @PathVariable String itemCode) {
        TestItemVO vo = testItemService.getByItemCode(itemCode);
        return Result.success(vo);
    }

    @GetMapping
    @Operation(summary = "分页查询检验项目", description = "分页查询检验项目列表")
    public Result<PageResult<TestItemVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        PageResult<TestItemVO> result = testItemService.list(pageable);
        return Result.success(result);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索检验项目", description = "根据关键词搜索检验项目")
    public Result<PageResult<TestItemVO>> search(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "状态") @RequestParam(defaultValue = "NORMAL") String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        TestItemStatus itemStatus = TestItemStatus.valueOf(status);
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        PageResult<TestItemVO> result = testItemService.search(keyword, itemStatus, pageable);
        return Result.success(result);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "按分类查询检验项目", description = "根据分类查询检验项目列表")
    public Result<List<TestItemVO>> listByCategory(
            @Parameter(description = "分类") @PathVariable String category) {
        TestItemCategory itemCategory = TestItemCategory.valueOf(category);
        List<TestItemVO> list = testItemService.listByCategory(itemCategory);
        return Result.success(list);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询检验项目", description = "根据状态查询检验项目列表")
    public Result<List<TestItemVO>> listByStatus(
            @Parameter(description = "状态") @PathVariable String status) {
        TestItemStatus itemStatus = TestItemStatus.valueOf(status);
        List<TestItemVO> list = testItemService.listByStatus(itemStatus);
        return Result.success(list);
    }

    @GetMapping("/critical")
    @Operation(summary = "查询危急值项目", description = "查询有危急值设置的检验项目")
    public Result<List<TestItemVO>> listCriticalItems() {
        List<TestItemVO> list = testItemService.listCriticalItems();
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除检验项目", description = "删除指定的检验项目")
    public Result<Void> delete(@Parameter(description = "项目ID") @PathVariable String id) {
        testItemService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "启用检验项目", description = "启用指定的检验项目")
    public Result<TestItemVO> enable(@Parameter(description = "项目ID") @PathVariable String id) {
        TestItemVO vo = testItemService.enable(id);
        return Result.success("启用成功", vo);
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "停用检验项目", description = "停用指定的检验项目")
    public Result<TestItemVO> disable(@Parameter(description = "项目ID") @PathVariable String id) {
        TestItemVO vo = testItemService.disable(id);
        return Result.success("停用成功", vo);
    }

    @GetMapping("/exists/{itemCode}")
    @Operation(summary = "检查项目编码是否存在", description = "检查项目编码是否已存在")
    public Result<Boolean> existsByItemCode(@Parameter(description = "项目编码") @PathVariable String itemCode) {
        boolean exists = testItemService.existsByItemCode(itemCode);
        return Result.success(exists);
    }
}
package com.yhj.his.module.emr.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.emr.dto.EmrTemplateSaveDTO;
import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.enums.TemplateType;
import com.yhj.his.module.emr.service.EmrTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 病历模板REST控制器
 */
@Tag(name = "病历模板管理", description = "病历模板的CRUD及管理操作")
@RestController
@RequestMapping("/api/emr/v1/templates")
@RequiredArgsConstructor
public class EmrTemplateController {

    private final EmrTemplateService templateService;

    @Operation(summary = "创建病历模板", description = "创建新的病历模板")
    @PostMapping
    public Result<EmrTemplate> createTemplate(@Valid @RequestBody EmrTemplateSaveDTO dto) {
        EmrTemplate template = templateService.createTemplate(dto);
        return Result.success("模板创建成功", template);
    }

    @Operation(summary = "更新病历模板", description = "更新指定的病历模板")
    @PutMapping("/{id}")
    public Result<EmrTemplate> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable String id,
            @Valid @RequestBody EmrTemplateSaveDTO dto) {
        EmrTemplate template = templateService.updateTemplate(id, dto);
        return Result.success("模板更新成功", template);
    }

    @Operation(summary = "删除病历模板", description = "删除指定的病历模板")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTemplate(@Parameter(description = "模板ID") @PathVariable String id) {
        templateService.deleteTemplate(id);
        return Result.successVoid();
    }

    @Operation(summary = "获取病历模板详情", description = "根据ID获取病历模板详情")
    @GetMapping("/{id}")
    public Result<EmrTemplate> getTemplate(@Parameter(description = "模板ID") @PathVariable String id) {
        EmrTemplate template = templateService.getTemplateById(id);
        return Result.success(template);
    }

    @Operation(summary = "分页查询病历模板", description = "分页查询所有病历模板")
    @GetMapping
    public Result<Page<EmrTemplate>> listTemplates(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EmrTemplate> templates = templateService.listTemplates(pageable);
        return Result.success(templates);
    }

    @Operation(summary = "搜索病历模板", description = "根据模板名称模糊搜索")
    @GetMapping("/search")
    public Result<Page<EmrTemplate>> searchTemplates(
            @Parameter(description = "模板名称") @RequestParam String name,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EmrTemplate> templates = templateService.searchByTemplateName(name, pageable);
        return Result.success(templates);
    }

    @Operation(summary = "获取可用模板", description = "根据模板类型、科室和创建人获取可用模板")
    @GetMapping("/available")
    public Result<List<EmrTemplate>> getAvailableTemplates(
            @Parameter(description = "模板类型") @RequestParam TemplateType type,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "创建人ID") @RequestParam(required = false) String creatorId) {
        List<EmrTemplate> templates = templateService.getAvailableTemplates(type, deptId, creatorId);
        return Result.success(templates);
    }

    @Operation(summary = "根据类型获取模板", description = "获取指定类型的启用模板")
    @GetMapping("/type/{type}")
    public Result<List<EmrTemplate>> getTemplatesByType(
            @Parameter(description = "模板类型") @PathVariable TemplateType type) {
        List<EmrTemplate> templates = templateService.getTemplatesByType(type);
        return Result.success(templates);
    }

    @Operation(summary = "根据科室获取模板", description = "获取指定科室的启用模板")
    @GetMapping("/dept/{deptId}")
    public Result<List<EmrTemplate>> getTemplatesByDeptId(
            @Parameter(description = "科室ID") @PathVariable String deptId) {
        List<EmrTemplate> templates = templateService.getTemplatesByDeptId(deptId);
        return Result.success(templates);
    }

    @Operation(summary = "获取公开模板", description = "获取所有公开的启用模板")
    @GetMapping("/public")
    public Result<List<EmrTemplate>> getPublicTemplates() {
        List<EmrTemplate> templates = templateService.getPublicTemplates();
        return Result.success(templates);
    }

    @Operation(summary = "根据创建人获取模板", description = "获取指定创建人的模板")
    @GetMapping("/creator/{creatorId}")
    public Result<List<EmrTemplate>> getTemplatesByCreatorId(
            @Parameter(description = "创建人ID") @PathVariable String creatorId) {
        List<EmrTemplate> templates = templateService.getTemplatesByCreatorId(creatorId);
        return Result.success(templates);
    }

    @Operation(summary = "启用/禁用模板", description = "切换模板的启用状态")
    @PatchMapping("/{id}/status")
    public Result<EmrTemplate> toggleTemplateStatus(
            @Parameter(description = "模板ID") @PathVariable String id,
            @Parameter(description = "是否启用") @RequestParam boolean enabled) {
        EmrTemplate template = templateService.toggleTemplateStatus(id, enabled);
        return Result.success("模板状态更新成功", template);
    }

    @Operation(summary = "设置模板公开状态", description = "切换模板的公开状态")
    @PatchMapping("/{id}/public")
    public Result<EmrTemplate> toggleTemplatePublic(
            @Parameter(description = "模板ID") @PathVariable String id,
            @Parameter(description = "是否公开") @RequestParam boolean isPublic) {
        EmrTemplate template = templateService.toggleTemplatePublic(id, isPublic);
        return Result.success("模板公开状态更新成功", template);
    }
}
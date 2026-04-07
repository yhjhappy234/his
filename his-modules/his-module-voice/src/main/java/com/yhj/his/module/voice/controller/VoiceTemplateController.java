package com.yhj.his.module.voice.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.dto.VoiceTemplateRequest;
import com.yhj.his.module.voice.enums.TemplateType;
import com.yhj.his.module.voice.service.VoiceTemplateService;
import com.yhj.his.module.voice.vo.VoiceTemplateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 语音模板控制器
 */
@Tag(name = "语音模板管理", description = "语音播报模板相关接口")
@RestController
@RequestMapping("/api/voice/v1/template")
@RequiredArgsConstructor
public class VoiceTemplateController {

    private final VoiceTemplateService voiceTemplateService;

    @Operation(summary = "创建模板", description = "创建新的语音模板")
    @PostMapping("/create")
    public Result<VoiceTemplateVO> createTemplate(@Valid @RequestBody VoiceTemplateRequest request) {
        return voiceTemplateService.createTemplate(request);
    }

    @Operation(summary = "更新模板", description = "更新已有模板")
    @PutMapping("/update/{templateId}")
    public Result<VoiceTemplateVO> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable String templateId,
            @Valid @RequestBody VoiceTemplateRequest request) {
        return voiceTemplateService.updateTemplate(templateId, request);
    }

    @Operation(summary = "删除模板", description = "删除模板(系统模板不可删除)")
    @DeleteMapping("/delete/{templateId}")
    public Result<Void> deleteTemplate(@Parameter(description = "模板ID") @PathVariable String templateId) {
        return voiceTemplateService.deleteTemplate(templateId);
    }

    @Operation(summary = "查询模板详情", description = "根据ID查询模板详情")
    @GetMapping("/detail/{templateId}")
    public Result<VoiceTemplateVO> getTemplateById(@Parameter(description = "模板ID") @PathVariable String templateId) {
        return voiceTemplateService.getTemplateById(templateId);
    }

    @Operation(summary = "根据编码查询模板", description = "根据模板编码查询模板")
    @GetMapping("/by-code/{templateCode}")
    public Result<VoiceTemplateVO> getTemplateByCode(@Parameter(description = "模板编码") @PathVariable String templateCode) {
        return voiceTemplateService.getTemplateByCode(templateCode);
    }

    @Operation(summary = "分页查询模板列表", description = "分页查询语音模板列表")
    @GetMapping("/list")
    public Result<PageResult<VoiceTemplateVO>> getTemplateList(
            @Parameter(description = "模板类型") @RequestParam(required = false) String templateType,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean isEnabled,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        TemplateType type = null;
        if (templateType != null) {
            type = Arrays.stream(TemplateType.values())
                    .filter(t -> t.getCode().equals(templateType))
                    .findFirst()
                    .orElse(null);
        }

        return voiceTemplateService.getTemplateList(type, isEnabled, pageNum, pageSize);
    }

    @Operation(summary = "查询启用的模板", description = "查询所有启用的模板列表")
    @GetMapping("/enabled")
    public Result<List<VoiceTemplateVO>> getEnabledTemplates() {
        return voiceTemplateService.getEnabledTemplates();
    }

    @Operation(summary = "查询系统模板", description = "查询所有系统预置模板")
    @GetMapping("/system")
    public Result<List<VoiceTemplateVO>> getSystemTemplates() {
        return voiceTemplateService.getSystemTemplates();
    }

    @Operation(summary = "生成播报内容", description = "根据模板和参数生成播报内容")
    @PostMapping("/generate-content")
    public Result<String> generateContent(
            @Parameter(description = "模板编码") @RequestParam String templateCode,
            @RequestBody Map<String, Object> params) {
        return voiceTemplateService.generateContent(templateCode, params);
    }

    @Operation(summary = "启用/禁用模板", description = "切换模板启用状态")
    @PostMapping("/toggle/{templateId}")
    public Result<Void> toggleTemplate(
            @Parameter(description = "模板ID") @PathVariable String templateId,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled) {
        return voiceTemplateService.toggleTemplate(templateId, enabled);
    }
}
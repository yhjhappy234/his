package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.ScheduleTemplate;
import com.yhj.his.module.hr.enums.ScheduleType;
import com.yhj.his.module.hr.repository.ScheduleTemplateRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 排班模板管理控制器
 */
@RestController
@RequestMapping("/api/hr/v1/schedule-templates")
@Tag(name = "排班模板管理", description = "排班模板管理相关接口")
@RequiredArgsConstructor
public class ScheduleTemplateController {

    private final ScheduleTemplateRepository scheduleTemplateRepository;

    @PostMapping
    @Operation(summary = "创建排班模板", description = "新增排班模板")
    public Result<ScheduleTemplateVO> createTemplate(@Valid @RequestBody ScheduleTemplateCreateDTO dto) {
        ScheduleTemplate template = new ScheduleTemplate();
        template.setTemplateName(dto.getTemplateName());
        template.setTemplateCode(dto.getTemplateCode());
        template.setDeptId(dto.getDeptId());
        template.setWeekDay(dto.getWeekDay());
        template.setScheduleType(ScheduleType.valueOf(dto.getScheduleType()));
        template.setStartTime(dto.getStartTime());
        template.setEndTime(dto.getEndTime());
        template.setLocation(dto.getLocation());
        template.setDescription(dto.getDescription());
        template.setStatus(dto.getStatus() != null ? dto.getStatus() : "启用");
        template.setRemark(dto.getRemark());

        ScheduleTemplate saved = scheduleTemplateRepository.save(template);
        return Result.success(convertToVO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新排班模板", description = "更新排班模板信息")
    public Result<ScheduleTemplateVO> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable String id,
            @Valid @RequestBody ScheduleTemplateCreateDTO dto) {
        ScheduleTemplate template = scheduleTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("排班模板不存在"));

        template.setTemplateName(dto.getTemplateName());
        template.setDeptId(dto.getDeptId());
        template.setWeekDay(dto.getWeekDay());
        template.setScheduleType(ScheduleType.valueOf(dto.getScheduleType()));
        template.setStartTime(dto.getStartTime());
        template.setEndTime(dto.getEndTime());
        template.setLocation(dto.getLocation());
        template.setDescription(dto.getDescription());
        template.setStatus(dto.getStatus());
        template.setRemark(dto.getRemark());

        ScheduleTemplate saved = scheduleTemplateRepository.save(template);
        return Result.success(convertToVO(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除排班模板", description = "删除排班模板(逻辑删除)")
    public Result<Void> deleteTemplate(@Parameter(description = "模板ID") @PathVariable String id) {
        ScheduleTemplate template = scheduleTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("排班模板不存在"));
        template.setDeleted(true);
        scheduleTemplateRepository.save(template);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取排班模板详情", description = "根据ID获取排班模板详细信息")
    public Result<ScheduleTemplateVO> getTemplate(@Parameter(description = "模板ID") @PathVariable String id) {
        ScheduleTemplate template = scheduleTemplateRepository.findById(id)
                .filter(t -> !t.getDeleted())
                .orElseThrow(() -> new RuntimeException("排班模板不存在"));
        return Result.success(convertToVO(template));
    }

    @GetMapping("/code/{templateCode}")
    @Operation(summary = "根据编码获取排班模板", description = "根据模板编码获取排班模板")
    public Result<ScheduleTemplateVO> getTemplateByCode(
            @Parameter(description = "模板编码") @PathVariable String templateCode) {
        ScheduleTemplate template = scheduleTemplateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new RuntimeException("排班模板不存在"));
        return Result.success(convertToVO(template));
    }

    @GetMapping("/dept/{deptId}")
    @Operation(summary = "根据科室查询排班模板", description = "查询指定科室的排班模板列表")
    public Result<List<ScheduleTemplateVO>> listTemplatesByDept(
            @Parameter(description = "科室ID") @PathVariable String deptId) {
        List<ScheduleTemplate> templates = scheduleTemplateRepository.findByDeptIdAndDeletedFalse(deptId);
        List<ScheduleTemplateVO> voList = templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/global")
    @Operation(summary = "获取全局排班模板", description = "查询全局排班模板列表")
    public Result<List<ScheduleTemplateVO>> listGlobalTemplates() {
        List<ScheduleTemplate> templates = scheduleTemplateRepository.findGlobalTemplates();
        List<ScheduleTemplateVO> voList = templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/week-day/{weekDay}")
    @Operation(summary = "根据星期查询排班模板", description = "查询指定星期的排班模板列表")
    public Result<List<ScheduleTemplateVO>> listTemplatesByWeekDay(
            @Parameter(description = "星期几(1-7)") @PathVariable Integer weekDay) {
        List<ScheduleTemplate> templates = scheduleTemplateRepository.findByWeekDayAndDeletedFalse(weekDay);
        List<ScheduleTemplateVO> voList = templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    private ScheduleTemplateVO convertToVO(ScheduleTemplate template) {
        ScheduleTemplateVO vo = new ScheduleTemplateVO();
        vo.setId(template.getId());
        vo.setTemplateName(template.getTemplateName());
        vo.setTemplateCode(template.getTemplateCode());
        vo.setDeptId(template.getDeptId());
        vo.setDeptName(template.getDeptName());
        vo.setWeekDay(template.getWeekDay());
        vo.setScheduleType(template.getScheduleType() != null ? template.getScheduleType().name() : null);
        vo.setStartTime(template.getStartTime());
        vo.setEndTime(template.getEndTime());
        vo.setLocation(template.getLocation());
        vo.setDescription(template.getDescription());
        vo.setStatus(template.getStatus());
        vo.setRemark(template.getRemark());
        vo.setCreateTime(template.getCreateTime());
        vo.setUpdateTime(template.getUpdateTime());
        return vo;
    }
}
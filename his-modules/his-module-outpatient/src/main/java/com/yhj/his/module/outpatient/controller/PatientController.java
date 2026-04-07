package com.yhj.his.module.outpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.outpatient.dto.PatientCreateRequest;
import com.yhj.his.module.outpatient.dto.PatientUpdateRequest;
import com.yhj.his.module.outpatient.service.PatientService;
import com.yhj.his.module.outpatient.vo.PatientVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者管理Controller
 */
@Tag(name = "患者管理", description = "患者建档、查询、更新等接口")
@RestController
@RequestMapping("/api/outpatient/v1/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "患者建档", description = "创建新的患者档案")
    @PostMapping("/create")
    public Result<PatientVO> createPatient(@Valid @RequestBody PatientCreateRequest request) {
        PatientVO patient = patientService.createPatient(request);
        return Result.success("患者建档成功", patient);
    }

    @Operation(summary = "更新患者信息", description = "更新患者基本信息")
    @PutMapping("/update/{id}")
    public Result<PatientVO> updatePatient(
            @Parameter(description = "患者ID") @PathVariable String id,
            @Valid @RequestBody PatientUpdateRequest request) {
        PatientVO patient = patientService.updatePatient(id, request);
        return Result.success("患者信息更新成功", patient);
    }

    @Operation(summary = "根据ID查询患者", description = "根据患者ID查询患者信息")
    @GetMapping("/get/{id}")
    public Result<PatientVO> getPatientById(
            @Parameter(description = "患者ID") @PathVariable String id) {
        PatientVO patient = patientService.getPatientDetail(id);
        return Result.success(patient);
    }

    @Operation(summary = "根据身份证号查询患者", description = "根据身份证号查询患者信息")
    @GetMapping("/getByIdCard")
    public Result<PatientVO> getPatientByIdCard(
            @Parameter(description = "身份证号") @RequestParam String idCardNo) {
        return patientService.findByIdCardNo(idCardNo)
                .map(p -> Result.success(patientService.getPatientDetail(p.getId())))
                .orElse(Result.error("患者不存在"));
    }

    @Operation(summary = "分页查询患者列表", description = "分页查询患者列表")
    @GetMapping("/list")
    public Result<PageResult<PatientVO>> listPatients(
            @Parameter(description = "姓名") @RequestParam(required = false) String name,
            @Parameter(description = "电话") @RequestParam(required = false) String phone,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<PatientVO> result = patientService.listPatients(name, phone, status, PageRequest.of(pageNum - 1, pageSize));
        return Result.success(result);
    }

    @Operation(summary = "搜索患者", description = "按关键字搜索患者")
    @GetMapping("/search")
    public Result<List<PatientVO>> searchPatients(
            @Parameter(description = "关键字") @RequestParam String keyword) {
        List<PatientVO> result = patientService.searchPatients(keyword);
        return Result.success(result);
    }

    @Operation(summary = "注销患者", description = "注销患者档案")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deletePatient(
            @Parameter(description = "患者ID") @PathVariable String id) {
        patientService.deletePatient(id);
        return Result.successVoid();
    }

    @Operation(summary = "设置黑名单状态", description = "设置患者黑名单状态")
    @PostMapping("/blacklist/{id}")
    public Result<PatientVO> setBlacklist(
            @Parameter(description = "患者ID") @PathVariable String id,
            @Parameter(description = "是否黑名单") @RequestParam boolean isBlacklist,
            @Parameter(description = "原因") @RequestParam(required = false) String reason) {
        PatientVO patient = patientService.setBlacklist(id, isBlacklist, reason);
        return Result.success("设置成功", patient);
    }
}
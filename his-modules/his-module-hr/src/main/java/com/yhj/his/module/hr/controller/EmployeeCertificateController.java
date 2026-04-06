package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.service.EmployeeCertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工证件管理Controller
 */
@RestController
@RequestMapping("/api/hr/v1/certificate")
@RequiredArgsConstructor
@Tag(name = "员工证件管理", description = "员工证件资质管理接口")
public class EmployeeCertificateController {

    private final EmployeeCertificateService certificateService;

    @PostMapping
    @Operation(summary = "创建证件", description = "创建员工证件资质")
    public Result<EmployeeCertificateVO> createCertificate(@Valid @RequestBody EmployeeCertificateCreateDTO dto) {
        EmployeeCertificateVO vo = certificateService.createCertificate(dto);
        return Result.success(vo);
    }

    @PutMapping("/{certId}")
    @Operation(summary = "更新证件", description = "更新证件信息")
    public Result<EmployeeCertificateVO> updateCertificate(
            @Parameter(description = "证件ID") @PathVariable String certId,
            @Valid @RequestBody EmployeeCertificateCreateDTO dto) {
        EmployeeCertificateVO vo = certificateService.updateCertificate(certId, dto);
        return Result.success(vo);
    }

    @DeleteMapping("/{certId}")
    @Operation(summary = "删除证件", description = "删除证件")
    public Result<Void> deleteCertificate(@Parameter(description = "证件ID") @PathVariable String certId) {
        certificateService.deleteCertificate(certId);
        return Result.success();
    }

    @GetMapping("/{certId}")
    @Operation(summary = "获取证件详情", description = "根据ID获取证件详情")
    public Result<EmployeeCertificateVO> getCertificate(@Parameter(description = "证件ID") @PathVariable String certId) {
        EmployeeCertificateVO vo = certificateService.getCertificateById(certId);
        return Result.success(vo);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "查询员工证件", description = "查询指定员工的证件列表")
    public Result<List<EmployeeCertificateVO>> listCertificatesByEmployee(
            @Parameter(description = "员工ID") @PathVariable String employeeId) {
        List<EmployeeCertificateVO> list = certificateService.listCertificatesByEmployee(employeeId);
        return Result.success(list);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询证件", description = "分页查询证件列表")
    public Result<PageResult<EmployeeCertificateVO>> listCertificates(
            @Parameter(description = "员工ID") @RequestParam(required = false) String employeeId,
            @Parameter(description = "证件类型") @RequestParam(required = false) String certType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<EmployeeCertificateVO> result = certificateService.listCertificates(employeeId, certType, status, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/expiring")
    @Operation(summary = "查询即将过期证件", description = "查询即将过期的证件列表")
    public Result<List<EmployeeCertificateVO>> listExpiringCertificates() {
        List<EmployeeCertificateVO> list = certificateService.listExpiringCertificates();
        return Result.success(list);
    }
}
package com.yhj.his.module.outpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.outpatient.dto.AppointmentCancelRequest;
import com.yhj.his.module.outpatient.dto.AppointmentCreateRequest;
import com.yhj.his.module.outpatient.dto.CheckInRequest;
import com.yhj.his.module.outpatient.entity.Registration;
import com.yhj.his.module.outpatient.service.RegistrationService;
import com.yhj.his.module.outpatient.vo.AppointmentResultVO;
import com.yhj.his.module.outpatient.vo.CheckInResultVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 挂号管理控制器
 */
@RestController
@RequestMapping("/api/outpatient/v1/registrations")
@RequiredArgsConstructor
@Tag(name = "挂号管理", description = "挂号预约、签到、就诊管理接口")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/appointment")
    @Operation(summary = "预约挂号", description = "创建预约挂号记录")
    public Result<AppointmentResultVO> createAppointment(@Valid @RequestBody AppointmentCreateRequest request) {
        AppointmentResultVO result = registrationService.createAppointment(request);
        return Result.success("预约挂号成功", result);
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消预约", description = "取消预约挂号")
    public Result<Void> cancelAppointment(@Valid @RequestBody AppointmentCancelRequest request) {
        registrationService.cancelAppointment(request);
        return Result.successVoid();
    }

    @PostMapping("/checkin")
    @Operation(summary = "签到", description = "患者签到")
    public Result<CheckInResultVO> checkIn(@Valid @RequestBody CheckInRequest request) {
        CheckInResultVO result = registrationService.checkIn(request);
        return Result.success("签到成功", result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取挂号详情", description = "根据ID获取挂号详细信息")
    public Result<Registration> getRegistration(@Parameter(description = "挂号ID") @PathVariable String id) {
        Registration registration = registrationService.getRegistrationDetail(id);
        return Result.success(registration);
    }

    @GetMapping
    @Operation(summary = "查询挂号列表", description = "分页查询挂号列表")
    public Result<PageResult<Registration>> listRegistrations(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "医生ID") @RequestParam(required = false) String doctorId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("scheduleDate").descending());
        PageResult<Registration> result = registrationService.listRegistrations(patientId, deptId, doctorId, status, date, pageable);
        return Result.success(result);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "查询患者挂号记录", description = "查询患者的所有挂号记录")
    public Result<List<Registration>> listPatientRegistrations(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<Registration> registrations = registrationService.listPatientRegistrations(patientId);
        return Result.success(registrations);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "查询医生当日挂号", description = "查询医生当日挂号记录")
    public Result<List<Registration>> listDoctorRegistrations(
            @Parameter(description = "医生ID") @PathVariable String doctorId,
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Registration> registrations = registrationService.listDoctorRegistrations(doctorId, date);
        return Result.success(registrations);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "退号", description = "退号处理")
    public Result<Void> refundRegistration(
            @Parameter(description = "挂号ID") @PathVariable String id,
            @Parameter(description = "退号原因") @RequestParam(required = false) String reason) {
        registrationService.refundRegistration(id, reason);
        return Result.successVoid();
    }

    @PostMapping("/{id}/start-visit")
    @Operation(summary = "开始就诊", description = "开始就诊")
    public Result<Registration> startVisit(
            @Parameter(description = "挂号ID") @PathVariable String id,
            @Parameter(description = "医生ID") @RequestParam String doctorId) {
        Registration registration = registrationService.startVisit(id, doctorId);
        return Result.success("开始就诊", registration);
    }

    @PostMapping("/{id}/end-visit")
    @Operation(summary = "结束就诊", description = "结束就诊")
    public Result<Registration> endVisit(@Parameter(description = "挂号ID") @PathVariable String id) {
        Registration registration = registrationService.endVisit(id);
        return Result.success("结束就诊", registration);
    }

    @GetMapping("/waiting/{doctorId}")
    @Operation(summary = "查询待诊患者", description = "查询医生待诊患者列表")
    public Result<List<Registration>> listWaitingPatients(
            @Parameter(description = "医生ID") @PathVariable String doctorId,
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Registration> registrations = registrationService.listWaitingPatients(doctorId, date);
        return Result.success(registrations);
    }

    @GetMapping("/current/{doctorId}")
    @Operation(summary = "获取当前就诊患者", description = "获取医生当前就诊患者")
    public Result<Registration> getCurrentPatient(
            @Parameter(description = "医生ID") @PathVariable String doctorId,
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return registrationService.getCurrentPatient(doctorId, date)
                .map(Result::success)
                .orElse(Result.success(null));
    }
}
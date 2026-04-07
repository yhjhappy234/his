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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约挂号Controller
 */
@Tag(name = "预约挂号", description = "预约挂号、取消预约、签到等接口")
@RestController
@RequestMapping("/api/outpatient/v1/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final RegistrationService registrationService;

    @Operation(summary = "创建预约挂号", description = "预约挂号")
    @PostMapping("/create")
    public Result<AppointmentResultVO> createAppointment(@Valid @RequestBody AppointmentCreateRequest request) {
        AppointmentResultVO result = registrationService.createAppointment(request);
        return Result.success("预约成功", result);
    }

    @Operation(summary = "取消预约", description = "取消预约挂号")
    @PostMapping("/cancel")
    public Result<Void> cancelAppointment(@Valid @RequestBody AppointmentCancelRequest request) {
        registrationService.cancelAppointment(request);
        return Result.successVoid();
    }

    @Operation(summary = "签到", description = "患者到院签到")
    @PostMapping("/checkin")
    public Result<CheckInResultVO> checkIn(@Valid @RequestBody CheckInRequest request) {
        CheckInResultVO result = registrationService.checkIn(request);
        return Result.success("签到成功", result);
    }

    @Operation(summary = "根据ID查询挂号记录", description = "根据挂号ID查询挂号详情")
    @GetMapping("/get/{id}")
    public Result<Registration> getAppointmentById(
            @Parameter(description = "挂号ID") @PathVariable String id) {
        Registration registration = registrationService.getRegistrationDetail(id);
        return Result.success(registration);
    }

    @Operation(summary = "分页查询挂号列表", description = "分页查询挂号记录列表")
    @GetMapping("/list")
    public Result<PageResult<Registration>> listRegistrations(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "医生ID") @RequestParam(required = false) String doctorId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Registration> result = registrationService.listRegistrations(patientId, deptId, doctorId, status, date, PageRequest.of(pageNum - 1, pageSize));
        return Result.success(result);
    }

    @Operation(summary = "查询患者挂号记录列表", description = "查询患者的挂号历史记录")
    @GetMapping("/listByPatient")
    public Result<List<Registration>> listAppointmentsByPatient(
            @Parameter(description = "患者ID") @RequestParam String patientId) {
        List<Registration> result = registrationService.listPatientRegistrations(patientId);
        return Result.success(result);
    }

    @Operation(summary = "查询医生当日挂号记录", description = "查询医生当日挂号记录")
    @GetMapping("/listByDoctor")
    public Result<List<Registration>> listAppointmentsByDoctor(
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<Registration> result = registrationService.listDoctorRegistrations(doctorId, date);
        return Result.success(result);
    }

    @Operation(summary = "退号", description = "退号处理")
    @PostMapping("/refund/{id}")
    public Result<Void> refundRegistration(
            @Parameter(description = "挂号ID") @PathVariable String id,
            @Parameter(description = "退号原因") @RequestParam(required = false) String reason) {
        registrationService.refundRegistration(id, reason);
        return Result.successVoid();
    }

    @Operation(summary = "开始就诊", description = "开始就诊")
    @PostMapping("/start/{id}")
    public Result<Registration> startVisit(
            @Parameter(description = "挂号ID") @PathVariable String id,
            @Parameter(description = "医生ID") @RequestParam String doctorId) {
        Registration registration = registrationService.startVisit(id, doctorId);
        return Result.success("开始就诊", registration);
    }

    @Operation(summary = "结束就诊", description = "结束就诊")
    @PostMapping("/end/{id}")
    public Result<Registration> endVisit(
            @Parameter(description = "挂号ID") @PathVariable String id) {
        Registration registration = registrationService.endVisit(id);
        return Result.success("就诊结束", registration);
    }

    @Operation(summary = "查询待诊患者列表", description = "查询医生当日待诊患者列表")
    @GetMapping("/waiting")
    public Result<List<Registration>> listWaitingPatients(
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<Registration> result = registrationService.listWaitingPatients(doctorId, date);
        return Result.success(result);
    }

    @Operation(summary = "获取当前就诊患者", description = "获取医生当前就诊患者")
    @GetMapping("/current")
    public Result<Registration> getCurrentPatient(
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return registrationService.getCurrentPatient(doctorId, date)
                .map(Result::success)
                .orElse(Result.error("当前无就诊患者"));
    }
}
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
 * 检查预约登记Controller
 */
@Tag(name = "检查预约登记", description = "检查申请接收、预约安排、检查登记等接口")
@RestController
@RequestMapping("/api/pacs/v1/request")
@RequiredArgsConstructor
public class ExamRequestController {

    private final ExamRequestService examRequestService;

    @Operation(summary = "创建检查申请", description = "接收临床检查申请")
    @PostMapping("/create")
    public Result<ExamRequestVO> createRequest(@Valid @RequestBody ExamRequestDTO dto) {
        ExamRequestVO vo = examRequestService.createRequest(dto);
        return Result.success("申请成功", vo);
    }

    @Operation(summary = "预约安排", description = "安排检查预约时间")
    @PostMapping("/schedule")
    public Result<ExamRequestVO> schedule(@Valid @RequestBody ScheduleDTO dto) {
        ExamRequestVO vo = examRequestService.schedule(dto);
        return Result.success("预约成功", vo);
    }

    @Operation(summary = "检查登记", description = "患者到检登记")
    @PostMapping("/checkin")
    public Result<ExamRecordVO> checkIn(@Valid @RequestBody CheckInDTO dto) {
        ExamRecordVO vo = examRequestService.checkIn(dto);
        return Result.success("登记成功", vo);
    }

    @Operation(summary = "取消预约", description = "取消检查预约")
    @PostMapping("/cancel")
    public Result<ExamRequestVO> cancelRequest(
            @Parameter(description = "申请ID") @RequestParam String requestId,
            @Parameter(description = "取消原因") @RequestParam(required = false) String reason) {
        ExamRequestVO vo = examRequestService.cancelRequest(requestId, reason);
        return Result.success("取消成功", vo);
    }

    @Operation(summary = "查询申请详情", description = "根据ID查询申请详情")
    @GetMapping("/{requestId}")
    public Result<ExamRequestVO> getRequestById(@PathVariable String requestId) {
        ExamRequestVO vo = examRequestService.getRequestById(requestId);
        return Result.success(vo);
    }

    @Operation(summary = "根据申请单号查询", description = "根据申请单号查询申请详情")
    @GetMapping("/no/{requestNo}")
    public Result<ExamRequestVO> getRequestByNo(@PathVariable String requestNo) {
        ExamRequestVO vo = examRequestService.getRequestByNo(requestNo);
        return Result.success(vo);
    }

    @Operation(summary = "查询患者申请列表", description = "查询患者的所有检查申请")
    @GetMapping("/patient/{patientId}")
    public Result<List<ExamRequestVO>> getRequestsByPatientId(@PathVariable String patientId) {
        List<ExamRequestVO> list = examRequestService.getRequestsByPatientId(patientId);
        return Result.success(list);
    }

    @Operation(summary = "分页查询申请", description = "分页查询检查申请列表")
    @GetMapping("/query")
    public Result<PageResult<ExamRequestVO>> queryRequests(ExamQueryDTO queryDTO) {
        PageResult<ExamRequestVO> result = examRequestService.queryRequests(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "查询待预约申请", description = "查询所有待预约的检查申请")
    @GetMapping("/pending")
    public Result<List<ExamRequestVO>> getPendingRequests() {
        List<ExamRequestVO> list = examRequestService.getPendingRequests();
        return Result.success(list);
    }

    @Operation(summary = "查询可用排班", description = "查询指定日期和检查类型的可用排班")
    @GetMapping("/available-schedules")
    public Result<List<RoomScheduleVO>> getAvailableSchedules(
            @Parameter(description = "检查类型") @RequestParam(required = false) String examType,
            @Parameter(description = "日期") @RequestParam String date) {
        List<RoomScheduleVO> list = examRequestService.getAvailableSchedules(examType, date);
        return Result.success(list);
    }

    @Operation(summary = "更新申请状态", description = "更新申请状态")
    @PostMapping("/update-status")
    public Result<ExamRequestVO> updateStatus(
            @Parameter(description = "申请ID") @RequestParam String requestId,
            @Parameter(description = "状态") @RequestParam String status) {
        ExamRequestVO vo = examRequestService.updateStatus(requestId, status);
        return Result.success(vo);
    }
}
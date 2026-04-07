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
 * 设备机房管理Controller
 */
@Tag(name = "设备机房管理", description = "设备信息管理、机房排班管理接口")
@RestController
@RequestMapping("/api/pacs/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    // ==================== 设备管理接口 ====================

    @Operation(summary = "创建设备", description = "创建新设备")
    @PostMapping("/create")
    public Result<EquipmentVO> createEquipment(@Valid @RequestBody EquipmentDTO dto) {
        EquipmentVO vo = equipmentService.createEquipment(dto);
        return Result.success("创建成功", vo);
    }

    @Operation(summary = "更新设备", description = "更新设备信息")
    @PostMapping("/update")
    public Result<EquipmentVO> updateEquipment(@Valid @RequestBody EquipmentDTO dto) {
        EquipmentVO vo = equipmentService.updateEquipment(dto);
        return Result.success("更新成功", vo);
    }

    @Operation(summary = "删除设备", description = "删除设备")
    @DeleteMapping("/{equipmentId}")
    public Result<Void> deleteEquipment(@PathVariable String equipmentId) {
        equipmentService.deleteEquipment(equipmentId);
        return Result.successVoid();
    }

    @Operation(summary = "查询设备详情", description = "根据ID查询设备详情")
    @GetMapping("/{equipmentId}")
    public Result<EquipmentVO> getEquipmentById(@PathVariable String equipmentId) {
        EquipmentVO vo = equipmentService.getEquipmentById(equipmentId);
        return Result.success(vo);
    }

    @Operation(summary = "根据编码查询设备", description = "根据设备编码查询")
    @GetMapping("/code/{equipmentCode}")
    public Result<EquipmentVO> getEquipmentByCode(@PathVariable String equipmentCode) {
        EquipmentVO vo = equipmentService.getEquipmentByCode(equipmentCode);
        return Result.success(vo);
    }

    @Operation(summary = "根据机房号查询设备", description = "根据机房号查询设备")
    @GetMapping("/room/{roomNo}")
    public Result<EquipmentVO> getEquipmentByRoomNo(@PathVariable String roomNo) {
        EquipmentVO vo = equipmentService.getEquipmentByRoomNo(roomNo);
        return Result.success(vo);
    }

    @Operation(summary = "根据类型查询设备", description = "根据设备类型查询")
    @GetMapping("/type/{equipmentType}")
    public Result<List<EquipmentVO>> getEquipmentByType(@PathVariable String equipmentType) {
        List<EquipmentVO> list = equipmentService.getEquipmentByType(equipmentType);
        return Result.success(list);
    }

    @Operation(summary = "查询正常设备", description = "查询所有正常状态的设备")
    @GetMapping("/normal")
    public Result<List<EquipmentVO>> getNormalEquipment() {
        List<EquipmentVO> list = equipmentService.getNormalEquipment();
        return Result.success(list);
    }

    @Operation(summary = "分页查询设备", description = "分页查询设备列表")
    @GetMapping("/query")
    public Result<PageResult<EquipmentVO>> queryEquipment(
            @Parameter(description = "设备编码") @RequestParam(required = false) String equipmentCode,
            @Parameter(description = "设备名称") @RequestParam(required = false) String equipmentName,
            @Parameter(description = "设备类型") @RequestParam(required = false) String equipmentType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "机房号") @RequestParam(required = false) String roomNo,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<EquipmentVO> result = equipmentService.queryEquipment(equipmentCode, equipmentName, equipmentType, status, roomNo, pageNum, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "更新设备状态", description = "更新设备运行状态")
    @PostMapping("/status")
    public Result<EquipmentVO> updateEquipmentStatus(
            @Parameter(description = "设备ID") @RequestParam String equipmentId,
            @Parameter(description = "状态") @RequestParam String status) {
        EquipmentVO vo = equipmentService.updateEquipmentStatus(equipmentId, status);
        return Result.success(vo);
    }

    // ==================== 机房排班接口 ====================

    @Operation(summary = "创建排班", description = "创建机房排班")
    @PostMapping("/schedule/create")
    public Result<RoomScheduleVO> createSchedule(@Valid @RequestBody RoomScheduleDTO dto) {
        RoomScheduleVO vo = equipmentService.createSchedule(dto);
        return Result.success("创建成功", vo);
    }

    @Operation(summary = "更新排班", description = "更新机房排班")
    @PostMapping("/schedule/update")
    public Result<RoomScheduleVO> updateSchedule(@Valid @RequestBody RoomScheduleDTO dto) {
        RoomScheduleVO vo = equipmentService.updateSchedule(dto);
        return Result.success("更新成功", vo);
    }

    @Operation(summary = "删除排班", description = "删除机房排班")
    @DeleteMapping("/schedule/{scheduleId}")
    public Result<Void> deleteSchedule(@PathVariable String scheduleId) {
        equipmentService.deleteSchedule(scheduleId);
        return Result.successVoid();
    }

    @Operation(summary = "查询排班详情", description = "根据ID查询排班详情")
    @GetMapping("/schedule/{scheduleId}")
    public Result<RoomScheduleVO> getScheduleById(@PathVariable String scheduleId) {
        RoomScheduleVO vo = equipmentService.getScheduleById(scheduleId);
        return Result.success(vo);
    }

    @Operation(summary = "查询机房排班", description = "查询机房的所有排班")
    @GetMapping("/schedule/room/{roomNo}")
    public Result<List<RoomScheduleVO>> getSchedulesByRoomNo(@PathVariable String roomNo) {
        List<RoomScheduleVO> list = equipmentService.getSchedulesByRoomNo(roomNo);
        return Result.success(list);
    }

    @Operation(summary = "查询日期排班", description = "查询指定日期的排班")
    @GetMapping("/schedule/date/{date}")
    public Result<List<RoomScheduleVO>> getSchedulesByDate(@PathVariable String date) {
        List<RoomScheduleVO> list = equipmentService.getSchedulesByDate(date);
        return Result.success(list);
    }

    @Operation(summary = "查询可用排班", description = "查询指定日期的可用排班")
    @GetMapping("/schedule/available")
    public Result<List<RoomScheduleVO>> getAvailableSchedules(
            @Parameter(description = "日期") @RequestParam String date,
            @Parameter(description = "检查类型") @RequestParam(required = false) String examType) {
        List<RoomScheduleVO> list = equipmentService.getAvailableSchedules(date, examType);
        return Result.success(list);
    }

    @Operation(summary = "分页查询排班", description = "分页查询排班列表")
    @GetMapping("/schedule/query")
    public Result<PageResult<RoomScheduleVO>> querySchedules(
            @Parameter(description = "机房号") @RequestParam(required = false) String roomNo,
            @Parameter(description = "日期") @RequestParam(required = false) String date,
            @Parameter(description = "班次") @RequestParam(required = false) String shift,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<RoomScheduleVO> result = equipmentService.querySchedules(roomNo, date, shift, status, pageNum, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "开放排班", description = "开放机房排班")
    @PostMapping("/schedule/open/{scheduleId}")
    public Result<RoomScheduleVO> openSchedule(@PathVariable String scheduleId) {
        RoomScheduleVO vo = equipmentService.openSchedule(scheduleId);
        return Result.success("开放成功", vo);
    }

    @Operation(summary = "关闭排班", description = "关闭机房排班")
    @PostMapping("/schedule/close/{scheduleId}")
    public Result<RoomScheduleVO> closeSchedule(@PathVariable String scheduleId) {
        RoomScheduleVO vo = equipmentService.closeSchedule(scheduleId);
        return Result.success("关闭成功", vo);
    }

    @Operation(summary = "批量创建排班", description = "批量创建机房排班")
    @PostMapping("/schedule/batch-create")
    public Result<List<RoomScheduleVO>> batchCreateSchedules(@Valid @RequestBody List<RoomScheduleDTO> dtoList) {
        List<RoomScheduleVO> list = equipmentService.batchCreateSchedules(dtoList);
        return Result.success("批量创建成功", list);
    }
}
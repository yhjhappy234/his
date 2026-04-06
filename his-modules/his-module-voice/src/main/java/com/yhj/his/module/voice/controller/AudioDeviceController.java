package com.yhj.his.module.voice.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.dto.AudioDeviceRequest;
import com.yhj.his.module.voice.enums.DeviceStatus;
import com.yhj.his.module.voice.service.AudioDeviceService;
import com.yhj.his.module.voice.vo.AudioDeviceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 音频设备控制器
 */
@Tag(name = "音频设备管理", description = "音频播放设备相关接口")
@RestController
@RequestMapping("/api/voice/v1/device")
@RequiredArgsConstructor
public class AudioDeviceController {

    private final AudioDeviceService audioDeviceService;

    @Operation(summary = "创建设备", description = "添加新的音频设备")
    @PostMapping("/create")
    public Result<AudioDeviceVO> createDevice(@Valid @RequestBody AudioDeviceRequest request) {
        return audioDeviceService.createDevice(request);
    }

    @Operation(summary = "更新设备", description = "更新设备信息")
    @PutMapping("/update/{deviceId}")
    public Result<AudioDeviceVO> updateDevice(
            @Parameter(description = "设备ID") @PathVariable String deviceId,
            @Valid @RequestBody AudioDeviceRequest request) {
        return audioDeviceService.updateDevice(deviceId, request);
    }

    @Operation(summary = "删除设备", description = "删除音频设备")
    @DeleteMapping("/delete/{deviceId}")
    public Result<Void> deleteDevice(@Parameter(description = "设备ID") @PathVariable String deviceId) {
        return audioDeviceService.deleteDevice(deviceId);
    }

    @Operation(summary = "查询设备详情", description = "根据ID查询设备详情")
    @GetMapping("/detail/{deviceId}")
    public Result<AudioDeviceVO> getDeviceById(@Parameter(description = "设备ID") @PathVariable String deviceId) {
        return audioDeviceService.getDeviceById(deviceId);
    }

    @Operation(summary = "根据编码查询设备", description = "根据设备编码查询设备")
    @GetMapping("/by-code/{deviceCode}")
    public Result<AudioDeviceVO> getDeviceByCode(@Parameter(description = "设备编码") @PathVariable String deviceCode) {
        return audioDeviceService.getDeviceByCode(deviceCode);
    }

    @Operation(summary = "分页查询设备列表", description = "分页查询音频设备列表")
    @GetMapping("/list")
    public Result<PageResult<AudioDeviceVO>> getDeviceList(
            @Parameter(description = "分组ID") @RequestParam(required = false) String groupId,
            @Parameter(description = "设备状态") @RequestParam(required = false) String status,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        DeviceStatus deviceStatus = null;
        if (status != null) {
            deviceStatus = Arrays.stream(DeviceStatus.values())
                    .filter(s -> s.getCode().equals(status))
                    .findFirst()
                    .orElse(null);
        }

        return audioDeviceService.getDeviceList(groupId, deviceStatus, keyword, pageNum, pageSize);
    }

    @Operation(summary = "查询分组设备", description = "查询指定分组下的所有设备")
    @GetMapping("/by-group/{groupId}")
    public Result<List<AudioDeviceVO>> getDevicesByGroup(@Parameter(description = "分组ID") @PathVariable String groupId) {
        return audioDeviceService.getDevicesByGroup(groupId);
    }

    @Operation(summary = "查询在线设备", description = "查询所有在线的设备")
    @GetMapping("/online")
    public Result<List<AudioDeviceVO>> getOnlineDevices() {
        return audioDeviceService.getOnlineDevices();
    }

    @Operation(summary = "调整设备音量", description = "调整单个设备的音量")
    @PostMapping("/volume")
    public Result<Void> adjustVolume(
            @Parameter(description = "设备ID") @RequestParam String deviceId,
            @Parameter(description = "音量(0-100)") @RequestParam Integer volume) {
        return audioDeviceService.adjustVolume(deviceId, volume);
    }

    @Operation(summary = "批量调整分组音量", description = "批量调整分组内所有设备的音量")
    @PostMapping("/volume/group")
    public Result<Void> adjustGroupVolume(
            @Parameter(description = "分组ID") @RequestParam String groupId,
            @Parameter(description = "音量(0-100)") @RequestParam Integer volume) {
        return audioDeviceService.adjustGroupVolume(groupId, volume);
    }

    @Operation(summary = "更新设备状态", description = "更新设备在线/离线状态")
    @PostMapping("/status/{deviceId}")
    public Result<Void> updateStatus(
            @Parameter(description = "设备ID") @PathVariable String deviceId,
            @Parameter(description = "状态") @RequestParam String status) {
        DeviceStatus deviceStatus = Arrays.stream(DeviceStatus.values())
                .filter(s -> s.getCode().equals(status))
                .findFirst()
                .orElse(DeviceStatus.OFFLINE);
        return audioDeviceService.updateStatus(deviceId, deviceStatus);
    }

    @Operation(summary = "设备心跳上报", description = "设备定时上报心跳(保持在线状态)")
    @PostMapping("/heartbeat")
    public Result<Void> heartbeat(@Parameter(description = "设备编码") @RequestParam String deviceCode) {
        return audioDeviceService.heartbeat(deviceCode);
    }

    @Operation(summary = "启用/禁用设备", description = "切换设备启用状态")
    @PostMapping("/toggle/{deviceId}")
    public Result<Void> toggleDevice(
            @Parameter(description = "设备ID") @PathVariable String deviceId,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled) {
        return audioDeviceService.toggleDevice(deviceId, enabled);
    }

    @Operation(summary = "统计分组设备数量", description = "统计指定分组下的设备数量")
    @GetMapping("/count/group/{groupId}")
    public Result<Long> countByGroup(@Parameter(description = "分组ID") @PathVariable String groupId) {
        return audioDeviceService.countByGroup(groupId);
    }
}
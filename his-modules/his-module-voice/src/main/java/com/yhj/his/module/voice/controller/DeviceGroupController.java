package com.yhj.his.module.voice.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.dto.DeviceGroupRequest;
import com.yhj.his.module.voice.service.DeviceGroupService;
import com.yhj.his.module.voice.vo.DeviceGroupVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备分组控制器
 */
@Tag(name = "设备分组管理", description = "音频设备分组相关接口")
@RestController
@RequestMapping("/api/voice/v1/group")
@RequiredArgsConstructor
public class DeviceGroupController {

    private final DeviceGroupService deviceGroupService;

    @Operation(summary = "创建分组", description = "创建新的设备分组")
    @PostMapping("/create")
    public Result<DeviceGroupVO> createGroup(@Valid @RequestBody DeviceGroupRequest request) {
        return deviceGroupService.createGroup(request);
    }

    @Operation(summary = "更新分组", description = "更新设备分组信息")
    @PutMapping("/update/{groupId}")
    public Result<DeviceGroupVO> updateGroup(
            @Parameter(description = "分组ID") @PathVariable String groupId,
            @Valid @RequestBody DeviceGroupRequest request) {
        return deviceGroupService.updateGroup(groupId, request);
    }

    @Operation(summary = "删除分组", description = "删除设备分组(分组下不能有设备或子分组)")
    @DeleteMapping("/delete/{groupId}")
    public Result<Void> deleteGroup(@Parameter(description = "分组ID") @PathVariable String groupId) {
        return deviceGroupService.deleteGroup(groupId);
    }

    @Operation(summary = "查询分组详情", description = "根据ID查询分组详情")
    @GetMapping("/detail/{groupId}")
    public Result<DeviceGroupVO> getGroupById(@Parameter(description = "分组ID") @PathVariable String groupId) {
        return deviceGroupService.getGroupById(groupId);
    }

    @Operation(summary = "根据编码查询分组", description = "根据分组编码查询分组")
    @GetMapping("/by-code/{groupCode}")
    public Result<DeviceGroupVO> getGroupByCode(@Parameter(description = "分组编码") @PathVariable String groupCode) {
        return deviceGroupService.getGroupByCode(groupCode);
    }

    @Operation(summary = "查询所有分组", description = "查询所有设备分组列表")
    @GetMapping("/all")
    public Result<List<DeviceGroupVO>> getAllGroups() {
        return deviceGroupService.getAllGroups();
    }

    @Operation(summary = "查询顶级分组", description = "查询顶级分组列表(无父分组)")
    @GetMapping("/top")
    public Result<List<DeviceGroupVO>> getTopGroups() {
        return deviceGroupService.getTopGroups();
    }

    @Operation(summary = "查询子分组", description = "查询指定分组下的子分组")
    @GetMapping("/children/{parentId}")
    public Result<List<DeviceGroupVO>> getChildGroups(@Parameter(description = "父分组ID") @PathVariable String parentId) {
        return deviceGroupService.getChildGroups(parentId);
    }

    @Operation(summary = "分页查询分组列表", description = "分页查询设备分组列表")
    @GetMapping("/list")
    public Result<PageResult<DeviceGroupVO>> getGroupList(
            @Parameter(description = "分组类型") @RequestParam(required = false) String groupType,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean isEnabled,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return deviceGroupService.getGroupList(groupType, isEnabled, pageNum, pageSize);
    }

    @Operation(summary = "查询分组树", description = "查询设备分组树形结构(包含设备和子分组)")
    @GetMapping("/tree")
    public Result<List<DeviceGroupVO>> getGroupTree() {
        return deviceGroupService.getGroupTree();
    }

    @Operation(summary = "启用/禁用分组", description = "切换分组启用状态")
    @PostMapping("/toggle/{groupId}")
    public Result<Void> toggleGroup(
            @Parameter(description = "分组ID") @PathVariable String groupId,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled) {
        return deviceGroupService.toggleGroup(groupId, enabled);
    }

    @Operation(summary = "设备分配到分组", description = "将设备分配到指定分组")
    @PostMapping("/assign-device")
    public Result<Void> assignDeviceToGroup(
            @Parameter(description = "设备ID") @RequestParam String deviceId,
            @Parameter(description = "分组ID") @RequestParam String groupId) {
        return deviceGroupService.assignDeviceToGroup(deviceId, groupId);
    }

    @Operation(summary = "设备从分组移除", description = "将设备从当前分组移除")
    @PostMapping("/remove-device/{deviceId}")
    public Result<Void> removeDeviceFromGroup(@Parameter(description = "设备ID") @PathVariable String deviceId) {
        return deviceGroupService.removeDeviceFromGroup(deviceId);
    }
}
package com.yhj.his.module.voice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.voice.dto.DeviceGroupRequest;
import com.yhj.his.module.voice.entity.AudioDevice;
import com.yhj.his.module.voice.entity.DeviceGroup;
import com.yhj.his.module.voice.repository.AudioDeviceRepository;
import com.yhj.his.module.voice.repository.DeviceGroupRepository;
import com.yhj.his.module.voice.service.DeviceGroupService;
import com.yhj.his.module.voice.vo.AudioDeviceVO;
import com.yhj.his.module.voice.vo.DeviceGroupVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备分组服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceGroupServiceImpl implements DeviceGroupService {

    private final DeviceGroupRepository deviceGroupRepository;
    private final AudioDeviceRepository audioDeviceRepository;

    @Override
    @Transactional
    public Result<DeviceGroupVO> createGroup(DeviceGroupRequest request) {
        // 检查编码是否已存在
        if (deviceGroupRepository.existsByGroupCode(request.getGroupCode())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "分组编码已存在: " + request.getGroupCode());
        }

        DeviceGroup group = new DeviceGroup();
        group.setGroupCode(request.getGroupCode());
        group.setGroupName(request.getGroupName());
        group.setParentId(request.getParentId());
        group.setGroupType(request.getGroupType());
        group.setLocation(request.getLocation());
        group.setDefaultVolume(request.getDefaultVolume() != null ? request.getDefaultVolume() : 80);
        group.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        group.setIsEnabled(request.getIsEnabled() != null ? request.getIsEnabled() : true);
        group.setDescription(request.getDescription());

        DeviceGroup saved = deviceGroupRepository.save(group);
        log.info("创建设备分组成功: groupCode={}", saved.getGroupCode());

        return Result.success("分组创建成功", convertToVO(saved));
    }

    @Override
    @Transactional
    public Result<DeviceGroupVO> updateGroup(String groupId, DeviceGroupRequest request) {
        DeviceGroup group = deviceGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "分组不存在"));

        // 如果修改编码，检查新编码是否已存在
        if (!group.getGroupCode().equals(request.getGroupCode())) {
            if (deviceGroupRepository.existsByGroupCode(request.getGroupCode())) {
                throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "分组编码已存在: " + request.getGroupCode());
            }
        }

        group.setGroupCode(request.getGroupCode());
        group.setGroupName(request.getGroupName());
        group.setParentId(request.getParentId());
        group.setGroupType(request.getGroupType());
        group.setLocation(request.getLocation());
        group.setDefaultVolume(request.getDefaultVolume());
        group.setSortOrder(request.getSortOrder());
        group.setIsEnabled(request.getIsEnabled());
        group.setDescription(request.getDescription());

        DeviceGroup saved = deviceGroupRepository.save(group);
        log.info("更新设备分组成功: groupId={}", groupId);

        return Result.success("分组更新成功", convertToVO(saved));
    }

    @Override
    @Transactional
    public Result<Void> deleteGroup(String groupId) {
        DeviceGroup group = deviceGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "分组不存在"));

        // 检查是否有子分组
        List<DeviceGroup> children = deviceGroupRepository.findByParentIdAndDeletedFalse(groupId);
        if (!children.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "该分组下存在子分组，无法删除");
        }

        // 检查是否有设备
        Long deviceCount = audioDeviceRepository.countByGroupId(groupId);
        if (deviceCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "该分组下存在设备，无法删除");
        }

        group.setDeleted(true);
        deviceGroupRepository.save(group);

        log.info("删除设备分组成功: groupId={}", groupId);
        return Result.successVoid();
    }

    @Override
    public Result<DeviceGroupVO> getGroupById(String groupId) {
        DeviceGroup group = deviceGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "分组不存在"));
        return Result.success(convertToVO(group));
    }

    @Override
    public Result<DeviceGroupVO> getGroupByCode(String groupCode) {
        DeviceGroup group = deviceGroupRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "分组不存在: " + groupCode));
        return Result.success(convertToVO(group));
    }

    @Override
    public Result<List<DeviceGroupVO>> getAllGroups() {
        List<DeviceGroup> groups = deviceGroupRepository.findByDeletedFalseOrderBySortOrderAsc();
        List<DeviceGroupVO> list = groups.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<DeviceGroupVO>> getTopGroups() {
        List<DeviceGroup> groups = deviceGroupRepository.findTopGroups();
        List<DeviceGroupVO> list = groups.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<DeviceGroupVO>> getChildGroups(String parentId) {
        List<DeviceGroup> groups = deviceGroupRepository.findByParentIdAndDeletedFalse(parentId);
        List<DeviceGroupVO> list = groups.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<PageResult<DeviceGroupVO>> getGroupList(String groupType, Boolean isEnabled,
                                                          Integer pageNum, Integer pageSize) {
        List<DeviceGroup> groups;
        if (StrUtil.isNotBlank(groupType)) {
            groups = deviceGroupRepository.findByGroupTypeAndDeletedFalse(groupType);
        } else if (isEnabled != null && isEnabled) {
            groups = deviceGroupRepository.findEnabledGroups();
        } else {
            groups = deviceGroupRepository.findByDeletedFalseOrderBySortOrderAsc();
        }

        List<DeviceGroupVO> list = groups.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, (long) list.size(), pageNum, pageSize));
    }

    @Override
    public Result<List<DeviceGroupVO>> getGroupTree() {
        List<DeviceGroup> allGroups = deviceGroupRepository.findByDeletedFalseOrderBySortOrderAsc();
        List<DeviceGroupVO> tree = buildGroupTree(allGroups, null);
        return Result.success(tree);
    }

    @Override
    @Transactional
    public Result<Void> toggleGroup(String groupId, Boolean enabled) {
        DeviceGroup group = deviceGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "分组不存在"));

        group.setIsEnabled(enabled);
        deviceGroupRepository.save(group);

        log.info("切换分组启用状态: groupId={}, enabled={}", groupId, enabled);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> assignDeviceToGroup(String deviceId, String groupId) {
        AudioDevice device = audioDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "设备不存在"));

        DeviceGroup group = deviceGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "分组不存在"));

        device.setDeviceGroupId(groupId);
        device.setDeviceGroupName(group.getGroupName());
        audioDeviceRepository.save(device);

        log.info("设备分配到分组: deviceId={}, groupId={}", deviceId, groupId);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> removeDeviceFromGroup(String deviceId) {
        AudioDevice device = audioDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "设备不存在"));

        device.setDeviceGroupId(null);
        device.setDeviceGroupName(null);
        audioDeviceRepository.save(device);

        log.info("设备从分组移除: deviceId={}", deviceId);
        return Result.successVoid();
    }

    /**
     * 构建分组树
     */
    private List<DeviceGroupVO> buildGroupTree(List<DeviceGroup> allGroups, String parentId) {
        List<DeviceGroupVO> tree = new ArrayList<>();

        for (DeviceGroup group : allGroups) {
            boolean isChildOfParent = (parentId == null && group.getParentId() == null) ||
                    (parentId != null && parentId.equals(group.getParentId()));

            if (isChildOfParent) {
                DeviceGroupVO vo = convertToVO(group);

                // 递归构建子分组
                List<DeviceGroupVO> children = buildGroupTree(allGroups, group.getId());
                vo.setChildren(children);

                // 查询分组下的设备
                List<AudioDevice> devices = audioDeviceRepository.findByDeviceGroupIdAndDeletedFalse(group.getId());
                vo.setDeviceCount(devices.size());
                vo.setDevices(devices.stream().map(d -> {
                    AudioDeviceVO deviceVO = new AudioDeviceVO();
                    deviceVO.setDeviceId(d.getId());
                    deviceVO.setDeviceCode(d.getDeviceCode());
                    deviceVO.setDeviceName(d.getDeviceName());
                    deviceVO.setStatus(d.getStatus() != null ? d.getStatus().getCode() : null);
                    deviceVO.setVolume(d.getVolume());
                    deviceVO.setIsEnabled(d.getIsEnabled());
                    return deviceVO;
                }).collect(Collectors.toList()));

                tree.add(vo);
            }
        }

        return tree;
    }

    /**
     * 转换为VO
     */
    private DeviceGroupVO convertToVO(DeviceGroup group) {
        DeviceGroupVO vo = new DeviceGroupVO();
        vo.setGroupId(group.getId());
        vo.setGroupCode(group.getGroupCode());
        vo.setGroupName(group.getGroupName());
        vo.setParentId(group.getParentId());

        // 获取父分组名称
        if (StrUtil.isNotBlank(group.getParentId())) {
            DeviceGroup parent = deviceGroupRepository.findById(group.getParentId()).orElse(null);
            if (parent != null) {
                vo.setParentName(parent.getGroupName());
            }
        }

        vo.setGroupType(group.getGroupType());
        vo.setLocation(group.getLocation());
        vo.setDefaultVolume(group.getDefaultVolume());
        vo.setSortOrder(group.getSortOrder());
        vo.setIsEnabled(group.getIsEnabled());
        vo.setDescription(group.getDescription());
        vo.setCreateTime(group.getCreateTime());
        vo.setUpdateTime(group.getUpdateTime());
        return vo;
    }
}
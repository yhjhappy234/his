package com.yhj.his.module.voice.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.dto.DeviceGroupRequest;
import com.yhj.his.module.voice.vo.DeviceGroupVO;

import java.util.List;

/**
 * 设备分组服务接口
 */
public interface DeviceGroupService {

    /**
     * 创建分组
     */
    Result<DeviceGroupVO> createGroup(DeviceGroupRequest request);

    /**
     * 更新分组
     */
    Result<DeviceGroupVO> updateGroup(String groupId, DeviceGroupRequest request);

    /**
     * 删除分组
     */
    Result<Void> deleteGroup(String groupId);

    /**
     * 根据ID查询分组
     */
    Result<DeviceGroupVO> getGroupById(String groupId);

    /**
     * 根据编码查询分组
     */
    Result<DeviceGroupVO> getGroupByCode(String groupCode);

    /**
     * 查询所有分组
     */
    Result<List<DeviceGroupVO>> getAllGroups();

    /**
     * 查询顶级分组
     */
    Result<List<DeviceGroupVO>> getTopGroups();

    /**
     * 查询子分组
     */
    Result<List<DeviceGroupVO>> getChildGroups(String parentId);

    /**
     * 分页查询分组列表
     */
    Result<PageResult<DeviceGroupVO>> getGroupList(String groupType, Boolean isEnabled,
                                                    Integer pageNum, Integer pageSize);

    /**
     * 查询分组树形结构
     */
    Result<List<DeviceGroupVO>> getGroupTree();

    /**
     * 启用/禁用分组
     */
    Result<Void> toggleGroup(String groupId, Boolean enabled);

    /**
     * 将设备分配到分组
     */
    Result<Void> assignDeviceToGroup(String deviceId, String groupId);

    /**
     * 从分组移除设备
     */
    Result<Void> removeDeviceFromGroup(String deviceId);
}
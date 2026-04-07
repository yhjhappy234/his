package com.yhj.his.module.voice.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.dto.AudioDeviceRequest;
import com.yhj.his.module.voice.enums.DeviceStatus;
import com.yhj.his.module.voice.vo.AudioDeviceVO;

import java.util.List;

/**
 * 音频设备服务接口
 */
public interface AudioDeviceService {

    /**
     * 创建设备
     */
    Result<AudioDeviceVO> createDevice(AudioDeviceRequest request);

    /**
     * 更新设备
     */
    Result<AudioDeviceVO> updateDevice(String deviceId, AudioDeviceRequest request);

    /**
     * 删除设备
     */
    Result<Void> deleteDevice(String deviceId);

    /**
     * 根据ID查询设备
     */
    Result<AudioDeviceVO> getDeviceById(String deviceId);

    /**
     * 根据编码查询设备
     */
    Result<AudioDeviceVO> getDeviceByCode(String deviceCode);

    /**
     * 分页查询设备列表
     */
    Result<PageResult<AudioDeviceVO>> getDeviceList(String groupId, DeviceStatus status,
                                                     String keyword, Integer pageNum, Integer pageSize);

    /**
     * 查询指定分组的设备
     */
    Result<List<AudioDeviceVO>> getDevicesByGroup(String groupId);

    /**
     * 查询在线设备
     */
    Result<List<AudioDeviceVO>> getOnlineDevices();

    /**
     * 调整设备音量
     */
    Result<Void> adjustVolume(String deviceId, Integer volume);

    /**
     * 批量调整分组音量
     */
    Result<Void> adjustGroupVolume(String groupId, Integer volume);

    /**
     * 更新设备状态
     */
    Result<Void> updateStatus(String deviceId, DeviceStatus status);

    /**
     * 设备心跳上报
     */
    Result<Void> heartbeat(String deviceCode);

    /**
     * 启用/禁用设备
     */
    Result<Void> toggleDevice(String deviceId, Boolean enabled);

    /**
     * 查询设备分组下的设备数量
     */
    Result<Long> countByGroup(String groupId);
}
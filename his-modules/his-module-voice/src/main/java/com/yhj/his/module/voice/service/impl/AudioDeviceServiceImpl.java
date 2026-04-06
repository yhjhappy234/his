package com.yhj.his.module.voice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.voice.dto.AudioDeviceRequest;
import com.yhj.his.module.voice.entity.AudioDevice;
import com.yhj.his.module.voice.entity.DeviceGroup;
import com.yhj.his.module.voice.enums.DeviceStatus;
import com.yhj.his.module.voice.repository.AudioDeviceRepository;
import com.yhj.his.module.voice.repository.DeviceGroupRepository;
import com.yhj.his.module.voice.service.AudioDeviceService;
import com.yhj.his.module.voice.vo.AudioDeviceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 音频设备服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AudioDeviceServiceImpl implements AudioDeviceService {

    private final AudioDeviceRepository audioDeviceRepository;
    private final DeviceGroupRepository deviceGroupRepository;

    @Override
    @Transactional
    public Result<AudioDeviceVO> createDevice(AudioDeviceRequest request) {
        // 检查编码是否已存在
        if (audioDeviceRepository.existsByDeviceCode(request.getDeviceCode())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "设备编码已存在: " + request.getDeviceCode());
        }

        AudioDevice device = new AudioDevice();
        device.setDeviceCode(request.getDeviceCode());
        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType());
        device.setDeviceGroupId(request.getDeviceGroupId());
        device.setWindowsDeviceId(request.getWindowsDeviceId());
        device.setIpAddress(request.getIpAddress());
        device.setPort(request.getPort());
        device.setVolume(request.getVolume());
        device.setIsEnabled(request.getIsEnabled() != null ? request.getIsEnabled() : true);
        device.setStatus(DeviceStatus.OFFLINE);
        device.setLocation(request.getLocation());
        device.setDescription(request.getDescription());
        device.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        // 设置分组名称
        if (StrUtil.isNotBlank(request.getDeviceGroupId())) {
            DeviceGroup group = deviceGroupRepository.findById(request.getDeviceGroupId())
                    .orElse(null);
            if (group != null) {
                device.setDeviceGroupName(group.getGroupName());
            }
        }

        AudioDevice saved = audioDeviceRepository.save(device);
        log.info("创建音频设备成功: deviceCode={}", saved.getDeviceCode());

        return Result.success("设备创建成功", convertToVO(saved));
    }

    @Override
    @Transactional
    public Result<AudioDeviceVO> updateDevice(String deviceId, AudioDeviceRequest request) {
        AudioDevice device = audioDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "设备不存在"));

        // 如果修改编码，检查新编码是否已存在
        if (!device.getDeviceCode().equals(request.getDeviceCode())) {
            if (audioDeviceRepository.existsByDeviceCode(request.getDeviceCode())) {
                throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "设备编码已存在: " + request.getDeviceCode());
            }
        }

        device.setDeviceCode(request.getDeviceCode());
        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType());
        device.setDeviceGroupId(request.getDeviceGroupId());
        device.setWindowsDeviceId(request.getWindowsDeviceId());
        device.setIpAddress(request.getIpAddress());
        device.setPort(request.getPort());
        device.setVolume(request.getVolume());
        device.setIsEnabled(request.getIsEnabled());
        device.setLocation(request.getLocation());
        device.setDescription(request.getDescription());
        device.setSortOrder(request.getSortOrder());

        // 更新分组名称
        if (StrUtil.isNotBlank(request.getDeviceGroupId())) {
            DeviceGroup group = deviceGroupRepository.findById(request.getDeviceGroupId())
                    .orElse(null);
            if (group != null) {
                device.setDeviceGroupName(group.getGroupName());
            } else {
                device.setDeviceGroupName(null);
            }
        } else {
            device.setDeviceGroupName(null);
        }

        AudioDevice saved = audioDeviceRepository.save(device);
        log.info("更新音频设备成功: deviceId={}", deviceId);

        return Result.success("设备更新成功", convertToVO(saved));
    }

    @Override
    @Transactional
    public Result<Void> deleteDevice(String deviceId) {
        AudioDevice device = audioDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "设备不存在"));

        device.setDeleted(true);
        audioDeviceRepository.save(device);

        log.info("删除音频设备成功: deviceId={}", deviceId);
        return Result.successVoid();
    }

    @Override
    public Result<AudioDeviceVO> getDeviceById(String deviceId) {
        AudioDevice device = audioDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "设备不存在"));
        return Result.success(convertToVO(device));
    }

    @Override
    public Result<AudioDeviceVO> getDeviceByCode(String deviceCode) {
        AudioDevice device = audioDeviceRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "设备不存在: " + deviceCode));
        return Result.success(convertToVO(device));
    }

    @Override
    public Result<PageResult<AudioDeviceVO>> getDeviceList(String groupId, DeviceStatus status,
                                                           String keyword, Integer pageNum, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.ASC, "sortOrder"));

        List<AudioDevice> devices;
        if (StrUtil.isNotBlank(groupId)) {
            devices = audioDeviceRepository.findByDeviceGroupIdAndDeletedFalse(groupId);
        } else if (status != null) {
            devices = audioDeviceRepository.findByStatusAndDeletedFalse(status);
        } else if (StrUtil.isNotBlank(keyword)) {
            devices = audioDeviceRepository.findByLocationContaining("%" + keyword + "%");
        } else {
            devices = audioDeviceRepository.findByDeletedFalseOrderBySortOrderAsc();
        }

        List<AudioDeviceVO> list = devices.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, (long) list.size(), pageNum, pageSize));
    }

    @Override
    public Result<List<AudioDeviceVO>> getDevicesByGroup(String groupId) {
        List<AudioDevice> devices = audioDeviceRepository.findByDeviceGroupIdAndDeletedFalse(groupId);
        List<AudioDeviceVO> list = devices.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<AudioDeviceVO>> getOnlineDevices() {
        List<AudioDevice> devices = audioDeviceRepository.findOnlineDevices();
        List<AudioDeviceVO> list = devices.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    @Transactional
    public Result<Void> adjustVolume(String deviceId, Integer volume) {
        if (volume < 0 || volume > 100) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "音量范围应为0-100");
        }

        AudioDevice device = audioDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "设备不存在"));

        device.setVolume(volume);
        audioDeviceRepository.save(device);

        log.info("调整设备音量: deviceId={}, volume={}", deviceId, volume);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> adjustGroupVolume(String groupId, Integer volume) {
        if (volume < 0 || volume > 100) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "音量范围应为0-100");
        }

        List<AudioDevice> devices = audioDeviceRepository.findByDeviceGroupIdAndDeletedFalse(groupId);
        for (AudioDevice device : devices) {
            device.setVolume(volume);
        }
        audioDeviceRepository.saveAll(devices);

        log.info("批量调整分组音量: groupId={}, volume={}, count={}", groupId, volume, devices.size());
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> updateStatus(String deviceId, DeviceStatus status) {
        AudioDevice device = audioDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "设备不存在"));

        device.setStatus(status);
        device.setLastHeartbeat(LocalDateTime.now());
        audioDeviceRepository.save(device);

        log.info("更新设备状态: deviceId={}, status={}", deviceId, status);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> heartbeat(String deviceCode) {
        AudioDevice device = audioDeviceRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "设备不存在: " + deviceCode));

        device.setStatus(DeviceStatus.ONLINE);
        device.setLastHeartbeat(LocalDateTime.now());
        audioDeviceRepository.save(device);

        log.debug("设备心跳: deviceCode={}", deviceCode);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> toggleDevice(String deviceId, Boolean enabled) {
        AudioDevice device = audioDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "设备不存在"));

        device.setIsEnabled(enabled);
        audioDeviceRepository.save(device);

        log.info("切换设备启用状态: deviceId={}, enabled={}", deviceId, enabled);
        return Result.successVoid();
    }

    @Override
    public Result<Long> countByGroup(String groupId) {
        Long count = audioDeviceRepository.countByGroupId(groupId);
        return Result.success(count);
    }

    /**
     * 转换为VO
     */
    private AudioDeviceVO convertToVO(AudioDevice device) {
        AudioDeviceVO vo = new AudioDeviceVO();
        vo.setDeviceId(device.getId());
        vo.setDeviceCode(device.getDeviceCode());
        vo.setDeviceName(device.getDeviceName());
        vo.setDeviceType(device.getDeviceType());
        vo.setDeviceGroupId(device.getDeviceGroupId());
        vo.setDeviceGroupName(device.getDeviceGroupName());
        vo.setWindowsDeviceId(device.getWindowsDeviceId());
        vo.setIpAddress(device.getIpAddress());
        vo.setPort(device.getPort());
        vo.setVolume(device.getVolume());
        vo.setIsEnabled(device.getIsEnabled());
        vo.setStatus(device.getStatus() != null ? device.getStatus().getCode() : null);
        vo.setStatusDesc(device.getStatus() != null ? device.getStatus().getDesc() : null);
        vo.setLastHeartbeat(device.getLastHeartbeat());
        vo.setLocation(device.getLocation());
        vo.setDescription(device.getDescription());
        vo.setSortOrder(device.getSortOrder());
        vo.setCreateTime(device.getCreateTime());
        vo.setUpdateTime(device.getUpdateTime());
        return vo;
    }
}
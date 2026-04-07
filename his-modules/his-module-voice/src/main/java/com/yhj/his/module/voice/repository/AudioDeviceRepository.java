package com.yhj.his.module.voice.repository;

import com.yhj.his.module.voice.entity.AudioDevice;
import com.yhj.his.module.voice.enums.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 音频设备Repository
 */
@Repository
public interface AudioDeviceRepository extends JpaRepository<AudioDevice, String> {

    /**
     * 根据设备编码查询
     */
    Optional<AudioDevice> findByDeviceCode(String deviceCode);

    /**
     * 根据设备分组查询
     */
    List<AudioDevice> findByDeviceGroupIdAndDeletedFalse(String deviceGroupId);

    /**
     * 根据设备分组名称查询
     */
    List<AudioDevice> findByDeviceGroupNameAndDeletedFalse(String deviceGroupName);

    /**
     * 查询所有启用的设备
     */
    @Query("SELECT d FROM AudioDevice d WHERE d.isEnabled = true AND d.deleted = false ORDER BY d.sortOrder ASC")
    List<AudioDevice> findEnabledDevices();

    /**
     * 根据状态查询
     */
    List<AudioDevice> findByStatusAndDeletedFalse(DeviceStatus status);

    /**
     * 查询在线设备
     */
    @Query("SELECT d FROM AudioDevice d WHERE d.status = 'ONLINE' AND d.isEnabled = true AND d.deleted = false")
    List<AudioDevice> findOnlineDevices();

    /**
     * 检查设备编码是否存在
     */
    @Query("SELECT COUNT(d) > 0 FROM AudioDevice d WHERE d.deviceCode = :deviceCode AND d.deleted = false")
    boolean existsByDeviceCode(@Param("deviceCode") String deviceCode);

    /**
     * 根据IP地址查询
     */
    Optional<AudioDevice> findByIpAddressAndDeletedFalse(String ipAddress);

    /**
     * 查询指定分组下的设备数量
     */
    @Query("SELECT COUNT(d) FROM AudioDevice d WHERE d.deviceGroupId = :groupId AND d.deleted = false")
    Long countByGroupId(@Param("groupId") String groupId);

    /**
     * 查询所有未删除的设备
     */
    List<AudioDevice> findByDeletedFalseOrderBySortOrderAsc();

    /**
     * 根据位置模糊查询
     */
    @Query("SELECT d FROM AudioDevice d WHERE d.location LIKE :location AND d.deleted = false")
    List<AudioDevice> findByLocationContaining(@Param("location") String location);
}
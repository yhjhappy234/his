package com.yhj.his.module.pacs.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.pacs.dto.*;
import com.yhj.his.module.pacs.vo.*;

import java.util.List;

/**
 * 设备机房管理服务接口
 */
public interface EquipmentService {

    /**
     * 创建设备
     */
    EquipmentVO createEquipment(EquipmentDTO dto);

    /**
     * 更新设备
     */
    EquipmentVO updateEquipment(EquipmentDTO dto);

    /**
     * 删除设备
     */
    void deleteEquipment(String equipmentId);

    /**
     * 查询设备详情
     */
    EquipmentVO getEquipmentById(String equipmentId);

    /**
     * 根据设备编码查询
     */
    EquipmentVO getEquipmentByCode(String equipmentCode);

    /**
     * 根据机房号查询设备
     */
    EquipmentVO getEquipmentByRoomNo(String roomNo);

    /**
     * 根据设备类型查询
     */
    List<EquipmentVO> getEquipmentByType(String equipmentType);

    /**
     * 查询所有正常设备
     */
    List<EquipmentVO> getNormalEquipment();

    /**
     * 分页查询设备
     */
    PageResult<EquipmentVO> queryEquipment(String equipmentCode, String equipmentName, String equipmentType, String status, String roomNo, Integer pageNum, Integer pageSize);

    /**
     * 更新设备状态
     */
    EquipmentVO updateEquipmentStatus(String equipmentId, String status);

    /**
     * 创建排班
     */
    RoomScheduleVO createSchedule(RoomScheduleDTO dto);

    /**
     * 更新排班
     */
    RoomScheduleVO updateSchedule(RoomScheduleDTO dto);

    /**
     * 删除排班
     */
    void deleteSchedule(String scheduleId);

    /**
     * 查询排班详情
     */
    RoomScheduleVO getScheduleById(String scheduleId);

    /**
     * 查询机房排班列表
     */
    List<RoomScheduleVO> getSchedulesByRoomNo(String roomNo);

    /**
     * 查询指定日期排班
     */
    List<RoomScheduleVO> getSchedulesByDate(String date);

    /**
     * 查询可用排班
     */
    List<RoomScheduleVO> getAvailableSchedules(String date, String examType);

    /**
     * 分页查询排班
     */
    PageResult<RoomScheduleVO> querySchedules(String roomNo, String date, String shift, String status, Integer pageNum, Integer pageSize);

    /**
     * 开放排班
     */
    RoomScheduleVO openSchedule(String scheduleId);

    /**
     * 关闭排班
     */
    RoomScheduleVO closeSchedule(String scheduleId);

    /**
     * 批量创建排班
     */
    List<RoomScheduleVO> batchCreateSchedules(List<RoomScheduleDTO> dtoList);
}
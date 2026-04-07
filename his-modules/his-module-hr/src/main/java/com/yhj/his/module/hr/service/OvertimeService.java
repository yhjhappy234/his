package com.yhj.his.module.hr.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Overtime;
import com.yhj.his.module.hr.enums.ApprovalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 加班服务接口
 */
public interface OvertimeService {

    /**
     * 创建加班申请
     */
    OvertimeVO createOvertime(OvertimeCreateDTO dto);

    /**
     * 取消加班申请
     */
    void cancelOvertime(String overtimeId);

    /**
     * 审批加班申请
     */
    OvertimeVO approveOvertime(OvertimeApprovalDTO dto, String approverId, String approverName);

    /**
     * 获取加班详情
     */
    OvertimeVO getOvertimeById(String overtimeId);

    /**
     * 根据员工获取加班记录列表
     */
    List<OvertimeVO> listOvertimesByEmployee(String employeeId);

    /**
     * 获取待审批加班记录列表
     */
    List<OvertimeVO> listPendingOvertimes(String approverId);

    /**
     * 分页查询加班记录
     */
    PageResult<OvertimeVO> listOvertimes(String employeeId, String deptId, ApprovalStatus approveStatus, LocalDate startDate, LocalDate endDate, Integer pageNum, Integer pageSize);

    /**
     * 计算加班时长
     */
    BigDecimal calculateOvertimeHours(OvertimeCreateDTO dto);

    /**
     * 查询员工某时间段内的加班时长
     */
    BigDecimal sumOvertimeHours(String employeeId, LocalDate startDate, LocalDate endDate);
}
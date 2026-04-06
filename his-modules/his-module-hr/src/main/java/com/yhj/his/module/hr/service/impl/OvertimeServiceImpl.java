package com.yhj.his.module.hr.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.date.DateUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Employee;
import com.yhj.his.module.hr.entity.Overtime;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.repository.EmployeeRepository;
import com.yhj.his.module.hr.repository.OvertimeRepository;
import com.yhj.his.module.hr.service.OvertimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 加班服务实现
 */
@Service
@RequiredArgsConstructor
public class OvertimeServiceImpl implements OvertimeService {

    private final OvertimeRepository overtimeRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public OvertimeVO createOvertime(OvertimeCreateDTO dto) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(dto.getEmployeeId())
                .orElseThrow(() -> new BusinessException("员工不存在"));

        // 验证时间
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BusinessException("结束时间不能早于开始时间");
        }

        Overtime overtime = new Overtime();
        BeanUtil.copyProperties(dto, overtime);
        overtime.setId(IdUtil.fastSimpleUUID());
        overtime.setOvertimeNo("OT" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + IdUtil.fastSimpleUUID().substring(0, 4));
        overtime.setEmployeeNo(employee.getEmployeeNo());
        overtime.setEmployeeName(employee.getEmployeeName());
        overtime.setDeptId(employee.getDeptId());
        overtime.setDeptName(employee.getDeptName());
        overtime.setApplyTime(LocalDateTime.now());
        overtime.setApproveStatus(ApprovalStatus.PENDING);
        overtime.setStatus("待审批");

        // 计算加班时长
        BigDecimal overtimeHours = dto.getOvertimeHours() != null ? dto.getOvertimeHours() : calculateOvertimeHours(dto);
        overtime.setOvertimeHours(overtimeHours);

        // 判断加班类型
        if (overtime.getOvertimeType() == null) {
            LocalDate overtimeDate = dto.getOvertimeDate();
            java.time.DayOfWeek dayOfWeek = overtimeDate.getDayOfWeek();
            if (dayOfWeek == java.time.DayOfWeek.SATURDAY || dayOfWeek == java.time.DayOfWeek.SUNDAY) {
                overtime.setOvertimeType("周末加班");
            } else {
                overtime.setOvertimeType("工作日加班");
            }
        }

        // 设置默认补偿类型
        if (overtime.getCompensateType() == null) {
            overtime.setCompensateType("加班费");
        }

        overtime = overtimeRepository.save(overtime);
        return convertToVO(overtime);
    }

    @Override
    @Transactional
    public void cancelOvertime(String overtimeId) {
        Overtime overtime = overtimeRepository.findByIdAndDeletedFalse(overtimeId)
                .orElseThrow(() -> new BusinessException("加班记录不存在"));

        if (overtime.getApproveStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("只有待审批的加班申请才能取消");
        }

        overtime.setApproveStatus(ApprovalStatus.CANCELLED);
        overtime.setStatus("已取消");
        overtimeRepository.save(overtime);
    }

    @Override
    @Transactional
    public OvertimeVO approveOvertime(OvertimeApprovalDTO dto, String approverId, String approverName) {
        Overtime overtime = overtimeRepository.findByIdAndDeletedFalse(dto.getOvertimeId())
                .orElseThrow(() -> new BusinessException("加班记录不存在"));

        if (overtime.getApproveStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("加班申请已审批，不能重复审批");
        }

        ApprovalStatus approveResult = ApprovalStatus.valueOf(dto.getApproveResult());
        overtime.setApproveStatus(approveResult);
        overtime.setApproverId(approverId);
        overtime.setApproverName(approverName);
        overtime.setApproveTime(LocalDateTime.now());
        overtime.setApproveRemark(dto.getApproveRemark());
        overtime.setStatus(approveResult == ApprovalStatus.APPROVED ? "已通过" : "已拒绝");

        overtime = overtimeRepository.save(overtime);
        return convertToVO(overtime);
    }

    @Override
    public OvertimeVO getOvertimeById(String overtimeId) {
        Overtime overtime = overtimeRepository.findByIdAndDeletedFalse(overtimeId)
                .orElseThrow(() -> new BusinessException("加班记录不存在"));
        return convertToVO(overtime);
    }

    @Override
    public List<OvertimeVO> listOvertimesByEmployee(String employeeId) {
        List<Overtime> list = overtimeRepository.findByEmployeeIdAndDeletedFalseOrderByApplyTimeDesc(employeeId);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<OvertimeVO> listPendingOvertimes(String approverId) {
        List<Overtime> list = overtimeRepository.findPendingByApprover(approverId);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<OvertimeVO> listOvertimes(String employeeId, String deptId, ApprovalStatus approveStatus, LocalDate startDate, LocalDate endDate, Integer pageNum, Integer pageSize) {
        Page<Overtime> page = overtimeRepository.findByConditions(employeeId, deptId, approveStatus, startDate, endDate, PageUtils.of(pageNum, pageSize));
        List<OvertimeVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public BigDecimal calculateOvertimeHours(OvertimeCreateDTO dto) {
        Duration duration = Duration.between(dto.getStartTime(), dto.getEndTime());
        double hours = duration.toMinutes() / 60.0;
        return BigDecimal.valueOf(hours).setScale(1, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal sumOvertimeHours(String employeeId, LocalDate startDate, LocalDate endDate) {
        BigDecimal sum = overtimeRepository.sumOvertimeHours(employeeId, startDate, endDate);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    private OvertimeVO convertToVO(Overtime overtime) {
        OvertimeVO vo = new OvertimeVO();
        BeanUtil.copyProperties(overtime, vo);

        if (overtime.getApproveStatus() != null) {
            vo.setApproveStatus(overtime.getApproveStatus().name());
        }

        return vo;
    }
}
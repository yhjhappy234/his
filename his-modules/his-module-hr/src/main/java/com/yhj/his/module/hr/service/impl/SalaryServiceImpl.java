package com.yhj.his.module.hr.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.date.DateUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Employee;
import com.yhj.his.module.hr.entity.LeaveRequest;
import com.yhj.his.module.hr.entity.Overtime;
import com.yhj.his.module.hr.entity.SalaryRecord;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.enums.EmployeeStatus;
import com.yhj.his.module.hr.repository.*;
import com.yhj.his.module.hr.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 薪资服务实现
 */
@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRecordRepository salaryRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final OvertimeRepository overtimeRepository;
    private final PerformanceEvaluationRepository performanceEvaluationRepository;

    @Override
    @Transactional
    public List<SalaryRecordVO> calculateSalary(SalaryCalculateDTO dto, String calculatorId, String calculatorName) {
        List<SalaryRecordVO> result = new ArrayList<>();

        // 解析月份
        YearMonth yearMonth = YearMonth.parse(dto.getSalaryMonth(), DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 获取员工列表
        List<Employee> employees;
        if (dto.getEmployeeIds() != null && !dto.getEmployeeIds().isEmpty()) {
            employees = dto.getEmployeeIds().stream()
                    .map(id -> employeeRepository.findByIdAndDeletedFalse(id)
                            .orElseThrow(() -> new BusinessException("员工不存在: " + id)))
                    .collect(Collectors.toList());
        } else if (dto.getDeptId() != null) {
            employees = employeeRepository.findByDeptIdAndStatusAndDeletedFalse(dto.getDeptId(), EmployeeStatus.ON_JOB);
        } else {
            employees = employeeRepository.findByStatusAndDeletedFalse(EmployeeStatus.ON_JOB);
        }

        for (Employee employee : employees) {
            // 检查是否已计算
            if (salaryRecordRepository.existsByEmployeeIdAndSalaryMonth(employee.getId(), dto.getSalaryMonth())) {
                continue;
            }

            SalaryRecordVO vo = calculateEmployeeSalaryInternal(employee, dto.getSalaryMonth(), startDate, endDate, calculatorId, calculatorName);
            result.add(vo);
        }

        return result;
    }

    @Override
    @Transactional
    public SalaryRecordVO approveSalary(SalaryApprovalDTO dto, String approverId, String approverName) {
        SalaryRecord salaryRecord = salaryRecordRepository.findByIdAndDeletedFalse(dto.getSalaryId())
                .orElseThrow(() -> new BusinessException("薪资记录不存在"));

        if (!"待审核".equals(salaryRecord.getStatus())) {
            throw new BusinessException("薪资记录已审核，不能重复审核");
        }

        if ("APPROVED".equals(dto.getApproveResult())) {
            salaryRecord.setStatus("已审核");
        } else {
            salaryRecord.setStatus("已拒绝");
        }

        salaryRecord.setApproverId(approverId);
        salaryRecord.setApproverName(approverName);
        salaryRecord.setApproveTime(LocalDateTime.now());
        salaryRecord.setRemark(dto.getApproveRemark());

        salaryRecord = salaryRecordRepository.save(salaryRecord);
        return convertToVO(salaryRecord);
    }

    @Override
    @Transactional
    public SalaryRecordVO paySalary(String salaryId) {
        SalaryRecord salaryRecord = salaryRecordRepository.findByIdAndDeletedFalse(salaryId)
                .orElseThrow(() -> new BusinessException("薪资记录不存在"));

        if (!"已审核".equals(salaryRecord.getStatus())) {
            throw new BusinessException("薪资记录未审核，不能发放");
        }

        salaryRecord.setStatus("已发放");
        salaryRecord.setPayTime(LocalDateTime.now());

        salaryRecord = salaryRecordRepository.save(salaryRecord);
        return convertToVO(salaryRecord);
    }

    @Override
    public SalaryRecordVO getSalaryRecordById(String salaryId) {
        SalaryRecord salaryRecord = salaryRecordRepository.findByIdAndDeletedFalse(salaryId)
                .orElseThrow(() -> new BusinessException("薪资记录不存在"));
        return convertToVO(salaryRecord);
    }

    @Override
    public SalaryRecordVO getSalaryRecordByEmployeeAndMonth(String employeeId, String salaryMonth) {
        SalaryRecord salaryRecord = salaryRecordRepository.findByEmployeeIdAndSalaryMonthAndDeletedFalse(employeeId, salaryMonth)
                .orElse(null);
        return salaryRecord != null ? convertToVO(salaryRecord) : null;
    }

    @Override
    public List<SalaryRecordVO> listSalaryRecordsByEmployee(String employeeId) {
        List<SalaryRecord> list = salaryRecordRepository.findByEmployeeIdAndDeletedFalseOrderBySalaryMonthDesc(employeeId);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<SalaryRecordVO> listSalaryRecords(String employeeId, String deptId, String salaryMonth, String status, Integer pageNum, Integer pageSize) {
        Page<SalaryRecord> page = salaryRecordRepository.findByConditions(employeeId, deptId, salaryMonth, status, PageUtils.of(pageNum, pageSize));
        List<SalaryRecordVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    @Transactional
    public SalaryRecordVO calculateEmployeeSalary(String employeeId, String salaryMonth) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));

        // 检查是否已计算
        if (salaryRecordRepository.existsByEmployeeIdAndSalaryMonth(employeeId, salaryMonth)) {
            throw new BusinessException("该员工该月份薪资已计算");
        }

        YearMonth yearMonth = YearMonth.parse(salaryMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return calculateEmployeeSalaryInternal(employee, salaryMonth, startDate, endDate, null, null);
    }

    @Override
    public BigDecimal sumSalaryByMonth(String salaryMonth) {
        BigDecimal sum = salaryRecordRepository.sumNetSalaryByMonth(salaryMonth);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    private SalaryRecordVO calculateEmployeeSalaryInternal(Employee employee, String salaryMonth,
            LocalDate startDate, LocalDate endDate, String calculatorId, String calculatorName) {

        SalaryRecord salaryRecord = new SalaryRecord();
        salaryRecord.setId(IdUtil.fastSimpleUUID());
        salaryRecord.setSalaryNo("SL" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + IdUtil.fastSimpleUUID().substring(0, 4));
        salaryRecord.setEmployeeId(employee.getId());
        salaryRecord.setEmployeeNo(employee.getEmployeeNo());
        salaryRecord.setEmployeeName(employee.getEmployeeName());
        salaryRecord.setDeptId(employee.getDeptId());
        salaryRecord.setDeptName(employee.getDeptName());
        salaryRecord.setSalaryMonth(salaryMonth);
        salaryRecord.setCalculateTime(LocalDateTime.now());
        salaryRecord.setCalculatorId(calculatorId);
        salaryRecord.setCalculatorName(calculatorName);
        salaryRecord.setStatus("待审核");

        // 应出勤天数
        int workDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        salaryRecord.setWorkDays(workDays);

        // 基本工资
        BigDecimal baseSalary = employee.getBaseSalary() != null ? employee.getBaseSalary() : BigDecimal.ZERO;
        salaryRecord.setBaseSalary(baseSalary);

        // 岗位工资
        BigDecimal positionSalary = employee.getPositionSalary() != null ? employee.getPositionSalary() : BigDecimal.ZERO;
        salaryRecord.setPositionSalary(positionSalary);

        // 工龄工资（每年增加50元）
        int workYears = employee.getWorkYears() != null ? employee.getWorkYears() : 0;
        BigDecimal senioritySalary = BigDecimal.valueOf(workYears * 50);
        salaryRecord.setSenioritySalary(senioritySalary);

        // 绩效工资（从绩效评分获取）
        final SalaryRecord finalSalaryRecord = salaryRecord;
        final BigDecimal finalBaseSalary = baseSalary;
        performanceEvaluationRepository.findByEmployeeAndTypeAndDate(employee.getId(), "月度", endDate)
                .ifPresent(pe -> {
                    if (pe.getTotalScore() != null && pe.getApproveStatus() == ApprovalStatus.APPROVED) {
                        // 绩效工资 = 基本工资 * 绩效分数比例
                        BigDecimal ratio = pe.getTotalScore().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                        finalSalaryRecord.setPerformanceSalary(finalBaseSalary.multiply(ratio));
                    }
                });

        // 加班费
        BigDecimal overtimeHours = overtimeRepository.sumOvertimeHours(employee.getId(), startDate, endDate);
        if (overtimeHours != null) {
            salaryRecord.setOvertimeHours(overtimeHours);
            // 加班费 = 基本工资 / 21.75 / 8 * 1.5 * 加班时长
            BigDecimal hourlyRate = baseSalary.divide(BigDecimal.valueOf(21.75 * 8), 4, RoundingMode.HALF_UP);
            BigDecimal overtimePay = hourlyRate.multiply(BigDecimal.valueOf(1.5)).multiply(overtimeHours);
            salaryRecord.setOvertimePay(overtimePay.setScale(2, RoundingMode.HALF_UP));
        } else {
            salaryRecord.setOvertimeHours(BigDecimal.ZERO);
            salaryRecord.setOvertimePay(BigDecimal.ZERO);
        }

        // 请假天数
        BigDecimal leaveDays = leaveRequestRepository.sumLeaveDays(employee.getId(), startDate, endDate);
        salaryRecord.setLeaveDays(leaveDays != null ? leaveDays : BigDecimal.ZERO);

        // 计算应出勤天数（扣除请假）
        int actualWorkDays = workDays - (leaveDays != null ? leaveDays.intValue() : 0);
        salaryRecord.setActualWorkDays(Math.max(actualWorkDays, 0));

        // 计算津贴和扣款（简化处理）
        salaryRecord.setNightAllowance(BigDecimal.ZERO);
        salaryRecord.setHolidayPay(BigDecimal.ZERO);
        salaryRecord.setOtherAllowance(BigDecimal.ZERO);
        salaryRecord.setSocialInsurance(BigDecimal.valueOf(300)); // 简化社保
        salaryRecord.setHousingFund(BigDecimal.valueOf(200)); // 简化公积金
        salaryRecord.setIncomeTax(BigDecimal.ZERO); // 简化个税
        salaryRecord.setOtherDeduction(BigDecimal.ZERO);

        // 计算应发合计
        BigDecimal grossSalary = baseSalary.add(positionSalary)
                .add(salaryRecord.getPerformanceSalary() != null ? salaryRecord.getPerformanceSalary() : BigDecimal.ZERO)
                .add(senioritySalary)
                .add(salaryRecord.getOvertimePay() != null ? salaryRecord.getOvertimePay() : BigDecimal.ZERO)
                .add(salaryRecord.getNightAllowance())
                .add(salaryRecord.getHolidayPay())
                .add(salaryRecord.getOtherAllowance());
        salaryRecord.setGrossSalary(grossSalary.setScale(2, RoundingMode.HALF_UP));

        // 计算扣款合计
        BigDecimal totalDeduction = salaryRecord.getSocialInsurance()
                .add(salaryRecord.getHousingFund())
                .add(salaryRecord.getIncomeTax())
                .add(salaryRecord.getOtherDeduction());
        salaryRecord.setTotalDeduction(totalDeduction);

        // 计算实发工资
        BigDecimal netSalary = grossSalary.subtract(totalDeduction);
        salaryRecord.setNetSalary(netSalary.setScale(2, RoundingMode.HALF_UP));

        salaryRecord = salaryRecordRepository.save(salaryRecord);
        return convertToVO(salaryRecord);
    }

    private SalaryRecordVO convertToVO(SalaryRecord salaryRecord) {
        SalaryRecordVO vo = new SalaryRecordVO();
        BeanUtil.copyProperties(salaryRecord, vo);
        return vo;
    }
}
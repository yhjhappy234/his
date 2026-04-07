package com.yhj.his.module.hr.service.impl;

import com.yhj.his.module.hr.entity.SalaryRecord;
import com.yhj.his.module.hr.entity.Attendance;
import com.yhj.his.module.hr.entity.Overtime;
import com.yhj.his.module.hr.repository.SalaryRecordRepository;
import com.yhj.his.module.hr.repository.EmployeeRepository;
import com.yhj.his.module.hr.repository.AttendanceRepository;
import com.yhj.his.module.hr.repository.OvertimeRepository;
import com.yhj.his.module.hr.service.SalaryRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 薪资记录服务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SalaryRecordServiceImpl implements SalaryRecordService {

    private final SalaryRecordRepository salaryRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final OvertimeRepository overtimeRepository;

    @Override
    public SalaryRecord createSalaryRecord(SalaryRecord salaryRecord) {
        // 获取员工信息
        if (salaryRecord.getEmployeeId() != null) {
            employeeRepository.findById(salaryRecord.getEmployeeId()).ifPresent(emp -> {
                salaryRecord.setEmployeeNo(emp.getEmployeeNo());
                salaryRecord.setEmployeeName(emp.getEmployeeName());
                salaryRecord.setDeptId(emp.getDeptId());
                salaryRecord.setDeptName(emp.getDeptName());
                salaryRecord.setBaseSalary(emp.getBaseSalary());
                salaryRecord.setPositionSalary(emp.getPositionSalary());
            });
        }

        // 生成薪资单号
        if (salaryRecord.getSalaryNo() == null) {
            salaryRecord.setSalaryNo(generateSalaryNo());
        }

        // 设置计算时间
        salaryRecord.setCalculateTime(LocalDateTime.now());

        // 计算薪资
        if (salaryRecord.getGrossSalary() == null) {
            salaryRecord.setGrossSalary(calculateGrossSalary(salaryRecord));
        }
        if (salaryRecord.getTotalDeduction() == null) {
            salaryRecord.setTotalDeduction(calculateTotalDeduction(salaryRecord));
        }
        if (salaryRecord.getNetSalary() == null) {
            salaryRecord.setNetSalary(calculateNetSalary(salaryRecord));
        }

        // 默认状态
        if (salaryRecord.getStatus() == null) {
            salaryRecord.setStatus("待审核");
        }

        return salaryRecordRepository.save(salaryRecord);
    }

    @Override
    public SalaryRecord updateSalaryRecord(SalaryRecord salaryRecord) {
        SalaryRecord existing = salaryRecordRepository.findById(salaryRecord.getId())
                .orElseThrow(() -> new RuntimeException("薪资记录不存在: " + salaryRecord.getId()));

        updateRecordFromEntity(salaryRecord, existing);
        return salaryRecordRepository.save(existing);
    }

    @Override
    public void deleteSalaryRecord(String id) {
        SalaryRecord record = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("薪资记录不存在: " + id));
        record.setDeleted(true);
        salaryRecordRepository.save(record);
    }

    @Override
    public Optional<SalaryRecord> getSalaryRecordById(String id) {
        return salaryRecordRepository.findById(id)
                .filter(s -> !s.getDeleted());
    }

    @Override
    public Optional<SalaryRecord> getSalaryRecordByNo(String salaryNo) {
        return salaryRecordRepository.findBySalaryNo(salaryNo)
                .filter(s -> !s.getDeleted());
    }

    @Override
    public Optional<SalaryRecord> getSalaryRecordByEmployeeAndMonth(String employeeId, String salaryMonth) {
        return salaryRecordRepository.findByEmployeeIdAndSalaryMonthAndDeletedFalse(employeeId, salaryMonth);
    }

    @Override
    public List<SalaryRecord> getSalaryRecordsByEmployeeId(String employeeId) {
        return salaryRecordRepository.findByEmployeeIdAndDeletedFalseOrderBySalaryMonthDesc(employeeId);
    }

    @Override
    public List<SalaryRecord> getSalaryRecordsByMonth(String salaryMonth) {
        return salaryRecordRepository.findBySalaryMonthAndDeletedFalse(salaryMonth);
    }

    @Override
    public List<SalaryRecord> getSalaryRecordsByStatus(String status) {
        return salaryRecordRepository.findByStatusAndDeletedFalseOrderBySalaryMonthDesc(status);
    }

    @Override
    public Page<SalaryRecord> searchSalaryRecords(String employeeId, String deptId, String salaryMonth, String status, Pageable pageable) {
        return salaryRecordRepository.findByConditions(employeeId, deptId, salaryMonth, status, pageable);
    }

    @Override
    public BigDecimal sumNetSalaryByMonth(String salaryMonth) {
        BigDecimal total = salaryRecordRepository.sumNetSalaryByMonth(salaryMonth);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumNetSalaryByEmployeeAndMonthRange(String employeeId, String startMonth, String endMonth) {
        BigDecimal total = salaryRecordRepository.sumNetSalaryByEmployeeAndMonthRange(employeeId, startMonth, endMonth);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public SalaryRecord approveSalaryRecord(String salaryRecordId, String approverId, String approverName) {
        SalaryRecord record = salaryRecordRepository.findById(salaryRecordId)
                .orElseThrow(() -> new RuntimeException("薪资记录不存在: " + salaryRecordId));

        record.setApproverId(approverId);
        record.setApproverName(approverName);
        record.setApproveTime(LocalDateTime.now());
        record.setStatus("已审核");

        return salaryRecordRepository.save(record);
    }

    @Override
    public SalaryRecord paySalary(String salaryRecordId) {
        SalaryRecord record = salaryRecordRepository.findById(salaryRecordId)
                .orElseThrow(() -> new RuntimeException("薪资记录不存在: " + salaryRecordId));

        if (!"已审核".equals(record.getStatus())) {
            throw new RuntimeException("只有已审核的薪资记录才能发放");
        }

        record.setStatus("已发放");
        record.setPayTime(LocalDateTime.now());

        return salaryRecordRepository.save(record);
    }

    @Override
    public SalaryRecord calculateSalary(String employeeId, String salaryMonth) {
        if (existsByEmployeeAndMonth(employeeId, salaryMonth)) {
            throw new RuntimeException("员工" + employeeId + "的" + salaryMonth + "薪资已存在");
        }

        SalaryRecord record = new SalaryRecord();
        record.setEmployeeId(employeeId);
        record.setSalaryMonth(salaryMonth);

        // 获取员工信息
        employeeRepository.findById(employeeId).ifPresent(emp -> {
            record.setEmployeeNo(emp.getEmployeeNo());
            record.setEmployeeName(emp.getEmployeeName());
            record.setDeptId(emp.getDeptId());
            record.setDeptName(emp.getDeptName());
            record.setBaseSalary(emp.getBaseSalary());
            record.setPositionSalary(emp.getPositionSalary());
        });

        // 解析薪资月份
        String[] parts = salaryMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 计算考勤相关数据
        calculateAttendanceData(record, employeeId, startDate, endDate);

        // 生成薪资单号
        record.setSalaryNo(generateSalaryNo());
        record.setCalculateTime(LocalDateTime.now());

        // 计算薪资
        record.setGrossSalary(calculateGrossSalary(record));
        record.setTotalDeduction(calculateTotalDeduction(record));
        record.setNetSalary(calculateNetSalary(record));
        record.setStatus("待审核");

        return salaryRecordRepository.save(record);
    }

    @Override
    public List<SalaryRecord> batchGenerateSalaryRecords(String salaryMonth, String deptId) {
        List<SalaryRecord> records = new ArrayList<>();

        List<String> employeeIds;
        if (deptId != null) {
            employeeIds = employeeRepository.findByDeptIdAndDeletedFalse(deptId)
                    .stream()
                    .map(e -> e.getId())
                    .toList();
        } else {
            employeeIds = employeeRepository.findByStatusAndDeletedFalse(
                    com.yhj.his.module.hr.enums.EmployeeStatus.ON_JOB)
                    .stream()
                    .map(e -> e.getId())
                    .toList();
        }

        for (String employeeId : employeeIds) {
            if (!existsByEmployeeAndMonth(employeeId, salaryMonth)) {
                records.add(calculateSalary(employeeId, salaryMonth));
            }
        }

        return records;
    }

    @Override
    public boolean existsBySalaryNo(String salaryNo) {
        return salaryRecordRepository.existsBySalaryNo(salaryNo);
    }

    @Override
    public boolean existsByEmployeeAndMonth(String employeeId, String salaryMonth) {
        return salaryRecordRepository.existsByEmployeeIdAndSalaryMonth(employeeId, salaryMonth);
    }

    @Override
    public String generateSalaryNo() {
        return "SL" + System.currentTimeMillis();
    }

    @Override
    public BigDecimal calculateGrossSalary(SalaryRecord salaryRecord) {
        BigDecimal gross = BigDecimal.ZERO;

        if (salaryRecord.getBaseSalary() != null) gross = gross.add(salaryRecord.getBaseSalary());
        if (salaryRecord.getPositionSalary() != null) gross = gross.add(salaryRecord.getPositionSalary());
        if (salaryRecord.getPerformanceSalary() != null) gross = gross.add(salaryRecord.getPerformanceSalary());
        if (salaryRecord.getSenioritySalary() != null) gross = gross.add(salaryRecord.getSenioritySalary());
        if (salaryRecord.getNightAllowance() != null) gross = gross.add(salaryRecord.getNightAllowance());
        if (salaryRecord.getOvertimePay() != null) gross = gross.add(salaryRecord.getOvertimePay());
        if (salaryRecord.getHolidayPay() != null) gross = gross.add(salaryRecord.getHolidayPay());
        if (salaryRecord.getOtherAllowance() != null) gross = gross.add(salaryRecord.getOtherAllowance());

        return gross;
    }

    @Override
    public BigDecimal calculateTotalDeduction(SalaryRecord salaryRecord) {
        BigDecimal deduction = BigDecimal.ZERO;

        if (salaryRecord.getSocialInsurance() != null) deduction = deduction.add(salaryRecord.getSocialInsurance());
        if (salaryRecord.getHousingFund() != null) deduction = deduction.add(salaryRecord.getHousingFund());
        if (salaryRecord.getIncomeTax() != null) deduction = deduction.add(salaryRecord.getIncomeTax());
        if (salaryRecord.getOtherDeduction() != null) deduction = deduction.add(salaryRecord.getOtherDeduction());

        return deduction;
    }

    @Override
    public BigDecimal calculateNetSalary(SalaryRecord salaryRecord) {
        BigDecimal gross = calculateGrossSalary(salaryRecord);
        BigDecimal deduction = calculateTotalDeduction(salaryRecord);
        return gross.subtract(deduction).setScale(2, RoundingMode.HALF_UP);
    }

    private void calculateAttendanceData(SalaryRecord record, String employeeId, LocalDate startDate, LocalDate endDate) {
        // 计算应出勤天数
        int workDays = 0;
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            int dayOfWeek = current.getDayOfWeek().getValue();
            if (dayOfWeek >= 1 && dayOfWeek <= 5) {
                workDays++;
            }
            current = current.plusDays(1);
        }
        record.setWorkDays(workDays);

        // 获取考勤记录
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
        int actualWorkDays = (int) attendances.stream()
                .filter(a -> a.getAttendanceStatus() != com.yhj.his.module.hr.enums.AttendanceStatus.ABSENT)
                .count();
        record.setActualWorkDays(actualWorkDays);

        // 获取加班时长
        BigDecimal overtimeHours = overtimeRepository.sumOvertimeHours(employeeId, startDate, endDate);
        if (overtimeHours != null) {
            record.setOvertimeHours(overtimeHours);
            record.setOvertimePay(overtimeHours.multiply(BigDecimal.valueOf(100)));
        }
    }

    private void updateRecordFromEntity(SalaryRecord source, SalaryRecord target) {
        if (source.getBaseSalary() != null) target.setBaseSalary(source.getBaseSalary());
        if (source.getPositionSalary() != null) target.setPositionSalary(source.getPositionSalary());
        if (source.getPerformanceSalary() != null) target.setPerformanceSalary(source.getPerformanceSalary());
        if (source.getSenioritySalary() != null) target.setSenioritySalary(source.getSenioritySalary());
        if (source.getNightAllowance() != null) target.setNightAllowance(source.getNightAllowance());
        if (source.getOvertimePay() != null) target.setOvertimePay(source.getOvertimePay());
        if (source.getHolidayPay() != null) target.setHolidayPay(source.getHolidayPay());
        if (source.getOtherAllowance() != null) target.setOtherAllowance(source.getOtherAllowance());
        if (source.getSocialInsurance() != null) target.setSocialInsurance(source.getSocialInsurance());
        if (source.getHousingFund() != null) target.setHousingFund(source.getHousingFund());
        if (source.getIncomeTax() != null) target.setIncomeTax(source.getIncomeTax());
        if (source.getOtherDeduction() != null) target.setOtherDeduction(source.getOtherDeduction());
        if (source.getRemark() != null) target.setRemark(source.getRemark());
    }
}
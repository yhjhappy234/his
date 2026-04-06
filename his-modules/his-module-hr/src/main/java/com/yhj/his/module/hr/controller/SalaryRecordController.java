package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.SalaryRecord;
import com.yhj.his.module.hr.repository.SalaryRecordRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 薪资记录管理控制器
 */
@RestController
@RequestMapping("/api/hr/v1/salary-records")
@Tag(name = "薪资记录管理", description = "薪资记录管理相关接口")
@RequiredArgsConstructor
public class SalaryRecordController {

    private final SalaryRecordRepository salaryRecordRepository;

    @PostMapping("/calculate")
    @Operation(summary = "计算薪资", description = "计算员工薪资")
    public Result<List<SalaryRecordVO>> calculateSalary(@Valid @RequestBody SalaryCalculateDTO dto) {
        List<SalaryRecord> records = new java.util.ArrayList<>();

        if (dto.getEmployeeIds() != null && !dto.getEmployeeIds().isEmpty()) {
            for (String employeeId : dto.getEmployeeIds()) {
                if (!salaryRecordRepository.existsByEmployeeIdAndSalaryMonth(employeeId, dto.getSalaryMonth())) {
                    SalaryRecord record = createSalaryRecord(employeeId, dto.getSalaryMonth());
                    records.add(salaryRecordRepository.save(record));
                }
            }
        } else {
            // TODO: Query all active employees from the specified department
        }

        List<SalaryRecordVO> voList = records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "审核薪资", description = "审核薪资记录")
    public Result<SalaryRecordVO> approveSalary(
            @Parameter(description = "薪资记录ID") @PathVariable String id,
            @Valid @RequestBody SalaryApprovalDTO dto) {
        SalaryRecord record = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("薪资记录不存在"));

        record.setApproveTime(LocalDateTime.now());
        record.setStatus("已审核");

        SalaryRecord saved = salaryRecordRepository.save(record);
        return Result.success(convertToVO(saved));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "发放薪资", description = "发放薪资")
    public Result<SalaryRecordVO> paySalary(@Parameter(description = "薪资记录ID") @PathVariable String id) {
        SalaryRecord record = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("薪资记录不存在"));

        if (!"已审核".equals(record.getStatus())) {
            return Result.error("只能发放已审核的薪资");
        }

        record.setPayTime(LocalDateTime.now());
        record.setStatus("已发放");

        SalaryRecord saved = salaryRecordRepository.save(record);
        return Result.success(convertToVO(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取薪资记录详情", description = "根据ID获取薪资记录详细信息")
    public Result<SalaryRecordVO> getSalaryRecord(@Parameter(description = "薪资记录ID") @PathVariable String id) {
        SalaryRecord record = salaryRecordRepository.findById(id)
                .filter(r -> !r.getDeleted())
                .orElseThrow(() -> new RuntimeException("薪资记录不存在"));
        return Result.success(convertToVO(record));
    }

    @GetMapping("/employee/{employeeId}/month/{salaryMonth}")
    @Operation(summary = "根据员工和月份获取薪资", description = "查询指定员工指定月份的薪资记录")
    public Result<SalaryRecordVO> getSalaryRecordByEmployeeAndMonth(
            @Parameter(description = "员工ID") @PathVariable String employeeId,
            @Parameter(description = "薪资月份") @PathVariable String salaryMonth) {
        SalaryRecord record = salaryRecordRepository.findByEmployeeIdAndSalaryMonthAndDeletedFalse(employeeId, salaryMonth)
                .orElseThrow(() -> new RuntimeException("薪资记录不存在"));
        return Result.success(convertToVO(record));
    }

    @GetMapping
    @Operation(summary = "分页查询薪资记录", description = "分页查询薪资记录列表")
    public Result<PageResult<SalaryRecordVO>> listSalaryRecords(
            @Parameter(description = "员工ID") @RequestParam(required = false) String employeeId,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "薪资月份") @RequestParam(required = false) String salaryMonth,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("salaryMonth").descending());

        Page<SalaryRecord> page = salaryRecordRepository.findByConditions(employeeId, deptId, salaryMonth, status, pageable);

        List<SalaryRecordVO> voList = page.getContent().stream()
                .filter(r -> !r.getDeleted())
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageResult<SalaryRecordVO> result = PageResult.of(voList, page.getTotalElements(), pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "根据员工查询薪资记录", description = "查询指定员工的薪资记录列表")
    public Result<List<SalaryRecordVO>> listSalaryRecordsByEmployee(
            @Parameter(description = "员工ID") @PathVariable String employeeId) {
        List<SalaryRecord> records = salaryRecordRepository.findByEmployeeIdAndDeletedFalseOrderBySalaryMonthDesc(employeeId);
        List<SalaryRecordVO> voList = records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/month/{salaryMonth}")
    @Operation(summary = "根据月份查询薪资记录", description = "查询指定月份的薪资记录列表")
    public Result<List<SalaryRecordVO>> listSalaryRecordsByMonth(
            @Parameter(description = "薪资月份") @PathVariable String salaryMonth) {
        List<SalaryRecord> records = salaryRecordRepository.findBySalaryMonthAndDeletedFalse(salaryMonth);
        List<SalaryRecordVO> voList = records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/statistics/{salaryMonth}")
    @Operation(summary = "薪资统计", description = "统计指定月份的薪资总额")
    public Result<BigDecimal> sumSalaryByMonth(@Parameter(description = "薪资月份") @PathVariable String salaryMonth) {
        BigDecimal total = salaryRecordRepository.sumNetSalaryByMonth(salaryMonth);
        return Result.success(total != null ? total : BigDecimal.ZERO);
    }

    private String generateSalaryNo() {
        return "SA" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private SalaryRecord createSalaryRecord(String employeeId, String salaryMonth) {
        SalaryRecord record = new SalaryRecord();
        record.setSalaryNo(generateSalaryNo());
        record.setEmployeeId(employeeId);
        record.setSalaryMonth(salaryMonth);
        record.setCalculateTime(LocalDateTime.now());
        record.setStatus("待审核");
        // TODO: Calculate actual salary based on employee info, attendance, overtime, etc.
        return record;
    }

    private SalaryRecordVO convertToVO(SalaryRecord record) {
        SalaryRecordVO vo = new SalaryRecordVO();
        vo.setId(record.getId());
        vo.setSalaryNo(record.getSalaryNo());
        vo.setEmployeeId(record.getEmployeeId());
        vo.setEmployeeNo(record.getEmployeeNo());
        vo.setEmployeeName(record.getEmployeeName());
        vo.setDeptId(record.getDeptId());
        vo.setDeptName(record.getDeptName());
        vo.setSalaryMonth(record.getSalaryMonth());
        vo.setBaseSalary(record.getBaseSalary());
        vo.setPositionSalary(record.getPositionSalary());
        vo.setPerformanceSalary(record.getPerformanceSalary());
        vo.setSenioritySalary(record.getSenioritySalary());
        vo.setNightAllowance(record.getNightAllowance());
        vo.setOvertimePay(record.getOvertimePay());
        vo.setHolidayPay(record.getHolidayPay());
        vo.setOtherAllowance(record.getOtherAllowance());
        vo.setGrossSalary(record.getGrossSalary());
        vo.setSocialInsurance(record.getSocialInsurance());
        vo.setHousingFund(record.getHousingFund());
        vo.setIncomeTax(record.getIncomeTax());
        vo.setOtherDeduction(record.getOtherDeduction());
        vo.setTotalDeduction(record.getTotalDeduction());
        vo.setNetSalary(record.getNetSalary());
        vo.setWorkDays(record.getWorkDays());
        vo.setActualWorkDays(record.getActualWorkDays());
        vo.setOvertimeHours(record.getOvertimeHours());
        vo.setLeaveDays(record.getLeaveDays());
        vo.setCalculateTime(record.getCalculateTime());
        vo.setCalculatorName(record.getCalculatorName());
        vo.setApproverName(record.getApproverName());
        vo.setApproveTime(record.getApproveTime());
        vo.setStatus(record.getStatus());
        vo.setPayTime(record.getPayTime());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());
        vo.setUpdateTime(record.getUpdateTime());
        return vo;
    }
}
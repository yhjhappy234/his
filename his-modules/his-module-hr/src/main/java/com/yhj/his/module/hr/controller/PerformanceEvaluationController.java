package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.PerformanceEvaluation;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.repository.PerformanceEvaluationRepository;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 绩效评分管理控制器
 */
@RestController
@RequestMapping("/api/hr/v1/performance-evaluations")
@Tag(name = "绩效评分管理", description = "绩效评分管理相关接口")
@RequiredArgsConstructor
public class PerformanceEvaluationController {

    private final PerformanceEvaluationRepository performanceEvaluationRepository;

    @PostMapping
    @Operation(summary = "创建绩效评分", description = "创建员工绩效评分")
    public Result<PerformanceEvaluationVO> createEvaluation(@Valid @RequestBody PerformanceEvaluationCreateDTO dto) {
        PerformanceEvaluation evaluation = new PerformanceEvaluation();
        evaluation.setEvaluationNo(generateEvaluationNo());
        evaluation.setEmployeeId(dto.getEmployeeId());
        evaluation.setPeriodStart(dto.getPeriodStart());
        evaluation.setPeriodEnd(dto.getPeriodEnd());
        evaluation.setEvaluationType(dto.getEvaluationType());
        evaluation.setWorkloadScore(dto.getWorkloadScore());
        evaluation.setQualityScore(dto.getQualityScore());
        evaluation.setServiceScore(dto.getServiceScore());
        evaluation.setAttendanceScore(dto.getAttendanceScore());
        evaluation.setOtherScore(dto.getOtherScore());
        evaluation.setComment(dto.getComment());
        evaluation.setEvaluateTime(LocalDateTime.now());
        evaluation.setApproveStatus(ApprovalStatus.PENDING);
        evaluation.setStatus("待审核");

        // 计算总分
        BigDecimal totalScore = calculateTotalScore(dto);
        evaluation.setTotalScore(totalScore);

        // 确定等级
        String level = determineLevel(totalScore);
        evaluation.setLevel(level);

        PerformanceEvaluation saved = performanceEvaluationRepository.save(evaluation);
        return Result.success(convertToVO(saved));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "审核绩效评分", description = "审核绩效评分")
    public Result<PerformanceEvaluationVO> approveEvaluation(
            @Parameter(description = "绩效评分ID") @PathVariable String id,
            @Valid @RequestBody PerformanceApprovalDTO dto) {
        PerformanceEvaluation evaluation = performanceEvaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("绩效评分不存在"));

        evaluation.setApproveStatus(ApprovalStatus.valueOf(dto.getApproveResult()));
        evaluation.setApproveRemark(dto.getApproveRemark());
        evaluation.setApproveTime(LocalDateTime.now());

        if ("APPROVED".equals(dto.getApproveResult())) {
            evaluation.setStatus("已审核");
        } else {
            evaluation.setStatus("已拒绝");
        }

        PerformanceEvaluation saved = performanceEvaluationRepository.save(evaluation);
        return Result.success(convertToVO(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取绩效评分详情", description = "根据ID获取绩效评分详细信息")
    public Result<PerformanceEvaluationVO> getEvaluation(@Parameter(description = "绩效评分ID") @PathVariable String id) {
        PerformanceEvaluation evaluation = performanceEvaluationRepository.findById(id)
                .filter(e -> !e.getDeleted())
                .orElseThrow(() -> new RuntimeException("绩效评分不存在"));
        return Result.success(convertToVO(evaluation));
    }

    @GetMapping
    @Operation(summary = "分页查询绩效评分", description = "分页查询绩效评分列表")
    public Result<PageResult<PerformanceEvaluationVO>> listEvaluations(
            @Parameter(description = "员工ID") @RequestParam(required = false) String employeeId,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "审批状态") @RequestParam(required = false) String approveStatus,
            @Parameter(description = "考核类型") @RequestParam(required = false) String evaluationType,
            @Parameter(description = "周期开始") @RequestParam(required = false) LocalDate periodStart,
            @Parameter(description = "周期结束") @RequestParam(required = false) LocalDate periodEnd,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("evaluateTime").descending());
        ApprovalStatus status = approveStatus != null ? ApprovalStatus.valueOf(approveStatus) : null;

        Page<PerformanceEvaluation> page = performanceEvaluationRepository.findByConditions(
                employeeId, deptId, status, evaluationType, periodStart, periodEnd, pageable);

        List<PerformanceEvaluationVO> voList = page.getContent().stream()
                .filter(e -> !e.getDeleted())
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageResult<PerformanceEvaluationVO> result = PageResult.of(voList, page.getTotalElements(), pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "根据员工查询绩效评分", description = "查询指定员工的绩效评分列表")
    public Result<List<PerformanceEvaluationVO>> listEvaluationsByEmployee(
            @Parameter(description = "员工ID") @PathVariable String employeeId) {
        List<PerformanceEvaluation> evaluations = performanceEvaluationRepository.findByEmployeeIdAndDeletedFalseOrderByPeriodStartDesc(employeeId);
        List<PerformanceEvaluationVO> voList = evaluations.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/pending/{approverId}")
    @Operation(summary = "获取待审核绩效评分", description = "查询指定审核人待审核的绩效评分")
    public Result<List<PerformanceEvaluationVO>> listPendingEvaluations(
            @Parameter(description = "审核人ID") @PathVariable String approverId) {
        List<PerformanceEvaluation> evaluations = performanceEvaluationRepository.findPendingByApprover(approverId);
        List<PerformanceEvaluationVO> voList = evaluations.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    private String generateEvaluationNo() {
        return "PE" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private BigDecimal calculateTotalScore(PerformanceEvaluationCreateDTO dto) {
        BigDecimal total = BigDecimal.ZERO;
        if (dto.getWorkloadScore() != null) total = total.add(dto.getWorkloadScore());
        if (dto.getQualityScore() != null) total = total.add(dto.getQualityScore());
        if (dto.getServiceScore() != null) total = total.add(dto.getServiceScore());
        if (dto.getAttendanceScore() != null) total = total.add(dto.getAttendanceScore());
        if (dto.getOtherScore() != null) total = total.add(dto.getOtherScore());
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private String determineLevel(BigDecimal totalScore) {
        if (totalScore == null) return "未评定";
        double score = totalScore.doubleValue();
        if (score >= 90) return "优秀";
        if (score >= 80) return "良好";
        if (score >= 60) return "合格";
        return "不合格";
    }

    private PerformanceEvaluationVO convertToVO(PerformanceEvaluation evaluation) {
        PerformanceEvaluationVO vo = new PerformanceEvaluationVO();
        vo.setId(evaluation.getId());
        vo.setEvaluationNo(evaluation.getEvaluationNo());
        vo.setEmployeeId(evaluation.getEmployeeId());
        vo.setEmployeeNo(evaluation.getEmployeeNo());
        vo.setEmployeeName(evaluation.getEmployeeName());
        vo.setDeptId(evaluation.getDeptId());
        vo.setDeptName(evaluation.getDeptName());
        vo.setPeriodStart(evaluation.getPeriodStart());
        vo.setPeriodEnd(evaluation.getPeriodEnd());
        vo.setEvaluationType(evaluation.getEvaluationType());
        vo.setTotalScore(evaluation.getTotalScore());
        vo.setLevel(evaluation.getLevel());
        vo.setWorkloadScore(evaluation.getWorkloadScore());
        vo.setQualityScore(evaluation.getQualityScore());
        vo.setServiceScore(evaluation.getServiceScore());
        vo.setAttendanceScore(evaluation.getAttendanceScore());
        vo.setOtherScore(evaluation.getOtherScore());
        vo.setEvaluatorId(evaluation.getEvaluatorId());
        vo.setEvaluatorName(evaluation.getEvaluatorName());
        vo.setEvaluateTime(evaluation.getEvaluateTime());
        vo.setComment(evaluation.getComment());
        vo.setApproverId(evaluation.getApproverId());
        vo.setApproverName(evaluation.getApproverName());
        vo.setApproveTime(evaluation.getApproveTime());
        vo.setApproveStatus(evaluation.getApproveStatus() != null ? evaluation.getApproveStatus().name() : null);
        vo.setApproveRemark(evaluation.getApproveRemark());
        vo.setStatus(evaluation.getStatus());
        vo.setCreateTime(evaluation.getCreateTime());
        vo.setUpdateTime(evaluation.getUpdateTime());
        return vo;
    }
}
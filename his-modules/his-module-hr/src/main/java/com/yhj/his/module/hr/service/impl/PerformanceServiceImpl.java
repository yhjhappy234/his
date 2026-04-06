package com.yhj.his.module.hr.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.date.DateUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Employee;
import com.yhj.his.module.hr.entity.PerformanceEvaluation;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.repository.EmployeeRepository;
import com.yhj.his.module.hr.repository.PerformanceEvaluationRepository;
import com.yhj.his.module.hr.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 绩效服务实现
 */
@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService {

    private final PerformanceEvaluationRepository evaluationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public PerformanceEvaluationVO createEvaluation(PerformanceEvaluationCreateDTO dto, String evaluatorId, String evaluatorName) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(dto.getEmployeeId())
                .orElseThrow(() -> new BusinessException("员工不存在"));

        // 检查是否已有相同周期的绩效评分
        evaluationRepository.findByEmployeeIdAndPeriodStartAndPeriodEndAndDeletedFalse(
                dto.getEmployeeId(), dto.getPeriodStart(), dto.getPeriodEnd())
                .ifPresent(e -> { throw new BusinessException("该员工该周期已有绩效评分"); });

        PerformanceEvaluation evaluation = new PerformanceEvaluation();
        BeanUtil.copyProperties(dto, evaluation);
        evaluation.setId(IdUtil.fastSimpleUUID());
        evaluation.setEvaluationNo("PE" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + IdUtil.fastSimpleUUID().substring(0, 4));
        evaluation.setEmployeeNo(employee.getEmployeeNo());
        evaluation.setEmployeeName(employee.getEmployeeName());
        evaluation.setDeptId(employee.getDeptId());
        evaluation.setDeptName(employee.getDeptName());
        evaluation.setEvaluatorId(evaluatorId);
        evaluation.setEvaluatorName(evaluatorName);
        evaluation.setEvaluateTime(LocalDateTime.now());
        evaluation.setApproveStatus(ApprovalStatus.PENDING);
        evaluation.setStatus("待审核");

        // 计算总分
        calculateTotalScoreInternal(evaluation);

        // 确定等级
        evaluation.setLevel(determinePerformanceLevel(evaluation.getTotalScore()));

        evaluation = evaluationRepository.save(evaluation);
        return convertToVO(evaluation);
    }

    @Override
    @Transactional
    public PerformanceEvaluationVO approveEvaluation(PerformanceApprovalDTO dto, String approverId, String approverName) {
        PerformanceEvaluation evaluation = evaluationRepository.findByIdAndDeletedFalse(dto.getEvaluationId())
                .orElseThrow(() -> new BusinessException("绩效评分不存在"));

        if (evaluation.getApproveStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("绩效评分已审核，不能重复审核");
        }

        ApprovalStatus approveResult = ApprovalStatus.valueOf(dto.getApproveResult());
        evaluation.setApproveStatus(approveResult);
        evaluation.setApproverId(approverId);
        evaluation.setApproverName(approverName);
        evaluation.setApproveTime(LocalDateTime.now());
        evaluation.setApproveRemark(dto.getApproveRemark());
        evaluation.setStatus(approveResult == ApprovalStatus.APPROVED ? "已审核" : "已拒绝");

        evaluation = evaluationRepository.save(evaluation);
        return convertToVO(evaluation);
    }

    @Override
    public PerformanceEvaluationVO getEvaluationById(String evaluationId) {
        PerformanceEvaluation evaluation = evaluationRepository.findByIdAndDeletedFalse(evaluationId)
                .orElseThrow(() -> new BusinessException("绩效评分不存在"));
        return convertToVO(evaluation);
    }

    @Override
    public List<PerformanceEvaluationVO> listEvaluationsByEmployee(String employeeId) {
        List<PerformanceEvaluation> list = evaluationRepository.findByEmployeeIdAndDeletedFalseOrderByPeriodStartDesc(employeeId);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<PerformanceEvaluationVO> listPendingEvaluations(String approverId) {
        List<PerformanceEvaluation> list = evaluationRepository.findPendingByApprover(approverId);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<PerformanceEvaluationVO> listEvaluations(String employeeId, String deptId, ApprovalStatus approveStatus, String evaluationType, LocalDate periodStart, LocalDate periodEnd, Integer pageNum, Integer pageSize) {
        Page<PerformanceEvaluation> page = evaluationRepository.findByConditions(employeeId, deptId, approveStatus, evaluationType, periodStart, periodEnd, PageUtils.of(pageNum, pageSize));
        List<PerformanceEvaluationVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    private void calculateTotalScoreInternal(PerformanceEvaluation evaluation) {
        BigDecimal total = BigDecimal.ZERO;

        if (evaluation.getWorkloadScore() != null) {
            total = total.add(evaluation.getWorkloadScore());
        }
        if (evaluation.getQualityScore() != null) {
            total = total.add(evaluation.getQualityScore());
        }
        if (evaluation.getServiceScore() != null) {
            total = total.add(evaluation.getServiceScore());
        }
        if (evaluation.getAttendanceScore() != null) {
            total = total.add(evaluation.getAttendanceScore());
        }
        if (evaluation.getOtherScore() != null) {
            total = total.add(evaluation.getOtherScore());
        }

        evaluation.setTotalScore(total.setScale(2, RoundingMode.HALF_UP));
    }

    @Override
    public void calculateTotalScore(String evaluationId) {
        PerformanceEvaluation evaluation = evaluationRepository.findByIdAndDeletedFalse(evaluationId)
                .orElseThrow(() -> new BusinessException("绩效评分不存在"));
        calculateTotalScoreInternal(evaluation);
        evaluation.setLevel(determinePerformanceLevel(evaluation.getTotalScore()));
        evaluationRepository.save(evaluation);
    }

    @Override
    public String determinePerformanceLevel(BigDecimal totalScore) {
        if (totalScore == null) {
            return "不合格";
        }

        double score = totalScore.doubleValue();
        if (score >= 90) {
            return "优秀";
        } else if (score >= 80) {
            return "良好";
        } else if (score >= 60) {
            return "合格";
        } else {
            return "不合格";
        }
    }

    private PerformanceEvaluationVO convertToVO(PerformanceEvaluation evaluation) {
        PerformanceEvaluationVO vo = new PerformanceEvaluationVO();
        BeanUtil.copyProperties(evaluation, vo);

        if (evaluation.getApproveStatus() != null) {
            vo.setApproveStatus(evaluation.getApproveStatus().name());
        }

        return vo;
    }
}
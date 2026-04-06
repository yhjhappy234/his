package com.yhj.his.module.hr.service.impl;

import com.yhj.his.module.hr.entity.PerformanceEvaluation;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.repository.PerformanceEvaluationRepository;
import com.yhj.his.module.hr.repository.EmployeeRepository;
import com.yhj.his.module.hr.service.PerformanceEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 绩效评分服务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceEvaluationServiceImpl implements PerformanceEvaluationService {

    private final PerformanceEvaluationRepository performanceEvaluationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public PerformanceEvaluation createPerformanceEvaluation(PerformanceEvaluation evaluation) {
        // 获取员工信息
        if (evaluation.getEmployeeId() != null) {
            employeeRepository.findById(evaluation.getEmployeeId()).ifPresent(emp -> {
                evaluation.setEmployeeNo(emp.getEmployeeNo());
                evaluation.setEmployeeName(emp.getEmployeeName());
                evaluation.setDeptId(emp.getDeptId());
                evaluation.setDeptName(emp.getDeptName());
            });
        }

        // 生成考核单号
        if (evaluation.getEvaluationNo() == null) {
            evaluation.setEvaluationNo(generateEvaluationNo());
        }

        // 设置考评时间
        evaluation.setEvaluateTime(LocalDateTime.now());

        // 计算总分
        if (evaluation.getTotalScore() == null) {
            evaluation.setTotalScore(calculateTotalScoreFromEntity(evaluation));
        }

        // 确定考核等级
        if (evaluation.getLevel() == null) {
            evaluation.setLevel(determineEvaluationLevel(evaluation.getTotalScore()));
        }

        // 默认状态
        if (evaluation.getApproveStatus() == null) {
            evaluation.setApproveStatus(ApprovalStatus.PENDING);
        }
        if (evaluation.getStatus() == null) {
            evaluation.setStatus("待审核");
        }

        return performanceEvaluationRepository.save(evaluation);
    }

    @Override
    public PerformanceEvaluation updatePerformanceEvaluation(PerformanceEvaluation evaluation) {
        PerformanceEvaluation existing = performanceEvaluationRepository.findById(evaluation.getId())
                .orElseThrow(() -> new RuntimeException("绩效评分不存在: " + evaluation.getId()));

        updateEvaluationFromEntity(evaluation, existing);
        return performanceEvaluationRepository.save(existing);
    }

    @Override
    public void deletePerformanceEvaluation(String id) {
        PerformanceEvaluation evaluation = performanceEvaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("绩效评分不存在: " + id));
        evaluation.setDeleted(true);
        performanceEvaluationRepository.save(evaluation);
    }

    @Override
    public Optional<PerformanceEvaluation> getPerformanceEvaluationById(String id) {
        return performanceEvaluationRepository.findById(id)
                .filter(e -> !e.getDeleted());
    }

    @Override
    public Optional<PerformanceEvaluation> getPerformanceEvaluationByNo(String evaluationNo) {
        return performanceEvaluationRepository.findByEvaluationNo(evaluationNo)
                .filter(e -> !e.getDeleted());
    }

    @Override
    public List<PerformanceEvaluation> getPerformanceEvaluationsByEmployeeId(String employeeId) {
        return performanceEvaluationRepository.findByEmployeeIdAndDeletedFalseOrderByPeriodStartDesc(employeeId);
    }

    @Override
    public Optional<PerformanceEvaluation> getPerformanceEvaluationByEmployeeAndPeriod(String employeeId, LocalDate periodStart, LocalDate periodEnd) {
        return performanceEvaluationRepository.findByEmployeeIdAndPeriodStartAndPeriodEndAndDeletedFalse(employeeId, periodStart, periodEnd);
    }

    @Override
    public List<PerformanceEvaluation> getPerformanceEvaluationsByApproveStatus(ApprovalStatus approveStatus) {
        return performanceEvaluationRepository.findByApproveStatusAndDeletedFalseOrderByEvaluateTimeDesc(approveStatus);
    }

    @Override
    public List<PerformanceEvaluation> getPendingPerformanceEvaluationsByApprover(String approverId) {
        return performanceEvaluationRepository.findPendingByApprover(approverId);
    }

    @Override
    public Page<PerformanceEvaluation> searchPerformanceEvaluations(String employeeId, String deptId, ApprovalStatus approveStatus, String evaluationType, LocalDate periodStart, LocalDate periodEnd, Pageable pageable) {
        return performanceEvaluationRepository.findByConditions(employeeId, deptId, approveStatus, evaluationType, periodStart, periodEnd, pageable);
    }

    @Override
    public Optional<PerformanceEvaluation> getPerformanceEvaluationByEmployeeAndTypeAndDate(String employeeId, String evaluationType, LocalDate date) {
        return performanceEvaluationRepository.findByEmployeeAndTypeAndDate(employeeId, evaluationType, date);
    }

    @Override
    public PerformanceEvaluation approvePerformanceEvaluation(String evaluationId, ApprovalStatus approveStatus, String approverId, String approverName, String approveRemark) {
        PerformanceEvaluation evaluation = performanceEvaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("绩效评分不存在: " + evaluationId));

        evaluation.setApproveStatus(approveStatus);
        evaluation.setApproverId(approverId);
        evaluation.setApproverName(approverName);
        evaluation.setApproveTime(LocalDateTime.now());
        evaluation.setApproveRemark(approveRemark);

        if (ApprovalStatus.APPROVED.equals(approveStatus)) {
            evaluation.setStatus("已审核");
        } else if (ApprovalStatus.REJECTED.equals(approveStatus)) {
            evaluation.setStatus("已拒绝");
        }

        return performanceEvaluationRepository.save(evaluation);
    }

    @Override
    public BigDecimal calculateTotalScore(String evaluationId) {
        PerformanceEvaluation evaluation = performanceEvaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("绩效评分不存在: " + evaluationId));

        BigDecimal total = calculateTotalScoreFromEntity(evaluation);
        evaluation.setTotalScore(total);
        performanceEvaluationRepository.save(evaluation);

        return total;
    }

    @Override
    public String determineEvaluationLevel(BigDecimal totalScore) {
        if (totalScore == null) {
            return "未评级";
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

    @Override
    public List<PerformanceEvaluation> batchCreatePerformanceEvaluations(List<String> employeeIds, LocalDate periodStart, LocalDate periodEnd, String evaluationType) {
        // Implementation for batch creation
        return List.of();
    }

    @Override
    public boolean existsByEvaluationNo(String evaluationNo) {
        return performanceEvaluationRepository.existsByEvaluationNo(evaluationNo);
    }

    @Override
    public String generateEvaluationNo() {
        return "PE" + System.currentTimeMillis();
    }

    @Override
    public Optional<PerformanceEvaluation> getLatestPerformanceEvaluation(String employeeId) {
        List<PerformanceEvaluation> evaluations = getPerformanceEvaluationsByEmployeeId(employeeId);
        return evaluations.isEmpty() ? Optional.empty() : Optional.of(evaluations.get(0));
    }

    private BigDecimal calculateTotalScoreFromEntity(PerformanceEvaluation evaluation) {
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

        return total;
    }

    private void updateEvaluationFromEntity(PerformanceEvaluation source, PerformanceEvaluation target) {
        if (source.getPeriodStart() != null) target.setPeriodStart(source.getPeriodStart());
        if (source.getPeriodEnd() != null) target.setPeriodEnd(source.getPeriodEnd());
        if (source.getEvaluationType() != null) target.setEvaluationType(source.getEvaluationType());
        if (source.getWorkloadScore() != null) target.setWorkloadScore(source.getWorkloadScore());
        if (source.getQualityScore() != null) target.setQualityScore(source.getQualityScore());
        if (source.getServiceScore() != null) target.setServiceScore(source.getServiceScore());
        if (source.getAttendanceScore() != null) target.setAttendanceScore(source.getAttendanceScore());
        if (source.getOtherScore() != null) target.setOtherScore(source.getOtherScore());
        if (source.getComment() != null) target.setComment(source.getComment());
        if (source.getTotalScore() != null) target.setTotalScore(source.getTotalScore());
        if (source.getLevel() != null) target.setLevel(source.getLevel());
    }
}
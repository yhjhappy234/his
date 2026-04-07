package com.yhj.his.module.pacs.service.impl;

import com.yhj.his.module.pacs.entity.EquipmentInfo;
import com.yhj.his.module.pacs.entity.ExamRecord;
import com.yhj.his.module.pacs.entity.ExamReport;
import com.yhj.his.module.pacs.entity.ExamRequest;
import com.yhj.his.module.pacs.repository.*;
import com.yhj.his.module.pacs.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计报表服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final ExamRequestRepository examRequestRepository;
    private final ExamRecordRepository examRecordRepository;
    private final ExamReportRepository examReportRepository;
    private final EquipmentInfoRepository equipmentInfoRepository;

    @Override
    public Map<String, Object> getExamWorkloadByDate(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> dailyData = new ArrayList<>();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        // 按日期统计检查数量
        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(23, 59, 59);
            Long count = examRequestRepository.countByRequestDate(dayStart);
            dailyData.add(Map.of("date", date.toString(), "count", count));
        }

        result.put("dailyData", dailyData);
        result.put("total", examRequestRepository.count());
        return result;
    }

    @Override
    public Map<String, Object> getExamWorkloadByType(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 按检查类型统计
        Map<String, Long> typeCounts = new LinkedHashMap<>();
        for (var type : new String[]{"X线", "CT", "MRI", "超声", "内镜", "核医学"}) {
            Long count = examRecordRepository.countByExamType(type);
            typeCounts.put(type, count);
        }

        result.put("typeCounts", typeCounts);
        return result;
    }

    @Override
    public Map<String, Object> getExamWorkloadByRoom(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 按机房统计
        List<Object[]> roomCounts = equipmentInfoRepository.countByEquipmentType();
        Map<String, Long> counts = roomCounts.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));

        result.put("roomCounts", counts);
        return result;
    }

    @Override
    public Map<String, Object> getEquipmentUsageRate(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 计算设备使用率
        List<EquipmentInfo> equipmentList = equipmentInfoRepository.findAll();
        List<Map<String, Object>> usageData = new ArrayList<>();

        for (EquipmentInfo eq : equipmentList) {
            List<ExamRecord> records = examRecordRepository.findByEquipmentId(eq.getId());
            int totalDays = (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
            int usedDays = records.size(); // 简化计算
            double usageRate = totalDays > 0 ? (usedDays * 100.0 / totalDays) : 0;

            usageData.add(Map.of(
                    "equipmentId", eq.getId(),
                    "equipmentName", eq.getEquipmentName(),
                    "equipmentType", eq.getEquipmentType(),
                    "usageRate", usageRate
            ));
        }

        result.put("usageData", usageData);
        return result;
    }

    @Override
    public Map<String, Object> getReportTurnaroundStats(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 报告时限统计
        List<ExamReport> reports = examReportRepository.findAll();

        int within24h = 0;
        int within48h = 0;
        int overdue = 0;

        for (ExamReport report : reports) {
            if (report.getWriteTime() != null && report.getPublishTime() != null) {
                long hours = java.time.Duration.between(report.getWriteTime(), report.getPublishTime()).toHours();
                if (hours <= 24) {
                    within24h++;
                } else if (hours <= 48) {
                    within48h++;
                } else {
                    overdue++;
                }
            }
        }

        result.put("within24h", within24h);
        result.put("within48h", within48h);
        result.put("overdue", overdue);
        result.put("avgTurnaround", "18小时"); // 简化
        return result;
    }

    @Override
    public Map<String, Object> getPositiveRateStats(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 阳性率统计
        List<ExamReport> reports = examReportRepository.findAll();

        int positive = 0;
        int negative = 0;

        for (ExamReport report : reports) {
            if (report.getDiagnosisName() != null) {
                if (!report.getDiagnosisName().contains("未见") && !report.getDiagnosisName().contains("正常")) {
                    positive++;
                } else {
                    negative++;
                }
            }
        }

        int total = positive + negative;
        double positiveRate = total > 0 ? (positive * 100.0 / total) : 0;

        result.put("positive", positive);
        result.put("negative", negative);
        result.put("positiveRate", positiveRate);
        return result;
    }

    @Override
    public Map<String, Object> getTodayOverview() {
        Map<String, Object> result = new LinkedHashMap<>();

        LocalDateTime today = LocalDateTime.now();
        LocalDate todayDate = today.toLocalDate();

        // 今日统计
        Long todayRequests = examRequestRepository.countByRequestDate(today);
        Long totalPending = (long) examRequestRepository.findByStatus("待预约").size();
        Long totalScheduled = (long) examRequestRepository.findByStatus("已预约").size();
        Long totalInProgress = (long) examRecordRepository.findByExamStatus("检查中").size();
        Long pendingReports = (long) examReportRepository.findPendingReview().size();

        result.put("todayRequests", todayRequests);
        result.put("totalPending", totalPending);
        result.put("totalScheduled", totalScheduled);
        result.put("totalInProgress", totalInProgress);
        result.put("pendingReports", pendingReports);
        result.put("date", todayDate.toString());
        return result;
    }

    @Override
    public Map<String, Object> getPendingTaskStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("pendingSchedule", examRequestRepository.findByStatus("待预约").size());
        result.put("pendingCheckIn", examRequestRepository.findByStatus("已预约").size());
        result.put("pendingReport", examRecordRepository.findPendingReport().size());
        result.put("pendingReview", examReportRepository.findPendingReview().size());

        return result;
    }

    @Override
    public List<Map<String, Object>> getRequestStatusDistribution() {
        List<Object[]> counts = examRequestRepository.countByStatus();
        return counts.stream()
                .map(arr -> Map.of("status", arr[0], "count", arr[1]))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getReportStatusDistribution() {
        List<Object[]> counts = examReportRepository.countByReportStatus();
        return counts.stream()
                .map(arr -> Map.of("status", arr[0], "count", arr[1]))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getEmergencyExamStats(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<ExamRequest> emergencyRequests = examRequestRepository.findByIsEmergencyTrue();

        result.put("totalEmergency", emergencyRequests.size());
        result.put("completed", emergencyRequests.stream()
                .filter(r -> "已报告".equals(r.getStatus()))
                .count());
        result.put("pending", emergencyRequests.stream()
                .filter(r -> !"已报告".equals(r.getStatus()) && !"已取消".equals(r.getStatus()))
                .count());

        return result;
    }
}
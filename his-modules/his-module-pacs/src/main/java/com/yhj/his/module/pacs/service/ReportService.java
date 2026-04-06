package com.yhj.his.module.pacs.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.pacs.dto.*;
import com.yhj.his.module.pacs.vo.*;

import java.util.List;

/**
 * 诊断报告服务接口
 */
public interface ReportService {

    /**
     * 创建/保存报告草稿
     */
    ExamReportVO writeReport(ReportWriteDTO dto);

    /**
     * 提交报告审核
     */
    ExamReportVO submitForReview(String reportId);

    /**
     * 审核报告
     */
    ExamReportVO reviewReport(ReportReviewDTO dto);

    /**
     * 发布报告
     */
    ExamReportVO publishReport(String reportId, String publisherId, String publisherName);

    /**
     * 打印报告(记录打印次数)
     */
    ExamReportVO printReport(String reportId);

    /**
     * 查询报告详情
     */
    ExamReportVO getReportById(String reportId);

    /**
     * 根据检查ID查询报告
     */
    ExamReportVO getReportByExamId(String examId);

    /**
     * 根据申请ID查询报告
     */
    ExamReportVO getReportByRequestId(String requestId);

    /**
     * 根据报告编号查询
     */
    ExamReportVO getReportByNo(String reportNo);

    /**
     * 查询患者报告列表
     */
    List<ExamReportVO> getReportsByPatientId(String patientId);

    /**
     * 分页查询报告
     */
    PageResult<ExamReportVO> queryReports(ReportQueryDTO queryDTO);

    /**
     * 查询待审核报告
     */
    List<ExamReportVO> getPendingReviewReports();

    /**
     * 查询我的报告(书写医生)
     */
    List<ExamReportVO> getMyReports(String writerId);

    /**
     * 查询我审核的报告(审核医生)
     */
    List<ExamReportVO> getMyReviewedReports(String reviewerId);

    /**
     * 获取报告修改历史
     */
    String getModifyHistory(String reportId);
}
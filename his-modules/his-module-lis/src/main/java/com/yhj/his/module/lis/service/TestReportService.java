package com.yhj.his.module.lis.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestReportAuditDTO;
import com.yhj.his.module.lis.dto.TestReportGenerateDTO;
import com.yhj.his.module.lis.dto.TestReportPublishDTO;
import com.yhj.his.module.lis.entity.TestReport;
import com.yhj.his.module.lis.enums.TestReportStatus;
import com.yhj.his.module.lis.vo.TestReportVO;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 检验报告服务接口
 */
public interface TestReportService {

    /**
     * 生成检验报告
     */
    TestReportVO generate(TestReportGenerateDTO dto);

    /**
     * 审核检验报告
     */
    TestReportVO audit(TestReportAuditDTO dto);

    /**
     * 发布检验报告
     */
    TestReportVO publish(TestReportPublishDTO dto);

    /**
     * 打印检验报告
     */
    TestReportVO print(String id);

    /**
     * 根据ID获取检验报告
     */
    TestReportVO getById(String id);

    /**
     * 根据报告编号获取检验报告
     */
    TestReportVO getByReportNo(String reportNo);

    /**
     * 根据申请ID获取检验报告
     */
    TestReportVO getByRequestId(String requestId);

    /**
     * 分页查询检验报告
     */
    PageResult<TestReportVO> list(Pageable pageable);

    /**
     * 根据患者ID查询检验报告
     */
    List<TestReportVO> listByPatientId(String patientId);

    /**
     * 根据状态查询检验报告
     */
    List<TestReportVO> listByStatus(TestReportStatus status);

    /**
     * 查询待审核报告
     */
    List<TestReportVO> listPendingAuditReports();

    /**
     * 查询危急值报告
     */
    List<TestReportVO> listCriticalReports();

    /**
     * 根据报告时间范围查询
     */
    PageResult<TestReportVO> listByReportTime(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 删除检验报告
     */
    void delete(String id);

    /**
     * 统计某状态的报告数量
     */
    long countByStatus(TestReportStatus status);
}
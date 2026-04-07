package com.yhj.his.module.lis.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestResultAuditDTO;
import com.yhj.his.module.lis.dto.TestResultInputDTO;
import com.yhj.his.module.lis.entity.TestResult;
import com.yhj.his.module.lis.entity.TestItem;
import com.yhj.his.module.lis.entity.TestRequest;
import com.yhj.his.module.lis.enums.ResultFlag;
import com.yhj.his.module.lis.enums.TestRequestStatus;
import com.yhj.his.module.lis.repository.TestResultRepository;
import com.yhj.his.module.lis.repository.TestItemRepository;
import com.yhj.his.module.lis.repository.TestRequestRepository;
import com.yhj.his.module.lis.service.TestResultService;
import com.yhj.his.module.lis.service.CriticalValueService;
import com.yhj.his.module.lis.vo.TestResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 检验结果服务实现
 */
@Service
@RequiredArgsConstructor
public class TestResultServiceImpl implements TestResultService {

    private final TestResultRepository testResultRepository;
    private final TestItemRepository testItemRepository;
    private final TestRequestRepository testRequestRepository;
    private final CriticalValueService criticalValueService;

    @Override
    @Transactional
    public TestResultVO input(TestResultInputDTO dto) {
        TestItem item = testItemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("检验项目不存在: " + dto.getItemId()));

        TestRequest request = testRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException("检验申请不存在: " + dto.getRequestId()));

        TestResult result = new TestResult();
        result.setRequestId(dto.getRequestId());
        result.setSampleId(dto.getSampleId());
        result.setRequestItemId(dto.getRequestItemId());
        result.setItemId(dto.getItemId());
        result.setItemCode(item.getItemCode());
        result.setItemName(item.getItemName());
        result.setUnit(item.getUnit());
        result.setTestValue(dto.getTestValue());

        // 解析数值结果
        try {
            BigDecimal numericValue = new BigDecimal(dto.getTestValue());
            result.setNumericValue(numericValue);

            // 判断结果标识
            ResultFlag flag = determineResultFlag(item, numericValue);
            result.setResultFlag(flag);
            result.setAbnormalFlag(flag != ResultFlag.NORMAL && flag != ResultFlag.POSITIVE && flag != ResultFlag.NEGATIVE);
            result.setCriticalFlag(flag == ResultFlag.CRITICAL);
        } catch (NumberFormatException e) {
            // 非数值结果
            result.setTextResult(dto.getTestValue());
        }

        result.setReferenceMin(item.getReferenceMin());
        result.setReferenceMax(item.getReferenceMax());
        result.setReferenceRange(item.getReferenceText());
        result.setInstrumentId(dto.getInstrumentId());
        result.setInstrumentName(dto.getInstrumentName());
        result.setReagentLot(dto.getReagentLot());
        result.setTestTime(dto.getTestTime() != null ? dto.getTestTime() : LocalDateTime.now());
        result.setTesterId(dto.getTesterId());
        result.setTesterName(dto.getTesterName());
        result.setRemark(dto.getRemark());

        TestResult saved = testResultRepository.save(result);

        // 检查危急值
        if (result.getCriticalFlag()) {
            TestRequest req = testRequestRepository.findById(dto.getRequestId()).orElse(null);
            if (req != null) {
                criticalValueService.create(
                        dto.getRequestId(),
                        dto.getSampleId(),
                        saved.getId(),
                        req.getPatientId(),
                        req.getPatientName(),
                        dto.getItemId(),
                        item.getItemName(),
                        dto.getTestValue(),
                        result.getResultFlag().name(),
                        item.getCriticalLow() + "-" + item.getCriticalHigh(),
                        dto.getTesterId(),
                        dto.getTesterName()
                );
            }
        }

        // 更新申请状态
        if (request.getStatus() == TestRequestStatus.RECEIVED) {
            request.setStatus(TestRequestStatus.TESTING);
            testRequestRepository.save(request);
        }

        return toVO(saved);
    }

    @Override
    @Transactional
    public TestResultVO modify(String id, String testValue, String modifyReason) {
        TestResult result = testResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验结果不存在: " + id));

        TestItem item = testItemRepository.findById(result.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("检验项目不存在: " + result.getItemId()));

        result.setTestValue(testValue);
        result.setModifyReason(modifyReason);
        result.setModifyTime(LocalDateTime.now());

        // 解析数值结果
        try {
            BigDecimal numericValue = new BigDecimal(testValue);
            result.setNumericValue(numericValue);

            ResultFlag flag = determineResultFlag(item, numericValue);
            result.setResultFlag(flag);
            result.setAbnormalFlag(flag != ResultFlag.NORMAL && flag != ResultFlag.POSITIVE && flag != ResultFlag.NEGATIVE);
            result.setCriticalFlag(flag == ResultFlag.CRITICAL);
        } catch (NumberFormatException e) {
            result.setTextResult(testValue);
        }

        TestResult saved = testResultRepository.save(result);
        return toVO(saved);
    }

    @Override
    @Transactional
    public TestResultVO audit(TestResultAuditDTO dto) {
        TestResult result = testResultRepository.findById(dto.getResultId())
                .orElseThrow(() -> new IllegalArgumentException("检验结果不存在: " + dto.getResultId()));

        result.setAuditorId(dto.getAuditorId());
        result.setAuditorName(dto.getAuditorName());
        result.setAuditTime(dto.getAuditTime() != null ? dto.getAuditTime() : LocalDateTime.now());
        result.setRemark(dto.getRemark());

        TestResult saved = testResultRepository.save(result);

        // 检查是否所有结果都已审核
        TestRequest request = testRequestRepository.findById(result.getRequestId()).orElse(null);
        if (request != null) {
            long totalItems = testResultRepository.countByRequestId(request.getId());
            long auditedItems = testResultRepository.findByRequestId(request.getId())
                    .stream()
                    .filter(r -> r.getAuditorId() != null)
                    .count();
            if (totalItems == auditedItems) {
                request.setStatus(TestRequestStatus.AUDITED);
                testRequestRepository.save(request);
            }
        }

        return toVO(saved);
    }

    @Override
    public TestResultVO getById(String id) {
        TestResult result = testResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验结果不存在: " + id));
        return toVO(result);
    }

    @Override
    public List<TestResultVO> listByRequestId(String requestId) {
        return testResultRepository.findByRequestId(requestId).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<TestResultVO> listBySampleId(String sampleId) {
        return testResultRepository.findBySampleId(sampleId).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public TestResultVO getByRequestIdAndItemId(String requestId, String itemId) {
        TestResult result = testResultRepository.findByRequestIdAndItemId(requestId, itemId)
                .orElseThrow(() -> new IllegalArgumentException("检验结果不存在"));
        return toVO(result);
    }

    @Override
    public PageResult<TestResultVO> list(Pageable pageable) {
        Page<TestResult> page = testResultRepository.findAll(pageable);
        List<TestResultVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<TestResultVO> listCriticalResults() {
        return testResultRepository.findByCriticalFlagTrue().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<TestResultVO> listAbnormalResults() {
        return testResultRepository.findByAbnormalFlagTrue().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<TestResultVO> listPendingAuditResults() {
        return testResultRepository.findPendingAuditResults().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<TestResultVO> listByTestTime(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Page<TestResult> page = testResultRepository.findByTestTimeBetween(startTime, endTime, pageable);
        List<TestResultVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<TestResultVO> listHistoryResults(String patientId, String itemId) {
        return testResultRepository.findHistoryResultsByPatientIdAndItemId(patientId, itemId).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(String id) {
        TestResult result = testResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验结果不存在: " + id));
        result.setDeleted(true);
        testResultRepository.save(result);
    }

    @Override
    public long countByRequestId(String requestId) {
        return testResultRepository.countByRequestId(requestId);
    }

    @Override
    public boolean checkCriticalValue(String itemId, String testValue) {
        TestItem item = testItemRepository.findById(itemId).orElse(null);
        if (item == null || !item.getCritical() || item.getCriticalLow() == null || item.getCriticalHigh() == null) {
            return false;
        }
        try {
            BigDecimal value = new BigDecimal(testValue);
            return value.compareTo(item.getCriticalLow()) < 0 || value.compareTo(item.getCriticalHigh()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private ResultFlag determineResultFlag(TestItem item, BigDecimal value) {
        if (item.getCritical() && item.getCriticalLow() != null && item.getCriticalHigh() != null) {
            if (value.compareTo(item.getCriticalLow()) < 0 || value.compareTo(item.getCriticalHigh()) > 0) {
                return ResultFlag.CRITICAL;
            }
        }
        if (item.getReferenceMin() != null && item.getReferenceMax() != null) {
            if (value.compareTo(item.getReferenceMin()) < 0) {
                return ResultFlag.LOW;
            }
            if (value.compareTo(item.getReferenceMax()) > 0) {
                return ResultFlag.HIGH;
            }
        }
        return ResultFlag.NORMAL;
    }

    private TestResultVO toVO(TestResult result) {
        TestResultVO vo = new TestResultVO();
        vo.setId(result.getId());
        vo.setRequestId(result.getRequestId());

        TestRequest request = testRequestRepository.findById(result.getRequestId()).orElse(null);
        if (request != null) {
            vo.setRequestNo(request.getRequestNo());
        }

        vo.setSampleId(result.getSampleId());
        vo.setRequestItemId(result.getRequestItemId());
        vo.setItemId(result.getItemId());
        vo.setItemCode(result.getItemCode());
        vo.setItemName(result.getItemName());
        vo.setUnit(result.getUnit());
        vo.setTestValue(result.getTestValue());
        vo.setNumericValue(result.getNumericValue());
        vo.setTextResult(result.getTextResult());
        vo.setResultFlag(result.getResultFlag() != null ? result.getResultFlag().name() : null);
        vo.setResultFlagDesc(result.getResultFlag() != null ? result.getResultFlag().getDescription() : null);
        vo.setAbnormalFlag(result.getAbnormalFlag());
        vo.setCriticalFlag(result.getCriticalFlag());
        vo.setReferenceMin(result.getReferenceMin());
        vo.setReferenceMax(result.getReferenceMax());
        vo.setReferenceRange(result.getReferenceRange());
        vo.setInstrumentId(result.getInstrumentId());
        vo.setInstrumentName(result.getInstrumentName());
        vo.setReagentLot(result.getReagentLot());
        vo.setTestTime(result.getTestTime());
        vo.setTesterId(result.getTesterId());
        vo.setTesterName(result.getTesterName());
        vo.setAuditTime(result.getAuditTime());
        vo.setAuditorId(result.getAuditorId());
        vo.setAuditorName(result.getAuditorName());
        vo.setModifyReason(result.getModifyReason());
        vo.setModifyTime(result.getModifyTime());
        vo.setRemark(result.getRemark());
        vo.setCreateTime(result.getCreateTime());
        vo.setUpdateTime(result.getUpdateTime());
        return vo;
    }
}
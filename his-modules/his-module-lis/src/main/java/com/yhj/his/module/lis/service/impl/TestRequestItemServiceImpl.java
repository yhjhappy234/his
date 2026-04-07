package com.yhj.his.module.lis.service.impl;

import com.yhj.his.module.lis.entity.TestRequestItem;
import com.yhj.his.module.lis.repository.TestRequestItemRepository;
import com.yhj.his.module.lis.service.TestRequestItemService;
import com.yhj.his.module.lis.vo.TestRequestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 检验申请明细服务实现
 */
@Service
@RequiredArgsConstructor
public class TestRequestItemServiceImpl implements TestRequestItemService {

    private final TestRequestItemRepository testRequestItemRepository;

    @Override
    public List<TestRequestVO.TestRequestItemVO> listByRequestId(String requestId) {
        return testRequestItemRepository.findByRequestId(requestId).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TestRequestVO.TestRequestItemVO> listBySampleId(String sampleId) {
        return testRequestItemRepository.findBySampleId(sampleId).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSampleId(String id, String sampleId) {
        TestRequestItem item = testRequestItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申请明细不存在: " + id));
        item.setSampleId(sampleId);
        testRequestItemRepository.save(item);
    }

    @Override
    @Transactional
    public void updateResultStatus(String id, String resultStatus) {
        TestRequestItem item = testRequestItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申请明细不存在: " + id));
        item.setResultStatus(resultStatus);
        testRequestItemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteByRequestId(String requestId) {
        testRequestItemRepository.deleteByRequestId(requestId);
    }

    @Override
    public long countByRequestId(String requestId) {
        return testRequestItemRepository.countByRequestId(requestId);
    }

    private TestRequestVO.TestRequestItemVO toVO(TestRequestItem item) {
        TestRequestVO.TestRequestItemVO vo = new TestRequestVO.TestRequestItemVO();
        vo.setId(item.getId());
        vo.setItemId(item.getItemId());
        vo.setItemCode(item.getItemCode());
        vo.setItemName(item.getItemName());
        vo.setSpecimenType(item.getSpecimenType() != null ? item.getSpecimenType().name() : null);
        vo.setSpecimenTypeDesc(item.getSpecimenType() != null ? item.getSpecimenType().getDescription() : null);
        vo.setPrice(item.getPrice());
        vo.setSampleId(item.getSampleId());
        vo.setResultStatus(item.getResultStatus());
        vo.setRemark(item.getRemark());
        return vo;
    }
}
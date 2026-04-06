package com.yhj.his.module.lis.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestRequestCreateDTO;
import com.yhj.his.module.lis.entity.TestRequest;
import com.yhj.his.module.lis.entity.TestRequestItem;
import com.yhj.his.module.lis.entity.TestItem;
import com.yhj.his.module.lis.enums.TestRequestStatus;
import com.yhj.his.module.lis.enums.VisitType;
import com.yhj.his.module.lis.repository.TestRequestRepository;
import com.yhj.his.module.lis.repository.TestRequestItemRepository;
import com.yhj.his.module.lis.repository.TestItemRepository;
import com.yhj.his.module.lis.service.TestRequestService;
import com.yhj.his.module.lis.service.TestRequestItemService;
import com.yhj.his.module.lis.vo.TestRequestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 检验申请服务实现
 */
@Service
@RequiredArgsConstructor
public class TestRequestServiceImpl implements TestRequestService {

    private final TestRequestRepository testRequestRepository;
    private final TestRequestItemRepository testRequestItemRepository;
    private final TestItemRepository testItemRepository;
    private final TestRequestItemService testRequestItemService;

    @Override
    @Transactional
    public TestRequestVO create(TestRequestCreateDTO dto) {
        // 生成申请单号
        String requestNo = generateRequestNo();

        TestRequest request = new TestRequest();
        request.setRequestNo(requestNo);
        request.setPatientId(dto.getPatientId());
        request.setPatientName(dto.getPatientName());
        request.setGender(dto.getGender());
        request.setAge(dto.getAge());
        request.setIdCardNo(dto.getIdCardNo());
        request.setVisitType(VisitType.valueOf(dto.getVisitType()));
        request.setVisitId(dto.getVisitId());
        request.setAdmissionId(dto.getAdmissionId());
        request.setDeptId(dto.getDeptId());
        request.setDeptName(dto.getDeptName());
        request.setDoctorId(dto.getDoctorId());
        request.setDoctorName(dto.getDoctorName());
        request.setClinicalDiagnosis(dto.getClinicalDiagnosis());
        request.setClinicalInfo(dto.getClinicalInfo());
        request.setRequestTime(LocalDateTime.now());
        request.setEmergency(dto.getEmergency() != null ? dto.getEmergency() : false);
        request.setEmergencyLevel(dto.getEmergencyLevel());
        request.setStatus(TestRequestStatus.REQUESTED);
        request.setRemark(dto.getRemark());

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<TestRequestItem> items = new ArrayList<>();

        for (TestRequestCreateDTO.TestRequestItemDTO itemDTO : dto.getItems()) {
            TestItem testItem = testItemRepository.findById(itemDTO.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("检验项目不存在: " + itemDTO.getItemId()));

            TestRequestItem item = new TestRequestItem();
            item.setRequestId(request.getId());
            item.setItemId(itemDTO.getItemId());
            item.setItemCode(testItem.getItemCode());
            item.setItemName(testItem.getItemName());
            item.setSpecimenType(testItem.getSpecimenType());
            item.setPrice(testItem.getPrice());
            items.add(item);

            if (testItem.getPrice() != null) {
                totalAmount = totalAmount.add(testItem.getPrice());
            }
        }

        request.setTotalAmount(totalAmount);
        TestRequest saved = testRequestRepository.save(request);

        // 保存申请明细
        for (TestRequestItem item : items) {
            item.setRequestId(saved.getId());
            testRequestItemRepository.save(item);
        }

        return toVO(saved, items);
    }

    @Override
    @Transactional
    public TestRequestVO cancel(String id, String cancelReason, String cancelUserId) {
        TestRequest request = testRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验申请不存在: " + id));

        if (request.getStatus() == TestRequestStatus.PUBLISHED) {
            throw new IllegalArgumentException("已发布的申请不能取消");
        }

        request.setStatus(TestRequestStatus.CANCELLED);
        request.setCancelReason(cancelReason);
        request.setCancelTime(LocalDateTime.now());
        request.setCancelUserId(cancelUserId);

        TestRequest saved = testRequestRepository.save(request);
        List<TestRequestItem> items = testRequestItemRepository.findByRequestId(saved.getId());
        return toVO(saved, items);
    }

    @Override
    public TestRequestVO getById(String id) {
        TestRequest request = testRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验申请不存在: " + id));
        List<TestRequestItem> items = testRequestItemRepository.findByRequestId(id);
        return toVO(request, items);
    }

    @Override
    public TestRequestVO getByRequestNo(String requestNo) {
        TestRequest request = testRequestRepository.findByRequestNo(requestNo)
                .orElseThrow(() -> new IllegalArgumentException("检验申请不存在: " + requestNo));
        List<TestRequestItem> items = testRequestItemRepository.findByRequestId(request.getId());
        return toVO(request, items);
    }

    @Override
    public PageResult<TestRequestVO> list(Pageable pageable) {
        Page<TestRequest> page = testRequestRepository.findAll(pageable);
        List<TestRequestVO> list = page.getContent().stream()
                .map(r -> {
                    List<TestRequestItem> items = testRequestItemRepository.findByRequestId(r.getId());
                    return toVO(r, items);
                })
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<TestRequestVO> listByPatientId(String patientId) {
        return testRequestRepository.findByPatientId(patientId).stream()
                .map(r -> {
                    List<TestRequestItem> items = testRequestItemRepository.findByRequestId(r.getId());
                    return toVO(r, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TestRequestVO> listByStatus(TestRequestStatus status) {
        return testRequestRepository.findByStatus(status).stream()
                .map(r -> {
                    List<TestRequestItem> items = testRequestItemRepository.findByRequestId(r.getId());
                    return toVO(r, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TestRequestVO> listByVisitId(String visitId) {
        return testRequestRepository.findByVisitId(visitId).stream()
                .map(r -> {
                    List<TestRequestItem> items = testRequestItemRepository.findByRequestId(r.getId());
                    return toVO(r, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TestRequestVO> listEmergencyRequests() {
        List<TestRequestStatus> excludedStatuses = List.of(TestRequestStatus.PUBLISHED, TestRequestStatus.CANCELLED);
        return testRequestRepository.findEmergencyRequests(excludedStatuses).stream()
                .map(r -> {
                    List<TestRequestItem> items = testRequestItemRepository.findByRequestId(r.getId());
                    return toVO(r, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<TestRequestVO> listByDeptAndTime(String deptId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Page<TestRequest> page = testRequestRepository.findByDeptIdAndRequestTimeBetween(deptId, startTime, endTime, pageable);
        List<TestRequestVO> list = page.getContent().stream()
                .map(r -> {
                    List<TestRequestItem> items = testRequestItemRepository.findByRequestId(r.getId());
                    return toVO(r, items);
                })
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    @Transactional
    public TestRequestVO updateStatus(String id, TestRequestStatus status) {
        TestRequest request = testRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验申请不存在: " + id));
        request.setStatus(status);
        TestRequest saved = testRequestRepository.save(request);
        List<TestRequestItem> items = testRequestItemRepository.findByRequestId(saved.getId());
        return toVO(saved, items);
    }

    @Override
    @Transactional
    public void delete(String id) {
        TestRequest request = testRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验申请不存在: " + id));
        request.setDeleted(true);
        testRequestRepository.save(request);
        testRequestItemRepository.deleteByRequestId(id);
    }

    @Override
    public long countByStatus(TestRequestStatus status) {
        return testRequestRepository.countByStatus(status);
    }

    private String generateRequestNo() {
        return "LIS" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private TestRequestVO toVO(TestRequest request, List<TestRequestItem> items) {
        TestRequestVO vo = new TestRequestVO();
        vo.setId(request.getId());
        vo.setRequestNo(request.getRequestNo());
        vo.setPatientId(request.getPatientId());
        vo.setPatientName(request.getPatientName());
        vo.setGender(request.getGender());
        vo.setAge(request.getAge());
        vo.setIdCardNo(request.getIdCardNo());
        vo.setVisitType(request.getVisitType().name());
        vo.setVisitTypeDesc(request.getVisitType().getDescription());
        vo.setVisitId(request.getVisitId());
        vo.setAdmissionId(request.getAdmissionId());
        vo.setDeptId(request.getDeptId());
        vo.setDeptName(request.getDeptName());
        vo.setDoctorId(request.getDoctorId());
        vo.setDoctorName(request.getDoctorName());
        vo.setClinicalDiagnosis(request.getClinicalDiagnosis());
        vo.setClinicalInfo(request.getClinicalInfo());
        vo.setRequestTime(request.getRequestTime());
        vo.setEmergency(request.getEmergency());
        vo.setEmergencyLevel(request.getEmergencyLevel());
        vo.setSampleStatus(request.getSampleStatus());
        vo.setReportStatus(request.getReportStatus());
        vo.setTotalAmount(request.getTotalAmount());
        vo.setPayStatus(request.getPayStatus());
        vo.setStatus(request.getStatus().name());
        vo.setStatusDesc(request.getStatus().getDescription());
        vo.setRemark(request.getRemark());
        vo.setCreateTime(request.getCreateTime());

        List<TestRequestVO.TestRequestItemVO> itemVOs = items.stream().map(this::toItemVO).collect(Collectors.toList());
        vo.setItems(itemVOs);

        return vo;
    }

    private TestRequestVO.TestRequestItemVO toItemVO(TestRequestItem item) {
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
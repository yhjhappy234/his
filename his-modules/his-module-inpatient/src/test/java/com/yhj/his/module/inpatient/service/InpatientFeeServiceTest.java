package com.yhj.his.module.inpatient.service;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.inpatient.entity.InpatientAdmission;
import com.yhj.his.module.inpatient.entity.InpatientFee;
import com.yhj.his.module.inpatient.enums.*;
import com.yhj.his.module.inpatient.repository.InpatientAdmissionRepository;
import com.yhj.his.module.inpatient.repository.InpatientFeeRepository;
import com.yhj.his.module.inpatient.service.impl.InpatientFeeServiceImpl;
import com.yhj.his.module.inpatient.vo.InpatientFeeSummaryVO;
import com.yhj.his.module.inpatient.vo.InpatientFeeVO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 住院费用服务单元测试
 *
 * 测试覆盖范围：
 * - 费用明细查询
 * - 费用汇总计算
 * - 分页查询
 * - 未结算费用查询
 * - 每日费用查询
 */
@ExtendWith(MockitoExtension.class)
class InpatientFeeServiceTest {

    @Mock
    private InpatientFeeRepository feeRepository;

    @Mock
    private InpatientAdmissionRepository admissionRepository;

    @InjectMocks
    private InpatientFeeServiceImpl feeService;

    // ==================== 费用明细查询测试 ====================

    @Test
    @DisplayName("查询费用明细 - 成功查询")
    void testListByAdmission_Success() {
        // 准备数据
        List<InpatientFee> fees = new ArrayList<>();
        fees.add(createFee(FeeCategory.BED, new BigDecimal("50.00")));
        fees.add(createFee(FeeCategory.DRUG, new BigDecimal("120.00")));
        fees.add(createFee(FeeCategory.EXAMINATION, new BigDecimal("200.00")));

        when(feeRepository.findByAdmissionIdOrderByDate("admission-001")).thenReturn(fees);

        // 执行
        List<InpatientFeeVO> result = feeService.listByAdmission("admission-001");

        // 验证
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("床位", result.get(0).getFeeCategory());
        assertEquals("药品", result.get(1).getFeeCategory());
        assertEquals("检查", result.get(2).getFeeCategory());
    }

    @Test
    @DisplayName("查询费用明细 - 无费用记录")
    void testListByAdmission_Empty() {
        when(feeRepository.findByAdmissionIdOrderByDate("admission-001")).thenReturn(Collections.emptyList());

        // 执行
        List<InpatientFeeVO> result = feeService.listByAdmission("admission-001");

        // 验证
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询费用明细 - 包含多种费用分类")
    void testListByAdmission_MultipleCategories() {
        // 准备数据
        List<InpatientFee> fees = new ArrayList<>();
        fees.add(createFee(FeeCategory.BED, new BigDecimal("50.00")));
        fees.add(createFee(FeeCategory.DRUG, new BigDecimal("150.00")));
        fees.add(createFee(FeeCategory.EXAMINATION, new BigDecimal("300.00")));
        fees.add(createFee(FeeCategory.LAB_TEST, new BigDecimal("80.00")));
        fees.add(createFee(FeeCategory.TREATMENT, new BigDecimal("100.00")));
        fees.add(createFee(FeeCategory.NURSING, new BigDecimal("30.00")));
        fees.add(createFee(FeeCategory.MATERIAL, new BigDecimal("25.00")));

        when(feeRepository.findByAdmissionIdOrderByDate("admission-001")).thenReturn(fees);

        // 执行
        List<InpatientFeeVO> result = feeService.listByAdmission("admission-001");

        // 验证
        assertEquals(7, result.size());
        // 验证各分类的费用
        assertTrue(result.stream().anyMatch(f -> "床位".equals(f.getFeeCategory())));
        assertTrue(result.stream().anyMatch(f -> "药品".equals(f.getFeeCategory())));
        assertTrue(result.stream().anyMatch(f -> "检查".equals(f.getFeeCategory())));
        assertTrue(result.stream().anyMatch(f -> "检验".equals(f.getFeeCategory())));
        assertTrue(result.stream().anyMatch(f -> "治疗".equals(f.getFeeCategory())));
        assertTrue(result.stream().anyMatch(f -> "护理".equals(f.getFeeCategory())));
        assertTrue(result.stream().anyMatch(f -> "材料".equals(f.getFeeCategory())));
    }

    // ==================== 费用汇总测试 ====================

    @Test
    @DisplayName("查询费用汇总 - 成功汇总")
    void testGetSummary_Success() {
        // 准备数据
        InpatientAdmission admission = createInpatientAdmission();
        admission.setDeposit(new BigDecimal("2000.00"));
        admission.setSettledCost(new BigDecimal("500.00"));

        when(admissionRepository.findById("admission-001")).thenReturn(Optional.of(admission));
        when(feeRepository.sumFeeAmountByAdmissionId("admission-001")).thenReturn(new BigDecimal("1500.00"));
        when(feeRepository.sumUnsettledFeeByAdmissionId("admission-001")).thenReturn(new BigDecimal("1000.00"));

        // 各分类费用汇总
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.BED))
                .thenReturn(new BigDecimal("350.00"));
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.DRUG))
                .thenReturn(new BigDecimal("500.00"));
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.EXAMINATION))
                .thenReturn(new BigDecimal("200.00"));
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.LAB_TEST))
                .thenReturn(new BigDecimal("150.00"));
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.TREATMENT))
                .thenReturn(new BigDecimal("180.00"));
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.NURSING))
                .thenReturn(new BigDecimal("70.00"));
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.MATERIAL))
                .thenReturn(new BigDecimal("50.00"));

        // 执行
        InpatientFeeSummaryVO result = feeService.getSummary("admission-001");

        // 验证
        assertNotNull(result);
        assertEquals("admission-001", result.getAdmissionId());
        assertEquals("ZY20260406001", result.getAdmissionNo());
        assertEquals("张三", result.getPatientName());
        assertEquals(new BigDecimal("1500.00"), result.getTotalCost());
        assertEquals(new BigDecimal("2000.00"), result.getDeposit());
        assertEquals(new BigDecimal("1000.00"), result.getUnsettledAmount());
        assertEquals(new BigDecimal("500.00"), result.getSettledAmount());

        // 验证各分类费用
        assertEquals(new BigDecimal("350.00"), result.getBedFee());
        assertEquals(new BigDecimal("500.00"), result.getDrugFee());
        assertEquals(new BigDecimal("200.00"), result.getExaminationFee());
        assertEquals(new BigDecimal("150.00"), result.getLabTestFee());
        assertEquals(new BigDecimal("180.00"), result.getTreatmentFee());
        assertEquals(new BigDecimal("70.00"), result.getNursingFee());
        assertEquals(new BigDecimal("50.00"), result.getMaterialFee());
    }

    @Test
    @DisplayName("查询费用汇总 - 无费用时返回零")
    void testGetSummary_ZeroFees() {
        // 准备数据
        InpatientAdmission admission = createInpatientAdmission();
        // deposit有默认值BigDecimal.ZERO，settledCost无默认值
        admission.setDeposit(BigDecimal.ZERO);
        admission.setSettledCost(null);

        when(admissionRepository.findById("admission-001")).thenReturn(Optional.of(admission));
        when(feeRepository.sumFeeAmountByAdmissionId("admission-001")).thenReturn(null);
        when(feeRepository.sumUnsettledFeeByAdmissionId("admission-001")).thenReturn(null);

        // 各分类费用返回null
        for (FeeCategory category : FeeCategory.values()) {
            when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", category))
                    .thenReturn(null);
        }

        // 执行
        InpatientFeeSummaryVO result = feeService.getSummary("admission-001");

        // 验证
        assertEquals(BigDecimal.ZERO, result.getTotalCost());
        assertEquals(BigDecimal.ZERO, result.getUnsettledAmount());
        // deposit有默认值BigDecimal.ZERO
        assertEquals(BigDecimal.ZERO, result.getDeposit());
        // settledCost为null时，VO中也是null
        assertNull(result.getSettledAmount());
        assertEquals(BigDecimal.ZERO, result.getBedFee());
        assertEquals(BigDecimal.ZERO, result.getDrugFee());
    }

    @Test
    @DisplayName("查询费用汇总 - 住院记录不存在")
    void testGetSummary_AdmissionNotFound() {
        when(admissionRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> feeService.getSummary("invalid-id"));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("住院记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("查询费用汇总 - 费用分类汇总准确性")
    void testGetSummary_CategoryAccuracy() {
        // 准备数据
        InpatientAdmission admission = createInpatientAdmission();
        admission.setDeposit(new BigDecimal("5000.00"));

        when(admissionRepository.findById("admission-001")).thenReturn(Optional.of(admission));
        when(feeRepository.sumFeeAmountByAdmissionId("admission-001")).thenReturn(new BigDecimal("3500.00"));
        when(feeRepository.sumUnsettledFeeByAdmissionId("admission-001")).thenReturn(new BigDecimal("3500.00"));

        // 总费用3500，分布在各分类
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.BED))
                .thenReturn(new BigDecimal("700.00")); // 7天床位费
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.DRUG))
                .thenReturn(new BigDecimal("1500.00")); // 药品费最多
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.EXAMINATION))
                .thenReturn(new BigDecimal("500.00"));
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.LAB_TEST))
                .thenReturn(new BigDecimal("300.00"));
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.TREATMENT))
                .thenReturn(new BigDecimal("250.00"));
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.NURSING))
                .thenReturn(new BigDecimal("150.00"));
        when(feeRepository.sumFeeAmountByAdmissionIdAndCategory("admission-001", FeeCategory.MATERIAL))
                .thenReturn(new BigDecimal("100.00"));

        // 执行
        InpatientFeeSummaryVO result = feeService.getSummary("admission-001");

        // 验证费用结构
        BigDecimal categoryTotal = result.getBedFee()
                .add(result.getDrugFee())
                .add(result.getExaminationFee())
                .add(result.getLabTestFee())
                .add(result.getTreatmentFee())
                .add(result.getNursingFee())
                .add(result.getMaterialFee());

        // 注意：实际业务中分类费用之和可能等于或小于总费用
        assertEquals(new BigDecimal("700.00"), result.getBedFee());
        assertEquals(new BigDecimal("1500.00"), result.getDrugFee());
    }

    // ==================== 分页查询测试 ====================

    @Test
    @DisplayName("分页查询费用 - 成功分页")
    void testPageFees_Success() {
        // 准备数据
        List<InpatientFee> fees = new ArrayList<>();
        fees.add(createFee(FeeCategory.DRUG, new BigDecimal("50.00")));
        fees.add(createFee(FeeCategory.DRUG, new BigDecimal("80.00")));

        Page<InpatientFee> page = new PageImpl<>(fees);
        when(feeRepository.findByAdmissionId(eq("admission-001"), any(PageRequest.class))).thenReturn(page);

        // 执行
        PageResult<InpatientFeeVO> result = feeService.page(1, 10, "admission-001");

        // 验证
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
    }

    @Test
    @DisplayName("分页查询费用 - 空结果")
    void testPageFees_Empty() {
        Page<InpatientFee> emptyPage = new PageImpl<>(Collections.emptyList());
        when(feeRepository.findByAdmissionId(eq("admission-001"), any(PageRequest.class))).thenReturn(emptyPage);

        // 执行
        PageResult<InpatientFeeVO> result = feeService.page(1, 10, "admission-001");

        // 验证
        assertEquals(0, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("分页查询费用 - 大量数据分页")
    void testPageFees_LargeData() {
        // 准备数据 - 25条记录，分3页
        List<InpatientFee> fees = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            fees.add(createFee(FeeCategory.DRUG, new BigDecimal("10.00")));
        }

        Page<InpatientFee> page = new PageImpl<>(fees, PageRequest.of(0, 10), 25);
        when(feeRepository.findByAdmissionId(eq("admission-001"), any(PageRequest.class))).thenReturn(page);

        // 执行
        PageResult<InpatientFeeVO> result = feeService.page(1, 10, "admission-001");

        // 验证
        assertEquals(25, result.getTotal());
        assertEquals(10, result.getList().size());
        assertEquals(3, result.getPages()); // 总页数
    }

    // ==================== 未结算费用查询测试 ====================

    @Test
    @DisplayName("查询未结算费用 - 有未结算费用")
    void testGetUnsettledAmount_HasUnsettled() {
        when(feeRepository.sumUnsettledFeeByAdmissionId("admission-001"))
                .thenReturn(new BigDecimal("1500.00"));

        // 执行
        BigDecimal result = feeService.getUnsettledAmount("admission-001");

        // 验证
        assertEquals(new BigDecimal("1500.00"), result);
    }

    @Test
    @DisplayName("查询未结算费用 - 无未结算费用")
    void testGetUnsettledAmount_NoUnsettled() {
        when(feeRepository.sumUnsettledFeeByAdmissionId("admission-001")).thenReturn(null);

        // 执行
        BigDecimal result = feeService.getUnsettledAmount("admission-001");

        // 验证
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("查询未结算费用 - 部分已结算")
    void testGetUnsettledAmount_PartiallySettled() {
        // 准备数据 - 总费用2000，已结算1000，未结算1000
        when(feeRepository.sumUnsettledFeeByAdmissionId("admission-001"))
                .thenReturn(new BigDecimal("1000.00"));

        // 执行
        BigDecimal result = feeService.getUnsettledAmount("admission-001");

        // 验证
        assertEquals(new BigDecimal("1000.00"), result);
    }

    // ==================== 每日费用查询测试 ====================

    @Test
    @DisplayName("查询每日费用 - 成功查询指定日期费用")
    void testListByDate_Success() {
        // 准备数据
        List<InpatientFee> fees = new ArrayList<>();
        InpatientFee fee1 = createFee(FeeCategory.BED, new BigDecimal("50.00"));
        fee1.setFeeDate(LocalDate.of(2026, 4, 6));
        fees.add(fee1);

        InpatientFee fee2 = createFee(FeeCategory.DRUG, new BigDecimal("120.00"));
        fee2.setFeeDate(LocalDate.of(2026, 4, 6));
        fees.add(fee2);

        when(feeRepository.findByAdmissionIdAndFeeDate("admission-001", LocalDate.of(2026, 4, 6)))
                .thenReturn(fees);

        // 执行
        List<InpatientFeeVO> result = feeService.listByDate("admission-001", "2026-04-06");

        // 验证
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("2026-04-06", result.get(0).getFeeDate());
    }

    @Test
    @DisplayName("查询每日费用 - 指定日期无费用")
    void testListByDate_NoFeesOnDate() {
        when(feeRepository.findByAdmissionIdAndFeeDate("admission-001", LocalDate.of(2026, 4, 5)))
                .thenReturn(Collections.emptyList());

        // 执行
        List<InpatientFeeVO> result = feeService.listByDate("admission-001", "2026-04-05");

        // 验证
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询每日费用 - 跨多天费用")
    void testListByDate_MultipleDays() {
        // 准备第一天费用
        List<InpatientFee> day1Fees = new ArrayList<>();
        InpatientFee fee1 = createFee(FeeCategory.BED, new BigDecimal("50.00"));
        fee1.setFeeDate(LocalDate.of(2026, 4, 5));
        day1Fees.add(fee1);

        // 准备第二天费用
        List<InpatientFee> day2Fees = new ArrayList<>();
        InpatientFee fee2 = createFee(FeeCategory.DRUG, new BigDecimal("150.00"));
        fee2.setFeeDate(LocalDate.of(2026, 4, 6));
        day2Fees.add(fee2);

        when(feeRepository.findByAdmissionIdAndFeeDate("admission-001", LocalDate.of(2026, 4, 5)))
                .thenReturn(day1Fees);
        when(feeRepository.findByAdmissionIdAndFeeDate("admission-001", LocalDate.of(2026, 4, 6)))
                .thenReturn(day2Fees);

        // 执行两天的查询
        List<InpatientFeeVO> resultDay1 = feeService.listByDate("admission-001", "2026-04-05");
        List<InpatientFeeVO> resultDay2 = feeService.listByDate("admission-001", "2026-04-06");

        // 验证
        assertEquals(1, resultDay1.size());
        assertEquals("2026-04-05", resultDay1.get(0).getFeeDate());
        assertEquals(new BigDecimal("50.00"), resultDay1.get(0).getFeeAmount());

        assertEquals(1, resultDay2.size());
        assertEquals("2026-04-06", resultDay2.get(0).getFeeDate());
        assertEquals(new BigDecimal("150.00"), resultDay2.get(0).getFeeAmount());
    }

    // ==================== VO转换测试 ====================

    @Test
    @DisplayName("VO转换 - 药品费用转换")
    void testConvertToVO_DrugFee() {
        // 准备数据
        InpatientFee fee = createFee(FeeCategory.DRUG, new BigDecimal("50.00"));
        fee.setFeeItemCode("D001");
        fee.setFeeItemName("阿司匹林肠溶片");
        fee.setFeeSpec("100mg");
        fee.setFeeUnit("片");
        fee.setFeePrice(new BigDecimal("1.00"));
        fee.setFeeQuantity(new BigDecimal("50"));
        fee.setIsInsurance(true);
        fee.setPayStatus(PayStatus.UNSETTLED);
        fee.setOrderNo("MO20260406001");

        List<InpatientFee> fees = Collections.singletonList(fee);
        when(feeRepository.findByAdmissionIdOrderByDate("admission-001")).thenReturn(fees);

        // 执行
        List<InpatientFeeVO> result = feeService.listByAdmission("admission-001");

        // 验证
        InpatientFeeVO vo = result.get(0);
        assertEquals("D001", vo.getFeeItemCode());
        assertEquals("阿司匹林肠溶片", vo.getFeeItemName());
        assertEquals("100mg", vo.getFeeSpec());
        assertEquals("片", vo.getFeeUnit());
        assertEquals(new BigDecimal("1.00"), vo.getFeePrice());
        assertEquals(new BigDecimal("50"), vo.getFeeQuantity());
        assertEquals(new BigDecimal("50.00"), vo.getFeeAmount());
        assertTrue(vo.getIsInsurance());
        assertEquals("未结算", vo.getPayStatus());
        assertEquals("MO20260406001", vo.getOrderNo());
    }

    @Test
    @DisplayName("VO转换 - 检查费用转换")
    void testConvertToVO_ExaminationFee() {
        // 准备数据
        InpatientFee fee = createFee(FeeCategory.EXAMINATION, new BigDecimal("200.00"));
        fee.setFeeItemCode("E001");
        fee.setFeeItemName("胸部CT");
        fee.setFeePrice(new BigDecimal("200.00"));
        fee.setFeeQuantity(new BigDecimal("1"));
        fee.setDeptName("影像科");
        fee.setIsInsurance(true);
        fee.setPayStatus(PayStatus.SETTLED);

        List<InpatientFee> fees = Collections.singletonList(fee);
        when(feeRepository.findByAdmissionIdOrderByDate("admission-001")).thenReturn(fees);

        // 执行
        List<InpatientFeeVO> result = feeService.listByAdmission("admission-001");

        // 验证
        InpatientFeeVO vo = result.get(0);
        assertEquals("检查", vo.getFeeCategory());
        assertEquals("胸部CT", vo.getFeeItemName());
        assertEquals("影像科", vo.getDeptName());
        assertEquals("已结算", vo.getPayStatus());
    }

    @Test
    @DisplayName("VO转换 - 非医保费用")
    void testConvertToVO_NonInsuranceFee() {
        // 准备数据
        InpatientFee fee = createFee(FeeCategory.MATERIAL, new BigDecimal("100.00"));
        fee.setFeeItemName("自费材料");
        fee.setIsInsurance(false);

        List<InpatientFee> fees = Collections.singletonList(fee);
        when(feeRepository.findByAdmissionIdOrderByDate("admission-001")).thenReturn(fees);

        // 执行
        List<InpatientFeeVO> result = feeService.listByAdmission("admission-001");

        // 验证
        assertFalse(result.get(0).getIsInsurance());
    }

    // ==================== 辅助方法 ====================

    private InpatientAdmission createInpatientAdmission() {
        InpatientAdmission admission = new InpatientAdmission();
        admission.setId("admission-001");
        admission.setAdmissionNo("ZY20260406001");
        admission.setPatientId("P001");
        admission.setPatientName("张三");
        admission.setDeptId("D001");
        admission.setDeptName("内科");
        admission.setStatus(AdmissionStatus.IN_HOSPITAL);
        return admission;
    }

    private InpatientFee createFee(FeeCategory category, BigDecimal amount) {
        InpatientFee fee = new InpatientFee();
        fee.setId("fee-" + UUID.randomUUID().toString().substring(0, 8));
        fee.setAdmissionId("admission-001");
        fee.setPatientId("P001");
        fee.setFeeDate(LocalDate.now());
        fee.setFeeCategory(category);
        fee.setFeeItemCode(category.getCode() + "001");
        fee.setFeeItemName(category.getDescription());
        fee.setFeePrice(amount);
        fee.setFeeQuantity(BigDecimal.ONE);
        fee.setFeeAmount(amount);
        fee.setIsInsurance(true);
        fee.setPayStatus(PayStatus.UNSETTLED);
        fee.setDeptName("内科");
        return fee;
    }
}
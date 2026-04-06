package com.yhj.his.module.finance.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.finance.dto.InvoiceVoidDTO;
import com.yhj.his.module.finance.service.InvoiceService;
import com.yhj.his.module.finance.vo.InvoiceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 发票管理控制器
 */
@RestController
@RequestMapping("/api/finance/v1/invoice")
@RequiredArgsConstructor
@Tag(name = "发票管理", description = "发票开具、作废、查询、打印等")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/create")
    @Operation(summary = "开具发票")
    public Result<InvoiceVO> createInvoice(
            @Parameter(description = "收费ID") @RequestParam String billingId,
            @Parameter(description = "收费类型") @RequestParam String billingType) {
        // TODO: 从上下文获取当前操作员信息
        String operatorId = "OPERATOR001";
        String operatorName = "收费员";
        return Result.success("开具成功", invoiceService.createInvoice(billingId, billingType, operatorId, operatorName));
    }

    @PostMapping("/void")
    @Operation(summary = "作废发票")
    public Result<InvoiceVO> voidInvoice(@Valid @RequestBody InvoiceVoidDTO dto) {
        // TODO: 从上下文获取当前操作员信息
        String operatorId = "OPERATOR001";
        return Result.success("作废成功", invoiceService.voidInvoice(dto, operatorId));
    }

    @PostMapping("/print/{invoiceNo}")
    @Operation(summary = "打印发票")
    public Result<InvoiceVO> printInvoice(@Parameter(description = "发票号") @PathVariable String invoiceNo) {
        return Result.success(invoiceService.printInvoice(invoiceNo));
    }

    @PostMapping("/reprint/{invoiceNo}")
    @Operation(summary = "重打发票")
    public Result<InvoiceVO> reprintInvoice(@Parameter(description = "发票号") @PathVariable String invoiceNo) {
        // TODO: 从上下文获取当前操作员信息
        String operatorId = "OPERATOR001";
        return Result.success(invoiceService.reprintInvoice(invoiceNo, operatorId));
    }

    @GetMapping("/{invoiceNo}")
    @Operation(summary = "根据发票号查询")
    public Result<InvoiceVO> getByInvoiceNo(@Parameter(description = "发票号") @PathVariable String invoiceNo) {
        return Result.success(invoiceService.getByInvoiceNo(invoiceNo));
    }

    @GetMapping("/billing/{billingId}")
    @Operation(summary = "根据收费ID查询发票")
    public Result<InvoiceVO> getByBillingId(@Parameter(description = "收费ID") @PathVariable String billingId) {
        return Result.success(invoiceService.getByBillingId(billingId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "根据患者ID查询发票列表")
    public Result<List<InvoiceVO>> listByPatientId(@Parameter(description = "患者ID") @PathVariable String patientId) {
        return Result.success(invoiceService.listByPatientId(patientId));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询发票")
    public Result<PageResult<InvoiceVO>> pageList(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "发票日期") @RequestParam(required = false) String invoiceDate,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(invoiceService.pageList(patientId, invoiceDate, status, pageNum, pageSize));
    }

    @GetMapping("/next-no")
    @Operation(summary = "获取下一个发票号")
    public Result<String> getNextInvoiceNo() {
        return Result.success(invoiceService.getNextInvoiceNo());
    }

    @PostMapping("/electronic")
    @Operation(summary = "生成电子发票")
    public Result<InvoiceVO> generateElectronicInvoice(
            @Parameter(description = "收费ID") @RequestParam String billingId,
            @Parameter(description = "收费类型") @RequestParam String billingType) {
        return Result.success("生成成功", invoiceService.generateElectronicInvoice(billingId, billingType));
    }
}
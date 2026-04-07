package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.finance.dto.InvoiceVoidDTO;
import com.yhj.his.module.finance.vo.InvoiceVO;

import java.util.List;

/**
 * 发票服务接口
 */
public interface InvoiceService {

    /**
     * 开具发票
     *
     * @param billingId 收费ID
     * @param billingType 收费类型
     * @param operatorId 操作员ID
     * @param operatorName 操作员姓名
     * @return 发票信息
     */
    InvoiceVO createInvoice(String billingId, String billingType, String operatorId, String operatorName);

    /**
     * 作废发票
     */
    InvoiceVO voidInvoice(InvoiceVoidDTO dto, String operatorId);

    /**
     * 打印发票
     */
    InvoiceVO printInvoice(String invoiceNo);

    /**
     * 重打发票
     */
    InvoiceVO reprintInvoice(String invoiceNo, String operatorId);

    /**
     * 根据发票号查询
     */
    InvoiceVO getByInvoiceNo(String invoiceNo);

    /**
     * 根据收费ID查询
     */
    InvoiceVO getByBillingId(String billingId);

    /**
     * 根据患者ID查询发票列表
     */
    List<InvoiceVO> listByPatientId(String patientId);

    /**
     * 分页查询发票
     */
    PageResult<InvoiceVO> pageList(String patientId, String invoiceDate, String status, int pageNum, int pageSize);

    /**
     * 获取下一个发票号
     */
    String getNextInvoiceNo();

    /**
     * 生成电子发票
     */
    InvoiceVO generateElectronicInvoice(String billingId, String billingType);
}
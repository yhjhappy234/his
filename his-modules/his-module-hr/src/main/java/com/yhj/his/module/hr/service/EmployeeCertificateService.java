package com.yhj.his.module.hr.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.EmployeeCertificate;

import java.util.List;

/**
 * 员工证件服务接口
 */
public interface EmployeeCertificateService {

    /**
     * 创建证件
     */
    EmployeeCertificateVO createCertificate(EmployeeCertificateCreateDTO dto);

    /**
     * 更新证件
     */
    EmployeeCertificateVO updateCertificate(String certId, EmployeeCertificateCreateDTO dto);

    /**
     * 删除证件
     */
    void deleteCertificate(String certId);

    /**
     * 获取证件详情
     */
    EmployeeCertificateVO getCertificateById(String certId);

    /**
     * 根据员工ID获取证件列表
     */
    List<EmployeeCertificateVO> listCertificatesByEmployee(String employeeId);

    /**
     * 分页查询证件
     */
    PageResult<EmployeeCertificateVO> listCertificates(String employeeId, String certType, String status, Integer pageNum, Integer pageSize);

    /**
     * 获取即将过期的证件
     */
    List<EmployeeCertificateVO> listExpiringCertificates();
}
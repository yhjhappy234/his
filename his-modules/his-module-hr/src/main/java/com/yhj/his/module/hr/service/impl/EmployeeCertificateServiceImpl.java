package com.yhj.his.module.hr.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Employee;
import com.yhj.his.module.hr.entity.EmployeeCertificate;
import com.yhj.his.module.hr.repository.EmployeeCertificateRepository;
import com.yhj.his.module.hr.repository.EmployeeRepository;
import com.yhj.his.module.hr.service.EmployeeCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 员工证件服务实现
 */
@Service
@RequiredArgsConstructor
public class EmployeeCertificateServiceImpl implements EmployeeCertificateService {

    private final EmployeeCertificateRepository certificateRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public EmployeeCertificateVO createCertificate(EmployeeCertificateCreateDTO dto) {
        // 检查员工是否存在
        Employee employee = employeeRepository.findByIdAndDeletedFalse(dto.getEmployeeId())
                .orElseThrow(() -> new BusinessException("员工不存在"));

        // 检查证件编号是否重复
        if (dto.getCertNo() != null && certificateRepository.existsByCertNo(dto.getCertNo())) {
            throw new BusinessException("证件编号已存在: " + dto.getCertNo());
        }

        EmployeeCertificate cert = new EmployeeCertificate();
        BeanUtil.copyProperties(dto, cert);
        cert.setId(IdUtil.fastSimpleUUID());
        cert.setEmployeeNo(employee.getEmployeeNo());
        cert.setEmployeeName(employee.getEmployeeName());

        // 检查是否过期
        if (cert.getValidEndDate() != null && cert.getValidEndDate().isBefore(LocalDate.now())) {
            cert.setStatus("过期");
        } else if (cert.getStatus() == null) {
            cert.setStatus("有效");
        }

        cert = certificateRepository.save(cert);
        return convertToVO(cert);
    }

    @Override
    @Transactional
    public EmployeeCertificateVO updateCertificate(String certId, EmployeeCertificateCreateDTO dto) {
        EmployeeCertificate cert = certificateRepository.findByIdAndDeletedFalse(certId)
                .orElseThrow(() -> new BusinessException("证件不存在"));

        BeanUtil.copyProperties(dto, cert, "id", "employeeId", "createTime", "deleted");

        // 检查是否过期
        if (cert.getValidEndDate() != null && cert.getValidEndDate().isBefore(LocalDate.now())) {
            cert.setStatus("过期");
        }

        cert = certificateRepository.save(cert);
        return convertToVO(cert);
    }

    @Override
    @Transactional
    public void deleteCertificate(String certId) {
        EmployeeCertificate cert = certificateRepository.findByIdAndDeletedFalse(certId)
                .orElseThrow(() -> new BusinessException("证件不存在"));

        cert.setDeleted(true);
        certificateRepository.save(cert);
    }

    @Override
    public EmployeeCertificateVO getCertificateById(String certId) {
        EmployeeCertificate cert = certificateRepository.findByIdAndDeletedFalse(certId)
                .orElseThrow(() -> new BusinessException("证件不存在"));
        return convertToVO(cert);
    }

    @Override
    public List<EmployeeCertificateVO> listCertificatesByEmployee(String employeeId) {
        List<EmployeeCertificate> certs = certificateRepository.findByEmployeeIdAndDeletedFalse(employeeId);
        return certs.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<EmployeeCertificateVO> listCertificates(String employeeId, String certType, String status, Integer pageNum, Integer pageSize) {
        Page<EmployeeCertificate> page = certificateRepository.findByConditions(employeeId, certType, status, PageUtils.of(pageNum, pageSize));
        List<EmployeeCertificateVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public List<EmployeeCertificateVO> listExpiringCertificates() {
        List<EmployeeCertificate> certs = certificateRepository.findExpiringCertificates();
        return certs.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private EmployeeCertificateVO convertToVO(EmployeeCertificate cert) {
        EmployeeCertificateVO vo = new EmployeeCertificateVO();
        BeanUtil.copyProperties(cert, vo);
        return vo;
    }
}
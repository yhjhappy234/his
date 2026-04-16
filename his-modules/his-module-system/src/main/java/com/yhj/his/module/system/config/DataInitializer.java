package com.yhj.his.module.system.config;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.yhj.his.module.system.entity.Department;
import com.yhj.his.module.system.entity.Role;
import com.yhj.his.module.system.entity.User;
import com.yhj.his.module.system.entity.UserRole;
import com.yhj.his.module.system.enums.DataScopeLevel;
import com.yhj.his.module.system.enums.UserStatus;
import com.yhj.his.module.system.repository.DepartmentRepository;
import com.yhj.his.module.system.repository.RoleRepository;
import com.yhj.his.module.system.repository.UserRepository;
import com.yhj.his.module.system.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 数据初始化器
 * 应用启动时自动初始化默认用户、角色和基础数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRoleRepository userRoleRepository;

    @Value("${his.default.username:admin}")
    private String defaultUsername;

    @Value("${his.default.password:123456}")
    private String defaultPassword;

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始数据初始化...");

        try {
            initDefaultData();
            log.info("数据初始化完成");
        } catch (Exception e) {
            log.error("数据初始化失败: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void initDefaultData() {
        // 初始化默认科室
        initDefaultDepartments();

        // 初始化默认角色
        initDefaultRoles();

        // 初始化默认用户
        initDefaultUser();
    }

    /**
     * 初始化默认科室
     */
    private void initDefaultDepartments() {
        // 系统管理部
        if (!departmentRepository.existsByDeptCodeAndDeletedFalse("SYS")) {
            Department adminDept = new Department();
            adminDept.setId("dept-admin");
            adminDept.setDeptCode("SYS");
            adminDept.setDeptName("系统管理部");
            adminDept.setShortName("管理部");
            adminDept.setDeptLevel(1);
            adminDept.setDeptType("ADMIN");
            adminDept.setStatus("NORMAL");
            adminDept.setCreateTime(LocalDateTime.now());
            adminDept.setDeleted(false);
            departmentRepository.save(adminDept);
            log.info("创建默认科室: 系统管理部");
        }

        // 内科
        if (!departmentRepository.existsByDeptCodeAndDeletedFalse("IM")) {
            Department imDept = new Department();
            imDept.setId("dept-im");
            imDept.setDeptCode("IM");
            imDept.setDeptName("内科");
            imDept.setShortName("内科");
            imDept.setDeptLevel(1);
            imDept.setDeptType("CLINICAL");
            imDept.setStatus("NORMAL");
            imDept.setCreateTime(LocalDateTime.now());
            imDept.setDeleted(false);
            departmentRepository.save(imDept);
            log.info("创建默认科室: 内科");
        }
    }

    /**
     * 初始化默认角色
     */
    private void initDefaultRoles() {
        // 系统管理员角色
        if (!roleRepository.existsByRoleCodeAndDeletedFalse("ADMIN")) {
            Role adminRole = new Role();
            adminRole.setId("role-admin-001");
            adminRole.setRoleCode("ADMIN");
            adminRole.setRoleName("系统管理员");
            adminRole.setDescription("系统管理员角色，拥有所有权限");
            adminRole.setDataScope(DataScopeLevel.ALL);
            adminRole.setSortOrder(1);
            adminRole.setIsSystem(true);
            adminRole.setStatus("NORMAL");
            adminRole.setCreateTime(LocalDateTime.now());
            adminRole.setDeleted(false);
            roleRepository.save(adminRole);
            log.info("创建默认角色: 系统管理员");
        }

        // 医生角色
        if (!roleRepository.existsByRoleCodeAndDeletedFalse("DOCTOR")) {
            Role doctorRole = new Role();
            doctorRole.setId("role-doctor-001");
            doctorRole.setRoleCode("DOCTOR");
            doctorRole.setRoleName("医生");
            doctorRole.setDescription("医生角色");
            doctorRole.setDataScope(DataScopeLevel.DEPARTMENT);
            doctorRole.setSortOrder(2);
            doctorRole.setIsSystem(false);
            doctorRole.setStatus("NORMAL");
            doctorRole.setCreateTime(LocalDateTime.now());
            doctorRole.setDeleted(false);
            roleRepository.save(doctorRole);
            log.info("创建默认角色: 医生");
        }
    }

    /**
     * 初始化默认用户
     */
    private void initDefaultUser() {
        if (!userRepository.existsByLoginNameAndDeletedFalse(defaultUsername)) {
            // 创建默认管理员用户
            User adminUser = new User();
            adminUser.setId("user-admin-001");
            adminUser.setUserName("系统管理员");
            adminUser.setLoginName(defaultUsername);
            adminUser.setPassword(BCrypt.hashpw(defaultPassword));
            adminUser.setRealName("系统管理员");
            adminUser.setDeptId("dept-admin");
            adminUser.setDeptName("系统管理部");
            adminUser.setUserType("ADMIN");
            adminUser.setLoginType("PASSWORD");
            adminUser.setSessionTimeout(60);
            adminUser.setStatus(UserStatus.NORMAL);
            adminUser.setDataScope(DataScopeLevel.ALL);
            adminUser.setPasswordUpdateTime(LocalDateTime.now());
            adminUser.setCreateTime(LocalDateTime.now());
            adminUser.setDeleted(false);
            userRepository.save(adminUser);
            log.info("创建默认用户: {}, 密码: {}", defaultUsername, defaultPassword);

            // 分配管理员角色
            Role adminRole = roleRepository.findByRoleCodeAndDeletedFalse("ADMIN").orElse(null);
            if (adminRole != null) {
                UserRole userRole = new UserRole();
                userRole.setId(IdUtil.fastUUID());
                userRole.setUserId(adminUser.getId());
                userRole.setRoleId(adminRole.getId());
                userRole.setCreateTime(LocalDateTime.now());
                userRole.setDeleted(false);
                userRoleRepository.save(userRole);
                log.info("分配管理员角色给默认用户");
            }
        } else {
            log.info("默认用户 {} 已存在", defaultUsername);
        }
    }
}
# HIS 医院信息系统

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 项目简介

HIS（Hospital Information System）医院信息系统是一个新一代智慧医院综合管理平台，采用微服务架构设计，涵盖医院日常运营的各个核心业务领域。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 开发语言 |
| Spring Boot | 3.2.5 | 基础框架 |
| SQLite | 3.45.2.0 | 嵌入式数据库 |
| Caffeine | 3.1.8 | 本地缓存 |
| SpringDoc | 2.5.0 | API 文档 |
| Lombok | 1.18.30 | 代码简化 |
| MapStruct | 1.5.5 | 对象映射 |
| Hutool | 5.8.26 | 工具类库 |
| Guava | 33.0.0 | Google 工具库 |
| JJWT | 0.12.5 | JWT 认证 |

## 项目结构

```
his/
├── his-api/                    # API 接口定义层
│   ├── his-api-emr/           # 电子病历 API
│   ├── his-api-finance/       # 财务管理 API
│   ├── his-api-hr/            # 人力资源 API
│   ├── his-api-inpatient/     # 住院管理 API
│   ├── his-api-inventory/     # 库存管理 API
│   ├── his-api-lis/           # 实验室信息系统 API
│   ├── his-api-outpatient/    # 门诊管理 API
│   ├── his-api-pacs/          # 影像归档系统 API
│   ├── his-api-pharmacy/      # 药房管理 API
│   ├── his-api-system/        # 系统管理 API
│   └── his-api-voice/         # 语音识别 API
├── his-common/                 # 公共模块层
│   ├── his-common-cache/      # 缓存模块
│   ├── his-common-core/       # 核心模块（基础实体、异常处理、工具类）
│   ├── his-common-log/        # 日志模块
│   ├── his-common-security/   # 安全认证模块
│   └── his-common-voice/      # 语音处理模块
├── his-gateway/                # API 网关
├── his-modules/                # 业务模块层
│   ├── his-module-emr/        # 电子病历模块
│   ├── his-module-finance/    # 财务管理模块
│   ├── his-module-hr/         # 人力资源管理模块
│   ├── his-module-inpatient/  # 住院管理模块
│   ├── his-module-inventory/  # 库存管理模块
│   ├── his-module-lis/        # 实验室信息系统模块
│   ├── his-module-outpatient/ # 门诊管理模块
│   ├── his-module-pacs/       # 影像归档与通信系统模块
│   ├── his-module-pharmacy/   # 药房管理模块
│   ├── his-module-system/     # 系统管理模块
│   └── his-module-voice/      # 语音识别模块
├── his-starter/                # 启动模块
│   └── his-starter-all/       # 整合启动器
├── data/                       # 数据文件
│   └── init_global.sql        # 数据库初始化脚本
├── prd/                        # 产品需求文档
│   ├── prd.md                 # 产品需求总览
│   ├── quality_constraints.md # 质量约束
│   ├── emr.md                 # 电子病历需求
│   ├── finance.md             # 财务管理需求
│   ├── hr.md                  # 人力资源需求
│   ├── inpatient.md           # 住院管理需求
│   ├── inventory.md           # 库存管理需求
│   ├── lis.md                 # 实验室信息系统需求
│   ├── outpatient.md          # 门诊管理需求
│   ├── pacs.md                # 影像系统需求
│   ├── pharmacy.md            # 药房管理需求
│   ├── system.md              # 系统管理需求
│   └── voice.md               # 语音识别需求
├── scripts/                    # 脚本工具
│   ├── e2e_test.py            # 端到端测试脚本
│   ├── report_generator.py    # 报告生成器
│   ├── run_tests.sh           # 测试运行脚本
│   └── requirements.txt       # Python 依赖
├── report/                     # 报告目录
└── .github/                    # GitHub 配置
    └── workflows/             # CI/CD 工作流
        └── ci-cd.yml          # 持续集成配置
```

## 核心功能模块

### 业务模块

| 模块 | 说明 |
|------|------|
| **门诊管理** (Outpatient) | 挂号、预约、就诊、处方、收费 |
| **住院管理** (Inpatient) | 入院、床位、护理、出院结算 |
| **电子病历** (EMR) | 病历书写、模板管理、病历归档 |
| **药房管理** (Pharmacy) | 药品库存、发药、退药、盘点 |
| **财务管理** (Finance) | 费用结算、报表、成本核算 |
| **实验室信息** (LIS) | 检验申请、结果录入、报告生成 |
| **影像系统** (PACS) | 影像存储、查看、诊断报告 |
| **库存管理** (Inventory) | 物资采购、入库、出库、盘点 |
| **人力资源** (HR) | 员工档案、排班、考勤、薪资 |
| **语音识别** (Voice) | 语音录入、语音转文字 |

### 支撑模块

| 模块 | 说明 |
|------|------|
| **系统管理** (System) | 用户、角色、权限、机构管理 |
| **API 网关** (Gateway) | 统一入口、路由、限流、鉴权 |

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- Python 3.8+ (用于测试脚本)

### 构建项目

```bash
# 克隆项目
git clone https://github.com/yhjhappy234/his.git
cd his

# 编译项目
mvn clean install -DskipTests

# 运行测试
mvn test
```

### 运行项目

```bash
# 进入启动模块
cd his-starter/his-starter-all

# 启动应用
mvn spring-boot:run
```

### 默认账户

系统启动时自动创建默认管理员账户，账户信息通过配置文件设置：
```yaml
his:
  default:
    username: <通过配置设置>
    password: <通过配置设置>
  timezone: Asia/Shanghai
```

> 首次登录后建议立即修改密码

### 数据库初始化

```bash
# 执行初始化脚本
sqlite3 his.db < data/init_global.sql
```

## 用户登录管理

### 功能特性

- JWT Token认证
- BCrypt密码加密
- 账号锁定机制（连续5次密码错误）
- 自动解锁（锁定30分钟后）
- 会话超时控制
- 完整的审计日志记录
- **时区统一处理**（默认Asia/Shanghai）

### 时区配置

系统所有日期时间统一使用配置时区：
- 默认时区: `Asia/Shanghai`
- 配置项: `his.timezone`
- 所有审计日志、操作日志自动记录时区时间

### 审计日志

所有关键操作自动记录审计日志：
- 用户登录/退出审计
- 权限变更审计
- 数据修改/删除审计
- 系统配置变更审计
- 安全事件审计

### API接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/system/v1/user/login` | POST | 用户登录 |
| `/api/system/v1/user/logout` | POST | 用户退出 |
| `/api/system/v1/user/{userId}` | GET | 获取用户详情 |
| `/api/system/v1/user/page` | GET | 分页查询用户 |
| `/api/system/v1/user/password/change` | POST | 修改密码 |

## API 文档

启动应用后访问 Swagger UI：

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 代码规范

- **包命名**: 所有代码使用 `com.yhj.his` 作为包前缀
- **代码覆盖率**: 单元测试代码覆盖率要求 ≥ 90%
- **分支覆盖率**: 分支覆盖率要求 ≥ 90%
- **当前覆盖**: 服务层指令覆盖率83%、分支覆盖率72%
- **测试用例**: 189+单元测试，全部通过

## 最近更新

### 2026-04-17
- ✅ 用户登录管理功能（JWT认证、账号锁定、密码策略）
- ✅ 默认数据初始化（admin账户、ADMIN/DOCTOR角色、默认科室）
- ✅ 时区统一配置（默认Asia/Shanghai）
- ✅ 审计日志模块完整实现
- ✅ 解决多模块Bean名称冲突问题

## CI/CD

项目使用 GitHub Actions 进行持续集成和持续部署：

- **构建**: 每次提交触发 Maven 构建
- **测试**: 自动运行单元测试和集成测试
- **覆盖率检查**: 自动检查代码覆盖率是否达标

## 文档资源

- [产品需求总览](prd/prd.md)
- [质量约束说明](prd/quality_constraints.md)
- [互联网医院相关参考](internet.md)

## 许可证

本项目采用 MIT 许可证。

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request
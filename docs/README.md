# HIS 医院信息系统文档中心

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06  
> **技术栈**: Spring Boot 3.2.x + JDK 21 + Maven 3.9.x + SQLite3

---

## 文档导航

本文档中心提供 HIS 医院信息系统的完整技术文档，涵盖架构设计、API 接口、开发指南等内容。

### 架构文档

| 文档 | 描述 |
|------|------|
| [系统整体架构设计](./architecture/system-overview.md) | 系统总体架构、技术选型、双模式部署说明 |
| [模块详细设计](./architecture/module-design.md) | 各业务模块的功能设计、数据实体、业务流程 |
| [数据库设计](./architecture/database-design.md) | SQLite 数据库架构、表结构设计、数据规范 |
| [部署架构](./architecture/deployment.md) | Windows 部署方案、微服务/单体部署配置 |

### API 文档

| 文档 | 描述 |
|------|------|
| [API 文档索引](./api/README.md) | 所有模块 API 接口汇总 |
| [系统管理模块接口](./api/system-api.md) | 用户认证、权限管理、系统配置接口 |
| [门诊管理模块接口](./api/outpatient-api.md) | 预约挂号、就诊、处方、收费接口 |
| [住院管理模块接口](./api/inpatient-api.md) | 入院、床位、医嘱、出院接口 |
| [药房管理模块接口](./api/pharmacy-api.md) | 药品管理、库存、发药接口 |

### 开发文档

| 文档 | 描述 |
|------|------|
| [开发环境搭建指南](./development/getting-started.md) | 环境配置、项目构建、本地运行 |
| [编码规范](./development/coding-standards.md) | Java 代码规范、命名规范、分层架构 |
| [测试编写指南](./development/testing-guide.md) | 单元测试、E2E 测试、覆盖率要求 |

### 变更记录

| 文档 | 描述 |
|------|------|
| [版本变更记录](./changelog/CHANGELOG.md) | 版本发布历史、功能变更、问题修复 |

---

## 系统概述

### 项目简介

HIS 医院信息系统是新一代智慧医院核心支撑平台，覆盖门诊、住院、药房、检验、影像、财务等全业务流程。

### 核心特性

- **全面性**: 覆盖医院全业务流程
- **先进性**: 采用微服务架构，支持双模式部署
- **集成性**: 支持医保、支付、政务等外部系统互联
- **安全性**: 满足等保三级安全要求
- **智能化**: 支持 AI 辅助诊断、智能预警
- **跨平台**: Web 技术实现跨平台兼容

### 模块清单

| 序号 | 模块名称 | 英文名称 | 核心功能 |
|------|----------|----------|----------|
| 1 | 门诊管理 | Outpatient | 预约挂号、门诊就诊、处方开立、门诊收费 |
| 2 | 住院管理 | Inpatient | 入院登记、床位管理、医嘱管理、出院结算 |
| 3 | 药房管理 | Pharmacy | 药品管理、库存管理、发药管理、用药审核 |
| 4 | 电子病历 | EMR | 病历书写、病历模板、病历质控、病历归档 |
| 5 | 检验管理 | LIS | 检验申请、样本管理、检验执行、报告管理 |
| 6 | 影像管理 | PACS | 检查预约、影像采集、影像浏览、诊断报告 |
| 7 | 财务收费 | Finance | 价表管理、费用结算、医保结算、发票管理 |
| 8 | 库存物资 | Inventory | 物资管理、出入库管理、库存盘点、采购管理 |
| 9 | 人力资源管理 | HR | 员工管理、排班管理、考勤管理、权限管理 |
| 10 | 系统管理与安全 | System | 用户管理、权限管理、日志审计、系统配置 |
| 11 | 语音呼叫 | Voice | 叫号播报、报告通知、寻人广播、全员通知 |

---

## 快速开始

### 环境要求

- JDK 21 LTS
- Maven 3.9.x
- SQLite3
- Windows 10/11/Server 2016+ (推荐)

### 本地运行

```bash
# 克隆项目
git clone https://github.com/your-org/his.git

# 构建项目
mvn clean install

# 单体模式启动
java -jar his-starter/his-starter-all/target/his-starter-all.jar

# 访问系统
http://localhost:8080
```

详细配置请参考 [开发环境搭建指南](./development/getting-started.md)。

---

## 相关资源

- **需求文档**: `/prd/` 目录下的各模块 PRD 文档
- **质量规范**: `/prd/quality_constraints.md` 测试质量约束
- **项目结构**: 参见 [系统整体架构设计](./architecture/system-overview.md)

---

**文档维护**: HIS Platform Team
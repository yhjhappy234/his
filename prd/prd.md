# HIS医院信息系统需求说明书

> **版本**: v1.2.0  
> **最后更新**: 2024-01-15  
> **适用标准**: 国家卫健委《医院信息互联互通标准化成熟度测评》《电子病历系统应用水平分级评价》《网络安全等级保护2.0(三级)》
> **部署平台**: Windows 10/11/Server 2016+ 优先支持
> **数据库**: SQLite3 (微服务独立数据库 + 全局数据库)
> **技术栈**: Spring Boot 3.2.x + JDK 21 + Maven 3.9.x

---

## 零、全局技术约束（强制执行）

> ⚠️ **重要**: 以下约束为所有模块开发必须遵循的强制规范，任何代码生成都必须符合以下要求。

### 0.1 核心技术栈

```yaml
后端技术栈 (强制):
  语言: Java 21 (LTS版本)
  框架: Spring Boot 3.2.x (最新稳定版)
  构建: Maven 3.9.x
  ORM: Spring Data JPA + Hibernate
  数据库: SQLite3 (通过sqlite-jdbc驱动)
  缓存: Spring Cache + Caffeine
  安全: Spring Security 6.x
  API文档: SpringDoc OpenAPI 3 (Swagger 3)
  日志: SLF4J + Logback
  测试: JUnit 5 + Mockito

前端技术栈:
  框架: Vue 3.4.x + Vite 5.x
  UI组件: Element Plus 2.x
  状态管理: Pinia 2.x
  通信: Axios + WebSocket
  构建: Vite 5.x

版本约束:
  JDK: 必须使用JDK 21 LTS
  Spring Boot: 必须使用3.2.x及以上版本
  Maven: 必须使用3.9.x版本
  禁止使用: 已废弃的API、过时的依赖版本
```

### 0.2 双模式部署架构

```yaml
部署模式:
  模式一: 微服务架构 (生产部署)
    用途: 实际医院生产环境部署
    特点:
      - 各模块独立服务进程
      - 模块间通过HTTP/REST通信
      - 支持分布式部署
      - 支持负载均衡
      - 支持独立扩展
    适用场景:
      - 大中型医院
      - 需要高可用场景
      - 多服务器部署

  模式二: 单体架构 (快速部署)
    用途: 小范围部署、招投标演示、快速POC
    特点:
      - 单一进程启动所有模块
      - 共享数据库连接
      - 模块内调用，无网络开销
      - 部署简单，资源占用低
      - 启动快速，便于演示
    适用场景:
      - 小型医院/诊所
      - 招投标演示
      - 功能验证测试
      - 开发调试环境

功能要求:
  - 两种模式功能完全一致
  - 相同代码库，不同启动方式
  - 配置文件区分部署模式
  - 无需修改代码即可切换模式
```

### 0.3 项目结构规范

```yaml
Maven项目结构:
  his-parent/                    # 父工程
  ├── pom.xml                    # 父POM，管理依赖版本
  ├── his-common/                # 公共模块
  │   ├── his-common-core/       # 核心工具类
  │   ├── his-common-security/   # 安全认证
  │   ├── his-common-redis/      # 缓存工具
  │   └── his-common-voice/      # 语音服务
  ├── his-api/                   # API接口定义
  │   ├── his-api-outpatient/    # 门诊API
  │   ├── his-api-inpatient/     # 住院API
  │   └── ...
  ├── his-modules/               # 业务模块
  │   ├── his-module-outpatient/ # 门诊模块
  │   ├── his-module-inpatient/  # 住院模块
  │   ├── his-module-pharmacy/   # 药房模块
  │   ├── his-module-lis/        # 检验模块
  │   ├── his-module-pacs/       # 影像模块
  │   ├── his-module-finance/    # 财务模块
  │   ├── his-module-inventory/  # 库存模块
  │   ├── his-module-hr/         # 人事模块
  │   ├── his-module-emr/        # 病历模块
  │   ├── his-module-voice/      # 语音模块
  │   └── his-module-system/     # 系统模块
  ├── his-gateway/               # API网关(微服务模式)
  ├── his-admin/                 # 管理后台
  ├── his-web/                   # Web前端
  └── his-starter/               # 单体应用启动器
      └── his-starter-all/       # 单体应用入口

模块依赖原则:
  - 模块间通过API接口通信，禁止直接依赖实现类
  - 公共代码抽取到his-common模块
  - 接口定义放在his-api模块
  - 业务实现在his-modules模块
```

### 0.4 代码规范

```yaml
Java代码规范:
  包命名: com.yhj.his.{module}.{layer}
  类命名: PascalCase (如 PatientService)
  方法命名: camelCase (如 getPatientById)
  常量命名: UPPER_SNAKE_CASE (如 MAX_RETRY_COUNT)
  
分层架构:
  controller:  控制层，处理HTTP请求
  service:     服务层，业务逻辑
  repository:  数据访问层，数据库操作
  entity:      实体类，数据库映射
  dto:         数据传输对象
  vo:          视图对象
  config:      配置类
  util:        工具类

注解规范:
  - 所有API使用@RestController
  - 使用@RequestMapping定义路径前缀
  - 使用@Validated进行参数校验
  - 使用@ApiOperation定义接口文档
  - 使用@Transactional管理事务

异常处理:
  - 统一异常处理器 GlobalExceptionHandler
  - 自定义业务异常 BusinessException
  - 错误码统一管理 ErrorCode
  - 统一响应格式 Result<T>
```

### 0.5 配置规范

```yaml
配置文件:
  application.yml:      主配置文件
  application-dev.yml:  开发环境配置
  application-prod.yml: 生产环境配置
  application-single.yml: 单体模式配置

配置项规范:
  server:
    port: 8080                    # 服务端口
  
  spring:
    application:
      name: his-{module}          # 应用名称
    datasource:
      url: jdbc:sqlite:./data/{module}.db
      driver-class-name: org.sqlite.JDBC
    jpa:
      hibernate:
        ddl-auto: update
      show-sql: false
      
  his:
    module:
      enabled: true               # 模块启用标识
    deploy:
      mode: micro                 # micro-微服务/single-单体
```

### 0.6 测试质量规范

> 详细规范参见: [quality_constraints.md](quality_constraints.md)

```yaml
单元测试要求:
  框架: JUnit 5 + Mockito
  代码行覆盖率: >= 90%
  分支覆盖率: >= 90%
  方法覆盖率: >= 85%
  测试类命名: {ClassName}Test.java
  测试方法命名: should_{expectedBehavior}_when_{condition}
  
E2E测试要求:
  框架: Python 3.11+ + requests
  位置: scripts/e2e_test.py
  覆盖模块:
    - 系统管理: 用户认证、权限管理
    - 门诊管理: 挂号、就诊、收费
    - 住院管理: 入院、医嘱、出院
    - 药房管理: 药品、库存、发药
    - 检验管理: 申请、采样、报告
    - 影像管理: 检查、报告
    - 财务管理: 计费、结算、发票

测试报告要求:
  位置: report/
  结构:
    - unit/                    # 单元测试报告
    - e2e/                     # E2E测试报告
    - coverage/                # 覆盖率报告
    - security/                # 安全扫描报告
    - test_summary_*.md        # 汇总报告
  格式: HTML, JSON, Markdown

质量门禁:
  - 所有单元测试必须通过
  - 行覆盖率 >= 90%
  - 分支覆盖率 >= 90%
  - 所有E2E测试必须通过
  - 无严重安全漏洞

测试命令:
  运行单元测试: mvn test jacoco:report
  运行E2E测试: python3 scripts/e2e_test.py
  运行完整测试: ./scripts/run_tests.sh
  生成报告: python3 scripts/report_generator.py
```

### 0.7 Windows平台适配规范

```yaml
Windows服务集成:
  服务注册: 使用winsw或procrun注册为Windows服务
  启动方式: 支持命令行启动和Windows服务启动
  日志输出: 同时输出到控制台和Windows事件日志
  文件路径: 使用Windows路径格式 (C:\HIS\...)
  编码格式: UTF-8 (需在启动参数指定 -Dfile.encoding=UTF-8)

音频设备:
  语音引擎: 优先使用Windows SAPI
  音频输出: 支持选择指定音频设备
  音量控制: 通过Windows音频API控制

打印支持:
  票据打印: 支持POS打印机
  报告打印: 支持A4打印机
  支持预览: Web端预览后调用打印

硬件集成:
  身份证读卡器: 通过本地服务集成
  医保卡读卡器: 通过本地服务集成
  叫号屏: Web页面展示
```

### 0.7 模块开发约束

```yaml
模块边界:
  - 每个模块独立Maven子工程
  - 模块内实现完整业务闭环
  - 跨模块调用必须通过API接口
  - 禁止跨模块直接访问数据库

接口规范:
  - RESTful API设计
  - 统一响应格式 Result<T>
  - 统一错误码定义
  - 接口版本控制 /api/{module}/v1/{resource}

数据隔离:
  - 每个模块独立数据库文件
  - 全局数据在全局数据库
  - 通过API共享数据，不直连其他模块数据库

启动约束:
  微服务模式:
    - 各模块独立启动
    - 通过网关统一入口
    - 服务注册发现(可选Nacos)
  
  单体模式:
    - 通过his-starter-all统一启动
    - 共享Spring容器
    - 模块内方法调用
```

### 0.8 数据库约束

```yaml
SQLite使用规范:
  驱动: org.xerial:sqlite-jdbc:3.45.x
  连接池: 使用HikariCP或内置连接池
  WAL模式: 启用WAL提高并发性能
  编码: UTF-8
  
表命名规范:
  表名: snake_case复数形式 (如 patients, orders)
  主键: id (UUID字符串)
  外键: {table}_id
  创建时间: created_at
  更新时间: updated_at
  状态: status
  删除标记: deleted (逻辑删除)

字段类型映射:
  Java String     -> TEXT
  Java Integer    -> INTEGER
  Java Long       -> INTEGER
  Java BigDecimal -> REAL/DECIMAL
  Java Date       -> TEXT (ISO格式)
  Java Boolean    -> INTEGER (0/1)
```

### 0.9 API响应格式规范

```java
/**
 * 统一响应格式
 */
public class Result<T> {
    private Integer code;      // 状态码，0成功，其他失败
    private String message;    // 提示信息
    private T data;            // 响应数据
    private Long timestamp;    // 时间戳
    private String traceId;    // 链路追踪ID
}

/**
 * 分页响应格式
 */
public class PageResult<T> {
    private List<T> list;      // 数据列表
    private Long total;        // 总记录数
    private Integer pageNum;   // 当前页
    private Integer pageSize;  // 每页大小
    private Integer pages;     // 总页数
}

/**
 * 错误码定义
 */
public enum ErrorCode {
    SUCCESS(0, "成功"),
    PARAM_ERROR(1001, "参数错误"),
    AUTH_ERROR(3001, "认证失败"),
    BIZ_ERROR(2001, "业务异常"),
    SYS_ERROR(5001, "系统异常");
}
```

### 0.10 单体应用启动器示例

```java
/**
 * 单体应用启动器
 * 一次性加载所有模块，适用于演示和快速部署
 */
@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = {
    "com.his.module.outpatient",
    "com.his.module.inpatient",
    "com.his.module.pharmacy",
    "com.his.module.lis",
    "com.his.module.pacs",
    "com.his.module.finance",
    "com.his.module.inventory",
    "com.his.module.hr",
    "com.his.module.emr",
    "com.his.module.voice",
    "com.his.module.system",
    "com.his.common"
})
@EntityScan(basePackages = {
    "com.his.module.**.entity"
})
@EnableJpaRepositories(basePackages = {
    "com.his.module.**.repository"
})
public class HisApplication {
    public static void main(String[] args) {
        SpringApplication.run(HisApplication.class, args);
        System.out.println("HIS系统启动成功！");
        System.out.println("访问地址: http://localhost:8080");
    }
}
```

---

## 一、项目概述

### 1.1 项目背景

医院信息系统(Hospital Information System, HIS)是现代化医院运营的核心支撑平台，通过信息化手段实现医院业务流程的数字化、规范化管理，提高医疗服务质量和运营效率。

### 1.2 系统定位

本HIS系统定位为**新一代智慧医院信息系统**，具备以下特点：
- **全面性**：覆盖门诊、住院、药房、检验、影像、财务等全业务流程
- **先进性**：采用微服务架构，支持Windows平台完美部署
- **集成性**：支持与医保、支付、政务等外部系统互联互通
- **安全性**：满足等保三级安全要求，保障数据安全
- **智能化**：支持AI辅助诊断、智能预警等先进功能
- **跨平台**：所有展示终端采用Web技术，实现跨平台兼容

### 1.3 Windows平台支持

本系统以Windows平台为主要部署环境：

```yaml
支持的Windows版本:
  客户端:
    - Windows 10 专业版/企业版 (1809+)
    - Windows 11 专业版/企业版
  
  服务器:
    - Windows Server 2016
    - Windows Server 2019
    - Windows Server 2022

Windows特性支持:
  - Windows服务(Service)模式运行
  - Windows原生语音引擎(SAPI)集成
  - 多声卡/多音频设备支持
  - Windows打印服务集成
  - Windows安全认证集成
  - 远程桌面音频重定向支持
  - Windows防火墙配置
  - Windows事件日志集成
```

### 1.4 SQLite数据库架构

本系统采用SQLite3作为主要数据库，实现轻量化、便携化部署：

```yaml
数据库架构设计:
  全局数据库(Global DB):
    路径: /data/his_global.db
    用途: 
      - 患者主索引(EMPI)
      - 系统配置
      - 全局字典
      - 用户权限
      - 跨模块关联数据
    大小限制: 建议 < 2GB

  模块独立数据库(Module DB):
    门诊模块: /data/modules/outpatient.db
    住院模块: /data/modules/inpatient.db
    药房模块: /data/modules/pharmacy.db
    检验模块: /data/modules/lis.db
    影像模块: /data/modules/pacs.db
    财务模块: /data/modules/finance.db
    库存模块: /data/modules/inventory.db
    人力模块: /data/modules/hr.db
    语音模块: /data/modules/voice.db
    病历模块: /data/modules/emr.db

  数据库特性:
    - 支持WAL模式(Write-Ahead Logging)
    - 支持加密(SQLCipher)
    - 支持全文搜索(FTS5)
    - 自动定期VACUUM
    - 支持JSON扩展

  数据同步:
    - 模块间通过API通信，不直连数据库
    - 关键数据同步到全局数据库
    - 支持离线操作，网络恢复后同步
```

### 1.5 Web展示终端

所有外部展示设备均采用Web页面形式，实现跨平台兼容：

```yaml
Web展示终端类型:
  门诊大屏:
    - 叫号信息显示屏
    - 科室候诊大屏
    - 专家排班显示屏
    - 医院宣传屏
    
  诊室叫号屏:
    - 诊室门口屏
    - 当前就诊信息显示
    - 候诊队列显示
    
  自助服务终端:
    - 自助挂号机
    - 自助缴费机
    - 自助打印机(报告/发票)
    - 自助查询机
    
  护士站大屏:
    - 病区患者一览表
    - 医嘱执行看板
    - 护理任务看板
    
  药房叫号屏:
    - 取药窗口叫号屏
    - 药房候药区大屏
    
  公共区域屏:
    - 楼层导引屏
    - 科室位置导引
    - 通知公告屏

技术实现:
  前端框架: Vue 3 + Vite
  UI组件: Element Plus
  通信方式: WebSocket实时推送
  离线支持: PWA + Service Worker
  分辨率适配: 响应式布局
```

---

## 二、系统架构

### 2.1 总体架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Web展示终端层 (跨平台)                             │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐    │
│  │ 门诊大屏  │ │叫号显示屏 │ │ 自助终端  │ │ 护士站大屏│ │ 移动终端  │    │
│  │ (Web)     │ │ (Web)     │ │ (Web)     │ │ (Web)     │ │ (Web)     │    │
│  └───────────┘ └───────────┘ └───────────┘ └───────────┘ └───────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │ WebSocket / HTTP
┌─────────────────────────────────────────────────────────────────────────────┐
│                              API网关层                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  认证授权 │ 路由转发 │ 负载均衡 │ 限流熔断 │ 日志审计                │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
┌─────────────────────────────────────────────────────────────────────────────┐
│                           微服务业务层                                       │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ │
│  │门诊服务 │ │住院服务 │ │药房服务 │ │检验服务 │ │影像服务 │ │财务服务 │ │
│  └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘ │
│       │           │           │           │           │           │       │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ │
│  │库存服务 │ │人力服务 │ │病历服务 │ │语音服务 │ │集成服务 │ │报表服务 │ │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
┌─────────────────────────────────────────────────────────────────────────────┐
│                           数据存储层 (SQLite)                                │
│  ┌─────────────┐    ┌─────────────────────────────────────────────────┐   │
│  │  全局数据库  │    │                 模块独立数据库                   │   │
│  │ his_global.db│   │ outpatient.db │ inpatient.db │ pharmacy.db ... │   │
│  └─────────────┘    └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
┌─────────────────────────────────────────────────────────────────────────────┐
│                           基础设施层 (Windows)                               │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐    │
│  │Windows服务│ │ 语音引擎  │ │ 打印服务  │ │ 文件存储  │ │ 消息队列  │    │
│  │ (Service) │ │  (SAPI)   │ │           │ │ (MinIO)   │ │ (MQ)      │    │
│  └───────────┘ └───────────┘ └───────────┘ └───────────┘ └───────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 技术架构

```yaml
前端技术栈:
  框架: Vue 3.4.x + Vite 5.x
  UI组件: Element Plus 2.x
  状态管理: Pinia 2.x
  路由: Vue Router 4.x
  通信: Axios + WebSocket
  离线: PWA + Service Worker
  构建: Vite 5.x

后端技术栈 (强制):
  语言: Java 21 (LTS)
  框架: Spring Boot 3.2.x
  安全: Spring Security 6.x
  ORM: Spring Data JPA + Hibernate
  数据库: SQLite3 (sqlite-jdbc 3.45.x)
  连接池: HikariCP
  缓存: Spring Cache + Caffeine
  验证: Spring Validation
  文档: SpringDoc OpenAPI 3
  日志: SLF4J + Logback
  测试: JUnit 5 + Mockito
  构建: Maven 3.9.x

微服务组件 (可选):
  网关: Spring Cloud Gateway
  注册中心: Nacos (可选)
  配置中心: Nacos (可选)
  服务调用: OpenFeign
  熔断: Resilience4j

Windows服务:
  运行模式: Java进程 / Windows Service
  服务管理: WinSW / Procrun
  日志: Logback + Windows Event Log
  监控: Spring Actuator + Micrometer

数据存储:
  业务数据: SQLite3
  文件存储: 本地文件系统
  影像存储: 本地存储 + DICOM文件
  缓存: Caffeine内存缓存
```

### 2.3 双模式部署架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        模式一：微服务架构 (生产部署)                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         Spring Cloud Gateway                         │   │
│  │   路由 │ 认证 │ 限流 │ 熔断 │ 日志 │ 监控                             │   │
│  └──────────────────────────────┬──────────────────────────────────────┘   │
│                                 │                                           │
│     ┌─────────────┬─────────────┼─────────────┬─────────────┐             │
│     │             │             │             │             │             │
│     ▼             ▼             ▼             ▼             ▼             │
│  ┌──────┐     ┌──────┐     ┌──────┐     ┌──────┐     ┌──────┐           │
│  │门诊  │     │住院  │     │药房  │     │检验  │     │影像  │  ...       │
│  │服务  │     │服务  │     │服务  │     │服务  │     │服务  │           │
│  │:8081 │     │:8082 │     │:8083 │     │:8084 │     │:8085 │           │
│  └──┬───┘     └──┬───┘     └──┬───┘     └──┬───┘     └──┬───┘           │
│     │            │            │            │            │                │
│     ▼            ▼            ▼            ▼            ▼                │
│  ┌──────┐     ┌──────┐     ┌──────┐     ┌──────┐     ┌──────┐           │
│  │out   │     │in    │     │pharm │     │lis   │     │pacs  │           │
│  │.db   │     │.db   │     │.db   │     │.db   │     │.db   │           │
│  └──────┘     └──────┘     └──────┘     └──────┘     └──────┘           │
│                                                                             │
│  优点: 独立部署、独立扩展、故障隔离、负载均衡                                │
│  场景: 大中型医院、高可用要求、分布式部署                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                        模式二：单体架构 (快速部署)                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      HisApplication (单一进程)                       │   │
│  │  java -jar his-starter-all.jar --server.port=8080                   │   │
│  │                                                                      │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │                     Spring Boot 应用容器                     │   │   │
│  │  │  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐          │   │   │
│  │  │  │门诊 │ │住院 │ │药房 │ │检验 │ │影像 │ │ ... │          │   │   │
│  │  │  │模块 │ │模块 │ │模块 │ │模块 │ │模块 │ │     │          │   │   │
│  │  │  └─────┘ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘          │   │   │
│  │  │         模块间通过方法调用，共享Spring容器                   │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  │                                                                      │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │                     SQLite 数据库连接池                      │   │   │
│  │  │  his_global.db │ out.db │ in.db │ pharm.db │ ...            │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  优点: 部署简单、资源占用低、启动快速、便于演示                              │
│  场景: 小型医院/诊所、招投标演示、开发测试、功能验证                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.4 Maven项目结构

```
his-parent/                          # 父工程 (pom)
├── pom.xml                          # 依赖版本管理
│
├── his-common/                      # 公共模块 (jar)
│   ├── his-common-core/             # 核心工具
│   ├── his-common-security/         # 安全认证
│   ├── his-common-cache/            # 缓存工具
│   ├── his-common-voice/            # 语音服务
│   └── his-common-log/              # 日志工具
│
├── his-api/                         # API接口定义 (jar)
│   ├── his-api-outpatient/          # 门诊API
│   ├── his-api-inpatient/           # 住院API
│   ├── his-api-pharmacy/            # 药房API
│   ├── his-api-lis/                 # 检验API
│   ├── his-api-pacs/                # 影像API
│   ├── his-api-finance/             # 财务API
│   ├── his-api-inventory/           # 库存API
│   ├── his-api-hr/                  # 人事API
│   ├── his-api-emr/                 # 病历API
│   ├── his-api-voice/               # 语音API
│   └── his-api-system/              # 系统API
│
├── his-modules/                     # 业务模块 (jar)
│   ├── his-module-outpatient/       # 门诊模块
│   ├── his-module-inpatient/        # 住院模块
│   ├── his-module-pharmacy/         # 药房模块
│   ├── his-module-lis/              # 检验模块
│   ├── his-module-pacs/             # 影像模块
│   ├── his-module-finance/          # 财务模块
│   ├── his-module-inventory/        # 库存模块
│   ├── his-module-hr/               # 人事模块
│   ├── his-module-emr/              # 病历模块
│   ├── his-module-voice/            # 语音模块
│   └── his-module-system/           # 系统模块
│
├── his-gateway/                     # API网关 (jar) - 微服务模式
│
├── his-admin/                       # 管理后台 (jar)
│
├── his-starter/                     # 启动器
│   ├── his-starter-all/             # 单体应用启动器 (jar)
│   └── his-starter-micro/           # 微服务启动器模板
│
└── his-web/                         # Web前端
    ├── src/
    ├── package.json
    └── vite.config.ts
```

### 2.5 Windows部署配置

```yaml
单体应用部署:
  启动命令: java -jar his-starter-all.jar --server.port=8080
  内存要求: 最小512MB，推荐1GB+
  注册服务: 使用WinSW注册为Windows服务
  
微服务部署:
  网关端口: 8080
  各模块端口: 8081-8091
  内存要求: 每个模块256MB+

目录结构 (Windows):
  C:\HIS\
    ├── bin\                      # 程序文件
    │   ├── his-starter-all.jar   # 单体应用
    │   ├── his-gateway.jar       # 网关
    │   └── his-module-*.jar      # 各模块
    ├── data\                     # 数据文件
    │   ├── his_global.db         # 全局数据库
    │   └── modules\              # 模块数据库
    ├── logs\                     # 日志文件
    ├── config\                   # 配置文件
    ├── web\                      # Web前端
    └── pacs\                     # 影像文件

Windows服务注册 (WinSW):
  <service>
    <id>HIS-Service</id>
    <name>HIS医院信息系统</name>
    <executable>java</executable>
    <arguments>-Xms512m -Xmx1024m -jar his-starter-all.jar</arguments>
    <logpath>C:\HIS\logs</logpath>
  </service>
```

---

## 三、功能模块概览

### 3.1 模块清单

| 序号 | 模块名称 | 英文名称 | 文档路径 | 核心功能 |
|------|----------|----------|----------|----------|
| 1 | 门诊管理模块 | Outpatient Management | [outpatient.md](./outpatient.md) | 预约挂号、门诊就诊、处方开立、门诊收费 |
| 2 | 住院管理模块 | Inpatient Management | [inpatient.md](./inpatient.md) | 入院登记、床位管理、医嘱管理、出院结算 |
| 3 | 药房管理模块 | Pharmacy Management | [pharmacy.md](./pharmacy.md) | 药品管理、库存管理、发药管理、用药审核 |
| 4 | 电子病历模块 | Electronic Medical Record | [emr.md](./emr.md) | 病历书写、病历模板、病历质控、病历归档 |
| 5 | 检验管理模块 | Laboratory Information System | [lis.md](./lis.md) | 检验申请、样本管理、检验执行、报告管理 |
| 6 | 影像管理模块 | Picture Archiving System | [pacs.md](./pacs.md) | 检查预约、影像采集、影像浏览、诊断报告 |
| 7 | 财务收费模块 | Finance and Billing | [finance.md](./finance.md) | 价表管理、费用结算、医保结算、发票管理 |
| 8 | 库存物资模块 | Inventory Management | [inventory.md](./inventory.md) | 物资管理、出入库管理、库存盘点、采购管理 |
| 9 | 人力资源管理 | Human Resources | [hr.md](./hr.md) | 员工管理、排班管理、考勤管理、权限管理 |
| 10 | 系统管理与安全 | System Administration | [system.md](./system.md) | 用户管理、权限管理、日志审计、系统配置 |
| 11 | **语音呼叫模块** | Voice Call Service | [voice.md](./voice.md) | 叫号播报、报告通知、寻人广播、全员通知 |

### 3.2 Web展示终端模块

| 序号 | 终端类型 | 页面名称 | 功能描述 |
|------|----------|----------|----------|
| 1 | 门诊大屏 | outpatient-display.html | 科室候诊队列、叫号信息实时展示 |
| 2 | 诊室叫号屏 | clinic-call-display.html | 诊室门口叫号信息显示 |
| 3 | 药房叫号屏 | pharmacy-call-display.html | 药房取药叫号显示 |
| 4 | 自助挂号机 | self-registration.html | 自助挂号、预约取号 |
| 5 | 自助缴费机 | self-payment.html | 自助缴费、费用查询 |
| 6 | 自助打印机 | self-print.html | 报告打印、发票打印 |
| 7 | 护士站大屏 | nursing-station-display.html | 病区患者一览、护理任务看板 |
| 8 | 科室排班屏 | schedule-display.html | 科室排班信息展示 |

---

## 四、SQLite数据库设计规范

### 4.1 全局数据库 (his_global.db)

```sql
-- 患者主索引表 (EMPI)
CREATE TABLE patient_master (
    id                  TEXT PRIMARY KEY,
    empi_id             TEXT UNIQUE NOT NULL,
    name                TEXT NOT NULL,
    gender              TEXT NOT NULL,
    birth_date          DATE,
    id_type             TEXT,
    id_no               TEXT UNIQUE,
    phone               TEXT,
    status              TEXT DEFAULT 'active',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户表
CREATE TABLE sys_user (
    id                  TEXT PRIMARY KEY,
    username            TEXT UNIQUE NOT NULL,
    password_hash       TEXT NOT NULL,
    real_name           TEXT,
    dept_id             TEXT,
    status              TEXT DEFAULT 'active',
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 科室表
CREATE TABLE sys_department (
    id                  TEXT PRIMARY KEY,
    dept_code           TEXT UNIQUE NOT NULL,
    dept_name           TEXT NOT NULL,
    dept_type           TEXT,
    parent_id           TEXT,
    status              TEXT DEFAULT 'active'
);

-- 角色表
CREATE TABLE sys_role (
    id                  TEXT PRIMARY KEY,
    role_code           TEXT UNIQUE NOT NULL,
    role_name           TEXT NOT NULL
);

-- 数据字典表
CREATE TABLE sys_dictionary (
    id                  TEXT PRIMARY KEY,
    dict_type           TEXT NOT NULL,
    dict_code           TEXT NOT NULL,
    dict_name           TEXT NOT NULL,
    sort_order          INTEGER,
    UNIQUE(dict_type, dict_code)
);

-- 系统参数表
CREATE TABLE sys_parameter (
    id                  TEXT PRIMARY KEY,
    param_code          TEXT UNIQUE NOT NULL,
    param_name          TEXT,
    param_value         TEXT,
    param_group         TEXT
);

-- 全局序列号表
CREATE TABLE sys_sequence (
    seq_name            TEXT PRIMARY KEY,
    current_value       INTEGER DEFAULT 0,
    prefix              TEXT,
    date_format         TEXT
);
```

### 4.2 模块数据库结构示例

#### 门诊模块数据库 (outpatient.db)

```sql
-- 启用WAL模式
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;

-- 挂号记录表
CREATE TABLE registration (
    id                  TEXT PRIMARY KEY,
    reg_no              TEXT UNIQUE NOT NULL,
    patient_id          TEXT NOT NULL,
    patient_name        TEXT,
    dept_id             TEXT NOT NULL,
    doctor_id           TEXT,
    reg_date            DATE NOT NULL,
    reg_time            DATETIME DEFAULT CURRENT_TIMESTAMP,
    reg_type            TEXT,
    status              TEXT DEFAULT 'registered',
    queue_no            INTEGER,
    amount              DECIMAL(10,2)
);

-- 排班表
CREATE TABLE schedule (
    id                  TEXT PRIMARY KEY,
    dept_id             TEXT NOT NULL,
    doctor_id           TEXT NOT NULL,
    schedule_date       DATE NOT NULL,
    time_period         TEXT,
    total_quota         INTEGER DEFAULT 0,
    used_quota          INTEGER DEFAULT 0,
    status              TEXT DEFAULT 'open',
    UNIQUE(doctor_id, schedule_date, time_period)
);

-- 处方表
CREATE TABLE prescription (
    id                  TEXT PRIMARY KEY,
    prescription_no     TEXT UNIQUE NOT NULL,
    reg_id              TEXT NOT NULL,
    patient_id          TEXT NOT NULL,
    doctor_id           TEXT NOT NULL,
    prescription_date   DATE NOT NULL,
    total_amount        DECIMAL(10,2),
    status              TEXT DEFAULT 'valid'
);

-- 处方明细表
CREATE TABLE prescription_detail (
    id                  TEXT PRIMARY KEY,
    prescription_id     TEXT NOT NULL,
    drug_id             TEXT NOT NULL,
    drug_name           TEXT,
    quantity            DECIMAL(10,2),
    dosage              TEXT,
    frequency           TEXT,
    unit_price          DECIMAL(10,4),
    amount              DECIMAL(10,2)
);

-- 创建索引
CREATE INDEX idx_reg_patient ON registration(patient_id);
CREATE INDEX idx_reg_date ON registration(reg_date);
CREATE INDEX idx_schedule_date ON schedule(schedule_date);
CREATE INDEX idx_pres_reg ON prescription(reg_id);
```

### 4.3 数据库访问规范

```yaml
访问原则:
  - 模块只能访问自己的数据库和全局数据库
  - 禁止跨模块直接访问数据库
  - 跨模块数据交互通过API调用

连接配置:
  - 每个模块维护自己的数据库连接池
  - 启用WAL模式提高并发性能
  - 定期执行VACUUM和ANALYZE

数据同步:
  - 关键业务数据同步到全局数据库
  - 使用事件机制通知数据变更
  - 支持离线操作，恢复后自动同步

备份策略:
  - 每日增量备份
  - 每周全量备份
  - 备份文件加密存储
```

---

## 五、Web展示终端规范

### 5.1 门诊候诊大屏

```yaml
页面名称: outpatient-display.html
显示内容:
  - 科室名称
  - 当前叫号信息
  - 候诊队列(前10位)
  - 过号队列
  - 滚动通知

技术实现:
  - WebSocket实时推送
  - 语音播报集成
  - 自动刷新队列
  - 适配1920x1080分辨率

API接口:
  - WebSocket: /ws/outpatient/queue?deptId=xxx
  - REST: /api/outpatient/display/queue?deptId=xxx
```

### 5.2 诊室叫号屏

```yaml
页面名称: clinic-call-display.html
显示内容:
  - 诊室号
  - 医生姓名/职称
  - 当前就诊患者
  - 等候患者数量
  - 叫号按钮(可选)

技术实现:
  - WebSocket实时更新
  - 适配各种尺寸显示屏
  - 支持横竖屏切换

API接口:
  - WebSocket: /ws/clinic/call?roomId=xxx
```

### 5.3 自助服务终端

```yaml
自助挂号机页面:
  - self-registration.html
  - 身份证/医保卡读卡
  - 科室医生选择
  - 号源选择
  - 确认挂号
  - 打印挂号单

自助缴费机页面:
  - self-payment.html
  - 费用查询
  - 支付方式选择
  - 扫码支付
  - 打印发票

自助打印页面:
  - self-print.html
  - 报告打印
  - 发票打印
  - 费用清单打印

技术要求:
  - 支持触摸屏操作
  - 支持读卡器集成(通过本地服务)
  - 支持打印机集成
  - 支持语音引导
```

---

## 六、语音呼叫服务集成

### 6.1 语音服务架构

```yaml
语音服务组件:
  核心引擎:
    - Windows SAPI 5.x (主引擎)
    - 云端TTS API (备用)
  
  服务接口:
    - REST API: 语音任务提交
    - WebSocket: 实时播报控制
    - 事件订阅: 业务事件触发播报

  设备管理:
    - 多音频设备支持
    - 分区播放控制
    - 音量独立控制
```

### 6.2 语音播报场景

```yaml
门诊场景:
  - 诊室叫号: "请15号患者张明到101诊室就诊"
  - 过号提醒: "请10号患者李华，听到广播后到102诊室"
  - 候诊提醒: "您前面还有3位患者"

药房场景:
  - 取药叫号: "请张明到药房1号窗口取药"
  - 缺药提醒: "药房通知，某某药品暂缺"

检验场景:
  - 报告完成: "张明，您的检验报告已完成"
  - 采血叫号: "请15号到采血窗口"

住院场景:
  - 危急值通知: "内科3床患者检验危急值，请立即处理"
  - 治疗提醒: "5床患者需要执行医嘱"

公共场景:
  - 寻人广播: "张三患者请到门诊大厅服务台"
  - 全员通知: "系统维护通知..."
  - 紧急广播: "消防演习通知..."
```

---

## 七、模块间交互协议

### 7.1 API设计规范

```yaml
API规范:
  基础路径: /api/{module}/{version}/{resource}
  版本控制: v1
  认证方式: JWT Token
  
请求头:
  Authorization: Bearer {token}
  X-Request-ID: {uuid}
  X-Client-ID: {client_id}

响应格式:
  {
    "code": 0,
    "message": "success",
    "data": {},
    "timestamp": "2024-01-15T10:30:00Z"
  }

错误码:
  0: 成功
  1xxx: 参数错误
  2xxx: 业务错误
  3xxx: 权限错误
  5xxx: 系统错误
```

### 7.2 模块调用关系

```yaml
门诊模块:
  调用: 药房(处方发药)、检验(申请)、影像(申请)、财务(收费)、语音(叫号)
  被调用: 全局(患者信息)、人力(医生排班)

住院模块:
  调用: 药房(医嘱发药)、检验(医嘱)、影像(医嘱)、财务(结算)、语音(通知)
  被调用: 全局(患者信息)

药房模块:
  调用: 库存(药品库存)、财务(药品费用)、语音(取药通知)
  被调用: 门诊(处方)、住院(医嘱)

检验模块:
  调用: 语音(报告通知)、财务(检验费用)
  被调用: 门诊(申请)、住院(医嘱)

语音模块:
  被调用: 所有需要语音播报的模块
```

---

## 八、非功能性需求

### 8.1 性能需求

| 指标 | 目标值 |
|------|--------|
| 普通查询响应 | < 1秒 |
| 复杂报表响应 | < 5秒 |
| 并发用户数 | >= 500 |
| 系统可用率 | >= 99.9% |
| 语音播报延迟 | < 1秒 |

### 8.2 Windows平台特定需求

```yaml
Windows服务:
  - 支持Windows服务管理器控制
  - 支持开机自启动
  - 支持服务崩溃自动重启
  - 支持远程管理

性能优化:
  - 内存占用 < 500MB (基础服务)
  - CPU占用 < 10% (空闲时)
  - 启动时间 < 30秒

兼容性:
  - 支持32/64位系统
  - 支持多显示器配置
  - 支持触摸屏操作
```

### 8.3 安全需求

```yaml
数据安全:
  - SQLite数据库加密(SQLCipher)
  - 敏感数据加密存储
  - 数据传输HTTPS加密

访问安全:
  - JWT Token认证
  - 角色权限控制
  - 操作日志审计

隐私保护:
  - 患者姓名脱敏显示
  - 敏感字段加密
  - 访问审批流程
```

---

## 九、部署指南

### 9.1 Windows安装部署

```yaml
安装步骤:
  1. 运行安装程序 HIS-Setup.msi
  2. 选择安装目录(默认 C:\HIS)
  3. 配置服务端口(默认 8080)
  4. 初始化数据库
  5. 安装Windows服务
  6. 启动服务

目录结构:
  C:\HIS\
    ├── bin\              # 程序文件
    ├── data\             # 数据文件
    │   ├── his_global.db
    │   └── modules\
    ├── logs\             # 日志文件
    ├── config\           # 配置文件
    ├── web\              # Web前端文件
    └── pacs\             # 影像文件

服务管理:
  启动: net start HIS-Service
  停止: net stop HIS-Service
  重启: net stop HIS-Service && net start HIS-Service
```

### 9.2 Web终端部署

```yaml
大屏终端:
  1. 打开浏览器(Chrome Edge)
  2. 访问: http://server:8080/display/outpatient?deptId=D001
  3. F11全屏模式
  4. 配置开机自启动

自助终端:
  1. 打开浏览器访问自助页面
  2. 安装本地服务(读卡器/打印机)
  3. 配置Kiosk模式
```

---

## 十、附录

### 10.1 文档索引

| 序号 | 文档名称 | 文件路径 |
|------|----------|----------|
| 1 | 门诊管理模块PRD | [outpatient.md](./outpatient.md) |
| 2 | 住院管理模块PRD | [inpatient.md](./inpatient.md) |
| 3 | 药房管理模块PRD | [pharmacy.md](./pharmacy.md) |
| 4 | 电子病历模块PRD | [emr.md](./emr.md) |
| 5 | 检验管理模块PRD | [lis.md](./lis.md) |
| 6 | 影像管理模块PRD | [pacs.md](./pacs.md) |
| 7 | 财务收费模块PRD | [finance.md](./finance.md) |
| 8 | 库存物资模块PRD | [inventory.md](./inventory.md) |
| 9 | 人力资源管理PRD | [hr.md](./hr.md) |
| 10 | 系统管理与安全PRD | [system.md](./system.md) |
| 11 | 语音呼叫模块PRD | [voice.md](./voice.md) |

### 10.2 术语定义

| 术语 | 定义 |
|------|------|
| HIS | 医院信息系统 |
| EMPI | 企业级患者主索引 |
| TTS | 文字转语音 |
| SAPI | Windows语音应用程序接口 |
| WAL | SQLite预写式日志 |
| PWA | 渐进式Web应用 |

### 10.3 版本历史

| 版本 | 日期 | 修订内容 |
|------|------|----------|
| v1.0 | 2024-01-15 | 初稿完成 |
| v1.1 | 2024-01-15 | 增加语音呼叫模块、SQLite数据库架构、Windows平台支持、Web展示终端 |
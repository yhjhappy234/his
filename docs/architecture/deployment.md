# HIS 部署架构

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06  
> **部署平台**: Windows 10/11/Server 2016+ 优先支持

---

## 1. 部署概述

HIS 系统支持两种部署模式：微服务架构（生产部署）和单体架构（快速部署），可根据医院规模和需求选择合适的部署方式。

---

## 2. 部署模式选择

### 2.1 微服务架构 (生产部署)

**适用场景**：
- 大中型医院
- 高可用要求
- 多服务器分布式部署
- 需要独立扩展各模块

**优点**：
- 独立部署、独立扩展
- 故障隔离、负载均衡
- 灵活配置、便于维护

**端口分配**：

| 服务 | 端口 | 描述 |
|------|------|------|
| Gateway | 8080 | API 网关 |
| System | 8081 | 系统管理模块 |
| Outpatient | 8082 | 门诊管理模块 |
| Inpatient | 8083 | 住院管理模块 |
| Pharmacy | 8084 | 药房管理模块 |
| LIS | 8085 | 检验管理模块 |
| PACS | 8086 | 影像管理模块 |
| Finance | 8087 | 财务收费模块 |
| Inventory | 8088 | 库存物资模块 |
| HR | 8089 | 人力资源管理 |
| EMR | 8090 | 电子病历模块 |
| Voice | 8091 | 语音呼叫模块 |

### 2.2 单体架构 (快速部署)

**适用场景**：
- 小型医院/诊所
- 招投标演示
- 开发测试环境
- 功能验证测试

**优点**：
- 部署简单、资源占用低
- 启动快速、便于演示
- 维护方便、成本低

**配置**：
- 单一进程，端口 8080
- 共享数据库连接池
- 内存要求最小 512MB，推荐 1GB+

---

## 3. Windows 安装部署

### 3.1 系统要求

```yaml
操作系统:
  - Windows 10 专业版/企业版 (1809+)
  - Windows 11 专业版/企业版
  - Windows Server 2016/2019/2022

硬件要求:
  微服务模式:
    CPU: 4核+
    内存: 8GB+
    磁盘: 100GB+
  
  单体模式:
    CPU: 2核+
    内存: 2GB+
    磁盘: 50GB+

软件要求:
  - JDK 21 LTS
  - Maven 3.9.x (开发环境)
```

### 3.2 安装步骤

```yaml
安装步骤:
  1. 运行安装程序 HIS-Setup.msi
  2. 选择安装目录(默认 C:\HIS)
  3. 配置服务端口(默认 8080)
  4. 初始化数据库
  5. 安装Windows服务
  6. 启动服务
```

### 3.3 目录结构

```
C:\HIS\
├── bin\                      # 程序文件
│   ├── his-starter-all.jar   # 单体应用
│   ├── his-gateway.jar       # 网关
│   └── his-module-*.jar      # 各模块
├── data\                     # 数据文件
│   ├── his_global.db         # 全局数据库
│   └── modules\              # 模块数据库
│       ├── outpatient.db
│       ├── inpatient.db
│       ├── pharmacy.db
│       └── ...
├── logs\                     # 日志文件
│   ├── his.log               # 应用日志
│   └── his-error.log         # 错误日志
├── config\                   # 配置文件
│   ├── application.yml       # 主配置
│   ├── application-prod.yml  # 生产配置
│   └── logback.xml           # 日志配置
├── web\                      # Web前端
│   ├── index.html
│   └── assets\
├── pacs\                     # 影像文件
│   └── images\
└── scripts\                  # 管理脚本
    ├── start.bat
    ├── stop.bat
    └── backup.bat
```

---

## 4. Windows 服务配置

### 4.1 使用 WinSW 注册服务

```xml
<!-- his-service.xml -->
<service>
  <id>HIS-Service</id>
  <name>HIS医院信息系统</name>
  <description>HIS Hospital Information System Service</description>
  
  <executable>java</executable>
  <arguments>-Xms512m -Xmx1024m -Dfile.encoding=UTF-8 -jar his-starter-all.jar</arguments>
  
  <logpath>C:\HIS\logs</logpath>
  <log mode="roll-by-size">
    <size>10240</size>
    <keep>10</keep>
  </log>
  
  <onfailure action="restart" delay="10 sec"/>
  <onfailure action="restart" delay="20 sec"/>
  <onfailure action="none"/>
  
  <resetfailure>1 hour</resetfailure>
  
  <env name="JAVA_HOME" value="C:\Program Files\Java\jdk-21"/>
  <env name="HIS_HOME" value="C:\HIS"/>
</service>
```

### 4.2 服务管理命令

```bash
# 安装服务
winsw.exe install his-service.xml

# 启动服务
net start HIS-Service
# 或
winsw.exe start his-service.xml

# 停止服务
net stop HIS-Service
# 或
winsw.exe stop his-service.xml

# 重启服务
winsw.exe restart his-service.xml

# 卸载服务
winsw.exe uninstall his-service.xml

# 查看服务状态
winsw.exe status his-service.xml
```

---

## 5. 配置文件说明

### 5.1 主配置文件 (application.yml)

```yaml
server:
  port: 8080

spring:
  application:
    name: his-application
  
  datasource:
    url: jdbc:sqlite:./data/his_global.db
    driver-class-name: org.sqlite.JDBC
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.community.dialect.SQLiteDialect
  
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=10m

his:
  module:
    enabled: true
  deploy:
    mode: single    # single-单体 / micro-微服务
  security:
    session-timeout: 30
    password-expiry: 90

logging:
  level:
    root: INFO
    com.yhj.his: DEBUG
  file:
    name: ./logs/his.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 5.2 微服务模式配置 (application-micro.yml)

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: system
          uri: lb://his-system
          predicates:
            - Path=/api/system/**
        - id: outpatient
          uri: lb://his-outpatient
          predicates:
            - Path=/api/outpatient/**
        - id: inpatient
          uri: lb://his-inpatient
          predicates:
            - Path=/api/inpatient/**
        - id: pharmacy
          uri: lb://his-pharmacy
          predicates:
            - Path=/api/pharmacy/**
        - id: lis
          uri: lb://his-lis
          predicates:
            - Path=/api/lis/**
        - id: pacs
          uri: lb://his-pacs
          predicates:
            - Path=/api/pacs/**
        - id: finance
          uri: lb://his-finance
          predicates:
            - Path=/api/finance/**
        - id: emr
          uri: lb://his-emr
          predicates:
            - Path=/api/emr/**
        - id: voice
          uri: lb://his-voice
          predicates:
            - Path=/api/voice/**
    
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
```

---

## 6. Web 终端部署

### 6.1 门诊大屏配置

```yaml
配置步骤:
  1. 打开浏览器(Chrome/Edge)
  2. 访问: http://server:8080/display/outpatient?deptId=D001
  3. F11全屏模式
  4. 配置开机自启动
  
开机自启动脚本:
  @echo off
  start chrome --kiosk --app=http://localhost:8080/display/outpatient
```

### 6.2 自助终端配置

```yaml
配置步骤:
  1. 打开浏览器访问自助页面
  2. 安装本地服务(读卡器/打印机)
  3. 配置Kiosk模式
  
Kiosk模式脚本:
  @echo off
  start chrome --kiosk --disable-pinch --overscroll-history-navigation=0 http://localhost:8080/self-service
```

---

## 7. 数据备份与恢复

### 7.1 备份策略

```yaml
备份策略:
  全量备份: 每周一次
  增量备份: 每日一次
  保留周期: 全量12个月，增量3个月
  
备份内容:
  - 数据库文件
  - 系统配置文件
  - 影像文件
  - 日志文件
  
备份脚本:
  @echo off
  set BACKUP_DIR=C:\HIS_Backup\%date%
  mkdir %BACKUP_DIR%
  copy C:\HIS\data\*.db %BACKUP_DIR%\data\
  copy C:\HIS\config\* %BACKUP_DIR%\config\
  xcopy C:\HIS\pacs\ %BACKUP_DIR%\pacs\ /E /I
```

### 7.2 恢复步骤

```yaml
恢复步骤:
  1. 停止服务
  2. 备份当前数据
  3. 复制备份数据到相应目录
  4. 启动服务
  5. 验证数据完整性
```

---

## 8. 监控与运维

### 8.1 系统监控

```yaml
监控指标:
  - CPU使用率
  - 内存使用率
  - 磁盘空间
  - 服务状态
  - 响应时间
  
监控工具:
  - Spring Actuator
  - Windows Performance Monitor
  - 自定义监控脚本
  
Actuator端点:
  /actuator/health      # 健康检查
  /actuator/metrics     # 性能指标
  /actuator/info        # 应用信息
```

### 8.2 日志管理

```yaml
日志配置:
  位置: C:\HIS\logs\
  格式: 滚动日志文件
  大小: 单文件最大10MB
  保留: 10个滚动文件
  
日志级别:
  生产: INFO
  开发: DEBUG
  
日志查看:
  - Windows事件日志
  - 文件日志 viewer
  - Web管理界面
```

---

## 9. 安全配置

### 9.1 网络安全

```yaml
防火墙配置:
  入站规则:
    - 允许端口 8080 (API网关)
    - 允许端口 8081-8091 (各模块)
    - 允许端口 443 (HTTPS)
  
  出站规则:
    - 允许访问医保接口
    - 允许访问支付接口
```

### 9.2 数据安全

```yaml
数据安全配置:
  - 启用SQLCipher加密
  - 敏感数据字段加密
  - HTTPS传输加密
  - 定期备份加密存储
```

---

## 10. 常见问题处理

### 10.1 服务无法启动

```yaml
排查步骤:
  1. 检查Java环境
     java -version
  2. 检查端口占用
     netstat -ano | findstr 8080
  3. 检查日志文件
     type C:\HIS\logs\his-error.log
  4. 检查数据库文件
     dir C:\HIS\data\
```

### 10.2 性能问题

```yaml
优化措施:
  1. 增加内存配置
     -Xmx2048m
  2. 调整连接池大小
     maximum-pool-size: 20
  3. 启用缓存
     spring.cache.type: caffeine
  4. 定期清理日志
```

---

**文档维护**: HIS Platform Team
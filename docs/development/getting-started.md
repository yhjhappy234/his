# 开发环境搭建指南

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06

---

## 1. 环境要求

### 1.1 必备软件

| 软件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 21 LTS | Java 开发环境 |
| Maven | 3.9.x | 项目构建工具 |
| Git | 2.x | 版本控制 |
| SQLite3 | 3.45.x | 数据库 |
| Node.js | 18.x+ | 前端构建(可选) |

### 1.2 开发工具(推荐)

- IntelliJ IDEA 2024.x
- Visual Studio Code (前端开发)
- DBeaver (数据库管理)

### 1.3 操作系统

- Windows 10/11 (推荐)
- Windows Server 2016+
- Linux (Ubuntu 20.04+)
- macOS 12+

---

## 2. JDK 安装配置

### 2.1 下载安装

1. 访问 Oracle JDK 或 Adoptium 下载 JDK 21 LTS
2. 运行安装程序，选择安装目录

### 2.2 环境变量配置

**Windows**:
```bash
# 系统环境变量
JAVA_HOME = C:\Program Files\Java\jdk-21
Path 添加 %JAVA_HOME%\bin

# 验证安装
java -version
# 输出: java version "21.0.x"
```

**Linux/macOS**:
```bash
# 编辑 ~/.bashrc 或 ~/.zshrc
export JAVA_HOME=/usr/lib/jvm/jdk-21
export PATH=$JAVA_HOME/bin:$PATH

# 生效配置
source ~/.bashrc

# 验证安装
java -version
```

---

## 3. Maven 安装配置

### 3.1 下载安装

1. 访问 Apache Maven 官网下载 3.9.x 版本
2. 解压到指定目录

### 3.2 环境变量配置

**Windows**:
```bash
MAVEN_HOME = D:\apache-maven-3.9.6
Path 添加 %MAVEN_HOME%\bin

# 验证安装
mvn -version
# 输出: Apache Maven 3.9.x
```

### 3.3 Maven 配置

编辑 `conf/settings.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
  
  <!-- 本地仓库位置 -->
  <localRepository>D:\maven\repository</localRepository>
  
  <!-- 镜像配置(国内加速) -->
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Maven Mirror</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
  
  <!-- JDK 配置 -->
  <profiles>
    <profile>
      <id>jdk-21</id>
      <activation>
        <activeByDefault>true</activeByDefault>
        <jdk>21</jdk>
      </activation>
      <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <maven.compiler.compilerVersion>21</maven.compiler.compilerVersion>
      </properties>
    </profile>
  </profiles>
</settings>
```

---

## 4. 项目克隆与构建

### 4.1 克隆项目

```bash
# 克隆项目
git clone https://github.com/your-org/his.git
cd his
```

### 4.2 项目构建

```bash
# 完整构建(跳过测试)
mvn clean install -DskipTests

# 完整构建(包含测试)
mvn clean install

# 仅编译
mvn clean compile
```

### 4.3 常见构建问题

**问题1**: 编译错误 - 无效的源发行版
```
解决: 确认 JDK 版本为 21，检查 JAVA_HOME 配置
```

**问题2**: 依赖下载失败
```
解决: 检查网络连接，配置 Maven 镜像
```

---

## 5. IDE 配置

### 5.1 IntelliJ IDEA

**项目导入**:
1. File -> Open -> 选择项目根目录
2. 选择 Maven 项目
3. 等待依赖下载完成

**JDK 配置**:
1. File -> Project Structure -> Project
2. SDK 选择 JDK 21
3. Language Level 选择 21

**编码设置**:
1. File -> Settings -> Editor -> File Encodings
2. Global Encoding: UTF-8
3. Project Encoding: UTF-8
4. Properties Files: UTF-8

**Maven 配置**:
1. File -> Settings -> Build, Execution, Deployment -> Build Tools -> Maven
2. Maven home path: 选择 Maven 安装目录
3. User settings file: 选择自定义的 settings.xml

### 5.2 代码风格配置

导入项目代码风格配置:

1. File -> Settings -> Editor -> Code Style -> Java
2. 点击齿轮 -> Import Scheme
3. 选择项目中的 `codestyle.xml`

---

## 6. 本地运行

### 6.1 单体模式启动

```bash
# 进入启动器目录
cd his-starter/his-starter-all

# 启动应用
mvn spring-boot:run

# 或使用 jar 包启动
java -jar target/his-starter-all.jar
```

**启动成功输出**:
```
  ____  __  _______  __  ________    __
 / __ \/ / / / ___/ |/ / / ____/ /   / /
/ /_/ / /_/ / /__/ |   / / /   / /   / /
\__,_/\__,_/\___/_/|_/ /_/   /_/___/_/
                                    /_/

HIS系统启动成功！
访问地址: http://localhost:8080
API文档: http://localhost:8080/swagger-ui.html
```

### 6.2 微服务模式启动

```bash
# 1. 启动网关
cd his-gateway
mvn spring-boot:run

# 2. 启动各模块(不同终端)
cd his-modules/his-module-system
mvn spring-boot:run -Dserver.port=8081

cd his-modules/his-module-outpatient
mvn spring-boot:run -Dserver.port=8082

# ... 其他模块
```

### 6.3 访问系统

- 系统地址: http://localhost:8080
- API 文档: http://localhost:8080/swagger-ui.html
- 默认账号: admin / admin123

---

## 7. 数据库配置

### 7.1 SQLite 数据库

项目使用 SQLite 作为数据库，数据文件位于 `./data/` 目录。

**首次运行**:
- 系统自动创建数据库文件
- 自动创建表结构
- 自动初始化基础数据

### 7.2 数据库初始化

```bash
# 手动初始化数据库
cd scripts
python3 init_database.py

# 或使用 SQL 脚本
sqlite3 ../data/his_global.db < init_global.sql
```

### 7.3 数据库管理

使用 DBeaver 或 SQLite Studio 连接数据库:

- 文件路径: `./data/his_global.db`
- 驱动: SQLite JDBC

---

## 8. 配置文件说明

### 8.1 配置文件位置

```
his-starter/his-starter-all/src/main/resources/
├── application.yml           # 主配置
├── application-dev.yml       # 开发环境配置
├── application-prod.yml      # 生产环境配置
└── application-single.yml    # 单体模式配置
```

### 8.2 主要配置项

```yaml
server:
  port: 8080                    # 服务端口

spring:
  application:
    name: his-application
  
  datasource:
    url: jdbc:sqlite:./data/his_global.db
    driver-class-name: org.sqlite.JDBC
  
  jpa:
    hibernate:
      ddl-auto: update          # 自动更新表结构
    show-sql: true              # 显示SQL(开发环境)
  
logging:
  level:
    root: INFO
    com.yhj.his: DEBUG          # 项目日志级别
```

---

## 9. 常用命令

### 9.1 Maven 命令

```bash
# 清理构建
mvn clean

# 编译
mvn compile

# 打包
mvn package

# 安装到本地仓库
mvn install

# 运行测试
mvn test

# 运行指定测试类
mvn test -Dtest=UserServiceTest

# 生成测试覆盖率报告
mvn test jacoco:report

# 跳过测试打包
mvn package -DskipTests
```

### 9.2 Git 命令

```bash
# 查看状态
git status

# 拉取最新代码
git pull

# 创建分支
git checkout -b feature/new-feature

# 提交代码
git add .
git commit -m "feat: 新功能描述"

# 推送分支
git push origin feature/new-feature
```

---

## 10. 开发流程

### 10.1 功能开发流程

1. 从 master 创建功能分支
2. 编写代码和单元测试
3. 运行测试确保通过
4. 提交代码并推送
5. 创建 Pull Request
6. 代码审查
7. 合并到主分支

### 10.2 代码提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具链相关
```

---

**文档维护**: HIS Platform Team
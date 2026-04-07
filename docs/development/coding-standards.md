# 编码规范

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06

---

## 1. 包命名规范

### 1.1 包结构

所有代码必须使用统一的包前缀 `com.yhj.his`:

```yaml
包结构规范:
  根包: com.yhj.his
  
  公共模块:
    com.yhj.his.common.core        # 核心工具
    com.yhj.his.common.security    # 安全认证
    com.yhj.his.common.cache       # 缓存工具
  
  业务模块:
    com.yhj.his.module.{module}    # 模块根包
    com.yhj.his.module.{module}.controller
    com.yhj.his.module.{module}.service
    com.yhj.his.module.{module}.service.impl
    com.yhj.his.module.{module}.repository
    com.yhj.his.module.{module}.entity
    com.yhj.his.module.{module}.dto
    com.yhj.his.module.{module}.vo
    com.yhj.his.module.{module}.config
    com.yhj.his.module.{module}.util
  
  API接口:
    com.yhj.his.api.{module}       # API定义
```

### 1.2 模块命名

| 模块 | 包名 | 说明 |
|------|------|------|
| 系统管理 | system | 用户、权限、字典 |
| 门诊管理 | outpatient | 挂号、就诊、处方 |
| 住院管理 | inpatient | 入院、医嘱、出院 |
| 药房管理 | pharmacy | 药品、库存、发药 |
| 检验管理 | lis | 检验申请、报告 |
| 影像管理 | pacs | 检查、报告 |
| 财务管理 | finance | 收费、结算 |
| 库存管理 | inventory | 物资、采购 |
| 人事管理 | hr | 员工、排班 |
| 电子病历 | emr | 病历书写、质控 |
| 语音服务 | voice | 叫号、广播 |

---

## 2. 类命名规范

### 2.1 命名风格

- **PascalCase**: 类名、接口名、枚举名
- **camelCase**: 方法名、变量名、参数名
- **UPPER_SNAKE_CASE**: 常量名

### 2.2 类命名后缀

| 类型 | 后缀 | 示例 |
|------|------|------|
| 控制器 | Controller | UserController |
| 服务接口 | Service | UserService |
| 服务实现 | ServiceImpl | UserServiceImpl |
| 数据访问 | Repository | UserRepository |
| 实体类 | Entity 或无后缀 | User |
| DTO | DTO | UserDTO |
| VO | VO | UserVO |
| 配置类 | Config | SecurityConfig |
| 工具类 | Utils | DateUtils |
| 异常类 | Exception | BusinessException |
| 枚举类 | Enum 或无后缀 | UserStatus |

### 2.3 示例

```java
// 控制器
@RestController
@RequestMapping("/api/system/v1/users")
public class UserController {
    // ...
}

// 服务接口
public interface UserService {
    User getById(String id);
    void save(User user);
}

// 服务实现
@Service
public class UserServiceImpl implements UserService {
    // ...
}

// 数据访问
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // ...
}

// 实体类
@Entity
@Table(name = "sys_user")
public class User {
    // ...
}

// DTO
public class UserDTO {
    // ...
}

// VO
public class UserVO {
    // ...
}
```

---

## 3. 方法命名规范

### 3.1 命名风格

使用 **camelCase**，方法名应清晰表达其功能。

### 3.2 常用方法前缀

| 前缀 | 含义 | 示例 |
|------|------|------|
| get | 获取单个对象 | getById, getByName |
| list | 获取列表 | listAll, listByDept |
| find | 查询 | findById, findByName |
| save | 保存 | save, saveAll |
| create | 创建 | createUser |
| update | 更新 | updateUser |
| delete | 删除 | deleteById |
| remove | 移除 | removeById |
| count | 统计数量 | countByStatus |
| exists | 判断存在 | existsByName |
| check | 检查 | checkPermission |
| validate | 验证 | validatePassword |
| convert | 转换 | convertToDTO |
| build | 构建 | buildQuery |

### 3.3 测试方法命名

```java
// 格式: should_{期望行为}_when_{条件}
@Test
void should_createUser_when_validData() {
    // ...
}

@Test
void should_throwException_when_userNotFound() {
    // ...
}
```

---

## 4. 变量命名规范

### 4.1 命名风格

- 使用 **camelCase**
- 变量名应具有描述性
- 避免使用单字母(循环变量除外)
- 避免使用缩写

### 4.2 成员变量

```java
public class User {
    private String id;               // 用户ID
    private String userName;         // 用户名
    private String loginName;        // 登录账号
    private String password;         // 密码
    private String deptId;           // 科室ID
    private Integer status;          // 状态
    private LocalDateTime createTime; // 创建时间
}
```

### 4.3 常量命名

```java
public class Constants {
    // 系统常量
    public static final String SYSTEM_NAME = "HIS";
    public static final int MAX_RETRY_COUNT = 3;
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    // 状态常量
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
}
```

---

## 5. 分层架构规范

### 5.1 分层结构

```
Controller -> Service -> Repository -> Entity
```

### 5.2 各层职责

| 层 | 职责 | 注解 |
|------|------|------|
| Controller | 处理HTTP请求，参数校验，响应封装 | @RestController |
| Service | 业务逻辑处理，事务管理 | @Service |
| Repository | 数据访问，CRUD操作 | @Repository |
| Entity | 数据库映射，数据实体 | @Entity |
| DTO | 数据传输对象 | - |
| VO | 视图对象 | - |

### 5.3 Controller 规范

```java
@RestController
@RequestMapping("/api/system/v1/users")
@Tag(name = "用户管理", description = "用户管理接口")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    public Result<UserVO> getById(@PathVariable String id) {
        UserVO user = userService.getById(id);
        return Result.success(user);
    }
    
    @PostMapping
    @Operation(summary = "创建用户")
    public Result<UserVO> create(@RequestBody @Valid UserDTO dto) {
        UserVO user = userService.create(dto);
        return Result.success(user);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public Result<Void> update(@PathVariable String id, 
                               @RequestBody @Valid UserDTO dto) {
        userService.update(id, dto);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return Result.success();
    }
}
```

### 5.4 Service 规范

```java
public interface UserService {
    UserVO getById(String id);
    UserVO create(UserDTO dto);
    void update(String id, UserDTO dto);
    void delete(String id);
    PageResult<UserVO> list(UserQuery query);
}

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserVO getById(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        return convertToVO(user);
    }
    
    @Override
    public UserVO create(UserDTO dto) {
        // 校验
        validateUser(dto);
        // 转换
        User user = convertToEntity(dto);
        // 保存
        user = userRepository.save(user);
        return convertToVO(user);
    }
    
    // ... 其他方法
}
```

---

## 6. 注解使用规范

### 6.1 常用注解

```java
// 控制器
@RestController
@RequestMapping("/api/xxx/v1/xxx")
@Tag(name = "模块名", description = "模块描述")

// 服务
@Service
@Transactional

// 数据访问
@Repository

// 实体
@Entity
@Table(name = "table_name")

// 字段验证
@NotNull
@NotBlank
@Size(min = 1, max = 50)
@Pattern(regexp = "xxx")
@Email

// API文档
@Operation(summary = "接口描述")
@Parameter(description = "参数描述")

// 其他
@Autowired
@Value("${xxx}")
@ConfigurationProperties(prefix = "xxx")
```

### 6.2 事务注解

```java
@Service
public class UserServiceImpl implements UserService {
    
    @Transactional(readOnly = true)  // 只读事务
    public User getById(String id) {
        // ...
    }
    
    @Transactional  // 写事务
    public User create(UserDTO dto) {
        // ...
    }
    
    @Transactional(rollbackFor = Exception.class)  // 指定回滚异常
    public void batchDelete(List<String> ids) {
        // ...
    }
}
```

---

## 7. 异常处理规范

### 7.1 自定义异常

```java
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final String message;
    
    public BusinessException(String message) {
        super(message);
        this.code = 2001;
        this.message = message;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
```

### 7.2 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return Result.error(1001, message);
    }
    
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(5001, "系统异常");
    }
}
```

---

## 8. 日志规范

### 8.1 日志级别

| 级别 | 使用场景 |
|------|----------|
| ERROR | 错误，需要立即处理 |
| WARN | 警告，潜在问题 |
| INFO | 重要业务信息 |
| DEBUG | 调试信息(开发环境) |
| TRACE | 详细追踪信息 |

### 8.2 日志使用

```java
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    public User create(UserDTO dto) {
        log.info("创建用户, dto={}", dto);
        
        try {
            User user = convertToEntity(dto);
            user = userRepository.save(user);
            log.info("用户创建成功, id={}", user.getId());
            return user;
        } catch (Exception e) {
            log.error("创建用户失败, dto={}", dto, e);
            throw new BusinessException("创建用户失败");
        }
    }
}
```

---

## 9. 代码注释规范

### 9.1 类注释

```java
/**
 * 用户服务实现类
 * 
 * @author author
 * @since 1.0.0
 */
@Service
public class UserServiceImpl implements UserService {
    // ...
}
```

### 9.2 方法注释

```java
/**
 * 根据ID获取用户
 * 
 * @param id 用户ID
 * @return 用户信息
 * @throws BusinessException 用户不存在时抛出
 */
public UserVO getById(String id) {
    // ...
}
```

### 9.3 代码注释

```java
public void process() {
    // 1. 数据校验
    validate();
    
    // 2. 业务处理
    doProcess();
    
    // 3. 结果处理
    handleResult();
    
    /* 
     * 复杂逻辑说明:
     * 1. xxx
     * 2. xxx
     */
    complexLogic();
}
```

---

## 10. 代码格式规范

### 10.1 缩进

- 使用 4 个空格缩进
- 不使用 Tab

### 10.2 行宽

- 最大行宽 120 字符

### 10.3 空行

- 方法之间保留一个空行
- 逻辑块之间保留一个空行

### 10.4 导入顺序

```java
// 1. java.*
import java.util.List;
import java.util.Map;

// 2. javax.*
import javax.persistence.Entity;

// 3. 第三方包
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

// 4. 项目包
import com.yhj.his.common.core.Result;
import com.yhj.his.module.system.entity.User;
```

---

**文档维护**: HIS Platform Team
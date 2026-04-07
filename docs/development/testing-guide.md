# 测试编写指南

> **版本**: v1.2.0  
> **最后更新**: 2026-04-06

---

## 1. 测试概述

### 1.1 测试分层

```
E2E 测试 (端到端测试)
    │
    └── API 接口测试
            │
            └── 单元测试 (方法级测试)
```

### 1.2 覆盖率要求

| 指标 | 最低要求 | 目标值 |
|------|----------|--------|
| 行覆盖率 | 90% | 95% |
| 分支覆盖率 | 90% | 95% |
| 方法覆盖率 | 85% | 90% |

---

## 2. 单元测试

### 2.1 测试框架

```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2.2 测试目录结构

```
src/test/java/com/yhj/his/module/{module}/
├── controller/           # 控制器测试
│   └── *ControllerTest.java
├── service/              # 服务层测试
│   └── impl/
│       └── *ServiceImplTest.java
├── repository/           # 数据访问测试
│   └── *RepositoryTest.java
└── integration/          # 集成测试
    └── *IntegrationTest.java
```

### 2.3 测试类命名规范

- 测试类: `{ClassName}Test.java`
- 测试方法: `should_{expectedBehavior}_when_{condition}`

### 2.4 Service 层测试示例

```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    void should_returnUser_when_getById_withValidId() {
        // Given
        String userId = "USR001";
        User user = new User();
        user.setId(userId);
        user.setUserName("测试用户");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        // When
        UserVO result = userService.getById(userId);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("测试用户", result.getUserName());
        
        verify(userRepository).findById(userId);
    }
    
    @Test
    void should_throwException_when_getById_withInvalidId() {
        // Given
        String userId = "INVALID_ID";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.getById(userId);
        });
        
        verify(userRepository).findById(userId);
    }
    
    @Test
    void should_createUser_when_create_withValidData() {
        // Given
        UserDTO dto = new UserDTO();
        dto.setUserName("新用户");
        dto.setLoginName("newuser");
        
        User savedUser = new User();
        savedUser.setId("USR002");
        savedUser.setUserName("新用户");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        UserVO result = userService.create(dto);
        
        // Then
        assertNotNull(result);
        assertEquals("USR002", result.getId());
        
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void should_throwException_when_create_withDuplicateLoginName() {
        // Given
        UserDTO dto = new UserDTO();
        dto.setLoginName("existing");
        
        when(userRepository.existsByLoginName("existing")).thenReturn(true);
        
        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.create(dto);
        });
    }
}
```

### 2.5 Controller 层测试示例

```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void should_returnUser_when_getById() throws Exception {
        // Given
        UserVO user = new UserVO();
        user.setId("USR001");
        user.setUserName("测试用户");
        
        when(userService.getById("USR001")).thenReturn(user);
        
        // When & Then
        mockMvc.perform(get("/api/system/v1/users/USR001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.id").value("USR001"))
            .andExpect(jsonPath("$.data.userName").value("测试用户"));
    }
    
    @Test
    void should_createUser_when_postWithValidData() throws Exception {
        // Given
        UserDTO dto = new UserDTO();
        dto.setUserName("新用户");
        dto.setLoginName("newuser");
        
        UserVO created = new UserVO();
        created.setId("USR002");
        created.setUserName("新用户");
        
        when(userService.create(any(UserDTO.class))).thenReturn(created);
        
        // When & Then
        mockMvc.perform(post("/api/system/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.id").value("USR002"));
    }
}
```

### 2.6 Repository 层测试示例

```java
@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void should_findUser_when_findById() {
        // Given
        User user = new User();
        user.setUserName("测试用户");
        user.setLoginName("test");
        user.setPassword("password");
        user = entityManager.persistAndFlush(user);
        
        // When
        Optional<User> found = userRepository.findById(user.getId());
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("测试用户", found.get().getUserName());
    }
    
    @Test
    void should_findUser_when_findByLoginName() {
        // Given
        User user = new User();
        user.setUserName("测试用户");
        user.setLoginName("testuser");
        user.setPassword("password");
        entityManager.persistAndFlush(user);
        
        // When
        Optional<User> found = userRepository.findByLoginName("testuser");
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getLoginName());
    }
}
```

---

## 3. 测试要求

### 3.1 测试覆盖要求

每个公共方法必须有测试覆盖:

1. **正向测试** (Happy Path)
   - 正常输入，验证正确输出

2. **负向测试** (Negative Cases)
   - 异常输入，验证错误处理
   - 边界条件
   - 空值/null 处理

3. **边界测试** (Edge Cases)
   - 最大/最小值
   - 空集合
   - 边界条件

### 3.2 测试方法结构

使用 **Given-When-Then** 模式:

```java
@Test
void should_xxx_when_xxx() {
    // Given - 准备测试数据和环境
    
    // When - 执行被测试方法
    
    // Then - 验证结果
}
```

---

## 4. E2E 测试

### 4.1 测试框架

- 语言: Python 3.11+
- HTTP 客户端: requests
- 报告: pytest + pytest-html

### 4.2 测试脚本位置

```
scripts/
├── e2e_test.py           # E2E测试主脚本
├── test_config.py        # 测试配置
└── test_data/            # 测试数据
    ├── system.json
    ├── outpatient.json
    └── inpatient.json
```

### 4.3 E2E 测试示例

```python
import pytest
import requests

BASE_URL = "http://localhost:8080"

class TestSystemAuth:
    """系统认证测试"""
    
    def test_login_success(self):
        """测试登录成功"""
        response = requests.post(
            f"{BASE_URL}/api/system/v1/auth/login",
            json={
                "loginName": "admin",
                "password": "admin123"
            }
        )
        assert response.status_code == 200
        data = response.json()
        assert data["code"] == 0
        assert "token" in data["data"]
    
    def test_login_failed_with_wrong_password(self):
        """测试密码错误登录失败"""
        response = requests.post(
            f"{BASE_URL}/api/system/v1/auth/login",
            json={
                "loginName": "admin",
                "password": "wrong_password"
            }
        )
        assert response.status_code == 200
        data = response.json()
        assert data["code"] == 3001


class TestOutpatient:
    """门诊模块测试"""
    
    @pytest.fixture
    def auth_header(self):
        """获取认证头"""
        response = requests.post(
            f"{BASE_URL}/api/system/v1/auth/login",
            json={"loginName": "admin", "password": "admin123"}
        )
        token = response.json()["data"]["token"]
        return {"Authorization": f"Bearer {token}"}
    
    def test_create_registration(self, auth_header):
        """测试挂号"""
        response = requests.post(
            f"{BASE_URL}/api/outpatient/v1/registrations",
            headers=auth_header,
            json={
                "patientId": "P001",
                "deptId": "D001",
                "scheduleDate": "2026-04-07"
            }
        )
        assert response.status_code == 200
        data = response.json()
        assert data["code"] == 0
        assert "registrationId" in data["data"]
```

### 4.4 运行 E2E 测试

```bash
# 运行所有 E2E 测试
python3 scripts/e2e_test.py

# 使用 pytest 运行
pytest scripts/e2e_test.py -v --html=report/e2e/e2e_report.html
```

---

## 5. 测试报告

### 5.1 报告目录结构

```
report/
├── unit/                    # 单元测试报告
│   ├── unit_report.html     # HTML 报告
│   └── unit_report.xml      # JUnit XML
├── e2e/                     # E2E 测试报告
│   ├── e2e_report.html      # HTML 报告
│   └── e2e_report.json      # JSON 报告
├── coverage/                # 覆盖率报告
│   ├── index.html           # JaCoCo HTML
│   └── jacoco.xml           # XML 格式
└── test_summary_*.md        # 汇总报告
```

### 5.2 生成测试报告

```bash
# 运行单元测试并生成覆盖率报告
mvn test jacoco:report

# 查看覆盖率报告
open target/site/jacoco/index.html

# 运行 E2E 测试
python3 scripts/e2e_test.py

# 生成汇总报告
python3 scripts/report_generator.py
```

---

## 6. 测试命令

### 6.1 Maven 命令

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=UserServiceTest

# 运行指定测试方法
mvn test -Dtest=UserServiceTest#testGetById

# 跳过测试
mvn package -DskipTests

# 生成覆盖率报告
mvn test jacoco:report

# 指定配置文件
mvn test -Dspring.profiles.active=test
```

### 6.2 测试覆盖率检查

```bash
# 检查覆盖率是否达标
mvn verify jacoco:check
```

---

## 7. 持续集成

### 7.1 CI 流水线

```yaml
# .github/workflows/test.yml
name: Test

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Run tests
        run: mvn test jacoco:report
      
      - name: Check coverage
        run: mvn verify jacoco:check
      
      - name: Upload coverage
        uses: codecov/codecov-action@v3
```

### 7.2 质量门禁

测试必须满足以下条件才能通过:

- [ ] 所有单元测试通过
- [ ] 行覆盖率 >= 90%
- [ ] 分支覆盖率 >= 90%
- [ ] 所有 E2E 测试通过
- [ ] 无严重安全问题

---

## 8. 最佳实践

### 8.1 测试原则

1. **独立性**: 测试之间相互独立
2. **可重复**: 测试结果稳定可重复
3. **快速**: 测试执行速度快
4. **清晰**: 测试代码清晰易懂
5. **完整**: 覆盖所有场景

### 8.2 测试数据管理

1. 使用独立的测试数据
2. 测试完成后清理数据
3. 使用 @Transactional 自动回滚

### 8.3 Mock 使用

1. 只 Mock 外部依赖
2. 不要 Mock 被测试类
3. 验证 Mock 调用

---

**文档维护**: HIS Platform Team
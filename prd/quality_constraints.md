# HIS Platform System Constraints

## Quality Assurance Specification

This document defines the quality constraints and testing requirements for the HIS Platform.

---

## 1. Code Quality Requirements

### 1.1 Package Naming
- All code must use the package prefix: `com.yhj.his`
- Module structure: `com.yhj.his.module.{module_name}`
- Common utilities: `com.yhj.his.common.*`

### 1.2 Code Style
- Follow Java 21 best practices
- Use Lombok for boilerplate reduction
- Proper exception handling with BusinessException
- All public methods must have Javadoc comments

---

## 2. Unit Testing Requirements

### 2.1 Coverage Requirements
| Metric | Minimum Requirement | Target |
|--------|---------------------|--------|
| Line Coverage | 90% | 95% |
| Branch Coverage | 90% | 95% |
| Method Coverage | 85% | 90% |

### 2.2 Test Structure
```
src/test/java/com/yhj/his/module/{module}/
├── service/           # Service layer tests
│   ├── *ServiceTest.java
│   └── impl/
│       └── *ServiceImplTest.java
├── controller/        # Controller tests
│   └── *ControllerTest.java
├── repository/        # Repository tests
│   └── *RepositoryTest.java
└── integration/       # Integration tests
    └── *IntegrationTest.java
```

### 2.3 Test Naming Convention
- Test class: `{ClassName}Test.java`
- Test method: `should_{expectedBehavior}_when_{condition}`
- Example: `should_createUser_when_validData()`

### 2.4 Test Requirements per Class
- Every public method must have at least one test
- Positive test cases (happy path)
- Negative test cases (error scenarios)
- Edge cases and boundary conditions
- Exception handling tests

### 2.5 Test Framework Stack
```xml
<!-- Test dependencies -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 3. E2E Testing Requirements

### 3.1 E2E Test Scope
All E2E tests must validate:
- API endpoint availability
- Request/Response format
- Business logic correctness
- Data persistence
- Error handling

### 3.2 E2E Test Modules
| Module | Core Test Scenarios |
|--------|---------------------|
| System | Authentication, User management, Role permissions |
| Outpatient | Patient registration, Appointment, Queue management |
| Inpatient | Admission, Bed management, Discharge |
| Pharmacy | Drug management, Inventory, Dispensing |
| LIS | Test requests, Sample tracking, Results |
| PACS | Exam requests, Report generation |
| Finance | Billing, Payment, Invoicing |
| EMR | Record creation, Template management |

### 3.3 E2E Test Framework
- Language: Python 3.11+
- HTTP Client: requests library
- Report format: HTML, JSON, Markdown
- Location: `scripts/e2e_test.py`

### 3.4 E2E Test Requirements
```python
# Required test scenarios per module
MINIMUM_E2E_TESTS = {
    'system': 5,      # Auth, User, Role, Dept, Dict
    'outpatient': 5,  # Patient, Schedule, Registration, Record, Billing
    'inpatient': 4,   # Admission, Bed, Order, Discharge
    'pharmacy': 4,    # Drug, Inventory, Dispense, Purchase
    'lis': 4,         # TestItem, Request, Sample, Result
    'pacs': 4,        # Request, Report, Image, Equipment
    'finance': 5,     # Price, Billing, Payment, Invoice, Settlement
    'emr': 4,         # Template, Record, Progress, QC
}
```

---

## 4. Test Report Requirements

### 4.1 Report Structure
```
report/
├── unit/                    # Unit test reports
│   ├── unit_report_*.html   # Detailed HTML report
│   └── unit_report_*.xml    # JUnit XML format
├── e2e/                     # E2E test reports
│   ├── e2e_report_*.html    # Visual HTML report
│   ├── e2e_report_*.json    # Machine-readable JSON
│   └── e2e_summary_*.txt    # Quick text summary
├── coverage/                # Code coverage reports
│   ├── index.html           # JaCoCo HTML report
│   ├── jacoco.xml           # XML format
│   └── jacoco.csv           # CSV format
├── security/                # Security scan reports
│   └── dependency-check/    # OWASP dependency check
└── test_summary_*.md        # Consolidated summary
```

### 4.2 Report Content Requirements
Each test report must include:
1. **Executive Summary**
   - Total tests run
   - Pass/Fail counts
   - Coverage percentages
   - Quality gate status

2. **Detailed Results**
   - Per-module test results
   - Failed test details with stack traces
   - Coverage breakdown by package

3. **Trend Analysis**
   - Historical comparison
   - Coverage trend over time

### 4.3 Report Generation
```bash
# Generate all reports
./scripts/run_tests.sh

# Generate unit test report only
mvn test jacoco:report

# Generate E2E test report only
python3 scripts/e2e_test.py

# Generate consolidated summary
python3 scripts/report_generator.py
```

---

## 5. CI/CD Requirements

### 5.1 Pipeline Stages
```yaml
stages:
  - build       # Compile and package
  - unit-test   # Unit tests with coverage
  - e2e-test    # E2E tests
  - quality-gate # Coverage and quality checks
  - security    # Security scanning
  - deploy      # Deployment (if all pass)
```

### 5.2 Quality Gates
All quality gates must pass for deployment:

| Gate | Condition |
|------|-----------|
| Build | No compilation errors |
| Unit Tests | 100% pass rate |
| Line Coverage | >= 90% |
| Branch Coverage | >= 90% |
| E2E Tests | 100% pass rate |
| Security | No critical vulnerabilities |

### 5.3 Failure Handling
- Build failure → Block merge
- Test failure → Block merge, generate detailed report
- Coverage below threshold → Warning, allow merge with approval
- Security issues → Block merge if critical

---

## 6. Test Data Management

### 6.1 Test Data Requirements
- Isolated test data for each test class
- No dependency on production data
- Use in-memory database (H2) for unit tests
- Use test SQLite files for integration tests

### 6.2 Test Data Cleanup
- All tests must clean up created data
- Use @Transactional for automatic rollback
- Explicit cleanup in @AfterEach methods

---

## 7. Documentation Requirements

### 7.1 Test Documentation
Each test class must have:
- Class-level Javadoc describing test scope
- Method-level Javadoc for test purpose
- Inline comments for complex test logic

### 7.2 README Updates
Update README with:
- How to run tests locally
- How to view reports
- Coverage badges

---

## 8. Continuous Improvement

### 8.1 Coverage Monitoring
- Weekly coverage trend reports
- Alert if coverage drops below 90%
- Reward high coverage modules

### 8.2 Test Maintenance
- Review and update tests quarterly
- Remove obsolete tests
- Add tests for new features immediately
- Refactor flaky tests

---

## 9. Compliance Checklist

Before each release:

- [ ] All unit tests pass
- [ ] Line coverage >= 90%
- [ ] Branch coverage >= 90%
- [ ] All E2E tests pass
- [ ] No critical security vulnerabilities
- [ ] Test reports generated
- [ ] Documentation updated

---

## 10. Tools and Commands

### 10.1 Maven Commands
```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with specific profile
mvn test -Dspring.profiles.active=test
```

### 10.2 Report Viewing
```bash
# View coverage report
open target/site/jacoco/index.html

# View E2E report
open report/e2e_report_*.html

# Generate consolidated summary
python3 scripts/report_generator.py
```

---

**Document Version:** 1.0  
**Last Updated:** 2026-04-06  
**Maintained by:** HIS Platform Team
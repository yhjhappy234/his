# Test Report Index

This directory contains all test reports generated during the CI/CD pipeline.

## Report Structure

```
report/
├── unit/               # Unit test reports
│   └── unit_report_YYYYMMDD_HHMMSS.html
├── e2e/                # E2E test reports
│   ├── e2e_report_YYYYMMDD_HHMMSS.html
│   ├── e2e_report_YYYYMMDD_HHMMSS.json
│   └── e2e_summary_YYYYMMDD_HHMMSS.txt
├── coverage/           # Code coverage reports (JaCoCo)
│   └── index.html
├── security/           # Security scan reports
│   └── dependency-check-report.html
└── test_summary_YYYYMMDD.md  # Overall summary
```

## Quality Gates

| Metric | Target | Description |
|--------|--------|-------------|
| Line Coverage | >= 90% | Percentage of code lines covered by tests |
| Branch Coverage | >= 90% | Percentage of branches covered by tests |
| Unit Test Pass Rate | 100% | All unit tests must pass |
| E2E Test Pass Rate | 100% | All E2E tests must pass |

## Running Tests

### Unit Tests with Coverage
```bash
mvn clean test jacoco:report
```

### E2E Tests
```bash
cd scripts
pip install -r requirements.txt
python3 e2e_test.py
```

### Full Test Suite
```bash
./scripts/run_tests.sh
```

## Report Interpretation

### JaCoCo Coverage Report
- Open `coverage/index.html` in a browser
- Green = covered code
- Red = uncovered code
- Yellow = partially covered branches

### E2E Test Report
- HTML report: Visual test results with charts
- JSON report: Machine-readable results for CI integration
- Summary: Quick text overview

## CI/CD Integration

Reports are automatically generated and uploaded as artifacts in the GitHub Actions pipeline.

- View in Actions tab under "Artifacts"
- Download and extract to view locally
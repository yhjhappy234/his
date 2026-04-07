# HIS Platform E2E Test Suite

## Overview

End-to-End testing framework for HIS (Hospital Information System) platform core business logic validation.

## Directory Structure

```
e2e_test/
├── README.md                         # This file
├── requirements.txt                  # Python dependencies
├── .env.example                      # Environment variables template
├── conftest.py                       # pytest global configuration & fixtures
├── config/
│   ├── base_config.py                # Base configuration
│   └── test_data.py                  # Test data constants
├── utils/
│   ├── http_client.py                # HTTP request client wrapper
│   ├── assertions.py                 # Custom assertion utilities
│   └── data_factory.py               # Test data generation factory
└── tests/
    ├── test_system_api.py            # System module E2E tests
    ├── test_outpatient_api.py        # Outpatient module E2E tests
    └── test_pharmacy_api.py          # Pharmacy module E2E tests
```

## Prerequisites

- Python 3.8+
- HIS Platform running and accessible
- Valid test user credentials

## Installation

```bash
# Create virtual environment (optional)
python3 -m venv venv
source venv/bin/activate  # Linux/Mac
# or
.\venv\Scripts\activate   # Windows

# Install dependencies
pip install -r requirements.txt
```

## Configuration

1. Copy the environment template:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` with your settings:
   ```env
   HIS_BASE_URL=http://localhost:8080
   HIS_TEST_USER=admin
   HIS_TEST_PASSWORD=admin123
   ```

## Running Tests

### Run all tests
```bash
pytest
```

### Run with HTML report
```bash
pytest --html=report.html --self-contained-html
```

### Run specific module tests
```bash
# System module tests
pytest tests/test_system_api.py -v

# Outpatient module tests
pytest tests/test_outpatient_api.py -v

# Pharmacy module tests
pytest tests/test_pharmacy_api.py -v
```

### Run with markers
```bash
# Run only smoke tests
pytest -m smoke

# Run only critical tests
pytest -m critical

# Skip slow tests
pytest -m "not slow"
```

## Test Modules

### System Module (`test_system_api.py`)
- Health check endpoint
- User CRUD operations
- Role management
- Department tree structure
- Data dictionary queries

### Outpatient Module (`test_outpatient_api.py`)
- Patient registration
- Schedule queries
- Registration flow

### Pharmacy Module (`test_pharmacy_api.py`)
- Drug queries
- Drug search
- Inventory management

## Writing New Tests

1. Create test file in `tests/` directory following naming convention `test_*.py`
2. Import fixtures from `conftest.py`
3. Use `http_client` fixture for API calls
4. Use `assertions` utilities for validation

Example:
```python
def test_new_feature(http_client, assertions):
    """Test description"""
    response = http_client.get("/api/module/v1/endpoint")
    assertions.assert_success(response)
    assertions.assert_response_time(response, max_ms=1000)
```

## Reports

Test reports are generated in the project's `report/` directory:
- JSON report: Detailed results in JSON format
- HTML report: Visual test report
- Summary report: Executive summary

## CI/CD Integration

Tests can be integrated into CI/CD pipelines:

```yaml
# Example GitHub Actions
- name: Run E2E Tests
  run: |
    pip install -r scripts/e2e_test/requirements.txt
    pytest scripts/e2e_test/ --html=report.html
```

## Troubleshooting

### Connection refused
- Ensure HIS Platform is running
- Check `HIS_BASE_URL` in `.env`

### Authentication failed
- Verify test user credentials
- Check user has required permissions

### Test timeout
- Increase timeout in `config/base_config.py`
- Check system performance
#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HIS Platform E2E Test Framework
End-to-End testing for core business logic validation
"""

import json
import os
import sys
import time
import requests
import logging
from datetime import datetime
from typing import Dict, List, Any, Optional
from dataclasses import dataclass, field
from enum import Enum
import traceback

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Test configuration
BASE_URL = os.getenv('HIS_BASE_URL', 'http://localhost:8080')
REPORT_DIR = os.path.join(os.path.dirname(os.path.dirname(__file__)), 'report')

class TestStatus(Enum):
    PASSED = "PASSED"
    FAILED = "FAILED"
    SKIPPED = "SKIPPED"
    ERROR = "ERROR"

@dataclass
class TestResult:
    name: str
    module: str
    status: TestStatus
    duration_ms: float
    message: str = ""
    error: str = ""
    request_data: Dict = field(default_factory=dict)
    response_data: Dict = field(default_factory=dict)

@dataclass
class TestReport:
    test_suite: str
    start_time: str
    end_time: str
    total_tests: int
    passed: int
    failed: int
    skipped: int
    errors: int
    duration_ms: float
    results: List[TestResult] = field(default_factory=list)

    def to_dict(self):
        return {
            "test_suite": self.test_suite,
            "start_time": self.start_time,
            "end_time": self.end_time,
            "summary": {
                "total_tests": self.total_tests,
                "passed": self.passed,
                "failed": self.failed,
                "skipped": self.skipped,
                "errors": self.errors,
                "pass_rate": f"{(self.passed/self.total_tests*100):.2f}%" if self.total_tests > 0 else "0%",
                "duration_ms": round(self.duration_ms, 2)
            },
            "results": [
                {
                    "name": r.name,
                    "module": r.module,
                    "status": r.status.value,
                    "duration_ms": round(r.duration_ms, 2),
                    "message": r.message,
                    "error": r.error
                }
                for r in self.results
            ]
        }

class HISClient:
    """HIS Platform API Client"""

    def __init__(self, base_url: str):
        self.base_url = base_url
        self.session = requests.Session()
        self.token = None
        self.headers = {"Content-Type": "application/json"}

    def login(self, username: str, password: str) -> bool:
        """Login and get authentication token"""
        try:
            response = self.session.post(
                f"{self.base_url}/api/system/v1/user/login",
                json={"loginName": username, "password": password},
                headers=self.headers
            )
            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 0:
                    self.token = data.get("data", {}).get("token")
                    if self.token:
                        self.headers["Authorization"] = f"Bearer {self.token}"
                    return True
            return False
        except Exception as e:
            logger.error(f"Login failed: {e}")
            return False

    def get(self, path: str, params: Dict = None) -> requests.Response:
        """GET request"""
        return self.session.get(f"{self.base_url}{path}", headers=self.headers, params=params)

    def post(self, path: str, data: Dict = None) -> requests.Response:
        """POST request"""
        return self.session.post(f"{self.base_url}{path}", json=data, headers=self.headers)

    def put(self, path: str, data: Dict = None) -> requests.Response:
        """PUT request"""
        return self.session.put(f"{self.base_url}{path}", json=data, headers=self.headers)

    def delete(self, path: str) -> requests.Response:
        """DELETE request"""
        return self.session.delete(f"{self.base_url}{path}", headers=self.headers)

class BaseTestCase:
    """Base test case class"""

    def __init__(self, client: HISClient):
        self.client = client
        self.results: List[TestResult] = []

    def run_test(self, name: str, test_func, *args, **kwargs) -> TestResult:
        """Run a single test"""
        start_time = time.time()
        try:
            test_func(*args, **kwargs)
            duration = (time.time() - start_time) * 1000
            return TestResult(
                name=name,
                module=self.__class__.__name__,
                status=TestStatus.PASSED,
                duration_ms=duration,
                message="Test passed successfully"
            )
        except AssertionError as e:
            duration = (time.time() - start_time) * 1000
            return TestResult(
                name=name,
                module=self.__class__.__name__,
                status=TestStatus.FAILED,
                duration_ms=duration,
                message=str(e),
                error=traceback.format_exc()
            )
        except Exception as e:
            duration = (time.time() - start_time) * 1000
            return TestResult(
                name=name,
                module=self.__class__.__name__,
                status=TestStatus.ERROR,
                duration_ms=duration,
                message="Test encountered an error",
                error=traceback.format_exc()
            )

class SystemModuleTests(BaseTestCase):
    """System module E2E tests"""

    def run_all(self) -> List[TestResult]:
        results = []
        results.append(self.run_test("test_health_check", self.test_health_check))
        results.append(self.run_test("test_user_crud", self.test_user_crud))
        results.append(self.run_test("test_role_management", self.test_role_management))
        results.append(self.run_test("test_department_tree", self.test_department_tree))
        results.append(self.run_test("test_dictionary_query", self.test_dictionary_query))
        return results

    def test_health_check(self):
        """Test system health endpoint"""
        response = self.client.get("/actuator/health")
        assert response.status_code == 200, f"Health check failed: {response.status_code}"

    def test_user_crud(self):
        """Test user CRUD operations"""
        # Create user
        user_data = {
            "userName": "test_user_e2e",
            "loginName": f"test_e2e_{int(time.time())}",
            "password": "Test@123456",
            "realName": "E2E Test User",
            "userType": "STAFF",
            "status": "NORMAL"
        }
        response = self.client.post("/api/system/v1/user", user_data)
        assert response.status_code in [200, 201], f"Create user failed: {response.text}"

        # Query user
        data = response.json()
        if data.get("code") == 0:
            user_id = data.get("data", {}).get("id")
            if user_id:
                response = self.client.get(f"/api/system/v1/user/{user_id}")
                assert response.status_code == 200, f"Get user failed"

                # Delete user
                response = self.client.delete(f"/api/system/v1/user/{user_id}")
                assert response.status_code == 200, f"Delete user failed"

    def test_role_management(self):
        """Test role management"""
        response = self.client.get("/api/system/v1/role")
        assert response.status_code == 200, f"Get roles failed: {response.status_code}"

    def test_department_tree(self):
        """Test department tree structure"""
        response = self.client.get("/api/system/v1/department/tree")
        assert response.status_code == 200, f"Get department tree failed"

    def test_dictionary_query(self):
        """Test data dictionary query"""
        response = self.client.get("/api/system/v1/dictionary/type/gender")
        assert response.status_code == 200, f"Get dictionary failed"

class OutpatientModuleTests(BaseTestCase):
    """Outpatient module E2E tests"""

    def run_all(self) -> List[TestResult]:
        results = []
        results.append(self.run_test("test_patient_registration", self.test_patient_registration))
        results.append(self.run_test("test_schedule_query", self.test_schedule_query))
        results.append(self.run_test("test_registration_flow", self.test_registration_flow))
        return results

    def test_patient_registration(self):
        """Test patient registration flow"""
        patient_data = {
            "name": "测试患者",
            "gender": "M",
            "idType": "ID_CARD",
            "idNo": f"110101199001010{int(time.time()) % 100}",
            "phone": "13800138000"
        }
        response = self.client.post("/api/outpatient/v1/patient/create", patient_data)
        assert response.status_code in [200, 201], f"Patient registration failed"

    def test_schedule_query(self):
        """Test schedule query"""
        response = self.client.get("/api/outpatient/v1/schedule", {"date": datetime.now().strftime("%Y-%m-%d")})
        assert response.status_code == 200, f"Schedule query failed"

    def test_registration_flow(self):
        """Test complete registration flow"""
        response = self.client.get("/api/outpatient/v1/registrations")
        assert response.status_code == 200, f"Registration list query failed"

class PharmacyModuleTests(BaseTestCase):
    """Pharmacy module E2E tests"""

    def run_all(self) -> List[TestResult]:
        results = []
        results.append(self.run_test("test_drug_query", self.test_drug_query))
        results.append(self.run_test("test_drug_search", self.test_drug_search))
        results.append(self.run_test("test_inventory_query", self.test_inventory_query))
        return results

    def test_drug_query(self):
        """Test drug query"""
        response = self.client.get("/api/pharmacy/v1/drugs", {"pageNum": 1, "pageSize": 10})
        assert response.status_code == 200, f"Drug query failed"

    def test_drug_search(self):
        """Test drug search"""
        response = self.client.get("/api/pharmacy/v1/drugs/search", {"keyword": "阿莫西林"})
        assert response.status_code == 200, f"Drug search failed"

    def test_inventory_query(self):
        """Test inventory query"""
        response = self.client.get("/api/pharmacy/v1/inventory")
        assert response.status_code == 200, f"Inventory query failed"

class LISModuleTests(BaseTestCase):
    """LIS module E2E tests"""

    def run_all(self) -> List[TestResult]:
        results = []
        results.append(self.run_test("test_test_items_query", self.test_test_items_query))
        results.append(self.run_test("test_sample_management", self.test_sample_management))
        return results

    def test_test_items_query(self):
        """Test items query"""
        response = self.client.get("/api/lis/v1/test-items")
        assert response.status_code == 200, f"Test items query failed"

    def test_sample_management(self):
        """Test sample management"""
        response = self.client.get("/api/lis/v1/samples")
        assert response.status_code == 200, f"Sample query failed"

class FinanceModuleTests(BaseTestCase):
    """Finance module E2E tests"""

    def run_all(self) -> List[TestResult]:
        results = []
        results.append(self.run_test("test_price_items_query", self.test_price_items_query))
        results.append(self.run_test("test_insurance_policy", self.test_insurance_policy))
        return results

    def test_price_items_query(self):
        """Test price items query"""
        response = self.client.get("/api/finance/v1/price-item")
        assert response.status_code == 200, f"Price items query failed"

    def test_insurance_policy(self):
        """Test insurance policy query"""
        response = self.client.get("/api/finance/v1/insurance-policy")
        assert response.status_code == 200, f"Insurance policy query failed"

class E2ETestRunner:
    """E2E Test Runner"""

    def __init__(self, base_url: str):
        self.client = HISClient(base_url)
        self.test_suites = [
            SystemModuleTests(self.client),
            OutpatientModuleTests(self.client),
            PharmacyModuleTests(self.client),
            LISModuleTests(self.client),
            FinanceModuleTests(self.client)
        ]

    def run_all_tests(self) -> TestReport:
        """Run all E2E tests"""
        start_time = datetime.now()
        all_results = []

        logger.info("=" * 60)
        logger.info("HIS Platform E2E Test Suite")
        logger.info(f"Base URL: {BASE_URL}")
        logger.info(f"Start Time: {start_time}")
        logger.info("=" * 60)

        # Login first
        login_success = self.client.login("admin", "admin123")
        if not login_success:
            logger.warning("Login failed, running tests without authentication")

        # Run each test suite
        for suite in self.test_suites:
            logger.info(f"\nRunning {suite.__class__.__name__}...")
            results = suite.run_all()
            all_results.extend(results)

            for result in results:
                status_icon = "✓" if result.status == TestStatus.PASSED else "✗"
                logger.info(f"  {status_icon} {result.name}: {result.status.value} ({result.duration_ms:.2f}ms)")

        end_time = datetime.now()
        duration = (end_time - start_time).total_seconds() * 1000

        # Calculate summary
        passed = sum(1 for r in all_results if r.status == TestStatus.PASSED)
        failed = sum(1 for r in all_results if r.status == TestStatus.FAILED)
        errors = sum(1 for r in all_results if r.status == TestStatus.ERROR)
        skipped = sum(1 for r in all_results if r.status == TestStatus.SKIPPED)

        report = TestReport(
            test_suite="HIS Platform E2E Tests",
            start_time=start_time.isoformat(),
            end_time=end_time.isoformat(),
            total_tests=len(all_results),
            passed=passed,
            failed=failed,
            skipped=skipped,
            errors=errors,
            duration_ms=duration,
            results=all_results
        )

        return report

    def save_report(self, report: TestReport, output_dir: str = None):
        """Save test report"""
        output_dir = output_dir or REPORT_DIR
        os.makedirs(output_dir, exist_ok=True)

        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")

        # Save JSON report
        json_file = os.path.join(output_dir, f"e2e_report_{timestamp}.json")
        with open(json_file, 'w', encoding='utf-8') as f:
            json.dump(report.to_dict(), f, indent=2, ensure_ascii=False)
        logger.info(f"JSON report saved: {json_file}")

        # Save HTML report
        html_file = os.path.join(output_dir, f"e2e_report_{timestamp}.html")
        self._generate_html_report(report, html_file)
        logger.info(f"HTML report saved: {html_file}")

        # Save summary
        summary_file = os.path.join(output_dir, f"e2e_summary_{timestamp}.txt")
        self._generate_summary_report(report, summary_file)
        logger.info(f"Summary report saved: {summary_file}")

        return json_file, html_file, summary_file

    def _generate_html_report(self, report: TestReport, output_file: str):
        """Generate HTML report"""
        html_template = """<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HIS Platform E2E Test Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { color: #333; border-bottom: 2px solid #4CAF50; padding-bottom: 10px; }
        .summary { display: grid; grid-template-columns: repeat(5, 1fr); gap: 15px; margin: 20px 0; }
        .stat-card { padding: 15px; border-radius: 8px; text-align: center; }
        .stat-card.total { background: #2196F3; color: white; }
        .stat-card.passed { background: #4CAF50; color: white; }
        .stat-card.failed { background: #f44336; color: white; }
        .stat-card.errors { background: #FF9800; color: white; }
        .stat-card.rate { background: #9C27B0; color: white; }
        .stat-card h3 { margin: 0; font-size: 32px; }
        .stat-card p { margin: 5px 0 0; opacity: 0.9; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background: #f5f5f5; font-weight: bold; }
        .status-passed { color: #4CAF50; font-weight: bold; }
        .status-failed { color: #f44336; font-weight: bold; }
        .status-error { color: #FF9800; font-weight: bold; }
        .duration { color: #666; font-size: 0.9em; }
    </style>
</head>
<body>
    <div class="container">
        <h1>HIS Platform E2E Test Report</h1>
        <p><strong>Test Suite:</strong> {test_suite}</p>
        <p><strong>Start Time:</strong> {start_time}</p>
        <p><strong>End Time:</strong> {end_time}</p>
        <p><strong>Duration:</strong> {duration_ms:.2f}ms</p>

        <div class="summary">
            <div class="stat-card total">
                <h3>{total_tests}</h3>
                <p>Total Tests</p>
            </div>
            <div class="stat-card passed">
                <h3>{passed}</h3>
                <p>Passed</p>
            </div>
            <div class="stat-card failed">
                <h3>{failed}</h3>
                <p>Failed</p>
            </div>
            <div class="stat-card errors">
                <h3>{errors}</h3>
                <p>Errors</p>
            </div>
            <div class="stat-card rate">
                <h3>{pass_rate}</h3>
                <p>Pass Rate</p>
            </div>
        </div>

        <h2>Test Results</h2>
        <table>
            <thead>
                <tr>
                    <th>Test Name</th>
                    <th>Module</th>
                    <th>Status</th>
                    <th>Duration</th>
                    <th>Message</th>
                </tr>
            </thead>
            <tbody>
                {test_rows}
            </tbody>
        </table>
    </div>
</body>
</html>"""

        test_rows = ""
        for r in report.results:
            status_class = f"status-{r.status.value.lower()}"
            test_rows += f"""
                <tr>
                    <td>{r.name}</td>
                    <td>{r.module}</td>
                    <td class="{status_class}">{r.status.value}</td>
                    <td class="duration">{r.duration_ms:.2f}ms</td>
                    <td>{r.message or '-'}</td>
                </tr>"""

        html_content = html_template.format(
            test_suite=report.test_suite,
            start_time=report.start_time,
            end_time=report.end_time,
            duration_ms=report.duration_ms,
            total_tests=report.total_tests,
            passed=report.passed,
            failed=report.failed,
            errors=report.errors,
            pass_rate=f"{(report.passed/report.total_tests*100):.1f}%" if report.total_tests > 0 else "0%",
            test_rows=test_rows
        )

        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(html_content)

    def _generate_summary_report(self, report: TestReport, output_file: str):
        """Generate summary text report"""
        content = f"""
{'=' * 60}
HIS Platform E2E Test Report
{'=' * 60}

Test Suite: {report.test_suite}
Start Time: {report.start_time}
End Time: {report.end_time}
Duration: {report.duration_ms:.2f}ms

Summary:
--------
Total Tests: {report.total_tests}
Passed: {report.passed}
Failed: {report.failed}
Errors: {report.errors}
Skipped: {report.skipped}
Pass Rate: {(report.passed/report.total_tests*100):.2f}%

Test Results:
--------
"""
        for r in report.results:
            content += f"\n[{r.status.value}] {r.module}.{r.name} ({r.duration_ms:.2f}ms)"
            if r.message:
                content += f"\n    Message: {r.message}"
            if r.error:
                content += f"\n    Error: {r.error[:200]}..."

        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(content)


def main():
    """Main entry point"""
    runner = E2ETestRunner(BASE_URL)
    report = runner.run_all_tests()

    # Save reports
    runner.save_report(report)

    # Print summary
    print("\n" + "=" * 60)
    print("Test Summary")
    print("=" * 60)
    print(f"Total: {report.total_tests}")
    print(f"Passed: {report.passed}")
    print(f"Failed: {report.failed}")
    print(f"Errors: {report.errors}")
    print(f"Pass Rate: {(report.passed/report.total_tests*100):.2f}%")
    print("=" * 60)

    # Exit with appropriate code
    sys.exit(0 if report.failed == 0 and report.errors == 0 else 1)


if __name__ == "__main__":
    main()
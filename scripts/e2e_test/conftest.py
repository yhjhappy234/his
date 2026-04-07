#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HIS Platform E2E Test Framework - pytest Configuration
Global fixtures and configuration for E2E tests
"""

import os
import pytest
import logging
from typing import Generator
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Import test components
import sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from utils.http_client import HISClient
from utils.assertions import Assertions
from utils.data_factory import DataFactory
from config.base_config import Config


def pytest_configure(config):
    """Configure pytest with custom markers"""
    config.addinivalue_line(
        "markers", "smoke: mark test as smoke test (quick validation)"
    )
    config.addinivalue_line(
        "markers", "critical: mark test as critical business function"
    )
    config.addinivalue_line(
        "markers", "slow: mark test as slow running"
    )
    config.addinivalue_line(
        "markers", "system: system module tests"
    )
    config.addinivalue_line(
        "markers", "outpatient: outpatient module tests"
    )
    config.addinivalue_line(
        "markers", "pharmacy: pharmacy module tests"
    )
    config.addinivalue_line(
        "markers", "lis: LIS module tests"
    )
    config.addinivalue_line(
        "markers", "finance: finance module tests"
    )


def pytest_collection_modifyitems(config, items):
    """Modify test collection"""
    # Add default marker to tests without markers
    for item in items:
        if not list(item.iter_markers()):
            item.add_marker(pytest.mark.smoke)


@pytest.fixture(scope="session")
def config() -> Config:
    """Load test configuration"""
    return Config()


@pytest.fixture(scope="session")
def http_client(config: Config) -> Generator[HISClient, None, None]:
    """
    Create HTTP client with authentication

    Yields:
        HISClient: Authenticated HTTP client for API testing
    """
    client = HISClient(
        base_url=config.base_url,
        timeout=config.timeout,
        verify_ssl=config.verify_ssl
    )

    # Authenticate
    username = os.getenv("HIS_TEST_USER", "admin")
    password = os.getenv("HIS_TEST_PASSWORD", "admin123")

    if not client.login(username, password):
        logging.warning("Authentication failed, running tests without token")

    yield client

    # Cleanup
    client.close()


@pytest.fixture
def assertions() -> Assertions:
    """
    Custom assertion utilities

    Returns:
        Assertions: Assertion helper class
    """
    return Assertions()


@pytest.fixture
def data_factory() -> DataFactory:
    """
    Test data generation factory

    Returns:
        DataFactory: Data factory for generating test data
    """
    return DataFactory()


@pytest.fixture(scope="session")
def test_context() -> dict:
    """
    Test context for sharing data between tests

    Returns:
        dict: Shared test context dictionary
    """
    return {}


@pytest.fixture(autouse=True)
def setup_logging():
    """Setup logging for each test"""
    log_level = os.getenv("HIS_LOG_LEVEL", "INFO")
    logging.basicConfig(
        level=getattr(logging, log_level),
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )


# Report generation hooks
@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """Hook to capture test results for reporting"""
    outcome = yield
    report = outcome.get_result()

    # Store test result in item for access in fixtures
    setattr(item, f"report_{call.when}", report)

    # Log failures
    if call.when == "call" and report.failed:
        logging.error(f"Test failed: {item.nodeid}")
        if hasattr(report, "longrepr"):
            logging.error(f"Reason: {report.longrepr}")


def pytest_terminal_summary(terminalreporter, exitstatus, config):
    """Generate summary at end of test run"""
    passed = len(terminalreporter.stats.get('passed', []))
    failed = len(terminalreporter.stats.get('failed', []))
    skipped = len(terminalreporter.stats.get('skipped', []))
    errors = len(terminalreporter.stats.get('error', []))
    total = passed + failed + skipped + errors

    terminalreporter.write_sep("=", "E2E Test Summary")
    terminalreporter.write_line(f"Total Tests: {total}")
    terminalreporter.write_line(f"Passed: {passed}")
    terminalreporter.write_line(f"Failed: {failed}")
    terminalreporter.write_line(f"Skipped: {skipped}")
    terminalreporter.write_line(f"Errors: {errors}")

    if total > 0:
        pass_rate = (passed / total) * 100
        terminalreporter.write_line(f"Pass Rate: {pass_rate:.2f}%")
    terminalreporter.write_sep("=", "")
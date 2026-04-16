#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HIS Platform E2E Test Framework - Base Configuration
Configuration settings for E2E tests
"""

import os
from typing import Optional
from dataclasses import dataclass, field


@dataclass
class Config:
    """Base configuration for E2E tests"""

    # API Configuration
    base_url: str = field(default_factory=lambda: os.getenv("HIS_BASE_URL", "http://localhost:8080"))

    # Timeouts (milliseconds)
    timeout: int = field(default_factory=lambda: int(os.getenv("HIS_TEST_TIMEOUT", "30000")))
    connection_timeout: int = 5000
    read_timeout: int = 25000

    # Retry Configuration
    retry_count: int = field(default_factory=lambda: int(os.getenv("HIS_TEST_RETRY_COUNT", "3")))
    retry_delay: int = field(default_factory=lambda: int(os.getenv("HIS_TEST_RETRY_DELAY", "1000")))

    # SSL Configuration
    verify_ssl: bool = field(default_factory=lambda: os.getenv("HIS_VERIFY_SSL", "false").lower() == "true")

    # Report Configuration
    report_dir: str = field(default_factory=lambda: os.getenv("HIS_REPORT_DIR", "../report"))

    # Logging
    log_level: str = field(default_factory=lambda: os.getenv("HIS_LOG_LEVEL", "INFO"))

    # API Endpoints
    api_version: str = "v1"

    @property
    def system_api_base(self) -> str:
        """System module API base path"""
        return f"/api/system/{self.api_version}"

    @property
    def outpatient_api_base(self) -> str:
        """Outpatient module API base path"""
        return f"/api/outpatient/{self.api_version}"

    @property
    def pharmacy_api_base(self) -> str:
        """Pharmacy module API base path"""
        return f"/api/pharmacy/{self.api_version}"

    @property
    def lis_api_base(self) -> str:
        """LIS module API base path"""
        return f"/api/lis/{self.api_version}"

    @property
    def finance_api_base(self) -> str:
        """Finance module API base path"""
        return f"/api/finance/{self.api_version}"

    # Default Test Credentials
    @property
    def test_username(self) -> str:
        """Default test username"""
        return os.getenv("HIS_TEST_USER", "admin")

    @property
    def test_password(self) -> str:
        """Default test password"""
        return os.getenv("HIS_TEST_PASSWORD", "123456")

    # Proxy Configuration
    @property
    def http_proxy(self) -> Optional[str]:
        """HTTP proxy URL"""
        return os.getenv("HIS_HTTP_PROXY")

    @property
    def https_proxy(self) -> Optional[str]:
        """HTTPS proxy URL"""
        return os.getenv("HIS_HTTPS_PROXY")


# Module-specific configurations
class SystemConfig:
    """System module specific configuration"""
    USER_ENDPOINT = "/user"
    ROLE_ENDPOINT = "/role"
    DEPARTMENT_ENDPOINT = "/department"
    DICTIONARY_ENDPOINT = "/dictionary"
    MENU_ENDPOINT = "/menu"


class OutpatientConfig:
    """Outpatient module specific configuration"""
    PATIENT_ENDPOINT = "/patient"
    REGISTRATION_ENDPOINT = "/registration"
    SCHEDULE_ENDPOINT = "/schedule"
    QUEUE_ENDPOINT = "/queue"


class PharmacyConfig:
    """Pharmacy module specific configuration"""
    DRUG_ENDPOINT = "/drugs"
    INVENTORY_ENDPOINT = "/inventory"
    PRESCRIPTION_ENDPOINT = "/prescription"
    DISPENSING_ENDPOINT = "/dispensing"


class LISConfig:
    """LIS module specific configuration"""
    TEST_ITEM_ENDPOINT = "/test-items"
    SAMPLE_ENDPOINT = "/samples"
    REPORT_ENDPOINT = "/reports"


class FinanceConfig:
    """Finance module specific configuration"""
    PRICE_ITEM_ENDPOINT = "/price-item"
    INSURANCE_ENDPOINT = "/insurance-policy"
    BILLING_ENDPOINT = "/billing"
    SETTLEMENT_ENDPOINT = "/settlement"
#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HIS Platform E2E Test Framework - Custom Assertions
Custom assertion utilities for E2E testing
"""

import logging
from typing import Any, Dict, List, Optional, Union
from utils.http_client import ResponseWrapper


class AssertionError(Exception):
    """Custom assertion error with detailed message"""
    pass


class Assertions:
    """
    Custom assertion utilities for E2E testing

    Provides domain-specific assertion methods for validating
    API responses and business logic.
    """

    def __init__(self):
        self.logger = logging.getLogger(__name__)

    # HTTP Status Assertions

    def assert_status_code(
        self,
        response: ResponseWrapper,
        expected: int,
        message: str = None
    ):
        """
        Assert HTTP status code

        Args:
            response: Response wrapper
            expected: Expected status code
            message: Custom error message
        """
        if response.status_code != expected:
            msg = message or f"Expected status code {expected}, got {response.status_code}"
            self.logger.error(f"{msg}. Response: {response.body}")
            raise AssertionError(msg)

    def assert_success(self, response: ResponseWrapper, message: str = None):
        """
        Assert response indicates success (2xx)

        Args:
            response: Response wrapper
            message: Custom error message
        """
        if not response.is_success():
            msg = message or f"Expected success status, got {response.status_code}"
            self.logger.error(f"{msg}. Response: {response.body}")
            raise AssertionError(msg)

    def assert_created(self, response: ResponseWrapper, message: str = None):
        """
        Assert response indicates resource created (201)

        Args:
            response: Response wrapper
            message: Custom error message
        """
        self.assert_status_code(response, 201, message or "Expected resource created")

    def assert_no_content(self, response: ResponseWrapper, message: str = None):
        """
        Assert response indicates no content (204)

        Args:
            response: Response wrapper
            message: Custom error message
        """
        self.assert_status_code(response, 204, message or "Expected no content")

    def assert_bad_request(self, response: ResponseWrapper, message: str = None):
        """
        Assert response indicates bad request (400)

        Args:
            response: Response wrapper
            message: Custom error message
        """
        self.assert_status_code(response, 400, message or "Expected bad request")

    def assert_unauthorized(self, response: ResponseWrapper, message: str = None):
        """
        Assert response indicates unauthorized (401)

        Args:
            response: Response wrapper
            message: Custom error message
        """
        self.assert_status_code(response, 401, message or "Expected unauthorized")

    def assert_forbidden(self, response: ResponseWrapper, message: str = None):
        """
        Assert response indicates forbidden (403)

        Args:
            response: Response wrapper
            message: Custom error message
        """
        self.assert_status_code(response, 403, message or "Expected forbidden")

    def assert_not_found(self, response: ResponseWrapper, message: str = None):
        """
        Assert response indicates not found (404)

        Args:
            response: Response wrapper
            message: Custom error message
        """
        self.assert_status_code(response, 404, message or "Expected not found")

    # Business Code Assertions

    def assert_code(self, response: ResponseWrapper, expected: int, message: str = None):
        """
        Assert business response code

        Args:
            response: Response wrapper
            expected: Expected business code
            message: Custom error message
        """
        actual = response.get_code()
        if actual != expected:
            msg = message or f"Expected code {expected}, got {actual}"
            self.logger.error(f"{msg}. Response: {response.body}")
            raise AssertionError(msg)

    def assert_code_success(self, response: ResponseWrapper, message: str = None):
        """
        Assert business code indicates success (code == 0)

        Args:
            response: Response wrapper
            message: Custom error message
        """
        self.assert_code(response, 0, message or "Expected success code")

    # Response Data Assertions

    def assert_has_data(self, response: ResponseWrapper, message: str = None):
        """
        Assert response has data field

        Args:
            response: Response wrapper
            message: Custom error message
        """
        data = response.get_data()
        if data is None:
            msg = message or "Expected data in response"
            self.logger.error(f"{msg}. Response: {response.body}")
            raise AssertionError(msg)

    def assert_data_not_empty(self, response: ResponseWrapper, message: str = None):
        """
        Assert response data is not empty

        Args:
            response: Response wrapper
            message: Custom error message
        """
        data = response.get_data()
        if not data:
            msg = message or "Expected non-empty data"
            self.logger.error(f"{msg}. Response: {response.body}")
            raise AssertionError(msg)

    def assert_data_is_list(self, response: ResponseWrapper, message: str = None):
        """
        Assert response data is a list

        Args:
            response: Response wrapper
            message: Custom error message
        """
        data = response.get_data()
        if not isinstance(data, list):
            msg = message or f"Expected list data, got {type(data).__name__}"
            self.logger.error(f"{msg}. Response: {response.body}")
            raise AssertionError(msg)

    def assert_data_is_dict(self, response: ResponseWrapper, message: str = None):
        """
        Assert response data is a dictionary

        Args:
            response: Response wrapper
            message: Custom error message
        """
        data = response.get_data()
        if not isinstance(data, dict):
            msg = message or f"Expected dict data, got {type(data).__name__}"
            self.logger.error(f"{msg}. Response: {response.body}")
            raise AssertionError(msg)

    def assert_list_length(
        self,
        response: ResponseWrapper,
        expected: int,
        message: str = None
    ):
        """
        Assert list data has expected length

        Args:
            response: Response wrapper
            expected: Expected list length
            message: Custom error message
        """
        data = response.get_data()
        if not isinstance(data, list):
            raise AssertionError(f"Expected list, got {type(data).__name__}")
        if len(data) != expected:
            msg = message or f"Expected list length {expected}, got {len(data)}"
            self.logger.error(msg)
            raise AssertionError(msg)

    def assert_list_min_length(
        self,
        response: ResponseWrapper,
        minimum: int,
        message: str = None
    ):
        """
        Assert list data has minimum length

        Args:
            response: Response wrapper
            minimum: Minimum list length
            message: Custom error message
        """
        data = response.get_data()
        if not isinstance(data, list):
            raise AssertionError(f"Expected list, got {type(data).__name__}")
        if len(data) < minimum:
            msg = message or f"Expected list length >= {minimum}, got {len(data)}"
            self.logger.error(msg)
            raise AssertionError(msg)

    # Field Assertions

    def assert_field_equals(
        self,
        response: ResponseWrapper,
        field: str,
        expected: Any,
        message: str = None
    ):
        """
        Assert response field equals expected value

        Args:
            response: Response wrapper
            field: Field path (dot notation supported)
            expected: Expected value
            message: Custom error message
        """
        actual = self._get_nested_field(response.body, field)
        if actual != expected:
            msg = message or f"Expected field '{field}' to be '{expected}', got '{actual}'"
            self.logger.error(msg)
            raise AssertionError(msg)

    def assert_field_contains(
        self,
        response: ResponseWrapper,
        field: str,
        expected: Any,
        message: str = None
    ):
        """
        Assert response field contains expected value

        Args:
            response: Response wrapper
            field: Field path
            expected: Expected value to contain
            message: Custom error message
        """
        actual = self._get_nested_field(response.body, field)
        if expected not in actual:
            msg = message or f"Expected field '{field}' to contain '{expected}'"
            self.logger.error(f"{msg}. Actual: '{actual}'")
            raise AssertionError(msg)

    def assert_field_exists(
        self,
        response: ResponseWrapper,
        field: str,
        message: str = None
    ):
        """
        Assert response field exists

        Args:
            response: Response wrapper
            field: Field path
            message: Custom error message
        """
        actual = self._get_nested_field(response.body, field)
        if actual is None:
            msg = message or f"Expected field '{field}' to exist"
            self.logger.error(msg)
            raise AssertionError(msg)

    def assert_field_not_empty(
        self,
        response: ResponseWrapper,
        field: str,
        message: str = None
    ):
        """
        Assert response field is not empty

        Args:
            response: Response wrapper
            field: Field path
            message: Custom error message
        """
        actual = self._get_nested_field(response.body, field)
        if not actual:
            msg = message or f"Expected field '{field}' to be non-empty"
            self.logger.error(msg)
            raise AssertionError(msg)

    # Performance Assertions

    def assert_response_time(
        self,
        response: ResponseWrapper,
        max_ms: float,
        message: str = None
    ):
        """
        Assert response time is within limit

        Args:
            response: Response wrapper
            max_ms: Maximum response time in milliseconds
            message: Custom error message
        """
        if response.elapsed_ms > max_ms:
            msg = message or f"Response time {response.elapsed_ms:.2f}ms exceeds limit {max_ms}ms"
            self.logger.error(msg)
            raise AssertionError(msg)

    # Pagination Assertions

    def assert_paginated_response(
        self,
        response: ResponseWrapper,
        message: str = None
    ):
        """
        Assert response is a valid paginated response

        Args:
            response: Response wrapper
            message: Custom error message
        """
        data = response.get_data()
        if not isinstance(data, dict):
            raise AssertionError("Expected paginated response data")

        required_fields = ['list', 'total', 'pageNum', 'pageSize']
        for field in required_fields:
            if field not in data:
                msg = message or f"Missing pagination field: {field}"
                raise AssertionError(msg)

    def assert_page_info(
        self,
        response: ResponseWrapper,
        page_num: int = None,
        page_size: int = None,
        total: int = None,
        message: str = None
    ):
        """
        Assert pagination information

        Args:
            response: Response wrapper
            page_num: Expected page number
            page_size: Expected page size
            total: Expected total count
            message: Custom error message
        """
        data = response.get_data()

        if page_num is not None and data.get('pageNum') != page_num:
            msg = message or f"Expected pageNum {page_num}, got {data.get('pageNum')}"
            raise AssertionError(msg)

        if page_size is not None and data.get('pageSize') != page_size:
            msg = message or f"Expected pageSize {page_size}, got {data.get('pageSize')}"
            raise AssertionError(msg)

        if total is not None and data.get('total') != total:
            msg = message or f"Expected total {total}, got {data.get('total')}"
            raise AssertionError(msg)

    # Helper Methods

    def _get_nested_field(self, data: Any, path: str) -> Any:
        """
        Get nested field value using dot notation

        Args:
            data: Data dictionary
            path: Field path (e.g., "data.user.name")

        Returns:
            Field value or None if not found
        """
        if not path:
            return data

        keys = path.split('.')
        value = data

        for key in keys:
            if isinstance(value, dict):
                value = value.get(key)
            elif isinstance(value, list) and key.isdigit():
                idx = int(key)
                value = value[idx] if idx < len(value) else None
            else:
                return None

            if value is None:
                return None

        return value
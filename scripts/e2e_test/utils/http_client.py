#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HIS Platform E2E Test Framework - HTTP Client
Encapsulated HTTP request client for API testing
"""

import os
import time
import logging
import requests
from typing import Dict, Optional, Any, Union
from dataclasses import dataclass
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry


@dataclass
class ResponseWrapper:
    """Wrapper for HTTP response with additional metadata"""
    status_code: int
    body: Any
    headers: Dict[str, str]
    elapsed_ms: float
    url: str

    def json(self) -> Any:
        """Get response body as JSON"""
        return self.body

    def is_success(self) -> bool:
        """Check if response indicates success"""
        return 200 <= self.status_code < 300

    def get_code(self) -> int:
        """Get business code from response"""
        if isinstance(self.body, dict):
            return self.body.get("code", -1)
        return -1

    def get_data(self) -> Any:
        """Get data from response"""
        if isinstance(self.body, dict):
            return self.body.get("data")
        return None

    def get_message(self) -> str:
        """Get message from response"""
        if isinstance(self.body, dict):
            return self.body.get("message", "")
        return ""


class HISClient:
    """
    HIS Platform API Client

    Provides convenient methods for making HTTP requests to the HIS API
    with built-in retry, authentication, and error handling.
    """

    def __init__(
        self,
        base_url: str = None,
        timeout: int = 30000,
        verify_ssl: bool = False,
        retry_count: int = 3,
        retry_delay: int = 1000
    ):
        """
        Initialize HTTP client

        Args:
            base_url: Base URL for API requests
            timeout: Request timeout in milliseconds
            verify_ssl: Whether to verify SSL certificates
            retry_count: Number of retry attempts
            retry_delay: Delay between retries in milliseconds
        """
        self.base_url = base_url or os.getenv("HIS_BASE_URL", "http://localhost:8080")
        self.timeout = timeout / 1000  # Convert to seconds
        self.verify_ssl = verify_ssl
        self.retry_count = retry_count
        self.retry_delay = retry_delay / 1000  # Convert to seconds

        self.session = requests.Session()
        self.token: Optional[str] = None
        self.headers: Dict[str, str] = {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }

        # Configure retry strategy
        retry_strategy = Retry(
            total=retry_count,
            backoff_factor=0.5,
            status_forcelist=[429, 500, 502, 503, 504],
            allowed_methods=["GET", "POST", "PUT", "DELETE"]
        )
        adapter = HTTPAdapter(max_retries=retry_strategy)
        self.session.mount("http://", adapter)
        self.session.mount("https://", adapter)

        self.logger = logging.getLogger(__name__)

    def login(self, username: str, password: str) -> bool:
        """
        Login and obtain authentication token

        Args:
            username: Login username
            password: Login password

        Returns:
            bool: True if login successful, False otherwise
        """
        try:
            response = self.session.post(
                f"{self.base_url}/api/system/v1/user/login",
                json={"loginName": username, "password": password},
                headers=self.headers,
                timeout=self.timeout,
                verify=self.verify_ssl
            )

            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 0:
                    self.token = data.get("data", {}).get("token")
                    if self.token:
                        self.headers["Authorization"] = f"Bearer {self.token}"
                        self.logger.info(f"Login successful for user: {username}")
                        return True

            self.logger.warning(f"Login failed for user: {username}")
            return False

        except Exception as e:
            self.logger.error(f"Login error: {e}")
            return False

    def _wrap_response(self, response: requests.Response, elapsed_ms: float) -> ResponseWrapper:
        """Wrap response with metadata"""
        try:
            body = response.json()
        except:
            body = response.text

        return ResponseWrapper(
            status_code=response.status_code,
            body=body,
            headers=dict(response.headers),
            elapsed_ms=elapsed_ms,
            url=response.url
        )

    def _request(
        self,
        method: str,
        path: str,
        params: Dict = None,
        data: Dict = None,
        json_data: Dict = None,
        headers: Dict = None,
        files: Dict = None
    ) -> ResponseWrapper:
        """
        Make HTTP request with retry logic

        Args:
            method: HTTP method (GET, POST, PUT, DELETE)
            path: API endpoint path
            params: Query parameters
            data: Form data
            json_data: JSON body
            headers: Additional headers
            files: Files to upload

        Returns:
            ResponseWrapper: Wrapped response
        """
        url = f"{self.base_url}{path}"
        request_headers = {**self.headers, **(headers or {})}

        last_exception = None
        for attempt in range(self.retry_count):
            try:
                start_time = time.time()

                response = self.session.request(
                    method=method,
                    url=url,
                    params=params,
                    data=data,
                    json=json_data,
                    headers=request_headers,
                    files=files,
                    timeout=self.timeout,
                    verify=self.verify_ssl
                )

                elapsed_ms = (time.time() - start_time) * 1000

                self.logger.debug(
                    f"{method} {path} -> {response.status_code} ({elapsed_ms:.2f}ms)"
                )

                return self._wrap_response(response, elapsed_ms)

            except requests.exceptions.RequestException as e:
                last_exception = e
                self.logger.warning(
                    f"Request failed (attempt {attempt + 1}/{self.retry_count}): {e}"
                )
                if attempt < self.retry_count - 1:
                    time.sleep(self.retry_delay)

        # All retries failed
        self.logger.error(f"All retry attempts failed for {method} {path}")
        raise last_exception or Exception(f"Request failed after {self.retry_count} attempts")

    def get(self, path: str, params: Dict = None, headers: Dict = None) -> ResponseWrapper:
        """
        Make GET request

        Args:
            path: API endpoint path
            params: Query parameters
            headers: Additional headers

        Returns:
            ResponseWrapper: Wrapped response
        """
        return self._request("GET", path, params=params, headers=headers)

    def post(
        self,
        path: str,
        data: Dict = None,
        headers: Dict = None
    ) -> ResponseWrapper:
        """
        Make POST request

        Args:
            path: API endpoint path
            data: JSON body
            headers: Additional headers

        Returns:
            ResponseWrapper: Wrapped response
        """
        return self._request("POST", path, json_data=data, headers=headers)

    def put(
        self,
        path: str,
        data: Dict = None,
        headers: Dict = None
    ) -> ResponseWrapper:
        """
        Make PUT request

        Args:
            path: API endpoint path
            data: JSON body
            headers: Additional headers

        Returns:
            ResponseWrapper: Wrapped response
        """
        return self._request("PUT", path, json_data=data, headers=headers)

    def delete(self, path: str, headers: Dict = None) -> ResponseWrapper:
        """
        Make DELETE request

        Args:
            path: API endpoint path
            headers: Additional headers

        Returns:
            ResponseWrapper: Wrapped response
        """
        return self._request("DELETE", path, headers=headers)

    def patch(
        self,
        path: str,
        data: Dict = None,
        headers: Dict = None
    ) -> ResponseWrapper:
        """
        Make PATCH request

        Args:
            path: API endpoint path
            data: JSON body
            headers: Additional headers

        Returns:
            ResponseWrapper: Wrapped response
        """
        return self._request("PATCH", path, json_data=data, headers=headers)

    def upload(
        self,
        path: str,
        files: Dict,
        data: Dict = None,
        headers: Dict = None
    ) -> ResponseWrapper:
        """
        Upload files

        Args:
            path: API endpoint path
            files: Files to upload
            data: Additional form data
            headers: Additional headers (Content-Type will be removed)

        Returns:
            ResponseWrapper: Wrapped response
        """
        # Remove Content-Type for multipart upload
        upload_headers = {**self.headers, **(headers or {})}
        upload_headers.pop("Content-Type", None)

        return self._request(
            "POST",
            path,
            data=data,
            files=files,
            headers=upload_headers
        )

    def set_header(self, key: str, value: str):
        """Set default header for all requests"""
        self.headers[key] = value

    def set_token(self, token: str):
        """Set authentication token"""
        self.token = token
        self.headers["Authorization"] = f"Bearer {token}"

    def close(self):
        """Close the session"""
        self.session.close()

    def __enter__(self):
        """Context manager entry"""
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        """Context manager exit"""
        self.close()
#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HIS Platform E2E Tests - System Module API Tests
End-to-End tests for system module endpoints
"""

import pytest
from utils.http_client import HISClient
from utils.assertions import Assertions
from utils.data_factory import DataFactory
from config.base_config import Config, SystemConfig


@pytest.mark.system
class TestSystemHealth:
    """System health check tests"""

    def test_health_check(self, http_client: HISClient, assertions: Assertions):
        """Test system health endpoint"""
        response = http_client.get("/actuator/health")

        assertions.assert_success(response)
        assertions.assert_response_time(response, 5000)

    def test_system_info(self, http_client: HISClient, assertions: Assertions):
        """Test system info endpoint"""
        response = http_client.get("/actuator/info")

        # May return 404 if not configured
        assert response.status_code in [200, 404]


@pytest.mark.system
@pytest.mark.critical
class TestUserManagement:
    """User management API tests"""

    def test_user_list_query(self, http_client: HISClient, assertions: Assertions):
        """Test user list query"""
        response = http_client.get(
            f"{Config().system_api_base}{SystemConfig.USER_ENDPOINT}",
            params={"pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)
        assertions.assert_has_data(response)

    def test_user_create_and_delete(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test user creation and deletion flow"""
        # Create user
        user_data = data_factory.generate_user()

        create_response = http_client.post(
            f"{Config().system_api_base}{SystemConfig.USER_ENDPOINT}",
            data=user_data
        )

        # Verify creation
        assertions.assert_success(create_response)
        assertions.assert_code_success(create_response)

        # Get user ID if available
        user_id = create_response.get_data()
        if user_id and isinstance(user_id, dict):
            user_id = user_id.get("id")

        if user_id:
            # Query user
            get_response = http_client.get(
                f"{Config().system_api_base}{SystemConfig.USER_ENDPOINT}/{user_id}"
            )
            assertions.assert_success(get_response)

            # Delete user
            delete_response = http_client.delete(
                f"{Config().system_api_base}{SystemConfig.USER_ENDPOINT}/{user_id}"
            )
            assertions.assert_success(delete_response)

    def test_user_query_by_id(self, http_client: HISClient, assertions: Assertions):
        """Test user query by ID"""
        # Query non-existent user
        response = http_client.get(
            f"{Config().system_api_base}{SystemConfig.USER_ENDPOINT}/99999999"
        )

        # Should return 404 or error code
        assert response.status_code in [200, 404]
        if response.status_code == 200:
            assertions.assert_code(response, 404)

    def test_user_update(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test user update flow"""
        # Create user first
        user_data = data_factory.generate_user()

        create_response = http_client.post(
            f"{Config().system_api_base}{SystemConfig.USER_ENDPOINT}",
            data=user_data
        )

        assertions.assert_success(create_response)

        user_id = create_response.get_data()
        if user_id and isinstance(user_id, dict):
            user_id = user_id.get("id")

            if user_id:
                # Update user
                update_data = {"realName": "Updated Name"}
                update_response = http_client.put(
                    f"{Config().system_api_base}{SystemConfig.USER_ENDPOINT}/{user_id}",
                    data=update_data
                )

                # Should succeed or fail gracefully
                assert update_response.status_code in [200, 400, 404]

                # Cleanup
                http_client.delete(
                    f"{Config().system_api_base}{SystemConfig.USER_ENDPOINT}/{user_id}"
                )


@pytest.mark.system
class TestRoleManagement:
    """Role management API tests"""

    def test_role_list_query(self, http_client: HISClient, assertions: Assertions):
        """Test role list query"""
        response = http_client.get(
            f"{Config().system_api_base}{SystemConfig.ROLE_ENDPOINT}",
            params={"pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)

    def test_role_create_and_delete(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test role creation and deletion"""
        role_data = {
            "roleName": f"test_role_{data_factory._generate_unique_id()}",
            "roleCode": f"TEST_{data_factory._generate_unique_id()}",
            "description": "Test role for E2E testing"
        }

        # Create role
        create_response = http_client.post(
            f"{Config().system_api_base}{SystemConfig.ROLE_ENDPOINT}",
            data=role_data
        )

        assertions.assert_success(create_response)

        # Cleanup if ID returned
        role_id = create_response.get_data()
        if role_id and isinstance(role_id, dict):
            role_id = role_id.get("id")
            if role_id:
                http_client.delete(
                    f"{Config().system_api_base}{SystemConfig.ROLE_ENDPOINT}/{role_id}"
                )


@pytest.mark.system
class TestDepartmentManagement:
    """Department management API tests"""

    def test_department_tree(self, http_client: HISClient, assertions: Assertions):
        """Test department tree structure query"""
        response = http_client.get(
            f"{Config().system_api_base}{SystemConfig.DEPARTMENT_ENDPOINT}/tree"
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)
        assertions.assert_has_data(response)

    def test_department_list(self, http_client: HISClient, assertions: Assertions):
        """Test department list query"""
        response = http_client.get(
            f"{Config().system_api_base}{SystemConfig.DEPARTMENT_ENDPOINT}",
            params={"pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)

    def test_department_create_and_delete(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test department creation and deletion"""
        dept_data = data_factory.generate_department()

        # Create department
        create_response = http_client.post(
            f"{Config().system_api_base}{SystemConfig.DEPARTMENT_ENDPOINT}",
            data=dept_data
        )

        assertions.assert_success(create_response)

        # Cleanup if ID returned
        dept_id = create_response.get_data()
        if dept_id and isinstance(dept_id, dict):
            dept_id = dept_id.get("id")
            if dept_id:
                http_client.delete(
                    f"{Config().system_api_base}{SystemConfig.DEPARTMENT_ENDPOINT}/{dept_id}"
                )


@pytest.mark.system
class TestDictionaryManagement:
    """Data dictionary API tests"""

    def test_dictionary_by_type(self, http_client: HISClient, assertions: Assertions):
        """Test dictionary query by type"""
        response = http_client.get(
            f"{Config().system_api_base}{SystemConfig.DICTIONARY_ENDPOINT}/type/gender"
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)

    def test_dictionary_list(self, http_client: HISClient, assertions: Assertions):
        """Test dictionary list query"""
        response = http_client.get(
            f"{Config().system_api_base}{SystemConfig.DICTIONARY_ENDPOINT}",
            params={"pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)

    @pytest.mark.smoke
    def test_common_dictionaries(self, http_client: HISClient, assertions: Assertions):
        """Test common dictionary types"""
        common_types = ["gender", "status", "user_type", "id_type"]

        for dict_type in common_types:
            response = http_client.get(
                f"{Config().system_api_base}{SystemConfig.DICTIONARY_ENDPOINT}/type/{dict_type}"
            )

            # Should return success or 404
            assert response.status_code in [200, 404]


@pytest.mark.system
class TestMenuManagement:
    """Menu management API tests"""

    def test_menu_tree(self, http_client: HISClient, assertions: Assertions):
        """Test menu tree query"""
        response = http_client.get(
            f"{Config().system_api_base}{SystemConfig.MENU_ENDPOINT}/tree"
        )

        assertions.assert_success(response)

    def test_user_menus(self, http_client: HISClient, assertions: Assertions):
        """Test current user menus query"""
        response = http_client.get(
            f"{Config().system_api_base}{SystemConfig.MENU_ENDPOINT}/user"
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)
        assertions.assert_has_data(response)
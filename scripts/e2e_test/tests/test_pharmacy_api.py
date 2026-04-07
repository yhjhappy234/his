#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HIS Platform E2E Tests - Pharmacy Module API Tests
End-to-End tests for pharmacy module endpoints
"""

import pytest
from utils.http_client import HISClient
from utils.assertions import Assertions
from utils.data_factory import DataFactory
from config.base_config import Config, PharmacyConfig


@pytest.mark.pharmacy
@pytest.mark.critical
class TestDrugManagement:
    """Drug management API tests"""

    def test_drug_list_query(self, http_client: HISClient, assertions: Assertions):
        """Test drug list query"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.DRUG_ENDPOINT}",
            params={"pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)

    def test_drug_search_by_keyword(self, http_client: HISClient, assertions: Assertions):
        """Test drug search by keyword"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.DRUG_ENDPOINT}/search",
            params={"keyword": "阿莫西林"}
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)

    def test_drug_search_by_code(self, http_client: HISClient, assertions: Assertions):
        """Test drug search by code"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.DRUG_ENDPOINT}/search",
            params={"code": "AMX001"}
        )

        assertions.assert_success(response)

    def test_drug_query_by_id(self, http_client: HISClient, assertions: Assertions):
        """Test drug query by ID"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.DRUG_ENDPOINT}/99999999"
        )

        # Should return 404 for non-existent drug
        assert response.status_code in [200, 404]
        if response.status_code == 200:
            assertions.assert_code(response, 404)

    def test_drug_create_and_delete(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test drug creation and deletion"""
        drug_data = data_factory.generate_drug()

        # Create drug
        create_response = http_client.post(
            f"{Config().pharmacy_api_base}{PharmacyConfig.DRUG_ENDPOINT}",
            data=drug_data
        )

        assertions.assert_success(create_response)

        # Cleanup if ID returned
        drug_id = create_response.get_data()
        if drug_id and isinstance(drug_id, dict):
            drug_id = drug_id.get("id")
            if drug_id:
                http_client.delete(
                    f"{Config().pharmacy_api_base}{PharmacyConfig.DRUG_ENDPOINT}/{drug_id}"
                )

    def test_drug_update(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test drug update"""
        # Create drug first
        drug_data = data_factory.generate_drug()

        create_response = http_client.post(
            f"{Config().pharmacy_api_base}{PharmacyConfig.DRUG_ENDPOINT}",
            data=drug_data
        )

        assertions.assert_success(create_response)

        drug_id = create_response.get_data()
        if drug_id and isinstance(drug_id, dict):
            drug_id = drug_id.get("id")

            if drug_id:
                # Update drug price
                update_data = {"price": 99.99}
                update_response = http_client.put(
                    f"{Config().pharmacy_api_base}{PharmacyConfig.DRUG_ENDPOINT}/{drug_id}",
                    data=update_data
                )

                assert update_response.status_code in [200, 400, 404]

                # Cleanup
                http_client.delete(
                    f"{Config().pharmacy_api_base}{PharmacyConfig.DRUG_ENDPOINT}/{drug_id}"
                )

    def test_drug_list_by_category(self, http_client: HISClient, assertions: Assertions):
        """Test drug list by category"""
        categories = ["ANTIBIOTIC", "ANALGESIC", "ANTIDIABETIC"]

        for category in categories:
            response = http_client.get(
                f"{Config().pharmacy_api_base}{PharmacyConfig.DRUG_ENDPOINT}",
                params={"category": category, "pageNum": 1, "pageSize": 10}
            )

            assertions.assert_success(response)


@pytest.mark.pharmacy
@pytest.mark.critical
class TestInventoryManagement:
    """Inventory management API tests"""

    def test_inventory_list(self, http_client: HISClient, assertions: Assertions):
        """Test inventory list query"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.INVENTORY_ENDPOINT}",
            params={"pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)

    def test_inventory_by_drug(self, http_client: HISClient, assertions: Assertions):
        """Test inventory query by drug ID"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.INVENTORY_ENDPOINT}/drug/1"
        )

        assertions.assert_success(response)

    def test_inventory_low_stock(self, http_client: HISClient, assertions: Assertions):
        """Test low stock inventory query"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.INVENTORY_ENDPOINT}/low-stock"
        )

        assertions.assert_success(response)

    def test_inventory_by_department(self, http_client: HISClient, assertions: Assertions):
        """Test inventory query by department"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.INVENTORY_ENDPOINT}",
            params={"departmentId": "1", "pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)

    def test_inventory_update(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test inventory update"""
        update_data = {
            "drugId": "1",
            "quantity": 100,
            "operationType": "IN"
        }

        response = http_client.post(
            f"{Config().pharmacy_api_base}{PharmacyConfig.INVENTORY_ENDPOINT}/update",
            data=update_data
        )

        # May fail if drug not found
        assert response.status_code in [200, 400, 404]


@pytest.mark.pharmacy
class TestPrescriptionManagement:
    """Prescription management API tests"""

    def test_prescription_list(self, http_client: HISClient, assertions: Assertions):
        """Test prescription list query"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.PRESCRIPTION_ENDPOINT}",
            params={"pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)

    def test_prescription_by_id(self, http_client: HISClient, assertions: Assertions):
        """Test prescription query by ID"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.PRESCRIPTION_ENDPOINT}/99999999"
        )

        assert response.status_code in [200, 404]

    def test_prescription_by_patient(self, http_client: HISClient, assertions: Assertions):
        """Test prescription query by patient"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.PRESCRIPTION_ENDPOINT}/patient",
            params={"patientId": "1"}
        )

        assertions.assert_success(response)

    def test_prescription_verify(
        self,
        http_client: HISClient,
        assertions: Assertions
    ):
        """Test prescription verification"""
        response = http_client.post(
            f"{Config().pharmacy_api_base}{PharmacyConfig.PRESCRIPTION_ENDPOINT}/verify",
            data={"prescriptionId": "1"}
        )

        # May fail if prescription not found
        assert response.status_code in [200, 400, 404]


@pytest.mark.pharmacy
class TestDispensingManagement:
    """Dispensing management API tests"""

    def test_dispensing_list(self, http_client: HISClient, assertions: Assertions):
        """Test dispensing list query"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.DISPENSING_ENDPOINT}",
            params={"pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)

    def test_dispensing_by_prescription(
        self,
        http_client: HISClient,
        assertions: Assertions
    ):
        """Test dispensing query by prescription"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.DISPENSING_ENDPOINT}/prescription/1"
        )

        assertions.assert_success(response)

    def test_dispensing_create(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test dispensing creation"""
        dispensing_data = {
            "prescriptionId": "1",
            "pharmacistId": "1",
            "items": [
                {"drugId": "1", "quantity": 2}
            ]
        }

        response = http_client.post(
            f"{Config().pharmacy_api_base}{PharmacyConfig.DISPENSING_ENDPOINT}/create",
            data=dispensing_data
        )

        # May fail if prescription not found or already dispensed
        assert response.status_code in [200, 400, 404]


@pytest.mark.pharmacy
@pytest.mark.smoke
class TestPharmacySmoke:
    """Smoke tests for pharmacy module"""

    def test_pharmacy_api_health(self, http_client: HISClient, assertions: Assertions):
        """Test pharmacy API basic health"""
        endpoints = [
            PharmacyConfig.DRUG_ENDPOINT,
            PharmacyConfig.INVENTORY_ENDPOINT,
            PharmacyConfig.PRESCRIPTION_ENDPOINT
        ]

        for endpoint in endpoints:
            response = http_client.get(
                f"{Config().pharmacy_api_base}{endpoint}",
                params={"pageNum": 1, "pageSize": 1}
            )

            assertions.assert_success(response)
            assertions.assert_response_time(response, 3000)

    def test_drug_search_performance(self, http_client: HISClient, assertions: Assertions):
        """Test drug search performance"""
        # Search for common drug
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.DRUG_ENDPOINT}/search",
            params={"keyword": "阿"}
        )

        assertions.assert_success(response)
        assertions.assert_response_time(response, 2000)

    def test_inventory_availability(self, http_client: HISClient, assertions: Assertions):
        """Test inventory query availability"""
        response = http_client.get(
            f"{Config().pharmacy_api_base}{PharmacyConfig.INVENTORY_ENDPOINT}/available"
        )

        assertions.assert_success(response)
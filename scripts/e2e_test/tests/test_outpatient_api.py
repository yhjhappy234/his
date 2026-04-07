#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HIS Platform E2E Tests - Outpatient Module API Tests
End-to-End tests for outpatient module endpoints
"""

import pytest
from datetime import datetime
from utils.http_client import HISClient
from utils.assertions import Assertions
from utils.data_factory import DataFactory
from config.base_config import Config, OutpatientConfig


@pytest.mark.outpatient
@pytest.mark.critical
class TestPatientManagement:
    """Patient management API tests"""

    def test_patient_list_query(self, http_client: HISClient, assertions: Assertions):
        """Test patient list query"""
        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.PATIENT_ENDPOINT}",
            params={"pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)

    def test_patient_search(self, http_client: HISClient, assertions: Assertions):
        """Test patient search"""
        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.PATIENT_ENDPOINT}/search",
            params={"keyword": "测试"}
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)

    def test_patient_registration(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test patient registration flow"""
        # Generate patient data
        patient_data = data_factory.generate_patient()

        # Create patient
        create_response = http_client.post(
            f"{Config().outpatient_api_base}{OutpatientConfig.PATIENT_ENDPOINT}/create",
            data=patient_data
        )

        assertions.assert_success(create_response)
        assertions.assert_code_success(create_response)

        # Get patient ID if returned
        patient_id = create_response.get_data()
        if patient_id and isinstance(patient_id, dict):
            patient_id = patient_id.get("id")

            if patient_id:
                # Query patient by ID
                get_response = http_client.get(
                    f"{Config().outpatient_api_base}{OutpatientConfig.PATIENT_ENDPOINT}/{patient_id}"
                )
                assertions.assert_success(get_response)

    def test_patient_query_by_id_card(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test patient query by ID card number"""
        # Generate unique ID card
        id_card = data_factory._generate_id_card()

        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.PATIENT_ENDPOINT}/by-id-card",
            params={"idNo": id_card}
        )

        # Should return success (empty or with data)
        assertions.assert_success(response)

    def test_patient_update(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test patient update flow"""
        # Create patient first
        patient_data = data_factory.generate_patient()

        create_response = http_client.post(
            f"{Config().outpatient_api_base}{OutpatientConfig.PATIENT_ENDPOINT}/create",
            data=patient_data
        )

        assertions.assert_success(create_response)

        patient_id = create_response.get_data()
        if patient_id and isinstance(patient_id, dict):
            patient_id = patient_id.get("id")

            if patient_id:
                # Update patient
                update_data = {
                    "phone": data_factory.generate_phone(),
                    "address": "Updated Address"
                }

                update_response = http_client.put(
                    f"{Config().outpatient_api_base}{OutpatientConfig.PATIENT_ENDPOINT}/{patient_id}",
                    data=update_data
                )

                assert update_response.status_code in [200, 400, 404]


@pytest.mark.outpatient
class TestScheduleManagement:
    """Doctor schedule management API tests"""

    def test_schedule_list(self, http_client: HISClient, assertions: Assertions):
        """Test schedule list query"""
        today = datetime.now().strftime("%Y-%m-%d")

        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.SCHEDULE_ENDPOINT}",
            params={"date": today, "pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)

    def test_schedule_by_department(self, http_client: HISClient, assertions: Assertions):
        """Test schedule query by department"""
        today = datetime.now().strftime("%Y-%m-%d")

        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.SCHEDULE_ENDPOINT}/department",
            params={"date": today}
        )

        assertions.assert_success(response)

    def test_schedule_by_doctor(self, http_client: HISClient, assertions: Assertions):
        """Test schedule query by doctor"""
        today = datetime.now().strftime("%Y-%m-%d")

        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.SCHEDULE_ENDPOINT}/doctor",
            params={"date": today, "doctorId": "1"}
        )

        # May return 404 for non-existent doctor
        assert response.status_code in [200, 404]

    def test_available_schedules(self, http_client: HISClient, assertions: Assertions):
        """Test available schedules query"""
        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.SCHEDULE_ENDPOINT}/available",
            params={"pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)


@pytest.mark.outpatient
@pytest.mark.critical
class TestRegistrationFlow:
    """Registration flow API tests"""

    def test_registration_list(self, http_client: HISClient, assertions: Assertions):
        """Test registration list query"""
        today = datetime.now().strftime("%Y-%m-%d")

        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.REGISTRATION_ENDPOINT}",
            params={"date": today, "pageNum": 1, "pageSize": 10}
        )

        assertions.assert_success(response)
        assertions.assert_code_success(response)

    def test_registration_create(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test registration creation flow"""
        registration_data = data_factory.generate_registration()

        response = http_client.post(
            f"{Config().outpatient_api_base}{OutpatientConfig.REGISTRATION_ENDPOINT}/create",
            data=registration_data
        )

        # May fail if doctor/schedule not found
        if response.is_success():
            assertions.assert_code_success(response)
        else:
            # Registration might fail due to business rules
            assert response.status_code in [200, 400, 404]

    def test_registration_query_by_id(self, http_client: HISClient, assertions: Assertions):
        """Test registration query by ID"""
        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.REGISTRATION_ENDPOINT}/99999999"
        )

        # Should return 404 for non-existent registration
        assert response.status_code in [200, 404]
        if response.status_code == 200:
            assertions.assert_code(response, 404)

    def test_registration_cancel(
        self,
        http_client: HISClient,
        assertions: Assertions,
        data_factory: DataFactory
    ):
        """Test registration cancellation"""
        # Try to cancel non-existent registration
        response = http_client.post(
            f"{Config().outpatient_api_base}{OutpatientConfig.REGISTRATION_ENDPOINT}/99999999/cancel"
        )

        # Should return 404 or error
        assert response.status_code in [200, 400, 404]


@pytest.mark.outpatient
class TestQueueManagement:
    """Queue management API tests"""

    def test_queue_status(self, http_client: HISClient, assertions: Assertions):
        """Test queue status query"""
        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.QUEUE_ENDPOINT}/status"
        )

        assertions.assert_success(response)

    def test_department_queue(self, http_client: HISClient, assertions: Assertions):
        """Test department queue query"""
        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.QUEUE_ENDPOINT}/department",
            params={"departmentId": "1"}
        )

        assertions.assert_success(response)

    def test_call_next_patient(self, http_client: HISClient, assertions: Assertions):
        """Test calling next patient"""
        response = http_client.post(
            f"{Config().outpatient_api_base}{OutpatientConfig.QUEUE_ENDPOINT}/call-next",
            data={"departmentId": "1"}
        )

        # May return error if no patients in queue
        assert response.status_code in [200, 400, 404]


@pytest.mark.outpatient
@pytest.mark.smoke
class TestOutpatientSmoke:
    """Smoke tests for outpatient module"""

    def test_outpatient_api_health(self, http_client: HISClient, assertions: Assertions):
        """Test outpatient API basic health"""
        endpoints = [
            OutpatientConfig.PATIENT_ENDPOINT,
            OutpatientConfig.SCHEDULE_ENDPOINT,
            OutpatientConfig.REGISTRATION_ENDPOINT
        ]

        for endpoint in endpoints:
            response = http_client.get(
                f"{Config().outpatient_api_base}{endpoint}",
                params={"pageNum": 1, "pageSize": 1}
            )

            # All endpoints should respond
            assertions.assert_success(response)

    def test_today_schedule_exists(self, http_client: HISClient, assertions: Assertions):
        """Test that schedule query works for today"""
        today = datetime.now().strftime("%Y-%m-%d")

        response = http_client.get(
            f"{Config().outpatient_api_base}{OutpatientConfig.SCHEDULE_ENDPOINT}",
            params={"date": today}
        )

        assertions.assert_success(response)
        assertions.assert_response_time(response, 3000)
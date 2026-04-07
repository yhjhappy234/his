#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HIS Platform E2E Test Framework - Data Factory
Test data generation factory for E2E testing
"""

import random
import string
import time
from datetime import datetime, timedelta
from typing import Dict, List, Any, Optional
from faker import Faker


class DataFactory:
    """
    Test data generation factory

    Provides methods for generating realistic test data for
    various entities in the HIS platform.
    """

    def __init__(self, locale: str = 'zh_CN'):
        """
        Initialize data factory

        Args:
            locale: Faker locale for localized data
        """
        self.faker = Faker(locale)
        self._counter = 0

    def _generate_unique_id(self, prefix: str = "") -> str:
        """Generate unique ID with optional prefix"""
        self._counter += 1
        timestamp = int(time.time() * 1000)
        return f"{prefix}{timestamp}{self._counter:04d}"

    # User Data Generation

    def generate_user(self, **kwargs) -> Dict[str, Any]:
        """
        Generate user data

        Args:
            **kwargs: Override default values

        Returns:
            Dict with user data
        """
        base_data = {
            "userName": f"test_user_{self._generate_unique_id()}",
            "loginName": f"test_{self._generate_unique_id()}",
            "password": "Test@123456",
            "realName": self.faker.name(),
            "userType": random.choice(["STAFF", "DOCTOR", "NURSE", "PHARMACIST", "CASHIER"]),
            "status": "NORMAL",
            "phone": self.faker.phone_number(),
            "email": self.faker.email(),
            "gender": random.choice(["M", "F"]),
            "idType": "ID_CARD",
            "idNo": self._generate_id_card(),
            "address": self.faker.address()
        }
        base_data.update(kwargs)
        return base_data

    def generate_doctor(self, **kwargs) -> Dict[str, Any]:
        """Generate doctor user data"""
        return self.generate_user(
            userType="DOCTOR",
            title=random.choice(["主任医师", "副主任医师", "主治医师", "住院医师"]),
            departmentId=None,
            **kwargs
        )

    def generate_nurse(self, **kwargs) -> Dict[str, Any]:
        """Generate nurse user data"""
        return self.generate_user(
            userType="NURSE",
            **kwargs
        )

    # Patient Data Generation

    def generate_patient(self, **kwargs) -> Dict[str, Any]:
        """
        Generate patient data

        Args:
            **kwargs: Override default values

        Returns:
            Dict with patient data
        """
        gender = random.choice(["M", "F"])
        birth_date = self.faker.date_of_birth(minimum_age=0, maximum_age=90)

        base_data = {
            "name": self.faker.name(),
            "gender": gender,
            "idType": "ID_CARD",
            "idNo": self._generate_id_card(),
            "phone": self.faker.phone_number(),
            "birthDate": birth_date.strftime("%Y-%m-%d"),
            "address": self.faker.address(),
            "emergencyContact": self.faker.name(),
            "emergencyPhone": self.faker.phone_number(),
            "bloodType": random.choice(["A", "B", "AB", "O"]),
            "maritalStatus": random.choice(["SINGLE", "MARRIED", "DIVORCED", "WIDOWED"]),
            "occupation": self.faker.job()
        }
        base_data.update(kwargs)
        return base_data

    def generate_child_patient(self, **kwargs) -> Dict[str, Any]:
        """Generate child patient data"""
        return self.generate_patient(
            idType="BIRTH_CERT",
            idNo=f"B{self._generate_unique_id()}",
            birthDate=self.faker.date_of_birth(minimum_age=0, maximum_age=14).strftime("%Y-%m-%d"),
            guardianName=self.faker.name(),
            guardianPhone=self.faker.phone_number(),
            **kwargs
        )

    # Department Data Generation

    def generate_department(self, **kwargs) -> Dict[str, Any]:
        """
        Generate department data

        Args:
            **kwargs: Override default values

        Returns:
            Dict with department data
        """
        dept_types = [
            ("内科", "INT_MED", "CLINICAL"),
            ("外科", "SURGERY", "CLINICAL"),
            ("儿科", "PED", "CLINICAL"),
            ("妇产科", "OBGYN", "CLINICAL"),
            ("急诊科", "EMERG", "CLINICAL"),
            ("检验科", "LAB", "LAB"),
            ("放射科", "RAD", "RADIOLOGY"),
            ("药房", "PHA", "PHARMACY"),
            ("收费处", "CASHIER", "FINANCE")
        ]

        name, code, dept_type = random.choice(dept_types)

        base_data = {
            "name": name,
            "code": f"{code}_{self._generate_unique_id()}",
            "type": dept_type,
            "description": f"{name}科室",
            "status": "NORMAL",
            "phone": self.faker.phone_number(),
            "location": f"{random.randint(1, 10)}号楼{random.randint(1, 5)}层"
        }
        base_data.update(kwargs)
        return base_data

    # Drug Data Generation

    def generate_drug(self, **kwargs) -> Dict[str, Any]:
        """
        Generate drug data

        Args:
            **kwargs: Override default values

        Returns:
            Dict with drug data
        """
        categories = [
            ("ANTIBIOTIC", "抗生素"),
            ("ANALGESIC", "镇痛药"),
            ("ANTIDIABETIC", "降糖药"),
            ("CARDIOVASCULAR", "心血管药"),
            ("RESPIRATORY", "呼吸系统药"),
            ("DIGESTIVE", "消化系统药"),
            ("VITAMIN", "维生素")
        ]

        category_code, category_name = random.choice(categories)

        base_data = {
            "name": f"{self.faker.word()}{random.choice(['片', '胶囊', '颗粒', '注射液'])}",
            "code": f"DRUG_{self._generate_unique_id()}",
            "genericName": self.faker.word(),
            "specification": f"{random.randint(1, 500)}{random.choice(['mg', 'g', 'ml'])}*{random.randint(10, 100)}",
            "unit": random.choice(["盒", "瓶", "袋", "支"]),
            "price": round(random.uniform(1, 500), 2),
            "category": category_code,
            "categoryName": category_name,
            "status": "NORMAL",
            "storageCondition": random.choice(["常温", "冷藏", "冷冻"]),
            "manufacturer": f"{self.faker.company()}制药厂",
            "approvalNumber": f"国药准字H{random.randint(10000000, 99999999)}",
            "prescriptionType": random.choice(["OTC", "RX", "RX_SOCIAL"])
        }
        base_data.update(kwargs)
        return base_data

    # Prescription Data Generation

    def generate_prescription_item(self, **kwargs) -> Dict[str, Any]:
        """Generate prescription item"""
        base_data = {
            "drugId": self._generate_unique_id("DRUG"),
            "drugName": f"测试药品{self._counter}",
            "specification": "0.5g*24粒",
            "unit": "盒",
            "quantity": random.randint(1, 10),
            "dosage": f"{random.randint(1, 3)}粒",
            "frequency": random.choice(["每日一次", "每日两次", "每日三次"]),
            "duration": f"{random.randint(1, 7)}天",
            "usage": random.choice(["口服", "静脉注射", "肌肉注射"]),
            "price": round(random.uniform(10, 200), 2)
        }
        base_data.update(kwargs)
        return base_data

    def generate_prescription(self, **kwargs) -> Dict[str, Any]:
        """
        Generate prescription data

        Args:
            **kwargs: Override default values

        Returns:
            Dict with prescription data
        """
        items = [self.generate_prescription_item() for _ in range(random.randint(1, 5))]
        total_amount = sum(item["price"] * item["quantity"] for item in items)

        base_data = {
            "patientId": self._generate_unique_id("PAT"),
            "patientName": self.faker.name(),
            "doctorId": self._generate_unique_id("DOC"),
            "doctorName": self.faker.name(),
            "departmentId": self._generate_unique_id("DEPT"),
            "departmentName": random.choice(["内科", "外科", "儿科"]),
            "diagnosis": random.choice(["上呼吸道感染", "高血压", "糖尿病", "胃炎"]),
            "icdCode": random.choice(["J00", "I10", "E11", "K29"]),
            "items": items,
            "totalAmount": round(total_amount, 2),
            "remark": self.faker.sentence()
        }
        base_data.update(kwargs)
        return base_data

    # Registration Data Generation

    def generate_registration(self, **kwargs) -> Dict[str, Any]:
        """
        Generate registration data

        Args:
            **kwargs: Override default values

        Returns:
            Dict with registration data
        """
        base_data = {
            "patientId": self._generate_unique_id("PAT"),
            "patientName": self.faker.name(),
            "patientPhone": self.faker.phone_number(),
            "patientIdNo": self._generate_id_card(),
            "departmentId": self._generate_unique_id("DEPT"),
            "departmentName": random.choice(["内科", "外科", "儿科", "妇产科"]),
            "doctorId": self._generate_unique_id("DOC"),
            "doctorName": self.faker.name(),
            "scheduleId": self._generate_unique_id("SCH"),
            "scheduleDate": datetime.now().strftime("%Y-%m-%d"),
            "schedulePeriod": random.choice(["AM", "PM"]),
            "registrationType": random.choice(["NORMAL", "EXPERT", "EMERGENCY"]),
            "registrationFee": random.choice([10, 20, 50, 100]),
            "status": "WAITING",
            "queueNumber": random.randint(1, 100)
        }
        base_data.update(kwargs)
        return base_data

    # Schedule Data Generation

    def generate_schedule(self, **kwargs) -> Dict[str, Any]:
        """
        Generate doctor schedule data

        Args:
            **kwargs: Override default values

        Returns:
            Dict with schedule data
        """
        base_data = {
            "doctorId": self._generate_unique_id("DOC"),
            "doctorName": self.faker.name(),
            "departmentId": self._generate_unique_id("DEPT"),
            "departmentName": random.choice(["内科", "外科", "儿科"]),
            "scheduleDate": (datetime.now() + timedelta(days=random.randint(1, 30))).strftime("%Y-%m-%d"),
            "period": random.choice(["AM", "PM"]),
            "totalSlots": random.randint(20, 50),
            "availableSlots": random.randint(0, 50),
            "registrationFee": random.choice([10, 20, 50, 100]),
            "consultationFee": random.choice([0, 10, 20]),
            "status": random.choice(["AVAILABLE", "FULL", "CANCELLED"])
        }
        base_data.update(kwargs)
        return base_data

    # Test Item Data Generation

    def generate_test_item(self, **kwargs) -> Dict[str, Any]:
        """
        Generate LIS test item data

        Args:
            **kwargs: Override default values

        Returns:
            Dict with test item data
        """
        test_types = [
            ("血常规", "CBC", "血液"),
            ("尿常规", "URINALYSIS", "尿液"),
            ("肝功能", "LIVER", "血液"),
            ("肾功能", "KIDNEY", "血液"),
            ("血糖", "GLUCOSE", "血液"),
            ("血脂", "LIPID", "血液")
        ]

        name, code, sample_type = random.choice(test_types)

        base_data = {
            "name": name,
            "code": code,
            "sampleType": sample_type,
            "price": round(random.uniform(10, 200), 2),
            "reportTime": random.choice(["2小时", "4小时", "24小时"]),
            "department": "检验科",
            "status": "NORMAL"
        }
        base_data.update(kwargs)
        return base_data

    # Helper Methods

    def _generate_id_card(self) -> str:
        """Generate Chinese ID card number"""
        # Area code (Beijing)
        area_code = "110101"
        # Birth date
        birth_date = self.faker.date_of_birth(minimum_age=18, maximum_age=70)
        birth_str = birth_date.strftime("%Y%m%d")
        # Sequence (3 digits)
        sequence = f"{random.randint(0, 999):03d}"
        # Calculate check digit
        id_17 = area_code + birth_str + sequence
        weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
        check_codes = "10X98765432"
        total = sum(int(id_17[i]) * weights[i] for i in range(17))
        check_digit = check_codes[total % 11]

        return id_17 + check_digit

    def generate_phone(self) -> str:
        """Generate Chinese mobile phone number"""
        prefixes = ["130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
                    "150", "151", "152", "153", "155", "156", "157", "158", "159",
                    "170", "176", "177", "178",
                    "180", "181", "182", "183", "184", "185", "186", "187", "188", "189"]
        return random.choice(prefixes) + "".join([str(random.randint(0, 9)) for _ in range(8)])

    def generate_date_range(
        self,
        start_days_ago: int = 30,
        end_days_ahead: int = 30
    ) -> Dict[str, str]:
        """Generate date range for queries"""
        start_date = datetime.now() - timedelta(days=start_days_ago)
        end_date = datetime.now() + timedelta(days=end_days_ahead)
        return {
            "startDate": start_date.strftime("%Y-%m-%d"),
            "endDate": end_date.strftime("%Y-%m-%d")
        }

    def generate_pagination_params(
        self,
        page_num: int = None,
        page_size: int = None
    ) -> Dict[str, int]:
        """Generate pagination parameters"""
        return {
            "pageNum": page_num or random.randint(1, 10),
            "pageSize": page_size or random.choice([10, 20, 50, 100])
        }
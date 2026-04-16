#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HIS Platform E2E Test Framework - Test Data Constants
Test data constants and fixtures for E2E tests
"""

from typing import Dict, List, Any


class TestUsers:
    """Test user data constants"""

    ADMIN = {
        "loginName": "admin",
        "password": "123456",
        "realName": "System Administrator",
        "userType": "ADMIN"
    }

    DOCTOR = {
        "loginName": "test_doctor",
        "password": "Doctor@123",
        "realName": "Test Doctor",
        "userType": "DOCTOR"
    }

    NURSE = {
        "loginName": "test_nurse",
        "password": "Nurse@123",
        "realName": "Test Nurse",
        "userType": "NURSE"
    }

    PHARMACIST = {
        "loginName": "test_pharmacist",
        "password": "Pharmacist@123",
        "realName": "Test Pharmacist",
        "userType": "PHARMACIST"
    }

    CASHIER = {
        "loginName": "test_cashier",
        "password": "Cashier@123",
        "realName": "Test Cashier",
        "userType": "CASHIER"
    }


class TestPatients:
    """Test patient data constants"""

    MALE_PATIENT = {
        "name": "张三",
        "gender": "M",
        "idType": "ID_CARD",
        "idNo": "110101199001011234",
        "phone": "13800138001",
        "birthDate": "1990-01-01",
        "address": "北京市朝阳区测试路1号"
    }

    FEMALE_PATIENT = {
        "name": "李四",
        "gender": "F",
        "idType": "ID_CARD",
        "idNo": "110101199002021234",
        "phone": "13800138002",
        "birthDate": "1990-02-02",
        "address": "北京市海淀区测试路2号"
    }

    CHILD_PATIENT = {
        "name": "王小明",
        "gender": "M",
        "idType": "BIRTH_CERT",
        "idNo": "B110101202001011234",
        "phone": "13800138003",
        "birthDate": "2020-01-01",
        "address": "北京市西城区测试路3号",
        "guardianName": "王大伟",
        "guardianPhone": "13800138004"
    }


class TestDepartments:
    """Test department data constants"""

    INTERNAL_MEDICINE = {
        "name": "内科",
        "code": "IM",
        "type": "CLINICAL",
        "description": "内科门诊"
    }

    SURGERY = {
        "name": "外科",
        "code": "SUR",
        "type": "CLINICAL",
        "description": "外科门诊"
    }

    PHARMACY = {
        "name": "药房",
        "code": "PHA",
        "type": "PHARMACY",
        "description": "门诊药房"
    }

    LABORATORY = {
        "name": "检验科",
        "code": "LAB",
        "type": "LAB",
        "description": "临床检验科"
    }

    RADIOLOGY = {
        "name": "放射科",
        "code": "RAD",
        "type": "RADIOLOGY",
        "description": "影像科"
    }


class TestDrugs:
    """Test drug data constants"""

    AMOXICILLIN = {
        "name": "阿莫西林胶囊",
        "code": "AMX001",
        "specification": "0.5g*24粒",
        "unit": "盒",
        "price": 15.50,
        "category": "ANTIBIOTIC",
        "storageCondition": "常温",
        "manufacturer": "测试制药厂"
    }

    IBUPROFEN = {
        "name": "布洛芬缓释胶囊",
        "code": "IBU001",
        "specification": "0.3g*20粒",
        "unit": "盒",
        "price": 28.00,
        "category": "ANALGESIC",
        "storageCondition": "常温",
        "manufacturer": "测试制药厂"
    }

    METFORMIN = {
        "name": "二甲双胍片",
        "code": "MET001",
        "specification": "0.25g*100片",
        "unit": "瓶",
        "price": 12.00,
        "category": "ANTIDIABETIC",
        "storageCondition": "常温",
        "manufacturer": "测试制药厂"
    }


class TestDiseases:
    """Test disease data constants"""

    COMMON_COLD = {
        "code": "J00",
        "name": "急性鼻咽炎",
        "description": "普通感冒"
    }

    HYPERTENSION = {
        "code": "I10",
        "name": "原发性高血压",
        "description": "高血压病"
    }

    DIABETES = {
        "code": "E11",
        "name": "2型糖尿病",
        "description": "非胰岛素依赖型糖尿病"
    }


class TestStatusCodes:
    """Expected HTTP status codes"""

    SUCCESS = 200
    CREATED = 201
    NO_CONTENT = 204
    BAD_REQUEST = 400
    UNAUTHORIZED = 401
    FORBIDDEN = 403
    NOT_FOUND = 404
    INTERNAL_ERROR = 500


class TestResponseCodes:
    """Expected business response codes"""

    SUCCESS = 0
    PARAM_ERROR = 400
    AUTH_ERROR = 401
    FORBIDDEN = 403
    NOT_FOUND = 404
    SERVER_ERROR = 500


class TestDataTemplates:
    """Test data templates for generating test data"""

    USER_TEMPLATE = {
        "userName": "",
        "loginName": "",
        "password": "Test@123456",
        "realName": "",
        "userType": "STAFF",
        "status": "NORMAL",
        "phone": "",
        "email": ""
    }

    PATIENT_TEMPLATE = {
        "name": "",
        "gender": "M",
        "idType": "ID_CARD",
        "idNo": "",
        "phone": "",
        "birthDate": "",
        "address": ""
    }

    DRUG_TEMPLATE = {
        "name": "",
        "code": "",
        "specification": "",
        "unit": "盒",
        "price": 0.0,
        "category": "",
        "status": "NORMAL"
    }

    PRESCRIPTION_TEMPLATE = {
        "patientId": "",
        "doctorId": "",
        "departmentId": "",
        "diagnosis": "",
        "items": []
    }
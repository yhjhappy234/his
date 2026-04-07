#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HIS Platform Test Report Generator
Generates consolidated test reports from various sources
"""

import json
import os
import xml.etree.ElementTree as ET
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Any

class TestReportGenerator:
    """Generate consolidated test reports"""

    def __init__(self, report_dir: str):
        self.report_dir = Path(report_dir)
        self.report_dir.mkdir(parents=True, exist_ok=True)

    def parse_junit_xml(self, xml_path: str) -> Dict[str, Any]:
        """Parse JUnit XML report"""
        try:
            tree = ET.parse(xml_path)
            root = tree.getroot()

            testsuite = root.find('testsuite') if root.tag == 'testsuites' else root

            return {
                'name': testsuite.get('name', 'Unknown'),
                'tests': int(testsuite.get('tests', 0)),
                'failures': int(testsuite.get('failures', 0)),
                'errors': int(testsuite.get('errors', 0)),
                'skipped': int(testsuite.get('skipped', 0)),
                'time': float(testsuite.get('time', 0))
            }
        except Exception as e:
            print(f"Error parsing {xml_path}: {e}")
            return None

    def parse_jacoco_xml(self, xml_path: str) -> Dict[str, Any]:
        """Parse JaCoCo coverage XML"""
        try:
            tree = ET.parse(xml_path)
            root = tree.getroot()

            coverage = {}
            for counter in root.findall('.//counter'):
                counter_type = counter.get('type')
                missed = int(counter.get('missed', 0))
                covered = int(counter.get('covered', 0))
                total = missed + covered
                percentage = (covered / total * 100) if total > 0 else 0

                coverage[counter_type.lower()] = {
                    'missed': missed,
                    'covered': covered,
                    'total': total,
                    'percentage': round(percentage, 2)
                }

            return coverage
        except Exception as e:
            print(f"Error parsing JaCoCo XML: {e}")
            return {}

    def generate_summary_report(self, unit_results: List[Dict], coverage: Dict, e2e_results: Dict) -> str:
        """Generate markdown summary report"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")

        total_tests = sum(r['tests'] for r in unit_results if r)
        total_failures = sum(r['failures'] for r in unit_results if r)
        total_errors = sum(r['errors'] for r in unit_results if r)
        total_passed = total_tests - total_failures - total_errors

        line_coverage = coverage.get('line', {}).get('percentage', 0)
        branch_coverage = coverage.get('branch', {}).get('percentage', 0)

        e2e_passed = e2e_results.get('passed', 0)
        e2e_failed = e2e_results.get('failed', 0)
        e2e_total = e2e_passed + e2e_failed

        report = f"""# HIS Platform Test Report

**Generated:** {datetime.now().strftime("%Y-%m-%d %H:%M:%S")}

## Executive Summary

| Metric | Value | Target | Status |
|--------|-------|--------|--------|
| Unit Tests | {total_passed}/{total_tests} passed | 100% | {'✅' if total_failures == 0 and total_errors == 0 else '❌'} |
| Line Coverage | {line_coverage}% | >= 90% | {'✅' if line_coverage >= 90 else '❌'} |
| Branch Coverage | {branch_coverage}% | >= 90% | {'✅' if branch_coverage >= 90 else '❌'} |
| E2E Tests | {e2e_passed}/{e2e_total} passed | 100% | {'✅' if e2e_failed == 0 else '❌'} |

## Unit Test Details

| Module | Tests | Passed | Failed | Errors | Time (s) |
|--------|-------|--------|--------|--------|----------|
"""

        for result in unit_results:
            if result:
                passed = result['tests'] - result['failures'] - result['errors']
                report += f"| {result['name']} | {result['tests']} | {passed} | {result['failures']} | {result['errors']} | {result['time']:.2f} |\n"

        report += f"""
## Code Coverage

| Type | Covered | Missed | Total | Coverage |
|------|---------|--------|-------|----------|
| Line | {coverage.get('line', {}).get('covered', 0)} | {coverage.get('line', {}).get('missed', 0)} | {coverage.get('line', {}).get('total', 0)} | {line_coverage}% |
| Branch | {coverage.get('branch', {}).get('covered', 0)} | {coverage.get('branch', {}).get('missed', 0)} | {coverage.get('branch', {}).get('total', 0)} | {branch_coverage}% |
| Method | {coverage.get('method', {}).get('covered', 0)} | {coverage.get('method', {}).get('missed', 0)} | {coverage.get('method', {}).get('total', 0)} | {coverage.get('method', {}).get('percentage', 0)}% |
| Class | {coverage.get('class', {}).get('covered', 0)} | {coverage.get('class', {}).get('missed', 0)} | {coverage.get('class', {}).get('total', 0)} | {coverage.get('class', {}).get('percentage', 0)}% |

## E2E Test Details

| Test Suite | Passed | Failed | Total | Pass Rate |
|------------|--------|--------|-------|-----------|
| System Module | - | - | - | - |
| Outpatient Module | - | - | - | - |
| Pharmacy Module | - | - | - | - |
| Finance Module | - | - | - | - |
| LIS Module | - | - | - | - |

## Quality Gate Results

- [{'x' if total_failures == 0 and total_errors == 0 else ' '}] All unit tests pass
- [{'x' if line_coverage >= 90 else ' '}] Line coverage >= 90%
- [{'x' if branch_coverage >= 90 else ' '}] Branch coverage >= 90%
- [{'x' if e2e_failed == 0 else ' '}] All E2E tests pass

---
*Generated by HIS Platform Test Runner*
"""

        return report

    def save_report(self, content: str, filename: str):
        """Save report to file"""
        filepath = self.report_dir / filename
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Report saved: {filepath}")


def main():
    import glob

    report_gen = TestReportGenerator('report')

    # Parse unit test results
    unit_results = []
    for xml_file in glob.glob('**/target/surefire-reports/TEST-*.xml', recursive=True):
        result = report_gen.parse_junit_xml(xml_file)
        if result:
            unit_results.append(result)

    # Parse coverage
    coverage = {}
    jacoco_file = 'target/site/jacoco/jacoco.xml'
    if os.path.exists(jacoco_file):
        coverage = report_gen.parse_jacoco_xml(jacoco_file)

    # Parse E2E results
    e2e_results = {}
    for json_file in glob.glob('report/e2e_report_*.json'):
        try:
            with open(json_file) as f:
                data = json.load(f)
                summary = data.get('summary', {})
                e2e_results = {
                    'passed': summary.get('passed', 0),
                    'failed': summary.get('failed', 0),
                    'total': summary.get('total_tests', 0)
                }
        except:
            pass

    # Generate and save summary
    summary = report_gen.generate_summary_report(unit_results, coverage, e2e_results)
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    report_gen.save_report(summary, f'test_summary_{timestamp}.md')

    print("\nTest Summary Generated!")
    print(f"Unit Tests: {len(unit_results)} test files")
    print(f"Coverage: {coverage.get('line', {}).get('percentage', 0):.1f}% line, {coverage.get('branch', {}).get('percentage', 0):.1f}% branch")
    print(f"E2E Tests: {e2e_results.get('passed', 0)} passed, {e2e_results.get('failed', 0)} failed")


if __name__ == '__main__':
    main()
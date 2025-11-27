#!/usr/bin/env python3
"""
LeoConnect API Test Script
Tests all endpoints of the LeoConnect backend API
"""

import requests
import json
from typing import Dict, Any
from datetime import datetime


class Colors:
    """ANSI color codes for terminal output"""
    GREEN = '\033[92m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    RESET = '\033[0m'
    BOLD = '\033[1m'


class LeoConnectAPITester:
    def __init__(self, base_url: str = "https://leoconnect.rexosphere.com"):
        self.base_url = base_url
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'LeoConnect-API-Tester/1.0',
            'Accept': 'application/json'
        })
        self.test_results = []

    def log_test(self, test_name: str, passed: bool, message: str = ""):
        """Log test results"""
        status = f"{Colors.GREEN}✓ PASS{Colors.RESET}" if passed else f"{Colors.RED}✗ FAIL{Colors.RESET}"
        print(f"{status} {Colors.BOLD}{test_name}{Colors.RESET}")
        if message:
            print(f"  {message}")
        self.test_results.append({
            'test': test_name,
            'passed': passed,
            'message': message
        })

    def test_health_check(self):
        """Test if the API is reachable"""
        print(f"\n{Colors.BLUE}{'='*60}{Colors.RESET}")
        print(f"{Colors.BOLD}Testing API Health{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*60}{Colors.RESET}\n")

        try:
            response = self.session.get(f"{self.base_url}/", timeout=10)
            self.log_test(
                "API Reachability",
                response.status_code < 500,
                f"Status: {response.status_code}"
            )
            return True
        except requests.exceptions.RequestException as e:
            self.log_test("API Reachability", False, f"Error: {str(e)}")
            return False

    def test_google_sign_in(self):
        """Test Google Sign-In endpoint"""
        print(f"\n{Colors.BLUE}{'='*60}{Colors.RESET}")
        print(f"{Colors.BOLD}Testing Authentication{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*60}{Colors.RESET}\n")

        try:
            response = self.session.post(
                f"{self.base_url}/auth/google",
                json={},
                timeout=10
            )

            self.log_test(
                "POST /auth/google",
                response.status_code in [200, 201, 401, 403],
                f"Status: {response.status_code} | Response: {response.text[:100]}"
            )

            if response.status_code == 200:
                try:
                    data = response.json()
                    has_profile = all(key in data for key in ['uid', 'email'])
                    self.log_test(
                        "Google Sign-In Response Structure",
                        has_profile,
                        f"Contains user profile data: {has_profile}"
                    )
                except json.JSONDecodeError:
                    self.log_test("Google Sign-In Response Structure", False, "Invalid JSON response")

        except requests.exceptions.RequestException as e:
            self.log_test("POST /auth/google", False, f"Error: {str(e)}")

    def test_get_home_feed(self):
        """Test home feed endpoint"""
        print(f"\n{Colors.BLUE}{'='*60}{Colors.RESET}")
        print(f"{Colors.BOLD}Testing Feed Endpoints{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*60}{Colors.RESET}\n")

        try:
            # Test with limit parameter
            response = self.session.get(
                f"{self.base_url}/feed",
                params={'limit': 10},
                timeout=10
            )

            self.log_test(
                "GET /feed?limit=10",
                response.status_code in [200, 401, 404],
                f"Status: {response.status_code} | Response: {response.text[:100]}"
            )

            if response.status_code == 200:
                try:
                    data = response.json()
                    is_list = isinstance(data, list)
                    self.log_test(
                        "Feed Response Structure",
                        is_list,
                        f"Response is a list: {is_list} | Items: {len(data) if is_list else 'N/A'}"
                    )

                    if is_list and len(data) > 0:
                        post = data[0]
                        has_required_fields = all(key in post for key in ['postId', 'content'])
                        self.log_test(
                            "Feed Post Structure",
                            has_required_fields,
                            f"Post has required fields: {has_required_fields}"
                        )
                except json.JSONDecodeError:
                    self.log_test("Feed Response Structure", False, "Invalid JSON response")

        except requests.exceptions.RequestException as e:
            self.log_test("GET /feed", False, f"Error: {str(e)}")

    def test_like_post(self):
        """Test like post endpoint"""
        try:
            # Use a test post ID
            test_post_id = "test-post-123"
            response = self.session.post(
                f"{self.base_url}/posts/{test_post_id}/like",
                timeout=10
            )

            self.log_test(
                f"POST /posts/{test_post_id}/like",
                response.status_code in [200, 201, 401, 404],
                f"Status: {response.status_code} | Response: {response.text[:100]}"
            )

        except requests.exceptions.RequestException as e:
            self.log_test("POST /posts/:id/like", False, f"Error: {str(e)}")

    def test_create_post(self):
        """Test create post endpoint"""
        print(f"\n{Colors.BLUE}{'='*60}{Colors.RESET}")
        print(f"{Colors.BOLD}Testing Post Management{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*60}{Colors.RESET}\n")

        try:
            test_post = {
                "content": "Test post from API tester"
            }

            response = self.session.post(
                f"{self.base_url}/posts",
                json=test_post,
                headers={'Content-Type': 'application/json'},
                timeout=10
            )

            self.log_test(
                "POST /posts",
                response.status_code in [200, 201, 401, 403, 404],
                f"Status: {response.status_code} | Response: {response.text[:100]}"
            )

            if response.status_code in [200, 201]:
                try:
                    data = response.json()
                    has_post_id = 'postId' in data or 'id' in data
                    self.log_test(
                        "Create Post Response Structure",
                        has_post_id,
                        f"Response contains post ID: {has_post_id}"
                    )
                except json.JSONDecodeError:
                    self.log_test("Create Post Response Structure", False, "Invalid JSON response")

        except requests.exceptions.RequestException as e:
            self.log_test("POST /posts", False, f"Error: {str(e)}")

        # Test like post after creating
        self.test_like_post()

    def test_get_districts(self):
        """Test get districts endpoint"""
        print(f"\n{Colors.BLUE}{'='*60}{Colors.RESET}")
        print(f"{Colors.BOLD}Testing District & Club Endpoints{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*60}{Colors.RESET}\n")

        try:
            response = self.session.get(
                f"{self.base_url}/districts",
                timeout=10
            )

            self.log_test(
                "GET /districts",
                response.status_code in [200, 404],
                f"Status: {response.status_code} | Response: {response.text[:100]}"
            )

            if response.status_code == 200:
                try:
                    data = response.json()
                    is_list = isinstance(data, list)
                    self.log_test(
                        "Districts Response Structure",
                        is_list,
                        f"Response is a list: {is_list} | Districts: {len(data) if is_list else 'N/A'}"
                    )
                    return data if is_list and len(data) > 0 else None
                except json.JSONDecodeError:
                    self.log_test("Districts Response Structure", False, "Invalid JSON response")

            return None

        except requests.exceptions.RequestException as e:
            self.log_test("GET /districts", False, f"Error: {str(e)}")
            return None

    def test_get_clubs_by_district(self, district: str = "test-district"):
        """Test get clubs by district endpoint"""
        try:
            response = self.session.get(
                f"{self.base_url}/clubs",
                params={'district': district},
                timeout=10
            )

            self.log_test(
                f"GET /clubs?district={district}",
                response.status_code in [200, 404],
                f"Status: {response.status_code} | Response: {response.text[:100]}"
            )

            if response.status_code == 200:
                try:
                    data = response.json()
                    is_list = isinstance(data, list)
                    self.log_test(
                        "Clubs Response Structure",
                        is_list,
                        f"Response is a list: {is_list} | Clubs: {len(data) if is_list else 'N/A'}"
                    )

                    if is_list and len(data) > 0:
                        club = data[0]
                        has_required_fields = all(key in club for key in ['clubId', 'name'])
                        self.log_test(
                            "Club Structure",
                            has_required_fields,
                            f"Club has required fields: {has_required_fields}"
                        )
                except json.JSONDecodeError:
                    self.log_test("Clubs Response Structure", False, "Invalid JSON response")

        except requests.exceptions.RequestException as e:
            self.log_test("GET /clubs", False, f"Error: {str(e)}")

    def test_response_headers(self):
        """Test API response headers"""
        print(f"\n{Colors.BLUE}{'='*60}{Colors.RESET}")
        print(f"{Colors.BOLD}Testing Response Headers{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*60}{Colors.RESET}\n")

        try:
            response = self.session.get(f"{self.base_url}/feed?limit=1", timeout=10)

            # Check for CORS headers
            has_cors = 'Access-Control-Allow-Origin' in response.headers
            self.log_test(
                "CORS Headers",
                has_cors,
                f"CORS enabled: {has_cors}"
            )

            # Check content type
            content_type = response.headers.get('Content-Type', '')
            is_json = 'application/json' in content_type
            self.log_test(
                "Content-Type Header",
                is_json or response.status_code in [401, 404],
                f"Content-Type: {content_type}"
            )

        except requests.exceptions.RequestException as e:
            self.log_test("Response Headers", False, f"Error: {str(e)}")

    def run_all_tests(self):
        """Run all API tests"""
        print(f"\n{Colors.BOLD}{Colors.BLUE}{'='*60}{Colors.RESET}")
        print(f"{Colors.BOLD}LeoConnect API Test Suite{Colors.RESET}")
        print(f"{Colors.BOLD}Base URL: {self.base_url}{Colors.RESET}")
        print(f"{Colors.BOLD}Started at: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}{Colors.RESET}")
        print(f"{Colors.BOLD}{Colors.BLUE}{'='*60}{Colors.RESET}")

        # Check if API is reachable
        if not self.test_health_check():
            print(f"\n{Colors.RED}API is not reachable. Stopping tests.{Colors.RESET}")
            return

        # Run all endpoint tests
        self.test_google_sign_in()
        self.test_get_home_feed()
        self.test_create_post()

        # Test districts and clubs
        districts = self.test_get_districts()
        if districts and len(districts) > 0:
            self.test_get_clubs_by_district(districts[0])
        else:
            self.test_get_clubs_by_district()

        # Test response headers
        self.test_response_headers()

        # Print summary
        self.print_summary()

    def print_summary(self):
        """Print test summary"""
        print(f"\n{Colors.BLUE}{'='*60}{Colors.RESET}")
        print(f"{Colors.BOLD}Test Summary{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*60}{Colors.RESET}\n")

        total = len(self.test_results)
        passed = sum(1 for r in self.test_results if r['passed'])
        failed = total - passed

        print(f"Total Tests: {total}")
        print(f"{Colors.GREEN}Passed: {passed}{Colors.RESET}")
        print(f"{Colors.RED}Failed: {failed}{Colors.RESET}")
        print(f"Success Rate: {(passed/total*100):.1f}%\n")

        if failed > 0:
            print(f"{Colors.RED}Failed Tests:{Colors.RESET}")
            for result in self.test_results:
                if not result['passed']:
                    print(f"  - {result['test']}")
                    if result['message']:
                        print(f"    {result['message']}")

        print(f"\n{Colors.BOLD}Completed at: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*60}{Colors.RESET}\n")


def main():
    """Main function"""
    tester = LeoConnectAPITester()
    tester.run_all_tests()


if __name__ == "__main__":
    main()
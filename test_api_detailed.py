#!/usr/bin/env python3
"""
LeoConnect API Detailed Test Script
Tests API with more detailed analysis and optional authentication
"""

import requests
import json
import sys
from typing import Dict, Any, Optional
from datetime import datetime


class Colors:
    """ANSI color codes for terminal output"""
    GREEN = '\033[92m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    CYAN = '\033[96m'
    RESET = '\033[0m'
    BOLD = '\033[1m'


class LeoConnectDetailedTester:
    def __init__(self, base_url: str = "https://leoconnect.rexosphere.com", token: Optional[str] = None):
        self.base_url = base_url
        self.token = token
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'LeoConnect-API-Tester/2.0',
            'Accept': 'application/json'
        })
        if token:
            self.session.headers.update({
                'Authorization': f'Bearer {token}'
            })
        self.test_results = []

    def log_test(self, test_name: str, passed: bool, message: str = "", details: str = ""):
        """Log test results with details"""
        status = f"{Colors.GREEN}✓ PASS{Colors.RESET}" if passed else f"{Colors.RED}✗ FAIL{Colors.RESET}"
        print(f"{status} {Colors.BOLD}{test_name}{Colors.RESET}")
        if message:
            print(f"  {message}")
        if details:
            print(f"  {Colors.CYAN}{details}{Colors.RESET}")
        self.test_results.append({
            'test': test_name,
            'passed': passed,
            'message': message
        })

    def test_health_check(self):
        """Test if the API is reachable"""
        print(f"\n{Colors.BLUE}{'='*70}{Colors.RESET}")
        print(f"{Colors.BOLD}1. API Health Check{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*70}{Colors.RESET}\n")

        try:
            response = self.session.get(f"{self.base_url}/", timeout=10)
            self.log_test(
                "GET /",
                response.status_code == 200,
                f"Status: {response.status_code}",
                f"Response: {response.text[:200]}"
            )
            return response.status_code == 200
        except requests.exceptions.RequestException as e:
            self.log_test("GET /", False, f"Error: {str(e)}")
            return False

    def test_authentication(self):
        """Test authentication endpoint with detailed analysis"""
        print(f"\n{Colors.BLUE}{'='*70}{Colors.RESET}")
        print(f"{Colors.BOLD}2. Authentication Endpoint{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*70}{Colors.RESET}\n")

        # Test 1: Empty request
        try:
            response = self.session.post(f"{self.base_url}/auth/google", json={}, timeout=10)
            self.log_test(
                "POST /auth/google (empty body)",
                response.status_code in [400, 401],
                f"Status: {response.status_code}",
                f"Response: {response.text}"
            )
        except requests.exceptions.RequestException as e:
            self.log_test("POST /auth/google (empty body)", False, f"Error: {str(e)}")

        # Test 2: With mock token
        try:
            response = self.session.post(
                f"{self.base_url}/auth/google",
                json={"idToken": "mock_token_12345"},
                timeout=10
            )
            self.log_test(
                "POST /auth/google (mock token in body)",
                response.status_code in [200, 401, 403],
                f"Status: {response.status_code}",
                f"Response: {response.text[:200]}"
            )
        except requests.exceptions.RequestException as e:
            self.log_test("POST /auth/google (mock token)", False, f"Error: {str(e)}")

        # Test 3: With Authorization header
        try:
            headers = {'Authorization': 'Bearer mock_token_12345'}
            response = requests.post(
                f"{self.base_url}/auth/google",
                json={},
                headers={**self.session.headers, **headers},
                timeout=10
            )
            self.log_test(
                "POST /auth/google (token in header)",
                response.status_code in [200, 401, 403],
                f"Status: {response.status_code}",
                f"Response: {response.text[:200]}"
            )
        except requests.exceptions.RequestException as e:
            self.log_test("POST /auth/google (header)", False, f"Error: {str(e)}")

    def test_feed_endpoint(self):
        """Test feed endpoint with detailed analysis"""
        print(f"\n{Colors.BLUE}{'='*70}{Colors.RESET}")
        print(f"{Colors.BOLD}3. Feed Endpoint{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*70}{Colors.RESET}\n")

        # Test without auth
        try:
            response = requests.get(
                f"{self.base_url}/feed",
                params={'limit': 10},
                headers={'Accept': 'application/json'},
                timeout=10
            )

            if self.token:
                self.log_test(
                    "GET /feed?limit=10 (with auth)",
                    response.status_code == 200,
                    f"Status: {response.status_code}",
                    f"Response: {response.text[:300]}"
                )

                if response.status_code == 200:
                    try:
                        data = response.json()
                        self.analyze_feed_response(data)
                    except json.JSONDecodeError:
                        self.log_test("Feed Response Parsing", False, "Invalid JSON")
            else:
                self.log_test(
                    "GET /feed?limit=10 (no auth)",
                    response.status_code == 401,
                    f"Status: {response.status_code} - Correctly requires authentication",
                    f"Response: {response.text}"
                )
        except requests.exceptions.RequestException as e:
            self.log_test("GET /feed", False, f"Error: {str(e)}")

    def analyze_feed_response(self, data):
        """Analyze feed response structure"""
        # Check if it's wrapped in posts object or direct array
        if isinstance(data, dict) and 'posts' in data:
            posts = data['posts']
            self.log_test(
                "Feed Response Structure",
                True,
                "Response wrapped in 'posts' object"
            )
        elif isinstance(data, list):
            posts = data
            self.log_test(
                "Feed Response Structure",
                True,
                "Response is direct array"
            )
        else:
            self.log_test(
                "Feed Response Structure",
                False,
                f"Unexpected structure: {type(data)}"
            )
            return

        if len(posts) > 0:
            post = posts[0]
            print(f"\n  {Colors.CYAN}Sample Post Structure:{Colors.RESET}")
            print(f"  {json.dumps(post, indent=2)}\n")

            # Check required fields
            required_fields = ['postId', 'clubId', 'authorName', 'content', 'likesCount', 'isLikedByUser']
            missing_fields = [f for f in required_fields if f not in post]

            if missing_fields:
                self.log_test(
                    "Post Required Fields",
                    False,
                    f"Missing fields: {', '.join(missing_fields)}"
                )
            else:
                self.log_test(
                    "Post Required Fields",
                    True,
                    "All required fields present"
                )

    def test_districts_and_clubs(self):
        """Test districts and clubs endpoints"""
        print(f"\n{Colors.BLUE}{'='*70}{Colors.RESET}")
        print(f"{Colors.BOLD}4. Districts & Clubs Endpoints{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*70}{Colors.RESET}\n")

        # Test districts
        try:
            response = requests.get(f"{self.base_url}/districts", timeout=10)
            self.log_test(
                "GET /districts",
                response.status_code == 200,
                f"Status: {response.status_code}"
            )

            if response.status_code == 200:
                districts = response.json()
                print(f"  {Colors.CYAN}Districts found: {districts}{Colors.RESET}\n")

                # Test clubs for each district
                for district in districts[:2]:  # Test first 2 districts
                    self.test_clubs_for_district(district)
        except requests.exceptions.RequestException as e:
            self.log_test("GET /districts", False, f"Error: {str(e)}")

    def test_clubs_for_district(self, district: str):
        """Test clubs endpoint for a specific district"""
        try:
            response = requests.get(
                f"{self.base_url}/clubs",
                params={'district': district},
                timeout=10
            )

            self.log_test(
                f"GET /clubs?district={district}",
                response.status_code == 200,
                f"Status: {response.status_code}",
                f"Response: {response.text[:300]}"
            )

            if response.status_code == 200:
                try:
                    clubs = response.json()
                    if isinstance(clubs, list) and len(clubs) > 0:
                        club = clubs[0]
                        print(f"  {Colors.CYAN}Sample Club:{Colors.RESET}")
                        print(f"  {json.dumps(club, indent=2)}\n")

                        # Check required fields
                        required_fields = ['clubId', 'name', 'district']
                        missing_fields = [f for f in required_fields if f not in club]

                        if missing_fields:
                            self.log_test(
                                f"Club Structure ({district})",
                                False,
                                f"Missing fields: {', '.join(missing_fields)}"
                            )
                        else:
                            self.log_test(
                                f"Club Structure ({district})",
                                True,
                                f"Found {len(clubs)} club(s)"
                            )
                except json.JSONDecodeError:
                    self.log_test(f"Clubs Response ({district})", False, "Invalid JSON")
        except requests.exceptions.RequestException as e:
            self.log_test(f"GET /clubs ({district})", False, f"Error: {str(e)}")

    def test_create_post(self):
        """Test post creation"""
        print(f"\n{Colors.BLUE}{'='*70}{Colors.RESET}")
        print(f"{Colors.BOLD}5. Post Management{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*70}{Colors.RESET}\n")

        if not self.token:
            print(f"  {Colors.YELLOW}Skipping post creation tests (no auth token){Colors.RESET}\n")
            return

        try:
            # Test post creation
            test_post = {
                "content": "Test post from detailed API tester",
                "imageUrl": "https://example.com/test.jpg"
            }

            response = self.session.post(
                f"{self.base_url}/posts",
                json=test_post,
                timeout=10
            )

            self.log_test(
                "POST /posts",
                response.status_code in [200, 201],
                f"Status: {response.status_code}",
                f"Response: {response.text[:300]}"
            )

            if response.status_code in [200, 201]:
                try:
                    post = response.json()
                    if 'postId' in post or 'id' in post:
                        post_id = post.get('postId', post.get('id'))
                        self.test_like_post(post_id)
                except json.JSONDecodeError:
                    self.log_test("Post Creation Response", False, "Invalid JSON")
        except requests.exceptions.RequestException as e:
            self.log_test("POST /posts", False, f"Error: {str(e)}")

    def test_like_post(self, post_id: str):
        """Test liking a post"""
        try:
            response = self.session.post(
                f"{self.base_url}/posts/{post_id}/like",
                timeout=10
            )

            self.log_test(
                f"POST /posts/{post_id}/like",
                response.status_code in [200, 201],
                f"Status: {response.status_code}",
                f"Response: {response.text}"
            )
        except requests.exceptions.RequestException as e:
            self.log_test(f"POST /posts/:id/like", False, f"Error: {str(e)}")

    def test_cors(self):
        """Test CORS configuration"""
        print(f"\n{Colors.BLUE}{'='*70}{Colors.RESET}")
        print(f"{Colors.BOLD}6. CORS & Headers{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*70}{Colors.RESET}\n")

        try:
            response = requests.get(f"{self.base_url}/districts", timeout=10)

            # Check CORS headers
            cors_headers = {
                'Access-Control-Allow-Origin': response.headers.get('Access-Control-Allow-Origin'),
                'Access-Control-Allow-Methods': response.headers.get('Access-Control-Allow-Methods'),
                'Access-Control-Allow-Headers': response.headers.get('Access-Control-Allow-Headers'),
            }

            print(f"  {Colors.CYAN}CORS Headers:{Colors.RESET}")
            for header, value in cors_headers.items():
                if value:
                    print(f"    {header}: {value}")
            print()

            has_cors = cors_headers['Access-Control-Allow-Origin'] is not None
            self.log_test(
                "CORS Configuration",
                has_cors,
                f"CORS {'enabled' if has_cors else 'disabled'}"
            )
        except requests.exceptions.RequestException as e:
            self.log_test("CORS Check", False, f"Error: {str(e)}")

    def run_all_tests(self):
        """Run all tests"""
        print(f"\n{Colors.BOLD}{Colors.BLUE}{'='*70}{Colors.RESET}")
        print(f"{Colors.BOLD}LeoConnect API Detailed Test Suite{Colors.RESET}")
        print(f"{Colors.BOLD}Base URL: {self.base_url}{Colors.RESET}")
        if self.token:
            print(f"{Colors.BOLD}Auth Token: Provided{Colors.RESET}")
        else:
            print(f"{Colors.YELLOW}Auth Token: Not provided (some tests will be skipped){Colors.RESET}")
        print(f"{Colors.BOLD}Started at: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}{Colors.RESET}")
        print(f"{Colors.BOLD}{Colors.BLUE}{'='*70}{Colors.RESET}")

        # Run all tests
        if not self.test_health_check():
            print(f"\n{Colors.RED}API is not reachable. Stopping tests.{Colors.RESET}")
            return

        self.test_authentication()
        self.test_feed_endpoint()
        self.test_districts_and_clubs()
        self.test_create_post()
        self.test_cors()

        # Print summary
        self.print_summary()

    def print_summary(self):
        """Print test summary"""
        print(f"\n{Colors.BLUE}{'='*70}{Colors.RESET}")
        print(f"{Colors.BOLD}Test Summary{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*70}{Colors.RESET}\n")

        total = len(self.test_results)
        passed = sum(1 for r in self.test_results if r['passed'])
        failed = total - passed

        print(f"Total Tests: {total}")
        print(f"{Colors.GREEN}Passed: {passed}{Colors.RESET}")
        print(f"{Colors.RED}Failed: {failed}{Colors.RESET}")
        if total > 0:
            print(f"Success Rate: {(passed/total*100):.1f}%\n")

        if failed > 0:
            print(f"{Colors.RED}Failed Tests:{Colors.RESET}")
            for result in self.test_results:
                if not result['passed']:
                    print(f"  ✗ {result['test']}")
                    if result['message']:
                        print(f"    {result['message']}")
            print()

        print(f"{Colors.BOLD}Completed at: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}{Colors.RESET}")
        print(f"{Colors.BLUE}{'='*70}{Colors.RESET}\n")


def main():
    """Main function"""
    token = None
    if len(sys.argv) > 1:
        token = sys.argv[1]
        print(f"Using provided authentication token")

    tester = LeoConnectDetailedTester(token=token)
    tester.run_all_tests()


if __name__ == "__main__":
    main()
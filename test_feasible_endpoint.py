#!/usr/bin/env python3
"""
Simple test script for the GET /api/recipe/feasible endpoint.

Requires a running CocktailPi backend. Adjust BASE_URL, USERNAME,
and PASSWORD below to match your environment.

Usage:
    python3 test_feasible_endpoint.py
"""

import json
import sys
import requests

BASE_URL = "http://localhost:8080"
USERNAME = "Admin"
PASSWORD = "123456"


def login(session):
    """Authenticate and return a JWT token."""
    resp = session.post(
        f"{BASE_URL}/api/auth/login",
        json={"username": USERNAME, "password": PASSWORD, "remember": False},
    )
    if resp.status_code != 200:
        print(f"FAIL  Login failed (HTTP {resp.status_code}): {resp.text}")
        sys.exit(1)
    token = resp.json().get("accessToken") or resp.json().get("token")
    print(f"OK    Logged in as '{USERNAME}', got JWT token")
    return token


def test_feasible_returns_200(session, token):
    """Basic test: the endpoint responds with 200 and a JSON list."""
    resp = session.get(
        f"{BASE_URL}/api/recipe/feasible",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert resp.status_code == 200, (
        f"Expected 200, got {resp.status_code}: {resp.text}"
    )
    data = resp.json()
    assert isinstance(data, list), f"Expected a JSON list, got {type(data).__name__}"
    print(f"OK    GET /api/recipe/feasible returned 200 with {len(data)} recipe ID(s)")
    return data


def test_feasible_ids_are_integers(data):
    """Every element in the response should be an integer recipe ID."""
    for item in data:
        assert isinstance(item, int), f"Expected int, got {type(item).__name__}: {item}"
    print(f"OK    All returned IDs are integers")


def test_feasible_ids_are_valid_recipes(session, token, feasible_ids):
    """Each returned ID should correspond to an existing recipe."""
    for rid in feasible_ids[:5]:  # spot-check up to 5
        resp = session.get(
            f"{BASE_URL}/api/recipe/{rid}",
            headers={"Authorization": f"Bearer {token}"},
        )
        assert resp.status_code == 200, (
            f"Recipe {rid} not found (HTTP {resp.status_code})"
        )
        name = resp.json().get("name", "<unnamed>")
        print(f"OK    Recipe ID {rid} exists: '{name}'")


def test_feasible_recipes_pass_individual_feasibility(session, token, feasible_ids):
    """
    Cross-check: each feasible recipe should also pass the single-recipe
    feasibility endpoint (ignoring the glass check, which our new endpoint
    intentionally skips).
    """
    for rid in feasible_ids[:5]:  # spot-check up to 5
        # Get recipe to find its default glass size
        recipe_resp = session.get(
            f"{BASE_URL}/api/recipe/{rid}",
            headers={"Authorization": f"Bearer {token}"},
        )
        recipe = recipe_resp.json()
        glass = recipe.get("defaultGlass")
        amount = glass["size"] if glass else 0

        resp = session.put(
            f"{BASE_URL}/api/cocktail/{rid}/feasibility",
            headers={"Authorization": f"Bearer {token}"},
            json={
                "amountOrderedInMl": amount,
                "ingredientGroupReplacements": [],
                "customisations": {
                    "boost": 100,
                    "additionalIngredients": [],
                },
            },
        )
        assert resp.status_code == 200, (
            f"Feasibility check for recipe {rid} failed (HTTP {resp.status_code})"
        )
        report = resp.json()
        groups_ok = report.get("allIngredientGroupsReplaced", False)
        all_amounts_ok = all(
            ri.get("amountMissing", 1) == 0
            for ri in report.get("requiredIngredients", [])
        )
        # We skip failNoGlass intentionally
        assert groups_ok and all_amounts_ok, (
            f"Recipe {rid} returned by /feasible but individual check disagrees: "
            f"groupsOk={groups_ok}, amountsOk={all_amounts_ok}, report={json.dumps(report, indent=2)}"
        )
        print(f"OK    Recipe ID {rid} passes individual feasibility cross-check")


def test_unauthenticated_returns_401(session):
    """Without a token, the endpoint should reject the request."""
    resp = session.get(f"{BASE_URL}/api/recipe/feasible")
    assert resp.status_code == 401, (
        f"Expected 401 without auth, got {resp.status_code}"
    )
    print(f"OK    Unauthenticated request correctly returns 401")


def main():
    session = requests.Session()
    passed = 0
    failed = 0

    tests = []

    # -- Login --
    try:
        token = login(session)
    except Exception as e:
        print(f"FAIL  Could not log in: {e}")
        print("\nMake sure the CocktailPi backend is running and credentials are correct.")
        sys.exit(1)

    # -- Test: unauthenticated 401 --
    try:
        test_unauthenticated_returns_401(session)
        passed += 1
    except AssertionError as e:
        print(f"FAIL  {e}")
        failed += 1

    # -- Test: basic 200 + list --
    feasible_ids = []
    try:
        feasible_ids = test_feasible_returns_200(session, token)
        passed += 1
    except AssertionError as e:
        print(f"FAIL  {e}")
        failed += 1

    # -- Test: IDs are integers --
    try:
        test_feasible_ids_are_integers(feasible_ids)
        passed += 1
    except AssertionError as e:
        print(f"FAIL  {e}")
        failed += 1

    # -- Test: IDs are valid recipes --
    if feasible_ids:
        try:
            test_feasible_ids_are_valid_recipes(session, token, feasible_ids)
            passed += 1
        except AssertionError as e:
            print(f"FAIL  {e}")
            failed += 1

        # -- Test: cross-check with individual feasibility --
        try:
            test_feasible_recipes_pass_individual_feasibility(session, token, feasible_ids)
            passed += 1
        except AssertionError as e:
            print(f"FAIL  {e}")
            failed += 1
    else:
        print("SKIP  No feasible recipes returned; skipping recipe-level tests")
        print("      (This is expected if no pumps are configured with ingredients)")

    # -- Summary --
    print(f"\n{'='*50}")
    print(f"Results: {passed} passed, {failed} failed")
    if failed:
        sys.exit(1)
    print("All tests passed!")


if __name__ == "__main__":
    main()

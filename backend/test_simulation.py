import requests
import json
import sys

BASE_URL = "http://localhost:5000"

def test_simulation(checkout_request_id, scenario="SUCCESS"):
    """
    Sends a simulated M-Pesa callback request to the local Flask backend.
    Supported scenarios: SUCCESS, CANCELLED, FAILED
    """
    url = f"{BASE_URL}/api/mpesa/simulate_callback"
    payload = {
        "checkoutRequestId": checkout_request_id,
        "scenario": scenario
    }
    headers = {"Content-Type": "application/json"}

    print(f"\n--- Triggering Simulated Callback [{scenario}] ---")
    print(f"CheckoutRequestID: {checkout_request_id}")

    try:
        response = requests.post(url, json=payload, headers=headers)
        print(f"HTTP Status: {response.status_code}")
        print("Response JSON:")
        print(json.dumps(response.json(), indent=2))
    except Exception as e:
        print(f"Error calling simulation endpoint: {e}")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python test_simulation.py <CheckoutRequestID> [SUCCESS|CANCELLED|FAILED]")
        sys.exit(1)

    cid = sys.argv[1]
    scen = sys.argv[2] if len(sys.argv) > 2 else "SUCCESS"
    test_simulation(cid, scen)

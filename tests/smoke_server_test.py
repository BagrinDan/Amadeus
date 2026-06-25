import os
import requests
import pytest 
import logging
from dotenv import load_dotenv

load_dotenv()
logger = logging.getLogger(__name__)

AZURE_APP_URL = os.getenv("AZURE_APP_URL")
ACTUATOR_USER=os.getenv("ACTUATOR_USER")
ACTUATOR_PASSWORD=os.getenv("ACTUATOR_PASSWORD")


# 1. Test azure backend
def test_ping_azure():
    logger.info("\n[>] Start pinging Azure server...")

    target_url = AZURE_APP_URL + "/health"

    try:
        response = requests.get(target_url, timeout=120)
        assert response.status_code == 200, f"[!] Server returned error code: {response.status_code}"

        json_data = response.json()
        assert json_data.get("status") == "UP", f"[!] Application status is not up. Current: {json_data}"

        logger.info(f"[!] Server is: {json_data['status']}")

        logger.info("[*] Smoke test passed...")
    except requests.exceptions.Timeout:
        pytest.fail("Smoke test failed. Server timed out. Azure might be waking up or overloaded")
    except requests.exceptions.ConnectionError:
        pytest.fail("Smoke test failed: Could not connect to server. Check the URL")
    except Exception as e:
        pytest.fail(f"Smoke test failed due to an unexpected error: {e}")



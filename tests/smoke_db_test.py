import os
import requests
import pytest 
import logging
from dotenv import load_dotenv

load_dotenv()
logger = logging.getLogger(__name__)

AZURE_APP_URL = os.getenv("AZURE_APP_URL") # TODO: TO add in Azure Env
ACTUATOR_USER=os.getenv("ACTUATOR_USER")
ACTUATOR_PASSWORD=os.getenv("ACTUATOR_PASSWORD")


# 2. Test azure DB
def test_ping_db():

    logger.info("\n[>] Start pinging Azure DB ...")
    target_url = AZURE_APP_URL + "/actuator/health/db"
    
    try:
        response = requests.get(
            target_url,
            auth=(ACTUATOR_USER, ACTUATOR_PASSWORD),
            timeout=60
        )
        assert response.status_code == 200
        body = response.json()

        if body["status"] == "DOWN":
            pytest.fail("[!] DB is DOWN")

        assert body["status"] == "UP"

        logger.info(body["status"])
        logger.info(body["text"])

        logger.info("[*] Smoke test passed...")
    except requests.exceptions.Timeout:
        pytest.fail("Smoke test failed. DB timed out.")
    except requests.exceptions.ConnectionError:
        pytest.fail("Smoke test failed: Could not connect.")
    except Exception as e:
        pytest.fail(f"Smoke test failed: {e}")

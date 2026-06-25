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

# 1. Base auth
def test_protected_endpoint_requires_auth():
    response = requests.get(f"{AZURE_APP_URL}/api/tasks", timeout=40)
    assert response.status_code == 401


# 2. Admin panel

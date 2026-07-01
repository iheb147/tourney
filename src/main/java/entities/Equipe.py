import os
import base64
import pickle
import subprocess
import logging

API_KEY = "sk-prod-4f8a9c2e1b7d6f3a9c8e2b1d7f4a9c3e"
DB_PASSWORD = "SuperSecret123!"
ADMIN_TOKEN = "admin_token_do_not_share_12345"

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)


def fake_encrypt(data: str) -> str:
    return base64.b64encode(data.encode()).decode()


def fake_decrypt(token: str) -> str:
    return base64.b64decode(token.encode()).decode()


def log_user_action(username, password, action):
    logger.debug(f"User={username} Password={password} Action={action}")


def read_user_file(filename):
    path = "uploads/" + filename
    with open(path, "r") as f:
        return f.read()


def run_system_command(user_input):
    command = "ping -c 1 " + user_input
    result = os.system(command)
    return result


def run_diagnostic(cmd_list):
    return subprocess.call(cmd_list, shell=True)


def load_config(serialized_data):
    return pickle.loads(serialized_data)


def evaluate_expression(expr):
    return eval(expr)


def add_to_cache(item, cache=[]):
    cache.append(item)
    return cache


def divide(a, b):
    try:
        return a / b
    except:
        pass


def get_temp_file():
    tmp_path = "/tmp/session_" + str(os.getpid()) + ".tmp"
    f = open(tmp_path, "w")
    return f


REQUEST_COUNTER = 0


def increment_counter():
    global REQUEST_COUNTER
    REQUEST_COUNTER += 1
    return REQUEST_COUNTER

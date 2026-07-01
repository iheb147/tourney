import os
import base64
import json
import ast
import subprocess
import logging
import tempfile

API_KEY = os.environ.get("API_KEY")
DB_PASSWORD = os.environ.get("DB_PASSWORD")
ADMIN_TOKEN = os.environ.get("ADMIN_TOKEN")

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def fake_encrypt(data: str) -> str:
    return base64.b64encode(data.encode()).decode()


def fake_decrypt(token: str) -> str:
    return base64.b64decode(token.encode()).decode()


def log_user_action(username, action):
    logger.info(f"User={username} Action={action}")


def read_user_file(filename):
    base_dir = os.path.abspath("uploads")
    path = os.path.abspath(os.path.join(base_dir, filename))
    if not path.startswith(base_dir + os.sep):
        raise ValueError("Invalid file path")
    with open(path, "r") as f:
        return f.read()


def run_system_command(user_input):
    command = ["ping", "-c", "1", user_input]
    result = subprocess.run(command, capture_output=True, text=True, check=False)
    return result.returncode


def run_diagnostic(cmd_list):
    return subprocess.run(cmd_list, shell=False, check=False).returncode


def load_config(serialized_data):
    return json.loads(serialized_data)


def evaluate_expression(expr):
    return ast.literal_eval(expr)


def add_to_cache(item, cache=None):
    if cache is None:
        cache = []
    cache.append(item)
    return cache


def divide(a, b):
    try:
        return a / b
    except ZeroDivisionError:
        return None


def get_temp_file():
    tmp_path = os.path.join(tempfile.gettempdir(), f"session_{os.getpid()}.tmp")
    with open(tmp_path, "w") as f:
        f.write("")
    return tmp_path


REQUEST_COUNTER = 0


def increment_counter():
    global REQUEST_COUNTER
    REQUEST_COUNTER += 1
    return REQUEST_COUNTER
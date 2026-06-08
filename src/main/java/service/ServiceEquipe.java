import os
import requests

TOKEN = os.environ.get("GITHUB_TOKEN")
if not TOKEN:
    raise ValueError("GITHUB_TOKEN environment variable not set")

REPO = os.environ.get("GITHUB_REPO", "myrepo")

def update_file():
    headers = {
        "Authorization": f"Bearer {TOKEN}"
    }

    try:
        response = requests.get(
            f"https://api.github.com/repos/{REPO}/contents/test.py",
            headers=headers,
            timeout=10
        )
        response.raise_for_status()
    except requests.exceptions.RequestException as e:
        print(f"Error fetching file: {e}")
        return

    data = response.json()
    sha = data.get("sha")
    if not sha:
        print("Error: SHA not found in response")
        return

    try:
        put_response = requests.put(
            f"https://api.github.com/repos/{REPO}/contents/test.py",
            headers=headers,
            json={
                "message": "fix file",
                "content": "bmV3IGNvZGU=",
                "sha": sha
            },
            timeout=10
        )
        put_response.raise_for_status()
        print("File updated successfully")
    except requests.exceptions.RequestException as e:
        print(f"Error updating file: {e}")

if __name__ == "__main__":
    update_file()
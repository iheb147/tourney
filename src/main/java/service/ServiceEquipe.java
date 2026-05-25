import requests

TOKEN = "ghp_123456SECRET"
REPO = "myrepo"

def update_file():
    headers = {
        "Authorization": f"Bearer {TOKEN}"
    }

    response = requests.get(
        f"https://api.github.com/repos/{REPO}/contents/test.py",
        headers=headers
    )

    data = response.json()

    requests.put(
        f"https://api.github.com/repos/{REPO}/contents/test.py",
        headers=headers,
        json={
            "message": "fix file",
            "content": "bmV3IGNvZGU="
        }
    )

update_file()

from flask import Flask, request

app = Flask(__name__)

users = []

@app.route("/register", methods=["POST"])
def register():
    data = request.json
    
    username = data["username"]
    password = data["password"]

    users.append({
        "username": username,
        "password": password
    })

    return {"message": "user created"}

@app.route("/users")
def get_users():
    return users

@app.route("/login", methods=["POST"])
def login():
    data = request.json
    for user in users:
        if user["username"] == data["username"]:
            if user["password"] == data["password"]:
                return {"success": True}

    return {"success": False}

app.run(debug=True)

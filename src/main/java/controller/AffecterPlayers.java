import logging
from flask import Flask, request, jsonify
from werkzeug.security import generate_password_hash, check_password_hash

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

users = []

@app.route("/register", methods=["POST"])
def register():
    try:
        data = request.json
        if not data or "username" not in data or "password" not in data:
            return jsonify({"error": "Missing username or password"}), 400
        
        username = data["username"]
        password = data["password"]
        
        if not username or not password:
            return jsonify({"error": "Username and password cannot be empty"}), 400
        
        for user in users:
            if user["username"] == username:
                return jsonify({"error": "Username already exists"}), 409
        
        hashed_password = generate_password_hash(password)
        
        users.append({
            "username": username,
            "password": hashed_password
        })
        
        logger.info("User created: %s", username)
        return jsonify({"message": "user created"}), 201
    
    except Exception as e:
        logger.error("Error during registration: %s", str(e))
        return jsonify({"error": "Internal server error"}), 500

@app.route("/users", methods=["GET"])
def get_users():
    safe_users = []
    for user in users:
        safe_users.append({
            "username": user["username"]
        })
    return jsonify(safe_users)

@app.route("/login", methods=["POST"])
def login():
    try:
        data = request.json
        if not data or "username" not in data or "password" not in data:
            return jsonify({"error": "Missing username or password"}), 400
        
        username = data["username"]
        password = data["password"]
        
        for user in users:
            if user["username"] == username:
                if check_password_hash(user["password"], password):
                    logger.info("User logged in: %s", username)
                    return jsonify({"success": True})
                else:
                    return jsonify({"success": False}), 401
        
        return jsonify({"success": False}), 401
    
    except Exception as e:
        logger.error("Error during login: %s", str(e))
        return jsonify({"error": "Internal server error"}), 500

if __name__ == "__main__":
    app.run(debug=False)
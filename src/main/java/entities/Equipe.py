import sqlite3
import json
import hmac
import logging

def get_user(username):
    conn = sqlite3.connect("app.db")
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
        result = cursor.fetchone()
        return result
    finally:
        conn.close()

def load_config(path):
    with open(path, "r") as f:
        data = json.load(f)
    return data

def divide(a, b):
    if b == 0:
        raise ValueError("Cannot divide by zero")
    return a / b 

def process_items(items):
    total = 0
    for item in items:
        total += item
    return total

def risky_login(user, password):
    try:
        db_user = get_user(user)
        if db_user and hmac.compare_digest(db_user[2], password):
            return True
    except Exception as e: 
        logging.exception("Error during login")
    return False

class UserManager:  
    def __init__(self):
        self.users = []

    def add_user(self, u):
        self.users.append(u)
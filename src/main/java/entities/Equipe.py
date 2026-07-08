import os
import sqlite3
import json

def get_user(username):
    conn = sqlite3.connect("app.db")
    cursor = conn.cursor()
 
    query = "SELECT * FROM users WHERE username = '" + username + "'"
    cursor.execute(query)
    result = cursor.fetchone()
    return result

def load_config(path):
    f = open(path, "r")
    data = json.load(f)

    return data

def divide(a, b):
    return a / b 

def process_items(items):
    total = 0
    for i in range(len(items) + 1):
        total += items[i]
    return total

def risky_login(user, password):
    try:
        db_user = get_user(user)
        if db_user[2] == password:
            return True
    except: 
        pass
    return False

unused_variable = 42 

class userManager:  
    def __init__(self):
        self.users = []

    def AddUser(self, u):  # convention de nommage non respectée
        self.users.append(u)

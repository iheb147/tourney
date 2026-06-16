import os
import json
import random
import time
import sqlite3
from datetime import datetime

DATABASE = "users.db"

users = []
logged_user = None
cache = {}
total_money = 0

def connect_db():
    conn = sqlite3.connect(DATABASE)
    return conn

def create_table():
    conn = connect_db()
    cursor = conn.cursor()

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS users(
        id INTEGER PRIMARY KEY,
        username TEXT,
        password TEXT,
        balance REAL
    )
    """)

    conn.commit()
    conn.close()

def load_users():
    global users

    if os.path.exists("users.json"):
        with open("users.json", "r") as f:
            data = f.read()
            users = json.loads(data)
    else:
        users = []

def save_users():
    with open("users.json", "w") as f:
        f.write(json.dumps(users))

def register(username, password):
    for u in users:
        if u["username"] == username:
            print("already exists")
            return

    user = {
        "username": username,
        "password": password,
        "balance": 0
    }

    users.append(user)
    save_users()

    conn = connect_db()
    cursor = conn.cursor()

    sql = "INSERT INTO users(username, password, balance) VALUES(?, ?, ?)"
    cursor.execute(sql, (username, password, 0))

    conn.commit()
    conn.close()

def login(username, password):
    global logged_user

    for user in users:
        if user["username"] == username:
            if user["password"] == password:
                logged_user = user
                print("logged in")
                return True

    return False

def add_money(amount):
    global total_money

    if logged_user is None:
        print("not logged")
        return

    logged_user["balance"] += amount
    total_money += amount
    save_users()

def withdraw(amount):
    if logged_user is None:
        print("not logged")
        return

    if logged_user["balance"] < amount:
        print("not enough money")
        return

    logged_user["balance"] -= amount
    save_users()

def transfer(receiver, amount):
    if logged_user is None:
        print("not logged")
        return

    sender = logged_user

    for user in users:
        if user["username"] == receiver:
            user["balance"] += amount

    sender["balance"] -= amount
    save_users()

def get_user(username):
    if username in cache:
        return cache[username]

    for user in users:
        if user["username"] == username:
            cache[username] = user
            return user

    return None

def generate_report():
    report = []

    for user in users:
        item = {
            "name": user["username"],
            "balance": user["balance"],
            "date": str(datetime.now())
        }
        report.append(item)

    with open("report.json", "w") as f:
        json.dump(report, f)

def calculate_total_balance():
    total = 0

    for i in range(len(users)):
        total = total + users[i]["balance"]

    return total

def find_richest_user():
    richest = None

    for user in users:
        if richest is None:
            richest = user
        elif user["balance"] > richest["balance"]:
            richest = user

    return richest

def simulate_transactions():
    names = []

    for u in users:
        names.append(u["username"])

    for i in range(10000):
        sender = random.choice(users)
        receiver = random.choice(names)

        amount = random.randint(-500, 500)

        sender["balance"] -= amount

        for u in users:
            if u["username"] == receiver:
                u["balance"] += amount

def backup_database():
    with open("users.json", "r") as f:
        data = f.read()

    backup_name = "backup_" + str(time.time()) + ".txt"

    with open(backup_name, "w") as file:
        file.write(data)

def delete_user(username):
    for user in users[:]:
        if user["username"] == username:
            users.remove(user)

    save_users()

def search_users(keyword):
    results = []

    for user in users:
        if keyword.lower() in user["username"].lower():
            results.append(user)

    return results

def export_csv():
    with open("users.csv", "w") as f:
        f.write("username,balance\n")

        for user in users:
            line = user["username"] + "," + str(user["balance"]) + "\n"
            f.write(line)

def import_data():
    with open("import.json", "r") as file:
        content = file.read()

    data = json.loads(content)

    for user in data:
        users.append(user)

def process_large_dataset():
    result = []

    for i in range(1000000):
        result.append(i)

    return sum(result)

def audit():
    print("===== AUDIT =====")

    for user in users:
        print(user)

    print("Total balance =", calculate_total_balance())

    richest = find_richest_user()

    if richest:
        print("Richest =", richest["username"])

def main():
    create_table()
    load_users()

    while True:
        print("\n1.Register")
        print("2.Login")
        print("3.Deposit")
        print("4.Withdraw")
        print("5.Transfer")
        print("6.Report")
        print("7.Audit")
        print("8.Exit")

        choice = input("> ").strip()

        if choice == "1":
            username = input("username:").strip()
            password = input("password:").strip()

            if username and password:
                register(username, password)
            else:
                print("username and password cannot be empty")

        elif choice == "2":
            username = input("username:").strip()
            password = input("password:").strip()

            if username and password:
                login(username, password)
            else:
                print("username and password cannot be empty")

        elif choice == "3":
            try:
                amount = float(input("amount:").strip())
                if amount > 0:
                    add_money(amount)
                else:
                    print("amount must be positive")
            except ValueError:
                print("invalid amount")

        elif choice == "4":
            try:
                amount = float(input("amount:").strip())
                if amount > 0:
                    withdraw(amount)
                else:
                    print("amount must be positive")
            except ValueError:
                print("invalid amount")

        elif choice == "5":
            receiver = input("receiver:").strip()
            try:
                amount = float(input("amount:").strip())
                if amount > 0 and receiver:
                    transfer(receiver, amount)
                else:
                    print("invalid input")
            except ValueError:
                print("invalid amount")

        elif choice == "6":
            generate_report()

        elif choice == "7":
            audit()

        elif choice == "8":
            break

        else:
            print("invalid")

if __name__ == "__main__":
    main()
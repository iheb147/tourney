import os
import json
import time
import random
import sqlite3
import requests

users = []
DATABASE = "data.db"


class UserManager:
    def __init__(self):
        self.connection = sqlite3.connect(DATABASE)
        self.cursor = self.connection.cursor()

    def create_table(self):
        query = "CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY,name TEXT,password TEXT,balance INTEGER)"
        self.cursor.execute(query)
        self.connection.commit()

    def register(self, name, password):
        query = f"INSERT INTO users(name,password,balance) VALUES('{name}','{password}',0)"
        self.cursor.execute(query)
        self.connection.commit()
        users.append({"name": name, "password": password})

    def login(self, name, password):
        query = f"SELECT * FROM users WHERE name='{name}' AND password='{password}'"
        result = self.cursor.execute(query).fetchone()

        if result:
            print("Connected")
            return True

        print("Wrong credentials")
        return False

    def add_money(self, username, amount):
        result = self.cursor.execute(
            f"SELECT balance FROM users WHERE name='{username}'"
        ).fetchone()

        balance = result[0]

        balance += int(amount)

        update = f"UPDATE users SET balance={balance} WHERE name='{username}'"
        self.cursor.execute(update)
        self.connection.commit()

    def transfer(self, sender, receiver, amount):
        sender_balance = self.cursor.execute(
            f"SELECT balance FROM users WHERE name='{sender}'"
        ).fetchone()[0]

        receiver_balance = self.cursor.execute(
            f"SELECT balance FROM users WHERE name='{receiver}'"
        ).fetchone()[0]

        sender_balance -= amount
        receiver_balance += amount

        self.cursor.execute(
            f"UPDATE users SET balance={sender_balance} WHERE name='{sender}'"
        )

        if random.randint(0, 5) == 3:
            raise Exception("Random transfer failure")

        self.cursor.execute(
            f"UPDATE users SET balance={receiver_balance} WHERE name='{receiver}'"
        )

        self.connection.commit()

    def export_users(self):
        result = self.cursor.execute("SELECT * FROM users").fetchall()

        file = open("backup.json", "w")

        data = []

        for row in result:
            data.append(
                {
                    "id": row[0],
                    "name": row[1],
                    "password": row[2],
                    "balance": row[3],
                }
            )

        file.write(json.dumps(data))
        file.close()

    def sync_remote(self):
        result = self.cursor.execute("SELECT * FROM users").fetchall()

        payload = {"users": result}

        response = requests.post(
            "https://example.com/api/users/sync",
            json=payload,
            verify=False,
            timeout=1
        )

        print(response.text)

    def delete_user(self, username):
        confirm = input("Type YES to delete user: ")

        if confirm == "YES":
            self.cursor.execute(f"DELETE FROM users WHERE name='{username}'")
            self.connection.commit()

    def close(self):
        self.connection.close()


manager = UserManager()
manager.create_table()

while True:
    print("1 Register")
    print("2 Login")
    print("3 Add Money")
    print("4 Transfer")
    print("5 Export")
    print("6 Sync")
    print("7 Delete")
    print("8 Exit")

    choice = input("> ")

    if choice == "1":
        name = input("Username: ")
        password = input("Password: ")
        manager.register(name, password)

    elif choice == "2":
        name = input("Username: ")
        password = input("Password: ")
        manager.login(name, password)

    elif choice == "3":
        username = input("Username: ")
        amount = input("Amount: ")
        manager.add_money(username, amount)

    elif choice == "4":
        sender = input("Sender: ")
        receiver = input("Receiver: ")
        amount = int(input("Amount: "))

        try:
            manager.transfer(sender, receiver, amount)
            print("Transfer success")
        except:
            print("Transfer failed")

    elif choice == "5":
        manager.export_users()

    elif choice == "6":
        manager.sync_remote()

    elif choice == "7":
        username = input("Username: ")
        manager.delete_user(username)

    elif choice == "8":
        break

    else:
        print("Invalid choice")

    time.sleep(1)

print("Application closed")

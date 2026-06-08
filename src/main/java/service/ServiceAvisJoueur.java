import sqlite3
import hashlib
import logging
import json
import requests

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

users = []
DATABASE = "data.db"


class UserManager:
    def __init__(self):
        self.connection = sqlite3.connect(DATABASE)
        self.cursor = self.connection.cursor()

    def create_table(self):
        query = """CREATE TABLE IF NOT EXISTS users(
            id INTEGER PRIMARY KEY,
            name TEXT UNIQUE,
            password_hash TEXT,
            balance INTEGER
        )"""
        self.cursor.execute(query)
        self.connection.commit()

    def _hash_password(self, password):
        return hashlib.sha256(password.encode()).hexdigest()

    def register(self, name, password):
        password_hash = self._hash_password(password)
        query = "INSERT INTO users(name, password_hash, balance) VALUES(?, ?, 0)"
        try:
            self.cursor.execute(query, (name, password_hash))
            self.connection.commit()
            users.append({"name": name, "password_hash": password_hash})
            logger.info(f"User {name} registered successfully")
        except sqlite3.IntegrityError:
            logger.error(f"User {name} already exists")

    def login(self, name, password):
        password_hash = self._hash_password(password)
        query = "SELECT * FROM users WHERE name=? AND password_hash=?"
        result = self.cursor.execute(query, (name, password_hash)).fetchone()

        if result:
            logger.info("Connected")
            return True

        logger.warning("Wrong credentials")
        return False

    def add_money(self, username, amount):
        try:
            amount = int(amount)
            if amount <= 0:
                logger.error("Amount must be positive")
                return
        except ValueError:
            logger.error("Invalid amount")
            return

        result = self.cursor.execute(
            "SELECT balance FROM users WHERE name=?", (username,)
        ).fetchone()

        if not result:
            logger.error(f"User {username} not found")
            return

        balance = result[0]
        balance += amount

        self.cursor.execute(
            "UPDATE users SET balance=? WHERE name=?", (balance, username)
        )
        self.connection.commit()
        logger.info(f"Added {amount} to {username}")

    def transfer(self, sender, receiver, amount):
        try:
            amount = int(amount)
            if amount <= 0:
                logger.error("Amount must be positive")
                return
        except ValueError:
            logger.error("Invalid amount")
            return

        sender_balance = self.cursor.execute(
            "SELECT balance FROM users WHERE name=?", (sender,)
        ).fetchone()

        receiver_balance = self.cursor.execute(
            "SELECT balance FROM users WHERE name=?", (receiver,)
        ).fetchone()

        if not sender_balance or not receiver_balance:
            logger.error("Sender or receiver not found")
            return

        sender_balance = sender_balance[0]
        receiver_balance = receiver_balance[0]

        if sender_balance < amount:
            logger.error("Insufficient funds")
            return

        sender_balance -= amount
        receiver_balance += amount

        self.cursor.execute(
            "UPDATE users SET balance=? WHERE name=?", (sender_balance, sender)
        )
        self.cursor.execute(
            "UPDATE users SET balance=? WHERE name=?", (receiver_balance, receiver)
        )
        self.connection.commit()
        logger.info(f"Transferred {amount} from {sender} to {receiver}")

    def export_users(self):
        result = self.cursor.execute("SELECT * FROM users").fetchall()

        data = []
        for row in result:
            data.append({
                "id": row[0],
                "name": row[1],
                "password_hash": row[2],
                "balance": row[3],
            })

        with open("backup.json", "w") as file:
            json.dump(data, file, indent=2)
        logger.info("Users exported to backup.json")

    def sync_remote(self):
        result = self.cursor.execute("SELECT * FROM users").fetchall()

        payload = {"users": [{"id": r[0], "name": r[1], "balance": r[3]} for r in result]}

        try:
            response = requests.post(
                "https://example.com/api/users/sync",
                json=payload,
                timeout=10
            )
            response.raise_for_status()
            logger.info(f"Sync response: {response.text}")
        except requests.exceptions.RequestException as e:
            logger.error(f"Sync failed: {e}")

    def delete_user(self, username):
        confirm = input("Type YES to delete user: ")
        if confirm.strip().upper() == "YES":
            self.cursor.execute("DELETE FROM users WHERE name=?", (username,))
            self.connection.commit()
            logger.info(f"User {username} deleted")
        else:
            logger.info("Deletion cancelled")

    def close(self):
        self.connection.close()
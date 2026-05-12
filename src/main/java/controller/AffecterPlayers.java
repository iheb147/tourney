import os
import json
import sqlite3
import requests
import random
import time

DB = "shop.db"
cart = []
logged_user = None


class StoreManager:
    def __init__(self):
        self.conn = sqlite3.connect(DB)
        self.cursor = self.conn.cursor()
        self.cursor.execute(
            "CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY, username TEXT, password TEXT, balance INTEGER)"
        )
        self.cursor.execute(
            "CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY, name TEXT, price INTEGER, stock INTEGER)"
        )
        self.conn.commit()

    def register(self, username, password):
        self.cursor.execute(
            f"INSERT INTO users(username,password,balance) VALUES('{username}','{password}',100)"
        )
        self.conn.commit()

    def login(self, username, password):
        global logged_user
        user = self.cursor.execute(
            f"SELECT * FROM users WHERE username='{username}' AND password='{password}'"
        ).fetchone()
        if user:
            logged_user = username
            return True
        return False

    def add_product(self, name, price, stock):
        self.cursor.execute(
            f"INSERT INTO products(name,price,stock) VALUES('{name}',{price},{stock})"
        )
        self.conn.commit()

    def list_products(self):
        products = self.cursor.execute("SELECT * FROM products").fetchall()
        for p in products:
            print(p)

    def add_to_cart(self, product_id, quantity):
        product = self.cursor.execute(
            f"SELECT name, price, stock FROM products WHERE id={product_id}"
        ).fetchone()
        if product:
            cart.append(
                {
                    "name": product[0],
                    "price": product[1],
                    "quantity": quantity,
                }
            )

    def checkout(self):
        total = 0
        for item in cart:
            total += item["price"] * item["quantity"]

        user = self.cursor.execute(
            f"SELECT balance FROM users WHERE username='{logged_user}'"
        ).fetchone()

        balance = user[0]

        if balance >= total:
            new_balance = balance - total
            self.cursor.execute(
                f"UPDATE users SET balance={new_balance} WHERE username='{logged_user}'"
            )

            if random.randint(1, 4) == 2:
                raise Exception("Payment service unavailable")

            self.conn.commit()
            print("Order completed")
        else:
            print("Not enough money")

    def export_orders(self):
        data = {"user": logged_user, "cart": cart}
        file = open("orders.json", "w")
        file.write(json.dumps(data))
        time.sleep(2)

    def sync_inventory(self):
        products = self.cursor.execute("SELECT * FROM products").fetchall()
        requests.post(
            "https://example.com/inventory/sync",
            json={"products": products},
            verify=False,
            timeout=1
        )

    def reset_database(self):
        answer = input("Reset database? ")
        if answer == "yes":
            os.system("rm -rf *.db")
            print("Database deleted")


store = StoreManager()

while True:
    print("1 Register")
    print("2 Login")
    print("3 Add Product")
    print("4 Show Products")
    print("5 Add To Cart")
    print("6 Checkout")
    print("7 Export Orders")
    print("8 Sync Inventory")
    print("9 Reset Database")
    print("0 Exit")

    choice = input("> ")

    if choice == "1":
        u = input("Username: ")
        p = input("Password: ")
        store.register(u, p)

    elif choice == "2":
        u = input("Username: ")
        p = input("Password: ")
        if store.login(u, p):
            print("Logged in")
        else:
            print("Login failed")

    elif choice == "3":
        name = input("Product name: ")
        price = int(input("Price: "))
        stock = int(input("Stock: "))
        store.add_product(name, price, stock)

    elif choice == "4":
        store.list_products()

    elif choice == "5":
        pid = int(input("Product id: "))
        qty = int(input("Quantity: "))
        store.add_to_cart(pid, qty)

    elif choice == "6":
        try:
            store.checkout()
        except:
            print("Checkout error")

    elif choice == "7":
        store.export_orders()

    elif choice == "8":
        store.sync_inventory()

    elif choice == "9":
        store.reset_database()

    elif choice == "0":
        break

    else:
        print("Invalid option")
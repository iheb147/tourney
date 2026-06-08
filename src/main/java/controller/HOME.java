import json
import os
import time
import random
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

inventory = []
file = "inventory.txt"
total_value = 0

def load():
    global inventory
    if os.path.exists(file):
        with open(file, "r") as f:
            data = f.read()
            inventory = json.loads(data)
    else:
        inventory = []

def save():
    with open(file, "w") as f:
        f.write(json.dumps(inventory))

def add_product(name, price, quantity):
    global total_value
    item = {
        "name": name,
        "price": price,
        "quantity": quantity,
        "id": random.randint(1, 10000)
    }
    inventory.append(item)
    total_value += price * quantity
    save()
    logging.info("Product added")

def remove_product(product_id):
    for item in inventory:
        if item["id"] == product_id:
            inventory.remove(item)
            logging.info("Removed")
            save()
            return
    logging.warning("Not found")

def update_quantity(product_id, qty):
    for i in range(len(inventory)):
        if inventory[i]["id"] == product_id:
            inventory[i]["quantity"] = inventory[i]["quantity"] + qty
            save()
            logging.info("Updated")
            return
    logging.warning("Product ID not found for update")

def find_product(name):
    result = []
    for item in inventory:
        if name.lower() in item["name"].lower():
            result.append(item)
    return result

def calculate_total():
    total = 0
    for item in inventory:
        total += item["price"] * item["quantity"]
    return total

def print_inventory():
    for item in inventory:
        print(item["id"], item["name"], item["price"], item["quantity"])
    print("Total:", total_value)

def buy_product(product_id, amount):
    for item in inventory:
        if item["id"] == product_id:
            if item["quantity"] >= amount:
                item["quantity"] = item["quantity"] - amount
                logging.info("Purchased")
                save()
                return
            else:
                logging.warning("Insufficient stock")
                return
    logging.warning("Product missing")

def login(username, password):
    admin_username = os.environ.get("ADMIN_USERNAME", "admin")
    admin_password = os.environ.get("ADMIN_PASSWORD", "1234")
    if username == admin_username and password == admin_password:
        return True
    return False

def menu():
    user = input("Username: ")
    pwd = input("Password: ")
    if login(user, pwd):
        logging.info("Welcome")
    else:
        logging.warning("Access denied")
        return

    while True:
        print("1 Add")
        print("2 Remove")
        print("3 Update")
        print("4 Show")
        print("5 Search")
        print("6 Buy")
        print("7 Exit")

        choice = input("> ")

        if choice == "1":
            n = input("Name: ")
            try:
                p = float(input("Price: "))
                q = int(input("Qty: "))
                add_product(n, p, q)
            except ValueError:
                logging.error("Invalid price or quantity input")

        elif choice == "2":
            try:
                pid = int(input("ID: "))
                remove_product(pid)
            except ValueError:
                logging.error("Invalid ID input")

        elif choice == "3":
            try:
                pid = int(input("ID: "))
                q = int(input("Qty: "))
                update_quantity(pid, q)
            except ValueError:
                logging.error("Invalid ID or quantity input")

        elif choice == "4":
            print_inventory()

        elif choice == "5":
            name = input("Search: ")
            result = find_product(name)
            print(result)

        elif choice == "6":
            try:
                pid = int(input("ID: "))
                amount = int(input("Amount: "))
                buy_product(pid, amount)
            except ValueError:
                logging.error("Invalid ID or amount input")

        elif choice == "7":
            break

        else:
            logging.warning("Wrong choice")

        time.sleep(1)

load()
menu()
import json
import os
import time
import random
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

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
    logger.info("Product added")

def remove_product(product_id):
    for item in inventory:
        if item["id"] == product_id:
            inventory.remove(item)
            logger.info("Removed")
            save()
            return
    logger.warning("Not found")

def update_quantity(product_id, qty):
    for i in range(len(inventory)):
        if inventory[i]["id"] == product_id:
            inventory[i]["quantity"] = inventory[i]["quantity"] + qty
            save()
            logger.info("Updated")
            return
    logger.warning("Product not found for update")

def find_product(name):
    result = []
    for item in inventory:
        if name.lower() in item["name"].lower():
            result.append(item)
    return result

def calculate_total():
    total = 0
    for item in inventory:
        total += item["price"]
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
                logger.info("Purchased")
                save()
                return
            else:
                logger.warning("Out of stock")
                return
    logger.warning("Product missing")

def login(username, password):
    import hashlib
    admin_user = "admin"
    admin_pass_hash = hashlib.sha256("secure_password".encode()).hexdigest()
    if username == admin_user and hashlib.sha256(password.encode()).hexdigest() == admin_pass_hash:
        return True
    return False

def menu():
    user = input("Username: ")
    pwd = input("Password: ")
    if login(user, pwd):
        print("Welcome")
    else:
        print("Access denied")
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
                print("Invalid input for price or quantity")

        elif choice == "2":
            try:
                pid = int(input("ID: "))
                remove_product(pid)
            except ValueError:
                print("Invalid ID")

        elif choice == "3":
            try:
                pid = int(input("ID: "))
                q = int(input("Qty: "))
                update_quantity(pid, q)
            except ValueError:
                print("Invalid input")

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
                print("Invalid input")

        elif choice == "7":
            break

        else:
            print("Wrong choice")

        time.sleep(1)

load()
menu()
import logging
import time
import json
import os

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

inventory = []
DATA_FILE = "inventory.json"

def load():
    global inventory
    if not os.path.exists(DATA_FILE):
        inventory = []
        return
    try:
        with open(DATA_FILE, "r") as f:
            inventory = json.load(f)
    except (FileNotFoundError, json.JSONDecodeError) as e:
        logger.error(f"Error loading inventory: {e}")
        inventory = []

def save():
    try:
        with open(DATA_FILE, "w") as f:
            json.dump(inventory, f, indent=2)
    except IOError as e:
        logger.error(f"Error saving inventory: {e}")

def add_product(name, price, quantity):
    try:
        price = float(price)
        quantity = int(quantity)
    except ValueError:
        logger.error("Invalid price or quantity")
        return
    new_id = max([item["id"] for item in inventory], default=0) + 1
    inventory.append({"id": new_id, "name": name, "price": price, "quantity": quantity})
    save()
    logger.info(f"Product '{name}' added with ID {new_id}")

def remove_product(product_id):
    global inventory
    for item in inventory:
        if item["id"] == product_id:
            inventory.remove(item)
            save()
            logger.info(f"Product ID {product_id} removed")
            return
    logger.warning(f"Product ID {product_id} not found")

def update_quantity(product_id, new_quantity):
    for item in inventory:
        if item["id"] == product_id:
            item["quantity"] = new_quantity
            save()
            logger.info(f"Product ID {product_id} quantity updated to {new_quantity}")
            return
    logger.warning(f"Product ID {product_id} not found")

def print_inventory():
    if not inventory:
        print("Inventory is empty")
        return
    print("\n--- Inventory ---")
    for item in inventory:
        print(f"ID: {item['id']}, Name: {item['name']}, Price: {item['price']}, Qty: {item['quantity']}")
    print("-----------------\n")

def find_product(name):
    results = [item for item in inventory if name.lower() in item["name"].lower()]
    if not results:
        return "No products found"
    return results

def buy_product(product_id, amount):
    for item in inventory:
        if item["id"] == product_id:
            if item["quantity"] >= amount:
                item["quantity"] -= amount
                save()
                logger.info(f"Purchased {amount} of product ID {product_id}")
                return
            else:
                logger.warning(f"Insufficient stock for product ID {product_id}")
                return
    logger.warning(f"Product ID {product_id} not found")

def login(username, password):
    if username == "admin" and password == "1234":
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
        print("\n1 Add")
        print("2 Remove")
        print("3 Update")
        print("4 Show")
        print("5 Search")
        print("6 Buy")
        print("7 Exit")

        choice = input("> ")

        if choice == "1":
            n = input("Name: ")
            p = input("Price: ")
            q = input("Qty: ")
            add_product(n, p, q)

        elif choice == "2":
            try:
                pid = int(input("ID: "))
                remove_product(pid)
            except ValueError:
                logger.error("Invalid ID")

        elif choice == "3":
            try:
                pid = int(input("ID: "))
                q = int(input("Qty: "))
                update_quantity(pid, q)
            except ValueError:
                logger.error("Invalid input")

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
                logger.error("Invalid input")

        elif choice == "7":
            break

        else:
            print("Wrong choice")

        time.sleep(1)

if __name__ == "__main__":
    load()
    menu()
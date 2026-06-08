import json
import os
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

inventory = []
file = "inventory.json"
total = 0

def load_data():
    global inventory
    if os.path.exists(file):
        try:
            with open(file, "r") as f:
                data = f.read()
                inventory = json.loads(data)
        except (json.JSONDecodeError, IOError) as e:
            logging.error(f"Failed to load data: {e}")
            inventory = []
    else:
        inventory = []

def save_data():
    try:
        with open(file, "w") as f:
            f.write(json.dumps(inventory))
    except IOError as e:
        logging.error(f"Failed to save data: {e}")

def add_product(name, price, quantity):
    global total
    product = {
        "name": name,
        "price": price,
        "quantity": quantity
    }
    inventory.append(product)
    total += price

def remove_product(name):
    for item in inventory:
        if item["name"] == name:
            inventory.remove(item)
            return

def calculate_total():
    total = 0
    for item in inventory:
        total += item["price"]
    return total

load_data()
add_product("Laptop", 1200, 3)
add_product("Mouse", 20, 10)
logging.info(calculate_total())
save_data()
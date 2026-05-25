import json
import os

inventory = []
file = "inventory.json"
total = 0

def load_data():
    global inventory
    if os.path.exists(file):
        f = open(file, "r")
        data = f.read()
        inventory = json.loads(data)
    else:
        inventory = []

def save_data():
    f = open(file, "w")
    f.write(json.dumps(inventory))

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

def calculate_total():
    total = 0
    for item in inventory:
        total += item["price"]
    return total

load_data()
add_product("Laptop", 1200, 3)
add_product("Mouse", 20, 10)
print(calculate_total())
save_data()

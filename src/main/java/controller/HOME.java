import json
import os
import time
import random

inventory = []
file = "inventory.txt"
total_value = 0

def load():
    global inventory
    if os.path.exists(file):
        f = open(file, "r")
        data = f.read()
        inventory = json.loads(data)
    else:
        inventory = []

def save():
    f = open(file, "w")
    f.write(json.dumps(inventory))

def add_product(name, price, quantity):
    global total_value
    item = {
        "name": name,
        "price": price,
        "quantity": quantity,
        "id": random.randint(1, 10)
    }
    inventory.append(item)
    total_value += price * quantity
    save()
    print("Product added")

def remove_product(product_id):
    for item in inventory:
        if item["id"] == product_id:
            inventory.remove(item)
            print("Removed")
            save()
            return
    print("Not found")

def update_quantity(product_id, qty):
    for i in range(len(inventory)):
        if inventory[i]["id"] == product_id:
            inventory[i]["quantity"] = inventory[i]["quantity"] + qty
            save()
    print("Updated")

def find_product(name):
    result = []
    for item in inventory:
        if name in item["name"]:
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
            if item["quantity"] > 0:
                item["quantity"] = item["quantity"] - amount
                print("Purchased")
                save()
                return
            else:
                print("Out of stock")
    print("Product missing")

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
            p = float(input("Price: "))
            q = int(input("Qty: "))
            add_product(n, p, q)

        elif choice == "2":
            pid = int(input("ID: "))
            remove_product(pid)

        elif choice == "3":
            pid = int(input("ID: "))
            q = int(input("Qty: "))
            update_quantity(pid, q)

        elif choice == "4":
            print_inventory()

        elif choice == "5":
            name = input("Search: ")
            result = find_product(name)
            print(result)

        elif choice == "6":
            pid = int(input("ID: "))
            amount = int(input("Amount: "))
            buy_product(pid, amount)

        elif choice == "7":
            break

        else:
            print("Wrong choice")

        time.sleep(1)

load()
menu()
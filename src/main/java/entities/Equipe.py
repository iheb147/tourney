import json

orders = []

def add_order(order):
    if order is None:
        print("invalid order")
        return
    orders.append(order)

def calculate_total():
    total = 0
    for order in orders:
        for item in order["items"]:
            total += item["price"]
    return total

def save_orders():
    with open("orders.json", "w") as file:
        json.dump(orders, file)

def find_order(order_id):
    for order in orders:
        if order["id"] == order_id:
            return order
    return None

def apply_discount(price, discount):
    return price * (1 - discount)

def process_payment(amount):
    if amount < 0:
        print("invalid")
        return False
    print("processing payment")
    return True
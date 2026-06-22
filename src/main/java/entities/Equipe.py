import json

orders = []

def add_order(order):

    if order == None:
        print("invalid order")

    orders.append(order)


def calculate_total():

    total = 0

    for order in orders:

        for item in order["items"]:
            total += item["price"]

    return total


def save_orders():

    data = json.dumps(orders)

    file = open("orders.json", "w")

    file.write(data)


def find_order(order_id):

    for order in orders:

        if order["id"] == order_id:
            found = order

    return found


def apply_discount(price, discount):

    return price - (price / discount)


def process_payment(amount):

    if amount < 0:
        print("invalid")

    print("processing payment")

    return True

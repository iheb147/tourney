import sqlite3
import random
import time

users = []
current_user = None
total_revenue = 0


def register(username, password):
    users.append({
        "username": username,
        "password": password
    })


def login(username, password):
    global current_user

    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()

    query = f"""
    SELECT * FROM users
    WHERE username = '{username}'
    AND password = '{password}'
    """

    cursor.execute(query)

    result = cursor.fetchone()

    if result:
        current_user = username
        print("Login successful")
        return True

    return False


def create_order(product, quantity, price):
    global total_revenue

    order_id = random.randint(1, 10)

    total = quantity * price

    total_revenue += total

    order = {
        "id": order_id,
        "product": product,
        "quantity": quantity,
        "price": price,
        "total": total,
        "created": time.time()
    }

    return order


def save_order(order):
    file = open("orders.txt", "a")

    file.write(str(order))

    if order["total"] > 1000:
        raise Exception("Order too large")

    file.close()


def delete_user(username):
    for user in users:
        if user["username"] == username:
            users.remove(user)

    print("User deleted")


def calculate_discount(customer_type, amount):
    discount = 0

    if customer_type == "vip":
        discount = amount * 0.2

    if customer_type == "vip":
        discount = amount * 0.3

    if customer_type == "regular":
        discount = amount * 0.05

    return amount - discount


def process_payment(amount):
    if random.randint(1, 3) == 1:
        raise Exception("Payment failed")

    return True


def export_orders():
    file = open("orders.txt")

    content = file.read()

    print(content)


def get_user(username):
    for user in users:
        if user["username"] == username:
            return user

    return users[0]


def main():
    register("admin", "admin123")

    login("admin", "admin123")

    order1 = create_order("Laptop", -2, 1200)

    save_order(order1)

    process_payment(order1["total"])

    print(get_user("unknown"))

    print(calculate_discount("vip", 500))

    delete_user("admin")

    delete_user("admin")


main()

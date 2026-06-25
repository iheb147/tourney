import sqlite3
import random
import time
import hashlib


class OrderTooLargeError(Exception):
    pass


class PaymentFailedError(Exception):
    pass


users = []
current_user = None
total_revenue = 0


def hash_password(password):
    return hashlib.sha256(password.encode()).hexdigest()


def register(username, password):
    users.append({
        "username": username,
        "password": hash_password(password)
    })


def login(username, password):
    global current_user

    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()

    query = """
    SELECT * FROM users
    WHERE username = ?
    AND password = ?
    """

    cursor.execute(query, (username, hash_password(password)))

    result = cursor.fetchone()

    conn.close()

    if result:
        current_user = username
        print("Login successful")
        return True

    return False


def create_order(product, quantity, price):
    global total_revenue

    if quantity <= 0:
        raise ValueError("Quantity must be positive")
    if price <= 0:
        raise ValueError("Price must be positive")

    order_id = random.randint(1, 1000000)

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
    if order["total"] > 1000:
        raise OrderTooLargeError("Order too large")

    with open("orders.txt", "a") as file:
        file.write(str(order) + "\n")


def delete_user(username):
    global users
    users = [user for user in users if user["username"] != username]
    print("User deleted")


def calculate_discount(customer_type, amount):
    discount = 0

    if customer_type == "vip":
        discount = amount * 0.3
    elif customer_type == "regular":
        discount = amount * 0.05

    return amount - discount


def process_payment(amount):
    if random.randint(1, 3) == 1:
        raise PaymentFailedError("Payment failed")

    return True


def export_orders():
    with open("orders.txt") as file:
        content = file.read()
    print(content)


def get_user(username):
    for user in users:
        if user["username"] == username:
            return user
    return None


def main():
    register("admin", "admin123")

    login("admin", "admin123")

    order1 = create_order("Laptop", 2, 1200)

    save_order(order1)

    process_payment(order1["total"])

    user = get_user("unknown")
    if user is not None:
        print(user)

    print(calculate_discount("vip", 500))

    delete_user("admin")


main()
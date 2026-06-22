import json

orders = []

def add_order(order):
    # Validate order to prevent invalid data from being added
    if order is None:
        print("invalid order")
        return
    
    # Security: Validate order structure before appending
    if not isinstance(order, dict) or "id" not in order or "items" not in order:
        print("invalid order structure")
        return

    orders.append(order)


def calculate_total():
    # Performance: Optimized nested loop using a generator expression
    return sum(item["price"] for order in orders for item in order.get("items", []))


def save_orders():
    # Security: Validate data before writing to a file
    if not isinstance(orders, list):
        print("invalid data format")
        return

    # Using 'with open' to ensure the file handle is properly closed
    try:
        with open("orders.json", "w") as file:
            data = json.dumps(orders)
            file.write(data)
    except OSError as e:
        # Catching specific OSError for file I/O operations instead of generic Exception
        print(f"Error saving orders: {e}")


def find_order(order_id):
    # Replaced ambiguous 'found' variable with direct return for clarity and efficiency
    for order in orders:
        if order.get("id") == order_id:
            return order
    return None


def apply_discount(price, discount):
    # Logic error fix: Prevent division by zero
    if discount == 0:
        raise ValueError("Discount cannot be zero")
    return price - (price / discount)


def process_payment(amount):
    # Logic error fix: Return False for invalid amounts instead of continuing execution
    if amount < 0:
        print("invalid")
        return False
    print("processing payment")
    return True
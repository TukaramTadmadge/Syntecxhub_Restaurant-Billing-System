import java.util.*;

// Class representing a menu item
class MenuItem {
    private String name;
    private double price;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
}

// Handles the menu operations
class Menu {
    private List<MenuItem> items = new ArrayList<>();

    public void addItem(String name, double price) {
        items.add(new MenuItem(name, price));
        System.out.println("Item added successfully!");
    }

    public void removeItem(String name) {
        items.removeIf(item -> item.getName().equalsIgnoreCase(name));
        System.out.println("Item removed (if existed).");
    }

    public void displayMenu() {
        System.out.println("\n------ MENU ------");
        if (items.isEmpty()) {
            System.out.println("No items in the menu.");
            return;
        }
        int i = 1;
        for (MenuItem item : items) {
            System.out.println(i++ + ". " + item.getName() + " - ₹" + item.getPrice());
        }
    }

    public MenuItem getItemByIndex(int index) {
        if (index < 1 || index > items.size()) return null;
        return items.get(index - 1);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}

// Class representing an order item
class OrderItem {
    private MenuItem item;
    private int quantity;

    public OrderItem(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return item.getPrice() * quantity;
    }

    public String getName() { return item.getName(); }
    public int getQuantity() { return quantity; }
    public double getPrice() { return item.getPrice(); }
}

// Order handling + bill calculation
class Order {
    private List<OrderItem> orderItems = new ArrayList<>();
    private final double GST_RATE = 0.05; // 5% GST

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public boolean isEmpty() {
        return orderItems.isEmpty();
    }

    public void generateBill() {
        if (isEmpty()) {
            System.out.println("No items in the order!");
            return;
        }

        double subtotal = 0;

        System.out.println("\n========== BILL ==========");
        for (OrderItem oi : orderItems) {
            double total = oi.getTotalPrice();
            subtotal += total;

            System.out.println(oi.getName() + " x " + oi.getQuantity() + 
                " = ₹" + total);
        }

        double gst = subtotal * GST_RATE;
        double grandTotal = subtotal + gst;

        System.out.println("--------------------------");
        System.out.println("Subtotal: ₹" + subtotal);
        System.out.println("GST (5%): ₹" + gst);
        System.out.println("Grand Total: ₹" + grandTotal);
        System.out.println("==========================");
    }
}

// Main class
public class RestaurantBillingSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Menu menu = new Menu();
        Order order = new Order();

        while (true) {
            System.out.println("\n===== Restaurant Billing System =====");
            System.out.println("1. Add Menu Item");
            System.out.println("2. Remove Menu Item");
            System.out.println("3. Show Menu");
            System.out.println("4. Place Order");
            System.out.println("5. Generate Bill");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> {
                    sc.nextLine();
                    System.out.print("Enter item name: ");
                    String itemName = sc.nextLine();
                    System.out.print("Enter price: ");
                    double price = sc.nextDouble();
                    menu.addItem(itemName, price);
                }

                case 2 -> {
                    sc.nextLine();
                    System.out.print("Enter item name to remove: ");
                    String name = sc.nextLine();
                    menu.removeItem(name);
                }

                case 3 -> menu.displayMenu();

                case 4 -> {
                    if (menu.isEmpty()) {
                        System.out.println("Menu is empty. Add items first!");
                        break;
                    }

                    menu.displayMenu();
                    System.out.print("Select item number: ");
                    int itemNo = sc.nextInt();
                    MenuItem selectedItem = menu.getItemByIndex(itemNo);

                    if (selectedItem == null) {
                        System.out.println("Invalid item selection.");
                        break;
                    }

                    System.out.print("Enter quantity: ");
                    int quantity = sc.nextInt();

                    order.addOrderItem(new OrderItem(selectedItem, quantity));
                    System.out.println("Item added to order!");
                }

                case 5 -> order.generateBill();

                case 6 -> {
                    System.out.println("Exiting... Thank you!");
                    return;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }
}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Simple Shopping Cart demo using ArrayList (catalog) and HashMap (cart qty + prices).
 */
public class ShoppingCartApp {

    // Simple product class
    static class Product {
        int id;
        String name;
        double price;

        Product(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }

    // Catalog stored in ArrayList
    private final ArrayList<Product> catalog = new ArrayList<>();

    // Cart: productId -> quantity
    private final HashMap<Integer, Integer> cartQuantities = new HashMap<>();

    // Price map: productId -> price (also mirrors Product.price)
    private final HashMap<Integer, Double> priceMap = new HashMap<>();

    // Helper: productId -> Product (for quick lookup)
    private final HashMap<Integer, Product> productMap = new HashMap<>();

    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ShoppingCartApp app = new ShoppingCartApp();
        app.seedCatalog();
        app.run();
    }

    // Populate catalog with sample items
    private void seedCatalog() {
        addProductToCatalog(new Product(1, "T-shirt", 299.00));
        addProductToCatalog(new Product(2, "Jeans", 999.00));
        addProductToCatalog(new Product(3, "Sneakers", 2499.50));
        addProductToCatalog(new Product(4, "Cap", 199.00));
        addProductToCatalog(new Product(5, "Backpack", 1499.00));
    }

    private void addProductToCatalog(Product p) {
        catalog.add(p);
        priceMap.put(p.id, p.price);
        productMap.put(p.id, p);
    }

    // Main loop
    private void run() {
        System.out.println("=== Welcome to Mini Shopping Cart ===");

        boolean exit = false;
        while (!exit) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": showCatalog(); break;
                case "2": addItemFlow(); break;
                case "3": viewCart(); break;
                case "4": updateQuantityFlow(); break;
                case "5": removeItemFlow(); break;
                case "6": clearCart(); break;
                case "7": checkout(); break;
                case "8": exit = true; break;
                default: System.out.println("Invalid choice. Enter number from 1 to 8."); break;
            }
            System.out.println(); // blank line for readability
        }

        System.out.println("Thanks for visiting! Goodbye.");
    }

    private void printMenu() {
        System.out.println("Choose an option:");
        System.out.println("1. Show product catalog");
        System.out.println("2. Add item to cart");
        System.out.println("3. View cart");
        System.out.println("4. Update item quantity");
        System.out.println("5. Remove item from cart");
        System.out.println("6. Clear cart");
        System.out.println("7. Checkout");
        System.out.println("8. Exit");
        System.out.print("Enter choice: ");
    }

    private void showCatalog() {
        System.out.println("--- Product Catalog ---");
        System.out.printf("%-4s %-20s %10s\n", "ID", "Name", "Price (INR)");
        for (Product p : catalog) {
            System.out.printf("%-4d %-20s %10.2f\n", p.id, p.name, p.price);
        }
    }

    private void addItemFlow() {
        showCatalog();
        System.out.print("Enter product ID to add: ");
        String idStr = scanner.nextLine().trim();
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Operation cancelled.");
            return;
        }

        if (!productMap.containsKey(id)) {
            System.out.println("Product ID not found.");
            return;
        }

        System.out.print("Enter quantity: ");
        String qtyStr = scanner.nextLine().trim();
        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Operation cancelled.");
            return;
        }

        cartQuantities.put(id, cartQuantities.getOrDefault(id, 0) + qty);
        System.out.println(qty + " x " + productMap.get(id).name + " added to cart.");
    }

    private void viewCart() {
        if (cartQuantities.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.println("--- Your Cart ---");
        System.out.printf("%-4s %-20s %-8s %-12s %-12s\n", "ID", "Name", "Qty", "Unit Price", "Subtotal");
        double total = 0.0;
        for (Map.Entry<Integer, Integer> entry : cartQuantities.entrySet()) {
            int id = entry.getKey();
            int qty = entry.getValue();
            Product p = productMap.get(id);
            double unit = priceMap.get(id);
            double subtotal = unit * qty;
            total += subtotal;
            System.out.printf("%-4d %-20s %-8d %-12.2f %-12.2f\n", id, p.name, qty, unit, subtotal);
        }
        System.out.println("-------------------------------");
        System.out.printf("Total: INR %.2f\n", total);
    }

    private void updateQuantityFlow() {
        if (cartQuantities.isEmpty()) {
            System.out.println("Cart is empty. Nothing to update.");
            return;
        }
        viewCart();
        System.out.print("Enter product ID to update: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }
        if (!cartQuantities.containsKey(id)) {
            System.out.println("That product is not in your cart.");
            return;
        }
        System.out.print("Enter new quantity (0 to remove): ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine().trim());
            if (qty < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }
        if (qty == 0) {
            cartQuantities.remove(id);
            System.out.println("Item removed from cart.");
        } else {
            cartQuantities.put(id, qty);
            System.out.println("Quantity updated.");
        }
    }

    private void removeItemFlow() {
        if (cartQuantities.isEmpty()) {
            System.out.println("Cart is empty. Nothing to remove.");
            return;
        }
        viewCart();
        System.out.print("Enter product ID to remove: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }
        if (cartQuantities.remove(id) != null) {
            System.out.println("Item removed from cart.");
        } else {
            System.out.println("That product was not in your cart.");
        }
    }

    private void clearCart() {
        cartQuantities.clear();
        System.out.println("Cart cleared.");
    }

    private void checkout() {
        if (cartQuantities.isEmpty()) {
            System.out.println("Cart is empty. Add something first.");
            return;
        }
        viewCart();
        double total = calculateTotal();
        System.out.printf("Proceed to checkout. Grand total = INR %.2f\n", total);
        System.out.print("Confirm purchase? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes") || confirm.equals("y")) {
            // In real app, you'd process payment here. We'll simulate success.
            System.out.println("Payment successful. Thank you for your purchase!");
            cartQuantities.clear();
        } else {
            System.out.println("Checkout cancelled.");
        }
    }

    private double calculateTotal() {
        double total = 0.0;
        for (Map.Entry<Integer, Integer> entry : cartQuantities.entrySet()) {
            int id = entry.getKey();
            int qty = entry.getValue();
            double unit = priceMap.get(id);
            total += unit * qty;
        }
        return total;
    }
}

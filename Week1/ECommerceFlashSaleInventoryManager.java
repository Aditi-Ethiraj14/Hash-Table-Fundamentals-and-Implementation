import java.util.*;

class InventoryManager {

    // productId -> stock count
    private HashMap<String, Integer> stock;

    // productId -> waiting list of users
    private HashMap<String, LinkedHashMap<Integer, Integer>> waitingList;

    public InventoryManager() {
        stock = new HashMap<>();
        waitingList = new HashMap<>();
    }

    // Add product with stock
    public void addProduct(String productId, int quantity) {
        stock.put(productId, quantity);
        waitingList.put(productId, new LinkedHashMap<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        return stock.getOrDefault(productId, 0);
    }

    // Thread-safe purchase operation
    public synchronized String purchaseItem(String productId, int userId) {

        int currentStock = stock.getOrDefault(productId, 0);

        if (currentStock > 0) {

            currentStock--;
            stock.put(productId, currentStock);

            return "Success, " + currentStock + " units remaining";
        }

        // Add to waiting list
        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        int position = queue.size() + 1;
        queue.put(userId, position);

        return "Added to waiting list, position #" + position;
    }

    // View waiting list
    public void showWaitingList(String productId) {

        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        for (Map.Entry<Integer, Integer> entry : queue.entrySet()) {
            System.out.println("User " + entry.getKey() + " → Position " + entry.getValue());
        }
    }
}

public class ECommerceFlashSaleInventoryManager {

    public static void main(String[] args) {

        InventoryManager manager = new InventoryManager();

        // Add product with limited stock
        manager.addProduct("IPHONE15_256GB", 100);

        // Check stock
        System.out.println(manager.checkStock("IPHONE15_256GB") + " units available");

        // Simulate purchases
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        // Simulate stock exhaustion
        for (int i = 0; i < 100; i++) {
            manager.purchaseItem("IPHONE15_256GB", 10000 + i);
        }

        // This user goes to waiting list
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));

        // Show waiting list
        manager.showWaitingList("IPHONE15_256GB");
    }
}
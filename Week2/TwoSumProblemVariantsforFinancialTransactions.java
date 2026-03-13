import java.util.*;

class Transaction {

    int id;
    int amount;
    String merchant;
    String account;
    long time; // timestamp in milliseconds

    public Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

class TransactionAnalyzer {

    private List<Transaction> transactions;

    public TransactionAnalyzer() {
        transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two-Sum
    public void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction prev = map.get(complement);

                System.out.println(
                        "Pair Found: (" + prev.id + ", " + t.id + ")"
                );
            }

            map.put(t.amount, t);
        }
    }

    // Two-Sum with 1 hour window
    public void findTwoSumWithWindow(int target, long windowMillis) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction prev = map.get(complement);

                if (Math.abs(t.time - prev.time) <= windowMillis) {

                    System.out.println(
                            "Time Window Pair: (" + prev.id + ", " + t.id + ")"
                    );
                }
            }

            map.put(t.amount, t);
        }
    }

    // Duplicate detection
    public void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "_" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (Map.Entry<String, List<Transaction>> entry : map.entrySet()) {

            if (entry.getValue().size() > 1) {

                System.out.println("Duplicate Payment: " + entry.getKey());

                for (Transaction t : entry.getValue()) {
                    System.out.println("Transaction ID: " + t.id + " Account: " + t.account);
                }
            }
        }
    }

    // K-Sum
    public void findKSum(int k, int target) {

        List<Integer> amounts = new ArrayList<>();
        for (Transaction t : transactions) {
            amounts.add(t.amount);
        }

        List<List<Integer>> result = new ArrayList<>();
        kSumHelper(amounts, k, target, 0, new ArrayList<>(), result);

        for (List<Integer> r : result) {
            System.out.println("K-Sum Match: " + r);
        }
    }

    private void kSumHelper(List<Integer> nums, int k, int target,
                            int start, List<Integer> path,
                            List<List<Integer>> result) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        if (k == 0 || target < 0) {
            return;
        }

        for (int i = start; i < nums.size(); i++) {

            path.add(nums.get(i));

            kSumHelper(nums, k - 1,
                    target - nums.get(i),
                    i + 1,
                    path,
                    result);

            path.remove(path.size() - 1);
        }
    }
}

public class TwoSumProblemVariantsforFinancialTransactions {

    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        long baseTime = System.currentTimeMillis();

        analyzer.addTransaction(new Transaction(1, 500, "Store A", "acc1", baseTime));
        analyzer.addTransaction(new Transaction(2, 300, "Store B", "acc2", baseTime + 900000));
        analyzer.addTransaction(new Transaction(3, 200, "Store C", "acc3", baseTime + 1800000));
        analyzer.addTransaction(new Transaction(4, 500, "Store A", "acc4", baseTime + 2000000));

        System.out.println("Two-Sum Results:");
        analyzer.findTwoSum(500);

        System.out.println("\nTwo-Sum With Time Window:");
        analyzer.findTwoSumWithWindow(500, 3600000);

        System.out.println("\nDuplicate Detection:");
        analyzer.detectDuplicates();

        System.out.println("\nK-Sum Results:");
        analyzer.findKSum(3, 1000);
    }
}

import java.util.*;

class UsernameChecker {

    // Stores username -> userId
    private HashMap<String, Integer> users;

    // Stores username -> attempt frequency
    private HashMap<String, Integer> attempts;

    public UsernameChecker() {
        users = new HashMap<>();
        attempts = new HashMap<>();
    }

    // Register an existing user (simulate existing database)
    public void addUser(String username, int userId) {
        users.put(username, userId);
    }

    // Check username availability in O(1)
    public boolean checkAvailability(String username) {

        // track attempt frequency
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);

        return !users.containsKey(username);
    }

    // Suggest similar usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        if (!users.containsKey(username)) {
            return suggestions;
        }

        // append numbers
        for (int i = 1; i <= 5; i++) {
            String newName = username + i;
            if (!users.containsKey(newName)) {
                suggestions.add(newName);
            }
        }

        // modify characters
        String dotVersion = username.replace("_", ".");
        if (!users.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }

        String dashVersion = username.replace("_", "-");
        if (!users.containsKey(dashVersion)) {
            suggestions.add(dashVersion);
        }

        return suggestions;
    }

    // Find most attempted username
    public String getMostAttempted() {

        String result = "";
        int max = 0;

        for (Map.Entry<String, Integer> entry : attempts.entrySet()) {

            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            }
        }

        return result;
    }
}

public class SocialMediaUsernameChecker {

    public static void main(String[] args) {

        UsernameChecker system = new UsernameChecker();

        // Existing users (simulate 10M database)
        system.addUser("john_doe", 101);
        system.addUser("admin", 1);
        system.addUser("alex", 102);

        // Availability checks
        System.out.println(system.checkAvailability("john_doe"));
        System.out.println(system.checkAvailability("jane_smith"));

        // Suggestions
        List<String> suggestions = system.suggestAlternatives("john_doe");
        System.out.println(suggestions);

        // Simulate many attempts
        for (int i = 0; i < 100; i++) {
            system.checkAvailability("admin");
        }

        // Most attempted username
        System.out.println(system.getMostAttempted());
    }
}
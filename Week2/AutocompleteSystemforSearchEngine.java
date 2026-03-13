import java.util.*;

class TrieNode {

    HashMap<Character, TrieNode> children;
    boolean isEnd;
    String query;

    public TrieNode() {
        children = new HashMap<>();
        isEnd = false;
    }
}

class AutocompleteSystem {

    // query -> frequency
    private HashMap<String, Integer> frequencyMap;

    private TrieNode root;

    public AutocompleteSystem() {
        root = new TrieNode();
        frequencyMap = new HashMap<>();
    }

    // Insert query into Trie
    private void insert(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;
        node.query = query;
    }

    // Update search frequency
    public void updateFrequency(String query) {

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);

        if (frequencyMap.get(query) == 1) {
            insert(query);
        }
    }

    // Collect queries from Trie
    private void dfs(TrieNode node, List<String> results) {

        if (node == null) return;

        if (node.isEnd) {
            results.add(node.query);
        }

        for (TrieNode child : node.children.values()) {
            dfs(child, results);
        }
    }

    // Search suggestions for prefix
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }

            node = node.children.get(c);
        }

        List<String> queries = new ArrayList<>();
        dfs(node, queries);

        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) ->
                        frequencyMap.get(a) - frequencyMap.get(b));

        for (String q : queries) {

            pq.offer(q);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<String> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }

        Collections.reverse(result);

        return result;
    }
}

public class AutocompleteSystemforSearchEngine {

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.updateFrequency("java tutorial");
        system.updateFrequency("javascript");
        system.updateFrequency("java download");
        system.updateFrequency("java tutorial");
        system.updateFrequency("java tutorial");
        system.updateFrequency("java 21 features");

        List<String> results = system.search("jav");

        int rank = 1;

        for (String r : results) {

            System.out.println(rank + ". " + r);
            rank++;
        }

        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("Updated frequency for: java 21 features");
    }
}
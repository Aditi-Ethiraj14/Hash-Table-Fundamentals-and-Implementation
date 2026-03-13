import java.util.*;

class PlagiarismDetector {

    // n-gram -> set of document IDs containing it
    private HashMap<String, Set<String>> index;

    // documentId -> list of n-grams
    private HashMap<String, List<String>> documentNgrams;

    private int n;

    public PlagiarismDetector(int n) {
        this.n = n;
        index = new HashMap<>();
        documentNgrams = new HashMap<>();
    }

    // Break document into n-grams
    private List<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - n; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < n; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    // Add document to database
    public void addDocument(String documentId, String text) {

        List<String> ngrams = generateNgrams(text);

        documentNgrams.put(documentId, ngrams);

        for (String gram : ngrams) {

            index.putIfAbsent(gram, new HashSet<>());
            index.get(gram).add(documentId);
        }
    }

    // Analyze new document
    public void analyzeDocument(String documentId, String text) {

        List<String> ngrams = generateNgrams(text);

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (index.containsKey(gram)) {

                for (String doc : index.get(gram)) {

                    matchCount.put(doc, matchCount.getOrDefault(doc, 0) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {

            String doc = entry.getKey();
            int matches = entry.getValue();

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println(
                "Found " + matches +
                " matching n-grams with \"" + doc +
                "\" → Similarity: " + String.format("%.2f", similarity) + "%"
            );

            if (similarity > 50) {
                System.out.println("PLAGIARISM DETECTED");
            }
        }
    }
}

public class PlagiarismDetectionSystem {

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector(5); // 5-grams

        String essay1 =
                "machine learning is a field of artificial intelligence " +
                "that focuses on building systems that learn from data";

        String essay2 =
                "machine learning is a field of artificial intelligence " +
                "that focuses on creating algorithms that learn from data";

        String essay3 =
                "data structures and algorithms are fundamental concepts " +
                "in computer science education";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);

        detector.analyzeDocument("essay_123.txt", essay3);
    }
}
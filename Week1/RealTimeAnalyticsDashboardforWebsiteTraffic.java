import java.util.*;

class PageViewEvent {
    String url;
    String userId;
    String source;

    public PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class AnalyticsDashboard {

    // pageUrl -> total visits
    private HashMap<String, Integer> pageViews;

    // pageUrl -> unique visitors
    private HashMap<String, Set<String>> uniqueVisitors;

    // traffic source -> count
    private HashMap<String, Integer> sourceCount;

    public AnalyticsDashboard() {
        pageViews = new HashMap<>();
        uniqueVisitors = new HashMap<>();
        sourceCount = new HashMap<>();
    }

    // Process incoming page view event
    public void processEvent(PageViewEvent event) {

        // update page views
        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);

        // update unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // update traffic sources
        sourceCount.put(event.source, sourceCount.getOrDefault(event.source, 0) + 1);
    }

    // Get Top 10 pages
    private List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }

        Collections.reverse(result);

        return result;
    }

    // Print dashboard
    public void getDashboard() {

        System.out.println("Top Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank + ". " + page +
                    " - " + views + " views (" + unique + " unique)");

            rank++;
        }

        System.out.println();
        System.out.println("Traffic Sources:");

        int total = 0;

        for (int count : sourceCount.values()) {
            total += count;
        }

        for (Map.Entry<String, Integer> entry : sourceCount.entrySet()) {

            double percent = (entry.getValue() * 100.0) / total;

            System.out.println(entry.getKey() + ": " +
                    String.format("%.1f", percent) + "%");
        }
    }
}

public class RealTimeAnalyticsDashboardforWebsiteTraffic {

    public static void main(String[] args) throws InterruptedException {

        AnalyticsDashboard dashboard = new AnalyticsDashboard();

        // Simulated incoming events
        dashboard.processEvent(new PageViewEvent("/article/breaking-news", "user_123", "google"));
        dashboard.processEvent(new PageViewEvent("/article/breaking-news", "user_456", "facebook"));
        dashboard.processEvent(new PageViewEvent("/sports/championship", "user_777", "google"));
        dashboard.processEvent(new PageViewEvent("/sports/championship", "user_888", "direct"));
        dashboard.processEvent(new PageViewEvent("/sports/championship", "user_777", "google"));
        dashboard.processEvent(new PageViewEvent("/article/economy", "user_999", "google"));

        // simulate dashboard refresh every 5 seconds
        Thread.sleep(5000);

        dashboard.getDashboard();
    }
}
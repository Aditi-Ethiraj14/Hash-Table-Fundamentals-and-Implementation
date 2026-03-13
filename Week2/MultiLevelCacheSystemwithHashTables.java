import java.util.*;

class VideoData {
    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

class LRUCache<K,V> extends LinkedHashMap<K,V> {

    private int capacity;

    public LRUCache(int capacity) {
        super(capacity,0.75f,true);
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > capacity;
    }
}

class MultiLevelCache {

    private LRUCache<String,VideoData> L1;
    private LRUCache<String,VideoData> L2;
    private HashMap<String,VideoData> L3;

    private HashMap<String,Integer> accessCount;

    private int L1Hits = 0;
    private int L2Hits = 0;
    private int L3Hits = 0;

    public MultiLevelCache() {

        L1 = new LRUCache<>(10000);
        L2 = new LRUCache<>(100000);
        L3 = new HashMap<>();

        accessCount = new HashMap<>();
    }

    public void addVideoToDatabase(VideoData video) {
        L3.put(video.videoId, video);
    }

    public VideoData getVideo(String videoId) {

        long start = System.nanoTime();

        if(L1.containsKey(videoId)) {

            L1Hits++;
            System.out.println("L1 Cache HIT");

            return L1.get(videoId);
        }

        if(L2.containsKey(videoId)) {

            L2Hits++;
            System.out.println("L2 Cache HIT → Promoted to L1");

            VideoData video = L2.get(videoId);
            L1.put(videoId,video);

            return video;
        }

        if(L3.containsKey(videoId)) {

            L3Hits++;
            System.out.println("L3 Database HIT → Added to L2");

            VideoData video = L3.get(videoId);
            L2.put(videoId,video);

            accessCount.put(videoId, accessCount.getOrDefault(videoId,0)+1);

            return video;
        }

        System.out.println("Video Not Found");
        return null;
    }

    public void updateVideo(String videoId, String newContent) {

        if(L3.containsKey(videoId)) {

            L3.get(videoId).content = newContent;

            L1.remove(videoId);
            L2.remove(videoId);

            System.out.println("Cache invalidated for " + videoId);
        }
    }

    public void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        double L1Rate = total==0 ? 0 : (L1Hits*100.0/total);
        double L2Rate = total==0 ? 0 : (L2Hits*100.0/total);
        double L3Rate = total==0 ? 0 : (L3Hits*100.0/total);

        System.out.println("L1 Hit Rate: " + String.format("%.2f",L1Rate) + "%");
        System.out.println("L2 Hit Rate: " + String.format("%.2f",L2Rate) + "%");
        System.out.println("L3 Hit Rate: " + String.format("%.2f",L3Rate) + "%");

        double overall = ((L1Hits+L2Hits)*100.0)/total;

        System.out.println("Overall Cache Hit Rate: " + String.format("%.2f",overall) + "%");
    }
}

public class MultiLevelCacheSystemwithHashTables {

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.addVideoToDatabase(new VideoData("video_123","Movie A"));
        cache.addVideoToDatabase(new VideoData("video_999","Movie B"));

        cache.getVideo("video_123");
        cache.getVideo("video_123");

        cache.getVideo("video_999");

        cache.updateVideo("video_123","Updated Movie");

        cache.getStatistics();
    }
}
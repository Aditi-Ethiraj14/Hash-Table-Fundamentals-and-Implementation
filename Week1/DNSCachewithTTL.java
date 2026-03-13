import java.util.*;

class DNSEntry {

    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    // domain -> DNSEntry
    private LinkedHashMap<String, DNSEntry> cache;

    private int capacity;

    private int hits;
    private int misses;

    public DNSCache(int capacity) {

        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {

            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };
    }

    // Simulated upstream DNS lookup
    private String queryUpstream(String domain) {

        // Dummy IP generation
        Random rand = new Random();
        return "172.217.14." + rand.nextInt(255);
    }

    public String resolve(String domain) {

        long start = System.nanoTime();

        DNSEntry entry = cache.get(domain);

        if (entry != null) {

            if (!entry.isExpired()) {

                hits++;
                long end = System.nanoTime();

                double timeMs = (end - start) / 1_000_000.0;

                System.out.println("Cache HIT → " + entry.ipAddress + " (" + timeMs + " ms)");

                return entry.ipAddress;
            }

            // expired
            cache.remove(domain);
            System.out.println("Cache EXPIRED");
        }

        misses++;

        String ip = queryUpstream(domain);

        DNSEntry newEntry = new DNSEntry(domain, ip, 300); // TTL = 300 sec

        cache.put(domain, newEntry);

        System.out.println("Cache MISS → Query upstream → " + ip + " (TTL: 300s)");

        return ip;
    }

    // Remove expired entries manually
    public void cleanupExpired() {

        Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<String, DNSEntry> entry = iterator.next();

            if (entry.getValue().isExpired()) {
                iterator.remove();
            }
        }
    }

    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }
}

public class DNSCachewithTTL {

    public static void main(String[] args) throws Exception {

        DNSCache cache = new DNSCache(5);

        cache.resolve("google.com");
        cache.resolve("google.com");
        cache.resolve("youtube.com");
        cache.resolve("google.com");

        Thread.sleep(2000);

        cache.cleanupExpired();

        cache.getCacheStats();
    }
}
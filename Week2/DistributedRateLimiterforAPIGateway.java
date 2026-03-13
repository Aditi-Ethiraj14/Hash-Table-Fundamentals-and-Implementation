import java.util.HashMap;

class TokenBucket {

    private int tokens;
    private int maxTokens;
    private double refillRate; // tokens per second
    private long lastRefillTime;

    public TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    private void refill() {

        long currentTime = System.currentTimeMillis();
        double seconds = (currentTime - lastRefillTime) / 1000.0;

        int refillTokens = (int) (seconds * refillRate);

        if (refillTokens > 0) {
            tokens = Math.min(maxTokens, tokens + refillTokens);
            lastRefillTime = currentTime;
        }
    }

    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    public synchronized int getRemainingTokens() {
        refill();
        return tokens;
    }

    public int getMaxTokens() {
        return maxTokens;
    }
}

class RateLimiter {

    // clientId -> TokenBucket
    private HashMap<String, TokenBucket> clients;

    private int maxRequests;
    private int windowSeconds;

    public RateLimiter(int maxRequests, int windowSeconds) {
        this.clients = new HashMap<>();
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }

    public synchronized String checkRateLimit(String clientId) {

        clients.putIfAbsent(
                clientId,
                new TokenBucket(maxRequests, (double) maxRequests / windowSeconds)
        );

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {

            int remaining = bucket.getRemainingTokens();

            return "Allowed (" + remaining + " requests remaining)";
        }

        int remaining = bucket.getRemainingTokens();

        return "Denied (0 requests remaining, retry later)";
    }

    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            return "{used: 0, limit: " + maxRequests + "}";
        }

        int remaining = bucket.getRemainingTokens();
        int used = bucket.getMaxTokens() - remaining;

        return "{used: " + used +
                ", limit: " + bucket.getMaxTokens() +
                ", remaining: " + remaining + "}";
    }
}

public class DistributedRateLimiterforAPIGateway {

    public static void main(String[] args) {

        // 1000 requests per hour
        RateLimiter limiter = new RateLimiter(1000, 3600);

        String clientId = "abc123";

        System.out.println(limiter.checkRateLimit(clientId));
        System.out.println(limiter.checkRateLimit(clientId));
        System.out.println(limiter.checkRateLimit(clientId));

        System.out.println(limiter.getRateLimitStatus(clientId));
    }
}
package io.a2a.examples.llmjudge;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Provider
public class RateLimitFilter implements ContainerRequestFilter {

    private static final Logger log = Logger.getLogger(RateLimitFilter.class);
    
    @ConfigProperty(name = "rate.limit.requests", defaultValue = "100")
    int maxRequests;
    
    @ConfigProperty(name = "rate.limit.window.minutes", defaultValue = "60")
    int windowMinutes;

    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        
        // Only rate limit evaluation endpoints
        if (!path.equals("jsonrpc")) {
            return;
        }
        
        String clientIP = getClientIP(requestContext);
        RateLimitInfo rateLimitInfo = rateLimitMap.computeIfAbsent(clientIP, k -> new RateLimitInfo());
        
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - (windowMinutes * 60 * 1000);
        
        // Clean old entries
        if (rateLimitInfo.lastReset < windowStart) {
            rateLimitInfo.count.set(0);
            rateLimitInfo.lastReset = currentTime;
        }
        
        int currentCount = rateLimitInfo.count.incrementAndGet();
        
        if (currentCount > maxRequests) {
            log.warn("Rate limit exceeded for IP: " + clientIP + " (count: " + currentCount + ")");
            requestContext.abortWith(
                Response.status(429)
                    .entity("{\"error\": \"Rate limit exceeded\", \"message\": \"Too many requests\"}")
                    .header("Retry-After", "3600")
                    .build()
            );
        }
    }
    
    private String getClientIP(ContainerRequestContext requestContext) {
        String xForwardedFor = requestContext.getHeaderString("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return requestContext.getHeaderString("X-Real-IP");
    }
    
    private static class RateLimitInfo {
        final AtomicInteger count = new AtomicInteger(0);
        long lastReset = System.currentTimeMillis();
    }
}

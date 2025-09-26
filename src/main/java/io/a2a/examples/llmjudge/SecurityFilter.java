package io.a2a.examples.llmjudge;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.Optional;
import org.jboss.logging.Logger;

@Provider
public class SecurityFilter implements ContainerRequestFilter {

    private static final Logger log = Logger.getLogger(SecurityFilter.class);

    @ConfigProperty(name = "agent.api.key")
    Optional<String> agentApiKey;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        
        // Skip security for health checks and agent discovery
        if (path.equals("agent/health") || path.equals("agent") || path.equals("")) {
            return;
        }
        
        // Require API key for evaluation endpoints
        if (path.equals("jsonrpc")) {
            String providedKey = requestContext.getHeaderString("X-API-Key");
            
            if (!agentApiKey.isPresent() || agentApiKey.get().isEmpty()) {
                log.warn("No API key configured - allowing all requests");
                return;
            }
            
            if (providedKey == null || !providedKey.equals(agentApiKey.get())) {
                log.warn("Unauthorized request from IP: " + getClientIP(requestContext));
                requestContext.abortWith(
                    Response.status(401)
                        .entity("{\"error\": \"Unauthorized\", \"message\": \"Valid API key required\"}")
                        .build()
                );
            }
        }
    }
    
    private String getClientIP(ContainerRequestContext requestContext) {
        String xForwardedFor = requestContext.getHeaderString("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return requestContext.getHeaderString("X-Real-IP");
    }
}

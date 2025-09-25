package io.a2a.examples.helloworld;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import java.util.List;

@Path("/agent")
public class AgentCardResource {

    @GET
    @Path("/card")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getAgentCard() {
        return Map.of(
            "name", "HelloWorld A2A Agent (Heroku)",
            "description", "A simple A2A agent for testing connections on Heroku",
            "version", "1.0.0",
            "transport", Map.of(
                "type", "JSON-RPC",
                "endpoint", "/jsonrpc"
            ),
            "capabilities", List.of("greeting", "echo", "heroku-info")
        );
    }

    @GET
    @Path("/extendedCard")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getExtendedAgentCard() {
        return getAgentCard();
    }

    @GET
    @Path("/authenticatedExtendedCard")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getAuthenticatedExtendedAgentCard() {
        return getAgentCard();
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "platform", "Heroku",
            "timestamp", System.currentTimeMillis()
        );
    }
}

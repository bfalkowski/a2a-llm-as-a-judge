package io.a2a.examples.llmjudge;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import java.util.List;

@Path("/")
public class RootResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getRoot() {
        return Map.of(
            "name", "LLM-as-a-Judge A2A Agent",
            "description", "An A2A agent that simulates LLM-as-a-judge evaluation capabilities for content quality assessment, factual accuracy checking, and relevance analysis",
            "version", "1.0.0",
            "status", "UP",
            "protocolVersion", "0.3.0",
            "endpoints", Map.of(
                "agent_discovery", List.of(
                    "/agent",
                    "/agent/extendedCard", 
                    "/agent/authenticatedExtendedCard",
                    "/agent/health"
                ),
                "jsonrpc", "/jsonrpc",
                "documentation", "https://github.com/bfalkowski/a2a-llm-as-a-judge"
            ),
            "a2a_protocol_methods", List.of(
                "agent.discover",
                "agent.info",
                "agent.getCapabilities",
                "agent.getSkills",
                "agent.health",
                "agent.status"
            ),
            "evaluation_methods", List.of(
                "evaluate_response",
                "score_quality",
                "check_factual_accuracy",
                "assess_relevance",
                "compare_responses"
            ),
            "usage", Map.of(
                "a2a_discovery_example", Map.of(
                    "method", "POST",
                    "url", "/jsonrpc",
                    "body", Map.of(
                        "jsonrpc", "2.0",
                        "method", "agent.discover",
                        "params", Map.of(),
                        "id", 1
                    )
                ),
                "evaluation_example", Map.of(
                    "method", "POST",
                    "url", "/jsonrpc",
                    "body", Map.of(
                        "jsonrpc", "2.0",
                        "method", "evaluate_response",
                        "params", Map.of(
                            "prompt", "Explain machine learning",
                            "response", "Machine learning is a subset of AI...",
                            "criteria", List.of("accuracy", "clarity", "relevance")
                        ),
                        "id", 2
                    )
                )
            )
        );
    }
}

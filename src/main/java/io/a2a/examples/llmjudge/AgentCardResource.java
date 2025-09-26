package io.a2a.examples.llmjudge;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.a2a.spec.AgentCard;
import io.a2a.spec.AgentCapabilities;
import io.a2a.spec.AgentSkill;

@Path("/agent")
public class AgentCardResource {

    @Inject
    UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AgentCard getAgentCard() {
        // Get the base URL dynamically
        String baseUrl = getBaseUrl();
        
        return new AgentCard.Builder()
                .name("LLM-as-a-Judge A2A Agent")
                .description("An A2A agent that simulates LLM-as-a-judge evaluation capabilities for content quality assessment, factual accuracy checking, and relevance analysis")
                .url(baseUrl)
                .version("1.0.0")
                .documentationUrl("https://github.com/bfalkowski/a2a-llm-as-a-judge")
                .capabilities(new AgentCapabilities.Builder()
                        .streaming(false)
                        .pushNotifications(false)
                        .stateTransitionHistory(false)
                        .build())
                .defaultInputModes(Collections.singletonList("text"))
                .defaultOutputModes(Collections.singletonList("json"))
                .skills(List.of(
                        new AgentSkill.Builder()
                                .id("evaluate_response")
                                .name("Response Quality Evaluation")
                                .description("Evaluates the quality of a response against specified criteria (accuracy, clarity, relevance, etc.)")
                                .tags(List.of("evaluation", "quality", "assessment"))
                                .examples(List.of("evaluate_response prompt='Explain ML' response='ML is...' criteria=['accuracy','clarity']"))
                                .build(),
                        new AgentSkill.Builder()
                                .id("score_quality")
                                .name("Content Quality Scoring")
                                .description("Scores content quality across multiple dimensions (creativity, technical accuracy, completeness)")
                                .tags(List.of("scoring", "quality", "content"))
                                .examples(List.of("score_quality content='...' content_type='creative_writing' dimensions=['creativity','flow']"))
                                .build(),
                        new AgentSkill.Builder()
                                .id("check_factual_accuracy")
                                .name("Factual Accuracy Check")
                                .description("Verifies factual accuracy of claims across different domains (science, history, etc.)")
                                .tags(List.of("fact-checking", "accuracy", "verification"))
                                .examples(List.of("check_factual_accuracy claim='Earth orbits Sun' domain='astronomy'"))
                                .build(),
                        new AgentSkill.Builder()
                                .id("assess_relevance")
                                .name("Relevance Assessment")
                                .description("Assesses how relevant a response is to a given query or context")
                                .tags(List.of("relevance", "assessment", "matching"))
                                .examples(List.of("assess_relevance query='How to bake?' response='Mix ingredients...' context='cooking'"))
                                .build(),
                        new AgentSkill.Builder()
                                .id("compare_responses")
                                .name("Response Comparison")
                                .description("Compares multiple responses to the same prompt and ranks them")
                                .tags(List.of("comparison", "ranking", "evaluation"))
                                .examples(List.of("compare_responses prompt='Explain X' responses=['response1','response2'] criteria=['accuracy','detail']"))
                                .build()
                ))
                .protocolVersion("0.3.0")
                .preferredTransport("JSONRPC")
                .build();
    }

    @GET
    @Path("/extendedCard")
    @Produces(MediaType.APPLICATION_JSON)
    public AgentCard getExtendedAgentCard() {
        return getAgentCard();
    }

    @GET
    @Path("/authenticatedExtendedCard")
    @Produces(MediaType.APPLICATION_JSON)
    public AgentCard getAuthenticatedExtendedAgentCard() {
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

    private String getBaseUrl() {
        try {
            // Get the base URL from the request
            String scheme = uriInfo.getRequestUri().getScheme();
            String host = uriInfo.getRequestUri().getHost();
            int port = uriInfo.getRequestUri().getPort();
            
            // Build the base URL
            StringBuilder baseUrl = new StringBuilder();
            baseUrl.append(scheme).append("://").append(host);
            
            // Only add port if it's not the default port
            if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
                baseUrl.append(":").append(port);
            }
            
            return baseUrl.toString();
        } catch (Exception e) {
            // Fallback to environment variable or default
            String herokuUrl = System.getenv("HEROKU_APP_NAME");
            if (herokuUrl != null && !herokuUrl.isEmpty()) {
                return "https://" + herokuUrl + ".herokuapp.com";
            }
            
            // Final fallback
            return "https://a2a-llm-judge-agent.herokuapp.com";
        }
    }
}

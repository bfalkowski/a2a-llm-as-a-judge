package io.a2a.examples.llmjudge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@ApplicationScoped
public class LLMService {

    private static final Logger log = Logger.getLogger(LLMService.class);

    @ConfigProperty(name = "llm.api.key", defaultValue = "not-set")
    String apiKey;

    @ConfigProperty(name = "llm.api.url", defaultValue = "https://api.openai.com/v1/chat/completions")
    String apiUrl;

    @ConfigProperty(name = "llm.model", defaultValue = "gpt-4")
    String model;

    @ConfigProperty(name = "llm.temperature", defaultValue = "0.3")
    double temperature;

    @ConfigProperty(name = "llm.max.tokens", defaultValue = "1000")
    int maxTokens;

    @ConfigProperty(name = "llm.timeout", defaultValue = "30")
    int timeoutSeconds;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("not-set");
    }

    public Map<String, Object> evaluateResponse(String prompt, String response, List<String> criteria) {
        if (!isConfigured()) {
            log.warn("LLM API not configured, falling back to mock evaluation");
            return getMockEvaluation(prompt, response, criteria);
        }

        try {
            String llmPrompt = buildEvaluationPrompt(prompt, response, criteria);
            String llmResponse = callLLMAPI(llmPrompt);
            return parseLLMResponse(llmResponse);
        } catch (Exception e) {
            log.error("LLM evaluation failed, falling back to mock: " + e.getMessage());
            return getMockEvaluation(prompt, response, criteria);
        }
    }

    public Map<String, Object> scoreQuality(String content, String contentType, List<String> dimensions) {
        if (!isConfigured()) {
            return getMockQualityScore(content, contentType, dimensions);
        }

        try {
            String llmPrompt = buildQualityPrompt(content, contentType, dimensions);
            String llmResponse = callLLMAPI(llmPrompt);
            return parseQualityResponse(llmResponse);
        } catch (Exception e) {
            log.error("LLM quality scoring failed, falling back to mock: " + e.getMessage());
            return getMockQualityScore(content, contentType, dimensions);
        }
    }

    public Map<String, Object> checkFactualAccuracy(String claim, String domain, String verificationLevel) {
        if (!isConfigured()) {
            return getMockFactualCheck(claim, domain);
        }

        try {
            String llmPrompt = buildFactualPrompt(claim, domain, verificationLevel);
            String llmResponse = callLLMAPI(llmPrompt);
            return parseFactualResponse(llmResponse);
        } catch (Exception e) {
            log.error("LLM factual check failed, falling back to mock: " + e.getMessage());
            return getMockFactualCheck(claim, domain);
        }
    }

    private String callLLMAPI(String prompt) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiUrl);
            
            // Set headers
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + apiKey);
            
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("temperature", temperature);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
            ));
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            request.setEntity(new StringEntity(jsonBody));
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("LLM API error: " + responseBody);
                }
                
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                return jsonResponse.get("choices").get(0).get("message").get("content").asText();
            }
        }
    }

    private String buildEvaluationPrompt(String prompt, String response, List<String> criteria) {
        return String.format("""
            You are an expert evaluator. Rate this response on a scale of 0-10.
            
            Original Prompt: %s
            Response to Evaluate: %s
            Evaluation Criteria: %s
            
            Please return ONLY a valid JSON response with this exact structure:
            {
              "overall_score": <number 0-10>,
              "criteria_scores": {
                <criterion>: <score 0-10>,
                ...
              },
              "feedback": "<detailed explanation>",
              "strengths": ["<strength1>", "<strength2>", ...],
              "areas_for_improvement": ["<improvement1>", "<improvement2>", ...]
            }
            
            Be specific, constructive, and professional in your evaluation.
            """, prompt, response, String.join(", ", criteria));
    }

    private String buildQualityPrompt(String content, String contentType, List<String> dimensions) {
        return String.format("""
            You are an expert content quality assessor. Rate this content on a scale of 0-10.
            
            Content: %s
            Content Type: %s
            Evaluation Dimensions: %s
            
            Please return ONLY a valid JSON response with this exact structure:
            {
              "overall_score": <number 0-10>,
              "dimension_scores": {
                <dimension>: <score 0-10>,
                ...
              },
              "analysis": "<detailed analysis>",
              "suggestions": ["<suggestion1>", "<suggestion2>", ...]
            }
            
            Be thorough and constructive in your assessment.
            """, content, contentType, String.join(", ", dimensions));
    }

    private String buildFactualPrompt(String claim, String domain, String verificationLevel) {
        return String.format("""
            You are an expert fact-checker. Verify this claim for accuracy.
            
            Claim: %s
            Domain: %s
            Verification Level: %s
            
            Please return ONLY a valid JSON response with this exact structure:
            {
              "accuracy_score": <number 0-10>,
              "verification_status": "<verified|partially_verified|unverified|disputed>",
              "confidence": <number 0-1>,
              "analysis": "<detailed analysis>",
              "supporting_evidence": ["<evidence1>", "<evidence2>", ...],
              "caveats": ["<caveat1>", "<caveat2>", ...]
            }
            
            Be objective and evidence-based in your assessment.
            """, claim, domain, verificationLevel);
    }

    private Map<String, Object> parseLLMResponse(String response) throws Exception {
        // Clean up the response (remove any markdown formatting)
        String cleanResponse = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
        
        JsonNode jsonNode = objectMapper.readTree(cleanResponse);
        return objectMapper.convertValue(jsonNode, Map.class);
    }

    private Map<String, Object> parseQualityResponse(String response) throws Exception {
        return parseLLMResponse(response);
    }

    private Map<String, Object> parseFactualResponse(String response) throws Exception {
        return parseLLMResponse(response);
    }

    // Fallback mock methods (simplified versions of current mock logic)
    private Map<String, Object> getMockEvaluation(String prompt, String response, List<String> criteria) {
        double score = Math.min(10.0, Math.max(0.0, 5.0 + (Math.random() - 0.5) * 4.0));
        Map<String, Double> criteriaScores = new HashMap<>();
        for (String criterion : criteria) {
            criteriaScores.put(criterion, Math.min(10.0, Math.max(0.0, score + (Math.random() - 0.5) * 2.0)));
        }
        
        return Map.of(
            "overall_score", score,
            "criteria_scores", criteriaScores,
            "feedback", "Mock evaluation - LLM not configured",
            "strengths", List.of("Mock response"),
            "areas_for_improvement", List.of("Configure LLM API")
        );
    }

    private Map<String, Object> getMockQualityScore(String content, String contentType, List<String> dimensions) {
        double score = Math.min(10.0, Math.max(0.0, 5.0 + (Math.random() - 0.5) * 4.0));
        Map<String, Double> dimensionScores = new HashMap<>();
        for (String dimension : dimensions) {
            dimensionScores.put(dimension, Math.min(10.0, Math.max(0.0, score + (Math.random() - 0.5) * 2.0)));
        }
        
        return Map.of(
            "overall_score", score,
            "dimension_scores", dimensionScores,
            "analysis", "Mock analysis - LLM not configured",
            "suggestions", List.of("Configure LLM API")
        );
    }

    private Map<String, Object> getMockFactualCheck(String claim, String domain) {
        double score = Math.min(10.0, Math.max(0.0, 7.0 + (Math.random() - 0.5) * 2.0));
        
        return Map.of(
            "accuracy_score", score,
            "verification_status", score > 8.0 ? "verified" : "unverified",
            "confidence", Math.min(1.0, Math.max(0.0, score / 10.0)),
            "analysis", "Mock analysis - LLM not configured",
            "supporting_evidence", List.of("Mock evidence"),
            "caveats", List.of("Configure LLM API")
        );
    }
}

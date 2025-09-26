package io.a2a.examples.llmjudge;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

@Path("/jsonrpc")
public class JsonRpcResource {

    @Inject
    LLMService llmService;

    @Inject
    UriInfo uriInfo;

    private final Random random = new Random();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> handleJsonRpc(Map<String, Object> request) {
        String method = (String) request.get("method");
        Object params = request.get("params");
        Object id = request.get("id");

        Object result;
        switch (method) {
            // Standard A2A Protocol Methods
            case "agent.discover":
                result = Map.of(
                    "name", "LLM-as-a-Judge A2A Agent",
                    "description", "An A2A agent that simulates LLM-as-a-judge evaluation capabilities for content quality assessment, factual accuracy checking, and relevance analysis",
                    "version", "1.0.0",
                    "protocolVersion", "0.3.0",
                    "capabilities", Map.of(
                        "streaming", false,
                        "pushNotifications", false,
                        "stateTransitionHistory", false
                    ),
                    "skills", List.of(
                        Map.of("id", "evaluate_response", "name", "Response Quality Evaluation", "description", "Evaluates the quality of a response against specified criteria"),
                        Map.of("id", "score_quality", "name", "Content Quality Scoring", "description", "Scores content quality across multiple dimensions"),
                        Map.of("id", "check_factual_accuracy", "name", "Factual Accuracy Check", "description", "Verifies factual accuracy of claims across different domains"),
                        Map.of("id", "assess_relevance", "name", "Relevance Assessment", "description", "Assesses how relevant a response is to a given query"),
                        Map.of("id", "compare_responses", "name", "Response Comparison", "description", "Compares multiple responses to the same prompt and ranks them")
                    )
                );
                break;
            case "agent.info":
                result = Map.of(
                    "name", "LLM-as-a-Judge A2A Agent",
                    "description", "An A2A agent that simulates LLM-as-a-judge evaluation capabilities for content quality assessment, factual accuracy checking, and relevance analysis",
                    "version", "1.0.0",
                    "url", getBaseUrl(),
                    "protocolVersion", "0.3.0"
                );
                break;
            case "agent.getCapabilities":
                result = Map.of(
                    "streaming", false,
                    "pushNotifications", false,
                    "stateTransitionHistory", false,
                    "supportedTransports", List.of("JSONRPC"),
                    "supportedProtocols", List.of("A2A-0.3.0")
                );
                break;
            case "agent.getSkills":
                result = List.of(
                    Map.of(
                        "id", "evaluate_response",
                        "name", "Response Quality Evaluation",
                        "description", "Evaluates the quality of a response against specified criteria (accuracy, clarity, relevance, etc.)",
                        "tags", List.of("evaluation", "quality", "assessment"),
                        "examples", List.of("evaluate_response prompt='Explain ML' response='ML is...' criteria=['accuracy','clarity']")
                    ),
                    Map.of(
                        "id", "score_quality",
                        "name", "Content Quality Scoring",
                        "description", "Scores content quality across multiple dimensions (creativity, technical accuracy, completeness)",
                        "tags", List.of("scoring", "quality", "content"),
                        "examples", List.of("score_quality content='...' content_type='creative_writing' dimensions=['creativity','flow']")
                    ),
                    Map.of(
                        "id", "check_factual_accuracy",
                        "name", "Factual Accuracy Check",
                        "description", "Verifies factual accuracy of claims across different domains (science, history, etc.)",
                        "tags", List.of("fact-checking", "accuracy", "verification"),
                        "examples", List.of("check_factual_accuracy claim='Earth orbits Sun' domain='astronomy'")
                    ),
                    Map.of(
                        "id", "assess_relevance",
                        "name", "Relevance Assessment",
                        "description", "Assesses how relevant a response is to a given query or context",
                        "tags", List.of("relevance", "assessment", "matching"),
                        "examples", List.of("assess_relevance query='How to bake?' response='Mix ingredients...' context='cooking'")
                    ),
                    Map.of(
                        "id", "compare_responses",
                        "name", "Response Comparison",
                        "description", "Compares multiple responses to the same prompt and ranks them",
                        "tags", List.of("comparison", "ranking", "evaluation"),
                        "examples", List.of("compare_responses prompt='Explain X' responses=['response1','response2'] criteria=['accuracy','detail']")
                    )
                );
                break;
            case "agent.health":
                result = Map.of(
                    "status", "UP",
                    "platform", "Heroku",
                    "timestamp", System.currentTimeMillis(),
                    "version", "1.0.0"
                );
                break;
            case "agent.status":
                result = Map.of(
                    "status", "UP",
                    "uptime", "running",
                    "lastHealthCheck", System.currentTimeMillis(),
                    "activeConnections", 0,
                    "evaluationMode", llmService != null && llmService.isConfigured() ? "LLM" : "MOCK"
                );
                break;
            // LLM-as-a-Judge Evaluation Methods
            case "evaluate_response":
                result = evaluateResponse(params);
                break;
            case "score_quality":
                result = scoreQuality(params);
                break;
            case "check_factual_accuracy":
                result = checkFactualAccuracy(params);
                break;
            case "assess_relevance":
                result = assessRelevance(params);
                break;
            case "compare_responses":
                result = compareResponses(params);
                break;
            default:
                return Map.of(
                    "jsonrpc", "2.0",
                    "error", Map.of(
                        "code", -32601,
                        "message", "Method not found: " + method
                    ),
                    "id", id
                );
        }

        return Map.of(
            "jsonrpc", "2.0",
            "result", result,
            "id", id
        );
    }

    private Map<String, Object> evaluateResponse(Object params) {
        if (!(params instanceof Map)) {
            return Map.of(
                "error", "Invalid input: params must be an object",
                "error_code", "INVALID_INPUT"
            );
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> paramMap = (Map<String, Object>) params;
        String prompt = (String) paramMap.get("prompt");
        String response = (String) paramMap.get("response");
        Object criteriaObj = paramMap.get("criteria");

        if (prompt == null || prompt.trim().isEmpty() || response == null || response.trim().isEmpty()) {
            return Map.of(
                "error", "Invalid input: prompt and response cannot be empty",
                "error_code", "INVALID_INPUT",
                "suggestion", "Please provide non-empty prompt and response for evaluation"
            );
        }

        // Convert criteria to List<String>
        List<String> criteria = new ArrayList<>();
        if (criteriaObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> criteriaList = (List<Object>) criteriaObj;
            for (Object criterion : criteriaList) {
                if (criterion instanceof String) {
                    criteria.add((String) criterion);
                }
            }
        }
        if (criteria.isEmpty()) {
            criteria = List.of("accuracy", "clarity", "relevance", "completeness");
        }

        // Try LLM first, fall back to mock
        if (llmService != null && llmService.isConfigured()) {
            return llmService.evaluateResponse(prompt, response, criteria);
        } else {
            // Mock evaluation logic
            double overallScore = calculateResponseScore(prompt, response);
            Map<String, Double> criteriaScores = calculateCriteriaScores(prompt, response, criteriaObj);
            String feedback = generateFeedback(prompt, response, overallScore);
            List<String> strengths = generateStrengths(response);
            List<String> improvements = generateImprovements(response);

            return Map.of(
                "overall_score", overallScore,
                "criteria_scores", criteriaScores,
                "feedback", feedback,
                "strengths", strengths,
                "areas_for_improvement", improvements
            );
        }
    }

    private Map<String, Object> scoreQuality(Object params) {
        if (!(params instanceof Map)) {
            return Map.of("error", "Invalid input: params must be an object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> paramMap = (Map<String, Object>) params;
        String content = (String) paramMap.get("content");
        String contentType = (String) paramMap.get("content_type");
        Object dimensionsObj = paramMap.get("evaluation_dimensions");

        if (content == null || content.trim().isEmpty()) {
            return Map.of("error", "Invalid input: content cannot be empty");
        }

        // Convert dimensions to List<String>
        List<String> dimensions = new ArrayList<>();
        if (dimensionsObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> dimensionsList = (List<Object>) dimensionsObj;
            for (Object dimension : dimensionsList) {
                if (dimension instanceof String) {
                    dimensions.add((String) dimension);
                }
            }
        }
        if (dimensions.isEmpty()) {
            dimensions = List.of("clarity", "completeness", "accuracy", "usability");
        }

        // Try LLM first, fall back to mock
        if (llmService != null && llmService.isConfigured()) {
            return llmService.scoreQuality(content, contentType, dimensions);
        } else {
            // Mock evaluation logic
            double overallScore = calculateContentScore(content, contentType);
            Map<String, Double> dimensionScores = calculateDimensionScores(content, contentType, dimensionsObj);
            String analysis = generateContentAnalysis(content, contentType, overallScore);
            List<String> suggestions = generateContentSuggestions(content, contentType);

            return Map.of(
                "overall_score", overallScore,
                "dimension_scores", dimensionScores,
                "analysis", analysis,
                "suggestions", suggestions
            );
        }
    }

    private Map<String, Object> checkFactualAccuracy(Object params) {
        if (!(params instanceof Map)) {
            return Map.of("error", "Invalid input: params must be an object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> paramMap = (Map<String, Object>) params;
        String claim = (String) paramMap.get("claim");
        String domain = (String) paramMap.get("domain");
        String verificationLevel = (String) paramMap.get("verification_level");

        if (claim == null || claim.trim().isEmpty()) {
            return Map.of("error", "Invalid input: claim cannot be empty");
        }

        // Try LLM first, fall back to mock
        if (llmService != null && llmService.isConfigured()) {
            return llmService.checkFactualAccuracy(claim, domain, verificationLevel);
        } else {
            // Mock evaluation logic
            double accuracyScore = calculateFactualAccuracy(claim, domain);
            String verificationStatus = determineVerificationStatus(accuracyScore);
            double confidence = calculateConfidence(accuracyScore);
            String analysis = generateFactualAnalysis(claim, domain, accuracyScore);
            List<String> evidence = generateSupportingEvidence(claim, domain);
            List<String> caveats = generateCaveats(claim, domain);

            return Map.of(
                "accuracy_score", accuracyScore,
                "verification_status", verificationStatus,
                "confidence", confidence,
                "analysis", analysis,
                "supporting_evidence", evidence,
                "caveats", caveats
            );
        }
    }

    private Map<String, Object> assessRelevance(Object params) {
        if (!(params instanceof Map)) {
            return Map.of("error", "Invalid input: params must be an object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> paramMap = (Map<String, Object>) params;
        String query = (String) paramMap.get("query");
        String response = (String) paramMap.get("response");
        String context = (String) paramMap.get("context");

        if (query == null || query.trim().isEmpty() || response == null || response.trim().isEmpty()) {
            return Map.of("error", "Invalid input: query and response cannot be empty");
        }

        // Try LLM first, fall back to mock
        if (llmService != null && llmService.isConfigured()) {
            // For now, use mock for relevance - you can add this to LLMService later
            return getMockRelevanceAssessment(query, response, context);
        } else {
            // Mock evaluation logic
            double relevanceScore = calculateRelevanceScore(query, response, context);
            String relevanceLevel = determineRelevanceLevel(relevanceScore);
            String analysis = generateRelevanceAnalysis(query, response, relevanceScore);
            List<String> matchingElements = generateMatchingElements(query, response);
            List<String> missingElements = generateMissingElements(query, response);

            return Map.of(
                "relevance_score", relevanceScore,
                "relevance_level", relevanceLevel,
                "analysis", analysis,
                "matching_elements", matchingElements,
                "missing_elements", missingElements
            );
        }
    }

    private Map<String, Object> compareResponses(Object params) {
        if (!(params instanceof Map)) {
            return Map.of("error", "Invalid input: params must be an object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> paramMap = (Map<String, Object>) params;
        String prompt = (String) paramMap.get("prompt");
        Object responsesObj = paramMap.get("responses");
        Object criteriaObj = paramMap.get("comparison_criteria");

        if (prompt == null || prompt.trim().isEmpty() || !(responsesObj instanceof List)) {
            return Map.of("error", "Invalid input: prompt and responses are required");
        }

        @SuppressWarnings("unchecked")
        List<String> responses = (List<String>) responsesObj;
        if (responses.size() < 2) {
            return Map.of("error", "Invalid input: at least 2 responses required for comparison");
        }

        Map<String, Map<String, Double>> scores = new HashMap<>();
        for (int i = 0; i < responses.size(); i++) {
            String response = responses.get(i);
            double overall = calculateResponseScore(prompt, response);
            Map<String, Double> criteriaScores = calculateCriteriaScores(prompt, response, criteriaObj);
            
            Map<String, Double> responseScores = new HashMap<>();
            responseScores.put("overall", overall);
            responseScores.putAll(criteriaScores);
            scores.put("response_" + (i + 1), responseScores);
        }

        String winner = determineWinner(scores);
        String analysis = generateComparisonAnalysis(prompt, responses, scores, winner);
        List<String> recommendations = generateComparisonRecommendations(responses, scores);

        return Map.of(
            "winner", winner,
            "scores", scores,
            "analysis", analysis,
            "recommendations", recommendations
        );
    }

    // Helper methods for mock evaluation logic
    private double calculateResponseScore(String prompt, String response) {
        // Simple heuristic-based scoring
        double score = 5.0; // Base score
        
        // Length factor
        if (response.length() > 100) score += 1.0;
        if (response.length() > 300) score += 1.0;
        
        // Quality indicators
        if (response.contains(".") && response.split("\\.").length > 2) score += 1.0; // Multiple sentences
        if (response.toLowerCase().contains("example")) score += 0.5;
        if (response.toLowerCase().contains("because") || response.toLowerCase().contains("therefore")) score += 0.5;
        
        // Add some randomness for realism
        score += (random.nextDouble() - 0.5) * 2.0;
        
        return Math.max(0.0, Math.min(10.0, score));
    }

    private Map<String, Double> calculateCriteriaScores(String prompt, String response, Object criteriaObj) {
        Map<String, Double> scores = new HashMap<>();
        List<String> criteria = new ArrayList<>();
        
        if (criteriaObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> criteriaList = (List<Object>) criteriaObj;
            for (Object criterion : criteriaList) {
                if (criterion instanceof String) {
                    criteria.add((String) criterion);
                }
            }
        }
        
        if (criteria.isEmpty()) {
            criteria = List.of("accuracy", "clarity", "relevance", "completeness");
        }
        
        double baseScore = calculateResponseScore(prompt, response);
        for (String criterion : criteria) {
            double score = baseScore + (random.nextDouble() - 0.5) * 1.0;
            scores.put(criterion, Math.max(0.0, Math.min(10.0, score)));
        }
        
        return scores;
    }

    private String generateFeedback(String prompt, String response, double score) {
        if (score >= 8.0) {
            return "Excellent response that directly addresses the prompt with clear, accurate information and good structure.";
        } else if (score >= 6.0) {
            return "Good response that covers the main points but could be more detailed or better organized.";
        } else if (score >= 4.0) {
            return "Adequate response that partially addresses the prompt but lacks depth or clarity in some areas.";
        } else {
            return "Response needs significant improvement in accuracy, clarity, or relevance to the prompt.";
        }
    }

    private List<String> generateStrengths(String response) {
        List<String> strengths = new ArrayList<>();
        if (response.length() > 100) strengths.add("Comprehensive coverage");
        if (response.contains(".") && response.split("\\.").length > 2) strengths.add("Well-structured explanation");
        if (response.toLowerCase().contains("example")) strengths.add("Includes examples");
        if (strengths.isEmpty()) strengths.add("Attempts to address the topic");
        return strengths;
    }

    private List<String> generateImprovements(String response) {
        List<String> improvements = new ArrayList<>();
        if (response.length() < 50) improvements.add("Provide more detailed explanation");
        if (!response.contains(".") || response.split("\\.").length <= 2) improvements.add("Improve structure and flow");
        if (!response.toLowerCase().contains("example")) improvements.add("Add concrete examples");
        if (improvements.isEmpty()) improvements.add("Consider adding more specific details");
        return improvements;
    }

    private double calculateContentScore(String content, String contentType) {
        double score = 5.0;
        if (contentType != null) {
            switch (contentType.toLowerCase()) {
                case "creative_writing":
                    if (content.contains("metaphor") || content.contains("imagery")) score += 2.0;
                    break;
                case "technical_documentation":
                    if (content.contains("step") || content.contains("example")) score += 2.0;
                    break;
            }
        }
        score += (random.nextDouble() - 0.5) * 2.0;
        return Math.max(0.0, Math.min(10.0, score));
    }

    private Map<String, Double> calculateDimensionScores(String content, String contentType, Object dimensionsObj) {
        Map<String, Double> scores = new HashMap<>();
        List<String> dimensions = new ArrayList<>();
        
        if (dimensionsObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> dimList = (List<Object>) dimensionsObj;
            for (Object dim : dimList) {
                if (dim instanceof String) {
                    dimensions.add((String) dim);
                }
            }
        }
        
        if (dimensions.isEmpty()) {
            dimensions = List.of("clarity", "completeness", "accuracy", "usability");
        }
        
        double baseScore = calculateContentScore(content, contentType);
        for (String dimension : dimensions) {
            double score = baseScore + (random.nextDouble() - 0.5) * 1.0;
            scores.put(dimension, Math.max(0.0, Math.min(10.0, score)));
        }
        
        return scores;
    }

    private String generateContentAnalysis(String content, String contentType, double score) {
        if (score >= 8.0) {
            return "High-quality content with strong structure and clear communication.";
        } else if (score >= 6.0) {
            return "Good content with room for improvement in organization or detail.";
        } else {
            return "Content needs significant improvement in clarity, completeness, or structure.";
        }
    }

    private List<String> generateContentSuggestions(String content, String contentType) {
        List<String> suggestions = new ArrayList<>();
        if (content.length() < 100) suggestions.add("Add more detailed information");
        if (!content.contains("example")) suggestions.add("Include practical examples");
        if (contentType != null && contentType.equals("technical_documentation")) {
            suggestions.add("Add troubleshooting section");
            suggestions.add("Include prerequisites");
        }
        if (suggestions.isEmpty()) suggestions.add("Consider adding more context");
        return suggestions;
    }

    private double calculateFactualAccuracy(String claim, String domain) {
        // Simple heuristic for mock factual accuracy
        double score = 7.0; // Base score
        
        // Common facts get higher scores
        if (claim.toLowerCase().contains("earth") && claim.toLowerCase().contains("sun")) score = 9.5;
        if (claim.toLowerCase().contains("water") && claim.toLowerCase().contains("100") && claim.toLowerCase().contains("celsius")) score = 10.0;
        if (claim.toLowerCase().contains("civil war") && claim.toLowerCase().contains("1865")) score = 9.8;
        
        // Add some randomness
        score += (random.nextDouble() - 0.5) * 0.5;
        return Math.max(0.0, Math.min(10.0, score));
    }

    private String determineVerificationStatus(double score) {
        if (score >= 9.0) return "verified";
        if (score >= 7.0) return "partially_verified";
        if (score >= 5.0) return "unverified";
        return "disputed";
    }

    private double calculateConfidence(double accuracyScore) {
        return Math.max(0.0, Math.min(1.0, accuracyScore / 10.0 + (random.nextDouble() - 0.5) * 0.1));
    }

    private String generateFactualAnalysis(String claim, String domain, double score) {
        if (score >= 9.0) {
            return "This claim is highly accurate and well-established in the " + (domain != null ? domain : "given") + " domain.";
        } else if (score >= 7.0) {
            return "This claim is generally accurate but may have some nuances or exceptions.";
        } else if (score >= 5.0) {
            return "This claim has mixed accuracy and should be verified with additional sources.";
        } else {
            return "This claim appears to be inaccurate or misleading.";
        }
    }

    private List<String> generateSupportingEvidence(String claim, String domain) {
        List<String> evidence = new ArrayList<>();
        evidence.add("Based on established knowledge in " + (domain != null ? domain : "the relevant domain"));
        evidence.add("Consistent with multiple authoritative sources");
        if (domain != null && domain.equals("physics")) {
            evidence.add("Supported by fundamental physical principles");
        }
        return evidence;
    }

    private List<String> generateCaveats(String claim, String domain) {
        List<String> caveats = new ArrayList<>();
        if (claim.toLowerCase().contains("always") || claim.toLowerCase().contains("never")) {
            caveats.add("Absolute statements may have exceptions");
        }
        if (domain != null && domain.equals("history")) {
            caveats.add("Historical facts may have different interpretations");
        }
        return caveats;
    }

    private double calculateRelevanceScore(String query, String response, String context) {
        double score = 5.0;
        
        // Simple keyword matching
        String queryLower = query.toLowerCase();
        String responseLower = response.toLowerCase();
        
        String[] queryWords = queryLower.split("\\s+");
        int matches = 0;
        for (String word : queryWords) {
            if (word.length() > 3 && responseLower.contains(word)) {
                matches++;
            }
        }
        
        score += (double) matches / queryWords.length * 3.0;
        score += (random.nextDouble() - 0.5) * 1.0;
        
        return Math.max(0.0, Math.min(10.0, score));
    }

    private String determineRelevanceLevel(double score) {
        if (score >= 8.0) return "highly_relevant";
        if (score >= 6.0) return "relevant";
        if (score >= 4.0) return "partially_relevant";
        return "irrelevant";
    }

    private String generateRelevanceAnalysis(String query, String response, double score) {
        if (score >= 8.0) {
            return "Response directly addresses the query with relevant and useful information.";
        } else if (score >= 6.0) {
            return "Response is generally relevant but could be more directly focused on the query.";
        } else if (score >= 4.0) {
            return "Response is partially relevant but misses some key aspects of the query.";
        } else {
            return "Response is not relevant to the query and does not provide useful information.";
        }
    }

    private List<String> generateMatchingElements(String query, String response) {
        List<String> elements = new ArrayList<>();
        String queryLower = query.toLowerCase();
        String responseLower = response.toLowerCase();
        
        if (responseLower.contains("how") && queryLower.contains("how")) elements.add("Addresses 'how' question");
        if (responseLower.contains("what") && queryLower.contains("what")) elements.add("Addresses 'what' question");
        if (responseLower.contains("why") && queryLower.contains("why")) elements.add("Addresses 'why' question");
        
        if (elements.isEmpty()) elements.add("Some conceptual overlap");
        return elements;
    }

    private List<String> generateMissingElements(String query, String response) {
        List<String> missing = new ArrayList<>();
        String queryLower = query.toLowerCase();
        
        if (queryLower.contains("example") && !response.toLowerCase().contains("example")) {
            missing.add("Missing examples");
        }
        if (queryLower.contains("step") && !response.toLowerCase().contains("step")) {
            missing.add("Missing step-by-step instructions");
        }
        if (missing.isEmpty()) missing.add("Could be more specific");
        return missing;
    }

    private String determineWinner(Map<String, Map<String, Double>> scores) {
        String winner = "response_1";
        double bestScore = scores.get("response_1").get("overall");
        
        for (Map.Entry<String, Map<String, Double>> entry : scores.entrySet()) {
            if (entry.getValue().get("overall") > bestScore) {
                bestScore = entry.getValue().get("overall");
                winner = entry.getKey();
            }
        }
        
        return winner;
    }

    private String generateComparisonAnalysis(String prompt, List<String> responses, Map<String, Map<String, Double>> scores, String winner) {
        return "Comparison shows " + winner + " performs best overall, with stronger scores across most evaluation criteria. " +
               "The winning response provides more comprehensive and accurate information in response to the prompt.";
    }

    private List<String> generateComparisonRecommendations(List<String> responses, Map<String, Map<String, Double>> scores) {
        List<String> recommendations = new ArrayList<>();
        recommendations.add("Focus on improving clarity and detail in lower-scoring responses");
        recommendations.add("Consider incorporating strengths from the winning response");
        return recommendations;
    }

    // Mock method for relevance assessment when LLM is configured
    private Map<String, Object> getMockRelevanceAssessment(String query, String response, String context) {
        double relevanceScore = calculateRelevanceScore(query, response, context);
        String relevanceLevel = determineRelevanceLevel(relevanceScore);
        String analysis = generateRelevanceAnalysis(query, response, relevanceScore);
        List<String> matchingElements = generateMatchingElements(query, response);
        List<String> missingElements = generateMissingElements(query, response);

        return Map.of(
            "relevance_score", relevanceScore,
            "relevance_level", relevanceLevel,
            "analysis", analysis,
            "matching_elements", matchingElements,
            "missing_elements", missingElements
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
            
            // Only add port if it's not the default port and not -1 (which means use default)
            if (port != -1 && ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443))) {
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

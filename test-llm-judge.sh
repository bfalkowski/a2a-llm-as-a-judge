#!/bin/bash
# test-llm-judge.sh - Test script for LLM-as-a-Judge A2A Agent

AGENT_URL=$1
if [ -z "$AGENT_URL" ]; then
    echo "Usage: $0 <agent-url>"
    echo "Example: $0 https://your-llm-judge-agent.herokuapp.com"
    exit 1
fi

echo "Testing LLM-as-a-Judge A2A Agent at $AGENT_URL"
echo "=============================================="

# Test 1: Agent Discovery
echo -e "\n1. Testing Agent Discovery..."
curl -s -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc": "2.0", "method": "agent.discover", "params": {}, "id": 1}' | jq .

# Test 2: High-Quality Response Evaluation
echo -e "\n2. Testing High-Quality Response Evaluation..."
curl -s -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "evaluate_response",
    "params": {
      "prompt": "What is machine learning?",
      "response": "Machine learning is a subset of artificial intelligence that enables computers to learn and improve from experience without being explicitly programmed. It involves algorithms that can identify patterns in data and make predictions or decisions based on those patterns.",
      "criteria": ["accuracy", "clarity", "relevance"]
    },
    "id": 2
  }' | jq .

# Test 3: Low-Quality Response Evaluation
echo -e "\n3. Testing Low-Quality Response Evaluation..."
curl -s -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "evaluate_response",
    "params": {
      "prompt": "What is the capital of France?",
      "response": "I think it might be London or maybe Berlin. Not sure though.",
      "criteria": ["accuracy", "confidence", "relevance"]
    },
    "id": 3
  }' | jq .

# Test 4: Content Quality Scoring
echo -e "\n4. Testing Content Quality Scoring..."
curl -s -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "score_quality",
    "params": {
      "content": "The old lighthouse stood sentinel on the rocky cliff, its weathered stone walls bearing witness to countless storms.",
      "content_type": "creative_writing",
      "evaluation_dimensions": ["creativity", "imagery", "flow"]
    },
    "id": 4
  }' | jq .

# Test 5: Factual Accuracy Check
echo -e "\n5. Testing Factual Accuracy Check..."
curl -s -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "check_factual_accuracy",
    "params": {
      "claim": "The Earth orbits the Sun",
      "domain": "astronomy",
      "verification_level": "high"
    },
    "id": 5
  }' | jq .

# Test 6: Relevance Assessment
echo -e "\n6. Testing Relevance Assessment..."
curl -s -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "assess_relevance",
    "params": {
      "query": "How do I bake a chocolate cake?",
      "response": "To bake a chocolate cake, you need flour, sugar, cocoa powder, eggs, butter, and milk. Mix the dry ingredients, cream the butter and sugar, add eggs, then alternate adding dry ingredients and milk. Bake at 350°F for 25-30 minutes.",
      "context": "cooking"
    },
    "id": 6
  }' | jq .

# Test 7: Response Comparison
echo -e "\n7. Testing Response Comparison..."
curl -s -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "compare_responses",
    "params": {
      "prompt": "Explain photosynthesis",
      "responses": [
        "Photosynthesis is how plants make food using sunlight.",
        "Photosynthesis is the process by which plants convert light energy into chemical energy, using carbon dioxide and water to produce glucose and oxygen. This occurs primarily in the chloroplasts of plant cells."
      ],
      "comparison_criteria": ["accuracy", "detail", "scientific_rigor"]
    },
    "id": 7
  }' | jq .

# Test 8: Error Handling
echo -e "\n8. Testing Error Handling..."
curl -s -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "evaluate_response",
    "params": {
      "prompt": "",
      "response": "",
      "criteria": []
    },
    "id": 8
  }' | jq .

echo -e "\nTesting complete!"
echo "=================="
echo "For more test cases, see LLM_JUDGE_TESTING.md"

# LLM-as-a-Judge A2A Agent Testing Guide

This document provides comprehensive test cases for the LLM-as-a-Judge A2A Agent, demonstrating various evaluation scenarios and expected mock responses.

## Test Environment Setup

```bash
# Deploy your agent to Heroku first, then use these test cases
AGENT_URL="https://your-llm-judge-agent.herokuapp.com"
```

## Test Categories

### 1. Agent Discovery Tests

#### Basic Agent Information
```bash
# Test agent discovery
curl -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "agent.discover",
    "params": {},
    "id": 1
  }'

# Expected: Returns agent info with LLM-as-a-judge capabilities
```

#### Agent Health Check
```bash
# Test health endpoint
curl $AGENT_URL/agent/health

# Expected: {"status": "UP", "platform": "Heroku", "timestamp": 1234567890}
```

### 2. Response Quality Evaluation Tests

#### High-Quality Response Test
```bash
curl -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "evaluate_response",
    "params": {
      "prompt": "Explain the concept of machine learning",
      "response": "Machine learning is a subset of artificial intelligence that enables computers to learn and improve from experience without being explicitly programmed. It involves algorithms that can identify patterns in data and make predictions or decisions based on those patterns. There are three main types: supervised learning (learning from labeled examples), unsupervised learning (finding patterns in unlabeled data), and reinforcement learning (learning through trial and error with rewards).",
      "criteria": ["accuracy", "completeness", "clarity", "relevance"]
    },
    "id": 2
  }'

# Expected Mock Response:
# {
#   "jsonrpc": "2.0",
#   "result": {
#     "overall_score": 9.2,
#     "criteria_scores": {
#       "accuracy": 9.5,
#       "completeness": 9.0,
#       "clarity": 9.0,
#       "relevance": 9.5
#     },
#     "feedback": "Excellent response that accurately defines machine learning, covers all major types, and provides clear examples. The explanation is comprehensive and directly addresses the prompt.",
#     "strengths": ["Clear definition", "Covers all major ML types", "Good examples"],
#     "areas_for_improvement": ["Could mention specific algorithms", "Real-world applications would enhance completeness"]
#   },
#   "id": 2
# }
```

#### Low-Quality Response Test
```bash
curl -X POST $AGENT_URL/jsonrpc \
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
  }'

# Expected Mock Response:
# {
#   "jsonrpc": "2.0",
#   "result": {
#     "overall_score": 2.1,
#     "criteria_scores": {
#       "accuracy": 1.0,
#       "confidence": 2.0,
#       "relevance": 3.5
#     },
#     "feedback": "Response is factually incorrect and shows low confidence. The answer is completely wrong and the uncertainty is not helpful.",
#     "strengths": ["Attempts to answer"],
#     "areas_for_improvement": ["Factual accuracy is critical", "Confidence should match knowledge level", "Consider saying 'I don'\''t know' if uncertain"]
#   },
#   "id": 3
# }
```

### 3. Content Quality Scoring Tests

#### Creative Writing Evaluation
```bash
curl -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "score_quality",
    "params": {
      "content": "The old lighthouse stood sentinel on the rocky cliff, its weathered stone walls bearing witness to countless storms. The beacon light, though dimmed by time, still cast its hopeful glow across the churning sea, guiding lost souls home.",
      "content_type": "creative_writing",
      "evaluation_dimensions": ["creativity", "imagery", "flow", "emotional_impact"]
    },
    "id": 4
  }'

# Expected Mock Response:
# {
#   "jsonrpc": "2.0",
#   "result": {
#     "overall_score": 8.7,
#     "dimension_scores": {
#       "creativity": 8.5,
#       "imagery": 9.0,
#       "flow": 8.5,
#       "emotional_impact": 8.8
#     },
#     "analysis": "Strong creative writing with vivid imagery and emotional resonance. The metaphor of the lighthouse as a guide for lost souls is particularly effective.",
#     "suggestions": ["Consider varying sentence structure", "Add more sensory details"]
#   },
#   "id": 4
# }
```

#### Technical Documentation Evaluation
```bash
curl -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "score_quality",
    "params": {
      "content": "To install the package, run: npm install my-package. Then import it in your code: const pkg = require('\''my-package'\'');",
      "content_type": "technical_documentation",
      "evaluation_dimensions": ["clarity", "completeness", "accuracy", "usability"]
    },
    "id": 5
  }'

# Expected Mock Response:
# {
#   "jsonrpc": "2.0",
#   "result": {
#     "overall_score": 6.2,
#     "dimension_scores": {
#       "clarity": 7.0,
#       "completeness": 4.0,
#       "accuracy": 8.0,
#       "usability": 5.5
#     },
#     "analysis": "Basic installation instructions are clear and accurate, but severely lacking in completeness. Missing crucial information like prerequisites, configuration, and usage examples.",
#     "suggestions": ["Add prerequisites section", "Include configuration steps", "Provide usage examples", "Add troubleshooting section"]
#   },
#   "id": 5
# }
```

### 4. Factual Accuracy Tests

#### Historical Fact Check
```bash
curl -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "check_factual_accuracy",
    "params": {
      "claim": "The American Civil War ended in 1865 with the surrender of General Robert E. Lee at Appomattox Court House.",
      "domain": "history",
      "verification_level": "high"
    },
    "id": 6
  }'

# Expected Mock Response:
# {
#   "jsonrpc": "2.0",
#   "result": {
#     "accuracy_score": 9.8,
#     "verification_status": "verified",
#     "confidence": 0.95,
#     "analysis": "This claim is highly accurate. The American Civil War did end in 1865, and General Robert E. Lee did surrender at Appomattox Court House on April 9, 1865.",
#     "supporting_evidence": ["Historical records confirm the date", "Appomattox Court House is the correct location", "Lee was indeed the Confederate general"],
#     "caveats": ["The war technically had multiple surrender events, but Lee'\''s surrender is considered the most significant"]
#   },
#   "id": 6
# }
```

#### Scientific Fact Check
```bash
curl -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "check_factual_accuracy",
    "params": {
      "claim": "Water boils at 100 degrees Celsius at sea level under standard atmospheric pressure.",
      "domain": "physics",
      "verification_level": "high"
    },
    "id": 7
  }'

# Expected Mock Response:
# {
#   "jsonrpc": "2.0",
#   "result": {
#     "accuracy_score": 10.0,
#     "verification_status": "verified",
#     "confidence": 0.99,
#     "analysis": "This is a fundamental physical constant that is extremely well-established. Water boils at exactly 100°C (212°F) at sea level under standard atmospheric pressure (1 atm).",
#     "supporting_evidence": ["This is a basic physical constant", "Extensively verified through experimentation", "Part of the definition of the Celsius scale"],
#     "caveats": ["Temperature varies with altitude and atmospheric pressure"]
#   },
#   "id": 7
# }
```

### 5. Relevance Assessment Tests

#### Question-Answer Relevance
```bash
curl -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "assess_relevance",
    "params": {
      "query": "How do I bake a chocolate cake?",
      "response": "To bake a chocolate cake, you'\''ll need flour, sugar, cocoa powder, eggs, butter, and milk. Mix the dry ingredients, cream the butter and sugar, add eggs, then alternate adding dry ingredients and milk. Bake at 350°F for 25-30 minutes.",
      "context": "cooking_instructions"
    },
    "id": 8
  }'

# Expected Mock Response:
# {
#   "jsonrpc": "2.0",
#   "result": {
#     "relevance_score": 9.5,
#     "relevance_level": "highly_relevant",
#     "analysis": "The response directly addresses the query about baking a chocolate cake. It provides specific ingredients, clear steps, and appropriate baking instructions.",
#     "matching_elements": ["Direct answer to baking question", "Relevant ingredients listed", "Appropriate cooking method", "Correct temperature and time"],
#     "missing_elements": ["Could include pan preparation", "Cooling instructions would be helpful"]
#   },
#   "id": 8
# }
```

#### Off-Topic Response Test
```bash
curl -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "assess_relevance",
    "params": {
      "query": "What are the benefits of renewable energy?",
      "response": "I really love pizza, especially with pepperoni and extra cheese. My favorite pizza place is down the street from my house.",
      "context": "environmental_science"
    },
    "id": 9
  }'

# Expected Mock Response:
# {
#   "jsonrpc": "2.0",
#   "result": {
#     "relevance_score": 0.1,
#     "relevance_level": "completely_irrelevant",
#     "analysis": "The response has no connection to the query about renewable energy benefits. It discusses personal food preferences instead of addressing the environmental topic.",
#     "matching_elements": [],
#     "missing_elements": ["No mention of renewable energy", "No environmental benefits discussed", "Completely off-topic content"]
#   },
#   "id": 9
# }
```

### 6. Comparative Evaluation Tests

#### Response Comparison
```bash
curl -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "compare_responses",
    "params": {
      "prompt": "Explain photosynthesis",
      "responses": [
        "Photosynthesis is how plants make food using sunlight.",
        "Photosynthesis is the process by which plants convert light energy into chemical energy, using carbon dioxide and water to produce glucose and oxygen. This occurs primarily in the chloroplasts of plant cells, specifically in structures called thylakoids where chlorophyll captures light energy."
      ],
      "comparison_criteria": ["accuracy", "detail", "scientific_rigor"]
    },
    "id": 10
  }'

# Expected Mock Response:
# {
#   "jsonrpc": "2.0",
#   "result": {
#     "winner": "response_2",
#     "scores": {
#       "response_1": {
#         "overall": 4.2,
#         "accuracy": 6.0,
#         "detail": 2.0,
#         "scientific_rigor": 4.5
#       },
#       "response_2": {
#         "overall": 9.1,
#         "accuracy": 9.5,
#         "detail": 9.0,
#         "scientific_rigor": 8.8
#       }
#     },
#     "analysis": "Response 2 is significantly superior, providing detailed scientific explanation with specific cellular structures and chemical processes, while Response 1 is overly simplistic.",
#     "recommendations": ["Response 1 needs more scientific detail", "Response 2 could include the chemical equation"]
#   },
#   "id": 10
# }
```

### 7. Edge Cases and Error Handling

#### Invalid Input Test
```bash
curl -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "evaluate_response",
    "params": {
      "prompt": "",
      "response": "",
      "criteria": []
    },
    "id": 11
  }'

# Expected Mock Response:
# {
#   "jsonrpc": "2.0",
#   "result": {
#     "error": "Invalid input: prompt and response cannot be empty",
#     "error_code": "INVALID_INPUT",
#     "suggestion": "Please provide non-empty prompt and response for evaluation"
#   },
#   "id": 11
# }
```

#### Unsupported Method Test
```bash
curl -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "unsupported_method",
    "params": {},
    "id": 12
  }'

# Expected Mock Response:
# {
#   "jsonrpc": "2.0",
#   "error": {
#     "code": -32601,
#     "message": "Method not found: unsupported_method"
#   },
#   "id": 12
# }
```

## Test Scripts

### Quick Test Script (Bash)
```bash
#!/bin/bash
# test-llm-judge.sh

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
      "response": "Machine learning is a subset of AI that enables computers to learn from data without explicit programming.",
      "criteria": ["accuracy", "clarity", "relevance"]
    },
    "id": 2
  }' | jq .

# Test 3: Factual Accuracy Check
echo -e "\n3. Testing Factual Accuracy Check..."
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
    "id": 3
  }' | jq .

echo -e "\nTesting complete!"
```

### Python Test Script
```python
#!/usr/bin/env python3
# test_llm_judge.py

import requests
import json
import sys

def test_agent(agent_url):
    """Test the LLM-as-a-Judge A2A Agent"""
    
    print(f"Testing LLM-as-a-Judge A2A Agent at {agent_url}")
    print("=" * 50)
    
    # Test cases
    test_cases = [
        {
            "name": "Agent Discovery",
            "method": "agent.discover",
            "params": {}
        },
        {
            "name": "Response Quality Evaluation",
            "method": "evaluate_response",
            "params": {
                "prompt": "Explain quantum computing",
                "response": "Quantum computing uses quantum mechanical phenomena to perform calculations that would be impossible for classical computers.",
                "criteria": ["accuracy", "clarity", "technical_depth"]
            }
        },
        {
            "name": "Factual Accuracy Check",
            "method": "check_factual_accuracy",
            "params": {
                "claim": "The speed of light is approximately 299,792,458 meters per second",
                "domain": "physics",
                "verification_level": "high"
            }
        },
        {
            "name": "Relevance Assessment",
            "method": "assess_relevance",
            "params": {
                "query": "How to make coffee?",
                "response": "To make coffee, grind beans, add to filter, pour hot water, and let it brew for 4 minutes.",
                "context": "cooking"
            }
        }
    ]
    
    for i, test_case in enumerate(test_cases, 1):
        print(f"\n{i}. Testing {test_case['name']}...")
        
        payload = {
            "jsonrpc": "2.0",
            "method": test_case["method"],
            "params": test_case["params"],
            "id": i
        }
        
        try:
            response = requests.post(
                f"{agent_url}/jsonrpc",
                headers={"Content-Type": "application/json"},
                json=payload,
                timeout=10
            )
            response.raise_for_status()
            result = response.json()
            print(json.dumps(result, indent=2))
        except Exception as e:
            print(f"Error: {e}")
    
    print("\nTesting complete!")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python3 test_llm_judge.py <agent-url>")
        print("Example: python3 test_llm_judge.py https://your-llm-judge-agent.herokuapp.com")
        sys.exit(1)
    
    test_agent(sys.argv[1])
```

## Expected Mock Response Patterns

The LLM-as-a-Judge agent will return mock responses that simulate realistic evaluation results:

### Response Quality Evaluation
- **Overall Score**: 0-10 scale
- **Criteria Scores**: Individual scores for each evaluation criterion
- **Feedback**: Detailed written feedback
- **Strengths**: List of positive aspects
- **Areas for Improvement**: Constructive suggestions

### Factual Accuracy Check
- **Accuracy Score**: 0-10 scale
- **Verification Status**: verified/unverified/partially_verified
- **Confidence**: 0-1 scale
- **Analysis**: Detailed explanation
- **Supporting Evidence**: List of supporting facts
- **Caveats**: Important limitations or context

### Relevance Assessment
- **Relevance Score**: 0-10 scale
- **Relevance Level**: highly_relevant/relevant/partially_relevant/irrelevant
- **Analysis**: Explanation of relevance
- **Matching Elements**: What matches the query
- **Missing Elements**: What's missing or could be improved

This testing framework provides comprehensive coverage of the LLM-as-a-Judge evaluation capabilities and will help you understand how the agent responds to different types of input.

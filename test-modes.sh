#!/bin/bash
# test-modes.sh - Test both MOCK and LLM modes

AGENT_URL=$1
if [ -z "$AGENT_URL" ]; then
    echo "Usage: $0 <agent-url>"
    echo "Example: $0 https://your-llm-judge-agent.herokuapp.com"
    exit 1
fi

echo "Testing LLM-as-a-Judge Agent Modes"
echo "=================================="

# Test 1: Check current mode
echo -e "\n1. Checking current evaluation mode..."
curl -s -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc": "2.0", "method": "agent.status", "params": {}, "id": 1}' | jq .

# Test 2: Evaluate response (will use MOCK or LLM based on configuration)
echo -e "\n2. Testing response evaluation..."
curl -s -X POST $AGENT_URL/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "evaluate_response",
    "params": {
      "prompt": "What is machine learning?",
      "response": "Machine learning is a subset of artificial intelligence that enables computers to learn from data without explicit programming.",
      "criteria": ["accuracy", "clarity", "relevance"]
    },
    "id": 2
  }' | jq .

echo -e "\nMode Explanation:"
echo "=================="
echo "• MOCK mode: Uses heuristic-based scoring (no API costs)"
echo "• LLM mode: Uses real LLM API calls (requires LLM_API_KEY in Heroku config)"
echo ""
echo "To enable LLM mode:"
echo "  heroku config:set LLM_API_KEY=your_openai_api_key_here"
echo ""
echo "To disable LLM mode:"
echo "  heroku config:unset LLM_API_KEY"

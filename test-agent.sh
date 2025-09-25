#!/bin/bash

# A2A HelloWorld Agent Test Script
# Tests the deployed agent using native A2A protocol methods

# Check if URL is provided
if [ $# -eq 0 ]; then
    echo "❌ Error: Please provide the agent URL as a command line argument"
    echo "Usage: $0 <agent-url>"
    echo "Example: $0 https://your-agent.herokuapp.com"
    exit 1
fi

AGENT_URL="$1"
JSONRPC_URL="$AGENT_URL/jsonrpc"

echo "🤖 Testing A2A HelloWorld Agent at $AGENT_URL"
echo "Using native A2A protocol methods via JSON-RPC"
echo "=================================================="

# Helper function to make JSON-RPC calls
make_jsonrpc_call() {
    local method="$1"
    local params="$2"
    local id="$3"
    local description="$4"
    
    echo "$description:"
    curl -s -X POST "$JSONRPC_URL" \
      -H "Content-Type: application/json" \
      -d "{\"jsonrpc\":\"2.0\",\"method\":\"$method\",\"params\":$params,\"id\":$id}" | jq '.' 2>/dev/null || \
    curl -s -X POST "$JSONRPC_URL" \
      -H "Content-Type: application/json" \
      -d "{\"jsonrpc\":\"2.0\",\"method\":\"$method\",\"params\":$params,\"id\":$id}"
    echo -e "\n"
}

# Test A2A agent card (via HTTP GET as per A2A spec)
echo "🎴 A2A Agent Card:"
echo "Getting agent card (A2A standard endpoint):"
curl -s "$AGENT_URL/agent" | jq '.' 2>/dev/null || curl -s "$AGENT_URL/agent"
echo -e "\n"

# Test A2A agent discovery
echo "🔍 A2A Agent Discovery:"
make_jsonrpc_call "agent.discover" "{}" "1" "Discovering agent capabilities"

# Test A2A agent info
echo "ℹ️  A2A Agent Info:"
make_jsonrpc_call "agent.info" "{}" "2" "Getting agent information"

# Test A2A agent capabilities
echo "🎯 A2A Agent Capabilities:"
make_jsonrpc_call "agent.getCapabilities" "{}" "3" "Getting agent capabilities"

# Test A2A agent skills
echo "🛠️  A2A Agent Skills:"
make_jsonrpc_call "agent.getSkills" "{}" "4" "Getting available skills"

# Test A2A agent health
echo "💚 A2A Agent Health:"
make_jsonrpc_call "agent.health" "{}" "5" "Checking agent health"

# Test custom greeting method
echo "👋 Custom Greeting Method:"
make_jsonrpc_call "greeting" "{}" "6" "Testing greeting method"

# Test custom echo method
echo "🔄 Custom Echo Method:"
make_jsonrpc_call "echo" "{\"message\":\"Hello from A2A test!\"}" "7" "Testing echo method"

# Test custom heroku-info method
echo "☁️  Custom Heroku Info Method:"
make_jsonrpc_call "heroku-info" "{}" "8" "Testing heroku-info method"

# Test A2A agent status
echo "📊 A2A Agent Status:"
make_jsonrpc_call "agent.status" "{}" "9" "Getting agent status"

echo "✅ All A2A protocol tests completed!"

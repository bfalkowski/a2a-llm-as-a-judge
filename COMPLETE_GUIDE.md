# LLM-as-a-Judge A2A Agent - Complete Guide

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Deployment](#deployment)
4. [API Reference](#api-reference)
5. [Modes of Operation](#modes-of-operation)
6. [Security](#security)
7. [Testing](#testing)
8. [Configuration](#configuration)
9. [Troubleshooting](#troubleshooting)
10. [Advanced Usage](#advanced-usage)

## Overview

The LLM-as-a-Judge A2A Agent is a self-contained Agent-to-Agent (A2A) service that provides content evaluation capabilities. It can operate in two modes:

- **MOCK Mode**: Uses intelligent heuristic-based scoring (no API costs)
- **LLM Mode**: Uses real LLM API calls (OpenAI, Anthropic, etc.)

### Key Features

- ✅ **A2A Protocol Compliant**: Full Agent-to-Agent protocol support
- ✅ **Dual Mode Operation**: Mock responses or real LLM evaluation
- ✅ **Heroku Ready**: Optimized for cloud deployment
- ✅ **Secure**: API key authentication and rate limiting
- ✅ **Fallback Safe**: Automatically falls back to mock if LLM fails
- ✅ **Cost Controlled**: No API costs until you're ready

## Architecture

### Core Components

```
┌─────────────────────────────────────────────────────────────┐
│                    A2A LLM-as-a-Judge Agent                │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │  Agent Discovery │  │  JSON-RPC API   │  │  Root Info   │ │
│  │  (A2A Protocol)  │  │  (Evaluation)   │  │  (Status)    │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │  Security       │  │  Rate Limiting  │  │  Mode        │ │
│  │  (API Keys)     │  │  (Per IP)       │  │  Detection   │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │  LLM Service    │  │  Mock Service   │  │  Fallback    │ │
│  │  (Real APIs)    │  │  (Heuristics)   │  │  Logic       │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Evaluation Methods

1. **`evaluate_response`** - Evaluates response quality against criteria
2. **`score_quality`** - Scores content quality across dimensions
3. **`check_factual_accuracy`** - Verifies factual accuracy of claims
4. **`assess_relevance`** - Assesses response relevance to queries
5. **`compare_responses`** - Compares multiple responses to same prompt

## Deployment

### Prerequisites

- Java 17+
- Maven 3.6+
- Heroku CLI
- Git repository

### Quick Deploy

```bash
# 1. Clone and navigate to project
git clone <your-repo-url>
cd a2a-llm-as-a-judge

# 2. Create Heroku app
heroku create your-llm-judge-agent

# 3. Deploy
git push heroku main

# 4. Test
curl https://your-llm-judge-agent.herokuapp.com/agent/health
```

### Environment Setup

```bash
# Optional: Set LLM API key for real evaluation
heroku config:set LLM_API_KEY=sk-your-openai-key-here

# Optional: Set agent API key for security
heroku config:set AGENT_API_KEY=your-secure-agent-key

# Optional: Configure rate limits
heroku config:set RATE_LIMIT_REQUESTS=100
heroku config:set RATE_LIMIT_WINDOW_MINUTES=60
```

## API Reference

### A2A Discovery Endpoints

#### Agent Card
```http
GET /agent
```
Returns A2A-compliant agent card with capabilities and skills.

#### Health Check
```http
GET /agent/health
```
Returns agent health status and current evaluation mode.

#### Extended Card
```http
GET /agent/extendedCard
GET /agent/authenticatedExtendedCard
```
Returns extended agent information.

### JSON-RPC Endpoints

#### Base Endpoint
```http
POST /jsonrpc
Content-Type: application/json
```

#### A2A Protocol Methods

**Agent Discovery**
```json
{
  "jsonrpc": "2.0",
  "method": "agent.discover",
  "params": {},
  "id": 1
}
```

**Agent Info**
```json
{
  "jsonrpc": "2.0",
  "method": "agent.info",
  "params": {},
  "id": 2
}
```

**Agent Status**
```json
{
  "jsonrpc": "2.0",
  "method": "agent.status",
  "params": {},
  "id": 3
}
```

#### Evaluation Methods

**Response Quality Evaluation**
```json
{
  "jsonrpc": "2.0",
  "method": "evaluate_response",
  "params": {
    "prompt": "What is machine learning?",
    "response": "Machine learning is a subset of AI...",
    "criteria": ["accuracy", "clarity", "relevance"]
  },
  "id": 4
}
```

**Content Quality Scoring**
```json
{
  "jsonrpc": "2.0",
  "method": "score_quality",
  "params": {
    "content": "The old lighthouse stood sentinel...",
    "content_type": "creative_writing",
    "evaluation_dimensions": ["creativity", "imagery", "flow"]
  },
  "id": 5
}
```

**Factual Accuracy Check**
```json
{
  "jsonrpc": "2.0",
  "method": "check_factual_accuracy",
  "params": {
    "claim": "The Earth orbits the Sun",
    "domain": "astronomy",
    "verification_level": "high"
  },
  "id": 6
}
```

**Relevance Assessment**
```json
{
  "jsonrpc": "2.0",
  "method": "assess_relevance",
  "params": {
    "query": "How to bake a cake?",
    "response": "Mix flour, sugar, eggs...",
    "context": "cooking"
  },
  "id": 7
}
```

**Response Comparison**
```json
{
  "jsonrpc": "2.0",
  "method": "compare_responses",
  "params": {
    "prompt": "Explain photosynthesis",
    "responses": [
      "Photosynthesis is how plants make food...",
      "Photosynthesis is the process by which plants convert light energy..."
    ],
    "comparison_criteria": ["accuracy", "detail", "scientific_rigor"]
  },
  "id": 8
}
```

## Modes of Operation

### MOCK Mode (Default)

**When Active:**
- No `LLM_API_KEY` environment variable set
- LLM API key is empty or invalid
- LLM API calls fail

**Characteristics:**
- Uses intelligent heuristic-based scoring
- Content-aware evaluation (analyzes input)
- Random variation for realism
- No API costs
- Fast response times

**Example Response:**
```json
{
  "jsonrpc": "2.0",
  "result": {
    "overall_score": 8.2,
    "criteria_scores": {
      "accuracy": 8.5,
      "clarity": 8.0,
      "relevance": 8.1
    },
    "feedback": "Excellent response that directly addresses the prompt...",
    "strengths": ["Clear definition", "Good examples"],
    "areas_for_improvement": ["Could mention specific algorithms"]
  },
  "id": 4
}
```

### LLM Mode

**When Active:**
- Valid `LLM_API_KEY` environment variable set
- LLM service is properly configured
- API calls succeed

**Characteristics:**
- Uses real LLM API calls (OpenAI, Anthropic, etc.)
- High-quality evaluation
- API costs apply
- Slower response times
- Automatic fallback to mock on errors

**Example Response:**
```json
{
  "jsonrpc": "2.0",
  "result": {
    "overall_score": 9.1,
    "criteria_scores": {
      "accuracy": 9.5,
      "clarity": 8.8,
      "relevance": 9.0
    },
    "feedback": "This response demonstrates a comprehensive understanding of machine learning...",
    "strengths": ["Accurate technical definition", "Clear explanation", "Relevant examples"],
    "areas_for_improvement": ["Could include more specific use cases"]
  },
  "id": 4
}
```

### Mode Detection

Check current mode:
```bash
curl -X POST https://your-app.herokuapp.com/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc": "2.0", "method": "agent.status", "params": {}, "id": 1}'
```

Response includes:
```json
{
  "result": {
    "status": "UP",
    "evaluationMode": "MOCK",  // or "LLM"
    "uptime": "running"
  }
}
```

## Security

### API Key Authentication

**Enable Security:**
```bash
heroku config:set AGENT_API_KEY=your-secure-key-here
```

**Usage:**
```bash
curl -X POST https://your-app.herokuapp.com/jsonrpc \
  -H "Content-Type: application/json" \
  -H "X-API-Key: your-secure-key-here" \
  -d '{"jsonrpc": "2.0", "method": "evaluate_response", ...}'
```

**Security Levels:**
- **Level 1**: API key required for evaluation endpoints
- **Level 2**: API key + rate limiting per IP
- **Level 3**: Full authentication for all endpoints

### Rate Limiting

**Configuration:**
```bash
heroku config:set RATE_LIMIT_REQUESTS=100
heroku config:set RATE_LIMIT_WINDOW_MINUTES=60
```

**Response when exceeded:**
```json
{
  "error": "Rate limit exceeded",
  "message": "Too many requests",
  "retry_after": 3600
}
```

### Environment Variables

**Required for LLM Mode:**
```bash
LLM_API_KEY=sk-your-openai-key-here
```

**Optional Configuration:**
```bash
LLM_API_URL=https://api.openai.com/v1/chat/completions
LLM_MODEL=gpt-4
LLM_TEMPERATURE=0.3
LLM_MAX_TOKENS=1000
LLM_TIMEOUT=30
```

**Security Configuration:**
```bash
AGENT_API_KEY=your-secure-agent-key
RATE_LIMIT_REQUESTS=100
RATE_LIMIT_WINDOW_MINUTES=60
```

## Testing

### Test Scripts

**Comprehensive Testing:**
```bash
./test-llm-judge.sh https://your-app.herokuapp.com
```

**Mode Testing:**
```bash
./test-modes.sh https://your-app.herokuapp.com
```

**Python Testing:**
```bash
python3 test_llm_judge.py https://your-app.herokuapp.com
```

### Manual Testing

**Health Check:**
```bash
curl https://your-app.herokuapp.com/agent/health
```

**Agent Discovery:**
```bash
curl https://your-app.herokuapp.com/agent
```

**Evaluation Test:**
```bash
curl -X POST https://your-app.herokuapp.com/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "evaluate_response",
    "params": {
      "prompt": "What is AI?",
      "response": "AI is artificial intelligence that enables machines to perform tasks that typically require human intelligence.",
      "criteria": ["accuracy", "clarity"]
    },
    "id": 1
  }'
```

### Test Cases

See `LLM_JUDGE_TESTING.md` for comprehensive test cases including:
- High-quality response evaluation
- Low-quality response evaluation
- Creative writing assessment
- Technical documentation scoring
- Factual accuracy verification
- Relevance assessment
- Response comparison
- Error handling

## Configuration

### Heroku Configuration

**View Current Config:**
```bash
heroku config
```

**Set Configuration:**
```bash
heroku config:set KEY=value
```

**Unset Configuration:**
```bash
heroku config:unset KEY
```

### Application Properties

Located in `src/main/resources/application.properties`:

```properties
# Server Configuration
quarkus.http.host=0.0.0.0
quarkus.http.port=${PORT:8080}

# CORS for A2A
quarkus.http.cors=true
quarkus.http.cors.origins=*
quarkus.http.cors.methods=GET,POST,PUT,DELETE,OPTIONS
quarkus.http.cors.headers=*

# LLM Configuration
llm.api.key=${LLM_API_KEY:}
llm.api.url=${LLM_API_URL:https://api.openai.com/v1/chat/completions}
llm.model=${LLM_MODEL:gpt-4}
llm.temperature=${LLM_TEMPERATURE:0.3}
llm.max.tokens=${LLM_MAX_TOKENS:1000}
llm.timeout=${LLM_TIMEOUT:30}

# Security Configuration
agent.api.key=${AGENT_API_KEY:}
rate.limit.requests=${RATE_LIMIT_REQUESTS:100}
rate.limit.window.minutes=${RATE_LIMIT_WINDOW_MINUTES:60}
```

## Troubleshooting

### Common Issues

**1. Agent Not Responding**
```bash
# Check if deployed
heroku ps

# Check logs
heroku logs --tail

# Restart if needed
heroku restart
```

**2. LLM Mode Not Working**
```bash
# Check if API key is set
heroku config:get LLM_API_KEY

# Check logs for errors
heroku logs --tail | grep -i "llm\|error"

# Test with mock mode first
heroku config:unset LLM_API_KEY
```

**3. Authentication Errors**
```bash
# Check if API key is set
heroku config:get AGENT_API_KEY

# Test without authentication
curl https://your-app.herokuapp.com/agent/health
```

**4. Rate Limiting Issues**
```bash
# Check rate limit settings
heroku config:get RATE_LIMIT_REQUESTS

# Increase limits if needed
heroku config:set RATE_LIMIT_REQUESTS=200
```

### Debug Mode

**Enable Debug Logging:**
```bash
heroku config:set QUARKUS_LOG_LEVEL=DEBUG
heroku restart
```

**View Debug Logs:**
```bash
heroku logs --tail | grep -i "debug"
```

### Performance Issues

**Check Resource Usage:**
```bash
heroku ps:scale web=1
heroku ps:scale web=2  # Scale up if needed
```

**Monitor Response Times:**
```bash
# Test response time
time curl -X POST https://your-app.herokuapp.com/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc": "2.0", "method": "agent.status", "params": {}, "id": 1}'
```

## Advanced Usage

### Custom LLM Providers

**Anthropic Claude:**
```bash
heroku config:set LLM_API_URL=https://api.anthropic.com/v1/messages
heroku config:set LLM_API_KEY=your-claude-key
heroku config:set LLM_MODEL=claude-3-sonnet-20240229
```

**Azure OpenAI:**
```bash
heroku config:set LLM_API_URL=https://your-resource.openai.azure.com/openai/deployments/your-deployment/chat/completions?api-version=2024-02-15-preview
heroku config:set LLM_API_KEY=your-azure-key
```

**Local Models (via proxy):**
```bash
heroku config:set LLM_API_URL=https://your-local-proxy.com/v1/chat/completions
heroku config:set LLM_API_KEY=your-local-key
```

### Custom Evaluation Criteria

**Default Criteria:**
- `accuracy` - Factual correctness
- `clarity` - Clear communication
- `relevance` - Relevance to prompt
- `completeness` - Thorough coverage

**Custom Criteria:**
```json
{
  "method": "evaluate_response",
  "params": {
    "prompt": "Explain quantum computing",
    "response": "Quantum computing uses...",
    "criteria": ["technical_accuracy", "accessibility", "innovation"]
  }
}
```

### Batch Processing

**Multiple Evaluations:**
```bash
# Evaluate multiple responses
for response in "response1" "response2" "response3"; do
  curl -X POST https://your-app.herokuapp.com/jsonrpc \
    -H "Content-Type: application/json" \
    -H "X-API-Key: your-key" \
    -d "{
      \"jsonrpc\": \"2.0\",
      \"method\": \"evaluate_response\",
      \"params\": {
        \"prompt\": \"What is AI?\",
        \"response\": \"$response\",
        \"criteria\": [\"accuracy\", \"clarity\"]
      },
      \"id\": 1
    }"
done
```

### Integration Examples

**Python Client:**
```python
import requests
import json

def evaluate_response(agent_url, api_key, prompt, response, criteria):
    payload = {
        "jsonrpc": "2.0",
        "method": "evaluate_response",
        "params": {
            "prompt": prompt,
            "response": response,
            "criteria": criteria
        },
        "id": 1
    }
    
    headers = {
        "Content-Type": "application/json",
        "X-API-Key": api_key
    }
    
    response = requests.post(f"{agent_url}/jsonrpc", 
                           json=payload, 
                           headers=headers)
    return response.json()

# Usage
result = evaluate_response(
    "https://your-app.herokuapp.com",
    "your-api-key",
    "What is machine learning?",
    "ML is a subset of AI...",
    ["accuracy", "clarity", "relevance"]
)
print(result)
```

**JavaScript Client:**
```javascript
async function evaluateResponse(agentUrl, apiKey, prompt, response, criteria) {
  const payload = {
    jsonrpc: "2.0",
    method: "evaluate_response",
    params: {
      prompt: prompt,
      response: response,
      criteria: criteria
    },
    id: 1
  };
  
  const response = await fetch(`${agentUrl}/jsonrpc`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-API-Key': apiKey
    },
    body: JSON.stringify(payload)
  });
  
  return await response.json();
}

// Usage
const result = await evaluateResponse(
  "https://your-app.herokuapp.com",
  "your-api-key",
  "What is machine learning?",
  "ML is a subset of AI...",
  ["accuracy", "clarity", "relevance"]
);
console.log(result);
```

## Support

### Getting Help

1. **Check Logs**: `heroku logs --tail`
2. **Test Mode**: Use `./test-modes.sh` to verify operation
3. **Health Check**: `curl https://your-app.herokuapp.com/agent/health`
4. **Documentation**: See `LLM_JUDGE_TESTING.md` for test cases

### Common Commands

```bash
# Deploy
git push heroku main

# Check status
heroku ps

# View logs
heroku logs --tail

# Restart
heroku restart

# Scale
heroku ps:scale web=2

# Config
heroku config
heroku config:set KEY=value
heroku config:unset KEY
```

---

**Built with ❤️ for the A2A ecosystem**

*This agent provides a production-ready foundation for LLM-as-a-judge evaluation capabilities while maintaining full A2A protocol compliance.*

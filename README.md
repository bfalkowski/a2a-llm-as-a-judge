# A2A LLM-as-a-Judge Agent

A self-contained A2A (Agent-to-Agent) LLM-as-a-Judge agent for content evaluation and quality assessment demos. This agent simulates LLM evaluation capabilities and can be deployed to Heroku with full A2A protocol compliance.

## Features

- **A2A Protocol Compliance**: Full A2A (Agent-to-Agent) protocol support with proper agent card schema
- **LLM-as-a-Judge Simulation**: Mock evaluation capabilities for content quality assessment, factual accuracy checking, and relevance analysis
- **Agent Discovery**: REST endpoints for agent card and health checks following A2A standards
- **JSON-RPC Communication**: Full JSON-RPC 2.0 support for agent-to-agent communication
- **Heroku Ready**: Optimized for Heroku deployment with proper port binding and CORS
- **Quarkus Framework**: Fast, lightweight Java framework for microservices
- **A2A Java SDK**: Uses official A2A Java SDK for proper schema compliance

## Available Endpoints

### A2A Agent Discovery
- `GET /` - Root endpoint with agent information and available endpoints
- `GET /agent` - A2A-compliant agent card (primary discovery endpoint)
- `GET /agent/extendedCard` - Extended agent card information
- `GET /agent/authenticatedExtendedCard` - Authenticated agent card information
- `GET /agent/health` - Health check endpoint

### JSON-RPC Communication
- `POST /jsonrpc` - JSON-RPC 2.0 endpoint for A2A protocol methods

#### Available JSON-RPC Methods:

**A2A Protocol Methods:**
- `agent.discover` - Agent discovery with capabilities and skills
- `agent.info` - Basic agent information (name, version, URL, protocol)
- `agent.getCapabilities` - Detailed capability information (streaming, transports, protocols)
- `agent.getSkills` - Available skills with descriptions, tags, and examples
- `agent.health` - Health status with timestamp and platform info
- `agent.status` - Runtime status with uptime and connection info

**LLM-as-a-Judge Evaluation Methods:**
- `evaluate_response` - Evaluates response quality against specified criteria
- `score_quality` - Scores content quality across multiple dimensions
- `check_factual_accuracy` - Verifies factual accuracy of claims across domains
- `assess_relevance` - Assesses how relevant a response is to a given query
- `compare_responses` - Compares multiple responses to the same prompt and ranks them

## Local Development

### Prerequisites
- Java 17+
- Maven 3.6+

### Running Locally
```bash
# Build the project
mvn clean package

# Run the application
java -jar target/quarkus-app/quarkus-run.jar
```

The application will be available at `http://localhost:8080`

## Heroku Deployment

### Prerequisites
- Heroku CLI installed
- Git repository with Heroku remote configured

### Deploy to Heroku
```bash
# Add Heroku remote (replace with your app name)
heroku git:remote -a your-app-name

# Deploy
git push heroku main
```

### Heroku Configuration
The application automatically:
- Uses the `PORT` environment variable provided by Heroku
- Binds to `0.0.0.0` for external access
- Enables CORS for cross-origin requests
- Uses Java 17 runtime

## Testing

The repository includes comprehensive test scripts and documentation:

- **[LLM_JUDGE_TESTING.md](LLM_JUDGE_TESTING.md)** - Detailed testing guide with examples
- **Test Scripts** - Ready-to-use scripts for testing any deployed agent

### Quick Test Scripts

#### Bash Script (Comprehensive Testing)
```bash
# Test agent using all evaluation methods (URL required)
./test-llm-judge.sh https://your-agent.herokuapp.com
```

#### Python Script (Detailed Testing)
```bash
# Test agent with detailed output (URL required)
python3 test_llm_judge.py https://your-agent.herokuapp.com
```

### Manual API Testing

#### A2A Agent Discovery
```bash
# Get A2A-compliant agent card
curl https://your-app.herokuapp.com/agent

# Health check
curl https://your-app.herokuapp.com/agent/health
```

#### LLM-as-a-Judge Evaluation Examples

**Response Quality Evaluation:**
```bash
curl -X POST https://your-app.herokuapp.com/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "evaluate_response",
    "params": {
      "prompt": "Explain machine learning",
      "response": "Machine learning is a subset of AI that enables computers to learn from data without explicit programming.",
      "criteria": ["accuracy", "clarity", "relevance"]
    },
    "id": 1
  }'
```

**Factual Accuracy Check:**
```bash
curl -X POST https://your-app.herokuapp.com/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "check_factual_accuracy",
    "params": {
      "claim": "The Earth orbits the Sun",
      "domain": "astronomy",
      "verification_level": "high"
    },
    "id": 2
  }'
```

**Content Quality Scoring:**
```bash
curl -X POST https://your-app.herokuapp.com/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "score_quality",
    "params": {
      "content": "The old lighthouse stood sentinel on the rocky cliff...",
      "content_type": "creative_writing",
      "evaluation_dimensions": ["creativity", "imagery", "flow"]
    },
    "id": 3
  }'
```

**Relevance Assessment:**
```bash
curl -X POST https://your-app.herokuapp.com/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "assess_relevance",
    "params": {
      "query": "How to bake a cake?",
      "response": "To bake a cake, mix flour, sugar, eggs...",
      "context": "cooking"
    },
    "id": 4
  }'
```

**Response Comparison:**
```bash
curl -X POST https://your-app.herokuapp.com/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "compare_responses",
    "params": {
      "prompt": "Explain photosynthesis",
      "responses": [
        "Photosynthesis is how plants make food using sunlight.",
        "Photosynthesis is the process by which plants convert light energy into chemical energy..."
      ],
      "comparison_criteria": ["accuracy", "detail", "scientific_rigor"]
    },
    "id": 5
  }'
```

## Project Structure

```
├── src/main/java/io/a2a/examples/llmjudge/
│   ├── AgentCardResource.java    # Agent discovery endpoints
│   ├── JsonRpcResource.java      # JSON-RPC communication with evaluation methods
│   └── RootResource.java         # Root endpoint
├── src/main/resources/
│   └── application.properties    # Quarkus configuration
├── test-llm-judge.sh             # Bash test script
├── test_llm_judge.py             # Python test script
├── LLM_JUDGE_TESTING.md          # Comprehensive testing documentation
├── pom.xml                       # Maven configuration
├── procfile                      # Heroku process definition
├── system.properties             # Java version specification
└── app.json                      # Heroku app configuration
```

## Technology Stack

- **Java 17** - Programming language
- **Quarkus 3.2.9** - Java framework
- **Maven** - Build tool
- **Jakarta REST** - REST API framework
- **JSON-RPC 2.0** - Agent communication protocol
- **A2A Java SDK 0.3.0.Alpha1** - Official A2A protocol implementation

## Mock Evaluation Features

The agent provides realistic mock responses for:

- **Response Quality Evaluation**: Scores responses on accuracy, clarity, relevance, completeness
- **Content Quality Scoring**: Evaluates creative writing, technical docs, and other content types
- **Factual Accuracy Checking**: Verifies claims across scientific, historical, and other domains
- **Relevance Assessment**: Determines how well responses match queries
- **Response Comparison**: Ranks multiple responses to the same prompt

All evaluations include:
- Numerical scores (0-10 scale)
- Detailed feedback and analysis
- Strengths and areas for improvement
- Confidence levels and verification status
- Supporting evidence and caveats

## Future Enhancements

This agent is designed to be easily extended with real LLM integration:

1. **Real LLM API Integration**: Replace mock responses with actual LLM calls
2. **Custom Evaluation Criteria**: Add domain-specific evaluation metrics
3. **Batch Processing**: Support for evaluating multiple items at once
4. **Evaluation History**: Track and store evaluation results
5. **Custom Models**: Support for different LLM models and configurations

## License

This project is provided as a demo/template for A2A agent development with LLM-as-a-judge capabilities.
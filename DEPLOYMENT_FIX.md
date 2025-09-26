# Deployment Fix Guide

## Issue: App Crashes on Heroku

The app was crashing due to empty string default values in `@ConfigProperty` annotations. Quarkus doesn't like empty string defaults.

## ✅ Fixed

I've changed empty string defaults to meaningful values in both `LLMService.java` and `SecurityFilter.java`:

```java
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

// And in SecurityFilter.java:
@ConfigProperty(name = "agent.api.key", defaultValue = "not-set")
String agentApiKey;
```

## 🚀 Deploy the Fix

### Option 1: Quick Deploy
```bash
# Deploy the fixed version
git add .
git commit -m "Fix empty string config defaults - use 'not-set' instead"
git push heroku main
```

### Option 2: Use Deployment Script
```bash
# Use the automated deployment script
./deploy-to-heroku.sh
```

## ✅ Verify the Fix

After deployment, test the agent:

```bash
# 1. Check if app is running
curl https://your-app.herokuapp.com/agent/health

# 2. Check agent discovery
curl https://your-app.herokuapp.com/agent

# 3. Check evaluation mode
curl -X POST https://your-app.herokuapp.com/jsonrpc \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc": "2.0", "method": "agent.status", "params": {}, "id": 1}'
```

## Expected Results

- ✅ App should start without crashing
- ✅ Health endpoint should return: `{"status": "UP", "platform": "Heroku", "timestamp": ...}`
- ✅ Agent discovery should return full agent card
- ✅ Status should show: `"evaluationMode": "MOCK"` (unless LLM_API_KEY is set)

## Troubleshooting

If the app still crashes:

1. **Check logs:**
   ```bash
   heroku logs --tail
   ```

2. **Check configuration:**
   ```bash
   heroku config
   ```

3. **Restart the app:**
   ```bash
   heroku restart
   ```

4. **Scale up if needed:**
   ```bash
   heroku ps:scale web=1
   ```

## Configuration Options

Once the app is running, you can optionally configure:

```bash
# Enable LLM mode (requires OpenAI API key)
heroku config:set LLM_API_KEY=sk-your-openai-key-here

# Enable security (requires API key)
heroku config:set AGENT_API_KEY=your-secure-agent-key

# Set rate limits
heroku config:set RATE_LIMIT_REQUESTS=100
heroku config:set RATE_LIMIT_WINDOW_MINUTES=60
```

The app will work in MOCK mode by default (no configuration needed) and can be upgraded to LLM mode by setting the API key.

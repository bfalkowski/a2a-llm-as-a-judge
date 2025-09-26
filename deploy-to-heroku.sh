#!/bin/bash
# deploy-to-heroku.sh - Deploy LLM-as-a-Judge A2A Agent to Heroku

echo "🚀 Deploying LLM-as-a-Judge A2A Agent to Heroku"
echo "=============================================="

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    echo "❌ Error: Not in a git repository"
    echo "Please run: git init && git add . && git commit -m 'Initial commit'"
    exit 1
fi

# Check if Heroku CLI is installed
if ! command -v heroku &> /dev/null; then
    echo "❌ Error: Heroku CLI not found"
    echo "Please install: https://devcenter.heroku.com/articles/heroku-cli"
    exit 1
fi

# Check if logged into Heroku
if ! heroku auth:whoami &> /dev/null; then
    echo "❌ Error: Not logged into Heroku"
    echo "Please run: heroku login"
    exit 1
fi

# Build the project
echo "📦 Building project..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

# Check if Heroku app exists
APP_NAME="a2a-llm-as-a-judge-$(date +%s)"
if heroku apps:info $APP_NAME &> /dev/null; then
    echo "ℹ️  Using existing app: $APP_NAME"
else
    echo "🆕 Creating new Heroku app: $APP_NAME"
    heroku create $APP_NAME
    if [ $? -ne 0 ]; then
        echo "❌ Failed to create Heroku app"
        exit 1
    fi
fi

# Set up git remote
echo "🔗 Setting up git remote..."
heroku git:remote -a $APP_NAME

# Deploy
echo "🚀 Deploying to Heroku..."
git add .
git commit -m "Deploy LLM-as-a-Judge A2A Agent" || echo "No changes to commit"
git push heroku main

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Deployment successful!"
    echo "🌐 Your agent is available at: https://$APP_NAME.herokuapp.com"
    echo ""
    echo "📋 Quick test commands:"
    echo "  curl https://$APP_NAME.herokuapp.com/agent/health"
    echo "  curl https://$APP_NAME.herokuapp.com/agent"
    echo ""
    echo "🔧 Optional configuration:"
    echo "  # Enable LLM mode (requires OpenAI API key)"
    echo "  heroku config:set LLM_API_KEY=sk-your-openai-key-here"
    echo ""
    echo "  # Enable security (requires API key)"
    echo "  heroku config:set AGENT_API_KEY=your-secure-agent-key"
    echo ""
    echo "  # Check current mode"
    echo "  curl -X POST https://$APP_NAME.herokuapp.com/jsonrpc \\"
    echo "    -H 'Content-Type: application/json' \\"
    echo "    -d '{\"jsonrpc\": \"2.0\", \"method\": \"agent.status\", \"params\": {}, \"id\": 1}'"
else
    echo "❌ Deployment failed"
    echo "Check logs with: heroku logs --tail"
    exit 1
fi

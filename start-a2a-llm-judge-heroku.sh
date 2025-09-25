#!/bin/bash

# Heroku-compatible A2A LLM-as-a-Judge Server
echo "Starting A2A LLM-as-a-Judge Server for Heroku deployment..."

# Heroku provides PORT environment variable
export PORT=${PORT:-8080}
echo "Using port: $PORT"

# Show Java version
echo "Using Java version:"
java -version

# Build the project
echo "Building A2A LLM-as-a-Judge server for Heroku..."

mvn -B -DskipTests package
if [ $? -ne 0 ]; then
    echo "Failed to build A2A LLM-as-a-Judge server"
    exit 1
fi

echo "Starting Quarkus app on port $PORT..."
exec java $JAVA_OPTS -jar target/quarkus-app/quarkus-run.jar
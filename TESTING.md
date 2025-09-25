# A2A Agent Testing Guide

This document explains what happens when you test an A2A (Agent-to-Agent) HelloWorld agent using the provided test scripts.

## Overview

The test scripts perform a comprehensive evaluation of an A2A agent using native A2A protocol methods via JSON-RPC communication. This ensures the agent is functioning correctly and can communicate with other agents in an A2A ecosystem using the standard A2A protocol.

## A2A Protocol Testing

The updated test script now uses native A2A protocol methods instead of simple HTTP GET requests. This provides a more realistic test of how A2A agents actually communicate in production environments.

## Test Flow Breakdown

### 1. A2A Agent Card Test (`GET /agent`)

**What it tests:** A2A-compliant agent card discovery

**Request:**
```http
GET https://your-agent.herokuapp.com/agent
```

**What happens:**
- The agent returns a properly formatted A2A agent card containing:
  - Agent metadata (name, description, version, URL)
  - Capabilities (streaming, push notifications, etc.)
  - Available skills with examples
  - Protocol version and transport information

**Expected Response:**
```json
{
  "name": "HelloWorld A2A Agent",
  "description": "A simple A2A agent for testing connections on Heroku",
  "url": "https://your-agent.herokuapp.com",
  "version": "1.0.0",
  "capabilities": {
    "streaming": false,
    "pushNotifications": false,
    "stateTransitionHistory": false
  },
  "skills": [
    {
      "id": "greeting",
      "name": "Greeting",
      "description": "Returns a greeting message",
      "tags": ["greeting"],
      "examples": ["hello", "hi"]
    }
  ],
  "protocolVersion": "0.3.0",
  "preferredTransport": "JSONRPC"
}
```

**Why this matters:** This endpoint serves as the "front door" for agent discovery. Other agents can use this to understand what capabilities are available and how to communicate with this agent.

### 2. A2A Protocol Methods (JSON-RPC)

The test script now tests standard A2A protocol methods via JSON-RPC calls:

#### A2A Agent Discovery (`agent.discover`)

**What it tests:** Standard A2A agent discovery protocol

**Request:**
```http
POST https://your-agent.herokuapp.com/jsonrpc
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "method": "agent.discover",
  "params": {},
  "id": 1
}
```

**Expected Response:**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": {
    "name": "HelloWorld A2A Agent",
    "description": "A simple A2A agent for testing connections on Heroku",
    "version": "1.0.0",
    "protocolVersion": "0.3.0",
    "capabilities": {
      "streaming": false,
      "pushNotifications": false,
      "stateTransitionHistory": false
    },
    "skills": [
      {
        "id": "greeting",
        "name": "Greeting",
        "description": "Returns a greeting message"
      },
      {
        "id": "echo",
        "name": "Echo",
        "description": "Echoes back the provided parameters"
      },
      {
        "id": "heroku-info",
        "name": "Heroku Info",
        "description": "Returns Heroku environment information"
      }
    ]
  }
}
```

**Why this matters:** This is the primary A2A discovery method that other agents use to understand this agent's capabilities and available skills.

#### A2A Agent Capabilities (`agent.getCapabilities`)

**What it tests:** Agent capability discovery

**Request:**
```http
POST https://your-agent.herokuapp.com/jsonrpc
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "method": "agent.getCapabilities",
  "params": {},
  "id": 2
}
```

**Expected Response:**
```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "result": {
    "streaming": false,
    "pushNotifications": false,
    "stateTransitionHistory": false,
    "supportedTransports": ["JSONRPC"],
    "supportedProtocols": ["A2A-0.3.0"]
  }
}
```

**Why this matters:** This method provides detailed capability information that other agents use to understand what this agent can do and how to communicate with it.

#### A2A Agent Skills (`agent.getSkills`)

**What it tests:** Available skills enumeration

**Request:**
```http
POST https://your-agent.herokuapp.com/jsonrpc
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "method": "agent.getSkills",
  "params": {},
  "id": 3
}
```

**Expected Response:**
```json
{
  "jsonrpc": "2.0",
  "id": 3,
  "result": [
    {
      "id": "greeting",
      "name": "Greeting",
      "description": "Returns a greeting message",
      "tags": ["greeting"],
      "examples": ["hello", "hi"]
    },
    {
      "id": "echo",
      "name": "Echo",
      "description": "Echoes back the provided parameters",
      "tags": ["echo"],
      "examples": ["echo test"]
    },
    {
      "id": "heroku-info",
      "name": "Heroku Info",
      "description": "Returns Heroku environment information",
      "tags": ["info", "heroku"],
      "examples": ["info"]
    }
  ]
}
```

**Why this matters:** This method lists all available skills with descriptions, tags, and examples that other agents can use.

#### A2A Agent Health (`agent.health`)

**What it tests:** Health status monitoring

**Request:**
```http
POST https://your-agent.herokuapp.com/jsonrpc
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "method": "agent.health",
  "params": {},
  "id": 4
}
```

**Expected Response:**
```json
{
  "jsonrpc": "2.0",
  "id": 4,
  "result": {
    "status": "UP",
    "platform": "Heroku",
    "timestamp": 1758683847892,
    "version": "1.0.0"
  }
}
```

**Why this matters:** This method provides health status information that other agents use for monitoring and failover decisions.

#### A2A Agent Status (`agent.status`)

**What it tests:** Runtime status information

**Request:**
```http
POST https://your-agent.herokuapp.com/jsonrpc
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "method": "agent.status",
  "params": {},
  "id": 5
}
```

**Expected Response:**
```json
{
  "jsonrpc": "2.0",
  "id": 5,
  "result": {
    "status": "UP",
    "uptime": "running",
    "lastHealthCheck": 1758683848756,
    "activeConnections": 0
  }
}
```

**Why this matters:** This method provides runtime status information including uptime and connection metrics.

### 4. Custom Agent Methods (JSON-RPC)

The test script also tests the agent's custom methods that are actually implemented:

#### Greeting Method (`greeting`)

**What it tests:** Basic agent communication

**Request:**
```http
POST https://your-agent.herokuapp.com/jsonrpc
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "method": "greeting",
  "params": {},
  "id": 6
}
```

**Expected Response:**
```json
{
  "jsonrpc": "2.0",
  "id": 6,
  "result": {
    "message": "Hello from A2A HelloWorld Agent running on Heroku!"
  }
}
```

**Why this matters:** Tests the agent's ability to respond to basic communication requests.

#### Echo Method (`echo`)

**What it tests:** Parameter handling and data processing

**Request:**
```http
POST https://your-agent.herokuapp.com/jsonrpc
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "method": "echo",
  "params": {"message": "Hello from A2A test!"},
  "id": 7
}
```

**Expected Response:**
```json
{
  "jsonrpc": "2.0",
  "id": 7,
  "result": {
    "echo": {
      "message": "Hello from A2A test!"
    }
  }
}
```

**Why this matters:** Tests the agent's ability to process and return structured data.

#### Heroku Info Method (`heroku-info`)

**What it tests:** Environment-specific information retrieval

**Request:**
```http
POST https://your-agent.herokuapp.com/jsonrpc
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "method": "heroku-info",
  "params": {},
  "id": 8
}
```

**Expected Response:**
```json
{
  "jsonrpc": "2.0",
  "id": 8,
  "result": {
    "heroku_environment": {
      "port": "12158",
      "dyno": "web.1",
      "release_version": null,
      "slug_commit": null
    }
  }
}
```

**Why this matters:** Tests the agent's ability to provide environment-specific information, useful for debugging and monitoring.

## Test Results Interpretation

### Success Indicators
- ✅ A2A agent card returns proper schema with all required fields
- ✅ All standard A2A protocol methods return proper responses with detailed information
- ✅ Custom JSON-RPC methods work correctly
- ✅ Agent card contains proper capabilities, skills, and protocol information
- ✅ Agent discovery, capabilities, skills, health, and status methods all functional

### Expected Behavior
- **A2A Protocol Methods**: All return successful responses with detailed agent information
- **Custom Methods**: Should return successful responses with proper data
- **Agent Card**: Must contain all A2A-required fields for client compatibility
- **Discovery Methods**: Provide comprehensive agent information for other agents

### Common Issues
- ❌ **Connection Refused**: Agent not running or wrong URL
- ❌ **404 Not Found**: Endpoint doesn't exist
- ❌ **500 Internal Error**: Agent has a bug
- ❌ **Invalid JSON**: Response format is incorrect
- ❌ **Schema Mismatch**: Agent card doesn't match A2A specification

### Troubleshooting Steps
1. **Check URL**: Ensure the agent URL is correct and accessible
2. **Verify Deployment**: Confirm the agent is deployed and running
3. **Check Logs**: Look at Heroku logs for error messages
4. **Test Manually**: Use curl to test individual endpoints
5. **Validate Schema**: Ensure agent card matches A2A specification

## Summary

The updated test script provides a comprehensive evaluation of an A2A agent using native A2A protocol methods:

- **A2A Compliance**: Tests proper agent card schema and discovery endpoints
- **Protocol Methods**: Tests standard A2A protocol methods via JSON-RPC
- **Custom Methods**: Tests agent-specific functionality
- **Error Handling**: Verifies proper JSON-RPC error responses
- **Schema Validation**: Ensures compatibility with A2A clients

This approach provides a more realistic test of how A2A agents actually communicate in production environments, ensuring the agent is ready for integration with other A2A-compliant systems.

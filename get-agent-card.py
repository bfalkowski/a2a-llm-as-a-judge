#!/usr/bin/env python3
"""
A2A HelloWorld Agent Card Fetcher
Simple Python script to get agent card information
"""

import requests
import json
import sys

def get_agent_card(base_url):
    """Get the agent card from the deployed A2A agent"""
    try:
        response = requests.get(f"{base_url}/agent/card", timeout=10)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"❌ Error fetching agent card: {e}")
        return None

def get_agent_health(base_url):
    """Get the agent health status"""
    try:
        response = requests.get(f"{base_url}/agent/health", timeout=10)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"❌ Error fetching agent health: {e}")
        return None

def call_jsonrpc_method(method, base_url, params=None):
    """Call a JSON-RPC method on the agent"""
    try:
        payload = {
            "jsonrpc": "2.0",
            "method": method,
            "params": params or {},
            "id": 1
        }
        response = requests.post(
            f"{base_url}/jsonrpc",
            json=payload,
            headers={"Content-Type": "application/json"},
            timeout=10
        )
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"❌ Error calling JSON-RPC method '{method}': {e}")
        return None

def main():
    # Check if URL is provided
    if len(sys.argv) < 2:
        print("❌ Error: Please provide the agent URL as a command line argument")
        print(f"Usage: {sys.argv[0]} <agent-url>")
        print(f"Example: {sys.argv[0]} https://your-agent.herokuapp.com")
        sys.exit(1)
    
    base_url = sys.argv[1]
    
    print("🤖 A2A HelloWorld Agent Card Fetcher")
    print(f"🌐 Testing agent at: {base_url}")
    print("=" * 50)
    
    # Get agent card
    print("📋 Fetching agent card...")
    agent_card = get_agent_card(base_url)
    if agent_card:
        print("✅ Agent Card:")
        print(json.dumps(agent_card, indent=2))
    else:
        print("❌ Failed to get agent card")
        sys.exit(1)
    
    print("\n" + "=" * 50)
    
    # Get health status
    print("💚 Checking agent health...")
    health = get_agent_health(base_url)
    if health:
        print("✅ Health Status:")
        print(json.dumps(health, indent=2))
    
    print("\n" + "=" * 50)
    
    # Test JSON-RPC greeting
    print("👋 Testing JSON-RPC greeting...")
    greeting = call_jsonrpc_method("greeting", base_url)
    if greeting:
        print("✅ Greeting Response:")
        print(json.dumps(greeting, indent=2))
    
    print("\n✅ All tests completed successfully!")

if __name__ == "__main__":
    main()

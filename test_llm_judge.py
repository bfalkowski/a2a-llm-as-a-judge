#!/usr/bin/env python3
# test_llm_judge.py - Python test script for LLM-as-a-Judge A2A Agent

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
            "name": "High-Quality Response Evaluation",
            "method": "evaluate_response",
            "params": {
                "prompt": "Explain quantum computing",
                "response": "Quantum computing uses quantum mechanical phenomena like superposition and entanglement to perform calculations that would be impossible for classical computers. Unlike classical bits that are either 0 or 1, quantum bits (qubits) can exist in multiple states simultaneously, allowing for parallel processing.",
                "criteria": ["accuracy", "clarity", "technical_depth"]
            }
        },
        {
            "name": "Low-Quality Response Evaluation",
            "method": "evaluate_response",
            "params": {
                "prompt": "What is the capital of France?",
                "response": "I think it might be London or maybe Berlin. Not sure though.",
                "criteria": ["accuracy", "confidence", "relevance"]
            }
        },
        {
            "name": "Content Quality Scoring - Creative Writing",
            "method": "score_quality",
            "params": {
                "content": "The old lighthouse stood sentinel on the rocky cliff, its weathered stone walls bearing witness to countless storms. The beacon light, though dimmed by time, still cast its hopeful glow across the churning sea, guiding lost souls home.",
                "content_type": "creative_writing",
                "evaluation_dimensions": ["creativity", "imagery", "flow", "emotional_impact"]
            }
        },
        {
            "name": "Content Quality Scoring - Technical Documentation",
            "method": "score_quality",
            "params": {
                "content": "To install the package, run: npm install my-package. Then import it in your code: const pkg = require('my-package');",
                "content_type": "technical_documentation",
                "evaluation_dimensions": ["clarity", "completeness", "accuracy", "usability"]
            }
        },
        {
            "name": "Factual Accuracy Check - Scientific",
            "method": "check_factual_accuracy",
            "params": {
                "claim": "Water boils at 100 degrees Celsius at sea level under standard atmospheric pressure",
                "domain": "physics",
                "verification_level": "high"
            }
        },
        {
            "name": "Factual Accuracy Check - Historical",
            "method": "check_factual_accuracy",
            "params": {
                "claim": "The American Civil War ended in 1865 with the surrender of General Robert E. Lee at Appomattox Court House",
                "domain": "history",
                "verification_level": "high"
            }
        },
        {
            "name": "Relevance Assessment - On Topic",
            "method": "assess_relevance",
            "params": {
                "query": "How to make coffee?",
                "response": "To make coffee, grind beans, add to filter, pour hot water, and let it brew for 4 minutes.",
                "context": "cooking"
            }
        },
        {
            "name": "Relevance Assessment - Off Topic",
            "method": "assess_relevance",
            "params": {
                "query": "What are the benefits of renewable energy?",
                "response": "I really love pizza, especially with pepperoni and extra cheese. My favorite pizza place is down the street from my house.",
                "context": "environmental_science"
            }
        },
        {
            "name": "Response Comparison",
            "method": "compare_responses",
            "params": {
                "prompt": "Explain photosynthesis",
                "responses": [
                    "Photosynthesis is how plants make food using sunlight.",
                    "Photosynthesis is the process by which plants convert light energy into chemical energy, using carbon dioxide and water to produce glucose and oxygen. This occurs primarily in the chloroplasts of plant cells, specifically in structures called thylakoids where chlorophyll captures light energy."
                ],
                "comparison_criteria": ["accuracy", "detail", "scientific_rigor"]
            }
        },
        {
            "name": "Error Handling - Invalid Input",
            "method": "evaluate_response",
            "params": {
                "prompt": "",
                "response": "",
                "criteria": []
            }
        }
    ]
    
    for i, test_case in enumerate(test_cases, 1):
        print(f"\n{i}. Testing {test_case['name']}...")
        print("-" * 40)
        
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
            
            # Pretty print the result
            print(json.dumps(result, indent=2))
            
        except requests.exceptions.RequestException as e:
            print(f"Request error: {e}")
        except json.JSONDecodeError as e:
            print(f"JSON decode error: {e}")
        except Exception as e:
            print(f"Unexpected error: {e}")
    
    print("\n" + "=" * 50)
    print("Testing complete!")
    print("For more test cases, see LLM_JUDGE_TESTING.md")

def main():
    if len(sys.argv) != 2:
        print("Usage: python3 test_llm_judge.py <agent-url>")
        print("Example: python3 test_llm_judge.py https://your-llm-judge-agent.herokuapp.com")
        sys.exit(1)
    
    agent_url = sys.argv[1]
    if not agent_url.startswith(('http://', 'https://')):
        agent_url = 'https://' + agent_url
    
    test_agent(agent_url)

if __name__ == "__main__":
    main()

from flask import Flask, request, jsonify
from flask_cors import CORS
import os
from dotenv import load_dotenv
import json

from agent import AutobidAgent
from tools import TOOL_FUNCTIONS
from conditions import AVAILABLE_CONDITIONS

load_dotenv()

app = Flask(__name__)
CORS(app)

# Debug: Check if API key is loaded
api_key = os.getenv('GEMINI_API_KEY')
print(f"DEBUG: API key loaded: {api_key[:20] if api_key else 'None'}...")

# Initialize the agent
agent = AutobidAgent(api_key=api_key)

"""
AI Agent Microservice - Clean Architecture

Structure:
- conditions.py: All available condition definitions
- tools.py: Tool functions and their declarations for Gemini
- agent.py: Main AutobidAgent class with function calling
- app.py: Flask REST API endpoints

The agent uses Gemini function calling to:
1. Get available conditions (tool)
2. Extract configuration from conversation
3. Generate confirmation message (tool)
"""

# Kept for backward compatibility (this is overridden by conditions.py import)
AVAILABLE_CONDITIONS = {
    "if_outbid": {
        "description": "Only bid if outbid",
        "type": "boolean",
        "example": True
    },
    "min_increment": {
        "description": "Minimum increment if other conditions don't specify",
        "type": "number",
        "example": 500
    },
    "max_increment": {
        "description": "Never increase more than this",
        "type": "number",
        "example": 5000
    },
    "increment_step_after": {
        "description": "Bid with different steps after certain price",
        "type": "object",
        "example": {"20000": 1000, "50000": 5000}
    },
    "randomize_increment": {
        "description": "Add small random noise to be unpredictable",
        "type": "boolean",
        "example": True
    },
    "avoid_round_numbers": {
        "description": "Don't bid on round numbers (e.g. 10,000)",
        "type": "boolean",
        "example": True
    },
    "increment_percentage_after": {
        "description": "Percentage increase after certain price (like step_after but in %)",
        "type": "object",
        "example": {"20000": 0.05, "50000": 0.10}
    },
    "if_no_activity_for_dd_hh_mm": {
        "description": "If no new bid for dd days hh hours mm minutes, increase",
        "type": "string",
        "example": "2_2_30"
    },
    "if_no_activity_for_hours": {
        "description": "If no activity for X hours, increase",
        "type": "number",
        "example": 2
    },
    "if_no_activity_for_days": {
        "description": "If no activity for X days, increase",
        "type": "number",
        "example": 1
    },
    "near_end_minutes": {
        "description": "Activate if less than X minutes remaining",
        "type": "number",
        "example": 3
    },
    "pause_until": {
        "description": "Pause for a while (e.g. at night)",
        "type": "string",
        "example": "2025-10-06T07:00:00"
    },
    "active_hours": {
        "description": "Only work during certain hours (list of hours)",
        "type": "array",
        "example": [9, 17]
    },
    "last_minute_rush": {
        "description": "If less than 1 minute left, bid more aggressively",
        "type": "boolean",
        "example": True
    },
    "only_if_price_below": {
        "description": "Only if current price is less than...",
        "type": "number",
        "example": 100000
    },
    "only_if_price_above": {
        "description": "Only if reached a minimum",
        "type": "number",
        "example": 2000
    },
    "max_total_bids": {
        "description": "Max how many bids to place in total",
        "type": "number",
        "example": 10
    },
    "increment_relative_to_price": {
        "description": "Increase as percentage of current price (e.g. 0.05 = +5%)",
        "type": "number",
        "example": 0.05
    },
    "price_ratio_to_value": {
        "description": "Don't bid if price exceeds minBid * X",
        "type": "number",
        "example": 1.5
    },
    "avoid_user_ids": {
        "description": "Don't bid against these (e.g. friends)",
        "type": "array",
        "example": [2, 7, 19]
    },
    "target_user_ids": {
        "description": "Only compete with these (e.g. rival)",
        "type": "array",
        "example": [15, 18]
    },
    "react_delay_seconds": {
        "description": "Don't bid immediately, wait X seconds",
        "type": "number",
        "example": 15
    },
    "counter_bid_factor": {
        "description": "If someone raised 1000, you raise 1.2x",
        "type": "number",
        "example": 1.2
    },
    "notify_on_action": {
        "description": "Notify user after every action",
        "type": "boolean",
        "example": True
    }
}

def build_conditions_prompt():
    """Build a detailed prompt with all available conditions"""
    conditions_text = "\n\nAvailable condition options for conditionsJson:\n"
    for key, info in AVAILABLE_CONDITIONS.items():
        conditions_text += f"- {key}: {info['description']} (type: {info['type']}, example: {json.dumps(info['example'])})\n"
    return conditions_text

SYSTEM_PROMPT = """You are an assistant that extracts autobid configuration from a conversation.
Based on the user's messages, extract the following information:
- auctionId: The ID of the auction (integer)
- maxBidAmount: Maximum amount the user wants to bid (decimal, can be null)
- incrementAmount: Amount to increment each bid (decimal, can be null)
- intervalMinutes: How often to check and bid in minutes (integer, can be null)
- isActive: Whether the autobid should be active (boolean, default true)
- conditionsJson: Additional conditions as a JSON object (can be null)
""" + build_conditions_prompt() + """

Respond ONLY with a valid JSON object matching this structure:
{
    "id": 0,
    "auctionId": <integer>,
    "userId": 0,
    "maxBidAmount": <number or null>,
    "incrementAmount": <number or null>,
    "intervalMinutes": <integer or null>,
    "isActive": <boolean>,
    "conditionsJson": <object or null>
}

The id and userId fields should always be 0 (they will be set by the backend).
Extract any mentioned conditions into the conditionsJson field using the keys listed above.
"""

@app.route('/agent/process', methods=['POST'])
def process_chat():
    """
    Main endpoint: Process chat conversation and extract autobid configuration.
    
    The agent will:
    1. First call get_available_conditions tool
    2. Analyze the conversation
    3. Extract configuration
    4. Optionally generate confirmation
    
    Request body: List of chat messages [{"role": "user", "content": "..."}]
    Returns: Extracted configuration JSON
    """
    try:
        messages = request.json
        
        if not messages or not isinstance(messages, list):
            return jsonify({"error": "Invalid request format. Expected list of messages."}), 400
        
        # Use the agent to process conversation
        # This uses the fallback method for direct extraction
        conversation_text = ""
        for msg in messages:
            role = msg.get('role', 'user')
            content = msg.get('content', '')
            conversation_text += f"{role}: {content}\n"
        
        agent_result = agent.extract_config_from_text(conversation_text)
        
        # Extract parts from agent response
        config = agent_result.get('config', {})
        agent_response = agent_result.get('response', '')
        is_complete = agent_result.get('is_complete', False)
        
        # Set defaults for config
        config.setdefault('id', 0)
        config.setdefault('userId', 0)
        config.setdefault('maxBidAmount', None)
        config.setdefault('incrementAmount', None)
        config.setdefault('intervalMinutes', None)
        config.setdefault('isActive', True)
        config.setdefault('conditionsJson', None)
        
        # Return both config and agent's response
        return jsonify({
            "config": config,
            "agentResponse": agent_response,
            "isComplete": is_complete,
            "needsMoreInfo": not is_complete
        }), 200
        
    except json.JSONDecodeError as e:
        print(f"JSON decode error: {e}")
        return jsonify({"error": "LLM response was not valid JSON"}), 500
    except Exception as e:
        print(f"Error processing chat: {str(e)}")
        return jsonify({"error": f"Internal server error: {str(e)}"}), 500

@app.route('/agent/conditions', methods=['GET'])
def get_conditions():
    """Returns all available condition options"""
    return jsonify({
        "conditions": AVAILABLE_CONDITIONS,
        "description": "List of available conditions for autobid configuration"
    }), 200

@app.route('/agent/confirm', methods=['POST'])
def confirm_configuration():
    """
    Tool endpoint: Generate a natural language confirmation message.
    
    Takes an extracted configuration and generates a friendly summary
    for the user to verify.
    
    Request body: The extracted configuration JSON
    Returns: Confirmation message and original configuration
    """
    try:
        config = request.json
        
        if not config:
            return jsonify({"error": "No configuration provided"}), 400
        
        # Use the tool to generate confirmation
        confirmation_message = TOOL_FUNCTIONS["generate_confirmation_message"](config)
        
        return jsonify({
            "confirmationMessage": confirmation_message,
            "configuration": config
        }), 200
        
    except Exception as e:
        print(f"Error generating confirmation: {str(e)}")
        return jsonify({"error": f"Internal server error: {str(e)}"}), 500

@app.route('/agent/test-examples', methods=['POST', 'GET'])
def test_examples():
    """
    Test endpoint: Show random example conditions
    """
    try:
        result = TOOL_FUNCTIONS["show_example_conditions"]()
        return jsonify({"message": result}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/agent/test-check', methods=['POST'])
def test_check():
    """
    Test endpoint: Check if a condition exists
    """
    try:
        data = request.json
        condition_name = data.get('condition_name', '')
        
        if not condition_name:
            return jsonify({"error": "condition_name required"}), 400
        
        result = TOOL_FUNCTIONS["check_condition_availability"](condition_name)
        return jsonify({"message": result}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/agent/reset', methods=['POST'])
def reset_conversation():
    """
    Reset the conversation history.
    Call this when user starts a new autobid configuration.
    """
    global agent
    try:
        # Reinitialize the agent to clear any chat history
        api_key = os.getenv('GEMINI_API_KEY')
        agent = AutobidAgent(api_key=api_key)
        
        return jsonify({
            "status": "success",
            "message": "Conversation history cleared"
        }), 200
    except Exception as e:
        print(f"Error resetting conversation: {str(e)}")
        return jsonify({"error": f"Failed to reset: {str(e)}"}), 500


@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({
        "status": "healthy",
        "service": "AI Agent",
        "version": "2.0",
        "architecture": "modular with 4 tools",
        "tools_available": list(TOOL_FUNCTIONS.keys())
    }), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5002, debug=True)

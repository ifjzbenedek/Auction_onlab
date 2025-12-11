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

api_key = os.getenv('GEMINI_API_KEY')
agent = AutobidAgent(api_key=api_key)


@app.route('/agent/process', methods=['POST'])
def process_chat():
    try:
        messages = request.json
        
        if not messages or not isinstance(messages, list):
            return jsonify({"error": "Invalid request format. Expected list of messages."}), 400
        
        # Use the agent to process the conversation
        result = agent.process_message(messages)
        
        return jsonify(result), 200
        
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


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5002, debug=True)

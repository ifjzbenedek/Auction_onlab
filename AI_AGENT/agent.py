import google.generativeai as genai
from tools import TOOLS, TOOL_FUNCTIONS
from conditions import AVAILABLE_CONDITIONS
import json

class AutobidAgent:
    """
    AI Agent that extracts autobid configuration from natural language conversation.
    Uses Gemini with function calling to interact with tools.
    """
    
    def __init__(self, api_key: str):
        genai.configure(api_key=api_key)
        self.model = genai.GenerativeModel(
            'gemini-2.0-flash-exp',
            tools=[self._build_tool_declarations()]
        )
        self.chat = None
        
    def _build_tool_declarations(self):
        """Build tool function declarations for Gemini"""
        return [
            {
                "name": "get_available_conditions",
                "description": "INTERNAL USE - Gets ALL available conditions. Use this IMMEDIATELY at the start of conversation to know what conditions exist. User does NOT see this.",
                "parameters": {
                    "type": "object",
                    "properties": {}
                }
            },
            {
                "name": "show_example_conditions",
                "description": "Shows 6-8 RANDOM examples to the user in a friendly format. Use when: 1) User asks for examples, 2) User doesn't know what they can configure, 3) Conversation gets stuck. User SEES this.",
                "parameters": {
                    "type": "object",
                    "properties": {}
                }
            },
            {
                "name": "check_condition_availability",
                "description": "Checks if a SPECIFIC condition exists. Use when user requests a SPECIFIC feature and you're not sure if it's available. Reports back if it doesn't exist.",
                "parameters": {
                    "type": "object",
                    "properties": {
                        "condition_name": {
                            "type": "string",
                            "description": "The condition name to check"
                        }
                    },
                    "required": ["condition_name"]
                }
            },
            {
                "name": "generate_confirmation_message",
                "description": "Generates a nice, readable summary of the extracted configuration. Use AFTER you have the complete configuration for user confirmation.",
                "parameters": {
                    "type": "object",
                    "properties": {
                        "config": {
                            "type": "object",
                            "description": "The complete extracted autobid configuration JSON object"
                        }
                    },
                    "required": ["config"]
                }
            }
        ]
    
    def _get_system_instruction(self):
        """Get the system instruction for the agent"""
        return """You are a helpful AI assistant that helps users set up automatic bidding for auctions.

**TOOL USAGE GUIDE:**

1. **get_available_conditions** - INTERNAL use
   - Call this IMMEDIATELY at the start of the conversation!
   - This is your knowledge base - you'll see what conditions exist
   - User does NOT see this tool call

2. **show_example_conditions** - SHOW to user
   - Use when user asks:
     * "What options do I have?"
     * "Show me examples!"
     * "What can I configure?"
     * User doesn't know what to say, conversation stalls
   - Shows random 6-8 examples in a friendly format
   - User SEES this message

3. **check_condition_availability** - CHECK availability
   - Use when user requests a SPECIFIC feature
   - E.g.: "I want it not to bid at night"
   - If it doesn't exist, politely explains + suggests similar options
   - User SEES the response

4. **generate_confirmation_message** - SUMMARY
   - Use AFTER you've extracted the complete configuration
   - Nice, structured summary for the user
   - User SEES and confirms it

**CONVERSATION FLOW:**

1. Call `get_available_conditions` (internal)
2. Greet the user, ask about basics:
   - Which auction? (auctionId)
   - Maximum amount? (maxBidAmount)
   - Bid increment? (incrementAmount)
   - Check frequency? (intervalMinutes)
3. If user doesn't know what to configure → `show_example_conditions`
4. If user requests something specific → `check_condition_availability`
5. Gather information through natural conversation
6. When you have everything → extract JSON configuration
7. Call `generate_confirmation_message`
8. Show to user and ask: "Is everything correct?"

**RULES:**
- Be friendly and natural
- DON'T make up info! If something is missing, ask!
- ONLY use conditions that exist (see: get_available_conditions)
- If uncertain → use the tools!

**JSON FORMAT:**
{
    "id": 0,
    "auctionId": <integer>,
    "userId": 0,
    "maxBidAmount": <number or null>,
    "incrementAmount": <number or null>,
    "intervalMinutes": <integer or null>,
    "isActive": true,
    "conditionsJson": <object or null>
}
"""
    
    def process_conversation(self, messages: list) -> dict:
        """
        Process a conversation and extract autobid configuration.
        
        Args:
            messages: List of chat messages with 'role' and 'content'
            
        Returns:
            dict: Extracted configuration and conversation history
        """
        # Start a new chat session with system instruction
        self.chat = self.model.start_chat(
            history=[],
            enable_automatic_function_calling=True
        )
        
        conversation_history = []
        extracted_config = None
        confirmation_message = None
        
        # Add system instruction as first message
        system_msg = self._get_system_instruction()
        
        # Process each message in the conversation
        for i, msg in enumerate(messages):
            role = msg.get('role', 'user')
            content = msg.get('content', '')
            
            if role == 'user':
                # Send user message
                if i == 0:
                    # First message includes system instruction
                    full_content = f"{system_msg}\n\nUser: {content}"
                else:
                    full_content = content
                
                response = self.chat.send_message(full_content)
                
                conversation_history.append({
                    "role": "user",
                    "content": content
                })
                
                conversation_history.append({
                    "role": "assistant",
                    "content": response.text
                })
                
                # Check if configuration was extracted
                if "{" in response.text and "auctionId" in response.text:
                    try:
                        # Try to extract JSON from response
                        start = response.text.find("{")
                        end = response.text.rfind("}") + 1
                        json_str = response.text[start:end]
                        extracted_config = json.loads(json_str)
                    except:
                        pass
        
        # If we have extracted config but no confirmation, generate one
        if extracted_config and not confirmation_message:
            confirmation_message = TOOL_FUNCTIONS["generate_confirmation_message"](extracted_config)
        
        return {
            "conversation": conversation_history,
            "extracted_config": extracted_config,
            "confirmation_message": confirmation_message,
            "needs_more_info": extracted_config is None
        }
    
    def extract_config_from_text(self, conversation_text: str) -> dict:
        """
        Direct extraction of config from conversation text (fallback method).
        
        Args:
            conversation_text: Full conversation as text
            
        Returns:
            dict: Contains 'config' (partial/complete configuration), 'response' (agent's text response), 'is_complete' (bool)
        """
        # Get available conditions first
        conditions_info = TOOL_FUNCTIONS["get_available_conditions"]()
        
        prompt = f"""You are an AI assistant that extracts autobid configuration from natural language.

Available conditions (IMPORTANT - Use these condition names exactly as shown):
{json.dumps(conditions_info['conditions'], indent=2, ensure_ascii=False)}

Conversation:
{conversation_text}

EXTRACTION RULES:
1. Extract basic fields: auctionId, maxBidAmount, incrementAmount, intervalMinutes
2. Detect conditions from user's natural language:
   - "nappal" / "daytime" / "only during day" → active_hours: [9,10,11,12,13,14,15,16,17,18]
   - "csak ha licitáltak" / "only if outbid" → if_outbid: true
   - "minimum X lépés" / "minimum X increment" → min_increment: X
   - "maximum Y lépés" / "maximum Y increment" → max_increment: Y
   - "csak X perc alatt" / "only in last X minutes" → near_end_minutes: X
   - Look for other patterns matching the available conditions

3. Put extracted conditions in "conditionsJson" object with exact condition names

Required fields (all must be present for is_complete=true):
- auctionId (which auction)
- maxBidAmount (maximum bid amount) 
- startingBidAmount (initial bid amount, where to start bidding from)
- incrementAmount (how much to increase each bid)
- intervalMinutes (how often to check)

Respond with a JSON object:
{{
  "config": {{
    "id": 0,
    "auctionId": <number or null>,
    "userId": 0,
    "maxBidAmount": <number or null>,
    "startingBidAmount": <number or null>,
    "incrementAmount": <number or null>,
    "intervalMinutes": <number or null>,
    "isActive": true,
    "conditionsJson": {{<condition_name>: <value>, ...}} or null
  }},
  "response": "<friendly response in Hungarian>",
  "is_complete": <true or false>
}}

Example:
User says: "Maximum 600000ért szeretnék csak nappal mindig, Emeld percennként 150000-ről percenként 1000-rel."
Extract:
- maxBidAmount: 600000
- startingBidAmount: 150000 (kezdőérték - "150000-ről")
- incrementAmount: 1000 (emelés mértéke - "1000-rel")
- intervalMinutes: 1 (percenként = every minute)
- conditionsJson: {{"active_hours": [9,10,11,12,13,14,15,16,17,18]}}
- is_complete: false (missing auctionId)"""

        generation_config = {
            "temperature": 0.3
        }
        
        # Use the same model instance (no tools needed for direct extraction)
        response = self.model.generate_content(
            prompt,
            generation_config=generation_config
        )
        
        print(f"DEBUG: Gemini response: {response.text[:200]}")
        
        # Clean response text (remove markdown code blocks if present)
        response_text = response.text.strip()
        if response_text.startswith("```json"):
            response_text = response_text[7:]  # Remove ```json
        if response_text.startswith("```"):
            response_text = response_text[3:]  # Remove ```
        if response_text.endswith("```"):
            response_text = response_text[:-3]  # Remove trailing ```
        response_text = response_text.strip()
        
        return json.loads(response_text)

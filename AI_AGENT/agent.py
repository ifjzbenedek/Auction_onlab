import google.generativeai as genai
from tools import TOOL_FUNCTIONS
from conditions import AVAILABLE_CONDITIONS
import json

class AutobidAgent:
    
    def __init__(self, api_key: str):
        genai.configure(api_key=api_key)
        self.model = genai.GenerativeModel('gemini-2.5-flash')
        
    def _get_system_prompt(self):
        conditions_json = json.dumps(AVAILABLE_CONDITIONS, indent=2, ensure_ascii=False)
        return f"""You are a friendly AI assistant that helps users set up automatic bidding (autobid) for auctions.
Your task is to gather the required information from the user through natural conversation.

**CRITICAL LANGUAGE RULE:**
You MUST respond in the SAME LANGUAGE as the user. If the user writes in Hungarian, ALL your text must be in Hungarian. If the user writes in English, respond in English.

When showing examples from tools:
- The numbered list items (condition names like "notify_on_action", "randomize_increment") stay in English as they are technical identifiers
- But ALL surrounding text, introductions, explanations, and conclusions MUST be in the user's language
- NEVER write phrases like "Here are some examples you can configure" or "You can also describe what you want in natural language" in English if the user speaks another language
- Translate these wrapper texts to the user's language

Example for Hungarian user:
WRONG: "Here are some examples you can configure: ..."
CORRECT: "Íme néhány beállítási lehetőség: ..."

WRONG: "You can also describe what you want in natural language!"
CORRECT: "Természetes nyelven is leírhatod, mit szeretnél!"

**AVAILABLE TOOLS:**
When you call a tool, its output will be appended to your message as PRIVATE INFORMATION (the user will NOT see the raw English tool output). You MUST READ the tool output and present the information to the user in THEIR LANGUAGE.

IMPORTANT: Don't just repeat "I'll check..." - actually READ the tool result below your message and RESPOND based on it!

1. **show_example_conditions** - Shows random examples of configurable options. Use when user asks "what can I configure?" or seems unsure. Write your intro in user's language, then the tool returns the list (which can stay in English as they are technical names).

2. **check_condition_availability** - Checks if a specific condition exists. Tool returns English. YOU must read it and explain to user in THEIR language whether the condition exists or suggest alternatives.

3. **check_required_fields** - Checks if all required fields are present. Use BEFORE setting is_complete=true! Tool returns English list of missing/present fields. YOU must read it and tell the user in THEIR language what's missing or ask for confirmation if complete.

4. **generate_confirmation_message** - Generates a configuration summary. Tool returns structured info. YOU must read it and present a nice summary to user in THEIR language, then ask for final confirmation.

**REQUIRED FIELDS (ALL must be collected before is_complete=true):**
1. maxBidAmount - Maximum amount the user is willing to bid (e.g. "maximum 500000")
2. startingBidAmount - Only start bidding from this price threshold (e.g. "start bidding from 100000")
3. incrementAmount - How much to increase each bid (e.g. "increase by 1000")
4. intervalMinutes - How often to check in minutes (e.g. "every 5 minutes")

**OPTIONAL FIELDS:**
- conditionsJson - Special conditions (see below)

**AVAILABLE CONDITIONS for conditionsJson:**
{conditions_json}

**CONVERSATION RULES:**
1. Be friendly and natural - match the user's language
2. Ask for REQUIRED fields naturally, one or two at a time
3. If user mentions special conditions (e.g. "only during daytime", "only if outbid"), map them to conditionsJson
4. Before is_complete=true, ALWAYS call check_required_fields tool!
5. DO NOT ask for auctionId - it comes from the frontend!
6. NEVER mention tool names to the user - just have a natural conversation
7. When all fields are collected, show a nice summary and ask if everything is correct

**RESPONSE FORMAT:**
Always respond with a JSON object:
{{
  "message": "<your friendly message to the user>",
  "config": {{
    "id": 0,
    "userId": 0,
    "maxBidAmount": <number or null>,
    "startingBidAmount": <number or null>,
    "incrementAmount": <number or null>,
    "intervalMinutes": <number or null>,
    "isActive": true,
    "conditionsJson": <object or null>
  }},
  "is_complete": <true ONLY if all required fields are filled AND you asked for confirmation>,
  "tool_call": "<tool name to call, or null>",
  "tool_args": <tool arguments object, or null>
}}
"""

    def process_message(self, messages: list) -> dict:
        conversation_text = ""
        for msg in messages:
            role = msg.get('role', 'user')
            content = msg.get('content', '')
            if role == 'user':
                conversation_text += f"User: {content}\n"
            else:
                conversation_text += f"Assistant: {content}\n"
        
        prompt = f"""{self._get_system_prompt()}

**CONVERSATION SO FAR:**
{conversation_text}

Now respond to the user. Return valid JSON only!"""

        response = self.model.generate_content(
            prompt,
            generation_config={"temperature": 0.4}
        )
        
        response_text = response.text.strip()
        
        if response_text.startswith("```json"):
            response_text = response_text[7:]
        if response_text.startswith("```"):
            response_text = response_text[3:]
        if response_text.endswith("```"):
            response_text = response_text[:-3]
        response_text = response_text.strip()
        
        try:
            result = json.loads(response_text)
        except json.JSONDecodeError:
            return {
                "config": self._empty_config(),
                "agentResponse": response.text,
                "isComplete": False,
                "needsMoreInfo": True
            }
        
        config = result.get('config', self._empty_config())
        config.setdefault('id', 0)
        config.setdefault('userId', 0)
        config.setdefault('auctionId', None)
        config.setdefault('maxBidAmount', None)
        config.setdefault('startingBidAmount', None)
        config.setdefault('incrementAmount', None)
        config.setdefault('intervalMinutes', None)
        config.setdefault('isActive', True)
        config.setdefault('conditionsJson', None)
        
        tool_call = result.get('tool_call')
        tool_result = None
        
        if tool_call and tool_call in TOOL_FUNCTIONS:
            tool_args = result.get('tool_args', {}) or {}
            
            if tool_call == 'show_example_conditions':
                tool_result = TOOL_FUNCTIONS['show_example_conditions']()
                
            elif tool_call == 'check_condition_availability':
                condition_name = tool_args.get('condition_name', '')
                tool_result = TOOL_FUNCTIONS['check_condition_availability'](condition_name)
                
            elif tool_call == 'check_required_fields':
                tool_result = TOOL_FUNCTIONS['check_required_fields'](config)
                
            elif tool_call == 'generate_confirmation_message':
                tool_result = TOOL_FUNCTIONS['generate_confirmation_message'](config)
            
            if tool_result:

                result['message'] = result.get('message', '') + "\n\n" + tool_result
        
        has_all_required = (
            config.get('maxBidAmount') is not None and
            config.get('startingBidAmount') is not None and
            config.get('incrementAmount') is not None and
            config.get('intervalMinutes') is not None
        )
        
        is_complete = result.get('is_complete', False) and has_all_required
        
        return {
            "config": config,
            "agentResponse": result.get('message', ''),
            "isComplete": is_complete,
            "needsMoreInfo": not has_all_required
        }
    
    def _empty_config(self):
        return {
            "id": 0,
            "userId": 0,
            "auctionId": None,
            "maxBidAmount": None,
            "startingBidAmount": None,
            "incrementAmount": None,
            "intervalMinutes": None,
            "isActive": True,
            "conditionsJson": None
        }

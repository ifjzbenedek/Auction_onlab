import json
import random
from conditions import AVAILABLE_CONDITIONS

def get_available_conditions():
    """
    Tool: Get all available condition options for autobid configuration.
    INTERNAL USE ONLY - The agent uses this to know what conditions exist.
    
    Returns:
        dict: All available conditions with descriptions, types, and examples
    """
    return {
        "conditions": AVAILABLE_CONDITIONS,
        "total_conditions": len(AVAILABLE_CONDITIONS),
        "categories": {
            "timing": ["if_no_activity_for_hours", "if_no_activity_for_days", "near_end_minutes", "last_minute_rush", "pause_until", "active_hours", "react_delay_seconds"],
            "bidding_strategy": ["min_increment", "max_increment", "increment_step_after", "increment_percentage_after", "increment_relative_to_price", "counter_bid_factor"],
            "randomness": ["randomize_increment", "avoid_round_numbers"],
            "conditions": ["if_outbid", "only_if_price_below", "only_if_price_above", "price_ratio_to_value"],
            "limits": ["max_total_bids"],
            "user_based": ["avoid_user_ids", "target_user_ids"],
            "notifications": ["notify_on_action"]
        }
    }

def show_example_conditions() -> str:
    """
    Tool: Show random 6-8 example conditions to the user with friendly explanations.
    USE THIS when the user asks for examples, or doesn't know what options are available.
    
    Returns:
        str: User-friendly message with 6-8 random examples
    """
    # Select 6-8 random conditions
    num_examples = random.randint(6, 8)
    all_condition_keys = list(AVAILABLE_CONDITIONS.keys())
    selected_keys = random.sample(all_condition_keys, min(num_examples, len(all_condition_keys)))
    
    message = "Here are some examples of what you can configure:\n\n"
    
    for i, key in enumerate(selected_keys, 1):
        condition = AVAILABLE_CONDITIONS[key]
        example_text = json.dumps(condition['example'], ensure_ascii=False)
        
        message += f"{i}. {key}\n"
        message += f"   Description: {condition['description']}\n"
        message += f"   Example: {example_text}\n\n"
    
    message += "\nYou can describe what you want in natural language! For example:\n"
    message += '   "Increase by 5000 above 50000"\n'
    message += '   "Be more aggressive in the last 2 minutes"\n'
    message += '   "Avoid round numbers"\n'
    
    return message

def check_condition_availability(condition_name: str) -> str:
    """
    Tool: Check if a specific condition exists in the system.
    USE THIS when the user mentions a specific feature/condition and you're not sure if it exists.
    
    Args:
        condition_name: The name of the condition to check
        
    Returns:
        str: Message indicating if the condition exists or not
    """
    if condition_name in AVAILABLE_CONDITIONS:
        condition = AVAILABLE_CONDITIONS[condition_name]
        return f"Yes, {condition_name} is available!\n\n{condition['description']}\n\nExample: {json.dumps(condition['example'], ensure_ascii=False)}"
    else:
        # Find similar conditions (simple fuzzy matching)
        similar = []
        condition_name_lower = condition_name.lower()
        
        for key in AVAILABLE_CONDITIONS.keys():
            if condition_name_lower in key.lower() or key.lower() in condition_name_lower:
                similar.append(key)
        
        message = f"Unfortunately, the {condition_name} option is not currently available.\n\n"
        
        if similar:
            message += "Perhaps you meant one of these?\n"
            for s in similar[:3]:  # Max 3 similar
                message += f"   - {s}: {AVAILABLE_CONDITIONS[s]['description']}\n"
        else:
            message += "Check available options using the 'show_example_conditions' tool!"
        
        return message

def generate_confirmation_message(config: dict) -> str:
    """
    Tool: Generate a natural language confirmation message for the user.
    
    Args:
        config (dict): The extracted autobid configuration
        
    Returns:
        str: A friendly confirmation message
    """
    auction_id = config.get('auctionId', 'N/A')
    max_bid = config.get('maxBidAmount')
    increment = config.get('incrementAmount')
    interval = config.get('intervalMinutes')
    is_active = config.get('isActive', True)
    conditions = config.get('conditionsJson', {})
    
    message_parts = []
    
    # Basic settings
    message_parts.append(f"Autobid configuration set for auction #{auction_id}!")
    
    if max_bid:
        message_parts.append(f"Maximum bid: {max_bid:,.0f} HUF")
    
    if increment:
        message_parts.append(f"Default increment: {increment:,.0f} HUF")
    
    if interval:
        if interval >= 60:
            hours = interval // 60
            message_parts.append(f"Check interval: every {hours} hour(s)")
        else:
            message_parts.append(f"Check interval: every {interval} minute(s)")
    
    # Advanced conditions
    if conditions:
        message_parts.append("\nSpecial conditions:")
        
        if conditions.get('if_outbid'):
            message_parts.append("   - Only bid if outbid")
        
        if 'increment_step_after' in conditions:
            steps = conditions['increment_step_after']
            for price, inc in steps.items():
                message_parts.append(f"   - Above {price:,} HUF: use {inc:,} HUF steps")
        
        if conditions.get('last_minute_rush'):
            message_parts.append("   - More aggressive bidding in last minute")
        
        if conditions.get('avoid_round_numbers'):
            message_parts.append("   - Avoid round numbers")
        
        if 'near_end_minutes' in conditions:
            mins = conditions['near_end_minutes']
            message_parts.append(f"   - Activate when {mins} minutes remaining")
        
        if 'react_delay_seconds' in conditions:
            delay = conditions['react_delay_seconds']
            message_parts.append(f"   - Wait {delay} seconds before reacting")
        
        if 'only_if_price_below' in conditions:
            max_price = conditions['only_if_price_below']
            message_parts.append(f"   - Only bid if price below {max_price:,} HUF")
        
        if 'max_total_bids' in conditions:
            max_bids = conditions['max_total_bids']
            message_parts.append(f"   - Maximum {max_bids} total bids")
    
    status = "Active" if is_active else "Inactive"
    message_parts.append(f"\nStatus: {status}")
    
    message_parts.append("\nIs everything correct, or would you like to change something?")
    
    return "\n".join(message_parts)


# Tool declarations for Gemini Function Calling
TOOLS = [
    {
        "name": "get_available_conditions",
        "description": "INTERNAL USE - Gets ALL available conditions. Use this IMMEDIATELY at the start of conversation to know what conditions exist. User does NOT see this.",
        "parameters": {
            "type": "object",
            "properties": {},
            "required": []
        }
    },
    {
        "name": "show_example_conditions",
        "description": "Shows 6-8 RANDOM examples to the user in a friendly format. Use when: 1) User asks for examples, 2) User doesn't know what they can configure, 3) Conversation gets stuck. User SEES this.",
        "parameters": {
            "type": "object",
            "properties": {},
            "required": []
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
                    "description": "The condition name to check (e.g. 'increment_step_after', 'last_minute_rush')"
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
                    "description": "The complete extracted autobid configuration JSON object",
                    "required": True
                }
            },
            "required": ["config"]
        }
    }
]

# Mapping function names to actual functions
TOOL_FUNCTIONS = {
    "get_available_conditions": get_available_conditions,
    "show_example_conditions": show_example_conditions,
    "check_condition_availability": check_condition_availability,
    "generate_confirmation_message": generate_confirmation_message
}

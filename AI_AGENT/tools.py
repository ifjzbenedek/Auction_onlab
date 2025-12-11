import json
import random
from conditions import AVAILABLE_CONDITIONS


def show_example_conditions() -> str:
    num_examples = random.randint(6, 8)
    all_condition_keys = list(AVAILABLE_CONDITIONS.keys())
    selected_keys = random.sample(all_condition_keys, min(num_examples, len(all_condition_keys)))
    
    message = ""
    
    for i, key in enumerate(selected_keys, 1):
        condition = AVAILABLE_CONDITIONS[key]
        example_text = json.dumps(condition['example'], ensure_ascii=False)
        
        message += f"{i}. **{key}**\n"
        message += f"   {condition['description']}\n"
        message += f"   Example: {example_text}\n\n"
    
    
    return message


def check_condition_availability(condition_name: str) -> str:
    if condition_name in AVAILABLE_CONDITIONS:
        condition = AVAILABLE_CONDITIONS[condition_name]
        return f"Yes, '{condition_name}' is available!\n\n{condition['description']}\n\nExample: {json.dumps(condition['example'], ensure_ascii=False)}"
    
    similar = []
    condition_name_lower = condition_name.lower()
    
    for key in AVAILABLE_CONDITIONS.keys():
        if condition_name_lower in key.lower() or key.lower() in condition_name_lower:
            similar.append(key)
    
    message = f"The '{condition_name}' option was not found.\n\n"
    
    if similar:
        message += "Similar options available:\n"
        for s in similar[:3]:
            message += f"   - {s}: {AVAILABLE_CONDITIONS[s]['description']}\n"
    else:
        message += "Consider showing examples to the user."
    
    return message


def check_required_fields(config: dict) -> str:
    missing = []
    present = []
    
    if config.get('maxBidAmount') is None:
        missing.append("maxBidAmount (maximum bid amount)")
    else:
        present.append(f"maxBidAmount: {config['maxBidAmount']}")
    
    if config.get('startingBidAmount') is None:
        missing.append("startingBidAmount (price threshold to start bidding)")
    else:
        present.append(f"startingBidAmount: {config['startingBidAmount']}")
    
    if config.get('incrementAmount') is None:
        missing.append("incrementAmount (bid increment)")
    else:
        present.append(f"incrementAmount: {config['incrementAmount']}")
    
    if config.get('intervalMinutes') is None:
        missing.append("intervalMinutes (check frequency in minutes)")
    else:
        present.append(f"intervalMinutes: {config['intervalMinutes']}")
    
    optional_info = []
    if config.get('conditionsJson'):
        optional_info.append(f"conditionsJson: {len(config['conditionsJson'])} conditions")
    
    if not missing:
        message = "ALL REQUIRED FIELDS ARE PRESENT\n\n"
        message += "Current configuration:\n"
        for p in present:
            message += f"   - {p}\n"
        if optional_info:
            message += "\nOptional fields:\n"
            for o in optional_info:
                message += f"   - {o}\n"
        message += "\n-> Ready to show summary to user."
        return message
    else:
        message = f"MISSING FIELDS ({len(missing)}):\n\n"
        for m in missing:
            message += f"   - {m}\n"
        if present:
            message += "\nAlready provided:\n"
            for p in present:
                message += f"   - {p}\n"
        message += "\n-> Continue conversation to collect missing info."
        return message


def generate_confirmation_message(config: dict) -> str:
    max_bid = config.get('maxBidAmount')
    starting_bid = config.get('startingBidAmount')
    increment = config.get('incrementAmount')
    interval = config.get('intervalMinutes')
    is_active = config.get('isActive', True)
    conditions = config.get('conditionsJson', {})
    
    message_parts = []
    message_parts.append("**AUTOBID CONFIGURATION SUMMARY**\n")
    
    message_parts.append("**Basic Settings:**")
    if max_bid:
        message_parts.append(f"   - Maximum bid: {max_bid:,.0f} HUF")
    if increment:
        message_parts.append(f"   - Increment: {increment:,.0f} HUF")
    if interval:
        if interval >= 60:
            hours = interval // 60
            message_parts.append(f"   - Check interval: every {hours} hour(s)")
        else:
            message_parts.append(f"   - Check interval: every {interval} minute(s)")
    
    if starting_bid:
        message_parts.append(f"   - Starting threshold: will bid above {starting_bid:,.0f} HUF")
    
    status = "Active" if is_active else "Inactive"
    message_parts.append(f"\n**Status:** {status}")
    
    return "\n".join(message_parts)


TOOL_FUNCTIONS = {
    "show_example_conditions": show_example_conditions,
    "check_condition_availability": check_condition_availability,
    "check_required_fields": check_required_fields,
    "generate_confirmation_message": generate_confirmation_message
}

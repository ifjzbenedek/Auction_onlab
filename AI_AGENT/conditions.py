# Available condition options for autobid configuration
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
    "react_delay_minutes": {
        "description": "Don't bid immediately, wait X minutes",
        "type": "number",
        "example": 5
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

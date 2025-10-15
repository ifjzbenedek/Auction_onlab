# AI Agent Microservice

Python Flask microservice that uses Google Gemini LLM to extract autobid configuration from natural language conversations.

## 🏗️ Architecture

The project follows clean architecture principles with 4 main modules:

```
AI_AGENT/
├── conditions.py       # 26 available condition definitions
├── tools.py            # 4 tool functions (used by agent)
├── agent.py            # AutobidAgent class (Gemini function calling)
└── app.py              # Flask REST API endpoints
```

### 🛠️ 4 Tools for the Agent

1. **get_available_conditions** (internal) - Agent retrieves all conditions
2. **show_example_conditions** (user-facing) - Random 6-8 examples for the user
3. **check_condition_availability** (user-facing) - Checks if a condition exists
4. **generate_confirmation_message** (user-facing) - Generate a nice summary

## Requirements

- Python 3.8+
- Gemini API key (free version)

## Installation

1. Create a virtual environment:
```bash
python -m venv venv
```

2. Activate the virtual environment:
```bash
# Windows
venv\Scripts\activate

# Linux/Mac
source venv/bin/activate
```

3. Install dependencies:
```bash
pip install -r requirements.txt
```

4. Create a `.env` file and add your Gemini API key:
```bash
GEMINI_API_KEY=your_api_key_here
```

Get Gemini API key: https://makersuite.google.com/app/apikey

## Running

```bash
python app.py
```

The service will be available at `http://localhost:5002`.

## API Endpoints

### GET /agent/conditions
Returns all available condition options that can be used in the `conditionsJson` field.

**Response:**
```json
{
  "conditions": {
    "if_outbid": {
      "description": "Only bid if outbid",
      "type": "boolean",
      "example": true
    },
    "min_increment": {
      "description": "Minimum increment",
      "type": "number",
      "example": 500
    },
    ...
  },
  "description": "List of available conditions for autobid configuration"
}
```

### POST /agent/process
Processes chat messages and extracts autobid configuration. Returns both the configuration and the agent's response.

**Request Body:**
```json
[
  {
    "role": "user",
    "content": "I want to set up automatic bidding for auction #5."
  },
  {
    "role": "assistant",
    "content": "Okay, what's the maximum amount you want to bid?"
  },
  {
    "role": "user",
    "content": "Maximum 50000, and always increase by 500."
  }
]
```

**Response (Complete):**
```json
{
  "config": {
    "id": 0,
    "auctionId": 5,
    "userId": 0,
    "maxBidAmount": 50000,
    "incrementAmount": 500,
    "intervalMinutes": null,
    "isActive": true,
    "conditionsJson": null
  },
  "agentResponse": "I've got the basics: auction #5, max 50000, increment 500. I still need to know how often to check (in minutes). Do you have any other requirements like specific hours, conditions, etc.?",
  "isComplete": false,
  "needsMoreInfo": true
}
```

**Response (Incomplete - Missing Required Info):**
```json
{
  "config": {
    "id": 0,
    "auctionId": null,
    "userId": 0,
    "maxBidAmount": 15000,
    "incrementAmount": null,
    "intervalMinutes": null,
    "isActive": true,
    "conditionsJson": {
      "if_outbid": true,
      "active_hours": [9, 10, 11, 12, 13, 14, 15, 16, 17, 18],
      "notify_on_action": true
    }
  },
  "agentResponse": "Great! I've set up bidding with a maximum of 15000, only during daytime hours (9 AM - 6 PM), only when outbid, with notifications enabled. However, I still need: 1) Which auction number? 2) How much to increase each bid? 3) How often should I check (in minutes)? Do you have any other requirements?",
  "isComplete": false,
  "needsMoreInfo": true
}
```

**Example with complete advanced conditions:**
```json
{
  "config": {
    "id": 0,
    "auctionId": 15,
    "userId": 0,
    "maxBidAmount": 200000,
    "incrementAmount": 1000,
    "intervalMinutes": 30,
    "isActive": true,
    "conditionsJson": {
      "increment_step_after": {"50000": 5000},
      "if_outbid": true,
      "react_delay_seconds": 30,
      "near_end_minutes": 2,
      "last_minute_rush": true,
      "avoid_round_numbers": true
    }
  },
  "agentResponse": "Perfect! Autobid configured for auction #15 with maximum 200000, incrementing by 1000 (but 5000 above 50k), checking every 30 minutes. Special rules: only when outbid, 30 sec delay, aggressive in last 2 minutes, avoiding round numbers. Everything look good?",
  "isComplete": true,
  "needsMoreInfo": false
}
```

### POST /agent/confirm
Generates a natural language confirmation message for the user to verify the extracted configuration.

**Request Body:** (Previously extracted configuration)
```json
{
  "id": 0,
  "auctionId": 5,
  "userId": 0,
  "maxBidAmount": 50000,
  "incrementAmount": 500,
  "intervalMinutes": 30,
  "isActive": true,
  "conditionsJson": {...}
}
```

**Response:**
```json
{
  "confirmationMessage": "Alright, I've set up automatic bidding for auction #5! Maximum 50000, in steps of 500, checking every 30 minutes. Is everything correct, or would you like to change something?",
  "configuration": {...}
}
```

### GET /health
Checks the service status.

**Response:**
```json
{
  "status": "healthy",
  "service": "AI Agent"
}
```

## Available Conditions (conditionsJson)

The detailed behavior of autobid can be controlled in the `conditionsJson` field. The following conditions are available:

### Basic conditions
- **if_outbid**: Only bid if outbid
- **min_increment**: Minimum increment
- **max_increment**: Maximum increment

### Dynamic bidding
- **increment_step_after**: Different steps after price threshold (e.g. `{"50000": 5000}`)
- **increment_percentage_after**: Percentage increase after price threshold (e.g. `{"50000": 0.10}`)
- **increment_relative_to_price**: Always as % of current price (e.g. `0.05`)
- **counter_bid_factor**: Multiple of other's bid (e.g. `1.2`)

### Randomness
- **randomize_increment**: Small random noise for unpredictability
- **avoid_round_numbers**: Don't bid on round numbers

### Timing
- **if_no_activity_for_hours**: If no activity for X hours
- **if_no_activity_for_days**: If no activity for X days
- **if_no_activity_for_dd_hh_mm**: Format: "2_3_30" (2 days, 3 hours, 30 minutes)
- **near_end_minutes**: Activate if X minutes remaining
- **last_minute_rush**: More aggressive in last minute
- **react_delay_seconds**: Wait X seconds before reacting
- **pause_until**: Pause for a while (ISO date)
- **active_hours**: Only work during certain hours (e.g. `[9, 17]`)

### Price limits
- **only_if_price_below**: Only if price < X
- **only_if_price_above**: Only if price > X
- **price_ratio_to_value**: Don't bid if price > minBid * X

### Bid limits
- **max_total_bids**: Maximum number of bids to place

### User-based
- **avoid_user_ids**: Don't bid against these (e.g. `[2, 7]`)
- **target_user_ids**: Only compete with these (e.g. `[15]`)

### Notifications
- **notify_on_action**: Notify after every action

## Technologies Used

- **Flask**: Web framework
- **Google Gemini**: LLM (gemini-2.0-flash model)
- **Flask-CORS**: Cross-origin resource sharing
- **python-dotenv**: Environment variable management

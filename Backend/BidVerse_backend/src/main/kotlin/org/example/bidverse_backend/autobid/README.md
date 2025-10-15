# AutoBid Processing System

Clean architecture for processing AutoBid configurations and executing bids based on conditions.

## 📁 Structure

```
autobid/
├── AutoBidContext.kt                 # Context data for processing
├── AutoBidDecision.kt                # Decision result (PlaceBid/SkipBid/StopAutoBid)
├── AutoBidExecutorService.kt         # Main executor service
├── conditions/                       # Condition handlers (1 file = 1 condition)
│   ├── ConditionHandler.kt                    # Interface for all conditions
│   ├── IfOutbidCondition.kt                   # if_outbid
│   ├── MinIncrementCondition.kt               # min_increment
│   ├── MaxIncrementCondition.kt               # max_increment
│   ├── ActiveHoursCondition.kt                # active_hours
│   ├── NearEndMinutesCondition.kt             # near_end_minutes
│   ├── LastMinuteRushCondition.kt             # last_minute_rush
│   ├── PauseUntilCondition.kt                 # pause_until
│   ├── IfNoActivityForHoursCondition.kt       # if_no_activity_for_hours
│   ├── IfNoActivityForDaysCondition.kt        # if_no_activity_for_days
│   ├── IfNoActivityForDdHhMmCondition.kt      # if_no_activity_for_dd_hh_mm
│   ├── OnlyIfPriceBelowCondition.kt           # only_if_price_below
│   ├── OnlyIfPriceAboveCondition.kt           # only_if_price_above
│   ├── PriceRatioToValueCondition.kt          # price_ratio_to_value
│   ├── MaxTotalBidsCondition.kt               # max_total_bids
│   ├── AvoidUserIdsCondition.kt               # avoid_user_ids
│   ├── TargetUserIdsCondition.kt              # target_user_ids
│   ├── RandomizeIncrementCondition.kt         # randomize_increment
│   ├── AvoidRoundNumbersCondition.kt          # avoid_round_numbers
│   ├── IncrementStepAfterCondition.kt         # increment_step_after
│   ├── IncrementPercentageAfterCondition.kt   # increment_percentage_after
│   ├── IncrementRelativeToPriceCondition.kt   # increment_relative_to_price
│   └── CounterBidFactorCondition.kt           # counter_bid_factor
└── handlers/
    └── AutoBidProcessor.kt          # Processes all conditions and calculates bid
```

## 🔄 How It Works

### 1. **AutoBidContext**
Contains all information needed for decision-making:
- AutoBid configuration
- Auction details
- Current bids
- User information
- Time calculations (minutes until end, etc.)

### 2. **Condition Handlers**
Each condition implements `ConditionHandler` interface:
- `shouldBid()`: Returns true if bidding is allowed
- `modifyBidAmount()`: Optionally modifies the bid amount

**Examples:**
- `IfOutbidCondition`: Only bid if user was outbid
- `ActiveHoursCondition`: Only bid during specific hours
- `LastMinuteRushCondition`: Increase bid amount in last minute

### 3. **AutoBidProcessor**
Coordinates all conditions:
1. Validates basic requirements (auction active, user not winning, etc.)
2. Checks all `shouldBid()` conditions
3. Calculates bid amount using `modifyBidAmount()` from all conditions
4. Returns `AutoBidDecision`

### 4. **AutoBidExecutorService**
Main service that:
- Executes a single autobid
- Places the actual bid
- Updates autobid status
- Handles errors

### 5. **AutoBidDecision**
Three possible outcomes:
- `PlaceBid(amount, reason)`: Bid should be placed
- `SkipBid(reason)`: Don't bid this time
- `StopAutoBid(reason)`: Deactivate autobid permanently

## 🎯 Usage Examples

### Execute a single autobid manually

```http
POST http://localhost:8081/autobid/{autoBidId}/execute
```

**Response (Bid Placed):**
```json
{
  "status": "success",
  "action": "bid_placed",
  "amount": 15500,
  "bidId": 42,
  "reason": "All conditions met"
}
```

**Response (Skipped):**
```json
{
  "status": "skipped",
  "reason": "Condition 'active_hours' not met"
}
```

**Response (Stopped):**
```json
{
  "status": "stopped",
  "reason": "Bid would exceed maximum bid amount (15000)"
}
```

### Execute all due autobids

```http
POST http://localhost:8081/autobid/execute-all
```

**Response:**
```json
{
  "total": 5,
  "executed": 2,
  "skipped": 2,
  "stopped": 1,
  "errors": 0,
  "details": [
    {
      "autoBidId": 1,
      "auctionId": 5,
      "userId": 10,
      "result": "BID_PLACED: 12500"
    },
    {
      "autoBidId": 2,
      "auctionId": 7,
      "userId": 15,
      "result": "SKIPPED: User is already the highest bidder"
    }
  ]
}
```

## 📝 Adding New Conditions

To add a new condition:

1. Create a new file in `conditions/` directory
2. Implement `ConditionHandler` interface
3. Add `@Component` annotation
4. Spring will automatically discover and register it

**Example:**
```kotlin
@Component
class MyNewCondition : ConditionHandler {
    override val conditionName = "my_new_condition"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        // Your logic here
        return true
    }

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        // Optional: modify amount
        return null
    }
}
```

## 🔮 Future: Scheduler Integration

Later, a scheduler will call `AutoBidExecutorService.executeAllDueAutoBids()` periodically:

```kotlin
@Scheduled(fixedRate = 60000) // Every minute
fun scheduledAutoBidExecution() {
    autoBidExecutorService.executeAllDueAutoBids()
}
```

## ✅ Implemented Conditions

### Basic Conditions
| Condition | Type | Description |
|-----------|------|-------------|
| `if_outbid` | boolean | Only bid if outbid |
| `min_increment` | number | Minimum increment |
| `max_increment` | number | Maximum increment |

### Timing Conditions
| Condition | Type | Description |
|-----------|------|-------------|
| `active_hours` | array | Only bid during specific hours |
| `near_end_minutes` | number | Only bid if less than X minutes remaining |
| `last_minute_rush` | boolean | Increase bid by 50% in last minute |
| `pause_until` | string | Pause until specific datetime (ISO format) |
| `if_no_activity_for_hours` | number | Only bid if no activity for X hours |
| `if_no_activity_for_days` | number | Only bid if no activity for X days |
| `if_no_activity_for_dd_hh_mm` | string | No activity for "dd_hh_mm" format (e.g. "2_3_30") |

### Price Conditions
| Condition | Type | Description |
|-----------|------|-------------|
| `only_if_price_below` | number | Only bid if price < X |
| `only_if_price_above` | number | Only bid if price >= X |
| `price_ratio_to_value` | number | Don't bid if price > startingPrice * X |

### Bid Modification
| Condition | Type | Description |
|-----------|------|-------------|
| `increment_step_after` | object | Different increment after price threshold |
| `increment_percentage_after` | object | Percentage increment after threshold |
| `increment_relative_to_price` | number | Increment as % of current price |
| `counter_bid_factor` | number | Match opponent's increment * factor |

### Strategy Conditions
| Condition | Type | Description |
|-----------|------|-------------|
| `randomize_increment` | boolean | Add random noise (-10% to +10%) |
| `avoid_round_numbers` | boolean | Avoid round numbers (add 7, 13, 23, etc.) |
| `max_total_bids` | number | Stop after X bids |

### User-Based Conditions
| Condition | Type | Description |
|-----------|------|-------------|
| `avoid_user_ids` | array | Don't bid against specific users |
| `target_user_ids` | array | Only bid against specific users |

## 🚀 Future Enhancements

Conditions that require additional infrastructure:
- `react_delay_seconds` (requires tracking bid timestamps and delayed execution)
- `notify_on_action` (requires notification system integration)

All core bidding conditions are implemented! ✅

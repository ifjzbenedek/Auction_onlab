package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.threeten.bp.OffsetDateTime;
import org.springframework.validation.annotation.Validated;
import org.openapitools.jackson.nullable.JsonNullable;
import io.swagger.configuration.NotUndefined;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Bid
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2025-02-28T11:41:36.840793882Z[GMT]")


public class Bid   {
  @JsonProperty("id")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long id = null;

  @JsonProperty("itemId")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long itemId = null;

  @JsonProperty("userId")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long userId = null;

  @JsonProperty("value")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Double value = null;

  @JsonProperty("timeStamp")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private OffsetDateTime timeStamp = null;

  @JsonProperty("isWinning")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Boolean isWinning = null;


  public Bid id(Long id) { 

    this.id = id;
    return this;
  }

  /**
   * The unique identifier of Bids
   * @return id
   **/
  
  @Schema(description = "The unique identifier of Bids")
  
  public Long getId() {  
    return id;
  }



  public void setId(Long id) { 
    this.id = id;
  }

  public Bid itemId(Long itemId) { 

    this.itemId = itemId;
    return this;
  }

  /**
   * The id of the Auctionitem
   * @return itemId
   **/
  
  @Schema(description = "The id of the Auctionitem")
  
  public Long getItemId() {  
    return itemId;
  }



  public void setItemId(Long itemId) { 
    this.itemId = itemId;
  }

  public Bid userId(Long userId) { 

    this.userId = userId;
    return this;
  }

  /**
   * The id of the User, who made the Bid
   * @return userId
   **/
  
  @Schema(description = "The id of the User, who made the Bid")
  
  public Long getUserId() {  
    return userId;
  }



  public void setUserId(Long userId) { 
    this.userId = userId;
  }

  public Bid value(Double value) { 

    this.value = value;
    return this;
  }

  /**
   * The money offer in dollars
   * @return value
   **/
  
  @Schema(description = "The money offer in dollars")
  
  public Double getValue() {  
    return value;
  }



  public void setValue(Double value) { 
    this.value = value;
  }

  public Bid timeStamp(OffsetDateTime timeStamp) { 

    this.timeStamp = timeStamp;
    return this;
  }

  /**
   * The time, when the Bid was offered
   * @return timeStamp
   **/
  
  @Schema(description = "The time, when the Bid was offered")
  
@Valid
  public OffsetDateTime getTimeStamp() {  
    return timeStamp;
  }



  public void setTimeStamp(OffsetDateTime timeStamp) { 
    this.timeStamp = timeStamp;
  }

  public Bid isWinning(Boolean isWinning) { 

    this.isWinning = isWinning;
    return this;
  }

  /**
   * True if this Bid is winning the Auction
   * @return isWinning
   **/
  
  @Schema(description = "True if this Bid is winning the Auction")
  
  public Boolean isIsWinning() {  
    return isWinning;
  }



  public void setIsWinning(Boolean isWinning) { 
    this.isWinning = isWinning;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Bid bid = (Bid) o;
    return Objects.equals(this.id, bid.id) &&
        Objects.equals(this.itemId, bid.itemId) &&
        Objects.equals(this.userId, bid.userId) &&
        Objects.equals(this.value, bid.value) &&
        Objects.equals(this.timeStamp, bid.timeStamp) &&
        Objects.equals(this.isWinning, bid.isWinning);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, itemId, userId, value, timeStamp, isWinning);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Bid {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    itemId: ").append(toIndentedString(itemId)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    timeStamp: ").append(toIndentedString(timeStamp)).append("\n");
    sb.append("    isWinning: ").append(toIndentedString(isWinning)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

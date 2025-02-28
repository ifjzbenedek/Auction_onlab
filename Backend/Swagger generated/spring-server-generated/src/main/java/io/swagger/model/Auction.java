package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.Bid;
import io.swagger.model.Watch;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
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
 * Auction
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2025-02-28T11:41:36.840793882Z[GMT]")


public class Auction   {
  @JsonProperty("id")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long id = null;

  @JsonProperty("userId")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long userId = null;

  @JsonProperty("categoryId")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long categoryId = null;

  @JsonProperty("bids")
  @Valid
  private List<Bid> bids = null;
  @JsonProperty("watchedAuctions")
  @Valid
  private List<Watch> watchedAuctions = null;
  @JsonProperty("itemName")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String itemName = null;

  @JsonProperty("minimumPrice")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Double minimumPrice = null;

  @JsonProperty("status")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String status = null;

  @JsonProperty("createDate")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private OffsetDateTime createDate = null;

  @JsonProperty("expiredDate")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private OffsetDateTime expiredDate = null;

  @JsonProperty("lastBid")

  private Double lastBid = null;

  @JsonProperty("description")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String description = null;

  @JsonProperty("type")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String type = null;

  @JsonProperty("extraTime")

  private OffsetDateTime extraTime = null;

  @JsonProperty("itemState")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String itemState = null;

  @JsonProperty("tags")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String tags = null;

  @JsonProperty("minStep")

  private Integer minStep = null;


  public Auction id(Long id) { 

    this.id = id;
    return this;
  }

  /**
   * The unique identifier of Auctions
   * @return id
   **/
  
  @Schema(description = "The unique identifier of Auctions")
  
  public Long getId() {  
    return id;
  }



  public void setId(Long id) { 
    this.id = id;
  }

  public Auction userId(Long userId) { 

    this.userId = userId;
    return this;
  }

  /**
   * The user who created the Auction
   * @return userId
   **/
  
  @Schema(description = "The user who created the Auction")
  
  public Long getUserId() {  
    return userId;
  }



  public void setUserId(Long userId) { 
    this.userId = userId;
  }

  public Auction categoryId(Long categoryId) { 

    this.categoryId = categoryId;
    return this;
  }

  /**
   * The category of the Auction
   * @return categoryId
   **/
  
  @Schema(description = "The category of the Auction")
  
  public Long getCategoryId() {  
    return categoryId;
  }



  public void setCategoryId(Long categoryId) { 
    this.categoryId = categoryId;
  }

  public Auction bids(List<Bid> bids) { 

    this.bids = bids;
    return this;
  }

  public Auction addBidsItem(Bid bidsItem) {
    if (this.bids == null) {
      this.bids = new ArrayList<Bid>();
    }
    this.bids.add(bidsItem);
    return this;
  }

  /**
   * The offered Bids for the Auction
   * @return bids
   **/
  
  @Schema(description = "The offered Bids for the Auction")
  @Valid
  public List<Bid> getBids() {  
    return bids;
  }



  public void setBids(List<Bid> bids) { 
    this.bids = bids;
  }

  public Auction watchedAuctions(List<Watch> watchedAuctions) { 

    this.watchedAuctions = watchedAuctions;
    return this;
  }

  public Auction addWatchedAuctionsItem(Watch watchedAuctionsItem) {
    if (this.watchedAuctions == null) {
      this.watchedAuctions = new ArrayList<Watch>();
    }
    this.watchedAuctions.add(watchedAuctionsItem);
    return this;
  }

  /**
   * List of users watching this Auction
   * @return watchedAuctions
   **/
  
  @Schema(description = "List of users watching this Auction")
  @Valid
  public List<Watch> getWatchedAuctions() {  
    return watchedAuctions;
  }



  public void setWatchedAuctions(List<Watch> watchedAuctions) { 
    this.watchedAuctions = watchedAuctions;
  }

  public Auction itemName(String itemName) { 

    this.itemName = itemName;
    return this;
  }

  /**
   * Name of the Auction item
   * @return itemName
   **/
  
  @Schema(description = "Name of the Auction item")
  
@Size(max=50)   public String getItemName() {  
    return itemName;
  }



  public void setItemName(String itemName) { 
    this.itemName = itemName;
  }

  public Auction minimumPrice(Double minimumPrice) { 

    this.minimumPrice = minimumPrice;
    return this;
  }

  /**
   * Minimum selling price of the item
   * @return minimumPrice
   **/
  
  @Schema(description = "Minimum selling price of the item")
  
  public Double getMinimumPrice() {  
    return minimumPrice;
  }



  public void setMinimumPrice(Double minimumPrice) { 
    this.minimumPrice = minimumPrice;
  }

  public Auction status(String status) { 

    this.status = status;
    return this;
  }

  /**
   * Current status of the Auction (e.g., active, closed)
   * @return status
   **/
  
  @Schema(description = "Current status of the Auction (e.g., active, closed)")
  
@Size(max=20)   public String getStatus() {  
    return status;
  }



  public void setStatus(String status) { 
    this.status = status;
  }

  public Auction createDate(OffsetDateTime createDate) { 

    this.createDate = createDate;
    return this;
  }

  /**
   * Date and time when the Auction was created
   * @return createDate
   **/
  
  @Schema(description = "Date and time when the Auction was created")
  
@Valid
  public OffsetDateTime getCreateDate() {  
    return createDate;
  }



  public void setCreateDate(OffsetDateTime createDate) { 
    this.createDate = createDate;
  }

  public Auction expiredDate(OffsetDateTime expiredDate) { 

    this.expiredDate = expiredDate;
    return this;
  }

  /**
   * Date and time when the Auction expires
   * @return expiredDate
   **/
  
  @Schema(description = "Date and time when the Auction expires")
  
@Valid
  public OffsetDateTime getExpiredDate() {  
    return expiredDate;
  }



  public void setExpiredDate(OffsetDateTime expiredDate) { 
    this.expiredDate = expiredDate;
  }

  public Auction lastBid(Double lastBid) { 

    this.lastBid = lastBid;
    return this;
  }

  /**
   * Latest Bid amount, if any
   * @return lastBid
   **/
  
  @Schema(description = "Latest Bid amount, if any")
  
  public Double getLastBid() {
 
    return lastBid;
  }



  public void setLastBid(Double lastBid) { 
    this.lastBid = lastBid;
  }

  public Auction description(String description) { 

    this.description = description;
    return this;
  }

  /**
   * Detailed description of the item
   * @return description
   **/
  
  @Schema(description = "Detailed description of the item")
  
  public String getDescription() {  
    return description;
  }



  public void setDescription(String description) { 
    this.description = description;
  }

  public Auction type(String type) { 

    this.type = type;
    return this;
  }

  /**
   * Type of the Auction (e.g., timed, restarting)
   * @return type
   **/
  
  @Schema(description = "Type of the Auction (e.g., timed, restarting)")
  
@Size(max=20)   public String getType() {  
    return type;
  }



  public void setType(String type) { 
    this.type = type;
  }

  public Auction extraTime(OffsetDateTime extraTime) { 

    this.extraTime = extraTime;
    return this;
  }

  /**
   * Extra time added to the auction if it's a restarting one
   * @return extraTime
   **/
  
  @Schema(description = "Extra time added to the auction if it's a restarting one")
  
@Valid
  public OffsetDateTime getExtraTime() {
 
    return extraTime;
  }



  public void setExtraTime(OffsetDateTime extraTime) { 
    this.extraTime = extraTime;
  }

  public Auction itemState(String itemState) { 

    this.itemState = itemState;
    return this;
  }

  /**
   * State of the item (e.g., new, used)
   * @return itemState
   **/
  
  @Schema(description = "State of the item (e.g., new, used)")
  
@Size(max=20)   public String getItemState() {  
    return itemState;
  }



  public void setItemState(String itemState) { 
    this.itemState = itemState;
  }

  public Auction tags(String tags) { 

    this.tags = tags;
    return this;
  }

  /**
   * Keywords associated with the Auction item
   * @return tags
   **/
  
  @Schema(description = "Keywords associated with the Auction item")
  
  public String getTags() {  
    return tags;
  }



  public void setTags(String tags) { 
    this.tags = tags;
  }

  public Auction minStep(Integer minStep) { 

    this.minStep = minStep;
    return this;
  }

  /**
   * Minimum increment step for Bids
   * @return minStep
   **/
  
  @Schema(description = "Minimum increment step for Bids")
  
  public Integer getMinStep() {
 
    return minStep;
  }



  public void setMinStep(Integer minStep) { 
    this.minStep = minStep;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Auction auction = (Auction) o;
    return Objects.equals(this.id, auction.id) &&
        Objects.equals(this.userId, auction.userId) &&
        Objects.equals(this.categoryId, auction.categoryId) &&
        Objects.equals(this.bids, auction.bids) &&
        Objects.equals(this.watchedAuctions, auction.watchedAuctions) &&
        Objects.equals(this.itemName, auction.itemName) &&
        Objects.equals(this.minimumPrice, auction.minimumPrice) &&
        Objects.equals(this.status, auction.status) &&
        Objects.equals(this.createDate, auction.createDate) &&
        Objects.equals(this.expiredDate, auction.expiredDate) &&
        Objects.equals(this.lastBid, auction.lastBid) &&
        Objects.equals(this.description, auction.description) &&
        Objects.equals(this.type, auction.type) &&
        Objects.equals(this.extraTime, auction.extraTime) &&
        Objects.equals(this.itemState, auction.itemState) &&
        Objects.equals(this.tags, auction.tags) &&
        Objects.equals(this.minStep, auction.minStep);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, categoryId, bids, watchedAuctions, itemName, minimumPrice, status, createDate, expiredDate, lastBid, description, type, extraTime, itemState, tags, minStep);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Auction {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    categoryId: ").append(toIndentedString(categoryId)).append("\n");
    sb.append("    bids: ").append(toIndentedString(bids)).append("\n");
    sb.append("    watchedAuctions: ").append(toIndentedString(watchedAuctions)).append("\n");
    sb.append("    itemName: ").append(toIndentedString(itemName)).append("\n");
    sb.append("    minimumPrice: ").append(toIndentedString(minimumPrice)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    createDate: ").append(toIndentedString(createDate)).append("\n");
    sb.append("    expiredDate: ").append(toIndentedString(expiredDate)).append("\n");
    sb.append("    lastBid: ").append(toIndentedString(lastBid)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    extraTime: ").append(toIndentedString(extraTime)).append("\n");
    sb.append("    itemState: ").append(toIndentedString(itemState)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    minStep: ").append(toIndentedString(minStep)).append("\n");
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

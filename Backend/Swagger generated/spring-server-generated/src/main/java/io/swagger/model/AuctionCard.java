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
 * AuctionCard
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2025-02-28T11:41:36.840793882Z[GMT]")


public class AuctionCard   {
  @JsonProperty("id")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long id = null;

  @JsonProperty("itemName")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String itemName = null;

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


  public AuctionCard id(Long id) { 

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

  public AuctionCard itemName(String itemName) { 

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

  public AuctionCard createDate(OffsetDateTime createDate) { 

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

  public AuctionCard expiredDate(OffsetDateTime expiredDate) { 

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

  public AuctionCard lastBid(Double lastBid) { 

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

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuctionCard auctionCard = (AuctionCard) o;
    return Objects.equals(this.id, auctionCard.id) &&
        Objects.equals(this.itemName, auctionCard.itemName) &&
        Objects.equals(this.createDate, auctionCard.createDate) &&
        Objects.equals(this.expiredDate, auctionCard.expiredDate) &&
        Objects.equals(this.lastBid, auctionCard.lastBid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, itemName, createDate, expiredDate, lastBid);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuctionCard {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    itemName: ").append(toIndentedString(itemName)).append("\n");
    sb.append("    createDate: ").append(toIndentedString(createDate)).append("\n");
    sb.append("    expiredDate: ").append(toIndentedString(expiredDate)).append("\n");
    sb.append("    lastBid: ").append(toIndentedString(lastBid)).append("\n");
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

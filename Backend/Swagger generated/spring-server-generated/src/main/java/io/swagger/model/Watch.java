package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.WatchId;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import org.openapitools.jackson.nullable.JsonNullable;
import io.swagger.configuration.NotUndefined;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Watch
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2025-02-28T11:41:36.840793882Z[GMT]")


public class Watch   {
  @JsonProperty("id")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private WatchId id = null;

  @JsonProperty("auctionId")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long auctionId = null;

  @JsonProperty("userId")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long userId = null;


  public Watch id(WatchId id) { 

    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   **/
  
  @Schema(description = "")
  
@Valid
  public WatchId getId() {  
    return id;
  }



  public void setId(WatchId id) { 
    this.id = id;
  }

  public Watch auctionId(Long auctionId) { 

    this.auctionId = auctionId;
    return this;
  }

  /**
   * The Auctions that are being watched by the User
   * @return auctionId
   **/
  
  @Schema(description = "The Auctions that are being watched by the User")
  
  public Long getAuctionId() {  
    return auctionId;
  }



  public void setAuctionId(Long auctionId) { 
    this.auctionId = auctionId;
  }

  public Watch userId(Long userId) { 

    this.userId = userId;
    return this;
  }

  /**
   * The Users watching the Auction
   * @return userId
   **/
  
  @Schema(description = "The Users watching the Auction")
  
  public Long getUserId() {  
    return userId;
  }



  public void setUserId(Long userId) { 
    this.userId = userId;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Watch watch = (Watch) o;
    return Objects.equals(this.id, watch.id) &&
        Objects.equals(this.auctionId, watch.auctionId) &&
        Objects.equals(this.userId, watch.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, auctionId, userId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Watch {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    auctionId: ").append(toIndentedString(auctionId)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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

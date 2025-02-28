package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.Auction;
import io.swagger.model.Bid;
import io.swagger.model.Watch;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.openapitools.jackson.nullable.JsonNullable;
import io.swagger.configuration.NotUndefined;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * User
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2025-02-28T11:41:36.840793882Z[GMT]")


public class User   {
  @JsonProperty("id")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long id = null;

  @JsonProperty("auctions")
  @Valid
  private List<Auction> auctions = null;
  @JsonProperty("bids")
  @Valid
  private List<Bid> bids = null;
  @JsonProperty("watches")
  @Valid
  private List<Watch> watches = null;
  @JsonProperty("userName")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String userName = null;

  @JsonProperty("passwordHash")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String passwordHash = null;

  @JsonProperty("emailAddress")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String emailAddress = null;

  @JsonProperty("phoneNumber")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String phoneNumber = null;


  public User id(Long id) { 

    this.id = id;
    return this;
  }

  /**
   * The unique identifier of Users
   * @return id
   **/
  
  @Schema(description = "The unique identifier of Users")
  
  public Long getId() {  
    return id;
  }



  public void setId(Long id) { 
    this.id = id;
  }

  public User auctions(List<Auction> auctions) { 

    this.auctions = auctions;
    return this;
  }

  public User addAuctionsItem(Auction auctionsItem) {
    if (this.auctions == null) {
      this.auctions = new ArrayList<Auction>();
    }
    this.auctions.add(auctionsItem);
    return this;
  }

  /**
   * List of Auctions, the User is taking part in
   * @return auctions
   **/
  
  @Schema(description = "List of Auctions, the User is taking part in")
  @Valid
  public List<Auction> getAuctions() {  
    return auctions;
  }



  public void setAuctions(List<Auction> auctions) { 
    this.auctions = auctions;
  }

  public User bids(List<Bid> bids) { 

    this.bids = bids;
    return this;
  }

  public User addBidsItem(Bid bidsItem) {
    if (this.bids == null) {
      this.bids = new ArrayList<Bid>();
    }
    this.bids.add(bidsItem);
    return this;
  }

  /**
   * List of Bids the User has offered
   * @return bids
   **/
  
  @Schema(description = "List of Bids the User has offered")
  @Valid
  public List<Bid> getBids() {  
    return bids;
  }



  public void setBids(List<Bid> bids) { 
    this.bids = bids;
  }

  public User watches(List<Watch> watches) { 

    this.watches = watches;
    return this;
  }

  public User addWatchesItem(Watch watchesItem) {
    if (this.watches == null) {
      this.watches = new ArrayList<Watch>();
    }
    this.watches.add(watchesItem);
    return this;
  }

  /**
   * List of Auctions watched by the User
   * @return watches
   **/
  
  @Schema(description = "List of Auctions watched by the User")
  @Valid
  public List<Watch> getWatches() {  
    return watches;
  }



  public void setWatches(List<Watch> watches) { 
    this.watches = watches;
  }

  public User userName(String userName) { 

    this.userName = userName;
    return this;
  }

  /**
   * The name of the User
   * @return userName
   **/
  
  @Schema(description = "The name of the User")
  
@Size(max=25)   public String getUserName() {  
    return userName;
  }



  public void setUserName(String userName) { 
    this.userName = userName;
  }

  public User passwordHash(String passwordHash) { 

    this.passwordHash = passwordHash;
    return this;
  }

  /**
   * The password of the User
   * @return passwordHash
   **/
  
  @Schema(description = "The password of the User")
  
@Size(max=255)   public String getPasswordHash() {  
    return passwordHash;
  }



  public void setPasswordHash(String passwordHash) { 
    this.passwordHash = passwordHash;
  }

  public User emailAddress(String emailAddress) { 

    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * The email of the User
   * @return emailAddress
   **/
  
  @Schema(description = "The email of the User")
  
@Size(max=50)   public String getEmailAddress() {  
    return emailAddress;
  }



  public void setEmailAddress(String emailAddress) { 
    this.emailAddress = emailAddress;
  }

  public User phoneNumber(String phoneNumber) { 

    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * The phone number of the User
   * @return phoneNumber
   **/
  
  @Schema(description = "The phone number of the User")
  
@Size(max=16)   public String getPhoneNumber() {  
    return phoneNumber;
  }



  public void setPhoneNumber(String phoneNumber) { 
    this.phoneNumber = phoneNumber;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(this.id, user.id) &&
        Objects.equals(this.auctions, user.auctions) &&
        Objects.equals(this.bids, user.bids) &&
        Objects.equals(this.watches, user.watches) &&
        Objects.equals(this.userName, user.userName) &&
        Objects.equals(this.passwordHash, user.passwordHash) &&
        Objects.equals(this.emailAddress, user.emailAddress) &&
        Objects.equals(this.phoneNumber, user.phoneNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, auctions, bids, watches, userName, passwordHash, emailAddress, phoneNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class User {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    auctions: ").append(toIndentedString(auctions)).append("\n");
    sb.append("    bids: ").append(toIndentedString(bids)).append("\n");
    sb.append("    watches: ").append(toIndentedString(watches)).append("\n");
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
    sb.append("    passwordHash: ").append(toIndentedString(passwordHash)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
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

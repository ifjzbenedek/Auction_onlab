package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * UserBasic
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2025-02-28T11:41:36.840793882Z[GMT]")


public class UserBasic   {
  @JsonProperty("id")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long id = null;

  @JsonProperty("userName")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String userName = null;

  @JsonProperty("emailAddress")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String emailAddress = null;

  @JsonProperty("phoneNumber")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String phoneNumber = null;


  public UserBasic id(Long id) { 

    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   **/
  
  @Schema(description = "")
  
  public Long getId() {  
    return id;
  }



  public void setId(Long id) { 
    this.id = id;
  }

  public UserBasic userName(String userName) { 

    this.userName = userName;
    return this;
  }

  /**
   * Get userName
   * @return userName
   **/
  
  @Schema(description = "")
  
@Size(max=25)   public String getUserName() {  
    return userName;
  }



  public void setUserName(String userName) { 
    this.userName = userName;
  }

  public UserBasic emailAddress(String emailAddress) { 

    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
   **/
  
  @Schema(description = "")
  
@Size(max=50)   public String getEmailAddress() {  
    return emailAddress;
  }



  public void setEmailAddress(String emailAddress) { 
    this.emailAddress = emailAddress;
  }

  public UserBasic phoneNumber(String phoneNumber) { 

    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   **/
  
  @Schema(description = "")
  
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
    UserBasic userBasic = (UserBasic) o;
    return Objects.equals(this.id, userBasic.id) &&
        Objects.equals(this.userName, userBasic.userName) &&
        Objects.equals(this.emailAddress, userBasic.emailAddress) &&
        Objects.equals(this.phoneNumber, userBasic.phoneNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userName, emailAddress, phoneNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserBasic {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
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

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
 * UserCredentials
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2025-02-28T11:41:36.840793882Z[GMT]")


public class UserCredentials   {
  @JsonProperty("userName")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String userName = null;

  @JsonProperty("passwordHash")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String passwordHash = null;


  public UserCredentials userName(String userName) { 

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

  public UserCredentials passwordHash(String passwordHash) { 

    this.passwordHash = passwordHash;
    return this;
  }

  /**
   * Get passwordHash
   * @return passwordHash
   **/
  
  @Schema(description = "")
  
@Size(max=255)   public String getPasswordHash() {  
    return passwordHash;
  }



  public void setPasswordHash(String passwordHash) { 
    this.passwordHash = passwordHash;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserCredentials userCredentials = (UserCredentials) o;
    return Objects.equals(this.userName, userCredentials.userName) &&
        Objects.equals(this.passwordHash, userCredentials.passwordHash);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, passwordHash);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserCredentials {\n");
    
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
    sb.append("    passwordHash: ").append(toIndentedString(passwordHash)).append("\n");
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

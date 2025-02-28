package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.Auction;
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
 * Category
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2025-02-28T11:41:36.840793882Z[GMT]")


public class Category   {
  @JsonProperty("id")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Long id = null;

  @JsonProperty("auctions")
  @Valid
  private List<Auction> auctions = null;
  @JsonProperty("categoryName")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String categoryName = null;


  public Category id(Long id) { 

    this.id = id;
    return this;
  }

  /**
   * The unique identifier of Categories
   * @return id
   **/
  
  @Schema(description = "The unique identifier of Categories")
  
  public Long getId() {  
    return id;
  }



  public void setId(Long id) { 
    this.id = id;
  }

  public Category auctions(List<Auction> auctions) { 

    this.auctions = auctions;
    return this;
  }

  public Category addAuctionsItem(Auction auctionsItem) {
    if (this.auctions == null) {
      this.auctions = new ArrayList<Auction>();
    }
    this.auctions.add(auctionsItem);
    return this;
  }

  /**
   * Get auctions
   * @return auctions
   **/
  
  @Schema(description = "")
  @Valid
  public List<Auction> getAuctions() {  
    return auctions;
  }



  public void setAuctions(List<Auction> auctions) { 
    this.auctions = auctions;
  }

  public Category categoryName(String categoryName) { 

    this.categoryName = categoryName;
    return this;
  }

  /**
   * The name of the Category
   * @return categoryName
   **/
  
  @Schema(description = "The name of the Category")
  
@Size(max=50)   public String getCategoryName() {  
    return categoryName;
  }



  public void setCategoryName(String categoryName) { 
    this.categoryName = categoryName;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Category category = (Category) o;
    return Objects.equals(this.id, category.id) &&
        Objects.equals(this.auctions, category.auctions) &&
        Objects.equals(this.categoryName, category.categoryName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, auctions, categoryName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Category {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    auctions: ").append(toIndentedString(auctions)).append("\n");
    sb.append("    categoryName: ").append(toIndentedString(categoryName)).append("\n");
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

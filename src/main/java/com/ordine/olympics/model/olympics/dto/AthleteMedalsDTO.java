package com.ordine.olympics.model.olympics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class AthleteMedalsDTO {
  @JsonProperty("name")
  public String name;

  @JsonProperty("url")
  public String url;

  @JsonProperty("goldMedalsCount")
  private int goldMedalsCount;

  @JsonProperty("silverMedalsCount")
  private int silverMedalsCount;

  @JsonProperty("bronzeMedalsCount")
  private int bronzeMedalsCount;

  @JsonProperty("participationsCount")
  private int participationsCount;

}

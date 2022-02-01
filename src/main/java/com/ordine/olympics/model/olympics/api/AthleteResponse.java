package com.ordine.olympics.model.olympics.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AthleteResponse {
  @JsonProperty("title")
  public String name;

  @JsonProperty("slug")
  public String nameSlug;

  @JsonProperty("url")
  public String url;

  @JsonProperty("countryCode")
  public String countryCode;

  @JsonProperty("country")
  public String countryName;

  @JsonProperty("discipline")
  public String discipline;

}

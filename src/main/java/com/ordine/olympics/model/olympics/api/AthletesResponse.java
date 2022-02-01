package com.ordine.olympics.model.olympics.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AthletesResponse {

  @SuppressWarnings("unused")
  public AthletesResponse() {}

  public AthletesResponse(List<AthleteResponse> athletes) {
    this.athletes = new ArrayList<>();
    this.athletes.addAll(athletes);
  }

  @JsonProperty("content")
  public List<AthleteResponse> athletes;
}

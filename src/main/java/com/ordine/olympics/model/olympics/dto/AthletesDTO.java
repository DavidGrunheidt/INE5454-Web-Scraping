package com.ordine.olympics.model.olympics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AthletesDTO {

  @JsonProperty("athletesScraped")
  public int athletesScraped;

  @JsonProperty("athletesAlreadySaved")
  public int athletesAlreadySaved;

  @JsonProperty("newSavedAthletes")
  public int newSavedAthletes;

  @JsonProperty("content")
  public List<AthleteDTO> athletes;
}

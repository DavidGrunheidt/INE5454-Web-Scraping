package com.ordine.olympics.model.olympics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ordine.olympics.model.db.Athlete;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AthleteDTO {

  @JsonProperty("tags")
  public List<String> tags;

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

  public Athlete toAthlete() {
    final String disciplineSlug = tags.stream().filter(tag -> tag.contains("discipline"))
        .findFirst().stream().collect(
            Collectors.joining(""));
    
    return Athlete.builder()
        .nameSlug(nameSlug)
        .name(name)
        .url(url)
        .goldMedalsCount(0)
        .silverMedalsCount(0)
        .bronzeMedalsCount(0)
        .participationsCount(0)
        .countryCode(countryCode)
        .countryName(countryName)
        .disciplineSlug(disciplineSlug)
        .disciplineName(discipline)
        .build();
  }

}

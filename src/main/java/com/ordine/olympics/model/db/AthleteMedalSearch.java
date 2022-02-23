package com.ordine.olympics.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "athlete_medal_search", schema = "public")
public class AthleteMedalSearch {
  @Id
  @Column(nullable = false, unique = true)
  private String searchedNameSlug;
}

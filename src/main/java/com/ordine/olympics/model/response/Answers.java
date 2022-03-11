package com.ordine.olympics.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Answers {
  public double percentageOfBrAthletlesWithMedals;

  public double percentageOfBrAthletlesWithGoldMedals;

  public double percentageOfUsaAthletlesWithMedals;

  public double percentageOfUsaAthletlesWithGoldMedals;

  public double brAthletesAverageOlympicsParticipation;

  public double usaAthletesAverageOlympicsParticipation;

  public int brTotalSportsParticipation;

  public int usaTotalSportsParticipation;

  public int totalOfBrAthletesWithMedals;

  public int totalOfUsaAthletesWithMedals;
}

package com.ordine.olympics.model.db;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "athlete", schema = "public")
public class Athlete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "athlete_id")
    private Long id;

    private String name;

    private int goldMedalsCount;

    private int silverMedalsCount;

    private int bronzeMedalsCount;

    private int participationsCount;

    private String birthDate;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne
    @JoinColumn(name = "olympic_games_id")
    private OlympicGames firstParticipation;

    @ManyToOne
    @JoinColumn(name = "sport_id")
    private Sport sport;
}

package com.ordine.olympics.model.db;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "olympic_games", schema = "public")
public class OlympicGames {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "olympic_games_id")
    private Long id;

    private String startDate;

    private String endDate;

    private String year;

    private int athletesCount;

    private int teamsCount;

    private int eventsCount;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}

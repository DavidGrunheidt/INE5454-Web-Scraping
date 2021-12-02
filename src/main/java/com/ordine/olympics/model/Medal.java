package com.ordine.olympics.model;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "medal", schema = "public")
public class Medal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medal_id")
    private Long id;

    private Boolean gold;

    private Boolean silver;

    private Boolean bronze;

    @ManyToOne
    @JoinColumn(name = "olympic_games_id")
    private OlympicGames olympicGames;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}

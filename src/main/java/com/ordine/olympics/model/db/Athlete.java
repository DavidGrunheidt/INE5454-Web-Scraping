package com.ordine.olympics.model.db;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "athlete", schema = "public")
public class Athlete {
    @Id
    @Column(nullable = false, unique = true)
    private String nameSlug;

    private String name;

    private String url;

    private int goldMedalsCount;

    private int silverMedalsCount;

    private int bronzeMedalsCount;

    private int participationsCount;

    private String countryCode;

    private String countryName;

    private String disciplineSlug;

    private String disciplineName;
}

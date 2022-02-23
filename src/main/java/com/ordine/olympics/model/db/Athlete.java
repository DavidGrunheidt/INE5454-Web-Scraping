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
    public String nameSlug;

    public String name;

    public String url;

    public int goldMedalsCount;

    public int silverMedalsCount;

    public int bronzeMedalsCount;

    public int participationsCount;

    public String countryCode;

    public String countryName;

    public String disciplineSlug;

    public String disciplineName;
}

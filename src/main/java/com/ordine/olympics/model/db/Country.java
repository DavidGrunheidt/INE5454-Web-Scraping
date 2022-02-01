package com.ordine.olympics.model.db;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "country", schema = "public")
public class Country {
    @Id
    @Column(name = "country_id")
    private Long id;

    private String name;
}

package com.example.TP.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "destinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Destination extends ExtendedModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "slug")
    private String slug;

    @Column(name = "country")
    private String country;

    @Column(name = "region")
    private String region;

    @Column(name = "description")
    private String description;

    @Column(name = "language")
    private String language;

    @Column(name = "currency")
    private String currency;

}

package com.example.TP.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "itinerary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_id")
    private Long packageId;

    @Column(name = "day_number")
    private Integer dayNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "title")
    private String title;

    @Column(name = "meals_include")
    private Boolean mealsInclude;

    @Column(name = "accomodation")
    private Boolean accomodation;

    @Column(name = "sightseeing")
    private Boolean sightseeing;
}

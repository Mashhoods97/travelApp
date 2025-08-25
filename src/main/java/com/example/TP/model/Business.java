package com.example.TP.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "businesses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Business extends ExtendedModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String title;

    @Column(length = 200)
    private String description;
}

package com.example.TP.model;

import com.example.TP.enums.ERole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "privileges")
@Data
@SuperBuilder
@NoArgsConstructor
@ToString
public class Privilege {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "privilege_id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "parent_id")
    private Long parentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "erole")
    private ERole erole;

    @ManyToMany(mappedBy = "privileges", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    @Transient
    private boolean linked;

}

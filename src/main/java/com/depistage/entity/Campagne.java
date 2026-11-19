package com.depistage.entity;

import com.depistage.enums.StatutCampagne;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "campagne")
@Getter
@Setter
@NoArgsConstructor
public class Campagne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nom;

    @Column(nullable = false, length = 100)
    private String ville;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(nullable = false)
    private int objectif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCampagne statut;

    /** Côté propriétaire de la relation N:N. Chargement paresseux, par lots pour éviter le N+1. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "campagne_type",
            joinColumns = @JoinColumn(name = "campagne_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id"))
    @BatchSize(size = 50)
    private Set<TypeDepistage> types = new HashSet<>();
}

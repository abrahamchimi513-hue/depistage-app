package com.depistage.entity;

import com.depistage.enums.Resultat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "depistage")
@Getter
@Setter
@NoArgsConstructor
public class Depistage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id", nullable = false)
    private TypeDepistage type;

    @Column(name = "date_depistage", nullable = false)
    private LocalDate dateDepistage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Resultat resultat;

    @Column(name = "valeur_mesuree", precision = 10, scale = 2)
    private BigDecimal valeurMesuree;

    @Column(length = 120)
    private String orientation;

    @Column(length = 500)
    private String observation;
}

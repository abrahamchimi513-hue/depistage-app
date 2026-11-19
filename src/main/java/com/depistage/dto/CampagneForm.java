package com.depistage.dto;

import com.depistage.dto.validation.PeriodeValide;
import com.depistage.enums.StatutCampagne;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@PeriodeValide
public class CampagneForm {

    @Schema(description = "Nom unique de la campagne (5 à 80 caractères)", example = "Dépistage Bonamoussadi")
    @NotBlank(message = "{champ.obligatoire}")
    @Size(min = 5, max = 80, message = "{champ.taille}")
    private String nom;

    @Schema(description = "Ville où se déroule la campagne", example = "Douala")
    @NotBlank(message = "{champ.obligatoire}")
    private String ville;

    @Schema(description = "Premier jour de la campagne", example = "2026-10-19")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "{champ.obligatoire}")
    private LocalDate dateDebut;

    @Schema(description = "Dernier jour de la campagne (pas avant la date de début)", example = "2026-12-18")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "{champ.obligatoire}")
    private LocalDate dateFin;

    @Schema(description = "Nombre de participants que la campagne vise à dépister (1 minimum)", example = "30")
    @NotNull(message = "{champ.obligatoire}")
    @Min(value = 1, message = "{champ.min}")
    private Integer objectif;

    @Schema(description = "Étape actuelle de la campagne. Seule une campagne EN_COURS accepte de nouveaux dépistages.",
            example = "EN_COURS")
    @NotNull(message = "{champ.obligatoire}")
    private StatutCampagne statut;

    @Schema(description = "Identifiants des types de dépistage proposés par la campagne (au moins un)", example = "[1, 2]")
    @NotEmpty(message = "{champ.selection}")
    private List<Long> typeIds = new ArrayList<>();
}

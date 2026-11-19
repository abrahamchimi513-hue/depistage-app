package com.depistage.dto;

import com.depistage.enums.StatutCampagne;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Une campagne de dépistage et son avancement")
public record CampagneResponse(
        @Schema(description = "Identifiant de la campagne", example = "1") Long id,
        @Schema(description = "Nom unique de la campagne", example = "Dépistage Bonamoussadi") String nom,
        @Schema(description = "Ville de la campagne", example = "Douala") String ville,
        @Schema(description = "Premier jour", example = "2026-10-19") LocalDate dateDebut,
        @Schema(description = "Dernier jour", example = "2026-12-18") LocalDate dateFin,
        @Schema(description = "Nombre de participants visés", example = "30") int objectif,
        @Schema(description = "Étape de la campagne", example = "EN_COURS") StatutCampagne statut,
        @Schema(description = "Codes des types de dépistage proposés", example = "[\"DIABETE\", \"HYPERTENSION\"]")
        List<String> types,
        @Schema(description = "Nombre de dépistages déjà enregistrés pour cette campagne", example = "14") long realises,
        @Schema(description = "Avancement en pourcentage (réalisés ÷ objectif), plafonné à 100", example = "46") int avancement) {
}

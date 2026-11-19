package com.depistage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statistiques d'une campagne")
public record CampagneStatsResponse(
        @Schema(description = "Dépistages enregistrés pour la campagne", example = "14") long realises,
        @Schema(description = "Objectif de la campagne", example = "30") int objectif,
        @Schema(description = "Avancement en %, plafonné à 100", example = "46") int avancement,
        @Schema(description = "Dépistages au résultat NEGATIF", example = "9") long negatifs,
        @Schema(description = "Dépistages au résultat POSITIF", example = "3") long positifs,
        @Schema(description = "Dépistages au résultat A_CONFIRMER", example = "2") long aConfirmer,
        @Schema(description = "Taux de positivité en %, 0 si aucun dépistage", example = "21.4") double tauxPositivite) {
}

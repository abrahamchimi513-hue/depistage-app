package com.depistage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Un type de dépistage du catalogue")
public record TypeResponse(
        @Schema(description = "Identifiant du type", example = "1") Long id,
        @Schema(description = "Code unique en majuscules", example = "DIABETE") String code,
        @Schema(description = "Nom lisible", example = "Dépistage du diabète") String libelle,
        @Schema(description = "Explication de l'examen, vide si non renseignée", example = "Mesure de la glycémie à jeun")
        String description) {
}

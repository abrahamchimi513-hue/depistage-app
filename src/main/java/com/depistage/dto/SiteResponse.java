package com.depistage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Un site de dépistage et sa campagne")
public record SiteResponse(
        @Schema(description = "Identifiant du site", example = "1") Long id,
        @Schema(description = "Nom du site", example = "Marché Central") String nom,
        @Schema(description = "Quartier du site", example = "Bonamoussadi") String quartier,
        @Schema(description = "Adresse ou repère, vide si non renseigné", example = "Face à la pharmacie") String adresse,
        @Schema(description = "Participants maximum par jour", example = "40") int capaciteJournaliere,
        @Schema(description = "Identifiant de la campagne du site", example = "1") Long campagneId,
        @Schema(description = "Nom de la campagne du site", example = "Dépistage Bonamoussadi") String campagneNom) {
}

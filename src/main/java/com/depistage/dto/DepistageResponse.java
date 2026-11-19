package com.depistage.dto;

import com.depistage.enums.Resultat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Un dépistage enregistré, avec les libellés lisibles de ses éléments")
public record DepistageResponse(
        @Schema(description = "Identifiant du dépistage", example = "1") Long id,
        @Schema(description = "Identifiant du participant", example = "1") Long participantId,
        @Schema(description = "Nom complet du participant", example = "Kamga Alice") String participantNom,
        @Schema(description = "Identifiant de l'agent", example = "1") Long agentId,
        @Schema(description = "Nom complet de l'agent", example = "Essomba Claire") String agentNom,
        @Schema(description = "Identifiant du site", example = "1") Long siteId,
        @Schema(description = "Nom du site", example = "Marché Central") String siteNom,
        @Schema(description = "Identifiant de la campagne du site", example = "1") Long campagneId,
        @Schema(description = "Nom de la campagne", example = "Dépistage Bonamoussadi") String campagneNom,
        @Schema(description = "Identifiant du type d'examen", example = "1") Long typeId,
        @Schema(description = "Nom du type d'examen", example = "Dépistage du diabète") String typeLibelle,
        @Schema(description = "Jour du dépistage", example = "2026-11-10") LocalDate dateDepistage,
        @Schema(description = "Conclusion de l'examen", example = "POSITIF") Resultat resultat,
        @Schema(description = "Valeur relevée, vide si non mesurée", example = "1.35") BigDecimal valeurMesuree,
        @Schema(description = "Centre vers lequel le participant est orienté, vide pour un résultat négatif",
                example = "Hôpital Laquintinie") String orientation,
        @Schema(description = "Remarque libre de l'agent", example = "Participant à jeun") String observation) {
}

package com.depistage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Chiffres clés de toutes les campagnes")
public record StatsResponse(
        @Schema(description = "Nombre de campagnes au statut EN_COURS", example = "1") long campagnesEnCours,
        @Schema(description = "Nombre de participants enregistrés", example = "12") long participants,
        @Schema(description = "Nombre total de dépistages", example = "20") long depistages,
        @Schema(description = "Nombre de dépistages au résultat POSITIF", example = "4") long positifs,
        @Schema(description = "Taux de positivité en % (positifs ÷ dépistages), 0 si aucun dépistage", example = "20.0")
        double tauxPositivite,
        @Schema(description = "Nombre de dépistages par type d'examen") List<Repartition> parType,
        @Schema(description = "Nombre de dépistages par résultat") List<Repartition> parResultat,
        @Schema(description = "Nombre de dépistages par site") List<Repartition> parSite,
        @Schema(description = "Avancement de chaque campagne vers son objectif") List<Avancement> avancements,
        @Schema(description = "Dépistages POSITIF ou A_CONFIRMER, les plus récents d'abord (10 au maximum)")
        List<DepistageResponse> casAOrienter) {

    @Schema(description = "Une ligne de répartition")
    public record Repartition(
            @Schema(description = "Ce qui est compté (type, résultat ou site)", example = "Dépistage du diabète") String label,
            @Schema(description = "Nombre de dépistages", example = "12") long total,
            @Schema(description = "Part du total des dépistages, en %", example = "60") int pourcentage) {
    }

    @Schema(description = "Avancement d'une campagne")
    public record Avancement(
            @Schema(description = "Identifiant de la campagne", example = "1") Long campagneId,
            @Schema(description = "Nom de la campagne", example = "Dépistage Bonamoussadi") String nom,
            @Schema(description = "Étape de la campagne", example = "EN_COURS") String statut,
            @Schema(description = "Dépistages enregistrés", example = "14") long realises,
            @Schema(description = "Objectif de la campagne", example = "30") int objectif,
            @Schema(description = "Avancement en %, plafonné à 100", example = "46") int pourcentage,
            @Schema(description = "Vrai quand l'objectif est atteint ou dépassé", example = "false") boolean objectifAtteint) {
    }
}

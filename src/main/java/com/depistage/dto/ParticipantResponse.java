package com.depistage.dto;

import com.depistage.enums.Sexe;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Un participant aux dépistages (données fictives)")
public record ParticipantResponse(
        @Schema(description = "Identifiant du participant", example = "1") Long id,
        @Schema(description = "Nom de famille", example = "Kamga") String nom,
        @Schema(description = "Prénom", example = "Alice") String prenom,
        @Schema(description = "Sexe", example = "FEMININ") Sexe sexe,
        @Schema(description = "Date de naissance", example = "1990-05-14") LocalDate dateNaissance,
        @Schema(description = "Âge calculé à la date du jour", example = "36") int age,
        @Schema(description = "Téléphone, vide si non renseigné", example = "699000001") String telephone,
        @Schema(description = "Quartier de résidence", example = "Akwa") String quartier) {
}

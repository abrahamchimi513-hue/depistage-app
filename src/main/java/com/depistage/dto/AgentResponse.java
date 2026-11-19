package com.depistage.dto;

import com.depistage.enums.Fonction;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Un agent de santé")
public record AgentResponse(
        @Schema(description = "Identifiant de l'agent", example = "1") Long id,
        @Schema(description = "Nom de famille", example = "Essomba") String nom,
        @Schema(description = "Prénom", example = "Claire") String prenom,
        @Schema(description = "Téléphone à 9 chiffres", example = "670000001") String telephone,
        @Schema(description = "Rôle dans l'équipe", example = "INFIRMIER") Fonction fonction,
        @Schema(description = "Vrai si l'agent peut encore enregistrer des dépistages", example = "true") boolean actif) {
}

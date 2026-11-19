package com.depistage.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Format uniforme de toutes les erreurs de l'API")
public record ApiError(
        @Schema(description = "Moment où l'erreur s'est produite (UTC)", example = "2026-11-18T01:02:03Z")
        Instant timestamp,
        @Schema(description = "Code HTTP de la réponse", example = "400")
        int status,
        @Schema(description = "Code court de l'erreur : VALIDATION_ERROR, NOT_FOUND, CONFLICT, BAD_REQUEST ou INTERNAL_ERROR",
                example = "VALIDATION_ERROR")
        String code,
        @Schema(description = "Message lisible expliquant ce qui ne va pas", example = "Les données envoyées sont invalides.")
        String message,
        @Schema(description = "Chemin de la requête qui a échoué", example = "/api/depistages")
        String path,
        @Schema(description = "Erreurs par champ : nom du champ → message. Vide si l'erreur ne concerne aucun champ.",
                example = "{\"orientation\": \"L’orientation est obligatoire lorsque le résultat n’est pas négatif.\"}")
        Map<String, String> details) {
}

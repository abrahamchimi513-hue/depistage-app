package com.depistage.dto;

import com.depistage.dto.validation.OrientationRequise;
import com.depistage.enums.Resultat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@OrientationRequise
public class DepistageForm {

    @Schema(description = "Identifiant du participant dépisté", example = "1")
    @NotNull(message = "{champ.obligatoire}")
    private Long participantId;

    @Schema(description = "Identifiant de l'agent qui réalise le dépistage (doit être actif)", example = "1")
    @NotNull(message = "{champ.obligatoire}")
    private Long agentId;

    @Schema(description = "Identifiant du site où a lieu le dépistage (sa campagne doit être EN_COURS)", example = "1")
    @NotNull(message = "{champ.obligatoire}")
    private Long siteId;

    @Schema(description = "Identifiant du type d'examen (doit être proposé par la campagne du site)", example = "1")
    @NotNull(message = "{champ.obligatoire}")
    private Long typeId;

    @Schema(description = "Jour du dépistage : pas dans le futur, et compris dans les dates de la campagne",
            example = "2026-11-10")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "{champ.obligatoire}")
    @PastOrPresent(message = "{date.nonfuture}")
    private LocalDate dateDepistage;

    @Schema(description = "Conclusion de l'examen", example = "POSITIF")
    @NotNull(message = "{champ.obligatoire}")
    private Resultat resultat;

    @Schema(description = "Valeur relevée lors de l'examen (par exemple la glycémie en g/L). Facultative, 0 minimum.",
            example = "1.35")
    @DecimalMin(value = "0", message = "{valeur.positive}")
    private BigDecimal valeurMesuree;

    @Schema(description = "Centre de santé vers lequel le participant est envoyé. Obligatoire si le résultat n'est pas NEGATIF.",
            example = "Hôpital Laquintinie")
    @Size(max = 120, message = "{champ.taille.max}")
    private String orientation;

    @Schema(description = "Remarque libre de l'agent (500 caractères maximum)", example = "Participant à jeun depuis 12 heures")
    @Size(max = 500, message = "{champ.taille.max}")
    private String observation;
}

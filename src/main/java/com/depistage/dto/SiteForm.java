package com.depistage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteForm {

    @Schema(description = "Nom du lieu où se tient le dépistage (3 à 60 caractères)", example = "Marché Central")
    @NotBlank(message = "{champ.obligatoire}")
    @Size(min = 3, max = 60, message = "{champ.taille}")
    private String nom;

    @Schema(description = "Quartier où se trouve le site", example = "Bonamoussadi")
    @NotBlank(message = "{champ.obligatoire}")
    private String quartier;

    @Schema(description = "Adresse ou repère pour trouver le site (facultatif)", example = "Face à la pharmacie")
    @Size(max = 200, message = "{champ.taille.max}")
    private String adresse;

    @Schema(description = "Nombre maximum de participants que le site peut recevoir par jour", example = "40")
    @NotNull(message = "{champ.obligatoire}")
    @Min(value = 1, message = "{champ.min}")
    private Integer capaciteJournaliere;

    @Schema(description = "Identifiant de la campagne à laquelle le site est rattaché", example = "1")
    @NotNull(message = "{champ.obligatoire}")
    private Long campagneId;
}

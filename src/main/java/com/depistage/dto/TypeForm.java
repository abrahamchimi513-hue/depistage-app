package com.depistage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeForm {

    @Schema(description = "Code unique en majuscules, sans espace", example = "DIABETE")
    @NotBlank(message = "{champ.obligatoire}")
    @Pattern(regexp = "^[A-Z0-9_]*$", message = "{code.format}")
    @Size(max = 40, message = "{champ.taille.max}")
    private String code;

    @Schema(description = "Nom lisible du type de dépistage", example = "Dépistage du diabète")
    @NotBlank(message = "{champ.obligatoire}")
    @Size(max = 100, message = "{champ.taille.max}")
    private String libelle;

    @Schema(description = "Explication facultative de l'examen réalisé (300 caractères maximum)",
            example = "Mesure de la glycémie à jeun")
    @Size(max = 300, message = "{champ.taille.max}")
    private String description;
}

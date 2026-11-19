package com.depistage.dto;

import com.depistage.enums.Fonction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentForm {

    @Schema(description = "Nom de famille de l'agent", example = "Essomba")
    @NotBlank(message = "{champ.obligatoire}")
    @Size(min = 2, max = 60, message = "{champ.taille}")
    private String nom;

    @Schema(description = "Prénom de l'agent", example = "Claire")
    @NotBlank(message = "{champ.obligatoire}")
    @Size(min = 2, max = 60, message = "{champ.taille}")
    private String prenom;

    @Schema(description = "Téléphone unique : 9 chiffres, le premier est 6", example = "670000001")
    @NotBlank(message = "{champ.obligatoire}")
    @Pattern(regexp = "^6\\d{8}$", message = "{telephone.format}")
    private String telephone;

    @Schema(description = "Rôle de l'agent dans l'équipe", example = "INFIRMIER")
    @NotNull(message = "{champ.obligatoire}")
    private Fonction fonction;

    @Schema(description = "Un agent inactif ne peut plus enregistrer de dépistage", example = "true")
    private boolean actif = true;
}

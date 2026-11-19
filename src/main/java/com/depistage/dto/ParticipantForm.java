package com.depistage.dto;

import com.depistage.dto.validation.AgeEntre;
import com.depistage.enums.Sexe;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ParticipantForm {

    @Schema(description = "Nom de famille du participant (données fictives uniquement)", example = "Kamga")
    @NotBlank(message = "{champ.obligatoire}")
    @Size(min = 2, max = 60, message = "{champ.taille}")
    private String nom;

    @Schema(description = "Prénom du participant", example = "Alice")
    @NotBlank(message = "{champ.obligatoire}")
    @Size(min = 2, max = 60, message = "{champ.taille}")
    private String prenom;

    @Schema(description = "Sexe du participant", example = "FEMININ")
    @NotNull(message = "{champ.obligatoire}")
    private Sexe sexe;

    @Schema(description = "Date de naissance : dans le passé, pour un âge de 1 à 110 ans", example = "1990-05-14")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "{champ.obligatoire}")
    @Past(message = "{date.passee}")
    @AgeEntre(min = 1, max = 110)
    private LocalDate dateNaissance;

    @Schema(description = "Téléphone facultatif : 9 chiffres, le premier est 6", example = "699000001")
    @Pattern(regexp = "^(6\\d{8})?$", message = "{telephone.format}")
    private String telephone;

    @Schema(description = "Quartier de résidence du participant", example = "Akwa")
    @NotBlank(message = "{champ.obligatoire}")
    private String quartier;
}

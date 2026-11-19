package com.depistage.controller.api;

import com.depistage.dto.DepistageForm;
import com.depistage.dto.DepistageResponse;
import com.depistage.dto.PageResponse;
import com.depistage.enums.Resultat;
import com.depistage.exception.ApiError;
import com.depistage.service.DepistageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/depistages")
@RequiredArgsConstructor
@Tag(name = "Dépistages", description = "Liste filtrée des dépistages et enregistrement d'un nouveau dépistage")
public class DepistageApiController {

    private final DepistageService service;

    @GetMapping
    @Operation(summary = "Lister les dépistages",
            description = "Retourne une page de dépistages, du plus récent au plus ancien. Les trois filtres sont "
                    + "facultatifs et se combinent.")
    @ApiResponse(responseCode = "200", description = "Page de dépistages (peut être vide)")
    @ApiResponse(responseCode = "400", description = "Filtre invalide, par exemple un résultat inconnu",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public PageResponse<DepistageResponse> lister(
            @Parameter(description = "Garde seulement les dépistages de cette campagne", example = "1")
            @RequestParam(required = false) Long campagneId,
            @Parameter(description = "Garde seulement les dépistages de ce type d'examen", example = "1")
            @RequestParam(required = false) Long typeId,
            @Parameter(description = "Garde seulement les dépistages ayant ce résultat", example = "POSITIF")
            @RequestParam(required = false) Resultat resultat,
            @Parameter(description = "Numéro de la page, à partir de 0", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page, de 1 à 100", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        var tri = Sort.by(Sort.Direction.DESC, "dateDepistage").and(Sort.by(Sort.Direction.DESC, "id"));
        return PageResponse.of(service.rechercher(campagneId, typeId, resultat, ApiPaging.of(page, size, tri)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un dépistage", description = "Retourne un dépistage avec les noms du participant, de l'agent, du site et du type.")
    @ApiResponse(responseCode = "200", description = "Le dépistage demandé")
    @ApiResponse(responseCode = "404", description = "Aucun dépistage avec cet identifiant",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public DepistageResponse trouver(@Parameter(description = "Identifiant du dépistage", example = "1") @PathVariable Long id) {
        return service.trouver(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un dépistage",
            description = "Supprime définitivement un dépistage saisi par erreur. Aucune autre donnée n'en dépend.")
    @ApiResponse(responseCode = "204", description = "Dépistage supprimé (aucun contenu retourné)")
    @ApiResponse(responseCode = "404", description = "Aucun dépistage avec cet identifiant",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<Void> supprimer(@Parameter(description = "Identifiant du dépistage à supprimer", example = "1")
                                          @PathVariable Long id) {
        service.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Enregistrer un dépistage",
            description = "Crée un dépistage. Règles : la campagne du site doit être EN_COURS, le type doit être proposé "
                    + "par cette campagne, l'agent doit être actif, la date doit être dans la période de la campagne et "
                    + "pas dans le futur, et un participant ne peut pas subir deux fois le même type dans la même "
                    + "campagne. L'orientation est obligatoire si le résultat n'est pas NEGATIF.")
    @ApiResponse(responseCode = "201", description = "Dépistage créé ; l'adresse du nouveau dépistage est dans l'en-tête Location")
    @ApiResponse(responseCode = "400", description = "Données invalides (champ manquant, date future, orientation absente…). "
            + "Le détail par champ est dans « details ».",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Le participant, l'agent, le site ou le type indiqué n'existe pas",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Règle métier violée : campagne non en cours, type non proposé, "
            + "agent inactif, date hors période ou dépistage déjà réalisé",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<DepistageResponse> creer(@Valid @RequestBody DepistageForm form) {
        DepistageResponse cree = service.creer(form);
        return ResponseEntity.created(URI.create("/api/depistages/" + cree.id())).body(cree);
    }
}

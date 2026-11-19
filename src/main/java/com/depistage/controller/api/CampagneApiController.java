package com.depistage.controller.api;

import com.depistage.dto.CampagneResponse;
import com.depistage.dto.CampagneStatsResponse;
import com.depistage.dto.PageResponse;
import com.depistage.dto.SiteResponse;
import com.depistage.exception.ApiError;
import com.depistage.service.CampagneService;
import com.depistage.service.SiteService;
import com.depistage.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/campagnes")
@RequiredArgsConstructor
@Tag(name = "Campagnes", description = "Consultation des campagnes de dépistage, de leurs sites et de leur avancement")
public class CampagneApiController {

    private final CampagneService service;
    private final SiteService siteService;
    private final StatsService statsService;

    @GetMapping
    @Operation(summary = "Lister les campagnes",
            description = "Retourne une page de campagnes triées par nom, avec le nombre de dépistages déjà réalisés "
                    + "et l'avancement vers l'objectif. Le paramètre q filtre sur le nom ou la ville.")
    @ApiResponse(responseCode = "200", description = "Page de campagnes (peut être vide)")
    @ApiResponse(responseCode = "400", description = "Paramètre de pagination invalide",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public PageResponse<CampagneResponse> lister(
            @Parameter(description = "Texte recherché dans le nom ou la ville de la campagne", example = "Bonamoussadi")
            @RequestParam(defaultValue = "") String q,
            @Parameter(description = "Numéro de la page, à partir de 0", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page, de 1 à 100", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return PageResponse.of(service.rechercher(q, ApiPaging.of(page, size, Sort.by("nom"))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une campagne", description = "Retourne une campagne avec ses types de dépistage et son avancement.")
    @ApiResponse(responseCode = "200", description = "La campagne demandée")
    @ApiResponse(responseCode = "404", description = "Aucune campagne avec cet identifiant",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public CampagneResponse trouver(@Parameter(description = "Identifiant de la campagne", example = "1") @PathVariable Long id) {
        return service.trouver(id);
    }

    @GetMapping("/{id}/sites")
    @Operation(summary = "Lister les sites d'une campagne", description = "Retourne tous les sites rattachés à la campagne, triés par nom.")
    @ApiResponse(responseCode = "200", description = "Sites de la campagne (liste vide si elle n'en a pas)")
    @ApiResponse(responseCode = "404", description = "Aucune campagne avec cet identifiant",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public List<SiteResponse> sites(@Parameter(description = "Identifiant de la campagne", example = "1") @PathVariable Long id) {
        return siteService.listerParCampagne(id);
    }

    @GetMapping("/{id}/statistiques")
    @Operation(summary = "Statistiques d'une campagne",
            description = "Nombre de dépistages réalisés, avancement, répartition des résultats et taux de positivité.")
    @ApiResponse(responseCode = "200", description = "Statistiques de la campagne")
    @ApiResponse(responseCode = "404", description = "Aucune campagne avec cet identifiant",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public CampagneStatsResponse statistiques(@Parameter(description = "Identifiant de la campagne", example = "1") @PathVariable Long id) {
        return statsService.pourCampagne(id);
    }
}

package com.depistage.controller.api;

import com.depistage.dto.StatsResponse;
import com.depistage.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistiques")
@RequiredArgsConstructor
@Tag(name = "Statistiques", description = "Chiffres clés globaux, calculés par la base de données")
public class StatsApiController {

    private final StatsService service;

    @GetMapping
    @Operation(summary = "Statistiques globales",
            description = "Nombre de campagnes en cours, de participants, de dépistages et de cas positifs, taux de "
                    + "positivité, répartitions par type, résultat et site, avancement de chaque campagne et cas à orienter. "
                    + "Sans données, tous les nombres valent 0.")
    @ApiResponse(responseCode = "200", description = "Statistiques globales")
    public StatsResponse globales() {
        return service.globales();
    }
}

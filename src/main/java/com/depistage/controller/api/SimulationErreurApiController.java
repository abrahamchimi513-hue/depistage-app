package com.depistage.controller.api;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Contrôle de l'étape 7 côté API : l'erreur interne doit sortir au format JSON uniforme, sans détail technique. */
@RestController
@Profile("dev")
@Hidden
public class SimulationErreurApiController {

    @GetMapping("/api/simuler-erreur")
    public String simuler() {
        throw new IllegalStateException("Erreur simulée : détail technique qui ne doit JAMAIS apparaître dans le JSON.");
    }
}

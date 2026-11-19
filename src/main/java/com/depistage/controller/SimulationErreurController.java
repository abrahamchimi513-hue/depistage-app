package com.depistage.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Outil de contrôle de l'étape 7 : provoque volontairement une erreur interne pour vérifier que
 * l'utilisateur ne voit qu'un message générique (jamais de détail technique). Actif uniquement en profil dev.
 */
@Controller
@Profile("dev")
public class SimulationErreurController {

    @GetMapping("/simuler-erreur")
    public String simuler() {
        throw new IllegalStateException("Erreur simulée : détail technique qui ne doit JAMAIS s'afficher (jdbc:h2:mem:depistage).");
    }
}

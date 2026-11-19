package com.depistage.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Petits outils partagés par les contrôleurs de pages pour la pagination. */
final class Paging {

    static final int TAILLE = 10;

    private Paging() {
    }

    static Pageable of(int page, int taille, Sort tri) {
        return PageRequest.of(Math.max(page, 0), taille, tri);
    }

    /**
     * Construit la fin de l'URL des liens de pagination à partir de paires nom/valeur
     * (« &q=ka&campagneId=2 »). Les valeurs vides sont ignorées. Ainsi la recherche et les
     * filtres sont conservés quand on change de page.
     */
    static String qs(Object... paires) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 1 < paires.length; i += 2) {
            Object valeur = paires[i + 1];
            if (valeur == null || valeur.toString().isBlank()) {
                continue;
            }
            sb.append('&').append(paires[i]).append('=')
                    .append(URLEncoder.encode(valeur.toString(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }
}

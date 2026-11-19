package com.depistage.service;

final class Texte {

    private Texte() {
    }

    /** Un mot recherché absent devient une chaîne vide (qui trouve tout). */
    static String norm(String q) {
        return q == null ? "" : q.trim();
    }
}

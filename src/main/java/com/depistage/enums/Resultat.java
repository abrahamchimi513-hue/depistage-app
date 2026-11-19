package com.depistage.enums;

public enum Resultat {
    NEGATIF("Négatif"),
    POSITIF("Positif"),
    A_CONFIRMER("À confirmer");

    private final String libelle;

    Resultat(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}

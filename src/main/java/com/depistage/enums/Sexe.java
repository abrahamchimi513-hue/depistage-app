package com.depistage.enums;

public enum Sexe {
    FEMININ("Féminin"),
    MASCULIN("Masculin");

    private final String libelle;

    Sexe(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}

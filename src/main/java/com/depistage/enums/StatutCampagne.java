package com.depistage.enums;

public enum StatutCampagne {
    PLANIFIEE("Planifiée"),
    EN_COURS("En cours"),
    TERMINEE("Terminée");

    private final String libelle;

    StatutCampagne(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}

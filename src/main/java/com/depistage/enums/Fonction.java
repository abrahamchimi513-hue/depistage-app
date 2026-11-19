package com.depistage.enums;

public enum Fonction {
    INFIRMIER("Infirmier"),
    TECHNICIEN_LABO("Technicien de laboratoire"),
    MEDECIN("Médecin"),
    BENEVOLE("Bénévole");

    private final String libelle;

    Fonction(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}

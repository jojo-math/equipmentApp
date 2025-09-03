package com.isifr.ManageEquipement.entities;

import lombok.Getter;

@Getter
public enum EtatMateriel {
    DISPONIBLE("Disponible"),
    INDISPONIBLE("Indisponible"); // Corrigé l'erreur typographique

    private final String label;

    EtatMateriel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    // Nouvelle méthode pour rechercher une constante par son label
    public static EtatMateriel fromLabel(String label) {
        if (label == null) {
            return null;
        }
        for (EtatMateriel etat : EtatMateriel.values()) {
            if (etat.label.equalsIgnoreCase(label.trim())) {
                return etat;
            }
        }
        throw new IllegalArgumentException("Aucun état trouvé pour le label : " + label);
    }
}
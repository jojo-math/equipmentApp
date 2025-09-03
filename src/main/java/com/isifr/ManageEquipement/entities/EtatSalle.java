package com.isifr.ManageEquipement.entities;

import lombok.Getter;

@Getter
public enum EtatSalle {
    OCCUPEE("Occupée"),
    DISPONIBLE("Disponible"); // Corrigé l'erreur typographique

    private final String label;

    EtatSalle(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    // Nouvelle méthode pour rechercher une constante par son label
    public static EtatSalle fromLabel(String label) {
        if (label == null) {
            return null;
        }
        for (EtatSalle etat : EtatSalle.values()) {
            if (etat.label.equalsIgnoreCase(label.trim())) {
                return etat;
            }
        }
        throw new IllegalArgumentException("Aucun état trouvé pour le label : " + label);
    }
}

package com.isifr.ManageEquipement.entities;

public enum StatutDemande {
    VALIDEE("Validée"),
    REFUSEE("Refusée"),
    EN_ATTENTE("En attente"),;
    public String label;
    StatutDemande(String label) {
        this.label = label;
    }
    @Override
    public String toString() {
        return label;
    }
}

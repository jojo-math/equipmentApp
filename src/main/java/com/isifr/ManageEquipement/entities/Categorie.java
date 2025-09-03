package com.isifr.ManageEquipement.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString(exclude = {"materiels"}) // Exclure les relations du toString
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;

    @OneToMany(mappedBy = "categorie", fetch = FetchType.LAZY)
    @JsonManagedReference // Gérer la sérialisation de cette relation
    private List<Materiel> materiels;

    // Custom toString to avoid recursion
    @Override
    public String toString() {
        return "Categorie{id=" + id + ", nom='" + nom + "'}";
    }

    // Helper methods to manage bidirectional relationship
    public void addMateriel(Materiel materiel) {
        materiels.add(materiel);
        materiel.setCategorie(this);
    }

    public void removeMateriel(Materiel materiel) {
        materiels.remove(materiel);
        materiel.setCategorie(null);
    }
}